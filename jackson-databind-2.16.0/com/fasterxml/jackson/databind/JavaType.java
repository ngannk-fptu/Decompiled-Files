/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.core.type.ResolvedType
 */
package com.fasterxml.jackson.databind;

import com.fasterxml.jackson.core.type.ResolvedType;
import com.fasterxml.jackson.databind.type.TypeBindings;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.databind.util.ClassUtil;
import java.io.Serializable;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.List;

public abstract class JavaType
extends ResolvedType
implements Serializable,
Type {
    private static final long serialVersionUID = 1L;
    protected final Class<?> _class;
    protected final int _hash;
    protected final Object _valueHandler;
    protected final Object _typeHandler;
    protected final boolean _asStatic;

    protected JavaType(Class<?> raw, int additionalHash, Object valueHandler, Object typeHandler, boolean asStatic) {
        this._class = raw;
        this._hash = 31 * additionalHash + raw.hashCode();
        this._valueHandler = valueHandler;
        this._typeHandler = typeHandler;
        this._asStatic = asStatic;
    }

    protected JavaType(JavaType base) {
        this._class = base._class;
        this._hash = base._hash;
        this._valueHandler = base._valueHandler;
        this._typeHandler = base._typeHandler;
        this._asStatic = base._asStatic;
    }

    public abstract JavaType withContentType(JavaType var1);

    public abstract JavaType withStaticTyping();

    public abstract JavaType withTypeHandler(Object var1);

    public abstract JavaType withContentTypeHandler(Object var1);

    public abstract JavaType withValueHandler(Object var1);

    public abstract JavaType withContentValueHandler(Object var1);

    public JavaType withHandlersFrom(JavaType src) {
        JavaType type = this;
        Object h = src.getTypeHandler();
        if (h != this._typeHandler) {
            type = type.withTypeHandler(h);
        }
        if ((h = src.getValueHandler()) != this._valueHandler) {
            type = type.withValueHandler(h);
        }
        return type;
    }

    public abstract JavaType refine(Class<?> var1, TypeBindings var2, JavaType var3, JavaType[] var4);

    @Deprecated
    public JavaType forcedNarrowBy(Class<?> subclass) {
        if (subclass == this._class) {
            return this;
        }
        return this._narrow(subclass);
    }

    @Deprecated
    protected JavaType _narrow(Class<?> subclass) {
        return this;
    }

    public final Class<?> getRawClass() {
        return this._class;
    }

    public final boolean hasRawClass(Class<?> clz) {
        return this._class == clz;
    }

    public boolean hasContentType() {
        return true;
    }

    public final boolean isTypeOrSubTypeOf(Class<?> clz) {
        return this._class == clz || clz.isAssignableFrom(this._class);
    }

    public final boolean isTypeOrSuperTypeOf(Class<?> clz) {
        return this._class == clz || this._class.isAssignableFrom(clz);
    }

    public boolean isAbstract() {
        return Modifier.isAbstract(this._class.getModifiers());
    }

    public boolean isConcrete() {
        int mod = this._class.getModifiers();
        if ((mod & 0x600) == 0) {
            return true;
        }
        return this._class.isPrimitive();
    }

    public boolean isThrowable() {
        return Throwable.class.isAssignableFrom(this._class);
    }

    public boolean isArrayType() {
        return false;
    }

    public final boolean isEnumType() {
        return ClassUtil.isEnumType(this._class);
    }

    public final boolean isEnumImplType() {
        return ClassUtil.isEnumType(this._class) && this._class != Enum.class;
    }

    public final boolean isRecordType() {
        return ClassUtil.isRecordType(this._class);
    }

    public boolean isIterationType() {
        return false;
    }

    public final boolean isInterface() {
        return this._class.isInterface();
    }

    public final boolean isPrimitive() {
        return this._class.isPrimitive();
    }

    public final boolean isFinal() {
        return Modifier.isFinal(this._class.getModifiers());
    }

    public abstract boolean isContainerType();

    public boolean isCollectionLikeType() {
        return false;
    }

    public boolean isMapLikeType() {
        return false;
    }

    public final boolean isJavaLangObject() {
        return this._class == Object.class;
    }

    public final boolean useStaticType() {
        return this._asStatic;
    }

    public boolean hasGenericTypes() {
        return this.containedTypeCount() > 0;
    }

    public JavaType getKeyType() {
        return null;
    }

    public JavaType getContentType() {
        return null;
    }

    public JavaType getReferencedType() {
        return null;
    }

    public abstract int containedTypeCount();

    public abstract JavaType containedType(int var1);

    @Deprecated
    public abstract String containedTypeName(int var1);

    @Deprecated
    public Class<?> getParameterSource() {
        return null;
    }

    public JavaType containedTypeOrUnknown(int index) {
        JavaType t = this.containedType(index);
        return t == null ? TypeFactory.unknownType() : t;
    }

    public abstract TypeBindings getBindings();

    public abstract JavaType findSuperType(Class<?> var1);

    public abstract JavaType getSuperClass();

    public abstract List<JavaType> getInterfaces();

    public abstract JavaType[] findTypeParameters(Class<?> var1);

    public <T> T getValueHandler() {
        return (T)this._valueHandler;
    }

    public <T> T getTypeHandler() {
        return (T)this._typeHandler;
    }

    public Object getContentValueHandler() {
        return null;
    }

    public Object getContentTypeHandler() {
        return null;
    }

    public boolean hasValueHandler() {
        return this._valueHandler != null;
    }

    public boolean hasHandlers() {
        return this._typeHandler != null || this._valueHandler != null;
    }

    public String getGenericSignature() {
        StringBuilder sb = new StringBuilder(40);
        this.getGenericSignature(sb);
        return sb.toString();
    }

    public abstract StringBuilder getGenericSignature(StringBuilder var1);

    public String getErasedSignature() {
        StringBuilder sb = new StringBuilder(40);
        this.getErasedSignature(sb);
        return sb.toString();
    }

    public abstract StringBuilder getErasedSignature(StringBuilder var1);

    public abstract String toString();

    public abstract boolean equals(Object var1);

    public int hashCode() {
        return this._hash;
    }
}

