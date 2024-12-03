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
 *  software.amazon.awssdk.core.util.SdkAutoConstructList
 *  software.amazon.awssdk.core.util.SdkAutoConstructMap
 *  software.amazon.awssdk.utils.ToString
 *  software.amazon.awssdk.utils.builder.CopyableBuilder
 *  software.amazon.awssdk.utils.builder.ToCopyableBuilder
 */
package software.amazon.awssdk.services.s3.model;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import software.amazon.awssdk.core.SdkField;
import software.amazon.awssdk.core.SdkPojo;
import software.amazon.awssdk.core.protocol.MarshallLocation;
import software.amazon.awssdk.core.protocol.MarshallingType;
import software.amazon.awssdk.core.traits.LocationTrait;
import software.amazon.awssdk.core.traits.Trait;
import software.amazon.awssdk.core.util.SdkAutoConstructList;
import software.amazon.awssdk.core.util.SdkAutoConstructMap;
import software.amazon.awssdk.services.s3.model.AnalyticsAndOperator;
import software.amazon.awssdk.services.s3.model.Tag;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

public final class AnalyticsFilter
implements SdkPojo,
Serializable,
ToCopyableBuilder<Builder, AnalyticsFilter> {
    private static final SdkField<String> PREFIX_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("Prefix").getter(AnalyticsFilter.getter(AnalyticsFilter::prefix)).setter(AnalyticsFilter.setter(Builder::prefix)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("Prefix").unmarshallLocationName("Prefix").build()}).build();
    private static final SdkField<Tag> TAG_FIELD = SdkField.builder((MarshallingType)MarshallingType.SDK_POJO).memberName("Tag").getter(AnalyticsFilter.getter(AnalyticsFilter::tag)).setter(AnalyticsFilter.setter(Builder::tag)).constructor(Tag::builder).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("Tag").unmarshallLocationName("Tag").build()}).build();
    private static final SdkField<AnalyticsAndOperator> AND_FIELD = SdkField.builder((MarshallingType)MarshallingType.SDK_POJO).memberName("And").getter(AnalyticsFilter.getter(AnalyticsFilter::and)).setter(AnalyticsFilter.setter(Builder::and)).constructor(AnalyticsAndOperator::builder).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("And").unmarshallLocationName("And").build()}).build();
    private static final List<SdkField<?>> SDK_FIELDS = Collections.unmodifiableList(Arrays.asList(PREFIX_FIELD, TAG_FIELD, AND_FIELD));
    private static final long serialVersionUID = 1L;
    private final String prefix;
    private final Tag tag;
    private final AnalyticsAndOperator and;
    private final Type type;

    private AnalyticsFilter(BuilderImpl builder) {
        this.prefix = builder.prefix;
        this.tag = builder.tag;
        this.and = builder.and;
        this.type = builder.type;
    }

    public final String prefix() {
        return this.prefix;
    }

    public final Tag tag() {
        return this.tag;
    }

    public final AnalyticsAndOperator and() {
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
        if (!(obj instanceof AnalyticsFilter)) {
            return false;
        }
        AnalyticsFilter other = (AnalyticsFilter)obj;
        return Objects.equals(this.prefix(), other.prefix()) && Objects.equals(this.tag(), other.tag()) && Objects.equals(this.and(), other.and());
    }

    public final String toString() {
        return ToString.builder((String)"AnalyticsFilter").add("Prefix", (Object)this.prefix()).add("Tag", (Object)this.tag()).add("And", (Object)this.and()).build();
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

    public static AnalyticsFilter fromPrefix(String prefix) {
        return (AnalyticsFilter)AnalyticsFilter.builder().prefix(prefix).build();
    }

    public static AnalyticsFilter fromTag(Tag tag) {
        return (AnalyticsFilter)AnalyticsFilter.builder().tag(tag).build();
    }

    public static AnalyticsFilter fromTag(Consumer<Tag.Builder> tag) {
        Tag.Builder builder = Tag.builder();
        tag.accept(builder);
        return AnalyticsFilter.fromTag((Tag)builder.build());
    }

    public static AnalyticsFilter fromAnd(AnalyticsAndOperator and) {
        return (AnalyticsFilter)AnalyticsFilter.builder().and(and).build();
    }

    public static AnalyticsFilter fromAnd(Consumer<AnalyticsAndOperator.Builder> and) {
        AnalyticsAndOperator.Builder builder = AnalyticsAndOperator.builder();
        and.accept(builder);
        return AnalyticsFilter.fromAnd((AnalyticsAndOperator)builder.build());
    }

    public Type type() {
        return this.type;
    }

    public final List<SdkField<?>> sdkFields() {
        return SDK_FIELDS;
    }

    private static <T> Function<Object, T> getter(Function<AnalyticsFilter, T> g) {
        return obj -> g.apply((AnalyticsFilter)obj);
    }

    private static <T> BiConsumer<Object, T> setter(BiConsumer<Builder, T> s) {
        return (obj, val) -> s.accept((Builder)obj, val);
    }

    public static enum Type {
        PREFIX,
        TAG,
        AND,
        UNKNOWN_TO_SDK_VERSION;

    }

    static final class BuilderImpl
    implements Builder {
        private String prefix;
        private Tag tag;
        private AnalyticsAndOperator and;
        private Type type = Type.UNKNOWN_TO_SDK_VERSION;
        private Set<Type> setTypes = EnumSet.noneOf(Type.class);

        private BuilderImpl() {
        }

        private BuilderImpl(AnalyticsFilter model) {
            this.prefix(model.prefix);
            this.tag(model.tag);
            this.and(model.and);
        }

        public final String getPrefix() {
            return this.prefix;
        }

        public final void setPrefix(String prefix) {
            String oldValue = this.prefix;
            this.prefix = prefix;
            this.handleUnionValueChange(Type.PREFIX, oldValue, this.prefix);
        }

        @Override
        public final Builder prefix(String prefix) {
            String oldValue = this.prefix;
            this.prefix = prefix;
            this.handleUnionValueChange(Type.PREFIX, oldValue, this.prefix);
            return this;
        }

        public final Tag.Builder getTag() {
            return this.tag != null ? this.tag.toBuilder() : null;
        }

        public final void setTag(Tag.BuilderImpl tag) {
            Tag oldValue = this.tag;
            this.tag = tag != null ? tag.build() : null;
            this.handleUnionValueChange(Type.TAG, oldValue, this.tag);
        }

        @Override
        public final Builder tag(Tag tag) {
            Tag oldValue = this.tag;
            this.tag = tag;
            this.handleUnionValueChange(Type.TAG, oldValue, this.tag);
            return this;
        }

        public final AnalyticsAndOperator.Builder getAnd() {
            return this.and != null ? this.and.toBuilder() : null;
        }

        public final void setAnd(AnalyticsAndOperator.BuilderImpl and) {
            AnalyticsAndOperator oldValue = this.and;
            this.and = and != null ? and.build() : null;
            this.handleUnionValueChange(Type.AND, oldValue, this.and);
        }

        @Override
        public final Builder and(AnalyticsAndOperator and) {
            AnalyticsAndOperator oldValue = this.and;
            this.and = and;
            this.handleUnionValueChange(Type.AND, oldValue, this.and);
            return this;
        }

        public AnalyticsFilter build() {
            return new AnalyticsFilter(this);
        }

        public List<SdkField<?>> sdkFields() {
            return SDK_FIELDS;
        }

        private final void handleUnionValueChange(Type type, Object oldValue, Object newValue) {
            if (this.type == type || oldValue == newValue) {
                return;
            }
            if (newValue == null || newValue instanceof SdkAutoConstructList || newValue instanceof SdkAutoConstructMap) {
                this.setTypes.remove((Object)type);
            } else if (oldValue == null || oldValue instanceof SdkAutoConstructList || oldValue instanceof SdkAutoConstructMap) {
                this.setTypes.add(type);
            }
            this.type = this.setTypes.size() == 1 ? this.setTypes.iterator().next() : (this.setTypes.isEmpty() ? Type.UNKNOWN_TO_SDK_VERSION : null);
        }
    }

    public static interface Builder
    extends SdkPojo,
    CopyableBuilder<Builder, AnalyticsFilter> {
        public Builder prefix(String var1);

        public Builder tag(Tag var1);

        default public Builder tag(Consumer<Tag.Builder> tag) {
            return this.tag((Tag)((Tag.Builder)Tag.builder().applyMutation(tag)).build());
        }

        public Builder and(AnalyticsAndOperator var1);

        default public Builder and(Consumer<AnalyticsAndOperator.Builder> and) {
            return this.and((AnalyticsAndOperator)((AnalyticsAndOperator.Builder)AnalyticsAndOperator.builder().applyMutation(and)).build());
        }
    }
}

