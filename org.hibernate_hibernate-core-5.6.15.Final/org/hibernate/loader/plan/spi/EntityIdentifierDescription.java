/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.loader.plan.spi;

public interface EntityIdentifierDescription {
    public boolean hasFetches();

    public boolean hasBidirectionalEntityReferences();
}

