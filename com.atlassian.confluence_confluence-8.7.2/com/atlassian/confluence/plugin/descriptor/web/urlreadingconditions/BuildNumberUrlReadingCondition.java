/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.webresource.QueryParams
 *  com.atlassian.plugin.webresource.condition.UrlReadingCondition
 *  com.atlassian.plugin.webresource.url.UrlBuilder
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.plugin.descriptor.web.urlreadingconditions;

import com.atlassian.confluence.setup.BootstrapManager;
import com.atlassian.confluence.setup.settings.BuildNumberRangeChecker;
import com.atlassian.plugin.webresource.QueryParams;
import com.atlassian.plugin.webresource.condition.UrlReadingCondition;
import com.atlassian.plugin.webresource.url.UrlBuilder;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;

public class BuildNumberUrlReadingCondition
implements UrlReadingCondition {
    private static final String BUILD_NUMBER_QUERY_PARAM_KEY = "build-number";
    private static final String MIN_BUILD_NUMBER_CONFIG_PARAM_KEY = "minBuildNumber";
    private static final String MAX_BUILD_NUMBER_CONFIG_PARAM_KEY = "maxBuildNumber";
    private final BootstrapManager bootstrapManager;
    private final BuildNumberRangeChecker buildNumberRangeChecker;
    private Integer minBuildNumber;
    private Integer maxBuildNumber;

    public BuildNumberUrlReadingCondition(BootstrapManager bootstrapManager, BuildNumberRangeChecker buildNumberRangeChecker) {
        this.bootstrapManager = bootstrapManager;
        this.buildNumberRangeChecker = buildNumberRangeChecker;
    }

    public void init(Map<String, String> params) {
        this.minBuildNumber = this.buildNumberRangeChecker.parseBuildNumberOrFail(params.get(MIN_BUILD_NUMBER_CONFIG_PARAM_KEY), "Invalid min build number, check plugin configuration.").orElse(null);
        this.maxBuildNumber = this.buildNumberRangeChecker.parseBuildNumberOrFail(params.get(MAX_BUILD_NUMBER_CONFIG_PARAM_KEY), "Invalid max build number, check plugin configuration.").orElse(null);
    }

    public void addToUrl(UrlBuilder urlBuilder) {
        String buildNumber = this.bootstrapManager.getBuildNumber();
        if (StringUtils.isNotEmpty((CharSequence)buildNumber)) {
            urlBuilder.addToQueryString(BUILD_NUMBER_QUERY_PARAM_KEY, buildNumber);
        }
    }

    public boolean shouldDisplay(QueryParams params) {
        String currentBuildNumber = params.get(BUILD_NUMBER_QUERY_PARAM_KEY);
        return StringUtils.isBlank((CharSequence)currentBuildNumber) || this.buildNumberRangeChecker.checkifBuildNumberInRange(currentBuildNumber, this.minBuildNumber, this.maxBuildNumber);
    }
}

