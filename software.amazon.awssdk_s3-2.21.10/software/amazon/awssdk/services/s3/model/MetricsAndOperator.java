/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.core.SdkField
 *  software.amazon.awssdk.core.SdkPojo
 *  software.amazon.awssdk.core.protocol.MarshallLocation
 *  software.amazon.awssdk.core.protocol.MarshallingType
 *  software.amazon.awssdk.core.traits.ListTrait
 *  software.amazon.awssdk.core.traits.LocationTrait
 *  software.amazon.awssdk.core.traits.Trait
 *  software.amazon.awssdk.core.util.DefaultSdkAutoConstructList
 *  software.amazon.awssdk.core.util.SdkAutoConstructList
 *  software.amazon.awssdk.utils.ToString
 *  software.amazon.awssdk.utils.builder.CopyableBuilder
 *  software.amazon.awssdk.utils.builder.ToCopyableBuilder
 */
package software.amazon.awssdk.services.s3.model;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import software.amazon.awssdk.core.SdkField;
import software.amazon.awssdk.core.SdkPojo;
import software.amazon.awssdk.core.protocol.MarshallLocation;
import software.amazon.awssdk.core.protocol.MarshallingType;
import software.amazon.awssdk.core.traits.ListTrait;
import software.amazon.awssdk.core.traits.LocationTrait;
import software.amazon.awssdk.core.traits.Trait;
import software.amazon.awssdk.core.util.DefaultSdkAutoConstructList;
import software.amazon.awssdk.core.util.SdkAutoConstructList;
import software.amazon.awssdk.services.s3.model.Tag;
import software.amazon.awssdk.services.s3.model.TagSetCopier;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

