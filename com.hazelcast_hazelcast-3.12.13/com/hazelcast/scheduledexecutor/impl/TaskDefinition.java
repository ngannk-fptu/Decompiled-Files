/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.scheduledexecutor.impl;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.scheduledexecutor.impl.ScheduledExecutorDataSerializerHook;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

public class TaskDefinition<V>
implements IdentifiedDataSerializable {
    private Type type;
    private String name;
    private Callable<V> command;
    private long initialDelay;
    private long period;
    private TimeUnit unit;

    public TaskDefinition() {
    }

    public TaskDefinition(Type type, String name, Callable<V> command, long delay, TimeUnit unit) {
        this.type = type;
        this.name = name;
        this.command = command;
        this.initialDelay = delay;
        this.unit = unit;
    }

    public TaskDefinition(Type type, String name, Callable<V> command, long initialDelay, long period, TimeUnit unit) {
        this.type = type;
        this.name = name;
        this.command = command;
        this.initialDelay = initialDelay;
        this.period = period;
        this.unit = unit;
    }

    public Type getType() {
        return this.type;
    }

    public String getName() {
        return this.name;
    }

    public Callable<V> getCommand() {
        return this.command;
    }

    public long getInitialDelay() {
        return this.initialDelay;
    }

    public long getPeriod() {
        return this.period;
    }

    public TimeUnit getUnit() {
        return this.unit;
    }

    @Override
    public int getFactoryId() {
        return ScheduledExecutorDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 3;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeUTF(this.type.name());
        out.writeUTF(this.name);
        out.writeObject(this.command);
        out.writeLong(this.initialDelay);
        out.writeLong(this.period);
        out.writeUTF(this.unit.name());
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.type = Type.valueOf(in.readUTF());
        this.name = in.readUTF();
        this.command = (Callable)in.readObject();
        this.initialDelay = in.readLong();
        this.period = in.readLong();
        this.unit = TimeUnit.valueOf(in.readUTF());
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        TaskDefinition that = (TaskDefinition)o;
        return this.initialDelay == that.initialDelay && this.period == that.period && this.type == that.type && this.name.equals(that.name) && this.unit == that.unit;
    }

    public int hashCode() {
        return Arrays.hashCode(new Object[]{this.type, this.name, this.initialDelay, this.period, this.unit});
    }

    public String toString() {
        return "TaskDefinition{type=" + (Object)((Object)this.type) + ", name='" + this.name + '\'' + ", command=" + this.command + ", initialDelay=" + this.initialDelay + ", period=" + this.period + ", unit=" + (Object)((Object)this.unit) + '}';
    }

    public static enum Type {
        SINGLE_RUN(0),
        AT_FIXED_RATE(1);

        private final byte id;

        private Type(int status) {
            this.id = (byte)status;
        }

        public byte getId() {
            return this.id;
        }

        public static Type getById(int id) {
            for (Type as : Type.values()) {
                if (as.getId() != id) continue;
                return as;
            }
            throw new IllegalArgumentException("Unsupported ID value");
        }
    }
}

