/*
 * Decompiled with CFR 0.152.
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

    @Override
    public String property() {
        return this.systemProperty;
    }

    @Override
    public String environmentVariable() {
        return this.name();
    }

    @Override
    public String defaultValue() {
        return this.defaultValue;
    }
}

