/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.type.JavaType
 */
package org.codehaus.jackson.map.module;

import java.lang.reflect.Modifier;
import java.util.HashMap;
import org.codehaus.jackson.map.AbstractTypeResolver;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.type.ClassKey;
import org.codehaus.jackson.type.JavaType;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class SimpleAbstractTypeResolver
extends AbstractTypeResolver {
    protected final HashMap<ClassKey, Class<?>> _mappings = new HashMap();

    public <T> SimpleAbstractTypeResolver addMapping(Class<T> superType, Class<? extends T> subType) {
        if (superType == subType) {
            throw new IllegalArgumentException("Can not add mapping from class to itself");
        }
        if (!superType.isAssignableFrom(subType)) {
            throw new IllegalArgumentException("Can not add mapping from class " + superType.getName() + " to " + subType.getName() + ", as latter is not a subtype of former");
        }
        if (!Modifier.isAbstract(superType.getModifiers())) {
            throw new IllegalArgumentException("Can not add mapping from class " + superType.getName() + " since it is not abstract");
        }
        this._mappings.put(new ClassKey(superType), subType);
        return this;
    }

    @Override
    public JavaType findTypeMapping(DeserializationConfig config, JavaType type) {
        Class src = type.getRawClass();
        Class<?> dst = this._mappings.get(new ClassKey(src));
        if (dst == null) {
            return null;
        }
        return type.narrowBy(dst);
    }

    @Override
    public JavaType resolveAbstractType(DeserializationConfig config, JavaType type) {
        return null;
    }
}

