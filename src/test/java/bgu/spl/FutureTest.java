package bgu.spl.mics;


import bgu.spl.mics.Future;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

class FutureTest {/*
    private static Future<String> future;
    @Before
    void setUp() {
        future=new Future<>();
    }

    @After
    void tearDown() {
    }

    @Test
    void get() {
        assertNull(future.get());
        future.resolve("future resolved");
        assertEquals("future resolved",future.get());
    }

    @Test
    void resolve() {
        future.resolve("future resolved");
        assertTrue(future.isDone());
        assertEquals("future resolved", future.get());
    }

    @Test
    void isDone() {
        assertFalse(future.isDone());
        future.resolve("future resolved");
        assertTrue(future.isDone());
    }

    @Test
    void testGet() {
        assertNull(future.get(10, TimeUnit.MICROSECONDS));
        final String[] s1 = new String[1];
        final String[] s2 = new String[1];
        Thread wait=new Thread(()->{
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        Thread get=new Thread(()->{
            s1[0] =future.get();
            s2[0] = future.get(50,TimeUnit.MICROSECONDS);
        });
        wait.start();
        get.start();
        assertNull(s1[0]);
        assertNull(s2[0]);
        assertEquals(s1[0],s2[0]);
    }*/
}
