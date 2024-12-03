/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableSet
 *  javax.annotation.Nonnull
 */
package com.atlassian.plugin.webresource.impl.discovery;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;

public class Found {
    public static final Found EMPTY = new Found(Collections.emptyList(), Collections.emptyList(), Collections.emptySet());
    private final List<Item> items;
    private final List<String> reducedInclusions;
    private final Set<String> reducedExclusions;

    @Deprecated
    public Found(List<Item> items) {
        this(items, Collections.emptyList(), Collections.emptySet());
    }

    public Found(List<Item> items, List<String> reducedInclusions, Set<String> reducedExclusions) {
        this.items = items;
        this.reducedInclusions = ImmutableList.copyOf(reducedInclusions);
        this.reducedExclusions = ImmutableSet.copyOf(reducedExclusions);
    }

    public List<String> getFound() {
        return this.items.stream().filter(item -> State.INCLUDED.equals((Object)((Item)item).getState())).map(Item::getKey).distinct().collect(Collectors.toList());
    }

    public List<String> getSkipped() {
        return this.items.stream().filter(item -> State.SKIPPED.equals((Object)((Item)item).getState())).map(Item::getKey).distinct().collect(Collectors.toList());
    }

    public List<String> getAll() {
        return this.items.stream().map(Item::getKey).distinct().collect(Collectors.toList());
    }

    public List<String> getReducedInclusions() {
        return this.reducedInclusions;
    }

    public Set<String> getReducedExclusions() {
        return this.reducedExclusions;
    }

    public static class Item {
        private final String key;
        private final State state;

        Item(@Nonnull String key, @Nonnull State state) {
            Objects.requireNonNull(key);
            this.key = key;
            this.state = state;
        }

        public String getKey() {
            return this.key;
        }

        private State getState() {
            return this.state;
        }
    }

    static enum State {
        INCLUDED,
        SKIPPED,
        IGNORED;

    }
}

