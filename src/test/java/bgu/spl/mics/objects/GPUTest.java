package bgu.spl.mics;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.objects.*;
import bgu.spl.mics.application.services.GPUService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


import java.util.LinkedList;

class GPUTest {/*
    private GPU gpu;
    private Cluster cluster;
    private Data data;
    private Student student;
    private Model model;
    private LinkedList<CPU>cpus;
    private LinkedList<GPU>gpus;
    private DataBatch batch;
    private GPUService GPUService;
    private int currTick;

    @Before
    void setUp() {
        student=new Student("yossi","CS","MSc");
        data=new Data("Images",100);
        model=new Model("model 1",data,student);
        gpu=new GPU("RTX2080", GPUService);
        batch = new DataBatch(data,0,gpu);
        currTick=0;
    }

    @After
    void tearDown() {
    }
    @Test
    void addTick(){
        if (currTick!=0) throw new IllegalArgumentException("failed test");
        gpu.addTick();
        if(currTick!=1) throw new IllegalArgumentException("failed test");

    }

    @Test
    void trainModel(){
        if(model.getStatus()!=Model.Status.Pretrained){ throw new IllegalArgumentException("failed test");}
        gpu.trainModel(model);
        if(model.getStatus()!=Model.Status.Trained){ throw new IllegalArgumentException("failed test");}
    }

    @Test
    void testModel(){
        if(model.getStatus()==Model.Status.Tested){ throw new IllegalArgumentException("failed test");}
        gpu.testModel(model);
        if(model.getStatus()!=Model.Status.Tested){ throw new IllegalArgumentException("failed test");}
    }*/
}
