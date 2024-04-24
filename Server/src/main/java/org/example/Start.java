package org.example;



import org.example.Utils.AbstractServer;
import org.example.Utils.ConncurrentRPCServer;
import org.example.Utils.ServerException;
import org.example.interfaces.ServiceInterface;
import org.example.repository.adminrepo.AdminRepoDB;
import org.example.repository.contestrepo.ContestRepoDB;
import org.example.repository.participantrepo.ParticipantRepoDB;
import org.example.repository.participationrepo.ParticipationRepoDB;
import org.example.service.Service;

import java.io.IOException;
import java.util.Properties;

public class Start {
    private static int defaultPort = 55555;

    public static void main(String[] args){
        Properties serverProps = new Properties();

        try{
            serverProps.load(Start.class.getResourceAsStream("/serverprop.properties"));
            serverProps.list(System.out);
        }
        catch(IOException e){
            System.err.println("Error: " + e);
            return;
        }
        AdminRepoDB adminRepoDB = new AdminRepoDB(serverProps);
        ContestRepoDB contestRepoDB = new ContestRepoDB(serverProps);
        ParticipantRepoDB participantRepoDB = new ParticipantRepoDB(serverProps);
        ParticipationRepoDB participationRepoDB = new ParticipationRepoDB(serverProps);
        ServiceInterface service = new Service(adminRepoDB,contestRepoDB,participantRepoDB,participationRepoDB);

        int serverPort = defaultPort;
        try{
            serverPort = Integer.parseInt(serverProps.getProperty("server.port"));
        }
        catch(NumberFormatException e){
            System.err.println("Wrong port, using default port");
        }

        AbstractServer server = new ConncurrentRPCServer(serverPort, service);
        try{
            server.start();
        }
        catch (ServerException e){
            System.err.println("Error starting server " + e.getMessage());
        }
        finally {
            try{
                server.stop();
            }
            catch (ServerException e){
                System.err.println("Error stopping server " + e.getMessage());
            }
        }
    }
}
