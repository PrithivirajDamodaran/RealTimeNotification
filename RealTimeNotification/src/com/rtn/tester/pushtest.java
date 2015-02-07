package com.rtn.tester;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.rtn.sdk.EventPubSub;
import com.rtn.sdk.GPMessage;
import com.rtn.sdk.PublishRequest;
import com.rtn.sdk.RingBufferProducer;

public class pushtest{

	static List<PublishRequest> data = new ArrayList<PublishRequest>();

	public static void main(String args[]){


		EventPubSub pubsub = new EventPubSub();
		pubsub.init();
		testData();
		

		long id = java.lang.Thread.currentThread( ).getId( );
		long startCPUTimeNano = getCpuTime( id);

		for (int x = 0; x < 20; x++)
			pubsub.Publish(data);
		
		long endCPUTimeNano = getCpuTime( id);
		System.out.print("Done");
		System.out.println((endCPUTimeNano-startCPUTimeNano) );
		pubsub.shutdown();

	}


	public static void testData(){
		
		GPMessage gpmessage = new GPMessage();
		gpmessage.EventOrigin = "MobilePush";
		gpmessage.EventID = "NewUpdates";
		gpmessage.payload = "test payload";
		String TargetTopic = "ab@yahoo.com";
		PublishRequest req = new PublishRequest();
		req.gpmessage = gpmessage;
		req.TargetTopic = TargetTopic;
		data.add(req);
	}

	/** Get CPU time in nanoseconds. */
	public static long getCpuTime( long ids ) {
		ThreadMXBean bean = ManagementFactory.getThreadMXBean( );
		if ( ! bean.isThreadCpuTimeSupported( ) )
			return 0L;
		long time = 0L;
		long t = bean.getThreadCpuTime( ids );
		if ( t != -1 )
			time += t;
		return time;
	}
}
