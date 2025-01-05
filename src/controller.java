/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

import java.io.File;
import java.io.FileInputStream;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import models.getSchedFromDocx;
import models.instructorsList;
import models.roomModel;
import org.apache.poi.xwpf.usermodel.IBodyElement;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;

/**
 *
 * @author Administrator
 */
public class controller {
    
    @FXML private TextField displaySelectedFile;
    @FXML private Button selectAFile;
    
    DatabaseConnection dc = new DatabaseConnection();
    public void initialize()throws Exception{
        dc.connect();
        generateTheSomething();
        getAllInstructors();
        getAllSchedulesFromDatabase();
        getAllSubjects();
    }
    
    
    
    ObservableList<getSchedFromDocx> listOfSched = FXCollections.observableArrayList();
    ObservableList<String> faculties = FXCollections.observableArrayList();
    ObservableList<String> tobeLoadedRoom = FXCollections.observableArrayList();
    
    
    ObservableList<schedule> generatedSchedules = FXCollections.observableArrayList();
    public void generateSchedule()throws Exception{
        String[] roomTimeDays;
        String room="", time="", days="";
        for(getSchedFromDocx sC: listOfSched){
            String facultyID = getInstructorId(sC.getfaculty());
            if(sC.getlec().contains("1")){
                roomTimeDays = generateSchedFor1Hour(facultyID, sC.getsec()).split(":");
                time=roomTimeDays[0];
                days=roomTimeDays[1];
                room=roomTimeDays[2];
            }else if(sC.getlec().contains("2")){
                roomTimeDays = generateSchedFor2Hour(facultyID, sC.getsec()).split(":");
                time=roomTimeDays[0];
                days=roomTimeDays[1];
                room=roomTimeDays[2];
            }else if(sC.getlec().contains("3")){
                roomTimeDays = generateSchedFor3Hour(facultyID, sC.getsec()).split(":");
                time=roomTimeDays[0];
                days=roomTimeDays[1];
                room=roomTimeDays[2];
            }
            if(sC.getlab().contains("1")){
                roomTimeDays = generateSchedFor3HourLab(facultyID, sC.getsec()).split(":");
                if(room.equals("")){
                    time+=roomTimeDays[0];
                    days+=roomTimeDays[1];
                    room+=roomTimeDays[2];
                }else{
                    time+="/"+roomTimeDays[0];
                    days+="/"+roomTimeDays[1];
                    room+="/"+roomTimeDays[2];
                }
            }
            String subID = getId(sC.getcourseNo(), sC.getcourseDes());
            schedules.add(new schedule("69", facultyID, sC.getsec(), time, days, room, sem.getValue()+"", sy.getValue()+"", "1", sC.getlec(), sC.getlab(), sC.getnumOfStudent(), subID ));
            generatedSchedules.add(new schedule("69", facultyID, sC.getsec(), time, days, room, sem.getValue()+"", sy.getValue()+"", "1", sC.getlec(), sC.getlab(), sC.getnumOfStudent(), subID));
            
        }
        insertIntoDatabase();
        for(schedule s: schedules){
            System.out.println(s.getInstructorId()+" : "+s.getTime()+" : "+s.getDay()+" : "+s.getRoom()+" : "+s.getLec());
        }
        System.out.println("");
    }
    public String getId(String code, String desc){
        for(subjects s:subCodeFromDatabase){
            if(s.getCode().equals(code) && s.getDescription().equals(desc)) {
                return s.getId();
            }
        }
        return "100000";
    }
    public String generateSchedFor3HourLab(String facultyId, String section)throws Exception{
        ObservableList<String> twoHourDis = FXCollections.observableArrayList("8-10am","9-11am","10-12am", "1-3pm","2-4pm","3-5pm","4-6pm","5-7pm", "6-8pm","7-9am");
        ObservableList<String> days = FXCollections.observableArrayList("F", "M", "TU", "W", "TH" );
        
        ObservableList<String> oneHourDis = FXCollections.observableArrayList("8-9am","9-10am","10-11am","1-2pm","2-3pm","3-4pm","4-5pm","5-6pm", "6-7pm", "7-8pm","7-8am");
        ObservableList<String> twodays = FXCollections.observableArrayList("MWF");
        
        getAllRooms("WHERE type = 'it-lab' ORDER BY type ");
        // hour, rm.getroomCODE()
        boolean ifSchedIsAvail = false;
        for(String hour: oneHourDis){ // hour
            if(procceed(section, hour)){ // if ang section kay Educ pero dapat ang offerings kay dili sa educ
                for(roomModel rm: rooms){ //rm.getroomCODE()
                    if(checkRoomToProceed(rm.getroomBuilding(), section, "lab")){
                        for(String day: twodays){ // day
                            ifSchedIsAvail = checkIfSchedIsAvailable(facultyId, section, hour, rm.getroomCODE(), day);
                            if(ifSchedIsAvail){
                                return hour+":"+day+":"+rm.getroomCODE();
                            }
                        }
                    }
                }
            }
        }
        
        // ===================Desperate for IT Laboratory===================================
        
        getAllRooms("ORDER BY type");
        ObservableList<String> HourDis = FXCollections.observableArrayList("8-9am","9-10am","10-11am","11-12am","1-2pm","2-3pm","3-4pm","4-5pm","5-6pm", "6-7pm", "7-8pm","7-8am");
        ObservableList<String> oneday = FXCollections.observableArrayList("M", "TU","W", "TH","F");
        
        String desHour="";
        String desRoom="";
        String desDays="";
        int count = 0;
        for(String hour: HourDis){ // hour
            if(procceed(section, hour)){ // if ang section kay Educ pero dapat ang offerings kay dili sa educ
                for(roomModel rm: rooms){ //rm.getroomCODE()
                    if(checkRoomToProceed(rm.getroomBuilding(), section, "lab")){
                        for(String day: oneday){ // day
                            ifSchedIsAvail = checkIfSchedIsAvailable(facultyId, section, hour, rm.getroomCODE(), day);
                            if(ifSchedIsAvail){
                                count++;
                                if(count!=3){
                                    desHour+=hour+"/";
                                    desRoom+=rm.getroomCODE()+"/";
                                    desDays+=day+"/";
                                }else{
                                    desHour+=hour;
                                    desRoom+=rm.getroomCODE();
                                    desDays+=day;
                                    return desHour+":"+desDays+":"+desRoom;
                                }
//                                return hour+":"+day+":"+rm.getroomCODE();
                            }
                        }
                    }
                }
            }
        }
        return "1am:1am:1am";
    }
    
