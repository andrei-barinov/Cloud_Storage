package com.andrei.cloud.storage.server.server.clienthandler;

import com.andrei.cloud.storage.server.server.Server;
import org.apache.log4j.Logger;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.concurrent.atomic.AtomicBoolean;

public class SignIn {
    private DataInputStream in;
    private DataOutputStream out;
    private com.andrei.cloud.storage.server.server.Server server;
    private ClientHandler client;
    private String credentials;
    private static final Logger logger = Logger.getLogger(SignIn.class.getName());
    private AtomicBoolean isAuth;

    public SignIn(DataInputStream in, DataOutputStream out, Server server, ClientHandler client, String credentials, AtomicBoolean isAuth) {
        this.in = in;
        this.out = out;
        this.server = server;
        this.client = client;
        this.credentials = credentials;
        this.isAuth = isAuth;
    }

    public void doSignIn(){
        /**
         * После сплитинга получим массив:
         * ["-auth", "n1@mail.com", "1"]
         */
        String[] credentialValues = credentials.split("\\s");
        server.getAuthenticationService()
                .doAuth(credentialValues[1], credentialValues[2])
                .ifPresentOrElse(
                        user ->  {
                            if(!server.isLoggedIn(user.getNickname())) {
                                client.sendMessage("Аутентификация прошла успешно");
                                client.setName(user.getNickname());
                                server.subscribe(client);
                                server.createDirectory(client);
                                isAuth.set(true);
                            }else {
                                client.sendMessage("Текущий пользователь уже зарегистрирован");
                            }
                        },
                        new Runnable(){
                            @Override
                            public void run() {
                                client.sendMessage("Не найден пользователь с таким email и паролем");
                            }
                        }
                );
    }

    public void doChekIn(){
        /**
         * После сплитинга получим массив:
         * ["-checkIn", alex, "alex@email.com", "4"]
         */
        String[] credentialValues = credentials.split("\\s");
        server.getAuthenticationService()
                .doAuth(credentialValues[1], credentialValues[2])
                .ifPresentOrElse(
                        user ->  {
                            client.sendMessage("Текущий пользователь уже зарегистрирован");
                        },
                        new Runnable(){
                            @Override
                            public void run() {
                                server.doCheckIn(credentials);
                                client.sendMessage("Данные пользователя успешно добавлены в БД. Пройдите " +
                                        "аутентификацию");
                                logger.info("Данные пользователя успешно добавлены в БД. Пройдите " +
                                        "аутентификацию");
                            }
                        }
                );
    }
}
