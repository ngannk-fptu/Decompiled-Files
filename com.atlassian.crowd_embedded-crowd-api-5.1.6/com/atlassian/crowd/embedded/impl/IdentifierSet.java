/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ForwardingSet
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Sets
 */
package com.atlassian.crowd.embedded.impl;

import com.atlassian.crowd.embedded.impl.IdentifierUtils;
import com.google.common.collect.ForwardingSet;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class IdentifierSet
extends ForwardingSet<String> {
    public static final IdentifierSet EMPTY = new IdentifierSet(ImmutableSet::of);
    private final Set<String> delegate;

    private IdentifierSet(Supplier<Set<String>> supplier) {
        this.delegate = supplier.get();
    }

    public IdentifierSet() {
        this.delegate = Sets.newHashSet();
    }

    public IdentifierSet(int expectedSize) {
        this.delegate = Sets.newHashSetWithExpectedSize((int)expectedSize);
    }

    public IdentifierSet(Collection<String> names) {
        this(names.size());
        this.addAll(names);
    }

    protected Set<String> delegate() {
        return this.delegate;
    }

    public boolean removeAll(Collection<?> collection) {
        return this.delegate().removeAll(IdentifierSet.lowercase(collection));
    }

    public boolean contains(Object object) {
        return this.delegate().contains(IdentifierSet.lowercase(object));
    }

    public boolean add(String element) {
        return this.delegate().add(IdentifierUtils.toLowerCase(element));
    }

    public boolean remove(Object object) {
        return this.delegate().remove(IdentifierSet.lowercase(object));
    }

    public boolean containsAll(Collection<?> collection) {
        return this.delegate().containsAll(IdentifierSet.lowercase(collection));
    }

    public boolean addAll(Collection<? extends String> strings) {
        return this.delegate().addAll(IdentifierSet.lowercaseStrings(strings));
    }

    public boolean retainAll(Collection<?> collection) {
        return this.delegate().retainAll(IdentifierSet.lowercase(collection));
    }

    public static IdentifierSet difference(Collection<String> set1, Collection<String> set2) {
        IdentifierSet result = new IdentifierSet(set1);
        result.removeAll(set2);
        return result;
    }

    public static Set<String> differenceWithOriginalCasing(Collection<String> set1, Collection<String> set2) {
        return set1.stream().filter(IdentifierUtils.containsIdentifierPredicate(set2).negate()).collect(Collectors.toSet());
    }

    public static IdentifierSet intersection(Collection<String> set1, Collection<String> set2) {
        boolean firstSmaller = set1.size() < set2.size();
        IdentifierSet smaller = new IdentifierSet(firstSmaller ? set1 : set2);
        smaller.retainAll(firstSmaller ? set2 : set1);
        return smaller;
    }

    private static Object lowercase(Object element) {
        return element instanceof String ? IdentifierUtils.toLowerCase((String)element) : element;
    }

    private static Collection<?> lowercase(Collection<?> collection) {
        if (collection instanceof IdentifierSet) {
            return ((IdentifierSet)((Object)collection)).delegate();
        }
        return collection.stream().map(IdentifierSet::lowercase).collect(Collectors.toSet());
    }

    private static Collection<? extends String> lowercaseStrings(Collection<? extends String> collection) {
        return collection instanceof IdentifierSet ? ((IdentifierSet)((Object)collection)).delegate() : IdentifierUtils.toLowerCase(collection);
    }
}

