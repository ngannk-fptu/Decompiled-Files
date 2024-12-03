/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.protocols.json;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.annotations.SdkTestInternalApi;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.SdkPojo;
import software.amazon.awssdk.core.client.config.SdkClientConfiguration;
import software.amazon.awssdk.core.client.config.SdkClientOption;
import software.amazon.awssdk.core.http.HttpResponseHandler;
import software.amazon.awssdk.core.http.MetricCollectingHttpResponseHandler;
import software.amazon.awssdk.core.metrics.CoreMetric;
import software.amazon.awssdk.core.protocol.MarshallLocation;
import software.amazon.awssdk.core.traits.TimestampFormatTrait;
import software.amazon.awssdk.http.SdkHttpFullRequest;
import software.amazon.awssdk.http.SdkHttpFullResponse;
import software.amazon.awssdk.protocols.core.ExceptionMetadata;
import software.amazon.awssdk.protocols.core.OperationInfo;
import software.amazon.awssdk.protocols.core.ProtocolMarshaller;
import software.amazon.awssdk.protocols.json.AwsJsonProtocol;
import software.amazon.awssdk.protocols.json.AwsJsonProtocolMetadata;
import software.amazon.awssdk.protocols.json.DefaultJsonContentTypeResolver;
import software.amazon.awssdk.protocols.json.JsonContentTypeResolver;
import software.amazon.awssdk.protocols.json.JsonOperationMetadata;
import software.amazon.awssdk.protocols.json.StructuredJsonFactory;
import software.amazon.awssdk.protocols.json.StructuredJsonGenerator;
import software.amazon.awssdk.protocols.json.internal.AwsStructuredPlainJsonFactory;
import software.amazon.awssdk.protocols.json.internal.marshall.JsonProtocolMarshallerBuilder;
import software.amazon.awssdk.protocols.json.internal.unmarshall.AwsJsonErrorMessageParser;
import software.amazon.awssdk.protocols.json.internal.unmarshall.AwsJsonProtocolErrorUnmarshaller;
import software.amazon.awssdk.protocols.json.internal.unmarshall.AwsJsonResponseHandler;
import software.amazon.awssdk.protocols.json.internal.unmarshall.JsonProtocolUnmarshaller;
import software.amazon.awssdk.protocols.json.internal.unmarshall.JsonResponseHandler;
import software.amazon.awssdk.protocols.jsoncore.JsonNodeParser;

@SdkProtectedApi
public abstract class BaseAwsJsonProtocolFactory {
    protected static final JsonContentTypeResolver AWS_JSON = new DefaultJsonContentTypeResolver("application/x-amz-json-");
    private final AwsJsonProtocolMetadata protocolMetadata;
    private final List<ExceptionMetadata> modeledExceptions;
    private final Supplier<SdkPojo> defaultServiceExceptionSupplier;
    private final String customErrorCodeFieldName;
    private final boolean hasAwsQueryCompatible;
    private final SdkClientConfiguration clientConfiguration;
    private final JsonProtocolUnmarshaller protocolUnmarshaller;

    protected BaseAwsJsonProtocolFactory(Builder<?> builder) {
        this.protocolMetadata = ((Builder)builder).protocolMetadata.build();
        this.modeledExceptions = Collections.unmodifiableList(((Builder)builder).modeledExceptions);
        this.defaultServiceExceptionSupplier = ((Builder)builder).defaultServiceExceptionSupplier;
        this.customErrorCodeFieldName = ((Builder)builder).customErrorCodeFieldName;
        this.hasAwsQueryCompatible = ((Builder)builder).hasAwsQueryCompatible;
        this.clientConfiguration = ((Builder)builder).clientConfiguration;
        this.protocolUnmarshaller = JsonProtocolUnmarshaller.builder().parser(JsonNodeParser.builder().jsonFactory(this.getSdkFactory().getJsonFactory()).build()).defaultTimestampFormats(this.getDefaultTimestampFormats()).build();
    }

    public final <T extends SdkPojo> HttpResponseHandler<T> createResponseHandler(JsonOperationMetadata operationMetadata, Supplier<SdkPojo> pojoSupplier) {
        return this.createResponseHandler(operationMetadata, (SdkHttpFullResponse r) -> (SdkPojo)pojoSupplier.get());
    }

    public final <T extends SdkPojo> HttpResponseHandler<T> createResponseHandler(JsonOperationMetadata operationMetadata, Function<SdkHttpFullResponse, SdkPojo> pojoSupplier) {
        return this.timeUnmarshalling(new AwsJsonResponseHandler(new JsonResponseHandler(this.protocolUnmarshaller, pojoSupplier, operationMetadata.hasStreamingSuccessResponse(), operationMetadata.isPayloadJson())));
    }

