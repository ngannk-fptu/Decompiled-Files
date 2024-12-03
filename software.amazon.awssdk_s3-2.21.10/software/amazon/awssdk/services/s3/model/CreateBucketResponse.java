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
import software.amazon.awssdk.services.s3.model.S3Response;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

public final class CreateBucketResponse
extends S3Response
implements ToCopyableBuilder<Builder, CreateBucketResponse> {
    private static final SdkField<String> LOCATION_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("Location").getter(CreateBucketResponse.getter(CreateBucketResponse::location)).setter(CreateBucketResponse.setter(Builder::location)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("Location").unmarshallLocationName("Location").build()}).build();
    private static final List<SdkField<?>> SDK_FIELDS = Collections.unmodifiableList(Arrays.asList(LOCATION_FIELD));
    private final String location;

    private CreateBucketResponse(BuilderImpl builder) {
        super(builder);
        this.location = builder.location;
    }

    public final String location() {
        return this.location;
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
        hashCode = 31 * hashCode + Objects.hashCode(this.location());
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
        if (!(obj instanceof CreateBucketResponse)) {
            return false;
        }
        CreateBucketResponse other = (CreateBucketResponse)((Object)obj);
        return Objects.equals(this.location(), other.location());
    }

    public final String toString() {
        return ToString.builder((String)"CreateBucketResponse").add("Location", (Object)this.location()).build();
    }

    public final <T> Optional<T> getValueForField(String fieldName, Class<T> clazz) {
        switch (fieldName) {
            case "Location": {
                return Optional.ofNullable(clazz.cast(this.location()));
            }
        }
        return Optional.empty();
    }

    public final List<SdkField<?>> sdkFields() {
        return SDK_FIELDS;
    }

    private static <T> Function<Object, T> getter(Function<CreateBucketResponse, T> g) {
        return obj -> g.apply((CreateBucketResponse)((Object)((Object)obj)));
    }

    private static <T> BiConsumer<Object, T> setter(BiConsumer<Builder, T> s) {
        return (obj, val) -> s.accept((Builder)obj, val);
    }

    static final class BuilderImpl
    extends S3Response.BuilderImpl
    implements Builder {
        private String location;

        private BuilderImpl() {
        }

        private BuilderImpl(CreateBucketResponse model) {
            super(model);
            this.location(model.location);
        }

        public final String getLocation() {
            return this.location;
        }

        public final void setLocation(String location) {
            this.location = location;
        }

        @Override
        public final Builder location(String location) {
            this.location = location;
            return this;
        }

        @Override
        public CreateBucketResponse build() {
            return new CreateBucketResponse(this);
        }

        public List<SdkField<?>> sdkFields() {
            return SDK_FIELDS;
        }
    }

    public static interface Builder
    extends S3Response.Builder,
    SdkPojo,
    CopyableBuilder<Builder, CreateBucketResponse> {
        public Builder location(String var1);
    }
}

