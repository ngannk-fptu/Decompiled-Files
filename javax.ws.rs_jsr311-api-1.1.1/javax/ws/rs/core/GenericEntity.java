/*
 * Decompiled with CFR 0.152.
 */
package javax.ws.rs.core;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class GenericEntity<T> {
    final Class<?> rawType;
    final Type type;
    final T entity;

    protected GenericEntity(T entity) {
        if (entity == null) {
            throw new IllegalArgumentException("The entity must not be null");
        }
        this.entity = entity;
        this.type = GenericEntity.getSuperclassTypeParameter(this.getClass());
        this.rawType = entity.getClass();
    }

    public GenericEntity(T entity, Type genericType) {
        if (entity == null || genericType == null) {
            throw new IllegalArgumentException("Arguments must not be null");
        }
        this.entity = entity;
        this.rawType = entity.getClass();
        this.checkTypeCompatibility(this.rawType, genericType);
        this.type = genericType;
    }

    private void checkTypeCompatibility(Class<?> c, Type t) {
        if (t instanceof Class) {
            Class ct = (Class)t;
            if (ct.isAssignableFrom(c)) {
                return;
            }
        } else {
            if (t instanceof ParameterizedType) {
                ParameterizedType pt = (ParameterizedType)t;
                Type rt = pt.getRawType();
                this.checkTypeCompatibility(c, rt);
                return;
            }
            if (c.isArray() && t instanceof GenericArrayType) {
                GenericArrayType at = (GenericArrayType)t;
                Type rt = at.getGenericComponentType();
                this.checkTypeCompatibility(c.getComponentType(), rt);
                return;
            }
        }
        throw new IllegalArgumentException("The type is incompatible with the class of the entity");
    }

    private static Type getSuperclassTypeParameter(Class<?> subclass) {
        Type superclass = subclass.getGenericSuperclass();
        if (!(superclass instanceof ParameterizedType)) {
            throw new RuntimeException("Missing type parameter.");
        }
        ParameterizedType parameterized = (ParameterizedType)superclass;
        return parameterized.getActualTypeArguments()[0];
    }

    public final Class<?> getRawType() {
        return this.rawType;
    }

    public final Type getType() {
        return this.type;
    }

    public final T getEntity() {
        return this.entity;
    }
}

