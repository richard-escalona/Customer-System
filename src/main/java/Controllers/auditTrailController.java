package Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class auditTrailController implements Initializable {

    @FXML
    private AnchorPane mainPane;

    @FXML
    private ListView<?> listview;
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
            System.out.println("this is the id that was entered: " + stringId);

        }
    }


    public void initialize(URL url, ResourceBundle resourceBundle) {


    }

    public void setOnMouseClicked(MouseEvent mouseEvent) {
    }
}
