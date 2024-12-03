/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.NotThreadSafe
 *  software.amazon.awssdk.annotations.SdkPublicApi
 *  software.amazon.awssdk.utils.ToString
 *  software.amazon.awssdk.utils.Validate
 *  software.amazon.awssdk.utils.builder.CopyableBuilder
 *  software.amazon.awssdk.utils.builder.ToCopyableBuilder
 */
package software.amazon.awssdk.core.interceptor;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import software.amazon.awssdk.annotations.NotThreadSafe;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.core.interceptor.ExecutionAttribute;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.Validate;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

@SdkPublicApi
@NotThreadSafe
public class ExecutionAttributes
implements ToCopyableBuilder<Builder, ExecutionAttributes> {
    private final Map<ExecutionAttribute<?>, Object> attributes;

    public ExecutionAttributes() {
        this.attributes = new HashMap(32);
    }

    protected ExecutionAttributes(Map<? extends ExecutionAttribute<?>, ?> attributes) {
        this.attributes = new HashMap(attributes);
    }

    public <U> U getAttribute(ExecutionAttribute<U> attribute) {
        return attribute.storage().get(this.attributes);
    }

    public Map<ExecutionAttribute<?>, Object> getAttributes() {
        return Collections.unmodifiableMap(this.attributes);
    }

    public <U> Optional<U> getOptionalAttribute(ExecutionAttribute<U> attribute) {
        return Optional.ofNullable(this.getAttribute(attribute));
    }

    public <U> ExecutionAttributes putAttribute(ExecutionAttribute<U> attribute, U value) {
        attribute.storage().set(this.attributes, value);
        return this;
    }

    public <U> ExecutionAttributes putAttributeIfAbsent(ExecutionAttribute<U> attribute, U value) {
        attribute.storage().setIfAbsent(this.attributes, value);
        return this;
    }

    public ExecutionAttributes merge(ExecutionAttributes lowerPrecedenceExecutionAttributes) {
        HashMap copiedAttributes = new HashMap(this.attributes);
        lowerPrecedenceExecutionAttributes.getAttributes().forEach(copiedAttributes::putIfAbsent);
        return new ExecutionAttributes(copiedAttributes);
    }

    public void putAbsentAttributes(ExecutionAttributes lowerPrecedenceExecutionAttributes) {
        if (lowerPrecedenceExecutionAttributes != null) {
            lowerPrecedenceExecutionAttributes.getAttributes().forEach(this.attributes::putIfAbsent);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public Builder toBuilder() {
        return new Builder(this);
    }

    public ExecutionAttributes copy() {
        return this.toBuilder().build();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        ExecutionAttributes that = (ExecutionAttributes)o;
        return this.attributes != null ? this.attributes.equals(that.attributes) : that.attributes == null;
    }

    public int hashCode() {
        return this.attributes != null ? this.attributes.hashCode() : 0;
    }

    public String toString() {
        return ToString.builder((String)"ExecutionAttributes").add("attributes", this.attributes.keySet()).build();
    }

    public static ExecutionAttributes unmodifiableExecutionAttributes(ExecutionAttributes attributes) {
        return new UnmodifiableExecutionAttributes(attributes);
    }

    public static final class Builder
    implements CopyableBuilder<Builder, ExecutionAttributes> {
        private final Map<ExecutionAttribute<?>, Object> executionAttributes = new HashMap(32);

        private Builder() {
        }

        private Builder(ExecutionAttributes source) {
            this.executionAttributes.putAll(source.attributes);
        }

        public <T> Builder put(ExecutionAttribute<T> key, T value) {
            Validate.notNull(key, (String)"Key to set must not be null.", (Object[])new Object[0]);
            key.storage().set(this.executionAttributes, value);
            return this;
        }

        public Builder putAll(Map<? extends ExecutionAttribute<?>, ?> attributes) {
            attributes.forEach(this::unsafePut);
            return this;
        }

        private <T> void unsafePut(ExecutionAttribute<T> key, Object value) {
            key.storage().set(this.executionAttributes, value);
        }

        public ExecutionAttributes build() {
            return new ExecutionAttributes(this.executionAttributes);
        }
    }

    private static class UnmodifiableExecutionAttributes
    extends ExecutionAttributes {
        UnmodifiableExecutionAttributes(ExecutionAttributes executionAttributes) {
            super(executionAttributes.attributes);
        }

        @Override
        public <U> ExecutionAttributes putAttribute(ExecutionAttribute<U> attribute, U value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public <U> ExecutionAttributes putAttributeIfAbsent(ExecutionAttribute<U> attribute, U value) {
            throw new UnsupportedOperationException();
        }
    }
}

