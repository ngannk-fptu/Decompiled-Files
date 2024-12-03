/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.core.SdkField
 *  software.amazon.awssdk.core.SdkPojo
 *  software.amazon.awssdk.core.protocol.MarshallLocation
 *  software.amazon.awssdk.core.protocol.MarshallingType
 *  software.amazon.awssdk.core.traits.LocationTrait
 *  software.amazon.awssdk.core.traits.PayloadTrait
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
import java.util.function.Consumer;
import java.util.function.Function;
import software.amazon.awssdk.core.SdkField;
import software.amazon.awssdk.core.SdkPojo;
import software.amazon.awssdk.core.protocol.MarshallLocation;
import software.amazon.awssdk.core.protocol.MarshallingType;
import software.amazon.awssdk.core.traits.LocationTrait;
import software.amazon.awssdk.core.traits.PayloadTrait;
import software.amazon.awssdk.core.traits.Trait;
import software.amazon.awssdk.services.s3.model.OwnershipControls;
import software.amazon.awssdk.services.s3.model.S3Response;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

public final class GetBucketOwnershipControlsResponse
extends S3Response
implements ToCopyableBuilder<Builder, GetBucketOwnershipControlsResponse> {
    private static final SdkField<OwnershipControls> OWNERSHIP_CONTROLS_FIELD = SdkField.builder((MarshallingType)MarshallingType.SDK_POJO).memberName("OwnershipControls").getter(GetBucketOwnershipControlsResponse.getter(GetBucketOwnershipControlsResponse::ownershipControls)).setter(GetBucketOwnershipControlsResponse.setter(Builder::ownershipControls)).constructor(OwnershipControls::builder).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("OwnershipControls").unmarshallLocationName("OwnershipControls").build(), PayloadTrait.create()}).build();
    private static final List<SdkField<?>> SDK_FIELDS = Collections.unmodifiableList(Arrays.asList(OWNERSHIP_CONTROLS_FIELD));
    private final OwnershipControls ownershipControls;

    private GetBucketOwnershipControlsResponse(BuilderImpl builder) {
        super(builder);
        this.ownershipControls = builder.ownershipControls;
    }

    public final OwnershipControls ownershipControls() {
        return this.ownershipControls;
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
        hashCode = 31 * hashCode + Objects.hashCode(this.ownershipControls());
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
        if (!(obj instanceof GetBucketOwnershipControlsResponse)) {
            return false;
        }
        GetBucketOwnershipControlsResponse other = (GetBucketOwnershipControlsResponse)((Object)obj);
        return Objects.equals(this.ownershipControls(), other.ownershipControls());
    }

    public final String toString() {
        return ToString.builder((String)"GetBucketOwnershipControlsResponse").add("OwnershipControls", (Object)this.ownershipControls()).build();
    }

    public final <T> Optional<T> getValueForField(String fieldName, Class<T> clazz) {
        switch (fieldName) {
            case "OwnershipControls": {
                return Optional.ofNullable(clazz.cast(this.ownershipControls()));
            }
        }
        return Optional.empty();
    }

    public final List<SdkField<?>> sdkFields() {
        return SDK_FIELDS;
    }

    private static <T> Function<Object, T> getter(Function<GetBucketOwnershipControlsResponse, T> g) {
        return obj -> g.apply((GetBucketOwnershipControlsResponse)((Object)((Object)obj)));
    }

    private static <T> BiConsumer<Object, T> setter(BiConsumer<Builder, T> s) {
        return (obj, val) -> s.accept((Builder)obj, val);
    }

    static final class BuilderImpl
    extends S3Response.BuilderImpl
    implements Builder {
        private OwnershipControls ownershipControls;

        private BuilderImpl() {
        }

        private BuilderImpl(GetBucketOwnershipControlsResponse model) {
            super(model);
            this.ownershipControls(model.ownershipControls);
        }

        public final OwnershipControls.Builder getOwnershipControls() {
            return this.ownershipControls != null ? this.ownershipControls.toBuilder() : null;
        }

        public final void setOwnershipControls(OwnershipControls.BuilderImpl ownershipControls) {
            this.ownershipControls = ownershipControls != null ? ownershipControls.build() : null;
        }

        @Override
        public final Builder ownershipControls(OwnershipControls ownershipControls) {
            this.ownershipControls = ownershipControls;
            return this;
        }

        @Override
        public GetBucketOwnershipControlsResponse build() {
            return new GetBucketOwnershipControlsResponse(this);
        }

        public List<SdkField<?>> sdkFields() {
            return SDK_FIELDS;
        }
    }

    public static interface Builder
    extends S3Response.Builder,
    SdkPojo,
    CopyableBuilder<Builder, GetBucketOwnershipControlsResponse> {
        public Builder ownershipControls(OwnershipControls var1);

        default public Builder ownershipControls(Consumer<OwnershipControls.Builder> ownershipControls) {
            return this.ownershipControls((OwnershipControls)((OwnershipControls.Builder)OwnershipControls.builder().applyMutation(ownershipControls)).build());
        }
    }
}

