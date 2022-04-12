package bgu.spl.mics;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */

public class MessageBusImpl implements MessageBus {
	private static class MessageBusImplHolder {
		private static MessageBusImpl instance = new MessageBusImpl();
	}

	private final HashMap<Class, Queue<MicroService>> message_subsribers_map;
	private final HashMap<MicroService, Queue<Message>> microservice_queue_map;
	private final HashMap<Message, Future> message_future_map;
	private final Object subscribing_Lock = new Object();
	private final Object sending_Lock = new Object();

	private MessageBusImpl() {
		message_subsribers_map = new HashMap<>();
		microservice_queue_map = new HashMap<>();
		message_future_map = new HashMap<>();
	}

	public static MessageBusImpl getInstance() {
		return MessageBusImplHolder.instance;
	}

	/**
	 *
	 * @param type The type to subscribe to,
	 * @param m    The subscribing micro-service.
	 * @param <T>
	 * @post m subscribe to type
	 */
	@Override
	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
		synchronized (subscribing_Lock) {
			if (!message_subsribers_map.containsKey(type)) {
				message_subsribers_map.put(type, new ArrayDeque<>());
			}
			message_subsribers_map.get(type).add(m);

		}
	}

	/**
	 *
	 * @param m
	 * @return boolean
	 */
	public synchronized boolean isRegister(MicroService m){
		if (m == null) {
			return false;
		}
		return this.message_subsribers_map.containsKey(m);
	}

	/**
	 *
	 * @param type 	The type to subscribe to.
	 * @param m    	The subscribing micro-service.
	 * @post m is subscribe to broadcast
	 */
	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		synchronized (subscribing_Lock) {
			if (!message_subsribers_map.containsKey(type)) {
				message_subsribers_map.put(type, new ArrayDeque<>());
			}
			message_subsribers_map.get(type).add(m);
		}
	}


	/**
	 *
	 * @param e      The completed event.
	 * @param result The resolved result of the completed event.
	 * @param <T>
	 * @post resolve linked future
	 */
		@Override
	public <T> void complete(Event<T> e, T result) {
		Future f = message_future_map.get(e);
		f.resolve(result);
	}

	public synchronized boolean isSubscribedBroad(Broadcast b, MicroService m){
			return (microservice_queue_map.get(m).contains(b));
	}
	public synchronized boolean isSubscribedEvent(Event e, MicroService m){
			return (microservice_queue_map.get(m).contains(e));
	}

	public  synchronized Future<Message> getFuture(Event<Message> e){
			return message_future_map.get(e);
	}
	/**
	 *
	 * @param b 	The message to added to the queues.
	 * @post b.been_sent()==true;
	 */
	@Override
	public void sendBroadcast(Broadcast b) {
		synchronized (sending_Lock) {
			if (message_subsribers_map.get(b.getClass()) != null && !message_subsribers_map.get(b.getClass()).isEmpty()) {
				for (MicroService m : message_subsribers_map.get(b.getClass())) {
					if (microservice_queue_map.get(m) != null) {
						microservice_queue_map.get(m).add(b);
					}
				}
				synchronized (this) {
					notifyAll();
				}
			}
		}
	}

	/**
	 *
	 * @param e     	The event to add to the queue.
	 * @param <T>
	 * @return
	 * @post e.been_send()==true
	 */
	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
		synchronized (message_subsribers_map) {
			try {
				Future<T> future = new Future<>();
				if (message_subsribers_map.get(e.getClass()) == null || message_subsribers_map.isEmpty()) {
					future.resolve(null);
					return future;
				}
				message_future_map.put(e, future);
				Queue<MicroService> subscribers = message_subsribers_map.get(e.getClass());
				MicroService chosenMicroService = subscribers.remove();
				microservice_queue_map.get(chosenMicroService).add(e);
				subscribers.add(chosenMicroService);
				return future;
			} catch (Exception exception) {
				exception.printStackTrace();
			}
			return null;
		}
	}

	public synchronized boolean isEventSent(Event e){
		return(message_future_map.containsKey(e));
	}

	/**
	 *
	 * @param m the micro-service to create a queue for.
	 * @pre isRegister(m) == false
	 * @post isRegister(m)==true
	 */
	@Override
	public void register(MicroService m) {
		synchronized (sending_Lock) {
			if (microservice_queue_map.get(m) == null) {
				microservice_queue_map.put(m, new ArrayDeque<Message>());
			}
		}
	}

	/**
	 *
	 * @param m the micro-service to unregister.
	 * @pre isRegister(m)==true
	 * @post isRegister(m)==false
	 */
	@Override
	public void unregister(MicroService m) {
		synchronized (sending_Lock) {
			for (Class c : message_subsribers_map.keySet()) {
				message_subsribers_map.get(c).remove(m);
			}
			microservice_queue_map.remove(m);
		}
	}

	/**
	 *
	 * @param m The micro-service requesting to take a message from its message
	 *          queue.
	 * @return
	 * @throws InterruptedException
	 * @pre isRegister(m)==true
	 */
	@Override
	public synchronized Message awaitMessage(MicroService m) throws InterruptedException {
		while (microservice_queue_map.get(m) == null || microservice_queue_map.get(m).isEmpty()) {
			wait();
		}
		notifyAll();
		return microservice_queue_map.get(m).poll();
	}

	public synchronized boolean haveAwaitMessage(MicroService m){
		return (!microservice_queue_map.get(m).isEmpty());
	}


}

