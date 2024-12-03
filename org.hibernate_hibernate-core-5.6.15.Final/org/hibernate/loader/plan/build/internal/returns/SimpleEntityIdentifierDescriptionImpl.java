/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.loader.plan.build.internal.returns;

import org.hibernate.loader.plan.spi.EntityIdentifierDescription;

public class SimpleEntityIdentifierDescriptionImpl
implements EntityIdentifierDescription {
    @Override
    public boolean hasFetches() {
        return false;
    }

    @Override
    public boolean hasBidirectionalEntityReferences() {
        return false;
    }
}

