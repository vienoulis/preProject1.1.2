package service;

import com.google.gson.Gson;
import model.User;
import usersDAO.UserDAO;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserService {

    public List<User> getAllUsers() {
        try {
            return getUserDAO().getAllUsers();
        } catch (SQLException e) {
            return null;
        }
    }

    private static Connection getMysqlConnection() {
        try {
            DriverManager.registerDriver((Driver) Class.forName("com.mysql.cj.jdbc.Driver").newInstance());
            StringBuilder url = new StringBuilder();
            url.
                    append("jdbc:mysql://").        //db type
                    append("localhost:").           //host name
                    append("3306/").                //port
                    append("db_example?").          //db name
                    append("user=root&").          //login
                    append("password=1234").
                    append("&serverTimezone=UTC");       //password

            System.out.println("URL: " + url + "\n");
            Connection connection = DriverManager.getConnection(url.toString());
            return connection;
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | SQLException e) {
            e.printStackTrace();
            System.out.printf(e.getMessage());
            throw new IllegalStateException();
        }
    }

    private static UserDAO getUserDAO() {
        return new UserDAO(getMysqlConnection());
    }

    public void addUser(String name, String age, String passport) {
        User user = new User(name,
                age == null ? 0 : Integer.parseInt(age),
                passport == null ? 0 : Long.parseLong(passport));
        try {
            if (!getAllUsers().contains(user)) {
                getUserDAO().addUser(user);
            }
        } catch (SQLException e) {

        }
    }

    public List<User> getAllSortUsers(String name, String age, String passprt) {
        List<User> users = new ArrayList<>();
        try {
            if (name != null) {
                users = getUserDAO().getAllSortUsers(name);
            }
            if (age != null) {
                users = getUserDAO().getAllSortUsers(Integer.parseInt(age));
            }
            if (passprt != null) {
                users = getUserDAO().getAllSortUsers(Long.parseLong(passprt));
            }
        } catch (SQLException e) {
            return users;
        }
        return users;
    }

    public String update(String name, String age, String passport, String newPassport, String newAge) {
        String result = "Update false";
        try {
            if (name != null && age != null && passport != null && (newPassport == null || newAge == null)) {
                if (newPassport != null) {
                    result = getUserDAO().update(Integer.parseInt(age), name,
                            Long.parseLong(passport), Long.parseLong(newPassport));
                } else {
                    result = getUserDAO().update(name, Integer.parseInt(age),
                            Long.parseLong(passport), Integer.parseInt(newAge));
                }
            }
        } catch (SQLException e) {
            result = "Update Exception";
        }
        return result;
    }

    public String delete(String name, String age, String passport) {
        String result = "Delete false";
        try {
            if (name != null && age != null && passport != null) {
                User user = new User(name, Integer.parseInt(age), Long.parseLong(passport));
                if (getAllUsers().contains(user)) {
                    getUserDAO().delete(user);
                    return new Gson().toJson(user) + "was deleted!";
                }
            }
        } catch (SQLException e ){
            return "Delete exception";
        }
        return result;
    }
}
