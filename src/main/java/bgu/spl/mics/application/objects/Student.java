package bgu.spl.mics.application.objects;

import java.util.LinkedList;

/**
 * Passive object representing single student.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Student {
    /**
     * Enum representing the Degree the student is studying for.
     */
    enum Degree {
        MSc, PhD
    }

    private String name;
    private String department;
    private Degree status;
    private int publications_num;
    private int papersRead_num;
    private LinkedList<Model> my_Models;

    public Student(String name,String department,String status){
        this.name=name;
        this.department=department;
        this.publications_num=0;
        this.papersRead_num=0;
        if(status.equals("MSc"))
            this.status=Degree.MSc;
        else if (status.equals("PhD"))
            this.status=Degree.PhD;
        else
            System.out.println("Illegal student status input");
        my_Models= new LinkedList<>();
    }
    public String getName(){
        return name;
    }
    public String getDepartment(){
        return department;
    }
    public Degree getStatus(){
        return status;
    }
    public String getStatusStr(){
        return status.toString();
    }
    public int getPublications_num(){
        return publications_num;
    }
    public int getPapersRead_num(){
        return papersRead_num;
    }
    public LinkedList<Model> getMy_Models(){return my_Models;}
    public void addModel(Model model) {
        my_Models.add(model);
    }

    public boolean is_model_mine(String name){
        boolean sol=false;
        for(Model m:my_Models){
            if (m.getName()==name) sol=true;
        }
        return sol;
    }
    public void Increase_PapersRead_num(){
        papersRead_num=papersRead_num+1;
    }

    public void Increase_publications_num(){
        publications_num=publications_num+1;
    }
    public Model getNextUntrainedModel(){
        for(Model m:my_Models)
            if(m.getStatus()== Model.Status.Pretrained)
                return m;
        return null;
    }


}
