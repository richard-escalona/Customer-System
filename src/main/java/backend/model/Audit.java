package backend.model;

import java.sql.Timestamp;

public class Audit {
    int id;
    String change_msg;
    int changed_by;
    int person_id;
    Timestamp when_occurred;


    public Audit(int id, String change_msg, int changed_by, int person_id, Timestamp when_occurred){
        this.id = id;
        this.change_msg = change_msg;
        this.changed_by = changed_by;
        this.person_id = person_id;
        this.when_occurred = when_occurred;
    }

    public int getId() {
        return id;
    }

    public String getChange_msg() {
        return change_msg;
    }

    public int getChanged_by() {
        return changed_by;
    }

    public int getPerson_id() {
        return person_id;
    }

    public Timestamp getWhen_occurred() {
        return when_occurred;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setChange_msg(String change_msg) {
        this.change_msg = change_msg;
    }

    public void setChanged_by(int changed_by) {
        this.changed_by = changed_by;
    }

    public void setPerson_id(int person_id) {
        this.person_id = person_id;
    }

    public void setWhen_occurred(Timestamp when_occurred) {
        this.when_occurred = when_occurred;
    }
    public String toString()
    {
        return "date/time:" + when_occurred +"  id:" + id + " " + "change message:" + change_msg +"  changed by:" +changed_by+ "  person ID:"+ person_id;
    }
}