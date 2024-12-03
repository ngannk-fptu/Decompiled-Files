/*
 * Decompiled with CFR 0.152.
 */
package com.google.template.soy.types.primitive;

import com.google.template.soy.data.SoyValue;
import com.google.template.soy.data.restricted.IntegerData;
import com.google.template.soy.types.SoyType;
import com.google.template.soy.types.primitive.PrimitiveType;

public final class IntType
extends PrimitiveType {
    private static final IntType INSTANCE = new IntType();

    private IntType() {
    }

    @Override
    public SoyType.Kind getKind() {
        return SoyType.Kind.INT;
    }

    @Override
    public boolean isInstance(SoyValue value) {
        return value instanceof IntegerData;
    }

    public String toString() {
        return "int";
    }

    public static IntType getInstance() {
        return INSTANCE;
    }
}

