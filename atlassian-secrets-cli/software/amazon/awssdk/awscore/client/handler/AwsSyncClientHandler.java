/*
 * Decompiled with CFR 0.152.
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

    @Override
    public <InputT extends SdkRequest, OutputT extends SdkResponse> OutputT execute(ClientExecutionParams<InputT, OutputT> executionParams) {
        ClientExecutionParams<InputT, OutputT> clientExecutionParams = this.addCrc32Validation(executionParams);
        return super.execute(clientExecutionParams);
    }

    @Override
    public <InputT extends SdkRequest, OutputT extends SdkResponse, ReturnT> ReturnT execute(ClientExecutionParams<InputT, OutputT> executionParams, ResponseTransformer<OutputT, ReturnT> responseTransformer) {
        return super.execute(executionParams, responseTransformer);
    }

    @Override
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

        @Override
        public T handle(SdkHttpFullResponse response, ExecutionAttributes executionAttributes) throws Exception {
            return this.delegate.handle(Crc32Validation.validate(AwsSyncClientHandler.this.isCalculateCrc32FromCompressedData(), response), executionAttributes);
        }
    }
}

