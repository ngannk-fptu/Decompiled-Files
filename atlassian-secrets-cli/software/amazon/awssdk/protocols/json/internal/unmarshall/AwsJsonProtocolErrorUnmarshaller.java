/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.protocols.json.internal.unmarshall;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.awscore.exception.AwsErrorDetails;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.core.SdkPojo;
import software.amazon.awssdk.core.http.HttpResponseHandler;
import software.amazon.awssdk.core.interceptor.ExecutionAttributes;
import software.amazon.awssdk.core.interceptor.SdkExecutionAttribute;
import software.amazon.awssdk.http.SdkHttpFullResponse;
import software.amazon.awssdk.protocols.core.ExceptionMetadata;
import software.amazon.awssdk.protocols.json.ErrorCodeParser;
import software.amazon.awssdk.protocols.json.JsonContent;
import software.amazon.awssdk.protocols.json.internal.unmarshall.ErrorMessageParser;
import software.amazon.awssdk.protocols.json.internal.unmarshall.JsonProtocolUnmarshaller;
import software.amazon.awssdk.thirdparty.jackson.core.JsonFactory;
import software.amazon.awssdk.utils.StringUtils;

@SdkInternalApi
public final class AwsJsonProtocolErrorUnmarshaller
implements HttpResponseHandler<AwsServiceException> {
    private static final String QUERY_COMPATIBLE_ERRORCODE_DELIMITER = ";";
    private static final String X_AMZN_QUERY_ERROR = "x-amzn-query-error";
    private final JsonProtocolUnmarshaller jsonProtocolUnmarshaller;
    private final List<ExceptionMetadata> exceptions;
    private final ErrorMessageParser errorMessageParser;
    private final JsonFactory jsonFactory;
    private final Supplier<SdkPojo> defaultExceptionSupplier;
    private final ErrorCodeParser errorCodeParser;
    private final boolean hasAwsQueryCompatible;

    private AwsJsonProtocolErrorUnmarshaller(Builder builder) {
        this.jsonProtocolUnmarshaller = builder.jsonProtocolUnmarshaller;
        this.errorCodeParser = builder.errorCodeParser;
        this.errorMessageParser = builder.errorMessageParser;
        this.jsonFactory = builder.jsonFactory;
        this.defaultExceptionSupplier = builder.defaultExceptionSupplier;
        this.exceptions = builder.exceptions;
        this.hasAwsQueryCompatible = builder.hasAwsQueryCompatible;
    }

    @Override
    public AwsServiceException handle(SdkHttpFullResponse response, ExecutionAttributes executionAttributes) {
        return this.unmarshall(response, executionAttributes);
    }

    private AwsServiceException unmarshall(SdkHttpFullResponse response, ExecutionAttributes executionAttributes) {
        JsonContent jsonContent = JsonContent.createJsonContent(response, this.jsonFactory);
        String errorCode = this.errorCodeParser.parseErrorCode(response, jsonContent);
        Optional<ExceptionMetadata> modeledExceptionMetadata = this.exceptions.stream().filter(e -> e.errorCode().equals(errorCode)).findAny();
        SdkPojo sdkPojo = modeledExceptionMetadata.map(ExceptionMetadata::exceptionBuilderSupplier).orElse(this.defaultExceptionSupplier).get();
        AwsServiceException.Builder exception = ((AwsServiceException)this.jsonProtocolUnmarshaller.unmarshall(sdkPojo, response, jsonContent.getJsonNode())).toBuilder();
        String errorMessage = this.errorMessageParser.parseErrorMessage(response, jsonContent.getJsonNode());
        exception.awsErrorDetails(this.extractAwsErrorDetails(response, executionAttributes, jsonContent, this.getEffectiveErrorCode(response, errorCode), errorMessage));
        exception.clockSkew(this.getClockSkew(executionAttributes));
        exception.message(this.errorMessageForException(errorMessage, errorCode, response.statusCode()));
        exception.statusCode(this.statusCode(response, modeledExceptionMetadata));
        exception.requestId(response.firstMatchingHeader(X_AMZN_REQUEST_ID_HEADERS).orElse(null));
        exception.extendedRequestId(response.firstMatchingHeader("x-amz-id-2").orElse(null));
        return exception.build();
    }

    private String getEffectiveErrorCode(SdkHttpFullResponse response, String errorCode) {
        String compatibleErrorCode;
        if (this.hasAwsQueryCompatible && !StringUtils.isEmpty(compatibleErrorCode = this.queryCompatibleErrorCodeFromResponse(response))) {
            return compatibleErrorCode;
        }
        return errorCode;
    }

    private String queryCompatibleErrorCodeFromResponse(SdkHttpFullResponse response) {
        Optional<String> headerValue = response.firstMatchingHeader(X_AMZN_QUERY_ERROR);
        return headerValue.map(this::parseQueryErrorCodeFromDelimiter).orElse(null);
    }

    private String parseQueryErrorCodeFromDelimiter(String queryHeaderValue) {
        int delimiter = queryHeaderValue.indexOf(QUERY_COMPATIBLE_ERRORCODE_DELIMITER);
        if (delimiter > 0) {
            return queryHeaderValue.substring(0, delimiter);
        }
        return null;
    }

    private String errorMessageForException(String errorMessage, String errorCode, int statusCode) {
        if (StringUtils.isNotBlank(errorMessage)) {
            return errorMessage;
        }
        if (StringUtils.isNotBlank(errorCode)) {
            return "Service returned error code " + errorCode;
        }
        return "Service returned HTTP status code " + statusCode;
    }

    private Duration getClockSkew(ExecutionAttributes executionAttributes) {
        Integer timeOffset = executionAttributes.getAttribute(SdkExecutionAttribute.TIME_OFFSET);
        return timeOffset == null ? null : Duration.ofSeconds(timeOffset.intValue());
    }

    private int statusCode(SdkHttpFullResponse response, Optional<ExceptionMetadata> modeledExceptionMetadata) {
        if (response.statusCode() != 0) {
            return response.statusCode();
        }
        return modeledExceptionMetadata.filter(m -> m.httpStatusCode() != null).map(ExceptionMetadata::httpStatusCode).orElse(500);
    }

    private AwsErrorDetails extractAwsErrorDetails(SdkHttpFullResponse response, ExecutionAttributes executionAttributes, JsonContent jsonContent, String errorCode, String errorMessage) {
        AwsErrorDetails.Builder errorDetails = AwsErrorDetails.builder().errorCode(errorCode).serviceName(executionAttributes.getAttribute(SdkExecutionAttribute.SERVICE_NAME)).sdkHttpResponse(response);
        if (jsonContent.getRawContent() != null) {
            errorDetails.rawResponse(SdkBytes.fromByteArray(jsonContent.getRawContent()));
        }
        errorDetails.errorMessage(errorMessage);
        return errorDetails.build();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private JsonProtocolUnmarshaller jsonProtocolUnmarshaller;
        private List<ExceptionMetadata> exceptions;
        private ErrorMessageParser errorMessageParser;
        private JsonFactory jsonFactory;
        private Supplier<SdkPojo> defaultExceptionSupplier;
        private ErrorCodeParser errorCodeParser;
        private boolean hasAwsQueryCompatible;

        private Builder() {
        }

        public Builder jsonProtocolUnmarshaller(JsonProtocolUnmarshaller jsonProtocolUnmarshaller) {
            this.jsonProtocolUnmarshaller = jsonProtocolUnmarshaller;
            return this;
        }

        public Builder exceptions(List<ExceptionMetadata> exceptions) {
            this.exceptions = exceptions;
            return this;
        }

        public Builder errorMessageParser(ErrorMessageParser errorMessageParser) {
            this.errorMessageParser = errorMessageParser;
            return this;
        }

        public Builder jsonFactory(JsonFactory jsonFactory) {
            this.jsonFactory = jsonFactory;
            return this;
        }

        public Builder defaultExceptionSupplier(Supplier<SdkPojo> defaultExceptionSupplier) {
            this.defaultExceptionSupplier = defaultExceptionSupplier;
            return this;
        }

        public Builder errorCodeParser(ErrorCodeParser errorCodeParser) {
            this.errorCodeParser = errorCodeParser;
            return this;
        }

        public AwsJsonProtocolErrorUnmarshaller build() {
            return new AwsJsonProtocolErrorUnmarshaller(this);
        }

        public Builder hasAwsQueryCompatible(boolean hasAwsQueryCompatible) {
            this.hasAwsQueryCompatible = hasAwsQueryCompatible;
            return this;
        }
    }
}

