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
package software.amazon.awssdk.services.s3.model;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import software.amazon.awssdk.core.SdkField;
import software.amazon.awssdk.core.SdkPojo;
import software.amazon.awssdk.core.protocol.MarshallLocation;
import software.amazon.awssdk.core.protocol.MarshallingType;
import software.amazon.awssdk.core.traits.ListTrait;
import software.amazon.awssdk.core.traits.LocationTrait;
import software.amazon.awssdk.core.traits.Trait;
import software.amazon.awssdk.core.util.DefaultSdkAutoConstructList;
import software.amazon.awssdk.core.util.SdkAutoConstructList;
import software.amazon.awssdk.services.s3.model.DeletedObject;
import software.amazon.awssdk.services.s3.model.DeletedObjectsCopier;
import software.amazon.awssdk.services.s3.model.ErrorsCopier;
import software.amazon.awssdk.services.s3.model.RequestCharged;
import software.amazon.awssdk.services.s3.model.S3Error;
import software.amazon.awssdk.services.s3.model.S3Response;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

public final class DeleteObjectsResponse
extends S3Response
implements ToCopyableBuilder<Builder, DeleteObjectsResponse> {
    private static final SdkField<List<DeletedObject>> DELETED_FIELD = SdkField.builder((MarshallingType)MarshallingType.LIST).memberName("Deleted").getter(DeleteObjectsResponse.getter(DeleteObjectsResponse::deleted)).setter(DeleteObjectsResponse.setter(Builder::deleted)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("Deleted").unmarshallLocationName("Deleted").build(), ListTrait.builder().memberLocationName(null).memberFieldInfo(SdkField.builder((MarshallingType)MarshallingType.SDK_POJO).constructor(DeletedObject::builder).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("member").unmarshallLocationName("member").build()}).build()).isFlattened(true).build()}).build();
    private static final SdkField<String> REQUEST_CHARGED_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("RequestCharged").getter(DeleteObjectsResponse.getter(DeleteObjectsResponse::requestChargedAsString)).setter(DeleteObjectsResponse.setter(Builder::requestCharged)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("x-amz-request-charged").unmarshallLocationName("x-amz-request-charged").build()}).build();
    private static final SdkField<List<S3Error>> ERRORS_FIELD = SdkField.builder((MarshallingType)MarshallingType.LIST).memberName("Errors").getter(DeleteObjectsResponse.getter(DeleteObjectsResponse::errors)).setter(DeleteObjectsResponse.setter(Builder::errors)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("Error").unmarshallLocationName("Error").build(), ListTrait.builder().memberLocationName(null).memberFieldInfo(SdkField.builder((MarshallingType)MarshallingType.SDK_POJO).constructor(S3Error::builder).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("member").unmarshallLocationName("member").build()}).build()).isFlattened(true).build()}).build();
    private static final List<SdkField<?>> SDK_FIELDS = Collections.unmodifiableList(Arrays.asList(DELETED_FIELD, REQUEST_CHARGED_FIELD, ERRORS_FIELD));
    private final List<DeletedObject> deleted;
    private final String requestCharged;
    private final List<S3Error> errors;

    private DeleteObjectsResponse(BuilderImpl builder) {
        super(builder);
        this.deleted = builder.deleted;
        this.requestCharged = builder.requestCharged;
        this.errors = builder.errors;
    }

    public final boolean hasDeleted() {
        return this.deleted != null && !(this.deleted instanceof SdkAutoConstructList);
    }

    public final List<DeletedObject> deleted() {
        return this.deleted;
    }

    public final RequestCharged requestCharged() {
        return RequestCharged.fromValue(this.requestCharged);
    }

    public final String requestChargedAsString() {
        return this.requestCharged;
    }

    public final boolean hasErrors() {
        return this.errors != null && !(this.errors instanceof SdkAutoConstructList);
    }

    public final List<S3Error> errors() {
        return this.errors;
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
        hashCode = 31 * hashCode + Objects.hashCode(this.hasDeleted() ? this.deleted() : null);
        hashCode = 31 * hashCode + Objects.hashCode(this.requestChargedAsString());
        hashCode = 31 * hashCode + Objects.hashCode(this.hasErrors() ? this.errors() : null);
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
        if (!(obj instanceof DeleteObjectsResponse)) {
            return false;
        }
        DeleteObjectsResponse other = (DeleteObjectsResponse)((Object)obj);
        return this.hasDeleted() == other.hasDeleted() && Objects.equals(this.deleted(), other.deleted()) && Objects.equals(this.requestChargedAsString(), other.requestChargedAsString()) && this.hasErrors() == other.hasErrors() && Objects.equals(this.errors(), other.errors());
    }

    public final String toString() {
        return ToString.builder((String)"DeleteObjectsResponse").add("Deleted", this.hasDeleted() ? this.deleted() : null).add("RequestCharged", (Object)this.requestChargedAsString()).add("Errors", this.hasErrors() ? this.errors() : null).build();
    }

    public final <T> Optional<T> getValueForField(String fieldName, Class<T> clazz) {
        switch (fieldName) {
            case "Deleted": {
                return Optional.ofNullable(clazz.cast(this.deleted()));
            }
            case "RequestCharged": {
                return Optional.ofNullable(clazz.cast(this.requestChargedAsString()));
            }
            case "Errors": {
                return Optional.ofNullable(clazz.cast(this.errors()));
            }
        }
        return Optional.empty();
    }

    public final List<SdkField<?>> sdkFields() {
        return SDK_FIELDS;
    }

    private static <T> Function<Object, T> getter(Function<DeleteObjectsResponse, T> g) {
        return obj -> g.apply((DeleteObjectsResponse)((Object)((Object)obj)));
    }

    private static <T> BiConsumer<Object, T> setter(BiConsumer<Builder, T> s) {
        return (obj, val) -> s.accept((Builder)obj, val);
    }

    static final class BuilderImpl
    extends S3Response.BuilderImpl
    implements Builder {
        private List<DeletedObject> deleted = DefaultSdkAutoConstructList.getInstance();
        private String requestCharged;
        private List<S3Error> errors = DefaultSdkAutoConstructList.getInstance();

        private BuilderImpl() {
        }

        private BuilderImpl(DeleteObjectsResponse model) {
            super(model);
            this.deleted(model.deleted);
            this.requestCharged(model.requestCharged);
            this.errors(model.errors);
        }

        public final List<DeletedObject.Builder> getDeleted() {
            List<DeletedObject.Builder> result = DeletedObjectsCopier.copyToBuilder(this.deleted);
            if (result instanceof SdkAutoConstructList) {
                return null;
            }
            return result;
        }

        public final void setDeleted(Collection<DeletedObject.BuilderImpl> deleted) {
            this.deleted = DeletedObjectsCopier.copyFromBuilder(deleted);
        }

        @Override
        public final Builder deleted(Collection<DeletedObject> deleted) {
            this.deleted = DeletedObjectsCopier.copy(deleted);
            return this;
        }

        @Override
        @SafeVarargs
        public final Builder deleted(DeletedObject ... deleted) {
            this.deleted(Arrays.asList(deleted));
            return this;
        }

        @Override
        @SafeVarargs
        public final Builder deleted(Consumer<DeletedObject.Builder> ... deleted) {
            this.deleted(Stream.of(deleted).map(c -> (DeletedObject)((DeletedObject.Builder)DeletedObject.builder().applyMutation((Consumer)c)).build()).collect(Collectors.toList()));
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

        public final List<S3Error.Builder> getErrors() {
            List<S3Error.Builder> result = ErrorsCopier.copyToBuilder(this.errors);
            if (result instanceof SdkAutoConstructList) {
                return null;
            }
            return result;
        }

        public final void setErrors(Collection<S3Error.BuilderImpl> errors) {
            this.errors = ErrorsCopier.copyFromBuilder(errors);
        }

        @Override
        public final Builder errors(Collection<S3Error> errors) {
            this.errors = ErrorsCopier.copy(errors);
            return this;
        }

        @Override
        @SafeVarargs
        public final Builder errors(S3Error ... errors) {
            this.errors(Arrays.asList(errors));
            return this;
        }

        @Override
        @SafeVarargs
        public final Builder errors(Consumer<S3Error.Builder> ... errors) {
            this.errors(Stream.of(errors).map(c -> (S3Error)((S3Error.Builder)S3Error.builder().applyMutation((Consumer)c)).build()).collect(Collectors.toList()));
            return this;
        }

        @Override
        public DeleteObjectsResponse build() {
            return new DeleteObjectsResponse(this);
        }

        public List<SdkField<?>> sdkFields() {
            return SDK_FIELDS;
        }
    }

    public static interface Builder
    extends S3Response.Builder,
    SdkPojo,
    CopyableBuilder<Builder, DeleteObjectsResponse> {
        public Builder deleted(Collection<DeletedObject> var1);

        public Builder deleted(DeletedObject ... var1);

        public Builder deleted(Consumer<DeletedObject.Builder> ... var1);

        public Builder requestCharged(String var1);

        public Builder requestCharged(RequestCharged var1);

        public Builder errors(Collection<S3Error> var1);

        public Builder errors(S3Error ... var1);

        public Builder errors(Consumer<S3Error.Builder> ... var1);
    }
}

