package Controllers;


import java.io.IOException;
import java.time.LocalDate;

/** this is the person class
 *
 * still need to implement the dateofBirth but unsure what datatype to make int string?
 *
 * **/
public class Person {
    public static final int NEW_PERSON = 0;
    String firstName;
    String lastName;
    LocalDate dateOfBirth;
    int id;
    int age;
    /**
     * person constructor
     **/
    Person(String firstName, String lastName, int id, int age, LocalDate dateOfBirth){
    this.firstName = firstName;
    this.lastName = lastName;
    this.id = id;
    this.age = age;
    this.dateOfBirth = dateOfBirth;
    }
    /**
     * allows you to retrieve persons lastname
     **/
    public String getLastName() {
        return lastName;
    }
    /**
     * allows you to set dob
     **/
    public void setDateOfBirth(LocalDate dateOfBirth)
    {
        this.dateOfBirth = dateOfBirth;
    }
    /**
     * allows you to retrieve DOB
     **/
    public LocalDate getDateOfBirth()
    {
        return dateOfBirth;
    }

    /**
     * allows you to setLastName
     **/
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * allows you to retrieve age
     **/
    public int getAge() {

        return age;
    }

    /**
     * allows you to set Age
     **/
    public void setAge(int age) {
            this.age = age;
    }

    /**
     * allows you to retrieve persons id
     **/
    public int getId() {
        return id;
    }

    public void setId(int id) {
            this.id = id;
    }

    /**
     * allows you to retrieve firstname
     **/
    public String getFirstName() {
        return firstName;
    }

    /**
     * allows you to set FristName
     **/
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    public String toString()
    {
        return firstName + " " + lastName + "  id:" + id + "  age:" +age+ "  dob:"+dateOfBirth;
    }
    public void save() throws PersonExceptions, IOException {
        PersonGateway pg = new PersonGateway("http://localhost:8080/people", ViewSwitcher.getInstance().getSessionid());
        PersonGateway pg1 = new PersonGateway("http://localhost:8080/people/1", ViewSwitcher.getInstance().getSessionid());
        // determine if I am a new record or not. call add if new, otherwise update
        if(getId() == NEW_PERSON) {
            // insert
         //   int newId = pg.insertPerson(this);
         //   this.setId(newId);
        } else {
            // update
          //  pg1.Update(this);
        }
    }

}
