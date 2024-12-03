/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cmpt.check.base.CheckResult
 *  com.atlassian.cmpt.check.base.Checker
 *  com.atlassian.cmpt.domain.Edition
 *  org.slf4j.Logger
 */
package com.atlassian.migration.agent.service.check.edition;

import com.atlassian.cmpt.check.base.CheckResult;
import com.atlassian.cmpt.check.base.Checker;
import com.atlassian.cmpt.domain.Edition;
import com.atlassian.migration.agent.entity.CloudSite;
import com.atlassian.migration.agent.logging.ContextLoggerFactory;
import com.atlassian.migration.agent.service.PreflightErrorCode;
import com.atlassian.migration.agent.service.check.edition.CloudPremiumEditionContext;
import com.atlassian.migration.agent.service.cloud.CloudSiteService;
import com.atlassian.migration.agent.service.user.CloudEditionCheckResponse;
import com.atlassian.migration.agent.service.user.RetryingUsersMigrationService;
import com.atlassian.migration.agent.service.user.UsersMigrationService;
import java.util.Collections;
import java.util.Optional;
import org.slf4j.Logger;

public class CloudPremiumEditionChecker
implements Checker<CloudPremiumEditionContext> {
    private static final Logger log = ContextLoggerFactory.getLogger(CloudPremiumEditionChecker.class);
    static final String EDITION_KEY = "edition";
    private final CloudSiteService cloudSiteService;
    private final UsersMigrationService usersMigrationService;

    public CloudPremiumEditionChecker(CloudSiteService cloudSiteService, RetryingUsersMigrationService usersMigrationService) {
        this.cloudSiteService = cloudSiteService;
        this.usersMigrationService = usersMigrationService;
    }

    public CheckResult check(CloudPremiumEditionContext ctx) {
        Optional<CloudSite> cloudSite = this.cloudSiteService.getByCloudId(ctx.getCloudId());
        if (!cloudSite.isPresent()) {
            PreflightErrorCode errorCode = PreflightErrorCode.CLOUD_ERROR;
            log.error("Cloud Edition check failed with errorCode: {} and message: {}", (Object)errorCode.getCode(), (Object)errorCode.getMessage());
            return Checker.buildCheckResultWithExecutionError((int)errorCode.getCode());
        }
        try {
            CloudEditionCheckResponse cloudEditionCheckResponse = this.usersMigrationService.getCloudEditionCheck(cloudSite.get().getContainerToken());
            Edition cloudEdition = cloudEditionCheckResponse.edition;
            if (cloudEdition != Edition.PREMIUM) {
                return new CheckResult(false, Collections.singletonMap(EDITION_KEY, cloudEdition.getKey()));
            }
            return new CheckResult(true);
        }
        catch (Exception exception) {
            log.error("Error executing cloud edition check.", (Throwable)exception);
            return Checker.buildCheckResultWithExecutionError((int)PreflightErrorCode.CLOUD_EDITION_CHECK_ERROR.getCode());
        }
    }
}

