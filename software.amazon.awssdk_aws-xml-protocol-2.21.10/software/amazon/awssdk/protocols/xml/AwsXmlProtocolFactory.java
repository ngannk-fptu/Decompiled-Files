/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkProtectedApi
 *  software.amazon.awssdk.awscore.AwsResponse
 *  software.amazon.awssdk.awscore.exception.AwsServiceException
 *  software.amazon.awssdk.core.Response
 *  software.amazon.awssdk.core.SdkPojo
 *  software.amazon.awssdk.core.client.config.ClientOption
 *  software.amazon.awssdk.core.client.config.SdkClientConfiguration
 *  software.amazon.awssdk.core.client.config.SdkClientOption
 *  software.amazon.awssdk.core.http.HttpResponseHandler
 *  software.amazon.awssdk.core.http.MetricCollectingHttpResponseHandler
 *  software.amazon.awssdk.core.internal.http.CombinedResponseHandler
 *  software.amazon.awssdk.core.metrics.CoreMetric
 *  software.amazon.awssdk.http.SdkHttpFullRequest
 *  software.amazon.awssdk.http.SdkHttpFullResponse
 *  software.amazon.awssdk.metrics.SdkMetric
 *  software.amazon.awssdk.protocols.core.ExceptionMetadata
 *  software.amazon.awssdk.protocols.core.OperationInfo
 *  software.amazon.awssdk.protocols.core.OperationMetadataAttribute
 *  software.amazon.awssdk.protocols.core.ProtocolMarshaller
 *  software.amazon.awssdk.protocols.query.unmarshall.AwsXmlErrorProtocolUnmarshaller
 *  software.amazon.awssdk.protocols.query.unmarshall.XmlElement
 *  software.amazon.awssdk.protocols.query.unmarshall.XmlErrorUnmarshaller
 */
package software.amazon.awssdk.protocols.xml;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.awscore.AwsResponse;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.Response;
import software.amazon.awssdk.core.SdkPojo;
import software.amazon.awssdk.core.client.config.ClientOption;
import software.amazon.awssdk.core.client.config.SdkClientConfiguration;
import software.amazon.awssdk.core.client.config.SdkClientOption;
import software.amazon.awssdk.core.http.HttpResponseHandler;
import software.amazon.awssdk.core.http.MetricCollectingHttpResponseHandler;
import software.amazon.awssdk.core.internal.http.CombinedResponseHandler;
import software.amazon.awssdk.core.metrics.CoreMetric;
import software.amazon.awssdk.http.SdkHttpFullRequest;
import software.amazon.awssdk.http.SdkHttpFullResponse;
import software.amazon.awssdk.metrics.SdkMetric;
import software.amazon.awssdk.protocols.core.ExceptionMetadata;
import software.amazon.awssdk.protocols.core.OperationInfo;
import software.amazon.awssdk.protocols.core.OperationMetadataAttribute;
import software.amazon.awssdk.protocols.core.ProtocolMarshaller;
import software.amazon.awssdk.protocols.query.unmarshall.AwsXmlErrorProtocolUnmarshaller;
import software.amazon.awssdk.protocols.query.unmarshall.XmlElement;
import software.amazon.awssdk.protocols.query.unmarshall.XmlErrorUnmarshaller;
import software.amazon.awssdk.protocols.xml.XmlOperationMetadata;
import software.amazon.awssdk.protocols.xml.internal.marshall.XmlGenerator;
import software.amazon.awssdk.protocols.xml.internal.marshall.XmlProtocolMarshaller;
import software.amazon.awssdk.protocols.xml.internal.unmarshall.AwsXmlErrorTransformer;
import software.amazon.awssdk.protocols.xml.internal.unmarshall.AwsXmlResponseHandler;
import software.amazon.awssdk.protocols.xml.internal.unmarshall.AwsXmlResponseTransformer;
import software.amazon.awssdk.protocols.xml.internal.unmarshall.AwsXmlUnmarshallingContext;
import software.amazon.awssdk.protocols.xml.internal.unmarshall.XmlProtocolUnmarshaller;
import software.amazon.awssdk.protocols.xml.internal.unmarshall.XmlResponseHandler;

@SdkProtectedApi
public class AwsXmlProtocolFactory {
    public static final OperationMetadataAttribute<String> XML_NAMESPACE_ATTRIBUTE = new OperationMetadataAttribute(String.class);
    public static final OperationMetadataAttribute<String> ROOT_MARSHALL_LOCATION_ATTRIBUTE = new OperationMetadataAttribute(String.class);
    private static final XmlProtocolUnmarshaller XML_PROTOCOL_UNMARSHALLER = XmlProtocolUnmarshaller.create();
    private final List<ExceptionMetadata> modeledExceptions;
    private final Supplier<SdkPojo> defaultServiceExceptionSupplier;
    private final HttpResponseHandler<AwsServiceException> errorUnmarshaller;
    private final SdkClientConfiguration clientConfiguration;

