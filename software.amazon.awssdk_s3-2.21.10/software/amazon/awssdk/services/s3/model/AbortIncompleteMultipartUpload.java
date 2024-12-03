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

public final class AbortIncompleteMultipartUpload
implements SdkPojo,
Serializable,
ToCopyableBuilder<Builder, AbortIncompleteMultipartUpload> {
    private static final SdkField<Integer> DAYS_AFTER_INITIATION_FIELD = SdkField.builder((MarshallingType)MarshallingType.INTEGER).memberName("DaysAfterInitiation").getter(AbortIncompleteMultipartUpload.getter(AbortIncompleteMultipartUpload::daysAfterInitiation)).setter(AbortIncompleteMultipartUpload.setter(Builder::daysAfterInitiation)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("DaysAfterInitiation").unmarshallLocationName("DaysAfterInitiation").build()}).build();
    private static final List<SdkField<?>> SDK_FIELDS = Collections.unmodifiableList(Arrays.asList(DAYS_AFTER_INITIATION_FIELD));
    private static final long serialVersionUID = 1L;
    private final Integer daysAfterInitiation;

    private AbortIncompleteMultipartUpload(BuilderImpl builder) {
        this.daysAfterInitiation = builder.daysAfterInitiation;
    }

    public final Integer daysAfterInitiation() {
        return this.daysAfterInitiation;
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
        hashCode = 31 * hashCode + Objects.hashCode(this.daysAfterInitiation());
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
        if (!(obj instanceof AbortIncompleteMultipartUpload)) {
            return false;
        }
        AbortIncompleteMultipartUpload other = (AbortIncompleteMultipartUpload)obj;
        return Objects.equals(this.daysAfterInitiation(), other.daysAfterInitiation());
    }

    public final String toString() {
        return ToString.builder((String)"AbortIncompleteMultipartUpload").add("DaysAfterInitiation", (Object)this.daysAfterInitiation()).build();
    }

    public final <T> Optional<T> getValueForField(String fieldName, Class<T> clazz) {
        switch (fieldName) {
            case "DaysAfterInitiation": {
                return Optional.ofNullable(clazz.cast(this.daysAfterInitiation()));
            }
        }
        return Optional.empty();
    }

    public final List<SdkField<?>> sdkFields() {
        return SDK_FIELDS;
    }

    private static <T> Function<Object, T> getter(Function<AbortIncompleteMultipartUpload, T> g) {
        return obj -> g.apply((AbortIncompleteMultipartUpload)obj);
    }

    private static <T> BiConsumer<Object, T> setter(BiConsumer<Builder, T> s) {
        return (obj, val) -> s.accept((Builder)obj, val);
    }

    static final class BuilderImpl
    implements Builder {
        private Integer daysAfterInitiation;

        private BuilderImpl() {
        }

        private BuilderImpl(AbortIncompleteMultipartUpload model) {
            this.daysAfterInitiation(model.daysAfterInitiation);
        }

        public final Integer getDaysAfterInitiation() {
            return this.daysAfterInitiation;
        }

        public final void setDaysAfterInitiation(Integer daysAfterInitiation) {
            this.daysAfterInitiation = daysAfterInitiation;
        }

        @Override
        public final Builder daysAfterInitiation(Integer daysAfterInitiation) {
            this.daysAfterInitiation = daysAfterInitiation;
            return this;
        }

        public AbortIncompleteMultipartUpload build() {
            return new AbortIncompleteMultipartUpload(this);
        }

        public List<SdkField<?>> sdkFields() {
            return SDK_FIELDS;
        }
    }

    public static interface Builder
    extends SdkPojo,
    CopyableBuilder<Builder, AbortIncompleteMultipartUpload> {
        public Builder daysAfterInitiation(Integer var1);
    }
}

