/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.audit.api.AuditCoverageConfigService
 *  com.atlassian.audit.entity.AuditCoverageConfig
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  org.springframework.beans.factory.DisposableBean
 *  org.springframework.beans.factory.InitializingBean
 */
package com.atlassian.audit.coverage;

import com.atlassian.audit.api.AuditCoverageConfigService;
import com.atlassian.audit.coverage.CoverageUpdatedEvent;
import com.atlassian.audit.coverage.SingleValueCache;
import com.atlassian.audit.entity.AuditCoverageConfig;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

public class CachingAuditCoverageService
implements InitializingBean,
DisposableBean {
    private final SingleValueCache<AuditCoverageConfig> configCache;
    private final EventPublisher eventPublisher;

    public CachingAuditCoverageService(EventPublisher eventPublisher, AuditCoverageConfigService delegate, int expirationSeconds) {
        this.eventPublisher = eventPublisher;
        this.configCache = new SingleValueCache<AuditCoverageConfig>(() -> ((AuditCoverageConfigService)delegate).getConfig(), (long)expirationSeconds, TimeUnit.SECONDS);
    }

    public AuditCoverageConfig getConfig() {
        return this.configCache.get();
    }

    @EventListener
    public void onCoverageUpdatedEvent(CoverageUpdatedEvent event) {
        this.configCache.invalidate();
    }

    public void afterPropertiesSet() {
        this.eventPublisher.register((Object)this);
    }

    public void destroy() {
        this.eventPublisher.unregister((Object)this);
    }
}

