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
import software.amazon.awssdk.services.s3.model.IntelligentTieringAccessTier;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

public final class Tiering
implements SdkPojo,
Serializable,
ToCopyableBuilder<Builder, Tiering> {
    private static final SdkField<Integer> DAYS_FIELD = SdkField.builder((MarshallingType)MarshallingType.INTEGER).memberName("Days").getter(Tiering.getter(Tiering::days)).setter(Tiering.setter(Builder::days)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("Days").unmarshallLocationName("Days").build(), RequiredTrait.create()}).build();
    private static final SdkField<String> ACCESS_TIER_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("AccessTier").getter(Tiering.getter(Tiering::accessTierAsString)).setter(Tiering.setter(Builder::accessTier)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("AccessTier").unmarshallLocationName("AccessTier").build(), RequiredTrait.create()}).build();
    private static final List<SdkField<?>> SDK_FIELDS = Collections.unmodifiableList(Arrays.asList(DAYS_FIELD, ACCESS_TIER_FIELD));
    private static final long serialVersionUID = 1L;
    private final Integer days;
    private final String accessTier;

    private Tiering(BuilderImpl builder) {
        this.days = builder.days;
        this.accessTier = builder.accessTier;
    }

    public final Integer days() {
        return this.days;
    }

    public final IntelligentTieringAccessTier accessTier() {
        return IntelligentTieringAccessTier.fromValue(this.accessTier);
    }

    public final String accessTierAsString() {
        return this.accessTier;
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
        hashCode = 31 * hashCode + Objects.hashCode(this.accessTierAsString());
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
        if (!(obj instanceof Tiering)) {
            return false;
        }
        Tiering other = (Tiering)obj;
        return Objects.equals(this.days(), other.days()) && Objects.equals(this.accessTierAsString(), other.accessTierAsString());
    }

    public final String toString() {
        return ToString.builder((String)"Tiering").add("Days", (Object)this.days()).add("AccessTier", (Object)this.accessTierAsString()).build();
    }

    public final <T> Optional<T> getValueForField(String fieldName, Class<T> clazz) {
        switch (fieldName) {
            case "Days": {
                return Optional.ofNullable(clazz.cast(this.days()));
            }
            case "AccessTier": {
                return Optional.ofNullable(clazz.cast(this.accessTierAsString()));
            }
        }
        return Optional.empty();
    }

    public final List<SdkField<?>> sdkFields() {
        return SDK_FIELDS;
    }

    private static <T> Function<Object, T> getter(Function<Tiering, T> g) {
        return obj -> g.apply((Tiering)obj);
    }

    private static <T> BiConsumer<Object, T> setter(BiConsumer<Builder, T> s) {
        return (obj, val) -> s.accept((Builder)obj, val);
    }

    static final class BuilderImpl
    implements Builder {
        private Integer days;
        private String accessTier;

        private BuilderImpl() {
        }

        private BuilderImpl(Tiering model) {
            this.days(model.days);
            this.accessTier(model.accessTier);
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

        public final String getAccessTier() {
            return this.accessTier;
        }

        public final void setAccessTier(String accessTier) {
            this.accessTier = accessTier;
        }

        @Override
        public final Builder accessTier(String accessTier) {
            this.accessTier = accessTier;
            return this;
        }

        @Override
        public final Builder accessTier(IntelligentTieringAccessTier accessTier) {
            this.accessTier(accessTier == null ? null : accessTier.toString());
            return this;
        }

        public Tiering build() {
            return new Tiering(this);
        }

        public List<SdkField<?>> sdkFields() {
            return SDK_FIELDS;
        }
    }

    public static interface Builder
    extends SdkPojo,
    CopyableBuilder<Builder, Tiering> {
        public Builder days(Integer var1);

        public Builder accessTier(String var1);

        public Builder accessTier(IntelligentTieringAccessTier var1);
    }
}

