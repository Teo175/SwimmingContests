package org.example.repository.participationrepo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.domain.*;
import org.example.repository.JdbcUtils;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class ParticipationRepoDB implements ParticipationRepo {

    private JdbcUtils jdbcUtils;
    private static final Logger logger = LogManager.getLogger(ParticipationRepoDB.class);

    public ParticipationRepoDB(Properties properties) {
        logger.info("Initializing ParticipationRepoDB  with properties: {} ", properties );
        this.jdbcUtils = new JdbcUtils(properties);
    }
    @Override
    public Participation findOne(Tuple<Long, Long> longLongTuple) {
        logger.traceEntry("Find participation with id: {} ", longLongTuple);

        if(longLongTuple == null) {
            logger.error(new IllegalArgumentException("Id's null"));
            throw new IllegalArgumentException("Error! Id's cannot be null!");
        }

        Connection con = jdbcUtils.getConnection();
        try(PreparedStatement statement = con.prepareStatement("select p.id_p as ip, p.id_c as ic, " +
                "pp.name as name, pp.age as age " +
                        "c.distance as distance, c.style as style " +
                        "from participation p " +
                        "inner join participant pp on p.id_p = pp.id " +
                        "inner join contest c on p.id_c = c.id " +
                        " where p.id_p = ? and p.id_c = ?")){

            ResultSet set = statement.executeQuery();

            statement.setLong(1, longLongTuple.getFirst());
            statement.setLong(2, longLongTuple.getSecond());
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()){
                Long id_p = set.getLong("ip");
                String name = set.getString("name");
                Long age = set.getLong("age");
                Participant participant = new Participant(name,age);
                participant.setId(id_p);

                Long id_c = set.getLong("ic");
                DistEnum contestDistance = DistEnum.valueOf(set.getString("distance"));
                StyleEnum contestStyle = StyleEnum.valueOf(set.getString("style"));
                Contest contest = new Contest(contestDistance, contestStyle);
                contest.setId(id_c);

                Participation  participation = new Participation(participant, contest);
                logger.traceExit(participation);
                return participation;
            }
        }
        catch (SQLException e){
            logger.error(e);
            throw new RuntimeException(e);
        }

        logger.traceExit("No participation found with id: {}", longLongTuple);
        return null;
    }

    @Override
    public Iterable<Participation> findAll() {
        logger.traceEntry("finding all participations");
        List<Participation> participations = new ArrayList<>();
        Connection con = jdbcUtils.getConnection();
        try (PreparedStatement statement = con.prepareStatement("select p.id_p as ip, p.id_c as ic, " +
                "pp.name as name, pp.age as age, " +
                "c.distance as distance, c.style as style " +
                "from participation p " +
                "inner join participant pp on p.id_p = pp.id " +
                "inner join contest c on p.id_c = c.id"))  {
            ResultSet set = statement.executeQuery();
            while (set.next()) {
                Long id_p = set.getLong("ip");
                String name = set.getString("name");
                Long age = set.getLong("age");
                Participant participant = new Participant(name,age);
                participant.setId(id_p);

                Long id_c = set.getLong("ic");
                DistEnum contestDistance = DistEnum.valueOf(set.getString("distance"));
                StyleEnum contestStyle = StyleEnum.valueOf(set.getString("style"));
                Contest contest = new Contest(contestDistance, contestStyle);
                contest.setId(id_c);

                participations.add(new Participation(participant, contest));
            }
        } catch (SQLException e) {
            logger.error(e);
            throw new RuntimeException(e);
        }
        logger.traceExit(participations);
        return participations;
    }

    @Override
    public void save(Participation entity) {
        logger.traceEntry("saving participation {}", entity);
        Connection con = jdbcUtils.getConnection();
        try(PreparedStatement prepStatement = con.prepareStatement("insert into participation(id_p, id_c) " +
                "values (?,?)")){
            prepStatement.setLong(1, entity.getParticipant().getId());
            prepStatement.setLong(2, entity.getContest().getId());
            int affectedRows = prepStatement.executeUpdate();
        } catch (SQLException ex) {
            logger.error(ex);
            throw new RuntimeException(ex);
        }
        logger.traceExit();
    }

    @Override
    public void delete(Tuple<Long, Long> longLongTuple) {
        logger.traceEntry("deleting participation with id{}", longLongTuple);
        if(longLongTuple == null) {
            logger.error(new IllegalArgumentException("Id's null"));
            throw new IllegalArgumentException("Error! Id's cannot be null!");
        }

        Connection con = jdbcUtils.getConnection();
        try(PreparedStatement statement = con.prepareStatement("delete from participation " +
                "where id_p = ? and id_c = ?")) {
            statement.setLong(1, longLongTuple.getFirst());
            statement.setLong(2, longLongTuple.getSecond());
            int affectedRows = statement.executeUpdate();
        }
        catch(SQLException e){
            logger.error(e);
            throw new RuntimeException(e);
        }

        logger.traceExit();

    }


    @Override
    public void update(Participation entity) {
        //nu pot sa fac, am doar id urile ca si cheie primara

    }


    public List<Tuple3> getCompetitionParticipants() {
        List<Tuple3> competitions = new ArrayList<>();
        Connection con = jdbcUtils.getConnection();
        try (PreparedStatement statement = con.prepareStatement("SELECT c.distance, c.style, COUNT(p.id_p) AS participant_count " +
                "FROM contest c " +
                "LEFT JOIN participation p ON c.id = p.id_c " +
                "GROUP BY c.id, c.distance, c.style")) {
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    String distance = resultSet.getString("distance");
                    String style = resultSet.getString("style");

                    DistEnum distEnum = null;
                    if (distance.equals("D50m")) distEnum = DistEnum.D50m;
                    else if (distance.equals("D200m")) distEnum = DistEnum.D200m;
                    else if (distance.equals("D800m")) distEnum = DistEnum.D800m;
                    else if (distance.equals("D1500m")) distEnum = DistEnum.D1500m;

                    StyleEnum styleEnum = null;
                    if (style.equals("FREESTYLE")) styleEnum = StyleEnum.FREESTYLE;
                    else if (style.equals("BACKSTROKE")) styleEnum = StyleEnum.BACKSTROKE;
                    else if (style.equals("BUTTERFLY")) styleEnum = StyleEnum.BUTTERFLY;
                    else if (style.equals("MIXED")) styleEnum = StyleEnum.MIXED;

                    long participantCount = resultSet.getLong("participant_count");
                    Tuple3 competition = new Tuple3(distEnum, styleEnum, participantCount);
                    competitions.add(competition);
                }
            }
        } catch (SQLException e) {
            logger.error(e);
            throw new RuntimeException(e);
        }
        return competitions;
    }



    public Iterable<Participation> findAllByDistStyle(DistEnum dist, StyleEnum style) {
        List<Participation> participations = new ArrayList<>();

        try (Connection con = jdbcUtils.getConnection();
             PreparedStatement statement = con.prepareStatement("SELECT p.*, par.name, par.age " +
                     "FROM participation p " +
                     "LEFT JOIN contest c ON p.id_c = c.id " +
                     "LEFT JOIN participant par ON p.id_p = par.id " +
                     "WHERE c.distance = ? AND c.style = ?")) {
            statement.setString(1, dist.toString());
            statement.setString(2, style.toString());

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    long id_p = resultSet.getLong("id_p");
                    long id_c = resultSet.getLong("id_c");
                    String name = resultSet.getString("name");
                    long age = resultSet.getLong("age");

                    Participant participant = new Participant(name,age);
                    participant.setId(id_p);
                    Contest contest = new Contest(dist,style);
                    contest.setId(id_c);
                    Participation participation = new Participation(participant,contest);
                    participations.add(participation);
                }
            }
        } catch (SQLException e) {
            logger.error(e);
            throw new RuntimeException(e);
        }

        return participations;
    }


    public Iterable<Contest> getAllContestsFromAParticipant(Long id_participant) {
        List<Contest> contests = new ArrayList<>();

        try (Connection con = jdbcUtils.getConnection();
             PreparedStatement statement = con.prepareStatement("SELECT c.* " +
                     "FROM participation p " +
                     "LEFT JOIN contest c ON p.id_c = c.id " +
                     "WHERE p.id_p = ?")) {
            statement.setLong(1, id_participant);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    long id = resultSet.getLong("id");
                    String distance = resultSet.getString("distance");
                    String style = resultSet.getString("style");

                    DistEnum distEnum = null;
                    if (distance.equals("D50m")) distEnum = DistEnum.D50m;
                    else if (distance.equals("D200m")) distEnum = DistEnum.D200m;
                    else if (distance.equals("D800m")) distEnum = DistEnum.D800m;
                    else if (distance.equals("D1500m")) distEnum = DistEnum.D1500m;

                    StyleEnum styleEnum = null;
                    if (style.equals("FREESTYLE")) styleEnum = StyleEnum.FREESTYLE;
                    else if (style.equals("BACKSTROKE")) styleEnum = StyleEnum.BACKSTROKE;
                    else if (style.equals("BUTTERFLY")) styleEnum = StyleEnum.BUTTERFLY;
                    else if (style.equals("MIXED")) styleEnum = StyleEnum.MIXED;


                    Contest contest = new Contest(distEnum,styleEnum);
                    contest.setId(id);
                    contests.add(contest);
                }
            }
        } catch (SQLException e) {
            logger.error(e);
            throw new RuntimeException(e);
        }

        return contests;
    }

}
