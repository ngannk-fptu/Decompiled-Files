/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.commons.predicate;

import javax.jcr.Item;
import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import org.apache.jackrabbit.commons.predicate.DepthPredicate;

public class DeclaringTypePredicate
extends DepthPredicate {
    protected final String nodeType;
    protected final boolean propsOnly;

    public DeclaringTypePredicate(String nodeType, boolean propsOnly, int minDepth, int maxDepth) {
        super(minDepth, maxDepth);
        this.nodeType = nodeType;
        this.propsOnly = propsOnly;
    }

    public DeclaringTypePredicate(String nodeType, boolean propsOnly) {
        this(nodeType, propsOnly, 0, Integer.MAX_VALUE);
    }

    @Override
    protected boolean matches(Item item) throws RepositoryException {
        if (item.isNode()) {
            return !this.propsOnly && ((Node)item).getDefinition().getDeclaringNodeType().getName().equals(this.nodeType);
        }
        return ((Property)item).getDefinition().getDeclaringNodeType().getName().equals(this.nodeType);
    }
}

