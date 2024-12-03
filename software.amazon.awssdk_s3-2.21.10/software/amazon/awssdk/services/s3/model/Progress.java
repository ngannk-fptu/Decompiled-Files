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

public final class Progress
implements SdkPojo,
Serializable,
ToCopyableBuilder<Builder, Progress> {
    private static final SdkField<Long> BYTES_SCANNED_FIELD = SdkField.builder((MarshallingType)MarshallingType.LONG).memberName("BytesScanned").getter(Progress.getter(Progress::bytesScanned)).setter(Progress.setter(Builder::bytesScanned)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("BytesScanned").unmarshallLocationName("BytesScanned").build()}).build();
    private static final SdkField<Long> BYTES_PROCESSED_FIELD = SdkField.builder((MarshallingType)MarshallingType.LONG).memberName("BytesProcessed").getter(Progress.getter(Progress::bytesProcessed)).setter(Progress.setter(Builder::bytesProcessed)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("BytesProcessed").unmarshallLocationName("BytesProcessed").build()}).build();
    private static final SdkField<Long> BYTES_RETURNED_FIELD = SdkField.builder((MarshallingType)MarshallingType.LONG).memberName("BytesReturned").getter(Progress.getter(Progress::bytesReturned)).setter(Progress.setter(Builder::bytesReturned)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("BytesReturned").unmarshallLocationName("BytesReturned").build()}).build();
    private static final List<SdkField<?>> SDK_FIELDS = Collections.unmodifiableList(Arrays.asList(BYTES_SCANNED_FIELD, BYTES_PROCESSED_FIELD, BYTES_RETURNED_FIELD));
    private static final long serialVersionUID = 1L;
    private final Long bytesScanned;
    private final Long bytesProcessed;
    private final Long bytesReturned;

    private Progress(BuilderImpl builder) {
        this.bytesScanned = builder.bytesScanned;
        this.bytesProcessed = builder.bytesProcessed;
        this.bytesReturned = builder.bytesReturned;
    }

    public final Long bytesScanned() {
        return this.bytesScanned;
    }

    public final Long bytesProcessed() {
        return this.bytesProcessed;
    }

    public final Long bytesReturned() {
        return this.bytesReturned;
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
        hashCode = 31 * hashCode + Objects.hashCode(this.bytesScanned());
        hashCode = 31 * hashCode + Objects.hashCode(this.bytesProcessed());
        hashCode = 31 * hashCode + Objects.hashCode(this.bytesReturned());
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
        if (!(obj instanceof Progress)) {
            return false;
        }
        Progress other = (Progress)obj;
        return Objects.equals(this.bytesScanned(), other.bytesScanned()) && Objects.equals(this.bytesProcessed(), other.bytesProcessed()) && Objects.equals(this.bytesReturned(), other.bytesReturned());
    }

    public final String toString() {
        return ToString.builder((String)"Progress").add("BytesScanned", (Object)this.bytesScanned()).add("BytesProcessed", (Object)this.bytesProcessed()).add("BytesReturned", (Object)this.bytesReturned()).build();
    }

    public final <T> Optional<T> getValueForField(String fieldName, Class<T> clazz) {
        switch (fieldName) {
            case "BytesScanned": {
                return Optional.ofNullable(clazz.cast(this.bytesScanned()));
            }
            case "BytesProcessed": {
                return Optional.ofNullable(clazz.cast(this.bytesProcessed()));
            }
            case "BytesReturned": {
                return Optional.ofNullable(clazz.cast(this.bytesReturned()));
            }
        }
        return Optional.empty();
    }

    public final List<SdkField<?>> sdkFields() {
        return SDK_FIELDS;
    }

    private static <T> Function<Object, T> getter(Function<Progress, T> g) {
        return obj -> g.apply((Progress)obj);
    }

    private static <T> BiConsumer<Object, T> setter(BiConsumer<Builder, T> s) {
        return (obj, val) -> s.accept((Builder)obj, val);
    }

    static final class BuilderImpl
    implements Builder {
        private Long bytesScanned;
        private Long bytesProcessed;
        private Long bytesReturned;

        private BuilderImpl() {
        }

        private BuilderImpl(Progress model) {
            this.bytesScanned(model.bytesScanned);
            this.bytesProcessed(model.bytesProcessed);
            this.bytesReturned(model.bytesReturned);
        }

        public final Long getBytesScanned() {
            return this.bytesScanned;
        }

        public final void setBytesScanned(Long bytesScanned) {
            this.bytesScanned = bytesScanned;
        }

        @Override
        public final Builder bytesScanned(Long bytesScanned) {
            this.bytesScanned = bytesScanned;
            return this;
        }

        public final Long getBytesProcessed() {
            return this.bytesProcessed;
        }

        public final void setBytesProcessed(Long bytesProcessed) {
            this.bytesProcessed = bytesProcessed;
        }

        @Override
        public final Builder bytesProcessed(Long bytesProcessed) {
            this.bytesProcessed = bytesProcessed;
            return this;
        }

        public final Long getBytesReturned() {
            return this.bytesReturned;
        }

        public final void setBytesReturned(Long bytesReturned) {
            this.bytesReturned = bytesReturned;
        }

        @Override
        public final Builder bytesReturned(Long bytesReturned) {
            this.bytesReturned = bytesReturned;
            return this;
        }

        public Progress build() {
            return new Progress(this);
        }

        public List<SdkField<?>> sdkFields() {
            return SDK_FIELDS;
        }
    }

    public static interface Builder
    extends SdkPojo,
    CopyableBuilder<Builder, Progress> {
        public Builder bytesScanned(Long var1);

        public Builder bytesProcessed(Long var1);

        public Builder bytesReturned(Long var1);
    }
}

