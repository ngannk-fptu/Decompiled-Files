/*
 * Decompiled with CFR 0.152.
 */
package com.google.template.soy.types.primitive;

import com.google.template.soy.data.SoyValue;
import com.google.template.soy.data.restricted.NullData;
import com.google.template.soy.data.restricted.UndefinedData;
import com.google.template.soy.types.SoyType;
import com.google.template.soy.types.primitive.PrimitiveType;

public final class NullType
extends PrimitiveType {
    private static final NullType INSTANCE = new NullType();

    private NullType() {
    }

    @Override
    public SoyType.Kind getKind() {
        return SoyType.Kind.NULL;
    }

    @Override
    public boolean isInstance(SoyValue value) {
        return value instanceof NullData || value instanceof UndefinedData;
    }

    public String toString() {
        return "null";
    }

    public static NullType getInstance() {
        return INSTANCE;
    }
}

