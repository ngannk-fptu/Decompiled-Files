/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.gatekeeper.evaluator;

import com.atlassian.confluence.plugins.gatekeeper.evaluator.EvaluationExpiryChecker;
import com.atlassian.confluence.plugins.gatekeeper.evaluator.Evaluator;
import com.atlassian.confluence.plugins.gatekeeper.evaluator.cache.EvaluatorCacheHolder;
import com.atlassian.confluence.plugins.gatekeeper.exception.EvaluatorCacheNotInitializedException;
import com.atlassian.confluence.plugins.gatekeeper.model.evaluation.Evaluation;
import com.atlassian.confluence.plugins.gatekeeper.model.evaluation.SpaceEvaluation;
import com.atlassian.confluence.plugins.gatekeeper.model.evaluation.result.ProcessingPhase;
import com.atlassian.confluence.plugins.gatekeeper.model.owner.TinyUser;
import com.atlassian.confluence.plugins.gatekeeper.model.page.PageRestriction;
import com.atlassian.confluence.plugins.gatekeeper.model.space.TinySpace;
import com.atlassian.confluence.plugins.gatekeeper.service.ConfluenceService;
import com.atlassian.confluence.plugins.gatekeeper.util.BitSetList;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import java.util.Collections;
import java.util.List;
import org.slf4j.LoggerFactory;

public class SpaceEvaluator
extends Evaluator {
    private List<PageRestriction> viewRestrictions;
    private List<PageRestriction> editRestrictions;

    public SpaceEvaluator(Evaluation evaluation, TransactionTemplate transactionTemplate, ConfluenceService confluenceService, EvaluatorCacheHolder evaluatorCacheHolder, EvaluationExpiryChecker evaluationExpiryChecker) {
        super(evaluation, transactionTemplate, confluenceService, evaluatorCacheHolder, evaluationExpiryChecker);
        this.logger = LoggerFactory.getLogger(SpaceEvaluator.class);
    }

    @Override
    public void initEvaluator() throws EvaluatorCacheNotInitializedException {
        SpaceEvaluation spaceEvaluation = (SpaceEvaluation)this.evaluation;
        TinySpace space = spaceEvaluation.getSpace();
        this.page = spaceEvaluation.getPage();
        this.evaluatorCache = this.evaluatorCacheHolder.getEvaluatorCache();
        if (this.evaluatorCache == null) {
            this.processingState.setPhase(ProcessingPhase.FAILED);
            throw new EvaluatorCacheNotInitializedException();
        }
        this.processingState.setPhase(ProcessingPhase.READY);
        if (this.page != null) {
            this.viewRestrictions = this.confluenceService.getViewRestrictions(this.page.getId());
            this.editRestrictions = this.confluenceService.getEditRestrictions(this.page.getId());
        }
        this.spaces = new BitSetList<TinySpace>(space);
        this.owners = new BitSetList<TinyUser>(this.evaluatorCache.getUsers(), this.evaluation.isExcludeOwnersNoPermissions());
        List userFilters = this.evaluation.getUserFilters();
        if (userFilters != null) {
            this.owners.applyFilters(userFilters);
        }
    }

    @Override
    public List<PageRestriction> getViewRestrictions() {
        return this.viewRestrictions != null ? this.viewRestrictions : Collections.emptyList();
    }

    @Override
    public List<PageRestriction> getEditRestrictions() {
        return this.editRestrictions != null ? this.editRestrictions : Collections.emptyList();
    }
}

