package example;

import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.io.*;
import java.util.*;

@Component
public class Scheduler {
    @Autowired
    private ExportLogRepository exportLogRepository;

    @Scheduled(cron = "*/10 * * * * *")
    public void dataImport() {

        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        ctx.register(MongoDBConfig.class);
        ctx.refresh();
        Calendar calendar = Calendar.getInstance();

        MongoTemplate database = ctx.getBean(MongoTemplate.class);


        File maindirectory = new File("./data");
        File[] fileList = maindirectory.listFiles();
        String data = "";
        int numberOfFiles = 0;
        boolean needToLogExport = false;
        for (File file: fileList) {
            if(file.getName().endsWith(".json")) {

                ArrayList<Document> listOfDocuments = retrieveDocuments(file);

                System.out.println(file.getName());
                String filename = (file.getName().split("\\."))[0];

                try {
                    if(!database.collectionExists(filename)) {
                        database.createCollection(filename);

                        MongoCollection collection = database.getCollection(filename);

                        collection.insertMany(listOfDocuments);
                        numberOfFiles++;
                        needToLogExport = true;
                        }


                    else{
                        System.out.println("Collection " + filename + " already exists");
                    }
                }
                catch (Exception e){
                    e.printStackTrace();
                }

            }

        }
        if(needToLogExport){
            ExportLog log = new ExportLog();
            log.setNumberOfFiles(numberOfFiles);
            exportLogRepository.save(log);
            System.out.println("Export log saved");
        }
        ctx.registerShutdownHook();
        ctx.close();
    }

    public ArrayList<Document> retrieveDocuments(File file){
        ArrayList<Document> listOfDocuments = new ArrayList<Document>();
        String documentData = "";
        try {
           // Scanner scan = new Scanner(file);
            BufferedReader reader = new BufferedReader(new FileReader(file));
            int data = reader.read();
            int bracketCounter = 0;
            boolean insideBracket = false;
            while(data != -1){
                if((char) data == '{'){
                    bracketCounter++;
                    insideBracket = true;
                }
                if(insideBracket)
                    documentData += (char) data;
                if((char) data == '}' && insideBracket){
                    bracketCounter--;
                }
                if(bracketCounter == 0 && insideBracket) {
                    listOfDocuments.add(Document.parse(documentData));
                    insideBracket = false;
                }
                data = reader.read();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return listOfDocuments;
    }

}