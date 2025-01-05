package just;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

import Models.facultySchedModel;
import Models.getRoomScheduleModel;
import Models.getSchedFromDocx;
import Models.getSchedulesModel;
import Models.schedModel;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javax.swing.JOptionPane;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.usermodel.IBodyElement;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
/**
 *
 * @author user
 */
public class Controller {
    @FXML private ChoiceBox prog,sem,sy;
    @FXML private TextField displaySelectedFile;
    @FXML private Button selectAFile;
    @FXML private Label facultyName;
    boolean firstSched = true;
    ObservableList<getSchedFromDocx> listOfSched = FXCollections.observableArrayList();
    ObservableList<listOfSubjects> dataOfSubjects = FXCollections.observableArrayList();
    ObservableList<section> secList = FXCollections.observableArrayList();
    ObservableList<roomModel> rooms = FXCollections.observableArrayList();
    ObservableList<progModel> progList = FXCollections.observableArrayList();
    ObservableList<String> prg = FXCollections.observableArrayList();
    ObservableList<String> smff = FXCollections.observableArrayList("1","2");
    ObservableList<schedModel> allSchedule = FXCollections.observableArrayList();
    ObservableList<instructorsList> faculty = FXCollections.observableArrayList();
    
    ObservableList<String> classSched = FXCollections.observableArrayList();
    ObservableList<String> sexist = FXCollections.observableArrayList("Male", "Female");
       
    ObservableList<getSchedulesModel> scheduleHolder = FXCollections.observableArrayList();
    ObservableList<String> scheduleholder = FXCollections.observableArrayList();
    ObservableList<section> secName = FXCollections.observableArrayList();
    
//   scheduleholder.add(sII);
     
    
    
    DatabaseConnection dc = new DatabaseConnection();
    //int=0;
    public void initialize()throws Exception{
        dc.connect();
        getSchedDataFromDatabase();
        setItemsForProgAndSem();
        getFac();
        getSection(); 
        
        //selectSection.setItems(secName);
//        for(int i=0; i<secName.size(); i++){
//            selectSection.getItems().add(secName.get(i));
//            System.out.println(secName.get(i));
//        }
        
       
        getRooms();
        getPrograms();
        getSubject();
        getFaculty();

        putRoomInSelectRoom();
        facultyTable();
        writeToExcelAllSched();
         try {
             
            putRoomInSelectRoom();
            putDaysInSelectDays();
            //putSectionInSelectSection();
           
       } catch (Exception e) {
            e.printStackTrace();
            // Handle the exception appropriately
        }
        selectRoom.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>(){
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue){
                try {
                    String selectedROOM = selectRoom.getValue().toString();
                    showRoomsSchedules(selectedROOM);
                    
                } catch (Exception ex) {
                    Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
            
        });
        
        selectDays.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>(){
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue){
                try {
                    String selectedDays = selectDays.getValue().toString();
                    showDaysSchedules(selectedDays);
                    
                } catch (Exception ex) {
                    Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
            
        });
        
         selectSection.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>(){
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue){
                try {
                    String selectedSection = selectSection.getValue().toString();
                    showSectionSchedules(selectedSection);
                    
                } catch (Exception ex) {
                    Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
            
        });
        

         
        x5.setItems(sexist);
        selectSection.setItems(scheduleholder);
        z5.setItems(sexist);
        
    }
    public boolean checkFilter(){
        if(prog.getValue()==null || sem.getValue()==null || sy.getValue()==null || displaySelectedFile.getText().equals("")){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("");
            alert.setHeaderText("!!!");
            alert.setContentText("Please choose a number of units");
            alert.showAndWait();
            return false;
        }
        return true;
    }
    public void updateThefile()throws Exception{
        String file = displaySelectedFile.getText();
        facultyScheduleToExcel exc = new facultyScheduleToExcel();
        exc.createFormatWithData(file, classSched);
    }
    public void cr(ActionEvent event)throws Exception{
        if(checkFilter()){
            String file = displaySelectedFile.getText();
            FileInputStream docxFile=new FileInputStream(new File(file));
            XWPFDocument docx = new XWPFDocument(docxFile);

            for(IBodyElement element:docx.getBodyElements()){
                if(element instanceof XWPFParagraph){
                    XWPFParagraph paragraph = (XWPFParagraph) element;
                }else if(element instanceof XWPFTable){
                    XWPFTable table = (XWPFTable) element;
                    List<XWPFTableRow> rows = table.getRows();
                    int an = 1;

                    for(XWPFTableRow row : rows){
                        List<XWPFTableCell> cells = row.getTableCells();
                        //System.out.println(an);
                        int index = 1;
                        String c1="",c2="",c3="",c4="",c5="",c6="",c10="",c11="";

                        for(XWPFTableCell cell:cells){
                            List<XWPFParagraph> cellParagraphs = cell.getParagraphs();

                            if(an>2 && cells.size()==11){
                                //System.out.println("Number of CELLS: "+cells.size());
                                for(XWPFParagraph cellParagraph : cellParagraphs){
                                    //if(!cellParagraph.getText().equals("")){
                                        switch(index){
                                            case 1:c1+=cellParagraph.getText();
                                                break;
                                            case 2:c2+=cellParagraph.getText();
                                                break;
                                            case 3:c3+=cellParagraph.getText();
                                                break;
                                            case 4:c4+=cellParagraph.getText();
                                                break;
                                            case 5:c5+=cellParagraph.getText();
                                                break;
                                            case 6:c6+=cellParagraph.getText();
                                                break;
                                            case 10:c10+=cellParagraph.getText();
                                                break;
                                            case 11:c11+=cellParagraph.getText();
                                                break;
                                        }
                                    //}
                                }
                            }
                            //System.out.println("This is INDEX: "+index);
                            index++;
                        }
                        if(!c4.equals("")&&!c5.equals("")){
                            listOfSched.add(new getSchedFromDocx(c1,c3,c4,c5,c6,prog.getValue().toString(),c2,c10,c11));
                        }
                        //System.out.println("This is AN: "+an);
                        an++;
                    }
                }
                //System.out.println("|||");
            }
            insertSection();
            insertSub();
            generateSchedules();
        }
        
    }
    public String getIDofSubject(String courseDes, String courseNo){
        for(listOfSubjects ls: dataOfSubjects){// ls.getName().equals(courseDes) && courseDes.equals(ls.getName()) && 
            if(ls.getCode().equals(courseNo) || courseNo.equals(ls.getCode())){
                return ls.getID();
            }
        }
        return "";
    }
    //ObservableList<instructorsList> faculty = FXCollections.observableArrayList();
    public String getIDofFaculty(String facul){
        //System.out.println("Facultyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyy----"+facul);
        String holdID="";
        if(facul.contains("/")){
            //System.out.println("True");
            String[] sepFac = facul.split("/");
            
            for(int i=0; i<sepFac.length;i++){
                boolean noFacultyFound = true;
                String[] holdFaculty = sepFac[i].split(" ");
                for(instructorsList fac : faculty){
                    String fnameChar = fac.getfname().charAt(0)+"";
                    if(fac.getfname().contains(sepFac[i])){
                        if(i<1){
                           holdID+=fac.getID(); 
                        }else{
                            holdID+="/"+fac.getID(); 
                        }
                        noFacultyFound = false;
                        break;
                    }else if(fac.getfname().contains(holdFaculty[0]) && fac.getfname().contains(holdFaculty[1])){
                        if(i<1){
                           holdID+=fac.getID(); 
                        }else{
                            holdID+="/"+fac.getID(); 
                        }
                        noFacultyFound = false;
                        break;
                    }else if(holdFaculty[0].contains(fac.getlname())  &&   holdFaculty[1].contains(fnameChar) ){
                        if(i<1){
                           holdID+=fac.getID(); 
                        }else{
                            holdID+="/"+fac.getID(); 
                        }
                        noFacultyFound = false;
                        break;
                    }
                }
                if(noFacultyFound){
                    if(i<1){
                        holdID+="101"; 
                    }else{
                        holdID+="/"+"101"; 
                    }
                }
            }
            return holdID;
            
        }else{
//            for(instructorsList fac: faculty){
//                if(facul.contains(fac.getlname())){
//                    return fac.getID();
//                }
//            }
            for(instructorsList fac: faculty){
                String fnameChar = fac.getfname().charAt(0)+"";
                String[] sepFacAgain = facul.split(" "); // separate this
                if(fac.getfname().contains(facul)){
                    return fac.getID();
                }
                if(fac.getfname().contains(sepFacAgain[0]) && fac.getfname().contains(sepFacAgain[1])){
                    return fac.getID();
                }
                if(sepFacAgain[0].contains(fac.getlname())  &&   sepFacAgain[1].contains(fnameChar) ){
                    return fac.getID();
                }
                
            }
        }
        
        return "176";
    }
    public String geIDofSection(String sec){
        for(section rm: secList){
            if(rm.getName().equals(sec)){
                return rm.getID();
            }
        }
        return "";
    }
    String roomD="";
    String timeD="";
    String daysD="";
    public void generateSchedules()throws Exception{
        int hf = 1;
        for(getSchedFromDocx ad : listOfSched){
            try{
            String[] roomTimeDays;
            String idOfSubject="";
            String idOfSection=geIDofSection(ad.getsec());
            String idOfFaculy=getIDofFaculty(ad.getfaculty());
            //String lecOrLab 
            //System.out.println("========"+idOfSubject+";"+idOfSection+";"+roomD+";"+timeD+";"+daysD+";"+"1"+";"+"2022-2023"+";"+idOfFaculy+";"+ad.getlec()+";"+ad.getlab()+";"+ad.getnumOfStudent());
            String buildingNo="1";
            if(prog.getValue().equals("BSTLEd")){
                buildingNo="2";
            }else if(prog.getValue().equals("BSBA")){
                buildingNo="1";
            }
            if(ad.getlec().equals("1")){
                System.out.println("Lecture 1");
                roomTimeDays = generateSchedFor1hourLecture("Lecture", idOfFaculy,idOfSection,buildingNo).split(";");// insertLater
                timeD+=roomTimeDays[0];
                daysD+=roomTimeDays[1];
                roomD+=roomTimeDays[2];
                idOfSubject = getIDofSubject(ad.getcourseDes(), ad.getcourseNo());
                idOfSection = geIDofSection(ad.getsec());
            }else if(ad.getlec().contains("2")){
                roomTimeDays = generateSchedFor2hoursLecture("Lecture", idOfFaculy,idOfSection,buildingNo).split(";");// insertLater
                timeD+=roomTimeDays[0];
                daysD+=roomTimeDays[1];
                roomD+=roomTimeDays[2];
                idOfSubject = getIDofSubject(ad.getcourseDes(), ad.getcourseNo());
                idOfSection = geIDofSection(ad.getsec());
                //classSched.add(roomTimeDays[0]+";"+roomTimeDays[1]+";"+roomTimeDays[2]);
            }else if(ad.getlec().contains("3")){// i stop here at 4/19/2024, 12:50PM
                if(ad.getcourseNo().contains("NSTP")){
                    roomTimeDays = generateSchedFor3hoursLectureNSTP("Lecture", idOfFaculy,idOfSection,buildingNo).split(";");// insertLater
                    idOfSubject = getIDofSubject(ad.getcourseDes(), ad.getcourseNo());
                    idOfSection = geIDofSection(ad.getsec());
                    timeD+=roomTimeDays[0];
                    daysD+=roomTimeDays[1];
                    roomD+=roomTimeDays[2];
                    
                }else{
                    roomTimeDays = generateSchedFor3hoursLecture("Lecture", idOfFaculy,idOfSection,buildingNo).split(";");// insertLater
                    idOfSubject = getIDofSubject(ad.getcourseDes(), ad.getcourseNo());
                    idOfSection = geIDofSection(ad.getsec());
                    timeD+=roomTimeDays[0];
                    daysD+=roomTimeDays[1];
                    roomD+=roomTimeDays[2];
                }
                
            }
            String whichLabIsIt = "";
            if(prog.getValue().equals("BSIT")){
                whichLabIsIt="IT-Laboratory";
            }else if(prog.getValue().equals("BSTLEd")){
                whichLabIsIt="TLE-Laboratory";
            }else if(prog.getValue().equals("BSA")){
                whichLabIsIt="Agri-Laboratory";
            }
            if(ad.getlab().contains("1")){
                roomTimeDays = generateSchedForlab1(whichLabIsIt, idOfFaculy,idOfSection).split(";");
                idOfSubject = getIDofSubject(ad.getcourseDes(), ad.getcourseNo());
                idOfSection = geIDofSection(ad.getsec());
                if(timeD.equals("")){
                    timeD+=roomTimeDays[0];
                    daysD+=roomTimeDays[1];
                    roomD+=roomTimeDays[2];
                }else{
                    timeD+="/"+roomTimeDays[0];
                    daysD+="/"+roomTimeDays[1];
                    roomD+="/"+roomTimeDays[2];
                }
                
            }else if(ad.getlab().contains("2")){
                System.out.println("Laboratory 2");
                roomTimeDays = generateSchedForlab2(whichLabIsIt, idOfFaculy,idOfSection).split(";");
                idOfSubject = getIDofSubject(ad.getcourseDes(), ad.getcourseNo());
                idOfSection = geIDofSection(ad.getsec());
                if(timeD.equals("")){
                    timeD+=roomTimeDays[0];
                    daysD+=roomTimeDays[1];
                    roomD+=roomTimeDays[2];
                }else{
                    timeD+="/"+roomTimeDays[0];
                    daysD+="/"+roomTimeDays[1];
                    roomD+="/"+roomTimeDays[2];
                }
            }
            if(idOfFaculy.equals("153")){
                //System.out.println(timeD+";"+daysD+";"+roomD);
            }
            if(idOfFaculy.contains("/")){
                String[] sepTime = timeD.split("/");
                String[] sepRoom = roomD.split("/");
                String[] sepDays = daysD.split("/");
                String[] sepFac = idOfFaculy.split("/");
                String exp1 = ad.getlec();
                String exp2 = "0";
                
                for(int yu=0; yu<2;yu++){
                    allSchedule.add(new schedModel(ad.getcourseNo(), ad.getcourseDes(), ad.getlec(),ad.getlab(), (Integer.parseInt(ad.getlec())+Integer.parseInt(ad.getlab()))+"" , prog.getValue().toString(), ad.getsec(), daysD, timeD, roomD, sepFac[yu],ad.getnumOfStudent(),"1",idOfSection));
                    scheduleHolder.add(new getSchedulesModel(idOfSubject,idOfSection ,sepTime[yu],sepDays[yu],sem.getValue().toString(),sy.getValue().toString(),"1",sepFac[yu],ad.getlec(),ad.getlab(),ad.getnumOfStudent(),sepRoom[yu] ));
                    exp1="0";
                    exp2=ad.getlab();
                }
            }else{
                allSchedule.add(new schedModel(ad.getcourseNo(), ad.getcourseDes(), ad.getlec(),ad.getlab(), (Integer.parseInt(ad.getlec())+Integer.parseInt(ad.getlab()))+"" , prog.getValue().toString(), ad.getsec(), daysD, timeD, roomD, idOfFaculy,ad.getnumOfStudent(),"1",idOfSection));
                scheduleHolder.add(new getSchedulesModel(idOfSubject,idOfSection ,timeD,daysD,sem.getValue().toString(),sy.getValue().toString(),"1",idOfFaculy,ad.getlec(),ad.getlab(),ad.getnumOfStudent(),roomD ));
            }
            //System.out.println(roomD+"-0000000-"+timeD+"-0000000-"+daysD);
            System.out.println(hf+"========"+idOfSubject+";"+idOfSection+";"+roomD+";"+timeD+";"+daysD+";"+"1"+";"+"2022-2023"+";"+idOfFaculy+";"+ad.getlec()+";"+ad.getlab()+";"+ad.getnumOfStudent());
        
            
            classSched.add(idOfSubject+";"+idOfSection+";"+roomD+";"+timeD+";"+daysD+";"+"1"+";"+"2022-2023"+";"+idOfFaculy+";"+ad.getlec()+";"+ad.getlab()+";"+ad.getnumOfStudent());
            hf++;
            roomD="";
            timeD="";
            daysD="";
            }catch(Exception e){
                System.out.println(e);
            }
        }
        // load sag data sa database
        tarongonNaUntaTaSunod();
        updateThefile();
        //System.out.println("SAD");
        int kl=1;
        for(String jk : classSched){
            System.out.println(kl+"Schedule in: "+jk);
            kl++;
        }
        //writeToExcelAllSched();
        listOfSched.clear();
        classSched.clear();
        JOptionPane.showMessageDialog(null, "SUCCESSFULLY CREATED");
        displaySelectedFile.clear();
        scheduleHolder.clear();
        getSchedDataFromDatabase();
        sy.setValue(null);
        prog.setValue(null);
        sem.setValue(null);
    }
    public void tarongonNaUntaTaSunod()throws Exception{
        Statement statement = dc.con.createStatement();
        String insertQuery = "INSERT INTO schedule (subjectID,sectionID,schedTime,schedDays,schedSem,schedSY,SchedStatus,facultyID,lec,lab,noOfStudents,schedRoom) "
                            +"Values ";
        String jhg = "";
        int i = 1;
        for(getSchedulesModel sm : scheduleHolder){
            System.out.println(sm.getidOfSub()+"------------------");
            if(sm.getidOfFac().contains("/")){
                String[] sepTime = sm.gettime().split("/");
                String[] sepRoom = sm.getroom().split("/");
                String[] sepDays = sm.getdays().split("/");
                String[] sepFac = sm.getidOfFac().split("/");
                String exp1 = sm.getlec();
                String exp2 = "0";
                
                for(int yu=0; yu<2;yu++){
                    jhg += "('" +sm.getidOfSub()+"','"+sm.getidOfSec()+"','"+sepTime[yu]+"','"+sepDays[yu]+"','"+sm.getsem()+"','"+sm.getsy()+"','"+sm.getstatus()+"','"+sepFac[yu]+"','"+exp1+"','"+exp2+"','"+sm.getnoOfStudents()+"','"+sepRoom[yu]+"')";
                    exp1="0";
                    exp2=sm.getlab();
                    if(i!=scheduleHolder.size() && yu == 0){
                        jhg += ", ";
                    }
                }
                insertQuery+=jhg;
            }else{
                jhg = "('" +sm.getidOfSub()+"','"+sm.getidOfSec()+"','"+sm.gettime()+"','"+sm.getdays()+"','"+sm.getsem()+"','"+sm.getsy()+"','"+sm.getstatus()+"','"+sm.getidOfFac()+"','"+sm.getlec()+"','"+sm.getlab()+"','"+sm.getnoOfStudents()+"','"+sm.getroom()+"')";
                
                
            }
            String zzz = insertQuery+jhg;
            System.out.println(i+"------"+zzz);
            int rowsAffected = statement.executeUpdate(zzz);
            jhg="";
            //sssssssssssssss
//            if(i!=scheduleHolder.size()){
//                insertQuery += ",";
//            }
            i++;
            
            
        }
        System.out.println(insertQuery);
        //int rowsAffected = statement.executeUpdate(insertQuery);
    } 
//    public void tarongonNaUntaTaSunod() throws Exception {
//    Statement statement = dc.con.createStatement();
//    String insertQuery = "INSERT INTO schedule (subjectID, sectionID, schedTime, schedDays, schedSem, schedSY, SchedStatus, facultyID, lec, lab, noOfStudents, schedRoom) VALUES ";
//    StringBuilder jhg = new StringBuilder();
//    int i = 1;
//
//    for (getSchedulesModel sm : scheduleHolder) {
//        // Validate subjectID before converting to integer
//        if (sm.getidOfSub() == null || sm.getidOfSub().isEmpty()) {
//            throw new IllegalArgumentException("Invalid subjectID: " + sm.getidOfSub());
//        }
//
//        int subjectID = Integer.valueOf(sm.getidOfSub());
//        
//        if (sm.getidOfFac().contains("/")) {
//            String[] sepTime = sm.gettime().split("/");
//            String[] sepRoom = sm.getroom().split("/");
//            String[] sepDays = sm.getdays().split("/");
//            String[] sepFac = sm.getidOfFac().split("/");
//            String exp1 = sm.getlec();
//            String exp2 = "0";
//
//            for (int yu = 0; yu < 2; yu++) {
//                jhg.append("('")
//                   .append(subjectID).append("','")
//                   .append(sm.getidOfSec()).append("','")
//                   .append(sepTime[yu]).append("','")
//                   .append(sepDays[yu]).append("','")
//                   .append(sm.getsem()).append("','")
//                   .append(sm.getsy()).append("','")
//                   .append(sm.getstatus()).append("','")
//                   .append(sepFac[yu]).append("','")
//                   .append(exp1).append("','")
//                   .append(exp2).append("','")
//                   .append(sm.getnoOfStudents()).append("','")
//                   .append(sepRoom[yu]).append("')");
//                exp1 = "0";
//                exp2 = sm.getlab();
//                if (yu == 0) {
//                    jhg.append(", ");
//                }
//            }
//        } else {
//            jhg.append("('")
//               .append(subjectID).append("','")
//               .append(sm.getidOfSec()).append("','")
//               .append(sm.gettime()).append("','")
//               .append(sm.getdays()).append("','")
//               .append(sm.getsem()).append("','")
//               .append(sm.getsy()).append("','")
//               .append(sm.getstatus()).append("','")
//               .append(sm.getidOfFac()).append("','")
//               .append(sm.getlec()).append("','")
//               .append(sm.getlab()).append("','")
//               .append(sm.getnoOfStudents()).append("','")
//               .append(sm.getroom()).append("')");
//        }
//        if (i != scheduleHolder.size()) {
//            jhg.append(", ");
//        }
//        i++;
//    }
//
//    insertQuery += jhg.toString();
//    System.out.println(insertQuery);
//    int rowsAffected = statement.executeUpdate(insertQuery);
//}


