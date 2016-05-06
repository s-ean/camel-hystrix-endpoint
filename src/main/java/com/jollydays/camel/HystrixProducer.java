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
 * HystrixProducer.java
 *
 * Authors:
 *     Roman Mohr (r.mohr@jollydays.com)
**/

package com.jollydays.camel;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixCommandProperties;
// modification from original source
import com.netflix.hystrix.HystrixThreadPoolProperties;
// end of modification
import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.ExchangePattern;
import org.apache.camel.Producer;
import org.apache.camel.impl.DefaultProducer;
import org.apache.camel.util.ServiceHelper;

public class HystrixProducer extends DefaultProducer {

    private final Producer child;
    private final HystrixCommand.Setter setter;
    private final boolean rethrowUnchecked;
    private final boolean rethrowChecked;

    @Override
    public Exchange createExchange() {
        return child.createExchange();
    }

    @Override
    public Exchange createExchange(final ExchangePattern exchangePattern) {
        return child.createExchange(exchangePattern);
    }

    @Override
    @Deprecated
    public Exchange createExchange(final Exchange exchange) {
        return child.createExchange(exchange);
    }

    @Override
    public Endpoint getEndpoint() {
        return child.getEndpoint();
    }

    @Override
    public boolean isSingleton() {
        return false;
    }

    @Override
    public void process(final Exchange exchange) throws Exception {
        final HystrixCommand command = new HystrixCommand(setter) {
            @Override
            protected Object run() throws Exception {
                child.process(exchange);
                Exception ex = exchange.getException();
                if (ex != null && ((ex instanceof RuntimeException && rethrowUnchecked) || (!(ex instanceof RuntimeException) && rethrowChecked))) {
                    throw ex;
                }
                return null;
            }
        };
        command.execute();
    }

    // signature modified from original source
    public HystrixProducer(final Endpoint endpoint, final Producer child, final String group, final String command, final Integer timeout, boolean rethrowUnchecked, boolean rethrowChecked,
                           Boolean circuitBreakerEnabled, Integer circuitBreakerErrorThresholdPercentage, Integer circuitBreakerRequestVolumeThreshold,
                           Integer circuitBreakerSleepWindowInMilliseconds, Boolean fallbackEnabled, Integer coreSize) {
        super(endpoint);
        this.child = child;
        HystrixCommandGroupKey groupKey = HystrixCommandGroupKey.Factory.asKey(group);
        setter = HystrixCommand.Setter.withGroupKey(groupKey);
        if (command != null) {
            setter.andCommandKey(HystrixCommandKey.Factory.asKey(command));
        }

        // modification from original source
        HystrixCommandProperties.Setter commandPropertiesSetter = HystrixCommandProperties.Setter();
        HystrixThreadPoolProperties.Setter threadPoolPropertiesSetter = HystrixThreadPoolProperties.Setter();

        if (timeout != null)
            commandPropertiesSetter.withExecutionTimeoutInMilliseconds(timeout);
        if (circuitBreakerEnabled != null)
            commandPropertiesSetter.withCircuitBreakerEnabled(circuitBreakerEnabled);
        if (circuitBreakerErrorThresholdPercentage != null)
            commandPropertiesSetter.withCircuitBreakerErrorThresholdPercentage(circuitBreakerErrorThresholdPercentage);
        if (circuitBreakerRequestVolumeThreshold != null)
            commandPropertiesSetter.withCircuitBreakerRequestVolumeThreshold(circuitBreakerRequestVolumeThreshold);
        if (circuitBreakerSleepWindowInMilliseconds != null)
            commandPropertiesSetter.withCircuitBreakerSleepWindowInMilliseconds(circuitBreakerSleepWindowInMilliseconds);
        if (fallbackEnabled != null)
            commandPropertiesSetter.withFallbackEnabled(fallbackEnabled);
        if (coreSize != null)
            threadPoolPropertiesSetter.withCoreSize(coreSize);


        setter.andCommandPropertiesDefaults(commandPropertiesSetter);
        setter.andThreadPoolPropertiesDefaults(threadPoolPropertiesSetter);
        // end of modification

        this.rethrowUnchecked = rethrowUnchecked;
        this.rethrowChecked = rethrowChecked;
    }

    @Override
    protected void doStart() throws Exception {
        ServiceHelper.startService(child);
        super.doStart();
    }

    @Override
    protected void doStop() throws Exception {
        ServiceHelper.stopService(child);
        super.doStop();
    }

    @Override
    protected void doSuspend() throws Exception {
        ServiceHelper.suspendService(child);
        super.doSuspend();
    }

    @Override
    protected void doResume() throws Exception {
        ServiceHelper.resumeService(child);
        super.doResume();
    }

    @Override
    protected void doShutdown() throws Exception {
        ServiceHelper.stopAndShutdownService(child);
        super.doShutdown();
    }
}
