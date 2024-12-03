/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.beanutils;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.beanutils.ConversionException;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.DynaBeanPropertyMapDecorator;
import org.apache.commons.beanutils.DynaClass;
import org.apache.commons.beanutils.DynaProperty;

public class BasicDynaBean
implements DynaBean,
Serializable {
    protected DynaClass dynaClass = null;
    protected HashMap<String, Object> values = new HashMap();
    private transient Map<String, Object> mapDecorator;

    public BasicDynaBean(DynaClass dynaClass) {
        this.dynaClass = dynaClass;
    }

    public Map<String, Object> getMap() {
        if (this.mapDecorator == null) {
            this.mapDecorator = new DynaBeanPropertyMapDecorator(this);
        }
        return this.mapDecorator;
    }

    @Override
    public boolean contains(String name, String key) {
        Object value = this.values.get(name);
        if (value == null) {
            throw new NullPointerException("No mapped value for '" + name + "(" + key + ")'");
        }
        if (value instanceof Map) {
            return ((Map)value).containsKey(key);
        }
        throw new IllegalArgumentException("Non-mapped property for '" + name + "(" + key + ")'");
    }

    @Override
    public Object get(String name) {
        Object value = this.values.get(name);
        if (value != null) {
            return value;
        }
        Class<?> type = this.getDynaProperty(name).getType();
        if (!type.isPrimitive()) {
            return value;
        }
        if (type == Boolean.TYPE) {
            return Boolean.FALSE;
        }
        if (type == Byte.TYPE) {
            return new Byte(0);
        }
        if (type == Character.TYPE) {
            return new Character('\u0000');
        }
        if (type == Double.TYPE) {
            return new Double(0.0);
        }
        if (type == Float.TYPE) {
            return new Float(0.0f);
        }
        if (type == Integer.TYPE) {
            return new Integer(0);
        }
        if (type == Long.TYPE) {
            return new Long(0L);
        }
        if (type == Short.TYPE) {
            return new Short(0);
        }
        return null;
    }

    @Override
    public Object get(String name, int index) {
        Object value = this.values.get(name);
        if (value == null) {
            throw new NullPointerException("No indexed value for '" + name + "[" + index + "]'");
        }
        if (value.getClass().isArray()) {
            return Array.get(value, index);
        }
        if (value instanceof List) {
            return ((List)value).get(index);
        }
        throw new IllegalArgumentException("Non-indexed property for '" + name + "[" + index + "]'");
    }

    @Override
    public Object get(String name, String key) {
        Object value = this.values.get(name);
        if (value == null) {
            throw new NullPointerException("No mapped value for '" + name + "(" + key + ")'");
        }
        if (value instanceof Map) {
            return ((Map)value).get(key);
        }
        throw new IllegalArgumentException("Non-mapped property for '" + name + "(" + key + ")'");
    }

    @Override
    public DynaClass getDynaClass() {
        return this.dynaClass;
    }

    @Override
    public void remove(String name, String key) {
        Object value = this.values.get(name);
        if (value == null) {
            throw new NullPointerException("No mapped value for '" + name + "(" + key + ")'");
        }
        if (!(value instanceof Map)) {
            throw new IllegalArgumentException("Non-mapped property for '" + name + "(" + key + ")'");
        }
        ((Map)value).remove(key);
    }

    @Override
    public void set(String name, Object value) {
        DynaProperty descriptor = this.getDynaProperty(name);
        if (value == null) {
            if (descriptor.getType().isPrimitive()) {
                throw new NullPointerException("Primitive value for '" + name + "'");
            }
        } else if (!this.isAssignable(descriptor.getType(), value.getClass())) {
            throw new ConversionException("Cannot assign value of type '" + value.getClass().getName() + "' to property '" + name + "' of type '" + descriptor.getType().getName() + "'");
        }
        this.values.put(name, value);
    }

    @Override
    public void set(String name, int index, Object value) {
        Object prop = this.values.get(name);
        if (prop == null) {
            throw new NullPointerException("No indexed value for '" + name + "[" + index + "]'");
        }
        if (prop.getClass().isArray()) {
            Array.set(prop, index, value);
        } else if (prop instanceof List) {
            try {
                List list = (List)prop;
                list.set(index, value);
            }
            catch (ClassCastException e) {
                throw new ConversionException(e.getMessage());
            }
        } else {
            throw new IllegalArgumentException("Non-indexed property for '" + name + "[" + index + "]'");
        }
    }

    @Override
    public void set(String name, String key, Object value) {
        Object prop = this.values.get(name);
        if (prop == null) {
            throw new NullPointerException("No mapped value for '" + name + "(" + key + ")'");
        }
        if (!(prop instanceof Map)) {
            throw new IllegalArgumentException("Non-mapped property for '" + name + "(" + key + ")'");
        }
        Map map = (Map)prop;
        map.put(key, value);
    }

    protected DynaProperty getDynaProperty(String name) {
        DynaProperty descriptor = this.getDynaClass().getDynaProperty(name);
        if (descriptor == null) {
            throw new IllegalArgumentException("Invalid property name '" + name + "'");
        }
        return descriptor;
    }

    protected boolean isAssignable(Class<?> dest, Class<?> source) {
        return dest.isAssignableFrom(source) || dest == Boolean.TYPE && source == Boolean.class || dest == Byte.TYPE && source == Byte.class || dest == Character.TYPE && source == Character.class || dest == Double.TYPE && source == Double.class || dest == Float.TYPE && source == Float.class || dest == Integer.TYPE && source == Integer.class || dest == Long.TYPE && source == Long.class || dest == Short.TYPE && source == Short.class;
    }
}

