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
import software.amazon.awssdk.services.s3.model.InventoryEncryption;
import software.amazon.awssdk.services.s3.model.InventoryFormat;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

public final class InventoryS3BucketDestination
implements SdkPojo,
Serializable,
ToCopyableBuilder<Builder, InventoryS3BucketDestination> {
    private static final SdkField<String> ACCOUNT_ID_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("AccountId").getter(InventoryS3BucketDestination.getter(InventoryS3BucketDestination::accountId)).setter(InventoryS3BucketDestination.setter(Builder::accountId)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("AccountId").unmarshallLocationName("AccountId").build()}).build();
    private static final SdkField<String> BUCKET_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("Bucket").getter(InventoryS3BucketDestination.getter(InventoryS3BucketDestination::bucket)).setter(InventoryS3BucketDestination.setter(Builder::bucket)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("Bucket").unmarshallLocationName("Bucket").build(), RequiredTrait.create()}).build();
    private static final SdkField<String> FORMAT_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("Format").getter(InventoryS3BucketDestination.getter(InventoryS3BucketDestination::formatAsString)).setter(InventoryS3BucketDestination.setter(Builder::format)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("Format").unmarshallLocationName("Format").build(), RequiredTrait.create()}).build();
    private static final SdkField<String> PREFIX_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("Prefix").getter(InventoryS3BucketDestination.getter(InventoryS3BucketDestination::prefix)).setter(InventoryS3BucketDestination.setter(Builder::prefix)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("Prefix").unmarshallLocationName("Prefix").build()}).build();
    private static final SdkField<InventoryEncryption> ENCRYPTION_FIELD = SdkField.builder((MarshallingType)MarshallingType.SDK_POJO).memberName("Encryption").getter(InventoryS3BucketDestination.getter(InventoryS3BucketDestination::encryption)).setter(InventoryS3BucketDestination.setter(Builder::encryption)).constructor(InventoryEncryption::builder).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("Encryption").unmarshallLocationName("Encryption").build()}).build();
    private static final List<SdkField<?>> SDK_FIELDS = Collections.unmodifiableList(Arrays.asList(ACCOUNT_ID_FIELD, BUCKET_FIELD, FORMAT_FIELD, PREFIX_FIELD, ENCRYPTION_FIELD));
    private static final long serialVersionUID = 1L;
    private final String accountId;
    private final String bucket;
    private final String format;
    private final String prefix;
    private final InventoryEncryption encryption;

    private InventoryS3BucketDestination(BuilderImpl builder) {
        this.accountId = builder.accountId;
        this.bucket = builder.bucket;
        this.format = builder.format;
        this.prefix = builder.prefix;
        this.encryption = builder.encryption;
    }

    public final String accountId() {
        return this.accountId;
    }

    public final String bucket() {
        return this.bucket;
    }

    public final InventoryFormat format() {
        return InventoryFormat.fromValue(this.format);
    }

    public final String formatAsString() {
        return this.format;
    }

    public final String prefix() {
        return this.prefix;
    }

    public final InventoryEncryption encryption() {
        return this.encryption;
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
        hashCode = 31 * hashCode + Objects.hashCode(this.accountId());
        hashCode = 31 * hashCode + Objects.hashCode(this.bucket());
        hashCode = 31 * hashCode + Objects.hashCode(this.formatAsString());
        hashCode = 31 * hashCode + Objects.hashCode(this.prefix());
        hashCode = 31 * hashCode + Objects.hashCode(this.encryption());
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
        if (!(obj instanceof InventoryS3BucketDestination)) {
            return false;
        }
        InventoryS3BucketDestination other = (InventoryS3BucketDestination)obj;
        return Objects.equals(this.accountId(), other.accountId()) && Objects.equals(this.bucket(), other.bucket()) && Objects.equals(this.formatAsString(), other.formatAsString()) && Objects.equals(this.prefix(), other.prefix()) && Objects.equals(this.encryption(), other.encryption());
    }

    public final String toString() {
        return ToString.builder((String)"InventoryS3BucketDestination").add("AccountId", (Object)this.accountId()).add("Bucket", (Object)this.bucket()).add("Format", (Object)this.formatAsString()).add("Prefix", (Object)this.prefix()).add("Encryption", (Object)this.encryption()).build();
    }

    public final <T> Optional<T> getValueForField(String fieldName, Class<T> clazz) {
        switch (fieldName) {
            case "AccountId": {
                return Optional.ofNullable(clazz.cast(this.accountId()));
            }
            case "Bucket": {
                return Optional.ofNullable(clazz.cast(this.bucket()));
            }
            case "Format": {
                return Optional.ofNullable(clazz.cast(this.formatAsString()));
            }
            case "Prefix": {
                return Optional.ofNullable(clazz.cast(this.prefix()));
            }
            case "Encryption": {
                return Optional.ofNullable(clazz.cast(this.encryption()));
            }
        }
        return Optional.empty();
    }

    public final List<SdkField<?>> sdkFields() {
        return SDK_FIELDS;
    }

    private static <T> Function<Object, T> getter(Function<InventoryS3BucketDestination, T> g) {
        return obj -> g.apply((InventoryS3BucketDestination)obj);
    }

    private static <T> BiConsumer<Object, T> setter(BiConsumer<Builder, T> s) {
        return (obj, val) -> s.accept((Builder)obj, val);
    }

    static final class BuilderImpl
    implements Builder {
        private String accountId;
        private String bucket;
        private String format;
        private String prefix;
        private InventoryEncryption encryption;

        private BuilderImpl() {
        }

        private BuilderImpl(InventoryS3BucketDestination model) {
            this.accountId(model.accountId);
            this.bucket(model.bucket);
            this.format(model.format);
            this.prefix(model.prefix);
            this.encryption(model.encryption);
        }

        public final String getAccountId() {
            return this.accountId;
        }

        public final void setAccountId(String accountId) {
            this.accountId = accountId;
        }

        @Override
        public final Builder accountId(String accountId) {
            this.accountId = accountId;
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
        public final Builder format(InventoryFormat format) {
            this.format(format == null ? null : format.toString());
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

        public final InventoryEncryption.Builder getEncryption() {
            return this.encryption != null ? this.encryption.toBuilder() : null;
        }

        public final void setEncryption(InventoryEncryption.BuilderImpl encryption) {
            this.encryption = encryption != null ? encryption.build() : null;
        }

        @Override
        public final Builder encryption(InventoryEncryption encryption) {
            this.encryption = encryption;
            return this;
        }

        public InventoryS3BucketDestination build() {
            return new InventoryS3BucketDestination(this);
        }

        public List<SdkField<?>> sdkFields() {
            return SDK_FIELDS;
        }
    }

    public static interface Builder
    extends SdkPojo,
    CopyableBuilder<Builder, InventoryS3BucketDestination> {
        public Builder accountId(String var1);

        public Builder bucket(String var1);

        public Builder format(String var1);

        public Builder format(InventoryFormat var1);

        public Builder prefix(String var1);

        public Builder encryption(InventoryEncryption var1);

        default public Builder encryption(Consumer<InventoryEncryption.Builder> encryption) {
            return this.encryption((InventoryEncryption)((InventoryEncryption.Builder)InventoryEncryption.builder().applyMutation(encryption)).build());
        }
    }
}

