package com.andrei.cloud.storage.client;

import com.andrei.cloud.storage.server.server.MessageServerHandler;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class MessageClientHandlerTest {

    @Test
    void shouldCheckGetPath(){
        MessageClientHandler msh = new MessageClientHandler("-upload%%%D:\\Загрузки\\file.txt");
        Assertions.assertEquals("D:\\Загрузки\\file.txt", msh.getPath());
    }

    @Test
    void shouldCheckGetSizeOfFile(){
        MessageClientHandler msh = new MessageClientHandler("-upload%%%D:\\Загрузки\\file.txt");
        Assertions.assertEquals(32, msh.getSizeOfFile());
    }

    @Test
    void  shouldCheckGetSizeOfFileForClient(){
        MessageClientHandler msh = new MessageClientHandler("-file_exists%%%D:\\Загрузки\\file.txt%%%32%%%1");
        Assertions.assertEquals(32, msh.getSizeOfFileForClient());
    }

    @Test
    void shouldProducePathFileOnServer(){
        MessageClientHandler msh = new MessageClientHandler("-download%%%file.txt");
        Assertions.assertEquals("C:/Users/Андрей/Desktop/Cloud_Storage/Cloud_Storage/download/file.txt", msh.producePathFileOnServer());
    }

    @Test
    void shouldGetPortFromMessage(){
        MessageClientHandler msh = new MessageClientHandler("-file_exists%%%D:\\Загрузки\\file.txt%%%32%%%1");
        Assertions.assertEquals(1, msh.getPortFromMessage());
    }
}
