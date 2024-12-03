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
import software.amazon.awssdk.services.s3.model.AnalyticsFilter;
import software.amazon.awssdk.services.s3.model.StorageClassAnalysis;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

public final class AnalyticsConfiguration
implements SdkPojo,
Serializable,
ToCopyableBuilder<Builder, AnalyticsConfiguration> {
    private static final SdkField<String> ID_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("Id").getter(AnalyticsConfiguration.getter(AnalyticsConfiguration::id)).setter(AnalyticsConfiguration.setter(Builder::id)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("Id").unmarshallLocationName("Id").build(), RequiredTrait.create()}).build();
    private static final SdkField<AnalyticsFilter> FILTER_FIELD = SdkField.builder((MarshallingType)MarshallingType.SDK_POJO).memberName("Filter").getter(AnalyticsConfiguration.getter(AnalyticsConfiguration::filter)).setter(AnalyticsConfiguration.setter(Builder::filter)).constructor(AnalyticsFilter::builder).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("Filter").unmarshallLocationName("Filter").build()}).build();
    private static final SdkField<StorageClassAnalysis> STORAGE_CLASS_ANALYSIS_FIELD = SdkField.builder((MarshallingType)MarshallingType.SDK_POJO).memberName("StorageClassAnalysis").getter(AnalyticsConfiguration.getter(AnalyticsConfiguration::storageClassAnalysis)).setter(AnalyticsConfiguration.setter(Builder::storageClassAnalysis)).constructor(StorageClassAnalysis::builder).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("StorageClassAnalysis").unmarshallLocationName("StorageClassAnalysis").build(), RequiredTrait.create()}).build();
    private static final List<SdkField<?>> SDK_FIELDS = Collections.unmodifiableList(Arrays.asList(ID_FIELD, FILTER_FIELD, STORAGE_CLASS_ANALYSIS_FIELD));
    private static final long serialVersionUID = 1L;
    private final String id;
    private final AnalyticsFilter filter;
    private final StorageClassAnalysis storageClassAnalysis;

    private AnalyticsConfiguration(BuilderImpl builder) {
        this.id = builder.id;
        this.filter = builder.filter;
        this.storageClassAnalysis = builder.storageClassAnalysis;
    }

    public final String id() {
        return this.id;
    }

    public final AnalyticsFilter filter() {
        return this.filter;
    }

    public final StorageClassAnalysis storageClassAnalysis() {
        return this.storageClassAnalysis;
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
        hashCode = 31 * hashCode + Objects.hashCode(this.storageClassAnalysis());
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
        if (!(obj instanceof AnalyticsConfiguration)) {
            return false;
        }
        AnalyticsConfiguration other = (AnalyticsConfiguration)obj;
        return Objects.equals(this.id(), other.id()) && Objects.equals(this.filter(), other.filter()) && Objects.equals(this.storageClassAnalysis(), other.storageClassAnalysis());
    }

    public final String toString() {
        return ToString.builder((String)"AnalyticsConfiguration").add("Id", (Object)this.id()).add("Filter", (Object)this.filter()).add("StorageClassAnalysis", (Object)this.storageClassAnalysis()).build();
    }

    public final <T> Optional<T> getValueForField(String fieldName, Class<T> clazz) {
        switch (fieldName) {
            case "Id": {
                return Optional.ofNullable(clazz.cast(this.id()));
            }
            case "Filter": {
                return Optional.ofNullable(clazz.cast(this.filter()));
            }
            case "StorageClassAnalysis": {
                return Optional.ofNullable(clazz.cast(this.storageClassAnalysis()));
            }
        }
        return Optional.empty();
    }

    public final List<SdkField<?>> sdkFields() {
        return SDK_FIELDS;
    }

    private static <T> Function<Object, T> getter(Function<AnalyticsConfiguration, T> g) {
        return obj -> g.apply((AnalyticsConfiguration)obj);
    }

    private static <T> BiConsumer<Object, T> setter(BiConsumer<Builder, T> s) {
        return (obj, val) -> s.accept((Builder)obj, val);
    }

    static final class BuilderImpl
    implements Builder {
        private String id;
        private AnalyticsFilter filter;
        private StorageClassAnalysis storageClassAnalysis;

        private BuilderImpl() {
        }

        private BuilderImpl(AnalyticsConfiguration model) {
            this.id(model.id);
            this.filter(model.filter);
            this.storageClassAnalysis(model.storageClassAnalysis);
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

        public final AnalyticsFilter.Builder getFilter() {
            return this.filter != null ? this.filter.toBuilder() : null;
        }

        public final void setFilter(AnalyticsFilter.BuilderImpl filter) {
            this.filter = filter != null ? filter.build() : null;
        }

        @Override
        public final Builder filter(AnalyticsFilter filter) {
            this.filter = filter;
            return this;
        }

        public final StorageClassAnalysis.Builder getStorageClassAnalysis() {
            return this.storageClassAnalysis != null ? this.storageClassAnalysis.toBuilder() : null;
        }

        public final void setStorageClassAnalysis(StorageClassAnalysis.BuilderImpl storageClassAnalysis) {
            this.storageClassAnalysis = storageClassAnalysis != null ? storageClassAnalysis.build() : null;
        }

        @Override
        public final Builder storageClassAnalysis(StorageClassAnalysis storageClassAnalysis) {
            this.storageClassAnalysis = storageClassAnalysis;
            return this;
        }

        public AnalyticsConfiguration build() {
            return new AnalyticsConfiguration(this);
        }

        public List<SdkField<?>> sdkFields() {
            return SDK_FIELDS;
        }
    }

    public static interface Builder
    extends SdkPojo,
    CopyableBuilder<Builder, AnalyticsConfiguration> {
        public Builder id(String var1);

        public Builder filter(AnalyticsFilter var1);

        default public Builder filter(Consumer<AnalyticsFilter.Builder> filter) {
            return this.filter((AnalyticsFilter)((AnalyticsFilter.Builder)AnalyticsFilter.builder().applyMutation(filter)).build());
        }

        public Builder storageClassAnalysis(StorageClassAnalysis var1);

        default public Builder storageClassAnalysis(Consumer<StorageClassAnalysis.Builder> storageClassAnalysis) {
            return this.storageClassAnalysis((StorageClassAnalysis)((StorageClassAnalysis.Builder)StorageClassAnalysis.builder().applyMutation(storageClassAnalysis)).build());
        }
    }
}

