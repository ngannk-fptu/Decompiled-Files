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
import software.amazon.awssdk.services.s3.model.MetricsAndOperator;
import software.amazon.awssdk.services.s3.model.Tag;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

public final class MetricsFilter
implements SdkPojo,
Serializable,
ToCopyableBuilder<Builder, MetricsFilter> {
    private static final SdkField<String> PREFIX_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("Prefix").getter(MetricsFilter.getter(MetricsFilter::prefix)).setter(MetricsFilter.setter(Builder::prefix)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("Prefix").unmarshallLocationName("Prefix").build()}).build();
    private static final SdkField<Tag> TAG_FIELD = SdkField.builder((MarshallingType)MarshallingType.SDK_POJO).memberName("Tag").getter(MetricsFilter.getter(MetricsFilter::tag)).setter(MetricsFilter.setter(Builder::tag)).constructor(Tag::builder).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("Tag").unmarshallLocationName("Tag").build()}).build();
    private static final SdkField<String> ACCESS_POINT_ARN_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("AccessPointArn").getter(MetricsFilter.getter(MetricsFilter::accessPointArn)).setter(MetricsFilter.setter(Builder::accessPointArn)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("AccessPointArn").unmarshallLocationName("AccessPointArn").build()}).build();
    private static final SdkField<MetricsAndOperator> AND_FIELD = SdkField.builder((MarshallingType)MarshallingType.SDK_POJO).memberName("And").getter(MetricsFilter.getter(MetricsFilter::and)).setter(MetricsFilter.setter(Builder::and)).constructor(MetricsAndOperator::builder).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("And").unmarshallLocationName("And").build()}).build();
    private static final List<SdkField<?>> SDK_FIELDS = Collections.unmodifiableList(Arrays.asList(PREFIX_FIELD, TAG_FIELD, ACCESS_POINT_ARN_FIELD, AND_FIELD));
    private static final long serialVersionUID = 1L;
    private final String prefix;
    private final Tag tag;
    private final String accessPointArn;
    private final MetricsAndOperator and;
    private final Type type;

    private MetricsFilter(BuilderImpl builder) {
        this.prefix = builder.prefix;
        this.tag = builder.tag;
        this.accessPointArn = builder.accessPointArn;
        this.and = builder.and;
        this.type = builder.type;
    }

    public final String prefix() {
        return this.prefix;
    }

    public final Tag tag() {
        return this.tag;
    }

    public final String accessPointArn() {
        return this.accessPointArn;
    }

    public final MetricsAndOperator and() {
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
        hashCode = 31 * hashCode + Objects.hashCode(this.accessPointArn());
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
        if (!(obj instanceof MetricsFilter)) {
            return false;
        }
        MetricsFilter other = (MetricsFilter)obj;
        return Objects.equals(this.prefix(), other.prefix()) && Objects.equals(this.tag(), other.tag()) && Objects.equals(this.accessPointArn(), other.accessPointArn()) && Objects.equals(this.and(), other.and());
    }

    public final String toString() {
        return ToString.builder((String)"MetricsFilter").add("Prefix", (Object)this.prefix()).add("Tag", (Object)this.tag()).add("AccessPointArn", (Object)this.accessPointArn()).add("And", (Object)this.and()).build();
    }

    public final <T> Optional<T> getValueForField(String fieldName, Class<T> clazz) {
        switch (fieldName) {
            case "Prefix": {
                return Optional.ofNullable(clazz.cast(this.prefix()));
            }
            case "Tag": {
                return Optional.ofNullable(clazz.cast(this.tag()));
            }
            case "AccessPointArn": {
                return Optional.ofNullable(clazz.cast(this.accessPointArn()));
            }
            case "And": {
                return Optional.ofNullable(clazz.cast(this.and()));
            }
        }
        return Optional.empty();
    }

    public static MetricsFilter fromPrefix(String prefix) {
        return (MetricsFilter)MetricsFilter.builder().prefix(prefix).build();
    }

    public static MetricsFilter fromTag(Tag tag) {
        return (MetricsFilter)MetricsFilter.builder().tag(tag).build();
    }

    public static MetricsFilter fromTag(Consumer<Tag.Builder> tag) {
        Tag.Builder builder = Tag.builder();
        tag.accept(builder);
        return MetricsFilter.fromTag((Tag)builder.build());
    }

    public static MetricsFilter fromAccessPointArn(String accessPointArn) {
        return (MetricsFilter)MetricsFilter.builder().accessPointArn(accessPointArn).build();
    }

    public static MetricsFilter fromAnd(MetricsAndOperator and) {
        return (MetricsFilter)MetricsFilter.builder().and(and).build();
    }

    public static MetricsFilter fromAnd(Consumer<MetricsAndOperator.Builder> and) {
        MetricsAndOperator.Builder builder = MetricsAndOperator.builder();
        and.accept(builder);
        return MetricsFilter.fromAnd((MetricsAndOperator)builder.build());
    }

    public Type type() {
        return this.type;
    }

    public final List<SdkField<?>> sdkFields() {
        return SDK_FIELDS;
    }

    private static <T> Function<Object, T> getter(Function<MetricsFilter, T> g) {
        return obj -> g.apply((MetricsFilter)obj);
    }

    private static <T> BiConsumer<Object, T> setter(BiConsumer<Builder, T> s) {
        return (obj, val) -> s.accept((Builder)obj, val);
    }

    public static enum Type {
        PREFIX,
        TAG,
        ACCESS_POINT_ARN,
        AND,
        UNKNOWN_TO_SDK_VERSION;

    }

    static final class BuilderImpl
    implements Builder {
        private String prefix;
        private Tag tag;
        private String accessPointArn;
        private MetricsAndOperator and;
        private Type type = Type.UNKNOWN_TO_SDK_VERSION;
        private Set<Type> setTypes = EnumSet.noneOf(Type.class);

        private BuilderImpl() {
        }

        private BuilderImpl(MetricsFilter model) {
            this.prefix(model.prefix);
            this.tag(model.tag);
            this.accessPointArn(model.accessPointArn);
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

        public final String getAccessPointArn() {
            return this.accessPointArn;
        }

        public final void setAccessPointArn(String accessPointArn) {
            String oldValue = this.accessPointArn;
            this.accessPointArn = accessPointArn;
            this.handleUnionValueChange(Type.ACCESS_POINT_ARN, oldValue, this.accessPointArn);
        }

        @Override
        public final Builder accessPointArn(String accessPointArn) {
            String oldValue = this.accessPointArn;
            this.accessPointArn = accessPointArn;
            this.handleUnionValueChange(Type.ACCESS_POINT_ARN, oldValue, this.accessPointArn);
            return this;
        }

        public final MetricsAndOperator.Builder getAnd() {
            return this.and != null ? this.and.toBuilder() : null;
        }

        public final void setAnd(MetricsAndOperator.BuilderImpl and) {
            MetricsAndOperator oldValue = this.and;
            this.and = and != null ? and.build() : null;
            this.handleUnionValueChange(Type.AND, oldValue, this.and);
        }

        @Override
        public final Builder and(MetricsAndOperator and) {
            MetricsAndOperator oldValue = this.and;
            this.and = and;
            this.handleUnionValueChange(Type.AND, oldValue, this.and);
            return this;
        }

        public MetricsFilter build() {
            return new MetricsFilter(this);
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
    CopyableBuilder<Builder, MetricsFilter> {
        public Builder prefix(String var1);

        public Builder tag(Tag var1);

        default public Builder tag(Consumer<Tag.Builder> tag) {
            return this.tag((Tag)((Tag.Builder)Tag.builder().applyMutation(tag)).build());
        }

        public Builder accessPointArn(String var1);

        public Builder and(MetricsAndOperator var1);

        default public Builder and(Consumer<MetricsAndOperator.Builder> and) {
            return this.and((MetricsAndOperator)((MetricsAndOperator.Builder)MetricsAndOperator.builder().applyMutation(and)).build());
        }
    }
}

