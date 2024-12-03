/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.mapreduce.impl.task;

import com.hazelcast.logging.ILogger;
import com.hazelcast.mapreduce.KeyValueSource;
import com.hazelcast.mapreduce.impl.MapReduceService;
import com.hazelcast.mapreduce.impl.operation.ProcessStatsUpdateOperation;
import com.hazelcast.mapreduce.impl.task.JobSupervisor;
import com.hazelcast.nio.Address;
import com.hazelcast.nio.serialization.BinaryInterface;
import com.hazelcast.spi.NodeEngine;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;

@BinaryInterface
class KeyValueSourceFacade<K, V>
extends KeyValueSource<K, V> {
    private static final int UPDATE_PROCESSED_RECORDS_INTERVAL = 1000;
    private final ILogger logger;
    private final KeyValueSource<K, V> keyValueSource;
    private final JobSupervisor supervisor;
    private int processedRecords;

    KeyValueSourceFacade(KeyValueSource<K, V> keyValueSource, JobSupervisor supervisor) {
        this.keyValueSource = keyValueSource;
        this.supervisor = supervisor;
        this.logger = supervisor.getMapReduceService().getNodeEngine().getLogger(KeyValueSourceFacade.class);
    }

    @Override
    public boolean open(NodeEngine nodeEngine) {
        return this.keyValueSource.open(nodeEngine);
    }

    @Override
    public boolean hasNext() {
        return this.keyValueSource.hasNext();
    }

    @Override
    public K key() {
        K key = this.keyValueSource.key();
        ++this.processedRecords;
        if (this.processedRecords == 1000) {
            this.notifyProcessStats();
            this.processedRecords = 0;
        }
        return key;
    }

    @Override
    public Map.Entry<K, V> element() {
        return this.keyValueSource.element();
    }

    @Override
    public boolean reset() {
        this.processedRecords = 0;
        return this.keyValueSource.reset();
    }

    @Override
    public boolean isAllKeysSupported() {
        return this.keyValueSource.isAllKeysSupported();
    }

    @Override
    protected Collection<K> getAllKeys0() {
        return this.keyValueSource.getAllKeys();
    }

    @Override
    public void close() throws IOException {
        this.notifyProcessStats();
        this.keyValueSource.close();
    }

    private void notifyProcessStats() {
        if (this.processedRecords > 0) {
            try {
                MapReduceService mapReduceService = this.supervisor.getMapReduceService();
                String name = this.supervisor.getConfiguration().getName();
                String jobId = this.supervisor.getConfiguration().getJobId();
                Address jobOwner = this.supervisor.getJobOwner();
                mapReduceService.processRequest(jobOwner, new ProcessStatsUpdateOperation(name, jobId, this.processedRecords));
            }
            catch (Exception ignore) {
                this.logger.finest("ProcessedRecords update couldn't be executed", ignore);
            }
        }
    }
}

