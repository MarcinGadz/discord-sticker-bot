package com.zzpj.dc.app.service;

import com.zzpj.dc.app.dao.UserDAO;
import com.zzpj.dc.app.model.Owner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private UserDAO userDAO;

    @Autowired
    public void setUserDAO(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public Owner getOwnerByName(String name) {
        return userDAO.getUser(name);
    }
}
