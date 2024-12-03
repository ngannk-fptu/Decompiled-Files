/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.query.impl.predicates;

import com.hazelcast.nio.serialization.BinaryInterface;
import com.hazelcast.query.impl.predicates.LikePredicate;

@BinaryInterface
public class ILikePredicate
extends LikePredicate {
    public ILikePredicate() {
    }

    public ILikePredicate(String attribute, String second) {
        super(attribute, second);
    }

    @Override
    public String toString() {
        return this.attributeName + " ILIKE '" + this.expression + "'";
    }

    @Override
    protected int getFlags() {
        return super.getFlags() | 2;
    }

    @Override
    public int getId() {
        return 6;
    }
}

