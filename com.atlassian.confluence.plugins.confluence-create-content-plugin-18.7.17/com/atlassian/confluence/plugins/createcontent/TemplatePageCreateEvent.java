/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.DefaultSaveContext
 *  com.atlassian.confluence.core.SaveContext
 *  com.atlassian.confluence.event.events.content.page.PageEvent
 *  com.atlassian.confluence.event.events.types.Created
 *  com.atlassian.confluence.pages.Page
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.plugin.ModuleCompleteKey
 */
package com.atlassian.confluence.plugins.createcontent;

import com.atlassian.confluence.core.DefaultSaveContext;
import com.atlassian.confluence.core.SaveContext;
import com.atlassian.confluence.event.events.content.page.PageEvent;
import com.atlassian.confluence.event.events.types.Created;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.plugins.createcontent.impl.ContentTemplateRef;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.plugin.ModuleCompleteKey;
import java.util.Map;

public class TemplatePageCreateEvent
extends PageEvent
implements Created {
    private final ModuleCompleteKey completeKey;
    private final Map<String, Object> context;
    private final ConfluenceUser user;
    private final ContentTemplateRef templateRef;
    private final SaveContext saveContext;

    @Deprecated
    public TemplatePageCreateEvent(Object src, Page page, ContentTemplateRef templateRef, ConfluenceUser user, Map<String, Object> context) {
        this(src, page, templateRef, user, context, DefaultSaveContext.DEFAULT);
    }

    public TemplatePageCreateEvent(Object src, Page page, ContentTemplateRef templateRef, ConfluenceUser user, Map<String, Object> context, SaveContext saveContext) {
        super(src, page, false);
        this.templateRef = templateRef;
        this.completeKey = templateRef.getModuleCompleteKey() == null ? null : new ModuleCompleteKey(templateRef.getModuleCompleteKey());
        this.context = context;
        this.user = user;
        this.saveContext = saveContext;
    }

    @Deprecated
    public ModuleCompleteKey getTemplateKey() {
        return this.completeKey;
    }

    public Map<String, Object> getContext() {
        return this.context;
    }

    public ConfluenceUser getUser() {
        return this.user;
    }

    public ContentTemplateRef getTemplateRef() {
        return this.templateRef;
    }

    public SaveContext getSaveContext() {
        return this.saveContext;
    }
}

