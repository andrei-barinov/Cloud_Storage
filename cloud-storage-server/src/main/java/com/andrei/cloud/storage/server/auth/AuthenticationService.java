package com.andrei.cloud.storage.server.auth;

import com.andrei.cloud.storage.server.entity.User;

import java.util.Optional;

public interface AuthenticationService {
    Optional<User> doAuth(String login, String password);
}
