/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.NotThreadSafe
 *  software.amazon.awssdk.annotations.SdkProtectedApi
 *  software.amazon.awssdk.metrics.MetricCollector
 *  software.amazon.awssdk.utils.builder.CopyableBuilder
 *  software.amazon.awssdk.utils.builder.ToCopyableBuilder
 */
package software.amazon.awssdk.core.http;

import software.amazon.awssdk.annotations.NotThreadSafe;
import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.core.interceptor.ExecutionAttributes;
import software.amazon.awssdk.core.interceptor.ExecutionInterceptorChain;
import software.amazon.awssdk.core.interceptor.InterceptorContext;
import software.amazon.awssdk.core.signer.Signer;
import software.amazon.awssdk.metrics.MetricCollector;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

@NotThreadSafe
@SdkProtectedApi
public final class ExecutionContext
implements ToCopyableBuilder<Builder, ExecutionContext> {
    private final Signer signer;
    private InterceptorContext interceptorContext;
    private final ExecutionInterceptorChain interceptorChain;
    private final ExecutionAttributes executionAttributes;
    private final MetricCollector metricCollector;

    private ExecutionContext(Builder builder) {
        this.signer = builder.signer;
        this.interceptorContext = builder.interceptorContext;
        this.interceptorChain = builder.interceptorChain;
        this.executionAttributes = builder.executionAttributes;
        this.metricCollector = builder.metricCollector;
    }

    public static Builder builder() {
        return new Builder();
    }

    public InterceptorContext interceptorContext() {
        return this.interceptorContext;
    }

    public ExecutionContext interceptorContext(InterceptorContext interceptorContext) {
        this.interceptorContext = interceptorContext;
        return this;
    }

    public ExecutionInterceptorChain interceptorChain() {
        return this.interceptorChain;
    }

    public ExecutionAttributes executionAttributes() {
        return this.executionAttributes;
    }

    public Signer signer() {
        return this.signer;
    }

    public MetricCollector metricCollector() {
        return this.metricCollector;
    }

    public Builder toBuilder() {
        return new Builder(this);
    }

    public static class Builder
    implements CopyableBuilder<Builder, ExecutionContext> {
        private InterceptorContext interceptorContext;
        private ExecutionInterceptorChain interceptorChain;
        private ExecutionAttributes executionAttributes;
        private Signer signer;
        private MetricCollector metricCollector;

        private Builder() {
        }

        private Builder(ExecutionContext executionContext) {
            this.signer = executionContext.signer;
            this.interceptorContext = executionContext.interceptorContext;
            this.interceptorChain = executionContext.interceptorChain;
            this.executionAttributes = executionContext.executionAttributes;
            this.metricCollector = executionContext.metricCollector;
        }

        public Builder interceptorContext(InterceptorContext interceptorContext) {
            this.interceptorContext = interceptorContext;
            return this;
        }

        public Builder interceptorChain(ExecutionInterceptorChain interceptorChain) {
            this.interceptorChain = interceptorChain;
            return this;
        }

        public Builder executionAttributes(ExecutionAttributes executionAttributes) {
            this.executionAttributes = executionAttributes;
            return this;
        }

        public Builder signer(Signer signer) {
            this.signer = signer;
            return this;
        }

        public Builder metricCollector(MetricCollector metricCollector) {
            this.metricCollector = metricCollector;
            return this;
        }

        public ExecutionContext build() {
            return new ExecutionContext(this);
        }
    }
}

