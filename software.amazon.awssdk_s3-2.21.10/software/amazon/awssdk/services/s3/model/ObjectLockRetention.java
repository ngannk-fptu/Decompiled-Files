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
import software.amazon.awssdk.services.s3.model.ObjectLockRetentionMode;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

public final class ObjectLockRetention
implements SdkPojo,
Serializable,
ToCopyableBuilder<Builder, ObjectLockRetention> {
    private static final SdkField<String> MODE_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("Mode").getter(ObjectLockRetention.getter(ObjectLockRetention::modeAsString)).setter(ObjectLockRetention.setter(Builder::mode)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("Mode").unmarshallLocationName("Mode").build()}).build();
    private static final SdkField<Instant> RETAIN_UNTIL_DATE_FIELD = SdkField.builder((MarshallingType)MarshallingType.INSTANT).memberName("RetainUntilDate").getter(ObjectLockRetention.getter(ObjectLockRetention::retainUntilDate)).setter(ObjectLockRetention.setter(Builder::retainUntilDate)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("RetainUntilDate").unmarshallLocationName("RetainUntilDate").build(), TimestampFormatTrait.create((TimestampFormatTrait.Format)TimestampFormatTrait.Format.ISO_8601)}).build();
    private static final List<SdkField<?>> SDK_FIELDS = Collections.unmodifiableList(Arrays.asList(MODE_FIELD, RETAIN_UNTIL_DATE_FIELD));
    private static final long serialVersionUID = 1L;
    private final String mode;
    private final Instant retainUntilDate;

    private ObjectLockRetention(BuilderImpl builder) {
        this.mode = builder.mode;
        this.retainUntilDate = builder.retainUntilDate;
    }

    public final ObjectLockRetentionMode mode() {
        return ObjectLockRetentionMode.fromValue(this.mode);
    }

    public final String modeAsString() {
        return this.mode;
    }

    public final Instant retainUntilDate() {
        return this.retainUntilDate;
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
        hashCode = 31 * hashCode + Objects.hashCode(this.modeAsString());
        hashCode = 31 * hashCode + Objects.hashCode(this.retainUntilDate());
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
        if (!(obj instanceof ObjectLockRetention)) {
            return false;
        }
        ObjectLockRetention other = (ObjectLockRetention)obj;
        return Objects.equals(this.modeAsString(), other.modeAsString()) && Objects.equals(this.retainUntilDate(), other.retainUntilDate());
    }

    public final String toString() {
        return ToString.builder((String)"ObjectLockRetention").add("Mode", (Object)this.modeAsString()).add("RetainUntilDate", (Object)this.retainUntilDate()).build();
    }

    public final <T> Optional<T> getValueForField(String fieldName, Class<T> clazz) {
        switch (fieldName) {
            case "Mode": {
                return Optional.ofNullable(clazz.cast(this.modeAsString()));
            }
            case "RetainUntilDate": {
                return Optional.ofNullable(clazz.cast(this.retainUntilDate()));
            }
        }
        return Optional.empty();
    }

    public final List<SdkField<?>> sdkFields() {
        return SDK_FIELDS;
    }

    private static <T> Function<Object, T> getter(Function<ObjectLockRetention, T> g) {
        return obj -> g.apply((ObjectLockRetention)obj);
    }

    private static <T> BiConsumer<Object, T> setter(BiConsumer<Builder, T> s) {
        return (obj, val) -> s.accept((Builder)obj, val);
    }

    static final class BuilderImpl
    implements Builder {
        private String mode;
        private Instant retainUntilDate;

        private BuilderImpl() {
        }

        private BuilderImpl(ObjectLockRetention model) {
            this.mode(model.mode);
            this.retainUntilDate(model.retainUntilDate);
        }

        public final String getMode() {
            return this.mode;
        }

        public final void setMode(String mode) {
            this.mode = mode;
        }

        @Override
        public final Builder mode(String mode) {
            this.mode = mode;
            return this;
        }

        @Override
        public final Builder mode(ObjectLockRetentionMode mode) {
            this.mode(mode == null ? null : mode.toString());
            return this;
        }

        public final Instant getRetainUntilDate() {
            return this.retainUntilDate;
        }

        public final void setRetainUntilDate(Instant retainUntilDate) {
            this.retainUntilDate = retainUntilDate;
        }

        @Override
        public final Builder retainUntilDate(Instant retainUntilDate) {
            this.retainUntilDate = retainUntilDate;
            return this;
        }

        public ObjectLockRetention build() {
            return new ObjectLockRetention(this);
        }

        public List<SdkField<?>> sdkFields() {
            return SDK_FIELDS;
        }
    }

    public static interface Builder
    extends SdkPojo,
    CopyableBuilder<Builder, ObjectLockRetention> {
        public Builder mode(String var1);

        public Builder mode(ObjectLockRetentionMode var1);

        public Builder retainUntilDate(Instant var1);
    }
}

