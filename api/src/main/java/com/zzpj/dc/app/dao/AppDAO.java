package com.zzpj.dc.app.dao;

public interface AppDAO {
    boolean verifyKeyAndApp(String apiKey, String appName);
    void addKey(String apiKey, String appName);
}
