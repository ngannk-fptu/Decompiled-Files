/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.commons.predicate;

import javax.jcr.Item;
import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import org.apache.jackrabbit.commons.predicate.DepthPredicate;

public class IsMandatoryPredicate
extends DepthPredicate {
    protected final boolean isMandatory;

    public IsMandatoryPredicate() {
        this(true);
    }

    public IsMandatoryPredicate(boolean isMandatory, int minDepth, int maxDepth) {
        super(minDepth, maxDepth);
        this.isMandatory = isMandatory;
    }

    public IsMandatoryPredicate(boolean isMandatory) {
        this(isMandatory, 0, Integer.MAX_VALUE);
    }

    @Override
    protected boolean matches(Item item) throws RepositoryException {
        if (item.isNode()) {
            return ((Node)item).getDefinition().isMandatory() == this.isMandatory;
        }
        return ((Property)item).getDefinition().isMandatory() == this.isMandatory;
    }
}

