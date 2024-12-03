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
import software.amazon.awssdk.services.s3.model.ObjectLockRetention;
import software.amazon.awssdk.services.s3.model.S3Response;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

public final class GetObjectRetentionResponse
extends S3Response
implements ToCopyableBuilder<Builder, GetObjectRetentionResponse> {
    private static final SdkField<ObjectLockRetention> RETENTION_FIELD = SdkField.builder((MarshallingType)MarshallingType.SDK_POJO).memberName("Retention").getter(GetObjectRetentionResponse.getter(GetObjectRetentionResponse::retention)).setter(GetObjectRetentionResponse.setter(Builder::retention)).constructor(ObjectLockRetention::builder).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("Retention").unmarshallLocationName("Retention").build(), PayloadTrait.create()}).build();
    private static final List<SdkField<?>> SDK_FIELDS = Collections.unmodifiableList(Arrays.asList(RETENTION_FIELD));
    private final ObjectLockRetention retention;

    private GetObjectRetentionResponse(BuilderImpl builder) {
        super(builder);
        this.retention = builder.retention;
    }

    public final ObjectLockRetention retention() {
        return this.retention;
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
        hashCode = 31 * hashCode + Objects.hashCode(this.retention());
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
        if (!(obj instanceof GetObjectRetentionResponse)) {
            return false;
        }
        GetObjectRetentionResponse other = (GetObjectRetentionResponse)((Object)obj);
        return Objects.equals(this.retention(), other.retention());
    }

    public final String toString() {
        return ToString.builder((String)"GetObjectRetentionResponse").add("Retention", (Object)this.retention()).build();
    }

    public final <T> Optional<T> getValueForField(String fieldName, Class<T> clazz) {
        switch (fieldName) {
            case "Retention": {
                return Optional.ofNullable(clazz.cast(this.retention()));
            }
        }
        return Optional.empty();
    }

    public final List<SdkField<?>> sdkFields() {
        return SDK_FIELDS;
    }

    private static <T> Function<Object, T> getter(Function<GetObjectRetentionResponse, T> g) {
        return obj -> g.apply((GetObjectRetentionResponse)((Object)((Object)obj)));
    }

    private static <T> BiConsumer<Object, T> setter(BiConsumer<Builder, T> s) {
        return (obj, val) -> s.accept((Builder)obj, val);
    }

    static final class BuilderImpl
    extends S3Response.BuilderImpl
    implements Builder {
        private ObjectLockRetention retention;

        private BuilderImpl() {
        }

        private BuilderImpl(GetObjectRetentionResponse model) {
            super(model);
            this.retention(model.retention);
        }

        public final ObjectLockRetention.Builder getRetention() {
            return this.retention != null ? this.retention.toBuilder() : null;
        }

        public final void setRetention(ObjectLockRetention.BuilderImpl retention) {
            this.retention = retention != null ? retention.build() : null;
        }

        @Override
        public final Builder retention(ObjectLockRetention retention) {
            this.retention = retention;
            return this;
        }

        @Override
        public GetObjectRetentionResponse build() {
            return new GetObjectRetentionResponse(this);
        }

        public List<SdkField<?>> sdkFields() {
            return SDK_FIELDS;
        }
    }

    public static interface Builder
    extends S3Response.Builder,
    SdkPojo,
    CopyableBuilder<Builder, GetObjectRetentionResponse> {
        public Builder retention(ObjectLockRetention var1);

        default public Builder retention(Consumer<ObjectLockRetention.Builder> retention) {
            return this.retention((ObjectLockRetention)((ObjectLockRetention.Builder)ObjectLockRetention.builder().applyMutation(retention)).build());
        }
    }
}

