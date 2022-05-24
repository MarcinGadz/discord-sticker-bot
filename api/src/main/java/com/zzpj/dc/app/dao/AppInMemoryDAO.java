package com.zzpj.dc.app.dao;

import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Repository
public class AppInMemoryDAO implements AppDAO {

    private Map<String, String> apiKeys = new HashMap<>();

    @PostConstruct
    private void init() {
        apiKeys.put("test-app", "12345678");
        apiKeys.put("other-app", "abcdefgh");
    }

    @Override
    public boolean verifyKeyAndApp(String apiKey, String appName) {
        try {
            return apiKeys.get(appName).equals(apiKey);
        } catch (NullPointerException ex) {
            return false;
        }
    }

    @Override
    public void addKey(String apiKey, String appName) {
        apiKeys.put(appName, apiKey);
    }
}
