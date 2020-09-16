package example;

import javax.persistence.*;
import java.util.Calendar;

@Entity
@Table(name = "export_logs")
public class ExportLog {

    @Id
    private String date;
    private int numberOfFiles;

    public ExportLog(){
        Calendar calendar = Calendar.getInstance();
        int month = calendar.get(Calendar.MONTH) + 1;
        date = month + "/" + calendar.get(Calendar.DATE) + "/" + calendar.get(Calendar.YEAR);
    }
    public String getDate(){
        return date;
    }
    public void setDate(String date){
         this.date = date;
    }
    public int getNumberOfFiles(){
        return numberOfFiles;
    }
    public void setNumberOfFiles(int numberOfFiles){
        this.numberOfFiles = numberOfFiles;
    }

}
