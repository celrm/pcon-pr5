package ambos;

import java.util.concurrent.atomic.AtomicInteger;

public class LockTicket {
	
	private volatile int[] turn;
	private volatile AtomicInteger number;
	private volatile long next = 1;
	
	public LockTicket(){
		this.turn = new int[2];
		this.number = new AtomicInteger(1);
	}
	public void take(int id) {
		this.turn[id] = number.getAndAdd(1);
		while(turn[id]!= next);
	}
	
	public void release() {
		++this.next;
	}

}
