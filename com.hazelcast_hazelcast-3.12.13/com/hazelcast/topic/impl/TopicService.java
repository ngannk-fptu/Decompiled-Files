/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.topic.impl;

import com.hazelcast.config.TopicConfig;
import com.hazelcast.core.ITopic;
import com.hazelcast.core.MessageListener;
import com.hazelcast.instance.MemberImpl;
import com.hazelcast.internal.cluster.ClusterService;
import com.hazelcast.monitor.LocalTopicStats;
import com.hazelcast.monitor.impl.LocalTopicStatsImpl;
import com.hazelcast.nio.Address;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.EventPublishingService;
import com.hazelcast.spi.EventRegistration;
import com.hazelcast.spi.EventService;
import com.hazelcast.spi.ManagedService;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.RemoteService;
import com.hazelcast.spi.StatisticsAwareService;
import com.hazelcast.topic.impl.DataAwareMessage;
import com.hazelcast.topic.impl.TopicEvent;
import com.hazelcast.topic.impl.TopicProxy;
import com.hazelcast.topic.impl.TotalOrderedTopicProxy;
import com.hazelcast.util.ConcurrencyUtil;
import com.hazelcast.util.ConstructorFunction;
import com.hazelcast.util.HashUtil;
import com.hazelcast.util.MapUtil;
import java.util.Collection;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class TopicService
implements ManagedService,
RemoteService,
EventPublishingService,
StatisticsAwareService<LocalTopicStats> {
    public static final String SERVICE_NAME = "hz:impl:topicService";
    public static final int ORDERING_LOCKS_LENGTH = 1000;
    private final ConcurrentMap<String, LocalTopicStatsImpl> statsMap = new ConcurrentHashMap<String, LocalTopicStatsImpl>();
    private final Lock[] orderingLocks = new Lock[1000];
    private NodeEngine nodeEngine;
    private final ConstructorFunction<String, LocalTopicStatsImpl> localTopicStatsConstructorFunction = new ConstructorFunction<String, LocalTopicStatsImpl>(){

        @Override
        public LocalTopicStatsImpl createNew(String mapName) {
            return new LocalTopicStatsImpl();
        }
    };
    private EventService eventService;
    private final AtomicInteger counter = new AtomicInteger(0);
    private Address localAddress;

    @Override
    public void init(NodeEngine nodeEngine, Properties properties) {
        this.nodeEngine = nodeEngine;
        this.localAddress = nodeEngine.getThisAddress();
        for (int i = 0; i < this.orderingLocks.length; ++i) {
            this.orderingLocks[i] = new ReentrantLock();
        }
        this.eventService = nodeEngine.getEventService();
    }

    public ConcurrentMap<String, LocalTopicStatsImpl> getStatsMap() {
        return this.statsMap;
    }

    @Override
    public void reset() {
        this.statsMap.clear();
    }

    @Override
    public void shutdown(boolean terminate) {
        this.reset();
    }

    public Lock getOrderLock(String key) {
        int index = this.getOrderLockIndex(key);
        return this.orderingLocks[index];
    }

    private int getOrderLockIndex(String key) {
        int hash = key.hashCode();
        return HashUtil.hashToIndex(hash, this.orderingLocks.length);
    }

    @Override
    public ITopic createDistributedObject(String name) {
        TopicConfig topicConfig = this.nodeEngine.getConfig().findTopicConfig(name);
        if (topicConfig.isGlobalOrderingEnabled()) {
            return new TotalOrderedTopicProxy(name, this.nodeEngine, this);
        }
        return new TopicProxy(name, this.nodeEngine, this);
    }

    @Override
    public void destroyDistributedObject(String objectId) {
        this.statsMap.remove(objectId);
        this.nodeEngine.getEventService().deregisterAllListeners(SERVICE_NAME, objectId);
    }

    public void dispatchEvent(Object event, Object listener) {
        TopicEvent topicEvent = (TopicEvent)event;
        ClusterService clusterService = this.nodeEngine.getClusterService();
        MemberImpl member = clusterService.getMember(topicEvent.publisherAddress);
        if (member == null) {
            member = new MemberImpl.Builder(topicEvent.publisherAddress).version(this.nodeEngine.getVersion()).build();
        }
        DataAwareMessage message = new DataAwareMessage(topicEvent.name, topicEvent.data, topicEvent.publishTime, member, this.nodeEngine.getSerializationService());
        this.incrementReceivedMessages(topicEvent.name);
        MessageListener messageListener = (MessageListener)listener;
        messageListener.onMessage(message);
    }

    public LocalTopicStatsImpl getLocalTopicStats(String name) {
        return ConcurrencyUtil.getOrPutSynchronized(this.statsMap, name, this.statsMap, this.localTopicStatsConstructorFunction);
    }

    public void incrementPublishes(String topicName) {
        this.getLocalTopicStats(topicName).incrementPublishes();
    }

    public void incrementReceivedMessages(String topicName) {
        this.getLocalTopicStats(topicName).incrementReceives();
    }

    public void publishMessage(String topicName, Object payload, boolean multithreaded) {
        Collection<EventRegistration> registrations = this.eventService.getRegistrations(SERVICE_NAME, topicName);
        if (!registrations.isEmpty()) {
            Data payloadData = this.nodeEngine.toData(payload);
            TopicEvent topicEvent = new TopicEvent(topicName, payloadData, this.localAddress);
            int partitionId = multithreaded ? this.counter.incrementAndGet() : topicName.hashCode();
            this.eventService.publishEvent(SERVICE_NAME, registrations, (Object)topicEvent, partitionId);
        }
    }

    public String addMessageListener(String name, MessageListener listener, boolean localOnly) {
        EventRegistration eventRegistration = localOnly ? this.eventService.registerLocalListener(SERVICE_NAME, name, listener) : this.eventService.registerListener(SERVICE_NAME, name, listener);
        return eventRegistration.getId();
    }

    public boolean removeMessageListener(String name, String registrationId) {
        return this.eventService.deregisterListener(SERVICE_NAME, name, registrationId);
    }

    @Override
    public Map<String, LocalTopicStats> getStats() {
        Map<String, LocalTopicStats> topicStats = MapUtil.createHashMap(this.statsMap.size());
        for (Map.Entry queueStat : this.statsMap.entrySet()) {
            topicStats.put((String)queueStat.getKey(), (LocalTopicStats)queueStat.getValue());
        }
        return topicStats;
    }
}

