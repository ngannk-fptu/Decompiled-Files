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
import java.util.stream.Collectors;
import java.util.stream.Stream;
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
import software.amazon.awssdk.services.s3.model.IntelligentTieringFilter;
import software.amazon.awssdk.services.s3.model.IntelligentTieringStatus;
import software.amazon.awssdk.services.s3.model.Tiering;
import software.amazon.awssdk.services.s3.model.TieringListCopier;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

public final class IntelligentTieringConfiguration
implements SdkPojo,
Serializable,
ToCopyableBuilder<Builder, IntelligentTieringConfiguration> {
    private static final SdkField<String> ID_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("Id").getter(IntelligentTieringConfiguration.getter(IntelligentTieringConfiguration::id)).setter(IntelligentTieringConfiguration.setter(Builder::id)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("Id").unmarshallLocationName("Id").build(), RequiredTrait.create()}).build();
    private static final SdkField<IntelligentTieringFilter> FILTER_FIELD = SdkField.builder((MarshallingType)MarshallingType.SDK_POJO).memberName("Filter").getter(IntelligentTieringConfiguration.getter(IntelligentTieringConfiguration::filter)).setter(IntelligentTieringConfiguration.setter(Builder::filter)).constructor(IntelligentTieringFilter::builder).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("Filter").unmarshallLocationName("Filter").build()}).build();
    private static final SdkField<String> STATUS_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("Status").getter(IntelligentTieringConfiguration.getter(IntelligentTieringConfiguration::statusAsString)).setter(IntelligentTieringConfiguration.setter(Builder::status)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("Status").unmarshallLocationName("Status").build(), RequiredTrait.create()}).build();
    private static final SdkField<List<Tiering>> TIERINGS_FIELD = SdkField.builder((MarshallingType)MarshallingType.LIST).memberName("Tierings").getter(IntelligentTieringConfiguration.getter(IntelligentTieringConfiguration::tierings)).setter(IntelligentTieringConfiguration.setter(Builder::tierings)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("Tiering").unmarshallLocationName("Tiering").build(), ListTrait.builder().memberLocationName(null).memberFieldInfo(SdkField.builder((MarshallingType)MarshallingType.SDK_POJO).constructor(Tiering::builder).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("member").unmarshallLocationName("member").build()}).build()).isFlattened(true).build(), RequiredTrait.create()}).build();
    private static final List<SdkField<?>> SDK_FIELDS = Collections.unmodifiableList(Arrays.asList(ID_FIELD, FILTER_FIELD, STATUS_FIELD, TIERINGS_FIELD));
    private static final long serialVersionUID = 1L;
    private final String id;
    private final IntelligentTieringFilter filter;
    private final String status;
    private final List<Tiering> tierings;

    private IntelligentTieringConfiguration(BuilderImpl builder) {
        this.id = builder.id;
        this.filter = builder.filter;
        this.status = builder.status;
        this.tierings = builder.tierings;
    }

    public final String id() {
        return this.id;
    }

    public final IntelligentTieringFilter filter() {
        return this.filter;
    }

    public final IntelligentTieringStatus status() {
        return IntelligentTieringStatus.fromValue(this.status);
    }

    public final String statusAsString() {
        return this.status;
    }

    public final boolean hasTierings() {
        return this.tierings != null && !(this.tierings instanceof SdkAutoConstructList);
    }

    public final List<Tiering> tierings() {
        return this.tierings;
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
        hashCode = 31 * hashCode + Objects.hashCode(this.statusAsString());
        hashCode = 31 * hashCode + Objects.hashCode(this.hasTierings() ? this.tierings() : null);
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
        if (!(obj instanceof IntelligentTieringConfiguration)) {
            return false;
        }
        IntelligentTieringConfiguration other = (IntelligentTieringConfiguration)obj;
        return Objects.equals(this.id(), other.id()) && Objects.equals(this.filter(), other.filter()) && Objects.equals(this.statusAsString(), other.statusAsString()) && this.hasTierings() == other.hasTierings() && Objects.equals(this.tierings(), other.tierings());
    }

    public final String toString() {
        return ToString.builder((String)"IntelligentTieringConfiguration").add("Id", (Object)this.id()).add("Filter", (Object)this.filter()).add("Status", (Object)this.statusAsString()).add("Tierings", this.hasTierings() ? this.tierings() : null).build();
    }

    public final <T> Optional<T> getValueForField(String fieldName, Class<T> clazz) {
        switch (fieldName) {
            case "Id": {
                return Optional.ofNullable(clazz.cast(this.id()));
            }
            case "Filter": {
                return Optional.ofNullable(clazz.cast(this.filter()));
            }
            case "Status": {
                return Optional.ofNullable(clazz.cast(this.statusAsString()));
            }
            case "Tierings": {
                return Optional.ofNullable(clazz.cast(this.tierings()));
            }
        }
        return Optional.empty();
    }

    public final List<SdkField<?>> sdkFields() {
        return SDK_FIELDS;
    }

    private static <T> Function<Object, T> getter(Function<IntelligentTieringConfiguration, T> g) {
        return obj -> g.apply((IntelligentTieringConfiguration)obj);
    }

    private static <T> BiConsumer<Object, T> setter(BiConsumer<Builder, T> s) {
        return (obj, val) -> s.accept((Builder)obj, val);
    }

    static final class BuilderImpl
    implements Builder {
        private String id;
        private IntelligentTieringFilter filter;
        private String status;
        private List<Tiering> tierings = DefaultSdkAutoConstructList.getInstance();

        private BuilderImpl() {
        }

        private BuilderImpl(IntelligentTieringConfiguration model) {
            this.id(model.id);
            this.filter(model.filter);
            this.status(model.status);
            this.tierings(model.tierings);
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

        public final IntelligentTieringFilter.Builder getFilter() {
            return this.filter != null ? this.filter.toBuilder() : null;
        }

        public final void setFilter(IntelligentTieringFilter.BuilderImpl filter) {
            this.filter = filter != null ? filter.build() : null;
        }

        @Override
        public final Builder filter(IntelligentTieringFilter filter) {
            this.filter = filter;
            return this;
        }

        public final String getStatus() {
            return this.status;
        }

        public final void setStatus(String status) {
            this.status = status;
        }

        @Override
        public final Builder status(String status) {
            this.status = status;
            return this;
        }

        @Override
        public final Builder status(IntelligentTieringStatus status) {
            this.status(status == null ? null : status.toString());
            return this;
        }

        public final List<Tiering.Builder> getTierings() {
            List<Tiering.Builder> result = TieringListCopier.copyToBuilder(this.tierings);
            if (result instanceof SdkAutoConstructList) {
                return null;
            }
            return result;
        }

        public final void setTierings(Collection<Tiering.BuilderImpl> tierings) {
            this.tierings = TieringListCopier.copyFromBuilder(tierings);
        }

        @Override
        public final Builder tierings(Collection<Tiering> tierings) {
            this.tierings = TieringListCopier.copy(tierings);
            return this;
        }

        @Override
        @SafeVarargs
        public final Builder tierings(Tiering ... tierings) {
            this.tierings(Arrays.asList(tierings));
            return this;
        }

        @Override
        @SafeVarargs
        public final Builder tierings(Consumer<Tiering.Builder> ... tierings) {
            this.tierings(Stream.of(tierings).map(c -> (Tiering)((Tiering.Builder)Tiering.builder().applyMutation((Consumer)c)).build()).collect(Collectors.toList()));
            return this;
        }

        public IntelligentTieringConfiguration build() {
            return new IntelligentTieringConfiguration(this);
        }

        public List<SdkField<?>> sdkFields() {
            return SDK_FIELDS;
        }
    }

    public static interface Builder
    extends SdkPojo,
    CopyableBuilder<Builder, IntelligentTieringConfiguration> {
        public Builder id(String var1);

        public Builder filter(IntelligentTieringFilter var1);

        default public Builder filter(Consumer<IntelligentTieringFilter.Builder> filter) {
            return this.filter((IntelligentTieringFilter)((IntelligentTieringFilter.Builder)IntelligentTieringFilter.builder().applyMutation(filter)).build());
        }

        public Builder status(String var1);

        public Builder status(IntelligentTieringStatus var1);

        public Builder tierings(Collection<Tiering> var1);

        public Builder tierings(Tiering ... var1);

        public Builder tierings(Consumer<Tiering.Builder> ... var1);
    }
}

