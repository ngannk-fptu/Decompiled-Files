/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.config.ApplicationConfiguration
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.webresource.QueryParams
 *  com.atlassian.plugin.webresource.condition.UrlReadingCondition
 *  com.atlassian.plugin.webresource.url.UrlBuilder
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.extra.calendar3.condition;

import com.atlassian.config.ApplicationConfiguration;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.webresource.QueryParams;
import com.atlassian.plugin.webresource.condition.UrlReadingCondition;
import com.atlassian.plugin.webresource.url.UrlBuilder;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BuildNumberUrlReadingCondition
implements UrlReadingCondition {
    private static final Logger log = LoggerFactory.getLogger(BuildNumberUrlReadingCondition.class);
    static final String BUILD_NUMBER_QUERY_PARAM_KEY = "build-number";
    static final String MIN_BUILD_NUMBER_CONFIG_PARAM_KEY = "minBuildNumber";
    static final String MAX_BUILD_NUMBER_CONFIG_PARAM_KEY = "maxBuildNumber";
    private final ApplicationConfiguration applicationConfig;
    private int minBuildNumber;
    private int maxBuildNumber;

    public BuildNumberUrlReadingCondition(ApplicationConfiguration applicationConfig) {
        this.applicationConfig = applicationConfig;
    }

    public void init(Map<String, String> params) {
        this.minBuildNumber = this.parseBuildNumber(MIN_BUILD_NUMBER_CONFIG_PARAM_KEY, params, 0);
        this.maxBuildNumber = this.parseBuildNumber(MAX_BUILD_NUMBER_CONFIG_PARAM_KEY, params, Integer.MAX_VALUE);
    }

    public void addToUrl(UrlBuilder urlBuilder) {
        String buildNumber = this.applicationConfig.getBuildNumber();
        if (StringUtils.isNotEmpty(buildNumber)) {
            urlBuilder.addToQueryString(BUILD_NUMBER_QUERY_PARAM_KEY, buildNumber);
        }
    }

    public boolean shouldDisplay(QueryParams params) {
        int buildNumber;
        if (StringUtils.isBlank(params.get(BUILD_NUMBER_QUERY_PARAM_KEY))) {
            return true;
        }
        try {
            buildNumber = Integer.parseInt(params.get(BUILD_NUMBER_QUERY_PARAM_KEY));
        }
        catch (NumberFormatException e) {
            log.info("Cannot parse Confluence build number: {}. Ignoring restriction.");
            return true;
        }
        return this.minBuildNumber <= buildNumber && buildNumber <= this.maxBuildNumber;
    }

    private int parseBuildNumber(String paramName, Map<String, String> params, int defaultValue) {
        if (!params.containsKey(paramName)) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(params.get(paramName));
        }
        catch (NumberFormatException e) {
            throw new PluginParseException("Invalid " + paramName, (Throwable)e);
        }
    }
}

