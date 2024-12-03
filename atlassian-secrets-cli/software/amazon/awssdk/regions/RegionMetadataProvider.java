/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.regions;

import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.regions.RegionMetadata;

@SdkPublicApi
public interface RegionMetadataProvider {
    public RegionMetadata regionMetadata(Region var1);
}

