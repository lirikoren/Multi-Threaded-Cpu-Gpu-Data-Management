package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;
import bgu.spl.mics.application.objects.Model;

import java.util.LinkedList;

public class PublishConferenceBroadcast implements Broadcast {
    private LinkedList<String> models_names;

    public PublishConferenceBroadcast(LinkedList<String> models_names){
        this.models_names=models_names;
    }

    public LinkedList<String> getModels_names(){return models_names;}
}
