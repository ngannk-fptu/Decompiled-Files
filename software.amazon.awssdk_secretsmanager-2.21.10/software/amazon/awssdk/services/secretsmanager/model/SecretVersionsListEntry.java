/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.core.SdkField
 *  software.amazon.awssdk.core.SdkPojo
 *  software.amazon.awssdk.core.protocol.MarshallLocation
 *  software.amazon.awssdk.core.protocol.MarshallingType
 *  software.amazon.awssdk.core.traits.ListTrait
 *  software.amazon.awssdk.core.traits.LocationTrait
 *  software.amazon.awssdk.core.traits.Trait
 *  software.amazon.awssdk.core.util.DefaultSdkAutoConstructList
 *  software.amazon.awssdk.core.util.SdkAutoConstructList
 *  software.amazon.awssdk.utils.ToString
 *  software.amazon.awssdk.utils.builder.CopyableBuilder
 *  software.amazon.awssdk.utils.builder.ToCopyableBuilder
 */
package software.amazon.awssdk.services.secretsmanager.model;

import java.io.Serializable;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collection;
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
import software.amazon.awssdk.core.traits.ListTrait;
import software.amazon.awssdk.core.traits.LocationTrait;
import software.amazon.awssdk.core.traits.Trait;
import software.amazon.awssdk.core.util.DefaultSdkAutoConstructList;
import software.amazon.awssdk.core.util.SdkAutoConstructList;
import software.amazon.awssdk.services.secretsmanager.model.KmsKeyIdListTypeCopier;
import software.amazon.awssdk.services.secretsmanager.model.SecretVersionStagesTypeCopier;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

