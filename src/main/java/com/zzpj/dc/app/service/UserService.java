package com.zzpj.dc.app.service;

import com.zzpj.dc.app.model.Owner;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    public Owner getOwnerByName(String name) {
        return new Owner();
    }
}
