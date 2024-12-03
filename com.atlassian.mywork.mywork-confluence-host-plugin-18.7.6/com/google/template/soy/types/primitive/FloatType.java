/*
 * Decompiled with CFR 0.152.
 */
package com.google.template.soy.types.primitive;

import com.google.template.soy.data.SoyValue;
import com.google.template.soy.data.restricted.FloatData;
import com.google.template.soy.types.SoyType;
import com.google.template.soy.types.primitive.PrimitiveType;

public final class FloatType
extends PrimitiveType {
    private static final FloatType INSTANCE = new FloatType();

    private FloatType() {
    }

    @Override
    public SoyType.Kind getKind() {
        return SoyType.Kind.FLOAT;
    }

    @Override
    public boolean isInstance(SoyValue value) {
        return value instanceof FloatData;
    }

    public String toString() {
        return "float";
    }

    public static FloatType getInstance() {
        return INSTANCE;
    }
}

