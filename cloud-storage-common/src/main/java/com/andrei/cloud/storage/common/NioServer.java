package com.andrei.cloud.storage.common;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.FileChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class NioServer {
    private ServerSocketChannel serverSocketChannel;
    private SocketChannel socketChannel;
    private int PORT;
    private static final Logger logger = Logger.getLogger(NioServer.class.getName());

    public NioServer(int PORT) {
        this.PORT = PORT;
    }

    public void recieveFile(String pathToServer, long fileSize){
        try{

            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();//Открываем сервер соккет
            serverSocketChannel.bind(new InetSocketAddress(PORT));//Устанавливаем связь соккета-канала с портом
            SocketChannel socketChannel = serverSocketChannel.accept();//Подключение нио-клиента
            System.out.println("Клиент подключился " + socketChannel);

            FileChannel fileChannel = FileChannel.open(Paths.get(pathToServer), StandardOpenOption.CREATE, StandardOpenOption.WRITE);

            long totalSize = fileSize;

            fileChannel.transferFrom(socketChannel, 0, totalSize);//Передаем байты из потока
            // socketChannel в поток fileChannel и дальше по указаному пути создается файл
            fileChannel.close();
            socketChannel.close();
            serverSocketChannel.close();
            logger.info("Файл успешно сохранен, потоки закрыты");
        }catch (IOException ex){
            ex.printStackTrace();
        }
    }



}
