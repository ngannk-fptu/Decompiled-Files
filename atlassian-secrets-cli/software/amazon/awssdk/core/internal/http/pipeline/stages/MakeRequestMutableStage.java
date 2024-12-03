/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.core.internal.http.pipeline.stages;

import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.internal.http.RequestExecutionContext;
import software.amazon.awssdk.core.internal.http.pipeline.RequestPipeline;
import software.amazon.awssdk.http.SdkHttpFullRequest;

@SdkInternalApi
public class MakeRequestMutableStage
implements RequestPipeline<SdkHttpFullRequest, SdkHttpFullRequest.Builder> {
    @Override
    public SdkHttpFullRequest.Builder execute(SdkHttpFullRequest input, RequestExecutionContext context) throws Exception {
        return input.toBuilder();
    }
}

