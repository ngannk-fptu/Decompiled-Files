/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 */
package software.amazon.awssdk.services.s3.internal.endpoints;

import java.util.Optional;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.services.s3.model.S3Request;

@SdkInternalApi
public final class S3EndpointResolverFactoryContext {
    private final String bucketName;
    private final S3Request originalRequest;

    private S3EndpointResolverFactoryContext(DefaultBuilder builder) {
        this.bucketName = builder.bucketName;
        this.originalRequest = builder.originalRequest;
    }

    public Optional<String> bucketName() {
        return Optional.ofNullable(this.bucketName);
    }

    public S3Request originalRequest() {
        return this.originalRequest;
    }

    public static Builder builder() {
        return new DefaultBuilder();
    }

    private static final class DefaultBuilder
    implements Builder {
        private String bucketName;
        private S3Request originalRequest;

        private DefaultBuilder() {
        }

        @Override
        public Builder bucketName(String bucketName) {
            this.bucketName = bucketName;
            return this;
        }

        @Override
        public Builder originalRequest(S3Request originalRequest) {
            this.originalRequest = originalRequest;
            return this;
        }

        @Override
        public S3EndpointResolverFactoryContext build() {
            return new S3EndpointResolverFactoryContext(this);
        }
    }

    public static interface Builder {
        public Builder bucketName(String var1);

        public Builder originalRequest(S3Request var1);

        public S3EndpointResolverFactoryContext build();
    }
}

