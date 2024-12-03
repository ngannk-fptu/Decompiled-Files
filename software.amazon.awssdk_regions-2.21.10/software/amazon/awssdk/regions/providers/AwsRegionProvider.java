/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkProtectedApi
 */
package software.amazon.awssdk.regions.providers;

import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.regions.Region;

@FunctionalInterface
@SdkProtectedApi
public interface AwsRegionProvider {
    public Region getRegion();
}

