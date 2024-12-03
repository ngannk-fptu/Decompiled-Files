/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.troubleshooting.preupgrade;

import com.atlassian.troubleshooting.stp.hercules.regex.cacheables.SavedExternalResource;
import java.net.MalformedURLException;
import java.net.URL;
import javax.annotation.Nonnull;

public class PupDataResource
implements SavedExternalResource {
    public static final PupDataResource INSTANCE = new PupDataResource();
    private static final String DEFAULT_URL = "https://puds.prod.atl-paas.net/rest/v1/upgrade/info";
    private static final String SAVED_FILENAME = SavedExternalResource.hashFilename("https://puds.prod.atl-paas.net/rest/v1/upgrade/info", "info.json");
    private final URL cachedUrl;

    private PupDataResource() {
        try {
            this.cachedUrl = new URL(System.getProperty("com.atlassian.troubleshooting.preupgrade.client.pup.data.url", DEFAULT_URL));
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
        return SAVED_FILENAME;
    }
}

