/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.event.Evented
 */
package com.atlassian.confluence.plugins.templates.actions;

import com.atlassian.confluence.event.Evented;
import com.atlassian.confluence.plugins.templates.actions.AbstractPageTemplateAction;
import com.atlassian.confluence.plugins.templates.events.RemovePageTemplateEvent;

public class RemovePageTemplateAction
extends AbstractPageTemplateAction
implements Evented<RemovePageTemplateEvent> {
    public String doRemove() throws Exception {
        this.pageTemplateManager.removePageTemplate(this.getPageTemplate());
        return "success" + this.globalTemplateSuffix();
    }

    public RemovePageTemplateEvent getEventToPublish(String result) {
        return new RemovePageTemplateEvent((Object)this);
    }
}

