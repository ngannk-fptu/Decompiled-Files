/*
 * Decompiled with CFR 0.152.
 */
package com.google.template.soy.types.primitive;

import com.google.template.soy.data.SanitizedContent;
import com.google.template.soy.data.SoyValue;
import com.google.template.soy.data.restricted.StringData;
import com.google.template.soy.types.SoyType;
import com.google.template.soy.types.primitive.PrimitiveType;
import com.google.template.soy.types.primitive.SanitizedType;

public final class StringType
extends PrimitiveType {
    private static final StringType INSTANCE = new StringType();

    private StringType() {
    }

    @Override
    public SoyType.Kind getKind() {
        return SoyType.Kind.STRING;
    }

    public String toString() {
        return "string";
    }

    public static StringType getInstance() {
        return INSTANCE;
    }

    @Override
    public boolean isAssignableFrom(SoyType srcType) {
        return srcType.getKind() == SoyType.Kind.STRING || srcType instanceof SanitizedType;
    }

    @Override
    public boolean isInstance(SoyValue value) {
        return value instanceof StringData || value instanceof SanitizedContent;
    }
}

