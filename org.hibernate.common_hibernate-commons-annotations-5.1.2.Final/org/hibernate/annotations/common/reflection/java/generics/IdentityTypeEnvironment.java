/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.annotations.common.reflection.java.generics;

import java.lang.reflect.Type;
import org.hibernate.annotations.common.reflection.java.generics.TypeEnvironment;

public final class IdentityTypeEnvironment
implements TypeEnvironment {
    public static final TypeEnvironment INSTANCE = new IdentityTypeEnvironment();

    private IdentityTypeEnvironment() {
    }

    @Override
    public Type bind(Type type) {
        return type;
    }

    public String toString() {
        return "{}";
    }
}

