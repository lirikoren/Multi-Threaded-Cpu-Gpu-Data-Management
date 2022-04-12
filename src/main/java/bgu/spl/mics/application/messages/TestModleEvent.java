package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.Future;
import bgu.spl.mics.application.objects.Model;
import bgu.spl.mics.application.objects.Student;

public class TestModleEvent implements Event<Model> {
    private Model model;
    //private Student student;
    private Future<Model> future;

    public TestModleEvent(Model model,Student student) {
        this.model = model;
        //this.student = student;
        future=null;
    }
    public boolean been_send() { return future!=null;}
    public void setFuture(Future<Model> future){this.future=future;}
    public boolean is_resolved(){return future.isDone();}
    //public Student getStudent() {
    //    return this.student;
    //}

    public Model getModel() {
        return this.model;
    }

    //public void Resolve(Model model){
    //    future.resolve(model);
    //    notifyAll();
    //}

}