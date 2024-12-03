/*
 * Decompiled with CFR 0.152.
 */
package org.jaxen.expr;

import java.io.Serializable;
import java.util.List;
import org.jaxen.expr.Predicate;
import org.jaxen.expr.PredicateSet;

public interface Predicated
extends Serializable {
    public void addPredicate(Predicate var1);

    public List getPredicates();

    public PredicateSet getPredicateSet();
}

