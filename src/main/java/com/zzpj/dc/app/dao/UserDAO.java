package com.zzpj.dc.app.dao;

import com.zzpj.dc.app.model.Owner;
import org.springframework.stereotype.Component;

public interface UserDAO {
    Owner getUser(String userId);
}
