/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.concurrent.countdownlatch;

import com.hazelcast.concurrent.countdownlatch.CountDownLatchDataSerializerHook;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import java.io.IOException;

public class CountDownLatchContainer
implements IdentifiedDataSerializable {
    private String name;
    private int count;

    public CountDownLatchContainer() {
    }

    public CountDownLatchContainer(String name) {
        this.name = name;
    }

    public int countDown() {
        if (this.count > 0) {
            --this.count;
        }
        return this.count;
    }

    public int getCount() {
        return this.count;
    }

    public String getName() {
        return this.name;
    }

    public boolean setCount(int count) {
        if (this.count > 0 || count <= 0) {
            return false;
        }
        this.count = count;
        return true;
    }

    public void setCountDirect(int count) {
        this.count = count;
    }

    @Override
    public int getFactoryId() {
        return CountDownLatchDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 0;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeUTF(this.name);
        out.writeInt(this.count);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.name = in.readUTF();
        this.count = in.readInt();
    }

    public String toString() {
        return "LocalCountDownLatch{name='" + this.name + '\'' + ", count=" + this.count + '}';
    }
}

