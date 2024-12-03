/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.context.support;

import java.util.LinkedHashSet;
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
    private long cacheMillis = -1L;

    public void setBasename(String basename) {
        this.setBasenames(basename);
    }

    public void setBasenames(String ... basenames) {
        this.basenameSet.clear();
        this.addBasenames(basenames);
    }

    public void addBasenames(String ... basenames) {
        if (!ObjectUtils.isEmpty(basenames)) {
            for (String basename : basenames) {
                Assert.hasText(basename, "Basename must not be empty");
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

    protected boolean isFallbackToSystemLocale() {
        return this.fallbackToSystemLocale;
    }

    public void setCacheSeconds(int cacheSeconds) {
        this.cacheMillis = cacheSeconds * 1000;
    }

    public void setCacheMillis(long cacheMillis) {
        this.cacheMillis = cacheMillis;
    }

    protected long getCacheMillis() {
        return this.cacheMillis;
    }
}

