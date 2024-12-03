/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicApi
 *  com.atlassian.confluence.pages.Page
 *  com.atlassian.confluence.user.ConfluenceUser
 */
package com.atlassian.confluence.plugins.createcontent.api.events;

import com.atlassian.annotations.PublicApi;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.plugins.createcontent.impl.ContentBlueprint;
import com.atlassian.confluence.user.ConfluenceUser;
import java.util.Map;

@PublicApi
public class BlueprintPageCreateEvent
extends com.atlassian.confluence.plugins.createcontent.events.BlueprintPageCreateEvent {
    public BlueprintPageCreateEvent(Object src, Page page, ContentBlueprint blueprint, ConfluenceUser creator, Map<String, Object> context) {
        super(src, page, blueprint, creator, context);
    }
}

