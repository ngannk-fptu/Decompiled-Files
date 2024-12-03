/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.adaptavist.confluence.naturalchildren;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.annotation.Nonnull;

public abstract class AncestorList
implements Iterable<Long> {
    protected Set<Long> ancestors = new LinkedHashSet<Long>();

    protected void addAncestor(long id) {
        this.ancestors.add(id);
    }

    @Override
    @Nonnull
    public Iterator<Long> iterator() {
        return this.ancestors.iterator();
    }
}

