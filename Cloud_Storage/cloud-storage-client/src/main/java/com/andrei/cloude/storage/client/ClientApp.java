package com.andrei.cloude.storage.client;

import com.andrei.cloude.storage.common.nio.NioClient;
import com.andrei.cloude.storage.common.nio.NioServer;
import org.apache.log4j.Logger;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.channels.ServerSocketChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class ClientApp {
    public static void main(String[] args) {
        new Client();
    }
}
