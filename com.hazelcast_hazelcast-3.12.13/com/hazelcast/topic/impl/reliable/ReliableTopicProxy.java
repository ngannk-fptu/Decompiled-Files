/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.topic.impl.reliable;

import com.hazelcast.config.ListenerConfig;
import com.hazelcast.config.ReliableTopicConfig;
import com.hazelcast.core.HazelcastException;
import com.hazelcast.core.HazelcastInstanceAware;
import com.hazelcast.core.ITopic;
import com.hazelcast.core.MessageListener;
import com.hazelcast.monitor.LocalTopicStats;
import com.hazelcast.monitor.impl.LocalTopicStatsImpl;
import com.hazelcast.nio.Address;
import com.hazelcast.nio.ClassLoaderUtil;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.ringbuffer.OverflowPolicy;
import com.hazelcast.ringbuffer.Ringbuffer;
import com.hazelcast.spi.AbstractDistributedObject;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.topic.ReliableMessageListener;
import com.hazelcast.topic.TopicOverloadException;
import com.hazelcast.topic.TopicOverloadPolicy;
import com.hazelcast.topic.impl.reliable.MessageRunner;
import com.hazelcast.topic.impl.reliable.ReliableMessageListenerAdapter;
import com.hazelcast.topic.impl.reliable.ReliableMessageRunner;
import com.hazelcast.topic.impl.reliable.ReliableTopicMessage;
import com.hazelcast.topic.impl.reliable.ReliableTopicService;
import com.hazelcast.util.ExceptionUtil;
import com.hazelcast.util.Preconditions;
import com.hazelcast.util.UuidUtil;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

