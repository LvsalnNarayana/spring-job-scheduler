package com.example.subscription_service.models;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionRequest {

    private String userId;

    private LocalDate startDate;

    private LocalDate expiryDate;

    private Boolean autoRenew;
}
