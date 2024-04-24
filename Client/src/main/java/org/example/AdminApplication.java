package org.example;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
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

public class AdminApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        Properties properties = new Properties();
        try {
            properties.load(new FileReader("bd.config"));
            System.out.println("Properties OK!");
            properties.list(System.out);
        }
        catch (IOException e) {
            System.out.println("Cannot oppen file!");
            return;
        }
        AdminRepoDB adminRepoDB = new AdminRepoDB(properties);
        ParticipantRepoDB participantRepoDB = new ParticipantRepoDB(properties);
        ContestRepoDB contestRepoDB = new ContestRepoDB(properties);
        ParticipationRepoDB participationRepoDB = new ParticipationRepoDB(properties);
        Service service = new Service(adminRepoDB,contestRepoDB,participantRepoDB,participationRepoDB);



        FXMLLoader fxmlLoader = new FXMLLoader(AdminApplication.class.getResource("admin_view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 700, 438);
        AdminController adminController = fxmlLoader.getController();
        adminController.setService(service, null);

        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

}
