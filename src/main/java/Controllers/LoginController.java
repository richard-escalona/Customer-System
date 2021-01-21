package Controllers;
import backend.Database.PersonGatewayDB;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import backend.model.*;
import backend.services.*;
import org.springframework.http.ResponseEntity;

import javax.swing.text.View;


public class LoginController implements Initializable {
    private static final Logger logger = LogManager.getLogger(ListViewController.class);
    String name, name2;
    boolean test, test2;
    @FXML
    private PasswordField txt1;

    @FXML
    private TextField txt2;

    /**
     * controller for main and makes sure user inputs data
     **/
    //take you to the "ListView" pane.
    @FXML
    public void handle1(ActionEvent event) throws IOException {
        String username = txt2.getText();
        String password = txt1.getText();

        try{
            SessionGateway session = new SessionGateway();
            String tok = session.loginVerification(username,password);

            if (tok.equals("bad")){
               throw new Exception();
            }else{
                ViewSwitcher.getInstance().sessionID(tok);
                logger.info("<{}>LOGGED IN", txt2.getText());
                ViewSwitcher.globalAction = event;
                System.out.println("BEFOREEEEEEEEEEEEEEEEEEEEEEEE");
                ViewSwitcher.getInstance().switchView(ViewType.ListViewController);
                System.out.println("AFTERRRRRRRRRRRRRRRRRRRRRRRRRR");

            }
        }catch (Exception e){

            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setContentText("Not able to authenticate.\n Please try again.");
            txt1.clear();
            txt2.clear();
            alert.showAndWait();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }
}
