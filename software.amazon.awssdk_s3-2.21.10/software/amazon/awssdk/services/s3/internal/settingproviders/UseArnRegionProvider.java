/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 */
package software.amazon.awssdk.services.s3.internal.settingproviders;

import java.util.Optional;
import software.amazon.awssdk.annotations.SdkInternalApi;

@FunctionalInterface
@SdkInternalApi
public interface UseArnRegionProvider {
    public Optional<Boolean> resolveUseArnRegion();
}

