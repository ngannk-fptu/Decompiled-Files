/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkProtectedApi
 *  software.amazon.awssdk.annotations.SdkPublicApi
 *  software.amazon.awssdk.utils.Validate
 */
package software.amazon.awssdk.awscore.presigner;

import java.time.Duration;
import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.utils.Validate;

@SdkPublicApi
public abstract class PresignRequest {
    private final Duration signatureDuration;

    protected PresignRequest(DefaultBuilder<?> builder) {
        this.signatureDuration = (Duration)Validate.paramNotNull((Object)((DefaultBuilder)builder).signatureDuration, (String)"signatureDuration");
    }

    public Duration signatureDuration() {
        return this.signatureDuration;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        PresignRequest that = (PresignRequest)o;
        return this.signatureDuration.equals(that.signatureDuration);
    }

    public int hashCode() {
        return this.signatureDuration.hashCode();
    }

    @SdkProtectedApi
    protected static abstract class DefaultBuilder<B extends DefaultBuilder<B>>
    implements Builder {
        private Duration signatureDuration;

        protected DefaultBuilder() {
        }

        protected DefaultBuilder(PresignRequest request) {
            this.signatureDuration = request.signatureDuration;
        }

        public B signatureDuration(Duration signatureDuration) {
            this.signatureDuration = signatureDuration;
            return this.thisBuilder();
        }

        private B thisBuilder() {
            return (B)this;
        }
    }

    @SdkPublicApi
    public static interface Builder {
        public Builder signatureDuration(Duration var1);

        public PresignRequest build();
    }
}

