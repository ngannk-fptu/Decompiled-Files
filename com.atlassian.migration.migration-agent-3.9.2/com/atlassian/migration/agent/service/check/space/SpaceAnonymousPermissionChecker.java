/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cmpt.check.base.CheckResult
 *  com.atlassian.cmpt.check.base.Checker
 *  com.google.common.collect.ImmutableMap
 *  lombok.Generated
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.migration.agent.service.check.space;

import com.atlassian.cmpt.check.base.CheckResult;
import com.atlassian.cmpt.check.base.Checker;
import com.atlassian.migration.agent.service.PreflightErrorCode;
import com.atlassian.migration.agent.service.check.CheckResultFileManager;
import com.atlassian.migration.agent.service.check.space.SpaceAnonymousPermissionContext;
import com.atlassian.migration.agent.store.impl.SpacePermissionStore;
import com.google.common.collect.ImmutableMap;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import lombok.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SpaceAnonymousPermissionChecker
implements Checker<SpaceAnonymousPermissionContext> {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(SpaceAnonymousPermissionChecker.class);
    static final String SPACE_ANONYMOUS_PERMISSIONS_FILE_PREFIX = "SpacesWithAnonymousPermissions";
    private final SpacePermissionStore spacePermissionStore;
    private final CheckResultFileManager checkResultFileManager;

    public SpaceAnonymousPermissionChecker(SpacePermissionStore spacePermissionStore, CheckResultFileManager checkResultFileManager) {
        this.spacePermissionStore = spacePermissionStore;
        this.checkResultFileManager = checkResultFileManager;
    }

    public CheckResult check(SpaceAnonymousPermissionContext ctx) {
        log.info("Running space anonymous permissions check for {} spaces", (Object)ctx.spaceKeys.size());
        try {
            List<String> spaceKeysWithAnonPermissions = this.spacePermissionStore.getSpacesWithAnonymousPermissions(ctx.spaceKeys);
            if (spaceKeysWithAnonPermissions.isEmpty()) {
                return new CheckResult(true, Collections.singletonMap("violationsCount", 0));
            }
            String path = this.checkResultFileManager.writeToJsonFile(SPACE_ANONYMOUS_PERMISSIONS_FILE_PREFIX, spaceKeysWithAnonPermissions);
            return new CheckResult(false, (Map)ImmutableMap.of((Object)"violationsCount", (Object)spaceKeysWithAnonPermissions.size(), (Object)"path", (Object)path));
        }
        catch (Exception e) {
            log.error("Error executing space anonymous permissions check.", (Throwable)e);
            return Checker.buildCheckResultWithExecutionError((int)PreflightErrorCode.GENERIC_ERROR.getCode());
        }
    }

    public static Integer retrieveSpaceWithAnonymousAccessCount(Map<String, Object> details) {
        return Integer.valueOf(details.getOrDefault("violationsCount", 0).toString());
    }
}

