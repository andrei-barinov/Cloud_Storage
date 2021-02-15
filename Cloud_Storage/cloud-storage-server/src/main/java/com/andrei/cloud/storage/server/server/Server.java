package com.andrei.cloud.storage.server.server;

import com.andrei.cloud.storage.server.auth.AuthenticationService;

public interface Server {
    boolean isLoggedIn(String nickname);
    void subscribe(ClientHandler client);
    void unsubscribe(ClientHandler client);
    void createDirectory(ClientHandler client);
    void changePassword(ClientHandler client, String message);
    void doCheckIn(String message);
    AuthenticationService getAuthenticationService();

}
