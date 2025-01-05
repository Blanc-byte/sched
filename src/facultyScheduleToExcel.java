
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import javafx.collections.ObservableList;
import org.apache.poi.xwpf.usermodel.IBodyElement;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author user
 */
public class facultyScheduleToExcel {
    public void createFormatWithData(String file, ObservableList<schedule> classSched) throws FileNotFoundException, IOException{
        FileInputStream docxFile=new FileInputStream(new File(file));
        XWPFDocument docx = new XWPFDocument(docxFile);
        int ro = 0;
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
                    String c7="",c8="",c9="";
                    if(an>2 && cells.size()==11){
                        schedule schedule = classSched.get(ro);
                        ro++;
                        for(XWPFTableCell cell:cells){
                        
                        
                            List<XWPFParagraph> cellParagraphs = cell.getParagraphs();
                                
                            //System.out.println("Number of CELLS: "+cells.size());
                            for(XWPFParagraph cellParagraph : cellParagraphs){
                                XWPFRun run = cellParagraph.createRun();
                                    switch(index){
                                        case 7:
                                            run.setText(schedule.getDay());
                                            run.setFontFamily("Times New Roman");
                                            run.setFontSize(8);
                                            break;
                                        case 8:
                                            run.setText(schedule.getTime());
                                            run.setFontFamily("Times New Roman");
                                            run.setFontSize(8);
                                            break;
                                        case 9:
                                            run.setText(schedule.getRoom());
                                            run.setFontFamily("Times New Roman");
                                            run.setFontSize(8);
                                            break;
                                    }
                                index++;
                            }
                        }
                        
                    }
                    an++;
                }
            }
        }
        //String[] sep = file.split("");
        System.out.println(file);
        FileOutputStream out = new FileOutputStream(new File("C:/UPDATEDSchedule.docx"));
        docx.write(out);
        out.close();
        docx.close();
    }
}
