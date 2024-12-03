/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.core.SdkField
 *  software.amazon.awssdk.core.SdkPojo
 *  software.amazon.awssdk.core.protocol.MarshallLocation
 *  software.amazon.awssdk.core.protocol.MarshallingType
 *  software.amazon.awssdk.core.traits.LocationTrait
 *  software.amazon.awssdk.core.traits.Trait
 *  software.amazon.awssdk.utils.ToString
 *  software.amazon.awssdk.utils.builder.CopyableBuilder
 *  software.amazon.awssdk.utils.builder.ToCopyableBuilder
 */
package software.amazon.awssdk.services.s3.model;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import software.amazon.awssdk.core.SdkField;
import software.amazon.awssdk.core.SdkPojo;
import software.amazon.awssdk.core.protocol.MarshallLocation;
import software.amazon.awssdk.core.protocol.MarshallingType;
import software.amazon.awssdk.core.traits.LocationTrait;
import software.amazon.awssdk.core.traits.Trait;
import software.amazon.awssdk.services.s3.model.IntelligentTieringAndOperator;
import software.amazon.awssdk.services.s3.model.Tag;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

public final class IntelligentTieringFilter
implements SdkPojo,
Serializable,
ToCopyableBuilder<Builder, IntelligentTieringFilter> {
    private static final SdkField<String> PREFIX_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("Prefix").getter(IntelligentTieringFilter.getter(IntelligentTieringFilter::prefix)).setter(IntelligentTieringFilter.setter(Builder::prefix)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("Prefix").unmarshallLocationName("Prefix").build()}).build();
    private static final SdkField<Tag> TAG_FIELD = SdkField.builder((MarshallingType)MarshallingType.SDK_POJO).memberName("Tag").getter(IntelligentTieringFilter.getter(IntelligentTieringFilter::tag)).setter(IntelligentTieringFilter.setter(Builder::tag)).constructor(Tag::builder).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("Tag").unmarshallLocationName("Tag").build()}).build();
    private static final SdkField<IntelligentTieringAndOperator> AND_FIELD = SdkField.builder((MarshallingType)MarshallingType.SDK_POJO).memberName("And").getter(IntelligentTieringFilter.getter(IntelligentTieringFilter::and)).setter(IntelligentTieringFilter.setter(Builder::and)).constructor(IntelligentTieringAndOperator::builder).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("And").unmarshallLocationName("And").build()}).build();
    private static final List<SdkField<?>> SDK_FIELDS = Collections.unmodifiableList(Arrays.asList(PREFIX_FIELD, TAG_FIELD, AND_FIELD));
    private static final long serialVersionUID = 1L;
    private final String prefix;
    private final Tag tag;
    private final IntelligentTieringAndOperator and;

    private IntelligentTieringFilter(BuilderImpl builder) {
        this.prefix = builder.prefix;
        this.tag = builder.tag;
        this.and = builder.and;
    }

    public final String prefix() {
        return this.prefix;
    }

    public final Tag tag() {
        return this.tag;
    }

    public final IntelligentTieringAndOperator and() {
        return this.and;
    }

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
        hashCode = 31 * hashCode + Objects.hashCode(this.prefix());
        hashCode = 31 * hashCode + Objects.hashCode(this.tag());
        hashCode = 31 * hashCode + Objects.hashCode(this.and());
        return hashCode;
    }

    public final boolean equals(Object obj) {
        return this.equalsBySdkFields(obj);
    }

    public final boolean equalsBySdkFields(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof IntelligentTieringFilter)) {
            return false;
        }
        IntelligentTieringFilter other = (IntelligentTieringFilter)obj;
        return Objects.equals(this.prefix(), other.prefix()) && Objects.equals(this.tag(), other.tag()) && Objects.equals(this.and(), other.and());
    }

    public final String toString() {
        return ToString.builder((String)"IntelligentTieringFilter").add("Prefix", (Object)this.prefix()).add("Tag", (Object)this.tag()).add("And", (Object)this.and()).build();
    }

    public final <T> Optional<T> getValueForField(String fieldName, Class<T> clazz) {
        switch (fieldName) {
            case "Prefix": {
                return Optional.ofNullable(clazz.cast(this.prefix()));
            }
            case "Tag": {
                return Optional.ofNullable(clazz.cast(this.tag()));
            }
            case "And": {
                return Optional.ofNullable(clazz.cast(this.and()));
            }
        }
        return Optional.empty();
    }

    public final List<SdkField<?>> sdkFields() {
        return SDK_FIELDS;
    }

    private static <T> Function<Object, T> getter(Function<IntelligentTieringFilter, T> g) {
        return obj -> g.apply((IntelligentTieringFilter)obj);
    }

    private static <T> BiConsumer<Object, T> setter(BiConsumer<Builder, T> s) {
        return (obj, val) -> s.accept((Builder)obj, val);
    }

    static final class BuilderImpl
    implements Builder {
        private String prefix;
        private Tag tag;
        private IntelligentTieringAndOperator and;

        private BuilderImpl() {
        }

        private BuilderImpl(IntelligentTieringFilter model) {
            this.prefix(model.prefix);
            this.tag(model.tag);
            this.and(model.and);
        }

        public final String getPrefix() {
            return this.prefix;
        }

        public final void setPrefix(String prefix) {
            this.prefix = prefix;
        }

        @Override
        public final Builder prefix(String prefix) {
            this.prefix = prefix;
            return this;
        }

        public final Tag.Builder getTag() {
            return this.tag != null ? this.tag.toBuilder() : null;
        }

        public final void setTag(Tag.BuilderImpl tag) {
            this.tag = tag != null ? tag.build() : null;
        }

        @Override
        public final Builder tag(Tag tag) {
            this.tag = tag;
            return this;
        }

        public final IntelligentTieringAndOperator.Builder getAnd() {
            return this.and != null ? this.and.toBuilder() : null;
        }

        public final void setAnd(IntelligentTieringAndOperator.BuilderImpl and) {
            this.and = and != null ? and.build() : null;
        }

        @Override
        public final Builder and(IntelligentTieringAndOperator and) {
            this.and = and;
            return this;
        }

        public IntelligentTieringFilter build() {
            return new IntelligentTieringFilter(this);
        }

        public List<SdkField<?>> sdkFields() {
            return SDK_FIELDS;
        }
    }

    public static interface Builder
    extends SdkPojo,
    CopyableBuilder<Builder, IntelligentTieringFilter> {
        public Builder prefix(String var1);

        public Builder tag(Tag var1);

        default public Builder tag(Consumer<Tag.Builder> tag) {
            return this.tag((Tag)((Tag.Builder)Tag.builder().applyMutation(tag)).build());
        }

        public Builder and(IntelligentTieringAndOperator var1);

        default public Builder and(Consumer<IntelligentTieringAndOperator.Builder> and) {
            return this.and((IntelligentTieringAndOperator)((IntelligentTieringAndOperator.Builder)IntelligentTieringAndOperator.builder().applyMutation(and)).build());
        }
    }
}

