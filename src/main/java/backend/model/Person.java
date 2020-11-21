package backend.model;


import java.time.LocalDate;

/** this is the person class
 *
 * still need to implement the dateofBirth but unsure what datatype to make int string?
 *
 * **/
public class Person {
    public static final int NEW_PERSON = 0;
    String first_name;
    String last_name;
    LocalDate date_birth;
    int id;
    int age;
    /**
     * person constructor
     **/

    public Person(int id, String first_name, String last_name, int age, LocalDate date_birth){
        this.first_name = first_name;
        this.last_name = last_name;
        this.id = id;
        this.age = age;
        this.date_birth = date_birth;
    }
    /**
     * allows you to retrieve persons lastname
     **/
    public String getLast_name() {
        return last_name;
    }
    /**
     * allows you to set dob
     **/
    public void setDate_birth(LocalDate date_birth)
    {
        this.date_birth = date_birth;
    }
    /**
     * allows you to retrieve DOB
     **/
    public LocalDate getDate_birth()
    {
        return date_birth;
    }

    /**
     * allows you to setLastName
     **/
    public void setLast_name(String last_name) {
        this.last_name = last_name;
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
    public String getFirst_name() {
        return first_name;
    }

    /**
     * allows you to set FristName
     **/
    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }
    public String toString()
    {
        return "id:" + id +"  first_name:" + first_name + " " + "last_name:" + last_name +"  age:" +age+ "  dob:"+ date_birth;
    }

}
