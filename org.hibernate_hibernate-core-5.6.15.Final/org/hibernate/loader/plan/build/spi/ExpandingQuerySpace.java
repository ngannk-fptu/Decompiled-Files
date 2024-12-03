/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.loader.plan.build.spi;

import org.hibernate.loader.plan.build.spi.ExpandingQuerySpaces;
import org.hibernate.loader.plan.spi.Join;
import org.hibernate.loader.plan.spi.QuerySpace;

public interface ExpandingQuerySpace
extends QuerySpace {
    public boolean canJoinsBeRequired();

    public void addJoin(Join var1);

    public ExpandingQuerySpaces getExpandingQuerySpaces();
}