    public String generateSchedFor3Hour(String facultyId, String section)throws Exception{
        ObservableList<String> twoHourDis = FXCollections.observableArrayList("8-10am","9-11am","10-12am", "1-3pm","2-4pm","3-5pm","4-6pm","5-7pm", "6-8pm","7-9am");
        ObservableList<String> days = FXCollections.observableArrayList("F", "M", "TU", "W", "TH" );
        
        ObservableList<String> oneHourDis = FXCollections.observableArrayList("8-9am","9-10am","10-11am","11-12am","1-2pm","2-3pm","3-4pm","4-5pm","5-6pm", "6-7pm", "7-8pm","7-8am");
        ObservableList<String> twodays = FXCollections.observableArrayList("MWF");
        
        getAllRooms("ORDER BY type DESC");
        // hour, rm.getroomCODE()
        boolean ifSchedIsAvail = false;
        for(String hour: oneHourDis){ // hour
            if(procceed(section, hour)){ // if ang section kay Educ pero dapat ang offerings kay dili sa educ
                for(roomModel rm: rooms){ //rm.getroomCODE()
                    if(checkRoomToProceed(rm.getroomBuilding(), section, "lec")){
                        for(String day: twodays){ // day
                            ifSchedIsAvail = checkIfSchedIsAvailable(facultyId, section, hour, rm.getroomCODE(), day);
                            if(ifSchedIsAvail){
                                return hour+":"+day+":"+rm.getroomCODE();
                            }
                        }
                    }
                }
            }
        }
        // ===================Desperate for 3 Hours Lecture===================================
        ObservableList<String> HourDis = FXCollections.observableArrayList("8-9am","9-10am","10-11am","11-12am","1-2pm","2-3pm","3-4pm","4-5pm","5-6pm", "6-7pm", "7-8pm","7-8am");
        ObservableList<String> oneday = FXCollections.observableArrayList("M", "TU","W", "TH","F");
        
        String desHour="";
        String desRoom="";
        String desDays="";
        int count = 0;
        for(String hour: HourDis){ // hour
            if(procceed(section, hour)){ // if ang section kay Educ pero dapat ang offerings kay dili sa educ
                for(roomModel rm: rooms){ //rm.getroomCODE()
                    if(checkRoomToProceed(rm.getroomBuilding(), section, "lab")){
                        for(String day: oneday){ // day
                            ifSchedIsAvail = checkIfSchedIsAvailable(facultyId, section, hour, rm.getroomCODE(), day);
                            if(ifSchedIsAvail){
                                count++;
                                if(count!=3){
                                    desHour+=hour+"/";
                                    desRoom+=rm.getroomCODE()+"/";
                                    desDays+=day+"/";
                                }else{
                                    desHour+=hour;
                                    desRoom+=rm.getroomCODE();
                                    desDays+=day;
                                    return desHour+":"+desDays+":"+desRoom;
                                }
//                                return hour+":"+day+":"+rm.getroomCODE();
                            }
                        }
                    }
                }
            }
        }
        return "1am:1am:1am";
    }
    public String generateSchedFor2Hour(String facultyId, String section)throws Exception{
        ObservableList<String> twoHourDis = FXCollections.observableArrayList("8-10am","9-11am","10-12am", "1-3pm","2-4pm","3-5pm","4-6pm","5-7pm", "6-8pm","7-9am");
        ObservableList<String> days = FXCollections.observableArrayList("F", "M", "TU", "W", "TH" );
        
        ObservableList<String> oneHourDis = FXCollections.observableArrayList("8-9am","9-10am","10-11am","1-2pm","2-3pm","3-4pm","4-5pm","5-6pm", "6-7pm", "7-8pm","7-8am");
        ObservableList<String> twodays = FXCollections.observableArrayList("MW", "TUTH");
        
        getAllRooms("ORDER BY type DESC");
        // hour, rm.getroomCODE()
        boolean ifSchedIsAvail = false;
        
        
        
        for(String hour: oneHourDis){ // hour
            if(procceed(section, hour)){ // if ang section kay Educ pero dapat ang offerings kay dili sa educ
                for(roomModel rm: rooms){ //rm.getroomCODE()
                    if(checkRoomToProceed(rm.getroomBuilding(), section, "lec")){
                        for(String day: twodays){ // day
                            ifSchedIsAvail = checkIfSchedIsAvailable(facultyId, section, hour, rm.getroomCODE(), day);
                            if(ifSchedIsAvail){
                                return hour+":"+day+":"+rm.getroomCODE();
                            }
                        }
                    }
                }
            }
        }
        for(String hour: twoHourDis){ // hour
            if(procceed(section, hour)){ // if ang section kay Educ pero dapat ang offerings kay dili sa educ
                for(roomModel rm: rooms){ //rm.getroomCODE()
                    if(checkRoomToProceed(rm.getroomBuilding(), section, "lec")){
                        for(String day: days){ // day
                            ifSchedIsAvail = checkIfSchedIsAvailable(facultyId, section, hour, rm.getroomCODE(), day);
                            if(ifSchedIsAvail){
                                return hour+":"+day+":"+rm.getroomCODE();
                            }
                        }
                    }
                }
            }
        }
        // ===================Desperate for 3 Hours Lecture===================================
        ObservableList<String> HourDis = FXCollections.observableArrayList("8-9am","9-10am","10-11am","11-12am","1-2pm","2-3pm","3-4pm","4-5pm","5-6pm", "6-7pm", "7-8pm","7-8am");
        ObservableList<String> oneday = FXCollections.observableArrayList("M", "TU","W", "TH","F");
        
        String desHour="";
        String desRoom="";
        String desDays="";
        int count = 0;
        for(String hour: HourDis){ // hour
            if(procceed(section, hour)){ // if ang section kay Educ pero dapat ang offerings kay dili sa educ
                for(roomModel rm: rooms){ //rm.getroomCODE()
                    if(checkRoomToProceed(rm.getroomBuilding(), section, "lab")){
                        for(String day: oneday){ // day
                            ifSchedIsAvail = checkIfSchedIsAvailable(facultyId, section, hour, rm.getroomCODE(), day);
                            if(ifSchedIsAvail){
                                count++;
                                if(count!=2){
                                    desHour+=hour+"/";
                                    desRoom+=rm.getroomCODE()+"/";
                                    desDays+=day+"/";
                                }else{
                                    desHour+=hour;
                                    desRoom+=rm.getroomCODE();
                                    desDays+=day;
                                    return desHour+":"+desDays+":"+desRoom;
                                }
//                                return hour+":"+day+":"+rm.getroomCODE();
                            }
                        }
                    }
                }
            }
        }
        
        return "1am:1am:1am";
    }
    