    public String generateSchedForlab2(String lecOrLab, String id, String secID){
        shuffle(id);
        
        
        ObservableList<String> time3hourDistanceAM = FXCollections.observableArrayList("8-11am","9-12nn");
        ObservableList<String> time3hourDistancePM = FXCollections.observableArrayList("1-4pm","2-5pm","3-6pm","4-7pm");
        
        
        ObservableList<String> dowFor3hours = FXCollections.observableArrayList("MW","TTH", "TW","WTH","THF","WF");
        
        Random r = new  Random();
        int AMorPM = r.nextInt(3)+1;
        List<String> useThisForTime3hour = new ArrayList<>();
        
        List<String> forDOW = new ArrayList<>();
        List<String> forTime = new ArrayList<>();
        
        boolean ifSchedIsAvailable = false;
        boolean roomIsRight = false;
           
        for(String rs: roomShuf){
            
            if(prog.getValue().equals("BSIT")){
                for(roomModel rm : rooms){
                    if(rs.equals(rm.getroomCODE()) && rm.getroomType().equals(lecOrLab)){
                        roomIsRight = true;
                        break;
                    }
                }
            }else if(prog.getValue().equals("BSA")){
                for(roomModel rm : rooms){
                    if(rs.equals(rm.getroomCODE()) && (rm.getroomType().equals(lecOrLab) || rm.getroomType().equals("Lecture"))){
                        roomIsRight = true;
                        break;
                    }
                }
            }else if(prog.getValue().equals("BSTLEd")){
                for(roomModel rm : rooms){
                    if(rs.equals(rm.getroomCODE()) && rm.getroomType().equals("TLE-Laboratory") && rm.getroomBuilding().equals("2")){
                        roomIsRight = true;
                        break;
                    }
                }
            }else{
                for(roomModel rm : rooms){
                    if(!rs.equals(rm.getroomCODE()) && !rm.getroomType().equals(lecOrLab)){
                        roomIsRight = true;
                        break;
                    }
                }
            }
            if(roomIsRight){
                if(AMorPM==1){
                    useThisForTime3hour.addAll(time3hourDistanceAM);
                    useThisForTime3hour.addAll(time3hourDistancePM);
                }else{
                    useThisForTime3hour.addAll(time3hourDistancePM);
                    useThisForTime3hour.addAll(time3hourDistanceAM);
                }
                forDOW.clear();
                forTime.clear();
                forDOW.addAll(dowFor3hours);
                forTime.addAll(useThisForTime3hour);
            
                
                for(String dow : forDOW){
                    for(String time: forTime){
                        String fac="", secs="";
                        for(schedModel sm: allSchedule){
                            if(!sm.getstatus().equals("0")){
                                fac=sm.getfacultyID();
                                secs=sm.getsectionID();
                                ifSchedIsAvailable = checkIfRoomTimeAndDaysIsAvailable(sm.getdays(), sm.gettime(), sm.getroom(), rs, time, dow, id,sm.getfacultyID(), sm.getsectionID(), secID);
                                if(!ifSchedIsAvailable){
                                    break;
                                }
                                firstSched=false;
                            }
                        }
                        if(firstSched){
                            firstSched=false;
                            return time+";"+dow+";"+rs;
                        }
                        if(!daysD.equals("") && ifSchedIsAvailable){
                            //System.out.println(daysD+"----"+timeD+"----"+roomD);
                            ifSchedIsAvailable = checkIfRoomTimeAndDaysIsAvailable(daysD, timeD, roomD, rs, time, dow, id,fac, secs, secID);
                        }
                        if(ifSchedIsAvailable){
                            return  time+";"+dow+";"+rs;
                        }
                    }
                }
            }
        }
        
        ObservableList<String> dowFor3hoursFD = FXCollections.observableArrayList("M","T","W","TH","F");
        String holdForTime="";
        String holdForDays="";
        String holdForRoom="";
        
        boolean schedF = false;
//        if(!holdForTime.contains("/")){
//
//            int schedFound=0;
//            while(schedFound<2){
//                int a = 0;
//                useThisForTime3hour.clear();
//                if(AMorPM==1){
//                    useThisForTime3hour.addAll(time3hourDistanceAM);
//                    useThisForTime3hour.addAll(time3hourDistancePM);
//                }else{
//                    useThisForTime3hour.addAll(time3hourDistancePM);
//                    useThisForTime3hour.addAll(time3hourDistanceAM);
//                }
//                forDOW.clear();
//                forTime.clear();
//                forDOW.addAll(dowFor3hoursFD);
//                forTime.addAll(useThisForTime3hour);
//                for(String rs: roomShuf){
//                    if(prog.getValue().equals("BSIT")){
//                        for(roomModel rm : rooms){
//                            if(rs.equals(rm.getroomCODE()) && rm.getroomType().equals(lecOrLab)){
//                                roomIsRight = true;
//                                break;
//                            }
//                        }
//                    }else if(prog.getValue().equals("BSA")){
//                        for(roomModel rm : rooms){
//                            if(rs.equals(rm.getroomCODE()) && (rm.getroomType().equals(lecOrLab) || rm.getroomType().equals("Lecture"))){
//                                roomIsRight = true;
//                                break;
//                            }
//                        }
//                    }else if(prog.getValue().equals("BSTLEd")){
//                        for(roomModel rm : rooms){
//                            if(rs.equals(rm.getroomCODE()) && rm.getroomType().equals(lecOrLab) && rm.getroomBuilding().equals("2")){
//                                roomIsRight = true;
//                                break;
//                            }
//                        }
//                    }
//                    else if(prog.getValue().equals("BSBA")){
//                        for(roomModel rm : rooms){
//
//                            if(rm.getroomBuilding().equals("1") && rm.getroomType().equals("Lecture") && rs.equals(rm.getroomCODE())){
//                                roomIsRight = true;
//                                break;
//                            }
//
//                        }
//                    }
//                    if(id.equals("144")){
//                        if(rs.equals("CL1") || rs.equals("CL2")){
//                            roomIsRight = true;
//                        }else{
//                            roomIsRight = false;
//                        }
//                    }
//                    if(roomIsRight){
//                        for(String dow : forDOW){
//                            for(String time: forTime){
//                                String fac="",secs="";
//                                for(schedModel sm: allSchedule){
//                                    if(!sm.getstatus().equals("0")){
//                                        fac=sm.getfacultyID();
//                                        secs=sm.getsectionID();
//                                        schedF = checkIfRoomTimeAndDaysIsAvailable(sm.getdays(), sm.gettime(), sm.getroom(), rs, time, dow,id,sm.getfacultyID(), sm.getsectionID(), secID);
//                                        if(!schedF){
//                                            break;
//                                        }
//                                        firstSched=false;
//                                    }
//                                }
//                                if(firstSched){
//                                    firstSched=false;
//                                    holdForTime+=time;
//                                    holdForDays+=dow;
//                                    holdForRoom+=rs;
//                                }
//                                if(!holdForDays.equals("") && schedF){
//                                    schedF = checkIfRoomTimeAndDaysIsAvailable(holdForDays, holdForTime, holdForRoom, rs, time, dow, id,fac, secs, secID);
//
//                                }
//                                if(schedF){
//                                    if(holdForTime.equals("")){
//                                        holdForTime+=time;
//                                        holdForDays+=dow;
//                                        holdForRoom+=rs;
//                                    }else{
//                                        holdForTime+="/"+time;
//                                        holdForDays+="/"+dow;
//                                        holdForRoom+="/"+rs;
//                                    }
//                                }
//                                if(schedF){break;}
//                            }
//                            if(schedF){break;}
//                        }
//                        if(schedF){break;}
//                    }
//                    if(schedF){break;}
//                }
//                System.out.println(holdForTime+"--"+holdForDays+"--"+holdForRoom);
//                System.out.println("OHHHHH-----"+ifSchedIsAvailable);
//                a++;
//                schedFound++;
//                schedF=false;
//            }
//        }
//        if(holdForTime.contains("/")){
//            ifSchedIsAvailable=true;
//            if(ifSchedIsAvailable){
//                return holdForTime+";"+holdForDays+";"+holdForRoom;
//            }
//        }
//        
        
        ObservableList<String> time2hourDistanceAM = FXCollections.observableArrayList("8-10am","9-11am","10-12nn");
        ObservableList<String> time2hourDistancePM = FXCollections.observableArrayList("1-3pm","2-4pm","3-5pm","4-6pm","5-7pm");
        
        holdForTime="";
        holdForDays="";
        holdForRoom="";
        if(!holdForTime.contains("/")){

            int schedFound=0;
            while(schedFound<3){
                int a = 0;
                useThisForTime3hour.clear();
                if(AMorPM==1){
                    useThisForTime3hour.addAll(time2hourDistanceAM);
                    useThisForTime3hour.addAll(time2hourDistancePM);
                }else{
                    useThisForTime3hour.addAll(time2hourDistancePM);
                    useThisForTime3hour.addAll(time2hourDistanceAM);
                }
                forDOW.clear();
                forTime.clear();
                forDOW.addAll(dowFor3hoursFD);
                forTime.addAll(useThisForTime3hour);
                for(String rs: roomShuf){
                    if(prog.getValue().equals("BSIT")){
                        for(roomModel rm : rooms){
                            if(rs.equals(rm.getroomCODE()) && rm.getroomType().equals(lecOrLab)){
                                roomIsRight = true;
                                break;
                            }
                        }
                    }else if(prog.getValue().equals("BSA")){
                        for(roomModel rm : rooms){
                            if(rs.equals(rm.getroomCODE()) && (rm.getroomType().equals(lecOrLab) || rm.getroomType().equals("Lecture"))){
                                roomIsRight = true;
                                break;
                            }
                        }
                    }else if(prog.getValue().equals("BSTLEd")){
                        for(roomModel rm : rooms){
                            if(rs.equals(rm.getroomCODE()) &&  rm.getroomBuilding().equals("2")){
                                roomIsRight = true;
                                break;
                            }
                        }
                    }
                    else if(prog.getValue().equals("BSBA")){
                        for(roomModel rm : rooms){

                            if(rm.getroomBuilding().equals("1") && rm.getroomType().equals("Lecture") && rs.equals(rm.getroomCODE())){
                                roomIsRight = true;
                                break;
                            }

                        }
                    }
                    if(id.equals("144")){
                        if(rs.equals("CL1") || rs.equals("CL2")){
                            roomIsRight = true;
                        }else{
                            roomIsRight = false;
                        }
                    }
                    if(roomIsRight){
                        for(String dow : forDOW){
                            for(String time: forTime){
                                String fac="",secs="";
                                for(schedModel sm: allSchedule){
                                    if(!sm.getstatus().equals("0")){
                                        fac=sm.getfacultyID();
                                        secs=sm.getsectionID();
                                        schedF = checkIfRoomTimeAndDaysIsAvailable(sm.getdays(), sm.gettime(), sm.getroom(), rs, time, dow,id,sm.getfacultyID(), sm.getsectionID(), secID);
                                        if(!schedF){
                                            break;
                                        }
                                        firstSched=false;
                                    }
                                }
                                if(firstSched){
                                    firstSched=false;
                                    holdForTime+=time;
                                    holdForDays+=dow;
                                    holdForRoom+=rs;
                                }
                                if(!holdForDays.equals("") && schedF){
                                    schedF = checkIfRoomTimeAndDaysIsAvailable(holdForDays, holdForTime, holdForRoom, rs, time, dow, id,fac, secs, secID);

                                }
                                if(!daysD.equals("") && schedF){
                                    //System.out.println(daysD+"----"+timeD+"----"+roomD);
                                    schedF = checkIfRoomTimeAndDaysIsAvailable(daysD, timeD, roomD, rs, time, dow, id,fac, secs, secID);

                                }
                                if(schedF){
                                    if(holdForTime.equals("")){
                                        holdForTime+=time;
                                        holdForDays+=dow;
                                        holdForRoom+=rs;
                                    }else{
                                        holdForTime+="/"+time;
                                        holdForDays+="/"+dow;
                                        holdForRoom+="/"+rs;
                                    }
                                }
                                if(schedF){break;}
                            }
                            if(schedF){break;}
                        }
                        if(schedF){break;}
                    }
                    if(schedF){break;}
                }
                System.out.println(holdForTime+"--"+holdForDays+"--"+holdForRoom);
                System.out.println("OHHHHH-----"+ifSchedIsAvailable);
                a++;
                schedFound++;
                schedF=false;
            }
        }
        ifSchedIsAvailable=true;
        if(ifSchedIsAvailable){
            return holdForTime+";"+holdForDays+";"+holdForRoom;
        }
        return "none;none;none";
    }
    public String generateSchedForlab1(String lecOrLab, String id, String secID){
            
        shuffle(id);
        System.out.println("here at generateSchedForlab1");
        ObservableList<String> time1and30minutesDisAM = FXCollections.observableArrayList("8-9:30am","9-10:30am","9:30-11am", "10-11:30am","10:30-12nn");
        ObservableList<String> time1and30minutesDisPM = FXCollections.observableArrayList("1-2:30pm","2:30-4pm","2-3:30pm", "3-4:30pm","3:30-5pm","4-5:30pm","4:30-6pm", "5-6:30pm", "5:30-7pm","6-7:30pm","6:30-8pm","7-8:30pm","7:30-9pm");
        
        ObservableList<String> time1hourDistanceAM = FXCollections.observableArrayList("8-9am","9-10am","10-11am","11-12nn");
        ObservableList<String> time1hourDistancePM = FXCollections.observableArrayList("1-2pm","2-3pm","3-4pm","4-5pm","5-6pm","6-7pm","7-8pm","8-9pm");
        
        ObservableList<String> time3hourDistanceAM = FXCollections.observableArrayList("8-11am","9-12nn");
        ObservableList<String> time3hourDistancePM = FXCollections.observableArrayList("1-4pm","2-5pm","3-6pm","4-7pm","5-8pm","6-9pm");
        
        
        ObservableList<String> dowFor1and30hours = FXCollections.observableArrayList("MW","TTH");
        ObservableList<String> dowFor3hours = FXCollections.observableArrayList("M","T","W","TH","F");
        ObservableList<String> dowFor1hours = FXCollections.observableArrayList("MWF");
        
        if(secID.equals("80")){
            time1hourDistanceAM.clear();
            time1hourDistancePM.clear();
            ObservableList<String> time1hourDistancePMDef = FXCollections.observableArrayList("9-10am","10-11am","1-2pm","2-3pm","3-4pm","11-12nn","8-9am","4-5pm","5-6pm","6-7pm","7-8pm","8-9pm");
            time1hourDistanceAM.addAll(time1hourDistancePMDef);
            
            time1and30minutesDisAM.clear();
            time1and30minutesDisPM.clear();
            ObservableList<String> time1and30minutesDisAMDef = FXCollections.observableArrayList("9-10:30am", "9:30-11am","10-11:30am","10:30-12nn","1-2:30pm","2:30-4pm","2-3:30pm", "3-4:30pm","3:30-5pm","8-9:30am","4-5:30pm","4:30-6pm", "5-6:30pm", "5:30-7pm","6-8:30pm","6:30-9pm");
            time1and30minutesDisAM.addAll(time1and30minutesDisAMDef);
        }
        
        Random r = new  Random();
        int AMorPM = r.nextInt(3)+1;
        List<String> useThisForTime1hour = new ArrayList<>();
        List<String> useThisForTime1and30hour = new ArrayList<>();
        List<String> useThisForTime3hour = new ArrayList<>();
        
        List<String> forDOW = new ArrayList<>();
        List<String> forTime = new ArrayList<>();
        
        boolean ifSchedIsAvailable = false;
        
        for(String rs: roomShuf){
            boolean roomIsRight = false;
            if(prog.getValue().equals("BSIT")){
                for(roomModel rm : rooms){
                    if(rs.equals(rm.getroomCODE()) && rm.getroomType().equals("IT-Laboratory") ){
                        roomIsRight = true;
                        break;
                    }
                }
            }else if(prog.getValue().equals("BSA")){
                for(roomModel rm : rooms){
                    if(rs.equals(rm.getroomCODE()) && (rm.getroomType().equals("Agri-Laboratory") || rm.getroomType().equals("Lecture"))&& rm.getroomBuilding().equals("1")){
                        roomIsRight = true;
                        break;
                    }
                }
            }else if(prog.getValue().equals("BSTLEd")){
                for(roomModel rm : rooms){
                    if(rs.equals(rm.getroomCODE()) && rm.getroomType().equals(lecOrLab) && rm.getroomBuilding().equals("2")){
                        roomIsRight = true;
                        break;
                    }
                }
            }else if(prog.getValue().equals("BSBA")){
                for(roomModel rm : rooms){
                    if(rm.getroomBuilding().equals("1") && rm.getroomType().equals("Lecture") && rs.equals(rm.getroomCODE())){
                        roomIsRight = true;
                        break;
                    }
                    
                }
            }
            if(id.equals("144")){
                if(rs.equals("CL1")){
                    roomIsRight = true;
                }else{
                    roomIsRight = false;
                }
            }
            if(roomIsRight){
                
                int a = 0;
                while(a<3){
                    switch(a){
                        case 0:
                            if(AMorPM==1){//1hour distance
                                useThisForTime1hour.addAll(time1hourDistanceAM);
                                useThisForTime1hour.addAll(time1hourDistancePM);
                            }else{
                                useThisForTime1hour.addAll(time1hourDistancePM);
                                useThisForTime1hour.addAll(time1hourDistanceAM);
                            }
                            forDOW.clear();
                            forTime.clear();
                            forDOW.addAll(dowFor1hours);
                            forTime.addAll(useThisForTime1hour);
                            break;
                            
                        case 2:
                            if(AMorPM==1){
                                useThisForTime3hour.addAll(time3hourDistanceAM);
                                useThisForTime3hour.addAll(time3hourDistancePM);
                            }else{
                                useThisForTime3hour.addAll(time3hourDistancePM);
                                useThisForTime3hour.addAll(time3hourDistanceAM);
                            }
                            forDOW.clear();
                            forTime.clear();
                            forDOW.addAll(dowFor3hours);
                            forTime.addAll(useThisForTime3hour);
                            break;
                        case 1://3hours distance
                            if(AMorPM==1){//1and30minutes distance
                                useThisForTime1and30hour.addAll(time1and30minutesDisAM);
                                useThisForTime1and30hour.addAll(time1and30minutesDisPM);
                            }else{
                                useThisForTime1and30hour.addAll(time1and30minutesDisPM);
                                useThisForTime1and30hour.addAll(time1and30minutesDisAM);
                            }
                            forDOW.clear();
                            forTime.clear();
                            forDOW.addAll(dowFor1and30hours);
                            forTime.addAll(useThisForTime1and30hour);
                            break;
                    }

                    for(String dow : forDOW){
                        for(String time: forTime){
                            String fac="",secs="";
                            for(schedModel sm: allSchedule){
                                if(!sm.getstatus().equals("0")){
                                    fac=sm.getfacultyID();
                                    secs=sm.getsectionID();
                                    ifSchedIsAvailable = checkIfRoomTimeAndDaysIsAvailable(sm.getdays(), sm.gettime(), sm.getroom(), rs, time, dow,id,sm.getfacultyID(), sm.getsectionID(), secID);
                                    if(!ifSchedIsAvailable){
                                        break;
                                    }
                                    firstSched=false;
                                }
                            }
                            if(firstSched){
                                firstSched=false;
                                return time+";"+dow+";"+rs;
                            }
                            if(!daysD.equals("") && ifSchedIsAvailable){
                                //System.out.println(daysD+"----"+timeD+"----"+roomD);
                                ifSchedIsAvailable = checkIfRoomTimeAndDaysIsAvailable(daysD, timeD, roomD, rs, time, dow, id,fac, secs, secID);
                            
                            }
                            if(ifSchedIsAvailable){
                                return  time+";"+dow+";"+rs;
                            }
                        }
                    }
                    a++;
                }
            }
        }
        System.out.println("SSADSAD----");
        
        String holdForTime="";
        String holdForDays="";
        String holdForRoom="";
        
        int schedFound=0;
        boolean schedF = false;
        if(!holdForTime.contains("/")){

            schedFound=0;
            while(schedFound<3){
                int a = 0;
                useThisForTime1and30hour.clear();
                if(AMorPM==1){
                    useThisForTime1and30hour.addAll(time1hourDistanceAM);
                    useThisForTime1and30hour.addAll(time1hourDistancePM);
                }else{
                    useThisForTime1and30hour.addAll(time1hourDistancePM);
                    useThisForTime1and30hour.addAll(time1hourDistanceAM);
                }
                forDOW.clear();
                forTime.clear();
                forDOW.addAll(dowFor3hours);
                forTime.addAll(useThisForTime1and30hour);


                for(String rs: roomShuf){
                    boolean roomIsRight = false;
                    if(prog.getValue().equals("BSIT")){
                        for(roomModel rm : rooms){
                            if(rs.equals(rm.getroomCODE()) && rm.getroomBuilding().equals("1") && (rm.getroomType().equals("Lecture") || rm.getroomType().equals("IT-Laboratory"))){
                                roomIsRight = true;
                                break;
                            }
                        }
                    }else if(prog.getValue().equals("BSA")){
                        for(roomModel rm : rooms){
                            if(rm.getroomBuilding().equals("1") && rs.equals(rm.getroomCODE()) && (rm.getroomType().equals("Agri-Laboratory") || rm.getroomType().equals("Lecture"))){
                                roomIsRight = true;
                                break;
                            }
                        }
                    }else if(prog.getValue().equals("BSTLEd")){
                        for(roomModel rm : rooms){
                            if(rs.equals(rm.getroomCODE()) && (rm.getroomType().equals("TLE-Laboratory") || rm.getroomType().equals("Lecture")) && rm.getroomBuilding().equals("2")){
                                roomIsRight = true;
                                break;
                            }
                        }
                    }
                    else if(prog.getValue().equals("BSBA")){
                        for(roomModel rm : rooms){

                            if(rm.getroomBuilding().equals("1") && rm.getroomType().equals("Lecture") && rs.equals(rm.getroomCODE())){
                                roomIsRight = true;
                                break;
                            }

                        }
                    }
                    if(id.equals("144")){
                        if(rs.equals("CL1") || rs.equals("CL2")){
                            roomIsRight = true;
                        }else{
                            roomIsRight = false;
                        }
                    }
                    if(roomIsRight){
                        for(String dow : forDOW){
                            for(String time: forTime){
                                String fac="",secs="";
                                for(schedModel sm: allSchedule){
                                    if(!sm.getstatus().equals("0")){
                                        fac=sm.getfacultyID();
                                        secs=sm.getsectionID();
                                        schedF = checkIfRoomTimeAndDaysIsAvailable(sm.getdays(), sm.gettime(), sm.getroom(), rs, time, dow,id,sm.getfacultyID(), sm.getsectionID(), secID);
                                        if(!schedF){
                                            break;
                                        }
                                        firstSched=false;
                                    }
                                }
                                if(firstSched){
                                    firstSched=false;
                                    holdForTime+=time;
                                    holdForDays+=dow;
                                    holdForRoom+=rs;
                                }
                                if(!holdForDays.equals("") && schedF){
                                    schedF = checkIfRoomTimeAndDaysIsAvailable(holdForDays, holdForTime, holdForRoom, rs, time, dow, id,fac, secs, secID);

                                }
                                if(!daysD.equals("") && schedF){
                                    //System.out.println(daysD+"----"+timeD+"----"+roomD);
                                    schedF = checkIfRoomTimeAndDaysIsAvailable(daysD, timeD, roomD, rs, time, dow, id,fac, secs, secID);

                                }
                                if(schedF){
                                    if(holdForTime.equals("")){
                                        holdForTime+=time;
                                        holdForDays+=dow;
                                        holdForRoom+=rs;
                                    }else{
                                        holdForTime+="/"+time;
                                        holdForDays+="/"+dow;
                                        holdForRoom+="/"+rs;
                                    }
                                }
                                if(schedF){break;}
                            }
                            if(schedF){break;}
                        }
                        if(schedF){break;}
                    }
                    if(schedF){break;}
                }
                System.out.println(holdForTime+"--"+holdForDays+"--"+holdForRoom);
                System.out.println("OHHHHH-----"+ifSchedIsAvailable);
                a++;
                schedFound++;
                schedF=false;
            }
        }
//        ifSchedIsAvailable=true;
        
//        if(holdForTime.contains("/")){
//            System.out.println("return a value");
//            System.out.println(holdForTime+";"+holdForDays+";"+holdForRoom);
//            return holdForTime+";"+holdForDays+";"+holdForRoom;
//        }
        
        System.out.println("generateSchedForlab1");
        return "none;none;none";
    }
    public String generateSchedFor3hoursLectureNSTP(String lecOrLab, String id, String secID, String bN){
        shuffle(id);
        System.out.println("here at generateSchedFor3hoursLectureNSTP");
        roomD="";
        timeD="";
        daysD="";
        
        ObservableList<String> time3hourDistanceAM = FXCollections.observableArrayList("8-11am","9-12nn");
        ObservableList<String> time3hourDistancePM = FXCollections.observableArrayList("1-4pm","2-5pm","3-6pm","4-7pm","5-8pm","6-9pm");
        
        ObservableList<String> dowFor3hours = FXCollections.observableArrayList("S");
        
        
        Random r = new  Random();
        int AMorPM = r.nextInt(3)+1;
        List<String> useThisForTime3hour = new ArrayList<>();
        
        List<String> forDOW = new ArrayList<>();
        List<String> forTime = new ArrayList<>();
        
        boolean ifSchedIsAvailable = false;
        boolean roomIsRight = false;
        for(String rs: roomShuf){
            if(prog.getValue().equals("BSTLEd")){
                for(roomModel rm : rooms){
                    if(rm.getroomBuilding().equals("2") && rm.getroomType().equals("Lecture") && rm.getroomCODE().equals(rs)){
                        roomIsRight = true;
                        break;
                    }else{
                        roomIsRight = false;
                    }
                }    
            }else if(prog.getValue().equals("BSBA")){
                for(roomModel rm : rooms){
                    if(rm.getroomBuilding().equals("1") && rm.getroomType().equals("Lecture") && rm.getroomCODE().equals(rs)){
                        roomIsRight = true;
                        break;
                    }else{
                        roomIsRight = false;
                    }
                }
            }else if(prog.getValue().equals("BSA")){
                for(roomModel rm : rooms){
                    if(rm.getroomBuilding().equals("1") && (rm.getroomType().equals("Lecture") || rm.getroomType().equals("Agri-Laboratory")) && rm.getroomCODE().equals(rs)){
                        roomIsRight = true;
                        break;
                    }else{
                        roomIsRight = false;
                    }
                }
            }
            else{
                for(roomModel rm : rooms){
                    if(rm.getroomBuilding().equals(bN)){
                        if(rs.equals(rm.getroomCODE()) && rm.getroomType().equals(lecOrLab) ){
                            roomIsRight = true;
                            break;
                        }
                    }
                }
            }
            if(id.equals("144")){
                if(rs.equals("CL1") || rs.equals("CL2")){
                    roomIsRight = true;
                }else{
                    roomIsRight = false;
                }
            }
            //System.out.println(timeD+"-ooooooooooo-"+daysD+"-ooooooooooo-"+roomD);
            if(roomIsRight){
                    
                useThisForTime3hour.clear();
                if(AMorPM==1){
                    useThisForTime3hour.addAll(time3hourDistanceAM);
                    useThisForTime3hour.addAll(time3hourDistancePM);
                }else{
                    useThisForTime3hour.addAll(time3hourDistancePM);
                    useThisForTime3hour.addAll(time3hourDistanceAM);
                }
                forDOW.clear();
                forTime.clear();
                forDOW.addAll(dowFor3hours);
                forTime.addAll(useThisForTime3hour);
                            

                for(String dow : forDOW){
                    for(String time: forTime){
                        String fac="",secs="";
                        for(schedModel sm: allSchedule){
                            if(!sm.getstatus().equals("0")){
                                fac=sm.getfacultyID();
                                secs=sm.getsectionID();
                                ifSchedIsAvailable = checkIfRoomTimeAndDaysIsAvailable(sm.getdays(), sm.gettime(), sm.getroom(), rs, time, dow,id,sm.getfacultyID(), sm.getsectionID(), secID);
                                if(!ifSchedIsAvailable){
                                    break;
                                }
                                firstSched=false;
                            }
                        }
                        if(firstSched){
                            firstSched=false;
                            return time+";"+dow+";"+rs;
                        }
                        if(!daysD.equals("") && ifSchedIsAvailable){
                            //System.out.println(daysD+"----"+timeD+"----"+roomD);
                            ifSchedIsAvailable = checkIfRoomTimeAndDaysIsAvailable(daysD, timeD, roomD, rs, time, dow, id,fac, secs, secID);

                        }
                        if(ifSchedIsAvailable){
                            return  time+";"+dow+";"+rs;
                        }
                    }
                }
                    //System.out.println("Change sya from generateSchedFor3hoursLecture");
                
                
            }
        }
        return "none;none;none";
    }
    public String generateSchedFor3hoursLecture(String lecOrLab, String id, String secID, String bN){
        shuffle(id);
        System.out.println("here at generateSchedFor3hoursLecture");
        roomD="";
        timeD="";
        daysD="";
        ObservableList<String> time1and30minutesDisAM = FXCollections.observableArrayList("8-9:30am","9-10:30am", "9:30-11am","10-11:30am","10:30-12nn");
        ObservableList<String> time1and30minutesDisPM = FXCollections.observableArrayList("1-2:30pm","2:30-4pm","2-3:30pm", "3-4:30pm","3:30-5pm","4-5:30pm","4:30-6pm", "5-6:30pm", "5:30-7pm","6-7:30pm","6:30-8pm","7-8:30pm","7:30-9pm");
        
        ObservableList<String> time1hourDistanceAM = FXCollections.observableArrayList("8-9am","9-10am","10-11am","11-12nn");
        ObservableList<String> time1hourDistancePM = FXCollections.observableArrayList("1-2pm","2-3pm","3-4pm","4-5pm","5-6pm","6-7pm");
        
        ObservableList<String> time3hourDistanceAM = FXCollections.observableArrayList("8-11am","9-12nn");
        ObservableList<String> time3hourDistancePM = FXCollections.observableArrayList("1-4pm","2-5pm","3-6pm");
        
        ObservableList<String> dowFor1and30hours = FXCollections.observableArrayList("MW","TTH");
        ObservableList<String> dowFor3hours = FXCollections.observableArrayList("M","T","W","TH","F");
        ObservableList<String> dowFor1hours = FXCollections.observableArrayList("MWF");
        
        if(secID.equals("80")){
            time1hourDistanceAM.clear();
            time1hourDistancePM.clear();
            ObservableList<String> time1hourDistancePMDef = FXCollections.observableArrayList("9-10am","10-11am","1-2pm","2-3pm","3-4pm","11-12nn","8-9am","4-5pm","5-6pm","6-7pm");
            time1hourDistanceAM.addAll(time1hourDistancePMDef);
            
            time1and30minutesDisAM.clear();
            time1and30minutesDisPM.clear();
            ObservableList<String> time1and30minutesDisAMDef = FXCollections.observableArrayList("9-10:30am", "9:30-11am","10-11:30am","10:30-12nn","1-2:30pm","2:30-4pm","2-3:30pm", "3-4:30pm","3:30-5pm","8-9:30am","4-5:30pm","4:30-6pm", "5-6:30pm", "5:30-7pm");
            time1and30minutesDisAM.addAll(time1and30minutesDisAMDef);
        }
        
        
        Random r = new  Random();
        int AMorPM = r.nextInt(3)+1;
        List<String> useThisForTime1hour = new ArrayList<>();
        List<String> useThisForTime1and30hour = new ArrayList<>();
        List<String> useThisForTime3hour = new ArrayList<>();
        
        List<String> forDOW = new ArrayList<>();
        List<String> forTime = new ArrayList<>();
        
        boolean ifSchedIsAvailable = false;
        boolean roomIsRight = false;
        for(String rs: roomShuf){
            if(prog.getValue().equals("BSTLEd")){
                for(roomModel rm : rooms){
                    if(rm.getroomBuilding().equals("2") && rm.getroomType().equals("Lecture") && rm.getroomCODE().equals(rs)){
                        roomIsRight = true;
                        break;
                    }else{
                        roomIsRight = false;
                    }
                }    
            }else if(prog.getValue().equals("BSBA")){
                for(roomModel rm : rooms){
                    if(rm.getroomBuilding().equals("1") && rm.getroomType().equals("Lecture") && rm.getroomCODE().equals(rs)){
                        roomIsRight = true;
                        break;
                    }else{
                        roomIsRight = false;
                    }
                }
            }else if(prog.getValue().equals("BSA")){
                for(roomModel rm : rooms){
                    if(rm.getroomBuilding().equals("1") && (rm.getroomType().equals("Lecture") || rm.getroomType().equals("Agri-Laboratory")) && rm.getroomCODE().equals(rs)){
                        roomIsRight = true;
                        break;
                    }else{
                        roomIsRight = false;
                    }
                }
            }
            else{
                for(roomModel rm : rooms){
                    if(rm.getroomBuilding().equals(bN)){
                        if(rs.equals(rm.getroomCODE()) && rm.getroomType().equals(lecOrLab) ){
                            roomIsRight = true;
                            break;
                        }
                    }
                }
            }
            if(id.equals("144")){
                if(rs.equals("CL1") || rs.equals("CL2")){
                    roomIsRight = true;
                }else{
                    roomIsRight = false;
                }
            }
            //System.out.println(timeD+"-ooooooooooo-"+daysD+"-ooooooooooo-"+roomD);
            if(roomIsRight){
                int a = 0;
                while(a<3){
                    switch(a){
                        case 0:
                            useThisForTime1hour.clear();
                            if(AMorPM==1){//1hour distance
                                useThisForTime1hour.addAll(time1hourDistanceAM);
                                useThisForTime1hour.addAll(time1hourDistancePM);
                            }else{
                                useThisForTime1hour.addAll(time1hourDistancePM);
                                useThisForTime1hour.addAll(time1hourDistanceAM);
                            }
                            forDOW.clear();
                            forTime.clear();
                            forDOW.addAll(dowFor1hours);
                            forTime.addAll(useThisForTime1hour);
                            break;
                        case 1://1and30minutes distance
                            useThisForTime1and30hour.clear();
                            if(AMorPM==1){
                                useThisForTime1and30hour.addAll(time1and30minutesDisAM);
                                useThisForTime1and30hour.addAll(time1and30minutesDisPM);
                            }else{
                                useThisForTime1and30hour.addAll(time1and30minutesDisPM);
                                useThisForTime1and30hour.addAll(time1and30minutesDisAM);
                            }
                            forDOW.clear();
                            forTime.clear();
                            forDOW.addAll(dowFor1and30hours);
                            forTime.addAll(useThisForTime1and30hour);
                            break;
                        case 2://3hours distance
                            useThisForTime3hour.clear();
                            if(AMorPM==1){
                                useThisForTime3hour.addAll(time3hourDistanceAM);
                                useThisForTime3hour.addAll(time3hourDistancePM);
                            }else{
                                useThisForTime3hour.addAll(time3hourDistancePM);
                                useThisForTime3hour.addAll(time3hourDistanceAM);
                            }
                            forDOW.clear();
                            forTime.clear();
                            forDOW.addAll(dowFor3hours);
                            forTime.addAll(useThisForTime3hour);
                            break;

                    }

                    for(String dow : forDOW){
                        for(String time: forTime){
                            String fac="",secs="";
                            for(schedModel sm: allSchedule){
                                if(!sm.getstatus().equals("0")){
                                    fac=sm.getfacultyID();
                                    secs=sm.getsectionID();
                                    ifSchedIsAvailable = checkIfRoomTimeAndDaysIsAvailable(sm.getdays(), sm.gettime(), sm.getroom(), rs, time, dow,id,sm.getfacultyID(), sm.getsectionID(), secID);
                                    if(!ifSchedIsAvailable){
                                        break;
                                    }
                                    firstSched=false;
                                }
                            }
                            if(firstSched){
                                firstSched=false;
                                return time+";"+dow+";"+rs;
                            }
                            if(!daysD.equals("") && ifSchedIsAvailable){
                                //System.out.println(daysD+"----"+timeD+"----"+roomD);
                                ifSchedIsAvailable = checkIfRoomTimeAndDaysIsAvailable(daysD, timeD, roomD, rs, time, dow, id,fac, secs, secID);
                            
                            }
                            if(ifSchedIsAvailable){
                                return  time+";"+dow+";"+rs;
                            }
                        }
                    }
                    //System.out.println("Change sya from generateSchedFor3hoursLecture");
                    a++;
                }
            }
        }
        System.out.println("HAKDOG");
        String holdForTime="";
        String holdForDays="";
        String holdForRoom="";
        
        int schedFound=0;
        boolean schedF = false;
        if(!holdForTime.contains("/")){

            schedFound=0;
            while(schedFound<3){
                int a = 0;
                useThisForTime1and30hour.clear();
                if(AMorPM==1){
                    useThisForTime1and30hour.addAll(time1hourDistanceAM);
                    useThisForTime1and30hour.addAll(time1hourDistancePM);
                }else{
                    useThisForTime1and30hour.addAll(time1hourDistancePM);
                    useThisForTime1and30hour.addAll(time1hourDistanceAM);
                }
                forDOW.clear();
                forTime.clear();
                forDOW.addAll(dowFor3hours);
                forTime.addAll(useThisForTime1and30hour);


                for(String rs: roomShuf){
                    roomIsRight = false;
                    if(prog.getValue().equals("BSIT")){
                        for(roomModel rm : rooms){
                            if(rs.equals(rm.getroomCODE()) && rm.getroomBuilding().equals("1") && (rm.getroomType().equals("Lecture") || rm.getroomType().equals("IT-Laboratory"))){
                                roomIsRight = true;
                                break;
                            }
                        }
                    }else if(prog.getValue().equals("BSA")){
                        for(roomModel rm : rooms){
                            if(rm.getroomBuilding().equals("1") && rs.equals(rm.getroomCODE()) && (rm.getroomType().equals(lecOrLab) || rm.getroomType().equals("Lecture"))){
                                roomIsRight = true;
                                break;
                            }
                        }
                    }else if(prog.getValue().equals("BSTLEd")){
                        for(roomModel rm : rooms){
                            if(rs.equals(rm.getroomCODE()) && (rm.getroomType().equals("TLE-Laboratory") || rm.getroomType().equals("Lecture")) && rm.getroomBuilding().equals("2")){
                                roomIsRight = true;
                                break;
                            }
                        }
                    }
                    else if(prog.getValue().equals("BSBA")){
                        for(roomModel rm : rooms){

                            if(rm.getroomBuilding().equals("1") && rm.getroomType().equals("Lecture") && rs.equals(rm.getroomCODE())){
                                roomIsRight = true;
                                break;
                            }

                        }
                    }
//                    if(id.equals("144")){
//                        if(rs.equals("CL1") || rs.equals("CL2")){
//                            roomIsRight = true;
//                        }else{
//                            roomIsRight = false;
//                        }
//                    }
                    if(roomIsRight){
                        for(String dow : forDOW){
                            for(String time: forTime){
                                String fac="",secs="";
                                for(schedModel sm: allSchedule){
                                    if(!sm.getstatus().equals("0")){
                                        fac=sm.getfacultyID();
                                        secs=sm.getsectionID();
                                        schedF = checkIfRoomTimeAndDaysIsAvailable(sm.getdays(), sm.gettime(), sm.getroom(), rs, time, dow,id,sm.getfacultyID(), sm.getsectionID(), secID);
                                        if(!schedF){
                                            break;
                                        }
                                        firstSched=false;
                                    }
                                }
                                if(firstSched){
                                    firstSched=false;
                                    holdForTime+=time;
                                    holdForDays+=dow;
                                    holdForRoom+=rs;
                                }
                                if(!holdForDays.equals("") && schedF){
                                    schedF = checkIfRoomTimeAndDaysIsAvailable(holdForDays, holdForTime, holdForRoom, rs, time, dow, id,fac, secs, secID);

                                }
                                if(!daysD.equals("") && schedF){
                                    //System.out.println(daysD+"----"+timeD+"----"+roomD);
                                    schedF = checkIfRoomTimeAndDaysIsAvailable(daysD, timeD, roomD, rs, time, dow, id,fac, secs, secID);

                                }
                                if(schedF){
                                    if(holdForTime.equals("")){
                                        holdForTime+=time;
                                        holdForDays+=dow;
                                        holdForRoom+=rs;
                                    }else{
                                        holdForTime+="/"+time;
                                        holdForDays+="/"+dow;
                                        holdForRoom+="/"+rs;
                                    }
                                }
                                if(schedF){break;}
                            }
                            if(schedF){break;}
                        }
                        if(schedF){break;}
                    }
                    if(schedF){break;}
                }
                System.out.println(holdForTime+"--"+holdForDays+"--"+holdForRoom);
                System.out.println("OHHHHH-----"+ifSchedIsAvailable);
                a++;
                schedFound++;
                schedF=false;
            }
        }
        ifSchedIsAvailable=true;
        
        if(ifSchedIsAvailable){
            System.out.println("return a value");
            System.out.println(holdForTime+";"+holdForDays+";"+holdForRoom);
            return holdForTime+";"+holdForDays+";"+holdForRoom;
        }
        
        System.out.println("generateSchedFor3hoursLecture");
        return "none;none;none";
    }
    
    
    public String generateSchedFor2hoursLecture(String lecOrLab, String id, String secID, String bN){
        shuffle(id);
        System.out.println("here at generateSchedFor2hoursLecture");
        ObservableList<String> time1hourDistanceAM = FXCollections.observableArrayList("8-9am","9-10am","10-11am","11-12nn");
        ObservableList<String> time1hourDistancePM = FXCollections.observableArrayList("1-2pm","2-3pm","3-4pm","4-5pm","5-6pm","6-7pm","7-8pm","8-9pm");

        ObservableList<String> time2hourDistanceAM = FXCollections.observableArrayList("8-10am","9-11am","10-12nn");
        ObservableList<String> time2hourDistancePM = FXCollections.observableArrayList("1-3pm","2-4pm","3-5pm","4-6pm","5-7pm","6-8pm","7-9pm");
        
        ObservableList<String> dowFor1hours = FXCollections.observableArrayList("MW","TTH");
        ObservableList<String> dowFor2hours = FXCollections.observableArrayList("M","T","W","TH","F");
        
        if(secID.equals("80")){
            time1hourDistanceAM.clear();
            time1hourDistancePM.clear();
            ObservableList<String> time1hourDistancePMDef = FXCollections.observableArrayList("9-10am","10-11am","1-2pm","2-3pm","3-4pm","11-12nn","8-9am","4-5pm","5-6pm","6-7pm","7-8pm","8-9pm");
            time1hourDistanceAM.addAll(time1hourDistancePMDef);
            
            time2hourDistanceAM.clear();
            time2hourDistancePM.clear();
            ObservableList<String> time2hourDistanceAMDef = FXCollections.observableArrayList("9-11am","10-12nn","1-3pm","2-4pm","3-5pm","4-6pm","5-7pm","8-10am","6-8pm","7-9pm");
            time2hourDistanceAM.addAll(time2hourDistanceAMDef);
        }
        
        Random r = new  Random();
        int hourORhours = r.nextInt(3)+1;
        int AMorPM = r.nextInt(3)+1;
        List<String> useThisForTime1hour = new ArrayList<>();
        List<String> useThisForTime2hour = new ArrayList<>();
        
        List<String> forDOW = new ArrayList<>();
        List<String> forTime = new ArrayList<>();
        
        boolean ifSchedIsAvailable = false;
        
        for(String rs: roomShuf){//&& rm.getroomBuilding().equals("2")
            boolean roomIsRight = false;
            if(prog.getValue().equals("BSA")){
                for(roomModel rm : rooms){
                    if(rm.getroomBuilding().equals("1") && rs.equals(rm.getroomCODE()) && (rm.getroomType().equals(lecOrLab) || rm.getroomType().equals("Lecture"))){
                        roomIsRight = true;
                        break;
                    }
                }
            }else if(prog.getValue().equals("BSTLEd")){
                for(roomModel rm : rooms){
                    if(rm.getroomBuilding().equals("2") && rs.equals(rm.getroomCODE()) && rm.getroomType().equals(lecOrLab)){
                        roomIsRight = true;
                        break;
                    }
                }
            }
            else{
                for(roomModel rm : rooms){
                    if(rm.getroomBuilding().equals(bN)){
                        if(rs.equals(rm.getroomCODE()) && rm.getroomType().equals(lecOrLab) ){
                            roomIsRight = true;
                            break;
                        }
                    }
                }
            }
            
            if(id.equals("144")){
                if(rs.equals("CL1") || rs.equals("CL2")){
                    roomIsRight = true;
                }else{
                    roomIsRight = false;
                }
            }
            if(roomIsRight){
                int a = 0;
                while(a<2){
                    switch(a){
                        case 0:
                            if(AMorPM==1){//1hour distance
                                useThisForTime1hour.addAll(time1hourDistanceAM);
                                useThisForTime1hour.addAll(time1hourDistancePM);
                            }else{
                                useThisForTime1hour.addAll(time1hourDistancePM);
                                useThisForTime1hour.addAll(time1hourDistanceAM);
                            }
                            forDOW.clear();
                            forTime.clear();
                            forDOW.addAll(dowFor1hours);
                            forTime.addAll(useThisForTime1hour);
                            break;
                        case 1://1and30minutes distance
                            if(AMorPM==1){
                                useThisForTime2hour.addAll(time2hourDistanceAM);
                                useThisForTime2hour.addAll(time2hourDistancePM);
                            }else{
                                useThisForTime2hour.addAll(time2hourDistancePM);
                                useThisForTime2hour.addAll(time2hourDistanceAM);
                            }
                            forDOW.clear();
                            forTime.clear();
                            forDOW.addAll(dowFor2hours);
                            forTime.addAll(useThisForTime2hour);
                            break;
                    }

                    for(String dow : forDOW){
                        for(String time: forTime){
                            String fac="",secs="";
                            for(schedModel sm: allSchedule){
                                if(!sm.getstatus().equals("0")){
                                    fac=sm.getfacultyID();
                                    secs=sm.getsectionID();
                                    ifSchedIsAvailable = checkIfRoomTimeAndDaysIsAvailable(sm.getdays(), sm.gettime(), sm.getroom(), rs, time, dow, id,sm.getfacultyID(), sm.getsectionID(), secID);
                                    if(!ifSchedIsAvailable){
                                        break;
                                    }
                                }
                                firstSched=false;
                            }
                            if(firstSched){
                                firstSched=false;
                                return time+";"+dow+";"+rs;
                            }
                            if(ifSchedIsAvailable){
                                return  time+";"+dow+";"+rs;
                            }
                        }
                    }
                    a++;
                    //System.out.println("generateSchedFor2hoursLecture change TIME --------------------------");
                }
            }
        }
        String holdForTime="";
        String holdForDays="";
        String holdForRoom="";
        
        int schedFound=0;
        
        if(!holdForTime.contains("/")){
            holdForTime="";
            holdForDays="";
            holdForRoom="";

            schedFound=0;
            while(schedFound<3){
                boolean schedF = false;
                int a = 0;
                useThisForTime1hour.clear();
                if(AMorPM==1){
                    useThisForTime1hour.addAll(time1hourDistanceAM);
                    useThisForTime1hour.addAll(time1hourDistancePM);
                }else{
                    useThisForTime1hour.addAll(time1hourDistancePM);
                    useThisForTime1hour.addAll(time1hourDistanceAM);
                }
                forDOW.clear();
                forTime.clear();
                forDOW.addAll(dowFor2hours);
                forTime.addAll(useThisForTime1hour);


                for(String rs: roomShuf){
                    boolean roomIsRight = false;
                    if(prog.getValue().equals("BSIT")){
                        for(roomModel rm : rooms){
                            if(rs.equals(rm.getroomCODE()) && rm.getroomType().equals(lecOrLab)){
                                roomIsRight = true;
                                break;
                            }
                        }
                    }else if(prog.getValue().equals("BSA")){
                        for(roomModel rm : rooms){
                            if(rm.getroomBuilding().equals("1") && rs.equals(rm.getroomCODE()) && (rm.getroomType().equals(lecOrLab) || rm.getroomType().equals("Lecture"))){
                                roomIsRight = true;
                                break;
                            }
                        }
                    }else if(prog.getValue().equals("BSTLEd")){
                        for(roomModel rm : rooms){
                            if(rs.equals(rm.getroomCODE()) && rm.getroomType().equals(lecOrLab) && rm.getroomBuilding().equals("2")){
                                roomIsRight = true;
                                break;
                            }
                        }
                    }
                    else if(prog.getValue().equals("BSBA")){
                        for(roomModel rm : rooms){

                            if(rm.getroomBuilding().equals("1") && rm.getroomType().equals("Lecture") && rs.equals(rm.getroomCODE())){
                                roomIsRight = true;
                                break;
                            }

                        }
                    }
                    if(roomIsRight){
                        for(String dow : forDOW){
                            for(String time: forTime){
                                String fac="",secs="";
                                for(schedModel sm: allSchedule){
                                    if(!sm.getstatus().equals("0")){
                                        fac=sm.getfacultyID();
                                        secs=sm.getsectionID();
                                        schedF = checkIfRoomTimeAndDaysIsAvailable(sm.getdays(), sm.gettime(), sm.getroom(), rs, time, dow,id,sm.getfacultyID(), sm.getsectionID(), secID);
                                        if(!schedF){
                                            break;
                                        }
                                        firstSched=false;
                                    }
                                }
                                if(firstSched){
                                    firstSched=false;
                                    holdForTime+=time;
                                    holdForDays+=dow;
                                    holdForRoom+=rs;
                                }
                                if(!holdForDays.equals("") && schedF){
                                    schedF = checkIfRoomTimeAndDaysIsAvailable(holdForDays, holdForTime, holdForRoom, rs, time, dow, id,fac, secs, secID);

                                }
                                if(schedF){
                                    if(holdForTime.equals("")){
                                        holdForTime+=time;
                                        holdForDays+=dow;
                                        holdForRoom+=rs;
                                    }else{
                                        holdForTime+="/"+time;
                                        holdForDays+="/"+dow;
                                        holdForRoom+="/"+rs;
                                    }
                                }
                                if(schedF){break;}
                            }
                            if(schedF){break;}
                        }
                        if(schedF){break;}
                    }
                    if(schedF){break;}
                }
                System.out.println(holdForTime+"--"+holdForDays+"--"+holdForRoom);
                System.out.println("OHHHHH-----"+ifSchedIsAvailable);
                a++;
                schedFound++;
                schedF=false;
            }
        }
        ifSchedIsAvailable=true;
        
        if(ifSchedIsAvailable){
            return holdForTime+";"+holdForDays+";"+holdForRoom;
        }
//        System.out.println(holdForTime+";"+holdForDays+";"+holdForRoom);
        System.out.println("generateSchedFor2hoursLecture");
        return "none;none;none";
    }
    public String generateSchedFor1hourLecture(String lecOrLab, String id, String secID, String bN){
        shuffle(id);
        
        ObservableList<String> time1hourDistanceAM = FXCollections.observableArrayList("8-9am","9-10am","10-11am","11-12nn");
        ObservableList<String> time1hourDistancePM = FXCollections.observableArrayList("1-2pm","2-3pm","3-4pm","4-5pm","5-6pm","6-7pm","7-8pm","8-9pm");
        ObservableList<String> dowFor2hours = FXCollections.observableArrayList("M","T","W","TH","F");
        
        if(secID.equals("80")){
            time1hourDistanceAM.clear();
            time1hourDistancePM.clear();
            ObservableList<String> time1hourDistancePMDef = FXCollections.observableArrayList("9-10am","10-11am","1-2pm","2-3pm","3-4pm","11-12nn","8-9am","4-5pm","5-6pm","6-7pm","7-8pm","8-9pm");
            time1hourDistanceAM.addAll(time1hourDistancePMDef);
        }
        
        Random r = new  Random();
        int hourORhours = r.nextInt(3)+1;
        int AMorPM = r.nextInt(3)+1;
        List<String> useThisForTime1hour = new ArrayList<>();
        List<String> useThisForTime2hour = new ArrayList<>();
        
        List<String> forDOW = new ArrayList<>();
        List<String> forTime = new ArrayList<>();
        
        boolean ifSchedIsAvailable = false;
        boolean roomIsRight = false;
        for(String rs: roomShuf){
            
            if(prog.getValue().equals("BSA")){
                for(roomModel rm : rooms){
                    if(rm.getroomBuilding().equals("1") && rs.equals(rm.getroomCODE()) && (rm.getroomType().equals(lecOrLab) || rm.getroomType().equals("Lecture"))){
                        roomIsRight = true;
                        break;
                    }
                }
            }else if(prog.getValue().equals("BSTLEd")){
                    for(roomModel rm : rooms){
                        if(rm.getroomBuilding().equals("2") && rs.equals(rm.getroomCODE()) && rm.getroomType().equals(lecOrLab)){
                            roomIsRight = true;
                            break;
                        }
                    }
            }else{
                for(roomModel rm : rooms){
                    if(rm.getroomBuilding().equals(bN)){
                        if(rs.equals(rm.getroomCODE()) && rm.getroomType().equals(lecOrLab) ){
                            roomIsRight = true;
                            break;
                        }
                    }
                }
            }
            
            if(roomIsRight){
                
                useThisForTime1hour.clear();
                if(AMorPM==1){//1hour distance
                    useThisForTime1hour.addAll(time1hourDistanceAM);
                    useThisForTime1hour.addAll(time1hourDistancePM);
                }else{
                    useThisForTime1hour.addAll(time1hourDistancePM);
                    useThisForTime1hour.addAll(time1hourDistanceAM);
                }
                forDOW.clear();
                forTime.clear();
                forDOW.addAll(dowFor2hours);
                forTime.addAll(useThisForTime1hour);

                for(String dow : forDOW){
                    for(String time: forTime){
                        String fac="",secs="";
                        for(schedModel sm: allSchedule){
                            if(!sm.getstatus().equals("0")){
                                fac=sm.getfacultyID();
                                secs=sm.getsectionID();
                                ifSchedIsAvailable = checkIfRoomTimeAndDaysIsAvailable(sm.getdays(), sm.gettime(), sm.getroom(), rs, time, dow, id,sm.getfacultyID(), sm.getsectionID(), secID);
                                if(!ifSchedIsAvailable){
                                    break;
                                }
                            }
                            firstSched=false;
                        }
                        if(firstSched){
                            firstSched=false;
                            return time+";"+dow+";"+rs;
                        }
                        if(ifSchedIsAvailable){
                            return  time+";"+dow+";"+rs;
                        }
                    }
                }
                    //System.out.println("generateSchedFor2hoursLecture change TIME --------------------------");
                
            }
        }
        System.out.println("generateSchedFor2hoursLecture");
        return "none;none;none";
    }
    List<String> roomShuf = new ArrayList<>();
    
