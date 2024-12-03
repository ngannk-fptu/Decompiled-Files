/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.NotThreadSafe
 *  software.amazon.awssdk.annotations.SdkProtectedApi
 *  software.amazon.awssdk.core.exception.SdkClientException
 *  software.amazon.awssdk.profiles.ProfileFile
 *  software.amazon.awssdk.profiles.ProfileFileSystemSetting
 *  software.amazon.awssdk.regions.EndpointTag
 *  software.amazon.awssdk.regions.Region
 *  software.amazon.awssdk.regions.ServiceEndpointKey
 *  software.amazon.awssdk.regions.ServiceMetadata
 *  software.amazon.awssdk.regions.ServiceMetadataAdvancedOption
 *  software.amazon.awssdk.utils.Lazy
 *  software.amazon.awssdk.utils.Validate
 */
package software.amazon.awssdk.awscore.endpoint;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import software.amazon.awssdk.annotations.NotThreadSafe;
import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.awscore.endpoint.DualstackEnabledProvider;
import software.amazon.awssdk.awscore.endpoint.FipsEnabledProvider;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.profiles.ProfileFile;
import software.amazon.awssdk.profiles.ProfileFileSystemSetting;
import software.amazon.awssdk.regions.EndpointTag;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.regions.ServiceEndpointKey;
import software.amazon.awssdk.regions.ServiceMetadata;
import software.amazon.awssdk.regions.ServiceMetadataAdvancedOption;
import software.amazon.awssdk.utils.Lazy;
import software.amazon.awssdk.utils.Validate;

@NotThreadSafe
@SdkProtectedApi
public final class DefaultServiceEndpointBuilder {
    private final String serviceName;
    private final String protocol;
    private Region region;
    private Supplier<ProfileFile> profileFile;
    private String profileName;
    private final Map<ServiceMetadataAdvancedOption<?>, Object> advancedOptions = new HashMap();
    private Boolean dualstackEnabled;
    private Boolean fipsEnabled;

    public DefaultServiceEndpointBuilder(String serviceName, String protocol) {
        this.serviceName = (String)Validate.paramNotNull((Object)serviceName, (String)"serviceName");
        this.protocol = (String)Validate.paramNotNull((Object)protocol, (String)"protocol");
    }

    public DefaultServiceEndpointBuilder withRegion(Region region) {
        if (region == null) {
            throw new IllegalArgumentException("Region cannot be null");
        }
        this.region = region;
        return this;
    }

    public DefaultServiceEndpointBuilder withProfileFile(Supplier<ProfileFile> profileFile) {
        this.profileFile = profileFile;
        return this;
    }

    public DefaultServiceEndpointBuilder withProfileFile(ProfileFile profileFile) {
        this.profileFile = () -> profileFile;
        return this;
    }

    public DefaultServiceEndpointBuilder withProfileName(String profileName) {
        this.profileName = profileName;
        return this;
    }

    public <T> DefaultServiceEndpointBuilder putAdvancedOption(ServiceMetadataAdvancedOption<T> option, T value) {
        this.advancedOptions.put(option, value);
        return this;
    }

    public DefaultServiceEndpointBuilder withDualstackEnabled(Boolean dualstackEnabled) {
        this.dualstackEnabled = dualstackEnabled;
        return this;
    }

    public DefaultServiceEndpointBuilder withFipsEnabled(Boolean fipsEnabled) {
        this.fipsEnabled = fipsEnabled;
        return this;
    }

    public URI getServiceEndpoint() {
        ServiceMetadata serviceMetadata;
        URI endpoint;
        if (this.profileFile == null) {
            this.profileFile = () -> ((Lazy)new Lazy(ProfileFile::defaultProfileFile)).getValue();
        }
        if (this.profileName == null) {
            this.profileName = ProfileFileSystemSetting.AWS_PROFILE.getStringValueOrThrow();
        }
        if (this.dualstackEnabled == null) {
            this.dualstackEnabled = DualstackEnabledProvider.builder().profileFile(this.profileFile).profileName(this.profileName).build().isDualstackEnabled().orElse(false);
        }
        if (this.fipsEnabled == null) {
            this.fipsEnabled = FipsEnabledProvider.builder().profileFile(this.profileFile).profileName(this.profileName).build().isFipsEnabled().orElse(false);
        }
        ArrayList<EndpointTag> endpointTags = new ArrayList<EndpointTag>();
        if (this.dualstackEnabled.booleanValue()) {
            endpointTags.add(EndpointTag.DUALSTACK);
        }
        if (this.fipsEnabled.booleanValue()) {
            endpointTags.add(EndpointTag.FIPS);
        }
        if ((endpoint = this.addProtocolToServiceEndpoint((serviceMetadata = ServiceMetadata.of((String)this.serviceName).reconfigure(c -> c.profileFile(this.profileFile).profileName(this.profileName).advancedOptions(this.advancedOptions))).endpointFor(ServiceEndpointKey.builder().region(this.region).tags(endpointTags).build()))).getHost() == null) {
            String error = "Configured region (" + this.region + ") and tags (" + endpointTags + ") resulted in an invalid URI: " + endpoint + ". This is usually caused by an invalid region configuration.";
            List exampleRegions = serviceMetadata.regions();
            if (!exampleRegions.isEmpty()) {
                error = error + " Valid regions: " + exampleRegions;
            }
            throw SdkClientException.create((String)error);
        }
        return endpoint;
    }

    private URI addProtocolToServiceEndpoint(URI endpointWithoutProtocol) throws IllegalArgumentException {
        try {
            return new URI(this.protocol + "://" + endpointWithoutProtocol);
        }
        catch (URISyntaxException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public Region getRegion() {
        return this.region;
    }
}

