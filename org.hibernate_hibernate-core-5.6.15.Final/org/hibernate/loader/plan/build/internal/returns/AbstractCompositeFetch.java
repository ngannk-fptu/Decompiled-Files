/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.loader.plan.build.internal.returns;

import org.hibernate.engine.FetchStrategy;
import org.hibernate.engine.FetchStyle;
import org.hibernate.engine.FetchTiming;
import org.hibernate.loader.PropertyPath;
import org.hibernate.loader.plan.build.internal.returns.AbstractCompositeReference;
import org.hibernate.loader.plan.build.spi.ExpandingCompositeQuerySpace;
import org.hibernate.loader.plan.spi.CompositeFetch;

public abstract class AbstractCompositeFetch
extends AbstractCompositeReference
implements CompositeFetch {
    protected static final FetchStrategy FETCH_STRATEGY = new FetchStrategy(FetchTiming.IMMEDIATE, FetchStyle.JOIN);

    protected AbstractCompositeFetch(ExpandingCompositeQuerySpace compositeQuerySpace, boolean allowCollectionFetches, PropertyPath propertyPath) {
        super(compositeQuerySpace, allowCollectionFetches, propertyPath);
    }

    @Override
    public FetchStrategy getFetchStrategy() {
        return FETCH_STRATEGY;
    }

    @Override
    public String getAdditionalJoinConditions() {
        return null;
    }

    @Override
    public String[] toSqlSelectFragments(String alias) {
        return new String[0];
    }
}

