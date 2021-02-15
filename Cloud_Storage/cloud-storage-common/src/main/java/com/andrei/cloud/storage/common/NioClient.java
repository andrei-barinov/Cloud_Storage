package com.andrei.cloud.storage.common;

import org.apache.log4j.Logger;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;

public class NioClient {
    private static final Logger logger = Logger.getLogger(NioClient.class.getName());
    private int PORT;
    private static final String HOST = "localhost";
    private SocketChannel socketChannel;

    public NioClient(int PORT) {
        this.PORT = PORT;
    }

    public void sendFile(String path){
        try{
            InetSocketAddress serverAddress = new InetSocketAddress(HOST, PORT);//Реализуем IP-адрес сокета
            SocketChannel socketChannel = SocketChannel.open(serverAddress);//Открываем сокет-канкал и связываемся
            // с сервером

            RandomAccessFile file = null;
            try {
                file = new RandomAccessFile(path, "rw");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            FileChannel channel = file.getChannel();
            ByteBuffer buffer = ByteBuffer.allocate(8192);//Создаем буфер и задаем ему размер

            int bytesRead = 0;//Вычисляем сколько байт в буфере
            bytesRead = channel.read(buffer);
            while (bytesRead > -1) {
                buffer.flip();//переключает режим буфера с режима записи на режим чтения. Он также устанавливает позицию
                // обратно в 0 и устанавливает предел, в котором позиция была во время записи.
                while (buffer.hasRemaining()) {
                    socketChannel.write(buffer);//Записываем данные из буфера в соккет канал
                }
                buffer.clear();//Устанавливаем позицию в ноль
                bytesRead = channel.read(buffer);//Вычисляем сколько байт в буфере
            }
            file.close();
            logger.info("Файл успешно передан на NioServer, файл-канал закрыт");

        }catch (IOException ex){
            ex.printStackTrace();
        }
    }


}
