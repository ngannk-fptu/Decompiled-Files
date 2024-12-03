/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.gatekeeper.evaluator;

import com.atlassian.confluence.plugins.gatekeeper.dto.TinyOwnerDto;
import com.atlassian.confluence.plugins.gatekeeper.evaluator.EvaluationExpiryChecker;
import com.atlassian.confluence.plugins.gatekeeper.evaluator.SpaceEvaluator;
import com.atlassian.confluence.plugins.gatekeeper.evaluator.cache.EvaluatorCacheHolder;
import com.atlassian.confluence.plugins.gatekeeper.exception.EvaluatorCacheNotInitializedException;
import com.atlassian.confluence.plugins.gatekeeper.model.evaluation.WhoCanViewEvaluation;
import com.atlassian.confluence.plugins.gatekeeper.model.evaluation.result.WhoCanViewResult;
import com.atlassian.confluence.plugins.gatekeeper.model.owner.TinyOwner;
import com.atlassian.confluence.plugins.gatekeeper.service.ConfluenceService;
import java.util.concurrent.TimeoutException;
import javax.annotation.Nullable;
import org.slf4j.LoggerFactory;

public class WhoCanViewEvaluator
extends SpaceEvaluator {
    public WhoCanViewEvaluator(WhoCanViewEvaluation evaluation, ConfluenceService confluenceService, EvaluatorCacheHolder evaluatorCacheHolder, EvaluationExpiryChecker evaluationExpiryChecker) {
        super(evaluation, null, confluenceService, evaluatorCacheHolder, evaluationExpiryChecker);
        this.logger = LoggerFactory.getLogger(WhoCanViewEvaluator.class);
    }

    @Nullable
    public WhoCanViewResult getUsers(int start, int count) throws EvaluatorCacheNotInitializedException, InterruptedException, TimeoutException {
        int last;
        this.prepareEvaluation();
        int max = this.getVisibleOwnerCount() - 1;
        if (start > max) {
            start = max / count * count;
            last = max;
        } else {
            last = Math.min(start + count - 1, max);
        }
        return this.evaluateOwners(start, last, this.getVisibleOwnerCount());
    }

    private WhoCanViewResult evaluateOwners(int first, int last, int totalVisibleUsersCount) {
        int i;
        int size = last - first + 1;
        this.logger.debug("who can view: from: " + first + " to: " + last + " total: " + totalVisibleUsersCount + " size: " + size);
        WhoCanViewResult result = new WhoCanViewResult(totalVisibleUsersCount, size, this.confluenceService.getHelpLink("gatekeeper.who-can-view.dialog"));
        int ownerIndex = 0;
        for (i = 0; i < first; ++i) {
            ownerIndex = this.owners.nextVisible(ownerIndex) + 1;
        }
        this.logger.debug("First index: " + ownerIndex + " from: " + first + " to: " + last);
        for (i = first; i <= last; ++i) {
            ownerIndex = this.owners.nextVisible(ownerIndex);
            TinyOwner owner = (TinyOwner)this.owners.get(ownerIndex++);
            result.add(new TinyOwnerDto(owner, this.confluenceService.getUserAvatarUrl(owner.getName())));
        }
        return result;
    }
}

