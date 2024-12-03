/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.type.JavaType
 */
package org.codehaus.jackson.map.type;

import org.codehaus.jackson.map.type.MapLikeType;
import org.codehaus.jackson.type.JavaType;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class MapType
extends MapLikeType {
    @Deprecated
    private MapType(Class<?> mapType, JavaType keyT, JavaType valueT) {
        this(mapType, keyT, valueT, null, null);
    }

    private MapType(Class<?> mapType, JavaType keyT, JavaType valueT, Object valueHandler, Object typeHandler) {
        super(mapType, keyT, valueT, valueHandler, typeHandler);
    }

    public static MapType construct(Class<?> rawType, JavaType keyT, JavaType valueT) {
        return new MapType(rawType, keyT, valueT, null, null);
    }

    @Override
    protected JavaType _narrow(Class<?> subclass) {
        return new MapType(subclass, this._keyType, this._valueType, this._valueHandler, this._typeHandler);
    }

    @Override
    public JavaType narrowContentsBy(Class<?> contentClass) {
        if (contentClass == this._valueType.getRawClass()) {
            return this;
        }
        return new MapType(this._class, this._keyType, this._valueType.narrowBy(contentClass), this._valueHandler, this._typeHandler);
    }

    @Override
    public JavaType widenContentsBy(Class<?> contentClass) {
        if (contentClass == this._valueType.getRawClass()) {
            return this;
        }
        return new MapType(this._class, this._keyType, this._valueType.widenBy(contentClass), this._valueHandler, this._typeHandler);
    }

    @Override
    public JavaType narrowKey(Class<?> keySubclass) {
        if (keySubclass == this._keyType.getRawClass()) {
            return this;
        }
        return new MapType(this._class, this._keyType.narrowBy(keySubclass), this._valueType, this._valueHandler, this._typeHandler);
    }

    @Override
    public JavaType widenKey(Class<?> keySubclass) {
        if (keySubclass == this._keyType.getRawClass()) {
            return this;
        }
        return new MapType(this._class, this._keyType.widenBy(keySubclass), this._valueType, this._valueHandler, this._typeHandler);
    }

    @Override
    public MapType withTypeHandler(Object h) {
        return new MapType(this._class, this._keyType, this._valueType, this._valueHandler, h);
    }

    @Override
    public MapType withContentTypeHandler(Object h) {
        return new MapType(this._class, this._keyType, this._valueType.withTypeHandler(h), this._valueHandler, this._typeHandler);
    }

    @Override
    public MapType withValueHandler(Object h) {
        return new MapType(this._class, this._keyType, this._valueType, h, this._typeHandler);
    }

    @Override
    public MapType withContentValueHandler(Object h) {
        return new MapType(this._class, this._keyType, this._valueType.withValueHandler(h), this._valueHandler, this._typeHandler);
    }

    @Override
    public MapType withKeyTypeHandler(Object h) {
        return new MapType(this._class, this._keyType.withTypeHandler(h), this._valueType, this._valueHandler, this._typeHandler);
    }

    @Override
    public MapType withKeyValueHandler(Object h) {
        return new MapType(this._class, this._keyType.withValueHandler(h), this._valueType, this._valueHandler, this._typeHandler);
    }

    @Override
    public String toString() {
        return "[map type; class " + this._class.getName() + ", " + this._keyType + " -> " + this._valueType + "]";
    }
}

