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
public class BooleanMatcher<T>
extends ElementMatcher.Junction.AbstractBase<T> {
    private static final BooleanMatcher<?> TRUE = new BooleanMatcher(true);
    private static final BooleanMatcher<?> FALSE = new BooleanMatcher(false);
    protected final boolean matches;

    public static <T> ElementMatcher.Junction<T> of(boolean matches) {
        return matches ? TRUE : FALSE;
    }

    public BooleanMatcher(boolean matches) {
        this.matches = matches;
    }

    @Override
    public boolean matches(@MaybeNull T target) {
        return this.matches;
    }

    public String toString() {
        return Boolean.toString(this.matches);
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
        return this.matches == ((BooleanMatcher)object).matches;
    }

    public int hashCode() {
        return this.getClass().hashCode() * 31 + this.matches;
    }
}

