package com.andrei.cloude.storage.common.nio;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.util.concurrent.TimeUnit;

public class Transmitter {

    private static final Logger logger = Logger.getLogger(Transmitter.class.getName());

    public void sendFile(String path, int PORT) throws InterruptedException {
        TimeUnit.SECONDS.sleep(1);
        NioClient nioClient = new NioClient(PORT);
        nioClient.sendFile(path);
        logger.info("Путь отправляемого файла: " + path);
    }

    /**
     * Метод для получения файла. В аргументы передается путь, куда будет загружен файл и размер файла
     * */
    public void receiveFile(String path, long size, int PORT) {
        NioServer nioServer = new NioServer(PORT);
        nioServer.recieveFile(path, size);
    }

    public synchronized Integer getPort(){
        boolean a = false;
        int PORT = 0;
        while (!a){
            for (int port = 1; port <= 65535; port++){
                try {
                    ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();//Открываем сервер соккет
                    serverSocketChannel.bind(new InetSocketAddress(port));
                    serverSocketChannel.close();
                    a = true;
                    PORT = port;
                }catch (IOException ex){
                    continue;
                }
                if(a) break;
            }
        }
        logger.info("Доступный порт: " + PORT);
        return PORT;
    }
}
