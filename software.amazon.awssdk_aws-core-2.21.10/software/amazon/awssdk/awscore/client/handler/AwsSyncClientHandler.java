/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.Immutable
 *  software.amazon.awssdk.annotations.SdkProtectedApi
 *  software.amazon.awssdk.annotations.ThreadSafe
 *  software.amazon.awssdk.core.SdkRequest
 *  software.amazon.awssdk.core.SdkResponse
 *  software.amazon.awssdk.core.client.config.SdkClientConfiguration
 *  software.amazon.awssdk.core.client.handler.ClientExecutionParams
 *  software.amazon.awssdk.core.client.handler.SdkSyncClientHandler
 *  software.amazon.awssdk.core.client.handler.SyncClientHandler
 *  software.amazon.awssdk.core.http.Crc32Validation
 *  software.amazon.awssdk.core.http.ExecutionContext
 *  software.amazon.awssdk.core.http.HttpResponseHandler
 *  software.amazon.awssdk.core.interceptor.ExecutionAttributes
 *  software.amazon.awssdk.core.sync.ResponseTransformer
 *  software.amazon.awssdk.http.SdkHttpFullResponse
 */
package software.amazon.awssdk.awscore.client.handler;

import software.amazon.awssdk.annotations.Immutable;
import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.annotations.ThreadSafe;
import software.amazon.awssdk.awscore.internal.AwsExecutionContextBuilder;
import software.amazon.awssdk.awscore.internal.client.config.AwsClientOptionValidation;
import software.amazon.awssdk.core.SdkRequest;
import software.amazon.awssdk.core.SdkResponse;
import software.amazon.awssdk.core.client.config.SdkClientConfiguration;
import software.amazon.awssdk.core.client.handler.ClientExecutionParams;
import software.amazon.awssdk.core.client.handler.SdkSyncClientHandler;
import software.amazon.awssdk.core.client.handler.SyncClientHandler;
import software.amazon.awssdk.core.http.Crc32Validation;
import software.amazon.awssdk.core.http.ExecutionContext;
import software.amazon.awssdk.core.http.HttpResponseHandler;
import software.amazon.awssdk.core.interceptor.ExecutionAttributes;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.http.SdkHttpFullResponse;

@ThreadSafe
@Immutable
@SdkProtectedApi
public final class AwsSyncClientHandler
extends SdkSyncClientHandler
implements SyncClientHandler {
    public AwsSyncClientHandler(SdkClientConfiguration clientConfiguration) {
        super(clientConfiguration);
        AwsClientOptionValidation.validateSyncClientOptions(clientConfiguration);
    }

    public <InputT extends SdkRequest, OutputT extends SdkResponse> OutputT execute(ClientExecutionParams<InputT, OutputT> executionParams) {
        ClientExecutionParams<InputT, OutputT> clientExecutionParams = this.addCrc32Validation(executionParams);
        return (OutputT)super.execute(clientExecutionParams);
    }

    public <InputT extends SdkRequest, OutputT extends SdkResponse, ReturnT> ReturnT execute(ClientExecutionParams<InputT, OutputT> executionParams, ResponseTransformer<OutputT, ReturnT> responseTransformer) {
        return (ReturnT)super.execute(executionParams, responseTransformer);
    }

    protected <InputT extends SdkRequest, OutputT extends SdkResponse> ExecutionContext invokeInterceptorsAndCreateExecutionContext(ClientExecutionParams<InputT, OutputT> executionParams) {
        SdkClientConfiguration clientConfiguration = this.resolveRequestConfiguration(executionParams);
        return AwsExecutionContextBuilder.invokeInterceptorsAndCreateExecutionContext(executionParams, clientConfiguration);
    }

    private <InputT extends SdkRequest, OutputT> ClientExecutionParams<InputT, OutputT> addCrc32Validation(ClientExecutionParams<InputT, OutputT> executionParams) {
        if (executionParams.getCombinedResponseHandler() != null) {
            return executionParams.withCombinedResponseHandler(new Crc32ValidationResponseHandler(executionParams.getCombinedResponseHandler()));
        }
        return executionParams.withResponseHandler(new Crc32ValidationResponseHandler(executionParams.getResponseHandler()));
    }

    private class Crc32ValidationResponseHandler<T>
    implements HttpResponseHandler<T> {
        private final HttpResponseHandler<T> delegate;

        private Crc32ValidationResponseHandler(HttpResponseHandler<T> delegate) {
            this.delegate = delegate;
        }

        public T handle(SdkHttpFullResponse response, ExecutionAttributes executionAttributes) throws Exception {
            return (T)this.delegate.handle(Crc32Validation.validate((boolean)AwsSyncClientHandler.this.isCalculateCrc32FromCompressedData(), (SdkHttpFullResponse)response), executionAttributes);
        }
    }
}

