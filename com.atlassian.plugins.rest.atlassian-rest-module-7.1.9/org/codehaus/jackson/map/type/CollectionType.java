/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.jackson.map.type;

import org.codehaus.jackson.map.type.CollectionLikeType;
import org.codehaus.jackson.type.JavaType;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class CollectionType
extends CollectionLikeType {
    private CollectionType(Class<?> collT, JavaType elemT, Object valueHandler, Object typeHandler) {
        super(collT, elemT, valueHandler, typeHandler);
    }

    @Override
    protected JavaType _narrow(Class<?> subclass) {
        return new CollectionType(subclass, this._elementType, null, null);
    }

    @Override
    public JavaType narrowContentsBy(Class<?> contentClass) {
        if (contentClass == this._elementType.getRawClass()) {
            return this;
        }
        return new CollectionType(this._class, this._elementType.narrowBy(contentClass), this._valueHandler, this._typeHandler);
    }

    @Override
    public JavaType widenContentsBy(Class<?> contentClass) {
        if (contentClass == this._elementType.getRawClass()) {
            return this;
        }
        return new CollectionType(this._class, this._elementType.widenBy(contentClass), this._valueHandler, this._typeHandler);
    }

    public static CollectionType construct(Class<?> rawType, JavaType elemT) {
        return new CollectionType(rawType, elemT, null, null);
    }

    @Override
    public CollectionType withTypeHandler(Object h) {
        return new CollectionType(this._class, this._elementType, this._valueHandler, h);
    }

    @Override
    public CollectionType withContentTypeHandler(Object h) {
        return new CollectionType(this._class, this._elementType.withTypeHandler(h), this._valueHandler, this._typeHandler);
    }

    @Override
    public CollectionType withValueHandler(Object h) {
        return new CollectionType(this._class, this._elementType, h, this._typeHandler);
    }

    @Override
    public CollectionType withContentValueHandler(Object h) {
        return new CollectionType(this._class, this._elementType.withValueHandler(h), this._valueHandler, this._typeHandler);
    }

    @Override
    public String toString() {
        return "[collection type; class " + this._class.getName() + ", contains " + this._elementType + "]";
    }
}

