/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author user
 */
public class getSchedFromDocx{
        private final StringProperty courseNo;
        private final StringProperty courseDes;
        private final StringProperty lec;
        private final StringProperty lab;
        private final StringProperty total;
        private final StringProperty sec;
        private final StringProperty numOfStudent;
        private final StringProperty faculty;
        private final StringProperty prog;
        
        public getSchedFromDocx(String  courseNo, String des, String lec, String lab, String total, String prog, String sec, String nos,String fac){
            this.courseNo = new SimpleStringProperty(courseNo);
            this.courseDes = new SimpleStringProperty(des);
            this.lec = new SimpleStringProperty(lec);
            this.lab = new SimpleStringProperty(lab);
            this.total = new SimpleStringProperty(total);
            this.prog = new SimpleStringProperty(prog);
            this.sec = new SimpleStringProperty(sec);
            this.numOfStudent = new SimpleStringProperty(nos);
            this.faculty = new SimpleStringProperty(fac);
        }
        
        public StringProperty courseNoProperty() {return courseNo;}
        public StringProperty courseDesProperty() {return courseDes;}
        public StringProperty lecProperty() {return lec;}
        public StringProperty labProperty() {return lab;}
        public StringProperty totalProperty() {return total;}
        public StringProperty progProperty() {return prog;}
        public StringProperty secProperty() {return sec;}
        public StringProperty numOfStudentProperty() {return numOfStudent;}
        public StringProperty facultyProperty() {return faculty;}
        
        public String getcourseNo() {return courseNo.get();}
        public void setcourseNo(String a) {this.courseNo.set(a);}
        
        public String getcourseDes() {return courseDes.get();}
        public void setcourseDes(String a) {this.courseDes.set(a);}
        
        public String getlec() {return lec.get();}
        public void setlec(String a) {this.lec.set(a);}
        
        public String getlab() {return lab.get();}
        public void setlab(String a) {this.lab.set(a);}
        
        public String gettotal() {return total.get();}
        public void settotal(String a) {this.total.set(a);}
        
        public String getprog() {return prog.get();}
        public void setprog(String a) {this.prog.set(a);}
        
        public String getsec() {return sec.get();}
        public void setsec(String a) {this.sec.set(a);}
        
        public String getnumOfStudent() {return numOfStudent.get();}
        public void setnumOfStudent(String a) {this.numOfStudent.set(a);}
        
        public String getfaculty() {return faculty.get();}
        public void setfaculty(String a) {this.faculty.set(a);}
    
}
