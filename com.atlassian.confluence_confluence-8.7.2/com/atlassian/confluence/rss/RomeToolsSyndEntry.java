/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.rometools.rome.feed.synd.SyndCategory
 *  com.rometools.rome.feed.synd.SyndCategoryImpl
 *  com.rometools.rome.feed.synd.SyndContent
 *  com.rometools.rome.feed.synd.SyndContentImpl
 *  com.rometools.rome.feed.synd.SyndEntry
 */
package com.atlassian.confluence.rss;

import com.atlassian.confluence.rss.RomeSyndEntry;
import com.rometools.rome.feed.synd.SyndCategory;
import com.rometools.rome.feed.synd.SyndCategoryImpl;
import com.rometools.rome.feed.synd.SyndContent;
import com.rometools.rome.feed.synd.SyndContentImpl;
import com.rometools.rome.feed.synd.SyndEntry;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

final class RomeToolsSyndEntry
implements RomeSyndEntry,
Supplier<SyndEntry> {
    private final SyndEntry delegate;

    RomeToolsSyndEntry(SyndEntry delegate) {
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
        this.delegate.setCategories(RomeToolsSyndEntry.asCategories(categoryNames));
    }

    private static List<SyndCategory> asCategories(Collection<String> categoryNames) {
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

