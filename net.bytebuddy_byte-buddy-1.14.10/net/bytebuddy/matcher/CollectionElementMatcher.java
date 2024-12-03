/*
 * Decompiled with CFR 0.152.
 */
package net.bytebuddy.matcher;

import java.util.Iterator;
import net.bytebuddy.build.HashCodeAndEqualsPlugin;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.utility.nullability.MaybeNull;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
@HashCodeAndEqualsPlugin.Enhance
public class CollectionElementMatcher<T>
extends ElementMatcher.Junction.ForNonNullValues<Iterable<? extends T>> {
    private final int index;
    private final ElementMatcher<? super T> matcher;

    public CollectionElementMatcher(int index, ElementMatcher<? super T> matcher) {
        this.index = index;
        this.matcher = matcher;
    }

    @Override
    protected boolean doMatch(Iterable<? extends T> target) {
        Iterator<T> iterator = target.iterator();
        for (int index = 0; index < this.index; ++index) {
            if (!iterator.hasNext()) {
                return false;
            }
            iterator.next();
        }
        return iterator.hasNext() && this.matcher.matches(iterator.next());
    }

    public String toString() {
        return "with(" + this.index + " matches " + this.matcher + ")";
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
        if (this.index != ((CollectionElementMatcher)object).index) {
            return false;
        }
        return this.matcher.equals(((CollectionElementMatcher)object).matcher);
    }

    @Override
    public int hashCode() {
        return (super.hashCode() * 31 + this.index) * 31 + this.matcher.hashCode();
    }
}