    public String generateSchedFor1Hour(String facultyId, String section)throws Exception{
        ObservableList<String> oneHourDis = FXCollections.observableArrayList("8-9am","9-10am","10-11am","1-2pm","2-3pm","3-4pm","4-5pm","5-6pm", "6-7pm", "7-8pm","7-8am");
        ObservableList<String> days = FXCollections.observableArrayList("M", "TU", "W", "TH", "F");
        getAllRooms("ORDER BY type DESC");
        // hour, rm.getroomCODE()
        boolean ifSchedIsAvail = false;
        for(String hour: oneHourDis){ // hour
            if(procceed(section, hour)){ // if ang section kay Educ pero dapat ang offerings kay dili sa educ
                for(roomModel rm: rooms){ //rm.getroomCODE()
                    if(checkRoomToProceed(rm.getroomBuilding(), section, "lec")){
                        for(String day: days){ // day
                            ifSchedIsAvail = checkIfSchedIsAvailable(facultyId, section, hour, rm.getroomCODE(), day);
                            if(ifSchedIsAvail){
                                return hour+":"+day+":"+rm.getroomCODE();
                            }
                        }
                    }
                }
            }
            
        }
        
        return "1am:1am:1am";
    }
    public boolean checkIfSchedIsAvailable(String facultyId, String section, String time, String room, String day){
        String prevHour="", prevDay="", prevRoom="";
        for( schedule sched: schedules){
            ObservableList<String> week = FXCollections.observableArrayList("M","TU","W","TH","F", "S");
            boolean checkInstructor = false,
                    checksection = false,
                    checktime = false,
                    checkroom = false,
                    checkday = false;
            checkInstructor = sched.getInstructorId().equals(facultyId);
            checksection = sched.getSection().equals(section);
            
            
            if(!sched.getDay().contains("/")){// 7-8am 9-10am
                boolean same = sched.getTime().contains("pm") && time.contains("pm") || sched.getTime().contains("am") && time.contains("am")  ? true:false;
                String[] past = sched.getTime().replaceAll("[^\\d-]", "").split("-");
                String[] suggested = time.replaceAll("[^\\d-]", "").split("-");
                int count =0;
                for(int a=Integer.parseInt(past[0]); a<= Integer.parseInt(past[1]) ; a++){
                    for(int b=Integer.parseInt(suggested[0]); b<= Integer.parseInt(suggested[1]) ; b++){
                        if(a==b){
                            count++;
                        }
                    }
                }
                checktime = count>1 && same;// false && true
                checkroom = sched.getRoom().equals(room);
                
                for(String daysOfWeek: week){
                    if(day.contains(daysOfWeek) && sched.getDay().contains(daysOfWeek)){
                        checkday = true;
                        break;
                    }
                }
                if(checkInstructor && checktime && checkday){
                    return false;
                }
                if(checksection && checktime && checkday){
                    return false;
                }
                if(checkroom && checktime && checkday){
                    return false;
                }
            }else{
                String[] sT = sched.getTime().split("/");
                String[] sD = sched.getDay().split("/");
                String[] sR = sched.getRoom().split("/");
                for(int a=0; a<sT.length; a++){
                    boolean same = sT[a].contains("pm") && time.contains("pm") || sT[a].contains("am") && time.contains("am")  ? true:false;
                    String[] past = sT[a].replaceAll("[^\\d-]", "").split("-");
                    String[] suggested = time.replaceAll("[^\\d-]", "").split("-");
                    int count =0;
                    for(int c=Integer.parseInt(past[0]); c<= Integer.parseInt(past[1]) ; c++){
                        for(int b=Integer.parseInt(suggested[0]); b<= Integer.parseInt(suggested[1]) ; b++){
                            if(c==b){
                                count++;
                            }
                        }
                    }
                    checktime = count>1 && same;
                    checkroom = sR[a].equals(room);
                    
                    for(String daysOfWeek: week){
                        if(day.contains(daysOfWeek) && sD[a].contains(daysOfWeek)){
                            checkday = true;
                            break;
                        }
                    }
                    
                    if(checkInstructor && checktime && checkday){
                        return false;
                    }

                    if(checksection && checktime && checkday){
                        return false;
                    }
                    
                    if(!prevHour.equals("")){
                        if(!prevHour.contains("/")){
                            boolean samePrev = prevHour.contains("pm") && time.contains("pm") || prevHour.contains("am") && time.contains("am")  ? true:false;
                            String[] pastPrev = prevHour.replaceAll("[^\\d-]", "").split("-");
                            String[] suggestedPrev = time.replaceAll("[^\\d-]", "").split("-");
                            int countPrev = 0;
                            for(int aa=Integer.parseInt(pastPrev[0]); aa<= Integer.parseInt(pastPrev[1]) ; aa++){
                                for(int bb=Integer.parseInt(suggestedPrev[0]); bb<= Integer.parseInt(suggestedPrev[1]) ; bb++){
                                    if(aa==bb){
                                        countPrev++;
                                    }
                                }
                            }
                            checktime = countPrev>1 && samePrev;// false && true
                            checkroom = prevRoom.equals(room);
                            for(String daysOfWeek: week){
                                if(day.contains(daysOfWeek) && prevDay.contains(daysOfWeek)){
                                    checkday = true;
                                    break;
                                }
                            }
                            if(checkInstructor && checktime && checkday){
                                return false;
                            }
                            if(checksection && checktime && checkday){
                                return false;
                            }
                            checktime = false;
                            checkroom = false;
                            checkday = false;
                        }else{
                            String[] sTp = prevHour.split("/");
                            String[] sDp = prevDay.split("/");
                            String[] sRp = prevRoom.split("/");
                            for(int ac=0; ac<sTp.length; ac++){
                                boolean same2 = sTp[ac].contains("pm") && time.contains("pm") || sTp[ac].contains("am") && time.contains("am")  ? true:false;
                                String[] past2 = sTp[ac].replaceAll("[^\\d-]", "").split("-");
                                String[] suggested2 = time.replaceAll("[^\\d-]", "").split("-");
                                int count2 =0;
                                for(int cc=Integer.parseInt(past2[0]); cc<= Integer.parseInt(past2[1]) ; cc++){
                                    for(int bc=Integer.parseInt(suggested2[0]); bc<= Integer.parseInt(suggested2[1]) ; bc++){
                                        if(cc==bc){
                                            count2++;
                                        }
                                    }
                                }
                                checktime = count2>1 && same2;
                                checkroom = sRp[a].equals(room);
                                
                                for(String daysOfWeek: week){
                                    if(day.contains(daysOfWeek) && sDp[a].contains(daysOfWeek)){
                                        checkday = true;
                                        break;
                                    }
                                }
                                if(checkInstructor && checktime && checkday){
                                    return false;
                                }
                                if(checksection && checktime && checkday){
                                    return false;
                                }
                                checktime = false;
                                checkroom = false;
                                checkday = false;
                            }
                        }
                    }
                    
                    if(!prevHour.equals("")){
                        prevHour+="/"+sT[a];
                        prevDay+="/"+sD[a];
                        prevRoom+="/"+sR[a];
                    }else{
                        prevHour+=sT[a];
                        prevDay+=sD[a];
                        prevRoom+=sR[a];
                    }
                    //System.out.println(prevHour+ " = "+ prevDay+ " = "+ prevRoom);
                    checktime = false;
                    checkroom = false;
                    checkday = false;
                }
            }
            prevHour="";
            prevDay="";
            prevRoom="";
        }
        return true;
    }
    
