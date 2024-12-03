/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.scheduledexecutor.impl;

import com.hazelcast.config.ScheduledExecutorConfig;
import com.hazelcast.logging.ILogger;
import com.hazelcast.scheduledexecutor.impl.AbstractScheduledExecutorContainerHolder;
import com.hazelcast.scheduledexecutor.impl.ScheduledExecutorContainer;
import com.hazelcast.scheduledexecutor.impl.ScheduledExecutorMemberOwnedContainer;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.util.ConstructorFunction;

public class ScheduledExecutorMemberBin
extends AbstractScheduledExecutorContainerHolder {
    private final ILogger logger;
    private final NodeEngine nodeEngine;
    private final ConstructorFunction<String, ScheduledExecutorContainer> containerConstructorFunction = new ConstructorFunction<String, ScheduledExecutorContainer>(){

        @Override
        public ScheduledExecutorContainer createNew(String name) {
            if (ScheduledExecutorMemberBin.this.logger.isFinestEnabled()) {
                ScheduledExecutorMemberBin.this.logger.finest("[Partition: -1] Create new scheduled executor container with name: " + name);
            }
            ScheduledExecutorConfig config = ScheduledExecutorMemberBin.this.nodeEngine.getConfig().findScheduledExecutorConfig(name);
            return new ScheduledExecutorMemberOwnedContainer(name, config.getCapacity(), ScheduledExecutorMemberBin.this.nodeEngine);
        }
    };

    public ScheduledExecutorMemberBin(NodeEngine nodeEngine) {
        super(nodeEngine);
        this.logger = nodeEngine.getLogger(this.getClass());
        this.nodeEngine = nodeEngine;
    }

    @Override
    public ConstructorFunction<String, ScheduledExecutorContainer> getContainerConstructorFunction() {
        return this.containerConstructorFunction;
    }
}

