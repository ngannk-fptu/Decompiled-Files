/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.loader.plan.spi;

import org.hibernate.loader.PropertyPath;
import org.hibernate.loader.plan.spi.BidirectionalEntityReference;
import org.hibernate.loader.plan.spi.EntityReference;
import org.hibernate.loader.plan.spi.Fetch;

public interface FetchSource {
    public PropertyPath getPropertyPath();

    public String getQuerySpaceUid();

    public Fetch[] getFetches();

    public BidirectionalEntityReference[] getBidirectionalEntityReferences();

    public EntityReference resolveEntityReference();
}

