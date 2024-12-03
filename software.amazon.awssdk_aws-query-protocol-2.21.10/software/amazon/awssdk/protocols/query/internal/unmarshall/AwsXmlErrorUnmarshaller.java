/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.awscore.AwsExecutionAttribute
 *  software.amazon.awssdk.awscore.exception.AwsErrorDetails
 *  software.amazon.awssdk.awscore.exception.AwsServiceException
 *  software.amazon.awssdk.awscore.exception.AwsServiceException$Builder
 *  software.amazon.awssdk.core.SdkBytes
 *  software.amazon.awssdk.core.SdkPojo
 *  software.amazon.awssdk.core.http.HttpResponseHandler
 *  software.amazon.awssdk.core.interceptor.ExecutionAttributes
 *  software.amazon.awssdk.core.interceptor.SdkExecutionAttribute
 *  software.amazon.awssdk.http.SdkHttpFullResponse
 *  software.amazon.awssdk.http.SdkHttpResponse
 *  software.amazon.awssdk.protocols.core.ExceptionMetadata
 *  software.amazon.awssdk.utils.FunctionalUtils
 */
package software.amazon.awssdk.protocols.query.internal.unmarshall;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.awscore.AwsExecutionAttribute;
import software.amazon.awssdk.awscore.exception.AwsErrorDetails;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.core.SdkPojo;
import software.amazon.awssdk.core.http.HttpResponseHandler;
import software.amazon.awssdk.core.interceptor.ExecutionAttributes;
import software.amazon.awssdk.core.interceptor.SdkExecutionAttribute;
import software.amazon.awssdk.http.SdkHttpFullResponse;
import software.amazon.awssdk.http.SdkHttpResponse;
import software.amazon.awssdk.protocols.core.ExceptionMetadata;
import software.amazon.awssdk.protocols.query.unmarshall.XmlElement;
import software.amazon.awssdk.protocols.query.unmarshall.XmlErrorUnmarshaller;
import software.amazon.awssdk.utils.FunctionalUtils;

@SdkInternalApi
public final class AwsXmlErrorUnmarshaller {
    private static final String X_AMZ_ID_2_HEADER = "x-amz-id-2";
    private final List<ExceptionMetadata> exceptions;
    private final Supplier<SdkPojo> defaultExceptionSupplier;
    private final XmlErrorUnmarshaller errorUnmarshaller;

    private AwsXmlErrorUnmarshaller(Builder builder) {
        this.exceptions = builder.exceptions;
        this.errorUnmarshaller = builder.errorUnmarshaller;
        this.defaultExceptionSupplier = builder.defaultExceptionSupplier;
    }

    public static Builder builder() {
        return new Builder();
    }

    public AwsServiceException unmarshall(XmlElement documentRoot, Optional<XmlElement> errorRoot, Optional<SdkBytes> documentBytes, SdkHttpFullResponse response, ExecutionAttributes executionAttributes) {
        String errorCode = this.getErrorCode(errorRoot);
        AwsServiceException.Builder builder = errorRoot.map(e -> (AwsServiceException.Builder)FunctionalUtils.invokeSafely(() -> this.unmarshallFromErrorCode(response, (XmlElement)e, errorCode))).orElseGet(this::defaultException);
        AwsErrorDetails awsErrorDetails = AwsErrorDetails.builder().errorCode(errorCode).errorMessage(builder.message()).rawResponse((SdkBytes)documentBytes.orElse(null)).sdkHttpResponse((SdkHttpResponse)response).serviceName((String)executionAttributes.getAttribute(AwsExecutionAttribute.SERVICE_NAME)).build();
        builder.requestId(this.getRequestId(response, documentRoot)).extendedRequestId(this.getExtendedRequestId(response)).statusCode(response.statusCode()).clockSkew(this.getClockSkew(executionAttributes)).awsErrorDetails(awsErrorDetails);
        return builder.build();
    }

    private Duration getClockSkew(ExecutionAttributes executionAttributes) {
        Integer timeOffset = (Integer)executionAttributes.getAttribute(SdkExecutionAttribute.TIME_OFFSET);
        return timeOffset == null ? null : Duration.ofSeconds(timeOffset.intValue());
    }

    private AwsServiceException.Builder defaultException() {
        return (AwsServiceException.Builder)this.defaultExceptionSupplier.get();
    }

    private AwsServiceException.Builder unmarshallFromErrorCode(SdkHttpFullResponse response, XmlElement errorRoot, String errorCode) {
        SdkPojo sdkPojo = this.exceptions.stream().filter(e -> e.errorCode().equals(errorCode)).map(ExceptionMetadata::exceptionBuilderSupplier).findAny().orElse(this.defaultExceptionSupplier).get();
        AwsServiceException.Builder builder = ((AwsServiceException)this.errorUnmarshaller.unmarshall(sdkPojo, errorRoot, response)).toBuilder();
        builder.message(this.getMessage(errorRoot));
        return builder;
    }

    private String getErrorCode(Optional<XmlElement> errorRoot) {
        return errorRoot.map(e -> e.getOptionalElementByName("Code").map(XmlElement::textContent).orElse(null)).orElse(null);
    }

    private String getMessage(XmlElement errorRoot) {
        return errorRoot.getOptionalElementByName("Message").map(XmlElement::textContent).orElse(null);
    }

    private String getRequestId(SdkHttpFullResponse response, XmlElement document) {
        XmlElement requestId = document.getOptionalElementByName("RequestId").orElseGet(() -> document.getElementByName("RequestID"));
        return requestId != null ? requestId.textContent() : this.matchRequestIdHeaders(response);
    }

    private String matchRequestIdHeaders(SdkHttpFullResponse response) {
        return HttpResponseHandler.X_AMZN_REQUEST_ID_HEADERS.stream().map(h -> response.firstMatchingHeader(h)).filter(Optional::isPresent).map(Optional::get).findFirst().orElse(null);
    }

    private String getExtendedRequestId(SdkHttpFullResponse response) {
        return response.firstMatchingHeader(X_AMZ_ID_2_HEADER).orElse(null);
    }

    public static final class Builder {
        private List<ExceptionMetadata> exceptions;
        private Supplier<SdkPojo> defaultExceptionSupplier;
        private XmlErrorUnmarshaller errorUnmarshaller;

        private Builder() {
        }

        public Builder exceptions(List<ExceptionMetadata> exceptions) {
            this.exceptions = exceptions;
            return this;
        }

        public Builder defaultExceptionSupplier(Supplier<SdkPojo> defaultExceptionSupplier) {
            this.defaultExceptionSupplier = defaultExceptionSupplier;
            return this;
        }

        public Builder errorUnmarshaller(XmlErrorUnmarshaller errorUnmarshaller) {
            this.errorUnmarshaller = errorUnmarshaller;
            return this;
        }

        public AwsXmlErrorUnmarshaller build() {
            return new AwsXmlErrorUnmarshaller(this);
        }
    }
}

