package bgu.spl.mics.application.objects;

import java.util.LinkedList;

/**
 * Passive object representing information on a conference.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class ConfrenceInformation {

    private String name;
    private long date;
    private LinkedList<Model> good_result_Models_List;
    private long current_tick;

    public ConfrenceInformation(String name, long date) {
        this.name = name;
        this.date = date;
        good_result_Models_List = new LinkedList<>();
        this.current_tick = 0;
    }

    public String getName() {
        return name;
    }

    public long getDate() {
        return date;
    }

    public void Increase_CurrTick() {
        this.current_tick++;
    }


    public boolean isConf_timeToDie(){
        return(date==current_tick);
    }

    public void addToConfList(Model model){
        good_result_Models_List.add(model);
    }

    public LinkedList<Model> getGood_result_Models_List(){
        return good_result_Models_List;
    }
    public LinkedList<String> getGood_result_Model_names(){
        LinkedList<String> ls=new LinkedList();
        for (Model model:good_result_Models_List)
            ls.add(model.getName());
        return ls;
    }

    public String getGoodResults_String(){
        String str="";
        for(Model model: good_result_Models_List)
            str=str+model.getName()+" , ";
        if(!str.isEmpty())
            str=str.substring(0,str.length()-1);
        return str;
    }


}
