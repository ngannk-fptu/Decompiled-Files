/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.core.internal.http;

import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.RequestOverrideConfiguration;
import software.amazon.awssdk.core.SdkRequest;
import software.amazon.awssdk.core.SdkRequestOverrideConfiguration;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.core.http.ExecutionContext;
import software.amazon.awssdk.core.interceptor.ExecutionAttributes;
import software.amazon.awssdk.core.interceptor.ExecutionInterceptorChain;
import software.amazon.awssdk.core.interceptor.SdkExecutionAttribute;
import software.amazon.awssdk.core.internal.http.timers.TimeoutTracker;
import software.amazon.awssdk.core.signer.Signer;
import software.amazon.awssdk.metrics.MetricCollector;
import software.amazon.awssdk.utils.Validate;

@SdkInternalApi
public final class RequestExecutionContext {
    private static final RequestOverrideConfiguration EMPTY_CONFIG = SdkRequestOverrideConfiguration.builder().build();
    private AsyncRequestBody requestProvider;
    private final SdkRequest originalRequest;
    private final ExecutionContext executionContext;
    private TimeoutTracker apiCallTimeoutTracker;
    private TimeoutTracker apiCallAttemptTimeoutTracker;
    private MetricCollector attemptMetricCollector;

    private RequestExecutionContext(Builder builder) {
        this.requestProvider = builder.requestProvider;
        this.originalRequest = Validate.paramNotNull(builder.originalRequest, "originalRequest");
        this.executionContext = Validate.paramNotNull(builder.executionContext, "executionContext");
    }

    public static Builder builder() {
        return new Builder();
    }

    public AsyncRequestBody requestProvider() {
        return this.requestProvider;
    }

    public ExecutionInterceptorChain interceptorChain() {
        return this.executionContext.interceptorChain();
    }

    public ExecutionAttributes executionAttributes() {
        return this.executionContext.executionAttributes();
    }

    public ExecutionContext executionContext() {
        return this.executionContext;
    }

    public SdkRequest originalRequest() {
        return this.originalRequest;
    }

    public RequestOverrideConfiguration requestConfig() {
        return this.originalRequest.overrideConfiguration().map(c -> c).orElse(EMPTY_CONFIG);
    }

    public Signer signer() {
        return this.executionContext.signer();
    }

    public TimeoutTracker apiCallTimeoutTracker() {
        return this.apiCallTimeoutTracker;
    }

    public void apiCallTimeoutTracker(TimeoutTracker timeoutTracker) {
        this.apiCallTimeoutTracker = timeoutTracker;
    }

    public TimeoutTracker apiCallAttemptTimeoutTracker() {
        return this.apiCallAttemptTimeoutTracker;
    }

    public void apiCallAttemptTimeoutTracker(TimeoutTracker timeoutTracker) {
        this.apiCallAttemptTimeoutTracker = timeoutTracker;
    }

    public MetricCollector attemptMetricCollector() {
        return this.attemptMetricCollector;
    }

    public void attemptMetricCollector(MetricCollector metricCollector) {
        this.executionAttributes().putAttribute(SdkExecutionAttribute.API_CALL_ATTEMPT_METRIC_COLLECTOR, metricCollector);
        this.attemptMetricCollector = metricCollector;
    }

    public void requestProvider(AsyncRequestBody publisher) {
        this.requestProvider = publisher;
    }

    public static final class Builder {
        private AsyncRequestBody requestProvider;
        private SdkRequest originalRequest;
        private ExecutionContext executionContext;

        public Builder requestProvider(AsyncRequestBody requestProvider) {
            this.requestProvider = requestProvider;
            return this;
        }

        public Builder originalRequest(SdkRequest originalRequest) {
            this.originalRequest = originalRequest;
            return this;
        }

        public Builder executionContext(ExecutionContext executionContext) {
            this.executionContext = executionContext;
            return this;
        }

        public RequestExecutionContext build() {
            return new RequestExecutionContext(this);
        }
    }
}

