/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.loader.plan.spi;

import java.util.List;
import org.hibernate.loader.plan.spi.QuerySpaces;
import org.hibernate.loader.plan.spi.Return;

public interface LoadPlan {
    public Disposition getDisposition();

    public List<? extends Return> getReturns();

    public QuerySpaces getQuerySpaces();

    public boolean areLazyAttributesForceFetched();

    public boolean hasAnyScalarReturns();

    public static enum Disposition {
        ENTITY_LOADER,
        COLLECTION_INITIALIZER,
        MIXED;

    }
}

