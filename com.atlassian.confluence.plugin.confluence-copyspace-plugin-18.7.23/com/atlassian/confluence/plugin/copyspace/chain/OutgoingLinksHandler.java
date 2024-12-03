/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.event.events.content.page.PageCopyEvent
 *  com.atlassian.confluence.pages.Page
 *  com.atlassian.confluence.pages.PageManager
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugin.copyspace.chain;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.event.events.content.page.PageCopyEvent;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.plugin.copyspace.chain.CopyHandler;
import com.atlassian.confluence.plugin.copyspace.context.CopySpaceContext;
import com.atlassian.confluence.plugin.copyspace.service.LinksUpdater;
import com.atlassian.confluence.plugin.copyspace.util.Constants;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component(value="outgoingLinksHandler")
public class OutgoingLinksHandler
implements CopyHandler {
    private final LinksUpdater linksUpdater;
    private final PageManager pageManager;

    @Autowired
    public OutgoingLinksHandler(LinksUpdater linksUpdater, @ComponentImport PageManager pageManager) {
        this.linksUpdater = linksUpdater;
        this.pageManager = pageManager;
    }

    @Override
    public void checkAndCopy(PageCopyEvent event, CopySpaceContext context) {
        Page newPage = event.getDestination();
        String newBody = this.linksUpdater.rewriteLinks(newPage.getBodyAsString(), (ContentEntityObject)newPage, context);
        newPage.setBodyAsString(newBody);
        newPage.setSynchronyRevisionSource("restored");
        this.pageManager.saveContentEntity((ContentEntityObject)newPage, Constants.SUPPRESS_EVENT_KEEP_LAST_MODIFIER);
    }
}

