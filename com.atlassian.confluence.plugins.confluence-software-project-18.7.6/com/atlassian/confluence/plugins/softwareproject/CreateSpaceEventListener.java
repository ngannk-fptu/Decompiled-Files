/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.plugins.createcontent.api.events.SpaceBlueprintCreateEvent
 *  com.atlassian.confluence.rpc.NotPermittedException
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  org.apache.commons.lang.StringUtils
 *  org.springframework.beans.factory.DisposableBean
 */
package com.atlassian.confluence.plugins.softwareproject;

import com.atlassian.confluence.plugins.createcontent.api.events.SpaceBlueprintCreateEvent;
import com.atlassian.confluence.plugins.softwareproject.event.SoftwareSpaceCreatedEvent;
import com.atlassian.confluence.rpc.NotPermittedException;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.DisposableBean;

public class CreateSpaceEventListener
implements DisposableBean {
    private static final String SOFTWARE_PROJECT_SPACE_COMPLETE_KEY = "com.atlassian.confluence.plugins.confluence-software-project:sp-space-blueprint";
    private final EventPublisher eventPublisher;

    public CreateSpaceEventListener(EventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
        this.eventPublisher.register((Object)this);
    }

    @EventListener
    public void onSpaceBlueprintCreate(SpaceBlueprintCreateEvent event) throws NotPermittedException {
        String projectName;
        if (!event.getSpaceBlueprint().getModuleCompleteKey().equals(SOFTWARE_PROJECT_SPACE_COMPLETE_KEY)) {
            return;
        }
        Map context = event.getContext();
        Space space = event.getSpace();
        context.put("spaceKey", space.getKey());
        String projectKey = (String)context.get("project-key");
        if (StringUtils.isEmpty((String)projectKey)) {
            context.put("project-key", space.getKey());
        }
        if (StringUtils.isEmpty((String)(projectName = (String)context.get("project-name")))) {
            context.put("project-name", space.getName());
        }
        this.eventPublisher.publish((Object)new SoftwareSpaceCreatedEvent());
    }

    public void destroy() throws Exception {
        this.eventPublisher.unregister((Object)this);
    }
}

