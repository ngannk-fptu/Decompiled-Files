/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.commons.predicate;

import javax.jcr.Item;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import org.apache.jackrabbit.commons.predicate.DepthPredicate;

public class NodeTypePredicate
extends DepthPredicate {
    protected final String nodeType;
    protected final boolean respectSupertype;

    public NodeTypePredicate(String nodeType, boolean respectSupertype, int minDepth, int maxDepth) {
        super(minDepth, maxDepth);
        this.nodeType = nodeType;
        this.respectSupertype = respectSupertype;
    }

    public NodeTypePredicate(String nodeType, boolean respectSupertype) {
        this(nodeType, respectSupertype, 0, Integer.MAX_VALUE);
    }

    @Override
    protected boolean matches(Item item) throws RepositoryException {
        if (item.isNode()) {
            if (this.respectSupertype) {
                try {
                    return ((Node)item).isNodeType(this.nodeType);
                }
                catch (RepositoryException e) {
                    return false;
                }
            }
            return ((Node)item).getPrimaryNodeType().getName().equals(this.nodeType);
        }
        return false;
    }
}

