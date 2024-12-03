/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugin.notifications.spi;

import com.atlassian.plugin.notifications.api.medium.RecipientType;
import java.util.Map;

public class TemplateParams {
    private final String customTemplatePath;
    private final String templateType;
    private final String mediumKey;
    private final String eventTypeKey;
    private final RecipientType recipientType;
    private final Map context;

    public TemplateParams(String customTemplatePath, String templateType, String mediumKey, String eventTypeKey, RecipientType recipientType, Map context) {
        this.customTemplatePath = customTemplatePath;
        this.templateType = templateType;
        this.mediumKey = mediumKey;
        this.eventTypeKey = eventTypeKey;
        this.recipientType = recipientType;
        this.context = context;
    }

    public String getCustomTemplatePath() {
        return this.customTemplatePath;
    }

    public String getTemplateType() {
        return this.templateType;
    }

    public String getMediumKey() {
        return this.mediumKey;
    }

    public String getEventTypeKey() {
        return this.eventTypeKey;
    }

    public RecipientType getRecipientType() {
        return this.recipientType;
    }

    public Map<String, Object> getContext() {
        return this.context;
    }
}

