package com.andrei.cloud.storage.server.server.clienthandler;

import com.andrei.cloud.storage.server.server.Server;
import org.apache.log4j.Logger;

import java.io.DataInputStream;
import java.io.DataOutputStream;

public class CheckIn {
    private DataInputStream in;
    private DataOutputStream out;
    private com.andrei.cloud.storage.server.server.Server server;
    private ClientHandler client;
    private String credentials;
    private static final Logger logger = Logger.getLogger(CheckIn.class.getName());


    public CheckIn(DataInputStream in, DataOutputStream out, Server server, ClientHandler client, String credentials) {
        this.in = in;
        this.out = out;
        this.server = server;
        this.client = client;
        this.credentials = credentials;
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

