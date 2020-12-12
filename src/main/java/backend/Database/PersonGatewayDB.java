package backend.Database;

import Controllers.ViewSwitcher;
import backend.model.*;
//import backend.model.loginModel;

import javax.persistence.OptimisticLockException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PersonGatewayDB {
    private Connection connection;
    public String toke;
    public PersonGatewayDB(){}
    private final int pageSize = 10;

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
            person.setLastModified(rows.getTimestamp("last_modified").toLocalDateTime());
            System.out.println("LAST MODIFIED ON:" + person.getLastModified());
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

            loginModel credentials = new loginModel(rows.getInt("id"), rows.getString("user_name"), rows.getString("password"));

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

    public List<Audit> fetchAudit(int userId) {
        PreparedStatement st = null;
        ResultSet rows = null;
        try {
            st = connection.prepareStatement("select * from audit_trail where person_id = ?",
                    PreparedStatement.RETURN_GENERATED_KEYS);
            st.setInt(1, userId);
            rows = st.executeQuery();
            rows.first();
            List<Audit> output = new ArrayList<>();

            output.add(new Audit(rows.getInt("id"), rows.getString("change_msg"), rows.getInt("changed_by"), rows.getInt("person_id"), rows.getTimestamp("when_occurred")));

            while(rows.next()){
                output.add(new Audit(rows.getInt("id"), rows.getString("change_msg"), rows.getInt("changed_by"), rows.getInt("person_id"), rows.getTimestamp("when_occurred")));
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




    public FetchResults fetchPeople(int pageNum, String searchText) {
        PreparedStatement st = null;
        ResultSet rows = null;
        try {
            String whereClause = " ";
            if(searchText != null && searchText.length() > 0)
                whereClause = " where last_name like ? ";
            st = connection.prepareStatement("select * from people " + whereClause + " ORDER BY id LIMIT " + pageNum + ", " +pageSize,
                    PreparedStatement.RETURN_GENERATED_KEYS);
            if(searchText != null && searchText.length() > 0)
                st.setString(1,searchText + "%");
            rows = st.executeQuery();
            rows.first();
            List<Person> output = new ArrayList<>();
            Person person= new Person(rows.getInt("id"), rows.getString("first_name"), rows.getString("last_name"), rows.getInt("age"), rows.getDate("birth_date").toLocalDate());
            person.setLastModified(rows.getTimestamp("last_modified").toLocalDateTime());
            output.add(person);


            while(rows.next()){
              Person person1 = new Person(rows.getInt("id"), rows.getString("first_name"), rows.getString("last_name"), rows.getInt("age"), rows.getDate("birth_date").toLocalDate());
                person1.setLastModified(rows.getTimestamp("last_modified").toLocalDateTime());
              output.add(person1);
            }

            st = connection.prepareStatement("select count(*) from people ",
                    PreparedStatement.RETURN_GENERATED_KEYS);
            ResultSet resultSet = st.executeQuery();
            resultSet.next();
            int numRows = resultSet.getInt(1);
             FetchResults results = new FetchResults(output,numRows);

            return results;
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
    public int insertPerson(Person person, int UserID) throws PersonException {
        // use connection to insert a person in the db
        PreparedStatement st = null;
        ResultSet newKeys = null;
        int newId = 0;
        try {
            connection.setAutoCommit(false);
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

           st = connection.prepareStatement("insert into audit_trail (id, change_msg, changed_by, person_id, when_occurred) values (? , ? , ? , ?, ?)");

           st.setInt(1,0);
           st.setString(2,"added");
           st.setInt(3,UserID);
           st.setInt(4, newId);
           st.setTimestamp(5, new Timestamp(System.currentTimeMillis()));
            st.executeUpdate();
            connection.commit();


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
                if(connection != null)
                    connection.setAutoCommit(true);
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
    public void updatePerson(Person person,String message, int UserID) throws PersonException {
        if(person.getId() == Person.NEW_PERSON)
            throw new PersonException("A new person must be inserted first.");
        //if the record in the database has a more recent time than the optimistic exception
        Person checkPerson  = fetchPerson(person.getId());
        if(checkPerson.getLastModified().isAfter(person.getLastModified())) {

            throw new OptimisticLockException(checkPerson.getLastModified().toString());
        }
        PreparedStatement st = null;
        try {
            connection.setAutoCommit(false);
            st = connection.prepareStatement("update people set first_name = ?, last_name = ?, age = ?, birth_date = ? where id = ?",
                    PreparedStatement.RETURN_GENERATED_KEYS);

            st.setString(1, person.getFirst_name());
            st.setString(2, person.getLast_name());
            st.setInt(3, person.getAge());
            st.setDate(4,java.sql.Date.valueOf(person.getBirth_date()));
            st.setInt(5,person.getId());
            st.executeUpdate();

            st = connection.prepareStatement("insert into audit_trail (id, change_msg, changed_by, person_id, when_occurred) values (? , ? , ? , ?, ?)");

            st.setInt(1,0);



            st.setString(2,message);

            st.setInt(3,UserID);
            st.setInt(4, person.getId());
            st.setTimestamp(5, new Timestamp(System.currentTimeMillis()));
            st.executeUpdate();
            connection.commit();

        } catch (SQLException e1) {
            e1.printStackTrace();
            throw new PersonException(e1);
        } finally {
            try {
                if(st != null)
                    st.close();
                if(connection != null)
                    connection.setAutoCommit(true);
            } catch (SQLException e2) {
                e2.printStackTrace();
            }
        }
    }



}