    public boolean checkIfRoomTimeAndDaysIsAvailable(String daysSched, String timeSched, String roomSched, String checkThisRoom, String checkThisTime, String checkThisDays, String facIDFromDocx, String facIDFromSched, String secIDFromSched, String secIDFromDocx){
        boolean checkIfSchedIsAvail = false;
        String amORpm = "";
        String forNN = "";
        if(checkThisTime.contains("am") || checkThisTime.contains("nn")){
            amORpm = "am";
            forNN = "nn";
        }else{
            amORpm = "pm";
            forNN = "pm";
        }
        
                    
        //System.out.println(facIDFromDocx+"======================="+facIDFromSched);
        try{
        if(daysSched.contains("/")){
            //System.out.println("With /");
            String[] separateDays = daysSched.split("/");
            String[] separateTime = timeSched.split("/"); //9-10AM
            String[] separateRoom = roomSched.split("/");
            
            for(int a=0; a<separateTime.length; a++){
                boolean sameMorningOrAfternoon = false;
                if(separateTime[a].contains(amORpm) || separateTime[a].contains(forNN) ){
                    sameMorningOrAfternoon = true;
                }
                boolean check30MinutesExist = false;
                //System.out.println(separateTime[a]+"------"+amORpm+";"+forNN+"------------"+sameMorningOrAfternoon);
                if(sameMorningOrAfternoon){// 10:30-12NN || 10-11AM
                    String holderForTime = "";
                    String limit = "";
                    String[] splitTimeAgain = separateTime[a].split("-");// [10:30, 12NN] - [10,9:30AM]
                    if(splitTimeAgain[0].contains(":")){//                      10:30  -        10
                        String[] op = splitTimeAgain[0].split(":"); //       [10,30]   
                        holderForTime = op[0];//[10]
                    }else{
                        holderForTime = splitTimeAgain[0]; // 10 
                    }                                 
                    limit=splitTimeAgain[1].charAt(0)+"" +  (splitTimeAgain[1].charAt(1) != 'a' && 
                                                                  splitTimeAgain[1].charAt(1) != 'n' && 
                                                                  splitTimeAgain[1].charAt(1) != ':' &&
                                                                  splitTimeAgain[1].charAt(1) != 'p'?   splitTimeAgain[1].charAt(1):"");
                    //System.out.println("-----------------------------------------------------------");
                    for(int hft = Integer.parseInt(holderForTime); hft<Integer.parseInt(limit); hft++){
                        holderForTime+=(","+(hft+1));
                    }
                    
                    String holderForCheckThisTime = "";
                    String itsLimit="";
                    String[] splitCheckThisTime = checkThisTime.split("-");
                    if(splitCheckThisTime[0].contains(":")){
                        String[] os = splitCheckThisTime[0].split(":");
                        holderForCheckThisTime=os[0];
                    }else{
                        holderForCheckThisTime=splitCheckThisTime[0];
                    }
                    itsLimit = splitCheckThisTime[1].charAt(0)+"" +  (splitCheckThisTime[1].charAt(1) != 'a' && //2-3pm
                                                                           splitCheckThisTime[1].charAt(1) != 'n' && 
                                                                           splitCheckThisTime[1].charAt(1) != ':' &&
                                                                           splitCheckThisTime[1].charAt(1) != 'p'?   splitCheckThisTime[1].charAt(1):"");
//                    System.out.println("SPLEI::::::"+splitCheckThisTime[1]);
//                    System.out.println("zzzzzz:::"+holderForCheckThisTime);
//                    System.out.println("KASLFFHBEJBFLSNFI ==== +++ :"+itsLimit);
                    for(int hfctt=Integer.parseInt(holderForCheckThisTime); hfctt<Integer.parseInt(itsLimit); hfctt++){
                        holderForCheckThisTime+= (","+(hfctt+1));
                    }
                    // condition for time 
                    
                    boolean ifTimeAlreadyExist = false;
                    int count = 0;
                    
                    //System.out.println("---------------"+holderForTime);
                    //System.out.println("---------------"+holderForCheckThisTime);
                    for(int b=0; b<holderForCheckThisTime.length(); b++){
                        if(holderForCheckThisTime.charAt(b)!=','){
                            if(holderForTime.contains(holderForCheckThisTime.charAt(b)+"")){
                                count++;
                            }
                        }
                    }
                    //System.out.println("------"+count);
                    //|| splitTimeAgain[1].contains(":")    this deleted condition below ---> && splitTimeAgain[1].contains(":")
                    if(count>1){
                        ifTimeAlreadyExist = true;
                    }
                    
                    //                      2-3:30                              3:30-5     false
                    //                      2-3:30                              3-5        true
//                    if(count<2 && splitTimeAgain[1].contains(":") && !splitCheckThisTime[0].contains(":")){
//                        ifTimeAlreadyExist = true;
//                    }
                    //
                    if(count==1 && splitCheckThisTime[1].contains(":")){
                        ifTimeAlreadyExist = true;
                    }
                    if(count==1 && (splitCheckThisTime[0].contains(":") || splitCheckThisTime[1].contains(":") || splitTimeAgain[1].contains(":") || splitTimeAgain[0].contains(":"))){
                        ifTimeAlreadyExist = true;
                    }
                    if(count==1 && (splitTimeAgain[1].contains(":") && splitCheckThisTime[0].contains(":"))){
                        ifTimeAlreadyExist = false;
                    }
//                    if(count<2 && !splitCheckThisTime[0].contains(":") && !splitCheckThisTime[1].contains(":") && !splitTimeAgain[1].contains(":") && !splitTimeAgain[0].contains(":")){
//                        ifTimeAlreadyExist = false;
//                    }
                    //System.out.println(holderForCheckThisTime +"-------"+holderForTime+";;;;;"+ifTimeAlreadyExist);
                    boolean ifDaysAreTheSame = false;
//                    if(separateDays[a].contains(checkThisDays) || checkThisDays.contains(separateDays[a])){
//                        ifDaysAreTheSame = true;
//                    }
//                                      THF
                    ObservableList<String> week = FXCollections.observableArrayList("M","H","W","T","F", "S");
                    for(String wee : week){
                        if(separateDays[a].contains(wee) && checkThisDays.contains(wee)){
                            ifDaysAreTheSame = true;
                            break;
                        }
                    }
                    if((separateDays[a].equals("TH") && checkThisDays.equals("T")) || (separateDays[a].equals("T") && checkThisDays.equals("TH"))){
                        if( separateDays[a].equals(checkThisDays) ){
                            ifDaysAreTheSame = true;
                        }else{
                           ifDaysAreTheSame = false; 
                        }
                    }
                    if(separateDays[a].equals(checkThisDays)){
                        ifDaysAreTheSame = true;
                    }
                    
                    //System.out.println(separateDays[a]+"---------"+checkThisDays +";;;;;"+ifDaysAreTheSame);
                    boolean ifRoomAreTheSame = false;
                    if(separateRoom[a].equals(checkThisRoom) || checkThisRoom.equals(separateRoom[a])){
                        ifRoomAreTheSame = true;
                    }
                    //System.out.println(separateRoom[a]+"----------"+checkThisRoom +";;;;;"+ifRoomAreTheSame);
                    boolean ifFacultyAreTheSame = false;
                    //System.out.println(facIDFromDocx);
                    if(facIDFromDocx.contains("/")){
                        String[] sepfacIDFromDocx = facIDFromDocx.split("/");
                        if(sepfacIDFromDocx[a].equals(facIDFromSched)){
                            ifFacultyAreTheSame = true;
                        }
                    }else{
                        if(facIDFromDocx.equals(facIDFromSched)){
                            ifFacultyAreTheSame = true;
                        }
                    }
                    boolean ifSectionAreTheSame = false;
                    if(secIDFromDocx.equals(secIDFromSched)){
                        ifSectionAreTheSame = true;
                    }
                    
                    if(ifTimeAlreadyExist && ifDaysAreTheSame && ifRoomAreTheSame){
                        checkIfSchedIsAvail=false;
                        return checkIfSchedIsAvail;
                    }else{
                        checkIfSchedIsAvail=true;
                    }
                    //System.out.println(ifFacultyAreTheSame);
                    if(ifTimeAlreadyExist && ifFacultyAreTheSame && ifDaysAreTheSame){
                        checkIfSchedIsAvail=false;
                        return checkIfSchedIsAvail;
                    }
                    if(ifSectionAreTheSame && ifTimeAlreadyExist && ifDaysAreTheSame){
                        checkIfSchedIsAvail=false;
                        return checkIfSchedIsAvail;
                    }
                    
                    //System.out.println("=========="+ifTimeAlreadyExist);
                    if(!checkIfSchedIsAvail){
                        return checkIfSchedIsAvail;
                    }
                }else{
                    checkIfSchedIsAvail=true;
                }
                
            }
            
            
        }else{
            //System.out.println("Without /");
            boolean sameMorningOrAfternoon = false;
            if(timeSched.contains(amORpm) || timeSched.contains(forNN) ){
                sameMorningOrAfternoon = true;
            }
            //System.out.println(timeSched+" if contains: "+checkThisTime+ " then "+sameMorningOrAfternoon);
            //String daysSched, String timeSched, String roomSched, String checkThisRoom, String checkThisTime, String checkThisDays, String am
            if(sameMorningOrAfternoon){
                String holderForTime = "";
                String limit = "";
                String[] splitTimeAgain = timeSched.split("-");// [10:30, 12NN] - [10,9:30AM]
                if(splitTimeAgain[0].contains(":")){//                      10:30  -        10
                    String[] op = splitTimeAgain[0].split(":"); //       [10,30]   
                    holderForTime = op[0];//[10]
                }else{
                    holderForTime = splitTimeAgain[0]; // 10 
                }                                 
                limit=splitTimeAgain[1].charAt(0)+"" +  (splitTimeAgain[1].charAt(1) != 'a' && 
                                                              splitTimeAgain[1].charAt(1) != 'n' && 
                                                              splitTimeAgain[1].charAt(1) != ':' &&
                                                              splitTimeAgain[1].charAt(1) != 'p'?   splitTimeAgain[1].charAt(1):"");
                //System.out.println("-----------------------------------------------------------");
                for(int hft = Integer.parseInt(holderForTime); hft<Integer.parseInt(limit); hft++){
                    holderForTime+=(","+(hft+1));
                }

                String holderForCheckThisTime = "";
                String itsLimit="";
                String[] splitCheckThisTime = checkThisTime.split("-");
                if(splitCheckThisTime[0].contains(":")){
                    String[] os = splitCheckThisTime[0].split(":");
                    holderForCheckThisTime=os[0];
                }else{
                    holderForCheckThisTime=splitCheckThisTime[0];
                }
                itsLimit = splitCheckThisTime[1].charAt(0)+"" +  (splitCheckThisTime[1].charAt(1) != 'a' && 
                                                                       splitCheckThisTime[1].charAt(1) != 'n' && 
                                                                       splitCheckThisTime[1].charAt(1) != ':' && 
                                                                       splitCheckThisTime[1].charAt(1) != 'p'?   splitCheckThisTime[1].charAt(1):"");
                for(int hfctt=Integer.parseInt(holderForCheckThisTime); hfctt<Integer.parseInt(itsLimit); hfctt++){
                    holderForCheckThisTime+= (","+(hfctt+1));
                }
                // condition for time 

                boolean ifTimeAlreadyExist = false;
                int count = 0;
                for(int b=0; b<holderForCheckThisTime.length(); b++){
                    if(holderForCheckThisTime.charAt(b)!=','){
                        if(holderForTime.contains(holderForCheckThisTime.charAt(b)+"")){
                            count++;
                        }
                    }
                }
                //|| splitTimeAgain[1].contains(":")        this deleted condition below ---> && splitTimeAgain[1].contains(":")
                if(count>1){//
                    ifTimeAlreadyExist = true;
                }
//                if(count<2 && splitTimeAgain[1].contains(":") && !splitCheckThisTime[0].contains(":")){
//                        ifTimeAlreadyExist = true;
//                }
                if(count==1 && splitCheckThisTime[1].contains(":")){
                    ifTimeAlreadyExist = true;
                }
                if(count==1 && (splitCheckThisTime[0].contains(":") || splitCheckThisTime[1].contains(":") || splitTimeAgain[1].contains(":") || splitTimeAgain[0].contains(":"))){
                    ifTimeAlreadyExist = true;
                }
                if(count==1 && (splitTimeAgain[1].contains(":") && splitCheckThisTime[0].contains(":"))){
                    ifTimeAlreadyExist = false;
                }
                
                
//                if(count<2 && !splitCheckThisTime[0].contains(":") && !splitCheckThisTime[1].contains(":") && !splitTimeAgain[1].contains(":") && !splitTimeAgain[0].contains(":")){
//                    ifTimeAlreadyExist = false;
//                }
                //System.out.println(holderForCheckThisTime +"-------"+holderForTime+";;;;;"+ifTimeAlreadyExist);
                boolean ifDaysAreTheSame = false;
//                if(daysSched.contains(checkThisDays) || checkThisDays.contains(daysSched)){
//                    ifDaysAreTheSame = true;
//                }
//                              MW/TTH
                ObservableList<String> week = FXCollections.observableArrayList("M","H","W","T","F", "S");
                for(String wee : week){
                    if(daysSched.contains(wee) && checkThisDays.contains(wee)){
                        ifDaysAreTheSame = true;
                        break;
                    }
                }
                
                if((daysSched.equals("TH") && checkThisDays.equals("T")) || (daysSched.equals("T") && checkThisDays.equals("TH"))){
                    if( daysSched.equals(checkThisDays) ){
                        ifDaysAreTheSame = true;
                    }else{
                        ifDaysAreTheSame = false; 
                    }
                }
                if(daysSched.equals(checkThisDays) || checkThisDays.equals(daysSched)){
                    ifDaysAreTheSame = true;
                }
                //System.out.println(daysSched+"---------"+checkThisDays +";;;;;"+ifDaysAreTheSame);
                boolean ifRoomAreTheSame = false;
                if(roomSched.equals(checkThisRoom)){
                    ifRoomAreTheSame = true;
                }
                //System.out.println(roomSched+"----------"+checkThisRoom +";;;;;"+ifRoomAreTheSame);
                boolean ifFacultyAreTheSame = false;
                if(facIDFromDocx.contains(facIDFromSched)){
                    ifFacultyAreTheSame = true;
                }
                boolean ifSectionAreTheSame = false;
                if(secIDFromDocx.equals(secIDFromSched)){
                    ifSectionAreTheSame = true;
                }
                
                if(ifTimeAlreadyExist && ifDaysAreTheSame && ifRoomAreTheSame){
                    checkIfSchedIsAvail=false;
                    return checkIfSchedIsAvail;
                }else{
                    checkIfSchedIsAvail=true;
                }
                //System.out.println(ifFacultyAreTheSame);
                if(ifTimeAlreadyExist && ifFacultyAreTheSame && ifDaysAreTheSame){
                    checkIfSchedIsAvail=false;
                    return checkIfSchedIsAvail;
                }
                if(ifSectionAreTheSame && ifTimeAlreadyExist && ifDaysAreTheSame){
                    checkIfSchedIsAvail=false;
                    return checkIfSchedIsAvail;
                }
                //System.out.println("==============111111"+checkIfSchedIsAvail);
                if(!checkIfSchedIsAvail){
                    return checkIfSchedIsAvail;
                }
            }else{
                checkIfSchedIsAvail=true;
            }
            if(!checkIfSchedIsAvail){
                return checkIfSchedIsAvail;
            }
            //System.out.println("JJAJAJ "+checkIfSchedIsAvail);
            
        }
        }catch(Exception e){
                System.out.println(e);
            }
        //System.out.println("last if: "+checkIfSchedIsAvail);
        return checkIfSchedIsAvail;
    }
    public void insertSub()throws Exception{
        Statement statement = dc.con.createStatement();
        getSubject();
        //System.out.println("INSERTSUB");
        for(getSchedFromDocx ad:listOfSched){
            boolean True = true;
            String fos = "Minor";
            for(listOfSubjects ls: dataOfSubjects){
                if(ad.getcourseNo().contains(ls.getCode()) && ad.getcourseDes().contains(ls.getName()) || ls.getCode().contains(ad.getcourseNo()) && ls.getName().contains(ad.getcourseDes())){
                    True = false;
                    //System.out.println("If subject exist: "+True);
                    break;
                } 
            }
            if(!ad.getlab().equals("0")){
                fos = "Major";
            }
            if(True){
                int resultSet = statement.executeUpdate("INSERT INTO subject (subCode, subName, subStatus, fieldOfStudy) "
                                                                       + "VALUES "+ "('"+ad.getcourseNo()+"', '"+ad.getcourseDes()+"' , '1' , '"+fos+ "')"  );
            }
            getSubject();
        }
    }
    public void insertSection()throws Exception{
        getSection();
        Statement statement = dc.con.createStatement();
        for(getSchedFromDocx ad:listOfSched){
            boolean True = true;
            for(section rm: secList){
                if( rm.getName().equals(ad.getsec()) ){
                    True = false;
                }
            }
            if(True){
                String agh = "";//prog.getValue())+1
                switch(prog.getValue().toString()){
                    case "BSIT":
                        agh = "1";
                        break;
                    case "BSBA":
                        agh = "2";
                        break;
                    case "BSA":
                        agh = "3";
                        break;
                    case "BSTLEd":
                        agh = "4";
                        break;
                }
                //System.out.println("SDA"+agh);
                int resultSet = statement.executeUpdate("INSERT INTO section (progID, secName, secStatus) "
                                                                       + " VALUES "+ "('"+agh+"','"+ad.getsec()+"','1')" );
            }
            getSection();
        }
    }
    
    
     
