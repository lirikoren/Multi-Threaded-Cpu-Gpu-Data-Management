package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.Future;
import bgu.spl.mics.application.objects.Model;
import bgu.spl.mics.application.objects.Student;

public class TrainModleEvent implements Event<Model> {

    private Model model;
    private Future<Model> future;
    private Student student;

    public TrainModleEvent(Model model, Student student) {
        this.model = model;
        future=null;
        this.student=student;

    }
    public Model getModel() {
        return this.model;
    }

    public void Resolve(Model model){
        this.model=model;
        future.resolve(model);
        this.notifyAll();
    }

    public boolean been_send(){return future!=null;}
    public boolean is_resolved(){return future.isDone();}
    public void setFuture(Future<Model> future){this.future=future;}

}

