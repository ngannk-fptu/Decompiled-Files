/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.core.internal.http.pipeline.stages;

import java.util.Random;
import java.util.UUID;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.internal.http.InterruptMonitor;
import software.amazon.awssdk.core.internal.http.RequestExecutionContext;
import software.amazon.awssdk.core.internal.http.pipeline.MutableRequestToRequestPipeline;
import software.amazon.awssdk.http.SdkHttpFullRequest;

@SdkInternalApi
public class ApplyTransactionIdStage
implements MutableRequestToRequestPipeline {
    public static final String HEADER_SDK_TRANSACTION_ID = "amz-sdk-invocation-id";
    private final Random random = new Random();

    @Override
    public SdkHttpFullRequest.Builder execute(SdkHttpFullRequest.Builder request, RequestExecutionContext context) throws Exception {
        InterruptMonitor.checkInterrupted();
        return request.putHeader(HEADER_SDK_TRANSACTION_ID, new UUID(this.random.nextLong(), this.random.nextLong()).toString());
    }
}

