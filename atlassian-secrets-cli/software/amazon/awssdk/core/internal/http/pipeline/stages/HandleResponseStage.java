/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.core.internal.http.pipeline.stages;

import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.Response;
import software.amazon.awssdk.core.http.HttpResponseHandler;
import software.amazon.awssdk.core.internal.http.RequestExecutionContext;
import software.amazon.awssdk.core.internal.http.pipeline.RequestPipeline;
import software.amazon.awssdk.http.SdkHttpFullResponse;

@SdkInternalApi
public class HandleResponseStage<OutputT>
implements RequestPipeline<SdkHttpFullResponse, Response<OutputT>> {
    private final HttpResponseHandler<Response<OutputT>> responseHandler;

    public HandleResponseStage(HttpResponseHandler<Response<OutputT>> responseHandler) {
        this.responseHandler = responseHandler;
    }

    @Override
    public Response<OutputT> execute(SdkHttpFullResponse httpResponse, RequestExecutionContext context) throws Exception {
        return this.responseHandler.handle(httpResponse, context.executionAttributes());
    }
}

