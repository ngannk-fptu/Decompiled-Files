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
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.utils.Validate;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

@SdkPublicApi
@Immutable
@ThreadSafe
public final class GetObjectPresignRequest
extends PresignRequest
implements ToCopyableBuilder<Builder, GetObjectPresignRequest> {
    private final GetObjectRequest getObjectRequest;

    private GetObjectPresignRequest(DefaultBuilder builder) {
        super((PresignRequest.DefaultBuilder)builder);
        this.getObjectRequest = (GetObjectRequest)((Object)Validate.notNull((Object)((Object)builder.getObjectRequest), (String)"getObjectRequest", (Object[])new Object[0]));
    }

    public static Builder builder() {
        return new DefaultBuilder();
    }

    public GetObjectRequest getObjectRequest() {
        return this.getObjectRequest;
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
        GetObjectPresignRequest that = (GetObjectPresignRequest)((Object)o);
        return this.getObjectRequest.equals((Object)that.getObjectRequest);
    }

    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + this.getObjectRequest.hashCode();
        return result;
    }

    @SdkInternalApi
    private static final class DefaultBuilder
    extends PresignRequest.DefaultBuilder<DefaultBuilder>
    implements Builder {
        private GetObjectRequest getObjectRequest;

        private DefaultBuilder() {
        }

        private DefaultBuilder(GetObjectPresignRequest request) {
            super((PresignRequest)request);
            this.getObjectRequest = request.getObjectRequest;
        }

        @Override
        public Builder getObjectRequest(GetObjectRequest getObjectRequest) {
            this.getObjectRequest = getObjectRequest;
            return this;
        }

        @Override
        public GetObjectPresignRequest build() {
            return new GetObjectPresignRequest(this);
        }
    }

    @SdkPublicApi
    @NotThreadSafe
    public static interface Builder
    extends PresignRequest.Builder,
    CopyableBuilder<Builder, GetObjectPresignRequest> {
        public Builder getObjectRequest(GetObjectRequest var1);

        default public Builder getObjectRequest(Consumer<GetObjectRequest.Builder> getObjectRequest) {
            GetObjectRequest.Builder builder = GetObjectRequest.builder();
            getObjectRequest.accept(builder);
            return this.getObjectRequest((GetObjectRequest)((Object)builder.build()));
        }

        public Builder signatureDuration(Duration var1);

        public GetObjectPresignRequest build();
    }
}

