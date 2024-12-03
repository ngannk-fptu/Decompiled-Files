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
import software.amazon.awssdk.services.s3.model.Tier;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

public final class GlacierJobParameters
implements SdkPojo,
Serializable,
ToCopyableBuilder<Builder, GlacierJobParameters> {
    private static final SdkField<String> TIER_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("Tier").getter(GlacierJobParameters.getter(GlacierJobParameters::tierAsString)).setter(GlacierJobParameters.setter(Builder::tier)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("Tier").unmarshallLocationName("Tier").build(), RequiredTrait.create()}).build();
    private static final List<SdkField<?>> SDK_FIELDS = Collections.unmodifiableList(Arrays.asList(TIER_FIELD));
    private static final long serialVersionUID = 1L;
    private final String tier;

    private GlacierJobParameters(BuilderImpl builder) {
        this.tier = builder.tier;
    }

    public final Tier tier() {
        return Tier.fromValue(this.tier);
    }

    public final String tierAsString() {
        return this.tier;
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
        hashCode = 31 * hashCode + Objects.hashCode(this.tierAsString());
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
        if (!(obj instanceof GlacierJobParameters)) {
            return false;
        }
        GlacierJobParameters other = (GlacierJobParameters)obj;
        return Objects.equals(this.tierAsString(), other.tierAsString());
    }

    public final String toString() {
        return ToString.builder((String)"GlacierJobParameters").add("Tier", (Object)this.tierAsString()).build();
    }

    public final <T> Optional<T> getValueForField(String fieldName, Class<T> clazz) {
        switch (fieldName) {
            case "Tier": {
                return Optional.ofNullable(clazz.cast(this.tierAsString()));
            }
        }
        return Optional.empty();
    }

    public final List<SdkField<?>> sdkFields() {
        return SDK_FIELDS;
    }

    private static <T> Function<Object, T> getter(Function<GlacierJobParameters, T> g) {
        return obj -> g.apply((GlacierJobParameters)obj);
    }

    private static <T> BiConsumer<Object, T> setter(BiConsumer<Builder, T> s) {
        return (obj, val) -> s.accept((Builder)obj, val);
    }

    static final class BuilderImpl
    implements Builder {
        private String tier;

        private BuilderImpl() {
        }

        private BuilderImpl(GlacierJobParameters model) {
            this.tier(model.tier);
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

        public GlacierJobParameters build() {
            return new GlacierJobParameters(this);
        }

        public List<SdkField<?>> sdkFields() {
            return SDK_FIELDS;
        }
    }

    public static interface Builder
    extends SdkPojo,
    CopyableBuilder<Builder, GlacierJobParameters> {
        public Builder tier(String var1);

        public Builder tier(Tier var1);
    }
}

