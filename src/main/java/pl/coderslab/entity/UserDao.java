package pl.coderslab.entity;

import org.mindrot.jbcrypt.BCrypt;

import pl.coderslab.DbUtil;
import pl.coderslab.entity.User;

import java.sql.*;
import java.util.Arrays;


public class UserDao {

    private static final String INSERT_USER = "INSERT INTO users(email, username, password) VALUES (?,?,?);";
    private static final String UPDATE_USER = "UPDATE users SET email = ?, username = ?, password = ? WHERE id = ?;";
    private static final String SELECT_USER = "SELECT * FROM users WHERE id = ?;";
    private static final String DELETE_USER = "DELETE FROM users WHERE id = ?;";
    private static final String SELECT_ALL = "SELECT * FROM users;";

    public static void main(String[] args) {
        UserDao userDao = new UserDao();
        //User user = new UserDao().create(new User("Kamilk@gmail.com","KamilK","TomaszK"));
        //System.out.println( new UserDao().read(1));
        //userDao.upload(new User(3,"katarzynakowalska@wp.pl","KasiaK","KasiaK"));
        //userDao.delete(1);
        //System.out.println(Arrays.toString(userDao.findAll()));

    }

    public String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());

    }
    public User create(User user){
        try(Connection conn = DbUtil.connect(); PreparedStatement stmt = conn.prepareStatement(INSERT_USER, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getUsername());
            stmt.setString(3, hashPassword(user.getPassword()));
            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                int id = rs.getInt(1);
                user.setId(id);
            }
            return user;
        }catch (SQLException e){
            e.printStackTrace();
            return null;
        }
    }


    public User read(int id){
        try(Connection conn = DbUtil.connect(); PreparedStatement stmt = conn.prepareStatement(SELECT_USER)) {
            stmt.setString(1, String.valueOf(id));
            ResultSet resultSet = stmt.executeQuery();
            while (resultSet.next()) {
                String email = resultSet.getString("email");
                String username = resultSet.getString("username");
                String password = resultSet.getString("password");
                return new User(id, email, username, password);
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }


    public void upload(User user){
        try(Connection conn = DbUtil.connect(); PreparedStatement stmt = conn.prepareStatement(UPDATE_USER)) {
            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getUsername());
            stmt.setString(3, hashPassword(user.getPassword()));
            stmt.setString(4, String.valueOf(user.getId()));
            stmt.executeUpdate();
        } catch (SQLException e){
            e.printStackTrace();
        }
    }


    public void delete(int id){
        try(Connection conn = DbUtil.connect(); PreparedStatement stmt = conn.prepareStatement(DELETE_USER)) {
            stmt.setString(1, String.valueOf(id));
            stmt.executeUpdate();
        } catch (SQLException e){
            e.printStackTrace();
        }
    }


    public User[] findAll(){
        User[] users = new User[0];
        try(Connection conn = DbUtil.connect(); PreparedStatement stmt = conn.prepareStatement(SELECT_ALL)) {
            ResultSet resultSet = stmt.executeQuery();
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String email = resultSet.getString("email");
                String username = resultSet.getString("username");
                String password = resultSet.getString("password");
                users = addToArray(users, new User(id,email,username,password));
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
        return users;
    }
    public static User[] addToArray(User[] users, User user) {
        users = Arrays.copyOf(users, users.length + 1);
        users[users.length - 1] = user;
        return users;
    }
}
