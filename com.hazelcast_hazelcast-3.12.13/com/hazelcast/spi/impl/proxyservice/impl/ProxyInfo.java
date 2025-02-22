/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi.impl.proxyservice.impl;

public final class ProxyInfo {
    private final String serviceName;
    private final String objectName;

    public ProxyInfo(String serviceName, String objectName) {
        this.serviceName = serviceName;
        this.objectName = objectName;
    }

    public String getServiceName() {
        return this.serviceName;
    }

    public String getObjectName() {
        return this.objectName;
    }

    public String toString() {
        return "ProxyInfo{serviceName='" + this.serviceName + "', objectName='" + this.objectName + "'}";
    }
}

