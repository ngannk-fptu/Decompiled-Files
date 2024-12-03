/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Predicate
 *  com.google.common.base.Predicates
 */
package com.atlassian.plugin.webresource.impl.discovery;

import com.atlassian.plugin.webresource.impl.config.Config;
import com.atlassian.plugin.webresource.impl.discovery.Found;
import com.atlassian.plugin.webresource.impl.discovery.PredicateFailStrategy;
import com.atlassian.plugin.webresource.impl.discovery.TraversalOption;
import com.atlassian.plugin.webresource.impl.snapshot.Bundle;
import com.atlassian.plugin.webresource.impl.snapshot.Snapshot;
import com.atlassian.plugin.webresource.impl.support.Support;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class BundleWalker {
    private static final Predicate<Bundle> alwaysFailingDeepFilter = Predicates.alwaysFalse();
    private final Snapshot snapshot;
    private Map<String, Found.State> discovered;
    private List<Found.Item> resolved;
    private LinkedHashSet<String> stack;

    public BundleWalker(Snapshot snapshot) {
        this.snapshot = snapshot;
    }

    public Found find(LinkedHashSet<String> included, Set<String> excluded, Predicate<Bundle> conditionsForExcluded, boolean shouldExpandExcludedKeys, Predicate<Bundle> deep, Predicate<Bundle> deepFilter, PredicateFailStrategy deepFilterFailStrategy, Predicate<Bundle> shallowFilter) {
        Set<String> excludedResolved;
        if (included.isEmpty()) {
            return Found.EMPTY;
        }
        TreeSet<String> reducedExclusions = new TreeSet<String>();
        if (shouldExpandExcludedKeys) {
            Found found = new BundleWalker(this.snapshot).find(new LinkedHashSet<String>(excluded), new LinkedHashSet<String>(), conditionsForExcluded, false, (Predicate<Bundle>)Predicates.alwaysTrue(), conditionsForExcluded, deepFilterFailStrategy, (Predicate<Bundle>)Predicates.alwaysTrue());
            excludedResolved = new LinkedHashSet<String>(found.getFound());
            reducedExclusions.addAll(found.getReducedInclusions());
        } else {
            excludedResolved = excluded;
            reducedExclusions.addAll(excluded);
        }
        this.resolved = new ArrayList<Found.Item>();
        this.stack = new LinkedHashSet();
        HashMap previouslyDiscovered = new HashMap();
        ArrayList<String> items = new ArrayList<String>(included);
        ArrayList<String> reducedInclusions = new ArrayList<String>(included);
        for (int i = 0; i < items.size(); ++i) {
            String key = (String)items.get(i);
            if (Found.State.INCLUDED.equals(previouslyDiscovered.get(key))) {
                reducedInclusions.remove(key);
                continue;
            }
            this.discovered = new HashMap<String, Found.State>();
            this.findItChangesArguments(key, excludedResolved, deep, deepFilter, deepFilterFailStrategy, shallowFilter, EnumSet.noneOf(TraversalOption.class));
            for (int j = 0; j < i; ++j) {
                String previousKey = (String)items.get(j);
                if (!Found.State.INCLUDED.equals((Object)this.discovered.get(previousKey))) continue;
                reducedInclusions.remove(previousKey);
            }
            this.discovered.forEach((item, state) -> {
                if (Found.State.INCLUDED.equals(previouslyDiscovered.get(item))) {
                    return;
                }
                previouslyDiscovered.put(item, state);
            });
        }
        return new Found(this.resolved, reducedInclusions, reducedExclusions);
    }

    private void findItChangesArguments(String key, Set<String> excluded, Predicate<Bundle> deep, Predicate<Bundle> deepFilter, PredicateFailStrategy deepFilterFailStrategy, Predicate<Bundle> shallowFilter, EnumSet<TraversalOption> traversalOptions) {
        if (this.discovered.containsKey(key)) {
            Found.State state = this.discovered.get(key);
            boolean disableBundleContribution = traversalOptions.contains((Object)TraversalOption.RECORD_FAILED);
            if (disableBundleContribution && !Found.State.INCLUDED.equals((Object)state) || !disableBundleContribution && Found.State.INCLUDED.equals((Object)state)) {
                return;
            }
        }
        if (excluded.contains(key)) {
            if (deepFilterFailStrategy == PredicateFailStrategy.CONTINUE) {
                EnumSet<TraversalOption> newOptions = EnumSet.copyOf(traversalOptions);
                newOptions.add(TraversalOption.RECORD_FAILED);
                Bundle bundle = this.getBundle(key);
                if (bundle != null) {
                    this.stack.add(key);
                    this.advanceDeeperIfAllowed(bundle, excluded, deep, alwaysFailingDeepFilter, deepFilterFailStrategy, shallowFilter, newOptions);
                    this.recordDiscoveredItem(key, Found.State.IGNORED);
                    this.stack.remove(key);
                } else if (Config.isContextKey(key)) {
                    this.findItChangesArguments(Config.virtualContextKeyToWebResourceKey(key), excluded, deep, deepFilter, deepFilterFailStrategy, shallowFilter, traversalOptions);
                }
            }
            return;
        }
        if (this.stack.contains(key)) {
            Support.LOGGER.warn("cyclic plugin resource dependency has been detected with: {}, stack trace: {}", (Object)key, this.stack);
            return;
        }
        this.stack.add(key);
        Bundle bundle = this.getBundle(key);
        if (bundle != null) {
            if (deepFilter.apply((Object)bundle)) {
                this.advanceDeeperIfAllowed(bundle, excluded, deep, deepFilter, deepFilterFailStrategy, shallowFilter, traversalOptions);
                if (shallowFilter.apply((Object)bundle)) {
                    this.recordDiscoveredItem(key, Found.State.INCLUDED);
                } else {
                    this.recordDiscoveredItem(key, Found.State.SKIPPED);
                }
            } else {
                if (deepFilterFailStrategy == PredicateFailStrategy.CONTINUE) {
                    EnumSet<TraversalOption> newOptions = EnumSet.copyOf(traversalOptions);
                    newOptions.add(TraversalOption.RECORD_FAILED);
                    this.advanceDeeperIfAllowed(bundle, excluded, deep, alwaysFailingDeepFilter, deepFilterFailStrategy, shallowFilter, newOptions);
                }
                Found.State state = traversalOptions.contains((Object)TraversalOption.RECORD_FAILED) ? Found.State.IGNORED : Found.State.SKIPPED;
                this.recordDiscoveredItem(key, state);
            }
        } else if (Config.isContextKey(key)) {
            this.findItChangesArguments(Config.virtualContextKeyToWebResourceKey(key), excluded, deep, deepFilter, deepFilterFailStrategy, shallowFilter, traversalOptions);
        } else if (Support.LOGGER.isDebugEnabled()) {
            Support.LOGGER.debug("Attempted to resolve bundle for {}, but it was null. stack trace: {}", (Object)key, this.stack);
        }
        this.stack.remove(key);
    }

    private void recordDiscoveredItem(String item, Found.State state) {
        if (Found.State.INCLUDED.equals((Object)this.discovered.get(item))) {
            return;
        }
        this.discovered.put(item, state);
        this.resolved.add(new Found.Item(item, state));
    }

    private void advanceDeeperIfAllowed(Bundle bundle, Set<String> excluded, Predicate<Bundle> deep, Predicate<Bundle> deepFilter, PredicateFailStrategy deepFilterFailStrategy, Predicate<Bundle> shallowFilter, EnumSet<TraversalOption> traversalOptions) {
        if (deep.apply((Object)bundle)) {
            List<String> bundleDependencies = bundle.getDependencies();
            for (String dependencyKey : bundleDependencies) {
                this.findItChangesArguments(dependencyKey, excluded, deep, deepFilter, deepFilterFailStrategy, shallowFilter, traversalOptions);
            }
        }
    }

    protected Bundle getBundle(String key) {
        return this.snapshot.get(key);
    }
}

