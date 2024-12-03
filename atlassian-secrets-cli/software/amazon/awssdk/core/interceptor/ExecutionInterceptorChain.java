/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.core.interceptor;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import org.reactivestreams.Publisher;
import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.core.SdkRequest;
import software.amazon.awssdk.core.SdkResponse;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.core.interceptor.Context;
import software.amazon.awssdk.core.interceptor.ExecutionAttributes;
import software.amazon.awssdk.core.interceptor.ExecutionInterceptor;
import software.amazon.awssdk.core.interceptor.InterceptorContext;
import software.amazon.awssdk.core.internal.interceptor.DefaultFailedExecutionContext;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.http.ContentStreamProvider;
import software.amazon.awssdk.http.SdkHttpFullRequest;
import software.amazon.awssdk.http.SdkHttpRequest;
import software.amazon.awssdk.http.SdkHttpResponse;
import software.amazon.awssdk.utils.Logger;
import software.amazon.awssdk.utils.Validate;

@SdkProtectedApi
public class ExecutionInterceptorChain {
    private static final Logger LOG = Logger.loggerFor(ExecutionInterceptorChain.class);
    private final List<ExecutionInterceptor> interceptors;

    public ExecutionInterceptorChain(List<ExecutionInterceptor> interceptors) {
        this.interceptors = new ArrayList<ExecutionInterceptor>((Collection)Validate.paramNotNull(interceptors, "interceptors"));
        LOG.debug(() -> "Creating an interceptor chain that will apply interceptors in the following order: " + interceptors);
    }

    public void beforeExecution(Context.BeforeExecution context, ExecutionAttributes executionAttributes) {
        this.interceptors.forEach(i -> i.beforeExecution(context, executionAttributes));
    }

    public InterceptorContext modifyRequest(InterceptorContext context, ExecutionAttributes executionAttributes) {
        InterceptorContext result = context;
        for (ExecutionInterceptor interceptor : this.interceptors) {
            SdkRequest interceptorResult = interceptor.modifyRequest(result, executionAttributes);
            this.validateInterceptorResult(result.request(), interceptorResult, interceptor, "modifyRequest");
            result = (InterceptorContext)result.copy(b -> b.request(interceptorResult));
        }
        return result;
    }

    public void beforeMarshalling(Context.BeforeMarshalling context, ExecutionAttributes executionAttributes) {
        this.interceptors.forEach(i -> i.beforeMarshalling(context, executionAttributes));
    }

    public void afterMarshalling(Context.AfterMarshalling context, ExecutionAttributes executionAttributes) {
        this.interceptors.forEach(i -> i.afterMarshalling(context, executionAttributes));
    }

    public InterceptorContext modifyHttpRequestAndHttpContent(InterceptorContext context, ExecutionAttributes executionAttributes) {
        InterceptorContext result = context;
        for (ExecutionInterceptor interceptor : this.interceptors) {
            AsyncRequestBody asyncRequestBody = interceptor.modifyAsyncHttpContent(result, executionAttributes).orElse(null);
            RequestBody requestBody = interceptor.modifyHttpContent(result, executionAttributes).orElse(null);
            SdkHttpRequest interceptorResult = interceptor.modifyHttpRequest(result, executionAttributes);
            this.validateInterceptorResult(result.httpRequest(), interceptorResult, interceptor, "modifyHttpRequest");
            InterceptorContext.Builder builder = result.toBuilder();
            this.applySdkHttpFullRequestHack(result, builder);
            result = builder.httpRequest(interceptorResult).asyncRequestBody(asyncRequestBody).requestBody(requestBody).build();
        }
        return result;
    }

    private void applySdkHttpFullRequestHack(InterceptorContext context, InterceptorContext.Builder builder) {
        SdkHttpFullRequest sdkHttpFullRequest = (SdkHttpFullRequest)context.httpRequest();
        if (context.requestBody().isPresent()) {
            return;
        }
        Optional<ContentStreamProvider> contentStreamProvider = sdkHttpFullRequest.contentStreamProvider();
        if (!contentStreamProvider.isPresent()) {
            return;
        }
        long contentLength = Long.parseLong(sdkHttpFullRequest.firstMatchingHeader("Content-Length").orElse("0"));
        String contentType = sdkHttpFullRequest.firstMatchingHeader("Content-Type").orElse("");
        RequestBody requestBody = RequestBody.fromContentProvider(contentStreamProvider.get(), contentLength, contentType);
        builder.requestBody(requestBody);
    }

