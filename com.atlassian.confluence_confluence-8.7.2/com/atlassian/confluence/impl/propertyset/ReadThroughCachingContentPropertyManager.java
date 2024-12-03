/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.CacheFactory
 *  com.opensymphony.module.propertyset.PropertySet
 *  org.apache.commons.lang3.StringUtils
 *  org.checkerframework.checker.nullness.qual.Nullable
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.propertyset;

import com.atlassian.cache.CacheFactory;
import com.atlassian.confluence.cache.CoreCache;
import com.atlassian.confluence.core.ConfluencePropertySetManager;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.ContentPropertyManager;
import com.atlassian.confluence.impl.cache.ReadThroughAtlassianCache;
import com.atlassian.confluence.impl.cache.ReadThroughCache;
import com.opensymphony.module.propertyset.PropertySet;
import java.io.Serializable;
import java.util.Objects;
import java.util.function.Supplier;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReadThroughCachingContentPropertyManager
implements ContentPropertyManager {
    private static final Logger log = LoggerFactory.getLogger(ReadThroughCachingContentPropertyManager.class);
    private final ConfluencePropertySetManager propertySetManager;
    private final ReadThroughCache<String, CacheValue> cache;

    ReadThroughCachingContentPropertyManager(ConfluencePropertySetManager propertySetManager, ReadThroughCache<String, CacheValue> cache) {
        this.propertySetManager = Objects.requireNonNull(propertySetManager);
        this.cache = cache;
    }

    public static ReadThroughCachingContentPropertyManager create(ConfluencePropertySetManager propertySetManager, CacheFactory cacheFactory) {
        return new ReadThroughCachingContentPropertyManager(propertySetManager, ReadThroughAtlassianCache.create(cacheFactory, CoreCache.CONTENT_PROPERTY_BY_CONTENT_ID_AND_KEY));
    }

    @Override
    public @Nullable String getStringProperty(ContentEntityObject entity, String key) {
        return (String)((Object)this.cacheGet(entity, key, String.class, PropertySet::getString));
    }

    @Override
    public void setStringProperty(ContentEntityObject entity, String key, @Nullable String value) {
        this.getPropertySet(entity).setString(key, StringUtils.left((String)value, (int)255));
        this.invalidateCache(entity, key);
    }

    @Override
    public @Nullable String getTextProperty(ContentEntityObject entity, String key) {
        return (String)((Object)this.cacheGet(entity, key, String.class, PropertySet::getText));
    }

    @Override
    public void setTextProperty(ContentEntityObject entity, String key, String value) {
        this.getPropertySet(entity).setText(key, value);
        this.invalidateCache(entity, key);
    }

    @Override
    public void removeProperty(ContentEntityObject entity, String key) {
        PropertySet propertySet = this.getPropertySet(entity);
        propertySet.remove(key);
        this.invalidateCache(entity, key);
    }

    @Override
    public void removeProperties(ContentEntityObject entity) {
        PropertySet propertySet = this.getPropertySet(entity);
        for (Object o : propertySet.getKeys()) {
            String key = (String)o;
            propertySet.remove(key);
            this.invalidateCache(entity, key);
        }
    }

    @Override
    public void transferProperties(ContentEntityObject source, ContentEntityObject destination) {
        PropertySet sourceProperties = this.getPropertySet(source);
        PropertySet destProperties = this.getPropertySet(destination);
        for (Object o : sourceProperties.getKeys()) {
            String key = (String)o;
            Object value = sourceProperties.getAsActualType(key);
            destProperties.setAsActualType(key, value);
            this.invalidateCache(destination, key);
            sourceProperties.remove(key);
            this.invalidateCache(source, key);
        }
    }

    private PropertySet getPropertySet(ContentEntityObject object) {
        return this.propertySetManager.getPropertySet(object);
    }

    private void invalidateCache(ContentEntityObject entity, String key) {
        this.cache.remove(ReadThroughCachingContentPropertyManager.cacheKey(entity, key));
    }

    private static String cacheKey(ContentEntityObject entity, String key) {
        return String.format("%s-%s", entity.getId(), key);
    }

    private @Nullable Serializable cacheGet(ContentEntityObject entity, String key, Class<?> expectedClass, Getter getter) {
        Supplier<CacheValue> loader;
        String cacheKey = ReadThroughCachingContentPropertyManager.cacheKey(entity, key);
        Serializable value = this.cache.get(cacheKey, loader = () -> {
            PropertySet ps = this.getPropertySet(entity);
            if (ps.exists(key)) {
                return new CacheValue(getter.get(ps, key));
            }
            return new CacheValue(null);
        }).getValue();
        if (value != null && !value.getClass().isAssignableFrom(expectedClass)) {
            log.warn("Expected {} to return {} but got {} ({})", new Object[]{cacheKey, expectedClass.getName(), value, value.getClass().getName()});
            this.cache.remove(cacheKey);
            return null;
        }
        return value;
    }

    private static interface Getter {
        public @Nullable Serializable get(PropertySet var1, String var2);
    }

    private static class CacheValue
    implements Serializable {
        private final Serializable value;

        private CacheValue(@Nullable Serializable value) {
            this.value = value;
        }

        Serializable getValue() {
            return this.value;
        }

        public boolean equals(@Nullable Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            CacheValue that = (CacheValue)o;
            return Objects.equals(this.value, that.value);
        }

        public int hashCode() {
            return Objects.hash(this.value);
        }

        public String toString() {
            return "CacheValue{value=" + this.value + "}";
        }
    }
}

