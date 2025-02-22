/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.management.dto;

import com.hazelcast.config.EvictionPolicy;
import com.hazelcast.config.InMemoryFormat;
import com.hazelcast.config.MapConfig;
import com.hazelcast.config.MaxSizeConfig;
import com.hazelcast.internal.json.JsonObject;
import com.hazelcast.internal.management.JsonSerializable;
import com.hazelcast.internal.management.ManagementDataSerializerHook;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.util.JsonUtil;
import java.io.IOException;

public class MapConfigDTO
implements JsonSerializable,
IdentifiedDataSerializable {
    private MapConfig config;

    public MapConfigDTO() {
    }

    public MapConfigDTO(MapConfig mapConfig) {
        this.config = mapConfig;
    }

    @Override
    public JsonObject toJson() {
        JsonObject root = new JsonObject();
        root.add("name", this.config.getName());
        root.add("memoryFormat", this.config.getInMemoryFormat().toString());
        root.add("backupCount", this.config.getBackupCount());
        root.add("asyncBackupCount", this.config.getAsyncBackupCount());
        root.add("evictionPercentage", this.config.getEvictionPercentage());
        root.add("minEvictionCheckMillis", this.config.getMinEvictionCheckMillis());
        root.add("ttl", this.config.getTimeToLiveSeconds());
        root.add("maxIdle", this.config.getMaxIdleSeconds());
        root.add("maxSize", this.config.getMaxSizeConfig().getSize());
        root.add("maxSizePolicy", this.config.getMaxSizeConfig().getMaxSizePolicy().toString());
        root.add("readBackupData", this.config.isReadBackupData());
        root.add("evictionPolicy", this.config.getEvictionPolicy().name());
        root.add("mergePolicy", this.config.getMergePolicy());
        return root;
    }

    @Override
    public void fromJson(JsonObject json) {
        this.config = new MapConfig();
        this.config.setName(JsonUtil.getString(json, "name"));
        this.config.setInMemoryFormat(InMemoryFormat.valueOf(JsonUtil.getString(json, "memoryFormat")));
        this.config.setBackupCount(JsonUtil.getInt(json, "backupCount"));
        this.config.setAsyncBackupCount(JsonUtil.getInt(json, "asyncBackupCount"));
        this.config.setEvictionPercentage(JsonUtil.getInt(json, "evictionPercentage"));
        this.config.setMinEvictionCheckMillis(JsonUtil.getLong(json, "minEvictionCheckMillis"));
        this.config.setTimeToLiveSeconds(JsonUtil.getInt(json, "ttl"));
        this.config.setMaxIdleSeconds(JsonUtil.getInt(json, "maxIdle"));
        this.config.setMaxSizeConfig(new MaxSizeConfig().setSize(JsonUtil.getInt(json, "maxSize")).setMaxSizePolicy(MaxSizeConfig.MaxSizePolicy.valueOf(JsonUtil.getString(json, "maxSizePolicy"))));
        this.config.setReadBackupData(JsonUtil.getBoolean(json, "readBackupData"));
        this.config.setEvictionPolicy(EvictionPolicy.valueOf(JsonUtil.getString(json, "evictionPolicy")));
        this.config.setMergePolicy(JsonUtil.getString(json, "mergePolicy"));
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.config = new MapConfig();
        this.config.setName(in.readUTF());
        this.config.setInMemoryFormat(InMemoryFormat.valueOf(in.readUTF()));
        this.config.setBackupCount(in.readInt());
        this.config.setAsyncBackupCount(in.readInt());
        this.config.setEvictionPercentage(in.readInt());
        this.config.setMinEvictionCheckMillis(in.readLong());
        this.config.setTimeToLiveSeconds(in.readInt());
        this.config.setMaxIdleSeconds(in.readInt());
        this.config.setMaxSizeConfig(new MaxSizeConfig().setSize(in.readInt()).setMaxSizePolicy(MaxSizeConfig.MaxSizePolicy.valueOf(in.readUTF())));
        this.config.setReadBackupData(in.readBoolean());
        this.config.setEvictionPolicy(EvictionPolicy.valueOf(in.readUTF()));
        this.config.setMergePolicy(in.readUTF());
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeUTF(this.config.getName());
        out.writeUTF(this.config.getInMemoryFormat().toString());
        out.writeInt(this.config.getBackupCount());
        out.writeInt(this.config.getAsyncBackupCount());
        out.writeInt(this.config.getEvictionPercentage());
        out.writeLong(this.config.getMinEvictionCheckMillis());
        out.writeInt(this.config.getTimeToLiveSeconds());
        out.writeInt(this.config.getMaxIdleSeconds());
        out.writeInt(this.config.getMaxSizeConfig().getSize());
        out.writeUTF(this.config.getMaxSizeConfig().getMaxSizePolicy().toString());
        out.writeBoolean(this.config.isReadBackupData());
        out.writeUTF(this.config.getEvictionPolicy().name());
        out.writeUTF(this.config.getMergePolicy());
    }

    public MapConfig getMapConfig() {
        return this.config;
    }

    @Override
    public int getFactoryId() {
        return ManagementDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 3;
    }
}

