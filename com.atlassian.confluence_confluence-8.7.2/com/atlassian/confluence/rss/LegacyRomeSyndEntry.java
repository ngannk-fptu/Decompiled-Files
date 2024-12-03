/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.syndication.feed.synd.SyndCategory
 *  com.sun.syndication.feed.synd.SyndCategoryImpl
 *  com.sun.syndication.feed.synd.SyndContent
 *  com.sun.syndication.feed.synd.SyndContentImpl
 *  com.sun.syndication.feed.synd.SyndEntry
 */
package com.atlassian.confluence.rss;

import com.atlassian.confluence.rss.RomeSyndEntry;
import com.sun.syndication.feed.synd.SyndCategory;
import com.sun.syndication.feed.synd.SyndCategoryImpl;
import com.sun.syndication.feed.synd.SyndContent;
import com.sun.syndication.feed.synd.SyndContentImpl;
import com.sun.syndication.feed.synd.SyndEntry;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

final class LegacyRomeSyndEntry
implements RomeSyndEntry,
Supplier<SyndEntry> {
    private final SyndEntry delegate;

    LegacyRomeSyndEntry(SyndEntry delegate) {
        this.delegate = delegate;
    }

    @Override
    public void setTitle(String title) {
        this.delegate.setTitle(title);
    }

    @Override
    public void setLink(String link) {
        this.delegate.setLink(link);
    }

    @Override
    public void setUri(String uri) {
        this.delegate.setUri(uri);
    }

    @Override
    public void setAuthor(String author) {
        this.delegate.setAuthor(author);
    }

    @Override
    public void setPublishedDate(Date publishedDate) {
        this.delegate.setPublishedDate(publishedDate);
    }

    @Override
    public void setUpdatedDate(Date updatedDate) {
        this.delegate.setUpdatedDate(updatedDate);
    }

    @Override
    public void setDescription(String type, String value) {
        SyndContentImpl description = new SyndContentImpl();
        description.setType(type);
        description.setValue(value);
        this.delegate.setDescription((SyndContent)description);
    }

    @Override
    public void setCategoryNames(Collection<String> categoryNames) {
        this.delegate.setCategories(LegacyRomeSyndEntry.asCategories(categoryNames));
    }

    static List<SyndCategory> asCategories(Collection<String> categoryNames) {
        return categoryNames.stream().map(name -> {
            SyndCategoryImpl category = new SyndCategoryImpl();
            category.setName(name);
            return category;
        }).collect(Collectors.toList());
    }

    @Override
    public SyndEntry get() {
        return this.delegate;
    }
}

