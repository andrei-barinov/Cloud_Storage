package com.andrei.cloud.storage.server.server.clienthandler;

import org.apache.log4j.Logger;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MessageServerHandler {
    private static final Logger logger = Logger.getLogger(MessageServerHandler.class.getName());
    private String message;
    private ClientHandler client;

    public MessageServerHandler(String message, ClientHandler client) {
        if (message.startsWith("-upload " )){
            String newMSg = message.replace("-upload ", "-upload%%%");
            this.message = newMSg;
        }
        else if(message.startsWith("-download ")){
            String newMSg = message.replace("-download ", "-download%%%");
            this.message = newMSg;
        }
        else if(message.startsWith("-delete ")){
            String newMSg = message.replace("-delete ", "-delete%%%");
            this.message = newMSg;
        }
        else this.message = message;
        this.client = client;
    }

    public String getMessage() {
        return message;
    }

    /**
     * Генерируем путь к файлу на сервере
     * */
    public String generatePathFileOnServer(){
        String[] messageArr = message.split("%%%");
        File f = new File(messageArr[1]);
        String pathOfFile = client.getDirectoryServerName() + "\\" + f.getName();
        logger.info("Путь к файлу на сервере сгенерирован: " + pathOfFile);
        return pathOfFile;
    }

    /**
     * Генерируем путь к файлу на сервере для метода проверки и отправки файла
     * */
    public String producePathFileOnServer(){
        String[] messageArr = message.split("%%%");
        String pathOfFile = client.getDirectoryServerName() + "/" + messageArr[1];
        logger.info("Путь к файлу на сервере: " + pathOfFile);
        return pathOfFile;
    }

    /**
     * Получаем список файлов доступных пользователю для скачивания и отправляем его клиенту
     * */
    public List<String> getListOfFile(){
        logger.info("Вызван метод getListOfFile(). Файлы доступные для скачивания:");
        try{
            File folder = new File(client.getDirectoryServerName());
            File[] Files = folder.listFiles();
            if(Files == null) return null;
            else {
                List<String> listOfFiles = Arrays.stream(Files).map(File::getName).collect(Collectors.toList());
                return listOfFiles;
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * Метод возвращает имя файла
     * */
    public String getName(){
        String[] IncMes = message.split("%%%");
        logger.info("Имя файла: " + IncMes[1]);
        return IncMes[1];
    }

    /**
     * Из входящего от клиента сообщения извлекаем размер файла который сервер должен принять
     * */
    public long getSizeOfFile(){
        String[] IncMes = message.split("%%%");
        long size = Long.parseLong(IncMes[2]);
        logger.info("Размер отправляемого файла: " + IncMes[2] + " байт");
        return size;
    }


    /**
     * Получаем размер передаваемого файла расположенного на сервере
     *
     **/
    public long getSizeOfFileForServer(){
        File file = new File(generatePathFileOnServer());
        long size = file.length();
        logger.info("Размер исходящего файла: " + size);
        return size;
    }

    /**
     * Удаляем файл из облака
     * */

    public void deleteFile(){
        File file = new File(generatePathFileOnServer());
        file.delete();
        logger.info("Файл удален из облака");
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
