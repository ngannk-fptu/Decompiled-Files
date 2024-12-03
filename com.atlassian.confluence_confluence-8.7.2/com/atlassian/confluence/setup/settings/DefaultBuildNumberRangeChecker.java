/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.PluginParseException
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.setup.settings;

import com.atlassian.confluence.setup.settings.BuildNumberRangeChecker;
import com.atlassian.plugin.PluginParseException;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultBuildNumberRangeChecker
implements BuildNumberRangeChecker {
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultBuildNumberRangeChecker.class);

    @Override
    public boolean checkifBuildNumberInRange(String currentBuildNumber, Integer minBuildNumber, Integer maxBuildNumber) {
        Optional<Integer> parsedBuildNumber = this.parseBuildNumber(currentBuildNumber);
        if (parsedBuildNumber.isEmpty()) {
            LOGGER.info("Cannot parse Confluence build number: {}. Ignoring restriction.", (Object)currentBuildNumber);
            return true;
        }
        return this.isBuildNumberNotLessThanMin(parsedBuildNumber.get(), minBuildNumber) && this.isBuildNumberNotGreaterThanMax(parsedBuildNumber.get(), maxBuildNumber);
    }

    @Override
    public Optional<Integer> parseBuildNumberOrFail(String buildNumber, String exceptionMessage) {
        if (StringUtils.isEmpty((CharSequence)buildNumber)) {
            return Optional.empty();
        }
        Optional<Integer> parsedBuildNumber = this.parseBuildNumber(buildNumber);
        if (parsedBuildNumber.isPresent()) {
            return parsedBuildNumber;
        }
        throw new PluginParseException(exceptionMessage);
    }

    private boolean isBuildNumberNotLessThanMin(int buildNumber, Integer minBuildNumber) {
        return minBuildNumber == null || buildNumber >= minBuildNumber;
    }

    private boolean isBuildNumberNotGreaterThanMax(int buildNumber, Integer maxBuildNumber) {
        return maxBuildNumber == null || buildNumber <= maxBuildNumber;
    }

    private Optional<Integer> parseBuildNumber(String buildNumber) {
        try {
            return Optional.of(Integer.parseInt(buildNumber));
        }
        catch (NumberFormatException e) {
            return Optional.empty();
        }
    }
}

