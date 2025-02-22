/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkPublicApi
 *  software.amazon.awssdk.core.SdkSystemSetting
 *  software.amazon.awssdk.profiles.ProfileFile
 *  software.amazon.awssdk.profiles.ProfileFileSystemSetting
 *  software.amazon.awssdk.utils.Lazy
 *  software.amazon.awssdk.utils.Logger
 */
package software.amazon.awssdk.regions.servicemetadata;

import java.net.URI;
import java.util.List;
import java.util.function.Supplier;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.core.SdkSystemSetting;
import software.amazon.awssdk.profiles.ProfileFile;
import software.amazon.awssdk.profiles.ProfileFileSystemSetting;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.regions.ServiceEndpointKey;
import software.amazon.awssdk.regions.ServiceMetadata;
import software.amazon.awssdk.regions.ServiceMetadataAdvancedOption;
import software.amazon.awssdk.regions.ServiceMetadataConfiguration;
import software.amazon.awssdk.regions.ServicePartitionMetadata;
import software.amazon.awssdk.regions.servicemetadata.S3ServiceMetadata;
import software.amazon.awssdk.utils.Lazy;
import software.amazon.awssdk.utils.Logger;

@SdkPublicApi
public final class EnhancedS3ServiceMetadata
implements ServiceMetadata {
    private static final Logger log = Logger.loggerFor(EnhancedS3ServiceMetadata.class);
    private static final String REGIONAL_SETTING = "regional";
    private final Lazy<Boolean> useUsEast1RegionalEndpoint;
    private final ServiceMetadata s3ServiceMetadata;

    public EnhancedS3ServiceMetadata() {
        this(ServiceMetadataConfiguration.builder().build());
    }

    private EnhancedS3ServiceMetadata(ServiceMetadataConfiguration config) {
        Supplier<ProfileFile> profileFile = config.profileFile() != null ? config.profileFile() : ProfileFile::defaultProfileFile;
        Supplier<String> profileName = config.profileName() != null ? () -> config.profileName() : () -> ((ProfileFileSystemSetting)ProfileFileSystemSetting.AWS_PROFILE).getStringValueOrThrow();
        this.useUsEast1RegionalEndpoint = new Lazy(() -> this.useUsEast1RegionalEndpoint(profileFile, profileName, config));
        this.s3ServiceMetadata = new S3ServiceMetadata().reconfigure(config);
    }

    @Override
    public URI endpointFor(ServiceEndpointKey key) {
        if (Region.US_EAST_1.equals(key.region()) && key.tags().isEmpty() && !((Boolean)this.useUsEast1RegionalEndpoint.getValue()).booleanValue()) {
            return URI.create("s3.amazonaws.com");
        }
        return this.s3ServiceMetadata.endpointFor(key);
    }

    @Override
    public Region signingRegion(ServiceEndpointKey key) {
        return this.s3ServiceMetadata.signingRegion(key);
    }

    @Override
    public List<Region> regions() {
        return this.s3ServiceMetadata.regions();
    }

    @Override
    public List<ServicePartitionMetadata> servicePartitions() {
        return this.s3ServiceMetadata.servicePartitions();
    }

    private boolean useUsEast1RegionalEndpoint(Supplier<ProfileFile> profileFile, Supplier<String> profileName, ServiceMetadataConfiguration config) {
        String env = EnhancedS3ServiceMetadata.envVarSetting();
        if (env != null) {
            return REGIONAL_SETTING.equalsIgnoreCase(env);
        }
        String profile = this.profileFileSetting(profileFile, profileName);
        if (profile != null) {
            return REGIONAL_SETTING.equalsIgnoreCase(profile);
        }
        return config.advancedOption(ServiceMetadataAdvancedOption.DEFAULT_S3_US_EAST_1_REGIONAL_ENDPOINT).filter(REGIONAL_SETTING::equalsIgnoreCase).isPresent();
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
            log.warn(() -> "Unable to load config file", (Throwable)t);
            return null;
        }
    }

    @Override
    public ServiceMetadata reconfigure(ServiceMetadataConfiguration configuration) {
        return new EnhancedS3ServiceMetadata(configuration);
    }
}

