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
import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.ExchangePattern;
import org.apache.camel.Producer;
import org.apache.camel.impl.DefaultProducer;
import org.apache.camel.util.ServiceHelper;

public class HystrixProducer extends DefaultProducer {

    private final Producer child;
    private final String groupId;

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
        HystrixCommandGroupKey key = HystrixCommandGroupKey.Factory.asKey(groupId);
        final HystrixCommand command = new HystrixCommand(key) {
            @Override
            protected Object run() throws Exception {
                child.process(exchange);
                return null;
            }
        };
        command.execute();
    }

    public HystrixProducer(final Endpoint endpoint, final Producer child, final String groupId) {
        super(endpoint);
        this.child = child;
        this.groupId = groupId;
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
