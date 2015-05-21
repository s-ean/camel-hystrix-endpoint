/*
 * Copyright 2015, Jollydays GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * HystrixProducerTest.java
 *
 * Authors:
 *     Roman Mohr (r.mohr@jollydays.com)
**/

package com.jollydays.camel;

import com.netflix.config.ConfigurationManager;
import com.netflix.hystrix.exception.HystrixRuntimeException;
import org.apache.camel.CamelContext;
import org.apache.camel.EndpointInject;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Before;
import org.junit.Test;

public class HystrixProducerTest extends CamelTestSupport {

    public static final String CIRCUIT_BREAKER_FORCE_OPEN = "hystrix.command.default.circuitBreaker.forceOpen";

    @Produce(uri = "direct:start")
    protected ProducerTemplate template;

    @EndpointInject(uri = "mock:result")
    protected MockEndpoint resultEndpoint;

    private class TestRoute extends RouteBuilder {

        @Override
        public void configure() throws Exception {
            onException(HystrixRuntimeException.class).handled(true).setBody().constant("error");
            from("direct:start").to("hystrix:direct:mitm?groupId=test");
            from("direct:mitm").to("mock:result");
        }
    }

    @Before
    public void setUp() throws Exception {
        super.setUp();
        ConfigurationManager.getConfigInstance().setProperty(CIRCUIT_BREAKER_FORCE_OPEN, "false");
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

    @Test
    public void shouldReactOnOpenCircuit() throws InterruptedException {
        ConfigurationManager.getConfigInstance().setProperty(CIRCUIT_BREAKER_FORCE_OPEN, "true");
        final Object response = template.requestBody("test");
        assertEquals("error", response);
    }
}
