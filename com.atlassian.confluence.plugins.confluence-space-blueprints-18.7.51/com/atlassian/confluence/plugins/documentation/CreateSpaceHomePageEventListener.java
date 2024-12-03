/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.labels.Label
 *  com.atlassian.confluence.labels.LabelManager
 *  com.atlassian.confluence.labels.Labelable
 *  com.atlassian.confluence.pages.Page
 *  com.atlassian.confluence.pages.PageManager
 *  com.atlassian.confluence.plugins.createcontent.api.events.SpaceBlueprintHomePageCreateEvent
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  javax.annotation.PostConstruct
 *  javax.annotation.PreDestroy
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.documentation;

import com.atlassian.confluence.labels.Label;
import com.atlassian.confluence.labels.LabelManager;
import com.atlassian.confluence.labels.Labelable;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.plugins.createcontent.api.events.SpaceBlueprintHomePageCreateEvent;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CreateSpaceHomePageEventListener {
    private static final String DOCUMENTATION_SPACE_COMPLETE_KEY = "com.atlassian.confluence.plugins.confluence-space-blueprints:documentation-space-blueprint";
    private final LabelManager labelManager;
    private final PageManager pageManager;
    private final EventPublisher eventPublisher;

    @Autowired
    public CreateSpaceHomePageEventListener(@ComponentImport LabelManager labelManager, @ComponentImport PageManager pageManager, @ComponentImport EventPublisher eventPublisher) {
        this.labelManager = labelManager;
        this.pageManager = pageManager;
        this.eventPublisher = eventPublisher;
    }

    @PostConstruct
    public void initialise() {
        this.eventPublisher.register((Object)this);
    }

    @PreDestroy
    public void teardown() {
        this.eventPublisher.unregister((Object)this);
    }

    @EventListener
    public void onSpaceHomePageCreate(SpaceBlueprintHomePageCreateEvent event) {
        if (!DOCUMENTATION_SPACE_COMPLETE_KEY.equals(event.getSpaceBlueprint().getModuleCompleteKey())) {
            return;
        }
        ArrayList<Label> labels = new ArrayList<Label>();
        labels.add(new Label("featured"));
        labels.add(new Label("documentation-space-sample"));
        Space space = event.getSpace();
        long homePageId = space.getHomePage().getId();
        List pages = this.pageManager.getPages(space, false);
        for (Page page : pages) {
            if (page.getId() == homePageId) continue;
            for (Label label : labels) {
                this.labelManager.addLabel((Labelable)page, label);
            }
        }
    }
}

