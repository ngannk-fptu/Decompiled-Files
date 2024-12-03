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
 *  software.amazon.awssdk.core.traits.RequiredTrait
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
import software.amazon.awssdk.core.SdkField;
import software.amazon.awssdk.core.SdkPojo;
import software.amazon.awssdk.core.protocol.MarshallLocation;
import software.amazon.awssdk.core.protocol.MarshallingType;
import software.amazon.awssdk.core.traits.ListTrait;
import software.amazon.awssdk.core.traits.LocationTrait;
import software.amazon.awssdk.core.traits.RequiredTrait;
import software.amazon.awssdk.core.traits.Trait;
import software.amazon.awssdk.core.util.DefaultSdkAutoConstructList;
import software.amazon.awssdk.core.util.SdkAutoConstructList;
import software.amazon.awssdk.services.s3.model.InventoryDestination;
import software.amazon.awssdk.services.s3.model.InventoryFilter;
import software.amazon.awssdk.services.s3.model.InventoryIncludedObjectVersions;
import software.amazon.awssdk.services.s3.model.InventoryOptionalField;
import software.amazon.awssdk.services.s3.model.InventoryOptionalFieldsCopier;
import software.amazon.awssdk.services.s3.model.InventorySchedule;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

public final class InventoryConfiguration
implements SdkPojo,
Serializable,
ToCopyableBuilder<Builder, InventoryConfiguration> {
    private static final SdkField<InventoryDestination> DESTINATION_FIELD = SdkField.builder((MarshallingType)MarshallingType.SDK_POJO).memberName("Destination").getter(InventoryConfiguration.getter(InventoryConfiguration::destination)).setter(InventoryConfiguration.setter(Builder::destination)).constructor(InventoryDestination::builder).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("Destination").unmarshallLocationName("Destination").build(), RequiredTrait.create()}).build();
    private static final SdkField<Boolean> IS_ENABLED_FIELD = SdkField.builder((MarshallingType)MarshallingType.BOOLEAN).memberName("IsEnabled").getter(InventoryConfiguration.getter(InventoryConfiguration::isEnabled)).setter(InventoryConfiguration.setter(Builder::isEnabled)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("IsEnabled").unmarshallLocationName("IsEnabled").build(), RequiredTrait.create()}).build();
    private static final SdkField<InventoryFilter> FILTER_FIELD = SdkField.builder((MarshallingType)MarshallingType.SDK_POJO).memberName("Filter").getter(InventoryConfiguration.getter(InventoryConfiguration::filter)).setter(InventoryConfiguration.setter(Builder::filter)).constructor(InventoryFilter::builder).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("Filter").unmarshallLocationName("Filter").build()}).build();
    private static final SdkField<String> ID_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("Id").getter(InventoryConfiguration.getter(InventoryConfiguration::id)).setter(InventoryConfiguration.setter(Builder::id)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("Id").unmarshallLocationName("Id").build(), RequiredTrait.create()}).build();
    private static final SdkField<String> INCLUDED_OBJECT_VERSIONS_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("IncludedObjectVersions").getter(InventoryConfiguration.getter(InventoryConfiguration::includedObjectVersionsAsString)).setter(InventoryConfiguration.setter(Builder::includedObjectVersions)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("IncludedObjectVersions").unmarshallLocationName("IncludedObjectVersions").build(), RequiredTrait.create()}).build();
    private static final SdkField<List<String>> OPTIONAL_FIELDS_FIELD = SdkField.builder((MarshallingType)MarshallingType.LIST).memberName("OptionalFields").getter(InventoryConfiguration.getter(InventoryConfiguration::optionalFieldsAsStrings)).setter(InventoryConfiguration.setter(Builder::optionalFieldsWithStrings)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("OptionalFields").unmarshallLocationName("OptionalFields").build(), ListTrait.builder().memberLocationName("Field").memberFieldInfo(SdkField.builder((MarshallingType)MarshallingType.STRING).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("Field").unmarshallLocationName("Field").build()}).build()).build()}).build();
    private static final SdkField<InventorySchedule> SCHEDULE_FIELD = SdkField.builder((MarshallingType)MarshallingType.SDK_POJO).memberName("Schedule").getter(InventoryConfiguration.getter(InventoryConfiguration::schedule)).setter(InventoryConfiguration.setter(Builder::schedule)).constructor(InventorySchedule::builder).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("Schedule").unmarshallLocationName("Schedule").build(), RequiredTrait.create()}).build();
    private static final List<SdkField<?>> SDK_FIELDS = Collections.unmodifiableList(Arrays.asList(DESTINATION_FIELD, IS_ENABLED_FIELD, FILTER_FIELD, ID_FIELD, INCLUDED_OBJECT_VERSIONS_FIELD, OPTIONAL_FIELDS_FIELD, SCHEDULE_FIELD));
    private static final long serialVersionUID = 1L;
    private final InventoryDestination destination;
    private final Boolean isEnabled;
    private final InventoryFilter filter;
    private final String id;
    private final String includedObjectVersions;
    private final List<String> optionalFields;
    private final InventorySchedule schedule;

    private InventoryConfiguration(BuilderImpl builder) {
        this.destination = builder.destination;
        this.isEnabled = builder.isEnabled;
        this.filter = builder.filter;
        this.id = builder.id;
        this.includedObjectVersions = builder.includedObjectVersions;
        this.optionalFields = builder.optionalFields;
        this.schedule = builder.schedule;
    }

    public final InventoryDestination destination() {
        return this.destination;
    }

    public final Boolean isEnabled() {
        return this.isEnabled;
    }

    public final InventoryFilter filter() {
        return this.filter;
    }

    public final String id() {
        return this.id;
    }

    public final InventoryIncludedObjectVersions includedObjectVersions() {
        return InventoryIncludedObjectVersions.fromValue(this.includedObjectVersions);
    }

    public final String includedObjectVersionsAsString() {
        return this.includedObjectVersions;
    }

    public final List<InventoryOptionalField> optionalFields() {
        return InventoryOptionalFieldsCopier.copyStringToEnum(this.optionalFields);
    }

    public final boolean hasOptionalFields() {
        return this.optionalFields != null && !(this.optionalFields instanceof SdkAutoConstructList);
    }

    public final List<String> optionalFieldsAsStrings() {
        return this.optionalFields;
    }

    public final InventorySchedule schedule() {
        return this.schedule;
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
        hashCode = 31 * hashCode + Objects.hashCode(this.destination());
        hashCode = 31 * hashCode + Objects.hashCode(this.isEnabled());
        hashCode = 31 * hashCode + Objects.hashCode(this.filter());
        hashCode = 31 * hashCode + Objects.hashCode(this.id());
        hashCode = 31 * hashCode + Objects.hashCode(this.includedObjectVersionsAsString());
        hashCode = 31 * hashCode + Objects.hashCode(this.hasOptionalFields() ? this.optionalFieldsAsStrings() : null);
        hashCode = 31 * hashCode + Objects.hashCode(this.schedule());
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
        if (!(obj instanceof InventoryConfiguration)) {
            return false;
        }
        InventoryConfiguration other = (InventoryConfiguration)obj;
        return Objects.equals(this.destination(), other.destination()) && Objects.equals(this.isEnabled(), other.isEnabled()) && Objects.equals(this.filter(), other.filter()) && Objects.equals(this.id(), other.id()) && Objects.equals(this.includedObjectVersionsAsString(), other.includedObjectVersionsAsString()) && this.hasOptionalFields() == other.hasOptionalFields() && Objects.equals(this.optionalFieldsAsStrings(), other.optionalFieldsAsStrings()) && Objects.equals(this.schedule(), other.schedule());
    }

    public final String toString() {
        return ToString.builder((String)"InventoryConfiguration").add("Destination", (Object)this.destination()).add("IsEnabled", (Object)this.isEnabled()).add("Filter", (Object)this.filter()).add("Id", (Object)this.id()).add("IncludedObjectVersions", (Object)this.includedObjectVersionsAsString()).add("OptionalFields", this.hasOptionalFields() ? this.optionalFieldsAsStrings() : null).add("Schedule", (Object)this.schedule()).build();
    }

    public final <T> Optional<T> getValueForField(String fieldName, Class<T> clazz) {
        switch (fieldName) {
            case "Destination": {
                return Optional.ofNullable(clazz.cast(this.destination()));
            }
            case "IsEnabled": {
                return Optional.ofNullable(clazz.cast(this.isEnabled()));
            }
            case "Filter": {
                return Optional.ofNullable(clazz.cast(this.filter()));
            }
            case "Id": {
                return Optional.ofNullable(clazz.cast(this.id()));
            }
            case "IncludedObjectVersions": {
                return Optional.ofNullable(clazz.cast(this.includedObjectVersionsAsString()));
            }
            case "OptionalFields": {
                return Optional.ofNullable(clazz.cast(this.optionalFieldsAsStrings()));
            }
            case "Schedule": {
                return Optional.ofNullable(clazz.cast(this.schedule()));
            }
        }
        return Optional.empty();
    }

    public final List<SdkField<?>> sdkFields() {
        return SDK_FIELDS;
    }

    private static <T> Function<Object, T> getter(Function<InventoryConfiguration, T> g) {
        return obj -> g.apply((InventoryConfiguration)obj);
    }

    private static <T> BiConsumer<Object, T> setter(BiConsumer<Builder, T> s) {
        return (obj, val) -> s.accept((Builder)obj, val);
    }

    static final class BuilderImpl
    implements Builder {
        private InventoryDestination destination;
        private Boolean isEnabled;
        private InventoryFilter filter;
        private String id;
        private String includedObjectVersions;
        private List<String> optionalFields = DefaultSdkAutoConstructList.getInstance();
        private InventorySchedule schedule;

        private BuilderImpl() {
        }

        private BuilderImpl(InventoryConfiguration model) {
            this.destination(model.destination);
            this.isEnabled(model.isEnabled);
            this.filter(model.filter);
            this.id(model.id);
            this.includedObjectVersions(model.includedObjectVersions);
            this.optionalFieldsWithStrings(model.optionalFields);
            this.schedule(model.schedule);
        }

        public final InventoryDestination.Builder getDestination() {
            return this.destination != null ? this.destination.toBuilder() : null;
        }

        public final void setDestination(InventoryDestination.BuilderImpl destination) {
            this.destination = destination != null ? destination.build() : null;
        }

        @Override
        public final Builder destination(InventoryDestination destination) {
            this.destination = destination;
            return this;
        }

        public final Boolean getIsEnabled() {
            return this.isEnabled;
        }

        public final void setIsEnabled(Boolean isEnabled) {
            this.isEnabled = isEnabled;
        }

        @Override
        public final Builder isEnabled(Boolean isEnabled) {
            this.isEnabled = isEnabled;
            return this;
        }

        public final InventoryFilter.Builder getFilter() {
            return this.filter != null ? this.filter.toBuilder() : null;
        }

        public final void setFilter(InventoryFilter.BuilderImpl filter) {
            this.filter = filter != null ? filter.build() : null;
        }

        @Override
        public final Builder filter(InventoryFilter filter) {
            this.filter = filter;
            return this;
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

        public final String getIncludedObjectVersions() {
            return this.includedObjectVersions;
        }

        public final void setIncludedObjectVersions(String includedObjectVersions) {
            this.includedObjectVersions = includedObjectVersions;
        }

        @Override
        public final Builder includedObjectVersions(String includedObjectVersions) {
            this.includedObjectVersions = includedObjectVersions;
            return this;
        }

        @Override
        public final Builder includedObjectVersions(InventoryIncludedObjectVersions includedObjectVersions) {
            this.includedObjectVersions(includedObjectVersions == null ? null : includedObjectVersions.toString());
            return this;
        }

        public final Collection<String> getOptionalFields() {
            if (this.optionalFields instanceof SdkAutoConstructList) {
                return null;
            }
            return this.optionalFields;
        }

        public final void setOptionalFields(Collection<String> optionalFields) {
            this.optionalFields = InventoryOptionalFieldsCopier.copy(optionalFields);
        }

        @Override
        public final Builder optionalFieldsWithStrings(Collection<String> optionalFields) {
            this.optionalFields = InventoryOptionalFieldsCopier.copy(optionalFields);
            return this;
        }

        @Override
        @SafeVarargs
        public final Builder optionalFieldsWithStrings(String ... optionalFields) {
            this.optionalFieldsWithStrings(Arrays.asList(optionalFields));
            return this;
        }

        @Override
        public final Builder optionalFields(Collection<InventoryOptionalField> optionalFields) {
            this.optionalFields = InventoryOptionalFieldsCopier.copyEnumToString(optionalFields);
            return this;
        }

        @Override
        @SafeVarargs
        public final Builder optionalFields(InventoryOptionalField ... optionalFields) {
            this.optionalFields(Arrays.asList(optionalFields));
            return this;
        }

        public final InventorySchedule.Builder getSchedule() {
            return this.schedule != null ? this.schedule.toBuilder() : null;
        }

        public final void setSchedule(InventorySchedule.BuilderImpl schedule) {
            this.schedule = schedule != null ? schedule.build() : null;
        }

        @Override
        public final Builder schedule(InventorySchedule schedule) {
            this.schedule = schedule;
            return this;
        }

        public InventoryConfiguration build() {
            return new InventoryConfiguration(this);
        }

        public List<SdkField<?>> sdkFields() {
            return SDK_FIELDS;
        }
    }

    public static interface Builder
    extends SdkPojo,
    CopyableBuilder<Builder, InventoryConfiguration> {
        public Builder destination(InventoryDestination var1);

        default public Builder destination(Consumer<InventoryDestination.Builder> destination) {
            return this.destination((InventoryDestination)((InventoryDestination.Builder)InventoryDestination.builder().applyMutation(destination)).build());
        }

        public Builder isEnabled(Boolean var1);

        public Builder filter(InventoryFilter var1);

        default public Builder filter(Consumer<InventoryFilter.Builder> filter) {
            return this.filter((InventoryFilter)((InventoryFilter.Builder)InventoryFilter.builder().applyMutation(filter)).build());
        }

        public Builder id(String var1);

        public Builder includedObjectVersions(String var1);

        public Builder includedObjectVersions(InventoryIncludedObjectVersions var1);

        public Builder optionalFieldsWithStrings(Collection<String> var1);

        public Builder optionalFieldsWithStrings(String ... var1);

        public Builder optionalFields(Collection<InventoryOptionalField> var1);

        public Builder optionalFields(InventoryOptionalField ... var1);

        public Builder schedule(InventorySchedule var1);

        default public Builder schedule(Consumer<InventorySchedule.Builder> schedule) {
            return this.schedule((InventorySchedule)((InventorySchedule.Builder)InventorySchedule.builder().applyMutation(schedule)).build());
        }
    }
}

