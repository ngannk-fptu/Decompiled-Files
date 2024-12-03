/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.core.SdkSystemSetting
 *  software.amazon.awssdk.regions.Region
 *  software.amazon.awssdk.regions.internal.util.EC2MetadataUtils
 *  software.amazon.awssdk.utils.JavaSystemSetting
 *  software.amazon.awssdk.utils.OptionalUtils
 *  software.amazon.awssdk.utils.SystemSetting
 *  software.amazon.awssdk.utils.internal.SystemSettingUtils
 */
package software.amazon.awssdk.awscore.internal.defaultsmode;

import java.util.Optional;
import java.util.function.Supplier;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.awscore.defaultsmode.DefaultsMode;
import software.amazon.awssdk.core.SdkSystemSetting;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.regions.internal.util.EC2MetadataUtils;
import software.amazon.awssdk.utils.JavaSystemSetting;
import software.amazon.awssdk.utils.OptionalUtils;
import software.amazon.awssdk.utils.SystemSetting;
import software.amazon.awssdk.utils.internal.SystemSettingUtils;

@SdkInternalApi
public class AutoDefaultsModeDiscovery {
    private static final String EC2_METADATA_REGION_PATH = "/latest/meta-data/placement/region";
    private static final DefaultsMode FALLBACK_DEFAULTS_MODE = DefaultsMode.STANDARD;
    private static final String ANDROID_JAVA_VENDOR = "The Android Project";
    private static final String AWS_DEFAULT_REGION_ENV_VAR = "AWS_DEFAULT_REGION";

    public DefaultsMode discover(Region regionResolvedFromSdkClient) {
        Optional<String> regionStr;
        if (AutoDefaultsModeDiscovery.isMobile()) {
            return DefaultsMode.MOBILE;
        }
        if (AutoDefaultsModeDiscovery.isAwsExecutionEnvironment() && (regionStr = AutoDefaultsModeDiscovery.regionFromAwsExecutionEnvironment()).isPresent()) {
            return AutoDefaultsModeDiscovery.compareRegion(regionStr.get(), regionResolvedFromSdkClient);
        }
        Optional<String> regionFromEc2 = AutoDefaultsModeDiscovery.queryImdsV2();
        if (regionFromEc2.isPresent()) {
            return AutoDefaultsModeDiscovery.compareRegion(regionFromEc2.get(), regionResolvedFromSdkClient);
        }
        return FALLBACK_DEFAULTS_MODE;
    }

    private static DefaultsMode compareRegion(String region, Region clientRegion) {
        if (region.equalsIgnoreCase(clientRegion.id())) {
            return DefaultsMode.IN_REGION;
        }
        return DefaultsMode.CROSS_REGION;
    }

    private static Optional<String> queryImdsV2() {
        try {
            String ec2InstanceRegion = EC2MetadataUtils.fetchData((String)EC2_METADATA_REGION_PATH, (boolean)false, (int)1);
            return Optional.ofNullable(ec2InstanceRegion);
        }
        catch (Exception exception) {
            return Optional.empty();
        }
    }

    private static boolean isMobile() {
        return JavaSystemSetting.JAVA_VENDOR.getStringValue().filter(o -> o.equals(ANDROID_JAVA_VENDOR)).isPresent();
    }

    private static boolean isAwsExecutionEnvironment() {
        return SdkSystemSetting.AWS_EXECUTION_ENV.getStringValue().isPresent();
    }

    private static Optional<String> regionFromAwsExecutionEnvironment() {
        Optional regionFromRegionEnvVar = SdkSystemSetting.AWS_REGION.getStringValue();
        return OptionalUtils.firstPresent((Optional)regionFromRegionEnvVar, (Supplier[])new Supplier[]{() -> SystemSettingUtils.resolveEnvironmentVariable((SystemSetting)new DefaultRegionEnvVar())});
    }

    private static final class DefaultRegionEnvVar
    implements SystemSetting {
        private DefaultRegionEnvVar() {
        }

        public String property() {
            return null;
        }

        public String environmentVariable() {
            return AutoDefaultsModeDiscovery.AWS_DEFAULT_REGION_ENV_VAR;
        }

        public String defaultValue() {
            return null;
        }
    }
}

