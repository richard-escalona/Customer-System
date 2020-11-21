package Controllers;

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
import java.util.List;
import java.util.ResourceBundle;

public class ViewSwitcher implements Initializable {

public static ActionEvent globalAction;

    @FXML
 private AnchorPane rootPane;
public String sessionid;


    private static ViewSwitcher instance = null;
    ViewSwitcher() {

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
    public String getSessionid(){return  sessionid;}
    public void sessionID(String session){
      this.sessionid = session;
    }

    public List<Person> peopleFetch(){
        PersonGateway pg = new PersonGateway("http://localhost:8080/people",this.getSessionid());
       List<Person> people = pg.fetchPeople();
     //  System.out.println("The people are: " + people.toString());
       return people;

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}
