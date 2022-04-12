package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.PublishConferenceBroadcast;
import bgu.spl.mics.application.messages.PublishResultsEvent;
import bgu.spl.mics.application.messages.TerminateAllBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.objects.ConfrenceInformation;

import java.util.concurrent.CountDownLatch;

/**
 * Conference service is in charge of
 * aggregating good results and publishing them via the {@link PublishConfrenceBroadcast},
 * after publishing results the conference will unregister from the system.
 * This class may not hold references for objects which it is not responsible for.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class ConferenceService extends MicroService {
    private ConfrenceInformation confrenceInformation;
    private CountDownLatch countDownLatch;
    //private long current_tick;
    //private LinkedList<String> good_models_names_list;

    public ConferenceService(String name, long date, CountDownLatch countDownLatch) {

        super(name);
        this.confrenceInformation = new ConfrenceInformation(name, date);
        this.countDownLatch = countDownLatch;
        //good_models_names_list=new LinkedList<>();
    }

    @Override
    protected void initialize() {
        countDownLatch.countDown();
        subscribeBroadcast(TickBroadcast.class, (TickBroadcast tickBroasdcast) -> {
            confrenceInformation.Increase_CurrTick();
            if (confrenceInformation.isConf_timeToDie()) {
                PublishConferenceBroadcast confBroadcast = new PublishConferenceBroadcast(confrenceInformation.getGood_result_Model_names());
                sendBroadcast(confBroadcast);
                terminate();
            }
        });
        subscribeBroadcast(TerminateAllBroadcast.class, (TerminateAllBroadcast terminateAll) -> {
            terminate();
        });
        subscribeEvent(PublishResultsEvent.class, (PublishResultsEvent publishEvent) -> {
            if (publishEvent.is_model_goodResult()) {
                confrenceInformation.addToConfList(publishEvent.getModel());
                publishEvent.getModel().setPublished();
            }
            complete(publishEvent, publishEvent.getModel().getName());
        });
    }
    public ConfrenceInformation getConferenceInfo() {
        return confrenceInformation;
    }
}
