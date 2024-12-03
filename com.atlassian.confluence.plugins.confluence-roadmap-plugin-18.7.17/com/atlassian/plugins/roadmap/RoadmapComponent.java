/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.DisposableBean
 *  org.springframework.beans.factory.InitializingBean
 */
package com.atlassian.plugins.roadmap;

import com.atlassian.plugins.roadmap.RoadmapMacroCacheSupplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

public class RoadmapComponent
implements InitializingBean,
DisposableBean {
    private final Logger logger = LoggerFactory.getLogger(RoadmapComponent.class);
    private final RoadmapMacroCacheSupplier cacheSupplier;

    public RoadmapComponent(RoadmapMacroCacheSupplier cacheSupplier) {
        this.cacheSupplier = cacheSupplier;
    }

    public void afterPropertiesSet() throws Exception {
        this.flushCaches();
    }

    public void destroy() throws Exception {
        this.flushCaches();
    }

    private void flushCaches() {
        this.logger.info("Clearing caches [RoadmapMacroImages,RoadmapMacroSources]");
        this.cacheSupplier.getMarcoSourceCache().removeAll();
        this.cacheSupplier.getImageCache().removeAll();
    }
}

