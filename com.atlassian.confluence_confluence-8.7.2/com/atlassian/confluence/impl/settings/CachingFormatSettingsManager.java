/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.CacheFactory
 */
package com.atlassian.confluence.impl.settings;

import com.atlassian.cache.CacheFactory;
import com.atlassian.confluence.cache.CoreCache;
import com.atlassian.confluence.core.FormatSettingsManager;
import com.atlassian.confluence.impl.cache.ReadThroughAtlassianCache;
import com.atlassian.confluence.impl.cache.ReadThroughCache;

public class CachingFormatSettingsManager
implements FormatSettingsManager {
    private final FormatSettingsManager delegate;
    private final ReadThroughCache<Key, String> cache;

    public static CachingFormatSettingsManager create(FormatSettingsManager delegate, CacheFactory cacheFactory) {
        return new CachingFormatSettingsManager(delegate, ReadThroughAtlassianCache.create(cacheFactory, CoreCache.FORMAT_SETTINGS));
    }

    CachingFormatSettingsManager(FormatSettingsManager delegate, ReadThroughCache<Key, String> cache) {
        this.delegate = delegate;
        this.cache = cache;
    }

    @Override
    public String getDateFormat() {
        return this.cache.get(Key.DATE, this.delegate::getDateFormat);
    }

    @Override
    public void setDateFormat(String pattern) {
        this.delegate.setDateFormat(pattern);
        this.cache.remove(Key.DATE);
    }

    @Override
    public String getTimeFormat() {
        return this.cache.get(Key.TIME, this.delegate::getTimeFormat);
    }

    @Override
    public void setTimeFormat(String pattern) {
        this.delegate.setTimeFormat(pattern);
        this.cache.remove(Key.TIME);
    }

    @Override
    public String getDateTimeFormat() {
        return this.cache.get(Key.DATETIME, this.delegate::getDateTimeFormat);
    }

    @Override
    public void setDateTimeFormat(String pattern) {
        this.delegate.setDateTimeFormat(pattern);
        this.cache.remove(Key.DATETIME);
    }

    @Override
    public String getLongNumberFormat() {
        return this.cache.get(Key.LONG, this.delegate::getLongNumberFormat);
    }

    @Override
    public void setLongNumberFormat(String pattern) {
        this.delegate.setLongNumberFormat(pattern);
        this.cache.remove(Key.LONG);
    }

    @Override
    public String getDecimalNumberFormat() {
        return this.cache.get(Key.DECIMAL, this.delegate::getDecimalNumberFormat);
    }

    @Override
    public String getEditorBlogPostDate() {
        return this.cache.get(Key.EDITOR_BLOG_POST_DATE, this.delegate::getEditorBlogPostDate);
    }

    @Override
    public String getEditorBlogPostTime() {
        return this.cache.get(Key.EDITOR_BLOG_POST_TIME, this.delegate::getEditorBlogPostTime);
    }

    @Override
    public void setDecimalNumberFormat(String pattern) {
        this.delegate.setDecimalNumberFormat(pattern);
        this.cache.remove(Key.DECIMAL);
    }

    static enum Key {
        DATE,
        TIME,
        DATETIME,
        LONG,
        DECIMAL,
        EDITOR_BLOG_POST_DATE,
        EDITOR_BLOG_POST_TIME;

    }
}

