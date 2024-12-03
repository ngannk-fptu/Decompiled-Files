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
import software.amazon.awssdk.services.s3.model.ReplicationTimeStatus;
import software.amazon.awssdk.services.s3.model.ReplicationTimeValue;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

public final class ReplicationTime
implements SdkPojo,
Serializable,
ToCopyableBuilder<Builder, ReplicationTime> {
    private static final SdkField<String> STATUS_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("Status").getter(ReplicationTime.getter(ReplicationTime::statusAsString)).setter(ReplicationTime.setter(Builder::status)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("Status").unmarshallLocationName("Status").build(), RequiredTrait.create()}).build();
    private static final SdkField<ReplicationTimeValue> TIME_FIELD = SdkField.builder((MarshallingType)MarshallingType.SDK_POJO).memberName("Time").getter(ReplicationTime.getter(ReplicationTime::time)).setter(ReplicationTime.setter(Builder::time)).constructor(ReplicationTimeValue::builder).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("Time").unmarshallLocationName("Time").build(), RequiredTrait.create()}).build();
    private static final List<SdkField<?>> SDK_FIELDS = Collections.unmodifiableList(Arrays.asList(STATUS_FIELD, TIME_FIELD));
    private static final long serialVersionUID = 1L;
    private final String status;
    private final ReplicationTimeValue time;

    private ReplicationTime(BuilderImpl builder) {
        this.status = builder.status;
        this.time = builder.time;
    }

    public final ReplicationTimeStatus status() {
        return ReplicationTimeStatus.fromValue(this.status);
    }

    public final String statusAsString() {
        return this.status;
    }

    public final ReplicationTimeValue time() {
        return this.time;
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
        hashCode = 31 * hashCode + Objects.hashCode(this.time());
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
        if (!(obj instanceof ReplicationTime)) {
            return false;
        }
        ReplicationTime other = (ReplicationTime)obj;
        return Objects.equals(this.statusAsString(), other.statusAsString()) && Objects.equals(this.time(), other.time());
    }

    public final String toString() {
        return ToString.builder((String)"ReplicationTime").add("Status", (Object)this.statusAsString()).add("Time", (Object)this.time()).build();
    }

    public final <T> Optional<T> getValueForField(String fieldName, Class<T> clazz) {
        switch (fieldName) {
            case "Status": {
                return Optional.ofNullable(clazz.cast(this.statusAsString()));
            }
            case "Time": {
                return Optional.ofNullable(clazz.cast(this.time()));
            }
        }
        return Optional.empty();
    }

    public final List<SdkField<?>> sdkFields() {
        return SDK_FIELDS;
    }

    private static <T> Function<Object, T> getter(Function<ReplicationTime, T> g) {
        return obj -> g.apply((ReplicationTime)obj);
    }

    private static <T> BiConsumer<Object, T> setter(BiConsumer<Builder, T> s) {
        return (obj, val) -> s.accept((Builder)obj, val);
    }

    static final class BuilderImpl
    implements Builder {
        private String status;
        private ReplicationTimeValue time;

        private BuilderImpl() {
        }

        private BuilderImpl(ReplicationTime model) {
            this.status(model.status);
            this.time(model.time);
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
        public final Builder status(ReplicationTimeStatus status) {
            this.status(status == null ? null : status.toString());
            return this;
        }

        public final ReplicationTimeValue.Builder getTime() {
            return this.time != null ? this.time.toBuilder() : null;
        }

        public final void setTime(ReplicationTimeValue.BuilderImpl time) {
            this.time = time != null ? time.build() : null;
        }

        @Override
        public final Builder time(ReplicationTimeValue time) {
            this.time = time;
            return this;
        }

        public ReplicationTime build() {
            return new ReplicationTime(this);
        }

        public List<SdkField<?>> sdkFields() {
            return SDK_FIELDS;
        }
    }

    public static interface Builder
    extends SdkPojo,
    CopyableBuilder<Builder, ReplicationTime> {
        public Builder status(String var1);

        public Builder status(ReplicationTimeStatus var1);

        public Builder time(ReplicationTimeValue var1);

        default public Builder time(Consumer<ReplicationTimeValue.Builder> time) {
            return this.time((ReplicationTimeValue)((ReplicationTimeValue.Builder)ReplicationTimeValue.builder().applyMutation(time)).build());
        }
    }
}

