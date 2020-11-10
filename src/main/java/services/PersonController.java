package services;

import Database.DBConnect;
import Database.PersonException;
import Database.PersonGatewayDB;
import model.Person;
import model.SessionToken;
import model.loginModel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
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
    private String sessionToken(){ return token; }

    // create a connection on startup
    @PostConstruct
    public void startup(){
        try {
            connection = DBConnect.connectToDB();
            logger.info("*** MySQL connection created");
        } catch (SQLException | IOException e) {
            logger.error("*** " + e);

            // TODO: find a better way to force shutdown on connect failure
            // System.exit(0);
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
    public ResponseEntity<String> login(@RequestHeader Map<String, String> headers, @RequestBody loginModel login) {

        if(login.getUser_name() == null || login.getPassword() == null){
            logger.error("Can not enter null values");
            return new ResponseEntity<String>("", HttpStatus.valueOf(400));
        }
        try{
            loginModel credentialDB = new PersonGatewayDB(connection).fetchUser(login.getUser_name());

            ValidateCredential validcred = new ValidateCredential();
            if(!validcred.ValidateCredential(login,credentialDB)){
                return new ResponseEntity<String>("", HttpStatus.valueOf(400));
            }
            token = validcred.generateNewToken();
            new PersonGatewayDB(connection).insertToken(token);

            return new ResponseEntity<String>(token, HttpStatus.valueOf(200));

        } catch (PersonException e){
            ResponseEntity<String> response = new ResponseEntity<String>("Not able to the user name", HttpStatus.valueOf(404));
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

    @GetMapping("/people")
    public Object fetchPeople(@RequestHeader Map<String, String> headers) {
        if (!authorize(headers)) {
            return new ResponseEntity<String>("", HttpStatus.valueOf(401));
        }
        // create the gateway and all people
        try {
            List people = new PersonGatewayDB(connection).fetchPeople();
            JSONArray array = new JSONArray();
            array.put(people);
            return new ResponseEntity<String>(array.toString(), HttpStatus.valueOf(200));

        } catch (PersonException e) {
            ResponseEntity<String> response = new ResponseEntity<String>(" not found", HttpStatus.valueOf(401));
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
            if(person.getFirstName().length() > 100){
                error = true;
                err.put("first name must be between 1 and 100 characters");
                logger.error("first name must be between 1 and 100 characters");
            }
            if(person.getLastName().length() > 100){
                error = true;
                err.put("last name must be between 1 and 100 characters");
                logger.error("last name must be between 1 and 100 characters");
            }
              // DOB must not be after today date
            if(person.getDateOfBirth().isAfter(LocalDate.now())){
                error = true;
                err.put("Date of birth is after today's date.");
                logger.error("Date of birth is after today's date.");
            }
            if (error == true){
                return new ResponseEntity<String>(err.toString(), HttpStatus.valueOf(400));
            }

            int id_returned  = new PersonGatewayDB(connection).insertPerson(person);
            person.setId(id_returned);

            return new ResponseEntity<String>("", HttpStatus.valueOf(200));
        }
    @PutMapping("/people/{personid}")
    public ResponseEntity<String> updatePerson(@RequestHeader Map<String, String> headers,
                                               @PathVariable("personid") int personID, @RequestBody Person personUpdate){
        if (!authorize(headers)) {
            return new ResponseEntity<String>("", HttpStatus.valueOf(401));
        }
        Person person = new PersonGatewayDB(connection).fetchPerson(personID);

        if(!(personUpdate.getFirstName() == null)){
            person.setFirstName(personUpdate.getFirstName());
        }
        if(!(personUpdate.getLastName() == null)){
            person.setLastName(personUpdate.getLastName());
        }
        if(!(personUpdate.getAge() == 0)){
            person.setAge(personUpdate.getAge());
        }
        if (!(personUpdate.getDateOfBirth() == null)){
            person.setDateOfBirth(personUpdate.getDateOfBirth());
        }

        JSONArray err = new JSONArray();
        Boolean error = false;
        // First Name and LastName be within 100 char

        if(person.getFirstName().length() > 100){
            error = true;
            err.put("first name must be between 1 and 100 characters");
            logger.error("first name must be between 1 and 100 characters");
        }
        if(person.getLastName().length() > 100){
            error = true;
            err.put("last name must be between 1 and 100 characters");
            logger.error("last name must be between 1 and 100 characters");
        }
        // DOB must not be after today date
        if(person.getDateOfBirth().isAfter(LocalDate.now())){
            error = true;
            err.put("Date of birth is after today's date.");
            logger.error("Date of birth is after today's date.");
        }
        if (error == true) {
            return new ResponseEntity<String>(err.toString(), HttpStatus.valueOf(400));
        }
        System.out.println(person.toString());
        PersonGatewayDB gateway = new PersonGatewayDB(connection) ;
        gateway.updatePerson(person);
        return new ResponseEntity<String>(person.toString(), HttpStatus.valueOf(200));
    }

    public boolean authorize( Map<String, String> headers){
        String sessionToken = "";
        Set<String> keys = headers.keySet();
        for (String key : keys) {
            if (key.equalsIgnoreCase("authorization"))
                sessionToken = headers.get(key);
        }
        if (!sessionToken.equals(sessionToken())) {
            return false;
        }
        return true;
    }

}
