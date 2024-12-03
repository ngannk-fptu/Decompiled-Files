/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.collections.bag;

import java.util.Set;
import org.apache.commons.collections.Bag;
import org.apache.commons.collections.collection.AbstractCollectionDecorator;

public abstract class AbstractBagDecorator
extends AbstractCollectionDecorator
implements Bag {
    protected AbstractBagDecorator() {
    }

    protected AbstractBagDecorator(Bag bag) {
        super(bag);
    }

    protected Bag getBag() {
        return (Bag)this.getCollection();
    }

    public int getCount(Object object) {
        return this.getBag().getCount(object);
    }

    public boolean add(Object object, int count) {
        return this.getBag().add(object, count);
    }

    public boolean remove(Object object, int count) {
        return this.getBag().remove(object, count);
    }

    public Set uniqueSet() {
        return this.getBag().uniqueSet();
    }
}

