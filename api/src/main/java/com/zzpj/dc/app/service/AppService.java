package com.zzpj.dc.app.service;

import com.zzpj.dc.app.dao.AppDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AppService {
    @Autowired
    private AppDAO dao;

    public boolean verifyKey(String apiKey, String appName) {
        return dao.verifyKeyAndApp(apiKey, appName);
    }
    public void addApp(String apiKey, String appName) {
        dao.addKey(apiKey, appName);
    }
}
