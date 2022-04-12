package bgu.spl.mics.application.objects;


import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Passive object representing the cluster.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Cluster {

	private static class ClusterlHolder {
		private static Cluster instance = new Cluster();
	}

	/**
	 * Retrieves the single instance of this class.
	 */
	private LinkedList<GPU> gpuList;
	private LinkedList<CPU> cpuList;
	private HashMap<GPU, Queue<DataBatch>> GPU_ProcessedDataBatch_map;
	private Queue<DataBatch> unprocessedBatches;
	//statistics
	private AtomicInteger numBatchesProcessed;
	private AtomicInteger CPUtimeUnitsUsed;
	private AtomicInteger GPUtimeUnitsUsed;
	private LinkedList<String> modelNamesTrained;

	Object lock;

	private Cluster() {
		gpuList = new LinkedList<>();
		cpuList = new LinkedList<>();
		GPU_ProcessedDataBatch_map = new HashMap<>();
		unprocessedBatches = new ArrayDeque<>();
		numBatchesProcessed = new AtomicInteger(0);
		CPUtimeUnitsUsed = new AtomicInteger(0);
		GPUtimeUnitsUsed = new AtomicInteger(0);
		modelNamesTrained = new LinkedList<>();
		lock = new Object();
	}

	public static Cluster getInstance() {
		return ClusterlHolder.instance;
	}

	public synchronized void addCpu(CPU cpu) {
		if (cpu != null) {
			this.cpuList.add(cpu);
		}
	}

	public synchronized void addGpu(GPU gpu) {
		if (gpu != null) {
			this.gpuList.add(gpu);
			Queue<DataBatch> queue = new ArrayDeque<>();
			GPU_ProcessedDataBatch_map.put(gpu, queue);
		}
	}


	public void doneProcessingBatch(DataBatch batch) {
		numBatchesProcessed.incrementAndGet();
		synchronized (lock) {
			GPU_ProcessedDataBatch_map.get(batch.getGPU()).add(batch);
		}
		//notifyAll();
	}


	public void add_to_unprocessedBatches(DataBatch dataBatch) throws IllegalArgumentException {
		synchronized (lock) {
			if (dataBatch == null || dataBatch.isProccessed()) {
				throw new IllegalArgumentException("dataBatch is null or already processed");
			}
			unprocessedBatches.add(dataBatch);
		}
	}

	public DataBatch sendUnprocessedBatchToCPU() {
		synchronized (lock) {
			if (!unprocessedBatches.isEmpty()) {
				return unprocessedBatches.poll();
			}
		}
		return null;
	}

	public int getNumOfBatchesProcessedByCPUs() {
		return numBatchesProcessed.get();
	}

	public void addTrainedModelName(String name) {
		modelNamesTrained.add(name);
	}

	public void increaseCPUtimeUnit() {
		CPUtimeUnitsUsed.incrementAndGet();
	}

	public void increaseGPUtimeUnit() {
		GPUtimeUnitsUsed.incrementAndGet();
	}

	public DataBatch getNextProccessedBatch(GPU gpu) {
		synchronized (lock) {
			if (GPU_ProcessedDataBatch_map.get(gpu).size() > 0) {
				return GPU_ProcessedDataBatch_map.get(gpu).poll();
			} else return null;
		}
	}
	public int getCPUtimeUsed(){
		return CPUtimeUnitsUsed.get();
	}
	public int getGPUtimeUsed(){
		return GPUtimeUnitsUsed.get();
	}
	public int getNumBatchesProcessed(){
		return numBatchesProcessed.get();
	}
}

