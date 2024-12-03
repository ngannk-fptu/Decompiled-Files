/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.profile.path.cred;

import com.amazonaws.annotation.SdkInternalApi;
import com.amazonaws.profile.path.AwsDirectoryBasePathProvider;
import java.io.File;

@SdkInternalApi
public class CredentialsDefaultLocationProvider
extends AwsDirectoryBasePathProvider {
    private static final String DEFAULT_CREDENTIAL_PROFILES_FILENAME = "credentials";

    @Override
    public File getLocation() {
        File credentialProfiles = new File(this.getAwsDirectory(), DEFAULT_CREDENTIAL_PROFILES_FILENAME);
        if (credentialProfiles.exists() && credentialProfiles.isFile()) {
            return credentialProfiles;
        }
        return null;
    }
}

