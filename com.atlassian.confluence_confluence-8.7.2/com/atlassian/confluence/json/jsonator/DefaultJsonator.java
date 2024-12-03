/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.json.jsonator;

import com.atlassian.confluence.json.introspector.DefaultIntrospector;
import com.atlassian.confluence.json.introspector.Introspector;
import com.atlassian.confluence.json.json.Json;
import com.atlassian.confluence.json.json.JsonArray;
import com.atlassian.confluence.json.json.JsonNull;
import com.atlassian.confluence.json.json.JsonObject;
import com.atlassian.confluence.json.jsonator.Jsonator;
import com.atlassian.confluence.json.jsonator.PrimitiveJsonator;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class DefaultJsonator
implements Jsonator<Object> {
    private Introspector introspector;
    private Map<Class<?>, Jsonator<?>> jsonators = new LinkedHashMap();
    private final Jsonator<Object> PRIMITIVE_JSONATOR = new PrimitiveJsonator();
    private final Jsonator<Collection<?>> COLLECTION_JSONATOR = new Jsonator<Collection<?>>(){

        @Override
        public Json convert(Collection<?> collection) {
            return this.convertElements(collection);
        }

        private <T> Json convertElements(Collection<T> collection) {
            JsonArray jsonArray = new JsonArray();
            for (T o : collection) {
                Json json = DefaultJsonator.this.convert((Object)o);
                jsonArray.add(json);
            }
            return jsonArray;
        }
    };
    private final Jsonator<Map<?, ?>> MAP_JSONATOR = new Jsonator<Map<?, ?>>(){

        @Override
        public Json convert(Map<?, ?> map) {
            return this.convertMap(map);
        }

        private <K, V> Json convertMap(Map<K, V> map) {
            JsonObject result = new JsonObject();
            for (Map.Entry<K, V> entry : map.entrySet()) {
                K key = entry.getKey();
                V value = entry.getValue();
                Json json = DefaultJsonator.this.convert(value);
                result.setProperty(key.toString(), json);
            }
            return result;
        }
    };

    public DefaultJsonator(Map<Class<?>, Jsonator<?>> jsonators) {
        this.jsonators.put(String.class, this.PRIMITIVE_JSONATOR);
        this.jsonators.put(Boolean.class, this.PRIMITIVE_JSONATOR);
        this.jsonators.put(Number.class, this.PRIMITIVE_JSONATOR);
        this.jsonators.put(Byte.class, this.PRIMITIVE_JSONATOR);
        this.jsonators.put(Collection.class, this.COLLECTION_JSONATOR);
        this.jsonators.put(Map.class, this.MAP_JSONATOR);
        this.introspector = DefaultIntrospector.getInstance();
        this.jsonators.putAll(jsonators);
    }

    public DefaultJsonator(Introspector introspector, Map<Class<?>, Jsonator<?>> jsonators) {
        this.jsonators.put(String.class, this.PRIMITIVE_JSONATOR);
        this.jsonators.put(Boolean.class, this.PRIMITIVE_JSONATOR);
        this.jsonators.put(Number.class, this.PRIMITIVE_JSONATOR);
        this.jsonators.put(Byte.class, this.PRIMITIVE_JSONATOR);
        this.jsonators.put(Collection.class, this.COLLECTION_JSONATOR);
        this.jsonators.put(Map.class, this.MAP_JSONATOR);
        this.introspector = introspector;
        this.jsonators.putAll(jsonators);
    }

    @Override
    public Json convert(Object bean) {
        if (bean == null) {
            return new JsonNull();
        }
        Jsonator<Object> jsonator = this.getJsonator(bean);
        if (jsonator == null) {
            Map<String, Object> properties = this.introspector.getProperties(bean);
            properties.remove("class");
            properties.remove("declaringClass");
            return this.MAP_JSONATOR.convert(properties);
        }
        return jsonator.convert(bean);
    }

    private <T> Jsonator<T> getJsonator(T value) {
        if (value == null) {
            return null;
        }
        Class<?> clazz = value.getClass();
        Set<Class<?>> keys = this.jsonators.keySet();
        for (Class<?> key : keys) {
            if (!key.isAssignableFrom(clazz)) continue;
            return this.jsonators.get(key);
        }
        return null;
    }
}

