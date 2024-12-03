/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.CacheFactory
 *  com.atlassian.sal.api.user.UserKey
 *  com.opensymphony.module.propertyset.PropertySet
 *  com.opensymphony.module.propertyset.PropertySetSchema
 *  io.atlassian.fugue.Option
 *  javax.annotation.Nullable
 */
package com.atlassian.confluence.impl.propertyset;

import com.atlassian.cache.CacheFactory;
import com.atlassian.confluence.cache.CoreCache;
import com.atlassian.confluence.impl.cache.ReadThroughAtlassianCache;
import com.atlassian.confluence.impl.cache.ReadThroughCache;
import com.atlassian.sal.api.user.UserKey;
import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.module.propertyset.PropertySetSchema;
import io.atlassian.fugue.Option;
import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Properties;
import java.util.function.Function;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import org.w3c.dom.Document;

final class ReadThroughCachingPropertySet<K extends Serializable>
implements PropertySet {
    private final PropertySet delegate;
    private final Function<String, K> cacheKeyResolver;
    private final ReadThroughCache<K, Option<Object>> cache;
    private final Predicate<Option<Object>> cacheableValueTester;

    ReadThroughCachingPropertySet(PropertySet delegate, Function<String, K> cacheKeyResolver, ReadThroughCache<K, Option<Object>> cache, Predicate<Option<Object>> cacheableValueTester) {
        this.delegate = delegate;
        this.cacheKeyResolver = cacheKeyResolver;
        this.cache = cache;
        this.cacheableValueTester = cacheableValueTester;
    }

    public static ReadThroughCachingPropertySet<?> create(PropertySet delegate, UserKey userKey, CacheFactory cacheFactory) {
        return new ReadThroughCachingPropertySet<String>(delegate, key -> userKey.getStringValue() + "." + key, ReadThroughAtlassianCache.create(cacheFactory, CoreCache.USER_PROPERTY_SETS), option -> option.forall(Serializable.class::isInstance));
    }

    public void init(Map config, Map args) {
    }

    public void setAsActualType(String key, @Nullable Object value) {
        this.delegate.setAsActualType(key, value);
        this.removeFromCache(key);
    }

    @Nullable
    public Object getAsActualType(String key) {
        return this.getNullable(key, Object.class, arg_0 -> ((PropertySet)this.delegate).getAsActualType(arg_0));
    }

    @Nullable
    private <T> T getNullable(String key, Class<T> type, Function<String, T> f) {
        return (T)this.cache.get(this.cacheKey(key), () -> Option.option(f.apply(key)), this.cacheableValueTester).map(type::cast).getOrNull();
    }

    private Object getPrimitive(String key, Function<String, ?> f) {
        return this.cache.get(this.cacheKey(key), () -> Option.option(f.apply(key))).get();
    }

    private K cacheKey(String key) {
        return (K)((Serializable)this.cacheKeyResolver.apply(key));
    }

    private void removeFromCache(String key) {
        this.cache.remove(this.cacheKey(key));
    }

    public void setBoolean(String key, boolean value) {
        this.delegate.setBoolean(key, value);
        this.removeFromCache(key);
    }

    public boolean getBoolean(String key) {
        return (Boolean)this.getPrimitive(key, arg_0 -> ((PropertySet)this.delegate).getBoolean(arg_0));
    }

    public void setData(String key, @Nullable byte[] value) {
        this.delegate.setData(key, value);
        this.removeFromCache(key);
    }

    @Nullable
    public byte[] getData(String key) {
        return (byte[])this.getNullable(key, Object.class, arg_0 -> ((PropertySet)this.delegate).getData(arg_0));
    }

    public void setDate(String key, @Nullable Date value) {
        this.delegate.setDate(key, value);
        this.removeFromCache(key);
    }

    @Nullable
    public Date getDate(String key) {
        return this.getNullable(key, Date.class, arg_0 -> ((PropertySet)this.delegate).getDate(arg_0));
    }

    public void setDouble(String key, double value) {
        this.delegate.setDouble(key, value);
        this.removeFromCache(key);
    }

    public double getDouble(String key) {
        return (Double)this.getPrimitive(key, arg_0 -> ((PropertySet)this.delegate).getDouble(arg_0));
    }

    public void setInt(String key, int value) {
        this.delegate.setInt(key, value);
        this.removeFromCache(key);
    }

    public int getInt(String key) {
        return (Integer)this.getPrimitive(key, arg_0 -> ((PropertySet)this.delegate).getInt(arg_0));
    }

    public Collection<?> getKeys() {
        return this.delegate.getKeys();
    }

    public Collection<?> getKeys(int type) {
        return this.delegate.getKeys(type);
    }

    public Collection<?> getKeys(String prefix) {
        return this.delegate.getKeys(prefix);
    }

    public Collection<?> getKeys(String prefix, int type) {
        return this.delegate.getKeys(prefix, type);
    }

    public void setLong(String key, long value) {
        this.delegate.setLong(key, value);
        this.removeFromCache(key);
    }

    public long getLong(String key) {
        return (Long)this.getPrimitive(key, arg_0 -> ((PropertySet)this.delegate).getLong(arg_0));
    }

    public void setObject(String key, @Nullable Object value) {
        this.delegate.setObject(key, value);
        this.removeFromCache(key);
    }

    @Nullable
    public Object getObject(String key) {
        return this.getNullable(key, Object.class, arg_0 -> ((PropertySet)this.delegate).getObject(arg_0));
    }

    public void setProperties(String key, @Nullable Properties value) {
        this.delegate.setProperties(key, value);
        this.removeFromCache(key);
    }

    @Nullable
    public Properties getProperties(String key) {
        return this.getNullable(key, Properties.class, arg_0 -> ((PropertySet)this.delegate).getProperties(arg_0));
    }

    public void setSchema(PropertySetSchema schema) {
        this.delegate.setSchema(schema);
    }

    public PropertySetSchema getSchema() {
        return this.delegate.getSchema();
    }

    public boolean isSettable(String property) {
        return this.delegate.isSettable(property);
    }

    public void setString(String key, @Nullable String value) {
        this.delegate.setString(key, value);
        this.removeFromCache(key);
    }

    @Nullable
    public String getString(String key) {
        return this.getNullable(key, String.class, arg_0 -> ((PropertySet)this.delegate).getString(arg_0));
    }

    public void setText(String key, @Nullable String value) {
        this.delegate.setText(key, value);
        this.removeFromCache(key);
    }

    public String getText(String key) {
        return this.getNullable(key, String.class, arg_0 -> ((PropertySet)this.delegate).getText(arg_0));
    }

    public int getType(String key) {
        return this.delegate.getType(key);
    }

    public void setXML(String key, @Nullable Document value) {
        this.delegate.setXML(key, value);
        this.removeFromCache(key);
    }

    public Document getXML(String key) {
        return this.getNullable(key, Document.class, arg_0 -> ((PropertySet)this.delegate).getXML(arg_0));
    }

    public boolean exists(String key) {
        return this.cache.get(this.cacheKey(key), () -> this.delegate.exists(key) ? Option.option((Object)this.delegate.getAsActualType(key)) : Option.none()).isDefined();
    }

    public void remove(String key) {
        this.delegate.remove(key);
        this.removeFromCache(key);
    }

    public boolean supportsType(int type) {
        return this.delegate.supportsType(type);
    }

    public boolean supportsTypes() {
        return this.delegate.supportsTypes();
    }
}

