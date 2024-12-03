/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.core.internal.http;

import java.util.function.Consumer;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.annotations.ThreadSafe;
import software.amazon.awssdk.core.ClientType;
import software.amazon.awssdk.core.Response;
import software.amazon.awssdk.core.SdkRequest;
import software.amazon.awssdk.core.client.config.SdkClientConfiguration;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.http.ExecutionContext;
import software.amazon.awssdk.core.http.HttpResponseHandler;
import software.amazon.awssdk.core.interceptor.ExecutionAttributes;
import software.amazon.awssdk.core.interceptor.InterceptorContext;
import software.amazon.awssdk.core.internal.http.HttpClientDependencies;
import software.amazon.awssdk.core.internal.http.RequestExecutionContext;
import software.amazon.awssdk.core.internal.http.StreamManagingStage;
import software.amazon.awssdk.core.internal.http.pipeline.RequestPipelineBuilder;
import software.amazon.awssdk.core.internal.http.pipeline.stages.AfterExecutionInterceptorsStage;
import software.amazon.awssdk.core.internal.http.pipeline.stages.AfterTransmissionExecutionInterceptorsStage;
import software.amazon.awssdk.core.internal.http.pipeline.stages.ApiCallAttemptMetricCollectionStage;
import software.amazon.awssdk.core.internal.http.pipeline.stages.ApiCallAttemptTimeoutTrackingStage;
import software.amazon.awssdk.core.internal.http.pipeline.stages.ApiCallMetricCollectionStage;
import software.amazon.awssdk.core.internal.http.pipeline.stages.ApiCallTimeoutTrackingStage;
import software.amazon.awssdk.core.internal.http.pipeline.stages.ApplyTransactionIdStage;
import software.amazon.awssdk.core.internal.http.pipeline.stages.ApplyUserAgentStage;
import software.amazon.awssdk.core.internal.http.pipeline.stages.BeforeTransmissionExecutionInterceptorsStage;
import software.amazon.awssdk.core.internal.http.pipeline.stages.BeforeUnmarshallingExecutionInterceptorsStage;
import software.amazon.awssdk.core.internal.http.pipeline.stages.CompressRequestStage;
import software.amazon.awssdk.core.internal.http.pipeline.stages.ExecutionFailureExceptionReportingStage;
import software.amazon.awssdk.core.internal.http.pipeline.stages.HandleResponseStage;
import software.amazon.awssdk.core.internal.http.pipeline.stages.HttpChecksumStage;
import software.amazon.awssdk.core.internal.http.pipeline.stages.MakeHttpRequestStage;
import software.amazon.awssdk.core.internal.http.pipeline.stages.MakeRequestImmutableStage;
import software.amazon.awssdk.core.internal.http.pipeline.stages.MakeRequestMutableStage;
import software.amazon.awssdk.core.internal.http.pipeline.stages.MergeCustomHeadersStage;
import software.amazon.awssdk.core.internal.http.pipeline.stages.MergeCustomQueryParamsStage;
import software.amazon.awssdk.core.internal.http.pipeline.stages.QueryParametersToBodyStage;
import software.amazon.awssdk.core.internal.http.pipeline.stages.RetryableStage;
import software.amazon.awssdk.core.internal.http.pipeline.stages.SigningStage;
import software.amazon.awssdk.core.internal.http.pipeline.stages.TimeoutExceptionHandlingStage;
import software.amazon.awssdk.core.internal.http.pipeline.stages.UnwrapResponseContainer;
import software.amazon.awssdk.http.SdkHttpFullRequest;
import software.amazon.awssdk.http.SdkHttpFullResponse;
import software.amazon.awssdk.utils.SdkAutoCloseable;

