package com.rtn.tester;

import java.util.ArrayList;

import java.util.List;
import com.rtn.sdk.EventPubSub;
import com.rtn.sdk.SubscribeRequest;

public class subscribetest{
	
	static List<SubscribeRequest> data = new ArrayList<SubscribeRequest>();

	public static void main(String args[]){
		EventPubSub pubsub = new EventPubSub();
		pubsub.init();
		testData();
		pubsub.Subscribe(data);
		pubsub.shutdown();
	}
	
	public static void testData(){
		
		SubscribeRequest req = new SubscribeRequest(); 
		req.EventOrigin = "MobilePush";
		req.EventID= "NewUpdates";
		req.SubscriptionType="PushNotification";
		req.TargetTopic="subscribe@yahoo.com";
		data.add(req);
	}

}