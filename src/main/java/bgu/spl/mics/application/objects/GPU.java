package bgu.spl.mics.application.objects;

import bgu.spl.mics.application.services.ConferenceService;
import bgu.spl.mics.application.services.GPUService;

import java.util.*;

/**
 * Passive object representing a single GPU.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class GPU {
    /**
     * Enum representing the type of the GPU.
     */
    enum Type {RTX3090, RTX2080, GTX1080}
    //fields
    private GPUService gpuService;
    private Cluster cluster;
    private Model currModel;
    private int VRAM_size;
    private int currVRAMSpace;
    private int ticksNeededToTrainNextBatch;
    private int ticksPerBatch;
    private long numTrainedBatches;

    private Queue<DataBatch> unproccessed_Queue;
    private Queue<DataBatch> proccessed_Queue;
    private List<Model> model_to_proccess_List;

    public GPU(String type,GPUService gpuService){
        this.gpuService=gpuService;
        currModel=null;
        if(type.equals( "RTX3090")){
            VRAM_size = 32;
            ticksPerBatch=1;}
        else if(type.equals( "RTX2080")){
            VRAM_size = 16;
            ticksPerBatch=2;}
        else if(type.equals("GTX1080")){
            VRAM_size = 8;
            ticksPerBatch=4;
        }else System.out.println("Illegal GPU type input");
        currVRAMSpace=VRAM_size;
        this.cluster=Cluster.getInstance();
        this.unproccessed_Queue=new ArrayDeque<>();
        model_to_proccess_List=new LinkedList<>();
        numTrainedBatches=0;
        proccessed_Queue=new ArrayDeque<>();
    }

    public void addTick() {
        getNextProcessedBatch(); //tries to fetch processed batch from cluster
        if (proccessed_Queue.size() > 0) {
            ticksNeededToTrainNextBatch--;
            cluster.increaseGPUtimeUnit();
            if (ticksNeededToTrainNextBatch == 0) {
                proccessed_Queue.poll(); //batch is trained, removes trained batch
                numTrainedBatches++;
                currVRAMSpace++;
                ticksNeededToTrainNextBatch = ticksPerBatch;
                if (proccessed_Queue.isEmpty() && numTrainedBatches == currModel.getData().getNumDataBatches()) {
                    //Model training finished
                    cluster.addTrainedModelName(currModel.getName());
                    currModel.setStatus(Model.Status.Trained);
                    gpuService.completeTrainEvent(currModel);
                    model_to_proccess_List.remove(currModel);
                    if (!model_to_proccess_List.isEmpty()) {  //starts training next model in line
                        currModel = model_to_proccess_List.get(0);
                        startTrainingCurrModel();
                    }
                } else { //model not finished training yet
                    if (!unproccessed_Queue.isEmpty()) {
                        cluster.add_to_unprocessedBatches(unproccessed_Queue.poll());
                        currVRAMSpace--;
                    }
                }
            }
        }
    }
    public void startTrainingCurrModel(){
        numTrainedBatches=0;
        for (int i = 0; i < currModel.getData().getNumDataBatches(); i++) { //create batches
            unproccessed_Queue.add(new DataBatch(currModel.getData(), i, this));
        }
        for (int i = 0; i < VRAM_size && !unproccessed_Queue.isEmpty(); i++) { //starts sending batches to cluster
                cluster.add_to_unprocessedBatches(unproccessed_Queue.poll());
                currVRAMSpace--;
        }
    }
    public int getVRAM_size(){
        return VRAM_size;
    }

    public void trainModel(Model model){
        if(model==null||model.getStatus()!= Model.Status.Pretrained||this.model_to_proccess_List.contains(model)){
            throw new IllegalArgumentException("the model is null or already trained or already in the modleList");
        }
        model.setStatus(Model.Status.Training);
        model_to_proccess_List.add(model);
        if(model_to_proccess_List.size()==1) {
            currModel=model;
            startTrainingCurrModel();
        }
    }

    public void testModel(Model model) throws IllegalArgumentException{
        if(model!=null && model.getStatus()== Model.Status.Trained){
            double good_result_probability;
            if(model.getStudent().getStatus() == Student.Degree.PhD){ good_result_probability=0.8;}
            else if(model.getStudent().getStatus()== Student.Degree.MSc){good_result_probability=0.6;}
            else throw new IllegalArgumentException("Student does'nt have a valid degree");

            if(Math.random()<good_result_probability){
                model.setResult(Model.Result.Good);

            }
            else{model.setResult(Model.Result.Bad);}
            model.setStatus(Model.Status.Tested);
            //System.out.print("Model "+model.getName()+" tested, result:");
//            if(model.getResult()==Model.Result.Good) System.out.println("good");
//            else if(model.getResult()==Model.Result.Bad) System.out.println("bad");
        }
        else {
            System.out.println("Error: "+model.getStatusString());
            throw new IllegalArgumentException("model is null or not trained yet");
        }
    }

    public void getNextProcessedBatch(){
        DataBatch batch=cluster.getNextProccessedBatch(this);
        if(batch!=null) {
            proccessed_Queue.add(batch);
            if (proccessed_Queue.size() > getVRAM_size()) {
                throw new RuntimeException("GPU VRAM overflow");
            }
            if(proccessed_Queue.size()==1)
                ticksNeededToTrainNextBatch = ticksPerBatch;
        }
    }
}

