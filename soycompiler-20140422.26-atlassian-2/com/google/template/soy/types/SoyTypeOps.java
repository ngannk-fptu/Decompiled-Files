/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.inject.Inject
 */
package com.google.template.soy.types;

import com.google.inject.Inject;
import com.google.template.soy.types.SoyType;
import com.google.template.soy.types.SoyTypeRegistry;
import com.google.template.soy.types.primitive.FloatType;
import java.util.Collection;

public final class SoyTypeOps {
    private final SoyTypeRegistry typeRegistry;

    @Inject
    public SoyTypeOps(SoyTypeRegistry typeRegistry) {
        this.typeRegistry = typeRegistry;
    }

    public SoyTypeRegistry getTypeRegistry() {
        return this.typeRegistry;
    }

    public SoyType computeLeastCommonType(SoyType t0, SoyType t1) {
        if (t0.isAssignableFrom(t1)) {
            return t0;
        }
        if (t1.isAssignableFrom(t0)) {
            return t1;
        }
        return this.typeRegistry.getOrCreateUnionType(t0, t1);
    }

    public SoyType computeLeastCommonType(Collection<SoyType> types) {
        SoyType result = null;
        for (SoyType type : types) {
            result = result == null ? type : this.computeLeastCommonType(result, type);
        }
        return result;
    }

    public SoyType computeLeastCommonTypeArithmetic(SoyType t0, SoyType t1) {
        if (t0.isAssignableFrom(t1)) {
            return t0;
        }
        if (t1.isAssignableFrom(t0)) {
            return t1;
        }
        if (t0.getKind() == SoyType.Kind.FLOAT && t1.getKind() == SoyType.Kind.INT || t1.getKind() == SoyType.Kind.FLOAT && t0.getKind() == SoyType.Kind.INT) {
            return FloatType.getInstance();
        }
        return this.typeRegistry.getOrCreateUnionType(t0, t1);
    }
}

