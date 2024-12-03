/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  org.springframework.beans.factory.DisposableBean
 *  org.springframework.beans.factory.InitializingBean
 */
package com.atlassian.audit.file;

import com.atlassian.audit.analytics.RetentionFileConfigUpdatedEvent;
import com.atlassian.audit.coverage.SingleValueCache;
import com.atlassian.audit.file.AuditRetentionFileConfig;
import com.atlassian.audit.file.AuditRetentionFileConfigService;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

public class CachingRetentionFileConfigService
implements InitializingBean,
DisposableBean {
    private final SingleValueCache<AuditRetentionFileConfig> configCache;
    private final EventPublisher eventPublisher;

    public CachingRetentionFileConfigService(EventPublisher eventPublisher, AuditRetentionFileConfigService delegate, int expirationSeconds) {
        this.eventPublisher = eventPublisher;
        this.configCache = new SingleValueCache<AuditRetentionFileConfig>(delegate::getConfig, (long)expirationSeconds, TimeUnit.SECONDS);
    }

    public AuditRetentionFileConfig getConfig() {
        return this.configCache.get();
    }

    @EventListener
    public void onRetentionFileConfigUpdatedEvent(RetentionFileConfigUpdatedEvent event) {
        this.configCache.invalidate();
    }

    public void afterPropertiesSet() {
        this.eventPublisher.register((Object)this);
    }

    public void destroy() {
        this.eventPublisher.unregister((Object)this);
    }
}

