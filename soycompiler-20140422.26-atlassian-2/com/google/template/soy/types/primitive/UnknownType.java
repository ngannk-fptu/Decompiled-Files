/*
 * Decompiled with CFR 0.152.
 */
package com.google.template.soy.types.primitive;

import com.google.template.soy.data.SoyValue;
import com.google.template.soy.types.SoyType;
import com.google.template.soy.types.primitive.PrimitiveType;

public class UnknownType
extends PrimitiveType {
    private static final UnknownType INSTANCE = new UnknownType();

    private UnknownType() {
    }

    @Override
    public SoyType.Kind getKind() {
        return SoyType.Kind.UNKNOWN;
    }

    @Override
    public boolean isAssignableFrom(SoyType srcType) {
        return true;
    }

    @Override
    public boolean isInstance(SoyValue value) {
        return true;
    }

    public String toString() {
        return "unknown";
    }

    public static UnknownType getInstance() {
        return INSTANCE;
    }
}