    public void getFaculty()throws SQLException{
        faculty.clear();
        
        Statement statement = dc.con.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT  facultyID, firstname, middlename,lastname,suffix,sex,expertise, contact, gmail,address,status, adminDesignation FROM faculty");
        while(resultSet.next()){
            String aa = resultSet.getString("facultyID");
            String ab = resultSet.getString("firstname");
            String ac = resultSet.getString("middlename");
            String ad = resultSet.getString("lastname");
            String ae = resultSet.getString("suffix");
            String af = resultSet.getString("sex");
            String ag = resultSet.getString("expertise");
            String ah = resultSet.getString("contact");
            String ai = resultSet.getString("gmail");
            String aj = resultSet.getString("address");
            String ak = resultSet.getString("status");
            String m4 = resultSet.getString("adminDesignation");
            faculty.add(new instructorsList(aa,ab,ac,ad,ae,af,ag,ah,ai,aj,ak,m4));
        }
    }
     
     int countFac=0;
     public void getFac()throws SQLException{
        faculty.clear();
        Statement statement = dc.con.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM faculty;");
        while(resultSet.next()){
            countFac++;
        }
    }
    
    
    public void getPrograms()throws SQLException{
        progList.clear();
        Statement statement = dc.con.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT progID, progCode, progDescription FROM program");
        while(resultSet.next()){
            String sI = resultSet.getString("progID");
            String sI2 = resultSet.getString("progCode");
            String sI3 = resultSet.getString("progDescription");
            progList.add(new progModel(sI,sI2,sI3) );
            prg.add(sI2);
            //System.out.println("Program"+sI2);
        }
    }
    public void getSection()throws SQLException{
        secList.clear();
        Statement statement = dc.con.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT secID, secName FROM section");
        while(resultSet.next()){
            String sI = resultSet.getString("secID");
            String sII = resultSet.getString("secName");
            secList.add(new section(sI, sII));
            //scheduleholder.add(sII);
          scheduleholder.add(sII);
            //System.out.println("Section"+sII);
        }
    }
    
