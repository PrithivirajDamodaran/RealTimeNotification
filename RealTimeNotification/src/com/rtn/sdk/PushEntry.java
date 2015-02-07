package com.rtn.sdk;

import com.lmax.disruptor.EventFactory;

/***
 * 
 * 
 * @author prithivirajdamodaran
 *
 */

public class PushEntry {

	public PublishRequest publishrequest;

	public static final EventFactory<PushEntry> FACTORY = new EventFactory<PushEntry>() {
		public PushEntry newInstance() {
			return new PushEntry();
		}
	};

}