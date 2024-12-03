/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.loader.plan.spi;

import org.hibernate.loader.plan.spi.CollectionReference;
import org.hibernate.loader.plan.spi.FetchSource;

public interface CollectionFetchableElement
extends FetchSource {
    public CollectionReference getCollectionReference();
}

