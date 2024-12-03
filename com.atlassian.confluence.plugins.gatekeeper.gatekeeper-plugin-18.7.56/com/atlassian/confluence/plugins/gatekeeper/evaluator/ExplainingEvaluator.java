/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.gatekeeper.evaluator;

import com.atlassian.confluence.plugins.gatekeeper.evaluator.EvaluationExpiryChecker;
import com.atlassian.confluence.plugins.gatekeeper.evaluator.SpaceEvaluator;
import com.atlassian.confluence.plugins.gatekeeper.evaluator.cache.EvaluatorCacheHolder;
import com.atlassian.confluence.plugins.gatekeeper.exception.EvaluatorCacheNotInitializedException;
import com.atlassian.confluence.plugins.gatekeeper.model.evaluation.ExplainingEvaluation;
import com.atlassian.confluence.plugins.gatekeeper.model.owner.OwnerType;
import com.atlassian.confluence.plugins.gatekeeper.model.owner.TinyAnonymous;
import com.atlassian.confluence.plugins.gatekeeper.model.owner.TinyOwner;
import com.atlassian.confluence.plugins.gatekeeper.model.permission.ExplainedPermissionSet;
import com.atlassian.confluence.plugins.gatekeeper.model.permission.ExplanationFormatter;
import com.atlassian.confluence.plugins.gatekeeper.model.permission.PermissionSet;
import com.atlassian.confluence.plugins.gatekeeper.model.space.TinySpace;
import com.atlassian.confluence.plugins.gatekeeper.service.ConfluenceService;
import com.atlassian.confluence.plugins.gatekeeper.util.BitSetList;
import com.atlassian.confluence.plugins.gatekeeper.util.ExplanationMapper;
import java.util.concurrent.TimeoutException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;

public class ExplainingEvaluator
extends SpaceEvaluator {
    private ExplanationFormatter explanationFormatter;
    private ExplanationMapper gatekeeperUtil;

    public ExplainingEvaluator(ExplainingEvaluation evaluation, ConfluenceService confluenceService, EvaluatorCacheHolder evaluatorCacheHolder, EvaluationExpiryChecker evaluationExpiryChecker) {
        super(evaluation, null, confluenceService, evaluatorCacheHolder, evaluationExpiryChecker);
        this.logger = LoggerFactory.getLogger(ExplainingEvaluator.class);
        this.explanationFormatter = evaluation.getExplanationFormatter();
        this.gatekeeperUtil = evaluation.getGatekeeperUtil();
    }

    public PermissionSet explainPermissions() throws EvaluatorCacheNotInitializedException, InterruptedException, TimeoutException {
        this.prepareEvaluation();
        ExplainingEvaluation explainingEvaluation = (ExplainingEvaluation)this.evaluation;
        String explainedOwnerName = explainingEvaluation.getExplainedOwnerName();
        if (this.spaces != null && this.spaces.getVisibleSize() == 1 && StringUtils.isNotEmpty((CharSequence)explainedOwnerName)) {
            TinyOwner owner = null;
            if (explainingEvaluation.isEvaluatingGroups()) {
                owner = this.evaluatorCache.getGroup(explainedOwnerName);
            } else if (explainingEvaluation.isEvaluatingUsers()) {
                owner = this.evaluatorCache.getUser(explainedOwnerName);
            } else if (explainingEvaluation.isEvaluatingAnonymous()) {
                owner = TinyAnonymous.ANONYMOUS;
            }
            if (owner != null) {
                this.owners = new BitSetList<TinyOwner>(owner);
                PermissionSet[] result = this.evaluateOwner(owner);
                return result[0];
            }
        }
        return null;
    }

    @Override
    protected PermissionSet createPermissionSet() {
        return new ExplainedPermissionSet(this.ownerType, this.explanationFormatter, this.page, this.gatekeeperUtil);
    }

    @Override
    void evaluateOwner(TinySpace space, TinyOwner owner, PermissionSet effectivePermissions, boolean quickViewAccessCheck) {
        super.evaluateOwner(space, owner, effectivePermissions, quickViewAccessCheck);
        if (OwnerType.TYPE_GROUP.equals((Object)this.ownerType) && this.confluenceService.hasGroupParents(owner.getName())) {
            ((ExplainedPermissionSet)effectivePermissions).setGroupHasParents(true);
        }
    }
}

