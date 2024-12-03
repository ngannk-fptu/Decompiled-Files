/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.content.Content
 *  com.atlassian.confluence.api.model.content.ContentType
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.plugin.descriptor.mail.NotificationContext
 *  com.atlassian.confluence.plugin.descriptor.mail.conditions.AbstractNotificationCondition
 *  com.atlassian.plugin.PluginParseException
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.notifications.impl.conditions;

import com.atlassian.confluence.api.model.content.Content;
import com.atlassian.confluence.api.model.content.ContentType;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.plugin.descriptor.mail.NotificationContext;
import com.atlassian.confluence.plugin.descriptor.mail.conditions.AbstractNotificationCondition;
import com.atlassian.plugin.PluginParseException;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;

public class ContentTypeCondition
extends AbstractNotificationCondition {
    private static final String CONTENT_TYPE = "contentType";
    private static final String CONTEXT_NAME = "contextName";
    private static final String DEFAULT_CONTEXT_NAME = "content";
    private ContentType acceptedContentType;
    private String contextName;

    public void init(Map<String, String> params) throws PluginParseException {
        String contentType;
        this.contextName = params.get(CONTEXT_NAME);
        if (StringUtils.isBlank((CharSequence)this.contextName)) {
            this.contextName = DEFAULT_CONTEXT_NAME;
        }
        if (StringUtils.isBlank((CharSequence)(contentType = params.get(CONTENT_TYPE)))) {
            throw new PluginParseException("Unable to parse required condition parameter contentType");
        }
        this.acceptedContentType = ContentType.valueOf((String)contentType.toLowerCase());
    }

    protected boolean shouldDisplay(NotificationContext context) {
        Object content = context.get(this.contextName);
        if (content instanceof Content) {
            ContentType contentType = ((Content)content).getType();
            return this.acceptedContentType.equals((Object)contentType);
        }
        if (content instanceof ContentEntityObject) {
            ContentType contentType = ContentType.valueOf((String)((ContentEntityObject)content).getType());
            return this.acceptedContentType.equals((Object)contentType);
        }
        return false;
    }
}

