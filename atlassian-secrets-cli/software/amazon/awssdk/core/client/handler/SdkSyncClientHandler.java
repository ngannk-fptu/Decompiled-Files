/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.core.client.handler;

import software.amazon.awssdk.annotations.Immutable;
import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.annotations.ThreadSafe;
import software.amazon.awssdk.core.SdkRequest;
import software.amazon.awssdk.core.SdkResponse;
import software.amazon.awssdk.core.client.config.SdkClientConfiguration;
import software.amazon.awssdk.core.client.config.SdkClientOptionValidation;
import software.amazon.awssdk.core.client.handler.ClientExecutionParams;
import software.amazon.awssdk.core.client.handler.SyncClientHandler;
import software.amazon.awssdk.core.internal.handler.BaseSyncClientHandler;
import software.amazon.awssdk.core.internal.http.AmazonSyncHttpClient;
import software.amazon.awssdk.core.sync.ResponseTransformer;

@ThreadSafe
@Immutable
@SdkProtectedApi
public class SdkSyncClientHandler
extends BaseSyncClientHandler
implements SyncClientHandler {
    protected SdkSyncClientHandler(SdkClientConfiguration clientConfiguration) {
        super(clientConfiguration, new AmazonSyncHttpClient(clientConfiguration));
        SdkClientOptionValidation.validateSyncClientOptions(clientConfiguration);
    }

    @Override
    public <InputT extends SdkRequest, OutputT extends SdkResponse> OutputT execute(ClientExecutionParams<InputT, OutputT> executionParams) {
        return super.execute(executionParams);
    }

    @Override
    public <InputT extends SdkRequest, OutputT extends SdkResponse, ReturnT> ReturnT execute(ClientExecutionParams<InputT, OutputT> executionParams, ResponseTransformer<OutputT, ReturnT> responseTransformer) {
        return super.execute(executionParams, responseTransformer);
    }
}

