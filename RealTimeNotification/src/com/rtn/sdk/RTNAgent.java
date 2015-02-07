package com.rtn.sdk;



public class RTNAgent{

	static RingBufferProducer rbp;

	public static void main(String args[]){

		if(args[0].equalsIgnoreCase("start"))
			start();
		else
			stop();
	}

	public static void start(){
		rbp = new RingBufferProducer();
		rbp.readGPQueue();
	}
	public static void stop(){
		rbp.stop();

	}
}

