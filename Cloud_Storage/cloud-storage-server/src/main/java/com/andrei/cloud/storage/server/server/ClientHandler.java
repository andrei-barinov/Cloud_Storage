package com.andrei.cloud.storage.server.server;
import com.andrei.cloud.storage.common.Transmitter;
import org.apache.log4j.Logger;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

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
        try {
            while (true){
                String credentials = in.readUTF();
                AtomicBoolean isAuth = new AtomicBoolean(false);
                /**
                 * "-auth andrei@email.com 1"
                 */
                if(credentials.startsWith("-auth")){
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
                                            sendMessage("Аутентификация прошла успешно");
                                            name = user.getNickname();
                                            server.subscribe(this);
                                            server.createDirectory(this);
                                            isAuth.set(true);
                                        }else {
                                            sendMessage("Текущий пользователь уже зарегистрирован");
                                        }
                                    },
                                    new Runnable(){
                                        @Override
                                        public void run() {
                                            sendMessage("Не найден пользователь с таким email и паролем");
                                        }
                                    }
                            );
                }
                else if(credentials.startsWith("-checkIn")){
                    /**
                     * После сплитинга получим массив:
                     * ["-checkIn", alex, "alex@email.com", "4"]
                     */
                    String[] credentialValues = credentials.split("\\s");
                    server.getAuthenticationService()
                            .doAuth(credentialValues[1], credentialValues[2])
                            .ifPresentOrElse(
                                    user ->  {
                                        sendMessage("Текущий пользователь уже зарегистрирован");
                                    },
                                    new Runnable(){
                                        @Override
                                        public void run() {
                                            server.doCheckIn(credentials);
                                            sendMessage("Данные пользователя успешно добавлены в БД. Пройдите " +
                                                    "аутентификацию");
                                            logger.info("Данные пользователя успешно добавлены в БД. Пройдите " +
                                                    "аутентификацию");
                                        }
                                    }
                            );
                }
                if(isAuth.get()){
                    break;
                }
            }
        }catch (IOException e){
            throw new RuntimeException("Что-то пошло не так", e);
        }
    }

    private void receiveMessage(){
        try {
            Date date = new Date();
            while (true){
                String message = in.readUTF();
                MessageServerHandler msh = new MessageServerHandler(message, this);
                Transmitter transmitter = new Transmitter();
                logger.info("Входящая команда на сервер: " + message);
                if(message.equals("-exit")){
                    out.writeUTF("-exit");
                    server.unsubscribe(this);
                    return;
                }
                else if(message.startsWith("-upload")) out.writeUTF(msh.getMessage());
                else if(message.startsWith("-file_exists")){
                    transmitter.receiveFile(msh.generatePathFileOnServer(), msh.getSizeOfFile(),
                            msh.getPortFromMessage());
                    out.writeUTF("Файл сохранен в облаке");
                }
                else if(message.startsWith("-list")){
                    if(msh.getListOfFile()==null) sendMessage("В хранилище нет файлов");
                    else msh.getListOfFile().stream().forEach(this::sendMessage);
                }
                else if(message.startsWith("-download")){
                    if(Files.exists(Paths.get(msh.producePathFileOnServer()))){
                        sendMessage("-exists_file%%%" + msh.getName() + "%%%" +
                                msh.getSizeOfFileForServer() + "%%%"
                                + transmitter.getPort());
                    }
                    else sendMessage("Неправильно введено имя файла");
                }
                else if(message.startsWith("-exists_file")){
                    transmitter.sendFile(msh.producePathFileOnServer(), msh.getPortFromMessage());
                }
                else if(message.startsWith("-delete")){
                    if(Files.exists(Paths.get(msh.generatePathFileOnServer()))){
                        msh.deleteFile();
                        sendMessage("Файл удален из облака");
                    }
                    else sendMessage("Неверно введено имя файла");
                }
                else if(message.startsWith("-changePassword")){
                    server.changePassword(this, message);
                    sendMessage("Пароль успешно изменен");
                }
                else sendMessage("Неизвестная команда");
            }
        } catch (IOException | InterruptedException e){
            throw new RuntimeException("Что-то пошло не так", e);
        }
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


