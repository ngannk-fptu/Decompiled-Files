/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.audit.api.AuditService
 *  com.atlassian.audit.entity.AuditAttribute
 *  com.atlassian.audit.entity.AuditEvent
 *  com.atlassian.audit.entity.AuditEvent$Builder
 *  com.atlassian.audit.entity.CoverageArea
 *  com.atlassian.audit.entity.CoverageLevel
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.sal.api.features.DarkFeatureManager
 *  com.google.common.annotations.VisibleForTesting
 */
package com.atlassian.confluence.impl.audit.listener;

import com.atlassian.audit.api.AuditService;
import com.atlassian.audit.entity.AuditAttribute;
import com.atlassian.audit.entity.AuditEvent;
import com.atlassian.audit.entity.CoverageArea;
import com.atlassian.audit.entity.CoverageLevel;
import com.atlassian.confluence.audit.AuditingContext;
import com.atlassian.confluence.audit.StandardAuditResourceTypes;
import com.atlassian.confluence.event.events.admin.ReIndexJobFinishedEvent;
import com.atlassian.confluence.impl.audit.AuditCategories;
import com.atlassian.confluence.impl.audit.AuditHelper;
import com.atlassian.confluence.impl.audit.handler.AuditHandlerService;
import com.atlassian.confluence.impl.audit.listener.AbstractAuditListener;
import com.atlassian.confluence.index.status.ReIndexJob;
import com.atlassian.confluence.index.status.ReIndexNodeStatus;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.event.api.EventListener;
import com.atlassian.sal.api.features.DarkFeatureManager;
import com.google.common.annotations.VisibleForTesting;
import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ReindexAuditListener
extends AbstractAuditListener {
    private static final String REINDEX_AUDIT_DARK_FEATURE = "confluence.reindex.audit";
    private final DarkFeatureManager darkFeatureManager;
    private final SpaceManager spaceManager;

    public ReindexAuditListener(AuditHandlerService auditHandlerService, AuditService service, AuditHelper auditHelper, StandardAuditResourceTypes resourceTypes, AuditingContext auditingContext, DarkFeatureManager darkFeatureManager, SpaceManager spaceManager) {
        super(auditHandlerService, service, auditHelper, resourceTypes, auditingContext);
        this.darkFeatureManager = Objects.requireNonNull(darkFeatureManager);
        this.spaceManager = Objects.requireNonNull(spaceManager);
    }

    @EventListener
    public void onReindexJobFinishedEvent(ReIndexJobFinishedEvent reindexJobFinishedEvent) {
        if (this.darkFeatureManager.isEnabledForAllUsers(REINDEX_AUDIT_DARK_FEATURE).orElse(Boolean.FALSE).booleanValue()) {
            ReIndexJob reIndexJob = reindexJobFinishedEvent.getReIndexJob();
            Optional.ofNullable(reIndexJob.getCreatedBy()).ifPresent(AuthenticatedUserThreadLocal::set);
            String summaryTextKey = reIndexJob.isSiteReindex() ? "reindex.site.job.completed" : "reindex.spaces.job.completed";
            AuditEvent.Builder auditEventBuilder = AuditEvent.fromI18nKeys((String)AuditCategories.REINDEX, (String)AuditHelper.buildSummaryTextKey(summaryTextKey), (CoverageLevel)CoverageLevel.BASE, (CoverageArea)CoverageArea.GLOBAL_CONFIG_AND_ADMINISTRATION);
            Optional.ofNullable(reIndexJob.getId()).ifPresent(reindexId -> auditEventBuilder.affectedObject(this.buildResourceWithoutId((String)reindexId, "Index")));
            if (!reIndexJob.isSiteReindex()) {
                reIndexJob.getSpaceKeys().forEach(spaceKey -> Optional.ofNullable(this.spaceManager.getSpace((String)spaceKey)).ifPresent(space -> auditEventBuilder.affectedObject(this.buildResource(space.getName(), this.resourceTypes.space(), space.getId()))));
            }
            this.addExtraAttributeIfPresent(auditEventBuilder, "reindex.status", reIndexJob.getStage(), Enum::name);
            this.addExtraAttributeIfPresent(auditEventBuilder, "reindex.start.time", reIndexJob.getStartTime(), Instant::toString);
            this.addExtraAttributeIfPresent(auditEventBuilder, "reindex.complete.time", reIndexJob.getFinishTime(), Instant::toString);
            this.addExtraAttributeIfPresent(auditEventBuilder, "reindex.duration", reIndexJob.getDuration(), Duration::toString);
            this.addExtraAttributeIfPresent(auditEventBuilder, "reindex.node.status", reIndexJob.getNodeStatuses(), this::generateNodeStatusInfo);
            this.save(() -> ((AuditEvent.Builder)auditEventBuilder).build());
        }
    }

    @VisibleForTesting
    String generateNodeStatusInfo(Collection<ReIndexNodeStatus> nodeStatus) {
        return nodeStatus.stream().map(nodeState -> {
            String status = String.format("%s -> %s", nodeState.getNodeId(), nodeState.getState().name());
            if (nodeState.getError() != null) {
                return status + ", " + nodeState.getError().name();
            }
            return status;
        }).collect(Collectors.joining("; "));
    }

    private <T> void addExtraAttributeIfPresent(AuditEvent.Builder auditEventBuilder, String extraAttrKey, T extraValue, Function<T, String> valueTransformer) {
        Optional.ofNullable(extraValue).ifPresent(value -> auditEventBuilder.extraAttribute(AuditAttribute.fromI18nKeys((String)AuditHelper.buildExtraAttribute(extraAttrKey), (String)((String)valueTransformer.apply(extraValue))).build()));
    }
}

