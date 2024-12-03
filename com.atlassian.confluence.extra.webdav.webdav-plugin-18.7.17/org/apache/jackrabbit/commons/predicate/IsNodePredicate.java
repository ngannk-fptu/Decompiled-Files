/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.commons.predicate;

import javax.jcr.Item;
import javax.jcr.RepositoryException;
import org.apache.jackrabbit.commons.predicate.DepthPredicate;

public class IsNodePredicate
extends DepthPredicate {
    protected final boolean isNode;

    public IsNodePredicate() {
        this(true);
    }

    public IsNodePredicate(boolean polarity, int minDepth, int maxDepth) {
        super(minDepth, maxDepth);
        this.isNode = polarity;
    }

    public IsNodePredicate(boolean polarity) {
        this(polarity, 0, Integer.MAX_VALUE);
    }

    @Override
    protected boolean matches(Item item) throws RepositoryException {
        return item.isNode() == this.isNode;
    }
}

