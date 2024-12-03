/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.awscore.AwsRequestOverrideConfiguration
 *  software.amazon.awssdk.awscore.AwsRequestOverrideConfiguration$Builder
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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import software.amazon.awssdk.awscore.AwsRequestOverrideConfiguration;
import software.amazon.awssdk.core.SdkField;
import software.amazon.awssdk.core.SdkPojo;
import software.amazon.awssdk.core.protocol.MarshallLocation;
import software.amazon.awssdk.core.protocol.MarshallingType;
import software.amazon.awssdk.core.traits.LocationTrait;
import software.amazon.awssdk.core.traits.RequiredTrait;
import software.amazon.awssdk.core.traits.Trait;
import software.amazon.awssdk.services.s3.model.ExpressionType;
import software.amazon.awssdk.services.s3.model.InputSerialization;
import software.amazon.awssdk.services.s3.model.OutputSerialization;
import software.amazon.awssdk.services.s3.model.RequestProgress;
import software.amazon.awssdk.services.s3.model.S3Request;
import software.amazon.awssdk.services.s3.model.ScanRange;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

public final class SelectObjectContentRequest
extends S3Request
implements ToCopyableBuilder<Builder, SelectObjectContentRequest> {
    private static final SdkField<String> BUCKET_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("Bucket").getter(SelectObjectContentRequest.getter(SelectObjectContentRequest::bucket)).setter(SelectObjectContentRequest.setter(Builder::bucket)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PATH).locationName("Bucket").unmarshallLocationName("Bucket").build(), RequiredTrait.create()}).build();
    private static final SdkField<String> KEY_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("Key").getter(SelectObjectContentRequest.getter(SelectObjectContentRequest::key)).setter(SelectObjectContentRequest.setter(Builder::key)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.GREEDY_PATH).locationName("Key").unmarshallLocationName("Key").build(), RequiredTrait.create()}).build();
    private static final SdkField<String> SSE_CUSTOMER_ALGORITHM_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("SSECustomerAlgorithm").getter(SelectObjectContentRequest.getter(SelectObjectContentRequest::sseCustomerAlgorithm)).setter(SelectObjectContentRequest.setter(Builder::sseCustomerAlgorithm)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("x-amz-server-side-encryption-customer-algorithm").unmarshallLocationName("x-amz-server-side-encryption-customer-algorithm").build()}).build();
    private static final SdkField<String> SSE_CUSTOMER_KEY_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("SSECustomerKey").getter(SelectObjectContentRequest.getter(SelectObjectContentRequest::sseCustomerKey)).setter(SelectObjectContentRequest.setter(Builder::sseCustomerKey)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("x-amz-server-side-encryption-customer-key").unmarshallLocationName("x-amz-server-side-encryption-customer-key").build()}).build();
    private static final SdkField<String> SSE_CUSTOMER_KEY_MD5_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("SSECustomerKeyMD5").getter(SelectObjectContentRequest.getter(SelectObjectContentRequest::sseCustomerKeyMD5)).setter(SelectObjectContentRequest.setter(Builder::sseCustomerKeyMD5)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("x-amz-server-side-encryption-customer-key-MD5").unmarshallLocationName("x-amz-server-side-encryption-customer-key-MD5").build()}).build();
    private static final SdkField<String> EXPRESSION_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("Expression").getter(SelectObjectContentRequest.getter(SelectObjectContentRequest::expression)).setter(SelectObjectContentRequest.setter(Builder::expression)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("Expression").unmarshallLocationName("Expression").build(), RequiredTrait.create()}).build();
    private static final SdkField<String> EXPRESSION_TYPE_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("ExpressionType").getter(SelectObjectContentRequest.getter(SelectObjectContentRequest::expressionTypeAsString)).setter(SelectObjectContentRequest.setter(Builder::expressionType)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("ExpressionType").unmarshallLocationName("ExpressionType").build(), RequiredTrait.create()}).build();
    private static final SdkField<RequestProgress> REQUEST_PROGRESS_FIELD = SdkField.builder((MarshallingType)MarshallingType.SDK_POJO).memberName("RequestProgress").getter(SelectObjectContentRequest.getter(SelectObjectContentRequest::requestProgress)).setter(SelectObjectContentRequest.setter(Builder::requestProgress)).constructor(RequestProgress::builder).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("RequestProgress").unmarshallLocationName("RequestProgress").build()}).build();
    private static final SdkField<InputSerialization> INPUT_SERIALIZATION_FIELD = SdkField.builder((MarshallingType)MarshallingType.SDK_POJO).memberName("InputSerialization").getter(SelectObjectContentRequest.getter(SelectObjectContentRequest::inputSerialization)).setter(SelectObjectContentRequest.setter(Builder::inputSerialization)).constructor(InputSerialization::builder).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("InputSerialization").unmarshallLocationName("InputSerialization").build(), RequiredTrait.create()}).build();
    private static final SdkField<OutputSerialization> OUTPUT_SERIALIZATION_FIELD = SdkField.builder((MarshallingType)MarshallingType.SDK_POJO).memberName("OutputSerialization").getter(SelectObjectContentRequest.getter(SelectObjectContentRequest::outputSerialization)).setter(SelectObjectContentRequest.setter(Builder::outputSerialization)).constructor(OutputSerialization::builder).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("OutputSerialization").unmarshallLocationName("OutputSerialization").build(), RequiredTrait.create()}).build();
    private static final SdkField<ScanRange> SCAN_RANGE_FIELD = SdkField.builder((MarshallingType)MarshallingType.SDK_POJO).memberName("ScanRange").getter(SelectObjectContentRequest.getter(SelectObjectContentRequest::scanRange)).setter(SelectObjectContentRequest.setter(Builder::scanRange)).constructor(ScanRange::builder).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("ScanRange").unmarshallLocationName("ScanRange").build()}).build();
    private static final SdkField<String> EXPECTED_BUCKET_OWNER_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("ExpectedBucketOwner").getter(SelectObjectContentRequest.getter(SelectObjectContentRequest::expectedBucketOwner)).setter(SelectObjectContentRequest.setter(Builder::expectedBucketOwner)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.HEADER).locationName("x-amz-expected-bucket-owner").unmarshallLocationName("x-amz-expected-bucket-owner").build()}).build();
    private static final List<SdkField<?>> SDK_FIELDS = Collections.unmodifiableList(Arrays.asList(BUCKET_FIELD, KEY_FIELD, SSE_CUSTOMER_ALGORITHM_FIELD, SSE_CUSTOMER_KEY_FIELD, SSE_CUSTOMER_KEY_MD5_FIELD, EXPRESSION_FIELD, EXPRESSION_TYPE_FIELD, REQUEST_PROGRESS_FIELD, INPUT_SERIALIZATION_FIELD, OUTPUT_SERIALIZATION_FIELD, SCAN_RANGE_FIELD, EXPECTED_BUCKET_OWNER_FIELD));
    private final String bucket;
    private final String key;
    private final String sseCustomerAlgorithm;
    private final String sseCustomerKey;
    private final String sseCustomerKeyMD5;
    private final String expression;
    private final String expressionType;
    private final RequestProgress requestProgress;
    private final InputSerialization inputSerialization;
    private final OutputSerialization outputSerialization;
    private final ScanRange scanRange;
    private final String expectedBucketOwner;

    private SelectObjectContentRequest(BuilderImpl builder) {
        super(builder);
        this.bucket = builder.bucket;
        this.key = builder.key;
        this.sseCustomerAlgorithm = builder.sseCustomerAlgorithm;
        this.sseCustomerKey = builder.sseCustomerKey;
        this.sseCustomerKeyMD5 = builder.sseCustomerKeyMD5;
        this.expression = builder.expression;
        this.expressionType = builder.expressionType;
        this.requestProgress = builder.requestProgress;
        this.inputSerialization = builder.inputSerialization;
        this.outputSerialization = builder.outputSerialization;
        this.scanRange = builder.scanRange;
        this.expectedBucketOwner = builder.expectedBucketOwner;
    }

    public final String bucket() {
        return this.bucket;
    }

    public final String key() {
        return this.key;
    }

    public final String sseCustomerAlgorithm() {
        return this.sseCustomerAlgorithm;
    }

    public final String sseCustomerKey() {
        return this.sseCustomerKey;
    }

    public final String sseCustomerKeyMD5() {
        return this.sseCustomerKeyMD5;
    }

    public final String expression() {
        return this.expression;
    }

    public final ExpressionType expressionType() {
        return ExpressionType.fromValue(this.expressionType);
    }

    public final String expressionTypeAsString() {
        return this.expressionType;
    }

    public final RequestProgress requestProgress() {
        return this.requestProgress;
    }

    public final InputSerialization inputSerialization() {
        return this.inputSerialization;
    }

    public final OutputSerialization outputSerialization() {
        return this.outputSerialization;
    }

    public final ScanRange scanRange() {
        return this.scanRange;
    }

    public final String expectedBucketOwner() {
        return this.expectedBucketOwner;
    }

    @Override
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
        hashCode = 31 * hashCode + Objects.hashCode(this.bucket());
        hashCode = 31 * hashCode + Objects.hashCode(this.key());
        hashCode = 31 * hashCode + Objects.hashCode(this.sseCustomerAlgorithm());
        hashCode = 31 * hashCode + Objects.hashCode(this.sseCustomerKey());
        hashCode = 31 * hashCode + Objects.hashCode(this.sseCustomerKeyMD5());
        hashCode = 31 * hashCode + Objects.hashCode(this.expression());
        hashCode = 31 * hashCode + Objects.hashCode(this.expressionTypeAsString());
        hashCode = 31 * hashCode + Objects.hashCode(this.requestProgress());
        hashCode = 31 * hashCode + Objects.hashCode(this.inputSerialization());
        hashCode = 31 * hashCode + Objects.hashCode(this.outputSerialization());
        hashCode = 31 * hashCode + Objects.hashCode(this.scanRange());
        hashCode = 31 * hashCode + Objects.hashCode(this.expectedBucketOwner());
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
        if (!(obj instanceof SelectObjectContentRequest)) {
            return false;
        }
        SelectObjectContentRequest other = (SelectObjectContentRequest)((Object)obj);
        return Objects.equals(this.bucket(), other.bucket()) && Objects.equals(this.key(), other.key()) && Objects.equals(this.sseCustomerAlgorithm(), other.sseCustomerAlgorithm()) && Objects.equals(this.sseCustomerKey(), other.sseCustomerKey()) && Objects.equals(this.sseCustomerKeyMD5(), other.sseCustomerKeyMD5()) && Objects.equals(this.expression(), other.expression()) && Objects.equals(this.expressionTypeAsString(), other.expressionTypeAsString()) && Objects.equals(this.requestProgress(), other.requestProgress()) && Objects.equals(this.inputSerialization(), other.inputSerialization()) && Objects.equals(this.outputSerialization(), other.outputSerialization()) && Objects.equals(this.scanRange(), other.scanRange()) && Objects.equals(this.expectedBucketOwner(), other.expectedBucketOwner());
    }

    public final String toString() {
        return ToString.builder((String)"SelectObjectContentRequest").add("Bucket", (Object)this.bucket()).add("Key", (Object)this.key()).add("SSECustomerAlgorithm", (Object)this.sseCustomerAlgorithm()).add("SSECustomerKey", (Object)(this.sseCustomerKey() == null ? null : "*** Sensitive Data Redacted ***")).add("SSECustomerKeyMD5", (Object)this.sseCustomerKeyMD5()).add("Expression", (Object)this.expression()).add("ExpressionType", (Object)this.expressionTypeAsString()).add("RequestProgress", (Object)this.requestProgress()).add("InputSerialization", (Object)this.inputSerialization()).add("OutputSerialization", (Object)this.outputSerialization()).add("ScanRange", (Object)this.scanRange()).add("ExpectedBucketOwner", (Object)this.expectedBucketOwner()).build();
    }

    public final <T> Optional<T> getValueForField(String fieldName, Class<T> clazz) {
        switch (fieldName) {
            case "Bucket": {
                return Optional.ofNullable(clazz.cast(this.bucket()));
            }
            case "Key": {
                return Optional.ofNullable(clazz.cast(this.key()));
            }
            case "SSECustomerAlgorithm": {
                return Optional.ofNullable(clazz.cast(this.sseCustomerAlgorithm()));
            }
            case "SSECustomerKey": {
                return Optional.ofNullable(clazz.cast(this.sseCustomerKey()));
            }
            case "SSECustomerKeyMD5": {
                return Optional.ofNullable(clazz.cast(this.sseCustomerKeyMD5()));
            }
            case "Expression": {
                return Optional.ofNullable(clazz.cast(this.expression()));
            }
            case "ExpressionType": {
                return Optional.ofNullable(clazz.cast(this.expressionTypeAsString()));
            }
            case "RequestProgress": {
                return Optional.ofNullable(clazz.cast(this.requestProgress()));
            }
            case "InputSerialization": {
                return Optional.ofNullable(clazz.cast(this.inputSerialization()));
            }
            case "OutputSerialization": {
                return Optional.ofNullable(clazz.cast(this.outputSerialization()));
            }
            case "ScanRange": {
                return Optional.ofNullable(clazz.cast(this.scanRange()));
            }
            case "ExpectedBucketOwner": {
                return Optional.ofNullable(clazz.cast(this.expectedBucketOwner()));
            }
        }
        return Optional.empty();
    }

    public final List<SdkField<?>> sdkFields() {
        return SDK_FIELDS;
    }

    private static <T> Function<Object, T> getter(Function<SelectObjectContentRequest, T> g) {
        return obj -> g.apply((SelectObjectContentRequest)((Object)((Object)obj)));
    }

    private static <T> BiConsumer<Object, T> setter(BiConsumer<Builder, T> s) {
        return (obj, val) -> s.accept((Builder)obj, val);
    }

    static final class BuilderImpl
    extends S3Request.BuilderImpl
    implements Builder {
        private String bucket;
        private String key;
        private String sseCustomerAlgorithm;
        private String sseCustomerKey;
        private String sseCustomerKeyMD5;
        private String expression;
        private String expressionType;
        private RequestProgress requestProgress;
        private InputSerialization inputSerialization;
        private OutputSerialization outputSerialization;
        private ScanRange scanRange;
        private String expectedBucketOwner;

        private BuilderImpl() {
        }

        private BuilderImpl(SelectObjectContentRequest model) {
            super(model);
            this.bucket(model.bucket);
            this.key(model.key);
            this.sseCustomerAlgorithm(model.sseCustomerAlgorithm);
            this.sseCustomerKey(model.sseCustomerKey);
            this.sseCustomerKeyMD5(model.sseCustomerKeyMD5);
            this.expression(model.expression);
            this.expressionType(model.expressionType);
            this.requestProgress(model.requestProgress);
            this.inputSerialization(model.inputSerialization);
            this.outputSerialization(model.outputSerialization);
            this.scanRange(model.scanRange);
            this.expectedBucketOwner(model.expectedBucketOwner);
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

        public final String getSseCustomerKey() {
            return this.sseCustomerKey;
        }

        public final void setSseCustomerKey(String sseCustomerKey) {
            this.sseCustomerKey = sseCustomerKey;
        }

        @Override
        public final Builder sseCustomerKey(String sseCustomerKey) {
            this.sseCustomerKey = sseCustomerKey;
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

        public final String getExpression() {
            return this.expression;
        }

        public final void setExpression(String expression) {
            this.expression = expression;
        }

        @Override
        public final Builder expression(String expression) {
            this.expression = expression;
            return this;
        }

        public final String getExpressionType() {
            return this.expressionType;
        }

        public final void setExpressionType(String expressionType) {
            this.expressionType = expressionType;
        }

        @Override
        public final Builder expressionType(String expressionType) {
            this.expressionType = expressionType;
            return this;
        }

        @Override
        public final Builder expressionType(ExpressionType expressionType) {
            this.expressionType(expressionType == null ? null : expressionType.toString());
            return this;
        }

        public final RequestProgress.Builder getRequestProgress() {
            return this.requestProgress != null ? this.requestProgress.toBuilder() : null;
        }

        public final void setRequestProgress(RequestProgress.BuilderImpl requestProgress) {
            this.requestProgress = requestProgress != null ? requestProgress.build() : null;
        }

        @Override
        public final Builder requestProgress(RequestProgress requestProgress) {
            this.requestProgress = requestProgress;
            return this;
        }

        public final InputSerialization.Builder getInputSerialization() {
            return this.inputSerialization != null ? this.inputSerialization.toBuilder() : null;
        }

        public final void setInputSerialization(InputSerialization.BuilderImpl inputSerialization) {
            this.inputSerialization = inputSerialization != null ? inputSerialization.build() : null;
        }

        @Override
        public final Builder inputSerialization(InputSerialization inputSerialization) {
            this.inputSerialization = inputSerialization;
            return this;
        }

        public final OutputSerialization.Builder getOutputSerialization() {
            return this.outputSerialization != null ? this.outputSerialization.toBuilder() : null;
        }

        public final void setOutputSerialization(OutputSerialization.BuilderImpl outputSerialization) {
            this.outputSerialization = outputSerialization != null ? outputSerialization.build() : null;
        }

        @Override
        public final Builder outputSerialization(OutputSerialization outputSerialization) {
            this.outputSerialization = outputSerialization;
            return this;
        }

        public final ScanRange.Builder getScanRange() {
            return this.scanRange != null ? this.scanRange.toBuilder() : null;
        }

        public final void setScanRange(ScanRange.BuilderImpl scanRange) {
            this.scanRange = scanRange != null ? scanRange.build() : null;
        }

        @Override
        public final Builder scanRange(ScanRange scanRange) {
            this.scanRange = scanRange;
            return this;
        }

        public final String getExpectedBucketOwner() {
            return this.expectedBucketOwner;
        }

        public final void setExpectedBucketOwner(String expectedBucketOwner) {
            this.expectedBucketOwner = expectedBucketOwner;
        }

        @Override
        public final Builder expectedBucketOwner(String expectedBucketOwner) {
            this.expectedBucketOwner = expectedBucketOwner;
            return this;
        }

        @Override
        public Builder overrideConfiguration(AwsRequestOverrideConfiguration overrideConfiguration) {
            super.overrideConfiguration(overrideConfiguration);
            return this;
        }

        @Override
        public Builder overrideConfiguration(Consumer<AwsRequestOverrideConfiguration.Builder> builderConsumer) {
            super.overrideConfiguration(builderConsumer);
            return this;
        }

        @Override
        public SelectObjectContentRequest build() {
            return new SelectObjectContentRequest(this);
        }

        public List<SdkField<?>> sdkFields() {
            return SDK_FIELDS;
        }
    }

    public static interface Builder
    extends S3Request.Builder,
    SdkPojo,
    CopyableBuilder<Builder, SelectObjectContentRequest> {
        public Builder bucket(String var1);

        public Builder key(String var1);

        public Builder sseCustomerAlgorithm(String var1);

        public Builder sseCustomerKey(String var1);

        public Builder sseCustomerKeyMD5(String var1);

        public Builder expression(String var1);

        public Builder expressionType(String var1);

        public Builder expressionType(ExpressionType var1);

        public Builder requestProgress(RequestProgress var1);

        default public Builder requestProgress(Consumer<RequestProgress.Builder> requestProgress) {
            return this.requestProgress((RequestProgress)((RequestProgress.Builder)RequestProgress.builder().applyMutation(requestProgress)).build());
        }

        public Builder inputSerialization(InputSerialization var1);

        default public Builder inputSerialization(Consumer<InputSerialization.Builder> inputSerialization) {
            return this.inputSerialization((InputSerialization)((InputSerialization.Builder)InputSerialization.builder().applyMutation(inputSerialization)).build());
        }

        public Builder outputSerialization(OutputSerialization var1);

        default public Builder outputSerialization(Consumer<OutputSerialization.Builder> outputSerialization) {
            return this.outputSerialization((OutputSerialization)((OutputSerialization.Builder)OutputSerialization.builder().applyMutation(outputSerialization)).build());
        }

        public Builder scanRange(ScanRange var1);

        default public Builder scanRange(Consumer<ScanRange.Builder> scanRange) {
            return this.scanRange((ScanRange)((ScanRange.Builder)ScanRange.builder().applyMutation(scanRange)).build());
        }

        public Builder expectedBucketOwner(String var1);

        public Builder overrideConfiguration(AwsRequestOverrideConfiguration var1);

        public Builder overrideConfiguration(Consumer<AwsRequestOverrideConfiguration.Builder> var1);
    }
}

