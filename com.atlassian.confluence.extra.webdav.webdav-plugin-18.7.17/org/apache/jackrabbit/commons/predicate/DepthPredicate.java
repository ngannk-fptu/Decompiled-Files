/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.commons.predicate;

import javax.jcr.Item;
import javax.jcr.RepositoryException;
import org.apache.jackrabbit.commons.predicate.Predicate;

public class DepthPredicate
implements Predicate {
    protected final int minDepth;
    protected final int maxDepth;

    public DepthPredicate(int minDepth, int maxDepth) {
        this.minDepth = minDepth;
        this.maxDepth = maxDepth;
    }

    @Override
    public boolean evaluate(Object item) {
        if (item instanceof Item) {
            try {
                int depth = ((Item)item).getDepth();
                return depth >= this.minDepth && depth <= this.maxDepth && this.matches((Item)item);
            }
            catch (RepositoryException re) {
                return false;
            }
        }
        return false;
    }

    protected boolean matches(Item item) throws RepositoryException {
        return true;
    }
}

