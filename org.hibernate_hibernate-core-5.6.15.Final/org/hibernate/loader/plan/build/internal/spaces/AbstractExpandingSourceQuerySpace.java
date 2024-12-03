/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.loader.plan.build.internal.spaces;

import org.hibernate.loader.plan.build.internal.spaces.AbstractQuerySpace;
import org.hibernate.loader.plan.build.spi.ExpandingQuerySpace;
import org.hibernate.loader.plan.build.spi.ExpandingQuerySpaces;
import org.hibernate.loader.plan.spi.Join;
import org.hibernate.loader.plan.spi.QuerySpace;

public abstract class AbstractExpandingSourceQuerySpace
extends AbstractQuerySpace
implements ExpandingQuerySpace {
    public AbstractExpandingSourceQuerySpace(String uid, QuerySpace.Disposition disposition, ExpandingQuerySpaces querySpaces, boolean canJoinsBeRequired) {
        super(uid, disposition, querySpaces, canJoinsBeRequired);
    }

    @Override
    public void addJoin(Join join) {
        this.internalGetJoins().add(join);
    }

    @Override
    public ExpandingQuerySpaces getExpandingQuerySpaces() {
        return super.getExpandingQuerySpaces();
    }
}

