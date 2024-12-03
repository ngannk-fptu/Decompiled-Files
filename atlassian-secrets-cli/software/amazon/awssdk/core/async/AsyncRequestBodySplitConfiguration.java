/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.core.async;

import java.util.Objects;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.utils.Validate;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

@SdkPublicApi
public final class AsyncRequestBodySplitConfiguration
implements ToCopyableBuilder<Builder, AsyncRequestBodySplitConfiguration> {
    private static final long DEFAULT_CHUNK_SIZE = 0x200000L;
    private static final long DEFAULT_BUFFER_SIZE = 0x800000L;
    private static final AsyncRequestBodySplitConfiguration DEFAULT_CONFIG = (AsyncRequestBodySplitConfiguration)AsyncRequestBodySplitConfiguration.builder().bufferSizeInBytes(0x800000L).chunkSizeInBytes(0x200000L).build();
    private final Long chunkSizeInBytes;
    private final Long bufferSizeInBytes;

    private AsyncRequestBodySplitConfiguration(DefaultBuilder builder) {
        this.chunkSizeInBytes = Validate.isPositiveOrNull(builder.chunkSizeInBytes, "chunkSizeInBytes");
        this.bufferSizeInBytes = Validate.isPositiveOrNull(builder.bufferSizeInBytes, "bufferSizeInBytes");
    }

    public static AsyncRequestBodySplitConfiguration defaultConfiguration() {
        return DEFAULT_CONFIG;
    }

    public Long chunkSizeInBytes() {
        return this.chunkSizeInBytes;
    }

    public Long bufferSizeInBytes() {
        return this.bufferSizeInBytes;
    }

    public static Builder builder() {
        return new DefaultBuilder();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        AsyncRequestBodySplitConfiguration that = (AsyncRequestBodySplitConfiguration)o;
        if (!Objects.equals(this.chunkSizeInBytes, that.chunkSizeInBytes)) {
            return false;
        }
        return Objects.equals(this.bufferSizeInBytes, that.bufferSizeInBytes);
    }

    public int hashCode() {
        int result = this.chunkSizeInBytes != null ? this.chunkSizeInBytes.hashCode() : 0;
        result = 31 * result + (this.bufferSizeInBytes != null ? this.bufferSizeInBytes.hashCode() : 0);
        return result;
    }

    @Override
    public Builder toBuilder() {
        return new DefaultBuilder(this);
    }

    private static final class DefaultBuilder
    implements Builder {
        private Long chunkSizeInBytes;
        private Long bufferSizeInBytes;

        private DefaultBuilder(AsyncRequestBodySplitConfiguration asyncRequestBodySplitConfiguration) {
            this.chunkSizeInBytes = asyncRequestBodySplitConfiguration.chunkSizeInBytes;
            this.bufferSizeInBytes = asyncRequestBodySplitConfiguration.bufferSizeInBytes;
        }

        private DefaultBuilder() {
        }

        @Override
        public Builder chunkSizeInBytes(Long chunkSizeInBytes) {
            this.chunkSizeInBytes = chunkSizeInBytes;
            return this;
        }

        @Override
        public Builder bufferSizeInBytes(Long bufferSizeInBytes) {
            this.bufferSizeInBytes = bufferSizeInBytes;
            return this;
        }

        @Override
        public AsyncRequestBodySplitConfiguration build() {
            return new AsyncRequestBodySplitConfiguration(this);
        }
    }

    public static interface Builder
    extends CopyableBuilder<Builder, AsyncRequestBodySplitConfiguration> {
        public Builder chunkSizeInBytes(Long var1);

        public Builder bufferSizeInBytes(Long var1);
    }
}

