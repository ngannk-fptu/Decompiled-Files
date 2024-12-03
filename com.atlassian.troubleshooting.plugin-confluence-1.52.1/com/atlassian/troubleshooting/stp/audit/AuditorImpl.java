/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.audit.api.AuditService
 *  com.atlassian.audit.entity.AuditAttribute
 *  com.atlassian.audit.entity.AuditEvent
 *  com.atlassian.audit.entity.AuditEvent$Builder
 *  com.atlassian.audit.entity.AuditType
 *  com.atlassian.audit.entity.CoverageArea
 *  com.atlassian.audit.entity.CoverageLevel
 *  javax.annotation.Nonnull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.troubleshooting.stp.audit;

import com.atlassian.audit.api.AuditService;
import com.atlassian.audit.entity.AuditAttribute;
import com.atlassian.audit.entity.AuditEvent;
import com.atlassian.audit.entity.AuditType;
import com.atlassian.audit.entity.CoverageArea;
import com.atlassian.audit.entity.CoverageLevel;
import com.atlassian.troubleshooting.stp.annotations.ConditionalOnClass;
import com.atlassian.troubleshooting.stp.audit.Auditor;
import java.util.Map;
import javax.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ConditionalOnClass(value={AuditService.class})
public class AuditorImpl
implements Auditor {
    private static final Logger LOGGER = LoggerFactory.getLogger(AuditorImpl.class);
    private static final String CATEGORY = "stp.audit.category.system";
    private final AuditService auditService;

    public AuditorImpl(AuditService auditService) {
        this.auditService = auditService;
    }

    private static AuditType auditType(@Nonnull String summaryKey) {
        return AuditType.fromI18nKeys((CoverageArea)CoverageArea.AUDIT_LOG, (CoverageLevel)CoverageLevel.BASE, (String)CATEGORY, (String)summaryKey).build();
    }

    @Override
    public void audit(@Nonnull String summaryKey) {
        try {
            AuditEvent.Builder auditEventBuilder = AuditEvent.builder((AuditType)AuditorImpl.auditType(summaryKey));
            this.auditService.audit(auditEventBuilder.build());
        }
        catch (Throwable t) {
            LOGGER.info("Failed to log audit event '{}'", (Object)summaryKey, (Object)t);
        }
    }

    @Override
    public void audit(@Nonnull String summaryKey, @Nonnull Map<String, String> extraAttributes) {
        try {
            AuditEvent.Builder auditEventBuilder = AuditEvent.builder((AuditType)AuditorImpl.auditType(summaryKey));
            for (Map.Entry<String, String> entry : extraAttributes.entrySet()) {
                auditEventBuilder.extraAttribute(AuditAttribute.fromI18nKeys((String)entry.getKey(), (String)entry.getValue()).build());
            }
            this.auditService.audit(auditEventBuilder.build());
        }
        catch (Throwable t) {
            LOGGER.info("Failed to log audit event '{}'", (Object)summaryKey, (Object)t);
        }
    }
}

