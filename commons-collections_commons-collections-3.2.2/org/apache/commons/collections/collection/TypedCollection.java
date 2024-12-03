/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.collections.collection;

import java.util.Collection;
import org.apache.commons.collections.collection.PredicatedCollection;
import org.apache.commons.collections.functors.InstanceofPredicate;

public class TypedCollection {
    public static Collection decorate(Collection coll, Class type) {
        return new PredicatedCollection(coll, InstanceofPredicate.getInstance(type));
    }

    protected TypedCollection() {
    }
}

