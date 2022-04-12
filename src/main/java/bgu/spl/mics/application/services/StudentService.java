package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.objects.Data;
import bgu.spl.mics.application.objects.Model;
import bgu.spl.mics.application.objects.Student;

import java.util.concurrent.CountDownLatch;


/**
 * Student is responsible for sending the {@link TrainModelEvent},
 * {@link TestModelEvent} and {@link PublishResultsEvent}.
 * In addition, it must sign up for the conference publication broadcasts.
 * This class may not hold references for objects which it is not responsible for.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class StudentService extends MicroService {
    private Student student;
    private CountDownLatch countDownLatch;
    private Future<Model> modelFuture;
    private Future<String> stringFuture;
    //private MessageBus messageBus= MessageBusImpl.getInstance();

    public StudentService(String name, Student student, CountDownLatch countDownLatch) {
        super(name);
        this.student = student;
        this.countDownLatch = countDownLatch;
        modelFuture = null;
        stringFuture = null;
    }

    @Override
    protected void initialize() {
        countDownLatch.countDown();
        subscribeBroadcast(PublishConferenceBroadcast.class, (PublishConferenceBroadcast publishConf) -> {
            for (String name : publishConf.getModels_names()) {
                if (student.is_model_mine(name)) student.Increase_publications_num();
                else student.Increase_PapersRead_num();
            }
        });
        subscribeBroadcast(TerminateAllBroadcast.class, (TerminateAllBroadcast terminateAll) -> terminate());

        subscribeBroadcast(TickBroadcast.class, ev-> {
            if (modelFuture == null && stringFuture == null) { //model is untrained
                if (student.getNextUntrainedModel() != null) {
                    modelFuture = sendEvent(new TrainModleEvent(student.getNextUntrainedModel(), student));
                }
            } else if (stringFuture == null && modelFuture != null && modelFuture.isDone()) { //model was testing or training, and now its done
                if (modelFuture.get().getStatus() == Model.Status.Trained)
                    modelFuture = sendEvent(new TestModleEvent(modelFuture.get(), student));
                else if (modelFuture.get().getStatus() == Model.Status.Tested)
                    stringFuture = sendEvent(new PublishResultsEvent(modelFuture.get()));
            } else if (stringFuture != null && stringFuture.isDone()) { //model was done testing
                modelFuture = null;
                stringFuture = null;
            }
        });
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    public Student getStudent(){
        return student;
    }
}
