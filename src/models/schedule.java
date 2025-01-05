import javafx.beans.property.StringProperty;
import javafx.beans.property.SimpleStringProperty;

public class schedule {
    private StringProperty id;
    private StringProperty instructorId;
    private StringProperty section;
    private StringProperty time;
    private StringProperty day;
    private StringProperty room;
    private StringProperty semester;
    private StringProperty schoolYear;
    private StringProperty status;
    private StringProperty lec;  
    private StringProperty lab;  
    private StringProperty noOfStudents;
    private StringProperty subid;

    // Constructor
    public schedule(String id, String instructorId, String section, String time, String day, String room, 
                    String semester, String schoolYear, String status, String lec, String lab, String noOfStudents, String subid) {
        this.id = new SimpleStringProperty(id);
        this.instructorId = new SimpleStringProperty(instructorId);
        this.section = new SimpleStringProperty(section);
        this.time = new SimpleStringProperty(time);
        this.day = new SimpleStringProperty(day);
        this.room = new SimpleStringProperty(room);
        this.semester = new SimpleStringProperty(semester);
        this.schoolYear = new SimpleStringProperty(schoolYear);
        this.status = new SimpleStringProperty(status);
        this.lec = new SimpleStringProperty(lec);
        this.lab = new SimpleStringProperty(lab);
        this.noOfStudents = new SimpleStringProperty(noOfStudents);
        this.subid = new SimpleStringProperty(subid);
    }

    // Getters and Setters for each field
    public String getsubid() {
        return subid.get();
    }

    public void setsubid(String id) {
        this.subid.set(id);
    }
    
    public String getId() {
        return id.get();
    }

    public void setId(String id) {
        this.id.set(id);
    }

    public StringProperty idProperty() {
        return id;
    }

    public String getInstructorId() {
        return instructorId.get();
    }

    public void setInstructorId(String instructorId) {
        this.instructorId.set(instructorId);
    }

    public StringProperty instructorIdProperty() {
        return instructorId;
    }

    public String getSection() {
        return section.get();
    }

    public void setSection(String section) {
        this.section.set(section);
    }

    public StringProperty sectionProperty() {
        return section;
    }

    public String getTime() {
        return time.get();
    }

    public void setTime(String time) {
        this.time.set(time);
    }

    public StringProperty timeProperty() {
        return time;
    }

    public String getDay() {
        return day.get();
    }

    public void setDay(String day) {
        this.day.set(day);
    }

    public StringProperty dayProperty() {
        return day;
    }

    public String getRoom() {
        return room.get();
    }

    public void setRoom(String room) {
        this.room.set(room);
    }

    public StringProperty roomProperty() {
        return room;
    }

    public String getSemester() {
        return semester.get();
    }

    public void setSemester(String semester) {
        this.semester.set(semester);
    }

    public StringProperty semesterProperty() {
        return semester;
    }

    public String getSchoolYear() {
        return schoolYear.get();
    }

    public void setSchoolYear(String schoolYear) {
        this.schoolYear.set(schoolYear);
    }

    public StringProperty schoolYearProperty() {
        return schoolYear;
    }

    public String getStatus() {
        return status.get();
    }

    public void setStatus(String status) {
        this.status.set(status);
    }

    public StringProperty statusProperty() {
        return status;
    }

    public String getLec() {
        return lec.get();
    }

    public void setLec(String lec) {
        this.lec.set(lec);
    }

    public StringProperty lecProperty() {
        return lec;
    }

    public String getLab() {
        return lab.get();
    }

    public void setLab(String lab) {
        this.lab.set(lab);
    }

    public StringProperty labProperty() {
        return lab;
    }

    public String getNoOfStudents() {
        return noOfStudents.get();
    }

    public void setNoOfStudents(String noOfStudents) {
        this.noOfStudents.set(noOfStudents);
    }

    public StringProperty noOfStudentsProperty() {
        return noOfStudents;
    }
    
}
