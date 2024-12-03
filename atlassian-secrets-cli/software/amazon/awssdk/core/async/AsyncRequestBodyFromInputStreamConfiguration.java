/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.core.async;

import java.io.InputStream;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.utils.Validate;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

@SdkPublicApi
public final class AsyncRequestBodyFromInputStreamConfiguration
implements ToCopyableBuilder<Builder, AsyncRequestBodyFromInputStreamConfiguration> {
    private final InputStream inputStream;
    private final Long contentLength;
    private final ExecutorService executor;
    private final Integer maxReadLimit;

    private AsyncRequestBodyFromInputStreamConfiguration(DefaultBuilder builder) {
        this.inputStream = Validate.paramNotNull(builder.inputStream, "inputStream");
        this.contentLength = Validate.isNotNegativeOrNull(builder.contentLength, "contentLength");
        this.maxReadLimit = Validate.isPositiveOrNull(builder.maxReadLimit, "maxReadLimit");
        this.executor = Validate.paramNotNull(builder.executor, "executor");
    }

    public InputStream inputStream() {
        return this.inputStream;
    }

    public Long contentLength() {
        return this.contentLength;
    }

    public ExecutorService executor() {
        return this.executor;
    }

    public Integer maxReadLimit() {
        return this.maxReadLimit;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        AsyncRequestBodyFromInputStreamConfiguration that = (AsyncRequestBodyFromInputStreamConfiguration)o;
        if (!Objects.equals(this.inputStream, that.inputStream)) {
            return false;
        }
        if (!Objects.equals(this.contentLength, that.contentLength)) {
            return false;
        }
        if (!Objects.equals(this.executor, that.executor)) {
            return false;
        }
        return Objects.equals(this.maxReadLimit, that.maxReadLimit);
    }

    public int hashCode() {
        int result = this.inputStream != null ? this.inputStream.hashCode() : 0;
        result = 31 * result + (this.contentLength != null ? this.contentLength.hashCode() : 0);
        result = 31 * result + (this.executor != null ? this.executor.hashCode() : 0);
        result = 31 * result + (this.maxReadLimit != null ? this.maxReadLimit.hashCode() : 0);
        return result;
    }

    public static Builder builder() {
        return new DefaultBuilder();
    }

    @Override
    public Builder toBuilder() {
        return new DefaultBuilder(this);
    }

    private static final class DefaultBuilder
    implements Builder {
        private InputStream inputStream;
        private Long contentLength;
        private ExecutorService executor;
        private Integer maxReadLimit;

        private DefaultBuilder(AsyncRequestBodyFromInputStreamConfiguration asyncRequestBodyFromInputStreamConfiguration) {
            this.inputStream = asyncRequestBodyFromInputStreamConfiguration.inputStream;
            this.contentLength = asyncRequestBodyFromInputStreamConfiguration.contentLength;
            this.executor = asyncRequestBodyFromInputStreamConfiguration.executor;
            this.maxReadLimit = asyncRequestBodyFromInputStreamConfiguration.maxReadLimit;
        }

        private DefaultBuilder() {
        }

        @Override
        public Builder inputStream(InputStream inputStream) {
            this.inputStream = inputStream;
            return this;
        }

        @Override
        public Builder contentLength(Long contentLength) {
            this.contentLength = contentLength;
            return this;
        }

        @Override
        public Builder executor(ExecutorService executor) {
            this.executor = executor;
            return this;
        }

        @Override
        public Builder maxReadLimit(Integer maxReadLimit) {
            this.maxReadLimit = maxReadLimit;
            return this;
        }

        @Override
        public AsyncRequestBodyFromInputStreamConfiguration build() {
            return new AsyncRequestBodyFromInputStreamConfiguration(this);
        }
    }

    public static interface Builder
    extends CopyableBuilder<Builder, AsyncRequestBodyFromInputStreamConfiguration> {
        public Builder inputStream(InputStream var1);

        public Builder contentLength(Long var1);

        public Builder executor(ExecutorService var1);

        public Builder maxReadLimit(Integer var1);
    }
}

