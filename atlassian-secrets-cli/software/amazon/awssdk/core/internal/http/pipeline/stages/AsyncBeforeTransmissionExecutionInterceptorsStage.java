/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.core.internal.http.pipeline.stages;

import java.util.concurrent.CompletableFuture;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.internal.http.RequestExecutionContext;
import software.amazon.awssdk.core.internal.http.pipeline.RequestPipeline;
import software.amazon.awssdk.http.SdkHttpFullRequest;
import software.amazon.awssdk.utils.CompletableFutureUtils;

@SdkInternalApi
public class AsyncBeforeTransmissionExecutionInterceptorsStage
implements RequestPipeline<CompletableFuture<SdkHttpFullRequest>, CompletableFuture<SdkHttpFullRequest>> {
    @Override
    public CompletableFuture<SdkHttpFullRequest> execute(CompletableFuture<SdkHttpFullRequest> input, RequestExecutionContext context) throws Exception {
        CompletableFuture future = new CompletableFuture();
        input.whenComplete((r, t) -> {
            if (t != null) {
                future.completeExceptionally((Throwable)t);
                return;
            }
            try {
                context.interceptorChain().beforeTransmission(context.executionContext().interceptorContext(), context.executionAttributes());
                future.complete(r);
            }
            catch (Throwable interceptorException) {
                future.completeExceptionally(interceptorException);
            }
        });
        return CompletableFutureUtils.forwardExceptionTo(future, input);
    }
}

