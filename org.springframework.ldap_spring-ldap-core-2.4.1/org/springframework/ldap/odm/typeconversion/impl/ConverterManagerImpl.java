/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.ldap.odm.typeconversion.impl;

import java.util.HashMap;
import java.util.Map;
import org.springframework.ldap.odm.typeconversion.ConverterException;
import org.springframework.ldap.odm.typeconversion.ConverterManager;
import org.springframework.ldap.odm.typeconversion.impl.Converter;

public final class ConverterManagerImpl
implements ConverterManager {
    private static final String KEY_SEP = ":";
    private final Map<String, Converter> converters = new HashMap<String, Converter>();
    private static Map<Class<?>, Class<?>> primitiveTypeMap = new HashMap();

    private String makeConverterKey(Class<?> fromClass, String syntax, Class<?> toClass) {
        StringBuilder key = new StringBuilder();
        if (syntax == null) {
            syntax = "";
        }
        key.append(fromClass.getName()).append(KEY_SEP).append(syntax).append(KEY_SEP).append(toClass.getName());
        return key.toString();
    }

    @Override
    public boolean canConvert(Class<?> fromClass, String syntax, Class<?> toClass) {
        Class<?> fixedToClass = toClass;
        if (toClass.isPrimitive()) {
            fixedToClass = primitiveTypeMap.get(toClass);
        }
        Class<?> fixedFromClass = fromClass;
        if (fromClass.isPrimitive()) {
            fixedFromClass = primitiveTypeMap.get(fromClass);
        }
        return fixedToClass.isAssignableFrom(fixedFromClass) || this.converters.get(this.makeConverterKey(fixedFromClass, syntax, fixedToClass)) != null || this.converters.get(this.makeConverterKey(fixedFromClass, null, fixedToClass)) != null;
    }

    @Override
    public <T> T convert(Object source, String syntax, Class<T> toClass) {
        Converter nullSyntaxConverter;
        Converter syntaxConverter;
        Object result = null;
        Class<?> fromClass = source.getClass();
        Class<Object> targetClass = toClass;
        if (toClass.isPrimitive()) {
            targetClass = primitiveTypeMap.get(toClass);
        }
        if ((syntaxConverter = this.converters.get(this.makeConverterKey(fromClass, syntax, targetClass))) != null) {
            try {
                result = syntaxConverter.convert(source, targetClass);
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
        if (result == null && targetClass.isAssignableFrom(fromClass)) {
            result = source;
        }
        if (result == null && syntax != null && (nullSyntaxConverter = this.converters.get(this.makeConverterKey(fromClass, null, targetClass))) != null) {
            try {
                result = nullSyntaxConverter.convert(source, targetClass);
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
        if (result == null) {
            throw new ConverterException(String.format("Cannot convert %1$s of class %2$s via syntax %3$s to class %4$s", source, source.getClass(), syntax, toClass));
        }
        return (T)result;
    }

    public void addConverter(Class<?> fromClass, String syntax, Class<?> toClass, Converter converter) {
        this.converters.put(this.makeConverterKey(fromClass, syntax, toClass), converter);
    }

    static {
        primitiveTypeMap.put(Byte.TYPE, Byte.class);
        primitiveTypeMap.put(Short.TYPE, Short.class);
        primitiveTypeMap.put(Integer.TYPE, Integer.class);
        primitiveTypeMap.put(Long.TYPE, Long.class);
        primitiveTypeMap.put(Float.TYPE, Float.class);
        primitiveTypeMap.put(Double.TYPE, Double.class);
        primitiveTypeMap.put(Boolean.TYPE, Boolean.class);
        primitiveTypeMap.put(Character.TYPE, Character.class);
    }
}

