package com.example.aram.dto;

import com.example.aram.enums.ExchangeType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExchangeConfigDto {
    private ExchangeType exchangeType;
    private String apiKey;
    private String apiSecret;
    private Boolean enabled;
    private Double feePercentage;
    private Double withdrawalFee;
}

