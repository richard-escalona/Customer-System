package Controllers;
import backend.model.Audit;
import backend.model.Person;
import net.bytebuddy.implementation.bytecode.Throw;
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

import javax.persistence.OptimisticLockException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
                Person person = new Person(jsonObject.getInt("id"), jsonObject.getString("first_name"), jsonObject.getString("last_name"),jsonObject.getInt("age"), LocalDate.parse((CharSequence) jsonObject.get("birth_date")));
                person.setLastModified(LocalDateTime.parse((CharSequence)jsonObject.get("lastModified")));
                people.add(person);
            }
        } catch (Exception e) {
            throw new PersonExceptions(e);
        }
        return people;
    }

    public Person fetchPerson(int ID) {

        ArrayList<Person> people = new ArrayList<Person>();
        try{
            HttpGet request = new HttpGet(wsURL + "/" + ID);
            // specify Authorization header
            request.setHeader("Authorization", sessionId);
            String response = waitForResponseAsString(request);
            System.out.println( response);
            for(Object obj : new JSONArray(response)) {
                JSONObject jsonObject = (JSONObject) obj;
                Person person = new Person(jsonObject.getInt("id"), jsonObject.getString("first_name"), jsonObject.getString("last_name"),jsonObject.getInt("age"), LocalDate.parse((CharSequence) jsonObject.get("birth_date")));
                person.setLastModified(LocalDateTime.parse((CharSequence)jsonObject.get("lastModified")));
                people.add(person);
            }
        } catch (Exception e) {
            throw new PersonExceptions(e);
        }
        return people.get(1);
    }

    public ArrayList<Audit> fetchTrails() {
        ArrayList<Audit> trails = new ArrayList<>();

        try {
            HttpGet request = new HttpGet(wsURL);
            // specify Authorization header
            request.setHeader("Authorization", sessionId);
            String response = waitForResponseAsString(request);
            System.out.println( response);
            for(Object obj : new JSONArray(response)) {
                JSONObject jsonObject = (JSONObject) obj;
                trails.add(new Audit(jsonObject.getInt("id"), jsonObject.getString("change_msg"), jsonObject.getInt("changed_by"),jsonObject.getInt("person_id"), Timestamp.valueOf(jsonObject.getString("when_occurred"))));
            }
        } catch (Exception e) {
            throw new PersonExceptions(e);
        }
        return trails;
    }

    public void Update(Person person, int OldId) throws IOException {
        CloseableHttpResponse response = null;
        CloseableHttpClient httpclient = null;
        httpclient = HttpClients.createDefault();
        JSONObject requestJson = new JSONObject();
        HttpPut httpPut = new HttpPut(wsURL + "/" + OldId);

        //--------------------------------------------------------------------------------

        System.out.println(person.getId());
        requestJson.put("id", person.getId());
        requestJson.put("first_name", person.getFirst_name());
        requestJson.put("last_name", person.getLast_name());
        requestJson.put("age", person.getAge());
        requestJson.put("birth_date", person.getBirth_date());
        requestJson.put("lastModified", person.getLastModified());

        String updateString = requestJson.toString();


        httpPut.setHeader("Authorization", sessionId);
        StringEntity stringEntity = new StringEntity(updateString);
        System.out.println("String entity" + stringEntity);
        httpPut.setEntity(stringEntity);
        httpPut.setHeader("Accept", "application/json");
        httpPut.setHeader("Content-type", "application/json");
        response = httpclient.execute(httpPut);

        System.out.println("Executing request " + response.getStatusLine());

        //For testing
        if (response.getStatusLine().getStatusCode() == 409) {
            System.out.println("OPTIMISTIC LOCK!!!!!!!!!!!!!");
            throw new OptimisticLockException();
        }

    }

    public int insertPerson(Person person) throws IOException {
        int respondID = 0;
        CloseableHttpResponse response = null;
        CloseableHttpClient httpclient = null;
        httpclient = HttpClients.createDefault();

        JSONObject requestJson = new JSONObject();

        //--------------------------------------------------------------------------------


        System.out.println("this is the getter test: " + person.getFirst_name());
        requestJson.put("first_name", person.getFirst_name());
        requestJson.put("last_name", person.getLast_name());
        requestJson.put("age", person.getAge());
        requestJson.put("birth_date", person.getBirth_date());
        String updateString = requestJson.toString();
        HttpPost httpPost = new HttpPost(wsURL);

        //--------------------------------------------------------------------------------

        httpPost.setHeader("Authorization", sessionId);
        StringEntity stringEntity = new StringEntity(updateString);
        httpPost.setEntity(stringEntity);
        httpPost.setHeader("Accept", "application/json");
        httpPost.setHeader("Content-type", "application/json");
        response = httpclient.execute(httpPost);

        if (response.getStatusLine().getStatusCode() == 200){
            System.out.println("Response Code: " + response.getStatusLine());
        }
        if (respondID !=0 ) {
            System.out.println("Response Body: id:" + respondID);
        }
        return  respondID;
    }


    public void deletePerson(Person person) throws IOException {
        CloseableHttpResponse response = null;
        CloseableHttpClient httpclient = null;
        httpclient = HttpClients.createDefault();
        HttpDelete httpDelete = new HttpDelete(wsURL + "/" + person.getId());
        System.out.println(httpDelete);
        //--------------------------------------------------------------------------------
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
