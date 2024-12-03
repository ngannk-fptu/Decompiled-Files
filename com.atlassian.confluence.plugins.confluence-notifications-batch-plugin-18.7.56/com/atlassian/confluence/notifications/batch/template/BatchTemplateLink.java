/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.notifications.batch.template;

import com.atlassian.confluence.notifications.batch.template.BatchTemplateElement;

public class BatchTemplateLink
implements BatchTemplateElement {
    public static final String TEMPLATE_NAME = "link";
    private final String name;
    private final String link;
    private final boolean relativeToInstance;

    public BatchTemplateLink(String name, String link, boolean relativeToInstance) {
        this.name = name;
        this.link = link;
        this.relativeToInstance = relativeToInstance;
    }

    public String getName() {
        return this.name;
    }

    public String getLink() {
        return this.link;
    }

    public boolean isRelativeToInstance() {
        return this.relativeToInstance;
    }

    @Override
    public String getTemplateName() {
        return TEMPLATE_NAME;
    }
}

