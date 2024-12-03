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
import software.amazon.awssdk.services.s3.model.BucketVersioningStatus;
import software.amazon.awssdk.services.s3.model.MFADeleteStatus;
import software.amazon.awssdk.services.s3.model.S3Response;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

public final class GetBucketVersioningResponse
extends S3Response
implements ToCopyableBuilder<Builder, GetBucketVersioningResponse> {
    private static final SdkField<String> STATUS_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("Status").getter(GetBucketVersioningResponse.getter(GetBucketVersioningResponse::statusAsString)).setter(GetBucketVersioningResponse.setter(Builder::status)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("Status").unmarshallLocationName("Status").build()}).build();
    private static final SdkField<String> MFA_DELETE_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("MFADelete").getter(GetBucketVersioningResponse.getter(GetBucketVersioningResponse::mfaDeleteAsString)).setter(GetBucketVersioningResponse.setter(Builder::mfaDelete)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("MfaDelete").unmarshallLocationName("MfaDelete").build()}).build();
    private static final List<SdkField<?>> SDK_FIELDS = Collections.unmodifiableList(Arrays.asList(STATUS_FIELD, MFA_DELETE_FIELD));
    private final String status;
    private final String mfaDelete;

    private GetBucketVersioningResponse(BuilderImpl builder) {
        super(builder);
        this.status = builder.status;
        this.mfaDelete = builder.mfaDelete;
    }

    public final BucketVersioningStatus status() {
        return BucketVersioningStatus.fromValue(this.status);
    }

    public final String statusAsString() {
        return this.status;
    }

    public final MFADeleteStatus mfaDelete() {
        return MFADeleteStatus.fromValue(this.mfaDelete);
    }

    public final String mfaDeleteAsString() {
        return this.mfaDelete;
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
        hashCode = 31 * hashCode + Objects.hashCode(this.statusAsString());
        hashCode = 31 * hashCode + Objects.hashCode(this.mfaDeleteAsString());
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
        if (!(obj instanceof GetBucketVersioningResponse)) {
            return false;
        }
        GetBucketVersioningResponse other = (GetBucketVersioningResponse)((Object)obj);
        return Objects.equals(this.statusAsString(), other.statusAsString()) && Objects.equals(this.mfaDeleteAsString(), other.mfaDeleteAsString());
    }

    public final String toString() {
        return ToString.builder((String)"GetBucketVersioningResponse").add("Status", (Object)this.statusAsString()).add("MFADelete", (Object)this.mfaDeleteAsString()).build();
    }

    public final <T> Optional<T> getValueForField(String fieldName, Class<T> clazz) {
        switch (fieldName) {
            case "Status": {
                return Optional.ofNullable(clazz.cast(this.statusAsString()));
            }
            case "MFADelete": {
                return Optional.ofNullable(clazz.cast(this.mfaDeleteAsString()));
            }
        }
        return Optional.empty();
    }

    public final List<SdkField<?>> sdkFields() {
        return SDK_FIELDS;
    }

    private static <T> Function<Object, T> getter(Function<GetBucketVersioningResponse, T> g) {
        return obj -> g.apply((GetBucketVersioningResponse)((Object)((Object)obj)));
    }

    private static <T> BiConsumer<Object, T> setter(BiConsumer<Builder, T> s) {
        return (obj, val) -> s.accept((Builder)obj, val);
    }

    static final class BuilderImpl
    extends S3Response.BuilderImpl
    implements Builder {
        private String status;
        private String mfaDelete;

        private BuilderImpl() {
        }

        private BuilderImpl(GetBucketVersioningResponse model) {
            super(model);
            this.status(model.status);
            this.mfaDelete(model.mfaDelete);
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
        public final Builder status(BucketVersioningStatus status) {
            this.status(status == null ? null : status.toString());
            return this;
        }

        public final String getMfaDelete() {
            return this.mfaDelete;
        }

        public final void setMfaDelete(String mfaDelete) {
            this.mfaDelete = mfaDelete;
        }

        @Override
        public final Builder mfaDelete(String mfaDelete) {
            this.mfaDelete = mfaDelete;
            return this;
        }

        @Override
        public final Builder mfaDelete(MFADeleteStatus mfaDelete) {
            this.mfaDelete(mfaDelete == null ? null : mfaDelete.toString());
            return this;
        }

        @Override
        public GetBucketVersioningResponse build() {
            return new GetBucketVersioningResponse(this);
        }

        public List<SdkField<?>> sdkFields() {
            return SDK_FIELDS;
        }
    }

    public static interface Builder
    extends S3Response.Builder,
    SdkPojo,
    CopyableBuilder<Builder, GetBucketVersioningResponse> {
        public Builder status(String var1);

        public Builder status(BucketVersioningStatus var1);

        public Builder mfaDelete(String var1);

        public Builder mfaDelete(MFADeleteStatus var1);
    }
}

