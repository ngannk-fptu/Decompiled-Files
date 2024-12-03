/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.core.retry;

import software.amazon.awssdk.annotations.Immutable;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.core.SdkRequest;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.core.interceptor.ExecutionAttributes;
import software.amazon.awssdk.http.SdkHttpFullRequest;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

@Immutable
@SdkPublicApi
public final class RetryPolicyContext
implements ToCopyableBuilder<Builder, RetryPolicyContext> {
    private final SdkRequest originalRequest;
    private final SdkHttpFullRequest request;
    private final SdkException exception;
    private final ExecutionAttributes executionAttributes;
    private final int retriesAttempted;
    private final Integer httpStatusCode;

    private RetryPolicyContext(Builder builder) {
        this.originalRequest = builder.originalRequest;
        this.request = builder.request;
        this.exception = builder.exception;
        this.executionAttributes = builder.executionAttributes;
        this.retriesAttempted = builder.retriesAttempted;
        this.httpStatusCode = builder.httpStatusCode;
    }

    public static Builder builder() {
        return new Builder();
    }

    public SdkRequest originalRequest() {
        return this.originalRequest;
    }

    public SdkHttpFullRequest request() {
        return this.request;
    }

    public SdkException exception() {
        return this.exception;
    }

    public ExecutionAttributes executionAttributes() {
        return this.executionAttributes;
    }

    public int retriesAttempted() {
        return this.retriesAttempted;
    }

    public int totalRequests() {
        return this.retriesAttempted + 1;
    }

    public Integer httpStatusCode() {
        return this.httpStatusCode;
    }

    @Override
    public Builder toBuilder() {
        return new Builder(this);
    }

    @SdkPublicApi
    public static final class Builder
    implements CopyableBuilder<Builder, RetryPolicyContext> {
        private SdkRequest originalRequest;
        private SdkHttpFullRequest request;
        private SdkException exception;
        private ExecutionAttributes executionAttributes;
        private int retriesAttempted;
        private Integer httpStatusCode;

        private Builder() {
        }

        private Builder(RetryPolicyContext copy) {
            this.originalRequest = copy.originalRequest;
            this.request = copy.request;
            this.exception = copy.exception;
            this.executionAttributes = copy.executionAttributes;
            this.retriesAttempted = copy.retriesAttempted;
            this.httpStatusCode = copy.httpStatusCode;
        }

        public Builder originalRequest(SdkRequest originalRequest) {
            this.originalRequest = originalRequest;
            return this;
        }

        public Builder request(SdkHttpFullRequest request) {
            this.request = request;
            return this;
        }

        public Builder exception(SdkException exception) {
            this.exception = exception;
            return this;
        }

        public Builder executionAttributes(ExecutionAttributes executionAttributes) {
            this.executionAttributes = executionAttributes;
            return this;
        }

        public Builder retriesAttempted(int retriesAttempted) {
            this.retriesAttempted = retriesAttempted;
            return this;
        }

        public Builder httpStatusCode(Integer httpStatusCode) {
            this.httpStatusCode = httpStatusCode;
            return this;
        }

        @Override
        public RetryPolicyContext build() {
            return new RetryPolicyContext(this);
        }
    }
}

