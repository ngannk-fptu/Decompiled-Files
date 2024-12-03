/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkPublicApi
 *  software.amazon.awssdk.utils.builder.CopyableBuilder
 *  software.amazon.awssdk.utils.builder.ToCopyableBuilder
 */
package software.amazon.awssdk.services.s3.multipart;

import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

@SdkPublicApi
public final class MultipartConfiguration
implements ToCopyableBuilder<Builder, MultipartConfiguration> {
    private final Long thresholdInBytes;
    private final Long minimumPartSizeInBytes;
    private final Long apiCallBufferSizeInBytes;

    private MultipartConfiguration(DefaultMultipartConfigBuilder builder) {
        this.thresholdInBytes = builder.thresholdInBytes;
        this.minimumPartSizeInBytes = builder.minimumPartSizeInBytes;
        this.apiCallBufferSizeInBytes = builder.apiCallBufferSizeInBytes;
    }

    public static Builder builder() {
        return new DefaultMultipartConfigBuilder();
    }

    public Builder toBuilder() {
        return MultipartConfiguration.builder().apiCallBufferSizeInBytes(this.apiCallBufferSizeInBytes).minimumPartSizeInBytes(this.minimumPartSizeInBytes).thresholdInBytes(this.thresholdInBytes);
    }

    public Long thresholdInBytes() {
        return this.thresholdInBytes;
    }

    public Long minimumPartSizeInBytes() {
        return this.minimumPartSizeInBytes;
    }

    public Long apiCallBufferSizeInBytes() {
        return this.apiCallBufferSizeInBytes;
    }

    private static class DefaultMultipartConfigBuilder
    implements Builder {
        private Long thresholdInBytes;
        private Long minimumPartSizeInBytes;
        private Long apiCallBufferSizeInBytes;

        private DefaultMultipartConfigBuilder() {
        }

        @Override
        public Builder thresholdInBytes(Long thresholdInBytes) {
            this.thresholdInBytes = thresholdInBytes;
            return this;
        }

        @Override
        public Long thresholdInBytes() {
            return this.thresholdInBytes;
        }

        @Override
        public Builder minimumPartSizeInBytes(Long minimumPartSizeInBytes) {
            this.minimumPartSizeInBytes = minimumPartSizeInBytes;
            return this;
        }

        @Override
        public Long minimumPartSizeInBytes() {
            return this.minimumPartSizeInBytes;
        }

        @Override
        public Builder apiCallBufferSizeInBytes(Long maximumMemoryUsageInBytes) {
            this.apiCallBufferSizeInBytes = maximumMemoryUsageInBytes;
            return this;
        }

        @Override
        public Long apiCallBufferSizeInBytes() {
            return this.apiCallBufferSizeInBytes;
        }

        public MultipartConfiguration build() {
            return new MultipartConfiguration(this);
        }
    }

    public static interface Builder
    extends CopyableBuilder<Builder, MultipartConfiguration> {
        public Builder thresholdInBytes(Long var1);

        public Long thresholdInBytes();

        public Builder minimumPartSizeInBytes(Long var1);

        public Long minimumPartSizeInBytes();

        public Builder apiCallBufferSizeInBytes(Long var1);

        public Long apiCallBufferSizeInBytes();
    }
}

