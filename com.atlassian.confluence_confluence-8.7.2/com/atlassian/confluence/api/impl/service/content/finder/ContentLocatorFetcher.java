/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.Expansion
 *  com.atlassian.confluence.api.model.Expansions
 *  com.atlassian.confluence.api.model.content.Content
 *  com.atlassian.confluence.api.model.content.ContentType
 *  com.atlassian.confluence.api.model.locator.ContentLocator
 *  com.atlassian.confluence.api.service.content.ContentService$SingleContentFetcher
 */
package com.atlassian.confluence.api.impl.service.content.finder;

import com.atlassian.confluence.api.impl.service.content.factory.ContentFactory;
import com.atlassian.confluence.api.impl.service.content.finder.AbstractFinder;
import com.atlassian.confluence.api.model.Expansion;
import com.atlassian.confluence.api.model.Expansions;
import com.atlassian.confluence.api.model.content.Content;
import com.atlassian.confluence.api.model.content.ContentType;
import com.atlassian.confluence.api.model.locator.ContentLocator;
import com.atlassian.confluence.api.service.content.ContentService;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.internal.pages.PageManagerInternal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Locale;
import java.util.Optional;

class ContentLocatorFetcher
extends AbstractFinder<Content>
implements ContentService.SingleContentFetcher {
    private final ContentLocator locator;
    private final ContentFactory contentFactory;
    private final PageManagerInternal pageManager;

    public ContentLocatorFetcher(ContentLocator locator, ContentFactory contentFactory, PageManagerInternal pageManager, Expansion ... expansions) {
        super(expansions);
        this.locator = locator;
        this.contentFactory = contentFactory;
        this.pageManager = pageManager;
    }

    public Optional<Content> fetch() {
        ContentEntityObject ceo = this.internalFetchContentEntityObject();
        if (ceo != null) {
            return Optional.of(this.contentFactory.buildFrom(ceo, new Expansions(this.expansions)));
        }
        return Optional.empty();
    }

    private ContentEntityObject internalFetchContentEntityObject() {
        if (this.locator.isForContent(ContentType.PAGE)) {
            return this.pageManager.getPage(this.locator.getSpaceKey(), this.locator.getTitle());
        }
        if (this.locator.isForContent(ContentType.BLOG_POST)) {
            Calendar day = this.toCalendar(this.locator.getPostingDate(), ZoneId.systemDefault());
            return this.pageManager.getBlogPost(this.locator.getSpaceKey(), this.locator.getTitle(), day);
        }
        return null;
    }

    private Calendar toCalendar(LocalDate localDate, ZoneId zone) {
        ZonedDateTime postingDate = localDate.atStartOfDay().atZone(zone);
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        calendar.setTimeInMillis(postingDate.toInstant().toEpochMilli());
        return calendar;
    }
}

