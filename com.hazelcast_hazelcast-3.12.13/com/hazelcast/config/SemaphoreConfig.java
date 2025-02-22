/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config;

import com.hazelcast.config.ConfigDataSerializerHook;
import com.hazelcast.config.NamedConfig;
import com.hazelcast.config.SemaphoreConfigReadOnly;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.nio.serialization.impl.Versioned;
import com.hazelcast.util.Preconditions;
import java.io.IOException;

public class SemaphoreConfig
implements IdentifiedDataSerializable,
Versioned,
NamedConfig {
    public static final int DEFAULT_SYNC_BACKUP_COUNT = 1;
    public static final int DEFAULT_ASYNC_BACKUP_COUNT = 0;
    private String name;
    private int initialPermits;
    private int backupCount = 1;
    private int asyncBackupCount = 0;
    private transient SemaphoreConfigReadOnly readOnly;
    private String quorumName;

    public SemaphoreConfig() {
    }

    public SemaphoreConfig(SemaphoreConfig config) {
        Preconditions.isNotNull(config, "config");
        this.name = config.getName();
        this.initialPermits = config.getInitialPermits();
        this.backupCount = config.getBackupCount();
        this.asyncBackupCount = config.getAsyncBackupCount();
        this.quorumName = config.getQuorumName();
    }

    public SemaphoreConfigReadOnly getAsReadOnly() {
        if (this.readOnly == null) {
            this.readOnly = new SemaphoreConfigReadOnly(this);
        }
        return this.readOnly;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public SemaphoreConfig setName(String name) {
        this.name = Preconditions.checkHasText(name, "name must contain text");
        return this;
    }

    public int getInitialPermits() {
        return this.initialPermits;
    }

    public SemaphoreConfig setInitialPermits(int initialPermits) {
        this.initialPermits = initialPermits;
        return this;
    }

    public int getBackupCount() {
        return this.backupCount;
    }

    public SemaphoreConfig setBackupCount(int backupCount) {
        this.backupCount = Preconditions.checkBackupCount(backupCount, this.asyncBackupCount);
        return this;
    }

    public int getAsyncBackupCount() {
        return this.asyncBackupCount;
    }

    public SemaphoreConfig setAsyncBackupCount(int asyncBackupCount) {
        this.asyncBackupCount = Preconditions.checkAsyncBackupCount(this.backupCount, asyncBackupCount);
        return this;
    }

    public int getTotalBackupCount() {
        return this.asyncBackupCount + this.backupCount;
    }

    public String getQuorumName() {
        return this.quorumName;
    }

    public SemaphoreConfig setQuorumName(String quorumName) {
        this.quorumName = quorumName;
        return this;
    }

    public String toString() {
        return "SemaphoreConfig{name='" + this.name + '\'' + ", initialPermits=" + this.initialPermits + ", backupCount=" + this.backupCount + ", asyncBackupCount=" + this.asyncBackupCount + ", quorumName=" + this.quorumName + '}';
    }

    @Override
    public int getFactoryId() {
        return ConfigDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 33;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeUTF(this.name);
        out.writeInt(this.initialPermits);
        out.writeInt(this.backupCount);
        out.writeInt(this.asyncBackupCount);
        out.writeUTF(this.quorumName);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.name = in.readUTF();
        this.initialPermits = in.readInt();
        this.backupCount = in.readInt();
        this.asyncBackupCount = in.readInt();
        this.quorumName = in.readUTF();
    }

    public final boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SemaphoreConfig)) {
            return false;
        }
        SemaphoreConfig that = (SemaphoreConfig)o;
        if (this.initialPermits != that.initialPermits) {
            return false;
        }
        if (this.backupCount != that.backupCount) {
            return false;
        }
        if (this.asyncBackupCount != that.asyncBackupCount) {
            return false;
        }
        if (this.quorumName != null ? !this.quorumName.equals(that.quorumName) : that.quorumName != null) {
            return false;
        }
        return this.name != null ? this.name.equals(that.name) : that.name == null;
    }

    public final int hashCode() {
        int result = this.name != null ? this.name.hashCode() : 0;
        result = 31 * result + this.initialPermits;
        result = 31 * result + this.backupCount;
        result = 31 * result + this.asyncBackupCount;
        result = 31 * result + (this.quorumName != null ? this.quorumName.hashCode() : 0);
        return result;
    }
}

