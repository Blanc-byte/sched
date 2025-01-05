package models;


import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author user
 */
public class instructorsList {
        private final StringProperty id;
        private final StringProperty fname;
        private final StringProperty lname;
        private final StringProperty role;

        public instructorsList(String id,String fname, String lname, String role) {
            this.id = new SimpleStringProperty(id);
            this.fname = new SimpleStringProperty(fname);
            this.lname = new SimpleStringProperty(lname);
            this.role = new SimpleStringProperty(role);
        }
        
        public StringProperty idProperty() {return id;}
        public StringProperty fnameProperty() {return fname;}
        public StringProperty lnameProperty() {return lname;}
        public StringProperty roleProperty() {return role;}
        
        public String getid() {return id.get();}

        public void setid(String a) {this.id.set(a);}
        
        public String getfname() {return fname.get();}

        public void setfname(String a) {this.fname.set(a);}
        
        public String getlname() {return lname.get();}

        public void setlname(String a) {this.lname.set(a);}

        public String getrole() {return role.get();}

        public void setrole(String a) {this.role.set(a);}
        
    }
