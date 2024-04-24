package org.example;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.RPCProtocol.ServiceRPCProxy;
import org.example.controllers.AdminController;
import org.example.controllers.LoginController;
import org.example.interfaces.ServiceInterface;
import org.example.repository.adminrepo.AdminRepoDB;
import org.example.repository.contestrepo.ContestRepoDB;
import org.example.repository.participantrepo.ParticipantRepoDB;
import org.example.repository.participationrepo.ParticipationRepoDB;
import org.example.service.Service;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class StartApplication extends Application {

    private Stage primaryStage = new Stage();
    private static int defaultPort = 55555;
    private static String defaultServer = "localhost";

    @Override
    public void start(Stage stage) throws Exception {
        Properties clientProperties = new Properties();
        try{
            clientProperties.load(StartApplication.class.getResourceAsStream("/clientprop.properties"));
        }
        catch (IOException e){
            System.err.println(e.getMessage());
            return;
        }

        String serverIP = clientProperties.getProperty("server.host", defaultServer);
        int serverPort = defaultPort;

        try{
            serverPort = Integer.parseInt(clientProperties.getProperty("server.port"));
        }
        catch(NumberFormatException e){
            System.err.println("Error in getting the port, using default port instead");
        }

        ServiceInterface server = new ServiceRPCProxy(serverIP, serverPort);

        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("login-view.fxml"));
        Parent root = loader.load();

        LoginController loginController = loader.getController();
        loginController.setService(server);

        FXMLLoader mainClient = new FXMLLoader(getClass().getClassLoader().getResource("admin-view.fxml"));
        Parent clientRoot = mainClient.load();

        AdminController adminController = mainClient.getController();
        loginController.setAdminController(adminController);
        loginController.setParent(clientRoot);

        primaryStage.setTitle("Hello");
        primaryStage.setScene((new Scene(root, 600, 360)));
        primaryStage.show();

    }

}
