/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.type.JavaType
 */
package org.codehaus.jackson.map;

import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.type.JavaType;

public abstract class AbstractTypeResolver {
    public JavaType findTypeMapping(DeserializationConfig config, JavaType type) {
        return null;
    }

    public JavaType resolveAbstractType(DeserializationConfig config, JavaType type) {
        return null;
    }
}

