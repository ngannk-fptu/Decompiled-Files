/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.jackson.type;

import java.lang.reflect.Modifier;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public abstract class JavaType {
    protected final Class<?> _class;
    protected final int _hashCode;
    protected Object _valueHandler;
    protected Object _typeHandler;

    protected JavaType(Class<?> raw, int additionalHash) {
        this._class = raw;
        this._hashCode = raw.getName().hashCode() + additionalHash;
        this._valueHandler = null;
        this._typeHandler = null;
    }

    public abstract JavaType withTypeHandler(Object var1);

    public abstract JavaType withContentTypeHandler(Object var1);

    public JavaType withValueHandler(Object h) {
        this.setValueHandler(h);
        return this;
    }

    public JavaType withContentValueHandler(Object h) {
        this.getContentType().setValueHandler(h);
        return this;
    }

    @Deprecated
    public void setValueHandler(Object h) {
        if (h != null && this._valueHandler != null) {
            throw new IllegalStateException("Trying to reset value handler for type [" + this.toString() + "]; old handler of type " + this._valueHandler.getClass().getName() + ", new handler of type " + h.getClass().getName());
        }
        this._valueHandler = h;
    }

    public JavaType narrowBy(Class<?> subclass) {
        if (subclass == this._class) {
            return this;
        }
        this._assertSubclass(subclass, this._class);
        JavaType result = this._narrow(subclass);
        if (this._valueHandler != result.getValueHandler()) {
            result = result.withValueHandler(this._valueHandler);
        }
        if (this._typeHandler != result.getTypeHandler()) {
            result = result.withTypeHandler(this._typeHandler);
        }
        return result;
    }

    public JavaType forcedNarrowBy(Class<?> subclass) {
        if (subclass == this._class) {
            return this;
        }
        JavaType result = this._narrow(subclass);
        if (this._valueHandler != result.getValueHandler()) {
            result = result.withValueHandler(this._valueHandler);
        }
        if (this._typeHandler != result.getTypeHandler()) {
            result = result.withTypeHandler(this._typeHandler);
        }
        return result;
    }

    public JavaType widenBy(Class<?> superclass) {
        if (superclass == this._class) {
            return this;
        }
        this._assertSubclass(this._class, superclass);
        return this._widen(superclass);
    }

    protected abstract JavaType _narrow(Class<?> var1);

    protected JavaType _widen(Class<?> superclass) {
        return this._narrow(superclass);
    }

    public abstract JavaType narrowContentsBy(Class<?> var1);

    public abstract JavaType widenContentsBy(Class<?> var1);

    public final Class<?> getRawClass() {
        return this._class;
    }

    public final boolean hasRawClass(Class<?> clz) {
        return this._class == clz;
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
        return this._class.isEnum();
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

    public boolean hasGenericTypes() {
        return this.containedTypeCount() > 0;
    }

    public JavaType getKeyType() {
        return null;
    }

    public JavaType getContentType() {
        return null;
    }

    public int containedTypeCount() {
        return 0;
    }

    public JavaType containedType(int index) {
        return null;
    }

    public String containedTypeName(int index) {
        return null;
    }

    public <T> T getValueHandler() {
        return (T)this._valueHandler;
    }

    public <T> T getTypeHandler() {
        return (T)this._typeHandler;
    }

    public abstract String toCanonical();

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

    public final boolean isTypeOrSubTypeOf(Class<?> clz) {
        return this._class == clz || clz.isAssignableFrom(this._class);
    }

    protected void _assertSubclass(Class<?> subclass, Class<?> superClass) {
        if (!this._class.isAssignableFrom(subclass)) {
            throw new IllegalArgumentException("Class " + subclass.getName() + " is not assignable to " + this._class.getName());
        }
    }

    public abstract String toString();

    public abstract boolean equals(Object var1);

    public final int hashCode() {
        return this._hashCode;
    }
}

