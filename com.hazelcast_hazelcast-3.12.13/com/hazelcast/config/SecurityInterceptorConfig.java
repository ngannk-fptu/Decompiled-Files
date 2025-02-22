/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config;

import com.hazelcast.security.SecurityInterceptor;

public class SecurityInterceptorConfig {
    protected String className;
    protected SecurityInterceptor implementation;

    public SecurityInterceptorConfig() {
    }

    public SecurityInterceptorConfig(String className) {
        this.className = className;
    }

    public SecurityInterceptorConfig(SecurityInterceptor implementation) {
        this.implementation = implementation;
    }

    public String getClassName() {
        return this.className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public SecurityInterceptor getImplementation() {
        return this.implementation;
    }

    public void setImplementation(SecurityInterceptor implementation) {
        this.implementation = implementation;
    }
}

