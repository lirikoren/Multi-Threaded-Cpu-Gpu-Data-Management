package bgu.spl.mics.application.objects;

/**
 * Passive object representing a single CPU.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class CPU {
    private int coresNum;
    private Cluster cluster;
    private int currTick;
    private int ticksNeeded;
    private boolean isProcessing;
    private DataBatch batch;
    public CPU(int c) {
        coresNum = c;
        cluster=Cluster.getInstance();
        isProcessing=false;
        batch=null;
        ticksNeeded=0;
    }


    public void processNextBatch(){
        batch=cluster.sendUnprocessedBatchToCPU();
        if (batch!=null) {
            ticksNeeded = getTicksNeeded(batch);
            isProcessing = true;
        }
    }

    /**
     *
     * @param batch
     * @return num of tick it take to process
     */
    public int getTicksNeeded(DataBatch batch){
        //Calculates number of ticks a batch takes to process
        //  Images- (32/this.nCores) * 4 ticks
        //  Text  - (32/this.nCores) * 2 ticks
        //  Table - (32/this.nCores) * 1 ticks
        int ticks=0;
        if (batch.getDataType() == Data.Type.Images) {
            ticks= (32 / coresNum) * 4;
        } else if (batch.getDataType() == Data.Type.Text) {
            ticks= (32 / coresNum) * 2;
        } else if (batch.getDataType() == Data.Type.Tabular) {
            ticks= (32 / coresNum);
        }
        return ticks;
    }
    public void IncreaseTick() {
        ticksNeeded--;
        if (!isProcessing) {
            processNextBatch();
        } else {
            cluster.increaseCPUtimeUnit();
            if (ticksNeeded == 0) { //finished processing
                batch.setProcessed();
                isProcessing = false;
                cluster.doneProcessingBatch(batch);
                processNextBatch();
            }
        }
    }
    public long get_Total_time() {
        return 0;
    }
}
