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
import software.amazon.awssdk.services.s3.model.InventoryFrequency;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

public final class InventorySchedule
implements SdkPojo,
Serializable,
ToCopyableBuilder<Builder, InventorySchedule> {
    private static final SdkField<String> FREQUENCY_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("Frequency").getter(InventorySchedule.getter(InventorySchedule::frequencyAsString)).setter(InventorySchedule.setter(Builder::frequency)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("Frequency").unmarshallLocationName("Frequency").build(), RequiredTrait.create()}).build();
    private static final List<SdkField<?>> SDK_FIELDS = Collections.unmodifiableList(Arrays.asList(FREQUENCY_FIELD));
    private static final long serialVersionUID = 1L;
    private final String frequency;

    private InventorySchedule(BuilderImpl builder) {
        this.frequency = builder.frequency;
    }

    public final InventoryFrequency frequency() {
        return InventoryFrequency.fromValue(this.frequency);
    }

    public final String frequencyAsString() {
        return this.frequency;
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
        hashCode = 31 * hashCode + Objects.hashCode(this.frequencyAsString());
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
        if (!(obj instanceof InventorySchedule)) {
            return false;
        }
        InventorySchedule other = (InventorySchedule)obj;
        return Objects.equals(this.frequencyAsString(), other.frequencyAsString());
    }

    public final String toString() {
        return ToString.builder((String)"InventorySchedule").add("Frequency", (Object)this.frequencyAsString()).build();
    }

    public final <T> Optional<T> getValueForField(String fieldName, Class<T> clazz) {
        switch (fieldName) {
            case "Frequency": {
                return Optional.ofNullable(clazz.cast(this.frequencyAsString()));
            }
        }
        return Optional.empty();
    }

    public final List<SdkField<?>> sdkFields() {
        return SDK_FIELDS;
    }

    private static <T> Function<Object, T> getter(Function<InventorySchedule, T> g) {
        return obj -> g.apply((InventorySchedule)obj);
    }

    private static <T> BiConsumer<Object, T> setter(BiConsumer<Builder, T> s) {
        return (obj, val) -> s.accept((Builder)obj, val);
    }

    static final class BuilderImpl
    implements Builder {
        private String frequency;

        private BuilderImpl() {
        }

        private BuilderImpl(InventorySchedule model) {
            this.frequency(model.frequency);
        }

        public final String getFrequency() {
            return this.frequency;
        }

        public final void setFrequency(String frequency) {
            this.frequency = frequency;
        }

        @Override
        public final Builder frequency(String frequency) {
            this.frequency = frequency;
            return this;
        }

        @Override
        public final Builder frequency(InventoryFrequency frequency) {
            this.frequency(frequency == null ? null : frequency.toString());
            return this;
        }

        public InventorySchedule build() {
            return new InventorySchedule(this);
        }

        public List<SdkField<?>> sdkFields() {
            return SDK_FIELDS;
        }
    }

    public static interface Builder
    extends SdkPojo,
    CopyableBuilder<Builder, InventorySchedule> {
        public Builder frequency(String var1);

        public Builder frequency(InventoryFrequency var1);
    }
}

