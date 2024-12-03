/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.http.nio.netty;

import java.time.Duration;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.utils.Validate;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

@SdkPublicApi
public final class Http2Configuration
implements ToCopyableBuilder<Builder, Http2Configuration> {
    private final Long maxStreams;
    private final Integer initialWindowSize;
    private final Duration healthCheckPingPeriod;

    private Http2Configuration(DefaultBuilder builder) {
        this.maxStreams = builder.maxStreams;
        this.initialWindowSize = builder.initialWindowSize;
        this.healthCheckPingPeriod = builder.healthCheckPingPeriod;
    }

    public Long maxStreams() {
        return this.maxStreams;
    }

    public Integer initialWindowSize() {
        return this.initialWindowSize;
    }

    public Duration healthCheckPingPeriod() {
        return this.healthCheckPingPeriod;
    }

    @Override
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
        Http2Configuration that = (Http2Configuration)o;
        if (this.maxStreams != null ? !this.maxStreams.equals(that.maxStreams) : that.maxStreams != null) {
            return false;
        }
        return this.initialWindowSize != null ? this.initialWindowSize.equals(that.initialWindowSize) : that.initialWindowSize == null;
    }

    public int hashCode() {
        int result = this.maxStreams != null ? this.maxStreams.hashCode() : 0;
        result = 31 * result + (this.initialWindowSize != null ? this.initialWindowSize.hashCode() : 0);
        return result;
    }

    public static Builder builder() {
        return new DefaultBuilder();
    }

    private static final class DefaultBuilder
    implements Builder {
        private Long maxStreams;
        private Integer initialWindowSize;
        private Duration healthCheckPingPeriod;

        private DefaultBuilder() {
        }

        private DefaultBuilder(Http2Configuration http2Configuration) {
            this.maxStreams = http2Configuration.maxStreams;
            this.initialWindowSize = http2Configuration.initialWindowSize;
            this.healthCheckPingPeriod = http2Configuration.healthCheckPingPeriod;
        }

        @Override
        public Builder maxStreams(Long maxStreams) {
            this.maxStreams = Validate.isPositiveOrNull(maxStreams, "maxStreams");
            return this;
        }

        public void setMaxStreams(Long maxStreams) {
            this.maxStreams(maxStreams);
        }

        @Override
        public Builder initialWindowSize(Integer initialWindowSize) {
            this.initialWindowSize = Validate.isPositiveOrNull(initialWindowSize, "initialWindowSize");
            return this;
        }

        public void setInitialWindowSize(Integer initialWindowSize) {
            this.initialWindowSize(initialWindowSize);
        }

        @Override
        public Builder healthCheckPingPeriod(Duration healthCheckPingPeriod) {
            this.healthCheckPingPeriod = healthCheckPingPeriod;
            return this;
        }

        public void setHealthCheckPingPeriod(Duration healthCheckPingPeriod) {
            this.healthCheckPingPeriod(healthCheckPingPeriod);
        }

        @Override
        public Http2Configuration build() {
            return new Http2Configuration(this);
        }
    }

    public static interface Builder
    extends CopyableBuilder<Builder, Http2Configuration> {
        public Builder maxStreams(Long var1);

        public Builder initialWindowSize(Integer var1);

        public Builder healthCheckPingPeriod(Duration var1);
    }
}

