/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.http.AbortableInputStream
 *  software.amazon.awssdk.http.SdkHttpFullRequest
 *  software.amazon.awssdk.http.SdkHttpFullResponse
 *  software.amazon.awssdk.http.SdkHttpRequest
 *  software.amazon.awssdk.metrics.MetricCollector
 */
package software.amazon.awssdk.core.internal.handler;

import java.util.Optional;
import java.util.function.Supplier;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.Response;
import software.amazon.awssdk.core.SdkRequest;
import software.amazon.awssdk.core.SdkResponse;
import software.amazon.awssdk.core.client.config.SdkClientConfiguration;
import software.amazon.awssdk.core.client.handler.ClientExecutionParams;
import software.amazon.awssdk.core.client.handler.SyncClientHandler;
import software.amazon.awssdk.core.exception.AbortedException;
import software.amazon.awssdk.core.exception.NonRetryableException;
import software.amazon.awssdk.core.exception.RetryableException;
import software.amazon.awssdk.core.http.ExecutionContext;
import software.amazon.awssdk.core.http.HttpResponseHandler;
import software.amazon.awssdk.core.interceptor.ExecutionAttributes;
import software.amazon.awssdk.core.interceptor.InterceptorContext;
import software.amazon.awssdk.core.internal.handler.BaseClientHandler;
import software.amazon.awssdk.core.internal.http.AmazonSyncHttpClient;
import software.amazon.awssdk.core.internal.http.CombinedResponseHandler;
import software.amazon.awssdk.core.internal.http.InterruptMonitor;
import software.amazon.awssdk.core.metrics.CoreMetric;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.http.AbortableInputStream;
import software.amazon.awssdk.http.SdkHttpFullRequest;
import software.amazon.awssdk.http.SdkHttpFullResponse;
import software.amazon.awssdk.http.SdkHttpRequest;
import software.amazon.awssdk.metrics.MetricCollector;

