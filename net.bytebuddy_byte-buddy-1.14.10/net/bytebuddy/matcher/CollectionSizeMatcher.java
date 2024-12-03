/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package net.bytebuddy.matcher;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.Collection;
import net.bytebuddy.build.HashCodeAndEqualsPlugin;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.utility.nullability.MaybeNull;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
@HashCodeAndEqualsPlugin.Enhance
public class CollectionSizeMatcher<T extends Iterable<?>>
extends ElementMatcher.Junction.ForNonNullValues<T> {
    private final int size;

    public CollectionSizeMatcher(int size) {
        this.size = size;
    }

    @Override
    @SuppressFBWarnings(value={"DLS_DEAD_LOCAL_STORE"}, justification="Iteration required to count size of an iterable.")
    protected boolean doMatch(T target) {
        if (target instanceof Collection) {
            return ((Collection)target).size() == this.size;
        }
        int size = 0;
        for (Object ignored : target) {
            ++size;
        }
        return size == this.size;
    }

    public String toString() {
        return "ofSize(" + this.size + ')';
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
        return this.size == ((CollectionSizeMatcher)object).size;
    }

    @Override
    public int hashCode() {
        return super.hashCode() * 31 + this.size;
    }
}

