/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.loader.plan.build.spi;

import org.hibernate.loader.plan.build.spi.ExpandingCompositeQuerySpace;
import org.hibernate.loader.plan.build.spi.ExpandingQuerySpace;
import org.hibernate.loader.plan.spi.EntityQuerySpace;

public interface ExpandingEntityQuerySpace
extends EntityQuerySpace,
ExpandingQuerySpace {
    public ExpandingCompositeQuerySpace makeCompositeIdentifierQuerySpace();
}

