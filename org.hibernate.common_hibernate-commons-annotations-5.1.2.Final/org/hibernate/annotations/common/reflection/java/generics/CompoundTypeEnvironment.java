/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.annotations.common.reflection.java.generics;

import java.lang.reflect.Type;
import org.hibernate.annotations.common.reflection.java.generics.IdentityTypeEnvironment;
import org.hibernate.annotations.common.reflection.java.generics.TypeEnvironment;

public final class CompoundTypeEnvironment
implements TypeEnvironment {
    private final TypeEnvironment f;
    private final TypeEnvironment g;
    private final int hashCode;

    public static TypeEnvironment create(TypeEnvironment f, TypeEnvironment g) {
        if (g == IdentityTypeEnvironment.INSTANCE) {
            return f;
        }
        if (f == IdentityTypeEnvironment.INSTANCE) {
            return g;
        }
        return new CompoundTypeEnvironment(f, g);
    }

    private CompoundTypeEnvironment(TypeEnvironment f, TypeEnvironment g) {
        this.f = f;
        this.g = g;
        this.hashCode = CompoundTypeEnvironment.doHashCode(f, g);
    }

    @Override
    public Type bind(Type type) {
        return this.f.bind(this.g.bind(type));
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CompoundTypeEnvironment)) {
            return false;
        }
        CompoundTypeEnvironment that = (CompoundTypeEnvironment)o;
        if (this.differentHashCode(that)) {
            return false;
        }
        if (!this.f.equals(that.f)) {
            return false;
        }
        return this.g.equals(that.g);
    }

    private boolean differentHashCode(CompoundTypeEnvironment that) {
        return this.hashCode != that.hashCode;
    }

    private static int doHashCode(TypeEnvironment f, TypeEnvironment g) {
        int result = f.hashCode();
        result = 29 * result + g.hashCode();
        return result;
    }

    public int hashCode() {
        return this.hashCode;
    }

    public String toString() {
        return this.f.toString() + "(" + this.g.toString() + ")";
    }
}

