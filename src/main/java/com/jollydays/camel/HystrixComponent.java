package com.jollydays.camel;

import org.apache.camel.Endpoint;
import org.apache.camel.impl.DefaultComponent;

import java.util.Map;

public class HystrixComponent extends DefaultComponent{
	@Override
	protected Endpoint createEndpoint(final String uri, final String remaining, final Map<String, Object> parameters) throws Exception {
		final HystrixEndpoint endpoint = new HystrixEndpoint(uri, remaining, this);
		endpoint.setGroupId(getAndRemoveParameter(parameters, "groupId", String.class)) ;
		return endpoint;
	}
}
