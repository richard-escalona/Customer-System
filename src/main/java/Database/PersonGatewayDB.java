package Database;

import model.Person;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PersonGatewayDB {
    private Connection connection;

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

    public List<Person> fetchPeople() {
        PreparedStatement st = null;
        ResultSet rows = null;
        try {
            st = connection.prepareStatement("select * from people",
                    PreparedStatement.RETURN_GENERATED_KEYS);
            rows = st.executeQuery();
            rows.first();
            List<Person> output = new ArrayList<>();
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
            st.setString(2, person.getFirstName());
            st.setString(3,person.getLastName());
            st.setInt(4, person.getAge());
            st.setDate(5,java.sql.Date.valueOf(person.getDateOfBirth()));
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

}
