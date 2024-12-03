/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.core.internal.http.pipeline.stages;

import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.internal.http.RequestExecutionContext;
import software.amazon.awssdk.core.internal.http.pipeline.RequestPipeline;
import software.amazon.awssdk.http.SdkHttpFullRequest;

@SdkInternalApi
public class MakeRequestImmutableStage
implements RequestPipeline<SdkHttpFullRequest.Builder, SdkHttpFullRequest> {
    @Override
    public SdkHttpFullRequest execute(SdkHttpFullRequest.Builder input, RequestExecutionContext context) throws Exception {
        return input.build();
    }
}

