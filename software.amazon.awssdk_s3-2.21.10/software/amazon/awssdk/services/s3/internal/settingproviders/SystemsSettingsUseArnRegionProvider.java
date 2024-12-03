/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 */
package software.amazon.awssdk.services.s3.internal.settingproviders;

import java.util.Optional;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.services.s3.S3SystemSetting;
import software.amazon.awssdk.services.s3.internal.settingproviders.UseArnRegionProvider;

@SdkInternalApi
public final class SystemsSettingsUseArnRegionProvider
implements UseArnRegionProvider {
    private SystemsSettingsUseArnRegionProvider() {
    }

    public static SystemsSettingsUseArnRegionProvider create() {
        return new SystemsSettingsUseArnRegionProvider();
    }

    @Override
    public Optional<Boolean> resolveUseArnRegion() {
        return S3SystemSetting.AWS_S3_USE_ARN_REGION.getBooleanValue();
    }
}

