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
import software.amazon.awssdk.services.s3.model.CompleteMultipartUploadRequest;
import software.amazon.awssdk.utils.Validate;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

@SdkPublicApi
@Immutable
@ThreadSafe
public final class CompleteMultipartUploadPresignRequest
extends PresignRequest
implements ToCopyableBuilder<Builder, CompleteMultipartUploadPresignRequest> {
    private final CompleteMultipartUploadRequest completeMultipartUploadRequest;

    private CompleteMultipartUploadPresignRequest(DefaultBuilder builder) {
        super((PresignRequest.DefaultBuilder)builder);
        this.completeMultipartUploadRequest = (CompleteMultipartUploadRequest)((Object)Validate.notNull((Object)((Object)builder.completeMultipartUploadRequest), (String)"completeMultipartUploadRequest", (Object[])new Object[0]));
    }

    public static Builder builder() {
        return new DefaultBuilder();
    }

    public CompleteMultipartUploadRequest completeMultipartUploadRequest() {
        return this.completeMultipartUploadRequest;
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
        CompleteMultipartUploadPresignRequest that = (CompleteMultipartUploadPresignRequest)((Object)o);
        return this.completeMultipartUploadRequest.equals((Object)that.completeMultipartUploadRequest);
    }

    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + this.completeMultipartUploadRequest.hashCode();
        return result;
    }

    @SdkInternalApi
    private static final class DefaultBuilder
    extends PresignRequest.DefaultBuilder<DefaultBuilder>
    implements Builder {
        private CompleteMultipartUploadRequest completeMultipartUploadRequest;

        private DefaultBuilder() {
        }

        private DefaultBuilder(CompleteMultipartUploadPresignRequest request) {
            super((PresignRequest)request);
            this.completeMultipartUploadRequest = request.completeMultipartUploadRequest;
        }

        @Override
        public Builder completeMultipartUploadRequest(CompleteMultipartUploadRequest completeMultipartUploadRequest) {
            this.completeMultipartUploadRequest = completeMultipartUploadRequest;
            return this;
        }

        @Override
        public CompleteMultipartUploadPresignRequest build() {
            return new CompleteMultipartUploadPresignRequest(this);
        }
    }

    @SdkPublicApi
    @NotThreadSafe
    public static interface Builder
    extends PresignRequest.Builder,
    CopyableBuilder<Builder, CompleteMultipartUploadPresignRequest> {
        public Builder completeMultipartUploadRequest(CompleteMultipartUploadRequest var1);

        default public Builder completeMultipartUploadRequest(Consumer<CompleteMultipartUploadRequest.Builder> completeMultipartUploadRequest) {
            CompleteMultipartUploadRequest.Builder builder = CompleteMultipartUploadRequest.builder();
            completeMultipartUploadRequest.accept(builder);
            return this.completeMultipartUploadRequest((CompleteMultipartUploadRequest)((Object)builder.build()));
        }

        public Builder signatureDuration(Duration var1);

        public CompleteMultipartUploadPresignRequest build();
    }
}

