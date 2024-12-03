/*
 * Decompiled with CFR 0.152.
 */
package com.thoughtworks.xstream.converters.reflection;

import com.thoughtworks.xstream.converters.reflection.FieldDictionary;
import com.thoughtworks.xstream.converters.reflection.ObjectAccessException;
import com.thoughtworks.xstream.converters.reflection.SunLimitedUnsafeReflectionProvider;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.WeakHashMap;

public class SunUnsafeReflectionProvider
extends SunLimitedUnsafeReflectionProvider {
    private transient Map fieldOffsetCache;

    public SunUnsafeReflectionProvider() {
    }

    public SunUnsafeReflectionProvider(FieldDictionary dic) {
        super(dic);
    }

    public void writeField(Object object, String fieldName, Object value, Class definedIn) {
        this.write(this.fieldDictionary.field(object.getClass(), fieldName, definedIn), object, value);
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private void write(Field field, Object object, Object value) {
        if (exception != null) {
            ObjectAccessException ex = new ObjectAccessException("Cannot set field", exception);
            ex.add("field", object.getClass() + "." + field.getName());
            throw ex;
        }
        try {
            long offset = this.getFieldOffset(field);
            Class<?> type = field.getType();
            if (!type.isPrimitive()) {
                unsafe.putObject(object, offset, value);
                return;
            }
            if (type.equals(Integer.TYPE)) {
                unsafe.putInt(object, offset, (Integer)value);
                return;
            }
            if (type.equals(Long.TYPE)) {
                unsafe.putLong(object, offset, (Long)value);
                return;
            }
            if (type.equals(Short.TYPE)) {
                unsafe.putShort(object, offset, (Short)value);
                return;
            }
            if (type.equals(Character.TYPE)) {
                unsafe.putChar(object, offset, ((Character)value).charValue());
                return;
            }
            if (type.equals(Byte.TYPE)) {
                unsafe.putByte(object, offset, (Byte)value);
                return;
            }
            if (type.equals(Float.TYPE)) {
                unsafe.putFloat(object, offset, ((Float)value).floatValue());
                return;
            }
            if (type.equals(Double.TYPE)) {
                unsafe.putDouble(object, offset, (Double)value);
                return;
            }
            if (type.equals(Boolean.TYPE)) {
                unsafe.putBoolean(object, offset, (Boolean)value);
                return;
            }
            ObjectAccessException ex = new ObjectAccessException("Cannot set field of unknown type", exception);
            ex.add("field", object.getClass() + "." + field.getName());
            ex.add("unknown-type", type.getName());
            throw ex;
        }
        catch (IllegalArgumentException e) {
            ObjectAccessException ex = new ObjectAccessException("Cannot set field", e);
            ex.add("field", object.getClass() + "." + field.getName());
            throw ex;
        }
    }

    private synchronized long getFieldOffset(Field f) {
        Long l = (Long)this.fieldOffsetCache.get(f);
        if (l == null) {
            l = new Long(unsafe.objectFieldOffset(f));
            this.fieldOffsetCache.put(f, l);
        }
        return l;
    }

    private Object readResolve() {
        this.init();
        return this;
    }

    protected void init() {
        super.init();
        this.fieldOffsetCache = new WeakHashMap();
    }
}

