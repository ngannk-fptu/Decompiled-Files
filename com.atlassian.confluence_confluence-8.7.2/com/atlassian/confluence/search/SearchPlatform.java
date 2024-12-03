/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.config.ApplicationConfiguration
 *  org.apache.commons.lang3.EnumUtils
 */
package com.atlassian.confluence.search;

import com.atlassian.config.ApplicationConfiguration;
import com.atlassian.confluence.impl.util.OptionalUtils;
import java.util.Objects;
import java.util.Optional;
import org.apache.commons.lang3.EnumUtils;

public enum SearchPlatform {
    OPENSEARCH,
    LUCENE;

    public static final String CONFIG_KEY = "search.platform";
    public static final SearchPlatform DEFAULT;

    public static SearchPlatform getSearchPlatform(ApplicationConfiguration applicationConfig) {
        Objects.requireNonNull(applicationConfig, "applicationConfig is required");
        return OptionalUtils.firstNonEmpty(() -> Optional.ofNullable(System.getProperty(CONFIG_KEY)), () -> Optional.ofNullable(applicationConfig.getProperty((Object)CONFIG_KEY)).map(String::valueOf)).map(SearchPlatform::parse).orElse(DEFAULT);
    }

    private static SearchPlatform parse(String str) {
        SearchPlatform platform = (SearchPlatform)EnumUtils.getEnumIgnoreCase(SearchPlatform.class, (String)str);
        if (platform == null) {
            throw new IllegalArgumentException("Invalid search platform: " + str);
        }
        return platform;
    }

    static {
        DEFAULT = LUCENE;
    }
}

