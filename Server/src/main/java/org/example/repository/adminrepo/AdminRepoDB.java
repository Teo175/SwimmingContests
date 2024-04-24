package org.example.repository.adminrepo;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.domain.Admin;
import org.example.repository.JdbcUtils;
import org.example.repository.PasswordHashing;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

public class AdminRepoDB implements AdminRepo {

    private JdbcUtils jdbcUtils;
    private static final Logger logger = LogManager.getLogger(AdminRepoDB.class);

    public AdminRepoDB(Properties properties) {
        logger.info("Initializing AdminRepoDB  with properties: {} ", properties );
        this.jdbcUtils = new JdbcUtils(properties);
    }
    @Override
    public Admin findOne(Long aLong) {
        logger.traceEntry("Find admin with id: {} ", aLong);

        if(aLong == null) {
            logger.error(new IllegalArgumentException("Id null"));
            throw new IllegalArgumentException("Error! Id cannot be null!");
        }

        Connection con = jdbcUtils.getConnection();
        try(PreparedStatement statement = con.prepareStatement("select * from admin " +
                "where id = ?")){

            statement.setInt(1, Math.toIntExact(aLong));
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()){
                String username = resultSet.getString("username");
                String password = resultSet.getString("password");

                Admin admin = new Admin(username, password);
                admin.setId(aLong);

                logger.traceExit(admin);
                return admin;
            }
        }
        catch (SQLException e){
            logger.error(e);
            throw new RuntimeException(e);
        }

        logger.traceExit("No admin found with id: {}", aLong);
        return null;
    }
    public Optional<Admin> findOneByUsername(String username, String password) {
        logger.traceEntry("Find admin with username: {} and password {} ",username,password );


        Connection con = jdbcUtils.getConnection();
        try(PreparedStatement statement = con.prepareStatement("select * from admin " +
                "where username = ? and password = ?")){

            statement.setString(1,username );
            statement.setString(2,PasswordHashing.hashPassword(password));
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()){
                Long id = resultSet.getLong("id");
                Admin admin = new Admin(username, password);
                admin.setId(id);

                logger.traceExit(admin);
                return Optional.ofNullable(admin);
            }
            else {
                logger.traceExit(username);
                return Optional.empty();
            }
        }
        catch (SQLException e){
            logger.error(e);
            System.out.println("error db"+e);
        }

        logger.traceExit("No admin found with username: {} ",username);
        return Optional.empty();
    }

    @Override
    public Iterable<Admin> findAll() {
        logger.traceEntry("Getting all admins");
        List<Admin> admins = new ArrayList<>();
        Connection con = jdbcUtils.getConnection();

        try (PreparedStatement statement = con.prepareStatement("select * from admin")){

            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next())
            {
                Long id = resultSet.getLong("id");
                String username = resultSet.getString("username");
                String password = resultSet.getString("password");

                Admin admin = new Admin(username, password);
                admin.setId(id);
                admins.add(admin);

            }

            logger.traceExit(admins);
            return admins;
        }
        catch (SQLException e){
            logger.error(e);
            throw new RuntimeException(e);
        }
    }

    public void save(Admin entity) {
        logger.traceEntry("saving admin: {}", entity);
        Connection con = jdbcUtils.getConnection();

        try (PreparedStatement prepStatement = con.prepareStatement(
                "INSERT INTO admin(username, password) VALUES (?, ?)")) {
            prepStatement.setString(1, entity.getUsername());


            String hashedPassword = PasswordHashing.hashPassword(entity.getPassword());
            prepStatement.setString(2, hashedPassword);

            int affectedRows = prepStatement.executeUpdate();
        } catch (SQLException e) {
            logger.error(e);
            System.out.println("Error from DataBase: " + e);
        }

        logger.traceExit();
    }

    @Override
    public void delete(Long aLong) {
        logger.traceEntry("deleting admin with id: {} ", aLong);

        if(aLong == null) {
            logger.error(new IllegalArgumentException("Id null"));
            throw new IllegalArgumentException("Error! Id cannot be null!");
        }

        Connection con = jdbcUtils.getConnection();
        try(PreparedStatement statement = con.prepareStatement("delete from admin " +
                "where id = ?")) {
            statement.setLong(1, aLong);
            int affectedRows = statement.executeUpdate();
        }
        catch(SQLException e){
            logger.error(e);
            throw new RuntimeException(e);
        }

        logger.traceExit();
    }

    @Override
    public void update(Admin entity) {
        logger.traceEntry("updating admin: {} ", entity);

        if(entity.getId() == null) {
            logger.error(new IllegalArgumentException("Id null"));
            throw new IllegalArgumentException("Error! Id cannot be null!");
        }

        Connection con = jdbcUtils.getConnection();
        try(PreparedStatement statement = con.prepareStatement("update admin " +
                "set username = ?,password = ? where id = ?")) {

            statement.setString(1, entity.getUsername());
            statement.setString(2, entity.getPassword());
            statement.setLong(3, entity.getId());

            int affectedRows = statement.executeUpdate();
            if(affectedRows == 0) logger.traceExit("could not update admin: {}", entity);
            else logger.traceExit("updated admin: {} ", entity);
        }
        catch(Exception e){
            logger.error(e);
            throw new RuntimeException(e);
        }
    }
}
