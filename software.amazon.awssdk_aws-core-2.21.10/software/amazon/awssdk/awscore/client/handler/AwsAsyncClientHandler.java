/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.Immutable
 *  software.amazon.awssdk.annotations.SdkProtectedApi
 *  software.amazon.awssdk.annotations.ThreadSafe
 *  software.amazon.awssdk.core.SdkRequest
 *  software.amazon.awssdk.core.SdkResponse
 *  software.amazon.awssdk.core.async.AsyncResponseTransformer
 *  software.amazon.awssdk.core.client.config.SdkClientConfiguration
 *  software.amazon.awssdk.core.client.handler.AsyncClientHandler
 *  software.amazon.awssdk.core.client.handler.ClientExecutionParams
 *  software.amazon.awssdk.core.client.handler.SdkAsyncClientHandler
 *  software.amazon.awssdk.core.http.ExecutionContext
 */
package software.amazon.awssdk.awscore.client.handler;

import java.util.concurrent.CompletableFuture;
import software.amazon.awssdk.annotations.Immutable;
import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.annotations.ThreadSafe;
import software.amazon.awssdk.awscore.internal.AwsExecutionContextBuilder;
import software.amazon.awssdk.awscore.internal.client.config.AwsClientOptionValidation;
import software.amazon.awssdk.core.SdkRequest;
import software.amazon.awssdk.core.SdkResponse;
import software.amazon.awssdk.core.async.AsyncResponseTransformer;
import software.amazon.awssdk.core.client.config.SdkClientConfiguration;
import software.amazon.awssdk.core.client.handler.AsyncClientHandler;
import software.amazon.awssdk.core.client.handler.ClientExecutionParams;
import software.amazon.awssdk.core.client.handler.SdkAsyncClientHandler;
import software.amazon.awssdk.core.http.ExecutionContext;

@ThreadSafe
@Immutable
@SdkProtectedApi
public final class AwsAsyncClientHandler
extends SdkAsyncClientHandler
implements AsyncClientHandler {
    public AwsAsyncClientHandler(SdkClientConfiguration clientConfiguration) {
        super(clientConfiguration);
        AwsClientOptionValidation.validateAsyncClientOptions(clientConfiguration);
    }

    public <InputT extends SdkRequest, OutputT extends SdkResponse> CompletableFuture<OutputT> execute(ClientExecutionParams<InputT, OutputT> executionParams) {
        return super.execute(executionParams);
    }

    public <InputT extends SdkRequest, OutputT extends SdkResponse, ReturnT> CompletableFuture<ReturnT> execute(ClientExecutionParams<InputT, OutputT> executionParams, AsyncResponseTransformer<OutputT, ReturnT> asyncResponseTransformer) {
        return super.execute(executionParams, asyncResponseTransformer);
    }

    protected <InputT extends SdkRequest, OutputT extends SdkResponse> ExecutionContext invokeInterceptorsAndCreateExecutionContext(ClientExecutionParams<InputT, OutputT> executionParams) {
        SdkClientConfiguration clientConfiguration = this.resolveRequestConfiguration(executionParams);
        return AwsExecutionContextBuilder.invokeInterceptorsAndCreateExecutionContext(executionParams, clientConfiguration);
    }
}

