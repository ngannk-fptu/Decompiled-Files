/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.jackson.map.util;

import java.util.HashMap;
import org.codehaus.jackson.map.AnnotationIntrospector;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class EnumResolver<T extends Enum<T>> {
    protected final Class<T> _enumClass;
    protected final T[] _enums;
    protected final HashMap<String, T> _enumsById;

    protected EnumResolver(Class<T> enumClass, T[] enums, HashMap<String, T> map) {
        this._enumClass = enumClass;
        this._enums = enums;
        this._enumsById = map;
    }

    public static <ET extends Enum<ET>> EnumResolver<ET> constructFor(Class<ET> enumCls, AnnotationIntrospector ai) {
        Enum[] enumValues = (Enum[])enumCls.getEnumConstants();
        if (enumValues == null) {
            throw new IllegalArgumentException("No enum constants for class " + enumCls.getName());
        }
        HashMap<String, Enum> map = new HashMap<String, Enum>();
        for (Enum e : enumValues) {
            map.put(ai.findEnumValue(e), e);
        }
        return new EnumResolver(enumCls, enumValues, map);
    }

    public static <ET extends Enum<ET>> EnumResolver<ET> constructUsingToString(Class<ET> enumCls) {
        Enum[] enumValues = (Enum[])enumCls.getEnumConstants();
        HashMap<String, Enum> map = new HashMap<String, Enum>();
        int i = enumValues.length;
        while (--i >= 0) {
            Enum e = enumValues[i];
            map.put(e.toString(), e);
        }
        return new EnumResolver(enumCls, enumValues, map);
    }

    public static EnumResolver<?> constructUnsafe(Class<?> rawEnumCls, AnnotationIntrospector ai) {
        Class<?> enumCls = rawEnumCls;
        return EnumResolver.constructFor(enumCls, ai);
    }

    public static EnumResolver<?> constructUnsafeUsingToString(Class<?> rawEnumCls) {
        Class<?> enumCls = rawEnumCls;
        return EnumResolver.constructUsingToString(enumCls);
    }

    public T findEnum(String key) {
        return (T)((Enum)this._enumsById.get(key));
    }

    public T getEnum(int index) {
        if (index < 0 || index >= this._enums.length) {
            return null;
        }
        return this._enums[index];
    }

    public Class<T> getEnumClass() {
        return this._enumClass;
    }

    public int lastValidIndex() {
        return this._enums.length - 1;
    }
}

