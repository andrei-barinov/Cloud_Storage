package com.andrei.cloude.storage.server.auth;

import com.andrei.cloude.storage.server.entity.User;

import java.util.List;
import java.util.Optional;

public class BasicAuthenticationService implements AuthenticationService {
    /**
     * База данных пользователей
     * */

    private static final List<User> users;

    static{
        users = List.of(
                new User("andrei", "andrei@mail.com", "1"),
                new User("ivan", "ivan@mail.com", "2"),
                new User("oleg", "oleg@mail.com", "3")
        );
    }
    @Override
    public Optional<User> doAuth(String email, String password) {
        for(User user: users){
            if(user.getEmail().equals(email) && user.getPassword().equals(password)){
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }
}
