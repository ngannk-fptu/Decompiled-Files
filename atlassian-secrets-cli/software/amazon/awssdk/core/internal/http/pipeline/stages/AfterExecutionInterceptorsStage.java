/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.core.internal.http.pipeline.stages;

import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.internal.http.RequestExecutionContext;
import software.amazon.awssdk.core.internal.http.pipeline.RequestPipeline;

@SdkInternalApi
public class AfterExecutionInterceptorsStage<OutputT>
implements RequestPipeline<OutputT, OutputT> {
    @Override
    public OutputT execute(OutputT input, RequestExecutionContext context) throws Exception {
        context.interceptorChain().afterExecution(context.executionContext().interceptorContext(), context.executionAttributes());
        return input;
    }
}

