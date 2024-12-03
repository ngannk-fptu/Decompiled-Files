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
import software.amazon.awssdk.services.s3.model.CopyPartResult;
import software.amazon.awssdk.services.s3.model.RequestCharged;
import software.amazon.awssdk.services.s3.model.S3Response;
import software.amazon.awssdk.services.s3.model.ServerSideEncryption;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

public final class UploadPartCopyResponse
extends S3Response
implements ToCopyableBuilder<Builder, UploadPartCopyResponse> {
    private static final SdkField<String> COPY_SOURCE_VERSION_ID_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("CopySourceVersionId").getter(UploadPartCopyResponse.getter(UploadPartCopyResponse::copySourceVersionId)).setter(UploadPartCopyResponse.setter(Builder::copySourceVersionId)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("x-amz-copy-source-version-id").unmarshallLocationName("x-amz-copy-source-version-id").build()}).build();
    private static final SdkField<CopyPartResult> COPY_PART_RESULT_FIELD = SdkField.builder((MarshallingType)MarshallingType.SDK_POJO).memberName("CopyPartResult").getter(UploadPartCopyResponse.getter(UploadPartCopyResponse::copyPartResult)).setter(UploadPartCopyResponse.setter(Builder::copyPartResult)).constructor(CopyPartResult::builder).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("CopyPartResult").unmarshallLocationName("CopyPartResult").build(), PayloadTrait.create()}).build();
    private static final SdkField<String> SERVER_SIDE_ENCRYPTION_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("ServerSideEncryption").getter(UploadPartCopyResponse.getter(UploadPartCopyResponse::serverSideEncryptionAsString)).setter(UploadPartCopyResponse.setter(Builder::serverSideEncryption)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("x-amz-server-side-encryption").unmarshallLocationName("x-amz-server-side-encryption").build()}).build();
    private static final SdkField<String> SSE_CUSTOMER_ALGORITHM_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("SSECustomerAlgorithm").getter(UploadPartCopyResponse.getter(UploadPartCopyResponse::sseCustomerAlgorithm)).setter(UploadPartCopyResponse.setter(Builder::sseCustomerAlgorithm)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("x-amz-server-side-encryption-customer-algorithm").unmarshallLocationName("x-amz-server-side-encryption-customer-algorithm").build()}).build();
    private static final SdkField<String> SSE_CUSTOMER_KEY_MD5_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("SSECustomerKeyMD5").getter(UploadPartCopyResponse.getter(UploadPartCopyResponse::sseCustomerKeyMD5)).setter(UploadPartCopyResponse.setter(Builder::sseCustomerKeyMD5)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("x-amz-server-side-encryption-customer-key-MD5").unmarshallLocationName("x-amz-server-side-encryption-customer-key-MD5").build()}).build();
    private static final SdkField<String> SSEKMS_KEY_ID_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("SSEKMSKeyId").getter(UploadPartCopyResponse.getter(UploadPartCopyResponse::ssekmsKeyId)).setter(UploadPartCopyResponse.setter(Builder::ssekmsKeyId)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("x-amz-server-side-encryption-aws-kms-key-id").unmarshallLocationName("x-amz-server-side-encryption-aws-kms-key-id").build()}).build();
    private static final SdkField<Boolean> BUCKET_KEY_ENABLED_FIELD = SdkField.builder((MarshallingType)MarshallingType.BOOLEAN).memberName("BucketKeyEnabled").getter(UploadPartCopyResponse.getter(UploadPartCopyResponse::bucketKeyEnabled)).setter(UploadPartCopyResponse.setter(Builder::bucketKeyEnabled)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("x-amz-server-side-encryption-bucket-key-enabled").unmarshallLocationName("x-amz-server-side-encryption-bucket-key-enabled").build()}).build();
    private static final SdkField<String> REQUEST_CHARGED_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("RequestCharged").getter(UploadPartCopyResponse.getter(UploadPartCopyResponse::requestChargedAsString)).setter(UploadPartCopyResponse.setter(Builder::requestCharged)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("x-amz-request-charged").unmarshallLocationName("x-amz-request-charged").build()}).build();
    private static final List<SdkField<?>> SDK_FIELDS = Collections.unmodifiableList(Arrays.asList(COPY_SOURCE_VERSION_ID_FIELD, COPY_PART_RESULT_FIELD, SERVER_SIDE_ENCRYPTION_FIELD, SSE_CUSTOMER_ALGORITHM_FIELD, SSE_CUSTOMER_KEY_MD5_FIELD, SSEKMS_KEY_ID_FIELD, BUCKET_KEY_ENABLED_FIELD, REQUEST_CHARGED_FIELD));
    private final String copySourceVersionId;
    private final CopyPartResult copyPartResult;
    private final String serverSideEncryption;
    private final String sseCustomerAlgorithm;
    private final String sseCustomerKeyMD5;
    private final String ssekmsKeyId;
    private final Boolean bucketKeyEnabled;
    private final String requestCharged;

    private UploadPartCopyResponse(BuilderImpl builder) {
        super(builder);
        this.copySourceVersionId = builder.copySourceVersionId;
        this.copyPartResult = builder.copyPartResult;
        this.serverSideEncryption = builder.serverSideEncryption;
        this.sseCustomerAlgorithm = builder.sseCustomerAlgorithm;
        this.sseCustomerKeyMD5 = builder.sseCustomerKeyMD5;
        this.ssekmsKeyId = builder.ssekmsKeyId;
        this.bucketKeyEnabled = builder.bucketKeyEnabled;
        this.requestCharged = builder.requestCharged;
    }

    public final String copySourceVersionId() {
        return this.copySourceVersionId;
    }

    public final CopyPartResult copyPartResult() {
        return this.copyPartResult;
    }

    public final ServerSideEncryption serverSideEncryption() {
        return ServerSideEncryption.fromValue(this.serverSideEncryption);
    }

    public final String serverSideEncryptionAsString() {
        return this.serverSideEncryption;
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
        hashCode = 31 * hashCode + Objects.hashCode(this.copySourceVersionId());
        hashCode = 31 * hashCode + Objects.hashCode(this.copyPartResult());
        hashCode = 31 * hashCode + Objects.hashCode(this.serverSideEncryptionAsString());
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
        if (!(obj instanceof UploadPartCopyResponse)) {
            return false;
        }
        UploadPartCopyResponse other = (UploadPartCopyResponse)((Object)obj);
        return Objects.equals(this.copySourceVersionId(), other.copySourceVersionId()) && Objects.equals(this.copyPartResult(), other.copyPartResult()) && Objects.equals(this.serverSideEncryptionAsString(), other.serverSideEncryptionAsString()) && Objects.equals(this.sseCustomerAlgorithm(), other.sseCustomerAlgorithm()) && Objects.equals(this.sseCustomerKeyMD5(), other.sseCustomerKeyMD5()) && Objects.equals(this.ssekmsKeyId(), other.ssekmsKeyId()) && Objects.equals(this.bucketKeyEnabled(), other.bucketKeyEnabled()) && Objects.equals(this.requestChargedAsString(), other.requestChargedAsString());
    }

    public final String toString() {
        return ToString.builder((String)"UploadPartCopyResponse").add("CopySourceVersionId", (Object)this.copySourceVersionId()).add("CopyPartResult", (Object)this.copyPartResult()).add("ServerSideEncryption", (Object)this.serverSideEncryptionAsString()).add("SSECustomerAlgorithm", (Object)this.sseCustomerAlgorithm()).add("SSECustomerKeyMD5", (Object)this.sseCustomerKeyMD5()).add("SSEKMSKeyId", (Object)(this.ssekmsKeyId() == null ? null : "*** Sensitive Data Redacted ***")).add("BucketKeyEnabled", (Object)this.bucketKeyEnabled()).add("RequestCharged", (Object)this.requestChargedAsString()).build();
    }

    public final <T> Optional<T> getValueForField(String fieldName, Class<T> clazz) {
        switch (fieldName) {
            case "CopySourceVersionId": {
                return Optional.ofNullable(clazz.cast(this.copySourceVersionId()));
            }
            case "CopyPartResult": {
                return Optional.ofNullable(clazz.cast(this.copyPartResult()));
            }
            case "ServerSideEncryption": {
                return Optional.ofNullable(clazz.cast(this.serverSideEncryptionAsString()));
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

    private static <T> Function<Object, T> getter(Function<UploadPartCopyResponse, T> g) {
        return obj -> g.apply((UploadPartCopyResponse)((Object)((Object)obj)));
    }

    private static <T> BiConsumer<Object, T> setter(BiConsumer<Builder, T> s) {
        return (obj, val) -> s.accept((Builder)obj, val);
    }

    static final class BuilderImpl
    extends S3Response.BuilderImpl
    implements Builder {
        private String copySourceVersionId;
        private CopyPartResult copyPartResult;
        private String serverSideEncryption;
        private String sseCustomerAlgorithm;
        private String sseCustomerKeyMD5;
        private String ssekmsKeyId;
        private Boolean bucketKeyEnabled;
        private String requestCharged;

        private BuilderImpl() {
        }

        private BuilderImpl(UploadPartCopyResponse model) {
            super(model);
            this.copySourceVersionId(model.copySourceVersionId);
            this.copyPartResult(model.copyPartResult);
            this.serverSideEncryption(model.serverSideEncryption);
            this.sseCustomerAlgorithm(model.sseCustomerAlgorithm);
            this.sseCustomerKeyMD5(model.sseCustomerKeyMD5);
            this.ssekmsKeyId(model.ssekmsKeyId);
            this.bucketKeyEnabled(model.bucketKeyEnabled);
            this.requestCharged(model.requestCharged);
        }

        public final String getCopySourceVersionId() {
            return this.copySourceVersionId;
        }

        public final void setCopySourceVersionId(String copySourceVersionId) {
            this.copySourceVersionId = copySourceVersionId;
        }

        @Override
        public final Builder copySourceVersionId(String copySourceVersionId) {
            this.copySourceVersionId = copySourceVersionId;
            return this;
        }

        public final CopyPartResult.Builder getCopyPartResult() {
            return this.copyPartResult != null ? this.copyPartResult.toBuilder() : null;
        }

        public final void setCopyPartResult(CopyPartResult.BuilderImpl copyPartResult) {
            this.copyPartResult = copyPartResult != null ? copyPartResult.build() : null;
        }

        @Override
        public final Builder copyPartResult(CopyPartResult copyPartResult) {
            this.copyPartResult = copyPartResult;
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
        public UploadPartCopyResponse build() {
            return new UploadPartCopyResponse(this);
        }

        public List<SdkField<?>> sdkFields() {
            return SDK_FIELDS;
        }
    }

    public static interface Builder
    extends S3Response.Builder,
    SdkPojo,
    CopyableBuilder<Builder, UploadPartCopyResponse> {
        public Builder copySourceVersionId(String var1);

        public Builder copyPartResult(CopyPartResult var1);

        default public Builder copyPartResult(Consumer<CopyPartResult.Builder> copyPartResult) {
            return this.copyPartResult((CopyPartResult)((CopyPartResult.Builder)CopyPartResult.builder().applyMutation(copyPartResult)).build());
        }

        public Builder serverSideEncryption(String var1);

        public Builder serverSideEncryption(ServerSideEncryption var1);

        public Builder sseCustomerAlgorithm(String var1);

        public Builder sseCustomerKeyMD5(String var1);

        public Builder ssekmsKeyId(String var1);

        public Builder bucketKeyEnabled(Boolean var1);

        public Builder requestCharged(String var1);

        public Builder requestCharged(RequestCharged var1);
    }
}

