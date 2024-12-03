/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.event.events.content.page.PageCopyEvent
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugin.copyspace.chain;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.event.events.content.page.PageCopyEvent;
import com.atlassian.confluence.plugin.copyspace.chain.CopyHandler;
import com.atlassian.confluence.plugin.copyspace.context.CopySpaceContext;
import com.atlassian.confluence.plugin.copyspace.service.WatcherService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class WatcherCopyHandler
implements CopyHandler {
    private static final Logger log = LoggerFactory.getLogger(WatcherCopyHandler.class);
    private final WatcherService watcherService;

    public WatcherCopyHandler(WatcherService watcherService) {
        this.watcherService = watcherService;
    }

    @Override
    public void checkAndCopy(PageCopyEvent event, CopySpaceContext context) {
        if (context.isPreserveWatchers()) {
            log.debug("Copying page watchers...");
            this.watcherService.copyPageWatchers((ContentEntityObject)event.getOrigin(), event.getDestination());
        }
    }
}

