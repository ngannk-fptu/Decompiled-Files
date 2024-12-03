/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.util.collections.CompositeMap
 */
package com.atlassian.confluence.setup.velocity;

import com.atlassian.confluence.cache.ThreadLocalCacheAccessor;
import com.atlassian.confluence.setup.velocity.VelocityContextItemProvider;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.util.collections.CompositeMap;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public final class ContextItemProviderChain
implements VelocityContextItemProvider {
    private static final String VELOCITY_CONTEXT_ITEM_PROVIDER_KEY_PREFIX = "VelocityContextItemProvider-";
    private static ThreadLocalCacheAccessor<String, Map<String, Object>> threadLocalCacheAccessor = ThreadLocalCacheAccessor.newInstance();
    private List<VelocityContextItemProvider> providers;

    @Override
    public Map<String, Object> getContextMap() {
        String userKey = Optional.ofNullable(AuthenticatedUserThreadLocal.get()).map(ConfluenceUser::getKey).map(Objects::toString).orElse("anon");
        return threadLocalCacheAccessor.getOrCompute(VELOCITY_CONTEXT_ITEM_PROVIDER_KEY_PREFIX + userKey, () -> {
            Map contextMap = Collections.emptyMap();
            for (VelocityContextItemProvider provider : this.providers) {
                contextMap = CompositeMap.of(provider.getContextMap(), contextMap);
            }
            return Collections.unmodifiableMap(contextMap);
        });
    }

    public void setProviders(List<VelocityContextItemProvider> providers) {
        this.providers = providers;
    }
}

