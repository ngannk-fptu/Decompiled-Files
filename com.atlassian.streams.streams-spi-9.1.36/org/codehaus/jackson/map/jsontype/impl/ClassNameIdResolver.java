/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.jackson.map.jsontype.impl;

import java.util.EnumMap;
import java.util.EnumSet;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.map.jsontype.impl.TypeIdResolverBase;
import org.codehaus.jackson.map.type.TypeFactory;
import org.codehaus.jackson.map.util.ClassUtil;
import org.codehaus.jackson.type.JavaType;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class ClassNameIdResolver
extends TypeIdResolverBase {
    public ClassNameIdResolver(JavaType baseType, TypeFactory typeFactory) {
        super(baseType, typeFactory);
    }

    @Override
    public JsonTypeInfo.Id getMechanism() {
        return JsonTypeInfo.Id.CLASS;
    }

    public void registerSubtype(Class<?> type, String name) {
    }

    @Override
    public String idFromValue(Object value) {
        return this._idFrom(value, value.getClass());
    }

    @Override
    public String idFromValueAndType(Object value, Class<?> type) {
        return this._idFrom(value, type);
    }

    @Override
    public JavaType typeFromId(String id) {
        if (id.indexOf(60) > 0) {
            JavaType t = this._typeFactory.constructFromCanonical(id);
            if (!t.isTypeOrSubTypeOf(this._baseType.getRawClass())) {
                throw new IllegalArgumentException(String.format("Class %s not subtype of %s", t.getRawClass().getName(), this._baseType));
            }
            return t;
        }
        try {
            Class<?> cls = ClassUtil.findClass(id);
            return this._typeFactory.constructSpecializedType(this._baseType, cls);
        }
        catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("Invalid type id '" + id + "' (for id type 'Id.class'): no such class found");
        }
        catch (Exception e) {
            throw new IllegalArgumentException("Invalid type id '" + id + "' (for id type 'Id.class'): " + e.getMessage(), e);
        }
    }

    protected final String _idFrom(Object value, Class<?> cls) {
        Class<?> staticType;
        Class<?> outer;
        String str;
        if (Enum.class.isAssignableFrom(cls) && !cls.isEnum()) {
            cls = cls.getSuperclass();
        }
        if ((str = cls.getName()).startsWith("java.util")) {
            if (value instanceof EnumSet) {
                Class<? extends Enum<?>> enumClass = ClassUtil.findEnumType((EnumSet)value);
                str = TypeFactory.defaultInstance().constructCollectionType(EnumSet.class, enumClass).toCanonical();
            } else if (value instanceof EnumMap) {
                Class<? extends Enum<?>> enumClass = ClassUtil.findEnumType((EnumMap)value);
                Class<Object> valueClass = Object.class;
                str = TypeFactory.defaultInstance().constructMapType(EnumMap.class, enumClass, valueClass).toCanonical();
            } else {
                String end = str.substring(9);
                if ((end.startsWith(".Arrays$") || end.startsWith(".Collections$")) && str.indexOf("List") >= 0) {
                    str = "java.util.ArrayList";
                }
            }
        } else if (str.indexOf(36) >= 0 && (outer = ClassUtil.getOuterClass(cls)) != null && ClassUtil.getOuterClass(staticType = this._baseType.getRawClass()) == null) {
            cls = this._baseType.getRawClass();
            str = cls.getName();
        }
        return str;
    }
}

