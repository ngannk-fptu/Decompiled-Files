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

public final class DeletedObject
implements SdkPojo,
Serializable,
ToCopyableBuilder<Builder, DeletedObject> {
    private static final SdkField<String> KEY_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("Key").getter(DeletedObject.getter(DeletedObject::key)).setter(DeletedObject.setter(Builder::key)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("Key").unmarshallLocationName("Key").build()}).build();
    private static final SdkField<String> VERSION_ID_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("VersionId").getter(DeletedObject.getter(DeletedObject::versionId)).setter(DeletedObject.setter(Builder::versionId)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("VersionId").unmarshallLocationName("VersionId").build()}).build();
    private static final SdkField<Boolean> DELETE_MARKER_FIELD = SdkField.builder((MarshallingType)MarshallingType.BOOLEAN).memberName("DeleteMarker").getter(DeletedObject.getter(DeletedObject::deleteMarker)).setter(DeletedObject.setter(Builder::deleteMarker)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("DeleteMarker").unmarshallLocationName("DeleteMarker").build()}).build();
    private static final SdkField<String> DELETE_MARKER_VERSION_ID_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("DeleteMarkerVersionId").getter(DeletedObject.getter(DeletedObject::deleteMarkerVersionId)).setter(DeletedObject.setter(Builder::deleteMarkerVersionId)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("DeleteMarkerVersionId").unmarshallLocationName("DeleteMarkerVersionId").build()}).build();
    private static final List<SdkField<?>> SDK_FIELDS = Collections.unmodifiableList(Arrays.asList(KEY_FIELD, VERSION_ID_FIELD, DELETE_MARKER_FIELD, DELETE_MARKER_VERSION_ID_FIELD));
    private static final long serialVersionUID = 1L;
    private final String key;
    private final String versionId;
    private final Boolean deleteMarker;
    private final String deleteMarkerVersionId;

    private DeletedObject(BuilderImpl builder) {
        this.key = builder.key;
        this.versionId = builder.versionId;
        this.deleteMarker = builder.deleteMarker;
        this.deleteMarkerVersionId = builder.deleteMarkerVersionId;
    }

    public final String key() {
        return this.key;
    }

    public final String versionId() {
        return this.versionId;
    }

    public final Boolean deleteMarker() {
        return this.deleteMarker;
    }

    public final String deleteMarkerVersionId() {
        return this.deleteMarkerVersionId;
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
        hashCode = 31 * hashCode + Objects.hashCode(this.key());
        hashCode = 31 * hashCode + Objects.hashCode(this.versionId());
        hashCode = 31 * hashCode + Objects.hashCode(this.deleteMarker());
        hashCode = 31 * hashCode + Objects.hashCode(this.deleteMarkerVersionId());
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
        if (!(obj instanceof DeletedObject)) {
            return false;
        }
        DeletedObject other = (DeletedObject)obj;
        return Objects.equals(this.key(), other.key()) && Objects.equals(this.versionId(), other.versionId()) && Objects.equals(this.deleteMarker(), other.deleteMarker()) && Objects.equals(this.deleteMarkerVersionId(), other.deleteMarkerVersionId());
    }

    public final String toString() {
        return ToString.builder((String)"DeletedObject").add("Key", (Object)this.key()).add("VersionId", (Object)this.versionId()).add("DeleteMarker", (Object)this.deleteMarker()).add("DeleteMarkerVersionId", (Object)this.deleteMarkerVersionId()).build();
    }

    public final <T> Optional<T> getValueForField(String fieldName, Class<T> clazz) {
        switch (fieldName) {
            case "Key": {
                return Optional.ofNullable(clazz.cast(this.key()));
            }
            case "VersionId": {
                return Optional.ofNullable(clazz.cast(this.versionId()));
            }
            case "DeleteMarker": {
                return Optional.ofNullable(clazz.cast(this.deleteMarker()));
            }
            case "DeleteMarkerVersionId": {
                return Optional.ofNullable(clazz.cast(this.deleteMarkerVersionId()));
            }
        }
        return Optional.empty();
    }

    public final List<SdkField<?>> sdkFields() {
        return SDK_FIELDS;
    }

    private static <T> Function<Object, T> getter(Function<DeletedObject, T> g) {
        return obj -> g.apply((DeletedObject)obj);
    }

    private static <T> BiConsumer<Object, T> setter(BiConsumer<Builder, T> s) {
        return (obj, val) -> s.accept((Builder)obj, val);
    }

    static final class BuilderImpl
    implements Builder {
        private String key;
        private String versionId;
        private Boolean deleteMarker;
        private String deleteMarkerVersionId;

        private BuilderImpl() {
        }

        private BuilderImpl(DeletedObject model) {
            this.key(model.key);
            this.versionId(model.versionId);
            this.deleteMarker(model.deleteMarker);
            this.deleteMarkerVersionId(model.deleteMarkerVersionId);
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

        public final Boolean getDeleteMarker() {
            return this.deleteMarker;
        }

        public final void setDeleteMarker(Boolean deleteMarker) {
            this.deleteMarker = deleteMarker;
        }

        @Override
        public final Builder deleteMarker(Boolean deleteMarker) {
            this.deleteMarker = deleteMarker;
            return this;
        }

        public final String getDeleteMarkerVersionId() {
            return this.deleteMarkerVersionId;
        }

        public final void setDeleteMarkerVersionId(String deleteMarkerVersionId) {
            this.deleteMarkerVersionId = deleteMarkerVersionId;
        }

        @Override
        public final Builder deleteMarkerVersionId(String deleteMarkerVersionId) {
            this.deleteMarkerVersionId = deleteMarkerVersionId;
            return this;
        }

        public DeletedObject build() {
            return new DeletedObject(this);
        }

        public List<SdkField<?>> sdkFields() {
            return SDK_FIELDS;
        }
    }

    public static interface Builder
    extends SdkPojo,
    CopyableBuilder<Builder, DeletedObject> {
        public Builder key(String var1);

        public Builder versionId(String var1);

        public Builder deleteMarker(Boolean var1);

        public Builder deleteMarkerVersionId(String var1);
    }
}

