/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.cache;

import org.hibernate.cache.CacheException;

public class NoCacheRegionFactoryAvailableException
extends CacheException {
    private static final String MSG = String.format("Second-level cache is used in the application, but property %s is not given; please either disable second level cache or set correct region factory using the %s setting and make sure the second level cache provider (hibernate-infinispan, e.g.) is available on the classpath.", "hibernate.cache.region.factory_class", "hibernate.cache.region.factory_class");

    public NoCacheRegionFactoryAvailableException() {
        super(MSG);
    }
}

