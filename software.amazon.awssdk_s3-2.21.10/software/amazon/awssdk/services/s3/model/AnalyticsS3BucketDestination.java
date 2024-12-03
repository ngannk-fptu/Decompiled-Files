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
import java.util.function.Function;
import software.amazon.awssdk.core.SdkField;
import software.amazon.awssdk.core.SdkPojo;
import software.amazon.awssdk.core.protocol.MarshallLocation;
import software.amazon.awssdk.core.protocol.MarshallingType;
import software.amazon.awssdk.core.traits.LocationTrait;
import software.amazon.awssdk.core.traits.RequiredTrait;
import software.amazon.awssdk.core.traits.Trait;
import software.amazon.awssdk.services.s3.model.AnalyticsS3ExportFileFormat;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

public final class AnalyticsS3BucketDestination
implements SdkPojo,
Serializable,
ToCopyableBuilder<Builder, AnalyticsS3BucketDestination> {
    private static final SdkField<String> FORMAT_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("Format").getter(AnalyticsS3BucketDestination.getter(AnalyticsS3BucketDestination::formatAsString)).setter(AnalyticsS3BucketDestination.setter(Builder::format)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("Format").unmarshallLocationName("Format").build(), RequiredTrait.create()}).build();
    private static final SdkField<String> BUCKET_ACCOUNT_ID_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("BucketAccountId").getter(AnalyticsS3BucketDestination.getter(AnalyticsS3BucketDestination::bucketAccountId)).setter(AnalyticsS3BucketDestination.setter(Builder::bucketAccountId)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("BucketAccountId").unmarshallLocationName("BucketAccountId").build()}).build();
    private static final SdkField<String> BUCKET_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("Bucket").getter(AnalyticsS3BucketDestination.getter(AnalyticsS3BucketDestination::bucket)).setter(AnalyticsS3BucketDestination.setter(Builder::bucket)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("Bucket").unmarshallLocationName("Bucket").build(), RequiredTrait.create()}).build();
    private static final SdkField<String> PREFIX_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("Prefix").getter(AnalyticsS3BucketDestination.getter(AnalyticsS3BucketDestination::prefix)).setter(AnalyticsS3BucketDestination.setter(Builder::prefix)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("Prefix").unmarshallLocationName("Prefix").build()}).build();
    private static final List<SdkField<?>> SDK_FIELDS = Collections.unmodifiableList(Arrays.asList(FORMAT_FIELD, BUCKET_ACCOUNT_ID_FIELD, BUCKET_FIELD, PREFIX_FIELD));
    private static final long serialVersionUID = 1L;
    private final String format;
    private final String bucketAccountId;
    private final String bucket;
    private final String prefix;

    private AnalyticsS3BucketDestination(BuilderImpl builder) {
        this.format = builder.format;
        this.bucketAccountId = builder.bucketAccountId;
        this.bucket = builder.bucket;
        this.prefix = builder.prefix;
    }

    public final AnalyticsS3ExportFileFormat format() {
        return AnalyticsS3ExportFileFormat.fromValue(this.format);
    }

    public final String formatAsString() {
        return this.format;
    }

    public final String bucketAccountId() {
        return this.bucketAccountId;
    }

    public final String bucket() {
        return this.bucket;
    }

    public final String prefix() {
        return this.prefix;
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
        hashCode = 31 * hashCode + Objects.hashCode(this.formatAsString());
        hashCode = 31 * hashCode + Objects.hashCode(this.bucketAccountId());
        hashCode = 31 * hashCode + Objects.hashCode(this.bucket());
        hashCode = 31 * hashCode + Objects.hashCode(this.prefix());
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
        if (!(obj instanceof AnalyticsS3BucketDestination)) {
            return false;
        }
        AnalyticsS3BucketDestination other = (AnalyticsS3BucketDestination)obj;
        return Objects.equals(this.formatAsString(), other.formatAsString()) && Objects.equals(this.bucketAccountId(), other.bucketAccountId()) && Objects.equals(this.bucket(), other.bucket()) && Objects.equals(this.prefix(), other.prefix());
    }

    public final String toString() {
        return ToString.builder((String)"AnalyticsS3BucketDestination").add("Format", (Object)this.formatAsString()).add("BucketAccountId", (Object)this.bucketAccountId()).add("Bucket", (Object)this.bucket()).add("Prefix", (Object)this.prefix()).build();
    }

    public final <T> Optional<T> getValueForField(String fieldName, Class<T> clazz) {
        switch (fieldName) {
            case "Format": {
                return Optional.ofNullable(clazz.cast(this.formatAsString()));
            }
            case "BucketAccountId": {
                return Optional.ofNullable(clazz.cast(this.bucketAccountId()));
            }
            case "Bucket": {
                return Optional.ofNullable(clazz.cast(this.bucket()));
            }
            case "Prefix": {
                return Optional.ofNullable(clazz.cast(this.prefix()));
            }
        }
        return Optional.empty();
    }

    public final List<SdkField<?>> sdkFields() {
        return SDK_FIELDS;
    }

    private static <T> Function<Object, T> getter(Function<AnalyticsS3BucketDestination, T> g) {
        return obj -> g.apply((AnalyticsS3BucketDestination)obj);
    }

    private static <T> BiConsumer<Object, T> setter(BiConsumer<Builder, T> s) {
        return (obj, val) -> s.accept((Builder)obj, val);
    }

    static final class BuilderImpl
    implements Builder {
        private String format;
        private String bucketAccountId;
        private String bucket;
        private String prefix;

        private BuilderImpl() {
        }

        private BuilderImpl(AnalyticsS3BucketDestination model) {
            this.format(model.format);
            this.bucketAccountId(model.bucketAccountId);
            this.bucket(model.bucket);
            this.prefix(model.prefix);
        }

        public final String getFormat() {
            return this.format;
        }

        public final void setFormat(String format) {
            this.format = format;
        }

        @Override
        public final Builder format(String format) {
            this.format = format;
            return this;
        }

        @Override
        public final Builder format(AnalyticsS3ExportFileFormat format) {
            this.format(format == null ? null : format.toString());
            return this;
        }

        public final String getBucketAccountId() {
            return this.bucketAccountId;
        }

        public final void setBucketAccountId(String bucketAccountId) {
            this.bucketAccountId = bucketAccountId;
        }

        @Override
        public final Builder bucketAccountId(String bucketAccountId) {
            this.bucketAccountId = bucketAccountId;
            return this;
        }

        public final String getBucket() {
            return this.bucket;
        }

        public final void setBucket(String bucket) {
            this.bucket = bucket;
        }

        @Override
        public final Builder bucket(String bucket) {
            this.bucket = bucket;
            return this;
        }

        public final String getPrefix() {
            return this.prefix;
        }

        public final void setPrefix(String prefix) {
            this.prefix = prefix;
        }

        @Override
        public final Builder prefix(String prefix) {
            this.prefix = prefix;
            return this;
        }

        public AnalyticsS3BucketDestination build() {
            return new AnalyticsS3BucketDestination(this);
        }

        public List<SdkField<?>> sdkFields() {
            return SDK_FIELDS;
        }
    }

    public static interface Builder
    extends SdkPojo,
    CopyableBuilder<Builder, AnalyticsS3BucketDestination> {
        public Builder format(String var1);

        public Builder format(AnalyticsS3ExportFileFormat var1);

        public Builder bucketAccountId(String var1);

        public Builder bucket(String var1);

        public Builder prefix(String var1);
    }
}

