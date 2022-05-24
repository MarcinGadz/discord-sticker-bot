package com.zzpj.dc.app.util;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class EnvironmentUtils {
    @Value("${app.limit.user.add-per-hour}")
    private Integer userAddPerHourLimit;

    @Value("${app.limit.user.max-images}")
    private Integer userMaxImages;

    @Value("${app.limit.user.add-per-day")
    private Integer getUserAddPerDayLimit;
}
