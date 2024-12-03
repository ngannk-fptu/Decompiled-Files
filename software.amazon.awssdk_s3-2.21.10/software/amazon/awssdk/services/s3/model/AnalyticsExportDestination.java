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
import software.amazon.awssdk.services.s3.model.AnalyticsS3BucketDestination;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

public final class AnalyticsExportDestination
implements SdkPojo,
Serializable,
ToCopyableBuilder<Builder, AnalyticsExportDestination> {
    private static final SdkField<AnalyticsS3BucketDestination> S3_BUCKET_DESTINATION_FIELD = SdkField.builder((MarshallingType)MarshallingType.SDK_POJO).memberName("S3BucketDestination").getter(AnalyticsExportDestination.getter(AnalyticsExportDestination::s3BucketDestination)).setter(AnalyticsExportDestination.setter(Builder::s3BucketDestination)).constructor(AnalyticsS3BucketDestination::builder).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("S3BucketDestination").unmarshallLocationName("S3BucketDestination").build(), RequiredTrait.create()}).build();
    private static final List<SdkField<?>> SDK_FIELDS = Collections.unmodifiableList(Arrays.asList(S3_BUCKET_DESTINATION_FIELD));
    private static final long serialVersionUID = 1L;
    private final AnalyticsS3BucketDestination s3BucketDestination;

    private AnalyticsExportDestination(BuilderImpl builder) {
        this.s3BucketDestination = builder.s3BucketDestination;
    }

    public final AnalyticsS3BucketDestination s3BucketDestination() {
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
        if (!(obj instanceof AnalyticsExportDestination)) {
            return false;
        }
        AnalyticsExportDestination other = (AnalyticsExportDestination)obj;
        return Objects.equals(this.s3BucketDestination(), other.s3BucketDestination());
    }

    public final String toString() {
        return ToString.builder((String)"AnalyticsExportDestination").add("S3BucketDestination", (Object)this.s3BucketDestination()).build();
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

    private static <T> Function<Object, T> getter(Function<AnalyticsExportDestination, T> g) {
        return obj -> g.apply((AnalyticsExportDestination)obj);
    }

    private static <T> BiConsumer<Object, T> setter(BiConsumer<Builder, T> s) {
        return (obj, val) -> s.accept((Builder)obj, val);
    }

    static final class BuilderImpl
    implements Builder {
        private AnalyticsS3BucketDestination s3BucketDestination;

        private BuilderImpl() {
        }

        private BuilderImpl(AnalyticsExportDestination model) {
            this.s3BucketDestination(model.s3BucketDestination);
        }

        public final AnalyticsS3BucketDestination.Builder getS3BucketDestination() {
            return this.s3BucketDestination != null ? this.s3BucketDestination.toBuilder() : null;
        }

        public final void setS3BucketDestination(AnalyticsS3BucketDestination.BuilderImpl s3BucketDestination) {
            this.s3BucketDestination = s3BucketDestination != null ? s3BucketDestination.build() : null;
        }

        @Override
        public final Builder s3BucketDestination(AnalyticsS3BucketDestination s3BucketDestination) {
            this.s3BucketDestination = s3BucketDestination;
            return this;
        }

        public AnalyticsExportDestination build() {
            return new AnalyticsExportDestination(this);
        }

        public List<SdkField<?>> sdkFields() {
            return SDK_FIELDS;
        }
    }

    public static interface Builder
    extends SdkPojo,
    CopyableBuilder<Builder, AnalyticsExportDestination> {
        public Builder s3BucketDestination(AnalyticsS3BucketDestination var1);

        default public Builder s3BucketDestination(Consumer<AnalyticsS3BucketDestination.Builder> s3BucketDestination) {
            return this.s3BucketDestination((AnalyticsS3BucketDestination)((AnalyticsS3BucketDestination.Builder)AnalyticsS3BucketDestination.builder().applyMutation(s3BucketDestination)).build());
        }
    }
}

