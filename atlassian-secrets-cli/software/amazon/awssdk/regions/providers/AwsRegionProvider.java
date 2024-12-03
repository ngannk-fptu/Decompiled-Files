/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.regions.providers;

import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.regions.Region;

@FunctionalInterface
@SdkProtectedApi
public interface AwsRegionProvider {
    public Region getRegion();
}

