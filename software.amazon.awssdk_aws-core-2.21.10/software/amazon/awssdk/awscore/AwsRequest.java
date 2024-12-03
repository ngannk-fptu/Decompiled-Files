/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkPublicApi
 *  software.amazon.awssdk.core.SdkRequest
 *  software.amazon.awssdk.core.SdkRequest$Builder
 */
package software.amazon.awssdk.awscore;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.awscore.AwsRequestOverrideConfiguration;
import software.amazon.awssdk.core.SdkRequest;

@SdkPublicApi
public abstract class AwsRequest
extends SdkRequest {
    private final AwsRequestOverrideConfiguration requestOverrideConfig;

    protected AwsRequest(Builder builder) {
        this.requestOverrideConfig = builder.overrideConfiguration();
    }

    public final Optional<AwsRequestOverrideConfiguration> overrideConfiguration() {
        return Optional.ofNullable(this.requestOverrideConfig);
    }

    public abstract Builder toBuilder();

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || ((Object)((Object)this)).getClass() != o.getClass()) {
            return false;
        }
        AwsRequest that = (AwsRequest)((Object)o);
        return Objects.equals((Object)this.requestOverrideConfig, (Object)that.requestOverrideConfig);
    }

    public int hashCode() {
        return Objects.hashCode((Object)this.requestOverrideConfig);
    }

    protected static abstract class BuilderImpl
    implements Builder {
        private AwsRequestOverrideConfiguration awsRequestOverrideConfig;

        protected BuilderImpl() {
        }

        protected BuilderImpl(AwsRequest request) {
            request.overrideConfiguration().ifPresent(this::overrideConfiguration);
        }

        @Override
        public Builder overrideConfiguration(AwsRequestOverrideConfiguration awsRequestOverrideConfig) {
            this.awsRequestOverrideConfig = awsRequestOverrideConfig;
            return this;
        }

        @Override
        public Builder overrideConfiguration(Consumer<AwsRequestOverrideConfiguration.Builder> builderConsumer) {
            AwsRequestOverrideConfiguration.Builder b = AwsRequestOverrideConfiguration.builder();
            builderConsumer.accept(b);
            this.awsRequestOverrideConfig = b.build();
            return this;
        }

        @Override
        public final AwsRequestOverrideConfiguration overrideConfiguration() {
            return this.awsRequestOverrideConfig;
        }
    }

    public static interface Builder
    extends SdkRequest.Builder {
        public AwsRequestOverrideConfiguration overrideConfiguration();

        public Builder overrideConfiguration(AwsRequestOverrideConfiguration var1);

        public Builder overrideConfiguration(Consumer<AwsRequestOverrideConfiguration.Builder> var1);

        public AwsRequest build();
    }
}

