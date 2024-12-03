/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.cluster.impl;

import com.hazelcast.config.Config;
import com.hazelcast.config.GroupConfig;
import com.hazelcast.config.PartitionGroupConfig;
import com.hazelcast.internal.cluster.impl.ConfigMismatchException;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.spi.properties.GroupProperty;
import com.hazelcast.util.EmptyStatement;
import com.hazelcast.util.MapUtil;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public final class ConfigCheck
implements IdentifiedDataSerializable {
    private static final String EMPTY_PWD = "";
    private String groupName;
    private String joinerType;
    private boolean partitionGroupEnabled;
    private PartitionGroupConfig.MemberGroupType memberGroupType;
    private Map<String, String> properties = new HashMap<String, String>();
    private final Map<String, Object> maps = new HashMap<String, Object>();
    private final Map<String, Object> queues = new HashMap<String, Object>();

    public ConfigCheck() {
    }

    public ConfigCheck(Config config, String joinerType) {
        PartitionGroupConfig partitionGroupConfig;
        this.joinerType = joinerType;
        this.properties.put(GroupProperty.PARTITION_COUNT.getName(), config.getProperty(GroupProperty.PARTITION_COUNT.getName()));
        this.properties.put(GroupProperty.APPLICATION_VALIDATION_TOKEN.getName(), config.getProperty(GroupProperty.APPLICATION_VALIDATION_TOKEN.getName()));
        GroupConfig groupConfig = config.getGroupConfig();
        if (groupConfig != null) {
            this.groupName = groupConfig.getName();
        }
        if ((partitionGroupConfig = config.getPartitionGroupConfig()) != null) {
            this.partitionGroupEnabled = partitionGroupConfig.isEnabled();
            this.memberGroupType = this.partitionGroupEnabled ? partitionGroupConfig.getGroupType() : PartitionGroupConfig.MemberGroupType.PER_MEMBER;
        }
    }

    public boolean isCompatible(ConfigCheck found) {
        if (!ConfigCheck.equals(this.groupName, found.groupName)) {
            return false;
        }
        this.verifyJoiner(found);
        this.verifyPartitionGroup(found);
        this.verifyPartitionCount(found);
        this.verifyApplicationValidationToken(found);
        return true;
    }

    public boolean isSameGroup(ConfigCheck found) {
        return ConfigCheck.equals(this.groupName, found.groupName);
    }

    private void verifyApplicationValidationToken(ConfigCheck found) {
        String foundValidationToken;
        String expectedValidationToken = this.properties.get(GroupProperty.APPLICATION_VALIDATION_TOKEN.getName());
        if (!ConfigCheck.equals(expectedValidationToken, foundValidationToken = found.properties.get(GroupProperty.APPLICATION_VALIDATION_TOKEN.getName()))) {
            throw new ConfigMismatchException("Incompatible '" + GroupProperty.APPLICATION_VALIDATION_TOKEN + "'! expected: " + expectedValidationToken + ", found: " + foundValidationToken);
        }
    }

    private void verifyPartitionCount(ConfigCheck found) {
        String foundPartitionCount;
        String expectedPartitionCount = this.properties.get(GroupProperty.PARTITION_COUNT.getName());
        if (!ConfigCheck.equals(expectedPartitionCount, foundPartitionCount = found.properties.get(GroupProperty.PARTITION_COUNT.getName()))) {
            throw new ConfigMismatchException("Incompatible partition count! expected: " + expectedPartitionCount + ", found: " + foundPartitionCount);
        }
    }

    private void verifyPartitionGroup(ConfigCheck found) {
        if (!this.partitionGroupEnabled && found.partitionGroupEnabled || this.partitionGroupEnabled && !found.partitionGroupEnabled) {
            throw new ConfigMismatchException("Incompatible partition groups! expected: " + (this.partitionGroupEnabled ? "enabled" : "disabled") + " / " + (Object)((Object)this.memberGroupType) + ", found: " + (found.partitionGroupEnabled ? "enabled" : "disabled") + " / " + (Object)((Object)found.memberGroupType));
        }
        if (this.partitionGroupEnabled && this.memberGroupType != found.memberGroupType) {
            throw new ConfigMismatchException("Incompatible partition groups! expected: " + (Object)((Object)this.memberGroupType) + ", found: " + (Object)((Object)found.memberGroupType));
        }
    }

    private void verifyJoiner(ConfigCheck found) {
        if (!ConfigCheck.equals(this.joinerType, found.joinerType)) {
            throw new ConfigMismatchException("Incompatible joiners! expected: " + this.joinerType + ", found: " + found.joinerType);
        }
    }

    private static boolean equals(String thisValue, String thatValue) {
        if (thisValue == null) {
            return thatValue == null;
        }
        return thisValue.equals(thatValue);
    }

    @Override
    public int getFactoryId() {
        return 0;
    }

    @Override
    public int getId() {
        return 4;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeUTF(this.groupName);
        out.writeUTF(EMPTY_PWD);
        out.writeUTF(this.joinerType);
        out.writeBoolean(this.partitionGroupEnabled);
        if (this.partitionGroupEnabled) {
            out.writeUTF(this.memberGroupType.toString());
        }
        out.writeInt(this.properties.size());
        for (Map.Entry<String, String> entry : this.properties.entrySet()) {
            out.writeUTF(entry.getKey());
            out.writeUTF(entry.getValue());
        }
        out.writeInt(this.maps.size());
        for (Map.Entry<String, Object> entry : this.maps.entrySet()) {
            out.writeUTF(entry.getKey());
            out.writeObject(entry.getValue());
        }
        out.writeInt(this.queues.size());
        for (Map.Entry<String, Object> entry : this.queues.entrySet()) {
            out.writeUTF(entry.getKey());
            out.writeObject(entry.getValue());
        }
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.groupName = in.readUTF();
        in.readUTF();
        this.joinerType = in.readUTF();
        this.partitionGroupEnabled = in.readBoolean();
        if (this.partitionGroupEnabled) {
            String s = in.readUTF();
            try {
                this.memberGroupType = PartitionGroupConfig.MemberGroupType.valueOf(s);
            }
            catch (IllegalArgumentException ignored) {
                EmptyStatement.ignore(ignored);
            }
        }
        int propSize = in.readInt();
        this.properties = MapUtil.createHashMap(propSize);
        for (int k = 0; k < propSize; ++k) {
            String key = in.readUTF();
            String value = in.readUTF();
            this.properties.put(key, value);
        }
        int mapSize = in.readInt();
        for (int k = 0; k < mapSize; ++k) {
            String key = in.readUTF();
            Object value = in.readObject();
            this.maps.put(key, value);
        }
        int queueSize = in.readInt();
        for (int k = 0; k < queueSize; ++k) {
            String key = in.readUTF();
            Object value = in.readObject();
            this.queues.put(key, value);
        }
    }
}

