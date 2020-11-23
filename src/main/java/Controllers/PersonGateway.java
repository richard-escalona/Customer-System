package Controllers;
import backend.model.Person;
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


public class PersonGateway {
    private static final Logger logger = LogManager.getLogger(ListViewController.class);
    private String wsURL;
    private String sessionId;

    public PersonGateway(String url, String sessionId) {
        this.sessionId = sessionId;
        this.wsURL = url;
    }



    public ArrayList<Person> fetchPeople() {
        ArrayList<Person> people = new ArrayList<Person>();

        try {
            HttpGet request = new HttpGet(wsURL);
            // specify Authorization header
            request.setHeader("Authorization", sessionId);
            String response = waitForResponseAsString(request);
            System.out.println( response);
            for(Object obj : new JSONArray(response)) {
                JSONObject jsonObject = (JSONObject) obj;
                people.add(new Person(jsonObject.getInt("id"), jsonObject.getString("first_name"), jsonObject.getString("last_name"),jsonObject.getInt("age"), LocalDate.parse((CharSequence) jsonObject.get("birth_date"))));
            }
        } catch (Exception e) {
            throw new PersonExceptions(e);
        }
        return people;
    }

    /*************************************Update******************************************************************************
     For Grading purposes / future reference
     *  Valid case: Person with id 1 has a first name change to “Bobby”) will recieve a 200 response
     *  Invalid case: TO TEST FOR 404 ERROR  --> Try to delete the person in ListViewController that does not have an id of 1. Can add to ' + "/" + 1111 ' to wsURL.
     *  Invalid case:  changing the session id to the variable seshtoken will result in a 401 not found response.
     *************************************************************************************************************************/
    public void Update(Person person) throws IOException {
        // swiped from https://hc.apache.org/httpcomponents-client-ga/quickstart.html
        CloseableHttpResponse response = null;
        CloseableHttpClient httpclient = null;

        httpclient = HttpClients.createDefault();
        // assemble credentials into a JSON encoded string
        JSONObject requestJson = new JSONObject();
        //--------------------------------------------------------------------------------
        // TO TEST FOR 400 ERROR --> change "firstName" to "fstName"
        requestJson.put("firstName", person.getFirst_name());
        String updateString = requestJson.toString();
        // TO TEST FOR 404 ERROR  --> Try to delete the person in ListViewController that does not have an id of 1. Can add to ' + "/" + 1111 ' to wsURL.
        HttpPut httpPut = new HttpPut(wsURL);
        //--------------------------------------------------------------------------------
        // TO TEST FOR 401 ERROR  --> change sessionID to the var seshtoken
        String seshtoken = "i am a sesh token";
        httpPut.setHeader("Authorization", sessionId);
        StringEntity stringEntity = new StringEntity(updateString);
        httpPut.setEntity(stringEntity);
        response = httpclient.execute(httpPut);
        httpPut.setHeader("Accept", "application/json");
        httpPut.setHeader("Content-type", "application/json");
        System.out.println("Executing request " + response.getStatusLine());

        //For testing
        if (response.getStatusLine().getStatusCode() != 200) {
            //System.out.println("Response Code: " + response.getStatusLine());
            logger.info("USER NOT FOUND");
            //  logger.info("Please enter in the username field 'Bobby' to recieve a 200 response.");
        }

    }
    /***********************************************insertPerson**************************************************************************************
     For Grading purposes / future reference
     * IMPORTANT: Valid case: please enter 01/01/1990 in the dateofBirthfield, then press enter and you should recieve a 200 response.
     * Also, Make sure to enter data into the DOB field!!
     * Invalid case: change line 113: to fstName to recieve a 400 response
     **************************************************************************************************************************************************/
    public int insertPerson(Person person) throws IOException {
        int respondID = 0;

        // swiped from https://hc.apache.org/httpcomponents-client-ga/quickstart.html
        CloseableHttpResponse response = null;
        CloseableHttpClient httpclient = null;

        httpclient = HttpClients.createDefault();
        // assemble credentials into a JSON encoded string
        JSONObject requestJson = new JSONObject();
        //--------------------------------------------------------------------------------
        // TO TEST FOR 400 ERROR --> change "firstName" to "fstName"
        requestJson.put("firstName", person.getFirst_name());
        requestJson.put("lastName", person.getLast_name());
        requestJson.put("dateOfBirth", person.getBirth_date());
        String updateString = requestJson.toString();
        HttpPost httpPost = new HttpPost(wsURL);
        //--------------------------------------------------------------------------------

        httpPost.setHeader("Authorization", sessionId);
        StringEntity stringEntity = new StringEntity(updateString);
        httpPost.setEntity(stringEntity);
        httpPost.setHeader("Accept", "application/json");
        httpPost.setHeader("Content-type", "application/json");
        System.out.println("Executing request " + httpPost.getRequestLine());
        response = httpclient.execute(httpPost);
        String responseString = waitForResponseAsString(httpPost);
        try {
            JSONObject responseJSON = new JSONObject(responseString);
            respondID = responseJSON.getInt("id");
        }catch (Exception e){

        }
        //For testing
        if (response.getStatusLine().getStatusCode() == 200){
            System.out.println("Response Code: " + response.getStatusLine());
        }
        if (respondID !=0 ) {
            System.out.println("Response Body: id:" + respondID);
        }
        return  respondID;
    }

    /****************************************deletePerson********************************************************************
     For Grading purposes / future reference
     * Valid case: correct sessionid and deleting the record with id 1 will result with a 200 respnse.
     * Invalid case: if authorization header is incorrect changing line 163 to seshtoken will trigger a 401 response code.
     * Invalid case2: if any other record besides one is deleted will trigger/respond with a 404 not found response code.
     ************************************************************************************************************************/

    public void deletePerson(Person person) throws IOException {
        // swiped from https://hc.apache.org/httpcomponents-client-ga/quickstart.html
        CloseableHttpResponse response = null;
        CloseableHttpClient httpclient = null;
        httpclient = HttpClients.createDefault();
        // TO TEST FOR 404 ERROR  --> Try to delete the person in ListViewController that does not have an id of 1.
        //--------------------------------------------------------------------------------
        System.out.println("inside delete");
        HttpDelete httpDelete = new HttpDelete(wsURL + "/" + person.getId());
        System.out.println(httpDelete);
        //--------------------------------------------------------------------------------
        // TO TEST FOR 401 ERROR  --> change sessionID to the var seshtoken
        String seshtoken = "i am a sesh token";
        httpDelete.setHeader("Authorization", sessionId);
        response = httpclient.execute(httpDelete);
        System.out.println("RESPOSE: " + response.getStatusLine());
    }

    private String waitForResponseAsString(HttpRequestBase request) throws IOException {
        CloseableHttpClient httpclient = null;
        CloseableHttpResponse response = null;

        try {
            httpclient = HttpClients.createDefault();
            response = httpclient.execute(request);

            switch(response.getStatusLine().getStatusCode()) {
                case 200:
                    break;
                case 401:
                    throw new PersonExceptions("401");
                default:
                    System.out.println("Non-200 status code returned: " + response.getStatusLine());
            }
            return parseResponseToString(response);

        } catch(Exception e) {
            throw new PersonExceptions(e);
        } finally {
            if(response != null)
                response.close();
            if(httpclient != null)
                httpclient.close();
        }
    }
    private String parseResponseToString(CloseableHttpResponse response) throws IOException {
        HttpEntity entity = response.getEntity();
        String strResponse = EntityUtils.toString(entity, StandardCharsets.UTF_8);
        EntityUtils.consume(entity);

        return strResponse;
    }
}
