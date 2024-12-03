/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.jackson.map.type;

import java.util.Map;
import org.codehaus.jackson.map.type.TypeBase;
import org.codehaus.jackson.type.JavaType;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class MapLikeType
extends TypeBase {
    protected final JavaType _keyType;
    protected final JavaType _valueType;

    @Deprecated
    protected MapLikeType(Class<?> mapType, JavaType keyT, JavaType valueT) {
        super(mapType, keyT.hashCode() ^ valueT.hashCode(), null, null);
        this._keyType = keyT;
        this._valueType = valueT;
    }

    protected MapLikeType(Class<?> mapType, JavaType keyT, JavaType valueT, Object valueHandler, Object typeHandler) {
        super(mapType, keyT.hashCode() ^ valueT.hashCode(), valueHandler, typeHandler);
        this._keyType = keyT;
        this._valueType = valueT;
    }

    public static MapLikeType construct(Class<?> rawType, JavaType keyT, JavaType valueT) {
        return new MapLikeType(rawType, keyT, valueT, null, null);
    }

    @Override
    protected JavaType _narrow(Class<?> subclass) {
        return new MapLikeType(subclass, this._keyType, this._valueType, this._valueHandler, this._typeHandler);
    }

    @Override
    public JavaType narrowContentsBy(Class<?> contentClass) {
        if (contentClass == this._valueType.getRawClass()) {
            return this;
        }
        return new MapLikeType(this._class, this._keyType, this._valueType.narrowBy(contentClass), this._valueHandler, this._typeHandler);
    }

    @Override
    public JavaType widenContentsBy(Class<?> contentClass) {
        if (contentClass == this._valueType.getRawClass()) {
            return this;
        }
        return new MapLikeType(this._class, this._keyType, this._valueType.widenBy(contentClass), this._valueHandler, this._typeHandler);
    }

    public JavaType narrowKey(Class<?> keySubclass) {
        if (keySubclass == this._keyType.getRawClass()) {
            return this;
        }
        return new MapLikeType(this._class, this._keyType.narrowBy(keySubclass), this._valueType, this._valueHandler, this._typeHandler);
    }

    public JavaType widenKey(Class<?> keySubclass) {
        if (keySubclass == this._keyType.getRawClass()) {
            return this;
        }
        return new MapLikeType(this._class, this._keyType.widenBy(keySubclass), this._valueType, this._valueHandler, this._typeHandler);
    }

    @Override
    public MapLikeType withTypeHandler(Object h) {
        return new MapLikeType(this._class, this._keyType, this._valueType, this._valueHandler, h);
    }

    @Override
    public MapLikeType withContentTypeHandler(Object h) {
        return new MapLikeType(this._class, this._keyType, this._valueType.withTypeHandler(h), this._valueHandler, this._typeHandler);
    }

    @Override
    public MapLikeType withValueHandler(Object h) {
        return new MapLikeType(this._class, this._keyType, this._valueType, h, this._typeHandler);
    }

    @Override
    public MapLikeType withContentValueHandler(Object h) {
        return new MapLikeType(this._class, this._keyType, this._valueType.withValueHandler(h), this._valueHandler, this._typeHandler);
    }

    @Override
    protected String buildCanonicalName() {
        StringBuilder sb = new StringBuilder();
        sb.append(this._class.getName());
        if (this._keyType != null) {
            sb.append('<');
            sb.append(this._keyType.toCanonical());
            sb.append(',');
            sb.append(this._valueType.toCanonical());
            sb.append('>');
        }
        return sb.toString();
    }

    @Override
    public boolean isContainerType() {
        return true;
    }

    @Override
    public boolean isMapLikeType() {
        return true;
    }

    @Override
    public JavaType getKeyType() {
        return this._keyType;
    }

    @Override
    public JavaType getContentType() {
        return this._valueType;
    }

    @Override
    public int containedTypeCount() {
        return 2;
    }

    @Override
    public JavaType containedType(int index) {
        if (index == 0) {
            return this._keyType;
        }
        if (index == 1) {
            return this._valueType;
        }
        return null;
    }

    @Override
    public String containedTypeName(int index) {
        if (index == 0) {
            return "K";
        }
        if (index == 1) {
            return "V";
        }
        return null;
    }

    @Override
    public StringBuilder getErasedSignature(StringBuilder sb) {
        return MapLikeType._classSignature(this._class, sb, true);
    }

    @Override
    public StringBuilder getGenericSignature(StringBuilder sb) {
        MapLikeType._classSignature(this._class, sb, false);
        sb.append('<');
        this._keyType.getGenericSignature(sb);
        this._valueType.getGenericSignature(sb);
        sb.append(">;");
        return sb;
    }

    public MapLikeType withKeyTypeHandler(Object h) {
        return new MapLikeType(this._class, this._keyType.withTypeHandler(h), this._valueType, this._valueHandler, this._typeHandler);
    }

    public MapLikeType withKeyValueHandler(Object h) {
        return new MapLikeType(this._class, this._keyType.withValueHandler(h), this._valueType, this._valueHandler, this._typeHandler);
    }

    public boolean isTrueMapType() {
        return Map.class.isAssignableFrom(this._class);
    }

    @Override
    public String toString() {
        return "[map-like type; class " + this._class.getName() + ", " + this._keyType + " -> " + this._valueType + "]";
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
        MapLikeType other = (MapLikeType)o;
        return this._class == other._class && this._keyType.equals(other._keyType) && this._valueType.equals(other._valueType);
    }
}

