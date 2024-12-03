/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.core.client.handler;

import java.net.URI;
import software.amazon.awssdk.annotations.NotThreadSafe;
import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.core.CredentialType;
import software.amazon.awssdk.core.Response;
import software.amazon.awssdk.core.SdkProtocolMetadata;
import software.amazon.awssdk.core.SdkRequest;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.core.client.config.SdkClientConfiguration;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.core.http.HttpResponseHandler;
import software.amazon.awssdk.core.interceptor.ExecutionAttribute;
import software.amazon.awssdk.core.interceptor.ExecutionAttributes;
import software.amazon.awssdk.core.runtime.transform.Marshaller;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.metrics.MetricCollector;

@SdkProtectedApi
@NotThreadSafe
public final class ClientExecutionParams<InputT extends SdkRequest, OutputT> {
    private InputT input;
    private RequestBody requestBody;
    private AsyncRequestBody asyncRequestBody;
    private Marshaller<InputT> marshaller;
    private HttpResponseHandler<OutputT> responseHandler;
    private HttpResponseHandler<? extends SdkException> errorResponseHandler;
    private HttpResponseHandler<Response<OutputT>> combinedResponseHandler;
    private boolean fullDuplex;
    private boolean hasInitialRequestEvent;
    private String hostPrefixExpression;
    private String operationName;
    private SdkProtocolMetadata protocolMetadata;
    private URI discoveredEndpoint;
    private CredentialType credentialType;
    private MetricCollector metricCollector;
    private final ExecutionAttributes attributes = new ExecutionAttributes();
    private SdkClientConfiguration requestConfiguration;

    public Marshaller<InputT> getMarshaller() {
        return this.marshaller;
    }

    public ClientExecutionParams<InputT, OutputT> withMarshaller(Marshaller<InputT> marshaller) {
        this.marshaller = marshaller;
        return this;
    }

    public InputT getInput() {
        return this.input;
    }

    public ClientExecutionParams<InputT, OutputT> withInput(InputT input) {
        this.input = input;
        return this;
    }

    public HttpResponseHandler<OutputT> getResponseHandler() {
        return this.responseHandler;
    }

    public ClientExecutionParams<InputT, OutputT> withResponseHandler(HttpResponseHandler<OutputT> responseHandler) {
        this.responseHandler = responseHandler;
        return this;
    }

    public HttpResponseHandler<? extends SdkException> getErrorResponseHandler() {
        return this.errorResponseHandler;
    }

    public ClientExecutionParams<InputT, OutputT> withErrorResponseHandler(HttpResponseHandler<? extends SdkException> errorResponseHandler) {
        this.errorResponseHandler = errorResponseHandler;
        return this;
    }

    public HttpResponseHandler<Response<OutputT>> getCombinedResponseHandler() {
        return this.combinedResponseHandler;
    }

    public ClientExecutionParams<InputT, OutputT> withCombinedResponseHandler(HttpResponseHandler<Response<OutputT>> combinedResponseHandler) {
        this.combinedResponseHandler = combinedResponseHandler;
        return this;
    }

    public RequestBody getRequestBody() {
        return this.requestBody;
    }

    public ClientExecutionParams<InputT, OutputT> withRequestBody(RequestBody requestBody) {
        this.requestBody = requestBody;
        return this;
    }

    public AsyncRequestBody getAsyncRequestBody() {
        return this.asyncRequestBody;
    }

    public ClientExecutionParams<InputT, OutputT> withAsyncRequestBody(AsyncRequestBody asyncRequestBody) {
        this.asyncRequestBody = asyncRequestBody;
        return this;
    }

    public boolean isFullDuplex() {
        return this.fullDuplex;
    }

    public ClientExecutionParams<InputT, OutputT> withFullDuplex(boolean fullDuplex) {
        this.fullDuplex = fullDuplex;
        return this;
    }

    public boolean hasInitialRequestEvent() {
        return this.hasInitialRequestEvent;
    }

    public ClientExecutionParams<InputT, OutputT> withInitialRequestEvent(boolean hasInitialRequestEvent) {
        this.hasInitialRequestEvent = hasInitialRequestEvent;
        return this;
    }

    public String getOperationName() {
        return this.operationName;
    }

    public ClientExecutionParams<InputT, OutputT> withOperationName(String operationName) {
        this.operationName = operationName;
        return this;
    }

    public SdkProtocolMetadata getProtocolMetadata() {
        return this.protocolMetadata;
    }

    public ClientExecutionParams<InputT, OutputT> withProtocolMetadata(SdkProtocolMetadata protocolMetadata) {
        this.protocolMetadata = protocolMetadata;
        return this;
    }

    public String hostPrefixExpression() {
        return this.hostPrefixExpression;
    }

    public ClientExecutionParams<InputT, OutputT> hostPrefixExpression(String hostPrefixExpression) {
        this.hostPrefixExpression = hostPrefixExpression;
        return this;
    }

    public URI discoveredEndpoint() {
        return this.discoveredEndpoint;
    }

    public ClientExecutionParams<InputT, OutputT> discoveredEndpoint(URI discoveredEndpoint) {
        this.discoveredEndpoint = discoveredEndpoint;
        return this;
    }

    public CredentialType credentialType() {
        return this.credentialType;
    }

    public ClientExecutionParams<InputT, OutputT> credentialType(CredentialType credentialType) {
        this.credentialType = credentialType;
        return this;
    }

    public ClientExecutionParams<InputT, OutputT> withMetricCollector(MetricCollector metricCollector) {
        this.metricCollector = metricCollector;
        return this;
    }

    public <T> ClientExecutionParams<InputT, OutputT> putExecutionAttribute(ExecutionAttribute<T> attribute, T value) {
        this.attributes.putAttribute(attribute, value);
        return this;
    }

    public ExecutionAttributes executionAttributes() {
        return this.attributes;
    }

    public MetricCollector getMetricCollector() {
        return this.metricCollector;
    }

    public SdkClientConfiguration requestConfiguration() {
        return this.requestConfiguration;
    }

    public <T> ClientExecutionParams<InputT, OutputT> withRequestConfiguration(SdkClientConfiguration requestConfiguration) {
        this.requestConfiguration = requestConfiguration;
        return this;
    }
}

