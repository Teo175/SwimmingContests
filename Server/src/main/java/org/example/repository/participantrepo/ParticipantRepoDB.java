package org.example.repository.participantrepo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.domain.Participant;
import org.example.repository.JdbcUtils;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class ParticipantRepoDB implements ParticipantRepo{

    private JdbcUtils jdbcUtils;
    private static final Logger logger = LogManager.getLogger(ParticipantRepoDB.class);

    public ParticipantRepoDB(Properties properties) {
        logger.info("Initializing ParticipantRepoDB  with properties: {} ", properties );
        this.jdbcUtils = new JdbcUtils(properties);
    }
    @Override
    public Participant findOne(Long aLong) {
        logger.traceEntry("Find participant with id: {} ", aLong);

        if(aLong == null) {
            logger.error(new IllegalArgumentException("Id null"));
            throw new IllegalArgumentException("Error! Id cannot be null!");
        }

        Connection con = jdbcUtils.getConnection();
        try(PreparedStatement statement = con.prepareStatement("select * from participant " +
                "where id = ?")){

            statement.setInt(1, Math.toIntExact(aLong));
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()){
                String name = resultSet.getString("name");
                Long age = resultSet.getLong("age");

                Participant participant = new Participant(name,age);
                participant.setId(aLong);

                logger.traceExit(participant);
                return participant;
            }
        }
        catch (SQLException e){
            logger.error(e);
            throw new RuntimeException(e);
        }

        logger.traceExit("No participant found with id: {}", aLong);
        return null;
    }


    public Participant findByNameAge(String name,Long age) {
        logger.traceEntry("Find participant with name: {} and age: {} ", name,age);

        if(name == null || age <0) {
            logger.error(new IllegalArgumentException("Date invalide"));
            throw new IllegalArgumentException("Eroare!Date invalide");
        }

        Connection con = jdbcUtils.getConnection();
        try(PreparedStatement statement = con.prepareStatement("select * from participant " +
                "where name = ? and age = ?")){

            statement.setString(1,name);
            statement.setLong(2, age);
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()){
                Long id = resultSet.getLong("id");
                Participant participant = new Participant(name,age);
                participant.setId(id);

                logger.traceExit(participant);
                return participant;
            }
        }
        catch (SQLException e){
            logger.error(e);
            throw new RuntimeException(e);
        }

        logger.traceExit("No participant was found");
        return null;
    }

    @Override
    public Iterable<Participant> findAll() {
        logger.traceEntry("Getting all participants");
        List<Participant> participants = new ArrayList<>();
        Connection con = jdbcUtils.getConnection();

        try (PreparedStatement statement = con.prepareStatement("select * from participant")){

            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next())
            {
                Long id = resultSet.getLong("id");
                String name = resultSet.getString("name");
                Long age = resultSet.getLong("age");
                Participant participant = new Participant(name,age);
                participant.setId(id);
                participants.add(participant);

            }

            logger.traceExit(participants);
            return participants;
        }
        catch (SQLException e){
            logger.error(e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void save(Participant entity) {
        logger.traceEntry("saving participant: {}", entity);
        Connection con = jdbcUtils.getConnection();

        try(PreparedStatement prepStatement = con.prepareStatement("insert into participant(name,age) " +
                "values (?,?)")){
            prepStatement.setString(1,entity.getName());
            prepStatement.setLong(2,entity.getAge());

            int affectedRows = prepStatement.executeUpdate();
        }
        catch(SQLException e){
            logger.error(e);
            System.out.println("Error from DataBase: " + e);
        }

        logger.traceExit();
    }

    @Override
    public void delete(Long aLong) {
        logger.traceEntry("deleting participant with id: {} ", aLong);

        if(aLong == null) {
            logger.error(new IllegalArgumentException("Id null"));
            throw new IllegalArgumentException("Error! Id cannot be null!");
        }

        Connection con = jdbcUtils.getConnection();
        try(PreparedStatement statement = con.prepareStatement("delete from participant " +
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
    public void update(Participant entity) {
        logger.traceEntry("updating participant: {} ", entity);

        if(entity.getId() == null) {
            logger.error(new IllegalArgumentException("Id null"));
            throw new IllegalArgumentException("Error! Id cannot be null!");
        }

        Connection con = jdbcUtils.getConnection();
        try(PreparedStatement statement = con.prepareStatement("update participant " +
                "set name = ?,age = ? where id = ?")) {

            statement.setString(1, entity.getName());
            statement.setLong(2, entity.getAge());
            statement.setLong(3, entity.getId());

            int affectedRows = statement.executeUpdate();
            if(affectedRows == 0) logger.traceExit("could not update participant: {}", entity);
            else logger.traceExit("updated participant: {} ", entity);
        }
        catch(Exception e){
            logger.error(e);
            throw new RuntimeException(e);
        }
    }
}
