/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.collections.bag;

import java.util.Set;
import org.apache.commons.collections.Bag;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.collection.PredicatedCollection;

public class PredicatedBag
extends PredicatedCollection
implements Bag {
    private static final long serialVersionUID = -2575833140344736876L;

    public static Bag decorate(Bag bag, Predicate predicate) {
        return new PredicatedBag(bag, predicate);
    }

    protected PredicatedBag(Bag bag, Predicate predicate) {
        super(bag, predicate);
    }

    protected Bag getBag() {
        return (Bag)this.getCollection();
    }

    public boolean add(Object object, int count) {
        this.validate(object);
        return this.getBag().add(object, count);
    }

    public boolean remove(Object object, int count) {
        return this.getBag().remove(object, count);
    }

    public Set uniqueSet() {
        return this.getBag().uniqueSet();
    }

    public int getCount(Object object) {
        return this.getBag().getCount(object);
    }
}

