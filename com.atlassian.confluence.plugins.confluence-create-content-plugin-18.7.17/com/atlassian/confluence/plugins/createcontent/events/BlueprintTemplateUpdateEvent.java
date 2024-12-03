/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.event.events.template.TemplateEvent
 *  com.atlassian.confluence.event.events.types.Updated
 *  com.atlassian.confluence.spaces.Space
 */
package com.atlassian.confluence.plugins.createcontent.events;

import com.atlassian.confluence.event.events.template.TemplateEvent;
import com.atlassian.confluence.event.events.types.Updated;
import com.atlassian.confluence.spaces.Space;

@Deprecated
public class BlueprintTemplateUpdateEvent
extends TemplateEvent
implements Updated {
    private final String pluginKey;
    private final String moduleKey;
    private final String spaceKey;

    public BlueprintTemplateUpdateEvent(Object src, String pluginKey, String moduleKey, Space space) {
        super(src);
        this.pluginKey = pluginKey;
        this.moduleKey = moduleKey;
        this.spaceKey = space != null ? space.getKey() : "";
    }

    public String getPluginKey() {
        return this.pluginKey;
    }

    public String getModuleKey() {
        return this.moduleKey;
    }

    public String getSpaceKey() {
        return this.spaceKey;
    }
}

