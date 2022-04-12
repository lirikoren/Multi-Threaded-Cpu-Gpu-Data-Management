package bgu.spl.mics.application.services;

import bgu.spl.mics.Event;
import bgu.spl.mics.Message;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.TerminateAllBroadcast;
import bgu.spl.mics.application.messages.TestModleEvent;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.messages.TrainModleEvent;
import bgu.spl.mics.application.objects.Cluster;
import bgu.spl.mics.application.objects.GPU;
import bgu.spl.mics.application.objects.Model;

import java.util.HashMap;
import java.util.concurrent.CountDownLatch;


/**
 * GPU service is responsible for handling the
 * {@link TrainModelEvent} and {@link TestModelEvent},
 * in addition to sending the {@link DataPreProcessEvent}.
 * This class may not hold references for objects which it is not responsible for.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class GPUService extends MicroService {
    private GPU gpu;
    private CountDownLatch countDownLatch;
    private HashMap<Model, Event> model_EventMap;

    public GPUService(String name, String type, CountDownLatch countDownLatch) {
        super(name);
        this.gpu=new GPU(type, this);
        this.countDownLatch=countDownLatch;
        model_EventMap=new HashMap<>();
    }

    @Override
    protected void initialize() {
        countDownLatch.countDown();
        Cluster.getInstance().addGpu(gpu);
        subscribeBroadcast(TickBroadcast.class, ev-> gpu.addTick()); //subscribe tick broadcast

        subscribeEvent(TrainModleEvent.class,ev->{
            model_EventMap.put(ev.getModel(),ev);
            gpu.trainModel(ev.getModel());
        });

        subscribeEvent(TestModleEvent.class,ev->{
            model_EventMap.put(ev.getModel(),ev);
            gpu.testModel(ev.getModel());
            complete(ev,ev.getModel());
        });

        subscribeBroadcast(TerminateAllBroadcast.class,(TerminateAllBroadcast terminateAll)->terminate());
    }
    public void completeTrainEvent(Model model){
        super.complete(model_EventMap.get(model),model);
    }
}
