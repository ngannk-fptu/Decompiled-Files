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
import software.amazon.awssdk.core.traits.Trait;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

public final class RestoreStatus
implements SdkPojo,
Serializable,
ToCopyableBuilder<Builder, RestoreStatus> {
    private static final SdkField<Boolean> IS_RESTORE_IN_PROGRESS_FIELD = SdkField.builder((MarshallingType)MarshallingType.BOOLEAN).memberName("IsRestoreInProgress").getter(RestoreStatus.getter(RestoreStatus::isRestoreInProgress)).setter(RestoreStatus.setter(Builder::isRestoreInProgress)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("IsRestoreInProgress").unmarshallLocationName("IsRestoreInProgress").build()}).build();
    private static final SdkField<Instant> RESTORE_EXPIRY_DATE_FIELD = SdkField.builder((MarshallingType)MarshallingType.INSTANT).memberName("RestoreExpiryDate").getter(RestoreStatus.getter(RestoreStatus::restoreExpiryDate)).setter(RestoreStatus.setter(Builder::restoreExpiryDate)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("RestoreExpiryDate").unmarshallLocationName("RestoreExpiryDate").build()}).build();
    private static final List<SdkField<?>> SDK_FIELDS = Collections.unmodifiableList(Arrays.asList(IS_RESTORE_IN_PROGRESS_FIELD, RESTORE_EXPIRY_DATE_FIELD));
    private static final long serialVersionUID = 1L;
    private final Boolean isRestoreInProgress;
    private final Instant restoreExpiryDate;

    private RestoreStatus(BuilderImpl builder) {
        this.isRestoreInProgress = builder.isRestoreInProgress;
        this.restoreExpiryDate = builder.restoreExpiryDate;
    }

    public final Boolean isRestoreInProgress() {
        return this.isRestoreInProgress;
    }

    public final Instant restoreExpiryDate() {
        return this.restoreExpiryDate;
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
        hashCode = 31 * hashCode + Objects.hashCode(this.isRestoreInProgress());
        hashCode = 31 * hashCode + Objects.hashCode(this.restoreExpiryDate());
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
        if (!(obj instanceof RestoreStatus)) {
            return false;
        }
        RestoreStatus other = (RestoreStatus)obj;
        return Objects.equals(this.isRestoreInProgress(), other.isRestoreInProgress()) && Objects.equals(this.restoreExpiryDate(), other.restoreExpiryDate());
    }

    public final String toString() {
        return ToString.builder((String)"RestoreStatus").add("IsRestoreInProgress", (Object)this.isRestoreInProgress()).add("RestoreExpiryDate", (Object)this.restoreExpiryDate()).build();
    }

    public final <T> Optional<T> getValueForField(String fieldName, Class<T> clazz) {
        switch (fieldName) {
            case "IsRestoreInProgress": {
                return Optional.ofNullable(clazz.cast(this.isRestoreInProgress()));
            }
            case "RestoreExpiryDate": {
                return Optional.ofNullable(clazz.cast(this.restoreExpiryDate()));
            }
        }
        return Optional.empty();
    }

    public final List<SdkField<?>> sdkFields() {
        return SDK_FIELDS;
    }

    private static <T> Function<Object, T> getter(Function<RestoreStatus, T> g) {
        return obj -> g.apply((RestoreStatus)obj);
    }

    private static <T> BiConsumer<Object, T> setter(BiConsumer<Builder, T> s) {
        return (obj, val) -> s.accept((Builder)obj, val);
    }

    static final class BuilderImpl
    implements Builder {
        private Boolean isRestoreInProgress;
        private Instant restoreExpiryDate;

        private BuilderImpl() {
        }

        private BuilderImpl(RestoreStatus model) {
            this.isRestoreInProgress(model.isRestoreInProgress);
            this.restoreExpiryDate(model.restoreExpiryDate);
        }

        public final Boolean getIsRestoreInProgress() {
            return this.isRestoreInProgress;
        }

        public final void setIsRestoreInProgress(Boolean isRestoreInProgress) {
            this.isRestoreInProgress = isRestoreInProgress;
        }

        @Override
        public final Builder isRestoreInProgress(Boolean isRestoreInProgress) {
            this.isRestoreInProgress = isRestoreInProgress;
            return this;
        }

        public final Instant getRestoreExpiryDate() {
            return this.restoreExpiryDate;
        }

        public final void setRestoreExpiryDate(Instant restoreExpiryDate) {
            this.restoreExpiryDate = restoreExpiryDate;
        }

        @Override
        public final Builder restoreExpiryDate(Instant restoreExpiryDate) {
            this.restoreExpiryDate = restoreExpiryDate;
            return this;
        }

        public RestoreStatus build() {
            return new RestoreStatus(this);
        }

        public List<SdkField<?>> sdkFields() {
            return SDK_FIELDS;
        }
    }

    public static interface Builder
    extends SdkPojo,
    CopyableBuilder<Builder, RestoreStatus> {
        public Builder isRestoreInProgress(Boolean var1);

        public Builder restoreExpiryDate(Instant var1);
    }
}

