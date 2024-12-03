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
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.utils.Validate;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

@SdkPublicApi
@Immutable
@ThreadSafe
public final class DeleteObjectPresignRequest
extends PresignRequest
implements ToCopyableBuilder<Builder, DeleteObjectPresignRequest> {
    private final DeleteObjectRequest deleteObjectRequest;

    protected DeleteObjectPresignRequest(DefaultBuilder builder) {
        super((PresignRequest.DefaultBuilder)builder);
        this.deleteObjectRequest = (DeleteObjectRequest)((Object)Validate.notNull((Object)((Object)builder.deleteObjectRequest), (String)"deleteObjectRequest", (Object[])new Object[0]));
    }

    public DeleteObjectRequest deleteObjectRequest() {
        return this.deleteObjectRequest;
    }

    public Builder toBuilder() {
        return new DefaultBuilder(this);
    }

    public static Builder builder() {
        return new DefaultBuilder();
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
        DeleteObjectPresignRequest that = (DeleteObjectPresignRequest)((Object)o);
        return this.deleteObjectRequest.equals((Object)that.deleteObjectRequest);
    }

    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + this.deleteObjectRequest.hashCode();
        return result;
    }

    @SdkInternalApi
    private static final class DefaultBuilder
    extends PresignRequest.DefaultBuilder<DefaultBuilder>
    implements Builder {
        private DeleteObjectRequest deleteObjectRequest;

        private DefaultBuilder() {
        }

        private DefaultBuilder(DeleteObjectPresignRequest deleteObjectPresignRequest) {
            super((PresignRequest)deleteObjectPresignRequest);
            this.deleteObjectRequest = deleteObjectPresignRequest.deleteObjectRequest;
        }

        @Override
        public Builder deleteObjectRequest(DeleteObjectRequest deleteObjectRequest) {
            this.deleteObjectRequest = deleteObjectRequest;
            return this;
        }

        @Override
        public DeleteObjectPresignRequest build() {
            return new DeleteObjectPresignRequest(this);
        }
    }

    @SdkPublicApi
    @NotThreadSafe
    public static interface Builder
    extends PresignRequest.Builder,
    CopyableBuilder<Builder, DeleteObjectPresignRequest> {
        public Builder deleteObjectRequest(DeleteObjectRequest var1);

        default public Builder deleteObjectRequest(Consumer<DeleteObjectRequest.Builder> deleteObjectRequest) {
            DeleteObjectRequest.Builder builder = DeleteObjectRequest.builder();
            deleteObjectRequest.accept(builder);
            return this.deleteObjectRequest((DeleteObjectRequest)((Object)builder.build()));
        }

        public Builder signatureDuration(Duration var1);

        public DeleteObjectPresignRequest build();
    }
}

