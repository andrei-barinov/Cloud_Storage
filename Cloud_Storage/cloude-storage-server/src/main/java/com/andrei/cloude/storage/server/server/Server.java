package com.andrei.cloude.storage.server.server;

import com.andrei.cloude.storage.server.auth.AuthenticationService;

public interface Server {
    boolean isLoggedIn(String nickname);
    void subscribe(ClientHandler client);
    void unsubscribe(ClientHandler client);
    void createDirectory(ClientHandler client);
    AuthenticationService getAuthenticationService();

}
