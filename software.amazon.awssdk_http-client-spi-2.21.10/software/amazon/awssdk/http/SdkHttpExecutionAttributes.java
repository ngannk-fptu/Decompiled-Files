/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkPublicApi
 *  software.amazon.awssdk.utils.AttributeMap
 *  software.amazon.awssdk.utils.AttributeMap$Builder
 *  software.amazon.awssdk.utils.Validate
 *  software.amazon.awssdk.utils.builder.CopyableBuilder
 *  software.amazon.awssdk.utils.builder.ToCopyableBuilder
 */
package software.amazon.awssdk.http;

import java.util.Map;
import java.util.Objects;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.http.SdkHttpExecutionAttribute;
import software.amazon.awssdk.utils.AttributeMap;
import software.amazon.awssdk.utils.Validate;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

@SdkPublicApi
public final class SdkHttpExecutionAttributes
implements ToCopyableBuilder<Builder, SdkHttpExecutionAttributes> {
    private final AttributeMap attributes;

    private SdkHttpExecutionAttributes(Builder builder) {
        this.attributes = builder.sdkHttpExecutionAttributes.build();
    }

    public <T> T getAttribute(SdkHttpExecutionAttribute<T> attribute) {
        return (T)this.attributes.get(attribute);
    }

    public static Builder builder() {
        return new Builder();
    }

    public Builder toBuilder() {
        return new Builder(this.attributes);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        SdkHttpExecutionAttributes that = (SdkHttpExecutionAttributes)o;
        return Objects.equals(this.attributes, that.attributes);
    }

    public int hashCode() {
        return this.attributes.hashCode();
    }

    public static final class Builder
    implements CopyableBuilder<Builder, SdkHttpExecutionAttributes> {
        private AttributeMap.Builder sdkHttpExecutionAttributes = AttributeMap.builder();

        private Builder(AttributeMap attributes) {
            this.sdkHttpExecutionAttributes = attributes.toBuilder();
        }

        private Builder() {
        }

        public <T> Builder put(SdkHttpExecutionAttribute<T> key, T value) {
            Validate.notNull(key, (String)"Key to set must not be null.", (Object[])new Object[0]);
            this.sdkHttpExecutionAttributes.put(key, value);
            return this;
        }

        public Builder putAll(Map<? extends SdkHttpExecutionAttribute<?>, ?> attributes) {
            this.sdkHttpExecutionAttributes.putAll(attributes);
            return this;
        }

        public SdkHttpExecutionAttributes build() {
            return new SdkHttpExecutionAttributes(this);
        }
    }
}

