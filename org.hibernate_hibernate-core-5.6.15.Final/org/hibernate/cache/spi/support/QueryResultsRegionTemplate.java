/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.cache.spi.support;

import org.hibernate.cache.spi.QueryResultsRegion;
import org.hibernate.cache.spi.RegionFactory;
import org.hibernate.cache.spi.support.DirectAccessRegionTemplate;
import org.hibernate.cache.spi.support.StorageAccess;

public class QueryResultsRegionTemplate
extends DirectAccessRegionTemplate
implements QueryResultsRegion {
    public QueryResultsRegionTemplate(String name, RegionFactory regionFactory, StorageAccess storageAccess) {
        super(name, regionFactory, storageAccess);
    }
}

