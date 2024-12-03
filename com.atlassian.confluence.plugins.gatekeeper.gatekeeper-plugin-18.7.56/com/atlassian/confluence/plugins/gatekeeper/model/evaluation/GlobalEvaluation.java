/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 */
package com.atlassian.confluence.plugins.gatekeeper.model.evaluation;

import com.atlassian.confluence.plugins.gatekeeper.evaluator.EvaluationExpiryChecker;
import com.atlassian.confluence.plugins.gatekeeper.evaluator.Evaluator;
import com.atlassian.confluence.plugins.gatekeeper.evaluator.GlobalEvaluator;
import com.atlassian.confluence.plugins.gatekeeper.evaluator.cache.EvaluatorCacheHolder;
import com.atlassian.confluence.plugins.gatekeeper.model.evaluation.Evaluation;
import com.atlassian.confluence.plugins.gatekeeper.model.evaluation.EvaluationLevel;
import com.atlassian.confluence.plugins.gatekeeper.model.evaluation.EvaluationOutput;
import com.atlassian.confluence.plugins.gatekeeper.model.filter.SpaceFilter;
import com.atlassian.confluence.plugins.gatekeeper.model.owner.OwnerType;
import com.atlassian.confluence.plugins.gatekeeper.service.ConfluenceService;
import com.atlassian.sal.api.transaction.TransactionTemplate;

public class GlobalEvaluation
extends Evaluation {
    private SpaceFilter spaceFilter;

    public GlobalEvaluation(OwnerType evaluationOwnerType, EvaluationOutput evaluationOutput) {
        super(EvaluationLevel.EVALUATE_GLOBAL, evaluationOwnerType, evaluationOutput);
    }

    public SpaceFilter getSpaceFilter() {
        return this.spaceFilter;
    }

    public void setSpaceFilter(SpaceFilter spaceFilter) {
        this.spaceFilter = spaceFilter;
    }

    @Override
    public Evaluator createEvaluator(TransactionTemplate transactionTemplate, ConfluenceService confluenceService, EvaluatorCacheHolder evaluatorCacheHolder, EvaluationExpiryChecker evaluationExpiryChecker) {
        return new GlobalEvaluator(this, transactionTemplate, confluenceService, evaluatorCacheHolder, evaluationExpiryChecker);
    }
}

