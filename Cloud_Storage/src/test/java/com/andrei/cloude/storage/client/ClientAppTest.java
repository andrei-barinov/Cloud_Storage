package com.andrei.cloude.storage.client;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ClientAppTest {

    @Test
     void shouldCheckGetPort(){
        Assertions.assertEquals(1, ClientApp.getPort());
    }
}
