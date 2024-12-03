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
import software.amazon.awssdk.services.s3.model.ObjectLockLegalHold;
import software.amazon.awssdk.services.s3.model.S3Response;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

public final class GetObjectLegalHoldResponse
extends S3Response
implements ToCopyableBuilder<Builder, GetObjectLegalHoldResponse> {
    private static final SdkField<ObjectLockLegalHold> LEGAL_HOLD_FIELD = SdkField.builder((MarshallingType)MarshallingType.SDK_POJO).memberName("LegalHold").getter(GetObjectLegalHoldResponse.getter(GetObjectLegalHoldResponse::legalHold)).setter(GetObjectLegalHoldResponse.setter(Builder::legalHold)).constructor(ObjectLockLegalHold::builder).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("LegalHold").unmarshallLocationName("LegalHold").build(), PayloadTrait.create()}).build();
    private static final List<SdkField<?>> SDK_FIELDS = Collections.unmodifiableList(Arrays.asList(LEGAL_HOLD_FIELD));
    private final ObjectLockLegalHold legalHold;

    private GetObjectLegalHoldResponse(BuilderImpl builder) {
        super(builder);
        this.legalHold = builder.legalHold;
    }

    public final ObjectLockLegalHold legalHold() {
        return this.legalHold;
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
        hashCode = 31 * hashCode + Objects.hashCode(this.legalHold());
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
        if (!(obj instanceof GetObjectLegalHoldResponse)) {
            return false;
        }
        GetObjectLegalHoldResponse other = (GetObjectLegalHoldResponse)((Object)obj);
        return Objects.equals(this.legalHold(), other.legalHold());
    }

    public final String toString() {
        return ToString.builder((String)"GetObjectLegalHoldResponse").add("LegalHold", (Object)this.legalHold()).build();
    }

    public final <T> Optional<T> getValueForField(String fieldName, Class<T> clazz) {
        switch (fieldName) {
            case "LegalHold": {
                return Optional.ofNullable(clazz.cast(this.legalHold()));
            }
        }
        return Optional.empty();
    }

    public final List<SdkField<?>> sdkFields() {
        return SDK_FIELDS;
    }

    private static <T> Function<Object, T> getter(Function<GetObjectLegalHoldResponse, T> g) {
        return obj -> g.apply((GetObjectLegalHoldResponse)((Object)((Object)obj)));
    }

    private static <T> BiConsumer<Object, T> setter(BiConsumer<Builder, T> s) {
        return (obj, val) -> s.accept((Builder)obj, val);
    }

    static final class BuilderImpl
    extends S3Response.BuilderImpl
    implements Builder {
        private ObjectLockLegalHold legalHold;

        private BuilderImpl() {
        }

        private BuilderImpl(GetObjectLegalHoldResponse model) {
            super(model);
            this.legalHold(model.legalHold);
        }

        public final ObjectLockLegalHold.Builder getLegalHold() {
            return this.legalHold != null ? this.legalHold.toBuilder() : null;
        }

        public final void setLegalHold(ObjectLockLegalHold.BuilderImpl legalHold) {
            this.legalHold = legalHold != null ? legalHold.build() : null;
        }

        @Override
        public final Builder legalHold(ObjectLockLegalHold legalHold) {
            this.legalHold = legalHold;
            return this;
        }

        @Override
        public GetObjectLegalHoldResponse build() {
            return new GetObjectLegalHoldResponse(this);
        }

        public List<SdkField<?>> sdkFields() {
            return SDK_FIELDS;
        }
    }

    public static interface Builder
    extends S3Response.Builder,
    SdkPojo,
    CopyableBuilder<Builder, GetObjectLegalHoldResponse> {
        public Builder legalHold(ObjectLockLegalHold var1);

        default public Builder legalHold(Consumer<ObjectLockLegalHold.Builder> legalHold) {
            return this.legalHold((ObjectLockLegalHold)((ObjectLockLegalHold.Builder)ObjectLockLegalHold.builder().applyMutation(legalHold)).build());
        }
    }
}

