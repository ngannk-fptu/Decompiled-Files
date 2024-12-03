/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.core.internal.interceptor;

import java.util.Optional;
import java.util.concurrent.CompletionException;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.SdkRequest;
import software.amazon.awssdk.core.SdkResponse;
import software.amazon.awssdk.core.interceptor.Context;
import software.amazon.awssdk.core.interceptor.InterceptorContext;
import software.amazon.awssdk.http.SdkHttpRequest;
import software.amazon.awssdk.http.SdkHttpResponse;
import software.amazon.awssdk.utils.Validate;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

@SdkInternalApi
public class DefaultFailedExecutionContext
implements Context.FailedExecution,
ToCopyableBuilder<Builder, DefaultFailedExecutionContext> {
    private final InterceptorContext interceptorContext;
    private final Throwable exception;

    private DefaultFailedExecutionContext(Builder builder) {
        this.exception = this.unwrap(Validate.paramNotNull(builder.exception, "exception"));
        this.interceptorContext = Validate.paramNotNull(builder.interceptorContext, "interceptorContext");
    }

    private Throwable unwrap(Throwable exception) {
        while (exception instanceof CompletionException) {
            exception = exception.getCause();
        }
        return exception;
    }

    @Override
    public SdkRequest request() {
        return this.interceptorContext.request();
    }

    @Override
    public Optional<SdkHttpRequest> httpRequest() {
        return Optional.ofNullable(this.interceptorContext.httpRequest());
    }

    @Override
    public Optional<SdkHttpResponse> httpResponse() {
        return Optional.ofNullable(this.interceptorContext.httpResponse());
    }

    @Override
    public Optional<SdkResponse> response() {
        return Optional.ofNullable(this.interceptorContext.response());
    }

    @Override
    public Throwable exception() {
        return this.exception;
    }

    @Override
    public Builder toBuilder() {
        return new Builder(this);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder
    implements CopyableBuilder<Builder, DefaultFailedExecutionContext> {
        private InterceptorContext interceptorContext;
        private Throwable exception;

        private Builder() {
        }

        private Builder(DefaultFailedExecutionContext defaultFailedExecutionContext) {
            this.exception = defaultFailedExecutionContext.exception;
            this.interceptorContext = defaultFailedExecutionContext.interceptorContext;
        }

        public Builder exception(Throwable exception) {
            this.exception = exception;
            return this;
        }

        public Builder interceptorContext(InterceptorContext interceptorContext) {
            this.interceptorContext = interceptorContext;
            return this;
        }

        @Override
        public DefaultFailedExecutionContext build() {
            return new DefaultFailedExecutionContext(this);
        }
    }
}

