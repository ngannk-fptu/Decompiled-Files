/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config;

import com.hazelcast.config.AbstractConfigLocator;

public class XmlConfigLocator
extends AbstractConfigLocator {
    public XmlConfigLocator() {
        super(false);
    }

    @Override
    public boolean locateFromSystemProperty() {
        return this.loadFromSystemProperty("hazelcast.config", new String[0]);
    }

    @Override
    protected boolean locateInWorkDir() {
        return this.loadFromWorkingDirectory("hazelcast.xml");
    }

    @Override
    protected boolean locateOnClasspath() {
        return this.loadConfigurationFromClasspath("hazelcast.xml");
    }

    @Override
    public boolean locateDefault() {
        this.loadDefaultConfigurationFromClasspath("hazelcast-default.xml");
        return true;
    }
}

