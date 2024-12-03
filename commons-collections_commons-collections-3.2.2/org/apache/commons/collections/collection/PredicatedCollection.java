/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.collections.collection;

import java.util.Collection;
import java.util.Iterator;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.collection.AbstractSerializableCollectionDecorator;

public class PredicatedCollection
extends AbstractSerializableCollectionDecorator {
    private static final long serialVersionUID = -5259182142076705162L;
    protected final Predicate predicate;

    public static Collection decorate(Collection coll, Predicate predicate) {
        return new PredicatedCollection(coll, predicate);
    }

    protected PredicatedCollection(Collection coll, Predicate predicate) {
        super(coll);
        if (predicate == null) {
            throw new IllegalArgumentException("Predicate must not be null");
        }
        this.predicate = predicate;
        Iterator it = coll.iterator();
        while (it.hasNext()) {
            this.validate(it.next());
        }
    }

    protected void validate(Object object) {
        if (!this.predicate.evaluate(object)) {
            throw new IllegalArgumentException("Cannot add Object '" + object + "' - Predicate rejected it");
        }
    }

    public boolean add(Object object) {
        this.validate(object);
        return this.getCollection().add(object);
    }

    public boolean addAll(Collection coll) {
        Iterator it = coll.iterator();
        while (it.hasNext()) {
            this.validate(it.next());
        }
        return this.getCollection().addAll(coll);
    }
}

