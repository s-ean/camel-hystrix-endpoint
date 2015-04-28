package com.jollydays.camel;

import org.apache.camel.*;
import org.apache.camel.impl.DefaultEndpoint;
import org.apache.camel.spi.Metadata;
import org.apache.camel.spi.UriEndpoint;
import org.apache.camel.spi.UriParam;
import org.apache.camel.spi.UriPath;

@UriEndpoint(scheme = "hystrix", syntax = "hystrix:childUri", title = "hystrix component", producerOnly = true)
public class HystrixEndpoint extends DefaultEndpoint implements DelegateEndpoint {

	@UriPath
	@Metadata(required = "true")
	private String childUri;

	@UriParam(name = "groupId")
	@Metadata(required = "true")
	private String groupId;

	public HystrixEndpoint(final String endpointUri, final String remainingUri, final Component component) {
		super(endpointUri, component);
		childUri = remainingUri;
	}

	@Override
	public Producer createProducer() throws Exception {
		return new HystrixProducer(this, getCamelContext().getEndpoint(childUri).createProducer(), groupId);
	}

	@Override
	public Consumer createConsumer(final Processor processor) throws Exception {
		return null;
	}

	@Override
	public boolean isSingleton() {
		return false;
	}

	@Override
	public Endpoint getEndpoint() {
		return getCamelContext().getEndpoint(childUri);
	}

	public void setGroupId(final String groupId) {
		this.groupId = groupId;
	}
}
