/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.event.events.template;

import com.atlassian.confluence.event.events.template.TemplateEvent;
import com.atlassian.confluence.event.events.types.Updated;
import com.atlassian.confluence.pages.templates.PageTemplate;

public class TemplateUpdateEvent
extends TemplateEvent
implements Updated {
    private static final long serialVersionUID = 7724847502437780134L;
    private PageTemplate oldTemplate;
    private PageTemplate newTemplate;

    public TemplateUpdateEvent(Object src, PageTemplate oldTemplate, PageTemplate newTemplate) {
        super(src);
        this.oldTemplate = oldTemplate;
        this.newTemplate = newTemplate;
    }

    public PageTemplate getOldTemplate() {
        return this.oldTemplate;
    }

    public PageTemplate getNewTemplate() {
        return this.newTemplate;
    }
}

