/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.plugins.createcontent.api.events.SpaceBlueprintHomePageCreateEvent
 *  com.atlassian.confluence.search.IndexManager
 *  com.atlassian.confluence.search.IndexManager$IndexQueueFlushMode
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.event.api.EventListener
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.confluence.plugins.softwareproject;

import com.atlassian.confluence.plugins.createcontent.api.events.SpaceBlueprintHomePageCreateEvent;
import com.atlassian.confluence.plugins.softwareproject.components.AppLinkCreator;
import com.atlassian.confluence.plugins.softwareproject.components.LabelCreator;
import com.atlassian.confluence.plugins.softwareproject.components.SampleAttachmentCreator;
import com.atlassian.confluence.search.IndexManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.event.api.EventListener;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;

public class CreateSpaceHomePageEventListener {
    private static final String SOFTWARE_PROJECT_SPACE_COMPLETE_KEY = "com.atlassian.confluence.plugins.confluence-software-project:sp-space-blueprint";
    private final SampleAttachmentCreator sampleAttachmentCreator;
    private final LabelCreator labelCreator;
    private final AppLinkCreator appLinkCreator;
    private final IndexManager indexManager;

    @Autowired
    public CreateSpaceHomePageEventListener(IndexManager indexManager, SampleAttachmentCreator sampleAttachmentCreator, LabelCreator labelCreator, AppLinkCreator appLinkCreator) {
        this.indexManager = Objects.requireNonNull(indexManager);
        this.sampleAttachmentCreator = Objects.requireNonNull(sampleAttachmentCreator);
        this.labelCreator = Objects.requireNonNull(labelCreator);
        this.appLinkCreator = Objects.requireNonNull(appLinkCreator);
    }

    @EventListener
    public void onSpaceHomePageCreate(SpaceBlueprintHomePageCreateEvent event) {
        if (!SOFTWARE_PROJECT_SPACE_COMPLETE_KEY.equals(event.getSpaceBlueprint().getModuleCompleteKey())) {
            return;
        }
        Space space = event.getSpace();
        this.sampleAttachmentCreator.addSampleAttachmentsToHomePage(space);
        this.labelCreator.addLabelsToIndexPages(space);
        this.appLinkCreator.addJiraAppLink(space, event.getContext());
        this.indexManager.flushQueue(IndexManager.IndexQueueFlushMode.ENTIRE_QUEUE);
    }
}

