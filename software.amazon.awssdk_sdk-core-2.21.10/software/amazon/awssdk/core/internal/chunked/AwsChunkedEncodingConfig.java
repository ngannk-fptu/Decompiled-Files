/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 */
package software.amazon.awssdk.core.internal.chunked;

import software.amazon.awssdk.annotations.SdkInternalApi;

@SdkInternalApi
public final class AwsChunkedEncodingConfig {
    private final int chunkSize;
    private final int bufferSize;

    private AwsChunkedEncodingConfig(BuilderImpl builder) {
        this.chunkSize = builder.chunkSize;
        this.bufferSize = builder.bufferSize;
    }

    public static AwsChunkedEncodingConfig create() {
        return AwsChunkedEncodingConfig.builder().build();
    }

    public static Builder builder() {
        return new BuilderImpl();
    }

    public int chunkSize() {
        return this.chunkSize;
    }

    public int bufferSize() {
        return this.bufferSize;
    }

    private static final class BuilderImpl
    implements Builder {
        static final int DEFAULT_CHUNKED_ENCODING_ENABLED = 131072;
        static final int DEFAULT_PAYLOAD_SIGNING_ENABLED = 262144;
        private int chunkSize = 131072;
        private int bufferSize = 262144;

        private BuilderImpl() {
        }

        @Override
        public Builder chunkSize(int chunkSize) {
            this.chunkSize = chunkSize;
            return this;
        }

        public void setChunkSize(int chunkSize) {
            this.chunkSize(chunkSize);
        }

        @Override
        public Builder bufferSize(int bufferSize) {
            this.bufferSize = bufferSize;
            return this;
        }

        public void setBufferSize(int bufferSize) {
            this.bufferSize(bufferSize);
        }

        @Override
        public AwsChunkedEncodingConfig build() {
            return new AwsChunkedEncodingConfig(this);
        }
    }

    public static interface Builder {
        public Builder chunkSize(int var1);

        public Builder bufferSize(int var1);

        public AwsChunkedEncodingConfig build();
    }
}

