/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.core.internal.http;

import java.util.function.Consumer;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.client.config.SdkClientConfiguration;
import software.amazon.awssdk.core.internal.http.SdkClientTime;
import software.amazon.awssdk.core.internal.retry.ClockSkewAdjuster;
import software.amazon.awssdk.utils.SdkAutoCloseable;
import software.amazon.awssdk.utils.Validate;

@SdkInternalApi
public final class HttpClientDependencies
implements SdkAutoCloseable {
    private final SdkClientTime sdkClientTime;
    private final ClockSkewAdjuster clockSkewAdjuster;
    private final SdkClientConfiguration clientConfiguration;

    private HttpClientDependencies(Builder builder) {
        this.sdkClientTime = builder.sdkClientTime != null ? builder.sdkClientTime : new SdkClientTime();
        this.clockSkewAdjuster = builder.clockSkewAdjuster != null ? builder.clockSkewAdjuster : new ClockSkewAdjuster();
        this.clientConfiguration = Validate.paramNotNull(builder.clientConfiguration, "ClientConfiguration");
    }

    public static Builder builder() {
        return new Builder();
    }

    public SdkClientConfiguration clientConfiguration() {
        return this.clientConfiguration;
    }

    public ClockSkewAdjuster clockSkewAdjuster() {
        return this.clockSkewAdjuster;
    }

    public int timeOffset() {
        return this.sdkClientTime.getTimeOffset();
    }

    public void updateTimeOffset(int timeOffset) {
        this.sdkClientTime.setTimeOffset(timeOffset);
    }

    public Builder toBuilder() {
        return new Builder(this);
    }

    @Override
    public void close() {
        this.clientConfiguration.close();
    }

    public static class Builder {
        private SdkClientTime sdkClientTime;
        private ClockSkewAdjuster clockSkewAdjuster;
        private SdkClientConfiguration clientConfiguration;

        private Builder() {
        }

        private Builder(HttpClientDependencies from) {
            this.sdkClientTime = from.sdkClientTime;
            this.clientConfiguration = from.clientConfiguration;
            this.clockSkewAdjuster = from.clockSkewAdjuster;
        }

        public Builder clockSkewAdjuster(ClockSkewAdjuster clockSkewAdjuster) {
            this.clockSkewAdjuster = clockSkewAdjuster;
            return this;
        }

        public Builder clientConfiguration(SdkClientConfiguration clientConfiguration) {
            this.clientConfiguration = clientConfiguration;
            return this;
        }

        public Builder clientConfiguration(Consumer<SdkClientConfiguration.Builder> clientConfiguration) {
            SdkClientConfiguration.Builder c = SdkClientConfiguration.builder();
            clientConfiguration.accept(c);
            this.clientConfiguration(c.build());
            return this;
        }

        public HttpClientDependencies build() {
            return new HttpClientDependencies(this);
        }
    }
}

