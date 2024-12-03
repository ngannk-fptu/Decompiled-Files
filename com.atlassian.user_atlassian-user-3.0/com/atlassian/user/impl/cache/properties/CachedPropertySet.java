/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.opensymphony.module.propertyset.PropertyException
 *  com.opensymphony.module.propertyset.PropertySet
 *  com.opensymphony.module.propertyset.PropertySetManager
 *  com.opensymphony.module.propertyset.PropertySetSchema
 *  com.opensymphony.module.propertyset.memory.SerializablePropertySet
 *  com.opensymphony.util.DataUtil
 */
package com.atlassian.user.impl.cache.properties;

import com.opensymphony.module.propertyset.PropertyException;
import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.module.propertyset.PropertySetManager;
import com.opensymphony.module.propertyset.PropertySetSchema;
import com.opensymphony.module.propertyset.memory.SerializablePropertySet;
import com.opensymphony.util.DataUtil;
import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import org.w3c.dom.Document;

public class CachedPropertySet
implements PropertySet,
Serializable {
    private PropertySet decoratedPS;
    private SerializablePropertySet cachePS;
    private Map<String, Object> existantKeyCache;

    public void init(Map config, Map args) {
        this.decoratedPS = (PropertySet)args.get("PropertySet");
        String serializableName = (String)config.get("serializableName");
        if (serializableName == null) {
            serializableName = "serializable";
        }
        this.cachePS = (SerializablePropertySet)PropertySetManager.getInstance((String)serializableName, null);
        this.existantKeyCache = new HashMap<String, Object>();
        Boolean bulkload = (Boolean)args.get("bulkload");
        if (bulkload != null && bulkload.booleanValue()) {
            PropertySetManager.clone((PropertySet)this.decoratedPS, (PropertySet)this.cachePS);
        }
    }

    public void setAsActualType(String key, Object value) throws PropertyException {
        if (value instanceof Boolean) {
            this.setBoolean(key, DataUtil.getBoolean((Boolean)((Boolean)value)));
        } else if (value instanceof Integer) {
            this.setInt(key, DataUtil.getInt((Integer)((Integer)value)));
        } else if (value instanceof Long) {
            this.setLong(key, DataUtil.getLong((Long)((Long)value)));
        } else if (value instanceof Double) {
            this.setDouble(key, DataUtil.getDouble((Double)((Double)value)));
        } else if (value instanceof String) {
            this.setString(key, (String)value);
        } else if (value instanceof Date) {
            this.setDate(key, (Date)value);
        } else if (value instanceof Document) {
            this.setXML(key, (Document)value);
        } else if (value instanceof byte[]) {
            this.setData(key, (byte[])value);
        } else if (value instanceof Properties) {
            this.setProperties(key, (Properties)value);
        } else {
            this.setObject(key, value);
        }
    }

    public Object getAsActualType(String key) throws PropertyException {
        int type = this.getType(key);
        Object value = null;
        switch (type) {
            case 1: {
                value = this.getBoolean(key);
                break;
            }
            case 2: {
                value = this.getInt(key);
                break;
            }
            case 3: {
                value = this.getLong(key);
                break;
            }
            case 4: {
                value = this.getDouble(key);
                break;
            }
            case 5: {
                value = this.getString(key);
                break;
            }
            case 7: {
                value = this.getDate(key);
                break;
            }
            case 9: {
                value = this.getXML(key);
                break;
            }
            case 10: {
                value = this.getData(key);
                break;
            }
            case 11: {
                value = this.getProperties(key);
                break;
            }
            case 8: {
                value = this.getObject(key);
            }
        }
        return value;
    }

    public void setBoolean(String key, boolean value) throws PropertyException {
        this.decoratedPS.setBoolean(key, value);
        this.cachePS.setBoolean(key, value);
        this.existantKeyCache.remove(key);
    }

    public boolean getBoolean(String key) throws PropertyException {
        if (!this.cachePS.exists(key)) {
            this.cachePS.setBoolean(key, this.decoratedPS.getBoolean(key));
        }
        return this.cachePS.getBoolean(key);
    }

    public void setData(String key, byte[] value) throws PropertyException {
        this.decoratedPS.setData(key, value);
        this.cachePS.setData(key, value);
        this.existantKeyCache.remove(key);
    }

    public byte[] getData(String key) throws PropertyException {
        if (!this.cachePS.exists(key)) {
            this.cachePS.setData(key, this.decoratedPS.getData(key));
        }
        return this.cachePS.getData(key);
    }

    public void setDate(String key, Date value) throws PropertyException {
        this.decoratedPS.setDate(key, value);
        this.cachePS.setDate(key, value);
        this.existantKeyCache.remove(key);
    }

    public Date getDate(String key) throws PropertyException {
        if (!this.cachePS.exists(key)) {
            this.cachePS.setDate(key, this.decoratedPS.getDate(key));
        }
        return this.cachePS.getDate(key);
    }

    public void setDouble(String key, double value) throws PropertyException {
        this.decoratedPS.setDouble(key, value);
        this.cachePS.setDouble(key, value);
        this.existantKeyCache.remove(key);
    }

    public double getDouble(String key) throws PropertyException {
        if (!this.cachePS.exists(key)) {
            this.cachePS.setDouble(key, this.decoratedPS.getDouble(key));
        }
        return this.cachePS.getDouble(key);
    }

    public void setInt(String key, int value) throws PropertyException {
        this.decoratedPS.setInt(key, value);
        this.cachePS.setInt(key, value);
        this.existantKeyCache.remove(key);
    }

    public int getInt(String key) throws PropertyException {
        if (!this.cachePS.exists(key)) {
            this.cachePS.setInt(key, this.decoratedPS.getInt(key));
        }
        return this.cachePS.getInt(key);
    }

    public Collection getKeys() throws PropertyException {
        return this.decoratedPS.getKeys();
    }

    public Collection getKeys(int type) throws PropertyException {
        return this.decoratedPS.getKeys(type);
    }

    public Collection getKeys(String prefix) throws PropertyException {
        return this.decoratedPS.getKeys(prefix);
    }

    public Collection getKeys(String prefix, int type) throws PropertyException {
        return this.decoratedPS.getKeys(prefix, type);
    }

    public void setLong(String key, long value) throws PropertyException {
        this.decoratedPS.setLong(key, value);
        this.cachePS.setLong(key, value);
        this.existantKeyCache.remove(key);
    }

    public long getLong(String key) throws PropertyException {
        if (!this.cachePS.exists(key)) {
            this.cachePS.setLong(key, this.decoratedPS.getLong(key));
        }
        return this.cachePS.getLong(key);
    }

    public void setObject(String key, Object value) throws PropertyException {
        this.decoratedPS.setObject(key, value);
        this.cachePS.setObject(key, value);
        this.existantKeyCache.remove(key);
    }

    public Object getObject(String key) throws PropertyException {
        if (!this.cachePS.exists(key)) {
            this.cachePS.setObject(key, this.decoratedPS.getObject(key));
        }
        return this.cachePS.getObject(key);
    }

    public void setProperties(String key, Properties value) throws PropertyException {
        this.decoratedPS.setProperties(key, value);
        this.cachePS.setProperties(key, value);
        this.existantKeyCache.remove(key);
    }

    public Properties getProperties(String key) throws PropertyException {
        if (!this.cachePS.exists(key)) {
            this.cachePS.setProperties(key, this.decoratedPS.getProperties(key));
        }
        return this.cachePS.getProperties(key);
    }

    public void setSchema(PropertySetSchema schema) throws PropertyException {
        this.decoratedPS.setSchema(schema);
    }

    public PropertySetSchema getSchema() throws PropertyException {
        return this.decoratedPS.getSchema();
    }

    public boolean isSettable(String property) {
        return this.decoratedPS.isSettable(property);
    }

    public void setString(String key, String value) throws PropertyException {
        this.decoratedPS.setString(key, value);
        this.cachePS.setString(key, value);
        this.existantKeyCache.remove(key);
    }

    public String getString(String key) throws PropertyException {
        if (!this.cachePS.exists(key)) {
            this.cachePS.setString(key, this.decoratedPS.getString(key));
        }
        return this.cachePS.getString(key);
    }

    public void setText(String key, String value) throws PropertyException {
        this.decoratedPS.setText(key, value);
        this.cachePS.setText(key, value);
        this.existantKeyCache.remove(key);
    }

    public String getText(String key) throws PropertyException {
        if (!this.cachePS.exists(key)) {
            this.cachePS.setText(key, this.decoratedPS.getText(key));
        }
        return this.cachePS.getText(key);
    }

    public int getType(String key) throws PropertyException {
        return this.decoratedPS.getType(key);
    }

    public void setXML(String key, Document value) throws PropertyException {
        this.decoratedPS.setXML(key, value);
        this.cachePS.setXML(key, value);
        this.existantKeyCache.remove(key);
    }

    public Document getXML(String key) throws PropertyException {
        if (!this.cachePS.exists(key)) {
            this.cachePS.setXML(key, this.decoratedPS.getXML(key));
        }
        return this.cachePS.getXML(key);
    }

    public boolean exists(String key) throws PropertyException {
        if (this.existantKeyCache.containsKey(key)) {
            return Boolean.TRUE.equals(this.existantKeyCache.get(key));
        }
        boolean keyExists = this.decoratedPS.exists(key);
        this.existantKeyCache.put(key, keyExists);
        return keyExists;
    }

    public void remove(String key) throws PropertyException {
        this.existantKeyCache.remove(key);
        this.cachePS.remove(key);
        this.decoratedPS.remove(key);
    }

    public boolean supportsType(int type) {
        return this.decoratedPS.supportsType(type);
    }

    public boolean supportsTypes() {
        return this.decoratedPS.supportsTypes();
    }
}

