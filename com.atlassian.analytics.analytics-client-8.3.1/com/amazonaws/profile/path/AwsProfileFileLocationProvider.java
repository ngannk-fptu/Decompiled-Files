/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.profile.path;

import com.amazonaws.annotation.SdkInternalApi;
import com.amazonaws.profile.path.AwsProfileFileLocationProviderChain;
import com.amazonaws.profile.path.config.ConfigEnvVarOverrideLocationProvider;
import com.amazonaws.profile.path.config.SharedConfigDefaultLocationProvider;
import com.amazonaws.profile.path.cred.CredentialsDefaultLocationProvider;
import com.amazonaws.profile.path.cred.CredentialsEnvVarOverrideLocationProvider;
import com.amazonaws.profile.path.cred.CredentialsLegacyConfigLocationProvider;
import java.io.File;

@SdkInternalApi
public interface AwsProfileFileLocationProvider {
    public static final AwsProfileFileLocationProvider DEFAULT_CREDENTIALS_LOCATION_PROVIDER = new AwsProfileFileLocationProviderChain(new CredentialsEnvVarOverrideLocationProvider(), new CredentialsDefaultLocationProvider(), new CredentialsLegacyConfigLocationProvider());
    public static final AwsProfileFileLocationProvider DEFAULT_CONFIG_LOCATION_PROVIDER = new AwsProfileFileLocationProviderChain(new ConfigEnvVarOverrideLocationProvider(), new SharedConfigDefaultLocationProvider());

    public File getLocation();
}

