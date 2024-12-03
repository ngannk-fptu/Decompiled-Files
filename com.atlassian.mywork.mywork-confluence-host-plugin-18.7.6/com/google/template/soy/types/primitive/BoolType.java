/*
 * Decompiled with CFR 0.152.
 */
package com.google.template.soy.types.primitive;

import com.google.template.soy.data.SoyValue;
import com.google.template.soy.data.restricted.BooleanData;
import com.google.template.soy.types.SoyType;
import com.google.template.soy.types.primitive.PrimitiveType;

public final class BoolType
extends PrimitiveType {
    private static final BoolType INSTANCE = new BoolType();

    private BoolType() {
    }

    @Override
    public SoyType.Kind getKind() {
        return SoyType.Kind.BOOL;
    }

    @Override
    public boolean isInstance(SoyValue value) {
        return value instanceof BooleanData;
    }

    public String toString() {
        return "bool";
    }

    public static BoolType getInstance() {
        return INSTANCE;
    }
}

