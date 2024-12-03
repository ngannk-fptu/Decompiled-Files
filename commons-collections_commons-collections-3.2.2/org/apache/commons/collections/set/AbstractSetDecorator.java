/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.collections.set;

import java.util.Set;
import org.apache.commons.collections.collection.AbstractCollectionDecorator;

public abstract class AbstractSetDecorator
extends AbstractCollectionDecorator
implements Set {
    protected AbstractSetDecorator() {
    }

    protected AbstractSetDecorator(Set set) {
        super(set);
    }

    protected Set getSet() {
        return (Set)this.getCollection();
    }
}

