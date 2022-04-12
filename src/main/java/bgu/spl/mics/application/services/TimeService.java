package bgu.spl.mics.application.services;

import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.PublishConferenceBroadcast;
import bgu.spl.mics.application.messages.TerminateAllBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;

/**
 * TimeService is the global system timer There is only one instance of this micro-service.
 * It keeps track of the amount of ticks passed since initialization and notifies
 * all other micro-services about the current time tick using {@link TickBroadcast}.
 * This class may not hold references for objects which it is not responsible for.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class TimeService extends MicroService {
	private static TimeService me = null;
	private MessageBusImpl messageBus = MessageBusImpl.getInstance();
	private long TickTime,duration;
	private long currTick;
	private Timer timer;
	private CountDownLatch countDownLatch;


	public TimeService(long speed,long duration,CountDownLatch countDownLatch) {
		super("time");
		this.duration = duration;
		this.TickTime = speed;
		currTick= 1;
		timer = new Timer();
		this.countDownLatch=countDownLatch;
	}

	@Override
	protected void initialize(){
		countDownLatch.countDown();
		try {
			countDownLatch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				if(duration>=0){
					sendBroadcast(new TickBroadcast(currTick));
					currTick++;
					duration--;
				}
				else{
					sendBroadcast((new TerminateAllBroadcast()));
					timer.cancel();
				}
			}
		},new Date(),TickTime);
		subscribeBroadcast(TerminateAllBroadcast.class, (TerminateAllBroadcast terminateAllBroadcast) -> terminate());
	}
}






