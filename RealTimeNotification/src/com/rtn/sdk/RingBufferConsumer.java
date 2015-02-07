package com.rtn.sdk;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;

import com.lmax.disruptor.EventHandler;
import com.rtn.notifiers.MQTTNotifier;

/**
 * 
 * @author prithivirajdamodaran
 *
 */
public class RingBufferConsumer implements EventHandler<PushEntry>
{

	TransportClient transportClient;
	MQTTNotifier mqttnotifier;
	final static Logger logger = Logger.getLogger(RingBufferConsumer.class);
	Config config = Config.getConfig();


	public RingBufferConsumer(){
		String ESCLUSTER= config.get("ELASTICHSEARCH_CLUSTER");
		Settings settings = ImmutableSettings.settingsBuilder().put("cluster.name", ESCLUSTER).build();
		transportClient = new TransportClient(settings);
		transportClient = transportClient.addTransportAddress(new InetSocketTransportAddress("localhost", 9300));
		mqttnotifier = new MQTTNotifier();
	}

	public void destroy(){
		transportClient.close();
		mqttnotifier.disconnect();
	}



	public void onEvent(final PushEntry pushentry, final long sequence, final boolean endOfBatch) throws Exception {

		// Unpack pushentry get the EventSource/EventID/ and get the publish type (Unicast/Multicast/Broadcast)


		PublishRequest req = pushentry.publishrequest;
		String EventOrigin= req.gpmessage.EventOrigin;
		String EventID = req.gpmessage.EventID;
		String payload = req.gpmessage.payload;
		String Topic = req.TargetTopic;

		// Broadcast - Get all active Subscriptions for this EventOrigin/EventID and send it to all.

		if("*".equalsIgnoreCase(Topic)){

			// Read the Subscriptions data-store for Active Subscriptions - SubscriberIDs are nothing but target MQTT topics 
			List<String> subscriptions = getSusbcriptions(EventOrigin, EventID);

			for (int i=0; i< subscriptions.size(); i++){
				mqttnotifier.Notify(subscriptions.get(i), payload);
				if(logger.isDebugEnabled()){
					logger.debug("Sending message " + payload + " for " + subscriptions.get(i));
				}
			}
		}

		// Unicast and Multicast falls here - Directly take the Targettopic and send the message to Targettopic.

		else{
			mqttnotifier.Notify(Topic, payload);
			if(logger.isDebugEnabled()){
				logger.debug("Sending message " + payload + " for " + Topic);
			}
		}
	}

	public List<String> getSusbcriptions(String EventOrigin, String EventID){

		List<String> subscriptions = new ArrayList<String>();
		Map<String, Object> source =  new HashMap<String, Object>();
		QueryBuilder qb = QueryBuilders
				.boolQuery()
				.must(QueryBuilders.termQuery("eventorigin", EventOrigin.toLowerCase()))
				.must(QueryBuilders.termQuery("eventid", EventID.toLowerCase()))
				.must(QueryBuilders.termQuery("status", "A".toLowerCase()))
				.must(QueryBuilders.termQuery("stype", "PushNotification".toLowerCase()));


		SearchResponse response = transportClient.prepareSearch("rtn")
				.setTypes("subscriptions")
				.setQuery(qb)
				.execute()
				.actionGet();




		SearchHit[] searchHits = response.getHits().getHits();

		if(logger.isDebugEnabled()){
			logger.debug("Number of subscriptions - " + searchHits.length + " for " + EventOrigin+EventID);
		}



		for(int i = 0; i < searchHits.length; i++){
			source = searchHits[i].sourceAsMap();
			subscriptions.add(source.get("subscriberid").toString());
		}

		return subscriptions;
	}


}