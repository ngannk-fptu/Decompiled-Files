/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.services.secretsmanager.model;

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
import software.amazon.awssdk.services.secretsmanager.model.StatusType;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

public final class ReplicationStatusType
implements SdkPojo,
Serializable,
ToCopyableBuilder<Builder, ReplicationStatusType> {
    private static final SdkField<String> REGION_FIELD = SdkField.builder(MarshallingType.STRING).memberName("Region").getter(ReplicationStatusType.getter(ReplicationStatusType::region)).setter(ReplicationStatusType.setter(Builder::region)).traits(LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("Region").build()).build();
    private static final SdkField<String> KMS_KEY_ID_FIELD = SdkField.builder(MarshallingType.STRING).memberName("KmsKeyId").getter(ReplicationStatusType.getter(ReplicationStatusType::kmsKeyId)).setter(ReplicationStatusType.setter(Builder::kmsKeyId)).traits(LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("KmsKeyId").build()).build();
    private static final SdkField<String> STATUS_FIELD = SdkField.builder(MarshallingType.STRING).memberName("Status").getter(ReplicationStatusType.getter(ReplicationStatusType::statusAsString)).setter(ReplicationStatusType.setter(Builder::status)).traits(LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("Status").build()).build();
    private static final SdkField<String> STATUS_MESSAGE_FIELD = SdkField.builder(MarshallingType.STRING).memberName("StatusMessage").getter(ReplicationStatusType.getter(ReplicationStatusType::statusMessage)).setter(ReplicationStatusType.setter(Builder::statusMessage)).traits(LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("StatusMessage").build()).build();
    private static final SdkField<Instant> LAST_ACCESSED_DATE_FIELD = SdkField.builder(MarshallingType.INSTANT).memberName("LastAccessedDate").getter(ReplicationStatusType.getter(ReplicationStatusType::lastAccessedDate)).setter(ReplicationStatusType.setter(Builder::lastAccessedDate)).traits(LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("LastAccessedDate").build()).build();
    private static final List<SdkField<?>> SDK_FIELDS = Collections.unmodifiableList(Arrays.asList(REGION_FIELD, KMS_KEY_ID_FIELD, STATUS_FIELD, STATUS_MESSAGE_FIELD, LAST_ACCESSED_DATE_FIELD));
    private static final long serialVersionUID = 1L;
    private final String region;
    private final String kmsKeyId;
    private final String status;
    private final String statusMessage;
    private final Instant lastAccessedDate;

    private ReplicationStatusType(BuilderImpl builder) {
        this.region = builder.region;
        this.kmsKeyId = builder.kmsKeyId;
        this.status = builder.status;
        this.statusMessage = builder.statusMessage;
        this.lastAccessedDate = builder.lastAccessedDate;
    }

    public final String region() {
        return this.region;
    }

    public final String kmsKeyId() {
        return this.kmsKeyId;
    }

    public final StatusType status() {
        return StatusType.fromValue(this.status);
    }

    public final String statusAsString() {
        return this.status;
    }

    public final String statusMessage() {
        return this.statusMessage;
    }

    public final Instant lastAccessedDate() {
        return this.lastAccessedDate;
    }

    @Override
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
        hashCode = 31 * hashCode + Objects.hashCode(this.region());
        hashCode = 31 * hashCode + Objects.hashCode(this.kmsKeyId());
        hashCode = 31 * hashCode + Objects.hashCode(this.statusAsString());
        hashCode = 31 * hashCode + Objects.hashCode(this.statusMessage());
        hashCode = 31 * hashCode + Objects.hashCode(this.lastAccessedDate());
        return hashCode;
    }

    public final boolean equals(Object obj) {
        return this.equalsBySdkFields(obj);
    }

    @Override
    public final boolean equalsBySdkFields(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof ReplicationStatusType)) {
            return false;
        }
        ReplicationStatusType other = (ReplicationStatusType)obj;
        return Objects.equals(this.region(), other.region()) && Objects.equals(this.kmsKeyId(), other.kmsKeyId()) && Objects.equals(this.statusAsString(), other.statusAsString()) && Objects.equals(this.statusMessage(), other.statusMessage()) && Objects.equals(this.lastAccessedDate(), other.lastAccessedDate());
    }

    public final String toString() {
        return ToString.builder("ReplicationStatusType").add("Region", this.region()).add("KmsKeyId", this.kmsKeyId()).add("Status", this.statusAsString()).add("StatusMessage", this.statusMessage()).add("LastAccessedDate", this.lastAccessedDate()).build();
    }

    public final <T> Optional<T> getValueForField(String fieldName, Class<T> clazz) {
        switch (fieldName) {
            case "Region": {
                return Optional.ofNullable(clazz.cast(this.region()));
            }
            case "KmsKeyId": {
                return Optional.ofNullable(clazz.cast(this.kmsKeyId()));
            }
            case "Status": {
                return Optional.ofNullable(clazz.cast(this.statusAsString()));
            }
            case "StatusMessage": {
                return Optional.ofNullable(clazz.cast(this.statusMessage()));
            }
            case "LastAccessedDate": {
                return Optional.ofNullable(clazz.cast(this.lastAccessedDate()));
            }
        }
        return Optional.empty();
    }

    @Override
    public final List<SdkField<?>> sdkFields() {
        return SDK_FIELDS;
    }

    private static <T> Function<Object, T> getter(Function<ReplicationStatusType, T> g) {
        return obj -> g.apply((ReplicationStatusType)obj);
    }

    private static <T> BiConsumer<Object, T> setter(BiConsumer<Builder, T> s) {
        return (obj, val) -> s.accept((Builder)obj, val);
    }

    static final class BuilderImpl
    implements Builder {
        private String region;
        private String kmsKeyId;
        private String status;
        private String statusMessage;
        private Instant lastAccessedDate;

        private BuilderImpl() {
        }

        private BuilderImpl(ReplicationStatusType model) {
            this.region(model.region);
            this.kmsKeyId(model.kmsKeyId);
            this.status(model.status);
            this.statusMessage(model.statusMessage);
            this.lastAccessedDate(model.lastAccessedDate);
        }

        public final String getRegion() {
            return this.region;
        }

        public final void setRegion(String region) {
            this.region = region;
        }

        @Override
        public final Builder region(String region) {
            this.region = region;
            return this;
        }

        public final String getKmsKeyId() {
            return this.kmsKeyId;
        }

        public final void setKmsKeyId(String kmsKeyId) {
            this.kmsKeyId = kmsKeyId;
        }

        @Override
        public final Builder kmsKeyId(String kmsKeyId) {
            this.kmsKeyId = kmsKeyId;
            return this;
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
        public final Builder status(StatusType status) {
            this.status(status == null ? null : status.toString());
            return this;
        }

        public final String getStatusMessage() {
            return this.statusMessage;
        }

        public final void setStatusMessage(String statusMessage) {
            this.statusMessage = statusMessage;
        }

        @Override
        public final Builder statusMessage(String statusMessage) {
            this.statusMessage = statusMessage;
            return this;
        }

        public final Instant getLastAccessedDate() {
            return this.lastAccessedDate;
        }

        public final void setLastAccessedDate(Instant lastAccessedDate) {
            this.lastAccessedDate = lastAccessedDate;
        }

        @Override
        public final Builder lastAccessedDate(Instant lastAccessedDate) {
            this.lastAccessedDate = lastAccessedDate;
            return this;
        }

        @Override
        public ReplicationStatusType build() {
            return new ReplicationStatusType(this);
        }

        @Override
        public List<SdkField<?>> sdkFields() {
            return SDK_FIELDS;
        }
    }

    public static interface Builder
    extends SdkPojo,
    CopyableBuilder<Builder, ReplicationStatusType> {
        public Builder region(String var1);

        public Builder kmsKeyId(String var1);

        public Builder status(String var1);

        public Builder status(StatusType var1);

        public Builder statusMessage(String var1);

        public Builder lastAccessedDate(Instant var1);
    }
}

