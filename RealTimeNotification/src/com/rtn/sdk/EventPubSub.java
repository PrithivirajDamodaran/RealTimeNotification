package com.rtn.sdk;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.List;

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
	ByteArrayOutputStream bos = new ByteArrayOutputStream();
	ObjectOutput out = null;
	byte[] yourBytes = null;
	Genson genson;
	Config config = Config.getConfig();



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


		try {
			connection = factory.newConnection();
			channel = connection.createChannel();
			out = new ObjectOutputStream(bos);  
			//channel.confirmSelect();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void shutdown(){
		try {
			channel.close();
			connection.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void Publish(List<PublishRequest> publishrequests){
		try {

			channel.queueDeclare(QUEUE_NAME, false, false, false, null);
			for (int i = 0; i < publishrequests.size() ; i++) {

				//channel.basicPublish("", QUEUE_NAME, null, serializeMessage(publishrequests.get(i)));
				channel.basicPublish("", QUEUE_NAME, null, convertMessage(publishrequests.get(i)));
				//System.out.println("sent");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	public byte[] convertMessage(PublishRequest req){
		String json = genson.serialize(req);
		return json.getBytes();
	}

	public void Subscribe(){

	}

}
