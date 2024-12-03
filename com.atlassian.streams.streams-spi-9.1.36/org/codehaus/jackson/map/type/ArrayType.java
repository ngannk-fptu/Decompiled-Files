/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.jackson.map.type;

import java.lang.reflect.Array;
import org.codehaus.jackson.map.type.TypeBase;
import org.codehaus.jackson.map.type.TypeFactory;
import org.codehaus.jackson.type.JavaType;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class ArrayType
extends TypeBase {
    protected final JavaType _componentType;
    protected final Object _emptyArray;

    private ArrayType(JavaType componentType, Object emptyInstance, Object valueHandler, Object typeHandler) {
        super(emptyInstance.getClass(), componentType.hashCode(), valueHandler, typeHandler);
        this._componentType = componentType;
        this._emptyArray = emptyInstance;
    }

    @Deprecated
    public static ArrayType construct(JavaType componentType) {
        return ArrayType.construct(componentType, null, null);
    }

    public static ArrayType construct(JavaType componentType, Object valueHandler, Object typeHandler) {
        Object emptyInstance = Array.newInstance(componentType.getRawClass(), 0);
        return new ArrayType(componentType, emptyInstance, null, null);
    }

    @Override
    public ArrayType withTypeHandler(Object h) {
        if (h == this._typeHandler) {
            return this;
        }
        return new ArrayType(this._componentType, this._emptyArray, this._valueHandler, h);
    }

    @Override
    public ArrayType withContentTypeHandler(Object h) {
        if (h == this._componentType.getTypeHandler()) {
            return this;
        }
        return new ArrayType(this._componentType.withTypeHandler(h), this._emptyArray, this._valueHandler, this._typeHandler);
    }

    @Override
    public ArrayType withValueHandler(Object h) {
        if (h == this._valueHandler) {
            return this;
        }
        return new ArrayType(this._componentType, this._emptyArray, h, this._typeHandler);
    }

    @Override
    public ArrayType withContentValueHandler(Object h) {
        if (h == this._componentType.getValueHandler()) {
            return this;
        }
        return new ArrayType(this._componentType.withValueHandler(h), this._emptyArray, this._valueHandler, this._typeHandler);
    }

    @Override
    protected String buildCanonicalName() {
        return this._class.getName();
    }

    @Override
    protected JavaType _narrow(Class<?> subclass) {
        if (!subclass.isArray()) {
            throw new IllegalArgumentException("Incompatible narrowing operation: trying to narrow " + this.toString() + " to class " + subclass.getName());
        }
        Class<?> newCompClass = subclass.getComponentType();
        JavaType newCompType = TypeFactory.defaultInstance().constructType(newCompClass);
        return ArrayType.construct(newCompType, this._valueHandler, this._typeHandler);
    }

    @Override
    public JavaType narrowContentsBy(Class<?> contentClass) {
        if (contentClass == this._componentType.getRawClass()) {
            return this;
        }
        return ArrayType.construct(this._componentType.narrowBy(contentClass), this._valueHandler, this._typeHandler);
    }

    @Override
    public JavaType widenContentsBy(Class<?> contentClass) {
        if (contentClass == this._componentType.getRawClass()) {
            return this;
        }
        return ArrayType.construct(this._componentType.widenBy(contentClass), this._valueHandler, this._typeHandler);
    }

    @Override
    public boolean isArrayType() {
        return true;
    }

    @Override
    public boolean isAbstract() {
        return false;
    }

    @Override
    public boolean isConcrete() {
        return true;
    }

    @Override
    public boolean hasGenericTypes() {
        return this._componentType.hasGenericTypes();
    }

    @Override
    public String containedTypeName(int index) {
        if (index == 0) {
            return "E";
        }
        return null;
    }

    @Override
    public boolean isContainerType() {
        return true;
    }

    @Override
    public JavaType getContentType() {
        return this._componentType;
    }

    @Override
    public int containedTypeCount() {
        return 1;
    }

    @Override
    public JavaType containedType(int index) {
        return index == 0 ? this._componentType : null;
    }

    @Override
    public StringBuilder getGenericSignature(StringBuilder sb) {
        sb.append('[');
        return this._componentType.getGenericSignature(sb);
    }

    @Override
    public StringBuilder getErasedSignature(StringBuilder sb) {
        sb.append('[');
        return this._componentType.getErasedSignature(sb);
    }

    @Override
    public String toString() {
        return "[array type, component type: " + this._componentType + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (o.getClass() != this.getClass()) {
            return false;
        }
        ArrayType other = (ArrayType)o;
        return this._componentType.equals(other._componentType);
    }
}

