/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.cache.CacheManager
 *  com.atlassian.cache.CacheSettingsBuilder
 *  com.atlassian.cache.CachedReference
 *  com.atlassian.confluence.pages.SavableAttachment
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.google.common.collect.Streams
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.hipchat.emoticons.service;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.cache.CacheManager;
import com.atlassian.cache.CacheSettingsBuilder;
import com.atlassian.cache.CachedReference;
import com.atlassian.confluence.pages.SavableAttachment;
import com.atlassian.confluence.plugins.hipchat.emoticons.content.entity.CustomEmoticon;
import com.atlassian.confluence.plugins.hipchat.emoticons.exception.EmoticonException;
import com.atlassian.confluence.plugins.hipchat.emoticons.service.CustomEmoticonService;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.google.common.collect.Streams;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component(value="customEmoticonService")
public class CachingCustomEmoticonService
implements CustomEmoticonService {
    static final String CUSTOM_EMOTICON_CACHE = CachingCustomEmoticonService.class.getName() + ".Cache";
    static final String CUSTOM_EMOTICON_ID_CACHE = CachingCustomEmoticonService.class.getName() + ".ID.Cache";
    static final String CUSTOM_EMOTICON_NON_EXIST_CACHE = CachingCustomEmoticonService.class.getName() + ".Non.Exist.Cache";
    private final CustomEmoticonService delegate;
    private final CachedReference<Map<String, CustomEmoticon>> emoticonCacheReference;
    private final CachedReference<Map<String, Long>> emoticonIDCacheReference;
    private final CachedReference<List<String>> nonExistEmoticonShortcutCache;

    @Autowired
    public CachingCustomEmoticonService(CustomEmoticonService delegate, @ComponentImport CacheManager cacheManager) {
        this(delegate, CachingCustomEmoticonService.createCachedReference(delegate, cacheManager), CachingCustomEmoticonService.createIDCachedReference(cacheManager), CachingCustomEmoticonService.createNonExistCachedReference(cacheManager));
    }

    @VisibleForTesting
    CachingCustomEmoticonService(CustomEmoticonService delegate, CachedReference<Map<String, CustomEmoticon>> emoticonCacheReference, CachedReference<Map<String, Long>> emoticonIDCacheReference, CachedReference<List<String>> nonExistEmoticonShortcutCache) {
        this.delegate = delegate;
        this.emoticonCacheReference = emoticonCacheReference;
        this.emoticonIDCacheReference = emoticonIDCacheReference;
        this.nonExistEmoticonShortcutCache = nonExistEmoticonShortcutCache;
    }

    private static CachedReference<List<String>> createNonExistCachedReference(@ComponentImport CacheManager cacheManager) {
        return cacheManager.getCachedReference(CUSTOM_EMOTICON_NON_EXIST_CACHE, () -> new ArrayList(), new CacheSettingsBuilder().replicateViaInvalidation().flushable().build());
    }

    private static CachedReference<Map<String, Long>> createIDCachedReference(@ComponentImport CacheManager cacheManager) {
        return cacheManager.getCachedReference(CUSTOM_EMOTICON_ID_CACHE, () -> new HashMap(), new CacheSettingsBuilder().replicateViaInvalidation().flushable().build());
    }

    private static CachedReference<Map<String, CustomEmoticon>> createCachedReference(CustomEmoticonService delegate, @ComponentImport CacheManager cacheManager) {
        return cacheManager.getCachedReference(CUSTOM_EMOTICON_CACHE, () -> CachingCustomEmoticonService.prepareEmoticonsMap(delegate.list()), new CacheSettingsBuilder().replicateViaInvalidation().flushable().build());
    }

    static Map<String, CustomEmoticon> prepareEmoticonsMap(Iterable<CustomEmoticon> emoticons) {
        return Streams.stream(emoticons).collect(Collectors.toMap(CustomEmoticon::getShortcut, Function.identity(), (shortcut1, shortcut2) -> shortcut1));
    }

    @Override
    public CustomEmoticon create(CustomEmoticon emoticon, SavableAttachment emoticonData) throws EmoticonException {
        CustomEmoticon customEmoticon = this.delegate.create(emoticon, emoticonData);
        this.resetCaches();
        return customEmoticon;
    }

    private void resetCaches() {
        this.emoticonCacheReference.reset();
        this.emoticonIDCacheReference.reset();
        this.nonExistEmoticonShortcutCache.reset();
    }

    @Override
    public Iterable<CustomEmoticon> list() {
        return ((Map)this.emoticonCacheReference.get()).values();
    }

    @Override
    public Collection<CustomEmoticon> findByShortcut(String ... shortcuts) {
        return Stream.of(shortcuts).map(shortcut -> (CustomEmoticon)((Map)this.emoticonCacheReference.get()).get(shortcut)).filter(Objects::nonNull).collect(Collectors.toList());
    }

    @Override
    public Map<String, Long> findIDByShortcut(String ... shortcuts) {
        List<String> missingCachedShortcutToQuery;
        ((Map)this.emoticonCacheReference.get()).entrySet().stream().forEach(entry -> {
            CustomEmoticon customEmoticon = (CustomEmoticon)entry.getValue();
            ((Map)this.emoticonIDCacheReference.get()).put(customEmoticon.getShortcut(), customEmoticon.getId());
        });
        HashMap<String, Long> returnResult = new HashMap<String, Long>();
        ArrayList missingCachedShortcut = new ArrayList();
        Stream.of(shortcuts).forEach(shortcut -> {
            if (((Map)this.emoticonIDCacheReference.get()).containsKey(shortcut)) {
                returnResult.put((String)shortcut, (Long)((Map)this.emoticonIDCacheReference.get()).get(shortcut));
            } else {
                missingCachedShortcut.add(shortcut);
            }
        });
        if (!missingCachedShortcut.isEmpty() && !(missingCachedShortcutToQuery = missingCachedShortcut.stream().filter(shortcut -> !((List)this.nonExistEmoticonShortcutCache.get()).contains(shortcut)).collect(Collectors.toList())).isEmpty()) {
            Map<String, Long> missingCacheMap = this.delegate.findIDByShortcut(missingCachedShortcutToQuery.toArray(new String[0]));
            missingCacheMap.entrySet().forEach(entry -> {
                ((Map)this.emoticonIDCacheReference.get()).put((String)entry.getKey(), (Long)entry.getValue());
                returnResult.put((String)entry.getKey(), (Long)entry.getValue());
            });
            List notExistShortcuts = missingCachedShortcutToQuery.stream().filter(missingShortcut -> !missingCacheMap.containsKey(missingShortcut)).collect(Collectors.toList());
            ((List)this.nonExistEmoticonShortcutCache.get()).addAll(notExistShortcuts);
        }
        return returnResult;
    }

    @Override
    public void delete(String shortcut) {
        this.delegate.delete(shortcut);
        this.resetCaches();
    }

    @Override
    public void cleanupInvalidEmoticon() {
        this.delegate.cleanupInvalidEmoticon();
        this.emoticonCacheReference.reset();
    }
}

