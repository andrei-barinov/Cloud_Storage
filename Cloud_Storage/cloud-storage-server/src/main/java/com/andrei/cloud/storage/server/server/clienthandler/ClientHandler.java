package com.andrei.cloud.storage.server.server.clienthandler;
import com.andrei.cloud.storage.server.server.Server;
import org.apache.log4j.Logger;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Objects;

public class ClientHandler {
    private com.andrei.cloud.storage.server.server.Server server;
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private String name;
    private String directoryServerName;
    private static final Logger logger = Logger.getLogger(ClientHandler.class.getName());

    public ClientHandler(Server server, Socket socket) {
        try {

            this.server = server;
            this.socket = socket;
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());

            doListen();

        } catch (IOException e){
            throw new RuntimeException("Что-то пошло не так", e);
        }
    }

    public void setDirectoryServerName(String directoryServerName) {
        this.directoryServerName = directoryServerName;
    }

    public String getDirectoryServerName() {
        return directoryServerName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) { this.name = name; }

    private void doListen(){
        new Thread(() ->{
            try{
                doAuth();
                receiveMessage();
            } catch (Exception e){
                throw new RuntimeException("Что-то пошло не так", e);
            } finally {
                server.unsubscribe(this);
            }
        }).start();
    }

    private void doAuth(){
        AuthService authService = new AuthService(this.in, this.out, this.server, this);
        authService.doAuth();
    }

    private void receiveMessage(){
        ReceiverMessage RM = new ReceiverMessage(this.in, this.out, this.server, this);
        RM.receiveMessage();
    }

    public void sendMessage(String message){
        try {
            out.writeUTF(message);
            logger.info("Сообщение отправленное с сервера клиенту: "+ message);
        } catch (IOException e){
            throw new RuntimeException("Что-то пошло не так", e);
        }
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClientHandler that = (ClientHandler) o;
        return Objects.equals(server, that.server) &&
                Objects.equals(socket, that.socket) &&
                Objects.equals(in, that.in) &&
                Objects.equals(out, that.out) &&
                Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(server, socket, in, out, name);
    }
}