@SdkInternalApi
public abstract class BaseSyncClientHandler
extends BaseClientHandler
implements SyncClientHandler {
    private final AmazonSyncHttpClient client;

    protected BaseSyncClientHandler(SdkClientConfiguration clientConfiguration, AmazonSyncHttpClient client) {
        super(clientConfiguration);
        this.client = client;
    }

    @Override
    public <InputT extends SdkRequest, OutputT extends SdkResponse, ReturnT> ReturnT execute(ClientExecutionParams<InputT, OutputT> executionParams, ResponseTransformer<OutputT, ReturnT> responseTransformer) {
        return (ReturnT)this.measureApiCallSuccess(executionParams, () -> {
            ExecutionContext executionContext = this.invokeInterceptorsAndCreateExecutionContext(executionParams);
            CombinedResponseHandler streamingCombinedResponseHandler = this.createStreamingCombinedResponseHandler(executionParams, responseTransformer, executionContext);
            return this.doExecute(executionParams, executionContext, streamingCombinedResponseHandler);
        });
    }

    @Override
    public <InputT extends SdkRequest, OutputT extends SdkResponse> OutputT execute(ClientExecutionParams<InputT, OutputT> executionParams) {
        return (OutputT)this.measureApiCallSuccess(executionParams, () -> {
            ExecutionContext executionContext = this.invokeInterceptorsAndCreateExecutionContext(executionParams);
            HttpResponseHandler combinedResponseHandler = this.createCombinedResponseHandler(executionParams, executionContext);
            return (SdkResponse)this.doExecute(executionParams, executionContext, combinedResponseHandler);
        });
    }

    public void close() {
        this.client.close();
    }

    private <OutputT> OutputT invoke(SdkClientConfiguration clientConfiguration, SdkHttpFullRequest request, SdkRequest originalRequest, ExecutionContext executionContext, HttpResponseHandler<Response<OutputT>> responseHandler) {
        return this.client.requestExecutionBuilder().request(request).originalRequest(originalRequest).executionContext(executionContext).httpClientDependencies(c -> c.clientConfiguration(clientConfiguration)).execute(responseHandler);
    }

    private <InputT extends SdkRequest, OutputT extends SdkResponse, ReturnT> CombinedResponseHandler<ReturnT> createStreamingCombinedResponseHandler(ClientExecutionParams<InputT, OutputT> executionParams, ResponseTransformer<OutputT, ReturnT> responseTransformer, ExecutionContext executionContext) {
        if (executionParams.getCombinedResponseHandler() != null) {
            throw new IllegalArgumentException("A streaming 'responseTransformer' may not be used when a 'combinedResponseHandler' has been specified in a ClientExecutionParams object.");
        }
        HttpResponseHandler<OutputT> decoratedResponseHandlers = this.decorateResponseHandlers(executionParams.getResponseHandler(), executionContext);
        HttpResponseHandlerAdapter httpResponseHandler = new HttpResponseHandlerAdapter(decoratedResponseHandlers, responseTransformer);
        return new CombinedResponseHandler(httpResponseHandler, executionParams.getErrorResponseHandler());
    }

    private <InputT extends SdkRequest, OutputT extends SdkResponse> HttpResponseHandler<Response<OutputT>> createCombinedResponseHandler(ClientExecutionParams<InputT, OutputT> executionParams, ExecutionContext executionContext) {
        HttpResponseHandler<Response<OutputT>> combinedResponseHandler;
        BaseSyncClientHandler.validateCombinedResponseHandler(executionParams);
        if (executionParams.getCombinedResponseHandler() != null) {
            combinedResponseHandler = this.decorateSuccessResponseHandlers(executionParams.getCombinedResponseHandler(), executionContext);
        } else {
            HttpResponseHandler<OutputT> decoratedResponseHandlers = this.decorateResponseHandlers(executionParams.getResponseHandler(), executionContext);
            combinedResponseHandler = new CombinedResponseHandler<OutputT>(decoratedResponseHandlers, executionParams.getErrorResponseHandler());
        }
        return combinedResponseHandler;
    }

    private <InputT extends SdkRequest, OutputT, ReturnT> ReturnT doExecute(ClientExecutionParams<InputT, OutputT> executionParams, ExecutionContext executionContext, HttpResponseHandler<Response<ReturnT>> responseHandler) {
        SdkRequest inputT = executionContext.interceptorContext().request();
        InterceptorContext sdkHttpFullRequestContext = BaseSyncClientHandler.finalizeSdkHttpFullRequest(executionParams, executionContext, inputT, this.resolveRequestConfiguration(executionParams));
        SdkHttpFullRequest marshalled = (SdkHttpFullRequest)sdkHttpFullRequestContext.httpRequest();
        this.validateSigningConfiguration((SdkHttpRequest)marshalled, executionContext.signer());
        Optional<RequestBody> requestBody = sdkHttpFullRequestContext.requestBody();
        if (requestBody.isPresent()) {
            marshalled = marshalled.toBuilder().contentStreamProvider(requestBody.get().contentStreamProvider()).build();
        }
        SdkClientConfiguration clientConfiguration = this.resolveRequestConfiguration(executionParams);
        return (ReturnT)this.invoke(clientConfiguration, marshalled, inputT, executionContext, responseHandler);
    }

    private <T> T measureApiCallSuccess(ClientExecutionParams<?, ?> executionParams, Supplier<T> thingToMeasureSuccessOf) {
        try {
            T result = thingToMeasureSuccessOf.get();
            this.reportApiCallSuccess(executionParams, true);
            return result;
        }
        catch (Exception e) {
            this.reportApiCallSuccess(executionParams, false);
            throw e;
        }
    }

    private void reportApiCallSuccess(ClientExecutionParams<?, ?> executionParams, boolean value) {
        MetricCollector metricCollector = executionParams.getMetricCollector();
        if (metricCollector != null) {
            metricCollector.reportMetric(CoreMetric.API_CALL_SUCCESSFUL, (Object)value);
        }
    }

    private static class HttpResponseHandlerAdapter<ReturnT, OutputT extends SdkResponse>
    implements HttpResponseHandler<ReturnT> {
        private final HttpResponseHandler<OutputT> httpResponseHandler;
        private final ResponseTransformer<OutputT, ReturnT> responseTransformer;

        private HttpResponseHandlerAdapter(HttpResponseHandler<OutputT> httpResponseHandler, ResponseTransformer<OutputT, ReturnT> responseTransformer) {
            this.httpResponseHandler = httpResponseHandler;
            this.responseTransformer = responseTransformer;
        }

        @Override
        public ReturnT handle(SdkHttpFullResponse response, ExecutionAttributes executionAttributes) throws Exception {
            SdkResponse resp = (SdkResponse)this.httpResponseHandler.handle(response, executionAttributes);
            return this.transformResponse(resp, response.content().orElseGet(AbortableInputStream::createEmpty));
        }

        @Override
        public boolean needsConnectionLeftOpen() {
            return this.responseTransformer.needsConnectionLeftOpen();
        }

        private ReturnT transformResponse(OutputT resp, AbortableInputStream inputStream) throws Exception {
            try {
                InterruptMonitor.checkInterrupted();
                ReturnT result = this.responseTransformer.transform(resp, inputStream);
                InterruptMonitor.checkInterrupted();
                return result;
            }
            catch (InterruptedException | AbortedException | RetryableException e) {
                throw e;
            }
            catch (Exception e) {
                InterruptMonitor.checkInterrupted();
                throw NonRetryableException.builder().cause(e).build();
            }
        }
    }
}

