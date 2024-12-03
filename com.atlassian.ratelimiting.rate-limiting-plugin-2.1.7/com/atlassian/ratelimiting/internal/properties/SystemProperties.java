/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  org.apache.commons.lang3.BooleanUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.util.StringUtils
 */
package com.atlassian.ratelimiting.internal.properties;

import java.util.Collections;
import java.util.Set;
import java.util.function.Function;
import javax.annotation.Nonnull;
import org.apache.commons.lang3.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

public class SystemProperties {
    public static final String WHITELISTED_URL_PATTERNS_PROPERTY_KEY = "com.atlassian.ratelimiting.whitelisted-url-patterns";
    public static final String WHITELISTED_OAUTH_CONSUMERS_PROPERTY_KEY = "com.atlassian.ratelimiting.whitelisted-oauth-consumers";
    public static final String EXEMPTION_LIMIT_PROPERTY_KEY = "com.atlassian.ratelimiting.exemptions.limit";
    public static final String ENABLE_PRE_AUTH_FILTER_PROPERTY_KEY = "com.atlassian.ratelimiting.enable-pre-auth-filter";
    private static final Logger logger = LoggerFactory.getLogger(SystemProperties.class);
    private static final int MAX_EXEMPTIONS = 50000;

    private static <T> T getOrDefaultValue(String propertyKey, Function<String, T> translator, T defaultValue) {
        try {
            String value = System.getProperty(propertyKey);
            if (null != value) {
                T translatedValue = translator.apply(value.trim());
                logger.debug("Using system-property for '{}'. Specified value: '{}'", (Object)propertyKey, translatedValue);
                return translatedValue;
            }
            logger.debug("System property '{}' not found", (Object)propertyKey);
        }
        catch (Exception e) {
            logger.debug("Unable to read in system property '{}', defaulting to '{}'. Problem: {}", new Object[]{propertyKey, defaultValue, e.getMessage()});
        }
        logger.debug("Defaulting system property '{}' to: '{}'", (Object)propertyKey, defaultValue);
        return defaultValue;
    }

    public Set<String> getWhitelistedUrlPatterns() {
        return SystemProperties.getOrDefaultValue(WHITELISTED_URL_PATTERNS_PROPERTY_KEY, StringUtils::commaDelimitedListToSet, Collections.emptySet());
    }

    public Set<String> getWhitelistedOAuthConsumers() {
        return SystemProperties.getOrDefaultValue(WHITELISTED_OAUTH_CONSUMERS_PROPERTY_KEY, StringUtils::commaDelimitedListToSet, Collections.emptySet());
    }

    public int getExemptionLimit() {
        return SystemProperties.getOrDefaultValue(EXEMPTION_LIMIT_PROPERTY_KEY, Integer::parseInt, 50000);
    }

    public boolean isPreAuthFilterEnabled(boolean defaultValue) {
        return SystemProperties.getOrDefaultValue(ENABLE_PRE_AUTH_FILTER_PROPERTY_KEY, this::parseBooleanPropertyValue, defaultValue);
    }

    private boolean parseBooleanPropertyValue(@Nonnull String value) {
        return value.isEmpty() || BooleanUtils.toBoolean((String)value);
    }
}

