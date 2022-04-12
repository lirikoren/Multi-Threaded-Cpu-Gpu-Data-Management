package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.Future;
import bgu.spl.mics.application.objects.Model;

public class PublishResultsEvent implements Event<String> {
    private Model model;
    private Future<String> future;

    public PublishResultsEvent(Model model){
        this.model=model;
        future=null;
    }

    //public void Resolve(String string){
    //    future.resolve(string);
    //}
    public boolean is_model_goodResult(){
        if(model.is_result_good()) return true;
        else return false;
    }

    public boolean been_send(){
        if(future!=null) return true;
        else return false;
    }
    public Model getModel(){
        return model;
    }

    public void setFuture(Future<String> future) {
        this.future = future;
    }
}

