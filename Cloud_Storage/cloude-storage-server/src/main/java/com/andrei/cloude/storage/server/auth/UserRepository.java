package com.andrei.cloude.storage.server.auth;

import com.andrei.cloude.storage.server.db.DataSource;
import com.andrei.cloude.storage.server.entity.User;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class UserRepository {
    private static final Logger logger = Logger.getLogger(UserRepository.class.getName());

    public Optional<User> findUserByEmailAndPassword(String email, String password){
        Connection connection = DataSource.getConnection();
        logger.info("Подключение к БД прошло успешно");
        try {
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT * FROM data WHERE email = ? AND password = ?"
            );
            statement.setString(1, email);
            statement.setString(2, password);

            ResultSet rs = statement.executeQuery();

            if (rs.next()){
                 return Optional.of(new User(
                        rs.getString("nickname"),
                        rs.getString("email"),
                        rs.getString("password")
                ));
            }
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
        return Optional.empty();
    }

    public void changePassword(String nickname, String newPassword){
        logger.info("Вызван метод changePassword()");
        Connection connection = DataSource.getConnection();
        logger.info("Подключение к БД прошло успешно");
        try {
            PreparedStatement statement = connection.prepareStatement(
                    "UPDATE data SET password = ? WHERE nickname = ?"
            );

            statement.setString(1, newPassword);
            statement.setString(2, nickname);

            statement.executeUpdate();

        }catch (SQLException ex){
            ex.printStackTrace();
        }
    }

    public void checkIn(String nickname, String email, String password){
        logger.info("Вызван метод checkIn");
        Connection connection = DataSource.getConnection();
        logger.info("Подключение к БД прошло успешно");
        try {
            PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO data (nickname, email, password) VALUES (?, ?, ?)"
            );

            statement.setString(1, nickname);
            statement.setString(2, email);
            statement.setString(3, password);

            statement.executeUpdate();

        }catch (SQLException ex){
            ex.printStackTrace();
        }
    }
}