    public void checkAgainTheSched(){
        
    }
    public void insertIntoDatabase()throws Exception{
        Statement statement = dc.con.createStatement();
        String insertQuery = "INSERT INTO `schedule`(`instructor_id`, `section`, `time`, `day`, `room`, `sem`, `sy`, `lec`, `lab`, `noOfStudents`, `subid`)"
                            +" Values ";
        String open = ",";
        String script = "";
        int counter=1;
        for(schedule sc: generatedSchedules){
            script += "('"+ sc.getInstructorId()+"','"+sc.getSection()+"','"+sc.getTime()+"','"+sc.getDay()+"','"+sc.getRoom()+"','"+ sem.getValue()+""+"','"+sy.getValue()+"','"+sc.getLec()+"','"+sc.getLab()+"','"+sc.getNoOfStudents()+"','"+sc.getsubid()+"')";
            System.out.println("('"+ sc.getInstructorId()+"','"+sc.getSection()+"','"+sc.getTime()+"','"+sc.getDay()+"','"+sc.getRoom()+"','"+ sem.getValue()+""+"','"+sy.getValue()+"','"+sc.getLec()+"','"+sc.getLab()+"','"+sc.getNoOfStudents()+"','"+sc.getsubid()+"')");
            if (counter < generatedSchedules.size()) {
                script+=",";
            }
            counter++;
        }
        insertQuery += script;
        System.out.println(insertQuery);
        int rowsAffected = statement.executeUpdate(insertQuery);
    }
    public boolean checkRoomToProceed(String building, String section, String typeOfClass){
        if(prog.getValue().equals("BSIT") && typeOfClass.equals("lab")){
            
        }
        if(prog.getValue().equals("BTLEd") && building.contains("2")){
            return true;
        }else if(section.contains("TLE") && building.contains("2")){
            return true;
        }else if(!section.contains("TLE")){
            return true;
        }
        return false;
    }
    