public final class SecretVersionsListEntry
implements SdkPojo,
Serializable,
ToCopyableBuilder<Builder, SecretVersionsListEntry> {
    private static final SdkField<String> VERSION_ID_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("VersionId").getter(SecretVersionsListEntry.getter(SecretVersionsListEntry::versionId)).setter(SecretVersionsListEntry.setter(Builder::versionId)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("VersionId").build()}).build();
    private static final SdkField<List<String>> VERSION_STAGES_FIELD = SdkField.builder((MarshallingType)MarshallingType.LIST).memberName("VersionStages").getter(SecretVersionsListEntry.getter(SecretVersionsListEntry::versionStages)).setter(SecretVersionsListEntry.setter(Builder::versionStages)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("VersionStages").build(), ListTrait.builder().memberLocationName(null).memberFieldInfo(SdkField.builder((MarshallingType)MarshallingType.STRING).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("member").build()}).build()).build()}).build();
    private static final SdkField<Instant> LAST_ACCESSED_DATE_FIELD = SdkField.builder((MarshallingType)MarshallingType.INSTANT).memberName("LastAccessedDate").getter(SecretVersionsListEntry.getter(SecretVersionsListEntry::lastAccessedDate)).setter(SecretVersionsListEntry.setter(Builder::lastAccessedDate)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("LastAccessedDate").build()}).build();
    private static final SdkField<Instant> CREATED_DATE_FIELD = SdkField.builder((MarshallingType)MarshallingType.INSTANT).memberName("CreatedDate").getter(SecretVersionsListEntry.getter(SecretVersionsListEntry::createdDate)).setter(SecretVersionsListEntry.setter(Builder::createdDate)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("CreatedDate").build()}).build();
    private static final SdkField<List<String>> KMS_KEY_IDS_FIELD = SdkField.builder((MarshallingType)MarshallingType.LIST).memberName("KmsKeyIds").getter(SecretVersionsListEntry.getter(SecretVersionsListEntry::kmsKeyIds)).setter(SecretVersionsListEntry.setter(Builder::kmsKeyIds)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("KmsKeyIds").build(), ListTrait.builder().memberLocationName(null).memberFieldInfo(SdkField.builder((MarshallingType)MarshallingType.STRING).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("member").build()}).build()).build()}).build();
    private static final List<SdkField<?>> SDK_FIELDS = Collections.unmodifiableList(Arrays.asList(VERSION_ID_FIELD, VERSION_STAGES_FIELD, LAST_ACCESSED_DATE_FIELD, CREATED_DATE_FIELD, KMS_KEY_IDS_FIELD));
    private static final long serialVersionUID = 1L;
    private final String versionId;
    private final List<String> versionStages;
    private final Instant lastAccessedDate;
    private final Instant createdDate;
    private final List<String> kmsKeyIds;

    private SecretVersionsListEntry(BuilderImpl builder) {
        this.versionId = builder.versionId;
        this.versionStages = builder.versionStages;
        this.lastAccessedDate = builder.lastAccessedDate;
        this.createdDate = builder.createdDate;
        this.kmsKeyIds = builder.kmsKeyIds;
    }

    public final String versionId() {
        return this.versionId;
    }

    public final boolean hasVersionStages() {
        return this.versionStages != null && !(this.versionStages instanceof SdkAutoConstructList);
    }

    public final List<String> versionStages() {
        return this.versionStages;
    }

    public final Instant lastAccessedDate() {
        return this.lastAccessedDate;
    }

    public final Instant createdDate() {
        return this.createdDate;
    }

    public final boolean hasKmsKeyIds() {
        return this.kmsKeyIds != null && !(this.kmsKeyIds instanceof SdkAutoConstructList);
    }

    public final List<String> kmsKeyIds() {
        return this.kmsKeyIds;
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
        hashCode = 31 * hashCode + Objects.hashCode(this.versionId());
        hashCode = 31 * hashCode + Objects.hashCode(this.hasVersionStages() ? this.versionStages() : null);
        hashCode = 31 * hashCode + Objects.hashCode(this.lastAccessedDate());
        hashCode = 31 * hashCode + Objects.hashCode(this.createdDate());
        hashCode = 31 * hashCode + Objects.hashCode(this.hasKmsKeyIds() ? this.kmsKeyIds() : null);
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
        if (!(obj instanceof SecretVersionsListEntry)) {
            return false;
        }
        SecretVersionsListEntry other = (SecretVersionsListEntry)obj;
        return Objects.equals(this.versionId(), other.versionId()) && this.hasVersionStages() == other.hasVersionStages() && Objects.equals(this.versionStages(), other.versionStages()) && Objects.equals(this.lastAccessedDate(), other.lastAccessedDate()) && Objects.equals(this.createdDate(), other.createdDate()) && this.hasKmsKeyIds() == other.hasKmsKeyIds() && Objects.equals(this.kmsKeyIds(), other.kmsKeyIds());
    }

    public final String toString() {
        return ToString.builder((String)"SecretVersionsListEntry").add("VersionId", (Object)this.versionId()).add("VersionStages", this.hasVersionStages() ? this.versionStages() : null).add("LastAccessedDate", (Object)this.lastAccessedDate()).add("CreatedDate", (Object)this.createdDate()).add("KmsKeyIds", this.hasKmsKeyIds() ? this.kmsKeyIds() : null).build();
    }

    public final <T> Optional<T> getValueForField(String fieldName, Class<T> clazz) {
        switch (fieldName) {
            case "VersionId": {
                return Optional.ofNullable(clazz.cast(this.versionId()));
            }
            case "VersionStages": {
                return Optional.ofNullable(clazz.cast(this.versionStages()));
            }
            case "LastAccessedDate": {
                return Optional.ofNullable(clazz.cast(this.lastAccessedDate()));
            }
            case "CreatedDate": {
                return Optional.ofNullable(clazz.cast(this.createdDate()));
            }
            case "KmsKeyIds": {
                return Optional.ofNullable(clazz.cast(this.kmsKeyIds()));
            }
        }
        return Optional.empty();
    }

    public final List<SdkField<?>> sdkFields() {
        return SDK_FIELDS;
    }

    private static <T> Function<Object, T> getter(Function<SecretVersionsListEntry, T> g) {
        return obj -> g.apply((SecretVersionsListEntry)obj);
    }

    private static <T> BiConsumer<Object, T> setter(BiConsumer<Builder, T> s) {
        return (obj, val) -> s.accept((Builder)obj, val);
    }

    static final class BuilderImpl
    implements Builder {
        private String versionId;
        private List<String> versionStages = DefaultSdkAutoConstructList.getInstance();
        private Instant lastAccessedDate;
        private Instant createdDate;
        private List<String> kmsKeyIds = DefaultSdkAutoConstructList.getInstance();

        private BuilderImpl() {
        }

        private BuilderImpl(SecretVersionsListEntry model) {
            this.versionId(model.versionId);
            this.versionStages(model.versionStages);
            this.lastAccessedDate(model.lastAccessedDate);
            this.createdDate(model.createdDate);
            this.kmsKeyIds(model.kmsKeyIds);
        }

        public final String getVersionId() {
            return this.versionId;
        }

        public final void setVersionId(String versionId) {
            this.versionId = versionId;
        }

        @Override
        public final Builder versionId(String versionId) {
            this.versionId = versionId;
            return this;
        }

        public final Collection<String> getVersionStages() {
            if (this.versionStages instanceof SdkAutoConstructList) {
                return null;
            }
            return this.versionStages;
        }

        public final void setVersionStages(Collection<String> versionStages) {
            this.versionStages = SecretVersionStagesTypeCopier.copy(versionStages);
        }

        @Override
        public final Builder versionStages(Collection<String> versionStages) {
            this.versionStages = SecretVersionStagesTypeCopier.copy(versionStages);
            return this;
        }

        @Override
        @SafeVarargs
        public final Builder versionStages(String ... versionStages) {
            this.versionStages(Arrays.asList(versionStages));
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

        public final Instant getCreatedDate() {
            return this.createdDate;
        }

        public final void setCreatedDate(Instant createdDate) {
            this.createdDate = createdDate;
        }

        @Override
        public final Builder createdDate(Instant createdDate) {
            this.createdDate = createdDate;
            return this;
        }

        public final Collection<String> getKmsKeyIds() {
            if (this.kmsKeyIds instanceof SdkAutoConstructList) {
                return null;
            }
            return this.kmsKeyIds;
        }

        public final void setKmsKeyIds(Collection<String> kmsKeyIds) {
            this.kmsKeyIds = KmsKeyIdListTypeCopier.copy(kmsKeyIds);
        }

        @Override
        public final Builder kmsKeyIds(Collection<String> kmsKeyIds) {
            this.kmsKeyIds = KmsKeyIdListTypeCopier.copy(kmsKeyIds);
            return this;
        }

        @Override
        @SafeVarargs
        public final Builder kmsKeyIds(String ... kmsKeyIds) {
            this.kmsKeyIds(Arrays.asList(kmsKeyIds));
            return this;
        }

        public SecretVersionsListEntry build() {
            return new SecretVersionsListEntry(this);
        }

        public List<SdkField<?>> sdkFields() {
            return SDK_FIELDS;
        }
    }

    public static interface Builder
    extends SdkPojo,
    CopyableBuilder<Builder, SecretVersionsListEntry> {
        public Builder versionId(String var1);

        public Builder versionStages(Collection<String> var1);

        public Builder versionStages(String ... var1);

        public Builder lastAccessedDate(Instant var1);

        public Builder createdDate(Instant var1);

        public Builder kmsKeyIds(Collection<String> var1);

        public Builder kmsKeyIds(String ... var1);
    }
}

