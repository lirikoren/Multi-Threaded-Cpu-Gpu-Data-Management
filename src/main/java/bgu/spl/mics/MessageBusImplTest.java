package bgu.spl.mics;

import bgu.spl.mics.example.messages.ExampleBroadcast;
import bgu.spl.mics.example.messages.ExampleEvent;
import bgu.spl.mics.example.services.ExampleBroadcastListenerService;
import bgu.spl.mics.example.services.ExampleEventHandlerService;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

class MessageBusImplTest {
    private MessageBusImpl messageBus;
    private MicroService microService;
    private ExampleEvent ev;
    private ExampleBroadcast eb;

    @Before
    void setUp() {
        messageBus = MessageBusImpl.getInstance();
    }

    @Test
    void subscribeEvent() {
        String[] arr = {"1"};
        Event event = new ExampleEvent("check");
        MicroService microService = new ExampleEventHandlerService("check", arr);
        if (messageBus.isSubscribedEvent(event, microService))
            throw new IllegalArgumentException("test failed: microservice is already subscribe");
        messageBus.subscribeEvent(ExampleEvent.class, microService);
        if (!messageBus.isSubscribedEvent(event, microService))
            throw new IllegalArgumentException("test failed:expect microservice is subscribe");
    }


    @Test
    void subscribeBroadcast() {
        String[] arr = {"1"};
        MicroService microService = new ExampleBroadcastListenerService("check", arr);
        Broadcast broadcast = new ExampleBroadcast("check");
        if (messageBus.isSubscribedBroad(broadcast, microService))
            throw new IllegalArgumentException("test failed: microservice is already subscribe");
        messageBus.subscribeBroadcast(ExampleBroadcast.class, microService);
        if (!messageBus.isSubscribedBroad(broadcast, microService))
            throw new IllegalArgumentException("test failed:expect microservice is subscribe");
    }


    @Test
    void complete() {
        Event ev = new ExampleEvent("Boaz");
        String[] arr = {"1"};
        MicroService microService = new ExampleBroadcastListenerService("check", arr);
        if (messageBus.getFuture(ev).equals("complete"))
            throw new IllegalArgumentException("test failed:expect future!= complete ");
        messageBus.complete(ev, "complete");
        if (!messageBus.getFuture(ev).equals("complete"))
            throw new IllegalArgumentException("test failed:expect future==complete ");

    }

    @Test
    void sendBroadcast() {
        String[] arr = {"1"};
        MicroService microService = new ExampleBroadcastListenerService("check", arr);
        messageBus.subscribeBroadcast(ExampleBroadcast.class, microService);
        Broadcast broadcast = new ExampleBroadcast("check");
        if (messageBus.isSubscribedBroad(broadcast, microService))
            throw new IllegalArgumentException("expect microservice to not be subscribe");
        messageBus.sendBroadcast(broadcast);
        if (!messageBus.isSubscribedBroad(broadcast, microService))
            throw new IllegalArgumentException("expect microservice to be subsribe");
    }

    @Test
    void sendEvent() {
        String[] arr = {"1"};
        MicroService microService = new ExampleEventHandlerService("check", arr);
        messageBus.subscribeEvent(ExampleEvent.class, microService);
        Event event = new ExampleEvent("check");
        if (messageBus.isEventSent(event))
            throw new IllegalArgumentException("expect event to not be sent");
        messageBus.sendEvent(event);
        if (!messageBus.isEventSent(event))
            throw new IllegalArgumentException("expect event to be sent");
    }

    @Test
    void register() {
        String[] arr = {"1"};
        MicroService microService = new ExampleBroadcastListenerService("check", arr);
        if (messageBus.isRegister(microService))
            throw new IllegalArgumentException("expect micro to not be register");
        messageBus.register(microService);
        if (!messageBus.isRegister(microService))
            throw new IllegalArgumentException("expect micro to be register");

    }

    @Test
    void unregister() {
        String[] arr = {"1"};
        MicroService microService = new ExampleBroadcastListenerService("check", arr);
        if (!messageBus.isRegister(microService))
            throw new IllegalArgumentException("expect micro to be register");
        messageBus.unregister(microService);
        if (messageBus.isRegister(microService))
            throw new IllegalArgumentException("expect micro to not be register");
    }

    @Test
    void awaitMessage() {
        String[] arr = {"1"};
        MicroService microService = new ExampleBroadcastListenerService("check", arr);
        messageBus.register(microService);
        ev = new ExampleEvent("check");
        if (messageBus.haveAwaitMessage(microService))
            throw new IllegalArgumentException("expect none await messages");
        messageBus.sendEvent(ev);
        if(!messageBus.haveAwaitMessage(microService))
            throw new IllegalArgumentException("expect await messages");
    }
}
