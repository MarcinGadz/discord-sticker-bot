package com.zzpj.dc.app.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Class representing environment/config file variables
 */
@Component
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class EnvironmentUtils {
    @Value("${app.limit.user.add-per-hour}")
    private Integer userAddPerHourLimit;

    @Value("${app.limit.user.max-images}")
    private Integer userMaxImages;

    @Value("${app.limit.user.add-per-day}")
    private Integer getUserAddPerDayLimit;

    @Value("${app.api.key}")
    private String apiKey;
}
