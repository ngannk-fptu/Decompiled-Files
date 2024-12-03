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
public class ClassLoaderHierarchyMatcher<T extends ClassLoader>
extends ElementMatcher.Junction.AbstractBase<T> {
    private final ElementMatcher<? super ClassLoader> matcher;

    public ClassLoaderHierarchyMatcher(ElementMatcher<? super ClassLoader> matcher) {
        this.matcher = matcher;
    }

    @Override
    public boolean matches(@MaybeNull T target) {
        for (Object current = target; current != null; current = ((ClassLoader)current).getParent()) {
            if (!this.matcher.matches((ClassLoader)current)) continue;
            return true;
        }
        return this.matcher.matches(null);
    }

    public String toString() {
        return "hasChild(" + this.matcher + ')';
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
        return this.matcher.equals(((ClassLoaderHierarchyMatcher)object).matcher);
    }

    public int hashCode() {
        return this.getClass().hashCode() * 31 + this.matcher.hashCode();
    }
}

