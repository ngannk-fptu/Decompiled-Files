/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.plugin.descriptor.web.conditions;

import com.atlassian.confluence.plugin.descriptor.web.WebInterfaceContext;
import com.atlassian.confluence.plugin.descriptor.web.conditions.BaseConfluenceCondition;
import com.atlassian.confluence.setup.BootstrapManager;
import com.atlassian.confluence.setup.settings.BuildNumberRangeChecker;
import com.atlassian.confluence.setup.settings.DefaultBuildNumberRangeChecker;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;

public class BuildNumberCondition
extends BaseConfluenceCondition {
    private static final String MIN_BUILD_NUMBER_CONFIG_PARAM_KEY = "minBuildNumber";
    private static final String MAX_BUILD_NUMBER_CONFIG_PARAM_KEY = "maxBuildNumber";
    private BootstrapManager bootstrapManager;
    private BuildNumberRangeChecker buildNumberRangeChecker = new DefaultBuildNumberRangeChecker();
    private Integer minBuildNumber;
    private Integer maxBuildNumber;

    @Override
    public void init(Map<String, String> params) {
        this.minBuildNumber = this.buildNumberRangeChecker.parseBuildNumberOrFail(params.get(MIN_BUILD_NUMBER_CONFIG_PARAM_KEY), "Invalid min build number, check plugin configuration.").orElse(null);
        this.maxBuildNumber = this.buildNumberRangeChecker.parseBuildNumberOrFail(params.get(MAX_BUILD_NUMBER_CONFIG_PARAM_KEY), "Invalid max build number, check plugin configuration.").orElse(null);
    }

    @Override
    protected boolean shouldDisplay(WebInterfaceContext context) {
        String currentBuildNumber = this.bootstrapManager.getBuildNumber();
        return StringUtils.isBlank((CharSequence)currentBuildNumber) || this.buildNumberRangeChecker.checkifBuildNumberInRange(currentBuildNumber, this.minBuildNumber, this.maxBuildNumber);
    }

    public void setBootstrapManager(BootstrapManager bootstrapManager) {
        this.bootstrapManager = bootstrapManager;
    }

    public void setBuildNumberRangeChecker(BuildNumberRangeChecker buildNumberRangeChecker) {
        this.buildNumberRangeChecker = buildNumberRangeChecker;
    }
}

