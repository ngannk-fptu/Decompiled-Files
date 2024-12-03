/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.collections.bag;

import java.util.Set;
import org.apache.commons.collections.Bag;
import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.collection.TransformedCollection;
import org.apache.commons.collections.set.TransformedSet;

public class TransformedBag
extends TransformedCollection
implements Bag {
    private static final long serialVersionUID = 5421170911299074185L;

    public static Bag decorate(Bag bag, Transformer transformer) {
        return new TransformedBag(bag, transformer);
    }

    protected TransformedBag(Bag bag, Transformer transformer) {
        super(bag, transformer);
    }

    protected Bag getBag() {
        return (Bag)this.collection;
    }

    public int getCount(Object object) {
        return this.getBag().getCount(object);
    }

    public boolean remove(Object object, int nCopies) {
        return this.getBag().remove(object, nCopies);
    }

    public boolean add(Object object, int nCopies) {
        object = this.transform(object);
        return this.getBag().add(object, nCopies);
    }

    public Set uniqueSet() {
        Set set = this.getBag().uniqueSet();
        return TransformedSet.decorate(set, this.transformer);
    }
}

