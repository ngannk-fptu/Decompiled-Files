/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.core.internal.http;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.annotations.ThreadSafe;
import software.amazon.awssdk.core.ClientType;
import software.amazon.awssdk.core.Response;
import software.amazon.awssdk.core.SdkRequest;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.core.client.config.SdkClientConfiguration;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.http.ExecutionContext;
import software.amazon.awssdk.core.internal.http.HttpClientDependencies;
import software.amazon.awssdk.core.internal.http.RequestExecutionContext;
import software.amazon.awssdk.core.internal.http.TransformingAsyncResponseHandler;
import software.amazon.awssdk.core.internal.http.pipeline.RequestPipelineBuilder;
import software.amazon.awssdk.core.internal.http.pipeline.stages.AfterExecutionInterceptorsStage;
import software.amazon.awssdk.core.internal.http.pipeline.stages.ApplyTransactionIdStage;
import software.amazon.awssdk.core.internal.http.pipeline.stages.ApplyUserAgentStage;
import software.amazon.awssdk.core.internal.http.pipeline.stages.AsyncApiCallAttemptMetricCollectionStage;
import software.amazon.awssdk.core.internal.http.pipeline.stages.AsyncApiCallMetricCollectionStage;
import software.amazon.awssdk.core.internal.http.pipeline.stages.AsyncApiCallTimeoutTrackingStage;
import software.amazon.awssdk.core.internal.http.pipeline.stages.AsyncBeforeTransmissionExecutionInterceptorsStage;
import software.amazon.awssdk.core.internal.http.pipeline.stages.AsyncExecutionFailureExceptionReportingStage;
import software.amazon.awssdk.core.internal.http.pipeline.stages.AsyncRetryableStage;
import software.amazon.awssdk.core.internal.http.pipeline.stages.AsyncSigningStage;
import software.amazon.awssdk.core.internal.http.pipeline.stages.CompressRequestStage;
import software.amazon.awssdk.core.internal.http.pipeline.stages.HttpChecksumStage;
import software.amazon.awssdk.core.internal.http.pipeline.stages.MakeAsyncHttpRequestStage;
import software.amazon.awssdk.core.internal.http.pipeline.stages.MakeRequestImmutableStage;
import software.amazon.awssdk.core.internal.http.pipeline.stages.MakeRequestMutableStage;
import software.amazon.awssdk.core.internal.http.pipeline.stages.MergeCustomHeadersStage;
import software.amazon.awssdk.core.internal.http.pipeline.stages.MergeCustomQueryParamsStage;
import software.amazon.awssdk.core.internal.http.pipeline.stages.QueryParametersToBodyStage;
import software.amazon.awssdk.core.internal.http.pipeline.stages.UnwrapResponseContainer;
import software.amazon.awssdk.core.internal.util.ThrowableUtils;
import software.amazon.awssdk.http.SdkHttpFullRequest;
import software.amazon.awssdk.utils.SdkAutoCloseable;

