/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.core.interceptor;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.utils.Validate;

@SdkPublicApi
public final class ExecutionAttribute<T> {
    private static final ConcurrentMap<String, ExecutionAttribute<?>> NAME_HISTORY = new ConcurrentHashMap();
    private final String name;
    private final ValueStorage<T> storage;

    public ExecutionAttribute(String name) {
        this(name, null);
    }

    private ExecutionAttribute(String name, ValueStorage<T> storage) {
        this.name = name;
        this.storage = storage == null ? new DefaultValueStorage() : storage;
        this.ensureUnique();
    }

    public static <T, U> DerivedAttributeBuilder<T, U> derivedBuilder(String name, Class<T> attributeType, ExecutionAttribute<U> realAttribute) {
        return new DerivedAttributeBuilder(name, realAttribute);
    }

    static <T, U> MappedAttributeBuilder<T, U> mappedBuilder(String name, Supplier<ExecutionAttribute<T>> backingAttributeSupplier, Supplier<ExecutionAttribute<U>> attributeSupplier) {
        return new MappedAttributeBuilder(name, backingAttributeSupplier, attributeSupplier);
    }

    private void ensureUnique() {
        ExecutionAttribute prev = NAME_HISTORY.putIfAbsent(this.name, this);
        if (prev != null) {
            throw new IllegalArgumentException(String.format("No duplicate ExecutionAttribute names allowed but both ExecutionAttributes %s and %s have the same name: %s. ExecutionAttributes should be referenced from a shared static constant to protect against erroneous or unexpected collisions.", Integer.toHexString(System.identityHashCode(prev)), Integer.toHexString(System.identityHashCode(this)), this.name));
        }
    }

    public String toString() {
        return this.name;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        ExecutionAttribute that = (ExecutionAttribute)o;
        return that.name.equals(this.name);
    }

    public int hashCode() {
        return Objects.hashCode(this.name);
    }

    ValueStorage<T> storage() {
        return this.storage;
    }

    protected static final class MappedAttributeBuilder<T, U> {
        private final String name;
        private final Supplier<ExecutionAttribute<T>> backingAttributeSupplier;
        private final Supplier<ExecutionAttribute<U>> attributeSupplier;
        private BiFunction<T, U, T> readMapping;
        private BiFunction<U, T, U> writeMapping;

        private MappedAttributeBuilder(String name, Supplier<ExecutionAttribute<T>> backingAttributeSupplier, Supplier<ExecutionAttribute<U>> attributeSupplier) {
            this.name = name;
            this.backingAttributeSupplier = backingAttributeSupplier;
            this.attributeSupplier = attributeSupplier;
        }

        public MappedAttributeBuilder<T, U> readMapping(BiFunction<T, U, T> readMapping) {
            this.readMapping = readMapping;
            return this;
        }

        public MappedAttributeBuilder<T, U> writeMapping(BiFunction<U, T, U> writeMapping) {
            this.writeMapping = writeMapping;
            return this;
        }

        public ExecutionAttribute<T> build() {
            return new ExecutionAttribute(this.name, new MappedValueStorage(this));
        }
    }

