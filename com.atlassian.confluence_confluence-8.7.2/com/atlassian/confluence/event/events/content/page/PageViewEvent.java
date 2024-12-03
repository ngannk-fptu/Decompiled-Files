/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.event.events.content.page;

import com.atlassian.analytics.api.annotations.EventName;
import com.atlassian.confluence.event.events.content.page.PageEvent;
import com.atlassian.confluence.event.events.types.Viewed;
import com.atlassian.confluence.languages.LocaleInfo;
import com.atlassian.confluence.pages.Page;
import java.util.Map;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

@EventName(value="confluence.page.view")
public class PageViewEvent
extends PageEvent
implements Viewed {
    private static final long serialVersionUID = 5663450646631856609L;
    private final LocaleInfo localeInfo;

    @Deprecated
    public PageViewEvent(Object source, Page page) {
        this(source, page, (LocaleInfo)null);
    }

    public PageViewEvent(Object source, Page page, @Nullable LocaleInfo localeInfo) {
        super(source, page, false);
        this.localeInfo = localeInfo;
    }

    @Override
    public final @Nullable LocaleInfo getLocaleInfo() {
        return this.localeInfo;
    }

    @Override
    public @NonNull Map<String, Object> getProperties() {
        return Viewed.super.getProperties();
    }
}

