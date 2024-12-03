/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.core.SdkField
 *  software.amazon.awssdk.core.SdkPojo
 *  software.amazon.awssdk.core.protocol.MarshallLocation
 *  software.amazon.awssdk.core.protocol.MarshallingType
 *  software.amazon.awssdk.core.traits.LocationTrait
 *  software.amazon.awssdk.core.traits.RequiredTrait
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
import software.amazon.awssdk.core.traits.RequiredTrait;
import software.amazon.awssdk.core.traits.Trait;
import software.amazon.awssdk.services.s3.model.MetricsFilter;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

public final class MetricsConfiguration
implements SdkPojo,
Serializable,
ToCopyableBuilder<Builder, MetricsConfiguration> {
    private static final SdkField<String> ID_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("Id").getter(MetricsConfiguration.getter(MetricsConfiguration::id)).setter(MetricsConfiguration.setter(Builder::id)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("Id").unmarshallLocationName("Id").build(), RequiredTrait.create()}).build();
    private static final SdkField<MetricsFilter> FILTER_FIELD = SdkField.builder((MarshallingType)MarshallingType.SDK_POJO).memberName("Filter").getter(MetricsConfiguration.getter(MetricsConfiguration::filter)).setter(MetricsConfiguration.setter(Builder::filter)).constructor(MetricsFilter::builder).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("Filter").unmarshallLocationName("Filter").build()}).build();
    private static final List<SdkField<?>> SDK_FIELDS = Collections.unmodifiableList(Arrays.asList(ID_FIELD, FILTER_FIELD));
    private static final long serialVersionUID = 1L;
    private final String id;
    private final MetricsFilter filter;

    private MetricsConfiguration(BuilderImpl builder) {
        this.id = builder.id;
        this.filter = builder.filter;
    }

    public final String id() {
        return this.id;
    }

    public final MetricsFilter filter() {
        return this.filter;
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
        hashCode = 31 * hashCode + Objects.hashCode(this.id());
        hashCode = 31 * hashCode + Objects.hashCode(this.filter());
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
        if (!(obj instanceof MetricsConfiguration)) {
            return false;
        }
        MetricsConfiguration other = (MetricsConfiguration)obj;
        return Objects.equals(this.id(), other.id()) && Objects.equals(this.filter(), other.filter());
    }

    public final String toString() {
        return ToString.builder((String)"MetricsConfiguration").add("Id", (Object)this.id()).add("Filter", (Object)this.filter()).build();
    }

    public final <T> Optional<T> getValueForField(String fieldName, Class<T> clazz) {
        switch (fieldName) {
            case "Id": {
                return Optional.ofNullable(clazz.cast(this.id()));
            }
            case "Filter": {
                return Optional.ofNullable(clazz.cast(this.filter()));
            }
        }
        return Optional.empty();
    }

    public final List<SdkField<?>> sdkFields() {
        return SDK_FIELDS;
    }

    private static <T> Function<Object, T> getter(Function<MetricsConfiguration, T> g) {
        return obj -> g.apply((MetricsConfiguration)obj);
    }

    private static <T> BiConsumer<Object, T> setter(BiConsumer<Builder, T> s) {
        return (obj, val) -> s.accept((Builder)obj, val);
    }

    static final class BuilderImpl
    implements Builder {
        private String id;
        private MetricsFilter filter;

        private BuilderImpl() {
        }

        private BuilderImpl(MetricsConfiguration model) {
            this.id(model.id);
            this.filter(model.filter);
        }

        public final String getId() {
            return this.id;
        }

        public final void setId(String id) {
            this.id = id;
        }

        @Override
        public final Builder id(String id) {
            this.id = id;
            return this;
        }

        public final MetricsFilter.Builder getFilter() {
            return this.filter != null ? this.filter.toBuilder() : null;
        }

        public final void setFilter(MetricsFilter.BuilderImpl filter) {
            this.filter = filter != null ? filter.build() : null;
        }

        @Override
        public final Builder filter(MetricsFilter filter) {
            this.filter = filter;
            return this;
        }

        public MetricsConfiguration build() {
            return new MetricsConfiguration(this);
        }

        public List<SdkField<?>> sdkFields() {
            return SDK_FIELDS;
        }
    }

    public static interface Builder
    extends SdkPojo,
    CopyableBuilder<Builder, MetricsConfiguration> {
        public Builder id(String var1);

        public Builder filter(MetricsFilter var1);

        default public Builder filter(Consumer<MetricsFilter.Builder> filter) {
            return this.filter((MetricsFilter)((MetricsFilter.Builder)MetricsFilter.builder().applyMutation(filter)).build());
        }
    }
}

