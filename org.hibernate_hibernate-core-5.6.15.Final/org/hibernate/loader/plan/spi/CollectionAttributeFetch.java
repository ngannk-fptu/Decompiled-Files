/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.loader.plan.spi;

import org.hibernate.loader.plan.spi.AttributeFetch;
import org.hibernate.loader.plan.spi.CollectionReference;
import org.hibernate.type.CollectionType;

public interface CollectionAttributeFetch
extends AttributeFetch,
CollectionReference {
    @Override
    public CollectionType getFetchedType();
}

