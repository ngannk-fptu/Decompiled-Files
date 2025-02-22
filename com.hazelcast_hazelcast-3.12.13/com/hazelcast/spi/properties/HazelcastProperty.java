/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi.properties;

import com.hazelcast.util.Preconditions;
import java.util.concurrent.TimeUnit;

public final class HazelcastProperty {
    private final String name;
    private final String defaultValue;
    private final TimeUnit timeUnit;
    private final HazelcastProperty parent;
    private volatile String deprecatedName;

    public HazelcastProperty(String name) {
        this(name, (String)null);
    }

    public HazelcastProperty(String name, boolean defaultValue) {
        this(name, defaultValue ? "true" : "false");
    }

    public HazelcastProperty(String name, Integer defaultValue) {
        this(name, String.valueOf(defaultValue));
    }

    public HazelcastProperty(String name, Byte defaultValue) {
        this(name, String.valueOf(defaultValue));
    }

    public HazelcastProperty(String name, Integer defaultValue, TimeUnit timeUnit) {
        this(name, String.valueOf(defaultValue), timeUnit);
    }

    public HazelcastProperty(String name, Long defaultValue, TimeUnit timeUnit) {
        this(name, Long.toString(defaultValue), timeUnit);
    }

    public HazelcastProperty(String name, HazelcastProperty groupProperty) {
        this(name, groupProperty.getDefaultValue(), groupProperty.timeUnit, groupProperty);
    }

    public HazelcastProperty(String name, String defaultValue) {
        this(name, defaultValue, null);
    }

    protected HazelcastProperty(String name, String defaultValue, TimeUnit timeUnit) {
        this(name, defaultValue, timeUnit, null);
    }

    public HazelcastProperty(String name, String defaultValue, TimeUnit timeUnit, HazelcastProperty parent) {
        Preconditions.checkHasText(name, "The property name cannot be null or empty!");
        this.name = name;
        this.defaultValue = defaultValue;
        this.timeUnit = timeUnit;
        this.parent = parent;
    }

    public HazelcastProperty setDeprecatedName(String deprecatedName) {
        this.deprecatedName = Preconditions.checkHasText(deprecatedName, "a valid string should be provided");
        return this;
    }

    public String getDeprecatedName() {
        return this.deprecatedName;
    }

    public String getName() {
        return this.name;
    }

    public String getDefaultValue() {
        return this.defaultValue;
    }

    public TimeUnit getTimeUnit() {
        if (this.timeUnit == null) {
            throw new IllegalArgumentException(String.format("groupProperty %s has no TimeUnit defined!", this));
        }
        return this.timeUnit;
    }

    public HazelcastProperty getParent() {
        return this.parent;
    }

    public void setSystemProperty(String value) {
        System.setProperty(this.name, value);
    }

    public String getSystemProperty() {
        return System.getProperty(this.name);
    }

    public String toString() {
        return this.name;
    }
}

