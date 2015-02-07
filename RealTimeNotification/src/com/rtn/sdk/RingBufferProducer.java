package com.rtn.sdk;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.json.JSONException;
import org.json.JSONObject;

import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.owlike.genson.Genson;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.ConsumerCancelledException;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.ShutdownSignalException;

/**
 * 
 * @author prithivirajdamodaran
 *
 */

public class RingBufferProducer
{

	private int ENTRIES;
	private String QUEUE_NAME;

	private final ExecutorService executorService;
	private final Disruptor<PushEntry> disruptor;
	private final RingBuffer<PushEntry> ringBuffer;
	ConnectionFactory factory;
	Connection connection;
	Channel channel ;
	QueueingConsumer consumer;
	ObjectInput in = null;
	Genson genson;
	RingBufferConsumer rbc; 
	Config config = Config.getConfig();

	public RingBufferProducer()
	{
		this.QUEUE_NAME = config.get("PUBLISHER_QUEUE");
		this.ENTRIES = Integer.parseInt(config.get("DISRUPTOR_ENTRIES"));
		rbc = new RingBufferConsumer();
		genson = new Genson();
		executorService = Executors.newCachedThreadPool();
		disruptor = new Disruptor<PushEntry>(PushEntry.FACTORY, ENTRIES, executorService);
		disruptor.handleEventsWith(rbc);
		disruptor.start();
		ringBuffer = disruptor.getRingBuffer();
		try {

			factory = new ConnectionFactory();
			String RabbitMQHost = config.get("RABBITMQ_HOST"); 
			String username = config.get("RABBITMQ_USER");
			String password = config.get("RABBITMQ_PASSWORD");
			factory.setHost(RabbitMQHost);
			factory.setUsername(username);
			factory.setPassword(password);
			connection = factory.newConnection();
			channel = connection.createChannel();
			channel.queueDeclare(QUEUE_NAME, false, false, false, null);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void produce(PublishRequest req){
		final long sequence = ringBuffer.next();
		final PushEntry pushentry = ringBuffer.get(sequence);
		pushentry.publishrequest = req;
		ringBuffer.publish(sequence);
	}

	public void readGPQueue(){


		try {
			System.out.println(" [*] Waiting for messages. To exit press CTRL+C");
			consumer= new QueueingConsumer(channel);
			channel.basicConsume(QUEUE_NAME, true, consumer);
		} catch (IOException e) {
			e.printStackTrace();
		}
		while (true) {
			QueueingConsumer.Delivery delivery;
			try {
				delivery = consumer.nextDelivery();
				byte[] message = delivery.getBody();

				PublishRequest req = json2Object(message);
				produce(req);

			} catch (ShutdownSignalException e) {
				e.printStackTrace();
			} catch (ConsumerCancelledException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}


	public PublishRequest json2Object(byte[] message){
		PublishRequest req= null; 
		req = genson.deserialize(message, PublishRequest.class); 
		return req;
	}


	public void stop()
	{
		disruptor.shutdown();
		executorService.shutdownNow();
		rbc.destroy();
	}
}
