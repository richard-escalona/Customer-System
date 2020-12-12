package backend.model;

import java.util.ArrayList;
import java.util.List;

public class FetchResults {

     private List<Person> people;
    private int numRows;

    public List<Person> getPeople() {
        return people;
    }

    public int getNumRows() {
        return numRows;
    }

    public FetchResults(List<Person> people, int numRows) {
        this.people = people;
        this.numRows = numRows;
    }
}
