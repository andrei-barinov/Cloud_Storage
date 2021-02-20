package com.andrei.cloud.storage.client;

import com.andrei.cloud.storage.common.Transmitter;
import org.apache.log4j.Logger;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class Client {
    private static final Logger logger = Logger.getLogger(Client.class.getName());
     {
        try {
            Socket socket = new Socket("localhost", 7777);
            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());

            AtomicBoolean isDrop = new AtomicBoolean(false);

            ExecutorService executorService = Executors.newFixedThreadPool(1);

            executorService.execute(new Thread(() ->{
                try {
                    while (true){
                        String message = in.readUTF();
                        MessageClientHandler mch = new MessageClientHandler(message);
                        Transmitter transmitter = new Transmitter();
                        if(message.equals("-exit")){
                            isDrop.set(true);
                            break;
                        }
                        else if(message.equals("Аутентификация прошла успешно")){
                            logger.info("Аутентификация прошла успешно");
                        }

                        else if(message.startsWith("-upload")){
                            if (Files.exists(Paths.get(mch.getPath()))){
                                int PORT = transmitter.getPort();
                                out.writeUTF("-file_exists%%%" + Paths.get(mch.getPath()) + "%%%"
                                        + mch.getSizeOfFile() + "%%%" + PORT);
                                transmitter.sendFile(mch.getPath(), PORT);
                                logger.info("Такой файл существует. Запускается метод sendFile для отправки файла");
                            }
                            else logger.info("Такого файла не существует");
                        }
                        else if(message.contains("-exists_file")){
                            int PORT = mch.getPortFromMessage();
                            out.writeUTF(message);
                            transmitter.receiveFile(mch.producePathFileOnServer(), mch.getSizeOfFileForClient(), PORT);
                        }
                        else logger.info(message);
                    }
                    System.out.println("Для завершения работы нажмите Enter");
                    executorService.shutdown();
                } catch (IOException | InterruptedException e){
                    throw new RuntimeException("Что-то пошло не так", e);
                }
            }));

            if(!isDrop.get()){

                BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

                try {
                    while (true){
                        out.writeUTF(reader.readLine());
                        if(isDrop.get()){
                            break;
                        }
                    }
                } catch (IOException e){
                    throw new RuntimeException("Что-то пошло не так", e);
                }
            }
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}
