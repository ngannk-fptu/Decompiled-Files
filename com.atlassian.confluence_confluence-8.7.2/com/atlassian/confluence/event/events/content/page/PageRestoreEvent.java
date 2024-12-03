/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.event.events.content.page;

import com.atlassian.confluence.event.events.content.page.PageEvent;
import com.atlassian.confluence.event.events.types.Restore;
import com.atlassian.confluence.pages.Page;

public class PageRestoreEvent
extends PageEvent
implements Restore {
    private static final long serialVersionUID = -5917100805565270036L;

    public PageRestoreEvent(Object src, Page page) {
        super(src, page, false);
    }
}

