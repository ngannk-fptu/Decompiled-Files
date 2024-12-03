/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.type.JavaType
 */
package org.codehaus.jackson.map.module;

import java.util.HashMap;
import org.codehaus.jackson.map.BeanDescription;
import org.codehaus.jackson.map.BeanProperty;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.Serializers;
import org.codehaus.jackson.map.TypeSerializer;
import org.codehaus.jackson.map.type.ArrayType;
import org.codehaus.jackson.map.type.ClassKey;
import org.codehaus.jackson.map.type.CollectionLikeType;
import org.codehaus.jackson.map.type.CollectionType;
import org.codehaus.jackson.map.type.MapLikeType;
import org.codehaus.jackson.map.type.MapType;
import org.codehaus.jackson.type.JavaType;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class SimpleSerializers
extends Serializers.Base {
    protected HashMap<ClassKey, JsonSerializer<?>> _classMappings = null;
    protected HashMap<ClassKey, JsonSerializer<?>> _interfaceMappings = null;

    public void addSerializer(JsonSerializer<?> ser) {
        Class<?> cls = ser.handledType();
        if (cls == null || cls == Object.class) {
            throw new IllegalArgumentException("JsonSerializer of type " + ser.getClass().getName() + " does not define valid handledType() -- must either register with method that takes type argument  or make serializer extend 'org.codehaus.jackson.map.ser.std.SerializerBase'");
        }
        this._addSerializer(cls, ser);
    }

    public <T> void addSerializer(Class<? extends T> type, JsonSerializer<T> ser) {
        this._addSerializer(type, ser);
    }

    private void _addSerializer(Class<?> cls, JsonSerializer<?> ser) {
        ClassKey key = new ClassKey(cls);
        if (cls.isInterface()) {
            if (this._interfaceMappings == null) {
                this._interfaceMappings = new HashMap();
            }
            this._interfaceMappings.put(key, ser);
        } else {
            if (this._classMappings == null) {
                this._classMappings = new HashMap();
            }
            this._classMappings.put(key, ser);
        }
    }

    @Override
    public JsonSerializer<?> findSerializer(SerializationConfig config, JavaType type, BeanDescription beanDesc, BeanProperty property) {
        Class cls = type.getRawClass();
        ClassKey key = new ClassKey(cls);
        JsonSerializer<?> ser = null;
        if (cls.isInterface()) {
            if (this._interfaceMappings != null && (ser = this._interfaceMappings.get(key)) != null) {
                return ser;
            }
        } else if (this._classMappings != null) {
            ser = this._classMappings.get(key);
            if (ser != null) {
                return ser;
            }
            for (Class curr = cls; curr != null; curr = curr.getSuperclass()) {
                key.reset(curr);
                ser = this._classMappings.get(key);
                if (ser == null) continue;
                return ser;
            }
        }
        if (this._interfaceMappings != null) {
            ser = this._findInterfaceMapping(cls, key);
            if (ser != null) {
                return ser;
            }
            if (!cls.isInterface()) {
                while ((cls = cls.getSuperclass()) != null) {
                    ser = this._findInterfaceMapping(cls, key);
                    if (ser == null) continue;
                    return ser;
                }
            }
        }
        return null;
    }

    @Override
    public JsonSerializer<?> findArraySerializer(SerializationConfig config, ArrayType type, BeanDescription beanDesc, BeanProperty property, TypeSerializer elementTypeSerializer, JsonSerializer<Object> elementValueSerializer) {
        return this.findSerializer(config, type, beanDesc, property);
    }

    @Override
    public JsonSerializer<?> findCollectionSerializer(SerializationConfig config, CollectionType type, BeanDescription beanDesc, BeanProperty property, TypeSerializer elementTypeSerializer, JsonSerializer<Object> elementValueSerializer) {
        return this.findSerializer(config, type, beanDesc, property);
    }

    @Override
    public JsonSerializer<?> findCollectionLikeSerializer(SerializationConfig config, CollectionLikeType type, BeanDescription beanDesc, BeanProperty property, TypeSerializer elementTypeSerializer, JsonSerializer<Object> elementValueSerializer) {
        return this.findSerializer(config, type, beanDesc, property);
    }

    @Override
    public JsonSerializer<?> findMapSerializer(SerializationConfig config, MapType type, BeanDescription beanDesc, BeanProperty property, JsonSerializer<Object> keySerializer, TypeSerializer elementTypeSerializer, JsonSerializer<Object> elementValueSerializer) {
        return this.findSerializer(config, type, beanDesc, property);
    }

    @Override
    public JsonSerializer<?> findMapLikeSerializer(SerializationConfig config, MapLikeType type, BeanDescription beanDesc, BeanProperty property, JsonSerializer<Object> keySerializer, TypeSerializer elementTypeSerializer, JsonSerializer<Object> elementValueSerializer) {
        return this.findSerializer(config, type, beanDesc, property);
    }

    protected JsonSerializer<?> _findInterfaceMapping(Class<?> cls, ClassKey key) {
        for (Class<?> iface : cls.getInterfaces()) {
            key.reset(iface);
            JsonSerializer<?> ser = this._interfaceMappings.get(key);
            if (ser != null) {
                return ser;
            }
            ser = this._findInterfaceMapping(iface, key);
            if (ser == null) continue;
            return ser;
        }
        return null;
    }
}

