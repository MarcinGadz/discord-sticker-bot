package com.zzpj.dc.app.dao;

import com.zzpj.dc.app.model.Owner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class UserInMemoryDAO implements UserDAO{
    private List<Owner> owners = new ArrayList<>();
    @Override
    public Owner getUser(String userId) {
        return owners.stream()
                .filter(owner -> owner.getId().equals(userId))
                .findFirst().orElse(null);
    }
}
