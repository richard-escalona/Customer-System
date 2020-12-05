package backend.services;

import Controllers.ViewSwitcher;
import backend.Database.DBConnect;
import backend.Database.PersonException;
import backend.Database.PersonGatewayDB;
import backend.model.Audit;
import backend.model.Person;
import backend.model.loginModel;
import backend.model.modifiedPerson;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.persistence.OptimisticLockException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
public class PersonController {
    private static final Logger logger = LogManager.getLogger();
    private Connection connection;
    private String token;
    private int userID;
    public int getUserID(){return userID;}
    public String sessionToken(){ return token; }

    // create a connection on startup
    @PostConstruct
    public void startup(){
        try {
            connection = DBConnect.connectToDB();
            logger.info("*** MySQL connection created");
        } catch (SQLException | IOException e) {
            logger.error("*** " + e);

        }
    }
    // close a connection on shutdown
    @PreDestroy
    public void cleanup() {
        try {
            connection.close();
            logger.info("*** MySQL connection closed");
        } catch (SQLException e) {
            logger.error("*** " + e);
        }
    }


    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody loginModel login) {

        if(login.getUser_name() == null || login.getPassword() == null){
            logger.error("Can not enter null values");
            return new ResponseEntity<String>("", HttpStatus.valueOf(400));
        }
        try{
            loginModel credentialDB = new PersonGatewayDB(connection).fetchUser(login.getUser_name());
            userID = credentialDB.getId();

            ValidateCredential validcred = new ValidateCredential();
            if(!validcred.ValidateCredential(login,credentialDB)){
                return new ResponseEntity<String>("", HttpStatus.valueOf(401));
            }

          token = validcred.generateNewToken();
            JSONObject obj = new JSONObject();
            obj.put("token",token);
            new PersonGatewayDB(connection).insertToken(token);


            return new ResponseEntity<String>(obj.toString(), HttpStatus.valueOf(200));

        } catch (PersonException e){
            ResponseEntity<String> response = new ResponseEntity<String>("Not able find the user name", HttpStatus.valueOf(404));
            return response;
        }
    }

        @GetMapping("/people/{personid}")
    public Object fetchPerson(@RequestHeader Map<String, String> headers,
                              @PathVariable("personid") int personID) {

        if (!authorize(headers)) {
            return new ResponseEntity<String>("", HttpStatus.valueOf(401));
        }
        // create the gateway and fetch the person
        try {
            Person person = new PersonGatewayDB(connection).fetchPerson(personID);
            JSONArray array = new JSONArray();
            array.put(person);
            return new ResponseEntity<String>(array.toString(), HttpStatus.valueOf(200));

        } catch (PersonException e) {
            ResponseEntity<String> response = new ResponseEntity<String>("person " + personID + " not found", HttpStatus.valueOf(404));
            return response;
        }
    }

    @GetMapping("/people/{personid}/audittrail")
    public Object fetchTrail(@RequestHeader Map<String, String> headers,
                              @PathVariable("personid") int personID) {
        if (!authorize(headers)) {
            return new ResponseEntity<String>("", HttpStatus.valueOf(401));
        }
        // create the gateway and fetch the person
        try {
            List<Audit> trail = new PersonGatewayDB(connection).fetchAudit(personID);
            JSONArray array = new JSONArray(trail);
            return new ResponseEntity<String>(array.toString(), HttpStatus.valueOf(200));

        } catch (PersonException e) {
            ResponseEntity<String> response = new ResponseEntity<String>("person " + personID + " not found", HttpStatus.valueOf(404));
            return response;
        }
    }

    @GetMapping("/people")
    public Object fetchPeople(@RequestHeader Map<String, String> headers) {
        if (!authorize(headers)) {
            return new ResponseEntity<String>(" ", HttpStatus.valueOf(401));
        }
        // create the gateway and all people
        try {
            List<Person> people = new PersonGatewayDB(connection).fetchPeople();

            JSONArray array = new JSONArray(people);

            return new ResponseEntity<String>(array.toString(), HttpStatus.valueOf(200));

        } catch (PersonException e) {
            ResponseEntity<String> response = new ResponseEntity<String>(" not found", HttpStatus.valueOf(401));
            return response;
        }
    }
    @DeleteMapping("/people/{personid}")
    public ResponseEntity<String> deletePerson (@RequestHeader Map<String, String> headers, @PathVariable("personid") int personID) {
        if (!authorize(headers)) {
            return new ResponseEntity<String>("", HttpStatus.valueOf(401));
        }
        try {
            Person person = new PersonGatewayDB(connection).fetchPerson(personID);
            new PersonGatewayDB(connection).deletePerson(person);
            return new ResponseEntity<String>(HttpStatus.valueOf(200));

        } catch (PersonException e) {
            ResponseEntity<String> response = new ResponseEntity<String>( HttpStatus.valueOf(404));
            return response;
        }

    }

        @PostMapping("/people")
        public ResponseEntity<String> insert(@RequestHeader Map<String, String> headers, @RequestBody Person person){
            if (!authorize(headers)) {
                return new ResponseEntity<String>("", HttpStatus.valueOf(401));
            }
            JSONArray err = new JSONArray();
            Boolean error = false;
           // First Name and LastName be within 100 char
            System.out.println("did this worl " + person.getFirst_name());
            if(person.getFirst_name().length() > 100){
                error = true;
                err.put("first name must be between 1 and 100 characters");
                logger.error("first name must be between 1 and 100 characters");
            }
            if(person.getLast_name().length() > 100){
                error = true;
                err.put("last name must be between 1 and 100 characters");
                logger.error("last name must be between 1 and 100 characters");
            }
              // DOB must not be after today date
            if(person.getBirth_date().isAfter(LocalDate.now())){
                error = true;
                err.put("Date of birth is after today's date.");
                logger.error("Date of birth is after today's date.");
            }
            if (error == true){
                return new ResponseEntity<String>(err.toString(), HttpStatus.valueOf(400));
            }

            int id_returned  = new PersonGatewayDB(connection).insertPerson(person,getUserID());
            person.setId(id_returned);

            return new ResponseEntity<String>("", HttpStatus.valueOf(200));
        }
    @PutMapping("/people/{personid}")
    public ResponseEntity<String> updatePerson(@RequestHeader Map<String, String> headers,
                                               @PathVariable("personid") int personID, @RequestBody modifiedPerson personUpdate){

        StringBuilder message = new StringBuilder();
          int count = 0;

        if (!authorize(headers)) {
            return new ResponseEntity<String>("", HttpStatus.valueOf(401));
        }
        try {
            Person person = new PersonGatewayDB(connection).fetchPerson(personID);
            person.setLastModified(personUpdate.getLastModified());

            // this is to add the message to audit trail
            if(!person.getFirst_name().equals(personUpdate.getFirst_name()) ){
                count ++;
                message.append("first name changed from " + person.getFirst_name() + " to " + personUpdate.getFirst_name() + " ");
            }
            if(!person.getLast_name().equals(personUpdate.getLast_name())){
                if (count != 0){ message.append(", ");}
                message.append("last name changed from " + person.getLast_name() + " to " + personUpdate.getLast_name() + " ");
            }
            if(person.getAge() != personUpdate.getAge()){
                if (count != 0){ message.append(", ");}
                message.append("age changed from " + person.getAge() + " to " + personUpdate.getAge());
            }

            //update
            if(!(personUpdate.getFirst_name() == null)){
                person.setFirst_name(personUpdate.getFirst_name());
            }
            if(!(personUpdate.getLast_name() == null)){
                person.setLast_name(personUpdate.getLast_name());
            }
            if(!(personUpdate.getAge() == 0)){
                person.setAge(personUpdate.getAge());
            }
            if (!(personUpdate.getBirth_date() == null)){
                person.setBirth_date(personUpdate.getBirth_date());
            }
            if (!(personUpdate.getId() == 0)){
                person.setId(personUpdate.getId());
            }

            JSONArray err = new JSONArray();
            Boolean error = false;
            // First Name and LastName be within 100 char

            if(person.getFirst_name().length() > 100){
                error = true;
                err.put("first name must be between 1 and 100 characters");
                logger.error("first name must be between 1 and 100 characters");
            }
            if(person.getLast_name().length() > 100){
                error = true;
                err.put("last name must be between 1 and 100 characters");
                logger.error("last name must be between 1 and 100 characters");
            }
            // DOB must not be after today date
            if(person.getBirth_date().isAfter(LocalDate.now())){
                error = true;
                err.put("Date of birth is after today's date.");
                logger.error("Date of birth is after today's date.");
            }
            if (error == true) {
                return new ResponseEntity<String>(err.toString(), HttpStatus.valueOf(400));
            }
            System.out.println(person.toString());
            PersonGatewayDB gateway = new PersonGatewayDB(connection) ;
            gateway.updatePerson(person,message.toString(),userID);
            return new ResponseEntity<String>("", HttpStatus.valueOf(200));
        } catch (PersonException e){
            return new ResponseEntity<String>("", HttpStatus.valueOf(401));
        }
        catch (OptimisticLockException e) {
            return new ResponseEntity<String>("Please Update Again ", HttpStatus.valueOf(409));
        }
    }

    public boolean authorize( Map<String, String> headers){
        String sessionToken = "";
        Set<String> keys = headers.keySet();
        for (String key : keys) {
            if (key.equalsIgnoreCase("Authorization"))
                sessionToken = headers.get(key);
        }

        if (!sessionToken.equals(sessionToken)) {
            return false;
        }
        return true;
    }



}
