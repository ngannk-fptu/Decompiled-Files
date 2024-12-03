/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.cache.spi;

import java.util.Map;
import org.hibernate.boot.spi.SessionFactoryOptions;
import org.hibernate.cache.CacheException;
import org.hibernate.cache.cfg.spi.DomainDataRegionBuildingContext;
import org.hibernate.cache.cfg.spi.DomainDataRegionConfig;
import org.hibernate.cache.spi.CacheTransactionSynchronization;
import org.hibernate.cache.spi.DomainDataRegion;
import org.hibernate.cache.spi.QueryResultsRegion;
import org.hibernate.cache.spi.StandardCacheTransactionSynchronization;
import org.hibernate.cache.spi.TimestampsRegion;
import org.hibernate.cache.spi.access.AccessType;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.service.Service;
import org.hibernate.service.spi.Stoppable;

public interface RegionFactory
extends Service,
Stoppable {
    public static final String DEFAULT_QUERY_RESULTS_REGION_UNQUALIFIED_NAME = "default-query-results-region";
    public static final String DEFAULT_UPDATE_TIMESTAMPS_REGION_UNQUALIFIED_NAME = "default-update-timestamps-region";

    public void start(SessionFactoryOptions var1, Map var2) throws CacheException;

    public boolean isMinimalPutsEnabledByDefault();

    public AccessType getDefaultAccessType();

    public String qualify(String var1);

    default public CacheTransactionSynchronization createTransactionContext(SharedSessionContractImplementor session) {
        return new StandardCacheTransactionSynchronization(this);
    }

    public long nextTimestamp();

    default public long getTimeout() {
        return 60000L;
    }

    public DomainDataRegion buildDomainDataRegion(DomainDataRegionConfig var1, DomainDataRegionBuildingContext var2);

    public QueryResultsRegion buildQueryResultsRegion(String var1, SessionFactoryImplementor var2);

    public TimestampsRegion buildTimestampsRegion(String var1, SessionFactoryImplementor var2);
}

