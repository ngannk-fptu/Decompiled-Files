/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.hazelcast.map.impl.querycache.publisher;

import com.hazelcast.map.impl.querycache.QueryCacheContext;
import com.hazelcast.map.impl.querycache.accumulator.AccumulatorHandler;
import com.hazelcast.map.impl.querycache.accumulator.AccumulatorProcessor;
import com.hazelcast.map.impl.querycache.event.BatchEventData;
import com.hazelcast.map.impl.querycache.event.QueryCacheEventData;
import com.hazelcast.map.impl.querycache.event.sequence.Sequenced;
import com.hazelcast.nio.Address;
import com.hazelcast.spi.properties.GroupProperty;
import com.hazelcast.util.MapUtil;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import javax.annotation.Nonnull;

public class PublisherAccumulatorHandler
implements AccumulatorHandler<Sequenced> {
    private final QueryCacheContext context;
    private final AccumulatorProcessor<Sequenced> processor;
    private Queue<QueryCacheEventData> eventCollection;

    public PublisherAccumulatorHandler(QueryCacheContext context, AccumulatorProcessor<Sequenced> processor) {
        this.context = context;
        this.processor = processor;
    }

    @Override
    public void handle(Sequenced eventData, boolean lastElement) {
        if (this.eventCollection == null) {
            this.eventCollection = new ArrayDeque<QueryCacheEventData>();
        }
        this.eventCollection.add((QueryCacheEventData)eventData);
        if (lastElement) {
            this.process();
        }
    }

    @Override
    public void reset() {
        if (this.eventCollection == null) {
            return;
        }
        this.eventCollection.clear();
    }

    private void process() {
        Queue<QueryCacheEventData> eventCollection = this.eventCollection;
        if (eventCollection.isEmpty()) {
            return;
        }
        if (eventCollection.size() < 2) {
            QueryCacheEventData eventData = eventCollection.poll();
            this.processor.process(eventData);
        } else {
            this.sendInBatches(eventCollection);
        }
    }

    private void sendInBatches(@Nonnull Queue<QueryCacheEventData> events) {
        Map<Integer, List<QueryCacheEventData>> partitionToEventDataMap = this.createPartitionToEventDataMap(events);
        this.sendToSubscriber(partitionToEventDataMap);
    }

    private Map<Integer, List<QueryCacheEventData>> createPartitionToEventDataMap(Queue<QueryCacheEventData> events) {
        QueryCacheEventData eventData;
        if (events.isEmpty()) {
            return Collections.emptyMap();
        }
        int defaultPartitionCount = Integer.parseInt(GroupProperty.PARTITION_COUNT.getDefaultValue());
        int roughSize = Math.min(events.size(), defaultPartitionCount);
        Map<Integer, List<QueryCacheEventData>> map = MapUtil.createHashMap(roughSize);
        while ((eventData = events.poll()) != null) {
            int partitionId = eventData.getPartitionId();
            List<QueryCacheEventData> eventDataList = map.get(partitionId);
            if (eventDataList == null) {
                eventDataList = new ArrayList<QueryCacheEventData>();
                map.put(partitionId, eventDataList);
            }
            eventDataList.add(eventData);
        }
        return map;
    }

    private void sendToSubscriber(Map<Integer, List<QueryCacheEventData>> map) {
        Set<Map.Entry<Integer, List<QueryCacheEventData>>> entries = map.entrySet();
        for (Map.Entry<Integer, List<QueryCacheEventData>> entry : entries) {
            Integer partitionId = entry.getKey();
            List<QueryCacheEventData> eventData = entry.getValue();
            String thisNodesAddress = this.getThisNodesAddress();
            BatchEventData batchEventData = new BatchEventData(eventData, thisNodesAddress, partitionId);
            this.processor.process(batchEventData);
        }
    }

    private String getThisNodesAddress() {
        Address thisAddress = this.context.getThisNodesAddress();
        return thisAddress.toString();
    }
}

