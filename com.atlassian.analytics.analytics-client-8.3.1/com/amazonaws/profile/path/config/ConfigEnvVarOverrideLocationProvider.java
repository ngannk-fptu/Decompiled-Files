/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.profile.path.config;

import com.amazonaws.annotation.SdkInternalApi;
import com.amazonaws.profile.path.AwsProfileFileLocationProvider;
import java.io.File;

@SdkInternalApi
public class ConfigEnvVarOverrideLocationProvider
implements AwsProfileFileLocationProvider {
    @Override
    public File getLocation() {
        String overrideLocation = System.getenv("AWS_CONFIG_FILE");
        if (overrideLocation != null) {
            return new File(overrideLocation);
        }
        return null;
    }
}

