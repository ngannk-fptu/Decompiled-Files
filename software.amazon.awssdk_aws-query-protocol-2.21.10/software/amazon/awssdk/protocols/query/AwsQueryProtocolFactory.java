/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkProtectedApi
 *  software.amazon.awssdk.awscore.AwsResponse
 *  software.amazon.awssdk.awscore.exception.AwsServiceException
 *  software.amazon.awssdk.core.SdkPojo
 *  software.amazon.awssdk.core.client.config.ClientOption
 *  software.amazon.awssdk.core.client.config.SdkClientConfiguration
 *  software.amazon.awssdk.core.client.config.SdkClientOption
 *  software.amazon.awssdk.core.http.HttpResponseHandler
 *  software.amazon.awssdk.core.http.MetricCollectingHttpResponseHandler
 *  software.amazon.awssdk.core.metrics.CoreMetric
 *  software.amazon.awssdk.http.SdkHttpFullRequest
 *  software.amazon.awssdk.metrics.SdkMetric
 *  software.amazon.awssdk.protocols.core.ExceptionMetadata
 *  software.amazon.awssdk.protocols.core.OperationInfo
 *  software.amazon.awssdk.protocols.core.ProtocolMarshaller
 */
package software.amazon.awssdk.protocols.query;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.awscore.AwsResponse;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.SdkPojo;
import software.amazon.awssdk.core.client.config.ClientOption;
import software.amazon.awssdk.core.client.config.SdkClientConfiguration;
import software.amazon.awssdk.core.client.config.SdkClientOption;
import software.amazon.awssdk.core.http.HttpResponseHandler;
import software.amazon.awssdk.core.http.MetricCollectingHttpResponseHandler;
import software.amazon.awssdk.core.metrics.CoreMetric;
import software.amazon.awssdk.http.SdkHttpFullRequest;
import software.amazon.awssdk.metrics.SdkMetric;
import software.amazon.awssdk.protocols.core.ExceptionMetadata;
import software.amazon.awssdk.protocols.core.OperationInfo;
import software.amazon.awssdk.protocols.core.ProtocolMarshaller;
import software.amazon.awssdk.protocols.query.internal.marshall.QueryProtocolMarshaller;
import software.amazon.awssdk.protocols.query.internal.unmarshall.AwsQueryResponseHandler;
import software.amazon.awssdk.protocols.query.internal.unmarshall.QueryProtocolUnmarshaller;
import software.amazon.awssdk.protocols.query.unmarshall.AwsXmlErrorProtocolUnmarshaller;
import software.amazon.awssdk.protocols.query.unmarshall.XmlElement;

@SdkProtectedApi
public class AwsQueryProtocolFactory {
    private final SdkClientConfiguration clientConfiguration;
    private final List<ExceptionMetadata> modeledExceptions;
    private final Supplier<SdkPojo> defaultServiceExceptionSupplier;
    private final MetricCollectingHttpResponseHandler<AwsServiceException> errorUnmarshaller;

    AwsQueryProtocolFactory(Builder<?> builder) {
        this.clientConfiguration = ((Builder)builder).clientConfiguration;
        this.modeledExceptions = Collections.unmodifiableList(((Builder)builder).modeledExceptions);
        this.defaultServiceExceptionSupplier = ((Builder)builder).defaultServiceExceptionSupplier;
        this.errorUnmarshaller = this.timeUnmarshalling(AwsXmlErrorProtocolUnmarshaller.builder().defaultExceptionSupplier(this.defaultServiceExceptionSupplier).exceptions(this.modeledExceptions).errorUnmarshaller(QueryProtocolUnmarshaller.builder().build()).errorRootExtractor(this::getErrorRoot).build());
    }

    public final ProtocolMarshaller<SdkHttpFullRequest> createProtocolMarshaller(OperationInfo operationInfo) {
        return QueryProtocolMarshaller.builder().endpoint((URI)this.clientConfiguration.option((ClientOption)SdkClientOption.ENDPOINT)).operationInfo(operationInfo).isEc2(this.isEc2()).build();
    }

    public final <T extends AwsResponse> HttpResponseHandler<T> createResponseHandler(Supplier<SdkPojo> pojoSupplier) {
        return this.timeUnmarshalling(new AwsQueryResponseHandler(QueryProtocolUnmarshaller.builder().hasResultWrapper(!this.isEc2()).build(), r -> (SdkPojo)pojoSupplier.get()));
    }

    public final HttpResponseHandler<AwsServiceException> createErrorResponseHandler() {
        return this.errorUnmarshaller;
    }

    private <T> MetricCollectingHttpResponseHandler<T> timeUnmarshalling(HttpResponseHandler<T> delegate) {
        return MetricCollectingHttpResponseHandler.create((SdkMetric)CoreMetric.UNMARSHALLING_DURATION, delegate);
    }

    Optional<XmlElement> getErrorRoot(XmlElement document) {
        return document.getOptionalElementByName("Error");
    }

    boolean isEc2() {
        return false;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder<SubclassT extends Builder> {
        private final List<ExceptionMetadata> modeledExceptions = new ArrayList<ExceptionMetadata>();
        private SdkClientConfiguration clientConfiguration;
        private Supplier<SdkPojo> defaultServiceExceptionSupplier;

        Builder() {
        }

        public final SubclassT clientConfiguration(SdkClientConfiguration clientConfiguration) {
            this.clientConfiguration = clientConfiguration;
            return this.getSubclass();
        }

        public final SubclassT registerModeledException(ExceptionMetadata errorMetadata) {
            this.modeledExceptions.add(errorMetadata);
            return this.getSubclass();
        }

        public final SubclassT defaultServiceExceptionSupplier(Supplier<SdkPojo> exceptionBuilderSupplier) {
            this.defaultServiceExceptionSupplier = exceptionBuilderSupplier;
            return this.getSubclass();
        }

        private SubclassT getSubclass() {
            return (SubclassT)this;
        }

        public AwsQueryProtocolFactory build() {
            return new AwsQueryProtocolFactory(this);
        }
    }
}

