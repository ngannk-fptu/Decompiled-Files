/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.Event
 */
package com.atlassian.confluence.pages.actions;

import com.atlassian.confluence.event.events.content.page.PageCreateFromTemplateEvent;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.actions.CreatePageAction;
import com.atlassian.event.Event;

public class CreatePageFromTemplateAction
extends CreatePageAction {
    private String sourceTemplateId;

    @Override
    protected String afterAdd() {
        this.eventManager.publishEvent((Event)new PageCreateFromTemplateEvent((Object)this, (Page)this.getPage(), this.getSourceTemplateId()));
        return super.afterAdd();
    }

    public String getSourceTemplateId() {
        return this.sourceTemplateId;
    }

    public void setSourceTemplateId(String sourceTemplateId) {
        this.sourceTemplateId = sourceTemplateId;
    }

    public boolean isTemplateApplied() {
        return true;
    }

    @Override
    public String getTemplateId() {
        return this.sourceTemplateId;
    }
}

