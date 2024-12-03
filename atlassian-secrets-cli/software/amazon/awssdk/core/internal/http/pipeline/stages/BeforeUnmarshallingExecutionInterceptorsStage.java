/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.core.internal.http.pipeline.stages;

import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.internal.http.InterruptMonitor;
import software.amazon.awssdk.core.internal.http.RequestExecutionContext;
import software.amazon.awssdk.core.internal.http.pipeline.RequestPipeline;
import software.amazon.awssdk.http.SdkHttpFullRequest;
import software.amazon.awssdk.http.SdkHttpFullResponse;
import software.amazon.awssdk.utils.Pair;

@SdkInternalApi
public class BeforeUnmarshallingExecutionInterceptorsStage
implements RequestPipeline<Pair<SdkHttpFullRequest, SdkHttpFullResponse>, SdkHttpFullResponse> {
    @Override
    public SdkHttpFullResponse execute(Pair<SdkHttpFullRequest, SdkHttpFullResponse> input, RequestExecutionContext context) throws Exception {
        context.interceptorChain().beforeUnmarshalling(context.executionContext().interceptorContext(), context.executionAttributes());
        InterruptMonitor.checkInterrupted(input.right());
        return input.right();
    }
}

