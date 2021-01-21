package Controllers;

import backend.model.Person;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;

/**
 * This is singleton class is used to centralize our navigation and globalize our session authentication key.
 **/

public class ViewSwitcher implements Initializable {

public static ActionEvent globalAction;

    @FXML
 private AnchorPane rootPane;
    private StringBuilder stringToken = new StringBuilder();
    private List<Person> people;



    private static ViewSwitcher instance = null;
    public ViewSwitcher() {

    }
    public  static ViewSwitcher getInstance(){
        if( instance == null){
            instance = new ViewSwitcher();
        }
        return  instance;
}
    public void switchView(ViewType viewtype) throws IOException {
        URL url;
        AddPerson person = new AddPerson();
        Stage window;
        Scene scene;
        switch (viewtype) {
            case addPerson:
                url = new File("src/main/resources/Add.fxml").toURI().toURL();
                rootPane = FXMLLoader.load(url);
                scene = new Scene(rootPane);// pane you are GOING TO show
                window = (Stage) ((Node) ViewSwitcher.globalAction.getSource()).getScene().getWindow();
                window.setScene(scene);
                window.show();
                break;

            case ListViewController:
                 url = new File("src/main/resources/ListView.fxml").toURI().toURL();
                rootPane = FXMLLoader.load(url);
                scene = new Scene(rootPane);// pane you are GOING TO show
                window = (Stage) ((Node) ViewSwitcher.globalAction.getSource()).getScene().getWindow();
                window.setScene(scene);
                window.show();
                break;
            case updatePerson:
                url = new File("src/main/resources/UpdateProfile.fxml").toURI().toURL();
                rootPane = FXMLLoader.load(url);
                scene = new Scene(rootPane);// pane you are GOING TO show
                window = (Stage) ((Node) ViewSwitcher.globalAction.getSource()).getScene().getWindow();
                window.setScene(scene);
                window.show();
                break;
            case auditTrail:
                url = new File("src/main/resources/audittrail.fxml").toURI().toURL();
                rootPane = FXMLLoader.load(url);
                scene = new Scene(rootPane);// pane you are GOING TO show
                window = (Stage) ((Node) ViewSwitcher.globalAction.getSource()).getScene().getWindow();
                window.setScene(scene);
                window.show();
                break;

            default:
                break;
        }
    }

    public String getSessionid(){return  stringToken.toString();}
    public void sessionID(String session){
     ViewSwitcher.getInstance().stringToken.append(session);
    }
    public void setPerson(List<Person> new_person){this.people = new_person;}
    public List<Person> getPeople(){return this.people;}
    public List<Person> peopleFetch(int pageNumber, String lastname){
        PersonGateway pg = new PersonGateway("http://localhost:8080/people?pageNum=" + pageNumber +"&lastName="+lastname ,this.getSessionid());
        List<Person> people = pg.fetchPeople();
         //System.out.println("The people are: " + people.toString());
        return people;

    }
    public void deletePerson(Person person) throws IOException {
        PersonGateway pg = new PersonGateway("http://localhost:8080/people",this.getSessionid());
        pg.deletePerson(person);
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}
