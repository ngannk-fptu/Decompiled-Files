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
 *  com.atlassian.confluence.xhtml.api.PageLayoutVisitor
 *  com.atlassian.confluence.xhtml.api.XhtmlContent
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.renderer.RenderContext
 *  org.apache.commons.lang3.StringUtils
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
import com.atlassian.confluence.tinymceplugin.events.Layout2CreatedEvent;
import com.atlassian.confluence.tinymceplugin.events.LayoutCreatedEvent;
import com.atlassian.confluence.xhtml.api.PageLayoutVisitor;
import com.atlassian.confluence.xhtml.api.XhtmlContent;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.renderer.RenderContext;
import java.util.Collections;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class PageLayoutCreateListener {
    private static final Logger log = LoggerFactory.getLogger(PageLayoutCreateListener.class);
    private final EventPublisher eventPublisher;
    private final XhtmlContent xhtmlContent;

    public PageLayoutCreateListener(EventPublisher eventPublisher, XhtmlContent xhtmlContent) {
        this.eventPublisher = eventPublisher;
        this.xhtmlContent = xhtmlContent;
        eventPublisher.register((Object)this);
    }

    private PageLayoutVisitor parsePageForLayout(AbstractPage abstractPage) {
        log.debug("Parsing page for layout, {}", (Object)abstractPage);
        PageLayoutVisitor visitor = new PageLayoutVisitor();
        try {
            this.xhtmlContent.handleXhtmlElements(abstractPage.getBodyAsString(), (ConversionContext)new DefaultConversionContext((RenderContext)abstractPage.toPageContext()), Collections.singletonList(visitor));
        }
        catch (XhtmlException e) {
            log.error("Error parsing content", (Throwable)e);
        }
        log.debug("Done parsing page for layout");
        return visitor;
    }

    private void handlePage(AbstractPage abstractPage) {
        PageLayoutVisitor visitor = this.parsePageForLayout(abstractPage);
        if (visitor.getPageLayout2CellCount() > 0 || visitor.getPageLayout2RowCount() > 0) {
            Layout2CreatedEvent layout2CreatedEvent = new Layout2CreatedEvent(visitor.getPageLayoutOneType(), abstractPage, visitor.getPageLayout2CellCount(), visitor.getPageLayout2RowCount());
            log.debug("Page layout two found {}", (Object)layout2CreatedEvent);
            this.eventPublisher.publish((Object)layout2CreatedEvent);
        } else if (StringUtils.isNotBlank((CharSequence)visitor.getPageLayoutOneType())) {
            LayoutCreatedEvent layoutCreatedEvent = new LayoutCreatedEvent(visitor.getPageLayoutOneType(), abstractPage);
            log.debug("Page layout one found {}" + visitor.getPageLayoutOneType());
            this.eventPublisher.publish((Object)layoutCreatedEvent);
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
}

