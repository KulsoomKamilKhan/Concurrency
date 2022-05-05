


/*
 * Ten Pin Manager class
 * 
 * You must write code here so that this class satisfies the Coursework User Requirements (see CW specification on Canvas).
 * 
 * You may add private classes and methods to this file 
 * 
 * 
 * 
 ********************* IMPORTANT ******************
 * 
 * 1. You must implement TenPinManager using Java's ReentrantLock class and condition interface (as imported below).
 * 2. Other thread safe classes, e.g. java.util.concurrent MUST not be used by your TenPinManager class.
 * 3. Other thread scheduling classes and methods (e.g. Thread.sleep(timeout), ScheduledThreadPoolExecutor etc.) must not be used by your TenPinManager class..
 * 4. Busy waiting must not be used: specifically, when an instance of your TenPinManager is waiting for an event (i.e. a call to booklane() or playerLogin() ) it must not consume CPU cycles.
 * 5. No other code except that provided by you here (in by your TenPinManager.java file) will be used in the automatic marking.
 * 6. Your code must be reasonably responsive (e.g. no use of sleep methods etc.).
 * 
 * Failure to comply with the above will mean that your code will not be marked and you will receive a mark of 0.
 * 
 */


import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;



public class TenPinManager implements Manager{
	
	private ReentrantLock lock = new ReentrantLock();
	
	public ArrayList<Players> playerList = new ArrayList<Players>();	//ArrayList that contains bookersName and nPlayers
	
	final Condition playerQ  = lock.newCondition();
	
	public void bookLane(String bookersName, int nPlayers) {
		
		
		Players p = new Players(bookersName);
		p.setInPlayer(nPlayers);
		playerList.add(p);	//populating the ArrayList with booking details
		
		//UR4 
//		if(p.getPlayersBooked() != 0) 
//		{	lock.lock();
//			if (p.getPlayersBooked() >= lock.getWaitQueueLength(p.threadQ)+1 && playerList.size() > 0) //if number of players booked is
//			{																			//equal to the number of players logging in
//				p.threadQ.signalAll();
//			}
//		}
//		else {
//			lock.unlock();
//			playerList.add(p);
//		}

		
	
	}; 

	
	public void playerLogin(String bookersName) {
		
		Thread.currentThread().setName(bookersName);	//setting thread names for test file 
		
		lock.lock();
		
		Players pl = new Players(bookersName);	//instance of Player class to access private methods
	
		if(!playerList.isEmpty()) 	//if playersList is not empty, obtain booking details (bookersName, nPlayers)
		{
			for(int i=0; i<playerList.size(); i++) 
			{
				if(bookersName.equals(playerList.get(i).getBookers())) //if a booking exists with the given bookersName 	                              						
				{													//and is equal to the playerLogin's bookerName, return booking details
					pl = playerList.get(i);
					break;
				}	
			}
		}
		
		pl.setPlayer(pl.playerLoggingIn()+1);	//incrementing the number of times playerLogin is called

		try {
			if (pl.getPlayersBooked() == pl.playerLoggingIn() && playerList.size() > 0) //if number of players booked is 
			{																			//equal to the number of players logging in
				for(int i=0; i < pl.playerLoggingIn(); i++) 
				{
					pl.threadQ.signal(); //signal or release the threads 
										 //calling new lock.newCondition each time a new booker is booked for (Player Class instance)
				}
				if(playerList.size() > 0) //remove the booking from the list once the players are released or signalled
				{
					playerList.remove(0); //done in FIFO order
				}

			} else {
				
				pl.threadQ.await(); //if the number of players booked is not equal to the number of players logging in, await
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			lock.unlock();
		}

	}; 
	
	
	

	// You may add private classes and methods as below:
	
	
	//Private class and methods to populate ArrayList containing bookersName and nPlayers, and other details
	private class Players 
	{	
		String booker;
		int players;
		
		int playersLoggedIn = 0;
		
		final Condition threadQ  = lock.newCondition(); //calling new lock.newCondition each time a new booker is booked for
		
		private Players(String booker){	
			
			this.booker = booker;	
		}
		
		private int getPlayersBooked() //getting nPlayers or players booked
		{
			return players;
		}
		
		private int playerLoggingIn() //getting number of players logging in
		{
			return playersLoggedIn;
		}
		
		private String getBookers() //getting name of bookers
		{
			return booker;
		}
		
		private void setInPlayer(int players) //setting players booked 
		{
			this.players = players;
		}
		
		private void setPlayer(int players) //setting players logging in
		{
			this.playersLoggedIn = players;
		}
		 
		
	}

}
