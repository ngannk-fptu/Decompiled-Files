/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.loader.plan.spi;

import org.hibernate.loader.plan.spi.AttributeFetch;
import org.hibernate.loader.plan.spi.EntityReference;
import org.hibernate.type.EntityType;

public interface EntityFetch
extends AttributeFetch,
EntityReference {
    @Override
    public EntityType getFetchedType();
}