    private static final class MappedValueStorage<T, U>
    implements ValueStorage<T> {
        private final Supplier<ExecutionAttribute<T>> backingAttributeSupplier;
        private final Supplier<ExecutionAttribute<U>> attributeSupplier;
        private final BiFunction<T, U, T> readMapping;
        private final BiFunction<U, T, U> writeMapping;

        private MappedValueStorage(MappedAttributeBuilder<T, U> builder) {
            this.backingAttributeSupplier = Validate.paramNotNull(((MappedAttributeBuilder)builder).backingAttributeSupplier, "backingAttributeSupplier");
            this.attributeSupplier = Validate.paramNotNull(((MappedAttributeBuilder)builder).attributeSupplier, "attributeSupplier");
            this.readMapping = Validate.paramNotNull(((MappedAttributeBuilder)builder).readMapping, "readMapping");
            this.writeMapping = Validate.paramNotNull(((MappedAttributeBuilder)builder).writeMapping, "writeMapping");
        }

        @Override
        public T get(Map<ExecutionAttribute<?>, Object> attributes) {
            return this.readMapping.apply(attributes.get(this.backingAttributeSupplier.get()), attributes.get(this.attributeSupplier.get()));
        }

        @Override
        public void set(Map<ExecutionAttribute<?>, Object> attributes, T value) {
            attributes.put(this.backingAttributeSupplier.get(), value);
            attributes.compute(this.attributeSupplier.get(), (k, attr) -> this.writeMapping.apply(attr, value));
        }

        @Override
        public void setIfAbsent(Map<ExecutionAttribute<?>, Object> attributes, T value) {
            T currentValue = this.get(attributes);
            if (currentValue == null) {
                this.set(attributes, value);
            }
        }
    }

    private static final class DerivationValueStorage<T, U>
    implements ValueStorage<T> {
        private final ExecutionAttribute<U> realAttribute;
        private final Function<U, T> readMapping;
        private final BiFunction<U, T, U> writeMapping;

        private DerivationValueStorage(DerivedAttributeBuilder<T, U> builder) {
            this.realAttribute = Validate.paramNotNull(((DerivedAttributeBuilder)builder).realAttribute, "realAttribute");
            this.readMapping = Validate.paramNotNull(((DerivedAttributeBuilder)builder).readMapping, "readMapping");
            this.writeMapping = Validate.paramNotNull(((DerivedAttributeBuilder)builder).writeMapping, "writeMapping");
        }

        @Override
        public T get(Map<ExecutionAttribute<?>, Object> attributes) {
            return this.readMapping.apply(attributes.get(this.realAttribute));
        }

        @Override
        public void set(Map<ExecutionAttribute<?>, Object> attributes, T value) {
            attributes.compute(this.realAttribute, (k, real) -> this.writeMapping.apply(real, value));
        }

        @Override
        public void setIfAbsent(Map<ExecutionAttribute<?>, Object> attributes, T value) {
            T currentValue = this.get(attributes);
            if (currentValue == null) {
                this.set(attributes, value);
            }
        }
    }

    private final class DefaultValueStorage
    implements ValueStorage<T> {
        private DefaultValueStorage() {
        }

        @Override
        public T get(Map<ExecutionAttribute<?>, Object> attributes) {
            return attributes.get(ExecutionAttribute.this);
        }

        @Override
        public void set(Map<ExecutionAttribute<?>, Object> attributes, T value) {
            attributes.put(ExecutionAttribute.this, value);
        }

        @Override
        public void setIfAbsent(Map<ExecutionAttribute<?>, Object> attributes, T value) {
            attributes.putIfAbsent(ExecutionAttribute.this, value);
        }
    }

    static interface ValueStorage<T> {
        public T get(Map<ExecutionAttribute<?>, Object> var1);

        public void set(Map<ExecutionAttribute<?>, Object> var1, T var2);

        public void setIfAbsent(Map<ExecutionAttribute<?>, Object> var1, T var2);
    }

    public static final class DerivedAttributeBuilder<T, U> {
        private final String name;
        private final ExecutionAttribute<U> realAttribute;
        private Function<U, T> readMapping;
        private BiFunction<U, T, U> writeMapping;

        private DerivedAttributeBuilder(String name, ExecutionAttribute<U> realAttribute) {
            this.name = name;
            this.realAttribute = realAttribute;
        }

        public DerivedAttributeBuilder<T, U> readMapping(Function<U, T> readMapping) {
            this.readMapping = readMapping;
            return this;
        }

        public DerivedAttributeBuilder<T, U> writeMapping(BiFunction<U, T, U> writeMapping) {
            this.writeMapping = writeMapping;
            return this;
        }

        public ExecutionAttribute<T> build() {
            return new ExecutionAttribute(this.name, new DerivationValueStorage(this));
        }
    }
}

