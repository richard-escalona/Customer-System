package Controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import backend.model.Person;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

public class ListViewController implements Initializable {

    //public static String token;
    private static final Logger logger = LogManager.getLogger(ListViewController.class);
    @FXML
    private Button update;

    public List<Person> people  = ViewSwitcher.getInstance().peopleFetch();
    //public List<Person> people = ViewSwitcher.getInstance().getPeople();
    @FXML
    public ListView<Person> listview;
    public ObservableList<Person> person=
            FXCollections.observableArrayList(people);
//                    new Person("Richard", "Escalona", 420666, 22, LocalDate.of(1997,9,23)),
//                    new Person("Chris", "Urista", 420666, 28,  LocalDate.of(1997,9,23)));
    public static int selectedIndex=-1;

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        listview.setItems(person);
    }

    /**
     * Adds a new person
     **/
    public static void setAddition( String firstName, String lastName, int id, int age, LocalDate Dob){
    //    person.add(new Person(firstName, lastName, id, age, Dob));
    }

    /**
     * shows index of person/index you are at
     **/
    public void setOnMouseClicked(MouseEvent event) {
        if (event.getClickCount() == 2){
            selectedIndex = listview.getSelectionModel().getSelectedIndex();
            String fName = person.get(selectedIndex).getFirst_name();
            String lName = person.get(selectedIndex).getLast_name();
            logger.info("READING <" + fName + " " + lName + ">");
            update.fire();
        }
        selectedIndex = listview.getSelectionModel().getSelectedIndex();
    }

    /**
     * Adds a new person to the list view
     **/
    @FXML
    public void Add(ActionEvent event) throws IOException {
        ViewSwitcher.globalAction = event;
        ViewSwitcher.getInstance().switchView(ViewType.addPerson);
    }

    /**
     * update the selected person if no one is selected it shows pop up message
     **/
    @FXML
    public void updateHandle(ActionEvent event) {
        try {
            ViewSwitcher.globalAction = event;
            ViewSwitcher.getInstance().switchView(ViewType.updatePerson);
        }
        catch(Exception e){
            AlertBox.display("Select a person ", "Nothing selected");
        }
    }

    /**
     * Removes person when selected
     * TO TEST FOR 404 ERROR  --> Try to delete the person in ListViewController that does not have an id of 1.
     * Click on the person with id=2 and click delete.
     **/
    @FXML
    public void removeHandle(ActionEvent event) {

       try {
           String fName = person.get(selectedIndex).getFirst_name();
           String lName = person.get(selectedIndex).getLast_name();
           logger.info("DELETING <" + fName + " " + lName + ">");
           //PersonGateway pg = new PersonGateway("http://localhost:8080/people",ViewSwitcher.getInstance().getSessionid());
           //pg.deletePerson(person.get(selectedIndex));
       }
       catch (Exception e)
       {
           AlertBox.display("Select a person ", "Nothing selected");
           logger.error(" ERROR - please select a person before attempting to delete.");
       }

    }

}

