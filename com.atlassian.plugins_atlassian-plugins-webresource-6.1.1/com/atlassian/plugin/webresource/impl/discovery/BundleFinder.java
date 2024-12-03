/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Predicate
 *  com.google.common.base.Predicates
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.plugin.webresource.impl.discovery;

import com.atlassian.plugin.webresource.impl.discovery.BundleWalker;
import com.atlassian.plugin.webresource.impl.discovery.Found;
import com.atlassian.plugin.webresource.impl.discovery.PredicateFailStrategy;
import com.atlassian.plugin.webresource.impl.snapshot.Bundle;
import com.atlassian.plugin.webresource.impl.snapshot.Snapshot;
import com.atlassian.plugin.webresource.impl.support.Support;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BundleFinder {
    private final Snapshot snapshot;
    private final LinkedHashSet<String> included;
    private final List<Predicate<Bundle>> deepFilters;
    private PredicateFailStrategy deepFilterFailStrategy = PredicateFailStrategy.STOP;
    private final Set<String> excluded;
    private final List<Predicate<Bundle>> shallowFilters;
    private Predicate<Bundle> conditionsForExcluded;
    private boolean deep = true;
    private boolean resolveExcluded = true;

    public BundleFinder(Snapshot snapshot) {
        this.deepFilters = new ArrayList<Predicate<Bundle>>();
        this.excluded = new HashSet<String>();
        this.included = new LinkedHashSet();
        this.snapshot = snapshot;
        this.shallowFilters = new ArrayList<Predicate<Bundle>>();
    }

    public BundleFinder included(Collection<String> keys) {
        this.included.clear();
        this.included.addAll(keys);
        return this;
    }

    public BundleFinder included(String key) {
        this.included.clear();
        this.included.add(key);
        return this;
    }

    public BundleFinder excluded(@Nonnull Collection<String> keys, @Nullable Predicate<Bundle> conditionsForExcluded) {
        this.excluded.clear();
        this.excluded.addAll(keys);
        this.conditionsForExcluded = Optional.ofNullable(conditionsForExcluded).orElseGet(Predicates::alwaysTrue);
        this.resolveExcluded = true;
        return this;
    }

    public BundleFinder excludedResolved(Collection<String> keys) {
        this.excluded.clear();
        this.excluded.addAll(keys);
        this.conditionsForExcluded = Predicates.alwaysTrue();
        this.resolveExcluded = false;
        return this;
    }

    public BundleFinder deep(boolean deep) {
        this.deep = deep;
        return this;
    }

    public BundleFinder deepFilter(Predicate<Bundle> filter) {
        this.deepFilters.add(filter);
        return this;
    }

    public BundleFinder onDeepFilterFail(PredicateFailStrategy failStrategy) {
        this.deepFilterFailStrategy = failStrategy;
        return this;
    }

    public BundleFinder shallowFilter(Predicate<Bundle> filter) {
        this.shallowFilters.add(filter);
        return this;
    }

    public Found endAndGetResult() {
        Predicate deepPredicate = this.deep ? Predicates.alwaysTrue() : Predicates.alwaysFalse();
        return new BundleWalker(this.snapshot).find(this.included, this.excluded, this.conditionsForExcluded, this.resolveExcluded, (Predicate<Bundle>)deepPredicate, Support.efficientAndPredicate(this.deepFilters), this.deepFilterFailStrategy, Support.efficientAndPredicate(this.shallowFilters));
    }

    public List<String> end() {
        return this.endAndGetResult().getFound();
    }
}

