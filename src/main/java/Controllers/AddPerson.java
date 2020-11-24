package Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.Period;
import java.util.ResourceBundle;


public class AddPerson implements Initializable {
    private static final Logger logger = LogManager.getLogger(ListViewController.class);
    static ActionEvent event1;
    int idNum, ageNum;
    boolean validAge, validName, validLastName, validId;
    String Age, ID, lastname, firstname;
    String FirstName, LastName;
    int iD, AGE;
    

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

    /**
     * stores the data from the user.
     **/
    @FXML
    public void addition(ActionEvent event) throws IOException {

        FirstName = firstName.getText();
        LastName = lastName.getText();
        LocalDate dob =  DOB.getValue();
        iD = Person.NEW_PERSON;
        Period period = Period.between(dob, LocalDate.now());
        AGE = period.getYears();
        Person person = new Person( FirstName,LastName,iD, AGE, dob);

        ViewSwitcher.globalAction = event;

        validId = isValid(id.getText(), 1);

        validName = isValid(firstName.getText(), 2);

        validLastName = isValid(lastName.getText(), 3);
        validAge = isValid(age.getText(), 0);

        System.out.println("inside the add person class ----->");
        if( !validName || !validLastName || dob.isAfter(LocalDate.now()) ) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Invalid input (check first name, last name, and DOB).\n Please try again.");
            alert.showAndWait();
        }
        else {
            //check to see if we have to update or add person (depends on id #)
            person.save();
            System.out.println("this is the person ----> add " + firstname + lastname + idNum + ageNum + dob);
            ListViewController.setAddition(firstname, lastname, idNum, ageNum, dob);
            logger.info("CREATING <" + firstname +" "+  lastname + idNum + ">");
            ViewSwitcher.getInstance().switchView(ViewType.ListViewController);
        }
    }
    /**
     * lets the user know that there was an error
     * **/

    private boolean isValid(String message, int num)
    {
        try{
            //makes sure the id is an int and not a string
            if(num ==1) {
                ID = id.getText();
                idNum = Integer.parseInt(ID);
             //   System.out.println("valid input");
            }if(num==0) {
                //makes sure the age is an int and not a string
                Age = age.getText();
                ageNum = Integer.parseInt(Age);

             //   System.out.println("this number is " + ageNum);
                if(ageNum < 0 || ageNum > 150)
                {
                    throw new IllegalArgumentException("Non readable file");
                }
            }
            if(num==2){
                // spaces for strings
                int lastNameLen = lastName.getLength();
                int firstNameLen = firstName.getLength();
                if(firstNameLen < 1 || firstNameLen > 100 || lastNameLen <1 || lastNameLen > 100)
                    return false;
                lastname = lastName.getText();
                firstname = firstName.getText();


                boolean testerz1 = isStringOnlyAlphabet(firstname);
                boolean testerz = isStringOnlyAlphabet(lastname);

                if(!testerz || !testerz1)
                    throw new IllegalArgumentException("Non readable file");
                return (firstname != null && !firstname.trim().isEmpty()) && (lastname != null && !lastname.trim().isEmpty());
            }
            return true;
        }catch (IllegalArgumentException err){
            if(num ==0)
               // logger.error("ERROR - invalid Age entered, please enter a number between 1-150.");
            if(num ==1)
              //  logger.error("ERROR - id number out of range, please enter an id within the range -2147483648 to 2147483647.");
            if(num ==2)
                logger.error("ERROR - invaid name entered, please ensure your first/last name field contains only characters.");
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
     * can delete
     **/

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        id.setDisable(true);
        age.setDisable(true);
        firstName.setText("Lou");
        lastName.setText("Smith");

    }
}
