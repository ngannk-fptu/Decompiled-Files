/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi;

import com.hazelcast.spi.WaitNotifyKey;

public abstract class AbstractWaitNotifyKey
implements WaitNotifyKey {
    private final String service;
    private final String objectName;

    protected AbstractWaitNotifyKey(String service, String objectName) {
        this.service = service;
        this.objectName = objectName;
    }

    @Override
    public final String getServiceName() {
        return this.service;
    }

    @Override
    public String getObjectName() {
        return this.objectName;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        AbstractWaitNotifyKey that = (AbstractWaitNotifyKey)o;
        if (this.objectName != null ? !this.objectName.equals(that.objectName) : that.objectName != null) {
            return false;
        }
        return !(this.service != null ? !this.service.equals(that.service) : that.service != null);
    }

    public int hashCode() {
        int result = this.service != null ? this.service.hashCode() : 0;
        result = 31 * result + (this.objectName != null ? this.objectName.hashCode() : 0);
        return result;
    }
}

