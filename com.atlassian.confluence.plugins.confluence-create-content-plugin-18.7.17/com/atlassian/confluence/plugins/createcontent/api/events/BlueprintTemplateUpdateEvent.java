/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicApi
 *  com.atlassian.confluence.spaces.Space
 */
package com.atlassian.confluence.plugins.createcontent.api.events;

import com.atlassian.annotations.PublicApi;
import com.atlassian.confluence.spaces.Space;

@PublicApi
public class BlueprintTemplateUpdateEvent
extends com.atlassian.confluence.plugins.createcontent.events.BlueprintTemplateUpdateEvent {
    public BlueprintTemplateUpdateEvent(Object src, String pluginKey, String moduleKey, Space space) {
        super(src, pluginKey, moduleKey, space);
    }
}

