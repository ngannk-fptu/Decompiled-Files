/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.audit.api.AuditService
 *  com.atlassian.audit.entity.AuditEvent
 *  com.atlassian.audit.entity.AuditType
 *  com.atlassian.audit.entity.ChangedValue
 *  com.atlassian.audit.entity.CoverageArea
 *  com.atlassian.audit.entity.CoverageLevel
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventListenerRegistrar
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  javax.annotation.PostConstruct
 *  javax.annotation.PreDestroy
 */
package com.atlassian.zdu.audit;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.audit.api.AuditService;
import com.atlassian.audit.entity.AuditEvent;
import com.atlassian.audit.entity.AuditType;
import com.atlassian.audit.entity.ChangedValue;
import com.atlassian.audit.entity.CoverageArea;
import com.atlassian.audit.entity.CoverageLevel;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventListenerRegistrar;
import com.atlassian.zdu.event.BuildInfo;
import com.atlassian.zdu.event.ZduApprovedEvent;
import com.atlassian.zdu.event.ZduCancelledEvent;
import com.atlassian.zdu.event.ZduCompletedEvent;
import com.atlassian.zdu.event.ZduRetryEvent;
import com.atlassian.zdu.event.ZduStartedEvent;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

public class ZduAuditListener {
    private final EventListenerRegistrar eventListenerRegistrar;
    private final AuditService auditService;

    public ZduAuditListener(@Nonnull EventListenerRegistrar eventListenerRegistrar, @Nonnull AuditService auditService) {
        this.eventListenerRegistrar = Objects.requireNonNull(eventListenerRegistrar);
        this.auditService = Objects.requireNonNull(auditService);
    }

    @PostConstruct
    public void subscribe() {
        this.eventListenerRegistrar.register((Object)this);
    }

    @PreDestroy
    public void unsubscribe() {
        this.eventListenerRegistrar.unregister((Object)this);
    }

    @EventListener
    public void onZduStarted(ZduStartedEvent ev) {
        this.auditService.audit(AuditEvent.builder((AuditType)ZduAuditTypes.ZDU_STARTED).changedValues(this.changedValues(null, ev.getNodeBuild())).build());
    }

    @EventListener
    public void onZduCancelled(ZduCancelledEvent ev) {
        this.auditService.audit(AuditEvent.builder((AuditType)ZduAuditTypes.ZDU_CANCELLED).changedValues(this.changedValues(ev.getNodeBuild(), ev.getNodeBuild())).build());
    }

    @EventListener
    public void onZduCompleted(ZduCompletedEvent ev) {
        this.auditService.audit(AuditEvent.builder((AuditType)ZduAuditTypes.ZDU_COMPLETED).changedValues(this.changedValues(ev.getFromBuild(), ev.getToBuild())).build());
    }

    @EventListener
    public void onZduRetry(ZduRetryEvent ev) {
        this.auditService.audit(AuditEvent.builder((AuditType)ZduAuditTypes.ZDU_RETRY).changedValues(this.changedValues(ev.getNodeBuild(), ev.getNodeBuild())).build());
    }

    @EventListener
    public void onZduApproved(ZduApprovedEvent ev) {
        this.auditService.audit(AuditEvent.builder((AuditType)ZduAuditTypes.ZDU_APPROVED).changedValues(this.changedValues(ev.getFromBuild(), ev.getToBuild())).build());
    }

    private List<ChangedValue> changedValues(@Nullable BuildInfo from, @Nonnull BuildInfo to) {
        return Collections.singletonList(ChangedValue.fromI18nKeys((String)"zdu.audit.value.version").from(from == null ? null : from.getVersion()).to(to.getVersion()).build());
    }

    @VisibleForTesting
    static class ZduAuditTypes {
        static final AuditType ZDU_STARTED = ZduAuditTypes.auditType("zdu.audit.summary.upgrade.started");
        static final AuditType ZDU_CANCELLED = ZduAuditTypes.auditType("zdu.audit.summary.upgrade.cancelled");
        static final AuditType ZDU_COMPLETED = ZduAuditTypes.auditType("zdu.audit.summary.upgrade.completed");
        static final AuditType ZDU_RETRY = ZduAuditTypes.auditType("zdu.audit.summary.upgrade.retry");
        static final AuditType ZDU_APPROVED = ZduAuditTypes.auditType("zdu.audit.summary.upgrade.approved");

        ZduAuditTypes() {
        }

        private static AuditType auditType(@Nonnull String summaryKey) {
            return AuditType.fromI18nKeys((CoverageArea)CoverageArea.GLOBAL_CONFIG_AND_ADMINISTRATION, (CoverageLevel)CoverageLevel.ADVANCED, (String)"zdu.audit.category.system", (String)summaryKey).build();
        }
    }

    @VisibleForTesting
    static class I18nKeys {
        static final String CATEGORY = "zdu.audit.category.system";
        static final String VERSION_VALUE = "zdu.audit.value.version";

        I18nKeys() {
        }
    }
}

