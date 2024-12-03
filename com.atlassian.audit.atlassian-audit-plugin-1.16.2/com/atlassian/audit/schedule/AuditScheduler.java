/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.event.events.PluginDisabledEvent
 *  com.atlassian.plugin.event.events.PluginEnabledEvent
 *  javax.annotation.Nonnull
 *  org.springframework.beans.factory.DisposableBean
 *  org.springframework.beans.factory.InitializingBean
 */
package com.atlassian.audit.schedule;

import com.atlassian.audit.plugin.AuditPluginInfo;
import com.atlassian.audit.retention.RetentionScheduler;
import com.atlassian.audit.schedule.db.limit.DbLimiterScheduler;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.event.events.PluginDisabledEvent;
import com.atlassian.plugin.event.events.PluginEnabledEvent;
import java.util.Objects;
import javax.annotation.Nonnull;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

public class AuditScheduler
implements InitializingBean,
DisposableBean {
    private final EventPublisher eventPublisher;
    private final RetentionScheduler retentionScheduler;
    private final DbLimiterScheduler dbLimiterScheduler;
    private final String pluginKey;

    public AuditScheduler(@Nonnull AuditPluginInfo auditPluginInfo, @Nonnull EventPublisher eventPublisher, @Nonnull RetentionScheduler retentionScheduler, @Nonnull DbLimiterScheduler dbLimiterScheduler) {
        this.eventPublisher = Objects.requireNonNull(eventPublisher, "eventPublisher");
        this.retentionScheduler = Objects.requireNonNull(retentionScheduler, "retentionScheduler");
        this.dbLimiterScheduler = Objects.requireNonNull(dbLimiterScheduler, "dbLimiterScheduler");
        this.pluginKey = Objects.requireNonNull(auditPluginInfo, "auditPluginInfo").getPluginKey();
    }

    private void registerJob() {
        this.retentionScheduler.registerJob();
        this.dbLimiterScheduler.registerJob();
    }

    private void unregisterJob() {
        this.retentionScheduler.unregisterJob();
        this.dbLimiterScheduler.unregisterJob();
    }

    @EventListener
    public void onPluginEnabled(PluginEnabledEvent event) {
        if (this.pluginKey.equals(event.getPlugin().getKey())) {
            this.registerJob();
        }
    }

    @EventListener
    public void onPluginDisabled(PluginDisabledEvent event) {
        if (this.pluginKey.equals(event.getPlugin().getKey())) {
            this.unregisterJob();
        }
    }

    public void afterPropertiesSet() throws Exception {
        this.eventPublisher.register((Object)this);
    }

    public void destroy() throws Exception {
        this.eventPublisher.unregister((Object)this);
        this.unregisterJob();
    }
}

