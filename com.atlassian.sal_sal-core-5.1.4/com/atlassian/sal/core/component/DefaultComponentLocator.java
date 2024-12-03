/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.component.ComponentLocator
 *  com.atlassian.sal.spi.HostContextAccessor
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.sal.core.component;

import com.atlassian.sal.api.component.ComponentLocator;
import com.atlassian.sal.spi.HostContextAccessor;
import java.util.Collection;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultComponentLocator
extends ComponentLocator {
    private static final Logger log = LoggerFactory.getLogger(DefaultComponentLocator.class);
    private final HostContextAccessor hostContextAccessor;

    public DefaultComponentLocator(HostContextAccessor accessor) {
        this.hostContextAccessor = accessor;
        ComponentLocator.setComponentLocator((ComponentLocator)this);
    }

    protected <T> T getComponentInternal(Class<T> iface) {
        Map beansOfType = this.hostContextAccessor.getComponentsOfType(iface);
        if (beansOfType == null || beansOfType.isEmpty()) {
            return null;
        }
        if (beansOfType.size() > 1) {
            String shortClassName = this.convertClassToName(iface);
            Object implementation = beansOfType.get(shortClassName);
            if (implementation == null) {
                log.warn("More than one instance of " + iface.getName() + " found but none of them has key " + shortClassName);
            }
            return (T)implementation;
        }
        return (T)beansOfType.values().iterator().next();
    }

    protected <T> T getComponentInternal(Class<T> iface, String componentId) {
        Map beansOfType = this.hostContextAccessor.getComponentsOfType(iface);
        return (T)beansOfType.get(componentId);
    }

    protected <T> Collection<T> getComponentsInternal(Class<T> iface) {
        Map beansOfType = this.hostContextAccessor.getComponentsOfType(iface);
        return beansOfType != null ? beansOfType.values() : null;
    }
}

