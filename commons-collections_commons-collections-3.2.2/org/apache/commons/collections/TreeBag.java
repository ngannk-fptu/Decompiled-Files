/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.collections;

import java.util.Collection;
import java.util.Comparator;
import java.util.SortedMap;
import java.util.TreeMap;
import org.apache.commons.collections.DefaultMapBag;
import org.apache.commons.collections.SortedBag;

public class TreeBag
extends DefaultMapBag
implements SortedBag {
    public TreeBag() {
        super(new TreeMap());
    }

    public TreeBag(Comparator comparator) {
        super(new TreeMap(comparator));
    }

    public TreeBag(Collection coll) {
        this();
        this.addAll(coll);
    }

    public Object first() {
        return ((SortedMap)this.getMap()).firstKey();
    }

    public Object last() {
        return ((SortedMap)this.getMap()).lastKey();
    }

    public Comparator comparator() {
        return ((SortedMap)this.getMap()).comparator();
    }
}

