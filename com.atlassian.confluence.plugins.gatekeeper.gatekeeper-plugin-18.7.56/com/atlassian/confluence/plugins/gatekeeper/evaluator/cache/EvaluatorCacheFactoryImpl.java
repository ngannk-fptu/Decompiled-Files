/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.persistence.EntityManagerProvider
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.crowd.embedded.api.CrowdDirectoryService
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.gatekeeper.evaluator.cache;

import com.atlassian.confluence.persistence.EntityManagerProvider;
import com.atlassian.confluence.plugins.gatekeeper.evaluator.cache.EvaluatorCache;
import com.atlassian.confluence.plugins.gatekeeper.evaluator.cache.EvaluatorCacheFactory;
import com.atlassian.confluence.plugins.gatekeeper.evaluator.cache.EvaluatorCacheHolder;
import com.atlassian.confluence.plugins.gatekeeper.evaluator.cache.EvaluatorCacheImpl;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.crowd.embedded.api.CrowdDirectoryService;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import org.springframework.stereotype.Component;

@Component
public class EvaluatorCacheFactoryImpl
implements EvaluatorCacheFactory {
    private final EntityManagerProvider entityManagerProvider;
    private final TransactionTemplate transactionTemplate;
    private final CrowdDirectoryService crowdDirectoryService;
    private final EvaluatorCacheHolder evaluatorCacheHolder;
    private final UserAccessor userAccessor;

    public EvaluatorCacheFactoryImpl(@ComponentImport EntityManagerProvider entityManagerProvider, @ComponentImport TransactionTemplate transactionTemplate, @ComponentImport CrowdDirectoryService crowdDirectoryService, EvaluatorCacheHolder evaluatorCacheHolder, UserAccessor userAccessor) {
        this.entityManagerProvider = entityManagerProvider;
        this.transactionTemplate = transactionTemplate;
        this.crowdDirectoryService = crowdDirectoryService;
        this.evaluatorCacheHolder = evaluatorCacheHolder;
        this.userAccessor = userAccessor;
    }

    @Override
    public EvaluatorCache createInstance() {
        return (EvaluatorCache)this.transactionTemplate.execute(() -> new EvaluatorCacheImpl(this.entityManagerProvider, this.crowdDirectoryService, this.evaluatorCacheHolder, this.userAccessor));
    }
}

