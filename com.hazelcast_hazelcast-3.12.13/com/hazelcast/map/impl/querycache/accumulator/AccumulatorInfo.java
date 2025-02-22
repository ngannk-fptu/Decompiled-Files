/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.querycache.accumulator;

import com.hazelcast.config.QueryCacheConfig;
import com.hazelcast.map.impl.client.MapPortableHook;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Portable;
import com.hazelcast.nio.serialization.PortableReader;
import com.hazelcast.nio.serialization.PortableWriter;
import com.hazelcast.query.Predicate;
import com.hazelcast.util.Preconditions;
import java.io.IOException;

public class AccumulatorInfo
implements Portable {
    private String mapName;
    private String cacheId;
    private Predicate predicate;
    private int batchSize;
    private int bufferSize;
    private long delaySeconds;
    private boolean includeValue;
    private boolean populate;
    private boolean coalesce;
    private volatile boolean publishable;

    public static AccumulatorInfo toAccumulatorInfo(QueryCacheConfig config, String mapName, String cacheId, Predicate predicate) {
        Preconditions.checkNotNull(config, "config cannot be null");
        AccumulatorInfo info = new AccumulatorInfo();
        info.mapName = mapName;
        info.cacheId = cacheId;
        info.batchSize = AccumulatorInfo.calculateBatchSize(config);
        info.bufferSize = config.getBufferSize();
        info.delaySeconds = config.getDelaySeconds();
        info.includeValue = config.isIncludeValue();
        info.populate = config.isPopulate();
        info.predicate = AccumulatorInfo.getPredicate(config, predicate);
        info.coalesce = config.isCoalesce();
        info.publishable = false;
        return info;
    }

    public static AccumulatorInfo toAccumulatorInfo(String mapName, String cacheId, Predicate predicate, int batchSize, int bufferSize, long delaySeconds, boolean includeValue, boolean populate, boolean coalesce) {
        AccumulatorInfo info = new AccumulatorInfo();
        info.mapName = mapName;
        info.cacheId = cacheId;
        info.batchSize = batchSize;
        info.bufferSize = bufferSize;
        info.delaySeconds = delaySeconds;
        info.includeValue = includeValue;
        info.populate = populate;
        info.predicate = predicate;
        info.coalesce = coalesce;
        info.publishable = false;
        return info;
    }

    private static Predicate getPredicate(QueryCacheConfig config, Predicate predicate) {
        if (predicate != null) {
            return predicate;
        }
        Predicate implementation = config.getPredicateConfig().getImplementation();
        if (implementation != null) {
            return implementation;
        }
        throw new IllegalArgumentException("Predicate cannot be null");
    }

    private static int calculateBatchSize(QueryCacheConfig config) {
        int bufferSize;
        int batchSize = config.getBatchSize();
        return batchSize > (bufferSize = config.getBufferSize()) ? bufferSize : batchSize;
    }

    public int getBatchSize() {
        return this.batchSize;
    }

    public int getBufferSize() {
        return this.bufferSize;
    }

    public long getDelaySeconds() {
        return this.delaySeconds;
    }

    public boolean isIncludeValue() {
        return this.includeValue;
    }

    public String getMapName() {
        return this.mapName;
    }

    public String getCacheId() {
        return this.cacheId;
    }

    public Predicate getPredicate() {
        return this.predicate;
    }

    public boolean isPublishable() {
        return this.publishable;
    }

    public boolean isPopulate() {
        return this.populate;
    }

    public void setPublishable(boolean publishable) {
        this.publishable = publishable;
    }

    public boolean isCoalesce() {
        return this.coalesce;
    }

    @Override
    public int getFactoryId() {
        return MapPortableHook.F_ID;
    }

    @Override
    public int getClassId() {
        return 1;
    }

    @Override
    public void writePortable(PortableWriter writer) throws IOException {
        writer.writeUTF("mn", this.mapName);
        writer.writeUTF("cn", this.cacheId);
        writer.writeInt("bas", this.batchSize);
        writer.writeInt("bus", this.bufferSize);
        writer.writeLong("ds", this.delaySeconds);
        writer.writeBoolean("iv", this.includeValue);
        writer.writeBoolean("ps", this.publishable);
        writer.writeBoolean("co", this.coalesce);
        writer.writeBoolean("po", this.populate);
        ObjectDataOutput output = writer.getRawDataOutput();
        output.writeObject(this.predicate);
    }

    @Override
    public void readPortable(PortableReader reader) throws IOException {
        this.mapName = reader.readUTF("mn");
        this.cacheId = reader.readUTF("cn");
        this.batchSize = reader.readInt("bas");
        this.bufferSize = reader.readInt("bus");
        this.delaySeconds = reader.readLong("ds");
        this.includeValue = reader.readBoolean("iv");
        this.publishable = reader.readBoolean("ps");
        this.coalesce = reader.readBoolean("co");
        this.populate = reader.readBoolean("po");
        ObjectDataInput input = reader.getRawDataInput();
        this.predicate = (Predicate)input.readObject();
    }

    public String toString() {
        return "AccumulatorInfo{batchSize=" + this.batchSize + ", mapName='" + this.mapName + '\'' + ", cacheId='" + this.cacheId + '\'' + ", predicate=" + this.predicate + ", bufferSize=" + this.bufferSize + ", delaySeconds=" + this.delaySeconds + ", includeValue=" + this.includeValue + ", populate=" + this.populate + ", coalesce=" + this.coalesce + ", publishable=" + this.publishable + '}';
    }
}

