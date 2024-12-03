/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.event.events.content.page;

import com.atlassian.confluence.event.events.content.page.PageViewEvent;
import com.atlassian.confluence.languages.LocaleInfo;
import com.atlassian.confluence.pages.Page;
import org.checkerframework.checker.nullness.qual.Nullable;

public class PageInfoViewEvent
extends PageViewEvent {
    private static final long serialVersionUID = 5057075545087064851L;

    @Deprecated
    public PageInfoViewEvent(Object source, Page page) {
        this(source, page, (LocaleInfo)null);
    }

    public PageInfoViewEvent(Object source, Page page, @Nullable LocaleInfo localeInfo) {
        super(source, page, localeInfo);
    }
}

