package bgu.spl.mics.application.services;

import bgu.spl.mics.Broadcast;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.TerminateAllBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.objects.CPU;
import bgu.spl.mics.application.objects.Cluster;

import java.util.concurrent.CountDownLatch;

/**
 * CPU service is responsible for handling the {@link DataPreProcessEvent}.
 * This class may not hold references for objects which it is not responsible for.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class CPUService extends MicroService {

    private CPU cpu;
    private CountDownLatch countDownLatch;

    public CPUService(String name,int cores, CountDownLatch countDownLatch) {
        super(name);
        cpu=new CPU(cores);
        this.countDownLatch=countDownLatch;
    }

    @Override
    protected void initialize() {
        countDownLatch.countDown();
        Cluster.getInstance().addCpu(cpu);

        subscribeBroadcast(TickBroadcast.class,(TickBroadcast tickBroadcast)->{
            cpu.IncreaseTick();
        });
        subscribeBroadcast(TerminateAllBroadcast.class,(TerminateAllBroadcast terminateAll)->terminate());
    }
}
