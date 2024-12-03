/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.Immutable
 *  software.amazon.awssdk.annotations.NotThreadSafe
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.annotations.SdkPublicApi
 *  software.amazon.awssdk.annotations.ThreadSafe
 *  software.amazon.awssdk.awscore.presigner.PresignedRequest
 *  software.amazon.awssdk.awscore.presigner.PresignedRequest$Builder
 *  software.amazon.awssdk.awscore.presigner.PresignedRequest$DefaultBuilder
 *  software.amazon.awssdk.core.SdkBytes
 *  software.amazon.awssdk.http.SdkHttpRequest
 *  software.amazon.awssdk.utils.builder.CopyableBuilder
 *  software.amazon.awssdk.utils.builder.ToCopyableBuilder
 */
package software.amazon.awssdk.services.s3.presigner.model;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import software.amazon.awssdk.annotations.Immutable;
import software.amazon.awssdk.annotations.NotThreadSafe;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.annotations.ThreadSafe;
import software.amazon.awssdk.awscore.presigner.PresignedRequest;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.http.SdkHttpRequest;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

@SdkPublicApi
@Immutable
@ThreadSafe
public class PresignedDeleteObjectRequest
extends PresignedRequest
implements ToCopyableBuilder<Builder, PresignedDeleteObjectRequest> {
    protected PresignedDeleteObjectRequest(DefaultBuilder builder) {
        super((PresignedRequest.DefaultBuilder)builder);
    }

    public Builder toBuilder() {
        return new DefaultBuilder(this);
    }

    public static Builder builder() {
        return new DefaultBuilder();
    }

    @SdkInternalApi
    private static final class DefaultBuilder
    extends PresignedRequest.DefaultBuilder<DefaultBuilder>
    implements Builder {
        private DefaultBuilder() {
        }

        private DefaultBuilder(PresignedDeleteObjectRequest presignedDeleteObjectRequest) {
            super((PresignedRequest)presignedDeleteObjectRequest);
        }

        @Override
        public PresignedDeleteObjectRequest build() {
            return new PresignedDeleteObjectRequest(this);
        }
    }

    @SdkPublicApi
    @NotThreadSafe
    public static interface Builder
    extends PresignedRequest.Builder,
    CopyableBuilder<Builder, PresignedDeleteObjectRequest> {
        public Builder expiration(Instant var1);

        public Builder isBrowserExecutable(Boolean var1);

        public Builder signedHeaders(Map<String, List<String>> var1);

        public Builder signedPayload(SdkBytes var1);

        public Builder httpRequest(SdkHttpRequest var1);

        public PresignedDeleteObjectRequest build();
    }
}