@ThreadSafe
@SdkInternalApi
public final class AmazonSyncHttpClient
implements SdkAutoCloseable {
    private final HttpClientDependencies httpClientDependencies;

    public AmazonSyncHttpClient(SdkClientConfiguration clientConfiguration) {
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
        private SdkHttpFullRequest request;
        private SdkRequest originalRequest;
        private ExecutionContext executionContext;

        private RequestExecutionBuilderImpl() {
        }

        @Override
        public RequestExecutionBuilder request(SdkHttpFullRequest request) {
            this.request = request;
            return this;
        }

        @Override
        public RequestExecutionBuilder originalRequest(SdkRequest originalRequest) {
            this.originalRequest = originalRequest;
            return this;
        }

        @Override
        public RequestExecutionBuilder executionContext(ExecutionContext executionContext) {
            this.executionContext = executionContext;
            return this;
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
        public <OutputT> OutputT execute(HttpResponseHandler<Response<OutputT>> responseHandler) {
            if (this.request != null && this.executionContext != null) {
                this.executionContext.interceptorContext((InterceptorContext)this.executionContext.interceptorContext().copy(ib -> ib.httpRequest(this.request)));
            }
            try {
                return (OutputT)RequestPipelineBuilder.first(RequestPipelineBuilder.first(MakeRequestMutableStage::new).then(ApplyTransactionIdStage::new).then(ApplyUserAgentStage::new).then(MergeCustomHeadersStage::new).then(MergeCustomQueryParamsStage::new).then(QueryParametersToBodyStage::new).then(() -> new CompressRequestStage(this.httpClientDependencies)).then(() -> new HttpChecksumStage(ClientType.SYNC)).then(MakeRequestImmutableStage::new).then(RequestPipelineBuilder.first(SigningStage::new).then(BeforeTransmissionExecutionInterceptorsStage::new).then(MakeHttpRequestStage::new).then(AfterTransmissionExecutionInterceptorsStage::new).then(BeforeUnmarshallingExecutionInterceptorsStage::new).then(() -> new HandleResponseStage(responseHandler)).wrappedWith(ApiCallAttemptTimeoutTrackingStage::new).wrappedWith(TimeoutExceptionHandlingStage::new).wrappedWith((deps, wrapped) -> new ApiCallAttemptMetricCollectionStage(wrapped)).wrappedWith(RetryableStage::new)::build).wrappedWith(StreamManagingStage::new).wrappedWith(ApiCallTimeoutTrackingStage::new)::build).wrappedWith((deps, wrapped) -> new ApiCallMetricCollectionStage(wrapped)).then(() -> new UnwrapResponseContainer()).then(() -> new AfterExecutionInterceptorsStage()).wrappedWith(ExecutionFailureExceptionReportingStage::new).build(this.httpClientDependencies).execute(this.request, this.createRequestExecutionDependencies());
            }
            catch (RuntimeException e) {
                throw e;
            }
            catch (Exception e) {
                throw SdkClientException.builder().cause(e).build();
            }
        }

        private RequestExecutionContext createRequestExecutionDependencies() {
            return RequestExecutionContext.builder().originalRequest(this.originalRequest).executionContext(this.executionContext).build();
        }
    }

    private static class NoOpResponseHandler<T>
    implements HttpResponseHandler<T> {
        private NoOpResponseHandler() {
        }

        @Override
        public T handle(SdkHttpFullResponse response, ExecutionAttributes executionAttributes) {
            return null;
        }

        @Override
        public boolean needsConnectionLeftOpen() {
            return false;
        }
    }

    public static interface RequestExecutionBuilder {
        public RequestExecutionBuilder request(SdkHttpFullRequest var1);

        public RequestExecutionBuilder originalRequest(SdkRequest var1);

        public RequestExecutionBuilder executionContext(ExecutionContext var1);

        public RequestExecutionBuilder httpClientDependencies(HttpClientDependencies var1);

        public HttpClientDependencies httpClientDependencies();

        default public RequestExecutionBuilder httpClientDependencies(Consumer<HttpClientDependencies.Builder> mutator) {
            HttpClientDependencies.Builder builder = this.httpClientDependencies().toBuilder();
            mutator.accept(builder);
            return this.httpClientDependencies(builder.build());
        }

        public <OutputT> OutputT execute(HttpResponseHandler<Response<OutputT>> var1);
    }
}

