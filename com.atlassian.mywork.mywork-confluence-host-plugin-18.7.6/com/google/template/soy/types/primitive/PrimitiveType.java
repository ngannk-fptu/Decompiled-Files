/*
 * Decompiled with CFR 0.152.
 */
package com.google.template.soy.types.primitive;

import com.google.template.soy.types.SoyType;

public abstract class PrimitiveType
implements SoyType {
    @Override
    public boolean isAssignableFrom(SoyType srcType) {
        return srcType.getKind() == this.getKind();
    }

    public boolean equals(Object other) {
        return other.getClass() == this.getClass();
    }

    public int hashCode() {
        return this.getClass().hashCode();
    }
}

