/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.event.events.content.blogpost.BlogPostCreateEvent
 *  com.atlassian.confluence.event.events.content.page.PageCreateEvent
 *  com.atlassian.confluence.pages.AbstractPage
 *  com.atlassian.confluence.plugins.createcontent.api.events.BlueprintPageCreateEvent
 *  com.atlassian.event.api.EventListener
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugins.roadmap;

import com.atlassian.confluence.event.events.content.blogpost.BlogPostCreateEvent;
import com.atlassian.confluence.event.events.content.page.PageCreateEvent;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.plugins.createcontent.api.events.BlueprintPageCreateEvent;
import com.atlassian.event.api.EventListener;
import com.atlassian.plugins.roadmap.BarParam;
import com.atlassian.plugins.roadmap.TimelinePlannerMacroManager;
import com.atlassian.plugins.roadmap.models.RoadmapPageLink;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreatePageEventListener {
    protected static final Logger log = LoggerFactory.getLogger(CreatePageEventListener.class);
    private TimelinePlannerMacroManager timelinePlannerMacroManager;

    public CreatePageEventListener(TimelinePlannerMacroManager timelinePlannerMacroManager) {
        this.timelinePlannerMacroManager = timelinePlannerMacroManager;
    }

    @EventListener
    public void pageCreateEvent(PageCreateEvent event) {
        this.handleRoadmapLink((AbstractPage)event.getPage(), (Map<String, ?>)event.getContext());
    }

    @EventListener
    public void blogpostCreateEvent(BlogPostCreateEvent event) {
        this.handleRoadmapLink((AbstractPage)event.getBlogPost(), (Map<String, ?>)event.getContext());
    }

    @EventListener
    public void blueprintPageCreateEvent(BlueprintPageCreateEvent event) {
        this.handleRoadmapLink((AbstractPage)event.getPage(), event.getContext());
    }

    private void handleRoadmapLink(AbstractPage linkPage, Map<String, ?> context) {
        if (!(context.containsKey("roadmapBarId") && (context.containsKey("updateRoadmap") || context.containsKey("roadmapHash") && context.containsKey("roadmapContentId") && context.containsKey("version")))) {
            return;
        }
        this.timelinePlannerMacroManager.put((String)context.get("roadmapBarId"), TimelinePlannerMacroManager.LinkStatus.REDEEM);
        this.timelinePlannerMacroManager.updatePagelinkToRoadmapBar(BarParam.fromMap(context), new RoadmapPageLink(linkPage));
    }
}

