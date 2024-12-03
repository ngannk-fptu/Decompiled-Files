/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.core.client.handler;

import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.core.SdkRequest;
import software.amazon.awssdk.core.SdkResponse;
import software.amazon.awssdk.core.client.handler.ClientExecutionParams;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.utils.SdkAutoCloseable;

@SdkProtectedApi
public interface SyncClientHandler
extends SdkAutoCloseable {
    public <InputT extends SdkRequest, OutputT extends SdkResponse> OutputT execute(ClientExecutionParams<InputT, OutputT> var1);

    public <InputT extends SdkRequest, OutputT extends SdkResponse, ReturnT> ReturnT execute(ClientExecutionParams<InputT, OutputT> var1, ResponseTransformer<OutputT, ReturnT> var2);
}

