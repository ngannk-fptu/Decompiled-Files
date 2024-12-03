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
import java.util.function.Consumer;
import java.util.function.Function;
import software.amazon.awssdk.core.SdkField;
import software.amazon.awssdk.core.SdkPojo;
import software.amazon.awssdk.core.protocol.MarshallLocation;
import software.amazon.awssdk.core.protocol.MarshallingType;
import software.amazon.awssdk.core.traits.LocationTrait;
import software.amazon.awssdk.core.traits.Trait;
import software.amazon.awssdk.services.s3.model.LoggingEnabled;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

public final class BucketLoggingStatus
implements SdkPojo,
Serializable,
ToCopyableBuilder<Builder, BucketLoggingStatus> {
    private static final SdkField<LoggingEnabled> LOGGING_ENABLED_FIELD = SdkField.builder((MarshallingType)MarshallingType.SDK_POJO).memberName("LoggingEnabled").getter(BucketLoggingStatus.getter(BucketLoggingStatus::loggingEnabled)).setter(BucketLoggingStatus.setter(Builder::loggingEnabled)).constructor(LoggingEnabled::builder).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("LoggingEnabled").unmarshallLocationName("LoggingEnabled").build()}).build();
    private static final List<SdkField<?>> SDK_FIELDS = Collections.unmodifiableList(Arrays.asList(LOGGING_ENABLED_FIELD));
    private static final long serialVersionUID = 1L;
    private final LoggingEnabled loggingEnabled;

    private BucketLoggingStatus(BuilderImpl builder) {
        this.loggingEnabled = builder.loggingEnabled;
    }

    public final LoggingEnabled loggingEnabled() {
        return this.loggingEnabled;
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
        hashCode = 31 * hashCode + Objects.hashCode(this.loggingEnabled());
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
        if (!(obj instanceof BucketLoggingStatus)) {
            return false;
        }
        BucketLoggingStatus other = (BucketLoggingStatus)obj;
        return Objects.equals(this.loggingEnabled(), other.loggingEnabled());
    }

    public final String toString() {
        return ToString.builder((String)"BucketLoggingStatus").add("LoggingEnabled", (Object)this.loggingEnabled()).build();
    }

    public final <T> Optional<T> getValueForField(String fieldName, Class<T> clazz) {
        switch (fieldName) {
            case "LoggingEnabled": {
                return Optional.ofNullable(clazz.cast(this.loggingEnabled()));
            }
        }
        return Optional.empty();
    }

    public final List<SdkField<?>> sdkFields() {
        return SDK_FIELDS;
    }

    private static <T> Function<Object, T> getter(Function<BucketLoggingStatus, T> g) {
        return obj -> g.apply((BucketLoggingStatus)obj);
    }

    private static <T> BiConsumer<Object, T> setter(BiConsumer<Builder, T> s) {
        return (obj, val) -> s.accept((Builder)obj, val);
    }

    static final class BuilderImpl
    implements Builder {
        private LoggingEnabled loggingEnabled;

        private BuilderImpl() {
        }

        private BuilderImpl(BucketLoggingStatus model) {
            this.loggingEnabled(model.loggingEnabled);
        }

        public final LoggingEnabled.Builder getLoggingEnabled() {
            return this.loggingEnabled != null ? this.loggingEnabled.toBuilder() : null;
        }

        public final void setLoggingEnabled(LoggingEnabled.BuilderImpl loggingEnabled) {
            this.loggingEnabled = loggingEnabled != null ? loggingEnabled.build() : null;
        }

        @Override
        public final Builder loggingEnabled(LoggingEnabled loggingEnabled) {
            this.loggingEnabled = loggingEnabled;
            return this;
        }

        public BucketLoggingStatus build() {
            return new BucketLoggingStatus(this);
        }

        public List<SdkField<?>> sdkFields() {
            return SDK_FIELDS;
        }
    }

    public static interface Builder
    extends SdkPojo,
    CopyableBuilder<Builder, BucketLoggingStatus> {
        public Builder loggingEnabled(LoggingEnabled var1);

        default public Builder loggingEnabled(Consumer<LoggingEnabled.Builder> loggingEnabled) {
            return this.loggingEnabled((LoggingEnabled)((LoggingEnabled.Builder)LoggingEnabled.builder().applyMutation(loggingEnabled)).build());
        }
    }
}

