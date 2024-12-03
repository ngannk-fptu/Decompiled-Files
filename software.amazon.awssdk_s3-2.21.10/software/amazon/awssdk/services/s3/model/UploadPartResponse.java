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

public final class UploadPartResponse
extends S3Response
implements ToCopyableBuilder<Builder, UploadPartResponse> {
    private static final SdkField<String> SERVER_SIDE_ENCRYPTION_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("ServerSideEncryption").getter(UploadPartResponse.getter(UploadPartResponse::serverSideEncryptionAsString)).setter(UploadPartResponse.setter(Builder::serverSideEncryption)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("x-amz-server-side-encryption").unmarshallLocationName("x-amz-server-side-encryption").build()}).build();
    private static final SdkField<String> E_TAG_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("ETag").getter(UploadPartResponse.getter(UploadPartResponse::eTag)).setter(UploadPartResponse.setter(Builder::eTag)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("ETag").unmarshallLocationName("ETag").build()}).build();
    private static final SdkField<String> CHECKSUM_CRC32_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("ChecksumCRC32").getter(UploadPartResponse.getter(UploadPartResponse::checksumCRC32)).setter(UploadPartResponse.setter(Builder::checksumCRC32)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("x-amz-checksum-crc32").unmarshallLocationName("x-amz-checksum-crc32").build()}).build();
    private static final SdkField<String> CHECKSUM_CRC32_C_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("ChecksumCRC32C").getter(UploadPartResponse.getter(UploadPartResponse::checksumCRC32C)).setter(UploadPartResponse.setter(Builder::checksumCRC32C)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("x-amz-checksum-crc32c").unmarshallLocationName("x-amz-checksum-crc32c").build()}).build();
    private static final SdkField<String> CHECKSUM_SHA1_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("ChecksumSHA1").getter(UploadPartResponse.getter(UploadPartResponse::checksumSHA1)).setter(UploadPartResponse.setter(Builder::checksumSHA1)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("x-amz-checksum-sha1").unmarshallLocationName("x-amz-checksum-sha1").build()}).build();
    private static final SdkField<String> CHECKSUM_SHA256_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("ChecksumSHA256").getter(UploadPartResponse.getter(UploadPartResponse::checksumSHA256)).setter(UploadPartResponse.setter(Builder::checksumSHA256)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("x-amz-checksum-sha256").unmarshallLocationName("x-amz-checksum-sha256").build()}).build();
    private static final SdkField<String> SSE_CUSTOMER_ALGORITHM_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("SSECustomerAlgorithm").getter(UploadPartResponse.getter(UploadPartResponse::sseCustomerAlgorithm)).setter(UploadPartResponse.setter(Builder::sseCustomerAlgorithm)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("x-amz-server-side-encryption-customer-algorithm").unmarshallLocationName("x-amz-server-side-encryption-customer-algorithm").build()}).build();
    private static final SdkField<String> SSE_CUSTOMER_KEY_MD5_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("SSECustomerKeyMD5").getter(UploadPartResponse.getter(UploadPartResponse::sseCustomerKeyMD5)).setter(UploadPartResponse.setter(Builder::sseCustomerKeyMD5)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("x-amz-server-side-encryption-customer-key-MD5").unmarshallLocationName("x-amz-server-side-encryption-customer-key-MD5").build()}).build();
    private static final SdkField<String> SSEKMS_KEY_ID_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("SSEKMSKeyId").getter(UploadPartResponse.getter(UploadPartResponse::ssekmsKeyId)).setter(UploadPartResponse.setter(Builder::ssekmsKeyId)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("x-amz-server-side-encryption-aws-kms-key-id").unmarshallLocationName("x-amz-server-side-encryption-aws-kms-key-id").build()}).build();
    private static final SdkField<Boolean> BUCKET_KEY_ENABLED_FIELD = SdkField.builder((MarshallingType)MarshallingType.BOOLEAN).memberName("BucketKeyEnabled").getter(UploadPartResponse.getter(UploadPartResponse::bucketKeyEnabled)).setter(UploadPartResponse.setter(Builder::bucketKeyEnabled)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("x-amz-server-side-encryption-bucket-key-enabled").unmarshallLocationName("x-amz-server-side-encryption-bucket-key-enabled").build()}).build();
    private static final SdkField<String> REQUEST_CHARGED_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("RequestCharged").getter(UploadPartResponse.getter(UploadPartResponse::requestChargedAsString)).setter(UploadPartResponse.setter(Builder::requestCharged)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("x-amz-request-charged").unmarshallLocationName("x-amz-request-charged").build()}).build();
    private static final List<SdkField<?>> SDK_FIELDS = Collections.unmodifiableList(Arrays.asList(SERVER_SIDE_ENCRYPTION_FIELD, E_TAG_FIELD, CHECKSUM_CRC32_FIELD, CHECKSUM_CRC32_C_FIELD, CHECKSUM_SHA1_FIELD, CHECKSUM_SHA256_FIELD, SSE_CUSTOMER_ALGORITHM_FIELD, SSE_CUSTOMER_KEY_MD5_FIELD, SSEKMS_KEY_ID_FIELD, BUCKET_KEY_ENABLED_FIELD, REQUEST_CHARGED_FIELD));
    private final String serverSideEncryption;
    private final String eTag;
    private final String checksumCRC32;
    private final String checksumCRC32C;
    private final String checksumSHA1;
    private final String checksumSHA256;
    private final String sseCustomerAlgorithm;
    private final String sseCustomerKeyMD5;
    private final String ssekmsKeyId;
    private final Boolean bucketKeyEnabled;
    private final String requestCharged;

    private UploadPartResponse(BuilderImpl builder) {
        super(builder);
        this.serverSideEncryption = builder.serverSideEncryption;
        this.eTag = builder.eTag;
        this.checksumCRC32 = builder.checksumCRC32;
        this.checksumCRC32C = builder.checksumCRC32C;
        this.checksumSHA1 = builder.checksumSHA1;
        this.checksumSHA256 = builder.checksumSHA256;
        this.sseCustomerAlgorithm = builder.sseCustomerAlgorithm;
        this.sseCustomerKeyMD5 = builder.sseCustomerKeyMD5;
        this.ssekmsKeyId = builder.ssekmsKeyId;
        this.bucketKeyEnabled = builder.bucketKeyEnabled;
        this.requestCharged = builder.requestCharged;
    }

    public final ServerSideEncryption serverSideEncryption() {
        return ServerSideEncryption.fromValue(this.serverSideEncryption);
    }

    public final String serverSideEncryptionAsString() {
        return this.serverSideEncryption;
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

    public final String sseCustomerAlgorithm() {
        return this.sseCustomerAlgorithm;
    }

    public final String sseCustomerKeyMD5() {
        return this.sseCustomerKeyMD5;
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
        hashCode = 31 * hashCode + Objects.hashCode(this.serverSideEncryptionAsString());
        hashCode = 31 * hashCode + Objects.hashCode(this.eTag());
        hashCode = 31 * hashCode + Objects.hashCode(this.checksumCRC32());
        hashCode = 31 * hashCode + Objects.hashCode(this.checksumCRC32C());
        hashCode = 31 * hashCode + Objects.hashCode(this.checksumSHA1());
        hashCode = 31 * hashCode + Objects.hashCode(this.checksumSHA256());
        hashCode = 31 * hashCode + Objects.hashCode(this.sseCustomerAlgorithm());
        hashCode = 31 * hashCode + Objects.hashCode(this.sseCustomerKeyMD5());
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
        if (!(obj instanceof UploadPartResponse)) {
            return false;
        }
        UploadPartResponse other = (UploadPartResponse)((Object)obj);
        return Objects.equals(this.serverSideEncryptionAsString(), other.serverSideEncryptionAsString()) && Objects.equals(this.eTag(), other.eTag()) && Objects.equals(this.checksumCRC32(), other.checksumCRC32()) && Objects.equals(this.checksumCRC32C(), other.checksumCRC32C()) && Objects.equals(this.checksumSHA1(), other.checksumSHA1()) && Objects.equals(this.checksumSHA256(), other.checksumSHA256()) && Objects.equals(this.sseCustomerAlgorithm(), other.sseCustomerAlgorithm()) && Objects.equals(this.sseCustomerKeyMD5(), other.sseCustomerKeyMD5()) && Objects.equals(this.ssekmsKeyId(), other.ssekmsKeyId()) && Objects.equals(this.bucketKeyEnabled(), other.bucketKeyEnabled()) && Objects.equals(this.requestChargedAsString(), other.requestChargedAsString());
    }

    public final String toString() {
        return ToString.builder((String)"UploadPartResponse").add("ServerSideEncryption", (Object)this.serverSideEncryptionAsString()).add("ETag", (Object)this.eTag()).add("ChecksumCRC32", (Object)this.checksumCRC32()).add("ChecksumCRC32C", (Object)this.checksumCRC32C()).add("ChecksumSHA1", (Object)this.checksumSHA1()).add("ChecksumSHA256", (Object)this.checksumSHA256()).add("SSECustomerAlgorithm", (Object)this.sseCustomerAlgorithm()).add("SSECustomerKeyMD5", (Object)this.sseCustomerKeyMD5()).add("SSEKMSKeyId", (Object)(this.ssekmsKeyId() == null ? null : "*** Sensitive Data Redacted ***")).add("BucketKeyEnabled", (Object)this.bucketKeyEnabled()).add("RequestCharged", (Object)this.requestChargedAsString()).build();
    }

    public final <T> Optional<T> getValueForField(String fieldName, Class<T> clazz) {
        switch (fieldName) {
            case "ServerSideEncryption": {
                return Optional.ofNullable(clazz.cast(this.serverSideEncryptionAsString()));
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
            case "SSECustomerAlgorithm": {
                return Optional.ofNullable(clazz.cast(this.sseCustomerAlgorithm()));
            }
            case "SSECustomerKeyMD5": {
                return Optional.ofNullable(clazz.cast(this.sseCustomerKeyMD5()));
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

    private static <T> Function<Object, T> getter(Function<UploadPartResponse, T> g) {
        return obj -> g.apply((UploadPartResponse)((Object)((Object)obj)));
    }

    private static <T> BiConsumer<Object, T> setter(BiConsumer<Builder, T> s) {
        return (obj, val) -> s.accept((Builder)obj, val);
    }

    static final class BuilderImpl
    extends S3Response.BuilderImpl
    implements Builder {
        private String serverSideEncryption;
        private String eTag;
        private String checksumCRC32;
        private String checksumCRC32C;
        private String checksumSHA1;
        private String checksumSHA256;
        private String sseCustomerAlgorithm;
        private String sseCustomerKeyMD5;
        private String ssekmsKeyId;
        private Boolean bucketKeyEnabled;
        private String requestCharged;

        private BuilderImpl() {
        }

        private BuilderImpl(UploadPartResponse model) {
            super(model);
            this.serverSideEncryption(model.serverSideEncryption);
            this.eTag(model.eTag);
            this.checksumCRC32(model.checksumCRC32);
            this.checksumCRC32C(model.checksumCRC32C);
            this.checksumSHA1(model.checksumSHA1);
            this.checksumSHA256(model.checksumSHA256);
            this.sseCustomerAlgorithm(model.sseCustomerAlgorithm);
            this.sseCustomerKeyMD5(model.sseCustomerKeyMD5);
            this.ssekmsKeyId(model.ssekmsKeyId);
            this.bucketKeyEnabled(model.bucketKeyEnabled);
            this.requestCharged(model.requestCharged);
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

        public final String getSseCustomerAlgorithm() {
            return this.sseCustomerAlgorithm;
        }

        public final void setSseCustomerAlgorithm(String sseCustomerAlgorithm) {
            this.sseCustomerAlgorithm = sseCustomerAlgorithm;
        }

        @Override
        public final Builder sseCustomerAlgorithm(String sseCustomerAlgorithm) {
            this.sseCustomerAlgorithm = sseCustomerAlgorithm;
            return this;
        }

        public final String getSseCustomerKeyMD5() {
            return this.sseCustomerKeyMD5;
        }

        public final void setSseCustomerKeyMD5(String sseCustomerKeyMD5) {
            this.sseCustomerKeyMD5 = sseCustomerKeyMD5;
        }

        @Override
        public final Builder sseCustomerKeyMD5(String sseCustomerKeyMD5) {
            this.sseCustomerKeyMD5 = sseCustomerKeyMD5;
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
        public UploadPartResponse build() {
            return new UploadPartResponse(this);
        }

        public List<SdkField<?>> sdkFields() {
            return SDK_FIELDS;
        }
    }

    public static interface Builder
    extends S3Response.Builder,
    SdkPojo,
    CopyableBuilder<Builder, UploadPartResponse> {
        public Builder serverSideEncryption(String var1);

        public Builder serverSideEncryption(ServerSideEncryption var1);

        public Builder eTag(String var1);

        public Builder checksumCRC32(String var1);

        public Builder checksumCRC32C(String var1);

        public Builder checksumSHA1(String var1);

        public Builder checksumSHA256(String var1);

        public Builder sseCustomerAlgorithm(String var1);

        public Builder sseCustomerKeyMD5(String var1);

        public Builder ssekmsKeyId(String var1);

        public Builder bucketKeyEnabled(Boolean var1);

        public Builder requestCharged(String var1);

        public Builder requestCharged(RequestCharged var1);
    }
}

