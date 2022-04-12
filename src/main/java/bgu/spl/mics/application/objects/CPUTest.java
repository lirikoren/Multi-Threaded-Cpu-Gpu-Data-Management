package bgu.spl.mics.application.objects;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

class CPUTest {

    private CPU cpu;
    private int currTick;

    @Before
    void setUp() {
        cpu = new CPU(4);
        currTick=0;
    }


    @Test
    void IncreaseTick(){
        assertFalse(currTick!=0);
        cpu.IncreaseTick();
        assertTrue(currTick==1);
    }
}