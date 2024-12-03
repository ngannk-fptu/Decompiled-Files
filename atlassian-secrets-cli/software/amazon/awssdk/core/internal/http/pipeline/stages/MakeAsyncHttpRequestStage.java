/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.core.internal.http.pipeline.stages;

import java.nio.ByteBuffer;
import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Supplier;
import org.reactivestreams.Subscriber;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.Response;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.core.client.config.SdkAdvancedAsyncClientOption;
import software.amazon.awssdk.core.client.config.SdkClientOption;
import software.amazon.awssdk.core.exception.ApiCallAttemptTimeoutException;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.interceptor.ExecutionAttributes;
import software.amazon.awssdk.core.interceptor.SdkInternalExecutionAttribute;
import software.amazon.awssdk.core.internal.http.HttpClientDependencies;
import software.amazon.awssdk.core.internal.http.RequestExecutionContext;
import software.amazon.awssdk.core.internal.http.TransformingAsyncResponseHandler;
import software.amazon.awssdk.core.internal.http.async.SimpleHttpContentPublisher;
import software.amazon.awssdk.core.internal.http.pipeline.RequestPipeline;
import software.amazon.awssdk.core.internal.http.timers.TimeoutTracker;
import software.amazon.awssdk.core.internal.http.timers.TimerUtils;
import software.amazon.awssdk.core.internal.util.MetricUtils;
import software.amazon.awssdk.core.metrics.CoreMetric;
import software.amazon.awssdk.http.SdkHttpFullRequest;
import software.amazon.awssdk.http.SdkHttpMethod;
import software.amazon.awssdk.http.async.AsyncExecuteRequest;
import software.amazon.awssdk.http.async.SdkAsyncHttpClient;
import software.amazon.awssdk.http.async.SdkHttpContentPublisher;
import software.amazon.awssdk.metrics.MetricCollector;
import software.amazon.awssdk.utils.CompletableFutureUtils;
import software.amazon.awssdk.utils.Logger;

