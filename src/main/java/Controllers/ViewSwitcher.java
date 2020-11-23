package Controllers;

import backend.model.Person;
import backend.model.loginModel;
import backend.services.PersonController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.*;

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
                window = (Stage) ((Node) ViewSwitcher.globalAction.getSource()).getScene().getWindow();// pane
                window.setScene(scene);
                window.show();
                break;

            case ListViewController:
                 url = new File("src/main/resources/ListView.fxml").toURI().toURL();
                rootPane = FXMLLoader.load(url);
                scene = new Scene(rootPane);// pane you are GOING TO show
                window = (Stage) ((Node) ViewSwitcher.globalAction.getSource()).getScene().getWindow();// pane
                window.setScene(scene);
                window.show();
                break;
            case updatePerson:
                url = new File("src/main/resources/UpdateProfile.fxml").toURI().toURL();
                rootPane = FXMLLoader.load(url);
                scene = new Scene(rootPane);// pane you are GOING TO show
                window = (Stage) ((Node) ViewSwitcher.globalAction.getSource()).getScene().getWindow();// pane
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

    public List<Person> peopleFetch(){

        PersonGateway pg = new PersonGateway("http://localhost:8080/people",this.getSessionid());
        List<Person> people = pg.fetchPeople();
        //  System.out.println("The people are: " + people.toString());
        return people;

    }
    public void deletePerson(Person person) throws IOException {
        PersonGateway pg = new PersonGateway("http://localhost:8080/people",this.getSessionid());
        pg.deletePerson(person);
    }
    public void addPerson(Person person) throws IOException {
        PersonGateway pg = new PersonGateway("http://localhost:8080/people",this.getSessionid());
        System.out.println("the add person inside viewswitcher " + person);
        pg.insertPerson(person);
    }




    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}
