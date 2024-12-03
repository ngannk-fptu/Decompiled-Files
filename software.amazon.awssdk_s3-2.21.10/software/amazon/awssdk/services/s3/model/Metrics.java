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
import java.util.function.Consumer;
import java.util.function.Function;
import software.amazon.awssdk.core.SdkField;
import software.amazon.awssdk.core.SdkPojo;
import software.amazon.awssdk.core.protocol.MarshallLocation;
import software.amazon.awssdk.core.protocol.MarshallingType;
import software.amazon.awssdk.core.traits.LocationTrait;
import software.amazon.awssdk.core.traits.RequiredTrait;
import software.amazon.awssdk.core.traits.Trait;
import software.amazon.awssdk.services.s3.model.MetricsStatus;
import software.amazon.awssdk.services.s3.model.ReplicationTimeValue;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

public final class Metrics
implements SdkPojo,
Serializable,
ToCopyableBuilder<Builder, Metrics> {
    private static final SdkField<String> STATUS_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("Status").getter(Metrics.getter(Metrics::statusAsString)).setter(Metrics.setter(Builder::status)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("Status").unmarshallLocationName("Status").build(), RequiredTrait.create()}).build();
    private static final SdkField<ReplicationTimeValue> EVENT_THRESHOLD_FIELD = SdkField.builder((MarshallingType)MarshallingType.SDK_POJO).memberName("EventThreshold").getter(Metrics.getter(Metrics::eventThreshold)).setter(Metrics.setter(Builder::eventThreshold)).constructor(ReplicationTimeValue::builder).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("EventThreshold").unmarshallLocationName("EventThreshold").build()}).build();
    private static final List<SdkField<?>> SDK_FIELDS = Collections.unmodifiableList(Arrays.asList(STATUS_FIELD, EVENT_THRESHOLD_FIELD));
    private static final long serialVersionUID = 1L;
    private final String status;
    private final ReplicationTimeValue eventThreshold;

    private Metrics(BuilderImpl builder) {
        this.status = builder.status;
        this.eventThreshold = builder.eventThreshold;
    }

    public final MetricsStatus status() {
        return MetricsStatus.fromValue(this.status);
    }

    public final String statusAsString() {
        return this.status;
    }

    public final ReplicationTimeValue eventThreshold() {
        return this.eventThreshold;
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
        hashCode = 31 * hashCode + Objects.hashCode(this.statusAsString());
        hashCode = 31 * hashCode + Objects.hashCode(this.eventThreshold());
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
        if (!(obj instanceof Metrics)) {
            return false;
        }
        Metrics other = (Metrics)obj;
        return Objects.equals(this.statusAsString(), other.statusAsString()) && Objects.equals(this.eventThreshold(), other.eventThreshold());
    }

    public final String toString() {
        return ToString.builder((String)"Metrics").add("Status", (Object)this.statusAsString()).add("EventThreshold", (Object)this.eventThreshold()).build();
    }

    public final <T> Optional<T> getValueForField(String fieldName, Class<T> clazz) {
        switch (fieldName) {
            case "Status": {
                return Optional.ofNullable(clazz.cast(this.statusAsString()));
            }
            case "EventThreshold": {
                return Optional.ofNullable(clazz.cast(this.eventThreshold()));
            }
        }
        return Optional.empty();
    }

    public final List<SdkField<?>> sdkFields() {
        return SDK_FIELDS;
    }

    private static <T> Function<Object, T> getter(Function<Metrics, T> g) {
        return obj -> g.apply((Metrics)obj);
    }

    private static <T> BiConsumer<Object, T> setter(BiConsumer<Builder, T> s) {
        return (obj, val) -> s.accept((Builder)obj, val);
    }

    static final class BuilderImpl
    implements Builder {
        private String status;
        private ReplicationTimeValue eventThreshold;

        private BuilderImpl() {
        }

        private BuilderImpl(Metrics model) {
            this.status(model.status);
            this.eventThreshold(model.eventThreshold);
        }

        public final String getStatus() {
            return this.status;
        }

        public final void setStatus(String status) {
            this.status = status;
        }

        @Override
        public final Builder status(String status) {
            this.status = status;
            return this;
        }

        @Override
        public final Builder status(MetricsStatus status) {
            this.status(status == null ? null : status.toString());
            return this;
        }

        public final ReplicationTimeValue.Builder getEventThreshold() {
            return this.eventThreshold != null ? this.eventThreshold.toBuilder() : null;
        }

        public final void setEventThreshold(ReplicationTimeValue.BuilderImpl eventThreshold) {
            this.eventThreshold = eventThreshold != null ? eventThreshold.build() : null;
        }

        @Override
        public final Builder eventThreshold(ReplicationTimeValue eventThreshold) {
            this.eventThreshold = eventThreshold;
            return this;
        }

        public Metrics build() {
            return new Metrics(this);
        }

        public List<SdkField<?>> sdkFields() {
            return SDK_FIELDS;
        }
    }

    public static interface Builder
    extends SdkPojo,
    CopyableBuilder<Builder, Metrics> {
        public Builder status(String var1);

        public Builder status(MetricsStatus var1);

        public Builder eventThreshold(ReplicationTimeValue var1);

        default public Builder eventThreshold(Consumer<ReplicationTimeValue.Builder> eventThreshold) {
            return this.eventThreshold((ReplicationTimeValue)((ReplicationTimeValue.Builder)ReplicationTimeValue.builder().applyMutation(eventThreshold)).build());
        }
    }
}

