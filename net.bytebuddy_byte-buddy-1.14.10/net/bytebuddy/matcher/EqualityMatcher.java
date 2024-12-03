/*
 * Decompiled with CFR 0.152.
 */
package net.bytebuddy.matcher;

import net.bytebuddy.build.HashCodeAndEqualsPlugin;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.utility.nullability.MaybeNull;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
@HashCodeAndEqualsPlugin.Enhance
public class EqualityMatcher<T>
extends ElementMatcher.Junction.AbstractBase<T> {
    private final Object value;

    public EqualityMatcher(Object value) {
        this.value = value;
    }

    @Override
    public boolean matches(@MaybeNull T target) {
        return this.value.equals(target);
    }

    public String toString() {
        return "is(" + this.value + ")";
    }

    public boolean equals(@MaybeNull Object object) {
        if (this == object) {
            return true;
        }
        if (object == null) {
            return false;
        }
        if (this.getClass() != object.getClass()) {
            return false;
        }
        return this.value.equals(((EqualityMatcher)object).value);
    }

    public int hashCode() {
        return this.getClass().hashCode() * 31 + this.value.hashCode();
    }
}

