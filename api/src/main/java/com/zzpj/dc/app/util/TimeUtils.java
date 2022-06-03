package com.zzpj.dc.app.util;

import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * Class to get data about time
 * Methods were externalized to make business logic 'testable'
 */
@Component
public class TimeUtils {

    public long getCurrentMilis() {
        return System.currentTimeMillis();
    }

    public LocalDate getCurrentDay() {
        return LocalDate.now();
    }
}
