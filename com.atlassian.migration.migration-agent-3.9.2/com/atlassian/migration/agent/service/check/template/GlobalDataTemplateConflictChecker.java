/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cmpt.check.base.CheckResult
 *  com.atlassian.cmpt.check.base.Checker
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableSet
 *  lombok.Generated
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.migration.agent.service.check.template;

import com.atlassian.cmpt.check.base.CheckResult;
import com.atlassian.cmpt.check.base.Checker;
import com.atlassian.migration.agent.entity.GlobalEntityType;
import com.atlassian.migration.agent.service.NonSpaceTemplateConflictsInfo;
import com.atlassian.migration.agent.service.PreflightErrorCode;
import com.atlassian.migration.agent.service.check.CheckResultFileManager;
import com.atlassian.migration.agent.service.check.template.GlobalDataTemplateConflictContext;
import com.atlassian.migration.agent.service.confluence.ConfluenceCloudService;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import lombok.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GlobalDataTemplateConflictChecker
implements Checker<GlobalDataTemplateConflictContext> {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(GlobalDataTemplateConflictChecker.class);
    private static Set<GlobalEntityType> applicableGlobalEntityTypes = ImmutableSet.of((Object)((Object)GlobalEntityType.SYSTEM_TEMPLATES), (Object)((Object)GlobalEntityType.GLOBAL_TEMPLATES), (Object)((Object)GlobalEntityType.GLOBAL_SYSTEM_TEMPLATES));
    static final String GLOBAL_DATA_TEMPLATES_FILE_PREFIX = "GlobalTemplatesWithConflicts";
    private final CheckResultFileManager checkResultFileManager;
    private final ConfluenceCloudService confluenceCloudService;

    public GlobalDataTemplateConflictChecker(ConfluenceCloudService confluenceCloudService, CheckResultFileManager checkResultFileManager) {
        this.confluenceCloudService = confluenceCloudService;
        this.checkResultFileManager = checkResultFileManager;
    }

    public CheckResult check(GlobalDataTemplateConflictContext ctx) {
        try {
            GlobalEntityType globalEntityType = GlobalEntityType.valueOf(ctx.getTemplateType());
            NonSpaceTemplateConflictsInfo conflictsInfo = this.confluenceCloudService.getNonSpaceTemplateConflictsInfo(globalEntityType, ctx.cloudId);
            if (conflictsInfo.getConflicts().isEmpty()) {
                return new CheckResult(true, Collections.singletonMap("violationsCount", 0));
            }
            String path = this.checkResultFileManager.writeToJsonFile(GLOBAL_DATA_TEMPLATES_FILE_PREFIX, conflictsInfo.getConflicts());
            return new CheckResult(false, (Map)ImmutableMap.of((Object)"violationsCount", (Object)conflictsInfo.getConflicts().size(), (Object)"path", (Object)path));
        }
        catch (Exception exception) {
            log.error("Error executing global data template conflict check.", (Throwable)exception);
            return Checker.buildCheckResultWithExecutionError((int)PreflightErrorCode.GLOBAL_SYSTEM_TEMPLATE_CHECK_ERROR.getCode());
        }
    }

    public static boolean isApplicable(GlobalEntityType globalEntityType) {
        return applicableGlobalEntityTypes.contains((Object)globalEntityType);
    }
}

