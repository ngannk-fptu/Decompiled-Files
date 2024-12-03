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
import software.amazon.awssdk.services.s3.model.LifecycleRuleAndOperator;
import software.amazon.awssdk.services.s3.model.Tag;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

public final class LifecycleRuleFilter
implements SdkPojo,
Serializable,
ToCopyableBuilder<Builder, LifecycleRuleFilter> {
    private static final SdkField<String> PREFIX_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("Prefix").getter(LifecycleRuleFilter.getter(LifecycleRuleFilter::prefix)).setter(LifecycleRuleFilter.setter(Builder::prefix)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("Prefix").unmarshallLocationName("Prefix").build()}).build();
    private static final SdkField<Tag> TAG_FIELD = SdkField.builder((MarshallingType)MarshallingType.SDK_POJO).memberName("Tag").getter(LifecycleRuleFilter.getter(LifecycleRuleFilter::tag)).setter(LifecycleRuleFilter.setter(Builder::tag)).constructor(Tag::builder).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("Tag").unmarshallLocationName("Tag").build()}).build();
    private static final SdkField<Long> OBJECT_SIZE_GREATER_THAN_FIELD = SdkField.builder((MarshallingType)MarshallingType.LONG).memberName("ObjectSizeGreaterThan").getter(LifecycleRuleFilter.getter(LifecycleRuleFilter::objectSizeGreaterThan)).setter(LifecycleRuleFilter.setter(Builder::objectSizeGreaterThan)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("ObjectSizeGreaterThan").unmarshallLocationName("ObjectSizeGreaterThan").build()}).build();
    private static final SdkField<Long> OBJECT_SIZE_LESS_THAN_FIELD = SdkField.builder((MarshallingType)MarshallingType.LONG).memberName("ObjectSizeLessThan").getter(LifecycleRuleFilter.getter(LifecycleRuleFilter::objectSizeLessThan)).setter(LifecycleRuleFilter.setter(Builder::objectSizeLessThan)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("ObjectSizeLessThan").unmarshallLocationName("ObjectSizeLessThan").build()}).build();
    private static final SdkField<LifecycleRuleAndOperator> AND_FIELD = SdkField.builder((MarshallingType)MarshallingType.SDK_POJO).memberName("And").getter(LifecycleRuleFilter.getter(LifecycleRuleFilter::and)).setter(LifecycleRuleFilter.setter(Builder::and)).constructor(LifecycleRuleAndOperator::builder).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("And").unmarshallLocationName("And").build()}).build();
    private static final List<SdkField<?>> SDK_FIELDS = Collections.unmodifiableList(Arrays.asList(PREFIX_FIELD, TAG_FIELD, OBJECT_SIZE_GREATER_THAN_FIELD, OBJECT_SIZE_LESS_THAN_FIELD, AND_FIELD));
    private static final long serialVersionUID = 1L;
    private final String prefix;
    private final Tag tag;
    private final Long objectSizeGreaterThan;
    private final Long objectSizeLessThan;
    private final LifecycleRuleAndOperator and;
    private final Type type;

    private LifecycleRuleFilter(BuilderImpl builder) {
        this.prefix = builder.prefix;
        this.tag = builder.tag;
        this.objectSizeGreaterThan = builder.objectSizeGreaterThan;
        this.objectSizeLessThan = builder.objectSizeLessThan;
        this.and = builder.and;
        this.type = builder.type;
    }

    public final String prefix() {
        return this.prefix;
    }

    public final Tag tag() {
        return this.tag;
    }

    public final Long objectSizeGreaterThan() {
        return this.objectSizeGreaterThan;
    }

    public final Long objectSizeLessThan() {
        return this.objectSizeLessThan;
    }

    public final LifecycleRuleAndOperator and() {
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
        hashCode = 31 * hashCode + Objects.hashCode(this.objectSizeGreaterThan());
        hashCode = 31 * hashCode + Objects.hashCode(this.objectSizeLessThan());
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
        if (!(obj instanceof LifecycleRuleFilter)) {
            return false;
        }
        LifecycleRuleFilter other = (LifecycleRuleFilter)obj;
        return Objects.equals(this.prefix(), other.prefix()) && Objects.equals(this.tag(), other.tag()) && Objects.equals(this.objectSizeGreaterThan(), other.objectSizeGreaterThan()) && Objects.equals(this.objectSizeLessThan(), other.objectSizeLessThan()) && Objects.equals(this.and(), other.and());
    }

    public final String toString() {
        return ToString.builder((String)"LifecycleRuleFilter").add("Prefix", (Object)this.prefix()).add("Tag", (Object)this.tag()).add("ObjectSizeGreaterThan", (Object)this.objectSizeGreaterThan()).add("ObjectSizeLessThan", (Object)this.objectSizeLessThan()).add("And", (Object)this.and()).build();
    }

    public final <T> Optional<T> getValueForField(String fieldName, Class<T> clazz) {
        switch (fieldName) {
            case "Prefix": {
                return Optional.ofNullable(clazz.cast(this.prefix()));
            }
            case "Tag": {
                return Optional.ofNullable(clazz.cast(this.tag()));
            }
            case "ObjectSizeGreaterThan": {
                return Optional.ofNullable(clazz.cast(this.objectSizeGreaterThan()));
            }
            case "ObjectSizeLessThan": {
                return Optional.ofNullable(clazz.cast(this.objectSizeLessThan()));
            }
            case "And": {
                return Optional.ofNullable(clazz.cast(this.and()));
            }
        }
        return Optional.empty();
    }

    public static LifecycleRuleFilter fromPrefix(String prefix) {
        return (LifecycleRuleFilter)LifecycleRuleFilter.builder().prefix(prefix).build();
    }

    public static LifecycleRuleFilter fromTag(Tag tag) {
        return (LifecycleRuleFilter)LifecycleRuleFilter.builder().tag(tag).build();
    }

    public static LifecycleRuleFilter fromTag(Consumer<Tag.Builder> tag) {
        Tag.Builder builder = Tag.builder();
        tag.accept(builder);
        return LifecycleRuleFilter.fromTag((Tag)builder.build());
    }

    public static LifecycleRuleFilter fromObjectSizeGreaterThan(Long objectSizeGreaterThan) {
        return (LifecycleRuleFilter)LifecycleRuleFilter.builder().objectSizeGreaterThan(objectSizeGreaterThan).build();
    }

    public static LifecycleRuleFilter fromObjectSizeLessThan(Long objectSizeLessThan) {
        return (LifecycleRuleFilter)LifecycleRuleFilter.builder().objectSizeLessThan(objectSizeLessThan).build();
    }

    public static LifecycleRuleFilter fromAnd(LifecycleRuleAndOperator and) {
        return (LifecycleRuleFilter)LifecycleRuleFilter.builder().and(and).build();
    }

    public static LifecycleRuleFilter fromAnd(Consumer<LifecycleRuleAndOperator.Builder> and) {
        LifecycleRuleAndOperator.Builder builder = LifecycleRuleAndOperator.builder();
        and.accept(builder);
        return LifecycleRuleFilter.fromAnd((LifecycleRuleAndOperator)builder.build());
    }

    public Type type() {
        return this.type;
    }

    public final List<SdkField<?>> sdkFields() {
        return SDK_FIELDS;
    }

    private static <T> Function<Object, T> getter(Function<LifecycleRuleFilter, T> g) {
        return obj -> g.apply((LifecycleRuleFilter)obj);
    }

    private static <T> BiConsumer<Object, T> setter(BiConsumer<Builder, T> s) {
        return (obj, val) -> s.accept((Builder)obj, val);
    }

    public static enum Type {
        PREFIX,
        TAG,
        OBJECT_SIZE_GREATER_THAN,
        OBJECT_SIZE_LESS_THAN,
        AND,
        UNKNOWN_TO_SDK_VERSION;

    }

    static final class BuilderImpl
    implements Builder {
        private String prefix;
        private Tag tag;
        private Long objectSizeGreaterThan;
        private Long objectSizeLessThan;
        private LifecycleRuleAndOperator and;
        private Type type = Type.UNKNOWN_TO_SDK_VERSION;
        private Set<Type> setTypes = EnumSet.noneOf(Type.class);

        private BuilderImpl() {
        }

        private BuilderImpl(LifecycleRuleFilter model) {
            this.prefix(model.prefix);
            this.tag(model.tag);
            this.objectSizeGreaterThan(model.objectSizeGreaterThan);
            this.objectSizeLessThan(model.objectSizeLessThan);
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

        public final Long getObjectSizeGreaterThan() {
            return this.objectSizeGreaterThan;
        }

        public final void setObjectSizeGreaterThan(Long objectSizeGreaterThan) {
            Long oldValue = this.objectSizeGreaterThan;
            this.objectSizeGreaterThan = objectSizeGreaterThan;
            this.handleUnionValueChange(Type.OBJECT_SIZE_GREATER_THAN, oldValue, this.objectSizeGreaterThan);
        }

        @Override
        public final Builder objectSizeGreaterThan(Long objectSizeGreaterThan) {
            Long oldValue = this.objectSizeGreaterThan;
            this.objectSizeGreaterThan = objectSizeGreaterThan;
            this.handleUnionValueChange(Type.OBJECT_SIZE_GREATER_THAN, oldValue, this.objectSizeGreaterThan);
            return this;
        }

        public final Long getObjectSizeLessThan() {
            return this.objectSizeLessThan;
        }

        public final void setObjectSizeLessThan(Long objectSizeLessThan) {
            Long oldValue = this.objectSizeLessThan;
            this.objectSizeLessThan = objectSizeLessThan;
            this.handleUnionValueChange(Type.OBJECT_SIZE_LESS_THAN, oldValue, this.objectSizeLessThan);
        }

        @Override
        public final Builder objectSizeLessThan(Long objectSizeLessThan) {
            Long oldValue = this.objectSizeLessThan;
            this.objectSizeLessThan = objectSizeLessThan;
            this.handleUnionValueChange(Type.OBJECT_SIZE_LESS_THAN, oldValue, this.objectSizeLessThan);
            return this;
        }

        public final LifecycleRuleAndOperator.Builder getAnd() {
            return this.and != null ? this.and.toBuilder() : null;
        }

        public final void setAnd(LifecycleRuleAndOperator.BuilderImpl and) {
            LifecycleRuleAndOperator oldValue = this.and;
            this.and = and != null ? and.build() : null;
            this.handleUnionValueChange(Type.AND, oldValue, this.and);
        }

        @Override
        public final Builder and(LifecycleRuleAndOperator and) {
            LifecycleRuleAndOperator oldValue = this.and;
            this.and = and;
            this.handleUnionValueChange(Type.AND, oldValue, this.and);
            return this;
        }

        public LifecycleRuleFilter build() {
            return new LifecycleRuleFilter(this);
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
    CopyableBuilder<Builder, LifecycleRuleFilter> {
        public Builder prefix(String var1);

        public Builder tag(Tag var1);

        default public Builder tag(Consumer<Tag.Builder> tag) {
            return this.tag((Tag)((Tag.Builder)Tag.builder().applyMutation(tag)).build());
        }

        public Builder objectSizeGreaterThan(Long var1);

        public Builder objectSizeLessThan(Long var1);

        public Builder and(LifecycleRuleAndOperator var1);

        default public Builder and(Consumer<LifecycleRuleAndOperator.Builder> and) {
            return this.and((LifecycleRuleAndOperator)((LifecycleRuleAndOperator.Builder)LifecycleRuleAndOperator.builder().applyMutation(and)).build());
        }
    }
}

