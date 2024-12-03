/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.event.events.content.page.PageCopyEvent
 */
package com.atlassian.confluence.plugin.copyspace.chain;

import com.atlassian.confluence.event.events.content.page.PageCopyEvent;
import com.atlassian.confluence.plugin.copyspace.context.CopySpaceContext;

public interface CopyHandler {
    public void checkAndCopy(PageCopyEvent var1, CopySpaceContext var2);
}

