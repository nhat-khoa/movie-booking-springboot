package com.ticket.moviebooking.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaymentCreateResponse {
    String appTransId;

    Integer returnCode;
    String returnMessage;
    Integer subReturnCode;
    String subReturnMessage;
    String orderUrl;
    String zpTransToken;
    String orderToken;
    String qrCode;
}
