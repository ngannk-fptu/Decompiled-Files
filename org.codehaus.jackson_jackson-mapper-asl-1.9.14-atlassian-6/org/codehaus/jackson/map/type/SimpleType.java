/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.type.JavaType
 */
package org.codehaus.jackson.map.type;

import java.util.Collection;
import java.util.Map;
import org.codehaus.jackson.map.type.TypeBase;
import org.codehaus.jackson.type.JavaType;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class SimpleType
extends TypeBase {
    protected final JavaType[] _typeParameters;
    protected final String[] _typeNames;

    protected SimpleType(Class<?> cls) {
        this(cls, null, null, null, null);
    }

    @Deprecated
    protected SimpleType(Class<?> cls, String[] typeNames, JavaType[] typeParams) {
        this(cls, typeNames, typeParams, null, null);
    }

    protected SimpleType(Class<?> cls, String[] typeNames, JavaType[] typeParams, Object valueHandler, Object typeHandler) {
        super(cls, 0, valueHandler, typeHandler);
        if (typeNames == null || typeNames.length == 0) {
            this._typeNames = null;
            this._typeParameters = null;
        } else {
            this._typeNames = typeNames;
            this._typeParameters = typeParams;
        }
    }

    public static SimpleType constructUnsafe(Class<?> raw) {
        return new SimpleType(raw, null, null, null, null);
    }

    protected JavaType _narrow(Class<?> subclass) {
        return new SimpleType(subclass, this._typeNames, this._typeParameters, this._valueHandler, this._typeHandler);
    }

    public JavaType narrowContentsBy(Class<?> subclass) {
        throw new IllegalArgumentException("Internal error: SimpleType.narrowContentsBy() should never be called");
    }

    public JavaType widenContentsBy(Class<?> subclass) {
        throw new IllegalArgumentException("Internal error: SimpleType.widenContentsBy() should never be called");
    }

    public static SimpleType construct(Class<?> cls) {
        if (Map.class.isAssignableFrom(cls)) {
            throw new IllegalArgumentException("Can not construct SimpleType for a Map (class: " + cls.getName() + ")");
        }
        if (Collection.class.isAssignableFrom(cls)) {
            throw new IllegalArgumentException("Can not construct SimpleType for a Collection (class: " + cls.getName() + ")");
        }
        if (cls.isArray()) {
            throw new IllegalArgumentException("Can not construct SimpleType for an array (class: " + cls.getName() + ")");
        }
        return new SimpleType(cls);
    }

    public SimpleType withTypeHandler(Object h) {
        return new SimpleType(this._class, this._typeNames, this._typeParameters, this._valueHandler, h);
    }

    public JavaType withContentTypeHandler(Object h) {
        throw new IllegalArgumentException("Simple types have no content types; can not call withContenTypeHandler()");
    }

    public SimpleType withValueHandler(Object h) {
        if (h == this._valueHandler) {
            return this;
        }
        return new SimpleType(this._class, this._typeNames, this._typeParameters, h, this._typeHandler);
    }

    public SimpleType withContentValueHandler(Object h) {
        throw new IllegalArgumentException("Simple types have no content types; can not call withContenValueHandler()");
    }

    @Override
    protected String buildCanonicalName() {
        StringBuilder sb = new StringBuilder();
        sb.append(this._class.getName());
        if (this._typeParameters != null && this._typeParameters.length > 0) {
            sb.append('<');
            boolean first = true;
            for (JavaType t : this._typeParameters) {
                if (first) {
                    first = false;
                } else {
                    sb.append(',');
                }
                sb.append(t.toCanonical());
            }
            sb.append('>');
        }
        return sb.toString();
    }

    public boolean isContainerType() {
        return false;
    }

    public int containedTypeCount() {
        return this._typeParameters == null ? 0 : this._typeParameters.length;
    }

    public JavaType containedType(int index) {
        if (index < 0 || this._typeParameters == null || index >= this._typeParameters.length) {
            return null;
        }
        return this._typeParameters[index];
    }

    public String containedTypeName(int index) {
        if (index < 0 || this._typeNames == null || index >= this._typeNames.length) {
            return null;
        }
        return this._typeNames[index];
    }

    @Override
    public StringBuilder getErasedSignature(StringBuilder sb) {
        return SimpleType._classSignature(this._class, sb, true);
    }

    @Override
    public StringBuilder getGenericSignature(StringBuilder sb) {
        SimpleType._classSignature(this._class, sb, false);
        if (this._typeParameters != null) {
            sb.append('<');
            for (JavaType param : this._typeParameters) {
                sb = param.getGenericSignature(sb);
            }
            sb.append('>');
        }
        sb.append(';');
        return sb;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(40);
        sb.append("[simple type, class ").append(this.buildCanonicalName()).append(']');
        return sb.toString();
    }

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
        SimpleType other = (SimpleType)o;
        if (other._class != this._class) {
            return false;
        }
        JavaType[] p1 = this._typeParameters;
        JavaType[] p2 = other._typeParameters;
        if (p1 == null) {
            return p2 == null || p2.length == 0;
        }
        if (p2 == null) {
            return false;
        }
        if (p1.length != p2.length) {
            return false;
        }
        int len = p1.length;
        for (int i = 0; i < len; ++i) {
            if (p1[i].equals((Object)p2[i])) continue;
            return false;
        }
        return true;
    }
}

