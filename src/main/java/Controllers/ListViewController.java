package Controllers;

import com.fasterxml.jackson.databind.ser.VirtualBeanPropertyWriter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import backend.model.Person;
import org.apache.logging.log4j.core.config.Property;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ListViewController implements Initializable {
    private int currPageNumber = 1;
    //public static String token;
    private static final Logger logger = LogManager.getLogger(ListViewController.class);
    @FXML
    private Button update;
    @FXML
    private Button previous;
    @FXML
    private TextField newPID;

    @FXML
    private TableColumn<Person,String> idCol;

    @FXML
    private TableColumn first_name;

    @FXML
    private TableColumn last_name;

    @FXML
    private TableColumn age;

    @FXML
    private TableColumn DOB;

    @FXML
    private TableColumn last_modified;

    public List<Person> people  = ViewSwitcher.getInstance().peopleFetch(1,"");


    @FXML
    public TableView<Person> tableView;
    public ObservableList<Person> person = FXCollections.observableArrayList(people);
    public static int selectedIndex=-1;

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
            idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
            first_name.setCellValueFactory(new PropertyValueFactory<>("first_name"));
             last_name.setCellValueFactory(new PropertyValueFactory<>("last_name"));
             age.setCellValueFactory(new PropertyValueFactory<>("age"));
             DOB.setCellValueFactory(new PropertyValueFactory<>("birth_date"));
             last_modified.setCellValueFactory(new PropertyValueFactory<>("lastModified"));

            tableView.setItems(person);


    }

    /**
     * Adds a new person
     **/
    public static void setAddition( Person person) throws IOException {
        PersonGateway pg = new PersonGateway("http://localhost:8080/people",ViewSwitcher.getInstance().getSessionid());
        pg.insertPerson(person);
    }

    @FXML
    public void First(ActionEvent event){
        String searchText = newPID.getText();
        currPageNumber = 1;
        List<Person> people  = ViewSwitcher.getInstance().peopleFetch(currPageNumber,searchText);
        person.clear();
        person.setAll(people);
        tableView.refresh();
    }

    @FXML
    public void Next(ActionEvent event){
        String searchText = newPID.getText();
        currPageNumber += 10;
        if(currPageNumber > 0)
            previous.setDisable(false);
        List<Person> newPeople  = ViewSwitcher.getInstance().peopleFetch(currPageNumber,searchText);
        person.clear();
        person= FXCollections.observableArrayList(newPeople);
       tableView.refresh();
        tableView.setItems(person);
   }
    @FXML
    public void Prev(ActionEvent event) {
        String searchText = newPID.getText();
        currPageNumber -= 10;
        if(currPageNumber < 0)
            previous.setDisable(true);
        else {
            List<Person> newPeople = ViewSwitcher.getInstance().peopleFetch(currPageNumber, searchText);
            person.clear();
            person = FXCollections.observableArrayList(newPeople);
            tableView.refresh();
            tableView.setItems(person);
        }
    }

    @FXML
    public void Last(ActionEvent event) {
    }


    @FXML
     public void newSearchList(ActionEvent event) {
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
                tableView.setItems(person1);
            }

        }

    }
    /**
     * shows index of person/index you are ate
     **/
    public void setOnMouseClicked(MouseEvent event) {
        if (event.getClickCount() == 2){
            selectedIndex = tableView.getSelectionModel().getSelectedIndex();
            String fName = person.get(selectedIndex).getFirst_name();
            String lName = person.get(selectedIndex).getLast_name();
            logger.info("READING <" + fName + " " + lName + ">");
            update.fire();
        }
        selectedIndex = tableView.getSelectionModel().getSelectedIndex();
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
           tableView.refresh();
           logger.info("DELETING <" + fName + " " + lName + ">");
       }
       catch (Exception e)
       {
           AlertBox.display("Select a person ", "Nothing selected");
           logger.error(" ERROR - please select a person before attempting to delete.");
       }

    }

}

