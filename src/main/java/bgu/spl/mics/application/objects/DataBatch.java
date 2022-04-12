package bgu.spl.mics.application.objects;

/**
 * Passive object representing a data used by a model.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */

public class DataBatch {
    private Data data;
    private int startIndex;
    private boolean isProccessed;
    private GPU gpu; //responsible GPU

    public DataBatch(Data data, int startIndex, GPU gpu) {
        this.data = data;
        this.startIndex=startIndex;
        isProccessed=false;
        this.gpu=gpu;
    }

    public boolean isCPUDone() {
        return false;
    }   //they gave us

    public Data.Type getDataType(){
        return data.getType();
    }
    public void setProcessed(){
        isProccessed=true;
    }
    public GPU getGPU(){
        return gpu;
    }

    public boolean isProccessed(){
        return isProccessed;
    }
}
