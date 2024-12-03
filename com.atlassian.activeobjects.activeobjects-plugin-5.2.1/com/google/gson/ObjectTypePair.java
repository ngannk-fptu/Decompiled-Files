/*
 * Decompiled with CFR 0.152.
 */
package com.google.gson;

import com.google.gson.Pair;
import com.google.gson.ParameterizedTypeHandlerMap;
import java.lang.reflect.Type;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
final class ObjectTypePair {
    private Object obj;
    final Type type;
    private final boolean preserveType;

    ObjectTypePair(Object obj, Type type, boolean preserveType) {
        this.obj = obj;
        this.type = type;
        this.preserveType = preserveType;
    }

    Object getObject() {
        return this.obj;
    }

    void setObject(Object obj) {
        this.obj = obj;
    }

    Type getType() {
        return this.type;
    }

    public String toString() {
        return String.format("preserveType: %b, type: %s, obj: %s", this.preserveType, this.type, this.obj);
    }

    <HANDLER> Pair<HANDLER, ObjectTypePair> getMatchingHandler(ParameterizedTypeHandlerMap<HANDLER> handlers) {
        Object handler = null;
        if (!this.preserveType && this.obj != null) {
            ObjectTypePair moreSpecificType = this.toMoreSpecificType();
            handler = handlers.getHandlerFor(moreSpecificType.type);
            if (handler != null) {
                return new Pair<Object, ObjectTypePair>(handler, moreSpecificType);
            }
        }
        return (handler = (Object)handlers.getHandlerFor(this.type)) == null ? null : new Pair<Object, ObjectTypePair>(handler, this);
    }

    ObjectTypePair toMoreSpecificType() {
        if (this.preserveType || this.obj == null) {
            return this;
        }
        Type actualType = ObjectTypePair.getActualTypeIfMoreSpecific(this.type, this.obj.getClass());
        if (actualType == this.type) {
            return this;
        }
        return new ObjectTypePair(this.obj, actualType, this.preserveType);
    }

    Type getMoreSpecificType() {
        if (this.preserveType || this.obj == null) {
            return this.type;
        }
        return ObjectTypePair.getActualTypeIfMoreSpecific(this.type, this.obj.getClass());
    }

    static Type getActualTypeIfMoreSpecific(Type type, Class<?> actualClass) {
        if (type instanceof Class) {
            Class typeAsClass = type;
            if (typeAsClass.isAssignableFrom(actualClass)) {
                type = actualClass;
            }
            if (type == Object.class) {
                type = actualClass;
            }
        }
        return type;
    }

    public int hashCode() {
        return this.obj == null ? 31 : this.obj.hashCode();
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        ObjectTypePair other = (ObjectTypePair)obj;
        if (this.obj == null ? other.obj != null : this.obj != other.obj) {
            return false;
        }
        if (this.type == null ? other.type != null : !this.type.equals(other.type)) {
            return false;
        }
        return this.preserveType == other.preserveType;
    }

    public boolean isPreserveType() {
        return this.preserveType;
    }
}

