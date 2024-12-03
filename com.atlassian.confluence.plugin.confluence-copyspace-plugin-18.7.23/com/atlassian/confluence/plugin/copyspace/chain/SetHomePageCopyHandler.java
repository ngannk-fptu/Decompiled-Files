/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.event.events.content.page.PageCopyEvent
 *  com.atlassian.confluence.pages.Page
 *  com.atlassian.confluence.spaces.Space
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugin.copyspace.chain;

import com.atlassian.confluence.event.events.content.page.PageCopyEvent;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.plugin.copyspace.chain.CopyHandler;
import com.atlassian.confluence.plugin.copyspace.context.CopySpaceContext;
import com.atlassian.confluence.spaces.Space;
import org.springframework.stereotype.Component;

@Component(value="setHomePageCopyHandler")
public class SetHomePageCopyHandler
implements CopyHandler {
    @Override
    public void checkAndCopy(PageCopyEvent event, CopySpaceContext context) {
        if (event.getOrigin().isHomePage()) {
            Page copiedPage = event.getDestination();
            Space space = copiedPage.getSpace();
            space.setHomePage(copiedPage);
        }
    }
}

