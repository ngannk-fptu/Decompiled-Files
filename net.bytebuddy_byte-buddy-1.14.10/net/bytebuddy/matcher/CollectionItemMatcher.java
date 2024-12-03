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
public class CollectionItemMatcher<T>
extends ElementMatcher.Junction.ForNonNullValues<Iterable<? extends T>> {
    private final ElementMatcher<? super T> matcher;

    public CollectionItemMatcher(ElementMatcher<? super T> matcher) {
        this.matcher = matcher;
    }

    @Override
    protected boolean doMatch(Iterable<? extends T> target) {
        for (T value : target) {
            if (!this.matcher.matches(value)) continue;
            return true;
        }
        return false;
    }

    public String toString() {
        return "whereOne(" + this.matcher + ")";
    }

    @Override
    public boolean equals(@MaybeNull Object object) {
        if (!super.equals(object)) {
            return false;
        }
        if (this == object) {
            return true;
        }
        if (object == null) {
            return false;
        }
        if (this.getClass() != object.getClass()) {
            return false;
        }
        return this.matcher.equals(((CollectionItemMatcher)object).matcher);
    }

    @Override
    public int hashCode() {
        return super.hashCode() * 31 + this.matcher.hashCode();
    }
}

