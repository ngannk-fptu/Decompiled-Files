/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkPublicApi
 *  software.amazon.awssdk.utils.builder.CopyableBuilder
 *  software.amazon.awssdk.utils.builder.ToCopyableBuilder
 */
package software.amazon.awssdk.core;

import java.util.Objects;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

@SdkPublicApi
public final class CompressionConfiguration
implements ToCopyableBuilder<Builder, CompressionConfiguration> {
    private final Boolean requestCompressionEnabled;
    private final Integer minimumCompressionThresholdInBytes;

    private CompressionConfiguration(DefaultBuilder builder) {
        this.requestCompressionEnabled = builder.requestCompressionEnabled;
        this.minimumCompressionThresholdInBytes = builder.minimumCompressionThresholdInBytes;
    }

    public Boolean requestCompressionEnabled() {
        return this.requestCompressionEnabled;
    }

    public Integer minimumCompressionThresholdInBytes() {
        return this.minimumCompressionThresholdInBytes;
    }

    public static Builder builder() {
        return new DefaultBuilder();
    }

    public Builder toBuilder() {
        return new DefaultBuilder(this);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        CompressionConfiguration that = (CompressionConfiguration)o;
        if (!this.requestCompressionEnabled.equals(that.requestCompressionEnabled)) {
            return false;
        }
        return Objects.equals(this.minimumCompressionThresholdInBytes, that.minimumCompressionThresholdInBytes);
    }

    public int hashCode() {
        int result = this.requestCompressionEnabled != null ? this.requestCompressionEnabled.hashCode() : 0;
        result = 31 * result + (this.minimumCompressionThresholdInBytes != null ? this.minimumCompressionThresholdInBytes.hashCode() : 0);
        return result;
    }

    private static final class DefaultBuilder
    implements Builder {
        private Boolean requestCompressionEnabled;
        private Integer minimumCompressionThresholdInBytes;

        private DefaultBuilder() {
        }

        private DefaultBuilder(CompressionConfiguration compressionConfiguration) {
            this.requestCompressionEnabled = compressionConfiguration.requestCompressionEnabled;
            this.minimumCompressionThresholdInBytes = compressionConfiguration.minimumCompressionThresholdInBytes;
        }

        @Override
        public Builder requestCompressionEnabled(Boolean requestCompressionEnabled) {
            this.requestCompressionEnabled = requestCompressionEnabled;
            return this;
        }

        @Override
        public Builder minimumCompressionThresholdInBytes(Integer minimumCompressionThresholdInBytes) {
            this.minimumCompressionThresholdInBytes = minimumCompressionThresholdInBytes;
            return this;
        }

        public CompressionConfiguration build() {
            return new CompressionConfiguration(this);
        }
    }

    public static interface Builder
    extends CopyableBuilder<Builder, CompressionConfiguration> {
        public Builder requestCompressionEnabled(Boolean var1);

        public Builder minimumCompressionThresholdInBytes(Integer var1);
    }
}

