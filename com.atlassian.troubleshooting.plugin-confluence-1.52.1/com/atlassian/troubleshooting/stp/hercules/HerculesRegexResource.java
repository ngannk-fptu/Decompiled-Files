/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.troubleshooting.stp.hercules;

import com.atlassian.troubleshooting.stp.hercules.regex.cacheables.SavedExternalResource;
import java.net.MalformedURLException;
import java.net.URL;
import javax.annotation.Nonnull;

public enum HerculesRegexResource implements SavedExternalResource
{
    BAMBOO_HERCULES_REGEX("bamboo_regex_v2.xml"),
    BITBUCKET_HERCULES_REGEX("stash_regex_v2.xml"),
    CONFLUENCE_HERCULES_REGEX("confluence_regex_v2.xml"),
    CROWD_HERCULES_REGEX("crowd_regex_v2.xml"),
    CRUCIBLE_HERCULES_REGEX("crucible_regex_v2.xml"),
    FISHEYE_HERCULES_REGEX("fisheye_regex_v2.xml"),
    JIRA_HERCULES_REGEX("jira_regex_v2.xml"),
    JIRA_SERVICE_DESK_HERCULES_REGEX("servicedesk_regex_v2.xml"),
    JIRA_SOFTWARE_HERCULES_REGEX("greenhopper_regex_v2.xml");

    private static final String BASE_URL = "https://confluence.atlassian.com/download/attachments/179443532/";
    private final URL cachedUrl;
    private final String localFilename;

    private HerculesRegexResource(String fileName) {
        try {
            this.cachedUrl = new URL(BASE_URL + fileName);
            this.localFilename = SavedExternalResource.hashFilename(this.cachedUrl.toString(), fileName);
        }
        catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @Nonnull
    public URL getCachedUrl() {
        return this.cachedUrl;
    }

    @Override
    public String getLocalFilename() {
        return this.localFilename;
    }
}

