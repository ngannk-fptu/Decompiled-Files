/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jetty.util;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;

public class IncludeExcludeSet<T, P>
implements Predicate<P> {
    private final Set<T> _includes;
    private final Predicate<P> _includePredicate;
    private final Set<T> _excludes;
    private final Predicate<P> _excludePredicate;

    public IncludeExcludeSet() {
        this(HashSet.class);
    }

    public <SET extends Set<T>> IncludeExcludeSet(Class<SET> setClass) {
        try {
            this._includes = (Set)setClass.getDeclaredConstructor(new Class[0]).newInstance(new Object[0]);
            this._excludes = (Set)setClass.getDeclaredConstructor(new Class[0]).newInstance(new Object[0]);
            this._includePredicate = this._includes instanceof Predicate ? (Predicate<Object>)((Object)this._includes) : new SetContainsPredicate<P>(this._includes);
            this._excludePredicate = this._excludes instanceof Predicate ? (Predicate<Object>)((Object)this._excludes) : new SetContainsPredicate<P>(this._excludes);
        }
        catch (RuntimeException e) {
            throw e;
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public <SET extends Set<T>> IncludeExcludeSet(Set<T> includeSet, Predicate<P> includePredicate, Set<T> excludeSet, Predicate<P> excludePredicate) {
        Objects.requireNonNull(includeSet, "Include Set");
        Objects.requireNonNull(includePredicate, "Include Predicate");
        Objects.requireNonNull(excludeSet, "Exclude Set");
        Objects.requireNonNull(excludePredicate, "Exclude Predicate");
        this._includes = includeSet;
        this._includePredicate = includePredicate;
        this._excludes = excludeSet;
        this._excludePredicate = excludePredicate;
    }

    public void include(T element) {
        this._includes.add(element);
    }

    public void include(T ... element) {
        this._includes.addAll(Arrays.asList(element));
    }

    public void exclude(T element) {
        this._excludes.add(element);
    }

    public void exclude(T ... element) {
        this._excludes.addAll(Arrays.asList(element));
    }

    @Override
    public boolean test(P t) {
        if (!this._includes.isEmpty() && !this._includePredicate.test(t)) {
            return false;
        }
        if (this._excludes.isEmpty()) {
            return true;
        }
        return !this._excludePredicate.test(t);
    }

    public Boolean isIncludedAndNotExcluded(P item) {
        if (!this._excludes.isEmpty() && this._excludePredicate.test(item)) {
            return Boolean.FALSE;
        }
        if (!this._includes.isEmpty() && this._includePredicate.test(item)) {
            return Boolean.TRUE;
        }
        return null;
    }

    public boolean hasIncludes() {
        return !this._includes.isEmpty();
    }

    public boolean hasExcludes() {
        return !this._excludes.isEmpty();
    }

    public int size() {
        return this._includes.size() + this._excludes.size();
    }

    public Set<T> getIncluded() {
        return this._includes;
    }

    public Set<T> getExcluded() {
        return this._excludes;
    }

    public void clear() {
        this._includes.clear();
        this._excludes.clear();
    }

    public String toString() {
        return String.format("%s@%x{i=%s,ip=%s,e=%s,ep=%s}", this.getClass().getSimpleName(), this.hashCode(), this._includes, this._includePredicate == this._includes ? "SELF" : this._includePredicate, this._excludes, this._excludePredicate == this._excludes ? "SELF" : this._excludePredicate);
    }

    public boolean isEmpty() {
        return this._includes.isEmpty() && this._excludes.isEmpty();
    }

    private static class SetContainsPredicate<T>
    implements Predicate<T> {
        private final Set<T> set;

        public SetContainsPredicate(Set<T> set) {
            this.set = set;
        }

        @Override
        public boolean test(T item) {
            return this.set.contains(item);
        }

        public String toString() {
            return "CONTAINS";
        }
    }
}

