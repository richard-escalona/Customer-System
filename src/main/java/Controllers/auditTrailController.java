package Controllers;

import backend.model.Audit;
import backend.model.Person;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class auditTrailController implements Initializable {

    @FXML
    private AnchorPane mainPane;

    @FXML
    private ListView<Audit> listview;
    @FXML
    private TextField personId;

    @FXML
    void backToListview(ActionEvent event) throws IOException {
        ViewSwitcher.globalAction = event;
        ViewSwitcher.getInstance().switchView(ViewType.ListViewController);
    }
    @FXML
    void getAudit(ActionEvent event) {
        String stringId = personId.getText();
        if (stringId == null) {
            stringId = null;
        }
        else {
            int id = Integer.parseInt(stringId);
            PersonGateway pg = new PersonGateway("http://localhost:8080/people/" + id + "/audittrail",ViewSwitcher.getInstance().getSessionid());

            List<Audit> trails = pg.fetchTrails();
            ObservableList<Audit> trail = FXCollections.observableArrayList(trails);
            listview.setItems(trail);
        }
    }


    public void initialize(URL url, ResourceBundle resourceBundle) {


    }

    public void setOnMouseClicked(MouseEvent mouseEvent) {
    }
}
