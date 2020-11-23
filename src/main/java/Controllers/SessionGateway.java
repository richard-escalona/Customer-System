package Controllers;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

public class SessionGateway {
    private static final String WS_URL = "http://localhost:8080";
    private static final Logger logger = LogManager.getLogger();

    // swiped from https://hc.apache.org/httpcomponents-client-ga/quickstart.html
    public static String getResponseAsString(CloseableHttpResponse response) throws IOException {
        HttpEntity entity = response.getEntity();
        // use org.apache.http.util.EntityUtils to read json as string
        String strResponse = EntityUtils.toString(entity, StandardCharsets.UTF_8);
        EntityUtils.consume(entity);

        return strResponse;
    }

    public String loginVerification(String username, String password) throws IOException {

        // swiped from https://hc.apache.org/httpcomponents-client-ga/quickstart.html
        CloseableHttpResponse response = null;
        CloseableHttpClient httpclient = null;

        // 1. authenticate and get back session token
        httpclient = HttpClients.createDefault();

        // assemble credentials into a JSON encoded string
        JSONObject credentials = new JSONObject();
        credentials.put("user_name", username);
        credentials.put("password", password);
        String credentialsString = credentials.toString();
        logger.info("credentials: " + credentialsString);

        HttpPost loginRequest = new HttpPost(WS_URL + "/login");
        StringEntity reqEntity = null;
        try {
            reqEntity = new StringEntity(credentialsString);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        loginRequest.setEntity(reqEntity);
        loginRequest.setHeader("Accept", "application/json");
        loginRequest.setHeader("Content-type", "application/json");

        response = httpclient.execute(loginRequest);

        // a special response for invalid credentials
        if (response.getStatusLine().getStatusCode() == 401) {
            logger.error("401 status code returned: " + response.getStatusLine());
            httpclient.close();
        }
        if (response.getStatusLine().getStatusCode() != 200) {
            httpclient.close();
        }
        //If authentication is successful.
        if (response.getStatusLine().getStatusCode() == 200) {
            logger.error("Response Code: " + response.getStatusLine().getStatusCode());
        }

        // get the session token
        String responseString = getResponseAsString(response);
        logger.info("Login response as a string: " + responseString);

        String token = null;
        try {
            JSONObject responseJSON = new JSONObject(responseString);
            token = responseJSON.getString("token");

        } catch (Exception e) {
            logger.error("could not get session token: " + e.getMessage());
            httpclient.close();
        }
        logger.info("Session token: " + token);

        return token;
    }
}
