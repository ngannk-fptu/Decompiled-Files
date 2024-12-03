/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.Cache
 *  com.atlassian.cache.CacheFactory
 *  com.atlassian.cache.CacheManager
 *  com.atlassian.cache.CacheSettingsBuilder
 *  com.atlassian.json.jsonorg.JSONObject
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  javax.servlet.ServletRequest
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.previews.jwt;

import com.atlassian.cache.Cache;
import com.atlassian.cache.CacheFactory;
import com.atlassian.cache.CacheManager;
import com.atlassian.cache.CacheSettingsBuilder;
import com.atlassian.confluence.plugins.previews.jwt.JwtTokenService;
import com.atlassian.json.jsonorg.JSONObject;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import javax.servlet.ServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class JwtLogHitService {
    static final String CACHE_NAME = "com.atlassian.confluence.plugins.previews.jwt.JwtUsageLogFilter";
    static final int CACHE_DURATION_SECONDS = 60;
    private final JwtTokenService jwtTokenService;
    private final CacheFactory cacheFactory;
    private final Cache<String, Boolean> cache;

    @Autowired
    public JwtLogHitService(JwtTokenService jwtTokenService, @ComponentImport CacheManager cacheManager) {
        this.jwtTokenService = jwtTokenService;
        this.cacheFactory = (CacheFactory)Objects.requireNonNull(cacheManager);
        this.cache = this.createCache();
    }

    private Cache<String, Boolean> createCache() {
        return this.cacheFactory.getCache(CACHE_NAME, null, new CacheSettingsBuilder().remote().expireAfterWrite(60L, TimeUnit.SECONDS).build());
    }

    private String getCacheKey(String user, String jwtId) {
        return String.format("%s,%s", user, jwtId);
    }

    public Optional<Boolean> isInCache(String userKey, String jwtId) {
        String cacheKey = this.getCacheKey(userKey, jwtId);
        return Optional.ofNullable((Boolean)this.cache.get((Object)cacheKey));
    }

    public void logHit(ServletRequest request) {
        JSONObject payload = this.jwtTokenService.extractJWTPayload(request);
        if (payload == null || !payload.has("jti") || !payload.has("userKey")) {
            return;
        }
        String userKey = payload.getString("userKey");
        String jwtId = payload.getString("jti");
        this.cache.put((Object)this.getCacheKey(userKey, jwtId), (Object)true);
    }
}

