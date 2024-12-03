/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.core.internal.http.pipeline.stages;

import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.interceptor.InterceptorContext;
import software.amazon.awssdk.core.internal.http.InterruptMonitor;
import software.amazon.awssdk.core.internal.http.RequestExecutionContext;
import software.amazon.awssdk.core.internal.http.pipeline.RequestPipeline;
import software.amazon.awssdk.http.AbortableInputStream;
import software.amazon.awssdk.http.SdkHttpFullRequest;
import software.amazon.awssdk.http.SdkHttpFullResponse;
import software.amazon.awssdk.http.SdkHttpResponse;
import software.amazon.awssdk.utils.Pair;

@SdkInternalApi
public class AfterTransmissionExecutionInterceptorsStage
implements RequestPipeline<Pair<SdkHttpFullRequest, SdkHttpFullResponse>, Pair<SdkHttpFullRequest, SdkHttpFullResponse>> {
    @Override
    public Pair<SdkHttpFullRequest, SdkHttpFullResponse> execute(Pair<SdkHttpFullRequest, SdkHttpFullResponse> input, RequestExecutionContext context) throws Exception {
        InterruptMonitor.checkInterrupted(input.right());
        InterceptorContext interceptorContext = (InterceptorContext)context.executionContext().interceptorContext().copy(b -> b.httpResponse((SdkHttpResponse)input.right()).responseBody(((SdkHttpFullResponse)input.right()).content().orElse(null)));
        context.interceptorChain().afterTransmission(interceptorContext, context.executionAttributes());
        interceptorContext = context.interceptorChain().modifyHttpResponse(interceptorContext, context.executionAttributes());
        context.executionContext().interceptorContext(interceptorContext);
        InterruptMonitor.checkInterrupted((SdkHttpFullResponse)interceptorContext.httpResponse());
        SdkHttpFullResponse response = (SdkHttpFullResponse)interceptorContext.httpResponse();
        if (interceptorContext.responseBody().isPresent()) {
            response = response.toBuilder().content(AbortableInputStream.create(interceptorContext.responseBody().get())).build();
        }
        return Pair.of(input.left(), response);
    }
}