    public void beforeTransmission(Context.BeforeTransmission context, ExecutionAttributes executionAttributes) {
        this.interceptors.forEach(i -> i.beforeTransmission(context, executionAttributes));
    }

    public void afterTransmission(Context.AfterTransmission context, ExecutionAttributes executionAttributes) {
        this.reverseForEach(i -> i.afterTransmission(context, executionAttributes));
    }

    public InterceptorContext modifyHttpResponse(InterceptorContext context, ExecutionAttributes executionAttributes) {
        InterceptorContext result = context;
        for (int i = this.interceptors.size() - 1; i >= 0; --i) {
            SdkHttpResponse interceptorResult = this.interceptors.get(i).modifyHttpResponse(result, executionAttributes);
            this.validateInterceptorResult(result.httpResponse(), interceptorResult, this.interceptors.get(i), "modifyHttpResponse");
            InputStream response = this.interceptors.get(i).modifyHttpResponseContent(result, executionAttributes).orElse(null);
            result = result.toBuilder().httpResponse(interceptorResult).responseBody(response).build();
        }
        return result;
    }

    public InterceptorContext modifyAsyncHttpResponse(InterceptorContext context, ExecutionAttributes executionAttributes) {
        InterceptorContext result = context;
        for (int i = this.interceptors.size() - 1; i >= 0; --i) {
            ExecutionInterceptor interceptor = this.interceptors.get(i);
            Publisher newResponsePublisher = interceptor.modifyAsyncHttpResponseContent(result, executionAttributes).orElse(null);
            result = result.toBuilder().responsePublisher(newResponsePublisher).build();
        }
        return result;
    }

    public void beforeUnmarshalling(Context.BeforeUnmarshalling context, ExecutionAttributes executionAttributes) {
        this.reverseForEach(i -> i.beforeUnmarshalling(context, executionAttributes));
    }

    public void afterUnmarshalling(Context.AfterUnmarshalling context, ExecutionAttributes executionAttributes) {
        this.reverseForEach(i -> i.afterUnmarshalling(context, executionAttributes));
    }

    public InterceptorContext modifyResponse(InterceptorContext context, ExecutionAttributes executionAttributes) {
        InterceptorContext result = context;
        for (int i = this.interceptors.size() - 1; i >= 0; --i) {
            SdkResponse interceptorResult = this.interceptors.get(i).modifyResponse(result, executionAttributes);
            this.validateInterceptorResult(result.response(), interceptorResult, this.interceptors.get(i), "modifyResponse");
            result = (InterceptorContext)result.copy(b -> b.response(interceptorResult));
        }
        return result;
    }

    public void afterExecution(Context.AfterExecution context, ExecutionAttributes executionAttributes) {
        this.reverseForEach(i -> i.afterExecution(context, executionAttributes));
    }

    public DefaultFailedExecutionContext modifyException(DefaultFailedExecutionContext context, ExecutionAttributes executionAttributes) {
        DefaultFailedExecutionContext result = context;
        for (int i = this.interceptors.size() - 1; i >= 0; --i) {
            Throwable interceptorResult = this.interceptors.get(i).modifyException(result, executionAttributes);
            this.validateInterceptorResult(result.exception(), interceptorResult, this.interceptors.get(i), "modifyException");
            result = (DefaultFailedExecutionContext)result.copy(b -> b.exception(interceptorResult));
        }
        return result;
    }

    public void onExecutionFailure(Context.FailedExecution context, ExecutionAttributes executionAttributes) {
        this.interceptors.forEach(i -> i.onExecutionFailure(context, executionAttributes));
    }

    private void validateInterceptorResult(Object originalMessage, Object newMessage, ExecutionInterceptor interceptor, String methodName) {
        if (!Objects.equals(originalMessage, newMessage)) {
            LOG.debug(() -> "Interceptor '" + interceptor + "' modified the message with its " + methodName + " method.");
            LOG.trace(() -> "Old: " + originalMessage + "\nNew: " + newMessage);
        }
        Validate.validState(newMessage != null, "Request interceptor '%s' returned null from its %s interceptor.", interceptor, methodName);
        Validate.isInstanceOf(originalMessage.getClass(), newMessage, "Request interceptor '%s' returned '%s' from its %s method, but '%s' was expected.", interceptor, newMessage.getClass(), methodName, originalMessage.getClass());
    }

    private void reverseForEach(Consumer<ExecutionInterceptor> action) {
        for (int i = this.interceptors.size() - 1; i >= 0; --i) {
            action.accept(this.interceptors.get(i));
        }
    }
}

