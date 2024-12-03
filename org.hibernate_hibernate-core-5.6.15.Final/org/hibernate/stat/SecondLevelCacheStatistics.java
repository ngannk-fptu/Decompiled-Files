/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.stat;

import java.util.Collections;
import java.util.Map;
import org.hibernate.stat.CacheRegionStatistics;

@Deprecated
public interface SecondLevelCacheStatistics
extends CacheRegionStatistics {
    default public Map getEntries() {
        return Collections.emptyMap();
    }
}

