/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.regions.providers;

import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.core.SdkSystemSetting;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.regions.internal.util.EC2MetadataUtils;
import software.amazon.awssdk.regions.providers.AwsRegionProvider;

@SdkProtectedApi
public final class InstanceProfileRegionProvider
implements AwsRegionProvider {
    private volatile String region;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Region getRegion() throws SdkClientException {
        if (SdkSystemSetting.AWS_EC2_METADATA_DISABLED.getBooleanValueOrThrow().booleanValue()) {
            throw SdkClientException.builder().message("EC2 Metadata is disabled. Unable to retrieve region information from EC2 Metadata service.").build();
        }
        if (this.region == null) {
            InstanceProfileRegionProvider instanceProfileRegionProvider = this;
            synchronized (instanceProfileRegionProvider) {
                if (this.region == null) {
                    this.region = this.tryDetectRegion();
                }
            }
        }
        if (this.region == null) {
            throw SdkClientException.builder().message("Unable to retrieve region information from EC2 Metadata service. Please make sure the application is running on EC2.").build();
        }
        return Region.of(this.region);
    }

    private String tryDetectRegion() {
        return EC2MetadataUtils.getEC2InstanceRegion();
    }
}

