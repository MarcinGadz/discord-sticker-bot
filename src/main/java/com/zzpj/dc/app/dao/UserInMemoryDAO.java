package com.zzpj.dc.app.dao;

import com.zzpj.dc.app.model.User;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class UserInMemoryDAO implements UserDAO{
    private List<User> users = new ArrayList<>();
    @Override
    public User getUser(String userId) {
        return users.stream()
                .filter(user -> user.getId().equals(userId))
                .findFirst().orElse(null);
    }
}
