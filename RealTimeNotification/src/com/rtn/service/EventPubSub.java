package com.rtn.service;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import com.rtn.sdk.GPMessage;

@Path("/")
public class EventPubSub{

	/**
	 * This is the entry point for applications using this library, applications can either publish or subscribe
	 * to events. PubSub is a high level EAI pattern all the "Event Notification" Systems use. Mobile Push Notification
	 * is a Distributed PubSub. Mobile devices are subscribers and your application notifies one or more of them
	 * when an Event of interest occurs.
	 * 
	 *    
	 */

	@POST
	@Path("/Publish")
	@Produces("application/json")
	@Consumes("application/json")

	public void Publish(List<GPMessage> Data, List<String> Receivers){

	}
	
	
	@POST
	@Path("/Subscribe")
	@Produces("application/json")
	@Consumes("application/json")


	public void Subscribe(){

	}

}
