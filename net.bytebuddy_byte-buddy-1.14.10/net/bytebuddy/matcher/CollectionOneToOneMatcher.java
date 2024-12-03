/*
 * Decompiled with CFR 0.152.
 */
package net.bytebuddy.matcher;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import net.bytebuddy.build.HashCodeAndEqualsPlugin;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.utility.nullability.MaybeNull;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
@HashCodeAndEqualsPlugin.Enhance
public class CollectionOneToOneMatcher<T>
extends ElementMatcher.Junction.ForNonNullValues<Iterable<? extends T>> {
    private final List<? extends ElementMatcher<? super T>> matchers;

    public CollectionOneToOneMatcher(List<? extends ElementMatcher<? super T>> matchers) {
        this.matchers = matchers;
    }

    @Override
    protected boolean doMatch(Iterable<? extends T> target) {
        if (target instanceof Collection && ((Collection)target).size() != this.matchers.size()) {
            return false;
        }
        Iterator<ElementMatcher<T>> iterator = this.matchers.iterator();
        for (T value : target) {
            if (iterator.hasNext() && iterator.next().matches(value)) continue;
            return false;
        }
        return true;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder("containing(");
        boolean first = true;
        for (ElementMatcher<? super T> elementMatcher : this.matchers) {
            if (first) {
                first = false;
            } else {
                stringBuilder.append(", ");
            }
            stringBuilder.append(elementMatcher);
        }
        return stringBuilder.append(')').toString();
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
        return ((Object)this.matchers).equals(((CollectionOneToOneMatcher)object).matchers);
    }

    @Override
    public int hashCode() {
        return super.hashCode() * 31 + ((Object)this.matchers).hashCode();
    }
}

