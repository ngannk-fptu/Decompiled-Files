/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.collections.set;

import java.util.Comparator;
import java.util.Set;
import java.util.SortedSet;
import org.apache.commons.collections.set.AbstractSetDecorator;

public abstract class AbstractSortedSetDecorator
extends AbstractSetDecorator
implements SortedSet {
    protected AbstractSortedSetDecorator() {
    }

    protected AbstractSortedSetDecorator(Set set) {
        super(set);
    }

    protected SortedSet getSortedSet() {
        return (SortedSet)this.getCollection();
    }

    public SortedSet subSet(Object fromElement, Object toElement) {
        return this.getSortedSet().subSet(fromElement, toElement);
    }

    public SortedSet headSet(Object toElement) {
        return this.getSortedSet().headSet(toElement);
    }

    public SortedSet tailSet(Object fromElement) {
        return this.getSortedSet().tailSet(fromElement);
    }

    public Object first() {
        return this.getSortedSet().first();
    }

    public Object last() {
        return this.getSortedSet().last();
    }

    public Comparator comparator() {
        return this.getSortedSet().comparator();
    }
}

