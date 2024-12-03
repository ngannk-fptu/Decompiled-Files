/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.regions.providers;

import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.core.SdkSystemSetting;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.regions.providers.AwsRegionProvider;

@SdkProtectedApi
public final class SystemSettingsRegionProvider
implements AwsRegionProvider {
    @Override
    public Region getRegion() throws SdkClientException {
        return SdkSystemSetting.AWS_REGION.getStringValue().map(Region::of).orElseThrow(this::exception);
    }

    private SdkClientException exception() {
        return SdkClientException.builder().message(String.format("Unable to load region from system settings. Region must be specified either via environment variable (%s) or  system property (%s).", SdkSystemSetting.AWS_REGION.environmentVariable(), SdkSystemSetting.AWS_REGION.property())).build();
    }
}

