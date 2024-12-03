/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkPublicApi
 *  software.amazon.awssdk.regions.Region
 *  software.amazon.awssdk.utils.builder.CopyableBuilder
 *  software.amazon.awssdk.utils.builder.ToCopyableBuilder
 */
package software.amazon.awssdk.services.s3.auth.scheme;

import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.auth.scheme.internal.DefaultS3AuthSchemeParams;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

@SdkPublicApi
public interface S3AuthSchemeParams
extends ToCopyableBuilder<Builder, S3AuthSchemeParams> {
    public static Builder builder() {
        return DefaultS3AuthSchemeParams.builder();
    }

    public String operation();

    public Region region();

    public String bucket();

    public Boolean useFips();

    public Boolean useDualStack();

    public String endpoint();

    public Boolean forcePathStyle();

    public Boolean accelerate();

    public Boolean useGlobalEndpoint();

    public Boolean useObjectLambdaEndpoint();

    public Boolean disableAccessPoints();

    public Boolean disableMultiRegionAccessPoints();

    public Boolean useArnRegion();

    public Builder toBuilder();

    public static interface Builder
    extends CopyableBuilder<Builder, S3AuthSchemeParams> {
        public Builder operation(String var1);

        public Builder region(Region var1);

        public Builder bucket(String var1);

        public Builder useFips(Boolean var1);

        public Builder useDualStack(Boolean var1);

        public Builder endpoint(String var1);

        public Builder forcePathStyle(Boolean var1);

        public Builder accelerate(Boolean var1);

        public Builder useGlobalEndpoint(Boolean var1);

        public Builder useObjectLambdaEndpoint(Boolean var1);

        public Builder disableAccessPoints(Boolean var1);

        public Builder disableMultiRegionAccessPoints(Boolean var1);

        public Builder useArnRegion(Boolean var1);

        public S3AuthSchemeParams build();
    }
}

