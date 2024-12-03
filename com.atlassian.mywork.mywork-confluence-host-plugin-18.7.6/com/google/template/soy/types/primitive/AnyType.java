/*
 * Decompiled with CFR 0.152.
 */
package com.google.template.soy.types.primitive;

import com.google.template.soy.data.SoyValue;
import com.google.template.soy.types.SoyType;
import com.google.template.soy.types.primitive.PrimitiveType;

public final class AnyType
extends PrimitiveType {
    private static final AnyType INSTANCE = new AnyType();

    private AnyType() {
    }

    @Override
    public SoyType.Kind getKind() {
        return SoyType.Kind.ANY;
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
        return "any";
    }

    public static AnyType getInstance() {
        return INSTANCE;
    }
}

