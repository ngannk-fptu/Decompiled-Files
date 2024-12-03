/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.loader.plan.build.internal;

import java.util.Collections;
import java.util.List;
import org.hibernate.loader.plan.spi.CollectionReturn;
import org.hibernate.loader.plan.spi.EntityReturn;
import org.hibernate.loader.plan.spi.LoadPlan;
import org.hibernate.loader.plan.spi.QuerySpaces;
import org.hibernate.loader.plan.spi.Return;

public class LoadPlanImpl
implements LoadPlan {
    private final List<? extends Return> returns;
    private final QuerySpaces querySpaces;
    private final LoadPlan.Disposition disposition;
    private final boolean areLazyAttributesForceFetched;

    protected LoadPlanImpl(List<? extends Return> returns, QuerySpaces querySpaces, LoadPlan.Disposition disposition, boolean areLazyAttributesForceFetched) {
        this.returns = returns;
        this.querySpaces = querySpaces;
        this.disposition = disposition;
        this.areLazyAttributesForceFetched = areLazyAttributesForceFetched;
    }

    public LoadPlanImpl(EntityReturn rootReturn, QuerySpaces querySpaces) {
        this(Collections.singletonList(rootReturn), querySpaces, LoadPlan.Disposition.ENTITY_LOADER, false);
    }

    public LoadPlanImpl(CollectionReturn rootReturn, QuerySpaces querySpaces) {
        this(Collections.singletonList(rootReturn), querySpaces, LoadPlan.Disposition.COLLECTION_INITIALIZER, false);
    }

    public LoadPlanImpl(List<? extends Return> returns, QuerySpaces querySpaces, boolean areLazyAttributesForceFetched) {
        this(returns, querySpaces, LoadPlan.Disposition.MIXED, areLazyAttributesForceFetched);
    }

    @Override
    public List<? extends Return> getReturns() {
        return this.returns;
    }

    @Override
    public QuerySpaces getQuerySpaces() {
        return this.querySpaces;
    }

    @Override
    public LoadPlan.Disposition getDisposition() {
        return this.disposition;
    }

    @Override
    public boolean areLazyAttributesForceFetched() {
        return this.areLazyAttributesForceFetched;
    }

    @Override
    public boolean hasAnyScalarReturns() {
        return this.disposition == LoadPlan.Disposition.MIXED;
    }
}