@SdkInternalApi
public final class MakeAsyncHttpRequestStage<OutputT>
implements RequestPipeline<CompletableFuture<SdkHttpFullRequest>, CompletableFuture<Response<OutputT>>> {
    private static final Logger log = Logger.loggerFor(MakeAsyncHttpRequestStage.class);
    private final SdkAsyncHttpClient sdkAsyncHttpClient;
    private final TransformingAsyncResponseHandler<Response<OutputT>> responseHandler;
    private final Executor futureCompletionExecutor;
    private final ScheduledExecutorService timeoutExecutor;
    private final Duration apiCallAttemptTimeout;

    public MakeAsyncHttpRequestStage(TransformingAsyncResponseHandler<Response<OutputT>> responseHandler, HttpClientDependencies dependencies) {
        this.responseHandler = responseHandler;
        this.futureCompletionExecutor = dependencies.clientConfiguration().option(SdkAdvancedAsyncClientOption.FUTURE_COMPLETION_EXECUTOR);
        this.sdkAsyncHttpClient = dependencies.clientConfiguration().option(SdkClientOption.ASYNC_HTTP_CLIENT);
        this.apiCallAttemptTimeout = dependencies.clientConfiguration().option(SdkClientOption.API_CALL_ATTEMPT_TIMEOUT);
        this.timeoutExecutor = dependencies.clientConfiguration().option(SdkClientOption.SCHEDULED_EXECUTOR_SERVICE);
    }

    @Override
    public CompletableFuture<Response<OutputT>> execute(CompletableFuture<SdkHttpFullRequest> requestFuture, RequestExecutionContext context) {
        CompletableFuture toReturn = new CompletableFuture();
        CompletableFutureUtils.forwardExceptionTo(requestFuture, toReturn);
        CompletableFutureUtils.forwardExceptionTo(toReturn, requestFuture);
        requestFuture.thenAccept(request -> {
            try {
                CompletableFuture<Response<OutputT>> executeFuture = this.executeHttpRequest((SdkHttpFullRequest)request, context);
                executeFuture.whenComplete((r, t) -> {
                    if (t != null) {
                        toReturn.completeExceptionally((Throwable)t);
                    } else {
                        toReturn.complete((Response)r);
                    }
                });
                CompletableFutureUtils.forwardExceptionTo(toReturn, executeFuture);
            }
            catch (Throwable t2) {
                toReturn.completeExceptionally(t2);
            }
        });
        return toReturn;
    }

    private CompletableFuture<Response<OutputT>> executeHttpRequest(SdkHttpFullRequest request, RequestExecutionContext context) {
        CompletableFuture<Response<OutputT>> responseFuture = new CompletableFuture<Response<OutputT>>();
        CompletableFuture<Response<OutputT>> responseHandlerFuture = this.responseHandler.prepare();
        SdkHttpContentPublisher requestProvider = context.requestProvider() == null ? new SimpleHttpContentPublisher(request) : new SdkHttpContentPublisherAdapter(context.requestProvider());
        SdkHttpFullRequest requestWithContentLength = this.getRequestWithContentLength(request, requestProvider);
        MetricCollector httpMetricCollector = MetricUtils.createHttpMetricsCollector(context);
        AsyncExecuteRequest.Builder executeRequestBuilder = AsyncExecuteRequest.builder().request(requestWithContentLength).requestContentPublisher(requestProvider).responseHandler(this.responseHandler).fullDuplex(this.isFullDuplex(context.executionAttributes())).metricCollector(httpMetricCollector);
        if (context.executionAttributes().getAttribute(SdkInternalExecutionAttribute.SDK_HTTP_EXECUTION_ATTRIBUTES) != null) {
            executeRequestBuilder.httpExecutionAttributes(context.executionAttributes().getAttribute(SdkInternalExecutionAttribute.SDK_HTTP_EXECUTION_ATTRIBUTES));
        }
        CompletableFuture<Void> httpClientFuture = this.doExecuteHttpRequest(context, executeRequestBuilder.build());
        TimeoutTracker timeoutTracker = this.setupAttemptTimer(responseFuture, context);
        context.apiCallAttemptTimeoutTracker(timeoutTracker);
        responseFuture.whenComplete((r, t) -> {
            if (t != null) {
                httpClientFuture.completeExceptionally((Throwable)t);
            }
        });
        CompletionStage asyncComplete = responseHandlerFuture.handleAsync((r, t) -> {
            this.completeResponseFuture(responseFuture, (Response<OutputT>)r, (Throwable)t);
            return null;
        }, this.futureCompletionExecutor);
        ((CompletableFuture)asyncComplete).whenComplete((ignored, asyncCompleteError) -> {
            if (asyncCompleteError != null) {
                log.debug(() -> String.format("Could not complete the service call future on the provided FUTURE_COMPLETION_EXECUTOR. The future will be completed synchronously by thread %s. This may be an indication that the executor is being overwhelmed by too many requests, and it may negatively affect performance. Consider changing the configuration of the executor to accommodate the load through the client.", Thread.currentThread().getName()), (Throwable)asyncCompleteError);
                responseHandlerFuture.whenComplete((r, t) -> this.completeResponseFuture(responseFuture, (Response<OutputT>)r, (Throwable)t));
            }
        });
        return responseFuture;
    }

    private CompletableFuture<Void> doExecuteHttpRequest(RequestExecutionContext context, AsyncExecuteRequest executeRequest) {
        MetricCollector metricCollector = context.attemptMetricCollector();
        long callStart = System.nanoTime();
        CompletableFuture<Void> httpClientFuture = this.sdkAsyncHttpClient.execute(executeRequest);
        CompletionStage result = httpClientFuture.whenComplete((r, t) -> {
            long duration = System.nanoTime() - callStart;
            metricCollector.reportMetric(CoreMetric.SERVICE_CALL_DURATION, Duration.ofNanos(duration));
        });
        CompletableFutureUtils.forwardExceptionTo(result, httpClientFuture);
        return result;
    }

    private boolean isFullDuplex(ExecutionAttributes executionAttributes) {
        return executionAttributes.getAttribute(SdkInternalExecutionAttribute.IS_FULL_DUPLEX) != null && executionAttributes.getAttribute(SdkInternalExecutionAttribute.IS_FULL_DUPLEX) != false;
    }

    private SdkHttpFullRequest getRequestWithContentLength(SdkHttpFullRequest request, SdkHttpContentPublisher requestProvider) {
        if (this.shouldSetContentLength(request, requestProvider)) {
            return request.toBuilder().putHeader("Content-Length", String.valueOf(requestProvider.contentLength().get())).build();
        }
        return request;
    }

    private boolean shouldSetContentLength(SdkHttpFullRequest request, SdkHttpContentPublisher requestProvider) {
        if (request.method() == SdkHttpMethod.GET || request.method() == SdkHttpMethod.HEAD || request.firstMatchingHeader("Content-Length").isPresent()) {
            return false;
        }
        return Optional.ofNullable(requestProvider).flatMap(SdkHttpContentPublisher::contentLength).isPresent();
    }

    private TimeoutTracker setupAttemptTimer(CompletableFuture<Response<OutputT>> executeFuture, RequestExecutionContext ctx) {
        long timeoutMillis = TimerUtils.resolveTimeoutInMillis(ctx.requestConfig()::apiCallAttemptTimeout, this.apiCallAttemptTimeout);
        Supplier<SdkClientException> exceptionSupplier = () -> ApiCallAttemptTimeoutException.create(timeoutMillis);
        return TimerUtils.timeAsyncTaskIfNeeded(executeFuture, this.timeoutExecutor, exceptionSupplier, timeoutMillis);
    }

    private void completeResponseFuture(CompletableFuture<Response<OutputT>> responseFuture, Response<OutputT> r, Throwable t) {
        if (t == null) {
            responseFuture.complete(r);
        } else {
            responseFuture.completeExceptionally(t);
        }
    }

    private static final class SdkHttpContentPublisherAdapter
    implements SdkHttpContentPublisher {
        private final AsyncRequestBody asyncRequestBody;

        private SdkHttpContentPublisherAdapter(AsyncRequestBody asyncRequestBody) {
            this.asyncRequestBody = asyncRequestBody;
        }

        @Override
        public Optional<Long> contentLength() {
            return this.asyncRequestBody.contentLength();
        }

        @Override
        public void subscribe(Subscriber<? super ByteBuffer> s) {
            this.asyncRequestBody.subscribe(s);
        }
    }
}

