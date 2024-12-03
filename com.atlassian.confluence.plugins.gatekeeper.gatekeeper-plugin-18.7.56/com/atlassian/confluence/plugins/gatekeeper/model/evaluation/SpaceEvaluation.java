/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 */
package com.atlassian.confluence.plugins.gatekeeper.model.evaluation;

import com.atlassian.confluence.plugins.gatekeeper.evaluator.EvaluationExpiryChecker;
import com.atlassian.confluence.plugins.gatekeeper.evaluator.Evaluator;
import com.atlassian.confluence.plugins.gatekeeper.evaluator.SpaceEvaluator;
import com.atlassian.confluence.plugins.gatekeeper.evaluator.cache.EvaluatorCacheHolder;
import com.atlassian.confluence.plugins.gatekeeper.model.evaluation.Evaluation;
import com.atlassian.confluence.plugins.gatekeeper.model.evaluation.EvaluationLevel;
import com.atlassian.confluence.plugins.gatekeeper.model.evaluation.EvaluationOutput;
import com.atlassian.confluence.plugins.gatekeeper.model.owner.OwnerType;
import com.atlassian.confluence.plugins.gatekeeper.model.page.TinyPage;
import com.atlassian.confluence.plugins.gatekeeper.model.space.TinySpace;
import com.atlassian.confluence.plugins.gatekeeper.service.ConfluenceService;
import com.atlassian.sal.api.transaction.TransactionTemplate;

public class SpaceEvaluation
extends Evaluation {
    private TinyPage page;
    private TinySpace space;

    public SpaceEvaluation(OwnerType evaluationOwnerType, EvaluationOutput evaluationOutput, TinySpace space) {
        this(evaluationOwnerType, evaluationOutput, space, null);
    }

    public SpaceEvaluation(OwnerType evaluationOwnerType, EvaluationOutput evaluationOutput, TinySpace space, TinyPage page) {
        super(EvaluationLevel.EVALUATE_SPACE, evaluationOwnerType, evaluationOutput);
        this.page = page;
        this.space = space;
    }

    @Override
    public Evaluator createEvaluator(TransactionTemplate transactionTemplate, ConfluenceService confluenceService, EvaluatorCacheHolder evaluatorCacheHolder, EvaluationExpiryChecker evaluationExpiryChecker) {
        return new SpaceEvaluator(this, transactionTemplate, confluenceService, evaluatorCacheHolder, evaluationExpiryChecker);
    }

    public TinyPage getPage() {
        return this.page;
    }

    public TinySpace getSpace() {
        return this.space;
    }
}

