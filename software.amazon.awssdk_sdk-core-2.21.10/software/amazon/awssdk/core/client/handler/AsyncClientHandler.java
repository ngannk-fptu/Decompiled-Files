/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkProtectedApi
 *  software.amazon.awssdk.utils.SdkAutoCloseable
 */
package software.amazon.awssdk.core.client.handler;

import java.util.concurrent.CompletableFuture;
import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.core.SdkRequest;
import software.amazon.awssdk.core.SdkResponse;
import software.amazon.awssdk.core.async.AsyncResponseTransformer;
import software.amazon.awssdk.core.client.handler.ClientExecutionParams;
import software.amazon.awssdk.utils.SdkAutoCloseable;

@SdkProtectedApi
public interface AsyncClientHandler
extends SdkAutoCloseable {
    public <InputT extends SdkRequest, OutputT extends SdkResponse> CompletableFuture<OutputT> execute(ClientExecutionParams<InputT, OutputT> var1);

    public <InputT extends SdkRequest, OutputT extends SdkResponse, ReturnT> CompletableFuture<ReturnT> execute(ClientExecutionParams<InputT, OutputT> var1, AsyncResponseTransformer<OutputT, ReturnT> var2);
}

