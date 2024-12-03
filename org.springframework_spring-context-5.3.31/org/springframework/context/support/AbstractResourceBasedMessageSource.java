/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.ObjectUtils
 */
package org.springframework.context.support;

import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;
import org.springframework.context.support.AbstractMessageSource;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

public abstract class AbstractResourceBasedMessageSource
extends AbstractMessageSource {
    private final Set<String> basenameSet = new LinkedHashSet<String>(4);
    @Nullable
    private String defaultEncoding;
    private boolean fallbackToSystemLocale = true;
    @Nullable
    private Locale defaultLocale;
    private long cacheMillis = -1L;

    public void setBasename(String basename) {
        this.setBasenames(basename);
    }

    public void setBasenames(String ... basenames) {
        this.basenameSet.clear();
        this.addBasenames(basenames);
    }

    public void addBasenames(String ... basenames) {
        if (!ObjectUtils.isEmpty((Object[])basenames)) {
            for (String basename : basenames) {
                Assert.hasText((String)basename, (String)"Basename must not be empty");
                this.basenameSet.add(basename.trim());
            }
        }
    }

    public Set<String> getBasenameSet() {
        return this.basenameSet;
    }

    public void setDefaultEncoding(@Nullable String defaultEncoding) {
        this.defaultEncoding = defaultEncoding;
    }

    @Nullable
    protected String getDefaultEncoding() {
        return this.defaultEncoding;
    }

    public void setFallbackToSystemLocale(boolean fallbackToSystemLocale) {
        this.fallbackToSystemLocale = fallbackToSystemLocale;
    }

    @Deprecated
    protected boolean isFallbackToSystemLocale() {
        return this.fallbackToSystemLocale;
    }

    public void setDefaultLocale(@Nullable Locale defaultLocale) {
        this.defaultLocale = defaultLocale;
    }

    @Nullable
    protected Locale getDefaultLocale() {
        if (this.defaultLocale != null) {
            return this.defaultLocale;
        }
        if (this.fallbackToSystemLocale) {
            return Locale.getDefault();
        }
        return null;
    }

    public void setCacheSeconds(int cacheSeconds) {
        this.cacheMillis = (long)cacheSeconds * 1000L;
    }

    public void setCacheMillis(long cacheMillis) {
        this.cacheMillis = cacheMillis;
    }

    protected long getCacheMillis() {
        return this.cacheMillis;
    }
}

