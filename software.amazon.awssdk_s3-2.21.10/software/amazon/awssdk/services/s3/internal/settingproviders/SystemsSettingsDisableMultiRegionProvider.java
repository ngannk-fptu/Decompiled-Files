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
import software.amazon.awssdk.services.s3.internal.settingproviders.DisableMultiRegionProvider;

@SdkInternalApi
public final class SystemsSettingsDisableMultiRegionProvider
implements DisableMultiRegionProvider {
    private SystemsSettingsDisableMultiRegionProvider() {
    }

    public static SystemsSettingsDisableMultiRegionProvider create() {
        return new SystemsSettingsDisableMultiRegionProvider();
    }

    @Override
    public Optional<Boolean> resolve() {
        return S3SystemSetting.AWS_S3_DISABLE_MULTIREGION_ACCESS_POINTS.getBooleanValue();
    }
}

