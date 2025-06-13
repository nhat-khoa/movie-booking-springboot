package com.ticket.moviebooking.configuration;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

@Component
@FieldDefaults(level = AccessLevel.PUBLIC, makeFinal = true)
public class ZaloPayConfig {
    String ngrokUrl = "https://2141-115-79-138-142.ngrok-free.app";

    int appId = 2554;
    String key1 = "sdngKKJmqEMzvh5QQcdD2A9XBSKUNaYn";
    String key2 = "trMrHtvjo6myautxDUiAcYsVtaeQ8nhf";
    String endpoint = "https://sb-openapi.zalopay.vn/v2/create";
    String queryEndpoint = "https://sb-openapi.zalopay.vn/v2/query";
    String redirectUrl = "https://www.google.com";
    String callbackUrl = ngrokUrl + "/payment/callback";
}
