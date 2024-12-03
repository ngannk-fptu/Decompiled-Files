/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.loader.plan.exec.query.spi;

import org.hibernate.LockMode;
import org.hibernate.LockOptions;
import org.hibernate.engine.spi.LoadQueryInfluencers;

public interface QueryBuildingParameters {
    public LoadQueryInfluencers getQueryInfluencers();

    public int getBatchSize();

    public LockMode getLockMode();

    public LockOptions getLockOptions();
}

