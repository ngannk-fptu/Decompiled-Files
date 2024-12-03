/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.Immutable
 *  software.amazon.awssdk.annotations.SdkPublicApi
 *  software.amazon.awssdk.annotations.ThreadSafe
 *  software.amazon.awssdk.utils.Validate
 *  software.amazon.awssdk.utils.builder.CopyableBuilder
 *  software.amazon.awssdk.utils.builder.ToCopyableBuilder
 */
package software.amazon.awssdk.services.s3.crt;

import java.util.Objects;
import software.amazon.awssdk.annotations.Immutable;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.annotations.ThreadSafe;
import software.amazon.awssdk.utils.Validate;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

@SdkPublicApi
@Immutable
@ThreadSafe
public final class S3CrtRetryConfiguration
implements ToCopyableBuilder<Builder, S3CrtRetryConfiguration> {
    private final Integer numRetries;

    private S3CrtRetryConfiguration(DefaultBuilder builder) {
        Validate.notNull((Object)builder.numRetries, (String)"numRetries", (Object[])new Object[0]);
        this.numRetries = builder.numRetries;
    }

    public static Builder builder() {
        return new DefaultBuilder();
    }

    public Integer numRetries() {
        return this.numRetries;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        S3CrtRetryConfiguration that = (S3CrtRetryConfiguration)o;
        return Objects.equals(this.numRetries, that.numRetries);
    }

    public int hashCode() {
        return this.numRetries != null ? this.numRetries.hashCode() : 0;
    }

    public Builder toBuilder() {
        return new DefaultBuilder(this);
    }

    private static final class DefaultBuilder
    implements Builder {
        private Integer numRetries;

        private DefaultBuilder() {
        }

        private DefaultBuilder(S3CrtRetryConfiguration crtRetryConfiguration) {
            this.numRetries = crtRetryConfiguration.numRetries;
        }

        @Override
        public Builder numRetries(Integer numRetries) {
            this.numRetries = numRetries;
            return this;
        }

        public S3CrtRetryConfiguration build() {
            return new S3CrtRetryConfiguration(this);
        }
    }

    public static interface Builder
    extends CopyableBuilder<Builder, S3CrtRetryConfiguration> {
        public Builder numRetries(Integer var1);
    }
}

