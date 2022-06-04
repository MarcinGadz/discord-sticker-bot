package com.zzpj.dc.app.service;

import com.zzpj.dc.app.util.EnvironmentUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AppService {
    private final String key;

    @Autowired
    public AppService(EnvironmentUtils envUtils) {
        this.key = envUtils.getApiKey();
    }

    public boolean verifyKey(String apiKey) {
        return key.equals(apiKey);
    }
}
