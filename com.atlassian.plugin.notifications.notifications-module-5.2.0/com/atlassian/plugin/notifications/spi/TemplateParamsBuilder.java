/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugin.notifications.spi;

import com.atlassian.plugin.notifications.api.medium.RecipientType;
import com.atlassian.plugin.notifications.spi.TemplateParams;
import java.util.Map;

public class TemplateParamsBuilder {
    private String customTemplatePath;
    private String templateType;
    private String mediumKey;
    private String eventTypeKey;
    private RecipientType recipientType;
    private Map context;

    public TemplateParamsBuilder(TemplateParams original) {
        this.customTemplatePath = original.getCustomTemplatePath();
        this.templateType = original.getTemplateType();
        this.mediumKey = original.getMediumKey();
        this.eventTypeKey = original.getEventTypeKey();
        this.recipientType = original.getRecipientType();
        this.context = original.getContext();
    }

    public TemplateParamsBuilder() {
    }

    public TemplateParamsBuilder customTemplatePath(String customTemplatePath) {
        this.customTemplatePath = customTemplatePath;
        return this;
    }

    public TemplateParamsBuilder templateType(String templateType) {
        this.templateType = templateType;
        return this;
    }

    public TemplateParamsBuilder mediumKey(String mediumKey) {
        this.mediumKey = mediumKey;
        return this;
    }

    public TemplateParamsBuilder eventTypeKey(String eventTypeKey) {
        this.eventTypeKey = eventTypeKey;
        return this;
    }

    public TemplateParamsBuilder recipientType(RecipientType recipientType) {
        this.recipientType = recipientType;
        return this;
    }

    public TemplateParamsBuilder context(Map context) {
        this.context = context;
        return this;
    }

    public TemplateParams build() {
        return new TemplateParams(this.customTemplatePath, this.templateType, this.mediumKey, this.eventTypeKey, this.recipientType, this.context);
    }

    public static TemplateParamsBuilder create() {
        return new TemplateParamsBuilder();
    }

    public static TemplateParamsBuilder create(TemplateParams original) {
        return new TemplateParamsBuilder(original);
    }
}

