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
import software.amazon.awssdk.services.s3.model.RequestCharged;
import software.amazon.awssdk.services.s3.model.S3Response;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

public final class DeleteObjectResponse
extends S3Response
implements ToCopyableBuilder<Builder, DeleteObjectResponse> {
    private static final SdkField<Boolean> DELETE_MARKER_FIELD = SdkField.builder((MarshallingType)MarshallingType.BOOLEAN).memberName("DeleteMarker").getter(DeleteObjectResponse.getter(DeleteObjectResponse::deleteMarker)).setter(DeleteObjectResponse.setter(Builder::deleteMarker)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("x-amz-delete-marker").unmarshallLocationName("x-amz-delete-marker").build()}).build();
    private static final SdkField<String> VERSION_ID_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("VersionId").getter(DeleteObjectResponse.getter(DeleteObjectResponse::versionId)).setter(DeleteObjectResponse.setter(Builder::versionId)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("x-amz-version-id").unmarshallLocationName("x-amz-version-id").build()}).build();
    private static final SdkField<String> REQUEST_CHARGED_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("RequestCharged").getter(DeleteObjectResponse.getter(DeleteObjectResponse::requestChargedAsString)).setter(DeleteObjectResponse.setter(Builder::requestCharged)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("x-amz-request-charged").unmarshallLocationName("x-amz-request-charged").build()}).build();
    private static final List<SdkField<?>> SDK_FIELDS = Collections.unmodifiableList(Arrays.asList(DELETE_MARKER_FIELD, VERSION_ID_FIELD, REQUEST_CHARGED_FIELD));
    private final Boolean deleteMarker;
    private final String versionId;
    private final String requestCharged;

    private DeleteObjectResponse(BuilderImpl builder) {
        super(builder);
        this.deleteMarker = builder.deleteMarker;
        this.versionId = builder.versionId;
        this.requestCharged = builder.requestCharged;
    }

    public final Boolean deleteMarker() {
        return this.deleteMarker;
    }

    public final String versionId() {
        return this.versionId;
    }

    public final RequestCharged requestCharged() {
        return RequestCharged.fromValue(this.requestCharged);
    }

    public final String requestChargedAsString() {
        return this.requestCharged;
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
        hashCode = 31 * hashCode + super.hashCode();
        hashCode = 31 * hashCode + Objects.hashCode(this.deleteMarker());
        hashCode = 31 * hashCode + Objects.hashCode(this.versionId());
        hashCode = 31 * hashCode + Objects.hashCode(this.requestChargedAsString());
        return hashCode;
    }

    public final boolean equals(Object obj) {
        return super.equals(obj) && this.equalsBySdkFields(obj);
    }

    public final boolean equalsBySdkFields(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof DeleteObjectResponse)) {
            return false;
        }
        DeleteObjectResponse other = (DeleteObjectResponse)((Object)obj);
        return Objects.equals(this.deleteMarker(), other.deleteMarker()) && Objects.equals(this.versionId(), other.versionId()) && Objects.equals(this.requestChargedAsString(), other.requestChargedAsString());
    }

    public final String toString() {
        return ToString.builder((String)"DeleteObjectResponse").add("DeleteMarker", (Object)this.deleteMarker()).add("VersionId", (Object)this.versionId()).add("RequestCharged", (Object)this.requestChargedAsString()).build();
    }

    public final <T> Optional<T> getValueForField(String fieldName, Class<T> clazz) {
        switch (fieldName) {
            case "DeleteMarker": {
                return Optional.ofNullable(clazz.cast(this.deleteMarker()));
            }
            case "VersionId": {
                return Optional.ofNullable(clazz.cast(this.versionId()));
            }
            case "RequestCharged": {
                return Optional.ofNullable(clazz.cast(this.requestChargedAsString()));
            }
        }
        return Optional.empty();
    }

    public final List<SdkField<?>> sdkFields() {
        return SDK_FIELDS;
    }

    private static <T> Function<Object, T> getter(Function<DeleteObjectResponse, T> g) {
        return obj -> g.apply((DeleteObjectResponse)((Object)((Object)obj)));
    }

    private static <T> BiConsumer<Object, T> setter(BiConsumer<Builder, T> s) {
        return (obj, val) -> s.accept((Builder)obj, val);
    }

    static final class BuilderImpl
    extends S3Response.BuilderImpl
    implements Builder {
        private Boolean deleteMarker;
        private String versionId;
        private String requestCharged;

        private BuilderImpl() {
        }

        private BuilderImpl(DeleteObjectResponse model) {
            super(model);
            this.deleteMarker(model.deleteMarker);
            this.versionId(model.versionId);
            this.requestCharged(model.requestCharged);
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

        public final String getRequestCharged() {
            return this.requestCharged;
        }

        public final void setRequestCharged(String requestCharged) {
            this.requestCharged = requestCharged;
        }

        @Override
        public final Builder requestCharged(String requestCharged) {
            this.requestCharged = requestCharged;
            return this;
        }

        @Override
        public final Builder requestCharged(RequestCharged requestCharged) {
            this.requestCharged(requestCharged == null ? null : requestCharged.toString());
            return this;
        }

        @Override
        public DeleteObjectResponse build() {
            return new DeleteObjectResponse(this);
        }

        public List<SdkField<?>> sdkFields() {
            return SDK_FIELDS;
        }
    }

    public static interface Builder
    extends S3Response.Builder,
    SdkPojo,
    CopyableBuilder<Builder, DeleteObjectResponse> {
        public Builder deleteMarker(Boolean var1);

        public Builder versionId(String var1);

        public Builder requestCharged(String var1);

        public Builder requestCharged(RequestCharged var1);
    }
}

