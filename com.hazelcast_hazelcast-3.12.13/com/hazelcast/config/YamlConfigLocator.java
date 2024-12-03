/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config;

import com.hazelcast.config.AbstractConfigLocator;

public class YamlConfigLocator
extends AbstractConfigLocator {
    public YamlConfigLocator() {
        this(false);
    }

    public YamlConfigLocator(boolean failIfSysPropWithNotExpectedSuffix) {
        super(failIfSysPropWithNotExpectedSuffix);
    }

    @Override
    public boolean locateFromSystemProperty() {
        return this.loadFromSystemProperty("hazelcast.config", "yaml", "yml");
    }

    @Override
    protected boolean locateInWorkDir() {
        return this.loadFromWorkingDirectory("hazelcast.yaml");
    }

    @Override
    protected boolean locateOnClasspath() {
        return this.loadConfigurationFromClasspath("hazelcast.yaml");
    }

    @Override
    public boolean locateDefault() {
        this.loadDefaultConfigurationFromClasspath("hazelcast-default.yaml");
        return true;
    }
}

