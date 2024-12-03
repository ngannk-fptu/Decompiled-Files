/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.mapreduce.impl;

import com.hazelcast.mapreduce.Collator;
import com.hazelcast.mapreduce.CombinerFactory;
import com.hazelcast.mapreduce.Job;
import com.hazelcast.mapreduce.JobCompletableFuture;
import com.hazelcast.mapreduce.JobTracker;
import com.hazelcast.mapreduce.KeyPredicate;
import com.hazelcast.mapreduce.KeyValueSource;
import com.hazelcast.mapreduce.Mapper;
import com.hazelcast.mapreduce.MappingJob;
import com.hazelcast.mapreduce.ReducerFactory;
import com.hazelcast.mapreduce.ReducingJob;
import com.hazelcast.mapreduce.ReducingSubmittableJob;
import com.hazelcast.mapreduce.TopologyChangedStrategy;
import com.hazelcast.util.Preconditions;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public abstract class AbstractJob<KeyIn, ValueIn>
implements Job<KeyIn, ValueIn> {
    protected final String name;
    protected final JobTracker jobTracker;
    protected final KeyValueSource<KeyIn, ValueIn> keyValueSource;
    protected Mapper<KeyIn, ValueIn, ?, ?> mapper;
    protected CombinerFactory<?, ?, ?> combinerFactory;
    protected ReducerFactory<?, ?, ?> reducerFactory;
    protected Collection<KeyIn> keys;
    protected KeyPredicate<? super KeyIn> predicate;
    protected int chunkSize = -1;
    protected TopologyChangedStrategy topologyChangedStrategy;

    public AbstractJob(String name, JobTracker jobTracker, KeyValueSource<KeyIn, ValueIn> keyValueSource) {
        this.name = name;
        this.jobTracker = jobTracker;
        this.keyValueSource = keyValueSource;
    }

    @Override
    public <KeyOut, ValueOut> MappingJob<KeyIn, KeyOut, ValueOut> mapper(Mapper<KeyIn, ValueIn, KeyOut, ValueOut> mapper) {
        Preconditions.isNotNull(mapper, "mapper");
        if (this.mapper != null) {
            throw new IllegalStateException("mapper already set");
        }
        this.mapper = mapper;
        return new MappingJobImpl();
    }

    @Override
    public Job<KeyIn, ValueIn> onKeys(Iterable<? extends KeyIn> keys) {
        this.addKeys(keys);
        return this;
    }

    @Override
    public Job<KeyIn, ValueIn> onKeys(KeyIn ... keys) {
        this.addKeys(keys);
        return this;
    }

    @Override
    public Job<KeyIn, ValueIn> keyPredicate(KeyPredicate<? super KeyIn> predicate) {
        this.setKeyPredicate(predicate);
        return this;
    }

    @Override
    public Job<KeyIn, ValueIn> chunkSize(int chunkSize) {
        this.chunkSize = chunkSize;
        return this;
    }

    @Override
    public Job<KeyIn, ValueIn> topologyChangedStrategy(TopologyChangedStrategy topologyChangedStrategy) {
        this.topologyChangedStrategy = topologyChangedStrategy;
        return this;
    }

    protected <T> JobCompletableFuture<T> submit(Collator collator) {
        this.prepareKeyPredicate();
        return this.invoke(collator);
    }

    protected abstract <T> JobCompletableFuture<T> invoke(Collator var1);

    protected void prepareKeyPredicate() {
        if (this.predicate == null) {
            return;
        }
        if (this.keyValueSource.isAllKeysSupported()) {
            Collection<KeyIn> allKeys = this.keyValueSource.getAllKeys();
            for (KeyIn key : allKeys) {
                if (!this.predicate.evaluate(key)) continue;
                if (this.keys == null) {
                    this.keys = new HashSet<KeyIn>();
                }
                this.keys.add(key);
            }
        }
    }

    private void addKeys(Iterable<? extends KeyIn> keys) {
        if (this.keys == null) {
            this.keys = new HashSet<KeyIn>();
        }
        for (KeyIn key : keys) {
            this.keys.add(key);
        }
    }

    private void addKeys(KeyIn ... keys) {
        if (this.keys == null) {
            this.keys = new ArrayList<KeyIn>();
        }
        this.keys.addAll(Arrays.asList(keys));
    }

    private void setKeyPredicate(KeyPredicate<? super KeyIn> predicate) {
        Preconditions.isNotNull(predicate, "predicate");
        this.predicate = predicate;
    }

    private <T> JobCompletableFuture<T> submit() {
        return this.submit(null);
    }

    protected class ReducingSubmittableJobImpl<EntryKey, Key, Value>
    implements ReducingSubmittableJob<EntryKey, Key, Value> {
        protected ReducingSubmittableJobImpl() {
        }

        @Override
        public ReducingSubmittableJob<EntryKey, Key, Value> onKeys(Iterable<EntryKey> keys) {
            AbstractJob.this.addKeys(keys);
            return this;
        }

        @Override
        public ReducingSubmittableJob<EntryKey, Key, Value> onKeys(EntryKey ... keys) {
            AbstractJob.this.addKeys(keys);
            return this;
        }

        @Override
        public ReducingSubmittableJob<EntryKey, Key, Value> keyPredicate(KeyPredicate<EntryKey> predicate) {
            AbstractJob.this.setKeyPredicate(predicate);
            return this;
        }

        @Override
        public ReducingSubmittableJob<EntryKey, Key, Value> chunkSize(int chunkSize) {
            AbstractJob.this.chunkSize = chunkSize;
            return this;
        }

        @Override
        public ReducingSubmittableJob<EntryKey, Key, Value> topologyChangedStrategy(TopologyChangedStrategy topologyChangedStrategy) {
            AbstractJob.this.topologyChangedStrategy = topologyChangedStrategy;
            return this;
        }

        @Override
        public JobCompletableFuture<Map<Key, Value>> submit() {
            return AbstractJob.this.submit();
        }

        @Override
        public <ValueOut> JobCompletableFuture<ValueOut> submit(Collator<Map.Entry<Key, Value>, ValueOut> collator) {
            return AbstractJob.this.submit(collator);
        }
    }

    protected class ReducingJobImpl<EntryKey, Key, Value>
    implements ReducingJob<EntryKey, Key, Value> {
        protected ReducingJobImpl() {
        }

        @Override
        public <ValueOut> ReducingSubmittableJob<EntryKey, Key, ValueOut> reducer(ReducerFactory<Key, Value, ValueOut> reducerFactory) {
            Preconditions.isNotNull(reducerFactory, "reducerFactory");
            if (AbstractJob.this.reducerFactory != null) {
                throw new IllegalStateException("reducerFactory already set");
            }
            AbstractJob.this.reducerFactory = reducerFactory;
            return new ReducingSubmittableJobImpl();
        }

        @Override
        public ReducingJob<EntryKey, Key, Value> onKeys(Iterable<EntryKey> keys) {
            AbstractJob.this.addKeys(keys);
            return this;
        }

        @Override
        public ReducingJob<EntryKey, Key, Value> onKeys(EntryKey ... keys) {
            AbstractJob.this.addKeys(keys);
            return this;
        }

        @Override
        public ReducingJob<EntryKey, Key, Value> keyPredicate(KeyPredicate<EntryKey> predicate) {
            AbstractJob.this.setKeyPredicate(predicate);
            return this;
        }

        @Override
        public ReducingJob<EntryKey, Key, Value> chunkSize(int chunkSize) {
            AbstractJob.this.chunkSize = chunkSize;
            return this;
        }

        @Override
        public ReducingJob<EntryKey, Key, Value> topologyChangedStrategy(TopologyChangedStrategy topologyChangedStrategy) {
            AbstractJob.this.topologyChangedStrategy = topologyChangedStrategy;
            return this;
        }

        @Override
        public JobCompletableFuture<Map<Key, List<Value>>> submit() {
            return AbstractJob.this.submit();
        }

        @Override
        public <ValueOut> JobCompletableFuture<ValueOut> submit(Collator<Map.Entry<Key, List<Value>>, ValueOut> collator) {
            return AbstractJob.this.submit(collator);
        }
    }

    protected class MappingJobImpl<EntryKey, Key, Value>
    implements MappingJob<EntryKey, Key, Value> {
        protected MappingJobImpl() {
        }

        @Override
        public MappingJob<EntryKey, Key, Value> onKeys(Iterable<? extends EntryKey> keys) {
            AbstractJob.this.addKeys(keys);
            return this;
        }

        @Override
        public MappingJob<EntryKey, Key, Value> onKeys(EntryKey ... keys) {
            AbstractJob.this.addKeys(keys);
            return this;
        }

        @Override
        public MappingJob<EntryKey, Key, Value> keyPredicate(KeyPredicate<? super EntryKey> predicate) {
            AbstractJob.this.setKeyPredicate(predicate);
            return this;
        }

        @Override
        public MappingJob<EntryKey, Key, Value> chunkSize(int chunkSize) {
            AbstractJob.this.chunkSize = chunkSize;
            return this;
        }

        @Override
        public MappingJob<EntryKey, Key, Value> topologyChangedStrategy(TopologyChangedStrategy topologyChangedStrategy) {
            AbstractJob.this.topologyChangedStrategy = topologyChangedStrategy;
            return this;
        }

        @Override
        public <ValueOut> ReducingJob<EntryKey, Key, ValueOut> combiner(CombinerFactory<? super Key, ? super Value, ? extends ValueOut> combinerFactory) {
            Preconditions.isNotNull(combinerFactory, "combinerFactory");
            if (AbstractJob.this.combinerFactory != null) {
                throw new IllegalStateException("combinerFactory already set");
            }
            AbstractJob.this.combinerFactory = combinerFactory;
            return new ReducingJobImpl();
        }

        @Override
        public <ValueOut> ReducingSubmittableJob<EntryKey, Key, ValueOut> reducer(ReducerFactory<? super Key, ? super Value, ? extends ValueOut> reducerFactory) {
            Preconditions.isNotNull(reducerFactory, "reducerFactory");
            if (AbstractJob.this.reducerFactory != null) {
                throw new IllegalStateException("reducerFactory already set");
            }
            AbstractJob.this.reducerFactory = reducerFactory;
            return new ReducingSubmittableJobImpl();
        }

        @Override
        public JobCompletableFuture<Map<Key, List<Value>>> submit() {
            return AbstractJob.this.submit();
        }

        @Override
        public <ValueOut> JobCompletableFuture<ValueOut> submit(Collator<Map.Entry<Key, List<Value>>, ValueOut> collator) {
            return AbstractJob.this.submit(collator);
        }
    }
}