    public void getSelectSection()throws SQLException{
        secList.clear();
        Statement statement = dc.con.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT secName FROM section");
        while(resultSet.next()){
            String sII = resultSet.getString("secName");
            //secList.add(new section(sII));
            scheduleholder.add(sII);
           selectSection.setItems(scheduleholder);
            //System.out.println("Section"+sII);
        }
    }
    public void getRooms()throws SQLException{
        rooms.clear();
        Statement statement = dc.con.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT roomID, roomCode, roomType, buildingNo FROM room");
        while(resultSet.next()){
            String sI = resultSet.getString("roomID");
            String sII = resultSet.getString("roomCode");
            String sIII = resultSet.getString("roomType");
            String sIV = resultSet.getString("buildingNo");
            rooms.add(new roomModel(sI, sII,sIII,sIV));
            //System.out.println("Room"+sII);
        }
    }
    public void getSubject()throws Exception{
        dataOfSubjects.clear();
        Statement statement = dc.con.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT subID, subCode, subName, fieldOfStudy FROM subject");
        while(resultSet.next()){
            String sI = resultSet.getString("subID");
            String sII = resultSet.getString("subCode");
            String ha = resultSet.getString("subName");
            String haa = resultSet.getString("fieldOfStudy");
            dataOfSubjects.add(new listOfSubjects(sI, sII,ha,haa));
            //System.out.println("SUbject"+sII);
        }
    }
    
    
    public void setItemsForProgAndSem(){
        prog.setItems(prg);
        sem.setItems(smff);
        ObservableList<String> gh = FXCollections.observableArrayList("2022-2023","2023-2024","2024-2025","2025-2026");
        sy.setItems(gh);
    }
    public void chooseAFile(ActionEvent e)throws Exception{
        displaySelectedFile.setText("");
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select a File");
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Word Documents (*.docx)", "*.docx");
        fileChooser.getExtensionFilters().add(extFilter);
        Stage stage = (Stage) selectAFile.getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(stage);
        displaySelectedFile.setText(selectedFile+"");
        
    }
    
