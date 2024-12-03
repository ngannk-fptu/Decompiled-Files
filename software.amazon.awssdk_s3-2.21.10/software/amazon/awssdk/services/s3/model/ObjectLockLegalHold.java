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
import software.amazon.awssdk.services.s3.model.ObjectLockLegalHoldStatus;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

public final class ObjectLockLegalHold
implements SdkPojo,
Serializable,
ToCopyableBuilder<Builder, ObjectLockLegalHold> {
    private static final SdkField<String> STATUS_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("Status").getter(ObjectLockLegalHold.getter(ObjectLockLegalHold::statusAsString)).setter(ObjectLockLegalHold.setter(Builder::status)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("Status").unmarshallLocationName("Status").build()}).build();
    private static final List<SdkField<?>> SDK_FIELDS = Collections.unmodifiableList(Arrays.asList(STATUS_FIELD));
    private static final long serialVersionUID = 1L;
    private final String status;

    private ObjectLockLegalHold(BuilderImpl builder) {
        this.status = builder.status;
    }

    public final ObjectLockLegalHoldStatus status() {
        return ObjectLockLegalHoldStatus.fromValue(this.status);
    }

    public final String statusAsString() {
        return this.status;
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
        if (!(obj instanceof ObjectLockLegalHold)) {
            return false;
        }
        ObjectLockLegalHold other = (ObjectLockLegalHold)obj;
        return Objects.equals(this.statusAsString(), other.statusAsString());
    }

    public final String toString() {
        return ToString.builder((String)"ObjectLockLegalHold").add("Status", (Object)this.statusAsString()).build();
    }

    public final <T> Optional<T> getValueForField(String fieldName, Class<T> clazz) {
        switch (fieldName) {
            case "Status": {
                return Optional.ofNullable(clazz.cast(this.statusAsString()));
            }
        }
        return Optional.empty();
    }

    public final List<SdkField<?>> sdkFields() {
        return SDK_FIELDS;
    }

    private static <T> Function<Object, T> getter(Function<ObjectLockLegalHold, T> g) {
        return obj -> g.apply((ObjectLockLegalHold)obj);
    }

    private static <T> BiConsumer<Object, T> setter(BiConsumer<Builder, T> s) {
        return (obj, val) -> s.accept((Builder)obj, val);
    }

    static final class BuilderImpl
    implements Builder {
        private String status;

        private BuilderImpl() {
        }

        private BuilderImpl(ObjectLockLegalHold model) {
            this.status(model.status);
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
        public final Builder status(ObjectLockLegalHoldStatus status) {
            this.status(status == null ? null : status.toString());
            return this;
        }

        public ObjectLockLegalHold build() {
            return new ObjectLockLegalHold(this);
        }

        public List<SdkField<?>> sdkFields() {
            return SDK_FIELDS;
        }
    }

    public static interface Builder
    extends SdkPojo,
    CopyableBuilder<Builder, ObjectLockLegalHold> {
        public Builder status(String var1);

        public Builder status(ObjectLockLegalHoldStatus var1);
    }
}

