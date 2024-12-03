/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package net.bytebuddy.matcher;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.Iterator;
import java.util.concurrent.ConcurrentMap;
import net.bytebuddy.build.HashCodeAndEqualsPlugin;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.utility.nullability.MaybeNull;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
@HashCodeAndEqualsPlugin.Enhance(permitSubclassEquality=true)
public class CachingMatcher<T>
extends ElementMatcher.Junction.AbstractBase<T> {
    private static final Object NULL_VALUE = new Object();
    private final ElementMatcher<? super T> matcher;
    @HashCodeAndEqualsPlugin.ValueHandling(value=HashCodeAndEqualsPlugin.ValueHandling.Sort.IGNORE)
    protected final ConcurrentMap<? super T, Boolean> map;

    public CachingMatcher(ElementMatcher<? super T> matcher, ConcurrentMap<? super T, Boolean> map) {
        this.matcher = matcher;
        this.map = map;
    }

    @Override
    public boolean matches(@MaybeNull T target) {
        Boolean cached = (Boolean)this.map.get(target == null ? NULL_VALUE : target);
        if (cached == null) {
            cached = this.onCacheMiss(target);
        }
        return cached;
    }

    protected boolean onCacheMiss(@MaybeNull T target) {
        boolean cached = this.matcher.matches(target);
        this.map.put(target == null ? NULL_VALUE : target, cached);
        return cached;
    }

    public String toString() {
        return "cached(" + this.matcher + ")";
    }

    public boolean equals(@MaybeNull Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof CachingMatcher)) {
            return false;
        }
        return this.matcher.equals(((CachingMatcher)object).matcher);
    }

    public int hashCode() {
        return CachingMatcher.class.hashCode() * 31 + this.matcher.hashCode();
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    @SuppressFBWarnings(value={"EQ_DOESNT_OVERRIDE_EQUALS"}, justification="Equality does not consider eviction size.")
    public static class WithInlineEviction<S>
    extends CachingMatcher<S> {
        private final int evictionSize;

        public WithInlineEviction(ElementMatcher<? super S> matcher, ConcurrentMap<? super S, Boolean> map, int evictionSize) {
            super(matcher, map);
            this.evictionSize = evictionSize;
        }

        @Override
        protected boolean onCacheMiss(@MaybeNull S target) {
            if (this.map.size() >= this.evictionSize) {
                Iterator iterator = this.map.entrySet().iterator();
                iterator.next();
                iterator.remove();
            }
            return super.onCacheMiss(target);
        }
    }
}

