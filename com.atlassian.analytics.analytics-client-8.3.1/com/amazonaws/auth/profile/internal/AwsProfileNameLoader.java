/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.auth.profile.internal;

import com.amazonaws.annotation.Immutable;
import com.amazonaws.annotation.SdkInternalApi;
import com.amazonaws.util.StringUtils;

@SdkInternalApi
@Immutable
public class AwsProfileNameLoader {
    public static final String DEFAULT_PROFILE_NAME = "default";
    public static final String AWS_PROFILE_ENVIRONMENT_VARIABLE = "AWS_PROFILE";
    public static final String AWS_PROFILE_SYSTEM_PROPERTY = "aws.profile";
    public static final AwsProfileNameLoader INSTANCE = new AwsProfileNameLoader();

    private AwsProfileNameLoader() {
    }

    public final String loadProfileName() {
        String profileEnvVarOverride = this.getEnvProfileName();
        if (!StringUtils.isNullOrEmpty(profileEnvVarOverride)) {
            return profileEnvVarOverride;
        }
        String profileSysPropOverride = this.getSysPropertyProfileName();
        if (!StringUtils.isNullOrEmpty(profileSysPropOverride)) {
            return profileSysPropOverride;
        }
        return DEFAULT_PROFILE_NAME;
    }

    private String getSysPropertyProfileName() {
        return StringUtils.trim(System.getProperty(AWS_PROFILE_SYSTEM_PROPERTY));
    }

    private String getEnvProfileName() {
        return StringUtils.trim(System.getenv(AWS_PROFILE_ENVIRONMENT_VARIABLE));
    }
}

