/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.audit.api.AuditService
 *  com.atlassian.audit.entity.AuditAttribute
 *  com.atlassian.audit.entity.AuditEvent
 *  com.atlassian.audit.entity.CoverageArea
 *  com.atlassian.audit.entity.CoverageLevel
 *  com.atlassian.event.api.EventListener
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.impl.audit.listener;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.audit.api.AuditService;
import com.atlassian.audit.entity.AuditAttribute;
import com.atlassian.audit.entity.AuditEvent;
import com.atlassian.audit.entity.CoverageArea;
import com.atlassian.audit.entity.CoverageLevel;
import com.atlassian.confluence.audit.AuditingContext;
import com.atlassian.confluence.audit.StandardAuditResourceTypes;
import com.atlassian.confluence.event.events.user.SendUserInviteEvent;
import com.atlassian.confluence.impl.audit.AuditCategories;
import com.atlassian.confluence.impl.audit.AuditHelper;
import com.atlassian.confluence.impl.audit.handler.AuditHandlerService;
import com.atlassian.confluence.impl.audit.listener.AbstractAuditListener;
import com.atlassian.event.api.EventListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;

public class SendUserInviteAuditListener
extends AbstractAuditListener {
    private static final String USER_INVITE_SUMMARY = AuditHelper.buildSummaryTextKey("user.invite");
    private static final int MAX_CHAR_LIMIT = 100;

    public SendUserInviteAuditListener(AuditHandlerService auditHandlerService, AuditService service, AuditHelper auditHelper, StandardAuditResourceTypes resourceTypes, AuditingContext auditingContext) {
        super(auditHandlerService, service, auditHelper, resourceTypes, auditingContext);
    }

    @EventListener
    public void sendUserInviteEvent(SendUserInviteEvent event) {
        this.save(() -> AuditEvent.fromI18nKeys((String)AuditCategories.USER_MANAGEMENT, (String)USER_INVITE_SUMMARY, (CoverageLevel)CoverageLevel.ADVANCED, (CoverageArea)CoverageArea.USER_MANAGEMENT).extraAttribute(AuditAttribute.fromI18nKeys((String)AuditHelper.buildExtraAttribute("users"), (String)this.getRecipientsAsString(event)).build()).build());
    }

    @VisibleForTesting
    String getRecipientsAsString(SendUserInviteEvent event) {
        String commaSeparatedListOfRecipients = "";
        List recipients = Optional.ofNullable(event.getRecipients()).orElse(new ArrayList());
        if (recipients.isEmpty()) {
            return "UNKNOWN";
        }
        commaSeparatedListOfRecipients = String.join((CharSequence)", ", recipients);
        if (commaSeparatedListOfRecipients.length() > 100) {
            commaSeparatedListOfRecipients = StringUtils.abbreviate((String)commaSeparatedListOfRecipients, (int)100);
        }
        return commaSeparatedListOfRecipients;
    }
}

