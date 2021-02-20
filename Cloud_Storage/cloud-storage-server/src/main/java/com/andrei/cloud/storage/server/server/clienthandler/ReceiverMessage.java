package com.andrei.cloud.storage.server.server.clienthandler;

import com.andrei.cloud.storage.common.Transmitter;
import com.andrei.cloud.storage.server.server.Server;
import org.apache.log4j.Logger;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ReceiverMessage {
    private DataInputStream in;
    private DataOutputStream out;
    private com.andrei.cloud.storage.server.server.Server server;
    private ClientHandler client;
    private static final Logger logger = Logger.getLogger(ReceiverMessage.class.getName());

    public ReceiverMessage(DataInputStream in, DataOutputStream out, Server server, ClientHandler client) {
        this.in = in;
        this.out = out;
        this.server = server;
        this.client = client;
    }

    public void receiveMessage(){
        try {
            while (true){
                String message = in.readUTF();
                MessageServerHandler msh = new MessageServerHandler(message, client);
                Transmitter transmitter = new Transmitter();
                logger.info("Входящая команда на сервер: " + message);
                if(message.equals("-exit")){
                    out.writeUTF("-exit");
                    server.unsubscribe(client);
                    return;
                }
                else if(message.startsWith("-upload")) out.writeUTF(msh.getMessage());
                else if(message.startsWith("-file_exists")){
                    transmitter.receiveFile(msh.generatePathFileOnServer(), msh.getSizeOfFile(),
                            msh.getPortFromMessage());
                    out.writeUTF("Файл сохранен в облаке");
                }
                else if(message.startsWith("-list")){
                    if(msh.getListOfFile()==null) client.sendMessage("В хранилище нет файлов");
                    else msh.getListOfFile().stream().forEach(client::sendMessage);
                }
                else if(message.startsWith("-download")){
                    if(Files.exists(Paths.get(msh.producePathFileOnServer()))){
                        client.sendMessage("-exists_file%%%" + msh.getName() + "%%%" +
                                msh.getSizeOfFileForServer() + "%%%"
                                + transmitter.getPort());
                    }
                    else client.sendMessage("Неправильно введено имя файла");
                }
                else if(message.startsWith("-exists_file")){
                    transmitter.sendFile(msh.producePathFileOnServer(), msh.getPortFromMessage());
                }
                else if(message.startsWith("-delete")){
                    if(Files.exists(Paths.get(msh.generatePathFileOnServer()))){
                        msh.deleteFile();
                        client.sendMessage("Файл удален из облака");
                    }
                    else client.sendMessage("Неверно введено имя файла");
                }
                else if(message.startsWith("-changePassword")){
                    server.changePassword(client, message);
                    client.sendMessage("Пароль успешно изменен");
                }
                else client.sendMessage("Неизвестная команда");
            }
        } catch (IOException | InterruptedException e){
            throw new RuntimeException("Что-то пошло не так", e);
        }
    }
}
