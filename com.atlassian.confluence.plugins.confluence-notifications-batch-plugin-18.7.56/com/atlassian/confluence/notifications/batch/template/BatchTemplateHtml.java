/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.notifications.batch.template;

import com.atlassian.confluence.notifications.batch.template.BatchTemplateElement;
import com.atlassian.confluence.notifications.batch.template.BatchTemplateMessage;

public class BatchTemplateHtml
implements BatchTemplateElement {
    public static final String TEMPLATE_NAME = "html";
    private final String html;
    private final boolean border;
    private final BatchTemplateMessage title;

    public BatchTemplateHtml(String html) {
        this(html, true, null);
    }

    public BatchTemplateHtml(String html, boolean border) {
        this(html, border, null);
    }

    public BatchTemplateHtml(String html, BatchTemplateMessage title) {
        this(html, true, title);
    }

    public BatchTemplateHtml(String html, boolean border, BatchTemplateMessage title) {
        this.html = html;
        this.border = border;
        this.title = title;
    }

    public String getHtml() {
        return this.html;
    }

    public boolean isBorder() {
        return this.border;
    }

    @Override
    public String getTemplateName() {
        return TEMPLATE_NAME;
    }

    public BatchTemplateMessage getTitle() {
        return this.title;
    }
}

