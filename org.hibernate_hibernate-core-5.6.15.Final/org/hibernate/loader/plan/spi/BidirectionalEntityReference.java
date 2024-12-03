/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.loader.plan.spi;

import org.hibernate.loader.plan.spi.EntityReference;

public interface BidirectionalEntityReference
extends EntityReference {
    public EntityReference getTargetEntityReference();

    @Override
    public String getQuerySpaceUid();
}

