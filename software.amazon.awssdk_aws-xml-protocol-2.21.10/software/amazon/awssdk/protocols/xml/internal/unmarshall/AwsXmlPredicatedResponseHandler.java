/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.core.Response
 *  software.amazon.awssdk.core.SdkPojo
 *  software.amazon.awssdk.core.SdkStandardLogger
 *  software.amazon.awssdk.core.exception.RetryableException
 *  software.amazon.awssdk.core.exception.SdkClientException
 *  software.amazon.awssdk.core.exception.SdkException
 *  software.amazon.awssdk.core.http.HttpResponseHandler
 *  software.amazon.awssdk.core.interceptor.ExecutionAttributes
 *  software.amazon.awssdk.http.SdkHttpFullResponse
 *  software.amazon.awssdk.http.SdkHttpResponse
 *  software.amazon.awssdk.protocols.query.unmarshall.XmlElement
 *  software.amazon.awssdk.utils.IoUtils
 */
package software.amazon.awssdk.protocols.xml.internal.unmarshall;

import java.util.Optional;
import java.util.function.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.Response;
import software.amazon.awssdk.core.SdkPojo;
import software.amazon.awssdk.core.SdkStandardLogger;
import software.amazon.awssdk.core.exception.RetryableException;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.core.http.HttpResponseHandler;
import software.amazon.awssdk.core.interceptor.ExecutionAttributes;
import software.amazon.awssdk.http.SdkHttpFullResponse;
import software.amazon.awssdk.http.SdkHttpResponse;
import software.amazon.awssdk.protocols.query.unmarshall.XmlElement;
import software.amazon.awssdk.protocols.xml.internal.unmarshall.AwsXmlUnmarshallingContext;
import software.amazon.awssdk.protocols.xml.internal.unmarshall.XmlResponseParserUtils;
import software.amazon.awssdk.utils.IoUtils;

@SdkInternalApi
public class AwsXmlPredicatedResponseHandler<OutputT>
implements HttpResponseHandler<Response<OutputT>> {
    private static final Logger log = LoggerFactory.getLogger(AwsXmlPredicatedResponseHandler.class);
    private final Function<SdkHttpFullResponse, SdkPojo> pojoSupplier;
    private final Function<AwsXmlUnmarshallingContext, OutputT> successResponseTransformer;
    private final Function<AwsXmlUnmarshallingContext, ? extends SdkException> errorResponseTransformer;
    private final Function<AwsXmlUnmarshallingContext, AwsXmlUnmarshallingContext> decorateContextWithError;
    private final boolean needsConnectionLeftOpen;

    public AwsXmlPredicatedResponseHandler(Function<SdkHttpFullResponse, SdkPojo> pojoSupplier, Function<AwsXmlUnmarshallingContext, OutputT> successResponseTransformer, Function<AwsXmlUnmarshallingContext, ? extends SdkException> errorResponseTransformer, Function<AwsXmlUnmarshallingContext, AwsXmlUnmarshallingContext> decorateContextWithError, boolean needsConnectionLeftOpen) {
        this.pojoSupplier = pojoSupplier;
        this.successResponseTransformer = successResponseTransformer;
        this.errorResponseTransformer = errorResponseTransformer;
        this.decorateContextWithError = decorateContextWithError;
        this.needsConnectionLeftOpen = needsConnectionLeftOpen;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Response<OutputT> handle(SdkHttpFullResponse httpResponse, ExecutionAttributes executionAttributes) {
        boolean didRequestFail = true;
        try {
            Response<OutputT> response = this.handleResponse(httpResponse, executionAttributes);
            didRequestFail = response.isSuccess() == false;
            Response<OutputT> response2 = response;
            return response2;
        }
        finally {
            this.closeInputStreamIfNeeded(httpResponse, didRequestFail);
        }
    }

    private Response<OutputT> handleResponse(SdkHttpFullResponse httpResponse, ExecutionAttributes executionAttributes) {
        AwsXmlUnmarshallingContext parsedResponse = this.parseResponse(httpResponse, executionAttributes);
        parsedResponse = this.decorateContextWithError.apply(parsedResponse);
        SdkStandardLogger.logRequestId((SdkHttpResponse)httpResponse);
        if (parsedResponse.isResponseSuccess().booleanValue()) {
            OutputT response = this.handleSuccessResponse(parsedResponse);
            return Response.builder().httpResponse(httpResponse).response(response).isSuccess(Boolean.valueOf(true)).build();
        }
        return Response.builder().httpResponse(httpResponse).exception(this.handleErrorResponse(parsedResponse)).isSuccess(Boolean.valueOf(false)).build();
    }

    private AwsXmlUnmarshallingContext parseResponse(SdkHttpFullResponse httpFullResponse, ExecutionAttributes executionAttributes) {
        XmlElement document = XmlResponseParserUtils.parse(this.pojoSupplier.apply(httpFullResponse), httpFullResponse);
        return AwsXmlUnmarshallingContext.builder().parsedXml(document).executionAttributes(executionAttributes).sdkHttpFullResponse(httpFullResponse).build();
    }

    private OutputT handleSuccessResponse(AwsXmlUnmarshallingContext parsedResponse) {
        try {
            return this.successResponseTransformer.apply(parsedResponse);
        }
        catch (RetryableException e) {
            throw e;
        }
        catch (Exception e) {
            if (e instanceof SdkException && ((SdkException)e).retryable()) {
                throw (SdkException)e;
            }
            String errorMessage = "Unable to unmarshall response (" + e.getMessage() + "). Response Code: " + parsedResponse.sdkHttpFullResponse().statusCode() + ", Response Text: " + (String)parsedResponse.sdkHttpFullResponse().statusText().orElse(null);
            throw SdkClientException.builder().message(errorMessage).cause((Throwable)e).build();
        }
    }

    private SdkException handleErrorResponse(AwsXmlUnmarshallingContext parsedResponse) {
        try {
            SdkException exception = this.errorResponseTransformer.apply(parsedResponse);
            exception.fillInStackTrace();
            return exception;
        }
        catch (Exception e) {
            String errorMessage = String.format("Unable to unmarshall error response (%s). Response Code: %d, Response Text: %s", e.getMessage(), parsedResponse.sdkHttpFullResponse().statusCode(), parsedResponse.sdkHttpFullResponse().statusText().orElse("null"));
            throw SdkClientException.builder().message(errorMessage).cause((Throwable)e).build();
        }
    }

    private void closeInputStreamIfNeeded(SdkHttpFullResponse httpResponse, boolean didRequestFail) {
        if (didRequestFail || !this.needsConnectionLeftOpen) {
            Optional.ofNullable(httpResponse).flatMap(SdkHttpFullResponse::content).ifPresent(s -> IoUtils.closeQuietly((AutoCloseable)s, (Logger)log));
        }
    }
}

