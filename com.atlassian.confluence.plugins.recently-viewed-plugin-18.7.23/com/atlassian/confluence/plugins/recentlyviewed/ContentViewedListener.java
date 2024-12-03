/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.core.SpaceContentEntityObject
 *  com.atlassian.confluence.event.events.content.blogpost.BlogPostRemoveEvent
 *  com.atlassian.confluence.event.events.content.blogpost.BlogPostViewEvent
 *  com.atlassian.confluence.event.events.content.page.PageRemoveEvent
 *  com.atlassian.confluence.event.events.content.page.PageViewEvent
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.executor.ThreadLocalDelegateExecutorFactory
 *  com.atlassian.sal.api.user.UserKey
 *  com.atlassian.sal.api.user.UserManager
 *  com.atlassian.util.concurrent.ThreadFactories
 *  com.atlassian.util.concurrent.ThreadFactories$Type
 *  javax.annotation.PostConstruct
 *  javax.annotation.PreDestroy
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.recentlyviewed;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.SpaceContentEntityObject;
import com.atlassian.confluence.event.events.content.blogpost.BlogPostRemoveEvent;
import com.atlassian.confluence.event.events.content.blogpost.BlogPostViewEvent;
import com.atlassian.confluence.event.events.content.page.PageRemoveEvent;
import com.atlassian.confluence.event.events.content.page.PageViewEvent;
import com.atlassian.confluence.plugins.recentlyviewed.RecentlyViewedManager;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.executor.ThreadLocalDelegateExecutorFactory;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.util.concurrent.ThreadFactories;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ContentViewedListener {
    private static final Logger log = LoggerFactory.getLogger(ContentViewedListener.class);
    private final EventPublisher eventPublisher;
    private final RecentlyViewedManager recentlyViewedManager;
    private final UserManager userManager;
    private final ExecutorService executorService;

    @Autowired
    public ContentViewedListener(@ComponentImport EventPublisher eventPublisher, RecentlyViewedManager recentlyViewedManager, @ComponentImport UserManager userManager, @ComponentImport ThreadLocalDelegateExecutorFactory threadLocalDelegateExecutorFactory) {
        this.eventPublisher = eventPublisher;
        this.recentlyViewedManager = recentlyViewedManager;
        this.userManager = userManager;
        this.executorService = threadLocalDelegateExecutorFactory.createExecutorService(Executors.newCachedThreadPool(ThreadFactories.namedThreadFactory((String)ContentViewedListener.class.getSimpleName(), (ThreadFactories.Type)ThreadFactories.Type.DAEMON)));
    }

    private void addPageViewToHistory(Long contentId, String contentType, String spaceKey, long timestamp) {
        long start = System.currentTimeMillis();
        try {
            UserKey userKey = this.userManager.getRemoteUserKey();
            if (userKey != null) {
                this.recentlyViewedManager.savePageView(userKey.getStringValue(), contentId, contentType, spaceKey, timestamp);
            }
        }
        catch (Exception e) {
            log.debug("Failed to add page view to history", (Throwable)e);
        }
        log.debug("Saving history entry for page took " + (System.currentTimeMillis() - start) + "ms.");
    }

    private void addPageViewToHistoryAsync(Long contentId, String contentType, String spaceKey, long timestamp) {
        this.executorService.execute(() -> this.addPageViewToHistory(contentId, contentType, spaceKey, timestamp));
    }

    private void removePageViewsInHistory(Long contentId) {
        this.recentlyViewedManager.removePageViews(contentId);
    }

    @EventListener
    public void onPageView(PageViewEvent event) {
        this.addPageViewToHistoryAsync(event.getContent().getId(), StringUtils.upperCase((String)event.getContent().getType()), this.getSpaceKey(event.getContent()), event.getTimestamp());
    }

    @EventListener
    public void onBlogPostView(BlogPostViewEvent event) {
        this.addPageViewToHistoryAsync(event.getContent().getId(), StringUtils.upperCase((String)event.getContent().getType()), this.getSpaceKey(event.getContent()), event.getTimestamp());
    }

    @EventListener
    public void onPageRemove(PageRemoveEvent event) {
        this.removePageViewsInHistory(event.getContent().getId());
    }

    @EventListener
    public void onBlogPostRemove(BlogPostRemoveEvent event) {
        this.removePageViewsInHistory(event.getContent().getId());
    }

    @PreDestroy
    public void destroy() {
        this.eventPublisher.unregister((Object)this);
        this.executorService.shutdownNow();
    }

    @PostConstruct
    public void afterPropertiesSet() {
        this.eventPublisher.register((Object)this);
    }

    private String getSpaceKey(ContentEntityObject ceo) {
        if (ceo instanceof SpaceContentEntityObject) {
            return ((SpaceContentEntityObject)ceo).getSpaceKey();
        }
        return null;
    }
}

