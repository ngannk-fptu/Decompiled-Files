/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.http.SdkHttpFullRequest
 */
package software.amazon.awssdk.core.internal.http.pipeline.stages;

import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.internal.http.InterruptMonitor;
import software.amazon.awssdk.core.internal.http.RequestExecutionContext;
import software.amazon.awssdk.core.internal.http.pipeline.RequestToRequestPipeline;
import software.amazon.awssdk.http.SdkHttpFullRequest;

@SdkInternalApi
public class BeforeTransmissionExecutionInterceptorsStage
implements RequestToRequestPipeline {
    @Override
    public SdkHttpFullRequest execute(SdkHttpFullRequest input, RequestExecutionContext context) throws Exception {
        InterruptMonitor.checkInterrupted();
        context.interceptorChain().beforeTransmission(context.executionContext().interceptorContext(), context.executionAttributes());
        return input;
    }
}

