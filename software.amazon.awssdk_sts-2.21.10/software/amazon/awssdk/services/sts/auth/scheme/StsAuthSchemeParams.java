/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkPublicApi
 *  software.amazon.awssdk.regions.Region
 *  software.amazon.awssdk.utils.builder.CopyableBuilder
 *  software.amazon.awssdk.utils.builder.ToCopyableBuilder
 */
package software.amazon.awssdk.services.sts.auth.scheme;

import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sts.auth.scheme.internal.DefaultStsAuthSchemeParams;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

@SdkPublicApi
public interface StsAuthSchemeParams
extends ToCopyableBuilder<Builder, StsAuthSchemeParams> {
    public static Builder builder() {
        return DefaultStsAuthSchemeParams.builder();
    }

    public String operation();

    public Region region();

    public Builder toBuilder();

    public static interface Builder
    extends CopyableBuilder<Builder, StsAuthSchemeParams> {
        public Builder operation(String var1);

        public Builder region(Region var1);

        public StsAuthSchemeParams build();
    }
}

