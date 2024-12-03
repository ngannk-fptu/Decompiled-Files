/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.type.JavaType
 */
package org.codehaus.jackson.map.type;

import java.util.Collection;
import org.codehaus.jackson.map.type.TypeBase;
import org.codehaus.jackson.type.JavaType;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class CollectionLikeType
extends TypeBase {
    protected final JavaType _elementType;

    @Deprecated
    protected CollectionLikeType(Class<?> collT, JavaType elemT) {
        super(collT, elemT.hashCode(), null, null);
        this._elementType = elemT;
    }

    protected CollectionLikeType(Class<?> collT, JavaType elemT, Object valueHandler, Object typeHandler) {
        super(collT, elemT.hashCode(), valueHandler, typeHandler);
        this._elementType = elemT;
    }

    protected JavaType _narrow(Class<?> subclass) {
        return new CollectionLikeType(subclass, this._elementType, this._valueHandler, this._typeHandler);
    }

    public JavaType narrowContentsBy(Class<?> contentClass) {
        if (contentClass == this._elementType.getRawClass()) {
            return this;
        }
        return new CollectionLikeType(this._class, this._elementType.narrowBy(contentClass), this._valueHandler, this._typeHandler);
    }

    public JavaType widenContentsBy(Class<?> contentClass) {
        if (contentClass == this._elementType.getRawClass()) {
            return this;
        }
        return new CollectionLikeType(this._class, this._elementType.widenBy(contentClass), this._valueHandler, this._typeHandler);
    }

    public static CollectionLikeType construct(Class<?> rawType, JavaType elemT) {
        return new CollectionLikeType(rawType, elemT, null, null);
    }

    public CollectionLikeType withTypeHandler(Object h) {
        return new CollectionLikeType(this._class, this._elementType, this._valueHandler, h);
    }

    public CollectionLikeType withContentTypeHandler(Object h) {
        return new CollectionLikeType(this._class, this._elementType.withTypeHandler(h), this._valueHandler, this._typeHandler);
    }

    public CollectionLikeType withValueHandler(Object h) {
        return new CollectionLikeType(this._class, this._elementType, h, this._typeHandler);
    }

    public CollectionLikeType withContentValueHandler(Object h) {
        return new CollectionLikeType(this._class, this._elementType.withValueHandler(h), this._valueHandler, this._typeHandler);
    }

    public boolean isContainerType() {
        return true;
    }

    public boolean isCollectionLikeType() {
        return true;
    }

    public JavaType getContentType() {
        return this._elementType;
    }

    public int containedTypeCount() {
        return 1;
    }

    public JavaType containedType(int index) {
        return index == 0 ? this._elementType : null;
    }

    public String containedTypeName(int index) {
        if (index == 0) {
            return "E";
        }
        return null;
    }

    @Override
    public StringBuilder getErasedSignature(StringBuilder sb) {
        return CollectionLikeType._classSignature(this._class, sb, true);
    }

    @Override
    public StringBuilder getGenericSignature(StringBuilder sb) {
        CollectionLikeType._classSignature(this._class, sb, false);
        sb.append('<');
        this._elementType.getGenericSignature(sb);
        sb.append(">;");
        return sb;
    }

    @Override
    protected String buildCanonicalName() {
        StringBuilder sb = new StringBuilder();
        sb.append(this._class.getName());
        if (this._elementType != null) {
            sb.append('<');
            sb.append(this._elementType.toCanonical());
            sb.append('>');
        }
        return sb.toString();
    }

    public boolean isTrueCollectionType() {
        return Collection.class.isAssignableFrom(this._class);
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
        CollectionLikeType other = (CollectionLikeType)o;
        return this._class == other._class && this._elementType.equals((Object)other._elementType);
    }

    public String toString() {
        return "[collection-like type; class " + this._class.getName() + ", contains " + this._elementType + "]";
    }
}

