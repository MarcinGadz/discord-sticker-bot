package com.zzpj.dc.app.dao;

import com.zzpj.dc.app.model.Owner;

public interface UserDAO {
    Owner getUser(String userId);
}
