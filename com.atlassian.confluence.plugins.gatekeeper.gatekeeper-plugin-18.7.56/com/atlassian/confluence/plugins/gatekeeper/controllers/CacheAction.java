/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ConfluenceActionSupport
 */
package com.atlassian.confluence.plugins.gatekeeper.controllers;

import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.plugins.gatekeeper.evaluator.cache.EvaluatorCache;
import com.atlassian.confluence.plugins.gatekeeper.evaluator.cache.EvaluatorCacheHolder;

public class CacheAction
extends ConfluenceActionSupport {
    private EvaluatorCacheHolder evaluatorCacheHolder;
    private String cacheAction = "";
    private String memory;

    public String manageCache() throws Exception {
        switch (this.cacheAction) {
            case "evict": {
                this.evictCache();
                break;
            }
            case "size": {
                this.calculateSize();
            }
        }
        return "success";
    }

    private String evictCache() {
        this.evaluatorCacheHolder.requestFullUpdate(true);
        return "success";
    }

    private String calculateSize() {
        EvaluatorCache cache = this.evaluatorCacheHolder.getEvaluatorCache();
        this.memory = cache.getMemoryUsage();
        return "success";
    }

    public void setCacheAction(String cacheAction) {
        this.cacheAction = cacheAction;
    }

    public String getMemory() {
        return this.memory;
    }
}

