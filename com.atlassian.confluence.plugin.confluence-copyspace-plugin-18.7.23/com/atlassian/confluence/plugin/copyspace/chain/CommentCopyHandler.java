/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.event.events.content.page.PageCopyEvent
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugin.copyspace.chain;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.event.events.content.page.PageCopyEvent;
import com.atlassian.confluence.plugin.copyspace.chain.CopyHandler;
import com.atlassian.confluence.plugin.copyspace.context.CopySpaceContext;
import com.atlassian.confluence.plugin.copyspace.service.CommentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component(value="commentCopyHandler")
public class CommentCopyHandler
implements CopyHandler {
    private static final Logger log = LoggerFactory.getLogger(CommentCopyHandler.class);
    private final CommentService commentService;

    @Autowired
    public CommentCopyHandler(CommentService commentService) {
        this.commentService = commentService;
    }

    @Override
    public void checkAndCopy(PageCopyEvent event, CopySpaceContext context) {
        if (!context.isCopyComments()) {
            return;
        }
        log.debug("Copying page comments...");
        this.commentService.copyComments((ContentEntityObject)event.getDestination(), event.getOrigin().getComments(), context);
        this.commentService.copyFileComments((ContentEntityObject)event.getOrigin(), (ContentEntityObject)event.getDestination());
    }
}

