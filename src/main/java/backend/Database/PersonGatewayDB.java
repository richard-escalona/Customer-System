package backend.Database;

import Controllers.ViewSwitcher;
import backend.model.*;
//import backend.model.loginModel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PersonGatewayDB {
    private Connection connection;
    public String toke;
    public PersonGatewayDB(){}

    public PersonGatewayDB(Connection connection) {
        this.connection = connection;
    }

    public Person fetchPerson(int personID) {
        if (personID == Person.NEW_PERSON) {
            throw new PersonException("Not able to find person");
        }
        PreparedStatement st = null;
        ResultSet rows = null;
        try {
            st = connection.prepareStatement("select * from people where id = ?",
                    PreparedStatement.RETURN_GENERATED_KEYS);
            st.setInt(1, personID);
            rows = st.executeQuery();
            rows.first();

            Person person = new Person(rows.getInt("id"), rows.getString("first_name"), rows.getString("last_name"), rows.getInt("age"), rows.getDate("birth_date").toLocalDate());
            return person;
        } catch (SQLException e1) {
            //e1.printStackTrace();
            throw new PersonException(e1);
        } finally {
            try {
                if (rows != null)
                    rows.close();
                if (st != null)
                    st.close();
            } catch (SQLException e2) {
                e2.printStackTrace();
            }
        }
    }


    public String getKey(){
        PreparedStatement st = null;
        ResultSet rows = null;
        try{
            st = connection.prepareStatement("SELECT * FROM `session` ORDER BY `id` DESC LIMIT 1",
                    PreparedStatement.RETURN_GENERATED_KEYS);


            rows= st.executeQuery();
            rows.first();

            String credentials = rows.getString("token");

            return credentials;

        }catch (SQLException e1){
            throw  new PersonException(e1);
        }finally {
            try {
                if (rows != null)
                    rows.close();
                if (st != null)
                    st.close();
            } catch (SQLException e2) {
                e2.printStackTrace();
            }
        }
    }



    //This method is to get a user for log in
    public loginModel fetchUser(String username){
        PreparedStatement st = null;
        ResultSet rows = null;
        try{
            st = connection.prepareStatement("select * from users where user_name = ?",
                    PreparedStatement.RETURN_GENERATED_KEYS);

            st.setString(1,username);
            rows= st.executeQuery();
            rows.first();

            loginModel credentials = new loginModel(rows.getString("user_name"), rows.getString("password"));

            return credentials;

        }catch (SQLException e1){
            throw  new PersonException(e1);
        }finally {
            try {
                if (rows != null)
                    rows.close();
                if (st != null)
                    st.close();
            } catch (SQLException e2) {
                e2.printStackTrace();
            }
        }
    }

    public void insertToken(String token) throws PersonException {
        PreparedStatement st = null;
        ResultSet newKeys = null;

        toke = token;


        try {
            st = connection.prepareStatement("insert into session (token) values (?)",
                    PreparedStatement.RETURN_GENERATED_KEYS);

            st.setString(1, token);
            st.executeUpdate();
            newKeys = st.getGeneratedKeys();
            newKeys.first();
        } catch (SQLException e1) {
            e1.printStackTrace();
            throw new PersonException(e1);
        } finally {
            try {
                if(newKeys != null) {
                    newKeys.close();
                }
                if(st != null)
                    st.close();
            } catch (SQLException e2) {
                e2.printStackTrace();
            }
        }
    }




    public List<Person> fetchPeople() {
        PreparedStatement st = null;
        ResultSet rows = null;
        try {
            st = connection.prepareStatement("select * from people",
                    PreparedStatement.RETURN_GENERATED_KEYS);
            rows = st.executeQuery();
            rows.first();
            List<Person> output = new ArrayList<>();
            output.add(new Person(rows.getInt("id"), rows.getString("first_name"), rows.getString("last_name"), rows.getInt("age"), rows.getDate("birth_date").toLocalDate()));

            while(rows.next()){
                output.add(new Person(rows.getInt("id"), rows.getString("first_name"), rows.getString("last_name"), rows.getInt("age"), rows.getDate("birth_date").toLocalDate()));
            }
            return output;
        } catch (SQLException e1) {
            //e1.printStackTrace();
            throw new PersonException(e1);
        } finally {
            try {
                if (rows != null)
                    rows.close();
                if (st != null)
                    st.close();
            } catch (SQLException e2) {
                e2.printStackTrace();
            }
        }
    }

    // crud functions
    public int insertPerson(Person person) throws PersonException {
        // use connection to insert a person in the db
        PreparedStatement st = null;
        ResultSet newKeys = null;
        int newId = 0;
        try {
            st = connection.prepareStatement("insert into people (id, first_name, last_name, age, birth_date) values (?, ?, ?, ?, ?)",
                    PreparedStatement.RETURN_GENERATED_KEYS);

            st.setInt(1, person.getId());
            st.setString(2, person.getFirst_name());
            st.setString(3,person.getLast_name());
            st.setInt(4, person.getAge());
            st.setDate(5,java.sql.Date.valueOf(person.getBirth_date()));
            st.executeUpdate();
            newKeys = st.getGeneratedKeys();
            newKeys.first();

            // set the person's id as the primary key returned from the db
            // System.out.println("new person id is " + );
            newId = newKeys.getInt(1);

        } catch (SQLException e1) {
            e1.printStackTrace();
            throw new PersonException(e1);
        } finally {
            try {
                if(newKeys != null) {
                    newKeys.close();
                }
                if(st != null)
                    st.close();
            } catch (SQLException e2) {
                e2.printStackTrace();
            }
        }
        return newId;
    }
    public void deletePerson(Person person) throws PersonException {
        if(person.getId() == person.NEW_PERSON)
            throw new PersonException("Not able to delete a non existing person");

        PreparedStatement st = null;
        try {
            st = connection.prepareStatement("delete from people where id = ?",
                    PreparedStatement.RETURN_GENERATED_KEYS);
            st.setInt(1, person.getId());
            st.executeUpdate();

        } catch (SQLException e1) {
            e1.printStackTrace();
            throw new PersonException(e1);
        } finally {
            try {
                if(st != null)
                    st.close();
            } catch (SQLException e2) {
                e2.printStackTrace();
            }
        }
    }
    public void updatePerson(Person person) throws PersonException {
        if(person.getId() == Person.NEW_PERSON)
            throw new PersonException("A new person must be inserted first.");
        System.out.println("backend debug pgate update meth" + person.getId());
        PreparedStatement st = null;
        try {
            st = connection.prepareStatement("update people set first_name = ?, last_name = ?, age = ?, birth_date = ? where id = ?",
                    PreparedStatement.RETURN_GENERATED_KEYS);

            st.setString(1, person.getFirst_name());
            st.setString(2,person.getLast_name());
            st.setInt(3, person.getAge());
            st.setDate(4,java.sql.Date.valueOf(person.getBirth_date()));
            st.setInt(5,person.getId());
            st.executeUpdate();

        } catch (SQLException e1) {
            e1.printStackTrace();
            throw new PersonException(e1);
        } finally {
            try {
                if(st != null)
                    st.close();
            } catch (SQLException e2) {
                e2.printStackTrace();
            }
        }
    }



}
