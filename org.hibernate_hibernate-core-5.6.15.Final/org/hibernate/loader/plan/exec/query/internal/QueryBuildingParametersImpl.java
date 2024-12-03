/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.loader.plan.exec.query.internal;

import org.hibernate.LockMode;
import org.hibernate.LockOptions;
import org.hibernate.engine.spi.LoadQueryInfluencers;
import org.hibernate.loader.plan.exec.query.spi.QueryBuildingParameters;

public class QueryBuildingParametersImpl
implements QueryBuildingParameters {
    private final LoadQueryInfluencers loadQueryInfluencers;
    private final int batchSize;
    private final LockMode lockMode;
    private final LockOptions lockOptions;

    public QueryBuildingParametersImpl(LoadQueryInfluencers loadQueryInfluencers, int batchSize, LockMode lockMode, LockOptions lockOptions) {
        this.loadQueryInfluencers = loadQueryInfluencers;
        this.batchSize = batchSize;
        this.lockMode = lockMode;
        this.lockOptions = lockOptions;
    }

    @Override
    public LoadQueryInfluencers getQueryInfluencers() {
        return this.loadQueryInfluencers;
    }

    @Override
    public int getBatchSize() {
        return this.batchSize;
    }

    @Override
    public LockMode getLockMode() {
        return this.lockMode;
    }

    @Override
    public LockOptions getLockOptions() {
        return this.lockOptions;
    }
}

