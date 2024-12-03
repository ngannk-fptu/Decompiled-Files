/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.SerializableByConvention;
import com.hazelcast.spi.ObjectNamespace;
import java.io.IOException;

@SerializableByConvention(value=SerializableByConvention.Reason.PUBLIC_API)
@Deprecated
public class DefaultObjectNamespace
implements ObjectNamespace {
    protected String service;
    protected String objectName;

    public DefaultObjectNamespace() {
    }

    public DefaultObjectNamespace(String serviceName, String objectName) {
        this.service = serviceName;
        this.objectName = objectName;
    }

    public DefaultObjectNamespace(ObjectNamespace namespace) {
        this(namespace.getServiceName(), namespace.getObjectName());
    }

    @Override
    public String getServiceName() {
        return this.service;
    }

    @Override
    public String getObjectName() {
        return this.objectName;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeUTF(this.service);
        out.writeObject(this.objectName);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.service = in.readUTF();
        this.objectName = (String)in.readObject();
    }

    public final boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DefaultObjectNamespace)) {
            return false;
        }
        DefaultObjectNamespace that = (DefaultObjectNamespace)o;
        return this.service.equals(that.service) && this.objectName.equals(that.objectName);
    }

    public final int hashCode() {
        int result = this.service.hashCode();
        result = 31 * result + this.objectName.hashCode();
        return result;
    }

    public String toString() {
        return "DefaultObjectNamespace{service='" + this.service + '\'' + ", objectName=" + this.objectName + '}';
    }
}

