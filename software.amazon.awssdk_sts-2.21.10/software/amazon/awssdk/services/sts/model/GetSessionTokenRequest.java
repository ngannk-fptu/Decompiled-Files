/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.awscore.AwsRequestOverrideConfiguration
 *  software.amazon.awssdk.awscore.AwsRequestOverrideConfiguration$Builder
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
package software.amazon.awssdk.services.sts.model;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import software.amazon.awssdk.awscore.AwsRequestOverrideConfiguration;
import software.amazon.awssdk.core.SdkField;
import software.amazon.awssdk.core.SdkPojo;
import software.amazon.awssdk.core.protocol.MarshallLocation;
import software.amazon.awssdk.core.protocol.MarshallingType;
import software.amazon.awssdk.core.traits.LocationTrait;
import software.amazon.awssdk.core.traits.Trait;
import software.amazon.awssdk.services.sts.model.StsRequest;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

public final class GetSessionTokenRequest
extends StsRequest
implements ToCopyableBuilder<Builder, GetSessionTokenRequest> {
    private static final SdkField<Integer> DURATION_SECONDS_FIELD = SdkField.builder((MarshallingType)MarshallingType.INTEGER).memberName("DurationSeconds").getter(GetSessionTokenRequest.getter(GetSessionTokenRequest::durationSeconds)).setter(GetSessionTokenRequest.setter(Builder::durationSeconds)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("DurationSeconds").build()}).build();
    private static final SdkField<String> SERIAL_NUMBER_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("SerialNumber").getter(GetSessionTokenRequest.getter(GetSessionTokenRequest::serialNumber)).setter(GetSessionTokenRequest.setter(Builder::serialNumber)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("SerialNumber").build()}).build();
    private static final SdkField<String> TOKEN_CODE_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("TokenCode").getter(GetSessionTokenRequest.getter(GetSessionTokenRequest::tokenCode)).setter(GetSessionTokenRequest.setter(Builder::tokenCode)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("TokenCode").build()}).build();
    private static final List<SdkField<?>> SDK_FIELDS = Collections.unmodifiableList(Arrays.asList(DURATION_SECONDS_FIELD, SERIAL_NUMBER_FIELD, TOKEN_CODE_FIELD));
    private final Integer durationSeconds;
    private final String serialNumber;
    private final String tokenCode;

    private GetSessionTokenRequest(BuilderImpl builder) {
        super(builder);
        this.durationSeconds = builder.durationSeconds;
        this.serialNumber = builder.serialNumber;
        this.tokenCode = builder.tokenCode;
    }

    public final Integer durationSeconds() {
        return this.durationSeconds;
    }

    public final String serialNumber() {
        return this.serialNumber;
    }

    public final String tokenCode() {
        return this.tokenCode;
    }

    @Override
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
        hashCode = 31 * hashCode + super.hashCode();
        hashCode = 31 * hashCode + Objects.hashCode(this.durationSeconds());
        hashCode = 31 * hashCode + Objects.hashCode(this.serialNumber());
        hashCode = 31 * hashCode + Objects.hashCode(this.tokenCode());
        return hashCode;
    }

    public final boolean equals(Object obj) {
        return super.equals(obj) && this.equalsBySdkFields(obj);
    }

    public final boolean equalsBySdkFields(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof GetSessionTokenRequest)) {
            return false;
        }
        GetSessionTokenRequest other = (GetSessionTokenRequest)((Object)obj);
        return Objects.equals(this.durationSeconds(), other.durationSeconds()) && Objects.equals(this.serialNumber(), other.serialNumber()) && Objects.equals(this.tokenCode(), other.tokenCode());
    }

    public final String toString() {
        return ToString.builder((String)"GetSessionTokenRequest").add("DurationSeconds", (Object)this.durationSeconds()).add("SerialNumber", (Object)this.serialNumber()).add("TokenCode", (Object)this.tokenCode()).build();
    }

    public final <T> Optional<T> getValueForField(String fieldName, Class<T> clazz) {
        switch (fieldName) {
            case "DurationSeconds": {
                return Optional.ofNullable(clazz.cast(this.durationSeconds()));
            }
            case "SerialNumber": {
                return Optional.ofNullable(clazz.cast(this.serialNumber()));
            }
            case "TokenCode": {
                return Optional.ofNullable(clazz.cast(this.tokenCode()));
            }
        }
        return Optional.empty();
    }

    public final List<SdkField<?>> sdkFields() {
        return SDK_FIELDS;
    }

    private static <T> Function<Object, T> getter(Function<GetSessionTokenRequest, T> g) {
        return obj -> g.apply((GetSessionTokenRequest)((Object)((Object)obj)));
    }

    private static <T> BiConsumer<Object, T> setter(BiConsumer<Builder, T> s) {
        return (obj, val) -> s.accept((Builder)obj, val);
    }

    static final class BuilderImpl
    extends StsRequest.BuilderImpl
    implements Builder {
        private Integer durationSeconds;
        private String serialNumber;
        private String tokenCode;

        private BuilderImpl() {
        }

        private BuilderImpl(GetSessionTokenRequest model) {
            super(model);
            this.durationSeconds(model.durationSeconds);
            this.serialNumber(model.serialNumber);
            this.tokenCode(model.tokenCode);
        }

        public final Integer getDurationSeconds() {
            return this.durationSeconds;
        }

        public final void setDurationSeconds(Integer durationSeconds) {
            this.durationSeconds = durationSeconds;
        }

        @Override
        public final Builder durationSeconds(Integer durationSeconds) {
            this.durationSeconds = durationSeconds;
            return this;
        }

        public final String getSerialNumber() {
            return this.serialNumber;
        }

        public final void setSerialNumber(String serialNumber) {
            this.serialNumber = serialNumber;
        }

        @Override
        public final Builder serialNumber(String serialNumber) {
            this.serialNumber = serialNumber;
            return this;
        }

        public final String getTokenCode() {
            return this.tokenCode;
        }

        public final void setTokenCode(String tokenCode) {
            this.tokenCode = tokenCode;
        }

        @Override
        public final Builder tokenCode(String tokenCode) {
            this.tokenCode = tokenCode;
            return this;
        }

        @Override
        public Builder overrideConfiguration(AwsRequestOverrideConfiguration overrideConfiguration) {
            super.overrideConfiguration(overrideConfiguration);
            return this;
        }

        @Override
        public Builder overrideConfiguration(Consumer<AwsRequestOverrideConfiguration.Builder> builderConsumer) {
            super.overrideConfiguration(builderConsumer);
            return this;
        }

        @Override
        public GetSessionTokenRequest build() {
            return new GetSessionTokenRequest(this);
        }

        public List<SdkField<?>> sdkFields() {
            return SDK_FIELDS;
        }
    }

    public static interface Builder
    extends StsRequest.Builder,
    SdkPojo,
    CopyableBuilder<Builder, GetSessionTokenRequest> {
        public Builder durationSeconds(Integer var1);

        public Builder serialNumber(String var1);

        public Builder tokenCode(String var1);

        public Builder overrideConfiguration(AwsRequestOverrideConfiguration var1);

        public Builder overrideConfiguration(Consumer<AwsRequestOverrideConfiguration.Builder> var1);
    }
}

