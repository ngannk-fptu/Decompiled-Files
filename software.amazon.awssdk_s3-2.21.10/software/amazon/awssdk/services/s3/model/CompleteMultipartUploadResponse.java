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
import software.amazon.awssdk.services.s3.model.RequestCharged;
import software.amazon.awssdk.services.s3.model.S3Response;
import software.amazon.awssdk.services.s3.model.ServerSideEncryption;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

public final class CompleteMultipartUploadResponse
extends S3Response
implements ToCopyableBuilder<Builder, CompleteMultipartUploadResponse> {
    private static final SdkField<String> LOCATION_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("Location").getter(CompleteMultipartUploadResponse.getter(CompleteMultipartUploadResponse::location)).setter(CompleteMultipartUploadResponse.setter(Builder::location)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("Location").unmarshallLocationName("Location").build()}).build();
    private static final SdkField<String> BUCKET_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("Bucket").getter(CompleteMultipartUploadResponse.getter(CompleteMultipartUploadResponse::bucket)).setter(CompleteMultipartUploadResponse.setter(Builder::bucket)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("Bucket").unmarshallLocationName("Bucket").build()}).build();
    private static final SdkField<String> KEY_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("Key").getter(CompleteMultipartUploadResponse.getter(CompleteMultipartUploadResponse::key)).setter(CompleteMultipartUploadResponse.setter(Builder::key)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("Key").unmarshallLocationName("Key").build()}).build();
    private static final SdkField<String> EXPIRATION_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("Expiration").getter(CompleteMultipartUploadResponse.getter(CompleteMultipartUploadResponse::expiration)).setter(CompleteMultipartUploadResponse.setter(Builder::expiration)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("x-amz-expiration").unmarshallLocationName("x-amz-expiration").build()}).build();
    private static final SdkField<String> E_TAG_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("ETag").getter(CompleteMultipartUploadResponse.getter(CompleteMultipartUploadResponse::eTag)).setter(CompleteMultipartUploadResponse.setter(Builder::eTag)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("ETag").unmarshallLocationName("ETag").build()}).build();
    private static final SdkField<String> CHECKSUM_CRC32_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("ChecksumCRC32").getter(CompleteMultipartUploadResponse.getter(CompleteMultipartUploadResponse::checksumCRC32)).setter(CompleteMultipartUploadResponse.setter(Builder::checksumCRC32)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("ChecksumCRC32").unmarshallLocationName("ChecksumCRC32").build()}).build();
    private static final SdkField<String> CHECKSUM_CRC32_C_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("ChecksumCRC32C").getter(CompleteMultipartUploadResponse.getter(CompleteMultipartUploadResponse::checksumCRC32C)).setter(CompleteMultipartUploadResponse.setter(Builder::checksumCRC32C)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("ChecksumCRC32C").unmarshallLocationName("ChecksumCRC32C").build()}).build();
    private static final SdkField<String> CHECKSUM_SHA1_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("ChecksumSHA1").getter(CompleteMultipartUploadResponse.getter(CompleteMultipartUploadResponse::checksumSHA1)).setter(CompleteMultipartUploadResponse.setter(Builder::checksumSHA1)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("ChecksumSHA1").unmarshallLocationName("ChecksumSHA1").build()}).build();
    private static final SdkField<String> CHECKSUM_SHA256_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("ChecksumSHA256").getter(CompleteMultipartUploadResponse.getter(CompleteMultipartUploadResponse::checksumSHA256)).setter(CompleteMultipartUploadResponse.setter(Builder::checksumSHA256)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("ChecksumSHA256").unmarshallLocationName("ChecksumSHA256").build()}).build();
    private static final SdkField<String> SERVER_SIDE_ENCRYPTION_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("ServerSideEncryption").getter(CompleteMultipartUploadResponse.getter(CompleteMultipartUploadResponse::serverSideEncryptionAsString)).setter(CompleteMultipartUploadResponse.setter(Builder::serverSideEncryption)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("x-amz-server-side-encryption").unmarshallLocationName("x-amz-server-side-encryption").build()}).build();
    private static final SdkField<String> VERSION_ID_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("VersionId").getter(CompleteMultipartUploadResponse.getter(CompleteMultipartUploadResponse::versionId)).setter(CompleteMultipartUploadResponse.setter(Builder::versionId)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("x-amz-version-id").unmarshallLocationName("x-amz-version-id").build()}).build();
    private static final SdkField<String> SSEKMS_KEY_ID_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("SSEKMSKeyId").getter(CompleteMultipartUploadResponse.getter(CompleteMultipartUploadResponse::ssekmsKeyId)).setter(CompleteMultipartUploadResponse.setter(Builder::ssekmsKeyId)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("x-amz-server-side-encryption-aws-kms-key-id").unmarshallLocationName("x-amz-server-side-encryption-aws-kms-key-id").build()}).build();
    private static final SdkField<Boolean> BUCKET_KEY_ENABLED_FIELD = SdkField.builder((MarshallingType)MarshallingType.BOOLEAN).memberName("BucketKeyEnabled").getter(CompleteMultipartUploadResponse.getter(CompleteMultipartUploadResponse::bucketKeyEnabled)).setter(CompleteMultipartUploadResponse.setter(Builder::bucketKeyEnabled)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("x-amz-server-side-encryption-bucket-key-enabled").unmarshallLocationName("x-amz-server-side-encryption-bucket-key-enabled").build()}).build();
    private static final SdkField<String> REQUEST_CHARGED_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("RequestCharged").getter(CompleteMultipartUploadResponse.getter(CompleteMultipartUploadResponse::requestChargedAsString)).setter(CompleteMultipartUploadResponse.setter(Builder::requestCharged)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("x-amz-request-charged").unmarshallLocationName("x-amz-request-charged").build()}).build();
    private static final List<SdkField<?>> SDK_FIELDS = Collections.unmodifiableList(Arrays.asList(LOCATION_FIELD, BUCKET_FIELD, KEY_FIELD, EXPIRATION_FIELD, E_TAG_FIELD, CHECKSUM_CRC32_FIELD, CHECKSUM_CRC32_C_FIELD, CHECKSUM_SHA1_FIELD, CHECKSUM_SHA256_FIELD, SERVER_SIDE_ENCRYPTION_FIELD, VERSION_ID_FIELD, SSEKMS_KEY_ID_FIELD, BUCKET_KEY_ENABLED_FIELD, REQUEST_CHARGED_FIELD));
    private final String location;
    private final String bucket;
    private final String key;
    private final String expiration;
    private final String eTag;
    private final String checksumCRC32;
    private final String checksumCRC32C;
    private final String checksumSHA1;
    private final String checksumSHA256;
    private final String serverSideEncryption;
    private final String versionId;
    private final String ssekmsKeyId;
    private final Boolean bucketKeyEnabled;
    private final String requestCharged;

    private CompleteMultipartUploadResponse(BuilderImpl builder) {
        super(builder);
        this.location = builder.location;
        this.bucket = builder.bucket;
        this.key = builder.key;
        this.expiration = builder.expiration;
        this.eTag = builder.eTag;
        this.checksumCRC32 = builder.checksumCRC32;
        this.checksumCRC32C = builder.checksumCRC32C;
        this.checksumSHA1 = builder.checksumSHA1;
        this.checksumSHA256 = builder.checksumSHA256;
        this.serverSideEncryption = builder.serverSideEncryption;
        this.versionId = builder.versionId;
        this.ssekmsKeyId = builder.ssekmsKeyId;
        this.bucketKeyEnabled = builder.bucketKeyEnabled;
        this.requestCharged = builder.requestCharged;
    }

    public final String location() {
        return this.location;
    }

    public final String bucket() {
        return this.bucket;
    }

    public final String key() {
        return this.key;
    }

    public final String expiration() {
        return this.expiration;
    }

    public final String eTag() {
        return this.eTag;
    }

    public final String checksumCRC32() {
        return this.checksumCRC32;
    }

    public final String checksumCRC32C() {
        return this.checksumCRC32C;
    }

    public final String checksumSHA1() {
        return this.checksumSHA1;
    }

    public final String checksumSHA256() {
        return this.checksumSHA256;
    }

    public final ServerSideEncryption serverSideEncryption() {
        return ServerSideEncryption.fromValue(this.serverSideEncryption);
    }

    public final String serverSideEncryptionAsString() {
        return this.serverSideEncryption;
    }

    public final String versionId() {
        return this.versionId;
    }

    public final String ssekmsKeyId() {
        return this.ssekmsKeyId;
    }

    public final Boolean bucketKeyEnabled() {
        return this.bucketKeyEnabled;
    }

    public final RequestCharged requestCharged() {
        return RequestCharged.fromValue(this.requestCharged);
    }

    public final String requestChargedAsString() {
        return this.requestCharged;
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
        hashCode = 31 * hashCode + Objects.hashCode(this.bucket());
        hashCode = 31 * hashCode + Objects.hashCode(this.key());
        hashCode = 31 * hashCode + Objects.hashCode(this.expiration());
        hashCode = 31 * hashCode + Objects.hashCode(this.eTag());
        hashCode = 31 * hashCode + Objects.hashCode(this.checksumCRC32());
        hashCode = 31 * hashCode + Objects.hashCode(this.checksumCRC32C());
        hashCode = 31 * hashCode + Objects.hashCode(this.checksumSHA1());
        hashCode = 31 * hashCode + Objects.hashCode(this.checksumSHA256());
        hashCode = 31 * hashCode + Objects.hashCode(this.serverSideEncryptionAsString());
        hashCode = 31 * hashCode + Objects.hashCode(this.versionId());
        hashCode = 31 * hashCode + Objects.hashCode(this.ssekmsKeyId());
        hashCode = 31 * hashCode + Objects.hashCode(this.bucketKeyEnabled());
        hashCode = 31 * hashCode + Objects.hashCode(this.requestChargedAsString());
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
        if (!(obj instanceof CompleteMultipartUploadResponse)) {
            return false;
        }
        CompleteMultipartUploadResponse other = (CompleteMultipartUploadResponse)((Object)obj);
        return Objects.equals(this.location(), other.location()) && Objects.equals(this.bucket(), other.bucket()) && Objects.equals(this.key(), other.key()) && Objects.equals(this.expiration(), other.expiration()) && Objects.equals(this.eTag(), other.eTag()) && Objects.equals(this.checksumCRC32(), other.checksumCRC32()) && Objects.equals(this.checksumCRC32C(), other.checksumCRC32C()) && Objects.equals(this.checksumSHA1(), other.checksumSHA1()) && Objects.equals(this.checksumSHA256(), other.checksumSHA256()) && Objects.equals(this.serverSideEncryptionAsString(), other.serverSideEncryptionAsString()) && Objects.equals(this.versionId(), other.versionId()) && Objects.equals(this.ssekmsKeyId(), other.ssekmsKeyId()) && Objects.equals(this.bucketKeyEnabled(), other.bucketKeyEnabled()) && Objects.equals(this.requestChargedAsString(), other.requestChargedAsString());
    }

    public final String toString() {
        return ToString.builder((String)"CompleteMultipartUploadResponse").add("Location", (Object)this.location()).add("Bucket", (Object)this.bucket()).add("Key", (Object)this.key()).add("Expiration", (Object)this.expiration()).add("ETag", (Object)this.eTag()).add("ChecksumCRC32", (Object)this.checksumCRC32()).add("ChecksumCRC32C", (Object)this.checksumCRC32C()).add("ChecksumSHA1", (Object)this.checksumSHA1()).add("ChecksumSHA256", (Object)this.checksumSHA256()).add("ServerSideEncryption", (Object)this.serverSideEncryptionAsString()).add("VersionId", (Object)this.versionId()).add("SSEKMSKeyId", (Object)(this.ssekmsKeyId() == null ? null : "*** Sensitive Data Redacted ***")).add("BucketKeyEnabled", (Object)this.bucketKeyEnabled()).add("RequestCharged", (Object)this.requestChargedAsString()).build();
    }

    public final <T> Optional<T> getValueForField(String fieldName, Class<T> clazz) {
        switch (fieldName) {
            case "Location": {
                return Optional.ofNullable(clazz.cast(this.location()));
            }
            case "Bucket": {
                return Optional.ofNullable(clazz.cast(this.bucket()));
            }
            case "Key": {
                return Optional.ofNullable(clazz.cast(this.key()));
            }
            case "Expiration": {
                return Optional.ofNullable(clazz.cast(this.expiration()));
            }
            case "ETag": {
                return Optional.ofNullable(clazz.cast(this.eTag()));
            }
            case "ChecksumCRC32": {
                return Optional.ofNullable(clazz.cast(this.checksumCRC32()));
            }
            case "ChecksumCRC32C": {
                return Optional.ofNullable(clazz.cast(this.checksumCRC32C()));
            }
            case "ChecksumSHA1": {
                return Optional.ofNullable(clazz.cast(this.checksumSHA1()));
            }
            case "ChecksumSHA256": {
                return Optional.ofNullable(clazz.cast(this.checksumSHA256()));
            }
            case "ServerSideEncryption": {
                return Optional.ofNullable(clazz.cast(this.serverSideEncryptionAsString()));
            }
            case "VersionId": {
                return Optional.ofNullable(clazz.cast(this.versionId()));
            }
            case "SSEKMSKeyId": {
                return Optional.ofNullable(clazz.cast(this.ssekmsKeyId()));
            }
            case "BucketKeyEnabled": {
                return Optional.ofNullable(clazz.cast(this.bucketKeyEnabled()));
            }
            case "RequestCharged": {
                return Optional.ofNullable(clazz.cast(this.requestChargedAsString()));
            }
        }
        return Optional.empty();
    }

    public final List<SdkField<?>> sdkFields() {
        return SDK_FIELDS;
    }

    private static <T> Function<Object, T> getter(Function<CompleteMultipartUploadResponse, T> g) {
        return obj -> g.apply((CompleteMultipartUploadResponse)((Object)((Object)obj)));
    }

    private static <T> BiConsumer<Object, T> setter(BiConsumer<Builder, T> s) {
        return (obj, val) -> s.accept((Builder)obj, val);
    }

    static final class BuilderImpl
    extends S3Response.BuilderImpl
    implements Builder {
        private String location;
        private String bucket;
        private String key;
        private String expiration;
        private String eTag;
        private String checksumCRC32;
        private String checksumCRC32C;
        private String checksumSHA1;
        private String checksumSHA256;
        private String serverSideEncryption;
        private String versionId;
        private String ssekmsKeyId;
        private Boolean bucketKeyEnabled;
        private String requestCharged;

        private BuilderImpl() {
        }

        private BuilderImpl(CompleteMultipartUploadResponse model) {
            super(model);
            this.location(model.location);
            this.bucket(model.bucket);
            this.key(model.key);
            this.expiration(model.expiration);
            this.eTag(model.eTag);
            this.checksumCRC32(model.checksumCRC32);
            this.checksumCRC32C(model.checksumCRC32C);
            this.checksumSHA1(model.checksumSHA1);
            this.checksumSHA256(model.checksumSHA256);
            this.serverSideEncryption(model.serverSideEncryption);
            this.versionId(model.versionId);
            this.ssekmsKeyId(model.ssekmsKeyId);
            this.bucketKeyEnabled(model.bucketKeyEnabled);
            this.requestCharged(model.requestCharged);
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

        public final String getKey() {
            return this.key;
        }

        public final void setKey(String key) {
            this.key = key;
        }

        @Override
        public final Builder key(String key) {
            this.key = key;
            return this;
        }

        public final String getExpiration() {
            return this.expiration;
        }

        public final void setExpiration(String expiration) {
            this.expiration = expiration;
        }

        @Override
        public final Builder expiration(String expiration) {
            this.expiration = expiration;
            return this;
        }

        public final String getETag() {
            return this.eTag;
        }

        public final void setETag(String eTag) {
            this.eTag = eTag;
        }

        @Override
        public final Builder eTag(String eTag) {
            this.eTag = eTag;
            return this;
        }

        public final String getChecksumCRC32() {
            return this.checksumCRC32;
        }

        public final void setChecksumCRC32(String checksumCRC32) {
            this.checksumCRC32 = checksumCRC32;
        }

        @Override
        public final Builder checksumCRC32(String checksumCRC32) {
            this.checksumCRC32 = checksumCRC32;
            return this;
        }

        public final String getChecksumCRC32C() {
            return this.checksumCRC32C;
        }

        public final void setChecksumCRC32C(String checksumCRC32C) {
            this.checksumCRC32C = checksumCRC32C;
        }

        @Override
        public final Builder checksumCRC32C(String checksumCRC32C) {
            this.checksumCRC32C = checksumCRC32C;
            return this;
        }

        public final String getChecksumSHA1() {
            return this.checksumSHA1;
        }

        public final void setChecksumSHA1(String checksumSHA1) {
            this.checksumSHA1 = checksumSHA1;
        }

        @Override
        public final Builder checksumSHA1(String checksumSHA1) {
            this.checksumSHA1 = checksumSHA1;
            return this;
        }

        public final String getChecksumSHA256() {
            return this.checksumSHA256;
        }

        public final void setChecksumSHA256(String checksumSHA256) {
            this.checksumSHA256 = checksumSHA256;
        }

        @Override
        public final Builder checksumSHA256(String checksumSHA256) {
            this.checksumSHA256 = checksumSHA256;
            return this;
        }

        public final String getServerSideEncryption() {
            return this.serverSideEncryption;
        }

        public final void setServerSideEncryption(String serverSideEncryption) {
            this.serverSideEncryption = serverSideEncryption;
        }

        @Override
        public final Builder serverSideEncryption(String serverSideEncryption) {
            this.serverSideEncryption = serverSideEncryption;
            return this;
        }

        @Override
        public final Builder serverSideEncryption(ServerSideEncryption serverSideEncryption) {
            this.serverSideEncryption(serverSideEncryption == null ? null : serverSideEncryption.toString());
            return this;
        }

        public final String getVersionId() {
            return this.versionId;
        }

        public final void setVersionId(String versionId) {
            this.versionId = versionId;
        }

        @Override
        public final Builder versionId(String versionId) {
            this.versionId = versionId;
            return this;
        }

        public final String getSsekmsKeyId() {
            return this.ssekmsKeyId;
        }

        public final void setSsekmsKeyId(String ssekmsKeyId) {
            this.ssekmsKeyId = ssekmsKeyId;
        }

        @Override
        public final Builder ssekmsKeyId(String ssekmsKeyId) {
            this.ssekmsKeyId = ssekmsKeyId;
            return this;
        }

        public final Boolean getBucketKeyEnabled() {
            return this.bucketKeyEnabled;
        }

        public final void setBucketKeyEnabled(Boolean bucketKeyEnabled) {
            this.bucketKeyEnabled = bucketKeyEnabled;
        }

        @Override
        public final Builder bucketKeyEnabled(Boolean bucketKeyEnabled) {
            this.bucketKeyEnabled = bucketKeyEnabled;
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

        @Override
        public CompleteMultipartUploadResponse build() {
            return new CompleteMultipartUploadResponse(this);
        }

        public List<SdkField<?>> sdkFields() {
            return SDK_FIELDS;
        }
    }

    public static interface Builder
    extends S3Response.Builder,
    SdkPojo,
    CopyableBuilder<Builder, CompleteMultipartUploadResponse> {
        public Builder location(String var1);

        public Builder bucket(String var1);

        public Builder key(String var1);

        public Builder expiration(String var1);

        public Builder eTag(String var1);

        public Builder checksumCRC32(String var1);

        public Builder checksumCRC32C(String var1);

        public Builder checksumSHA1(String var1);

        public Builder checksumSHA256(String var1);

        public Builder serverSideEncryption(String var1);

        public Builder serverSideEncryption(ServerSideEncryption var1);

        public Builder versionId(String var1);

        public Builder ssekmsKeyId(String var1);

        public Builder bucketKeyEnabled(Boolean var1);

        public Builder requestCharged(String var1);

        public Builder requestCharged(RequestCharged var1);
    }
}

