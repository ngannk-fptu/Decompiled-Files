/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.Immutable
 *  software.amazon.awssdk.annotations.NotThreadSafe
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.annotations.SdkPublicApi
 *  software.amazon.awssdk.annotations.ThreadSafe
 *  software.amazon.awssdk.awscore.presigner.PresignRequest
 *  software.amazon.awssdk.awscore.presigner.PresignRequest$Builder
 *  software.amazon.awssdk.awscore.presigner.PresignRequest$DefaultBuilder
 *  software.amazon.awssdk.utils.Validate
 *  software.amazon.awssdk.utils.builder.CopyableBuilder
 *  software.amazon.awssdk.utils.builder.ToCopyableBuilder
 */
package software.amazon.awssdk.services.s3.presigner.model;

import java.time.Duration;
import java.util.function.Consumer;
import software.amazon.awssdk.annotations.Immutable;
import software.amazon.awssdk.annotations.NotThreadSafe;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.annotations.ThreadSafe;
import software.amazon.awssdk.awscore.presigner.PresignRequest;
import software.amazon.awssdk.services.s3.model.UploadPartRequest;
import software.amazon.awssdk.utils.Validate;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

@SdkPublicApi
@Immutable
@ThreadSafe
public final class UploadPartPresignRequest
extends PresignRequest
implements ToCopyableBuilder<Builder, UploadPartPresignRequest> {
    private final UploadPartRequest uploadPartRequest;

    private UploadPartPresignRequest(DefaultBuilder builder) {
        super((PresignRequest.DefaultBuilder)builder);
        this.uploadPartRequest = (UploadPartRequest)((Object)Validate.notNull((Object)((Object)builder.uploadPartRequest), (String)"uploadPartRequest", (Object[])new Object[0]));
    }

    public static Builder builder() {
        return new DefaultBuilder();
    }

    public UploadPartRequest uploadPartRequest() {
        return this.uploadPartRequest;
    }

    public Builder toBuilder() {
        return new DefaultBuilder(this);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || ((Object)((Object)this)).getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        UploadPartPresignRequest that = (UploadPartPresignRequest)((Object)o);
        return this.uploadPartRequest.equals((Object)that.uploadPartRequest);
    }

    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + this.uploadPartRequest.hashCode();
        return result;
    }

    @SdkInternalApi
    private static final class DefaultBuilder
    extends PresignRequest.DefaultBuilder<DefaultBuilder>
    implements Builder {
        private UploadPartRequest uploadPartRequest;

        private DefaultBuilder() {
        }

        private DefaultBuilder(UploadPartPresignRequest request) {
            super((PresignRequest)request);
            this.uploadPartRequest = request.uploadPartRequest;
        }

        @Override
        public Builder uploadPartRequest(UploadPartRequest uploadPartRequest) {
            this.uploadPartRequest = uploadPartRequest;
            return this;
        }

        @Override
        public UploadPartPresignRequest build() {
            return new UploadPartPresignRequest(this);
        }
    }

    @SdkPublicApi
    @NotThreadSafe
    public static interface Builder
    extends PresignRequest.Builder,
    CopyableBuilder<Builder, UploadPartPresignRequest> {
        public Builder uploadPartRequest(UploadPartRequest var1);

        default public Builder uploadPartRequest(Consumer<UploadPartRequest.Builder> uploadPartRequest) {
            UploadPartRequest.Builder builder = UploadPartRequest.builder();
            uploadPartRequest.accept(builder);
            return this.uploadPartRequest((UploadPartRequest)((Object)builder.build()));
        }

        public Builder signatureDuration(Duration var1);

        public UploadPartPresignRequest build();
    }
}

