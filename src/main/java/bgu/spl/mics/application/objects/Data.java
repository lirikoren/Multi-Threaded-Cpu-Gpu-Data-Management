package bgu.spl.mics.application.objects;

/**
 * Passive object representing a data used by a model.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Data {
    /**
     * Enum representing the Data type.
     */
    enum Type {
        Images, Text, Tabular
    }
    private String name;
    private Type type;
    private boolean isProcessed;
    private long size;

    private long num_of_dataBatches;
    private long num_of_trained_dataBatches;

    public Data (String type, long size){
        if(type.equals("Images")||type.equals("images"))
            this.type=Type.Images;
        else if(type.equals("Text")||type.equals("text"))
            this.type=Type.Text;
        else if(type.equals("Tabular")||type.equals("tabular"))
            this.type=Type.Tabular;
        else System.out.println("Illegal data type input");
        this.size=size;
        isProcessed=false;
        num_of_dataBatches=0;
        num_of_trained_dataBatches=0;

    }

    public void split_to_dataBatches(GPU gpu) throws IllegalArgumentException{
        if(gpu==null||isProcessed==true){
            throw new IllegalArgumentException("gpu==null or data is already processed");
        }
        num_of_dataBatches=size/1000;
    }

    public void train_dataBatch(){
        num_of_trained_dataBatches++;
    }

    public boolean is_trained() {
        if (num_of_trained_dataBatches == num_of_dataBatches) {
            return true;
        } else {
            return false;
        }
    }
    public Type getType(){
        return type;
    }
    public String getTypeStr(){
        return type.toString();
    }
    public long getSize(){
        return size;
    }
    public long getNumDataBatches(){
        return size/1000;
    }

}
