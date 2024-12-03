/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.Cache
 *  com.atlassian.cache.CacheManager
 *  com.atlassian.cache.CacheSettings
 *  com.atlassian.cache.CacheSettingsBuilder
 *  org.springframework.beans.factory.DisposableBean
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.gatekeeper.service;

import com.atlassian.cache.Cache;
import com.atlassian.cache.CacheManager;
import com.atlassian.cache.CacheSettings;
import com.atlassian.cache.CacheSettingsBuilder;
import com.atlassian.confluence.plugins.gatekeeper.concurrent.ManagedThreadPoolExecutor;
import com.atlassian.confluence.plugins.gatekeeper.evaluator.Evaluator;
import com.atlassian.confluence.plugins.gatekeeper.model.evaluation.result.PreEvaluationResult;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

@Component
public class AddonGlobal
implements InitializingBean,
DisposableBean {
    private static final int CORE_POOL_SIZE = 4;
    private static final int MAX_POOL_SIZE = 4;
    private static final int THREAD_KEEP_ALIVE_TIME = 60;
    private final CacheManager cacheManager;
    private ManagedThreadPoolExecutor<Evaluator, PreEvaluationResult> evaluationThreadPoolExecutor;
    private Cache<String, Evaluator> evaluatorJobs;

    public AddonGlobal(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    public void afterPropertiesSet() {
        this.evaluationThreadPoolExecutor = new ManagedThreadPoolExecutor(4, 4, 60);
        CacheSettings cacheSettings = new CacheSettingsBuilder().local().flushable().expireAfterWrite(5L, TimeUnit.MINUTES).maxEntries(1000).build();
        this.evaluatorJobs = this.cacheManager.getCache(AddonGlobal.class.getName() + ".evaluatorJobs", null, cacheSettings);
    }

    public void destroy() {
        this.evaluationThreadPoolExecutor.shutdown();
        this.evaluationThreadPoolExecutor.cleanup();
        this.evaluatorJobs.removeAll();
    }

    public ManagedThreadPoolExecutor<Evaluator, PreEvaluationResult> getEvaluationThreadPoolExecutor() {
        return this.evaluationThreadPoolExecutor;
    }

    public Cache<String, Evaluator> getEvaluatorJobs() {
        return this.evaluatorJobs;
    }
}

