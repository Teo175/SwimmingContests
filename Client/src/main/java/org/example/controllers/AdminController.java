package org.example.controllers;

import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.domain.ParticipationDTO;
import org.example.SwimmingException;
import org.example.alert.UserActionsAlert;
import org.example.domain.*;
import org.example.interfaces.ObserverInterface;
import org.example.interfaces.ServiceInterface;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class AdminController implements ObserverInterface {

    private ServiceInterface server;
    private Admin loggedAdmin;

    public void setService(ServiceInterface server, Admin admin) {
        this.server = server;
        this.loggedAdmin = admin;
        initialize_table();
        initialize_combos();
    }

    ObservableList<Tuple3> model = FXCollections.observableArrayList();

    @FXML
    public TableView<Tuple3> table_contests;
    @FXML
    public TableColumn<Tuple3, String> table_column_1;
    @FXML
    public TableColumn<Tuple3, String> table_column_2;
    @FXML
    public TableColumn<Tuple3, Long> table_column_3;

    ObservableList<ParticipationDTO> model2 = FXCollections.observableArrayList();

    @FXML
    public TableView<ParticipationDTO> table_search;
    @FXML
    public TableColumn<ParticipationDTO, String> table_column_name;
    @FXML
    public TableColumn<ParticipationDTO, Long> table_column_age;
    @FXML
    public TableColumn<ParticipationDTO, String> table_column_contests;

    @FXML
    private Button logoutButton;

    @FXML
    private TextField name;
    @FXML
    private TextField age;
    @FXML
    private Button add;

    @FXML
    private Button search;

    @FXML
    private ComboBox<DistEnum> distance_combo;
    @FXML
    private ComboBox<StyleEnum> style_combo;


    public void initialize_table()  {
        try {
            Iterable<Tuple3> contests = server.findTuple3_table();
            List<Tuple3> challengesList = StreamSupport.stream(contests.spliterator(), false)
                    .collect(Collectors.toList());

            ObservableList<Tuple3> challenges1 = FXCollections.observableArrayList(challengesList);
            table_contests.setItems(challenges1);
        }
        catch (SwimmingException e){
            UserActionsAlert.showMessage(null, Alert.AlertType.ERROR, "Error", "Error! Could not initialize table of contests!");
        }

    }

    @FXML
    private void handle_search(ActionEvent event) throws IOException, SQLException {
        DistEnum dist = distance_combo.getValue();
        StyleEnum style = style_combo.getValue();

        if(dist == null || style == null){
            UserActionsAlert.showMessage(null, Alert.AlertType.ERROR,"Error","Error! Distance and style must be initialised");
            return;
        }

        try {
            Iterable<ParticipationDTO> participations = server.findAllParticipationByDistStyle(dist, style);
            initialize_table_search(participations);
        }
        catch (SwimmingException e){
            UserActionsAlert.showMessage(null, Alert.AlertType.ERROR, "Error", "Error! Could not get handle the search!");
        }
    }

    private void initialize_table_search(Iterable<ParticipationDTO> participationDTOS){
        List<ParticipationDTO> participationList = StreamSupport.stream(participationDTOS.spliterator(), false)
                .collect(Collectors.toList());

        ObservableList<ParticipationDTO> participants = FXCollections.observableArrayList(participationList);
        table_search.setItems(participants);
    }

    @FXML
    public void initialize() {

        table_column_1.setCellValueFactory(cellData -> {
            if(cellData.getValue() != null){
                String distance = cellData.getValue().getDistance().toString();
                return new SimpleObjectProperty<>(distance);}
            return null;
        });

        table_column_2.setCellValueFactory(cellData -> {
            if(cellData.getValue()!=null){
            String style = cellData.getValue().getStyle().toString();
            return new SimpleObjectProperty<>(style);}
            return null;
        });
        table_column_3.setCellValueFactory(cellData -> {
            if(cellData.getValue()!=null){
            Long nr = cellData.getValue().getNrParticipants();
            return new SimpleObjectProperty<>(nr);}
            return null;
        });

        table_contests.setItems(model);

        table_contests.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        table_column_name.setCellValueFactory(cellData -> {
            if(cellData.getValue() != null){
                String name = cellData.getValue().getName();
                return new SimpleObjectProperty<>(name);}
            return null;
        });

        table_column_age.setCellValueFactory(cellData -> {
            if(cellData.getValue()!=null){
                Long age =  cellData.getValue().getAge();
                return new SimpleObjectProperty<>(age);}
            return null;
        });
        table_column_contests.setCellValueFactory(cellData -> {
            if(cellData.getValue()!=null){
                String contests = cellData.getValue().getContests();
                return new SimpleObjectProperty<>(contests);}
            return null;
        });

        table_search.setItems(model2);


    }

    public void initialize_combos() {
        distance_combo.setItems(FXCollections.observableArrayList(DistEnum.values()));
        style_combo.setItems(FXCollections.observableArrayList(StyleEnum.values()));

    }


    @FXML
    private void handle_add_participant(ActionEvent event){
        try{
        String Name = name.getText().toString();
        Long Age = Long.valueOf(age.getText());
        ObservableList<Tuple3> selectedItems = table_contests.getSelectionModel().getSelectedItems();
        Participant participant = new Participant(Name, Age);

        Iterable<Tuple3> selectedItemsList = selectedItems.stream().toList();
            try {
                server.addParticipantandParticipation(selectedItemsList,participant);
                name.clear();
                age.clear();
            } catch (SwimmingException e) {
                UserActionsAlert.showMessage(null, Alert.AlertType.ERROR, "Error", "Error! Could not get handle the add!");
            }
        }
        catch (Exception e)
        {
            UserActionsAlert.showMessage(null, Alert.AlertType.ERROR, "Error", "Error! Could not parse the age!");

        }
/*
        try {
            for (Tuple3 tpl : selectedItems) {
                Optional<Contest> contest = server.findContestId(tpl.getDistance(), tpl.getStyle());
                id_contests.add(contest.get().getId());
            }
            if (!id_contests.isEmpty()) {
                if (server.findParticipantByNameAge(Name, Age) == null) {
                    server.saveParticipant(Name, Age);
                    Participant participant = server.findParticipantByNameAge(Name, Age);

                    for (Long id_contest : id_contests) {
                        Participation participation = new Participation(participant, server.findContestById(id_contest));
                        server.addParticipation(participation);
                    }
                    //  service.notifyObservers(new ContestTaskChangeEvent(ChangeEventType.ADD, null));


                } else {
                    UserActionsAlert.showMessage(null, Alert.AlertType.ERROR, "Error", "Eroare!Exista deja un participant cu acest nume si aceasta varsta!");

                }
            } else {
                UserActionsAlert.showMessage(null, Alert.AlertType.ERROR, "Error", "Eroare!Trebuie sa selectezi o competitie" +
                        "!");

            }
            name.clear();
            age.clear();
        }catch (SwimmingException e){
            UserActionsAlert.showMessage(null, Alert.AlertType.ERROR, "Error", "Error! Could not get handle the search!");
        }
*/
    }



    @FXML
    public void handleLogout() {
        try {
            server.logout(loggedAdmin);
        } catch (SwimmingException e) {
            System.out.println("Logout error " + e);
        }

        Node src = logoutButton;
        Stage stage = (Stage) src.getScene().getWindow();

        stage.close();
    }

    @Override
    public void updateContests(Iterable<Tuple3> contests) throws SwimmingException {
        System.out.println("sunt in controller");
        contests.forEach(System.out::println);
        Platform.runLater(() ->{
            List<Tuple3> challengesList = StreamSupport.stream(contests.spliterator(), false)
                    .collect(Collectors.toList());

            ObservableList<Tuple3> challenges1 = FXCollections.observableArrayList(challengesList);
            table_contests.setItems(challenges1);
        });
    }
}
