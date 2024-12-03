/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.CacheFactory
 *  com.atlassian.cache.CacheSettingsBuilder
 *  com.atlassian.cache.CachedReference
 *  javax.annotation.Nullable
 */
package com.atlassian.confluence.impl.themes;

import com.atlassian.cache.CacheFactory;
import com.atlassian.cache.CacheSettingsBuilder;
import com.atlassian.cache.CachedReference;
import com.atlassian.confluence.cache.CoreCache;
import com.atlassian.confluence.impl.cache.ReadThroughAtlassianCache;
import com.atlassian.confluence.impl.cache.ReadThroughCache;
import com.atlassian.confluence.impl.themes.ThemeKeyDao;
import java.util.Optional;
import javax.annotation.Nullable;

public class CachingThemeKeyDao
implements ThemeKeyDao {
    private final ThemeKeyDao delegate;
    private final CachedReference<Optional<String>> cachedGlobalThemeKey;
    private final ReadThroughCache<String, Optional<String>> cachedSpaceThemeKeys;

    public static CachingThemeKeyDao create(ThemeKeyDao delegate, CacheFactory cacheFactory) {
        return new CachingThemeKeyDao(delegate, CachingThemeKeyDao.createGlobalKeyCache(delegate, cacheFactory), CachingThemeKeyDao.createSpaceKeyCache(cacheFactory));
    }

    private static ReadThroughAtlassianCache<String, Optional<String>> createSpaceKeyCache(CacheFactory cacheFactory) {
        return ReadThroughAtlassianCache.create(cacheFactory, CoreCache.SPACE_THEME_KEY);
    }

    private static CachedReference<Optional<String>> createGlobalKeyCache(ThemeKeyDao delegate, CacheFactory cacheFactory) {
        return CoreCache.GLOBAL_THEME_KEY.resolve(name -> cacheFactory.getCachedReference(name, delegate::getGlobalThemeKey, new CacheSettingsBuilder().replicateViaInvalidation().build()));
    }

    CachingThemeKeyDao(ThemeKeyDao delegate, CachedReference<Optional<String>> cachedGlobalThemeKey, ReadThroughCache<String, Optional<String>> cachedSpaceThemeKeys) {
        this.delegate = delegate;
        this.cachedGlobalThemeKey = cachedGlobalThemeKey;
        this.cachedSpaceThemeKeys = cachedSpaceThemeKeys;
    }

    @Override
    public Optional<String> getGlobalThemeKey() {
        return (Optional)this.cachedGlobalThemeKey.get();
    }

    @Override
    public Optional<String> getSpaceThemeKey(@Nullable String spaceKey) {
        if (spaceKey == null) {
            return this.delegate.getSpaceThemeKey(null);
        }
        return this.cachedSpaceThemeKeys.get(spaceKey, () -> this.delegate.getSpaceThemeKey(spaceKey));
    }

    @Override
    public void setGlobalThemeKey(String themeKey) {
        this.delegate.setGlobalThemeKey(themeKey);
        this.cachedGlobalThemeKey.reset();
    }

    @Override
    public void setSpaceThemeKey(String spaceKey, String themeKey) {
        this.delegate.setSpaceThemeKey(spaceKey, themeKey);
        this.cachedSpaceThemeKeys.remove(spaceKey);
    }
}

