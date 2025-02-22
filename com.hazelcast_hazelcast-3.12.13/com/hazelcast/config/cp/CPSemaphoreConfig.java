/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config.cp;

public class CPSemaphoreConfig {
    public static final boolean DEFAULT_SEMAPHORE_JDK_COMPATIBILITY = false;
    private String name;
    private boolean jdkCompatible = false;

    public CPSemaphoreConfig() {
    }

    public CPSemaphoreConfig(String name) {
        this.name = name;
    }

    public CPSemaphoreConfig(String name, boolean jdkCompatible) {
        this.name = name;
        this.jdkCompatible = jdkCompatible;
    }

    CPSemaphoreConfig(CPSemaphoreConfig config) {
        this.name = config.name;
        this.jdkCompatible = config.jdkCompatible;
    }

    public String getName() {
        return this.name;
    }

    public CPSemaphoreConfig setName(String name) {
        this.name = name;
        return this;
    }

    public boolean isJDKCompatible() {
        return this.jdkCompatible;
    }

    public CPSemaphoreConfig setJDKCompatible(boolean jdkCompatible) {
        this.jdkCompatible = jdkCompatible;
        return this;
    }

    public String toString() {
        return "CPSemaphoreConfig{name='" + this.name + ", jdkCompatible=" + this.jdkCompatible + '\'' + '}';
    }
}

