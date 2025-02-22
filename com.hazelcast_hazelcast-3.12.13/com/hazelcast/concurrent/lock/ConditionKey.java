/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.concurrent.lock;

import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.WaitNotifyKey;

public final class ConditionKey
implements WaitNotifyKey {
    private final String name;
    private final Data key;
    private final String conditionId;
    private final long threadId;
    private final String uuid;

    public ConditionKey(String name, Data key, String conditionId, String uuid, long threadId) {
        this.name = name;
        this.key = key;
        this.conditionId = conditionId;
        this.uuid = uuid;
        this.threadId = threadId;
    }

    @Override
    public String getServiceName() {
        return "hz:impl:lockService";
    }

    public String getUuid() {
        return this.uuid;
    }

    @Override
    public String getObjectName() {
        return this.name;
    }

    public Data getKey() {
        return this.key;
    }

    public String getConditionId() {
        return this.conditionId;
    }

    public long getThreadId() {
        return this.threadId;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ConditionKey)) {
            return false;
        }
        ConditionKey that = (ConditionKey)o;
        if (this.threadId != that.threadId) {
            return false;
        }
        if (!this.name.equals(that.name)) {
            return false;
        }
        if (!this.key.equals(that.key)) {
            return false;
        }
        if (!this.uuid.equals(that.uuid)) {
            return false;
        }
        return this.conditionId.equals(that.conditionId);
    }

    public int hashCode() {
        int result = this.name.hashCode();
        result = 31 * result + this.key.hashCode();
        result = 31 * result + this.conditionId.hashCode();
        result = 31 * result + (int)(this.threadId ^ this.threadId >>> 32);
        result = 31 * result + this.uuid.hashCode();
        return result;
    }

    public String toString() {
        return "ConditionKey{name='" + this.name + '\'' + ", key=" + this.key + ", conditionId='" + this.conditionId + '\'' + ", threadId=" + this.threadId + '}';
    }
}