    public void shuffle(String hi){//, String lecLab
        roomShuf.clear();
        if(hi.equals("144")){
            roomShuf.add("CL1");
            roomShuf.add("CL2");
        }else{
            roomShuf.add("CL2");
            roomShuf.add("CL1");
        }
        List<String> roomShufww = new ArrayList<>();
        for(roomModel rm : rooms){
            if(prog.getValue().equals("BSIT")){
                if(rm.getroomCODE().contains("CANT") || rm.getroomCODE().contains("TENT") || rm.getroomCODE().contains("BOS") || rm.getroomCODE().contains("HALLWAY") ){
                    roomShufww.add(rm.getroomCODE());
                }else{
                    roomShuf.add(rm.getroomCODE());
                }
            }else{
                if(!rm.getroomCODE().equals("CL1") || !rm.getroomCODE().equals("CL2")){
                    roomShuf.add(rm.getroomCODE());
                }
            }
        }
        //Collections.shuffle(roomShuf);
        roomShuf.addAll(roomShufww);
//        if(lecLab.equals("Lec")){
//            
//        }
        
    }
    public void getSchedDataFromDatabase(){
        //scheData.clear();
        allSchedule.clear();
        try {
            //Connection con = DriverManager.getConnection("jdbc:mysql://localhost/schedulingsystemdatabase", "root", "");
            Statement statement = dc.con.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT schedule.schedRoom, schedule.schedID, schedule.facultyID, subject.subCode, subject.subName, schedule.lec, schedule.lab, "
                    + " (schedule.lec + schedule.lab) AS Total, program.progCode,section.secName , schedule.schedDays, "
                    + "schedule.schedTime, schedule.noOfStudents, schedule.SchedStatus, schedule.sectionID "
                    + "FROM schedule, subject, section, program "
                    + "WHERE schedule.subjectID = subject.subID "
                    + "AND   schedule.sectionID = section.secID "
                    + "AND   schedule.SchedStatus = 1 "
                    //+ "AND   schedRoom like '%"++"'"
                    + "GROUP BY schedule.schedID");
            
            while (resultSet.next()) {
                String schedIDDS = resultSet.getString("schedule.schedID");
                String aa = resultSet.getString("subject.subCode");
                String b = resultSet.getString("subject.subName");
                String cc = resultSet.getString("schedule.lec");
                String d = resultSet.getString("schedule.lab");
                String e = resultSet.getString("Total");
                String ff = resultSet.getString("program.progCode");
                String gg = resultSet.getString("section.secName");
                String h = resultSet.getString("schedule.schedDays");
                String i = resultSet.getString("schedule.schedTime");
                String j = resultSet.getString("schedule.schedRoom");
                String j4 = resultSet.getString("schedule.facultyID");
                String j5 = resultSet.getString("schedule.noOfStudents");
                String j55 = resultSet.getString("schedule.SchedStatus");
                String j555 = resultSet.getString("schedule.sectionID");

                allSchedule.add(new schedModel(aa, b, cc, d, e, ff, gg, h, i, j, j4,j5,j55,j555));
                //tableForSchedule.setItems(scheData);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        
        
    }
    //"C:/HEHE/BAJUNS.docx"
    // write sched to excel
    public void writeToExcelAllSched()throws Exception{
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Emp info");
        
        int num = 1;
        //faculty.add(new instructorsList(aa,ab,ac,ad,ae,af,ag,ah,ai,aj,ak));
        int count = 0;
        int  three=3,four=4,five=5,six = 6,seven=7;
        for(instructorsList fac:faculty){
            
            int     b4=three,b6=five,c4=three,i4=three,c5=four,e6=five,f5=four,i6=five,j4=three,q4=three,j5=four,k6=five,l5=four,m6=five,
                    n5=four,o6=five,p5=four,q6=five,b7=six,c8=seven,d7=six,g8=seven,h7=six,j7=six,k7=six,k8=seven,l7=six,l8=seven,m7=six,m8=seven,
                    n7=six,n8=seven,o7=six,o8=seven,p7=six,p8=seven,q7=six,q8=seven;
            
            //Connection con = DriverManager.getConnection("jdbc:mysql://localhost/schedulingsystemdatabase", "root", "");
            Statement statement = dc.con.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT schedule.schedRoom, schedule.schedID, schedule.facultyID, subject.subCode, subject.subName, schedule.lec, schedule.lab,\n" +
"                     (schedule.lec + schedule.lab) AS Total, program.progCode,section.secName , schedule.schedDays,\n" +
"                    schedule.schedTime, schedule.noOfStudents\n" +
"                    FROM schedule, subject, section, program, faculty\n" +
"                    WHERE schedule.subjectID = subject.subID\n" +
"                    AND   schedule.sectionID = section.secID\n" +
"                    AND   schedule.SchedStatus = 1\n" +
"                    AND   schedule.facultyID = '" + fac.getID() +"'"+
"                    GROUP BY schedule.schedID;");
            int cout=0;
            int addSubCode = c8+1;
            int addSubName = g8+1;
            int forLecLabTotal = g8+1;
            double totalEus = 0;
            double totalTotalTeachingLoad = 0;
            boolean ifNasulodSaresultSet = false;
            while(resultSet.next()){
                ifNasulodSaresultSet = true;
                String schedIDDS = resultSet.getString("subject.subCode");
                String a1 = resultSet.getString("subject.subName");
                String a2 = resultSet.getString("schedule.lec");
                String a3 ="";
                if(resultSet.getString("schedule.lab").contains("1")){
                    a3="2.25";
                }else if(resultSet.getString("schedule.lab").contains("2")){
                    a3="4.5";
                }else{
                    a3="0";
                }
                String a4 = Double.parseDouble(a2)+Double.parseDouble(a3)+"";
                String a5 = resultSet.getString("program.progCode");
                String a6 = resultSet.getString("section.secName");
                String a7 = resultSet.getString("schedule.schedDays");
                String a8 = resultSet.getString("schedule.schedTime");
                String a9 = resultSet.getString("schedule.schedRoom");
                String a10 = resultSet.getString("schedule.noOfStudents");
                String a11 = Double.parseDouble(a10)*Double.parseDouble(a4)+"";
                totalTotalTeachingLoad+=Double.parseDouble(a4);
                totalEus += Double.parseDouble(a11);
                if(cout<1){
                    sheet.addMergedRegion(new CellRangeAddress(b4, b6, 1, 1)); // B4 to B6
                    sheet.addMergedRegion(new CellRangeAddress(c4, i4, 2, 8)); // C4 to I4
                    //sheet.addMergedRegion(new CellRangeAddress(c5, e6, 2, 4)); // C5 to E6
                    //sheet.addMergedRegion(new CellRangeAddress(f5, i6, 5, 8)); // F5 to I6
                    sheet.addMergedRegion(new CellRangeAddress(j4, q4, 9, 16)); // J4 to Q4
                    sheet.addMergedRegion(new CellRangeAddress(j5, k6, 9, 10)); // J5 to K6/
                    sheet.addMergedRegion(new CellRangeAddress(l5, m6, 11, 12)); // L5 to M6/
                    sheet.addMergedRegion(new CellRangeAddress(n5, o6, 13, 14)); // N5 to O6/
                    //sheet.addMergedRegion(new CellRangeAddress(p5, q6, 15, 16)); // P5 to Q6/
                    sheet.addMergedRegion(new CellRangeAddress(b7, c8, 1, 2)); // B7 to C8/ c8=7
                    sheet.addMergedRegion(new CellRangeAddress(d7, g8, 3, 6)); // D7 to G8/
                    sheet.addMergedRegion(new CellRangeAddress(h7, j7, 7, 9)); // H7 to J7/
                    sheet.addMergedRegion(new CellRangeAddress(k7, k8, 10, 10)); // K7 to K8/
                    sheet.addMergedRegion(new CellRangeAddress(l7, l8, 11, 11)); // L7 to L8
                    sheet.addMergedRegion(new CellRangeAddress(m7, m8, 12, 12)); // M7 to M8
                    sheet.addMergedRegion(new CellRangeAddress(n7, n8, 13, 13)); // N7 to N8
                    sheet.addMergedRegion(new CellRangeAddress(o7, o8, 14, 14)); // O7 to O8
                    sheet.addMergedRegion(new CellRangeAddress(p7, p8, 15, 15)); // P7 to P8
                    sheet.addMergedRegion(new CellRangeAddress(q7, q8, 16, 16)); // Q7 to Q8
                    applyBoldBorderToMergedRegions(sheet,
                        new CellRangeAddress(b4, b6, 1, 1),
                        new CellRangeAddress(c4, i4, 2, 8),
                        new CellRangeAddress(c5, e6, 2, 4),
                        new CellRangeAddress(f5, i6, 5, 8),
                        new CellRangeAddress(j4, q4, 9, 16),
                        new CellRangeAddress(j5, k6, 9, 10),
                        new CellRangeAddress(l5, m6, 11, 12),
                        new CellRangeAddress(n5, o6, 13, 14),
                        new CellRangeAddress(p5, q6, 15, 16),
                        new CellRangeAddress(b7, c8, 1, 2),
                        new CellRangeAddress(d7, g8, 3, 6),
                        new CellRangeAddress(h7, j7, 7, 9),
                        new CellRangeAddress(k7, k8, 10, 10),
                        new CellRangeAddress(l7, l8, 11, 11),
                        new CellRangeAddress(m7, m8, 12, 12),
                        new CellRangeAddress(n7, n8, 13, 13),
                        new CellRangeAddress(o7, o8, 14, 14),
                        new CellRangeAddress(p7, p8, 15, 15),
                        new CellRangeAddress(q7, q8, 16, 16)
                    );
                    setCellValue(sheet, b4, 1, num+"");
                    setCellValue(sheet, c4, 2, "Name: "+fac.getfname()+", "+fac.getlname()+" "+fac.getmname()+" "+fac.getSuffix());
                   // setCellValue(sheet, c5, 2, "No. of Preparations: ");
                    //setCellValue(sheet, f5, 5, "Total Teaching Load: ");
                    setCellValue(sheet, j4, 9, "Administrative Designation: "+ (!fac.getdesignation().equals("none") ? fac.getdesignation():" "));
                    setCellValue(sheet, j5, 9, "Admin Load: ");
                    setCellValue(sheet, l5, 11, "Total Load: ");
                    setCellValue(sheet, n5, 13, "Overload: ");
                    //setCellValue(sheet, p5, 15, "Total Eus: ");
                    setCellValue(sheet, b7, 1, "Course No.");
                    setCellValue(sheet, d7, 3, "Course Description");
                    setCellValue(sheet, h7, 7, "Units");
                    setCellValue(sheet, k7, 10, "Program");
                    setCellValue(sheet, l7, 11, "Section");
                    setCellValue(sheet, m7, 12, "Days");
                    setCellValue(sheet, n7, 13, "Time");
                    setCellValue(sheet, o7, 14, "Room");
                    setCellValue(sheet, p7, 15, "No. of Students");
                    setCellValue(sheet, q7, 16, "Eus");
                    setCellValue(sheet, h7+1, 7, "Lec");//for Lab
                    setCellValue(sheet, h7+1, 8, "Lab");//for Lec
                    setCellValue(sheet, h7+1, 9, "Total");//for Total
                    
                    num++;
                }
                
                sheet.addMergedRegion(new CellRangeAddress(addSubCode,addSubCode,1, 2));
                setCellValue(sheet,addSubCode, 1, schedIDDS);
                sheet.addMergedRegion(new CellRangeAddress(addSubName,addSubName,3, 6));
                setCellValue(sheet,addSubName, 3, a1);
                setCellValue(sheet, forLecLabTotal, 8, a3);//for Lab
                setCellValue(sheet, forLecLabTotal, 7, a2);//for Lec
                setCellValue(sheet, forLecLabTotal, 9, a4);//for total  
                setCellValue(sheet, forLecLabTotal, 10, a5);//for prg
                setCellValue(sheet, forLecLabTotal, 11, a6);//for sec
                setCellValue(sheet, forLecLabTotal, 12, a7);//for days  
                setCellValue(sheet, forLecLabTotal, 13, a8);//for time  
                setCellValue(sheet, forLecLabTotal, 14, a9);//for room
                setCellValue(sheet, forLecLabTotal, 15, a10);//for no of students
                setCellValue(sheet, forLecLabTotal, 16, a11);//for eus
                
                addSubCode++;
                addSubName++;
                forLecLabTotal++;
                cout++;
                count++;
                
            }
            int addt = cout+8;
            if(ifNasulodSaresultSet){
                sheet.addMergedRegion(new CellRangeAddress(f5, i6, 5, 8));
                setCellValue(sheet, f5, 5, "Total Teaching Load: "+totalTotalTeachingLoad);
                sheet.addMergedRegion(new CellRangeAddress(c5, e6, 2, 4)); // C5 to E6
                setCellValue(sheet, c5, 2, "No. of Preparations: "+getNumberOfRow(fac.getID()));
                sheet.addMergedRegion(new CellRangeAddress(p5, q6, 15, 16));
                setCellValue(sheet, p5, 15, "Total Eus: "+totalEus);
                three+=addt;
                four+=addt;
                five+=addt;
                six+=addt;
                seven+=addt;  
//                System.out.println(addSubCode+"lllllllllllllllllllllllll");
//                System.out.println(three+"----"+four);
            }
            

            
            
        }
        FileOutputStream outStream = new FileOutputStream("C:/Schedule 24-25/FacultyWorkload.xlsx");
        workbook.write(outStream);
        outStream.close();
    }
    public int getNumberOfRow(String id)throws Exception{
        int cont=0;
        //Connection con = DriverManager.getConnection("jdbc:mysql://localhost/schedulingsystemdatabase", "root", "");
        Statement statement = dc.con.createStatement();
        ResultSet resultSet = statement.executeQuery("select  *\n" +
                                                    "from schedule\n" +
                                                    "where facultyID='"+id+"' group by subjectID");
        while(resultSet.next()){
            cont++;
        }
        return cont;
    }
    private static void setCellValue(XSSFSheet sheet, int rowNum, int cellNum, String value) {
        
        Row row = sheet.getRow(rowNum);
        if (row == null) {
            row = sheet.createRow(rowNum);
        }
        Cell cell = row.createCell(cellNum);
        cell.setCellValue(value);
        
        CellStyle style = sheet.getWorkbook().createCellStyle();
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        // Apply the cell style to the cell
        cell.setCellStyle(style);   
    }
    public void applyBoldBorderToMergedRegions(XSSFSheet sheet, CellRangeAddress... mergedRegions) {
    XSSFWorkbook workbook = sheet.getWorkbook();
    CellStyle boldBordersStyle = workbook.createCellStyle();
    boldBordersStyle.setBorderBottom(BorderStyle.THICK);
    boldBordersStyle.setBorderTop(BorderStyle.THICK);
    boldBordersStyle.setBorderRight(BorderStyle.THICK);
    boldBordersStyle.setBorderLeft(BorderStyle.THICK);

    for (CellRangeAddress mergedRegion : mergedRegions) {
        for (int row = mergedRegion.getFirstRow(); row <= mergedRegion.getLastRow(); row++) {
            Row currentRow = sheet.getRow(row);
            if (currentRow == null) {
                currentRow = sheet.createRow(row);
            }
            for (int column = mergedRegion.getFirstColumn(); column <= mergedRegion.getLastColumn(); column++) {
                Cell currentCell = currentRow.getCell(column);
                if (currentCell == null) {
                    currentCell = currentRow.createCell(column);
                }
                currentCell.setCellStyle(boldBordersStyle);
            }
        }
    }
}
//    private static void applyBoldBorderToMergedRegions(XSSFSheet sheet) {
//        for (int i = 0; i < sheet.getNumMergedRegions(); i++) {
//            CellRangeAddress mergedRegion = sheet.getMergedRegion(i);
//            int startRow = mergedRegion.getFirstRow();
//            int endRow = mergedRegion.getLastRow();
//            int startCol = mergedRegion.getFirstColumn();
//            int endCol = mergedRegion.getLastColumn();
//
//            for (int row = startRow; row <= endRow; row++) {
//                for (int col = startCol; col <= endCol; col++) {
//                    Row currentRow = sheet.getRow(row);
//                    if (currentRow == null) {
//                        currentRow = sheet.createRow(row);
//                    }
//                    Cell currentCell = currentRow.getCell(col);
//                    if (currentCell == null) {
//                        currentCell = currentRow.createCell(col);
//                    }
//
//                    XSSFWorkbook workbook = sheet.getWorkbook();
//                    CellStyle style = workbook.createCellStyle();
//                    Font font = workbook.createFont();
//                    font.setBold(true); // Set the font to bold
//                    font.setFontName("Arial");
//                    style.setBorderTop(BorderStyle.MEDIUM);
//                    style.setBorderBottom(BorderStyle.MEDIUM);
//                    style.setBorderLeft(BorderStyle.MEDIUM);
//                    style.setBorderRight(BorderStyle.MEDIUM);
//                    style.setFont(font);
//                    currentCell.setCellStyle(style);
//                }
//            }
//        }
//    }
    
