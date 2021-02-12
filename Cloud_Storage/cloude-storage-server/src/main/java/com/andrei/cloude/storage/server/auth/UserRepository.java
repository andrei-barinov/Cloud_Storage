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
        logger.info("Подключение у БД прошло успешно");
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
}
