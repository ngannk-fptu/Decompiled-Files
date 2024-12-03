/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.notifications.batch.template;

import com.atlassian.confluence.notifications.batch.template.BatchTemplateElement;

public class BatchTemplateLozenge
implements BatchTemplateElement {
    public static final String TEMPLATE_NAME = "lozenge";
    private final String text;
    private final Status status;
    @Deprecated
    private final boolean subtle;

    public BatchTemplateLozenge(String text, Status status, boolean subtle) {
        this.text = text;
        this.status = status;
        this.subtle = subtle;
    }

    public String getText() {
        return this.text;
    }

    public Status getStatus() {
        return this.status;
    }

    @Deprecated
    public boolean isSubtle() {
        return this.subtle;
    }

    @Override
    public String getTemplateName() {
        return TEMPLATE_NAME;
    }

    public static enum Status {
        DEFAULT,
        SUCCESS,
        ERROR,
        CURRENT,
        NEW,
        MOVED;

    }
}

