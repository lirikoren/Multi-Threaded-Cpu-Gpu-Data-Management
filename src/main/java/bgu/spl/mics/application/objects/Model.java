package bgu.spl.mics.application.objects;


import org.junit.runner.Result;

/**
 * Passive object representing a Deep Learning model.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Model {
    private String name;//  name of the model.
    private Data data;// the data the model should train on.
    private Student student; //The student which created the model.
    private Status status;
    private Result result;
    private boolean isPublished;

    public enum Status{Pretrained,Training,Trained,Tested,Published}
    public enum Result{None,Good,Bad}

    public Model(String name, Data data, Student student){
        this.name=name;
        this.data=data;
        this.student=student;
        this.status= Status.Pretrained;
        this.result = Result.None;
        this.isPublished=false;
    }

    public Student getStudent(){
        return student;
    }

    public Data getData(){
        return data;
    }

    public Status getStatus(){
        return status;
    }
    public void setStatus(Status status){
        this.status=status;
    }

    public Result getResult(){
        return result;
    }
    public void setResult(Result result){
        this.result=result;
    }
    public String getName(){
        return name;
    }
    public Boolean is_result_good(){
        if(result==Result.Good)return true;
        else return false;
    }
    public String getStatusString(){
        if(status==Status.Pretrained)
            return "Pretrained";
        else if(status==Status.Training)
            return "Training";
        else if(status==Status.Trained)
            return "Trained";
        else return "Tested";
    }
    public void setPublished(){
        isPublished=true;
        status=Status.Published;
    }
    public boolean isPublished(){
        return isPublished;
    }
}
