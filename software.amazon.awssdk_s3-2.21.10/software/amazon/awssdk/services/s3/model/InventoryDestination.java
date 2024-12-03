/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.core.SdkField
 *  software.amazon.awssdk.core.SdkPojo
 *  software.amazon.awssdk.core.protocol.MarshallLocation
 *  software.amazon.awssdk.core.protocol.MarshallingType
 *  software.amazon.awssdk.core.traits.LocationTrait
 *  software.amazon.awssdk.core.traits.RequiredTrait
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
import java.util.function.Consumer;
import java.util.function.Function;
import software.amazon.awssdk.core.SdkField;
import software.amazon.awssdk.core.SdkPojo;
import software.amazon.awssdk.core.protocol.MarshallLocation;
import software.amazon.awssdk.core.protocol.MarshallingType;
import software.amazon.awssdk.core.traits.LocationTrait;
import software.amazon.awssdk.core.traits.RequiredTrait;
import software.amazon.awssdk.core.traits.Trait;
import software.amazon.awssdk.services.s3.model.InventoryS3BucketDestination;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

public final class InventoryDestination
implements SdkPojo,
Serializable,
ToCopyableBuilder<Builder, InventoryDestination> {
    private static final SdkField<InventoryS3BucketDestination> S3_BUCKET_DESTINATION_FIELD = SdkField.builder((MarshallingType)MarshallingType.SDK_POJO).memberName("S3BucketDestination").getter(InventoryDestination.getter(InventoryDestination::s3BucketDestination)).setter(InventoryDestination.setter(Builder::s3BucketDestination)).constructor(InventoryS3BucketDestination::builder).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("S3BucketDestination").unmarshallLocationName("S3BucketDestination").build(), RequiredTrait.create()}).build();
    private static final List<SdkField<?>> SDK_FIELDS = Collections.unmodifiableList(Arrays.asList(S3_BUCKET_DESTINATION_FIELD));
    private static final long serialVersionUID = 1L;
    private final InventoryS3BucketDestination s3BucketDestination;

    private InventoryDestination(BuilderImpl builder) {
        this.s3BucketDestination = builder.s3BucketDestination;
    }

    public final InventoryS3BucketDestination s3BucketDestination() {
        return this.s3BucketDestination;
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
        hashCode = 31 * hashCode + Objects.hashCode(this.s3BucketDestination());
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
        if (!(obj instanceof InventoryDestination)) {
            return false;
        }
        InventoryDestination other = (InventoryDestination)obj;
        return Objects.equals(this.s3BucketDestination(), other.s3BucketDestination());
    }

    public final String toString() {
        return ToString.builder((String)"InventoryDestination").add("S3BucketDestination", (Object)this.s3BucketDestination()).build();
    }

    public final <T> Optional<T> getValueForField(String fieldName, Class<T> clazz) {
        switch (fieldName) {
            case "S3BucketDestination": {
                return Optional.ofNullable(clazz.cast(this.s3BucketDestination()));
            }
        }
        return Optional.empty();
    }

    public final List<SdkField<?>> sdkFields() {
        return SDK_FIELDS;
    }

    private static <T> Function<Object, T> getter(Function<InventoryDestination, T> g) {
        return obj -> g.apply((InventoryDestination)obj);
    }

    private static <T> BiConsumer<Object, T> setter(BiConsumer<Builder, T> s) {
        return (obj, val) -> s.accept((Builder)obj, val);
    }

    static final class BuilderImpl
    implements Builder {
        private InventoryS3BucketDestination s3BucketDestination;

        private BuilderImpl() {
        }

        private BuilderImpl(InventoryDestination model) {
            this.s3BucketDestination(model.s3BucketDestination);
        }

        public final InventoryS3BucketDestination.Builder getS3BucketDestination() {
            return this.s3BucketDestination != null ? this.s3BucketDestination.toBuilder() : null;
        }

        public final void setS3BucketDestination(InventoryS3BucketDestination.BuilderImpl s3BucketDestination) {
            this.s3BucketDestination = s3BucketDestination != null ? s3BucketDestination.build() : null;
        }

        @Override
        public final Builder s3BucketDestination(InventoryS3BucketDestination s3BucketDestination) {
            this.s3BucketDestination = s3BucketDestination;
            return this;
        }

        public InventoryDestination build() {
            return new InventoryDestination(this);
        }

        public List<SdkField<?>> sdkFields() {
            return SDK_FIELDS;
        }
    }

    public static interface Builder
    extends SdkPojo,
    CopyableBuilder<Builder, InventoryDestination> {
        public Builder s3BucketDestination(InventoryS3BucketDestination var1);

        default public Builder s3BucketDestination(Consumer<InventoryS3BucketDestination.Builder> s3BucketDestination) {
            return this.s3BucketDestination((InventoryS3BucketDestination)((InventoryS3BucketDestination.Builder)InventoryS3BucketDestination.builder().applyMutation(s3BucketDestination)).build());
        }
    }
}

