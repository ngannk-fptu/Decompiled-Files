/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.xhtml.ConversionContext
 *  com.atlassian.confluence.content.render.xhtml.DefaultConversionContext
 *  com.atlassian.confluence.content.render.xhtml.XhtmlException
 *  com.atlassian.confluence.event.events.content.blogpost.BlogPostCreateEvent
 *  com.atlassian.confluence.event.events.content.page.PageCreateEvent
 *  com.atlassian.confluence.pages.AbstractPage
 *  com.atlassian.confluence.xhtml.api.MacroDefinition
 *  com.atlassian.confluence.xhtml.api.MacroDefinitionHandler
 *  com.atlassian.confluence.xhtml.api.XhtmlContent
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.renderer.RenderContext
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.tinymceplugin.listeners;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.DefaultConversionContext;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.event.events.content.blogpost.BlogPostCreateEvent;
import com.atlassian.confluence.event.events.content.page.PageCreateEvent;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.tinymceplugin.events.SectionMacroCreatedEvent;
import com.atlassian.confluence.xhtml.api.MacroDefinition;
import com.atlassian.confluence.xhtml.api.MacroDefinitionHandler;
import com.atlassian.confluence.xhtml.api.XhtmlContent;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.renderer.RenderContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class SectionAndColumnMacroCreateListener {
    private static final Logger log = LoggerFactory.getLogger(SectionAndColumnMacroCreateListener.class);
    private final XhtmlContent xhtmlContent;
    private final EventPublisher eventPublisher;

    public SectionAndColumnMacroCreateListener(XhtmlContent xhtmlContent, EventPublisher eventPublisher) {
        this.xhtmlContent = xhtmlContent;
        this.eventPublisher = eventPublisher;
        eventPublisher.register((Object)this);
    }

    private boolean doesContentIncludeSectionMacro(AbstractPage abstractPage) {
        DefaultConversionContext context = new DefaultConversionContext((RenderContext)abstractPage.toPageContext());
        SectionAndColumnMacroDefinitionHandler handler = new SectionAndColumnMacroDefinitionHandler();
        try {
            this.xhtmlContent.handleMacroDefinitions(abstractPage.getBodyAsString(), (ConversionContext)context, (MacroDefinitionHandler)handler);
        }
        catch (XhtmlException e) {
            log.error("Error parsing content", (Throwable)e);
        }
        return handler.isFound();
    }

    private void handlePage(AbstractPage abstractPage) {
        if (this.doesContentIncludeSectionMacro(abstractPage)) {
            log.debug("Content created with the section macro");
            this.eventPublisher.publish((Object)new SectionMacroCreatedEvent(abstractPage));
        }
    }

    @EventListener
    public void onPageCreateEvent(PageCreateEvent event) {
        this.handlePage((AbstractPage)event.getPage());
    }

    @EventListener
    public void onBlogPostCreateEvent(BlogPostCreateEvent event) {
        this.handlePage((AbstractPage)event.getBlogPost());
    }

    public void destroy() throws Exception {
        this.eventPublisher.unregister((Object)this);
    }

    private static class SectionAndColumnMacroDefinitionHandler
    implements MacroDefinitionHandler {
        private static final String SECTION_MACRO_NAME = "section";
        private boolean found = false;

        private SectionAndColumnMacroDefinitionHandler() {
        }

        public void handle(MacroDefinition macroDefinition) {
            if (!this.found && SECTION_MACRO_NAME.equals(macroDefinition.getName())) {
                this.found = true;
            }
        }

        public boolean isFound() {
            return this.found;
        }
    }
}

