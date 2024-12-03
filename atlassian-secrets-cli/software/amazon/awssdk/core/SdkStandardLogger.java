/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.core;

import java.util.function.Supplier;
import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.core.http.HttpResponseHandler;
import software.amazon.awssdk.http.SdkHttpResponse;
import software.amazon.awssdk.utils.Logger;

@SdkProtectedApi
public final class SdkStandardLogger {
    public static final Logger REQUEST_LOGGER = Logger.loggerFor("software.amazon.awssdk.request");
    public static final Logger REQUEST_ID_LOGGER = Logger.loggerFor("software.amazon.awssdk.requestId");

    private SdkStandardLogger() {
    }

    public static void logRequestId(SdkHttpResponse response) {
        Supplier<String> logStatement = () -> {
            String placeholder = "not available";
            String requestId = "Request ID: " + response.firstMatchingHeader(HttpResponseHandler.X_AMZN_REQUEST_ID_HEADERS).orElse(placeholder) + ", Extended Request ID: " + response.firstMatchingHeader("x-amz-id-2").orElse(placeholder);
            String responseState = response.isSuccessful() ? "successful" : "failed";
            return "Received " + responseState + " response: " + response.statusCode() + ", " + requestId;
        };
        REQUEST_ID_LOGGER.debug(logStatement);
        REQUEST_LOGGER.debug(logStatement);
    }
}