    @FXML private ChoiceBox selectRoom;
    @FXML private ChoiceBox selectDays;
    @FXML private ChoiceBox selectSection;
    @FXML private TableView<getRoomScheduleModel> roomSchedule;
    //@FXML private TableC<sectionModel> sectionModel;
   // @FXML private TableView<getSection> Section;
    @FXML private TableColumn<getRoomScheduleModel, String> r1,r2,r3,r4,r5;
   
    
    ObservableList<getRoomScheduleModel> roomsss = FXCollections.observableArrayList();
    
  
    
//    public void showDaysSchedules(String gtRoom)throws Exception{
//        
//        showDaysSchedules(gtRoom);
//        //getTheRoomsSchedule(gtRoom);
//        selectDays.setItems(roomsss);
//    }
    
//   public void showSectionSchedules(String gtRoom)throws Exception{
//        getTheRoomsSchedule(gtRoom);
//        selectSection.setItems(sII);
//    } 
//   
  
    public void putRoomInSelectRoom()throws Exception{
        r1.setCellValueFactory(cellData -> cellData.getValue().timeProperty());
        r2.setCellValueFactory(cellData -> cellData.getValue().daysProperty());
        r3.setCellValueFactory(cellData -> cellData.getValue().subjectProperty());
        r4.setCellValueFactory(cellData -> cellData.getValue().sectionProperty());
        r5.setCellValueFactory(cellData -> cellData.getValue().programProperty());
        ObservableList<String> roo = FXCollections.observableArrayList();
        Statement statement = dc.con.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT roomCode FROM room ;");
        while(resultSet.next()){
            roo.add(resultSet.getString("roomCode"));
        }
        selectRoom.setItems(roo);
    }
    
     
    
   public void putDaysInSelectDays() throws Exception {
    ObservableList<String> days = FXCollections.observableArrayList("MW", "MWF", "TTH", "FS");
    selectDays.setItems(days);
}
//   
//   public void putSectionInSelectSection()throws Exception{
//      
////        r1.setCellValueFactory(cellData -> cellData.getValue().timeProperty());
////        r2.setCellValueFactory(cellData -> cellData.getValue().daysProperty());
////        r3.setCellValueFactory(cellData -> cellData.getValue().subjectProperty());
////        r4.setCellValueFactory(cellData -> cellData.getValue().sectionProperty());
////        r5.setCellValueFactory(cellData -> cellData.getValue().programProperty());  
//        ObservableList<String> section = FXCollections.observableArrayList();
//        Statement statement = dc.con.createStatement();
//        ResultSet resultSet = statement.executeQuery("SELECT secName FROM section;");
//        while(resultSet.next()){
//            section.add(resultSet.getString("secName"));
//        }
//        selectSection.setItems(section);
//    }
    
   
       
   public void putSelectInSelectSection() throws Exception {
    ObservableList<String> section = FXCollections.observableArrayList("secName");
    selectSection.setItems(section);
}
   
   
  public void getTheRoomsSchedule(String gRoom, String gDay) throws Exception {
    roomsss.clear();

    Statement statement = dc.con.createStatement();
    ResultSet resultSet = statement.executeQuery(
        "SELECT schedule.schedTime, schedule.schedDays, subject.subName, section.secName, program.progCode, schedule.schedRoom " +
        "FROM schedule, subject, section, program " +
        "WHERE schedule.subjectID = subject.subID " +
        "AND schedule.sectionID = section.secID " +
        "AND section.progID = program.progID " +
        "AND schedule.schedRoom LIKE '%" + gRoom + "%' " +
        "AND schedule.schedDays LIKE '%" + gDay + "%';"
    );

    while (resultSet.next()) {
        String i = resultSet.getString("schedule.schedTime");
        String ii = resultSet.getString("schedule.schedDays");
        String iii = resultSet.getString("subject.subName");
        String iv = resultSet.getString("section.secName");
        String v = resultSet.getString("program.progCode");
        String vi = resultSet.getString("schedule.schedRoom");

        if (vi.contains("/")) {
            String[] splitRoom = vi.split("/");
            String[] splitTime = i.split("/");
            String[] splitDays = ii.split("/");

            for (int vii = 0; vii < splitRoom.length; vii++) {
                // Check if the selected day abbreviation is contained in the schedule.schedDays
                if (splitRoom[vii].contains(gRoom) && splitDays[vii].contains(gDay)) {
                    roomsss.add(new getRoomScheduleModel(splitTime[vii], splitDays[vii], iii, iv, v, splitRoom[vii]));
                }
            }
        } else {
            // Check if the selected day abbreviation is contained in the schedule.schedDays
            if (vi.contains(gRoom) && ii.contains(gDay)) {
                roomsss.add(new getRoomScheduleModel(i, ii, iii, iv, v, vi));
            }
        }
    }
    
   
}
 
//  public void getTheSectionSchedule(String sII) throws Exception{
//      Statement statement = dc.con.createStatement();
//    ResultSet resultSet = statement.executeQuery(
//      
//    "SELECT section.secName FROM section WHERE section.secName LIKE '%" + getSection. + "%'  );
//  }



