package Controllers;
import backend.Database.PersonException;
import backend.model.Person;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.persistence.OptimisticLockException;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.Period;
import java.util.ResourceBundle;


public class UpdateProfile implements Initializable {
    private static final Logger logger = LogManager.getLogger(ListViewController.class);
    String Age, ID, lastname, firstname, dateofBirth;
    int idNum, ageNum, AGE, oldId;
    boolean validAge, validName, validLastName, validId;
    ListViewController listPerson = new ListViewController();
    @FXML
    public Button closeButton;
    @FXML
    private TextField firstName;
    @FXML
    private TextField lastName;
    @FXML
    private TextField age;
    @FXML
    private TextField id;
    @FXML
    private DatePicker DOB;

    final DatePicker datePicker = new DatePicker();

    @FXML
    public void update(ActionEvent event) throws IOException {

        ViewSwitcher.globalAction = event;
        validName = isValid(firstName, firstName.getText(), 2);
        validLastName = isValid(lastName, lastName.getText(), 3);
        validId = isValid(id, id.getText(), 1);
        validAge = isValid(age, age.getText(), 0);
        LocalDate dob = DOB.getValue();

        Period period = Period.between(dob, LocalDate.now());
        AGE = period.getYears();
        datePicker.setValue(LocalDate.of(2016, 7, 8));

        //create person
        Person person = new Person(Integer.parseInt(id.getText()),firstName.getText(),lastName.getText(), AGE,dob);
        person.setLastModified(listPerson.people.get(ListViewController.selectedIndex).getLastModified());


        if(!validId || !validName || !validLastName || !validAge || dob.isAfter(LocalDate.now()))
            AlertBox.display("Title of window", "Invalid input entered");
        else {
            try{
                PersonGateway pg = new PersonGateway("http://localhost:8080/people",ViewSwitcher.getInstance().getSessionid());
                pg.Update(person, person.getId());
            logger.info("UPDATING <" + firstname +" "+  lastname + idNum+ ">");
            ViewSwitcher.getInstance().switchView(ViewType.ListViewController);
            } catch (OptimisticLockException e){
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Please redo your changes and try to save again.");
                alert.showAndWait();
            }catch ( PersonException e){
                System.out.println("NOO");
            }

        }
    }

    @FXML
    public void exitDetail(ActionEvent event) throws IOException {
       // logger.info("READING <" + firstName.getText() +" "+  lastName.getText() + ">");
        ViewSwitcher.globalAction = event;
        ViewSwitcher.getInstance().switchView(ViewType.ListViewController);

    }

    /**
     * validates information
     **/
    private boolean isValid(TextField input, String message, int num)
   {
      try{
          datePicker.setValue(LocalDate.now());
            if(num ==1) {
                ID = id.getText();
                idNum = Integer.parseInt(ID);
            }
            if(num==0) {
                //makes sure the age is an int and not a string
                Age = age.getText();
                ageNum = Integer.parseInt(Age);

                if(ageNum < 0 || ageNum > 150)
                {
                  //  logger.error(" ERROR2 - This is an error log message");
                    throw new IllegalArgumentException("Non readable file");
                }
            }
            if(num==2){
                // spaces for strings
                firstname = firstName.getText();
                lastname = lastName.getText();

                int firstNameLen = firstName.getLength();
                int lastNameLen = lastName.getLength();
                if(firstNameLen < 1 || firstNameLen > 100 || lastNameLen <1 || lastNameLen > 100)
                    return false;
                boolean testerz1 = isStringOnlyAlphabet(firstname);
                boolean testerz = isStringOnlyAlphabet(lastname);

                if(!testerz)
                    throw new IllegalArgumentException("Non readable file");
                if(!testerz1){
                    throw new IllegalArgumentException("Non readable file");

                }
                return (firstname != null && !firstname.trim().isEmpty()) && (lastname != null && !lastname.trim().isEmpty());
            }else {
                return true;
            }
      }catch (IllegalArgumentException err){
            if(num ==0)
            logger.error(" ERROR - invalid Age entered, please enter a number between 1-150.");
          if(num ==1)
              logger.error(" ERROR - id number out of range, please enter an id within the range -2147483648 to 2147483647.");
          if(num ==2)
              logger.error(" ERROR - invaid name entered, please ensure your first/last name field contains only characters.");

           return false;
     }
   }

    public boolean isStringOnlyAlphabet(String str)
    {
        return ((str != null)
                && (!str.equals(""))
                && (str.matches("^[a-zA-Z]*$")));
    }

    /**
     * can delete later
     **/
    public void ShowDate(ActionEvent event1)
    {
        LocalDate ld = DOB.getValue();
        dateofBirth = ld.toString();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        firstName.setText(listPerson.people.get(ListViewController.selectedIndex).getFirst_name());
        lastName.setText(listPerson.people.get(ListViewController.selectedIndex).getLast_name());
        age.setText(Integer.toString(listPerson.people.get(ListViewController.selectedIndex).getAge()));
        id.setText(Integer.toString(listPerson.people.get(ListViewController.selectedIndex).getId()));
        oldId = listPerson.people.get(ListViewController.selectedIndex).getId();
        DOB.setValue(listPerson.people.get(ListViewController.selectedIndex).getBirth_date());


    }
}