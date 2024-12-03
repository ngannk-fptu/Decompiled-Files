/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.Cache
 *  com.atlassian.cache.CacheFactory
 *  com.opensymphony.module.propertyset.PropertyException
 *  com.opensymphony.module.propertyset.PropertySet
 *  com.opensymphony.module.propertyset.PropertySetSchema
 */
package com.atlassian.user.impl.cache.properties;

import com.atlassian.cache.Cache;
import com.atlassian.cache.CacheFactory;
import com.atlassian.user.Entity;
import com.atlassian.user.EntityException;
import com.atlassian.user.impl.cache.properties.CachedPropertySet;
import com.atlassian.user.properties.PropertySetFactory;
import com.opensymphony.module.propertyset.PropertyException;
import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.module.propertyset.PropertySetSchema;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import org.w3c.dom.Document;

public class CachingPropertySetFactory
implements PropertySetFactory {
    private final PropertySetFactory underlyingPropertySetFactory;
    private final CacheFactory cacheFactory;

    public CachingPropertySetFactory(PropertySetFactory underlyingPropertySetFactory, CacheFactory cacheFactory) {
        this.underlyingPropertySetFactory = underlyingPropertySetFactory;
        this.cacheFactory = cacheFactory;
    }

    public PropertySet getPropertySet(Entity entity) throws EntityException {
        Cache cache = this.getCache();
        CachedPropertySet cachedPropertySet = (CachedPropertySet)cache.get((Object)entity.getName());
        if (cachedPropertySet == null) {
            PropertySet underlyingPropertySet = this.underlyingPropertySetFactory.getPropertySet(entity);
            HashMap<String, PropertySet> args = new HashMap<String, PropertySet>();
            args.put("PropertySet", underlyingPropertySet);
            cachedPropertySet = new CachedPropertySet();
            cachedPropertySet.init(args, args);
            cache.put((Object)entity.getName(), (Object)cachedPropertySet);
        }
        return new RecacheOnWritePropertySet(cachedPropertySet, entity.getName());
    }

    private Cache getCache() {
        String cacheName = this.underlyingPropertySetFactory.getClass().getName() + ".propertysets";
        return this.cacheFactory.getCache(cacheName);
    }

    private class RecacheOnWritePropertySet
    implements PropertySet {
        private final PropertySet delegate;
        private String cacheKey;

        public RecacheOnWritePropertySet(PropertySet delegate, String cacheKey) {
            this.delegate = delegate;
            this.cacheKey = cacheKey;
        }

        public void init(Map config, Map args) {
            this.delegate.init(config, args);
            this.recache();
        }

        public void setAsActualType(String key, Object value) throws PropertyException {
            this.delegate.setAsActualType(key, value);
            this.recache();
        }

        public Object getAsActualType(String key) throws PropertyException {
            return this.delegate.getAsActualType(key);
        }

        public void setBoolean(String key, boolean value) throws PropertyException {
            this.delegate.setBoolean(key, value);
            this.recache();
        }

        public boolean getBoolean(String key) throws PropertyException {
            return this.delegate.getBoolean(key);
        }

        public void setData(String key, byte[] value) throws PropertyException {
            this.delegate.setData(key, value);
            this.recache();
        }

        public byte[] getData(String key) throws PropertyException {
            return this.delegate.getData(key);
        }

        public void setDate(String key, Date value) throws PropertyException {
            this.delegate.setDate(key, value);
            this.recache();
        }

        public Date getDate(String key) throws PropertyException {
            return this.delegate.getDate(key);
        }

        public void setDouble(String key, double value) throws PropertyException {
            this.delegate.setDouble(key, value);
            this.recache();
        }

        public double getDouble(String key) throws PropertyException {
            return this.delegate.getDouble(key);
        }

        public void setInt(String key, int value) throws PropertyException {
            this.delegate.setInt(key, value);
            this.recache();
        }

        public int getInt(String key) throws PropertyException {
            return this.delegate.getInt(key);
        }

        public Collection getKeys() throws PropertyException {
            return this.delegate.getKeys();
        }

        public Collection getKeys(int type) throws PropertyException {
            return this.delegate.getKeys(type);
        }

        public Collection getKeys(String prefix) throws PropertyException {
            return this.delegate.getKeys(prefix);
        }

        public Collection getKeys(String prefix, int type) throws PropertyException {
            return this.delegate.getKeys(prefix, type);
        }

        public void setLong(String key, long value) throws PropertyException {
            this.delegate.setLong(key, value);
            this.recache();
        }

        public long getLong(String key) throws PropertyException {
            return this.delegate.getLong(key);
        }

        public void setObject(String key, Object value) throws PropertyException {
            this.delegate.setObject(key, value);
            this.recache();
        }

        public Object getObject(String key) throws PropertyException {
            return this.delegate.getObject(key);
        }

        public void setProperties(String key, Properties value) throws PropertyException {
            this.delegate.setProperties(key, value);
            this.recache();
        }

        public Properties getProperties(String key) throws PropertyException {
            return this.delegate.getProperties(key);
        }

        public void setSchema(PropertySetSchema schema) throws PropertyException {
            this.delegate.setSchema(schema);
            this.recache();
        }

        public PropertySetSchema getSchema() throws PropertyException {
            return this.delegate.getSchema();
        }

        public boolean isSettable(String property) {
            return this.delegate.isSettable(property);
        }

        public void setString(String key, String value) throws PropertyException {
            this.delegate.setString(key, value);
            this.recache();
        }

        public String getString(String key) throws PropertyException {
            return this.delegate.getString(key);
        }

        public void setText(String key, String value) throws PropertyException {
            this.delegate.setText(key, value);
            this.recache();
        }

        public String getText(String key) throws PropertyException {
            return this.delegate.getText(key);
        }

        public int getType(String key) throws PropertyException {
            return this.delegate.getType(key);
        }

        public void setXML(String key, Document value) throws PropertyException {
            this.delegate.setXML(key, value);
            this.recache();
        }

        public Document getXML(String key) throws PropertyException {
            return this.delegate.getXML(key);
        }

        public boolean exists(String key) throws PropertyException {
            return this.delegate.exists(key);
        }

        public void remove(String key) throws PropertyException {
            this.delegate.remove(key);
            this.recache();
        }

        public boolean supportsType(int type) {
            return this.delegate.supportsType(type);
        }

        public boolean supportsTypes() {
            return this.delegate.supportsTypes();
        }

        private void recache() {
            CachingPropertySetFactory.this.getCache().put((Object)this.cacheKey, (Object)this.delegate);
        }
    }
}

