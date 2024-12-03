/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.http.SdkHttpFullRequest
 *  software.amazon.awssdk.http.SdkHttpFullResponse
 *  software.amazon.awssdk.http.SdkHttpRequest
 *  software.amazon.awssdk.metrics.MetricCollector
 *  software.amazon.awssdk.utils.CompletableFutureUtils
 *  software.amazon.awssdk.utils.FunctionalUtils
 *  software.amazon.awssdk.utils.Logger
 */
package software.amazon.awssdk.core.internal.handler;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;
import java.util.function.Supplier;
import org.slf4j.Logger;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.Response;
import software.amazon.awssdk.core.SdkRequest;
import software.amazon.awssdk.core.SdkResponse;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.core.async.AsyncResponseTransformer;
import software.amazon.awssdk.core.client.config.SdkClientConfiguration;
import software.amazon.awssdk.core.client.handler.AsyncClientHandler;
import software.amazon.awssdk.core.client.handler.ClientExecutionParams;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.core.http.Crc32Validation;
import software.amazon.awssdk.core.http.ExecutionContext;
import software.amazon.awssdk.core.http.HttpResponseHandler;
import software.amazon.awssdk.core.interceptor.ExecutionAttributes;
import software.amazon.awssdk.core.interceptor.InterceptorContext;
import software.amazon.awssdk.core.internal.InternalCoreExecutionAttribute;
import software.amazon.awssdk.core.internal.handler.BaseClientHandler;
import software.amazon.awssdk.core.internal.http.AmazonAsyncHttpClient;
import software.amazon.awssdk.core.internal.http.IdempotentAsyncResponseHandler;
import software.amazon.awssdk.core.internal.http.TransformingAsyncResponseHandler;
import software.amazon.awssdk.core.internal.http.async.AsyncAfterTransmissionInterceptorCallingResponseHandler;
import software.amazon.awssdk.core.internal.http.async.AsyncResponseHandler;
import software.amazon.awssdk.core.internal.http.async.AsyncStreamingResponseHandler;
import software.amazon.awssdk.core.internal.http.async.CombinedResponseAsyncHttpResponseHandler;
import software.amazon.awssdk.core.internal.util.ThrowableUtils;
import software.amazon.awssdk.core.metrics.CoreMetric;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.http.SdkHttpFullRequest;
import software.amazon.awssdk.http.SdkHttpFullResponse;
import software.amazon.awssdk.http.SdkHttpRequest;
import software.amazon.awssdk.metrics.MetricCollector;
import software.amazon.awssdk.utils.CompletableFutureUtils;
import software.amazon.awssdk.utils.FunctionalUtils;

