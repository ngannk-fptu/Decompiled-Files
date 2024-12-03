/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.event.events.template;

import com.atlassian.confluence.event.events.template.TemplateEvent;
import com.atlassian.confluence.event.events.types.Removed;
import com.atlassian.confluence.pages.templates.PageTemplate;

public class TemplateRemoveEvent
extends TemplateEvent
implements Removed {
    private static final long serialVersionUID = 6795833813376994721L;
    private PageTemplate template;

    public TemplateRemoveEvent(Object src, PageTemplate template) {
        super(src);
        this.template = template;
    }

    public PageTemplate getTemplate() {
        return this.template;
    }
}

