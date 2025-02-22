/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi.impl.eventservice.impl;

import com.hazelcast.nio.Address;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.BinaryInterface;
import com.hazelcast.spi.EventFilter;
import com.hazelcast.spi.EventRegistration;
import com.hazelcast.util.Preconditions;
import java.io.IOException;

@BinaryInterface
public class Registration
implements EventRegistration {
    private String id;
    private String serviceName;
    private String topic;
    private EventFilter filter;
    private Address subscriber;
    private transient boolean localOnly;
    private transient Object listener;

    public Registration() {
    }

    public Registration(String id, String serviceName, String topic, EventFilter filter, Address subscriber, Object listener, boolean localOnly) {
        this.id = Preconditions.checkNotNull(id, "Registration ID cannot be null!");
        this.filter = filter;
        this.listener = listener;
        this.serviceName = serviceName;
        this.topic = topic;
        this.subscriber = subscriber;
        this.localOnly = localOnly;
    }

    public String getServiceName() {
        return this.serviceName;
    }

    public String getTopic() {
        return this.topic;
    }

    @Override
    public EventFilter getFilter() {
        return this.filter;
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public Address getSubscriber() {
        return this.subscriber;
    }

    @Override
    public boolean isLocalOnly() {
        return this.localOnly;
    }

    public Object getListener() {
        return this.listener;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Registration)) {
            return false;
        }
        Registration that = (Registration)o;
        return this.id.equals(that.id);
    }

    public int hashCode() {
        return this.id.hashCode();
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeUTF(this.id);
        out.writeUTF(this.serviceName);
        out.writeUTF(this.topic);
        this.subscriber.writeData(out);
        out.writeObject(this.filter);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.id = in.readUTF();
        this.serviceName = in.readUTF();
        this.topic = in.readUTF();
        this.subscriber = new Address();
        this.subscriber.readData(in);
        this.filter = (EventFilter)in.readObject();
    }

    public String toString() {
        return "Registration{filter=" + this.filter + ", id='" + this.id + '\'' + ", serviceName='" + this.serviceName + '\'' + ", subscriber=" + this.subscriber + ", listener=" + this.listener + '}';
    }
}

