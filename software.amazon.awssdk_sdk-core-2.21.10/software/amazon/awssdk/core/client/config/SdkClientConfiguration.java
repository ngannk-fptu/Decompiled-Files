/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkProtectedApi
 *  software.amazon.awssdk.utils.AttributeMap
 *  software.amazon.awssdk.utils.AttributeMap$Builder
 *  software.amazon.awssdk.utils.SdkAutoCloseable
 *  software.amazon.awssdk.utils.builder.CopyableBuilder
 *  software.amazon.awssdk.utils.builder.ToCopyableBuilder
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
        return (T)this.attributes.get(option);
    }

    public SdkClientConfiguration merge(SdkClientConfiguration configuration) {
        return new SdkClientConfiguration(this.attributes.merge(configuration.attributes));
    }

    public SdkClientConfiguration merge(Consumer<Builder> configuration) {
        return this.merge(((Builder)SdkClientConfiguration.builder().applyMutation(configuration)).build());
    }

    public Builder toBuilder() {
        return new Builder(this.attributes.toBuilder());
    }

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
        return this.attributes.equals((Object)that.attributes);
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
            return (T)this.attributes.get(option);
        }

        public SdkClientConfiguration build() {
            return new SdkClientConfiguration(this.attributes.build());
        }
    }
}

