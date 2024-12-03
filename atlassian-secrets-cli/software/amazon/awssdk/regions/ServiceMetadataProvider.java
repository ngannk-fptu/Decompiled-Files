/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.regions;

import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.regions.ServiceMetadata;

@SdkPublicApi
public interface ServiceMetadataProvider {
    public ServiceMetadata serviceMetadata(String var1);
}

