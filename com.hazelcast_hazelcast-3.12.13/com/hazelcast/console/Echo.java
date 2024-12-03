/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.console;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.HazelcastInstanceAware;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.BinaryInterface;
import com.hazelcast.nio.serialization.DataSerializable;
import java.io.IOException;
import java.util.concurrent.Callable;

@BinaryInterface
public class Echo
implements Callable<String>,
DataSerializable,
HazelcastInstanceAware {
    String input;
    private transient HazelcastInstance hz;

    public Echo() {
    }

    public Echo(String input) {
        this.input = input;
    }

    @Override
    public String call() {
        this.hz.getCountDownLatch("latch").countDown();
        return this.hz.getCluster().getLocalMember().toString() + ":" + this.input;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeUTF(this.input);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.input = in.readUTF();
    }

    @Override
    public void setHazelcastInstance(HazelcastInstance hazelcastInstance) {
        this.hz = hazelcastInstance;
    }
}

