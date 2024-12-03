/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 */
package com.atlassian.confluence.plugins.gatekeeper.model.evaluation;

import com.atlassian.confluence.plugins.gatekeeper.evaluator.EvaluationExpiryChecker;
import com.atlassian.confluence.plugins.gatekeeper.evaluator.Evaluator;
import com.atlassian.confluence.plugins.gatekeeper.evaluator.WhoCanViewEvaluator;
import com.atlassian.confluence.plugins.gatekeeper.evaluator.cache.EvaluatorCacheHolder;
import com.atlassian.confluence.plugins.gatekeeper.model.evaluation.EvaluationOutput;
import com.atlassian.confluence.plugins.gatekeeper.model.evaluation.SpaceEvaluation;
import com.atlassian.confluence.plugins.gatekeeper.model.owner.OwnerType;
import com.atlassian.confluence.plugins.gatekeeper.model.page.TinyPage;
import com.atlassian.confluence.plugins.gatekeeper.model.permission.Permission;
import com.atlassian.confluence.plugins.gatekeeper.model.space.TinySpace;
import com.atlassian.confluence.plugins.gatekeeper.service.ConfluenceService;
import com.atlassian.sal.api.transaction.TransactionTemplate;

public class WhoCanViewEvaluation
extends SpaceEvaluation {
    public WhoCanViewEvaluation(OwnerType evaluationOwnerType, EvaluationOutput evaluationOutput, TinySpace space) {
        this(evaluationOwnerType, evaluationOutput, space, null);
    }

    public WhoCanViewEvaluation(OwnerType evaluationOwnerType, EvaluationOutput evaluationOutput, TinySpace space, TinyPage page) {
        super(evaluationOwnerType, evaluationOutput, space, page);
        this.setExcludeOwnersNoPermissions(true);
        this.setExcludeDisabled(true);
        this.setEvaluatedPermissions(Permission.VIEW_SPACE_PERMISSION);
    }

    public WhoCanViewEvaluator createEvaluator(ConfluenceService confluenceService, EvaluatorCacheHolder evaluatorCacheHolder, EvaluationExpiryChecker evaluationExpiryChecker) {
        return new WhoCanViewEvaluator(this, confluenceService, evaluatorCacheHolder, evaluationExpiryChecker);
    }

    @Override
    public Evaluator createEvaluator(TransactionTemplate transactionTemplate, ConfluenceService confluenceService, EvaluatorCacheHolder evaluatorCacheHolder, EvaluationExpiryChecker evaluationExpiryChecker) {
        throw new UnsupportedOperationException("Can't create WhoCanViewEvaluator!");
    }
}

