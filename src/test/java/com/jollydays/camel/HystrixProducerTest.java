package com.jollydays.camel;

import org.apache.camel.CamelContext;
import org.apache.camel.EndpointInject;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

public class HystrixProducerTest extends CamelTestSupport {

	@Produce(uri = "direct:start")
	protected ProducerTemplate template;

	@EndpointInject(uri = "mock:result")
	protected MockEndpoint resultEndpoint;

	private class TestRoute extends RouteBuilder {

		@Override
		public void configure() throws Exception {
			from("direct:start").to("hystrix:direct:mitm?groupId=test");
			from("direct:mitm").to("mock:result");
		}
	}

	@Override
	protected CamelContext createCamelContext() throws Exception {
		CamelContext context = super.createCamelContext();
		return context;
	}

	@Override
	protected RouteBuilder createRouteBuilder() throws Exception {
		return new TestRoute();
	}

	@Test
	public void shouldUseHystrixProducer() throws InterruptedException {
		resultEndpoint.expectedBodiesReceived("test");
		template.sendBody("test");
		assertMockEndpointsSatisfied();
	}
}
