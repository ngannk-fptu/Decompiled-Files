/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.cache.CacheManager
 *  com.atlassian.cache.CacheSettingsBuilder
 *  com.atlassian.cache.CachedReference
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.hipchat.emoticons.service;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.cache.CacheManager;
import com.atlassian.cache.CacheSettingsBuilder;
import com.atlassian.cache.CachedReference;
import com.atlassian.confluence.plugins.hipchat.emoticons.rest.AtlaskitEmoticonModel;
import com.atlassian.confluence.plugins.hipchat.emoticons.service.CachingTwitterEmoticonServiceHelper;
import com.atlassian.confluence.plugins.hipchat.emoticons.service.TwitterEmoticonService;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import java.util.Collection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component(value="twitterEmoticonService")
public class CachingTwitterEmoticonService
implements TwitterEmoticonService {
    static final String CUSTOM_EMOTICON_CACHE = CachingTwitterEmoticonService.class.getName() + ".Cache";
    private final CachedReference<CachingTwitterEmoticonServiceHelper> emoticonCacheReference;
    private final TwitterEmoticonService twitterEmoticonService;

    @Autowired
    public CachingTwitterEmoticonService(TwitterEmoticonService twitterEmoticonService, @ComponentImport CacheManager cacheManager) {
        this(twitterEmoticonService, CachingTwitterEmoticonService.createCachedReference(twitterEmoticonService, cacheManager));
    }

    @VisibleForTesting
    CachingTwitterEmoticonService(TwitterEmoticonService twitterEmoticonService, CachedReference<CachingTwitterEmoticonServiceHelper> emoticonCacheReference) {
        this.emoticonCacheReference = emoticonCacheReference;
        this.twitterEmoticonService = twitterEmoticonService;
    }

    private static CachedReference<CachingTwitterEmoticonServiceHelper> createCachedReference(TwitterEmoticonService delegate, @ComponentImport CacheManager cacheManager) {
        return cacheManager.getCachedReference(CUSTOM_EMOTICON_CACHE, () -> new CachingTwitterEmoticonServiceHelper(delegate.list()), new CacheSettingsBuilder().local().build());
    }

    CachingTwitterEmoticonServiceHelper getCachedHelper() {
        return (CachingTwitterEmoticonServiceHelper)this.emoticonCacheReference.get();
    }

    @Override
    public Collection<AtlaskitEmoticonModel> list() {
        return this.getCachedHelper().list();
    }

    @Override
    public AtlaskitEmoticonModel findById(String id) {
        return this.getCachedHelper().findById(id);
    }

    @Override
    public String getImageFileContent(AtlaskitEmoticonModel model) {
        return this.getCachedHelper().getSvgFileContent(model);
    }

    @Override
    public String getResourceUrl(AtlaskitEmoticonModel atlaskitEmoticonModel) {
        return this.twitterEmoticonService.getResourceUrl(atlaskitEmoticonModel);
    }
}

