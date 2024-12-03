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
import java.util.function.Consumer;
import java.util.function.Function;
import software.amazon.awssdk.core.SdkField;
import software.amazon.awssdk.core.SdkPojo;
import software.amazon.awssdk.core.protocol.MarshallLocation;
import software.amazon.awssdk.core.protocol.MarshallingType;
import software.amazon.awssdk.core.traits.LocationTrait;
import software.amazon.awssdk.core.traits.Trait;
import software.amazon.awssdk.services.s3.model.Owner;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

public final class DeleteMarkerEntry
implements SdkPojo,
Serializable,
ToCopyableBuilder<Builder, DeleteMarkerEntry> {
    private static final SdkField<Owner> OWNER_FIELD = SdkField.builder((MarshallingType)MarshallingType.SDK_POJO).memberName("Owner").getter(DeleteMarkerEntry.getter(DeleteMarkerEntry::owner)).setter(DeleteMarkerEntry.setter(Builder::owner)).constructor(Owner::builder).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("Owner").unmarshallLocationName("Owner").build()}).build();
    private static final SdkField<String> KEY_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("Key").getter(DeleteMarkerEntry.getter(DeleteMarkerEntry::key)).setter(DeleteMarkerEntry.setter(Builder::key)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("Key").unmarshallLocationName("Key").build()}).build();
    private static final SdkField<String> VERSION_ID_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("VersionId").getter(DeleteMarkerEntry.getter(DeleteMarkerEntry::versionId)).setter(DeleteMarkerEntry.setter(Builder::versionId)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("VersionId").unmarshallLocationName("VersionId").build()}).build();
    private static final SdkField<Boolean> IS_LATEST_FIELD = SdkField.builder((MarshallingType)MarshallingType.BOOLEAN).memberName("IsLatest").getter(DeleteMarkerEntry.getter(DeleteMarkerEntry::isLatest)).setter(DeleteMarkerEntry.setter(Builder::isLatest)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("IsLatest").unmarshallLocationName("IsLatest").build()}).build();
    private static final SdkField<Instant> LAST_MODIFIED_FIELD = SdkField.builder((MarshallingType)MarshallingType.INSTANT).memberName("LastModified").getter(DeleteMarkerEntry.getter(DeleteMarkerEntry::lastModified)).setter(DeleteMarkerEntry.setter(Builder::lastModified)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("LastModified").unmarshallLocationName("LastModified").build()}).build();
    private static final List<SdkField<?>> SDK_FIELDS = Collections.unmodifiableList(Arrays.asList(OWNER_FIELD, KEY_FIELD, VERSION_ID_FIELD, IS_LATEST_FIELD, LAST_MODIFIED_FIELD));
    private static final long serialVersionUID = 1L;
    private final Owner owner;
    private final String key;
    private final String versionId;
    private final Boolean isLatest;
    private final Instant lastModified;

    private DeleteMarkerEntry(BuilderImpl builder) {
        this.owner = builder.owner;
        this.key = builder.key;
        this.versionId = builder.versionId;
        this.isLatest = builder.isLatest;
        this.lastModified = builder.lastModified;
    }

    public final Owner owner() {
        return this.owner;
    }

    public final String key() {
        return this.key;
    }

    public final String versionId() {
        return this.versionId;
    }

    public final Boolean isLatest() {
        return this.isLatest;
    }

    public final Instant lastModified() {
        return this.lastModified;
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
        hashCode = 31 * hashCode + Objects.hashCode(this.owner());
        hashCode = 31 * hashCode + Objects.hashCode(this.key());
        hashCode = 31 * hashCode + Objects.hashCode(this.versionId());
        hashCode = 31 * hashCode + Objects.hashCode(this.isLatest());
        hashCode = 31 * hashCode + Objects.hashCode(this.lastModified());
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
        if (!(obj instanceof DeleteMarkerEntry)) {
            return false;
        }
        DeleteMarkerEntry other = (DeleteMarkerEntry)obj;
        return Objects.equals(this.owner(), other.owner()) && Objects.equals(this.key(), other.key()) && Objects.equals(this.versionId(), other.versionId()) && Objects.equals(this.isLatest(), other.isLatest()) && Objects.equals(this.lastModified(), other.lastModified());
    }

    public final String toString() {
        return ToString.builder((String)"DeleteMarkerEntry").add("Owner", (Object)this.owner()).add("Key", (Object)this.key()).add("VersionId", (Object)this.versionId()).add("IsLatest", (Object)this.isLatest()).add("LastModified", (Object)this.lastModified()).build();
    }

    public final <T> Optional<T> getValueForField(String fieldName, Class<T> clazz) {
        switch (fieldName) {
            case "Owner": {
                return Optional.ofNullable(clazz.cast(this.owner()));
            }
            case "Key": {
                return Optional.ofNullable(clazz.cast(this.key()));
            }
            case "VersionId": {
                return Optional.ofNullable(clazz.cast(this.versionId()));
            }
            case "IsLatest": {
                return Optional.ofNullable(clazz.cast(this.isLatest()));
            }
            case "LastModified": {
                return Optional.ofNullable(clazz.cast(this.lastModified()));
            }
        }
        return Optional.empty();
    }

    public final List<SdkField<?>> sdkFields() {
        return SDK_FIELDS;
    }

    private static <T> Function<Object, T> getter(Function<DeleteMarkerEntry, T> g) {
        return obj -> g.apply((DeleteMarkerEntry)obj);
    }

    private static <T> BiConsumer<Object, T> setter(BiConsumer<Builder, T> s) {
        return (obj, val) -> s.accept((Builder)obj, val);
    }

    static final class BuilderImpl
    implements Builder {
        private Owner owner;
        private String key;
        private String versionId;
        private Boolean isLatest;
        private Instant lastModified;

        private BuilderImpl() {
        }

        private BuilderImpl(DeleteMarkerEntry model) {
            this.owner(model.owner);
            this.key(model.key);
            this.versionId(model.versionId);
            this.isLatest(model.isLatest);
            this.lastModified(model.lastModified);
        }

        public final Owner.Builder getOwner() {
            return this.owner != null ? this.owner.toBuilder() : null;
        }

        public final void setOwner(Owner.BuilderImpl owner) {
            this.owner = owner != null ? owner.build() : null;
        }

        @Override
        public final Builder owner(Owner owner) {
            this.owner = owner;
            return this;
        }

        public final String getKey() {
            return this.key;
        }

        public final void setKey(String key) {
            this.key = key;
        }

        @Override
        public final Builder key(String key) {
            this.key = key;
            return this;
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

        public final Boolean getIsLatest() {
            return this.isLatest;
        }

        public final void setIsLatest(Boolean isLatest) {
            this.isLatest = isLatest;
        }

        @Override
        public final Builder isLatest(Boolean isLatest) {
            this.isLatest = isLatest;
            return this;
        }

        public final Instant getLastModified() {
            return this.lastModified;
        }

        public final void setLastModified(Instant lastModified) {
            this.lastModified = lastModified;
        }

        @Override
        public final Builder lastModified(Instant lastModified) {
            this.lastModified = lastModified;
            return this;
        }

        public DeleteMarkerEntry build() {
            return new DeleteMarkerEntry(this);
        }

        public List<SdkField<?>> sdkFields() {
            return SDK_FIELDS;
        }
    }

    public static interface Builder
    extends SdkPojo,
    CopyableBuilder<Builder, DeleteMarkerEntry> {
        public Builder owner(Owner var1);

        default public Builder owner(Consumer<Owner.Builder> owner) {
            return this.owner((Owner)((Owner.Builder)Owner.builder().applyMutation(owner)).build());
        }

        public Builder key(String var1);

        public Builder versionId(String var1);

        public Builder isLatest(Boolean var1);

        public Builder lastModified(Instant var1);
    }
}

