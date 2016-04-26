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
 * HystrixEndpoint.java
 *
 * Authors:
 *     Roman Mohr (r.mohr@jollydays.com)
**/

package com.jollydays.camel;

import org.apache.camel.*;
import org.apache.camel.impl.DefaultEndpoint;
import org.apache.camel.spi.Metadata;
import org.apache.camel.spi.UriEndpoint;
import org.apache.camel.spi.UriParam;

@UriEndpoint(scheme = "hystrix", syntax = "hystrix:childUri", title = "hystrix component", producerOnly = true)
public class HystrixEndpoint extends DefaultEndpoint implements DelegateEndpoint {

    private final String childUri;

    @UriParam(name = "hystrixGroup")
    @Metadata(required = "true")
    private String group;

    @UriParam(name = "hystrixCommand")
    @Metadata(required = "false")
    private String command;

    @UriParam(name = "hystrixCommandTimeout")
    @Metadata(required = "false")
    private Integer timeout;

    @UriParam(name = "hystrixRethrowUnchecked")
    @Metadata(required = "false", label = "Rethrow unchecked exception contained in the exchange and cause hystrix command to fail", defaultValue = "true")
    private boolean rethrowUnchecked = true;

    @UriParam(name = "hystrixRethrowChecked")
    @Metadata(required = "false", label = "Rethrow checked exception contained in the exchange and cause hystrix command to fail", defaultValue = "false")
    private boolean rethrowChecked = false;

    // addition to original source
    @UriParam(name = "hystrixCircuitBreakerEnabled")
    @Metadata(required = "false")
    private Boolean circuitBreakerEnabled;

    @UriParam(name = "hystrixCircuitBreakerErrorThresholdPercentage")
    @Metadata(required = "false")
    private Integer circuitBreakerErrorThresholdPercentage;

    @UriParam(name = "hystrixCircuitBreakerRequestVolumeThreshold")
    @Metadata(required = "false")
    private Integer circuitBreakerRequestVolumeThreshold;

    @UriParam(name = "hystrixCircuitBreakerSleepWindowInMilliseconds")
    @Metadata(required = "false")
    private Integer circuitBreakerSleepWindowInMilliseconds;
    // end of addition

    public HystrixEndpoint(final String endpointUri, final String remainingUri, final Component component) {
        super(endpointUri, component);
        childUri = remainingUri;
    }

    @Override
    // signature modified from original source
    public Producer createProducer() throws Exception {
        return new HystrixProducer(this, getCamelContext().getEndpoint(childUri).createProducer(), group, command, timeout, rethrowUnchecked, rethrowChecked,
                circuitBreakerEnabled, circuitBreakerErrorThresholdPercentage, circuitBreakerRequestVolumeThreshold, circuitBreakerSleepWindowInMilliseconds);
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

    public void setGroup(final String group) {
        this.group = group;
    }

    @Override
    public boolean isLenientProperties() {
        return true;
    }

    public void setCommand(final String command) {
        this.command = command;
    }

    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
    }

    public boolean isRethrowChecked() {
        return rethrowChecked;
    }

    public void setRethrowChecked(boolean rethrowChecked) {
        this.rethrowChecked = rethrowChecked;
    }

    public boolean isRethrowUnchecked() {
        return rethrowUnchecked;
    }

    public void setRethrowUnchecked(boolean rethrowUnchecked) {
        this.rethrowUnchecked = rethrowUnchecked;
    }

    // addition to original source
    public void setCircuitBreakerEnabled(Boolean circuitBreakerEnabled) {
        this.circuitBreakerEnabled = circuitBreakerEnabled;
    }

    public void setCircuitBreakerErrorThresholdPercentage(Integer circuitBreakerErrorThresholdPercentage) {
        this.circuitBreakerErrorThresholdPercentage = circuitBreakerErrorThresholdPercentage;
    }

    public void setCircuitBreakerRequestVolumeThreshold(Integer circuitBreakerRequestVolumeThreshold) {
        this.circuitBreakerRequestVolumeThreshold = circuitBreakerRequestVolumeThreshold;
    }

    public void setCircuitBreakerSleepWindowInMilliseconds(Integer circuitBreakerSleepWindowInMilliseconds) {
        this.circuitBreakerSleepWindowInMilliseconds = circuitBreakerSleepWindowInMilliseconds;
    }
    // end of addition
}