@SdkInternalApi
public abstract class BaseAsyncClientHandler
extends BaseClientHandler
implements AsyncClientHandler {
    private static final software.amazon.awssdk.utils.Logger log = software.amazon.awssdk.utils.Logger.loggerFor(BaseAsyncClientHandler.class);
    private final AmazonAsyncHttpClient client;
    private final Function<SdkHttpFullResponse, SdkHttpFullResponse> crc32Validator;

    protected BaseAsyncClientHandler(SdkClientConfiguration clientConfiguration, AmazonAsyncHttpClient client) {
        super(clientConfiguration);
        this.client = client;
        this.crc32Validator = response -> Crc32Validation.validate(this.isCalculateCrc32FromCompressedData(), response);
    }

    @Override
    public <InputT extends SdkRequest, OutputT extends SdkResponse> CompletableFuture<OutputT> execute(ClientExecutionParams<InputT, OutputT> executionParams) {
        return this.measureApiCallSuccess(executionParams, () -> {
            ExecutionContext executionContext = this.invokeInterceptorsAndCreateExecutionContext(executionParams);
            TransformingAsyncResponseHandler combinedResponseHandler = this.createCombinedResponseHandler(executionParams, executionContext);
            return this.doExecute(executionParams, executionContext, combinedResponseHandler);
        });
    }

    @Override
    public <InputT extends SdkRequest, OutputT extends SdkResponse, ReturnT> CompletableFuture<ReturnT> execute(ClientExecutionParams<InputT, OutputT> executionParams, AsyncResponseTransformer<OutputT, ReturnT> asyncResponseTransformer) {
        return this.measureApiCallSuccess(executionParams, () -> {
            if (executionParams.getCombinedResponseHandler() != null) {
                throw new IllegalArgumentException("A streaming 'asyncResponseTransformer' may not be used when a 'combinedResponseHandler' has been specified in a ClientExecutionParams object.");
            }
            ExecutionAttributes executionAttributes = executionParams.executionAttributes();
            executionAttributes.putAttribute(InternalCoreExecutionAttribute.EXECUTION_ATTEMPT, 1);
            AsyncStreamingResponseHandler asyncStreamingResponseHandler = new AsyncStreamingResponseHandler(asyncResponseTransformer);
            IdempotentAsyncResponseHandler wrappedAsyncStreamingResponseHandler = IdempotentAsyncResponseHandler.create(asyncStreamingResponseHandler, () -> executionAttributes.getAttribute(InternalCoreExecutionAttribute.EXECUTION_ATTEMPT), Integer::equals);
            wrappedAsyncStreamingResponseHandler.prepare();
            ExecutionContext context = this.invokeInterceptorsAndCreateExecutionContext(executionParams);
            HttpResponseHandler decoratedResponseHandlers = this.decorateResponseHandlers(executionParams.getResponseHandler(), context);
            asyncStreamingResponseHandler.responseHandler(decoratedResponseHandlers);
            TransformingAsyncResponseHandler<? extends SdkException> errorHandler = this.resolveErrorResponseHandler(executionParams.getErrorResponseHandler(), context, this.crc32Validator);
            CombinedResponseAsyncHttpResponseHandler combinedResponseHandler = new CombinedResponseAsyncHttpResponseHandler(wrappedAsyncStreamingResponseHandler, errorHandler);
            return this.doExecute(executionParams, context, combinedResponseHandler);
        });
    }

    private <InputT extends SdkRequest, OutputT extends SdkResponse> TransformingAsyncResponseHandler<Response<OutputT>> createCombinedResponseHandler(ClientExecutionParams<InputT, OutputT> executionParams, ExecutionContext executionContext) {
        BaseAsyncClientHandler.validateCombinedResponseHandler(executionParams);
        TransformingAsyncResponseHandler<Response<OutputT>> combinedResponseHandler = executionParams.getCombinedResponseHandler() == null ? this.createDecoratedHandler(executionParams.getResponseHandler(), executionParams.getErrorResponseHandler(), executionContext) : this.createDecoratedHandler(executionParams.getCombinedResponseHandler(), executionContext);
        return combinedResponseHandler;
    }

    private <OutputT extends SdkResponse> TransformingAsyncResponseHandler<Response<OutputT>> createDecoratedHandler(HttpResponseHandler<OutputT> successHandler, HttpResponseHandler<? extends SdkException> errorHandler, ExecutionContext executionContext) {
        HttpResponseHandler<OutputT> decoratedResponseHandlers = this.decorateResponseHandlers(successHandler, executionContext);
        AsyncResponseHandler<OutputT> decoratedSuccessHandler = new AsyncResponseHandler<OutputT>(decoratedResponseHandlers, this.crc32Validator, executionContext.executionAttributes());
        TransformingAsyncResponseHandler<? extends SdkException> decoratedErrorHandler = this.resolveErrorResponseHandler(errorHandler, executionContext, this.crc32Validator);
        return new CombinedResponseAsyncHttpResponseHandler<OutputT>(decoratedSuccessHandler, decoratedErrorHandler);
    }

    private <OutputT extends SdkResponse> TransformingAsyncResponseHandler<Response<OutputT>> createDecoratedHandler(HttpResponseHandler<Response<OutputT>> combinedResponseHandler, ExecutionContext executionContext) {
        HttpResponseHandler<Response<OutputT>> decoratedResponseHandlers = this.decorateSuccessResponseHandlers(combinedResponseHandler, executionContext);
        return new AsyncResponseHandler<Response<OutputT>>(decoratedResponseHandlers, this.crc32Validator, executionContext.executionAttributes());
    }

    private <InputT extends SdkRequest, OutputT extends SdkResponse, ReturnT> CompletableFuture<ReturnT> doExecute(ClientExecutionParams<InputT, OutputT> executionParams, ExecutionContext executionContext, TransformingAsyncResponseHandler<Response<ReturnT>> asyncResponseHandler) {
        try {
            SdkRequest inputT = executionContext.interceptorContext().request();
            InterceptorContext finalizeSdkHttpRequestContext = BaseAsyncClientHandler.finalizeSdkHttpFullRequest(executionParams, executionContext, inputT, this.resolveRequestConfiguration(executionParams));
            SdkHttpFullRequest marshalled = (SdkHttpFullRequest)finalizeSdkHttpRequestContext.httpRequest();
            try {
                this.validateSigningConfiguration((SdkHttpRequest)marshalled, executionContext.signer());
            }
            catch (Exception e) {
                return CompletableFutureUtils.failedFuture((Throwable)e);
            }
            Optional<RequestBody> requestBody = finalizeSdkHttpRequestContext.requestBody();
            if (!finalizeSdkHttpRequestContext.asyncRequestBody().isPresent() && requestBody.isPresent()) {
                marshalled = marshalled.toBuilder().contentStreamProvider(requestBody.get().contentStreamProvider()).build();
            }
            SdkClientConfiguration clientConfiguration = this.resolveRequestConfiguration(executionParams);
            CompletableFuture<OutputT> invokeFuture = this.invoke(clientConfiguration, marshalled, finalizeSdkHttpRequestContext.asyncRequestBody().orElse(null), inputT, executionContext, new AsyncAfterTransmissionInterceptorCallingResponseHandler<Response<OutputT>>(asyncResponseHandler, executionContext));
            CompletionStage exceptionTranslatedFuture = invokeFuture.handle((resp, err) -> {
                if (err != null) {
                    throw ThrowableUtils.failure(err);
                }
                return resp;
            });
            return CompletableFutureUtils.forwardExceptionTo((CompletableFuture)exceptionTranslatedFuture, invokeFuture);
        }
        catch (Throwable t) {
            FunctionalUtils.runAndLogError((Logger)log.logger(), (String)"Error thrown from TransformingAsyncResponseHandler#onError, ignoring.", () -> asyncResponseHandler.onError(t));
            return CompletableFutureUtils.failedFuture((Throwable)ThrowableUtils.asSdkException(t));
        }
    }

    public void close() {
        this.client.close();
    }

    private TransformingAsyncResponseHandler<? extends SdkException> resolveErrorResponseHandler(HttpResponseHandler<? extends SdkException> errorHandler, ExecutionContext executionContext, Function<SdkHttpFullResponse, SdkHttpFullResponse> responseAdapter) {
        return new AsyncResponseHandler<SdkException>(errorHandler, responseAdapter, executionContext.executionAttributes());
    }

    private <InputT extends SdkRequest, OutputT> CompletableFuture<OutputT> invoke(SdkClientConfiguration clientConfiguration, SdkHttpFullRequest request, AsyncRequestBody requestProvider, InputT originalRequest, ExecutionContext executionContext, TransformingAsyncResponseHandler<Response<OutputT>> responseHandler) {
        return this.client.requestExecutionBuilder().requestProvider(requestProvider).request(request).originalRequest(originalRequest).executionContext(executionContext).httpClientDependencies(c -> c.clientConfiguration(clientConfiguration)).execute(responseHandler);
    }

    private <T> CompletableFuture<T> measureApiCallSuccess(ClientExecutionParams<?, ?> executionParams, Supplier<CompletableFuture<T>> apiCall) {
        try {
            CompletableFuture<T> apiCallResult = apiCall.get();
            CompletionStage outputFuture = apiCallResult.whenComplete((r, t) -> this.reportApiCallSuccess(executionParams, t == null));
            CompletableFutureUtils.forwardExceptionTo((CompletableFuture)outputFuture, apiCallResult);
            return outputFuture;
        }
        catch (Exception e) {
            this.reportApiCallSuccess(executionParams, false);
            return CompletableFutureUtils.failedFuture((Throwable)e);
        }
    }

    private void reportApiCallSuccess(ClientExecutionParams<?, ?> executionParams, boolean value) {
        MetricCollector metricCollector = executionParams.getMetricCollector();
        if (metricCollector != null) {
            metricCollector.reportMetric(CoreMetric.API_CALL_SUCCESSFUL, (Object)value);
        }
    }
}

