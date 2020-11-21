package Controllers;
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
        loginModel model = new loginModel(username,password);
        HttpPost httpPost = new HttpPost("http://localhost:8080/login");
        CloseableHttpClient httpclient = null;

        httpclient = HttpClients.createDefault();
        // assemble credentials into a JSON encoded string
        JSONObject requestJson = new JSONObject();
        //--------------------------------------------------------------------------------
        // TO TEST FOR 400 ERROR --> change "firstName" to "fstName"
        requestJson.put("user_name", username);
        requestJson.put("password", password);
        String updateString = requestJson.toString();
        StringEntity stringEntity = new StringEntity(updateString);
        httpPost.setEntity(stringEntity);
        httpPost.addHeader("content-type", "application/json");
        try{

            httpclient.execute(httpPost);
            //System.out.println(val);
            logger.info("<{}>LOGGED IN", txt2.getText());
            //ViewSwitcher.globalAction = event;
           // ViewSwitcher.getInstance().switchView(ViewType.ListViewController);
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
        txt2.setText("ragnar");
        txt1.setText("flapjacks");
    }
}