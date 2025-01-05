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
public class roomModel {
        private final StringProperty roomID;
        private final StringProperty roomCODE;
        private final StringProperty roomType;
        private final StringProperty roomBuilding;
        public roomModel(String roomid,String roomCode, String roomTYpe,String roomBuilding) {
            this.roomID = new SimpleStringProperty(roomid);
            this.roomCODE = new SimpleStringProperty(roomCode);
            this.roomType = new SimpleStringProperty(roomTYpe);
            this.roomBuilding = new SimpleStringProperty(roomBuilding);
        }
        
        public StringProperty roomIdProperty() {return roomID;}
        public StringProperty roomCodeProperty() {return roomCODE;}
        public StringProperty roomTypeProperty() {return roomType;}
        public StringProperty roomBuildingProperty() {return roomBuilding;}
        
        public String getroomID() {return roomID.get();}

        public void setroomID(String a) {this.roomID.set(a);}
        
        
        public String getroomCODE() {return roomCODE.get();}

        public void setroomCODE(String a) {this.roomCODE.set(a);}
        
        public String getroomType() {return roomType.get();}

        public void setroomType(String a) {this.roomType.set(a);}
        
        public String getroomBuilding() {return roomBuilding.get();}

        public void setroomBuilding(String a) {this.roomBuilding.set(a);}
    }
