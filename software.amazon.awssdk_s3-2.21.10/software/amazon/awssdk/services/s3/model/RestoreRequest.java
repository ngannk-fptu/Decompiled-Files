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
import software.amazon.awssdk.services.s3.model.GlacierJobParameters;
import software.amazon.awssdk.services.s3.model.OutputLocation;
import software.amazon.awssdk.services.s3.model.RestoreRequestType;
import software.amazon.awssdk.services.s3.model.SelectParameters;
import software.amazon.awssdk.services.s3.model.Tier;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

public final class RestoreRequest
implements SdkPojo,
Serializable,
ToCopyableBuilder<Builder, RestoreRequest> {
    private static final SdkField<Integer> DAYS_FIELD = SdkField.builder((MarshallingType)MarshallingType.INTEGER).memberName("Days").getter(RestoreRequest.getter(RestoreRequest::days)).setter(RestoreRequest.setter(Builder::days)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("Days").unmarshallLocationName("Days").build()}).build();
    private static final SdkField<GlacierJobParameters> GLACIER_JOB_PARAMETERS_FIELD = SdkField.builder((MarshallingType)MarshallingType.SDK_POJO).memberName("GlacierJobParameters").getter(RestoreRequest.getter(RestoreRequest::glacierJobParameters)).setter(RestoreRequest.setter(Builder::glacierJobParameters)).constructor(GlacierJobParameters::builder).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("GlacierJobParameters").unmarshallLocationName("GlacierJobParameters").build()}).build();
    private static final SdkField<String> TYPE_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("Type").getter(RestoreRequest.getter(RestoreRequest::typeAsString)).setter(RestoreRequest.setter(Builder::type)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("Type").unmarshallLocationName("Type").build()}).build();
    private static final SdkField<String> TIER_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("Tier").getter(RestoreRequest.getter(RestoreRequest::tierAsString)).setter(RestoreRequest.setter(Builder::tier)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("Tier").unmarshallLocationName("Tier").build()}).build();
    private static final SdkField<String> DESCRIPTION_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("Description").getter(RestoreRequest.getter(RestoreRequest::description)).setter(RestoreRequest.setter(Builder::description)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("Description").unmarshallLocationName("Description").build()}).build();
    private static final SdkField<SelectParameters> SELECT_PARAMETERS_FIELD = SdkField.builder((MarshallingType)MarshallingType.SDK_POJO).memberName("SelectParameters").getter(RestoreRequest.getter(RestoreRequest::selectParameters)).setter(RestoreRequest.setter(Builder::selectParameters)).constructor(SelectParameters::builder).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("SelectParameters").unmarshallLocationName("SelectParameters").build()}).build();
    private static final SdkField<OutputLocation> OUTPUT_LOCATION_FIELD = SdkField.builder((MarshallingType)MarshallingType.SDK_POJO).memberName("OutputLocation").getter(RestoreRequest.getter(RestoreRequest::outputLocation)).setter(RestoreRequest.setter(Builder::outputLocation)).constructor(OutputLocation::builder).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("OutputLocation").unmarshallLocationName("OutputLocation").build()}).build();
    private static final List<SdkField<?>> SDK_FIELDS = Collections.unmodifiableList(Arrays.asList(DAYS_FIELD, GLACIER_JOB_PARAMETERS_FIELD, TYPE_FIELD, TIER_FIELD, DESCRIPTION_FIELD, SELECT_PARAMETERS_FIELD, OUTPUT_LOCATION_FIELD));
    private static final long serialVersionUID = 1L;
    private final Integer days;
    private final GlacierJobParameters glacierJobParameters;
    private final String type;
    private final String tier;
    private final String description;
    private final SelectParameters selectParameters;
    private final OutputLocation outputLocation;

    private RestoreRequest(BuilderImpl builder) {
        this.days = builder.days;
        this.glacierJobParameters = builder.glacierJobParameters;
        this.type = builder.type;
        this.tier = builder.tier;
        this.description = builder.description;
        this.selectParameters = builder.selectParameters;
        this.outputLocation = builder.outputLocation;
    }

    public final Integer days() {
        return this.days;
    }

    public final GlacierJobParameters glacierJobParameters() {
        return this.glacierJobParameters;
    }

    public final RestoreRequestType type() {
        return RestoreRequestType.fromValue(this.type);
    }

    public final String typeAsString() {
        return this.type;
    }

    public final Tier tier() {
        return Tier.fromValue(this.tier);
    }

    public final String tierAsString() {
        return this.tier;
    }

    public final String description() {
        return this.description;
    }

    public final SelectParameters selectParameters() {
        return this.selectParameters;
    }

    public final OutputLocation outputLocation() {
        return this.outputLocation;
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
        hashCode = 31 * hashCode + Objects.hashCode(this.days());
        hashCode = 31 * hashCode + Objects.hashCode(this.glacierJobParameters());
        hashCode = 31 * hashCode + Objects.hashCode(this.typeAsString());
        hashCode = 31 * hashCode + Objects.hashCode(this.tierAsString());
        hashCode = 31 * hashCode + Objects.hashCode(this.description());
        hashCode = 31 * hashCode + Objects.hashCode(this.selectParameters());
        hashCode = 31 * hashCode + Objects.hashCode(this.outputLocation());
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
        if (!(obj instanceof RestoreRequest)) {
            return false;
        }
        RestoreRequest other = (RestoreRequest)obj;
        return Objects.equals(this.days(), other.days()) && Objects.equals(this.glacierJobParameters(), other.glacierJobParameters()) && Objects.equals(this.typeAsString(), other.typeAsString()) && Objects.equals(this.tierAsString(), other.tierAsString()) && Objects.equals(this.description(), other.description()) && Objects.equals(this.selectParameters(), other.selectParameters()) && Objects.equals(this.outputLocation(), other.outputLocation());
    }

    public final String toString() {
        return ToString.builder((String)"RestoreRequest").add("Days", (Object)this.days()).add("GlacierJobParameters", (Object)this.glacierJobParameters()).add("Type", (Object)this.typeAsString()).add("Tier", (Object)this.tierAsString()).add("Description", (Object)this.description()).add("SelectParameters", (Object)this.selectParameters()).add("OutputLocation", (Object)this.outputLocation()).build();
    }

    public final <T> Optional<T> getValueForField(String fieldName, Class<T> clazz) {
        switch (fieldName) {
            case "Days": {
                return Optional.ofNullable(clazz.cast(this.days()));
            }
            case "GlacierJobParameters": {
                return Optional.ofNullable(clazz.cast(this.glacierJobParameters()));
            }
            case "Type": {
                return Optional.ofNullable(clazz.cast(this.typeAsString()));
            }
            case "Tier": {
                return Optional.ofNullable(clazz.cast(this.tierAsString()));
            }
            case "Description": {
                return Optional.ofNullable(clazz.cast(this.description()));
            }
            case "SelectParameters": {
                return Optional.ofNullable(clazz.cast(this.selectParameters()));
            }
            case "OutputLocation": {
                return Optional.ofNullable(clazz.cast(this.outputLocation()));
            }
        }
        return Optional.empty();
    }

    public final List<SdkField<?>> sdkFields() {
        return SDK_FIELDS;
    }

    private static <T> Function<Object, T> getter(Function<RestoreRequest, T> g) {
        return obj -> g.apply((RestoreRequest)obj);
    }

    private static <T> BiConsumer<Object, T> setter(BiConsumer<Builder, T> s) {
        return (obj, val) -> s.accept((Builder)obj, val);
    }

    static final class BuilderImpl
    implements Builder {
        private Integer days;
        private GlacierJobParameters glacierJobParameters;
        private String type;
        private String tier;
        private String description;
        private SelectParameters selectParameters;
        private OutputLocation outputLocation;

        private BuilderImpl() {
        }

        private BuilderImpl(RestoreRequest model) {
            this.days(model.days);
            this.glacierJobParameters(model.glacierJobParameters);
            this.type(model.type);
            this.tier(model.tier);
            this.description(model.description);
            this.selectParameters(model.selectParameters);
            this.outputLocation(model.outputLocation);
        }

        public final Integer getDays() {
            return this.days;
        }

        public final void setDays(Integer days) {
            this.days = days;
        }

        @Override
        public final Builder days(Integer days) {
            this.days = days;
            return this;
        }

        public final GlacierJobParameters.Builder getGlacierJobParameters() {
            return this.glacierJobParameters != null ? this.glacierJobParameters.toBuilder() : null;
        }

        public final void setGlacierJobParameters(GlacierJobParameters.BuilderImpl glacierJobParameters) {
            this.glacierJobParameters = glacierJobParameters != null ? glacierJobParameters.build() : null;
        }

        @Override
        public final Builder glacierJobParameters(GlacierJobParameters glacierJobParameters) {
            this.glacierJobParameters = glacierJobParameters;
            return this;
        }

        public final String getType() {
            return this.type;
        }

        public final void setType(String type) {
            this.type = type;
        }

        @Override
        public final Builder type(String type) {
            this.type = type;
            return this;
        }

        @Override
        public final Builder type(RestoreRequestType type) {
            this.type(type == null ? null : type.toString());
            return this;
        }

        public final String getTier() {
            return this.tier;
        }

        public final void setTier(String tier) {
            this.tier = tier;
        }

        @Override
        public final Builder tier(String tier) {
            this.tier = tier;
            return this;
        }

        @Override
        public final Builder tier(Tier tier) {
            this.tier(tier == null ? null : tier.toString());
            return this;
        }

        public final String getDescription() {
            return this.description;
        }

        public final void setDescription(String description) {
            this.description = description;
        }

        @Override
        public final Builder description(String description) {
            this.description = description;
            return this;
        }

        public final SelectParameters.Builder getSelectParameters() {
            return this.selectParameters != null ? this.selectParameters.toBuilder() : null;
        }

        public final void setSelectParameters(SelectParameters.BuilderImpl selectParameters) {
            this.selectParameters = selectParameters != null ? selectParameters.build() : null;
        }

        @Override
        public final Builder selectParameters(SelectParameters selectParameters) {
            this.selectParameters = selectParameters;
            return this;
        }

        public final OutputLocation.Builder getOutputLocation() {
            return this.outputLocation != null ? this.outputLocation.toBuilder() : null;
        }

        public final void setOutputLocation(OutputLocation.BuilderImpl outputLocation) {
            this.outputLocation = outputLocation != null ? outputLocation.build() : null;
        }

        @Override
        public final Builder outputLocation(OutputLocation outputLocation) {
            this.outputLocation = outputLocation;
            return this;
        }

        public RestoreRequest build() {
            return new RestoreRequest(this);
        }

        public List<SdkField<?>> sdkFields() {
            return SDK_FIELDS;
        }
    }

    public static interface Builder
    extends SdkPojo,
    CopyableBuilder<Builder, RestoreRequest> {
        public Builder days(Integer var1);

        public Builder glacierJobParameters(GlacierJobParameters var1);

        default public Builder glacierJobParameters(Consumer<GlacierJobParameters.Builder> glacierJobParameters) {
            return this.glacierJobParameters((GlacierJobParameters)((GlacierJobParameters.Builder)GlacierJobParameters.builder().applyMutation(glacierJobParameters)).build());
        }

        public Builder type(String var1);

        public Builder type(RestoreRequestType var1);

        public Builder tier(String var1);

        public Builder tier(Tier var1);

        public Builder description(String var1);

        public Builder selectParameters(SelectParameters var1);

        default public Builder selectParameters(Consumer<SelectParameters.Builder> selectParameters) {
            return this.selectParameters((SelectParameters)((SelectParameters.Builder)SelectParameters.builder().applyMutation(selectParameters)).build());
        }

        public Builder outputLocation(OutputLocation var1);

        default public Builder outputLocation(Consumer<OutputLocation.Builder> outputLocation) {
            return this.outputLocation((OutputLocation)((OutputLocation.Builder)OutputLocation.builder().applyMutation(outputLocation)).build());
        }
    }
}

