/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.config;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import org.apache.tika.config.Field;
import org.apache.tika.exception.TikaConfigException;

public class ParamField {
    public static final String DEFAULT = "#default";
    private static final Map<Class<?>, Class<?>> PRIMITIVE_MAP = new HashMap<Class<?>, Class<?>>(){
        {
            this.put(Integer.TYPE, Integer.class);
            this.put(Short.TYPE, Short.class);
            this.put(Boolean.TYPE, Boolean.class);
            this.put(Long.TYPE, Long.class);
            this.put(Float.TYPE, Float.class);
            this.put(Double.TYPE, Double.class);
        }
    };
    private java.lang.reflect.Field field;
    private Method setter;
    private String name;
    private Class<?> type;
    private boolean required;

    public ParamField(AccessibleObject member) throws TikaConfigException {
        if (member instanceof java.lang.reflect.Field) {
            this.field = (java.lang.reflect.Field)member;
        } else {
            this.setter = (Method)member;
        }
        Field annotation = member.getAnnotation(Field.class);
        this.required = annotation.required();
        this.name = this.retrieveParamName(annotation);
        this.type = this.retrieveType();
    }

    public java.lang.reflect.Field getField() {
        return this.field;
    }

    public Method getSetter() {
        return this.setter;
    }

    public String getName() {
        return this.name;
    }

    public Class<?> getType() {
        return this.type;
    }

    public boolean isRequired() {
        return this.required;
    }

    public void assignValue(Object bean, Object value) throws IllegalAccessException, InvocationTargetException {
        if (this.field != null) {
            this.field.set(bean, value);
        } else {
            this.setter.invoke(bean, value);
        }
    }

    private Class retrieveType() throws TikaConfigException {
        Class<?> type;
        if (this.field != null) {
            type = this.field.getType();
        } else {
            Class<?>[] params = this.setter.getParameterTypes();
            if (params.length != 1) {
                String msg = "Invalid setter method. Must have one and only one parameter. ";
                if (this.setter.getName().startsWith("get")) {
                    msg = msg + "Perhaps the annotation is misplaced on " + this.setter.getName() + " while a set'X' is expected?";
                }
                throw new TikaConfigException(msg);
            }
            type = params[0];
        }
        if (type.isPrimitive() && PRIMITIVE_MAP.containsKey(type)) {
            type = PRIMITIVE_MAP.get(type);
        }
        return type;
    }

    private String retrieveParamName(Field annotation) {
        String setterName;
        String name = annotation.name().equals(DEFAULT) ? (this.field != null ? this.field.getName() : ((setterName = this.setter.getName()).startsWith("set") && setterName.length() > 3 ? setterName.substring(3, 4).toLowerCase(Locale.ROOT) + setterName.substring(4) : this.setter.getName())) : annotation.name();
        return name;
    }

    public String toString() {
        return "ParamField{name='" + this.name + '\'' + ", type=" + this.type + ", required=" + this.required + '}';
    }
}

