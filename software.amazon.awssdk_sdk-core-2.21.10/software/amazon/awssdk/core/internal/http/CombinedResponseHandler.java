/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.http.SdkHttpFullResponse
 *  software.amazon.awssdk.http.SdkHttpResponse
 *  software.amazon.awssdk.utils.IoUtils
 */
package software.amazon.awssdk.core.internal.http;

import java.io.IOException;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.Response;
import software.amazon.awssdk.core.SdkStandardLogger;
import software.amazon.awssdk.core.exception.RetryableException;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.core.http.HttpResponseHandler;
import software.amazon.awssdk.core.interceptor.ExecutionAttributes;
import software.amazon.awssdk.http.SdkHttpFullResponse;
import software.amazon.awssdk.http.SdkHttpResponse;
import software.amazon.awssdk.utils.IoUtils;

@SdkInternalApi
public class CombinedResponseHandler<OutputT>
implements HttpResponseHandler<Response<OutputT>> {
    private static final Logger log = LoggerFactory.getLogger(CombinedResponseHandler.class);
    private final HttpResponseHandler<OutputT> successResponseHandler;
    private final HttpResponseHandler<? extends SdkException> errorResponseHandler;

    public CombinedResponseHandler(HttpResponseHandler<OutputT> successResponseHandler, HttpResponseHandler<? extends SdkException> errorResponseHandler) {
        this.successResponseHandler = successResponseHandler;
        this.errorResponseHandler = errorResponseHandler;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Response<OutputT> handle(SdkHttpFullResponse httpResponse, ExecutionAttributes executionAttributes) throws Exception {
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

    private Response<OutputT> handleResponse(SdkHttpFullResponse httpResponse, ExecutionAttributes executionAttributes) throws IOException, InterruptedException {
        SdkStandardLogger.logRequestId((SdkHttpResponse)httpResponse);
        if (httpResponse.isSuccessful()) {
            OutputT response = this.handleSuccessResponse(httpResponse, executionAttributes);
            return Response.builder().httpResponse(httpResponse).response(response).isSuccess(true).build();
        }
        return Response.builder().httpResponse(httpResponse).exception(this.handleErrorResponse(httpResponse, executionAttributes)).isSuccess(false).build();
    }

    private OutputT handleSuccessResponse(SdkHttpFullResponse httpResponse, ExecutionAttributes executionAttributes) throws IOException, InterruptedException {
        try {
            return this.successResponseHandler.handle(httpResponse, executionAttributes);
        }
        catch (IOException | InterruptedException | RetryableException e) {
            throw e;
        }
        catch (Exception e) {
            if (e instanceof SdkException && ((SdkException)e).retryable()) {
                throw (SdkException)e;
            }
            String errorMessage = "Unable to unmarshall response (" + e.getMessage() + "). Response Code: " + httpResponse.statusCode() + ", Response Text: " + (String)httpResponse.statusText().orElse(null);
            throw SdkClientException.builder().message(errorMessage).cause(e).build();
        }
    }

    private SdkException handleErrorResponse(SdkHttpFullResponse httpResponse, ExecutionAttributes executionAttributes) throws IOException, InterruptedException {
        try {
            SdkException exception = this.errorResponseHandler.handle(httpResponse, executionAttributes);
            exception.fillInStackTrace();
            return exception;
        }
        catch (IOException | InterruptedException e) {
            throw e;
        }
        catch (Exception e) {
            String errorMessage = String.format("Unable to unmarshall error response (%s). Response Code: %d, Response Text: %s", e.getMessage(), httpResponse.statusCode(), httpResponse.statusText().orElse("null"));
            throw SdkClientException.builder().message(errorMessage).cause(e).build();
        }
    }

    private void closeInputStreamIfNeeded(SdkHttpFullResponse httpResponse, boolean didRequestFail) {
        if (didRequestFail || !this.successResponseHandler.needsConnectionLeftOpen()) {
            Optional.ofNullable(httpResponse).flatMap(SdkHttpFullResponse::content).ifPresent(s -> IoUtils.closeQuietly((AutoCloseable)s, (Logger)log));
        }
    }
}

