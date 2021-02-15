package com.andrei.cloud.storage.client;

import org.apache.log4j.Logger;

import java.io.File;

public class MessageClientHandler {
    private String message;
    private static final String DIRECTORY_CLIENT_NAME = "C:/Users/Андрей/Desktop/Cloud_Storage/Cloud_Storage/download/";
    private static final Logger logger = Logger.getLogger(MessageClientHandler.class.getName());

    public MessageClientHandler(String message) {
        this.message = message;
    }


    /**
     * Метод возвращает путь к файлу из исходящего сообщения
     * */
    public String getPath(){
        String[] IncMes = message.split("%%%");
        return IncMes[1];
    }

    /**
     * Получаем размер передаваемого файла на сервер
     * */
    public long getSizeOfFile(){
        String path = getPath();
        File file = new File(path);
        long size = file.length();
        return size;
    }

    /**
     * Получаем размер загружаемого файла с сервера из входящего сообщения
     * */
    public long getSizeOfFileForClient(){
        String[] IncMes = message.split("%%%");
        long size = Long.parseLong(IncMes[2]);
        logger.info("Размер загружаемого файла: " + IncMes[2] + " байт");
        return size;
    }

    /**
     * Генерируем путь к файлу на сервере для метода проверки
     * */
    public String producePathFileOnServer(){
        String[] messageArr = message.split("%%%");
        String pathOfFile = DIRECTORY_CLIENT_NAME + messageArr[1];
        logger.info("Путь для выгрузки файла: " + pathOfFile);
        return pathOfFile;
    }


    /**
     * Получаем порт из входящего сообщения
     * */
    public Integer getPortFromMessage(){
        String[] IncMes = message.split("%%%");
        int PORT = Integer.valueOf(IncMes[3]);
        return PORT;
    }
}
