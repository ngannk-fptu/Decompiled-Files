/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.commons.predicate;

import javax.jcr.Item;
import javax.jcr.RepositoryException;
import org.apache.jackrabbit.commons.predicate.DepthPredicate;

public class NamePredicate
extends DepthPredicate {
    protected final String name;

    public NamePredicate(String name, int minDepth, int maxDepth) {
        super(minDepth, maxDepth);
        this.name = name;
    }

    public NamePredicate(String name) {
        this(name, 0, Integer.MAX_VALUE);
    }

    @Override
    protected boolean matches(Item item) throws RepositoryException {
        return item.getName().equals(this.name);
    }
}

