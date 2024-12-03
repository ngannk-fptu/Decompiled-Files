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
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.utils.Validate;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

@SdkPublicApi
@Immutable
@ThreadSafe
public final class PutObjectPresignRequest
extends PresignRequest
implements ToCopyableBuilder<Builder, PutObjectPresignRequest> {
    private final PutObjectRequest putObjectRequest;

    private PutObjectPresignRequest(DefaultBuilder builder) {
        super((PresignRequest.DefaultBuilder)builder);
        this.putObjectRequest = (PutObjectRequest)((Object)Validate.notNull((Object)((Object)builder.putObjectRequest), (String)"putObjectRequest", (Object[])new Object[0]));
    }

    public static Builder builder() {
        return new DefaultBuilder();
    }

    public PutObjectRequest putObjectRequest() {
        return this.putObjectRequest;
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
        PutObjectPresignRequest that = (PutObjectPresignRequest)((Object)o);
        return this.putObjectRequest.equals((Object)that.putObjectRequest);
    }

    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + this.putObjectRequest.hashCode();
        return result;
    }

    @SdkInternalApi
    private static final class DefaultBuilder
    extends PresignRequest.DefaultBuilder<DefaultBuilder>
    implements Builder {
        private PutObjectRequest putObjectRequest;

        private DefaultBuilder() {
        }

        private DefaultBuilder(PutObjectPresignRequest request) {
            super((PresignRequest)request);
            this.putObjectRequest = request.putObjectRequest;
        }

        @Override
        public Builder putObjectRequest(PutObjectRequest putObjectRequest) {
            this.putObjectRequest = putObjectRequest;
            return this;
        }

        @Override
        public PutObjectPresignRequest build() {
            return new PutObjectPresignRequest(this);
        }
    }

    @SdkPublicApi
    @NotThreadSafe
    public static interface Builder
    extends PresignRequest.Builder,
    CopyableBuilder<Builder, PutObjectPresignRequest> {
        public Builder putObjectRequest(PutObjectRequest var1);

        default public Builder putObjectRequest(Consumer<PutObjectRequest.Builder> putObjectRequest) {
            PutObjectRequest.Builder builder = PutObjectRequest.builder();
            putObjectRequest.accept(builder);
            return this.putObjectRequest((PutObjectRequest)((Object)builder.build()));
        }

        public Builder signatureDuration(Duration var1);

        public PutObjectPresignRequest build();
    }
}