    public final HttpResponseHandler<AwsServiceException> createErrorResponseHandler(JsonOperationMetadata errorResponseMetadata) {
        return this.timeUnmarshalling(AwsJsonProtocolErrorUnmarshaller.builder().jsonProtocolUnmarshaller(this.protocolUnmarshaller).exceptions(this.modeledExceptions).errorCodeParser(this.getSdkFactory().getErrorCodeParser(this.customErrorCodeFieldName)).hasAwsQueryCompatible(this.hasAwsQueryCompatible).errorMessageParser(AwsJsonErrorMessageParser.DEFAULT_ERROR_MESSAGE_PARSER).jsonFactory(this.getSdkFactory().getJsonFactory()).defaultExceptionSupplier(this.defaultServiceExceptionSupplier).build());
    }

    private <T> MetricCollectingHttpResponseHandler<T> timeUnmarshalling(HttpResponseHandler<T> delegate) {
        return MetricCollectingHttpResponseHandler.create(CoreMetric.UNMARSHALLING_DURATION, delegate);
    }

    private StructuredJsonGenerator createGenerator(OperationInfo operationInfo) {
        if (operationInfo.hasPayloadMembers() || this.protocolMetadata.protocol() == AwsJsonProtocol.AWS_JSON) {
            return this.createGenerator();
        }
        return StructuredJsonGenerator.NO_OP;
    }

    @SdkTestInternalApi
    private StructuredJsonGenerator createGenerator() {
        return this.getSdkFactory().createWriter(this.getContentType());
    }

    @SdkTestInternalApi
    public final String getContentType() {
        return this.protocolMetadata.contentType() != null ? this.protocolMetadata.contentType() : this.getContentTypeResolver().resolveContentType(this.protocolMetadata);
    }

    protected JsonContentTypeResolver getContentTypeResolver() {
        return AWS_JSON;
    }

    protected StructuredJsonFactory getSdkFactory() {
        return AwsStructuredPlainJsonFactory.SDK_JSON_FACTORY;
    }

    protected Map<MarshallLocation, TimestampFormatTrait.Format> getDefaultTimestampFormats() {
        EnumMap<MarshallLocation, TimestampFormatTrait.Format> formats = new EnumMap<MarshallLocation, TimestampFormatTrait.Format>(MarshallLocation.class);
        formats.put(MarshallLocation.HEADER, TimestampFormatTrait.Format.RFC_822);
        formats.put(MarshallLocation.PAYLOAD, TimestampFormatTrait.Format.UNIX_TIMESTAMP);
        return Collections.unmodifiableMap(formats);
    }

    public final ProtocolMarshaller<SdkHttpFullRequest> createProtocolMarshaller(OperationInfo operationInfo) {
        return JsonProtocolMarshallerBuilder.create().endpoint(this.clientConfiguration.option(SdkClientOption.ENDPOINT)).jsonGenerator(this.createGenerator(operationInfo)).contentType(this.getContentType()).operationInfo(operationInfo).sendExplicitNullForPayload(false).protocolMetadata(this.protocolMetadata).build();
    }

    public static abstract class Builder<SubclassT extends Builder> {
        private final AwsJsonProtocolMetadata.Builder protocolMetadata = AwsJsonProtocolMetadata.builder();
        private final List<ExceptionMetadata> modeledExceptions = new ArrayList<ExceptionMetadata>();
        private Supplier<SdkPojo> defaultServiceExceptionSupplier;
        private String customErrorCodeFieldName;
        private SdkClientConfiguration clientConfiguration;
        private boolean hasAwsQueryCompatible;

        protected Builder() {
        }

        public final SubclassT registerModeledException(ExceptionMetadata errorMetadata) {
            this.modeledExceptions.add(errorMetadata);
            return this.getSubclass();
        }

        public final SubclassT defaultServiceExceptionSupplier(Supplier<SdkPojo> exceptionBuilderSupplier) {
            this.defaultServiceExceptionSupplier = exceptionBuilderSupplier;
            return this.getSubclass();
        }

        public final SubclassT protocol(AwsJsonProtocol protocol) {
            this.protocolMetadata.protocol(protocol);
            return this.getSubclass();
        }

        public final SubclassT protocolVersion(String protocolVersion) {
            this.protocolMetadata.protocolVersion(protocolVersion);
            return this.getSubclass();
        }

        public final SubclassT contentType(String contentType) {
            this.protocolMetadata.contentType(contentType);
            return this.getSubclass();
        }

        public final SubclassT customErrorCodeFieldName(String customErrorCodeFieldName) {
            this.customErrorCodeFieldName = customErrorCodeFieldName;
            return this.getSubclass();
        }

        public final SubclassT clientConfiguration(SdkClientConfiguration clientConfiguration) {
            this.clientConfiguration = clientConfiguration;
            return this.getSubclass();
        }

        public final SubclassT hasAwsQueryCompatible(boolean hasAwsQueryCompatible) {
            this.hasAwsQueryCompatible = hasAwsQueryCompatible;
            return this.getSubclass();
        }

        private SubclassT getSubclass() {
            return (SubclassT)this;
        }
    }
}

