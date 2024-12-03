/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package com.amazonaws.profile.path.cred;

import com.amazonaws.annotation.SdkInternalApi;
import com.amazonaws.profile.path.AwsDirectoryBasePathProvider;
import java.io.File;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@SdkInternalApi
public class CredentialsLegacyConfigLocationProvider
extends AwsDirectoryBasePathProvider {
    private static final Log LOG = LogFactory.getLog(CredentialsLegacyConfigLocationProvider.class);
    private static final String LEGACY_CONFIG_PROFILES_FILENAME = "config";

    @Override
    public File getLocation() {
        File legacyConfigProfiles = new File(this.getAwsDirectory(), LEGACY_CONFIG_PROFILES_FILENAME);
        if (legacyConfigProfiles.exists() && legacyConfigProfiles.isFile()) {
            LOG.warn((Object)("Found the legacy config profiles file at [" + legacyConfigProfiles.getAbsolutePath() + "]. Please move it to the latest default location [~/.aws/credentials]."));
            return legacyConfigProfiles;
        }
        return null;
    }
}

