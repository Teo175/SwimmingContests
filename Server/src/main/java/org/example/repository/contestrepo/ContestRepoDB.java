package org.example.repository.contestrepo;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.domain.Contest;
import org.example.domain.DistEnum;
import org.example.domain.StyleEnum;
import org.example.repository.JdbcUtils;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

public class ContestRepoDB implements ContestRepo{
    private JdbcUtils jdbcUtils;
    private static final Logger logger = LogManager.getLogger(ContestRepoDB.class);

    public ContestRepoDB(Properties properties) {
        logger.info("Initializing ContestRepoDB  with properties: {} ", properties );
        this.jdbcUtils = new JdbcUtils(properties);
    }
    @Override
    public Contest findOne(Long aLong) {
        logger.traceEntry("Find contest with id: {} ", aLong);

        if(aLong == null) {
            logger.error(new IllegalArgumentException("Id null"));
            throw new IllegalArgumentException("Error! Id cannot be null!");
        }

        Connection con = jdbcUtils.getConnection();
        try(PreparedStatement statement = con.prepareStatement("select * from contest " +
                "where id = ?")){

            statement.setInt(1, Math.toIntExact(aLong));
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()){
                String distance = resultSet.getString("distance");
                String style = resultSet.getString("style");

                DistEnum distEnum = null;
                if(distance.equals("D50m")) distEnum = distEnum.D50m;
                else if(distance.equals("D200m"))  distEnum = distEnum.D200m;
                else if(distance.equals("D800m"))  distEnum = distEnum.D800m;
                else if(distance.equals("D1500m"))  distEnum = distEnum.D1500m;

                StyleEnum styleEnum = null;
                if (style.equals("FREESTYLE")) styleEnum = styleEnum.FREESTYLE;
                else if (style.equals("BACKSTROKE")) styleEnum = styleEnum.BACKSTROKE;
                else if (style.equals("BUTTERFLY")) styleEnum = styleEnum.BUTTERFLY;
                else if (style.equals("MIXED")) styleEnum = styleEnum.MIXED;


                Contest contest = new Contest(distEnum,styleEnum);
                contest.setId(aLong);

                logger.traceExit(contest);
                return contest;
            }
        }
        catch (SQLException e){
            logger.error(e);
            throw new RuntimeException(e);
        }

        logger.traceExit("No contest found with id: {}", aLong);
        return null;
    }

    @Override
    public Iterable<Contest> findAll() {
        logger.traceEntry("Getting all contests");
        List<Contest> contests = new ArrayList<>();
        Connection con = jdbcUtils.getConnection();

        try (PreparedStatement statement = con.prepareStatement("select * from contest")){

            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next())
            {
                Long id = resultSet.getLong("id");
                String distance = resultSet.getString("distance");
                String style = resultSet.getString("style");

                DistEnum distEnum = null;
                if(distance.equals("D50m")) distEnum = distEnum.D50m;
                else if(distance.equals("D200m"))  distEnum = distEnum.D200m;
                else if(distance.equals("D800m"))  distEnum = distEnum.D800m;
                else if(distance.equals("D1500m"))  distEnum = distEnum.D1500m;

                StyleEnum styleEnum = null;
                if (style.equals("FREESTYLE")) styleEnum = styleEnum.FREESTYLE;
                else if (style.equals("BACKSTROKE")) styleEnum = styleEnum.BACKSTROKE;
                else if (style.equals("BUTTERFLY")) styleEnum = styleEnum.BUTTERFLY;
                else if (style.equals("MIXED")) styleEnum = styleEnum.MIXED;


                Contest contest = new Contest(distEnum,styleEnum);
                contest.setId(id);

                contests.add(contest);

            }

            logger.traceExit(contests);
            return contests;
        }
        catch (SQLException e){
            logger.error(e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void save(Contest entity) {
        logger.traceEntry("saving contest: {}", entity);
        Connection con = jdbcUtils.getConnection();

        try(PreparedStatement prepStatement = con.prepareStatement("insert into contest(distance,style) " +
                "values (?,?)")){
            prepStatement.setString(1,entity.getDistance().name());
            prepStatement.setString(2,entity.getStyle().name());

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
        logger.traceEntry("deleting contest with id: {} ", aLong);

        if(aLong == null) {
            logger.error(new IllegalArgumentException("Id null"));
            throw new IllegalArgumentException("Error! Id cannot be null!");
        }

        Connection con = jdbcUtils.getConnection();
        try(PreparedStatement statement = con.prepareStatement("delete from contest " +
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
    public void update(Contest entity) {

    }

    public Optional<Contest> findByDistStyle(DistEnum dist, StyleEnum style) {
        logger.traceEntry("Find contest id with dist: {} and style {} ", dist, style);

        Connection con = jdbcUtils.getConnection();
        try (PreparedStatement statement = con.prepareStatement("select * from contest " +
                "where distance = ? and style = ?")) {

            statement.setString(1, dist.name());
            statement.setString(2, style.name());
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                Long id = resultSet.getLong("id");
                Contest contest = new Contest(dist, style);
                contest.setId(id);

                logger.traceExit(contest);
                return Optional.of(contest);
            }

        } catch (SQLException e) {
            logger.error(e);
            System.out.println("error db" + e);
        }


        return Optional.empty();
    }
}
