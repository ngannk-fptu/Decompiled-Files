/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkPublicApi
 *  software.amazon.awssdk.metrics.MetricCollector
 *  software.amazon.awssdk.utils.Validate
 */
package software.amazon.awssdk.http.async;

import java.util.Optional;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.http.SdkHttpExecutionAttribute;
import software.amazon.awssdk.http.SdkHttpExecutionAttributes;
import software.amazon.awssdk.http.SdkHttpRequest;
import software.amazon.awssdk.http.async.SdkAsyncHttpResponseHandler;
import software.amazon.awssdk.http.async.SdkHttpContentPublisher;
import software.amazon.awssdk.metrics.MetricCollector;
import software.amazon.awssdk.utils.Validate;

@SdkPublicApi
public final class AsyncExecuteRequest {
    private final SdkHttpRequest request;
    private final SdkHttpContentPublisher requestContentPublisher;
    private final SdkAsyncHttpResponseHandler responseHandler;
    private final MetricCollector metricCollector;
    private final boolean isFullDuplex;
    private final SdkHttpExecutionAttributes sdkHttpExecutionAttributes;

    private AsyncExecuteRequest(BuilderImpl builder) {
        this.request = builder.request;
        this.requestContentPublisher = builder.requestContentPublisher;
        this.responseHandler = builder.responseHandler;
        this.metricCollector = builder.metricCollector;
        this.isFullDuplex = builder.isFullDuplex;
        this.sdkHttpExecutionAttributes = builder.executionAttributesBuilder.build();
    }

    public SdkHttpRequest request() {
        return this.request;
    }

    public SdkHttpContentPublisher requestContentPublisher() {
        return this.requestContentPublisher;
    }

    public SdkAsyncHttpResponseHandler responseHandler() {
        return this.responseHandler;
    }

    public Optional<MetricCollector> metricCollector() {
        return Optional.ofNullable(this.metricCollector);
    }

    public boolean fullDuplex() {
        return this.isFullDuplex;
    }

    public SdkHttpExecutionAttributes httpExecutionAttributes() {
        return this.sdkHttpExecutionAttributes;
    }

    public static Builder builder() {
        return new BuilderImpl();
    }

    private static class BuilderImpl
    implements Builder {
        private SdkHttpRequest request;
        private SdkHttpContentPublisher requestContentPublisher;
        private SdkAsyncHttpResponseHandler responseHandler;
        private MetricCollector metricCollector;
        private boolean isFullDuplex;
        private SdkHttpExecutionAttributes.Builder executionAttributesBuilder = SdkHttpExecutionAttributes.builder();

        private BuilderImpl() {
        }

        @Override
        public Builder request(SdkHttpRequest request) {
            this.request = request;
            return this;
        }

        @Override
        public Builder requestContentPublisher(SdkHttpContentPublisher requestContentPublisher) {
            this.requestContentPublisher = requestContentPublisher;
            return this;
        }

        @Override
        public Builder responseHandler(SdkAsyncHttpResponseHandler responseHandler) {
            this.responseHandler = responseHandler;
            return this;
        }

        @Override
        public Builder metricCollector(MetricCollector metricCollector) {
            this.metricCollector = metricCollector;
            return this;
        }

        @Override
        public Builder fullDuplex(boolean fullDuplex) {
            this.isFullDuplex = fullDuplex;
            return this;
        }

        @Override
        public <T> Builder putHttpExecutionAttribute(SdkHttpExecutionAttribute<T> attribute, T value) {
            this.executionAttributesBuilder.put(attribute, value);
            return this;
        }

        @Override
        public Builder httpExecutionAttributes(SdkHttpExecutionAttributes executionAttributes) {
            Validate.paramNotNull((Object)executionAttributes, (String)"executionAttributes");
            this.executionAttributesBuilder = executionAttributes.toBuilder();
            return this;
        }

        @Override
        public AsyncExecuteRequest build() {
            return new AsyncExecuteRequest(this);
        }
    }

    public static interface Builder {
        public Builder request(SdkHttpRequest var1);

        public Builder requestContentPublisher(SdkHttpContentPublisher var1);

        public Builder responseHandler(SdkAsyncHttpResponseHandler var1);

        public Builder metricCollector(MetricCollector var1);

        public Builder fullDuplex(boolean var1);

        public <T> Builder putHttpExecutionAttribute(SdkHttpExecutionAttribute<T> var1, T var2);

        public Builder httpExecutionAttributes(SdkHttpExecutionAttributes var1);

        public AsyncExecuteRequest build();
    }
}

