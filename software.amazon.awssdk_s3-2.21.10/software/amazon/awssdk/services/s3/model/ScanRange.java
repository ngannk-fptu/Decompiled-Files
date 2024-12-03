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

public final class ScanRange
implements SdkPojo,
Serializable,
ToCopyableBuilder<Builder, ScanRange> {
    private static final SdkField<Long> START_FIELD = SdkField.builder((MarshallingType)MarshallingType.LONG).memberName("Start").getter(ScanRange.getter(ScanRange::start)).setter(ScanRange.setter(Builder::start)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("Start").unmarshallLocationName("Start").build()}).build();
    private static final SdkField<Long> END_FIELD = SdkField.builder((MarshallingType)MarshallingType.LONG).memberName("End").getter(ScanRange.getter(ScanRange::end)).setter(ScanRange.setter(Builder::end)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("End").unmarshallLocationName("End").build()}).build();
    private static final List<SdkField<?>> SDK_FIELDS = Collections.unmodifiableList(Arrays.asList(START_FIELD, END_FIELD));
    private static final long serialVersionUID = 1L;
    private final Long start;
    private final Long end;

    private ScanRange(BuilderImpl builder) {
        this.start = builder.start;
        this.end = builder.end;
    }

    public final Long start() {
        return this.start;
    }

    public final Long end() {
        return this.end;
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
        hashCode = 31 * hashCode + Objects.hashCode(this.start());
        hashCode = 31 * hashCode + Objects.hashCode(this.end());
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
        if (!(obj instanceof ScanRange)) {
            return false;
        }
        ScanRange other = (ScanRange)obj;
        return Objects.equals(this.start(), other.start()) && Objects.equals(this.end(), other.end());
    }

    public final String toString() {
        return ToString.builder((String)"ScanRange").add("Start", (Object)this.start()).add("End", (Object)this.end()).build();
    }

    public final <T> Optional<T> getValueForField(String fieldName, Class<T> clazz) {
        switch (fieldName) {
            case "Start": {
                return Optional.ofNullable(clazz.cast(this.start()));
            }
            case "End": {
                return Optional.ofNullable(clazz.cast(this.end()));
            }
        }
        return Optional.empty();
    }

    public final List<SdkField<?>> sdkFields() {
        return SDK_FIELDS;
    }

    private static <T> Function<Object, T> getter(Function<ScanRange, T> g) {
        return obj -> g.apply((ScanRange)obj);
    }

    private static <T> BiConsumer<Object, T> setter(BiConsumer<Builder, T> s) {
        return (obj, val) -> s.accept((Builder)obj, val);
    }

    static final class BuilderImpl
    implements Builder {
        private Long start;
        private Long end;

        private BuilderImpl() {
        }

        private BuilderImpl(ScanRange model) {
            this.start(model.start);
            this.end(model.end);
        }

        public final Long getStart() {
            return this.start;
        }

        public final void setStart(Long start) {
            this.start = start;
        }

        @Override
        public final Builder start(Long start) {
            this.start = start;
            return this;
        }

        public final Long getEnd() {
            return this.end;
        }

        public final void setEnd(Long end) {
            this.end = end;
        }

        @Override
        public final Builder end(Long end) {
            this.end = end;
            return this;
        }

        public ScanRange build() {
            return new ScanRange(this);
        }

        public List<SdkField<?>> sdkFields() {
            return SDK_FIELDS;
        }
    }

    public static interface Builder
    extends SdkPojo,
    CopyableBuilder<Builder, ScanRange> {
        public Builder start(Long var1);

        public Builder end(Long var1);
    }
}

