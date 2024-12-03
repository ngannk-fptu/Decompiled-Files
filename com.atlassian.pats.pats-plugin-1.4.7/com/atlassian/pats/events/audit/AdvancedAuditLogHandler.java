/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.audit.entity.AuditAttribute
 *  com.atlassian.audit.entity.AuditEvent
 *  com.atlassian.audit.entity.AuditResource
 *  com.atlassian.audit.entity.AuditType
 *  com.atlassian.audit.entity.CoverageArea
 *  com.atlassian.audit.entity.CoverageLevel
 */
package com.atlassian.pats.events.audit;

import com.atlassian.audit.entity.AuditAttribute;
import com.atlassian.audit.entity.AuditEvent;
import com.atlassian.audit.entity.AuditResource;
import com.atlassian.audit.entity.AuditType;
import com.atlassian.audit.entity.CoverageArea;
import com.atlassian.audit.entity.CoverageLevel;
import com.atlassian.pats.events.audit.AuditLogHandler;

public abstract class AdvancedAuditLogHandler
implements AuditLogHandler {
    protected AuditResource auditResource(String username, String objectType, String userKey) {
        return AuditResource.builder((String)username, (String)objectType).id(userKey).build();
    }

    protected AuditEvent auditEvent(String actionI18nKey, AuditResource auditResource, String tokenName) {
        return AuditEvent.builder((AuditType)AuditType.fromI18nKeys((CoverageArea)CoverageArea.SECURITY, (CoverageLevel)CoverageLevel.BASE, (String)"personal.access.tokens.audit.log.category", (String)actionI18nKey).build()).affectedObject(auditResource).extraAttribute(AuditAttribute.fromI18nKeys((String)"personal.access.tokens.audit.log.extra.attribute.name", (String)tokenName).build()).build();
    }
}

