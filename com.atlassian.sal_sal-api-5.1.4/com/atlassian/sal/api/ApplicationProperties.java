/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicApi
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.sal.api;

import com.atlassian.annotations.PublicApi;
import com.atlassian.sal.api.UrlMode;
import java.io.File;
import java.nio.file.Path;
import java.util.Date;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@PublicApi
public interface ApplicationProperties {
    public static final String PLATFORM_BAMBOO = "bamboo";
    public static final String PLATFORM_BITBUCKET = "bitbucket";
    public static final String PLATFORM_CONFLUENCE = "conf";
    public static final String PLATFORM_CROWD = "crowd";
    public static final String PLATFORM_FECRU = "fisheye";
    public static final String PLATFORM_JIRA = "jira";
    public static final String PLATFORM_STASH = "stash";

    @Deprecated
    public String getBaseUrl();

    @Nonnull
    public String getBaseUrl(UrlMode var1);

    @Nonnull
    public String getDisplayName();

    @Nonnull
    public String getPlatformId();

    @Nonnull
    public String getVersion();

    @Nonnull
    public Date getBuildDate();

    @Nonnull
    public String getBuildNumber();

    @Nullable
    @Deprecated
    public File getHomeDirectory();

    @Nonnull
    public Optional<Path> getLocalHomeDirectory();

    @Nonnull
    public Optional<Path> getSharedHomeDirectory();

    @Deprecated
    public String getPropertyValue(String var1);

    @Nonnull
    public String getApplicationFileEncoding();
}

