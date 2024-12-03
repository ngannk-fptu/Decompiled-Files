/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.event.events.content.page.PageEvent
 *  com.atlassian.confluence.event.events.types.Created
 *  com.atlassian.confluence.pages.Page
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.plugin.ModuleCompleteKey
 */
package com.atlassian.confluence.plugins.createcontent.events;

import com.atlassian.confluence.event.events.content.page.PageEvent;
import com.atlassian.confluence.event.events.types.Created;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.plugins.createcontent.impl.ContentBlueprint;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.plugin.ModuleCompleteKey;
import java.util.Map;

@Deprecated
public class BlueprintPageCreateEvent
extends PageEvent
implements Created {
    private final ModuleCompleteKey blueprintKey;
    private final Map<String, Object> context;
    private final ConfluenceUser creator;
    private final ContentBlueprint blueprint;

    public BlueprintPageCreateEvent(Object src, Page page, ContentBlueprint blueprint, ConfluenceUser creator, Map<String, Object> context) {
        super(src, page);
        this.blueprint = blueprint;
        this.blueprintKey = new ModuleCompleteKey(blueprint.getModuleCompleteKey());
        this.context = context;
        this.creator = creator;
    }

    @Deprecated
    public ModuleCompleteKey getBlueprintKey() {
        return this.blueprintKey;
    }

    @Deprecated
    public ModuleCompleteKey getBluePrintKey() {
        return this.getBlueprintKey();
    }

    public Map<String, Object> getContext() {
        return this.context;
    }

    public ConfluenceUser getCreator() {
        return this.creator;
    }

    public ContentBlueprint getBlueprint() {
        return this.blueprint;
    }
}

