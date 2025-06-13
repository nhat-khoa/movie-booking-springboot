package com.ticket.moviebooking.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ticket.moviebooking.configuration.ZaloPayConfig;
import com.ticket.moviebooking.dto.request.PaymentCreateRequest;
import com.ticket.moviebooking.dto.request.TicketRequest;
import com.ticket.moviebooking.dto.response.PaymentCreateResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Hex;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ZaloPayService {
    ZaloPayConfig config;
    RestTemplate restTemplate;
    ObjectMapper objectMapper;
    TicketService ticketService;
    RedisService redisService;

    public PaymentCreateResponse createPayment(PaymentCreateRequest request) throws Exception {
        String uuid = UUID.randomUUID().toString().replace("-", "");
        String appTransId = new SimpleDateFormat("yyMMdd").format(new Date()) + "_" + uuid.substring(0, 12);

        Long appTime = System.currentTimeMillis();
        String item = objectMapper.writeValueAsString(request.getSeatIds());;
        Map<String, Object> embedDataMap = new HashMap<>();
        embedDataMap.put("schedule_id", request.getScheduleId());
        embedDataMap.put("user_id", request.getUserId());
        embedDataMap.put("total_price", request.getTotalPrice());
        String embedData = objectMapper.writeValueAsString(embedDataMap);

        Map<String, Object> order = new LinkedHashMap<>();
        order.put("app_id", config.appId);
        order.put("app_trans_id", appTransId);
        order.put("app_user", request.getUserId());
        order.put("app_time", appTime);
        order.put("amount", request.getTotalPrice());
        order.put("item", item);
        order.put("embed_data", embedData);
        order.put("description", "Thanh toán - #" + appTransId);
        order.put("bank_code", "");
        order.put("callback_url", config.callbackUrl);
        order.put("redirect_url", config.redirectUrl);

        // Tạo chuỗi dữ liệu để hash
        String data = String.join("|",
                String.valueOf(config.appId),
                appTransId,
                request.getUserId(),
                String.valueOf(request.getTotalPrice()),
                String.valueOf(appTime),
                embedData,
                item
        );

        String mac = hmacSHA256(data, config.key1);
        order.put("mac", mac);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<String> entity = new HttpEntity<>(toUrlEncodedString(order), headers);

        ResponseEntity<String> response = restTemplate.postForEntity(config.endpoint, entity, String.class);
        Map<String, Object> result = new ObjectMapper().readValue(response.getBody(), Map.class);

        return PaymentCreateResponse.builder()
                .appTransId(appTransId)
                .returnCode((Integer) result.get("return_code"))
                .returnMessage((String) result.get("return_message"))
                .subReturnCode((Integer) result.get("sub_return_code"))
                .subReturnMessage((String) result.get("sub_return_message"))
                .orderUrl((String) result.get("order_url"))
                .zpTransToken((String) result.get("zp_trans_token"))
                .orderToken((String) result.get("order_token"))
                .qrCode((String) result.get("qr_code"))
                .build();
    }

    public Map<String, Object> handleCallback(String jsonStr) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> payload = objectMapper.readValue(jsonStr, Map.class);

            String data = (String) payload.get("data");
            String reqMac = (String) payload.get("mac");

            String mac = hmacSHA256(data, config.key2);

            if (!mac.equals(reqMac)) {
                log.error("MAC mismatch: expected {}, received {}", mac, reqMac);
                return Map.of("return_code", -1, "return_message", "mac not equal");
            }

            Map<String, Object> dataJson = objectMapper.readValue(data, Map.class);

            String appTransId = (String) dataJson.get("app_trans_id");

            String embedDataStr = (String) dataJson.get("embed_data");
            Map<String, Object> embedDataMap = objectMapper.readValue(embedDataStr, Map.class);
            String scheduleId = (String) embedDataMap.get("schedule_id");
            String userId = (String) embedDataMap.get("user_id");
            Integer totalPrice = (Integer) embedDataMap.get("total_price");

            String itemStr = (String) dataJson.get("item");
            List<String> seatIds = new ObjectMapper().readValue(itemStr, new TypeReference<List<String>>() {});

            ticketService.createTicket(
                    TicketRequest.builder()
                        .totalPrice(totalPrice)
                        .scheduleId(scheduleId)
                        .userId(userId)
                        .seatIds(seatIds)
                    .build()
            );

            redisService.removeSeatHoldsByScheduleIdAndUserId(scheduleId, userId);
            redisService.removeTicketHoldsByAppTransId(appTransId);
            log.info("Successfully processed callback for appTransId: {}", appTransId);
            return Map.of("return_code", 1, "return_message", "success");
        } catch (Exception e) {
            log.error("Error handling callback: {}", e.getMessage(), e);
            return Map.of("return_code", 0, "return_message", e.getMessage());
        }
    }


    //    public Map<String, Object> queryStatus(User user, String appTransId) throws Exception {
//        Map<String, String> params = new LinkedHashMap<>();
//        params.put("app_id", String.valueOf(config.appId));
//        params.put("app_trans_id", appTransId);
//
//        String data = config.appId + "|" + appTransId + "|" + config.key1;
//        params.put("mac", hmacSHA256(data, config.key1));
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
//        HttpEntity<String> entity = new HttpEntity<>(toUrlEncodedString(params), headers);
//
//        ResponseEntity<String> response = restTemplate.postForEntity(config.queryEndpoint, entity, String.class);
//        Map<String, Object> result = new ObjectMapper().readValue(response.getBody(), Map.class);
//
//        if ((Integer) result.get("return_code") == 1) {
//            Optional<PaymentTransaction> txnOpt = paymentRepo.findByInvoiceId(appTransId);
//            if (txnOpt.isPresent()) {
//                PaymentTransaction txn = txnOpt.get();
//                txn.setPaymentStatus("completed");
//                txn.setTransactionDate(LocalDateTime.now());
//                paymentRepo.save(txn);
//
//                LocalDateTime now = LocalDateTime.now();
//                if (user.getPremiumExpired() != null && user.getPremiumExpired().isAfter(now)) {
//                    user.setPremiumExpired(user.getPremiumExpired().plusYears(1));
//                } else {
//                    user.setPremiumExpired(now.plusYears(1));
//                }
//                userRepo.save(user);
//            }
//        }
//
//        return result;
//    }
//
    private String hmacSHA256(String data, String key) throws Exception {
        Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
        SecretKeySpec secret_key = new SecretKeySpec(key.getBytes(), "HmacSHA256");
        sha256_HMAC.init(secret_key);
        return Hex.encodeHexString(sha256_HMAC.doFinal(data.getBytes()));
    }

    private String toUrlEncodedString(Map<String, ?> data) {
        return data.entrySet().stream()
                .map(e -> e.getKey() + "=" + URLEncoder.encode(String.valueOf(e.getValue()), StandardCharsets.UTF_8))
                .collect(Collectors.joining("&"));
    }
}

