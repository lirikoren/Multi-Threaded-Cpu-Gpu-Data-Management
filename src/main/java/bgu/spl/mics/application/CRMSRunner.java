package bgu.spl.mics.application;
import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.TerminateAllBroadcast;
import bgu.spl.mics.application.objects.*;
import bgu.spl.mics.application.services.*;
import com.google.gson.*;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.CountDownLatch;

/** This is the Main class of Compute Resources Management System application. You should parse the input file,
 * create the different instances of the objects, and run the system.
 * In the end, you should output a text file.
 */
public class CRMSRunner {
    public static void main(String[] args) throws FileNotFoundException, InterruptedException {

        MessageBusImpl msgBus = MessageBusImpl.getInstance();
        Cluster cluster = Cluster.getInstance();
        Object obj = JsonParser.parseReader(new FileReader(args[0]));
        JsonObject jsonObject = (JsonObject) obj;
        LinkedList<MicroService> services = new LinkedList<>();
        int numServices = 0;
        numServices += ((JsonArray) jsonObject.get("Students")).getAsJsonArray().size();
        numServices += ((JsonArray) jsonObject.get("GPUS")).getAsJsonArray().size();
        numServices += ((JsonArray) jsonObject.get("CPUS")).getAsJsonArray().size();
        numServices += ((JsonArray) jsonObject.get("Conferences")).getAsJsonArray().size();
        numServices += 1; //timeservice

        final CountDownLatch countDownLatch = new CountDownLatch(numServices);
        LinkedList<Thread> threads = new LinkedList<>();

        JsonArray studentsJsnArray = (JsonArray) jsonObject.get("Students");
        JsonArray gpusJsnArray = (JsonArray) jsonObject.get("GPUS");
        JsonArray cpusJsnArray = (JsonArray) jsonObject.get("CPUS");
        JsonArray conferencesJsnArray = (JsonArray) jsonObject.get("Conferences");

        long tickTime = jsonObject.get("TickTime").getAsLong();
        long duration = jsonObject.get("Duration").getAsLong();

        TerminateAllBroadcast terminate = new TerminateAllBroadcast();
        msgBus.sendBroadcast(terminate);

        //add studentServices
        for (JsonElement studentElement : studentsJsnArray) {
            JsonObject JSonStudent = studentElement.getAsJsonObject();
            String name = JSonStudent.get("name").getAsString();
            String department = JSonStudent.get("department").getAsString();
            String status = JSonStudent.get("status").getAsString();
            Student student = new Student(name, department, status);
            StudentService studentService = new StudentService("student service", student, countDownLatch);
            services.addLast(studentService);
            threads.addLast(new Thread(studentService));

            JsonArray modelsJsnArray = (JsonArray) JSonStudent.get("models");
            for (JsonElement modelElement : modelsJsnArray) {
                JsonObject JSonModel = modelElement.getAsJsonObject();
                String modelName = JSonModel.get("name").getAsString();
                String type = JSonModel.get("type").getAsString();
                long size = JSonModel.get("size").getAsLong();
                Data data = new Data(type, size);
                Model model = new Model(modelName, data, student);
                student.addModel(model);
            }
            msgBus.register(studentService);
        }
        //add gpuServices
        for (JsonElement gpuElement : gpusJsnArray) {
            String gpuType = gpuElement.getAsString();
            GPUService gpuService = new GPUService("gpu service", gpuType, countDownLatch);
            msgBus.register(gpuService);
            services.addLast(gpuService);
            threads.addLast(new Thread(gpuService));
        }

        //add cpuServices
        for (JsonElement cpuElement : cpusJsnArray) {
            int cpuSize = cpuElement.getAsInt();
            CPUService cpuService = new CPUService("cpu service", cpuSize, countDownLatch);
            msgBus.register(cpuService);
            services.addLast(cpuService);
            threads.addLast(new Thread(cpuService));
        }

        //add conferenceServices
        for (JsonElement conferenceElement : conferencesJsnArray) {
            JsonObject JSonConference = conferenceElement.getAsJsonObject();
            String name = JSonConference.get("name").getAsString();
            long date = JSonConference.get("date").getAsLong();
            ConferenceService conferenceService = new ConferenceService(name, date, countDownLatch);
            msgBus.register(conferenceService);
            services.addLast(conferenceService);
            threads.addLast(new Thread(conferenceService));
        }
        TimeService timeService = new TimeService(tickTime, duration, countDownLatch);
        for (Thread thread : threads) {
            thread.start();
        }
        threads.addLast(new Thread(timeService));
        Thread timeServiceThread = threads.getLast();

        timeServiceThread.start();
        countDownLatch.await();
        for (Thread thread : threads) {
            thread.join();
        }
        //OUTPUT:
        JsonArray students=new JsonArray();
        int trainedModels = 0;
        for (MicroService service : services) {
            if (service.getClass() == StudentService.class) {
                Student student = ((StudentService) (service)).getStudent();
                //System.out.println("Student name:" + student.getName());
                JsonObject studentObj=new JsonObject();
                studentObj.addProperty("name",student.getName());
                studentObj.addProperty("department", student.getDepartment());
                studentObj.addProperty("status",student.getStatusStr());
                studentObj.addProperty("publications",student.getPublications_num());
                studentObj.addProperty("papersRead",student.getPapersRead_num());

                JsonArray models = new JsonArray();
                //System.out.println("Trained models:");
                for (Model model : student.getMy_Models()) {
                    if (model.getStatusString().equals("Trained") || model.getStatusString().equals("Tested")) {
                        Data data= model.getData();
                        JsonObject modelObj = new JsonObject();
                        modelObj.addProperty("name",model.getName());
                        JsonObject dataObj = new JsonObject();
                        dataObj.addProperty("type",data.getTypeStr());
                        dataObj.addProperty("size",data.getSize());
                        modelObj.add("data",dataObj);
                        modelObj.addProperty("status",model.getStatusString());
                        modelObj.addProperty("results", model.getResult().toString());
                        models.add(modelObj);
                        trainedModels++;
//                        System.out.print("name: " + model.getName() + " Status:" + model.getStatusString() + " Published:");
//                        if (model.isPublished()) System.out.println(" true");
//                        else System.out.println(" false");
                    }
                }
                studentObj.add("trainedModels", models);
                students.add(studentObj);
//                System.out.println("Number of read papers: " + student.getPapersRead_num());
//                System.out.println("----------------------------------------------");
            }
        }
        JsonArray conferences = new JsonArray();
        for (MicroService service : services) {
            if (service.getClass() == ConferenceService.class) {
                ConfrenceInformation conferenceInfo = ((ConferenceService) (service)).getConferenceInfo();
                //System.out.println("Conference " + conferenceInfo.getName() + " has published: " + conferenceInfo.getGoodResults_String());
                JsonObject conObj = new JsonObject();
                conObj.addProperty("name", conferenceInfo.getName());
                conObj.addProperty("date", conferenceInfo.getDate());
                JsonArray publications = new JsonArray();
                for(Model model:conferenceInfo.getGood_result_Models_List()){
                    JsonObject publication=new JsonObject();
                    publication.addProperty("name",model.getName());
                    JsonObject dataObj = new JsonObject();
                    dataObj.addProperty("type",model.getData().getTypeStr());
                    dataObj.addProperty("size",model.getData().getSize());
                    publication.add("data",dataObj);
                    publication.addProperty("status",model.getStatusString());
                    publication.addProperty("results",model.getResult().toString());
                    publications.add(publication);
                }
                conObj.add("publications",publications);
                conferences.add(conObj);
            }
        }
        JsonObject allData = new JsonObject();
        allData.add("Students",students);
        allData.add("conferences",conferences);
        allData.addProperty("cpuTimeUsed", cluster.getCPUtimeUsed());
        allData.addProperty("gpuTimeUsed", cluster.getGPUtimeUsed());
        allData.addProperty("batchesProcessed",cluster.getNumBatchesProcessed());
        try {
            FileWriter file = new FileWriter(args[1]);
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(allData,file);
            file.flush();
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Program ended");


        Object output = JsonParser.parseReader(new FileReader(args[1]));

    }

}


