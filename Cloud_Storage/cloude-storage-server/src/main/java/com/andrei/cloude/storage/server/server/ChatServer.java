package com.andrei.cloude.storage.server.server;

import com.andrei.cloude.storage.server.auth.AuthenticationService;
import com.andrei.cloude.storage.server.auth.BasicAuthenticationService;
import com.andrei.cloude.storage.server.auth.UserRepository;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

public class ChatServer implements Server {
    private static final Logger logger = Logger.getLogger(ChatServer.class.getName());
    private Set<ClientHandler> clients;
    private AuthenticationService authenticationService;
    private static final String SERVER_DIRECTORY = "C:\\Users\\Андрей\\Desktop\\Cloud_Storage\\Cloud_Storage\\ServerStorage\\";


    public ChatServer(){
        try{
            logger.info("Сервер запускается...");
            ServerSocket serverSocket = new ServerSocket(7777);
            clients = new HashSet<>();
            authenticationService = new BasicAuthenticationService();
            logger.info("Сервер успешно запущен");


            while (true){
                logger.info("Сервер ождидает подключения клиента...");
                Socket socket = serverSocket.accept();
                logger.info("Клиент успешно подключился. Сокет: " + socket);
                new ClientHandler(this, socket);


            }
        } catch (IOException e){
            logger.error("Выброс исключения", e);
            throw new RuntimeException("Что-то пошло не так", e);
        }

    }



    @Override
    public synchronized boolean isLoggedIn(String nickname) {
        return clients.stream()
                .filter(clientHandler -> clientHandler.getName().equals(nickname))
                .findFirst()
                .isPresent();
    }

    /**
     * Добавляем пользователя к множеству авторизованных пользователей
     * */
    @Override
    public synchronized void subscribe(ClientHandler client) {
        clients.add(client);
        logger.info("Клиент " + client.getName() + " авторизовался");
    }

    /**
     * Удаляем пользователя из множества авторизованных пользователей
     * */
    @Override
    public void unsubscribe(ClientHandler client) {
        clients.remove(client);
        logger.info("Клиент " + client.getName() + " вышел из облака");
    }

    @Override
    public AuthenticationService getAuthenticationService() {
        return authenticationService;
    }


    /**
     * Проверяем существует ли папка на сервере, если нет создаем
     * */
    @Override
    public void createDirectory(ClientHandler client) {
        /**
         * Создаем путь к корневой папке клиента на сервере*/
        String directoryName = SERVER_DIRECTORY + client.getName();
        /**
         * Устанавливаем и сохраняем путь к корневой папке на сервере как свойство клиента*/
        client.setDirectoryServerName(directoryName);
        Path path = Paths.get(directoryName);
        if(!Files.exists(path)){
            new File(directoryName).mkdir();
            logger.info("Создана папка " + directoryName);
        }
        return;
    }

    /**
     * Метод для смены пароля
     * */
    @Override
    public void changePassword(ClientHandler client, String message) {
        String password = message.replaceAll("-changePassword ","");
        UserRepository userRepository = new UserRepository();
        userRepository.changePassword(client.getName(), password);
        logger.info("Пароль успешно изменен");
    }

    /**
     * Регистрация нового пользователя
     * */
    @Override
    public void doCheckIn(String message) {
        logger.info("Вызван метод doCheckIn");
        String[] cred = message.split("\\s");
        String nickname = cred[1];
        String email = cred[2];
        String password = cred[3];
        UserRepository userRepository = new UserRepository();
        userRepository.checkIn(nickname, email, password);
    }
}
