/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.util.i18n.I18NBean
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 */
package com.atlassian.confluence.plugins.gatekeeper.model.evaluation;

import com.atlassian.confluence.plugins.gatekeeper.evaluator.EvaluationExpiryChecker;
import com.atlassian.confluence.plugins.gatekeeper.evaluator.Evaluator;
import com.atlassian.confluence.plugins.gatekeeper.evaluator.ExplainingEvaluator;
import com.atlassian.confluence.plugins.gatekeeper.evaluator.cache.EvaluatorCacheHolder;
import com.atlassian.confluence.plugins.gatekeeper.model.evaluation.EvaluationOutput;
import com.atlassian.confluence.plugins.gatekeeper.model.evaluation.SpaceEvaluation;
import com.atlassian.confluence.plugins.gatekeeper.model.owner.OwnerType;
import com.atlassian.confluence.plugins.gatekeeper.model.page.TinyPage;
import com.atlassian.confluence.plugins.gatekeeper.model.permission.ExplanationFormatter;
import com.atlassian.confluence.plugins.gatekeeper.model.space.TinySpace;
import com.atlassian.confluence.plugins.gatekeeper.service.ConfluenceService;
import com.atlassian.confluence.plugins.gatekeeper.util.ExplanationMapper;
import com.atlassian.confluence.util.i18n.I18NBean;
import com.atlassian.sal.api.transaction.TransactionTemplate;

public class ExplainingEvaluation
extends SpaceEvaluation {
    private String explainedOwnerName;
    private String contextPath;
    private I18NBean i18nBean;
    private ExplanationFormatter explanationFormatter;
    private ExplanationMapper gatekeeperUtil;

    public ExplainingEvaluation(OwnerType evaluationOwnerType, TinySpace space, TinyPage page, I18NBean i18nBean, String contextPath) {
        this(evaluationOwnerType, null, space, page, i18nBean, contextPath);
    }

    public ExplainingEvaluation(OwnerType evaluationOwnerType, String explainedOwnerName, TinySpace space, TinyPage page, I18NBean i18nBean, String contextPath) {
        super(evaluationOwnerType, EvaluationOutput.ADMIN_VIEW, space, page);
        this.explainedOwnerName = explainedOwnerName;
        this.i18nBean = i18nBean;
        this.contextPath = contextPath;
        this.gatekeeperUtil = new ExplanationMapper(i18nBean);
        this.explanationFormatter = new ExplanationFormatter(this);
    }

    public ExplainingEvaluator createEvaluator(ConfluenceService confluenceService, EvaluatorCacheHolder evaluatorCacheHolder, EvaluationExpiryChecker evaluationExpiryChecker) {
        return new ExplainingEvaluator(this, confluenceService, evaluatorCacheHolder, evaluationExpiryChecker);
    }

    @Override
    public Evaluator createEvaluator(TransactionTemplate transactionTemplate, ConfluenceService confluenceService, EvaluatorCacheHolder evaluatorCacheHolder, EvaluationExpiryChecker evaluationExpiryChecker) {
        throw new UnsupportedOperationException("Can't create ExplainingEvaluator!");
    }

    public String getExplainedOwnerName() {
        return this.isEvaluatingAnonymous() ? "<anonymous>" : this.explainedOwnerName;
    }

    public String getContextPath() {
        return this.contextPath;
    }

    public I18NBean getI18nBean() {
        return this.i18nBean;
    }

    public ExplanationFormatter getExplanationFormatter() {
        return this.explanationFormatter;
    }

    public ExplanationMapper getGatekeeperUtil() {
        return this.gatekeeperUtil;
    }
}

