/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.notifications.batch.template;

import com.atlassian.confluence.notifications.batch.template.BatchTemplateElement;

public class BatchTemplateBadge
implements BatchTemplateElement {
    public static final String TEMPLATE_NAME = "badge";
    private final int number;
    private final boolean highlighted;

    public BatchTemplateBadge(int number, boolean highlighted) {
        this.number = number;
        this.highlighted = highlighted;
    }

    public int getNumber() {
        return this.number;
    }

    public boolean isHighlighted() {
        return this.highlighted;
    }

    @Override
    public String getTemplateName() {
        return TEMPLATE_NAME;
    }
}