public class ReliableTopicProxy<E>
extends AbstractDistributedObject<ReliableTopicService>
implements ITopic<E> {
    public static final int MAX_BACKOFF = 2000;
    public static final int INITIAL_BACKOFF_MS = 100;
    final Ringbuffer<ReliableTopicMessage> ringbuffer;
    final Executor executor;
    final ConcurrentMap<String, MessageRunner<E>> runnersMap = new ConcurrentHashMap<String, MessageRunner<E>>();
    final LocalTopicStatsImpl localTopicStats;
    final ReliableTopicConfig topicConfig;
    final TopicOverloadPolicy overloadPolicy;
    private final NodeEngine nodeEngine;
    private final Address thisAddress;
    private final String name;

    public ReliableTopicProxy(String name, NodeEngine nodeEngine, ReliableTopicService service, ReliableTopicConfig topicConfig) {
        super(nodeEngine, service);
        this.name = name;
        this.topicConfig = topicConfig;
        this.nodeEngine = nodeEngine;
        this.ringbuffer = nodeEngine.getHazelcastInstance().getRingbuffer("_hz_rb_" + name);
        this.executor = this.initExecutor(nodeEngine, topicConfig);
        this.thisAddress = nodeEngine.getThisAddress();
        this.overloadPolicy = topicConfig.getTopicOverloadPolicy();
        this.localTopicStats = service.getLocalTopicStats(name);
        for (ListenerConfig listenerConfig : topicConfig.getMessageListenerConfigs()) {
            this.addMessageListener(listenerConfig);
        }
    }

    @Override
    public String getServiceName() {
        return "hz:impl:reliableTopicService";
    }

    @Override
    public String getName() {
        return this.name;
    }

    private void addMessageListener(ListenerConfig listenerConfig) {
        NodeEngine nodeEngine = this.getNodeEngine();
        MessageListener listener = this.loadListener(listenerConfig);
        if (listener == null) {
            return;
        }
        if (listener instanceof HazelcastInstanceAware) {
            HazelcastInstanceAware hazelcastInstanceAware = (HazelcastInstanceAware)((Object)listener);
            hazelcastInstanceAware.setHazelcastInstance(nodeEngine.getHazelcastInstance());
        }
        this.addMessageListener(listener);
    }

    private MessageListener loadListener(ListenerConfig listenerConfig) {
        try {
            MessageListener listener = (MessageListener)listenerConfig.getImplementation();
            if (listener != null) {
                return listener;
            }
            if (listenerConfig.getClassName() != null) {
                Object object = ClassLoaderUtil.newInstance(this.nodeEngine.getConfigClassLoader(), listenerConfig.getClassName());
                if (!(object instanceof MessageListener)) {
                    throw new HazelcastException("class '" + listenerConfig.getClassName() + "' is not an instance of " + MessageListener.class.getName());
                }
                listener = (MessageListener)object;
            }
            return listener;
        }
        catch (Exception e) {
            throw ExceptionUtil.rethrow(e);
        }
    }

    private Executor initExecutor(NodeEngine nodeEngine, ReliableTopicConfig topicConfig) {
        Executor executor = topicConfig.getExecutor();
        if (executor == null) {
            executor = nodeEngine.getExecutionService().getExecutor("hz:async");
        }
        return executor;
    }

    @Override
    public void publish(E payload) {
        try {
            Data data = this.nodeEngine.toData(payload);
            ReliableTopicMessage message = new ReliableTopicMessage(data, this.thisAddress);
            switch (this.overloadPolicy) {
                case ERROR: {
                    this.addOrFail(message);
                    break;
                }
                case DISCARD_OLDEST: {
                    this.addOrOverwrite(message);
                    break;
                }
                case DISCARD_NEWEST: {
                    this.ringbuffer.addAsync(message, OverflowPolicy.FAIL).get();
                    break;
                }
                case BLOCK: {
                    this.addWithBackoff(message);
                    break;
                }
                default: {
                    throw new IllegalArgumentException("Unknown overloadPolicy:" + (Object)((Object)this.overloadPolicy));
                }
            }
            this.localTopicStats.incrementPublishes();
        }
        catch (Exception e) {
            throw (RuntimeException)ExceptionUtil.peel(e, null, "Failed to publish message: " + payload + " to topic:" + this.getName());
        }
    }

    private Long addOrOverwrite(ReliableTopicMessage message) throws Exception {
        return (Long)this.ringbuffer.addAsync(message, OverflowPolicy.OVERWRITE).get();
    }

    private void addOrFail(ReliableTopicMessage message) throws Exception {
        long sequenceId = (Long)this.ringbuffer.addAsync(message, OverflowPolicy.FAIL).get();
        if (sequenceId == -1L) {
            throw new TopicOverloadException("Failed to publish message: " + message + " on topic:" + this.getName());
        }
    }

    private void addWithBackoff(ReliableTopicMessage message) throws Exception {
        long result;
        long timeoutMs = 100L;
        while ((result = ((Long)this.ringbuffer.addAsync(message, OverflowPolicy.FAIL).get()).longValue()) == -1L) {
            TimeUnit.MILLISECONDS.sleep(timeoutMs);
            if ((timeoutMs *= 2L) <= 2000L) continue;
            timeoutMs = 2000L;
        }
    }

    @Override
    public String addMessageListener(MessageListener<E> listener) {
        Preconditions.checkNotNull(listener, "listener can't be null");
        String id = UuidUtil.newUnsecureUuidString();
        ReliableMessageListenerAdapter<E> reliableMessageListener = listener instanceof ReliableMessageListener ? (ReliableMessageListenerAdapter<E>)listener : new ReliableMessageListenerAdapter<E>(listener);
        ReliableMessageRunner<E> runner = new ReliableMessageRunner<E>(id, reliableMessageListener, this.nodeEngine.getSerializationService(), this.executor, this.nodeEngine.getLogger(this.getClass()), this.nodeEngine.getClusterService(), this);
        this.runnersMap.put(id, runner);
        runner.next();
        return id;
    }

    @Override
    public boolean removeMessageListener(String registrationId) {
        Preconditions.checkNotNull(registrationId, "registrationId can't be null");
        MessageRunner runner = (MessageRunner)this.runnersMap.get(registrationId);
        if (runner == null) {
            return false;
        }
        runner.cancel();
        return true;
    }

    @Override
    protected void postDestroy() {
        this.ringbuffer.destroy();
    }

    @Override
    public LocalTopicStats getLocalTopicStats() {
        return this.localTopicStats;
    }
}

