/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.jackson.map.deser;

import org.codehaus.jackson.map.JsonDeserializer;
import org.codehaus.jackson.map.TypeDeserializer;
import org.codehaus.jackson.map.deser.std.ObjectArrayDeserializer;
import org.codehaus.jackson.map.type.ArrayType;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
@Deprecated
public class ArrayDeserializer
extends ObjectArrayDeserializer {
    @Deprecated
    public ArrayDeserializer(ArrayType arrayType, JsonDeserializer<Object> elemDeser) {
        this(arrayType, elemDeser, null);
    }

    public ArrayDeserializer(ArrayType arrayType, JsonDeserializer<Object> elemDeser, TypeDeserializer elemTypeDeser) {
        super(arrayType, elemDeser, elemTypeDeser);
    }
}

