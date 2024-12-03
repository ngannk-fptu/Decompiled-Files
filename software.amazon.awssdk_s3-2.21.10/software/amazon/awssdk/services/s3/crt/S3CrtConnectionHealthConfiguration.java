/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.Immutable
 *  software.amazon.awssdk.annotations.SdkPublicApi
 *  software.amazon.awssdk.annotations.ThreadSafe
 *  software.amazon.awssdk.crtcore.CrtConnectionHealthConfiguration
 *  software.amazon.awssdk.crtcore.CrtConnectionHealthConfiguration$Builder
 *  software.amazon.awssdk.crtcore.CrtConnectionHealthConfiguration$DefaultBuilder
 *  software.amazon.awssdk.utils.builder.CopyableBuilder
 *  software.amazon.awssdk.utils.builder.ToCopyableBuilder
 */
package software.amazon.awssdk.services.s3.crt;

import java.time.Duration;
import software.amazon.awssdk.annotations.Immutable;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.annotations.ThreadSafe;
import software.amazon.awssdk.crtcore.CrtConnectionHealthConfiguration;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

@SdkPublicApi
@Immutable
@ThreadSafe
public final class S3CrtConnectionHealthConfiguration
extends CrtConnectionHealthConfiguration
implements ToCopyableBuilder<Builder, S3CrtConnectionHealthConfiguration> {
    private S3CrtConnectionHealthConfiguration(DefaultBuilder builder) {
        super((CrtConnectionHealthConfiguration.DefaultBuilder)builder);
    }

    public static Builder builder() {
        return new DefaultBuilder();
    }

    public Builder toBuilder() {
        return new DefaultBuilder(this);
    }

    public boolean equals(Object o) {
        return super.equals(o);
    }

    public int hashCode() {
        return super.hashCode();
    }

    private static final class DefaultBuilder
    extends CrtConnectionHealthConfiguration.DefaultBuilder<DefaultBuilder>
    implements Builder {
        private DefaultBuilder() {
        }

        private DefaultBuilder(S3CrtConnectionHealthConfiguration configuration) {
            super((CrtConnectionHealthConfiguration)configuration);
        }

        @Override
        public S3CrtConnectionHealthConfiguration build() {
            return new S3CrtConnectionHealthConfiguration(this);
        }
    }

    public static interface Builder
    extends CrtConnectionHealthConfiguration.Builder,
    CopyableBuilder<Builder, S3CrtConnectionHealthConfiguration> {
        public Builder minimumThroughputInBps(Long var1);

        public Builder minimumThroughputTimeout(Duration var1);

        public S3CrtConnectionHealthConfiguration build();
    }
}

