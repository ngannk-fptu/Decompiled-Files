/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cmpt.check.base.CheckResult
 *  com.atlassian.cmpt.check.base.Checker
 *  org.apache.commons.collections.CollectionUtils
 *  org.slf4j.Logger
 */
package com.atlassian.migration.agent.service.check.space;

import com.atlassian.cmpt.check.base.CheckResult;
import com.atlassian.cmpt.check.base.Checker;
import com.atlassian.migration.agent.entity.CloudSite;
import com.atlassian.migration.agent.logging.ContextLoggerFactory;
import com.atlassian.migration.agent.service.PreflightErrorCode;
import com.atlassian.migration.agent.service.SpaceConflict;
import com.atlassian.migration.agent.service.check.space.SpaceConflictContext;
import com.atlassian.migration.agent.service.cloud.CloudSiteService;
import com.atlassian.migration.agent.service.confluence.ConfluenceCloudService;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;

public class SpaceConflictChecker
implements Checker<SpaceConflictContext> {
    private static final Logger log = ContextLoggerFactory.getLogger(SpaceConflictChecker.class);
    private static final String VIOLATIONS_KEY = "violations";
    private final CloudSiteService cloudSiteService;
    private final ConfluenceCloudService confluenceCloudService;

    public SpaceConflictChecker(CloudSiteService cloudSiteService, ConfluenceCloudService confluenceCloudService) {
        this.cloudSiteService = cloudSiteService;
        this.confluenceCloudService = confluenceCloudService;
    }

    public CheckResult check(SpaceConflictContext ctx) {
        if (CollectionUtils.isEmpty(ctx.spaceKeys)) {
            return new CheckResult(true);
        }
        Optional<CloudSite> cloudSite = this.cloudSiteService.getByCloudId(ctx.cloudId);
        if (!cloudSite.isPresent()) {
            PreflightErrorCode errorCode = PreflightErrorCode.CLOUD_ERROR;
            log.error("Error code- {} : {}. Cloud id: {}", new Object[]{errorCode.getCode(), errorCode.getMessage(), ctx.cloudId});
            return Checker.buildCheckResultWithExecutionError((int)errorCode.getCode());
        }
        try {
            Set<SpaceConflict> spaceConflictsResult = this.confluenceCloudService.getConflictingSpaces(cloudSite.get(), ctx.spaceKeys);
            List spaceConflicts = spaceConflictsResult.stream().sorted(Comparator.comparing(space -> space.key)).collect(Collectors.toList());
            return new CheckResult(spaceConflicts.isEmpty(), Collections.singletonMap(VIOLATIONS_KEY, spaceConflicts));
        }
        catch (RuntimeException e) {
            log.error("Error executing space keys conflict check.", (Throwable)e);
            return Checker.buildCheckResultWithExecutionError((int)PreflightErrorCode.SPACE_CONFLICTS_CHECK_ERROR.getCode());
        }
    }

    static List<SpaceConflict> retrieveConflictingSpaces(Map<String, Object> details) {
        return details.getOrDefault(VIOLATIONS_KEY, Collections.emptyList());
    }
}

