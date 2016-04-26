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
 * HystrixComponent.java
 *
 * Authors:
 *     Roman Mohr (r.mohr@jollydays.com)
**/

package com.jollydays.camel;

import org.apache.camel.Endpoint;
import org.apache.camel.impl.DefaultComponent;
import org.apache.camel.util.URISupport;

import java.util.Map;

public class HystrixComponent extends DefaultComponent {
    @Override
    protected Endpoint createEndpoint(final String uri, final String remaining, final Map<String, Object> parameters) throws Exception {
        final String group = getAndRemoveParameter(parameters, "hystrixGroup", String.class);
        final String command = getAndRemoveParameter(parameters, "hystrixCommand", String.class);
        final String timeout = getAndRemoveParameter(parameters, "hystrixCommandTimeout", String.class);

        // addition to original source
        final String circuitBreakerEnabled = getAndRemoveParameter(parameters, "hystrixCircuitBreakerEnabled", String.class);
        final String circuitBreakerErrorThresholdPercentage = getAndRemoveParameter(parameters, "hystrixCircuitBreakerErrorThresholdPercentage", String.class);
        final String circuitBreakerRequestVolumeThreshold = getAndRemoveParameter(parameters, "hystrixCircuitBreakerRequestVolumeThreshold", String.class);
        final String circuitBreakerSleepWindowInMilliseconds = getAndRemoveParameter(parameters, "hystrixCircuitBreakerSleepWindowInMilliseconds", String.class);
        // end of addition

        final HystrixEndpoint endpoint = new HystrixEndpoint(uri, URISupport.appendParametersToURI(remaining, parameters), this);
        endpoint.setGroup(group);
        endpoint.setCommand(command);

        if(timeout != null) {
            try {
                Integer parsedTimeout = Integer.valueOf(timeout);
                endpoint.setTimeout(parsedTimeout);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid value " + timeout + " for parameter hystrixCommandTimeout (needs to be a number)");
            }
        }

        // addition to original source
        endpoint.setCircuitBreakerEnabled(ParseUtility.tryParseBoolean(circuitBreakerEnabled));
        endpoint.setCircuitBreakerErrorThresholdPercentage(ParseUtility.tryParseInt(circuitBreakerErrorThresholdPercentage));
        endpoint.setCircuitBreakerRequestVolumeThreshold(ParseUtility.tryParseInt(circuitBreakerRequestVolumeThreshold));
        endpoint.setCircuitBreakerSleepWindowInMilliseconds(ParseUtility.tryParseInt(circuitBreakerSleepWindowInMilliseconds));

        endpoint.setRethrowUnchecked(false);
        // end of addition

        return endpoint;
    }
}
