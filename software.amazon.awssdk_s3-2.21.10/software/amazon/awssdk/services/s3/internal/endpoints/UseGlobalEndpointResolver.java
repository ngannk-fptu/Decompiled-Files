/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.core.SdkSystemSetting
 *  software.amazon.awssdk.core.client.config.ClientOption
 *  software.amazon.awssdk.core.client.config.SdkClientConfiguration
 *  software.amazon.awssdk.core.client.config.SdkClientOption
 *  software.amazon.awssdk.profiles.ProfileFile
 *  software.amazon.awssdk.regions.Region
 *  software.amazon.awssdk.regions.ServiceMetadataAdvancedOption
 *  software.amazon.awssdk.utils.Lazy
 *  software.amazon.awssdk.utils.Logger
 */
package software.amazon.awssdk.services.s3.internal.endpoints;

import java.util.function.Supplier;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.SdkSystemSetting;
import software.amazon.awssdk.core.client.config.ClientOption;
import software.amazon.awssdk.core.client.config.SdkClientConfiguration;
import software.amazon.awssdk.core.client.config.SdkClientOption;
import software.amazon.awssdk.profiles.ProfileFile;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.regions.ServiceMetadataAdvancedOption;
import software.amazon.awssdk.utils.Lazy;
import software.amazon.awssdk.utils.Logger;

@SdkInternalApi
public class UseGlobalEndpointResolver {
    private static final Logger LOG = Logger.loggerFor(UseGlobalEndpointResolver.class);
    private static final String REGIONAL_SETTING = "regional";
    private final Lazy<Boolean> useUsEast1RegionalEndpoint;

    public UseGlobalEndpointResolver(SdkClientConfiguration config) {
        String defaultS3UsEast1RegionalEndpointFromSmartDefaults = (String)config.option((ClientOption)ServiceMetadataAdvancedOption.DEFAULT_S3_US_EAST_1_REGIONAL_ENDPOINT);
        this.useUsEast1RegionalEndpoint = new Lazy(() -> this.useUsEast1RegionalEndpoint((Supplier)config.option((ClientOption)SdkClientOption.PROFILE_FILE_SUPPLIER), () -> (String)config.option((ClientOption)SdkClientOption.PROFILE_NAME), defaultS3UsEast1RegionalEndpointFromSmartDefaults));
    }

    public boolean resolve(Region region) {
        if (!Region.US_EAST_1.equals(region)) {
            return false;
        }
        return (Boolean)this.useUsEast1RegionalEndpoint.getValue() == false;
    }

    private boolean useUsEast1RegionalEndpoint(Supplier<ProfileFile> profileFile, Supplier<String> profileName, String defaultS3UsEast1RegionalEndpoint) {
        String env = UseGlobalEndpointResolver.envVarSetting();
        if (env != null) {
            return REGIONAL_SETTING.equalsIgnoreCase(env);
        }
        String profile = this.profileFileSetting(profileFile, profileName);
        if (profile != null) {
            return REGIONAL_SETTING.equalsIgnoreCase(profile);
        }
        return REGIONAL_SETTING.equalsIgnoreCase(defaultS3UsEast1RegionalEndpoint);
    }

    private static String envVarSetting() {
        return SdkSystemSetting.AWS_S3_US_EAST_1_REGIONAL_ENDPOINT.getStringValue().orElse(null);
    }

    private String profileFileSetting(Supplier<ProfileFile> profileFileSupplier, Supplier<String> profileNameSupplier) {
        try {
            ProfileFile profileFile = profileFileSupplier.get();
            String profileName = profileNameSupplier.get();
            if (profileFile == null || profileName == null) {
                return null;
            }
            return profileFile.profile(profileName).flatMap(p -> p.property("s3_us_east_1_regional_endpoint")).orElse(null);
        }
        catch (Exception t) {
            LOG.warn(() -> "Unable to load config file", (Throwable)t);
            return null;
        }
    }
}

