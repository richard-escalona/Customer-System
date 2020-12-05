package Controllers;

import com.fasterxml.jackson.databind.ser.VirtualBeanPropertyWriter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import backend.model.Person;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ListViewController implements Initializable {

    //public static String token;
    private static final Logger logger = LogManager.getLogger(ListViewController.class);
    @FXML
    private Button update;
    @FXML
    private TextField newPID;

    public List<Person> people  = ViewSwitcher.getInstance().peopleFetch();
    //public List<Person> people = ViewSwitcher.getInstance().getPeople();
    @FXML
    public ListView<Person> listview;
    public ObservableList<Person> person=
            FXCollections.observableArrayList(people);
    public static int selectedIndex=-1;

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        listview.setItems(person);
    }

    /**
     * Adds a new person
     **/
    public static void setAddition( Person person) throws IOException {
        PersonGateway pg = new PersonGateway("http://localhost:8080/people",ViewSwitcher.getInstance().getSessionid());
        pg.insertPerson(person);
    }
    @FXML
    void newSearchList(ActionEvent event) {
        String name = newPID.getText();
        List<Person> filteredList = new ArrayList<>();
        for(Person person : people )
        {
            Pattern pattern = Pattern.compile(    "^" + name , Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(person.getLast_name());
            boolean matchFound = matcher.find();

            if(matchFound){
                filteredList.add(person);
                ObservableList<Person> person1 = FXCollections.observableArrayList(filteredList);
                listview.setItems(person1);
            }

        }

    }
    /**
     * shows index of person/index you are ate
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
     * Adds a new person to the list viewe
     **/

    @FXML
    public void Add(ActionEvent event) throws IOException {
        ViewSwitcher.globalAction = event;
        ViewSwitcher.getInstance().switchView(ViewType.addPerson);
    }

    @FXML
    void AuditTrail(ActionEvent event) throws IOException {
        ViewSwitcher.globalAction = event;
        ViewSwitcher.getInstance().switchView(ViewType.auditTrail);
    }

    /**
     * update the selected person if no one is selected it shows pop up message
     **/
    @FXML
    public  void updateHandle(ActionEvent event) {
        try {
            int personId = person.get(selectedIndex).getId();
            for(Person person : people)
            {
                if(person.getId() == personId)
                {
                    System.out.println(person + "before change");
                    person.setFirst_name(person.getFirst_name());
                    System.out.println(person + "After change");
                }
            }
            ViewSwitcher.globalAction = event;
            ViewSwitcher.getInstance().switchView(ViewType.updatePerson);
        }
        catch(Exception e){
            AlertBox.display("Select a person ", "Nothing selected");
        }
    }

    public static void UpdatePerson( Person person) throws IOException {
        PersonGateway pg = new PersonGateway("http://localhost:8080/people",ViewSwitcher.getInstance().getSessionid());
        pg.Update(person, person.getId());
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
           //get the id
           int personId = person.get(selectedIndex).getId();
           System.out.println("duh" + people);
           for(Person person : people)
           {
               if(person.getId() == personId)
               {
                   System.out.println(person);
                   ViewSwitcher.getInstance().deletePerson(person);
               }
           }
           person.remove(selectedIndex);
           listview.refresh();
           logger.info("DELETING <" + fName + " " + lName + ">");
       }
       catch (Exception e)
       {
           AlertBox.display("Select a person ", "Nothing selected");
           logger.error(" ERROR - please select a person before attempting to delete.");
       }

    }

}

