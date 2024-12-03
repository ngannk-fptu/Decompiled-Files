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
public class NullMatcher<T>
extends ElementMatcher.Junction.AbstractBase<T> {
    private static final NullMatcher<?> INSTANCE = new NullMatcher();

    public static <T> ElementMatcher.Junction<T> make() {
        return INSTANCE;
    }

    @Override
    public boolean matches(@MaybeNull T target) {
        return target == null;
    }

    public String toString() {
        return "isNull()";
    }

    public boolean equals(@MaybeNull Object object) {
        if (this == object) {
            return true;
        }
        if (object == null) {
            return false;
        }
        return this.getClass() == object.getClass();
    }

    public int hashCode() {
        return this.getClass().hashCode();
    }
}

