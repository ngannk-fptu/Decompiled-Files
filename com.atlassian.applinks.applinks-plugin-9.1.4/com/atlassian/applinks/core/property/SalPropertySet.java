/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.PropertySet
 *  com.atlassian.sal.api.pluginsettings.PluginSettings
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.applinks.core.property;

import com.atlassian.applinks.api.PropertySet;
import com.atlassian.sal.api.pluginsettings.PluginSettings;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SalPropertySet
implements PropertySet {
    private final PluginSettings pluginSettings;
    private final String keyPrefix;
    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private final Lock write = this.readWriteLock.writeLock();
    private final Lock read = this.readWriteLock.readLock();
    private static final Logger LOG = LoggerFactory.getLogger(SalPropertySet.class);

    public SalPropertySet(PluginSettings pluginSettings, String keyPrefix) {
        this.pluginSettings = pluginSettings;
        this.keyPrefix = keyPrefix;
    }

    public Object getProperty(String s) {
        try {
            this.read.lock();
            Object object = this.pluginSettings.get(this.namespace(s));
            return object;
        }
        finally {
            this.read.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Object putProperty(String s, Object o) {
        String namespace = this.namespace(s);
        if (LOG.isDebugEnabled()) {
            String message = String.format("Putting property [%s] as namespace [%s] with value [%s]", s, namespace, o);
            LOG.debug(message);
        }
        try {
            this.write.lock();
            Object object = this.pluginSettings.put(this.namespace(s), o);
            return object;
        }
        finally {
            this.write.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Object removeProperty(String s) {
        String namespace = this.namespace(s);
        if (LOG.isDebugEnabled()) {
            String message = String.format("Removing property [%s] as namespace [%s] with value [%s]", s, namespace, this.pluginSettings.get(namespace));
            LOG.debug(message);
        }
        try {
            this.write.lock();
            Object object = this.pluginSettings.remove(namespace);
            return object;
        }
        finally {
            this.write.unlock();
        }
    }

    private String namespace(String key) {
        return this.keyPrefix + "." + key;
    }
}

