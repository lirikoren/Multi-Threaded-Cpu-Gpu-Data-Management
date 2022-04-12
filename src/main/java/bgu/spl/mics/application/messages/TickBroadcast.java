package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;

public class TickBroadcast implements Broadcast {
    long currTick;

    public TickBroadcast(long currTick){
        this.currTick = currTick;
    }
    public long getCurrTick(){
        return currTick;
    }
}