public final class MetricsAndOperator
implements SdkPojo,
Serializable,
ToCopyableBuilder<Builder, MetricsAndOperator> {
    private static final SdkField<String> PREFIX_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("Prefix").getter(MetricsAndOperator.getter(MetricsAndOperator::prefix)).setter(MetricsAndOperator.setter(Builder::prefix)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("Prefix").unmarshallLocationName("Prefix").build()}).build();
    private static final SdkField<List<Tag>> TAGS_FIELD = SdkField.builder((MarshallingType)MarshallingType.LIST).memberName("Tags").getter(MetricsAndOperator.getter(MetricsAndOperator::tags)).setter(MetricsAndOperator.setter(Builder::tags)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("Tag").unmarshallLocationName("Tag").build(), ListTrait.builder().memberLocationName("Tag").memberFieldInfo(SdkField.builder((MarshallingType)MarshallingType.SDK_POJO).constructor(Tag::builder).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("Tag").unmarshallLocationName("Tag").build()}).build()).isFlattened(true).build()}).build();
    private static final SdkField<String> ACCESS_POINT_ARN_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("AccessPointArn").getter(MetricsAndOperator.getter(MetricsAndOperator::accessPointArn)).setter(MetricsAndOperator.setter(Builder::accessPointArn)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("AccessPointArn").unmarshallLocationName("AccessPointArn").build()}).build();
    private static final List<SdkField<?>> SDK_FIELDS = Collections.unmodifiableList(Arrays.asList(PREFIX_FIELD, TAGS_FIELD, ACCESS_POINT_ARN_FIELD));
    private static final long serialVersionUID = 1L;
    private final String prefix;
    private final List<Tag> tags;
    private final String accessPointArn;

    private MetricsAndOperator(BuilderImpl builder) {
        this.prefix = builder.prefix;
        this.tags = builder.tags;
        this.accessPointArn = builder.accessPointArn;
    }

    public final String prefix() {
        return this.prefix;
    }

    public final boolean hasTags() {
        return this.tags != null && !(this.tags instanceof SdkAutoConstructList);
    }

    public final List<Tag> tags() {
        return this.tags;
    }

    public final String accessPointArn() {
        return this.accessPointArn;
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
        hashCode = 31 * hashCode + Objects.hashCode(this.hasTags() ? this.tags() : null);
        hashCode = 31 * hashCode + Objects.hashCode(this.accessPointArn());
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
        if (!(obj instanceof MetricsAndOperator)) {
            return false;
        }
        MetricsAndOperator other = (MetricsAndOperator)obj;
        return Objects.equals(this.prefix(), other.prefix()) && this.hasTags() == other.hasTags() && Objects.equals(this.tags(), other.tags()) && Objects.equals(this.accessPointArn(), other.accessPointArn());
    }

    public final String toString() {
        return ToString.builder((String)"MetricsAndOperator").add("Prefix", (Object)this.prefix()).add("Tags", this.hasTags() ? this.tags() : null).add("AccessPointArn", (Object)this.accessPointArn()).build();
    }

    public final <T> Optional<T> getValueForField(String fieldName, Class<T> clazz) {
        switch (fieldName) {
            case "Prefix": {
                return Optional.ofNullable(clazz.cast(this.prefix()));
            }
            case "Tags": {
                return Optional.ofNullable(clazz.cast(this.tags()));
            }
            case "AccessPointArn": {
                return Optional.ofNullable(clazz.cast(this.accessPointArn()));
            }
        }
        return Optional.empty();
    }

    public final List<SdkField<?>> sdkFields() {
        return SDK_FIELDS;
    }

    private static <T> Function<Object, T> getter(Function<MetricsAndOperator, T> g) {
        return obj -> g.apply((MetricsAndOperator)obj);
    }

    private static <T> BiConsumer<Object, T> setter(BiConsumer<Builder, T> s) {
        return (obj, val) -> s.accept((Builder)obj, val);
    }

    static final class BuilderImpl
    implements Builder {
        private String prefix;
        private List<Tag> tags = DefaultSdkAutoConstructList.getInstance();
        private String accessPointArn;

        private BuilderImpl() {
        }

        private BuilderImpl(MetricsAndOperator model) {
            this.prefix(model.prefix);
            this.tags(model.tags);
            this.accessPointArn(model.accessPointArn);
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

        public final List<Tag.Builder> getTags() {
            List<Tag.Builder> result = TagSetCopier.copyToBuilder(this.tags);
            if (result instanceof SdkAutoConstructList) {
                return null;
            }
            return result;
        }

        public final void setTags(Collection<Tag.BuilderImpl> tags) {
            this.tags = TagSetCopier.copyFromBuilder(tags);
        }

        @Override
        public final Builder tags(Collection<Tag> tags) {
            this.tags = TagSetCopier.copy(tags);
            return this;
        }

        @Override
        @SafeVarargs
        public final Builder tags(Tag ... tags) {
            this.tags(Arrays.asList(tags));
            return this;
        }

        @Override
        @SafeVarargs
        public final Builder tags(Consumer<Tag.Builder> ... tags) {
            this.tags(Stream.of(tags).map(c -> (Tag)((Tag.Builder)Tag.builder().applyMutation((Consumer)c)).build()).collect(Collectors.toList()));
            return this;
        }

        public final String getAccessPointArn() {
            return this.accessPointArn;
        }

        public final void setAccessPointArn(String accessPointArn) {
            this.accessPointArn = accessPointArn;
        }

        @Override
        public final Builder accessPointArn(String accessPointArn) {
            this.accessPointArn = accessPointArn;
            return this;
        }

        public MetricsAndOperator build() {
            return new MetricsAndOperator(this);
        }

        public List<SdkField<?>> sdkFields() {
            return SDK_FIELDS;
        }
    }

    public static interface Builder
    extends SdkPojo,
    CopyableBuilder<Builder, MetricsAndOperator> {
        public Builder prefix(String var1);

        public Builder tags(Collection<Tag> var1);

        public Builder tags(Tag ... var1);

        public Builder tags(Consumer<Tag.Builder> ... var1);

        public Builder accessPointArn(String var1);
    }
}