@ThreadSafe
@SdkInternalApi
public final class AmazonAsyncHttpClient
implements SdkAutoCloseable {
    private final HttpClientDependencies httpClientDependencies;

    public AmazonAsyncHttpClient(SdkClientConfiguration clientConfiguration) {
        this.httpClientDependencies = HttpClientDependencies.builder().clientConfiguration(clientConfiguration).build();
    }

    @Override
    public void close() {
        this.httpClientDependencies.close();
    }

    public RequestExecutionBuilder requestExecutionBuilder() {
        return new RequestExecutionBuilderImpl().httpClientDependencies(this.httpClientDependencies);
    }

    private static class RequestExecutionBuilderImpl
    implements RequestExecutionBuilder {
        private HttpClientDependencies httpClientDependencies;
        private AsyncRequestBody requestProvider;
        private SdkHttpFullRequest request;
        private SdkRequest originalRequest;
        private ExecutionContext executionContext;

        private RequestExecutionBuilderImpl() {
        }

        @Override
        public RequestExecutionBuilder httpClientDependencies(HttpClientDependencies httpClientDependencies) {
            this.httpClientDependencies = httpClientDependencies;
            return this;
        }

        @Override
        public HttpClientDependencies httpClientDependencies() {
            return this.httpClientDependencies;
        }

        @Override
        public RequestExecutionBuilder requestProvider(AsyncRequestBody requestProvider) {
            this.requestProvider = requestProvider;
            return this;
        }

        @Override
        public RequestExecutionBuilder request(SdkHttpFullRequest request) {
            this.request = request;
            return this;
        }

        @Override
        public RequestExecutionBuilder executionContext(ExecutionContext executionContext) {
            this.executionContext = executionContext;
            return this;
        }

        @Override
        public RequestExecutionBuilder originalRequest(SdkRequest originalRequest) {
            this.originalRequest = originalRequest;
            return this;
        }

        @Override
        public <OutputT> CompletableFuture<OutputT> execute(TransformingAsyncResponseHandler<Response<OutputT>> responseHandler) {
            try {
                return (CompletableFuture)RequestPipelineBuilder.first(RequestPipelineBuilder.first(MakeRequestMutableStage::new).then(ApplyTransactionIdStage::new).then(ApplyUserAgentStage::new).then(MergeCustomHeadersStage::new).then(MergeCustomQueryParamsStage::new).then(QueryParametersToBodyStage::new).then(() -> new CompressRequestStage(this.httpClientDependencies)).then(() -> new HttpChecksumStage(ClientType.ASYNC)).then(MakeRequestImmutableStage::new).then(RequestPipelineBuilder.first(AsyncSigningStage::new).then(AsyncBeforeTransmissionExecutionInterceptorsStage::new).then(d -> new MakeAsyncHttpRequestStage(responseHandler, (HttpClientDependencies)d)).wrappedWith(AsyncApiCallAttemptMetricCollectionStage::new).wrappedWith((deps, wrapped) -> new AsyncRetryableStage(responseHandler, (HttpClientDependencies)deps, wrapped)).then(RequestPipelineBuilder.async(() -> new UnwrapResponseContainer())).then(RequestPipelineBuilder.async(() -> new AfterExecutionInterceptorsStage())).wrappedWith(AsyncExecutionFailureExceptionReportingStage::new).wrappedWith(AsyncApiCallTimeoutTrackingStage::new).wrappedWith(AsyncApiCallMetricCollectionStage::new)::build)::build).build(this.httpClientDependencies).execute(this.request, this.createRequestExecutionDependencies());
            }
            catch (RuntimeException e) {
                throw ThrowableUtils.asSdkException(e);
            }
            catch (Exception e) {
                throw SdkClientException.builder().cause(e).build();
            }
        }

        private RequestExecutionContext createRequestExecutionDependencies() {
            return RequestExecutionContext.builder().requestProvider(this.requestProvider).originalRequest(this.originalRequest).executionContext(this.executionContext).build();
        }
    }

    public static interface RequestExecutionBuilder {
        public RequestExecutionBuilder requestProvider(AsyncRequestBody var1);

        public RequestExecutionBuilder request(SdkHttpFullRequest var1);

        public RequestExecutionBuilder executionContext(ExecutionContext var1);

        public RequestExecutionBuilder originalRequest(SdkRequest var1);

        public RequestExecutionBuilder httpClientDependencies(HttpClientDependencies var1);

        public HttpClientDependencies httpClientDependencies();

        default public RequestExecutionBuilder httpClientDependencies(Consumer<HttpClientDependencies.Builder> mutator) {
            HttpClientDependencies.Builder builder = this.httpClientDependencies().toBuilder();
            mutator.accept(builder);
            return this.httpClientDependencies(builder.build());
        }

        public <OutputT> CompletableFuture<OutputT> execute(TransformingAsyncResponseHandler<Response<OutputT>> var1);
    }
}

