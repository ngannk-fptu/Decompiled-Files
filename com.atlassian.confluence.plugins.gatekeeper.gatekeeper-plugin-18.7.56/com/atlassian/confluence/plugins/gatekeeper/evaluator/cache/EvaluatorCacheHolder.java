/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.gatekeeper.evaluator.cache;

import com.atlassian.confluence.plugins.gatekeeper.evaluator.cache.EvaluatorCache;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import javax.annotation.Nullable;
import org.springframework.stereotype.Component;

@Component
public class EvaluatorCacheHolder {
    private final AtomicReference<EvaluatorCache> evaluatorCache = new AtomicReference();
    private final AtomicBoolean fullUpdateRequested = new AtomicBoolean();

    @Nullable
    public EvaluatorCache getEvaluatorCache() {
        return this.evaluatorCache.get();
    }

    public void setEvaluatorCache(EvaluatorCache evaluatorCache) {
        this.evaluatorCache.set(evaluatorCache);
        this.requestFullUpdate(false);
    }

    public void reset() {
        this.evaluatorCache.set(null);
        this.requestFullUpdate(false);
    }

    public boolean isFullUpdateRequested() {
        return this.fullUpdateRequested.get();
    }

    public void requestFullUpdate(boolean required) {
        this.fullUpdateRequested.set(required);
    }
}