    public boolean procceed(String section, String hour){
        if(prog.getValue().equals("BTLEd")){
            return true;
        }else if(section.contains("TLE") && hour.contains("pm")){
            return true;
        }else if(!section.contains("TLE")){
            return true;
        }
        return false;
    }
    
    public String getInstructorId(String fullname){
        String[] splitfullname = fullname.split(" ");
        for(instructorsList il:instructors){
            if((il.getlname().contains(splitfullname[0]) || splitfullname[0].contains(il.getlname())) &&
               ( il.getfname().contains(splitfullname[1]) || splitfullname[1].contains(il.getfname()))
              ){
                return il.getid();
            }
        }
        return "10000";
    }
    ObservableList<roomModel> rooms = FXCollections.observableArrayList();
    public void getAllRooms(String orderBy)throws Exception{
        rooms.clear();
        Statement statement = dc.con.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM rooms "+orderBy);
        while (resultSet.next()) {
            String aa = resultSet.getString("id");
            String b = resultSet.getString("name");
            String cc = resultSet.getString("type");
            String d = resultSet.getString("building");
            rooms.add(new roomModel(aa, b, cc, d));
        }
    }
    
    ObservableList<instructorsList> instructors = FXCollections.observableArrayList();
    public void getAllInstructors()throws Exception{
        instructors.clear();
        Statement statement = dc.con.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM instructor");
        while (resultSet.next()) {
            String aa = resultSet.getString("id");
            String b = resultSet.getString("fname");
            String cc = resultSet.getString("lname");
            String d = resultSet.getString("role");
            instructors.add(new instructorsList(aa, b, cc, d));
        }
    }
    public void insertTheInstructor()throws Exception{
        Statement statement = dc.con.createStatement();
        for(String sC: faculties){
            String[] split = sC.split(" ");
            boolean True = true;
            for(instructorsList il: instructors){
                if(il.getfname().contains(split[1]) && il.getlname().contains(split[0]) ){
                    True = false;
                }
            }
            if(True){
                statement.executeUpdate("INSERT INTO `instructor`(`fname`, `lname`) "
                                + "VALUES ('"+split[1]+"','"+split[0]+"')");
            }
            
        }
    }
    ObservableList<subjects> subCodeFromDatabase = FXCollections.observableArrayList();
    public void getAllSubjects()throws Exception{
        subCodeFromDatabase.clear();
        Statement statement = dc.con.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM subjects");
        while (resultSet.next()) {
            String aa = resultSet.getString("id");
            String b = resultSet.getString("code");
            String cc = resultSet.getString("description");
            subCodeFromDatabase.add(new subjects(aa, b, cc));
        }
    }
    public void insertTheSubjects()throws Exception{
        Statement statement = dc.con.createStatement();
        for(subjects sC: subCode){
            boolean True = true;
            ResultSet resultSet = statement.executeQuery("SELECT * FROM subjects WHERE code = '"+sC.getCode()+"' AND description = '"+sC.getDescription()+"'");
            while (resultSet.next()) {
                True = false;
            }
            if(True){
                statement.executeUpdate("INSERT INTO `subjects`(`code`, `description`) "
                                        + "VALUES ('"+sC.getCode()+"','"+sC.getDescription()+"')");
            }
        }
        getAllSubjects();
    }
    ObservableList<subjects> subCode = FXCollections.observableArrayList();
    public void generate(ActionEvent event)throws Exception{
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
                        int index = 1;
                        String c1="",c2="",c3="",c4="",c5="",c6="",c10="",c11="";
                        for(XWPFTableCell cell:cells){
                            List<XWPFParagraph> cellParagraphs = cell.getParagraphs();

                            if(an>2 && cells.size()==11){
                                
                                for(XWPFParagraph cellParagraph : cellParagraphs){
                                    
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
                            index++;
                        }
                        if(!c4.equals("")&&!c5.equals("")){
                            listOfSched.add(new getSchedFromDocx(c1,c3,c4,c5,c6,prog.getValue().toString(),c2,c10,c11));
                            subCode.add(new subjects("1", c1, c3));
                            faculties.add(c11);
                        }
                        an++;
                    }
                }
                
            }
            displayAllSubCode();
            insertTheInstructor();
            generateSchedule();
            insertTheSubjects();
            updateThefile();
        }
        
    }
    
    
    ObservableList<schedule> schedules = FXCollections.observableArrayList();
    public void getAllSchedulesFromDatabase()throws Exception{
        schedules.clear();
        Statement statement = dc.con.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM schedule");
        while (resultSet.next()) {
            String a1 = resultSet.getString("id");
            String a2 = resultSet.getString("instructor_id");
            String a3 = resultSet.getString("section");
            String a4 = resultSet.getString("time");
            String a5 = resultSet.getString("day");
            String a6 = resultSet.getString("room");
            String a7 = resultSet.getString("sem");
            String a8 = resultSet.getString("sy");
            String a9 = resultSet.getString("status");
            String a10 = resultSet.getString("lec");
            String a11 = resultSet.getString("lab");
            String a12 = resultSet.getString("noOfStudents");
            String a13 = resultSet.getString("subid");
            schedules.add(new schedule(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13));
        }
    }
    
    public void displayAllSubCode()throws Exception{
        for(String sC: faculties){
            System.out.println(sC);
        }
        for(getSchedFromDocx sC: listOfSched){
            System.out.println(sC.getcourseNo()+" - "+sC.getcourseDes()+" - "+sC.getfaculty()+" - "+sC.getprog()+" - "+sC.getsec()+" - "+sC.gettotal());
        }
        insertTheInstructor();
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
    
    @FXML private ChoiceBox sy,prog,sem;
    ObservableList<String> syGen = FXCollections.observableArrayList("2024-2025");
    ObservableList<String> progGen = FXCollections.observableArrayList("BSIT", "BSBA", "BSA", "BTLEd");
    ObservableList<String> semGen = FXCollections.observableArrayList("1","2");
    public void generateTheSomething(){
        sy.setItems(syGen);
        prog.setItems(progGen);
        sem.setItems(semGen);
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
        exc.createFormatWithData(file, schedules);
    }
    
}
