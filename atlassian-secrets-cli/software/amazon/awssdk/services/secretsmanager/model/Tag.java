/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.services.secretsmanager.model;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;
import software.amazon.awssdk.core.SdkField;
import software.amazon.awssdk.core.SdkPojo;
import software.amazon.awssdk.core.protocol.MarshallLocation;
import software.amazon.awssdk.core.protocol.MarshallingType;
import software.amazon.awssdk.core.traits.LocationTrait;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

public final class Tag
implements SdkPojo,
Serializable,
ToCopyableBuilder<Builder, Tag> {
    private static final SdkField<String> KEY_FIELD = SdkField.builder(MarshallingType.STRING).memberName("Key").getter(Tag.getter(Tag::key)).setter(Tag.setter(Builder::key)).traits(LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("Key").build()).build();
    private static final SdkField<String> VALUE_FIELD = SdkField.builder(MarshallingType.STRING).memberName("Value").getter(Tag.getter(Tag::value)).setter(Tag.setter(Builder::value)).traits(LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("Value").build()).build();
    private static final List<SdkField<?>> SDK_FIELDS = Collections.unmodifiableList(Arrays.asList(KEY_FIELD, VALUE_FIELD));
    private static final long serialVersionUID = 1L;
    private final String key;
    private final String value;

    private Tag(BuilderImpl builder) {
        this.key = builder.key;
        this.value = builder.value;
    }

    public final String key() {
        return this.key;
    }

    public final String value() {
        return this.value;
    }

    @Override
    public Builder toBuilder() {
        return new BuilderImpl(this);
    }

    public static Builder builder() {
        return new BuilderImpl();
    }

    public static Class<? extends Builder> serializableBuilderClass() {
        return BuilderImpl.class;
    }

    public final int hashCode() {
        int hashCode = 1;
        hashCode = 31 * hashCode + Objects.hashCode(this.key());
        hashCode = 31 * hashCode + Objects.hashCode(this.value());
        return hashCode;
    }

    public final boolean equals(Object obj) {
        return this.equalsBySdkFields(obj);
    }

    @Override
    public final boolean equalsBySdkFields(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Tag)) {
            return false;
        }
        Tag other = (Tag)obj;
        return Objects.equals(this.key(), other.key()) && Objects.equals(this.value(), other.value());
    }

    public final String toString() {
        return ToString.builder("Tag").add("Key", this.key()).add("Value", this.value()).build();
    }

    public final <T> Optional<T> getValueForField(String fieldName, Class<T> clazz) {
        switch (fieldName) {
            case "Key": {
                return Optional.ofNullable(clazz.cast(this.key()));
            }
            case "Value": {
                return Optional.ofNullable(clazz.cast(this.value()));
            }
        }
        return Optional.empty();
    }

    @Override
    public final List<SdkField<?>> sdkFields() {
        return SDK_FIELDS;
    }

    private static <T> Function<Object, T> getter(Function<Tag, T> g) {
        return obj -> g.apply((Tag)obj);
    }

    private static <T> BiConsumer<Object, T> setter(BiConsumer<Builder, T> s) {
        return (obj, val) -> s.accept((Builder)obj, val);
    }

    static final class BuilderImpl
    implements Builder {
        private String key;
        private String value;

        private BuilderImpl() {
        }

        private BuilderImpl(Tag model) {
            this.key(model.key);
            this.value(model.value);
        }

        public final String getKey() {
            return this.key;
        }

        public final void setKey(String key) {
            this.key = key;
        }

        @Override
        public final Builder key(String key) {
            this.key = key;
            return this;
        }

        public final String getValue() {
            return this.value;
        }

        public final void setValue(String value) {
            this.value = value;
        }

        @Override
        public final Builder value(String value) {
            this.value = value;
            return this;
        }

        @Override
        public Tag build() {
            return new Tag(this);
        }

        @Override
        public List<SdkField<?>> sdkFields() {
            return SDK_FIELDS;
        }
    }

    public static interface Builder
    extends SdkPojo,
    CopyableBuilder<Builder, Tag> {
        public Builder key(String var1);

        public Builder value(String var1);
    }
}

