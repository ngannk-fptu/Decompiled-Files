/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 */
package org.springframework.data.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;
import org.springframework.data.util.ClassTypeInformation;
import org.springframework.data.util.NullableWrapperConverters;
import org.springframework.lang.Nullable;

public interface TypeInformation<S> {
    public List<TypeInformation<?>> getParameterTypes(Constructor<?> var1);

    @Nullable
    public TypeInformation<?> getProperty(String var1);

    default public TypeInformation<?> getRequiredProperty(String property) {
        TypeInformation<?> typeInformation = this.getProperty(property);
        if (typeInformation != null) {
            return typeInformation;
        }
        throw new IllegalArgumentException(String.format("Could not find required property %s on %s!", property, this.getType()));
    }

    public boolean isCollectionLike();

    @Nullable
    public TypeInformation<?> getComponentType();

    default public TypeInformation<?> getRequiredComponentType() {
        TypeInformation<?> componentType = this.getComponentType();
        if (componentType != null) {
            return componentType;
        }
        throw new IllegalStateException(String.format("Can't resolve required component type for %s!", this.getType()));
    }

    public boolean isMap();

    @Nullable
    public TypeInformation<?> getMapValueType();

    default public TypeInformation<?> getRequiredMapValueType() {
        TypeInformation<?> mapValueType = this.getMapValueType();
        if (mapValueType != null) {
            return mapValueType;
        }
        throw new IllegalStateException(String.format("Can't resolve required map value type for %s!", this.getType()));
    }

    public Class<S> getType();

    public ClassTypeInformation<?> getRawTypeInformation();

    @Nullable
    public TypeInformation<?> getActualType();

    default public TypeInformation<?> getRequiredActualType() {
        TypeInformation<?> result = this.getActualType();
        if (result == null) {
            throw new IllegalStateException("Expected to be able to resolve a type but got null! This usually stems from types implementing raw Map or Collection interfaces!");
        }
        return result;
    }

    public TypeInformation<?> getReturnType(Method var1);

    public List<TypeInformation<?>> getParameterTypes(Method var1);

    @Nullable
    public TypeInformation<?> getSuperTypeInformation(Class<?> var1);

    default public TypeInformation<?> getRequiredSuperTypeInformation(Class<?> superType) {
        TypeInformation<?> result = this.getSuperTypeInformation(superType);
        if (result == null) {
            throw new IllegalArgumentException(String.format("Can't retrieve super type information for %s! Does current type really implement the given one?", superType));
        }
        return result;
    }

    public boolean isAssignableFrom(TypeInformation<?> var1);

    public List<TypeInformation<?>> getTypeArguments();

    public TypeInformation<? extends S> specialize(ClassTypeInformation<?> var1);

    default public boolean isSubTypeOf(Class<?> type) {
        return !type.equals(this.getType()) && type.isAssignableFrom(this.getType());
    }

    default public boolean isNullableWrapper() {
        return NullableWrapperConverters.supports(this.getType());
    }
}

