/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.client;

public class DistributedObjectInfo {
    private String serviceName;
    private String name;

    public DistributedObjectInfo() {
    }

    public DistributedObjectInfo(String serviceName, String name) {
        this.serviceName = serviceName;
        this.name = name;
    }

    public String getServiceName() {
        return this.serviceName;
    }

    public String getName() {
        return this.name;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        DistributedObjectInfo that = (DistributedObjectInfo)o;
        if (this.name != null ? !this.name.equals(that.name) : that.name != null) {
            return false;
        }
        return !(this.serviceName != null ? !this.serviceName.equals(that.serviceName) : that.serviceName != null);
    }

    public int hashCode() {
        int result = this.serviceName != null ? this.serviceName.hashCode() : 0;
        result = 31 * result + (this.name != null ? this.name.hashCode() : 0);
        return result;
    }
}

