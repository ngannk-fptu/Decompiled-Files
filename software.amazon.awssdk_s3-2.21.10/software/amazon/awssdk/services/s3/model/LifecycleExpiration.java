/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.core.SdkField
 *  software.amazon.awssdk.core.SdkPojo
 *  software.amazon.awssdk.core.protocol.MarshallLocation
 *  software.amazon.awssdk.core.protocol.MarshallingType
 *  software.amazon.awssdk.core.traits.LocationTrait
 *  software.amazon.awssdk.core.traits.TimestampFormatTrait
 *  software.amazon.awssdk.core.traits.TimestampFormatTrait$Format
 *  software.amazon.awssdk.core.traits.Trait
 *  software.amazon.awssdk.utils.ToString
 *  software.amazon.awssdk.utils.builder.CopyableBuilder
 *  software.amazon.awssdk.utils.builder.ToCopyableBuilder
 */
package software.amazon.awssdk.services.s3.model;

import java.io.Serializable;
import java.time.Instant;
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
import software.amazon.awssdk.core.traits.TimestampFormatTrait;
import software.amazon.awssdk.core.traits.Trait;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

public final class LifecycleExpiration
implements SdkPojo,
Serializable,
ToCopyableBuilder<Builder, LifecycleExpiration> {
    private static final SdkField<Instant> DATE_FIELD = SdkField.builder((MarshallingType)MarshallingType.INSTANT).memberName("Date").getter(LifecycleExpiration.getter(LifecycleExpiration::date)).setter(LifecycleExpiration.setter(Builder::date)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("Date").unmarshallLocationName("Date").build(), TimestampFormatTrait.create((TimestampFormatTrait.Format)TimestampFormatTrait.Format.ISO_8601)}).build();
    private static final SdkField<Integer> DAYS_FIELD = SdkField.builder((MarshallingType)MarshallingType.INTEGER).memberName("Days").getter(LifecycleExpiration.getter(LifecycleExpiration::days)).setter(LifecycleExpiration.setter(Builder::days)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("Days").unmarshallLocationName("Days").build()}).build();
    private static final SdkField<Boolean> EXPIRED_OBJECT_DELETE_MARKER_FIELD = SdkField.builder((MarshallingType)MarshallingType.BOOLEAN).memberName("ExpiredObjectDeleteMarker").getter(LifecycleExpiration.getter(LifecycleExpiration::expiredObjectDeleteMarker)).setter(LifecycleExpiration.setter(Builder::expiredObjectDeleteMarker)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("ExpiredObjectDeleteMarker").unmarshallLocationName("ExpiredObjectDeleteMarker").build()}).build();
    private static final List<SdkField<?>> SDK_FIELDS = Collections.unmodifiableList(Arrays.asList(DATE_FIELD, DAYS_FIELD, EXPIRED_OBJECT_DELETE_MARKER_FIELD));
    private static final long serialVersionUID = 1L;
    private final Instant date;
    private final Integer days;
    private final Boolean expiredObjectDeleteMarker;

    private LifecycleExpiration(BuilderImpl builder) {
        this.date = builder.date;
        this.days = builder.days;
        this.expiredObjectDeleteMarker = builder.expiredObjectDeleteMarker;
    }

    public final Instant date() {
        return this.date;
    }

    public final Integer days() {
        return this.days;
    }

    public final Boolean expiredObjectDeleteMarker() {
        return this.expiredObjectDeleteMarker;
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
        hashCode = 31 * hashCode + Objects.hashCode(this.date());
        hashCode = 31 * hashCode + Objects.hashCode(this.days());
        hashCode = 31 * hashCode + Objects.hashCode(this.expiredObjectDeleteMarker());
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
        if (!(obj instanceof LifecycleExpiration)) {
            return false;
        }
        LifecycleExpiration other = (LifecycleExpiration)obj;
        return Objects.equals(this.date(), other.date()) && Objects.equals(this.days(), other.days()) && Objects.equals(this.expiredObjectDeleteMarker(), other.expiredObjectDeleteMarker());
    }

    public final String toString() {
        return ToString.builder((String)"LifecycleExpiration").add("Date", (Object)this.date()).add("Days", (Object)this.days()).add("ExpiredObjectDeleteMarker", (Object)this.expiredObjectDeleteMarker()).build();
    }

    public final <T> Optional<T> getValueForField(String fieldName, Class<T> clazz) {
        switch (fieldName) {
            case "Date": {
                return Optional.ofNullable(clazz.cast(this.date()));
            }
            case "Days": {
                return Optional.ofNullable(clazz.cast(this.days()));
            }
            case "ExpiredObjectDeleteMarker": {
                return Optional.ofNullable(clazz.cast(this.expiredObjectDeleteMarker()));
            }
        }
        return Optional.empty();
    }

    public final List<SdkField<?>> sdkFields() {
        return SDK_FIELDS;
    }

    private static <T> Function<Object, T> getter(Function<LifecycleExpiration, T> g) {
        return obj -> g.apply((LifecycleExpiration)obj);
    }

    private static <T> BiConsumer<Object, T> setter(BiConsumer<Builder, T> s) {
        return (obj, val) -> s.accept((Builder)obj, val);
    }

    static final class BuilderImpl
    implements Builder {
        private Instant date;
        private Integer days;
        private Boolean expiredObjectDeleteMarker;

        private BuilderImpl() {
        }

        private BuilderImpl(LifecycleExpiration model) {
            this.date(model.date);
            this.days(model.days);
            this.expiredObjectDeleteMarker(model.expiredObjectDeleteMarker);
        }

        public final Instant getDate() {
            return this.date;
        }

        public final void setDate(Instant date) {
            this.date = date;
        }

        @Override
        public final Builder date(Instant date) {
            this.date = date;
            return this;
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

        public final Boolean getExpiredObjectDeleteMarker() {
            return this.expiredObjectDeleteMarker;
        }

        public final void setExpiredObjectDeleteMarker(Boolean expiredObjectDeleteMarker) {
            this.expiredObjectDeleteMarker = expiredObjectDeleteMarker;
        }

        @Override
        public final Builder expiredObjectDeleteMarker(Boolean expiredObjectDeleteMarker) {
            this.expiredObjectDeleteMarker = expiredObjectDeleteMarker;
            return this;
        }

        public LifecycleExpiration build() {
            return new LifecycleExpiration(this);
        }

        public List<SdkField<?>> sdkFields() {
            return SDK_FIELDS;
        }
    }

    public static interface Builder
    extends SdkPojo,
    CopyableBuilder<Builder, LifecycleExpiration> {
        public Builder date(Instant var1);

        public Builder days(Integer var1);

        public Builder expiredObjectDeleteMarker(Boolean var1);
    }
}

