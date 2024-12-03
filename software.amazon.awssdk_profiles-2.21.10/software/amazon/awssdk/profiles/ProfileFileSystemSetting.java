/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkProtectedApi
 *  software.amazon.awssdk.utils.SystemSetting
 */
package software.amazon.awssdk.profiles;

import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.utils.SystemSetting;

@SdkProtectedApi
public enum ProfileFileSystemSetting implements SystemSetting
{
    AWS_CONFIG_FILE("aws.configFile", null),
    AWS_SHARED_CREDENTIALS_FILE("aws.sharedCredentialsFile", null),
    AWS_PROFILE("aws.profile", "default");

    private final String systemProperty;
    private final String defaultValue;

    private ProfileFileSystemSetting(String systemProperty, String defaultValue) {
        this.systemProperty = systemProperty;
        this.defaultValue = defaultValue;
    }

    public String property() {
        return this.systemProperty;
    }

    public String environmentVariable() {
        return this.name();
    }

    public String defaultValue() {
        return this.defaultValue;
    }
}

