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
import java.util.function.Function;
import software.amazon.awssdk.core.SdkField;
import software.amazon.awssdk.core.SdkPojo;
import software.amazon.awssdk.core.protocol.MarshallLocation;
import software.amazon.awssdk.core.protocol.MarshallingType;
import software.amazon.awssdk.core.traits.LocationTrait;
import software.amazon.awssdk.core.traits.RequiredTrait;
import software.amazon.awssdk.core.traits.Trait;
import software.amazon.awssdk.services.s3.model.Payer;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

public final class RequestPaymentConfiguration
implements SdkPojo,
Serializable,
ToCopyableBuilder<Builder, RequestPaymentConfiguration> {
    private static final SdkField<String> PAYER_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("Payer").getter(RequestPaymentConfiguration.getter(RequestPaymentConfiguration::payerAsString)).setter(RequestPaymentConfiguration.setter(Builder::payer)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("Payer").unmarshallLocationName("Payer").build(), RequiredTrait.create()}).build();
    private static final List<SdkField<?>> SDK_FIELDS = Collections.unmodifiableList(Arrays.asList(PAYER_FIELD));
    private static final long serialVersionUID = 1L;
    private final String payer;

    private RequestPaymentConfiguration(BuilderImpl builder) {
        this.payer = builder.payer;
    }

    public final Payer payer() {
        return Payer.fromValue(this.payer);
    }

    public final String payerAsString() {
        return this.payer;
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
        hashCode = 31 * hashCode + Objects.hashCode(this.payerAsString());
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
        if (!(obj instanceof RequestPaymentConfiguration)) {
            return false;
        }
        RequestPaymentConfiguration other = (RequestPaymentConfiguration)obj;
        return Objects.equals(this.payerAsString(), other.payerAsString());
    }

    public final String toString() {
        return ToString.builder((String)"RequestPaymentConfiguration").add("Payer", (Object)this.payerAsString()).build();
    }

    public final <T> Optional<T> getValueForField(String fieldName, Class<T> clazz) {
        switch (fieldName) {
            case "Payer": {
                return Optional.ofNullable(clazz.cast(this.payerAsString()));
            }
        }
        return Optional.empty();
    }

    public final List<SdkField<?>> sdkFields() {
        return SDK_FIELDS;
    }

    private static <T> Function<Object, T> getter(Function<RequestPaymentConfiguration, T> g) {
        return obj -> g.apply((RequestPaymentConfiguration)obj);
    }

    private static <T> BiConsumer<Object, T> setter(BiConsumer<Builder, T> s) {
        return (obj, val) -> s.accept((Builder)obj, val);
    }

    static final class BuilderImpl
    implements Builder {
        private String payer;

        private BuilderImpl() {
        }

        private BuilderImpl(RequestPaymentConfiguration model) {
            this.payer(model.payer);
        }

        public final String getPayer() {
            return this.payer;
        }

        public final void setPayer(String payer) {
            this.payer = payer;
        }

        @Override
        public final Builder payer(String payer) {
            this.payer = payer;
            return this;
        }

        @Override
        public final Builder payer(Payer payer) {
            this.payer(payer == null ? null : payer.toString());
            return this;
        }

        public RequestPaymentConfiguration build() {
            return new RequestPaymentConfiguration(this);
        }

        public List<SdkField<?>> sdkFields() {
            return SDK_FIELDS;
        }
    }

    public static interface Builder
    extends SdkPojo,
    CopyableBuilder<Builder, RequestPaymentConfiguration> {
        public Builder payer(String var1);

        public Builder payer(Payer var1);
    }
}

