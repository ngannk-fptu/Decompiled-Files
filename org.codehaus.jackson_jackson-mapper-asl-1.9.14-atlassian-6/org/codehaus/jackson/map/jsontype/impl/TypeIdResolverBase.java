/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.type.JavaType
 */
package org.codehaus.jackson.map.jsontype.impl;

import org.codehaus.jackson.map.jsontype.TypeIdResolver;
import org.codehaus.jackson.map.type.TypeFactory;
import org.codehaus.jackson.type.JavaType;

public abstract class TypeIdResolverBase
implements TypeIdResolver {
    protected final TypeFactory _typeFactory;
    protected final JavaType _baseType;

    protected TypeIdResolverBase(JavaType baseType, TypeFactory typeFactory) {
        this._baseType = baseType;
        this._typeFactory = typeFactory;
    }

    public void init(JavaType bt) {
    }

    public String idFromBaseType() {
        return this.idFromValueAndType(null, this._baseType.getRawClass());
    }
}

