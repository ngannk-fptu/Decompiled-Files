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
import software.amazon.awssdk.services.s3.model.CreateMultipartUploadRequest;
import software.amazon.awssdk.utils.Validate;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

@SdkPublicApi
@Immutable
@ThreadSafe
public final class CreateMultipartUploadPresignRequest
extends PresignRequest
implements ToCopyableBuilder<Builder, CreateMultipartUploadPresignRequest> {
    private final CreateMultipartUploadRequest createMultipartUploadRequest;

    private CreateMultipartUploadPresignRequest(DefaultBuilder builder) {
        super((PresignRequest.DefaultBuilder)builder);
        this.createMultipartUploadRequest = (CreateMultipartUploadRequest)((Object)Validate.notNull((Object)((Object)builder.createMultipartUploadRequest), (String)"createMultipartUploadRequest", (Object[])new Object[0]));
    }

    public static Builder builder() {
        return new DefaultBuilder();
    }

    public CreateMultipartUploadRequest createMultipartUploadRequest() {
        return this.createMultipartUploadRequest;
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
        CreateMultipartUploadPresignRequest that = (CreateMultipartUploadPresignRequest)((Object)o);
        return this.createMultipartUploadRequest.equals((Object)that.createMultipartUploadRequest);
    }

    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + this.createMultipartUploadRequest.hashCode();
        return result;
    }

    @SdkInternalApi
    private static final class DefaultBuilder
    extends PresignRequest.DefaultBuilder<DefaultBuilder>
    implements Builder {
        private CreateMultipartUploadRequest createMultipartUploadRequest;

        private DefaultBuilder() {
        }

        private DefaultBuilder(CreateMultipartUploadPresignRequest request) {
            super((PresignRequest)request);
            this.createMultipartUploadRequest = request.createMultipartUploadRequest;
        }

        @Override
        public Builder createMultipartUploadRequest(CreateMultipartUploadRequest createMultipartUploadRequest) {
            this.createMultipartUploadRequest = createMultipartUploadRequest;
            return this;
        }

        @Override
        public CreateMultipartUploadPresignRequest build() {
            return new CreateMultipartUploadPresignRequest(this);
        }
    }

    @SdkPublicApi
    @NotThreadSafe
    public static interface Builder
    extends PresignRequest.Builder,
    CopyableBuilder<Builder, CreateMultipartUploadPresignRequest> {
        public Builder createMultipartUploadRequest(CreateMultipartUploadRequest var1);

        default public Builder createMultipartUploadRequest(Consumer<CreateMultipartUploadRequest.Builder> createMultipartUploadRequest) {
            CreateMultipartUploadRequest.Builder builder = CreateMultipartUploadRequest.builder();
            createMultipartUploadRequest.accept(builder);
            return this.createMultipartUploadRequest((CreateMultipartUploadRequest)((Object)builder.build()));
        }

        public Builder signatureDuration(Duration var1);

        public CreateMultipartUploadPresignRequest build();
    }
}

