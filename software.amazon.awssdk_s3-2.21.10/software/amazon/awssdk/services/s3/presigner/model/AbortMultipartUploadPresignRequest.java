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
import software.amazon.awssdk.services.s3.model.AbortMultipartUploadRequest;
import software.amazon.awssdk.utils.Validate;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

@SdkPublicApi
@Immutable
@ThreadSafe
public final class AbortMultipartUploadPresignRequest
extends PresignRequest
implements ToCopyableBuilder<Builder, AbortMultipartUploadPresignRequest> {
    private final AbortMultipartUploadRequest abortMultipartUploadRequest;

    private AbortMultipartUploadPresignRequest(DefaultBuilder builder) {
        super((PresignRequest.DefaultBuilder)builder);
        this.abortMultipartUploadRequest = (AbortMultipartUploadRequest)((Object)Validate.notNull((Object)((Object)builder.abortMultipartUploadRequest), (String)"abortMultipartUploadRequest", (Object[])new Object[0]));
    }

    public static Builder builder() {
        return new DefaultBuilder();
    }

    public AbortMultipartUploadRequest abortMultipartUploadRequest() {
        return this.abortMultipartUploadRequest;
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
        AbortMultipartUploadPresignRequest that = (AbortMultipartUploadPresignRequest)((Object)o);
        return this.abortMultipartUploadRequest.equals((Object)that.abortMultipartUploadRequest);
    }

    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + this.abortMultipartUploadRequest.hashCode();
        return result;
    }

    @SdkInternalApi
    private static final class DefaultBuilder
    extends PresignRequest.DefaultBuilder<DefaultBuilder>
    implements Builder {
        private AbortMultipartUploadRequest abortMultipartUploadRequest;

        private DefaultBuilder() {
        }

        private DefaultBuilder(AbortMultipartUploadPresignRequest request) {
            super((PresignRequest)request);
            this.abortMultipartUploadRequest = request.abortMultipartUploadRequest;
        }

        @Override
        public Builder abortMultipartUploadRequest(AbortMultipartUploadRequest abortMultipartUploadRequest) {
            this.abortMultipartUploadRequest = abortMultipartUploadRequest;
            return this;
        }

        @Override
        public AbortMultipartUploadPresignRequest build() {
            return new AbortMultipartUploadPresignRequest(this);
        }
    }

    @SdkPublicApi
    @NotThreadSafe
    public static interface Builder
    extends PresignRequest.Builder,
    CopyableBuilder<Builder, AbortMultipartUploadPresignRequest> {
        public Builder abortMultipartUploadRequest(AbortMultipartUploadRequest var1);

        default public Builder abortMultipartUploadRequest(Consumer<AbortMultipartUploadRequest.Builder> abortMultipartUploadRequest) {
            AbortMultipartUploadRequest.Builder builder = AbortMultipartUploadRequest.builder();
            abortMultipartUploadRequest.accept(builder);
            return this.abortMultipartUploadRequest((AbortMultipartUploadRequest)((Object)builder.build()));
        }

        public Builder signatureDuration(Duration var1);

        public AbortMultipartUploadPresignRequest build();
    }
}

