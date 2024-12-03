/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.core.client.config;

import java.util.function.Consumer;
import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.core.client.config.ClientOption;
import software.amazon.awssdk.utils.AttributeMap;
import software.amazon.awssdk.utils.SdkAutoCloseable;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

@SdkProtectedApi
public final class SdkClientConfiguration
implements ToCopyableBuilder<Builder, SdkClientConfiguration>,
SdkAutoCloseable {
    private final AttributeMap attributes;

    private SdkClientConfiguration(AttributeMap attributes) {
        this.attributes = attributes;
    }

    public static Builder builder() {
        return new Builder(AttributeMap.builder());
    }

    public <T> T option(ClientOption<T> option) {
        return this.attributes.get(option);
    }

    public SdkClientConfiguration merge(SdkClientConfiguration configuration) {
        return new SdkClientConfiguration(this.attributes.merge(configuration.attributes));
    }

    public SdkClientConfiguration merge(Consumer<Builder> configuration) {
        return this.merge(SdkClientConfiguration.builder().applyMutation(configuration).build());
    }

    @Override
    public Builder toBuilder() {
        return new Builder(this.attributes.toBuilder());
    }

    @Override
    public void close() {
        this.attributes.close();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        SdkClientConfiguration that = (SdkClientConfiguration)o;
        return this.attributes.equals(that.attributes);
    }

    public int hashCode() {
        return this.attributes.hashCode();
    }

    public static final class Builder
    implements CopyableBuilder<Builder, SdkClientConfiguration> {
        private final AttributeMap.Builder attributes;

        private Builder(AttributeMap.Builder attributes) {
            this.attributes = attributes;
        }

        public <T> Builder option(ClientOption<T> option, T value) {
            this.attributes.put(option, value);
            return this;
        }

        public <T> T option(ClientOption<T> option) {
            return this.attributes.get(option);
        }

        @Override
        public SdkClientConfiguration build() {
            return new SdkClientConfiguration(this.attributes.build());
        }
    }
}

