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
import com.atlassian.confluence.plugins.gatekeeper.model.evaluation.GlobalEvaluation;
import com.atlassian.confluence.plugins.gatekeeper.model.evaluation.result.ProcessingPhase;
import com.atlassian.confluence.plugins.gatekeeper.model.filter.GroupFilter;
import com.atlassian.confluence.plugins.gatekeeper.model.filter.SpaceFilter;
import com.atlassian.confluence.plugins.gatekeeper.model.owner.TinyGroup;
import com.atlassian.confluence.plugins.gatekeeper.model.owner.TinyUser;
import com.atlassian.confluence.plugins.gatekeeper.model.page.PageRestriction;
import com.atlassian.confluence.plugins.gatekeeper.model.space.TinySpace;
import com.atlassian.confluence.plugins.gatekeeper.service.ConfluenceService;
import com.atlassian.confluence.plugins.gatekeeper.util.BitSetList;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import java.util.Collections;
import java.util.List;
import org.slf4j.LoggerFactory;

public class GlobalEvaluator
extends Evaluator {
    public GlobalEvaluator(Evaluation evaluation, TransactionTemplate transactionTemplate, ConfluenceService confluenceService, EvaluatorCacheHolder evaluatorCacheHolder, EvaluationExpiryChecker evaluationExpiryChecker) {
        super(evaluation, transactionTemplate, confluenceService, evaluatorCacheHolder, evaluationExpiryChecker);
        this.logger = LoggerFactory.getLogger(GlobalEvaluator.class);
    }

    @Override
    public void initEvaluator() throws EvaluatorCacheNotInitializedException {
        this.logger.debug("Starting global evaluation");
        this.evaluatorCache = this.evaluatorCacheHolder.getEvaluatorCache();
        if (this.evaluatorCache == null) {
            this.processingState.setPhase(ProcessingPhase.FAILED);
            throw new EvaluatorCacheNotInitializedException();
        }
        this.processingState.setPhase(ProcessingPhase.READY);
        this.spaces = new BitSetList<TinySpace>(this.evaluatorCache.getSpaces(), this.evaluation.isExcludeSpacesNoPermissions());
        SpaceFilter spaceFilter = ((GlobalEvaluation)this.evaluation).getSpaceFilter();
        if (spaceFilter != null && !spaceFilter.isEmptyFilter()) {
            this.spaces.applyFilter(spaceFilter);
        }
        if (this.evaluation.isEvaluatingUsers()) {
            this.owners = new BitSetList<TinyUser>(this.evaluatorCache.getUsers(), this.evaluation.isExcludeOwnersNoPermissions());
            List userFilters = this.evaluation.getUserFilters();
            if (userFilters != null) {
                this.owners.applyFilters(userFilters);
            }
        } else if (this.evaluation.isEvaluatingGroups()) {
            this.owners = new BitSetList<TinyGroup>(this.evaluatorCache.getGroups(), this.evaluation.isExcludeOwnersNoPermissions());
            GroupFilter groupFilter = this.evaluation.getGroupFilter();
            if (groupFilter != null && !groupFilter.isEmptyFilter()) {
                this.owners.applyFilter(groupFilter);
            }
        }
    }

    @Override
    public List<PageRestriction> getViewRestrictions() {
        return Collections.emptyList();
    }

    @Override
    public List<PageRestriction> getEditRestrictions() {
        return Collections.emptyList();
    }
}

