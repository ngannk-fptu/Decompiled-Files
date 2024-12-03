/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  software.amazon.awssdk.annotations.SdkProtectedApi
 *  software.amazon.awssdk.core.exception.SdkClientException
 *  software.amazon.awssdk.core.exception.SdkServiceException
 *  software.amazon.awssdk.protocols.jsoncore.JsonNode
 *  software.amazon.awssdk.protocols.jsoncore.JsonNodeParser
 *  software.amazon.awssdk.utils.IoUtils
 */
package software.amazon.awssdk.regions.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.exception.SdkServiceException;
import software.amazon.awssdk.protocols.jsoncore.JsonNode;
import software.amazon.awssdk.protocols.jsoncore.JsonNodeParser;
import software.amazon.awssdk.regions.internal.util.ConnectionUtils;
import software.amazon.awssdk.regions.util.ResourcesEndpointProvider;
import software.amazon.awssdk.regions.util.ResourcesEndpointRetryParameters;
import software.amazon.awssdk.utils.IoUtils;

@SdkProtectedApi
public final class HttpResourcesUtils {
    private static final Logger log = LoggerFactory.getLogger(HttpResourcesUtils.class);
    private static final JsonNodeParser JSON_PARSER = JsonNode.parser();
    private static volatile HttpResourcesUtils instance;
    private final ConnectionUtils connectionUtils;

    private HttpResourcesUtils() {
        this(ConnectionUtils.create());
    }

    HttpResourcesUtils(ConnectionUtils connectionUtils) {
        this.connectionUtils = connectionUtils;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public static HttpResourcesUtils instance() {
        if (instance != null) return instance;
        Class<HttpResourcesUtils> clazz = HttpResourcesUtils.class;
        synchronized (HttpResourcesUtils.class) {
            if (instance != null) return instance;
            instance = new HttpResourcesUtils();
            // ** MonitorExit[var0] (shouldn't be in output)
            return instance;
        }
    }

    public String readResource(URI endpoint) throws IOException {
        return this.readResource(() -> endpoint, "GET");
    }

    public String readResource(ResourcesEndpointProvider endpointProvider) throws IOException {
        return this.readResource(endpointProvider, "GET");
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public String readResource(ResourcesEndpointProvider endpointProvider, String method) throws IOException {
        int retriesAttempted = 0;
        InputStream inputStream = null;
        while (true) {
            block9: {
                int statusCode;
                HttpURLConnection connection;
                block8: {
                    connection = this.connectionUtils.connectToEndpoint(endpointProvider.endpoint(), endpointProvider.headers(), method);
                    statusCode = connection.getResponseCode();
                    if (statusCode != 200) break block8;
                    inputStream = connection.getInputStream();
                    String string = IoUtils.toUtf8String((InputStream)inputStream);
                    IoUtils.closeQuietly((AutoCloseable)inputStream, (Logger)log);
                    return string;
                }
                try {
                    if (statusCode == 404) {
                        throw SdkClientException.builder().message("The requested metadata is not found at " + connection.getURL()).build();
                    }
                    if (endpointProvider.retryPolicy().shouldRetry(retriesAttempted++, ResourcesEndpointRetryParameters.builder().withStatusCode(statusCode).build())) break block9;
                    inputStream = connection.getErrorStream();
                    this.handleErrorResponse(inputStream, statusCode, connection.getResponseMessage());
                }
                catch (IOException ioException) {
                    try {
                        if (!endpointProvider.retryPolicy().shouldRetry(retriesAttempted++, ResourcesEndpointRetryParameters.builder().withException(ioException).build())) {
                            throw ioException;
                        }
                        log.debug("An IOException occurred when connecting to endpoint: {} \n Retrying to connect again", (Object)endpointProvider.endpoint());
                    }
                    catch (Throwable throwable) {
                        IoUtils.closeQuietly(inputStream, (Logger)log);
                        throw throwable;
                    }
                    IoUtils.closeQuietly((AutoCloseable)inputStream, (Logger)log);
                    continue;
                }
            }
            IoUtils.closeQuietly((AutoCloseable)inputStream, (Logger)log);
            continue;
            break;
        }
    }

    private void handleErrorResponse(InputStream errorStream, int statusCode, String responseMessage) throws IOException {
        if (errorStream != null) {
            String errorResponse = IoUtils.toUtf8String((InputStream)errorStream);
            try {
                Optional message = JSON_PARSER.parse(errorResponse).field("message");
                if (message.isPresent()) {
                    responseMessage = ((JsonNode)message.get()).text();
                }
            }
            catch (RuntimeException exception) {
                log.debug("Unable to parse error stream", (Throwable)exception);
            }
        }
        throw SdkServiceException.builder().message(responseMessage).statusCode(statusCode).build();
    }
}

