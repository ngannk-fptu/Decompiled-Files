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
import java.util.function.Function;
import software.amazon.awssdk.core.SdkField;
import software.amazon.awssdk.core.SdkPojo;
import software.amazon.awssdk.core.protocol.MarshallLocation;
import software.amazon.awssdk.core.protocol.MarshallingType;
import software.amazon.awssdk.core.traits.LocationTrait;
import software.amazon.awssdk.core.traits.Trait;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

public final class ReplicationTimeValue
implements SdkPojo,
Serializable,
ToCopyableBuilder<Builder, ReplicationTimeValue> {
    private static final SdkField<Integer> MINUTES_FIELD = SdkField.builder((MarshallingType)MarshallingType.INTEGER).memberName("Minutes").getter(ReplicationTimeValue.getter(ReplicationTimeValue::minutes)).setter(ReplicationTimeValue.setter(Builder::minutes)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("Minutes").unmarshallLocationName("Minutes").build()}).build();
    private static final List<SdkField<?>> SDK_FIELDS = Collections.unmodifiableList(Arrays.asList(MINUTES_FIELD));
    private static final long serialVersionUID = 1L;
    private final Integer minutes;

    private ReplicationTimeValue(BuilderImpl builder) {
        this.minutes = builder.minutes;
    }

    public final Integer minutes() {
        return this.minutes;
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
        hashCode = 31 * hashCode + Objects.hashCode(this.minutes());
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
        if (!(obj instanceof ReplicationTimeValue)) {
            return false;
        }
        ReplicationTimeValue other = (ReplicationTimeValue)obj;
        return Objects.equals(this.minutes(), other.minutes());
    }

    public final String toString() {
        return ToString.builder((String)"ReplicationTimeValue").add("Minutes", (Object)this.minutes()).build();
    }

    public final <T> Optional<T> getValueForField(String fieldName, Class<T> clazz) {
        switch (fieldName) {
            case "Minutes": {
                return Optional.ofNullable(clazz.cast(this.minutes()));
            }
        }
        return Optional.empty();
    }

    public final List<SdkField<?>> sdkFields() {
        return SDK_FIELDS;
    }

    private static <T> Function<Object, T> getter(Function<ReplicationTimeValue, T> g) {
        return obj -> g.apply((ReplicationTimeValue)obj);
    }

    private static <T> BiConsumer<Object, T> setter(BiConsumer<Builder, T> s) {
        return (obj, val) -> s.accept((Builder)obj, val);
    }

    static final class BuilderImpl
    implements Builder {
        private Integer minutes;

        private BuilderImpl() {
        }

        private BuilderImpl(ReplicationTimeValue model) {
            this.minutes(model.minutes);
        }

        public final Integer getMinutes() {
            return this.minutes;
        }

        public final void setMinutes(Integer minutes) {
            this.minutes = minutes;
        }

        @Override
        public final Builder minutes(Integer minutes) {
            this.minutes = minutes;
            return this;
        }

        public ReplicationTimeValue build() {
            return new ReplicationTimeValue(this);
        }

        public List<SdkField<?>> sdkFields() {
            return SDK_FIELDS;
        }
    }

    public static interface Builder
    extends SdkPojo,
    CopyableBuilder<Builder, ReplicationTimeValue> {
        public Builder minutes(Integer var1);
    }
}