    public void showRoomsSchedules(String gtRoom, String gtDay) throws Exception {
    getTheRoomsSchedule(gtRoom, gtDay);
    roomSchedule.setItems(roomsss);
    
    
}
    
    
    ObservableList<instructorsList> faculties = FXCollections.observableArrayList();
    @FXML private TableView<instructorsList> instructorTable;
    @FXML private TableColumn <instructorsList, String> f11,f111,f1111,f2,f3,f4,f5,f6;
    @FXML private TableColumn <instructorsList,instructorsList> f7;
    public void facultyTable()throws Exception{
        f11.setCellValueFactory(cellData -> cellData.getValue().fnameProperty());
        f111.setCellValueFactory(cellData -> cellData.getValue().mnameProperty());
        f1111.setCellValueFactory(cellData -> cellData.getValue().lnameProperty());
        f2.setCellValueFactory(cellData -> cellData.getValue().sexProperty());
        f3.setCellValueFactory(cellData -> cellData.getValue().expertiseProperty());
        f4.setCellValueFactory(cellData -> cellData.getValue().designationProperty());
        f5.setCellValueFactory(cellData -> cellData.getValue().gmailProperty());
        f6.setCellValueFactory(cellData -> cellData.getValue().contactProperty());
        f7.setCellFactory(param -> new ButtonTableCell());
        //getFacultyForPop();
        instructorTable.setItems(faculty);
        
    }
    public void getFacultyForPop()throws Exception{
        faculties.clear();
        Statement statement = dc.con.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM faculty ;");
        while(resultSet.next()){
            String i = resultSet.getString("facultyID");
            String ii = resultSet.getString("firstname");
            String iii = resultSet.getString("middlename");
            String iv = resultSet.getString("lastname");
            String v = resultSet.getString("suffix");
            String vi = resultSet.getString("sex");
            String vii = resultSet.getString("expertise");
            String viii = resultSet.getString("contact");
            String viv = resultSet.getString("gmail");
            String x = resultSet.getString("address");
            String xi = resultSet.getString("status");
            String xii = resultSet.getString("adminDesignation");
            faculties.add(new instructorsList(i,ii,iii,iv,v,vi,vii,viii,viv,x,xi,xii));
        }
    }
    @FXML private AnchorPane facultyPane,facultySchedulePane,abc;
    public class ButtonTableCell extends TableCell<instructorsList, instructorsList> {//ShowFacultyProfile
        private final Button viewButton = new Button("View");
        private final Button editButton = new Button("Edit");
        private final Button deleteButton = new Button("Delete");
        ButtonTableCell() {
            viewButton.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    facultySchedulePane.setVisible(true);
                    facultyPane.setVisible(false);
                    instructorsList selectedItem = getTableView().getItems().get(getIndex());
                    try {
                        getScheduleOf(selectedItem.getID());
                        facultyName.setText("Faculty Name: "+selectedItem.getfname()+" "+selectedItem.getmname()+" "+selectedItem.getlname());
                    } catch (Exception ex) {
                        Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            });
             editButton.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    try {
                        instructorsList selectedItem = getTableView().getItems().get(getIndex());
                        //,selectedItem.getfname(),selectedItem.getmname(),selectedItem.getlname(),selectedItem.getSuffix(),selectedItem.getSex(),selectedItem.getExpertise(),selectedItem.getContact(),selectedItem.getGmail(),selectedItem.getAddress(),selectedItem.getstatus(),selectedItem.getdesignation()
                        ShowFacultyProfile(selectedItem.getID());
                        abc.setVisible(true);facultyPane.setVisible(false);
                    } catch (Exception ex) {
                        Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            });
             deleteButton.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    instructorsList selectedItem = getTableView().getItems().get(getIndex());
                    try {
                        DeleteFunction(selectedItem.getID());
                    } catch (Exception ex) {
                        Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    
                }
            });
        }
        protected void updateItem(instructorsList person, boolean empty) {
            super.updateItem(person, empty);
            if (empty) {
                setGraphic(null);
            } else {
                HBox h = new HBox(10);
                h.getChildren().addAll(viewButton, editButton,deleteButton);
                setGraphic(h);
            }
        }
    }
    public void backButton(ActionEvent e){
        facultySchedulePane.setVisible(false);
        facultyPane.setVisible(true);
    }
    ObservableList<facultySchedModel> facultySched = FXCollections.observableArrayList();
    @FXML private TableView<facultySchedModel> scheduleOfFaculty;
    @FXML private TableColumn<facultySchedModel, String> s1,s2,s3,s4,s5,s6,s7,s8,s9,s10;
    public void getScheduleOf(String id)throws Exception{
        facultySched.clear();
        s1.setCellValueFactory(cellData -> cellData.getValue().courseNoProperty());
        s2.setCellValueFactory(cellData -> cellData.getValue().courseDesProperty());
        s3.setCellValueFactory(cellData -> cellData.getValue().secProperty());
        s4.setCellValueFactory(cellData -> cellData.getValue().progProperty());
        s5.setCellValueFactory(cellData -> cellData.getValue().timeProperty());
        s6.setCellValueFactory(cellData -> cellData.getValue().daysProperty());
        s7.setCellValueFactory(cellData -> cellData.getValue().roomProperty());
        s8.setCellValueFactory(cellData -> cellData.getValue().lecProperty());
        s9.setCellValueFactory(cellData -> cellData.getValue().labProperty());
        s10.setCellValueFactory(cellData -> cellData.getValue().totalProperty());
        getSchedFromDatabase(id);
        scheduleOfFaculty.setItems(facultySched);
    }
    public void getSchedFromDatabase(String ids)throws Exception{
        Statement statement = dc.con.createStatement();
        ResultSet resultSet = statement.executeQuery("select subject.subCode, subject.subName, section.secName, program.progCode, schedule.schedTime,\n" +
                                                    "schedule.schedDays, schedule.schedRoom, schedule.lec, schedule.lab,(schedule.lec + schedule.lab) AS Total\n" +
                                                    "from schedule, subject, room, section, program, faculty\n" +
                                                    "where schedule.subjectID = subject.subID\n" +
                                                    "AND schedule.sectionID = section.secID\n" +
                                                    "AND schedule.facultyID = '"+ids+"'" +
                                                    "group by schedule.schedID");
        while (resultSet.next()) {
            String i = resultSet.getString("subject.subName");
            String ii = resultSet.getString("section.secName");
            String iii = resultSet.getString("program.progCode");
            String iv = resultSet.getString("schedule.schedTime");
            String v = resultSet.getString("schedule.schedDays");
            String vi = resultSet.getString("schedule.schedRoom");
            String vii = resultSet.getString("schedule.lec");
            String viii = resultSet.getString("schedule.lab");
            String viv = resultSet.getString("Total");
            String x = resultSet.getString("subject.subCode");

            facultySched.add(new facultySchedModel(x,i,vii,viii,viv,iii,ii,v,iv,vi));
            //tableForSchedule.setItems(scheData);
        }
        
    }
    @FXML private TextField FacultyID;
    @FXML private AnchorPane roomPane,homePane;
    public void homeClick(ActionEvent e){
        roomPane.setVisible(false);
        homePane.setVisible(true);
        facultyPane.setVisible(false);
        facultySchedulePane.setVisible(false);
        abc.setVisible(false);
        facultyPane.setVisible(false);
        //getFacultyDataFromDatabase();
        //FacultyID.setVisible(true);
        getSchedDataFromDatabase();
    }
    public void facultiesClick(ActionEvent e){
        roomPane.setVisible(false);
        homePane.setVisible(false);
        facultyPane.setVisible(true);
        facultySchedulePane.setVisible(false);
        abc.setVisible(false);
       // FacultyID.setVisible(false);
        getSchedDataFromDatabase();
    }
    public void roomClick(ActionEvent e){
        roomPane.setVisible(true);
        homePane.setVisible(false); 
        facultyPane.setVisible(false);
        facultySchedulePane.setVisible(false);
        abc.setVisible(false);
        facultyPane.setVisible(false);
       // FacultyID.setVisible(false);
        getSchedDataFromDatabase();
    }
    //,String a,String b,String c,String d,String e,String f,String g,String h,String i,String j,String k
    @FXML private AnchorPane hammm;
    @FXML private TextField z1,z2,z3,z4,z6,z7,z8,z9,z10,z11;
    @FXML private ChoiceBox z5;
    String gettID = "";
    public void ShowFacultyProfile(String IDd)throws Exception{
        gettID = IDd;
        Statement statement = dc.con.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM faculty WHERE facultyID = "+IDd);
        while(resultSet.next()){
            String i = resultSet.getString("facultyID");
            String ii = resultSet.getString("firstname");
            String iii = resultSet.getString("middlename");
            String iv = resultSet.getString("lastname");
            String v = resultSet.getString("suffix");
            String vi = resultSet.getString("sex");
            String vii = resultSet.getString("expertise");
            String viii = resultSet.getString("contact");
            String viv = resultSet.getString("gmail");
            String x = resultSet.getString("address");
            String xi = resultSet.getString("status"); 
            String xii = resultSet.getString("adminDesignation");
            z1.setText(ii);
            z2.setText(iii);
            z3.setText(iv);
            z4.setText(v);
            z5.setValue(vi);
            z6.setText(vii);
            z7.setText(viii);
            z8.setText(viv);
            z9.setText(x);
            z10.setText(xi);
            z11.setText(xii);
        }
        //faculties.add(new instructorsList(i,ii,iii,iv,v,vi,vii,viii,viv,x,xi,xii));
        
    }
    public void ClickCancel(ActionEvent e){
        abc.setVisible(false);facultyPane.setVisible(true);
        
    }
    public void ClickSave(ActionEvent e)throws Exception{
        Statement statement = dc.con.createStatement();
    
        String updateQuery = "UPDATE faculty SET " +
                "firstname = '" + z1.getText() + "', " +
                "middlename = '" + z2.getText() + "', " +
                "lastname = '" + z3.getText() + "', " +
                "suffix = '" + z4.getText() + "', " +
                "sex = '" + z5.getValue().toString() + "', " +
                "expertise = '" + z6.getText() + "', " +
                "contact = '" + z7.getText() + "', " +
                "gmail = '" + z8.getText() + "', " +
                "address = '" + z9.getText() + "', " +
                "status = '" + z10.getText() + "', " +
                "adminDesignation = '" + z11.getText() + "' " +
                "WHERE facultyID = '" + gettID + "';";

        int rowsAffected = statement.executeUpdate(updateQuery);

        statement.close();

        abc.setVisible(false);facultyPane.setVisible(true);
        //getFacultyForPop();
        getFaculty();
        facultyTable();
    }
    @FXML private AnchorPane AddNewFacultyPane;
    @FXML private TextField x1,x2,x3,x4,x6,x7,x8,x9,x10,x11;
    @FXML private ChoiceBox x5;
    public void AddNewFacultyPane(ActionEvent e)throws Exception{
        Statement statement = dc.con.createStatement();
    
        String insertQuery = "INSERT INTO faculty (firstname,middlename,lastname,suffix,sex,expertise,contact,gmail,address,status,adminDesignation) "
                           + "VALUES ('"+x1.getText()+"',"+
                                      "'"+x2.getText()+"',"+
                                      "'"+x3.getText()+"',"+
                                      "'"+x4.getText()+"',"+
                                      "'"+x5.getValue()+"',"+
                                      "'"+x6.getText()+"',"+
                                      "'"+x7.getText()+"',"+
                                      "'"+x8.getText()+"',"+
                                      "'"+x9.getText()+"',"+
                                      "'"+x10.getText()+"',"+
                                      "'"+x11.getText()+"')";
        int rowsAffected = statement.executeUpdate(insertQuery);
        x1.clear();
        x2.clear();
        x3.clear();
        x4.clear();
        x6.clear();
        x7.clear();
        x8.clear();
        x9.clear();
        x10.clear();
        x11.clear();
        AddNewFacultyPane.setVisible(false);
        facultyPane.setVisible(true);
        getFaculty();
        facultyTable();
        
        
    }
    public void DeleteFunction(String iid)throws Exception{
        Statement statement = dc.con.createStatement();
        String deleteQuery = "DELETE FROM faculty WHERE facultyID = " + iid;
        int rowsAffected = statement.executeUpdate(deleteQuery);
        getFaculty();
        facultyTable();
    }
    public void getTheRoomsSchedules(String gRoom)throws Exception{
        roomsss.clear();
        Statement statement = dc.con.createStatement();
        ResultSet resultSet = statement.executeQuery("""
                                                     SELECT schedule.schedTime, schedule.schedDays,subject.subName, section.secName, program.progCode, schedule.schedRoom
                                                      FROM schedule, subject, section, program
                                                      where schedule.subjectID = subject.subID
                                                      and   schedule.sectionID = section.secID
                                                      and   section.progID = program.progID
                                                      and   schedule.schedRoom like '%"""+gRoom+"%';"
        );
        while(resultSet.next()){
            String i = resultSet.getString("schedule.schedTime");
            String ii = resultSet.getString("schedule.schedDays");
            String iii = resultSet.getString("subject.subName");
            String iv = resultSet.getString("section.secName");
            String v = resultSet.getString("program.progCode");
            String vi = resultSet.getString("schedule.schedRoom");
            if(vi.contains("/")){
                String[] splitRoom = vi.split("/");
                String[] splitTime = i.split("/");
                String[] splitDays = ii.split("/");
                for(int vii=0;vii<splitRoom.length;vii++){
                    if(splitRoom[vii].contains(gRoom)){
                        roomsss.add(new getRoomScheduleModel(splitTime[vii],splitDays[vii],iii,iv,v,vi));
                    }
                }
            }else{
                roomsss.add(new getRoomScheduleModel(i,ii,iii,iv,v,vi));
            }
            
        }
    }
    
    public void showRoomsSchedules(String gRoom)throws Exception{
        roomsss.clear();
        Statement statement = dc.con.createStatement();
        ResultSet resultSet = statement.executeQuery("""
                                                     SELECT schedule.schedTime, schedule.schedDays,subject.subName, section.secName, program.progCode, schedule.schedRoom
                                                      FROM schedule, subject, section, program
                                                      where schedule.subjectID = subject.subID
                                                      and   schedule.sectionID = section.secID
                                                      and   section.progID = program.progID
                                                      and   schedule.schedRoom like '%"""+gRoom+"%';"
        );
        while(resultSet.next()){
            String i = resultSet.getString("schedule.schedTime");
            String ii = resultSet.getString("schedule.schedDays");
            String iii = resultSet.getString("subject.subName");
            String iv = resultSet.getString("section.secName");
            String v = resultSet.getString("program.progCode");
            String vi = resultSet.getString("schedule.schedRoom");
            if(vi.contains("/")){
                String[] splitRoom = vi.split("/");
                String[] splitTime = i.split("/");
                String[] splitDays = ii.split("/");
                for(int vii=0;vii<splitRoom.length;vii++){
                    if(splitRoom[vii].contains(gRoom)){
                        roomsss.add(new getRoomScheduleModel(splitTime[vii],splitDays[vii],iii,iv,v,vi));
                    }
                }
            }else{
                roomsss.add(new getRoomScheduleModel(i,ii,iii,iv,v,vi));
            }
            
        }
      
    }
    public void showDaysSchedules(String gRoom)throws Exception{
        roomsss.clear();
        Statement statement = dc.con.createStatement();
        ResultSet resultSet = statement.executeQuery("""
                                                     SELECT schedule.schedTime, schedule.schedDays,subject.subName, section.secName, program.progCode, schedule.schedRoom
                                                      FROM schedule, subject, section, program
                                                      where schedule.subjectID = subject.subID
                                                      and   schedule.sectionID = section.secID
                                                      and   section.progID = program.progID
                                                      and   schedule.schedDays like '%"""+gRoom+"%';"
        );
        while(resultSet.next()){
            String i = resultSet.getString("schedule.schedTime");
            String ii = resultSet.getString("schedule.schedDays");
            String iii = resultSet.getString("subject.subName");
            String iv = resultSet.getString("section.secName");
            String v = resultSet.getString("program.progCode");
            String vi = resultSet.getString("schedule.schedRoom");
            if(vi.contains("/")){
                String[] splitRoom = vi.split("/");
                String[] splitTime = i.split("/");
                String[] splitDays = ii.split("/");
                for(int vii=0;vii<splitRoom.length;vii++){
                    if(splitRoom[vii].contains(gRoom)){
                        roomsss.add(new getRoomScheduleModel(splitTime[vii],splitDays[vii],iii,iv,v,vi));
                    }
                }
            }else{
                roomsss.add(new getRoomScheduleModel(i,ii,iii,iv,v,vi));
            }
            
        }
    
    }
      
      
      public void showSectionSchedules(String gRoom)throws Exception{
         //selectSection.setItems(scheduleholder);
         roomsss.clear();
        Statement statement = dc.con.createStatement();
        ResultSet resultSet = statement.executeQuery("""
                                                     SELECT schedule.schedTime, schedule.schedDays,subject.subName, section.secName, program.progCode, schedule.schedRoom
                                                      FROM schedule, subject, section, program
                                                      where schedule.subjectID = subject.subID
                                                      and   schedule.sectionID = section.secID
                                                      and   section.progID = program.progID
                                                      and   section.secName like '%"""+gRoom+"%';"
        );
        while(resultSet.next()){
            String i = resultSet.getString("schedule.schedTime");
            String ii = resultSet.getString("schedule.schedDays");
            String iii = resultSet.getString("subject.subName");
            String iv = resultSet.getString("section.secName");
            String v = resultSet.getString("program.progCode");
            String vi = resultSet.getString("schedule.schedRoom");
            if(vi.contains("/")){
                String[] splitRoom = vi.split("/");
                String[] splitTime = i.split("/");
                String[] splitDays = ii.split("/");
                for(int vii=0;vii<splitRoom.length;vii++){
                    if(splitRoom[vii].contains(gRoom)){
                        roomsss.add(new getRoomScheduleModel(splitTime[vii],splitDays[vii],iii,iv,v,vi));
                    }
                }
            }else{
                roomsss.add(new getRoomScheduleModel(i,ii,iii,iv,v,vi));
            }
            selectSection.setItems(scheduleholder);
            
        }
     
    }
}