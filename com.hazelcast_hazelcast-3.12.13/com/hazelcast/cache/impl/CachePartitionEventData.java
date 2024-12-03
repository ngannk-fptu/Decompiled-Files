/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cache.impl;

import com.hazelcast.cache.CacheEventType;
import com.hazelcast.cache.impl.CacheEventData;
import com.hazelcast.cache.impl.CacheEventDataImpl;
import com.hazelcast.core.Member;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.BinaryInterface;
import java.io.IOException;

@BinaryInterface
public class CachePartitionEventData
extends CacheEventDataImpl
implements CacheEventData {
    private int partitionId;
    private Member member;

    public CachePartitionEventData() {
    }

    public CachePartitionEventData(String name, int partitionId, Member member) {
        super(name, CacheEventType.PARTITION_LOST, null, null, null, false);
        this.partitionId = partitionId;
        this.member = member;
    }

    public Member getMember() {
        return this.member;
    }

    public int getPartitionId() {
        return this.partitionId;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        super.writeData(out);
        out.writeInt(this.partitionId);
        out.writeObject(this.member);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        super.readData(in);
        this.partitionId = in.readInt();
        this.member = (Member)in.readObject();
    }

    @Override
    public String toString() {
        return "CachePartitionEventData{" + super.toString() + ", partitionId=" + this.partitionId + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CachePartitionEventData)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        CachePartitionEventData that = (CachePartitionEventData)o;
        if (this.partitionId != that.partitionId) {
            return false;
        }
        return !(this.member != null ? !this.member.equals(that.member) : that.member != null);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + this.partitionId;
        result = 31 * result + (this.member != null ? this.member.hashCode() : 0);
        return result;
    }

    @Override
    public int getId() {
        return 49;
    }
}

