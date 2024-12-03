/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalSpi
 *  com.atlassian.confluence.api.model.content.Content
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.plugin.descriptor.mail.NotificationContext
 *  com.atlassian.confluence.plugin.descriptor.mail.conditions.AbstractNotificationCondition
 *  com.atlassian.plugin.PluginParseException
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.notifications.batch.service;

import com.atlassian.annotations.ExperimentalSpi;
import com.atlassian.confluence.api.model.content.Content;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.notifications.batch.service.BatchTarget;
import com.atlassian.confluence.plugin.descriptor.mail.NotificationContext;
import com.atlassian.confluence.plugin.descriptor.mail.conditions.AbstractNotificationCondition;
import com.atlassian.plugin.PluginParseException;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ExperimentalSpi
public class BatchTargetCondition
extends AbstractNotificationCondition {
    private static final String BATCH_TARGET = "batchTarget";
    private static final String CONTEXT_NAME_KEY = "contextName";
    private static final String DEFAULT_CONTEXT_NAME = "content";
    private final Logger log = LoggerFactory.getLogger(((Object)((Object)this)).getClass());
    private String contextName;

    public void init(Map<String, String> params) throws PluginParseException {
        this.contextName = params.get(CONTEXT_NAME_KEY);
        if (StringUtils.isBlank((CharSequence)this.contextName)) {
            this.contextName = DEFAULT_CONTEXT_NAME;
        }
    }

    protected boolean shouldDisplay(NotificationContext notificationContext) {
        String contentId;
        Object content = notificationContext.get(this.contextName);
        if (content instanceof Content) {
            contentId = ((Content)content).getId().serialise();
        } else if (content instanceof ContentEntityObject) {
            contentId = ((ContentEntityObject)content).getIdAsString();
        } else {
            this.log.warn("Missing Content or ContentEntityObject from context");
            contentId = null;
        }
        if (contentId != null) {
            Object batchTarget = notificationContext.get(BATCH_TARGET);
            if (batchTarget instanceof BatchTarget) {
                return contentId.equals(((BatchTarget)batchTarget).getContentId());
            }
            this.log.warn("Missing BatchTarget from context");
        }
        return false;
    }
}

