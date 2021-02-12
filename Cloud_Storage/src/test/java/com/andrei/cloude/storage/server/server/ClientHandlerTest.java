package com.andrei.cloude.storage.server.server;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ClientHandlerTest {

    @Test
    void shouldCheckMakeNewMessage(){
        Assertions.assertEquals("-upload%%%D:\\Загрузки\\file.txt", ClientHandler.makeNewMessageUpload("-upload D:\\Загрузки\\file.txt"));
    }

    @Test
    void shouldCheckMakeNewMessageDownload(){
        Assertions.assertEquals("-download%%%D:\\Загрузки\\file.txt", ClientHandler.makeNewMessageDownload("-download D:\\Загрузки\\file.txt"));
    }

    @Test
    void shouldCheckMakeNewMessageDelete(){
        Assertions.assertEquals("-delete%%%D:\\Загрузки\\file.txt", ClientHandler.makeNewMessageDelete("-delete D:\\Загрузки\\file.txt"));
    }

}