    AwsXmlProtocolFactory(Builder<?> builder) {
        this.modeledExceptions = Collections.unmodifiableList(((Builder)builder).modeledExceptions);
        this.defaultServiceExceptionSupplier = ((Builder)builder).defaultServiceExceptionSupplier;
        this.clientConfiguration = ((Builder)builder).clientConfiguration;
        this.errorUnmarshaller = this.timeUnmarshalling((HttpResponseHandler)AwsXmlErrorProtocolUnmarshaller.builder().defaultExceptionSupplier(this.defaultServiceExceptionSupplier).exceptions(this.modeledExceptions).errorUnmarshaller((XmlErrorUnmarshaller)XML_PROTOCOL_UNMARSHALLER).errorRootExtractor(this::getErrorRoot).build());
    }

    public ProtocolMarshaller<SdkHttpFullRequest> createProtocolMarshaller(OperationInfo operationInfo) {
        return XmlProtocolMarshaller.builder().endpoint((URI)this.clientConfiguration.option((ClientOption)SdkClientOption.ENDPOINT)).xmlGenerator(this.createGenerator(operationInfo)).operationInfo(operationInfo).build();
    }

    public <T extends SdkPojo> HttpResponseHandler<T> createResponseHandler(Supplier<SdkPojo> pojoSupplier, XmlOperationMetadata staxOperationMetadata) {
        return this.createResponseHandler((SdkHttpFullResponse r) -> (SdkPojo)pojoSupplier.get(), staxOperationMetadata);
    }

    public <T extends SdkPojo> HttpResponseHandler<T> createResponseHandler(Function<SdkHttpFullResponse, SdkPojo> pojoSupplier, XmlOperationMetadata staxOperationMetadata) {
        return this.timeUnmarshalling(new AwsXmlResponseHandler(new XmlResponseHandler(XML_PROTOCOL_UNMARSHALLER, pojoSupplier, staxOperationMetadata.isHasStreamingSuccessResponse())));
    }

    protected <T extends AwsResponse> Function<AwsXmlUnmarshallingContext, T> createResponseTransformer(Supplier<SdkPojo> pojoSupplier) {
        return new AwsXmlResponseTransformer(XML_PROTOCOL_UNMARSHALLER, r -> (SdkPojo)pojoSupplier.get());
    }

    protected Function<AwsXmlUnmarshallingContext, AwsServiceException> createErrorTransformer() {
        return AwsXmlErrorTransformer.builder().defaultExceptionSupplier(this.defaultServiceExceptionSupplier).exceptions(this.modeledExceptions).errorUnmarshaller(XML_PROTOCOL_UNMARSHALLER).build();
    }

    public HttpResponseHandler<AwsServiceException> createErrorResponseHandler() {
        return this.errorUnmarshaller;
    }

    private <T> MetricCollectingHttpResponseHandler<T> timeUnmarshalling(HttpResponseHandler<T> delegate) {
        return MetricCollectingHttpResponseHandler.create((SdkMetric)CoreMetric.UNMARSHALLING_DURATION, delegate);
    }

    public <T extends AwsResponse> HttpResponseHandler<Response<T>> createCombinedResponseHandler(Supplier<SdkPojo> pojoSupplier, XmlOperationMetadata staxOperationMetadata) {
        return new CombinedResponseHandler(this.createResponseHandler(pojoSupplier, staxOperationMetadata), this.createErrorResponseHandler());
    }

    Optional<XmlElement> getErrorRoot(XmlElement document) {
        return document.getOptionalElementByName("Error");
    }

    private XmlGenerator createGenerator(OperationInfo operationInfo) {
        return operationInfo.hasPayloadMembers() ? XmlGenerator.create((String)operationInfo.addtionalMetadata(XML_NAMESPACE_ATTRIBUTE)) : null;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder<SubclassT extends Builder> {
        private final List<ExceptionMetadata> modeledExceptions = new ArrayList<ExceptionMetadata>();
        private Supplier<SdkPojo> defaultServiceExceptionSupplier;
        private SdkClientConfiguration clientConfiguration;

        Builder() {
        }

        public final SubclassT registerModeledException(ExceptionMetadata errorMetadata) {
            this.modeledExceptions.add(errorMetadata);
            return this.getSubclass();
        }

        public SubclassT defaultServiceExceptionSupplier(Supplier<SdkPojo> exceptionBuilderSupplier) {
            this.defaultServiceExceptionSupplier = exceptionBuilderSupplier;
            return this.getSubclass();
        }

        public SubclassT clientConfiguration(SdkClientConfiguration clientConfiguration) {
            this.clientConfiguration = clientConfiguration;
            return this.getSubclass();
        }

        private SubclassT getSubclass() {
            return (SubclassT)this;
        }

        public AwsXmlProtocolFactory build() {
            return new AwsXmlProtocolFactory(this);
        }
    }
}

