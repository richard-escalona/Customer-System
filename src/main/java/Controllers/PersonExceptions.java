package Controllers;

public class PersonExceptions extends RuntimeException{
    public PersonExceptions (Exception e) {
        super(e);
    }
    public PersonExceptions(String msg) { super(msg);
    }
}
