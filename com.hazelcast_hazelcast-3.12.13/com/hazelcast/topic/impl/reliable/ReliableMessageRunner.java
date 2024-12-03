/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.topic.impl.reliable;

import com.hazelcast.core.Member;
import com.hazelcast.internal.cluster.ClusterService;
import com.hazelcast.logging.ILogger;
import com.hazelcast.ringbuffer.StaleSequenceException;
import com.hazelcast.spi.serialization.SerializationService;
import com.hazelcast.topic.ReliableMessageListener;
import com.hazelcast.topic.impl.reliable.MessageRunner;
import com.hazelcast.topic.impl.reliable.ReliableTopicMessage;
import com.hazelcast.topic.impl.reliable.ReliableTopicProxy;
import java.util.concurrent.Executor;

public class ReliableMessageRunner<E>
extends MessageRunner<E> {
    private final ClusterService clusterService;
    private final ReliableTopicProxy<E> proxy;

    ReliableMessageRunner(String id, ReliableMessageListener<E> listener, SerializationService serializationService, Executor executor, ILogger logger, ClusterService clusterService, ReliableTopicProxy<E> proxy) {
        super(id, listener, proxy.ringbuffer, proxy.getName(), proxy.topicConfig.getReadBatchSize(), serializationService, executor, proxy.runnersMap, logger);
        this.clusterService = clusterService;
        this.proxy = proxy;
    }

    @Override
    protected void updateStatistics() {
        this.proxy.localTopicStats.incrementReceives();
    }

    @Override
    protected Member getMember(ReliableTopicMessage m) {
        return this.clusterService.getMember(m.getPublisherAddress());
    }

    @Override
    protected Throwable adjustThrowable(Throwable t) {
        return t;
    }

    @Override
    protected long getHeadSequence(StaleSequenceException staleSequenceException) {
        return staleSequenceException.getHeadSeq();
    }
}

