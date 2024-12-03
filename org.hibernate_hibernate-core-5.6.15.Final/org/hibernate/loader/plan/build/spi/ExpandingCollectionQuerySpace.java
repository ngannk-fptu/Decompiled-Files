/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.loader.plan.build.spi;

import org.hibernate.loader.plan.build.spi.ExpandingQuerySpace;
import org.hibernate.loader.plan.spi.CollectionQuerySpace;
import org.hibernate.loader.plan.spi.Join;

public interface ExpandingCollectionQuerySpace
extends CollectionQuerySpace,
ExpandingQuerySpace {
    @Override
    public void addJoin(Join var1);
}

