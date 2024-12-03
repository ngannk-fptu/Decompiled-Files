/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.opensymphony.module.propertyset.PropertyException
 *  com.opensymphony.module.propertyset.PropertySet
 *  com.opensymphony.module.propertyset.PropertySetSchema
 *  org.apache.commons.lang3.builder.ToStringBuilder
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.user;

import com.opensymphony.module.propertyset.PropertyException;
import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.module.propertyset.PropertySetSchema;
import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Properties;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

public final class DebugLoggingPropertySet
implements PropertySet,
Serializable {
    private static final Logger log = LoggerFactory.getLogger(DebugLoggingPropertySet.class);
    private static final long serialVersionUID = 1L;
    private final PropertySet delegate;

    public DebugLoggingPropertySet(PropertySet delegate) {
        this.delegate = delegate;
    }

    public void setSchema(PropertySetSchema schema) throws PropertyException {
        log.debug("Setting schema '{}' for: {}", (Object)schema, (Object)this);
        this.delegate.setSchema(schema);
    }

    public PropertySetSchema getSchema() throws PropertyException {
        log.debug("Getting schema for: {}", (Object)this);
        return this.delegate.getSchema();
    }

    public void setAsActualType(String key, Object value) throws PropertyException {
        log.debug("Setting key '{}' for: {}", (Object)key, (Object)this);
        this.delegate.setAsActualType(key, value);
    }

    public Object getAsActualType(String key) throws PropertyException {
        log.debug("Getting key '{}' for: {}", (Object)key, (Object)this);
        return this.delegate.getAsActualType(key);
    }

    public void setBoolean(String key, boolean value) throws PropertyException {
        log.debug("Setting key '{}' for: {}", (Object)key, (Object)this);
        this.delegate.setBoolean(key, value);
    }

    public boolean getBoolean(String key) throws PropertyException {
        log.debug("Getting key '{}' for: {}", (Object)key, (Object)this);
        return this.delegate.getBoolean(key);
    }

    public void setData(String key, byte[] value) throws PropertyException {
        log.debug("Setting key '{}' for: {}", (Object)key, (Object)this);
        this.delegate.setData(key, value);
    }

    public byte[] getData(String key) throws PropertyException {
        log.debug("Getting key '{}' for: {}", (Object)key, (Object)this);
        return this.delegate.getData(key);
    }

    public void setDate(String key, Date value) throws PropertyException {
        log.debug("Setting key '{}' for: {}", (Object)key, (Object)this);
        this.delegate.setDate(key, value);
    }

    public Date getDate(String key) throws PropertyException {
        log.debug("Getting key '{}' for: {}", (Object)key, (Object)this);
        return this.delegate.getDate(key);
    }

    public void setDouble(String key, double value) throws PropertyException {
        log.debug("Setting key '{}' for: {}", (Object)key, (Object)this);
        this.delegate.setDouble(key, value);
    }

    public double getDouble(String key) throws PropertyException {
        log.debug("Getting key '{}' for: {}", (Object)key, (Object)this);
        return this.delegate.getDouble(key);
    }

    public void setInt(String key, int value) throws PropertyException {
        log.debug("Setting key '{}' for: {}", (Object)key, (Object)this);
        this.delegate.setInt(key, value);
    }

    public int getInt(String key) throws PropertyException {
        log.debug("Getting key '{}' for: {}", (Object)key, (Object)this);
        return this.delegate.getInt(key);
    }

    public Collection getKeys() throws PropertyException {
        log.debug("Getting keys for: {}", (Object)this);
        return this.delegate.getKeys();
    }

    public Collection getKeys(int type) throws PropertyException {
        log.debug("Getting keys of type '{}' for: {}", (Object)type, (Object)this);
        return this.delegate.getKeys(type);
    }

    public Collection getKeys(String prefix) throws PropertyException {
        log.debug("Getting keys with prefix '{}' for: {}", (Object)prefix, (Object)this);
        return this.delegate.getKeys(prefix);
    }

    public Collection getKeys(String prefix, int type) throws PropertyException {
        log.debug("Getting keys of prefix '{}' and type '{}' for: {}", new Object[]{prefix, type, this});
        return this.delegate.getKeys(prefix, type);
    }

    public void setLong(String key, long value) throws PropertyException {
        log.debug("Setting key '{}' for: {}", (Object)key, (Object)this);
        this.delegate.setLong(key, value);
    }

    public long getLong(String key) throws PropertyException {
        log.debug("Getting key '{}' for: {}", (Object)key, (Object)this);
        return this.delegate.getLong(key);
    }

    public void setObject(String key, Object value) throws PropertyException {
        log.debug("Setting key '{}' for: {}", (Object)key, (Object)this);
        this.delegate.setObject(key, value);
    }

    public Object getObject(String key) throws PropertyException {
        log.debug("Getting key '{}' for: {}", (Object)key, (Object)this);
        return this.delegate.getObject(key);
    }

    public void setProperties(String key, Properties value) throws PropertyException {
        log.debug("Setting key '{}' for: {}", (Object)key, (Object)this);
        this.delegate.setProperties(key, value);
    }

    public Properties getProperties(String key) throws PropertyException {
        log.debug("Getting key '{}' for: {}", (Object)key, (Object)this);
        return this.delegate.getProperties(key);
    }

    public boolean isSettable(String property) {
        log.debug("Checking key '{}' is settable for: {}", (Object)property, (Object)this);
        return this.delegate.isSettable(property);
    }

    public void setString(String key, String value) throws PropertyException {
        log.debug("Setting key '{}' for: {}", (Object)key, (Object)this);
        this.delegate.setString(key, value);
    }

    public String getString(String key) throws PropertyException {
        log.debug("Getting key '{}' for: {}", (Object)key, (Object)this);
        return this.delegate.getString(key);
    }

    public void setText(String key, String value) throws PropertyException {
        log.debug("Setting key '{}' for: {}", (Object)key, (Object)this);
        this.delegate.setText(key, value);
    }

    public String getText(String key) throws PropertyException {
        log.debug("Getting key '{}' for: {}", (Object)key, (Object)this);
        return this.delegate.getText(key);
    }

    public int getType(String key) throws PropertyException {
        log.debug("Getting type of key '{}' for: {}", (Object)key, (Object)this);
        return this.delegate.getType(key);
    }

    public void setXML(String key, Document value) throws PropertyException {
        log.debug("Setting key '{}' for: {}", (Object)key, (Object)this);
        this.delegate.setXML(key, value);
    }

    public Document getXML(String key) throws PropertyException {
        log.debug("Getting key '{}' for: {}", (Object)key, (Object)this);
        return this.delegate.getXML(key);
    }

    public boolean exists(String key) throws PropertyException {
        log.debug("Checking whether key '{}' exists for: {}", (Object)key, (Object)this);
        return this.delegate.exists(key);
    }

    public void init(Map config, Map args) {
        log.debug("Initialising PropertySet with config: {}, arguments: {}", (Object)config, (Object)args);
        this.delegate.init(config, args);
    }

    public void remove(String key) throws PropertyException {
        log.debug("Removing key '{}' for: {}", (Object)key, (Object)this);
        this.delegate.remove(key);
    }

    public boolean supportsType(int type) {
        log.debug("Checking whether type '{}' is supported for: {}", (Object)type, (Object)this);
        return this.delegate.supportsType(type);
    }

    public boolean supportsTypes() {
        log.debug("Getting supported types for: {}", (Object)this);
        return this.delegate.supportsTypes();
    }

    public String toString() {
        return new ToStringBuilder((Object)this).append((Object)this.delegate).toString();
    }
}

