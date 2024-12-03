/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkProtectedApi
 *  software.amazon.awssdk.utils.SystemSetting
 */
package software.amazon.awssdk.services.s3;

import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.utils.SystemSetting;

@SdkProtectedApi
public enum S3SystemSetting implements SystemSetting
{
    AWS_S3_USE_ARN_REGION("aws.s3UseArnRegion", null),
    AWS_S3_DISABLE_MULTIREGION_ACCESS_POINTS("aws.s3DisableMultiRegionAccessPoints", null);

    private final String systemProperty;
    private final String defaultValue;

    private S3SystemSetting(String systemProperty, String defaultValue) {
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

