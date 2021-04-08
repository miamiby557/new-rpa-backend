package com.szcinda.rpa.service;



import com.szcinda.rpa.repository.User;

import java.util.List;

public interface UserService {
    List<User> findAll();
}
