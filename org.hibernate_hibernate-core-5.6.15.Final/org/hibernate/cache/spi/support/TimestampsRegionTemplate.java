/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.cache.spi.support;

import org.hibernate.cache.spi.RegionFactory;
import org.hibernate.cache.spi.TimestampsRegion;
import org.hibernate.cache.spi.support.DirectAccessRegionTemplate;
import org.hibernate.cache.spi.support.StorageAccess;

public class TimestampsRegionTemplate
extends DirectAccessRegionTemplate
implements TimestampsRegion {
    public TimestampsRegionTemplate(String name, RegionFactory regionFactory, StorageAccess storageAccess) {
        super(name, regionFactory, storageAccess);
    }
}

