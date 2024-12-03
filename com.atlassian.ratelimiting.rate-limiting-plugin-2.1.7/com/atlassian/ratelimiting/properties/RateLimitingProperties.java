/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.ratelimiting.properties;

import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import org.apache.commons.lang3.StringUtils;

public interface RateLimitingProperties {
    @Nonnull
    public Set<String> getWhitelistedUrlPatterns();

    @Nonnull
    public Set<String> getWhitelistedOAuthConsumers();

    public boolean isPreAuthFilterEnabled();

    default public void reloadCache() {
    }

    public static Set<String> sanitizeTrimmingWhitespace(Set<String> ... values) {
        return Stream.of(values).filter(Objects::nonNull).flatMap(p -> p.stream().filter(Objects::nonNull).map(String::trim)).filter(StringUtils::isNotBlank).collect(Collectors.toCollection(TreeSet::new));
    }
}

