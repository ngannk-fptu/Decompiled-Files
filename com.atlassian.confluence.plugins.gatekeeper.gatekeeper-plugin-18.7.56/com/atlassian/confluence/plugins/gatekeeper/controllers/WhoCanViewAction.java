/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ConfluenceActionSupport
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.fasterxml.jackson.databind.ObjectMapper
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.gatekeeper.controllers;

import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.plugins.gatekeeper.evaluator.EvaluationExpiryChecker;
import com.atlassian.confluence.plugins.gatekeeper.evaluator.WhoCanViewEvaluator;
import com.atlassian.confluence.plugins.gatekeeper.evaluator.cache.EvaluatorCacheHolder;
import com.atlassian.confluence.plugins.gatekeeper.exception.EvaluatorCacheNotInitializedException;
import com.atlassian.confluence.plugins.gatekeeper.license.AddonLicenseManager;
import com.atlassian.confluence.plugins.gatekeeper.model.evaluation.EvaluationOutput;
import com.atlassian.confluence.plugins.gatekeeper.model.evaluation.WhoCanViewEvaluation;
import com.atlassian.confluence.plugins.gatekeeper.model.evaluation.result.WhoCanViewResult;
import com.atlassian.confluence.plugins.gatekeeper.model.filter.UserFilter;
import com.atlassian.confluence.plugins.gatekeeper.model.owner.OwnerType;
import com.atlassian.confluence.plugins.gatekeeper.model.page.TinyPage;
import com.atlassian.confluence.plugins.gatekeeper.model.space.TinySpace;
import com.atlassian.confluence.plugins.gatekeeper.service.ConfluenceService;
import com.atlassian.confluence.plugins.gatekeeper.util.ActionUtil;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WhoCanViewAction
extends ConfluenceActionSupport {
    private static final Logger logger = LoggerFactory.getLogger(WhoCanViewAction.class);
    private boolean licensed;
    private String spaceKey;
    private long pageId;
    private boolean ignoreRestrictions;
    private AddonLicenseManager licenseManager;
    private ConfluenceService confluenceService;
    private EvaluatorCacheHolder evaluatorCacheHolder;
    private int start;
    private int count;
    private String filter;
    private EvaluationExpiryChecker evaluationExpiryChecker;

    private boolean checkLicense() {
        this.licensed = this.licenseManager.getLicenseInfo().isValid();
        return this.licensed;
    }

    public String evaluate() throws IOException {
        WhoCanViewEvaluation evaluation;
        if (AuthenticatedUserThreadLocal.get() == null) {
            logger.warn("Anonymous users are not allowed to see PII");
            ActionUtil.sendForbiddenResponse("Anonymous users are not allowed!");
            return "none";
        }
        if (!this.checkLicense()) {
            logger.warn("Invalid license");
            ActionUtil.sendBadResponse("Gatekeeper's license is missing or expired!");
            return "none";
        }
        TinySpace tinySpace = this.confluenceService.getSpace(this.spaceKey);
        if (tinySpace == null) {
            ActionUtil.sendBadResponse("Space not found: " + this.spaceKey);
            return "none";
        }
        if (!this.confluenceService.canCurrentUserViewSpace(this.spaceKey)) {
            ActionUtil.sendForbiddenResponse("User has no view permission to space: " + this.spaceKey);
            return "none";
        }
        TinyPage tinyPage = this.confluenceService.getPage(this.spaceKey, this.pageId);
        if (tinyPage == null) {
            ActionUtil.sendBadResponse("Page not found: " + this.pageId + " in space " + this.spaceKey);
            return "none";
        }
        if (!this.confluenceService.canCurrentUserViewPage(this.spaceKey, this.pageId)) {
            ActionUtil.sendForbiddenResponse("User has no view permission to page: " + this.pageId + " in space " + this.spaceKey);
            return "none";
        }
        WhoCanViewEvaluation whoCanViewEvaluation = evaluation = this.ignoreRestrictions ? new WhoCanViewEvaluation(OwnerType.TYPE_USER, EvaluationOutput.WHO_CAN_VIEW, tinySpace) : new WhoCanViewEvaluation(OwnerType.TYPE_USER, EvaluationOutput.WHO_CAN_VIEW, tinySpace, tinyPage);
        if (StringUtils.isNotBlank((CharSequence)this.filter)) {
            evaluation.setUserFilter(new UserFilter(this.filter + "*"));
        }
        WhoCanViewEvaluator evaluator = evaluation.createEvaluator(this.confluenceService, this.evaluatorCacheHolder, this.evaluationExpiryChecker);
        ObjectMapper om = new ObjectMapper();
        try {
            WhoCanViewResult whoCanViewResult = evaluator.getUsers(this.start, this.count);
            String result = om.writeValueAsString((Object)whoCanViewResult);
            ActionUtil.sendJsonResponse(result);
        }
        catch (EvaluatorCacheNotInitializedException cacheNotInitializedException) {
            logger.debug("The EvaluatorCache is not ready yet: {}", (Throwable)cacheNotInitializedException);
            ActionUtil.sendNotFoundResponse();
        }
        catch (Exception e) {
            logger.error("Evaluation failed!", (Throwable)e);
            ActionUtil.sendBadResponse(e.getMessage());
        }
        return "none";
    }

    public void setLicensed(boolean licensed) {
        this.licensed = licensed;
    }

    public void setSpaceKey(String spaceKey) {
        this.spaceKey = spaceKey;
    }

    public void setPageId(long pageId) {
        this.pageId = pageId;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public void setLicenseManager(AddonLicenseManager licenseManager) {
        this.licenseManager = licenseManager;
    }

    public void setConfluenceService(ConfluenceService confluenceService) {
        this.confluenceService = confluenceService;
    }

    public void setIgnoreRestrictions(boolean ignoreRestrictions) {
        this.ignoreRestrictions = ignoreRestrictions;
    }

    public void setEvaluatorCacheHolder(EvaluatorCacheHolder evaluatorCacheHolder) {
        this.evaluatorCacheHolder = evaluatorCacheHolder;
    }

    public void setEvaluationExpiryChecker(EvaluationExpiryChecker evaluationExpiryChecker) {
        this.evaluationExpiryChecker = evaluationExpiryChecker;
    }
}

