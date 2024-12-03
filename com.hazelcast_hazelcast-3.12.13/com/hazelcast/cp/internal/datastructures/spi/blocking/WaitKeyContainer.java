/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal.datastructures.spi.blocking;

import com.hazelcast.cp.internal.datastructures.RaftDataServiceDataSerializerHook;
import com.hazelcast.cp.internal.datastructures.spi.blocking.WaitKey;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.util.Preconditions;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class WaitKeyContainer<W extends WaitKey>
implements IdentifiedDataSerializable {
    private W key;
    private List<W> keys;

    public WaitKeyContainer() {
    }

    WaitKeyContainer(W key) {
        this.key = key;
    }

    public W key() {
        return this.key;
    }

    public Collection<W> retries() {
        return this.keys != null ? this.keys.subList(1, this.keys.size()) : Collections.emptyList();
    }

    public Collection<W> keyAndRetries() {
        if (this.keys == null) {
            return Collections.singletonList(this.key);
        }
        return this.keys;
    }

    public int retryCount() {
        return this.keys != null ? this.keys.size() - 1 : 0;
    }

    public long sessionId() {
        return ((WaitKey)this.key).sessionId();
    }

    public UUID invocationUid() {
        return ((WaitKey)this.key).invocationUid();
    }

    void addRetry(W retry) {
        Preconditions.checkTrue(this.sessionId() == ((WaitKey)this.key).sessionId(), this.key + " and its retry: " + retry + " has different session ids!");
        Preconditions.checkTrue(((WaitKey)this.key).invocationUid().equals(((WaitKey)retry).invocationUid()), this.key + " and its retry: " + retry + " has different invocation uids!");
        if (this.keys == null) {
            this.keys = new ArrayList<W>(3);
            this.keys.add(this.key);
        }
        this.keys.add(retry);
    }

    @Override
    public int getFactoryId() {
        return RaftDataServiceDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 1;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeObject(this.key);
        Collection<W> retries = this.retries();
        out.writeInt(retries.size());
        for (WaitKey retry : retries) {
            out.writeObject(retry);
        }
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.key = (WaitKey)in.readObject();
        int retryCount = in.readInt();
        if (retryCount > 0) {
            this.keys = new ArrayList<W>(retryCount + 1);
            this.keys.add(this.key);
            for (int i = 0; i < retryCount; ++i) {
                WaitKey retry = (WaitKey)in.readObject();
                this.keys.add(retry);
            }
        }
    }

    public String toString() {
        return "WaitKeyContainer{key=" + this.key + ", retries=" + this.retries() + '}';
    }
}

