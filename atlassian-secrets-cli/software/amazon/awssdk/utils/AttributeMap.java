/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import software.amazon.awssdk.annotations.Immutable;
import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.utils.IoUtils;
import software.amazon.awssdk.utils.SdkAutoCloseable;
import software.amazon.awssdk.utils.Validate;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

@SdkProtectedApi
@Immutable
public final class AttributeMap
implements ToCopyableBuilder<Builder, AttributeMap>,
SdkAutoCloseable {
    private static final AttributeMap EMPTY = AttributeMap.builder().build();
    private final Map<Key<?>, Object> attributes;

    private AttributeMap(Map<? extends Key<?>, ?> attributes) {
        this.attributes = new HashMap(attributes);
    }

    public <T> boolean containsKey(Key<T> typedKey) {
        return this.attributes.containsKey(typedKey);
    }

    public <T> T get(Key<T> key) {
        Validate.notNull(key, "Key to retrieve must not be null.", new Object[0]);
        return key.convertValue(this.attributes.get(key));
    }

    public AttributeMap merge(AttributeMap lowerPrecedence) {
        HashMap copiedConfiguration = new HashMap(this.attributes);
        lowerPrecedence.attributes.forEach(copiedConfiguration::putIfAbsent);
        return new AttributeMap(copiedConfiguration);
    }

    public static AttributeMap empty() {
        return EMPTY;
    }

    public AttributeMap copy() {
        return this.toBuilder().build();
    }

    @Override
    public void close() {
        this.attributes.values().forEach(this::closeIfPossible);
    }

    private void closeIfPossible(Object object) {
        if (object instanceof ExecutorService) {
            ((ExecutorService)object).shutdown();
        } else {
            IoUtils.closeIfCloseable(object, null);
        }
    }

    public String toString() {
        return this.attributes.toString();
    }

    public int hashCode() {
        return this.attributes.hashCode();
    }

    public boolean equals(Object obj) {
        return obj instanceof AttributeMap && this.attributes.equals(((AttributeMap)obj).attributes);
    }

    @Override
    public Builder toBuilder() {
        return new Builder(this);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder
    implements CopyableBuilder<Builder, AttributeMap> {
        private final Map<Key<?>, Object> configuration = new HashMap();

        private Builder() {
        }

        private Builder(AttributeMap attributeMap) {
            this.configuration.putAll(attributeMap.attributes);
        }

        public <T> T get(Key<T> key) {
            Validate.notNull(key, "Key to retrieve must not be null.", new Object[0]);
            return key.convertValue(this.configuration.get(key));
        }

        public <T> Builder put(Key<T> key, T value) {
            Validate.notNull(key, "Key to set must not be null.", new Object[0]);
            this.configuration.put(key, value);
            return this;
        }

        public Builder putAll(Map<? extends Key<?>, ?> attributes) {
            attributes.forEach((key, value) -> {
                key.validateValue(value);
                this.configuration.put((Key<?>)key, value);
            });
            return this;
        }

        @Override
        public AttributeMap build() {
            return new AttributeMap(this.configuration);
        }
    }

    public static abstract class Key<T> {
        private final Class<?> valueType;

        protected Key(Class<T> valueType) {
            this.valueType = valueType;
        }

        protected Key(UnsafeValueType unsafeValueType) {
            this.valueType = unsafeValueType.valueType;
        }

        final void validateValue(Object value) {
            if (value != null) {
                Validate.isAssignableFrom(this.valueType, value.getClass(), "Invalid option: %s. Required value of type %s, but was %s.", this, this.valueType, value.getClass());
            }
        }

        public final T convertValue(Object value) {
            this.validateValue(value);
            Object result = this.valueType.cast(value);
            return (T)result;
        }

        protected static class UnsafeValueType {
            private final Class<?> valueType;

            public UnsafeValueType(Class<?> valueType) {
                this.valueType = valueType;
            }
        }
    }
}

