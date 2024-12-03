/*
 * Decompiled with CFR 0.152.
 */
package com.google.template.soy.types.primitive;

import com.google.template.soy.data.SoyValue;
import com.google.template.soy.types.SoyType;

public final class ErrorType
implements SoyType {
    private final String name;

    public ErrorType(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    @Override
    public SoyType.Kind getKind() {
        return SoyType.Kind.ERROR;
    }

    @Override
    public boolean isAssignableFrom(SoyType srcType) {
        return false;
    }

    @Override
    public boolean isInstance(SoyValue value) {
        return false;
    }

    public String toString() {
        return this.name;
    }

    public boolean equals(Object other) {
        return other != null && other.getClass() == ErrorType.class && ((ErrorType)other).name.equals(this.name);
    }
}

