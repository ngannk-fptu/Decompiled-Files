/*
 * Decompiled with CFR 0.152.
 */
package com.fasterxml.jackson.databind.deser.std;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.deser.std.AtomicBooleanDeserializer;
import com.fasterxml.jackson.databind.deser.std.AtomicIntegerDeserializer;
import com.fasterxml.jackson.databind.deser.std.AtomicLongDeserializer;
import com.fasterxml.jackson.databind.deser.std.ByteBufferDeserializer;
import com.fasterxml.jackson.databind.deser.std.FromStringDeserializer;
import com.fasterxml.jackson.databind.deser.std.NullifyingDeserializer;
import com.fasterxml.jackson.databind.deser.std.StackTraceElementDeserializer;
import com.fasterxml.jackson.databind.deser.std.UUIDDeserializer;
import java.nio.ByteBuffer;
import java.util.HashSet;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class JdkDeserializers {
    private static final HashSet<String> _classNames;

    @Deprecated
    public static JsonDeserializer<?> find(Class<?> rawType, String clsName) throws JsonMappingException {
        return JdkDeserializers.find(null, rawType, clsName);
    }

    public static JsonDeserializer<?> find(DeserializationContext ctxt, Class<?> rawType, String clsName) throws JsonMappingException {
        if (_classNames.contains(clsName)) {
            FromStringDeserializer<?> d = FromStringDeserializer.findDeserializer(rawType);
            if (d != null) {
                return d;
            }
            if (rawType == UUID.class) {
                return new UUIDDeserializer();
            }
            if (rawType == StackTraceElement.class) {
                return StackTraceElementDeserializer.construct(ctxt);
            }
            if (rawType == AtomicBoolean.class) {
                return new AtomicBooleanDeserializer();
            }
            if (rawType == AtomicInteger.class) {
                return new AtomicIntegerDeserializer();
            }
            if (rawType == AtomicLong.class) {
                return new AtomicLongDeserializer();
            }
            if (rawType == ByteBuffer.class) {
                return new ByteBufferDeserializer();
            }
            if (rawType == Void.class) {
                return NullifyingDeserializer.instance;
            }
        }
        return null;
    }

    public static boolean hasDeserializerFor(Class<?> rawType) {
        return _classNames.contains(rawType.getName());
    }

    static {
        Class[] types;
        _classNames = new HashSet();
        for (Class clazz : types = new Class[]{UUID.class, AtomicBoolean.class, AtomicInteger.class, AtomicLong.class, StackTraceElement.class, ByteBuffer.class, Void.class}) {
            _classNames.add(clazz.getName());
        }
        for (Class clazz : FromStringDeserializer.types()) {
            _classNames.add(clazz.getName());
        }
    }
}

