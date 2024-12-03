/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonTypeInfo$As
 *  org.codehaus.jackson.type.JavaType
 */
package org.codehaus.jackson.map.jsontype.impl;

import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.map.BeanProperty;
import org.codehaus.jackson.map.jsontype.TypeIdResolver;
import org.codehaus.jackson.map.jsontype.impl.AsArrayTypeDeserializer;
import org.codehaus.jackson.type.JavaType;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class AsExternalTypeDeserializer
extends AsArrayTypeDeserializer {
    protected final String _typePropertyName;

    public AsExternalTypeDeserializer(JavaType bt, TypeIdResolver idRes, BeanProperty property, Class<?> defaultImpl, String typePropName) {
        super(bt, idRes, property, defaultImpl);
        this._typePropertyName = typePropName;
    }

    @Override
    public JsonTypeInfo.As getTypeInclusion() {
        return JsonTypeInfo.As.EXTERNAL_PROPERTY;
    }

    @Override
    public String getPropertyName() {
        return this._typePropertyName;
    }
}

