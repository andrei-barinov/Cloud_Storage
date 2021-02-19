package com.andrei.cloud.storage.server.server.clienthandler;

import com.andrei.cloud.storage.server.server.Server;
import org.apache.log4j.Logger;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

public class AuthService {
    private DataInputStream in;
    private DataOutputStream out;
    private static final Logger logger = Logger.getLogger(AuthService.class.getName());
    private com.andrei.cloud.storage.server.server.Server server;
    private ClientHandler client;

    public AuthService(DataInputStream in, DataOutputStream out, Server server, ClientHandler client) {
        this.in = in;
        this.out = out;
        this.server = server;
        this.client = client;
    }

    public void doAuth(){
        try {
            while (true){
                String credentials = in.readUTF();
                AtomicBoolean isAuth = new AtomicBoolean(false);
                /**
                 * "-auth andrei@email.com 1"
                 */
                if(credentials.startsWith("-auth")){
                   SignIn signIn = new SignIn(this.in, this.out, this.server, client, credentials, isAuth);
                   signIn.doSignIn();
                }
                else if(credentials.startsWith("-checkIn")){
                   CheckIn checkIn = new CheckIn(this.in, this.out, this.server, client, credentials);
                   checkIn.doChekIn();
                }
                if(isAuth.get()){
                    break;
                }
            }
        }catch (IOException e){
            throw new RuntimeException("Что-то пошло не так", e);
        }
    }
}
