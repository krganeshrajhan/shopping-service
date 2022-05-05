package com.shopping.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

public class UserDao {
    private static final Logger logger = Logger.getLogger(UserDao.class.getName());

    public User getDetails(String username) {
        User user = new User();

        try {
            Connection connection = H2DatabaseConnection.getConnectionToDatabase();
            PreparedStatement statement = connection.prepareStatement("select * from user where username=?");
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                user.setId(resultSet.getInt("id"));
                user.setName(resultSet.getString("name"));
                user.setAge(resultSet.getInt("age"));
                user.setGender(resultSet.getString("gender"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }
}
