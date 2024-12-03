/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 */
package software.amazon.awssdk.core.internal.http.pipeline.stages;

import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.Response;
import software.amazon.awssdk.core.internal.http.RequestExecutionContext;
import software.amazon.awssdk.core.internal.http.pipeline.RequestPipeline;

@SdkInternalApi
public class UnwrapResponseContainer<OutputT>
implements RequestPipeline<Response<OutputT>, OutputT> {
    @Override
    public OutputT execute(Response<OutputT> input, RequestExecutionContext context) throws Exception {
        return input.response();
    }
}

