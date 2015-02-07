package com.rtn.sdk;

import java.io.IOException;
import java.io.ObjectOutput;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;

import com.owlike.genson.Genson;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;



public class EventPubSub{

	/**
	 * @author prithivirajdamodaran
	 * This is the entry point for applications using this library, applications can either publish or subscribe
	 * to events. PubSub is a high level EAI pattern all the "Event Notification" Systems use. Mobile Push Notification
	 * is a Distributed PubSub. Mobile devices are subscribers and your application notifies one or more of them
	 * when an Event of interest occurs.
	 * 
	 *    
	 */
	String QUEUE_NAME;
	Connection connection;
	ConnectionFactory factory;
	Channel channel;
	ObjectOutput out = null;
	byte[] yourBytes = null;
	Genson genson;
	String ESCLUSTER;
	Config config = Config.getConfig();
	TransportClient client;



	public void init(){


		QUEUE_NAME = config.get("PUBLISHER_QUEUE");
		String RabbitMQHost = config.get("RABBITMQ_HOST"); 
		String username = config.get("RABBITMQ_USER");
		String password = config.get("RABBITMQ_PASSWORD");
		factory = new ConnectionFactory();
		factory.setHost(RabbitMQHost); 
		factory.setUsername(username);
		factory.setPassword(password);
		genson = new Genson();
		ESCLUSTER = config.get("ELASTICHSEARCH_CLUSTER");
		



		try {
			connection = factory.newConnection();
			channel = connection.createChannel();
			//channel.confirmSelect();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void shutdown(){
		try {
			channel.close();
			connection.close();

			
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void Publish(List<PublishRequest> publishrequests){
		try {
			channel.queueDeclare(QUEUE_NAME, false, false, false, null);
			for (int i = 0; i < publishrequests.size() ; i++) {
				channel.basicPublish("", QUEUE_NAME, null, convertMessage(publishrequests.get(i)));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	public byte[] convertMessage(PublishRequest req){
		String json = genson.serialize(req);
		return json.getBytes();
	}

	public void Subscribe(List<SubscribeRequest> subscribeRequests){

		Settings settings = ImmutableSettings.settingsBuilder().put("cluster.name", ESCLUSTER).build();
		client = new TransportClient(settings);
		client = client.addTransportAddress(new InetSocketTransportAddress("localhost", 9300));			
	
		Map<String, Object> subscription = new HashMap<String, Object>();
		for (int i = 0; i < subscribeRequests.size(); i++){
			subscription.put("eventorigin", subscribeRequests.get(i).EventOrigin);
			subscription.put("eventid", subscribeRequests.get(i).EventID);
			subscription.put("stype", subscribeRequests.get(i).SubscriptionType);
			subscription.put("targettopic", subscribeRequests.get(i).TargetTopic);
			subscription.put("status", "A");
			IndexResponse ir = client.prepareIndex("rtn", "subscriptions")
					.setSource(subscription)
					.execute().actionGet();
			System.out.print(ir.getId());
		}
		client.close();
	}

}
