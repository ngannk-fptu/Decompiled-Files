/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.collections.functors;

import org.apache.commons.collections.Predicate;

public interface PredicateDecorator
extends Predicate {
    public Predicate[] getPredicates();
}

