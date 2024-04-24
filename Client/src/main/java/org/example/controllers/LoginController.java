package org.example.controllers;


import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.example.AdminApplication;
import org.example.SwimmingException;
import org.example.alert.LoginActionAlert;
import org.example.domain.Admin;
import org.example.interfaces.ServiceInterface;

import java.io.IOException;
import java.util.Optional;

public class LoginController {

    private ServiceInterface server;
    private AdminController adminController;
    Parent mainParent;

    public void setService(ServiceInterface server) {
        this.server = server;
    }
    public void setAdminController(AdminController adminController){
        this.adminController = adminController;
    }

    public void setParent(Parent p) {
        mainParent = p;
    }

    @FXML
    private TextField username;

    @FXML
    private PasswordField password;

    @FXML
    private Button loginButton;

    @FXML
    private Button exitButton;

    @FXML
    private void handle_exit(){
        Node src = exitButton;
        Stage stage = (Stage) src.getScene().getWindow();

        stage.close();
    }

    @FXML
    private void handle_login(){
        String username_field = username.getText();
        String password_field = password.getText();

        if(username_field.isEmpty() || password_field.isEmpty()){
            LoginActionAlert.showMessage(null, Alert.AlertType.ERROR, "Error", "Error! Textfields can not be empty!");
            username.clear();
            password.clear();
            return;
        }

        Admin admin = new Admin(username_field,password_field);
        try{
            server.login(admin, adminController);
            Stage stage = new Stage();
            stage.setTitle("Welcome " + username);
            stage.setScene(new Scene(mainParent));

//!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!aici ce sa scriu>
           /* stage.setOnCloseRequest(handleWindowEvent>()-> {
                    adminController.handleLogout();
                    System.exit(0);

            });
*/

            adminController.setService(server, admin);
            stage.show();
            handle_exit();
        }
        catch (SwimmingException e){
            LoginActionAlert.showMessage(null, Alert.AlertType.ERROR,"Error!", e.getMessage());
        }
        username.clear();
        password.clear();
    }
}
