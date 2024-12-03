/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.kms;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.annotation.ThreadSafe;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.client.AwsAsyncClientParams;
import com.amazonaws.handlers.AsyncHandler;
import com.amazonaws.services.kms.AWSKMSAsync;
import com.amazonaws.services.kms.AWSKMSAsyncClientBuilder;
import com.amazonaws.services.kms.AWSKMSClient;
import com.amazonaws.services.kms.model.CancelKeyDeletionRequest;
import com.amazonaws.services.kms.model.CancelKeyDeletionResult;
import com.amazonaws.services.kms.model.ConnectCustomKeyStoreRequest;
import com.amazonaws.services.kms.model.ConnectCustomKeyStoreResult;
import com.amazonaws.services.kms.model.CreateAliasRequest;
import com.amazonaws.services.kms.model.CreateAliasResult;
import com.amazonaws.services.kms.model.CreateCustomKeyStoreRequest;
import com.amazonaws.services.kms.model.CreateCustomKeyStoreResult;
import com.amazonaws.services.kms.model.CreateGrantRequest;
import com.amazonaws.services.kms.model.CreateGrantResult;
import com.amazonaws.services.kms.model.CreateKeyRequest;
import com.amazonaws.services.kms.model.CreateKeyResult;
import com.amazonaws.services.kms.model.DecryptRequest;
import com.amazonaws.services.kms.model.DecryptResult;
import com.amazonaws.services.kms.model.DeleteAliasRequest;
import com.amazonaws.services.kms.model.DeleteAliasResult;
import com.amazonaws.services.kms.model.DeleteCustomKeyStoreRequest;
import com.amazonaws.services.kms.model.DeleteCustomKeyStoreResult;
import com.amazonaws.services.kms.model.DeleteImportedKeyMaterialRequest;
import com.amazonaws.services.kms.model.DeleteImportedKeyMaterialResult;
import com.amazonaws.services.kms.model.DescribeCustomKeyStoresRequest;
import com.amazonaws.services.kms.model.DescribeCustomKeyStoresResult;
import com.amazonaws.services.kms.model.DescribeKeyRequest;
import com.amazonaws.services.kms.model.DescribeKeyResult;
import com.amazonaws.services.kms.model.DisableKeyRequest;
import com.amazonaws.services.kms.model.DisableKeyResult;
import com.amazonaws.services.kms.model.DisableKeyRotationRequest;
import com.amazonaws.services.kms.model.DisableKeyRotationResult;
import com.amazonaws.services.kms.model.DisconnectCustomKeyStoreRequest;
import com.amazonaws.services.kms.model.DisconnectCustomKeyStoreResult;
import com.amazonaws.services.kms.model.EnableKeyRequest;
import com.amazonaws.services.kms.model.EnableKeyResult;
import com.amazonaws.services.kms.model.EnableKeyRotationRequest;
import com.amazonaws.services.kms.model.EnableKeyRotationResult;
import com.amazonaws.services.kms.model.EncryptRequest;
import com.amazonaws.services.kms.model.EncryptResult;
import com.amazonaws.services.kms.model.GenerateDataKeyPairRequest;
import com.amazonaws.services.kms.model.GenerateDataKeyPairResult;
import com.amazonaws.services.kms.model.GenerateDataKeyPairWithoutPlaintextRequest;
import com.amazonaws.services.kms.model.GenerateDataKeyPairWithoutPlaintextResult;
import com.amazonaws.services.kms.model.GenerateDataKeyRequest;
import com.amazonaws.services.kms.model.GenerateDataKeyResult;
import com.amazonaws.services.kms.model.GenerateDataKeyWithoutPlaintextRequest;
import com.amazonaws.services.kms.model.GenerateDataKeyWithoutPlaintextResult;
import com.amazonaws.services.kms.model.GenerateMacRequest;
import com.amazonaws.services.kms.model.GenerateMacResult;
import com.amazonaws.services.kms.model.GenerateRandomRequest;
import com.amazonaws.services.kms.model.GenerateRandomResult;
import com.amazonaws.services.kms.model.GetKeyPolicyRequest;
import com.amazonaws.services.kms.model.GetKeyPolicyResult;
import com.amazonaws.services.kms.model.GetKeyRotationStatusRequest;
import com.amazonaws.services.kms.model.GetKeyRotationStatusResult;
import com.amazonaws.services.kms.model.GetParametersForImportRequest;
import com.amazonaws.services.kms.model.GetParametersForImportResult;
import com.amazonaws.services.kms.model.GetPublicKeyRequest;
import com.amazonaws.services.kms.model.GetPublicKeyResult;
import com.amazonaws.services.kms.model.ImportKeyMaterialRequest;
import com.amazonaws.services.kms.model.ImportKeyMaterialResult;
import com.amazonaws.services.kms.model.ListAliasesRequest;
import com.amazonaws.services.kms.model.ListAliasesResult;
import com.amazonaws.services.kms.model.ListGrantsRequest;
import com.amazonaws.services.kms.model.ListGrantsResult;
import com.amazonaws.services.kms.model.ListKeyPoliciesRequest;
import com.amazonaws.services.kms.model.ListKeyPoliciesResult;
import com.amazonaws.services.kms.model.ListKeysRequest;
import com.amazonaws.services.kms.model.ListKeysResult;
import com.amazonaws.services.kms.model.ListResourceTagsRequest;
import com.amazonaws.services.kms.model.ListResourceTagsResult;
import com.amazonaws.services.kms.model.ListRetirableGrantsRequest;
import com.amazonaws.services.kms.model.ListRetirableGrantsResult;
import com.amazonaws.services.kms.model.PutKeyPolicyRequest;
import com.amazonaws.services.kms.model.PutKeyPolicyResult;
import com.amazonaws.services.kms.model.ReEncryptRequest;
import com.amazonaws.services.kms.model.ReEncryptResult;
import com.amazonaws.services.kms.model.ReplicateKeyRequest;
import com.amazonaws.services.kms.model.ReplicateKeyResult;
import com.amazonaws.services.kms.model.RetireGrantRequest;
import com.amazonaws.services.kms.model.RetireGrantResult;
import com.amazonaws.services.kms.model.RevokeGrantRequest;
import com.amazonaws.services.kms.model.RevokeGrantResult;
import com.amazonaws.services.kms.model.ScheduleKeyDeletionRequest;
import com.amazonaws.services.kms.model.ScheduleKeyDeletionResult;
import com.amazonaws.services.kms.model.SignRequest;
import com.amazonaws.services.kms.model.SignResult;
import com.amazonaws.services.kms.model.TagResourceRequest;
import com.amazonaws.services.kms.model.TagResourceResult;
import com.amazonaws.services.kms.model.UntagResourceRequest;
import com.amazonaws.services.kms.model.UntagResourceResult;
import com.amazonaws.services.kms.model.UpdateAliasRequest;
import com.amazonaws.services.kms.model.UpdateAliasResult;
import com.amazonaws.services.kms.model.UpdateCustomKeyStoreRequest;
import com.amazonaws.services.kms.model.UpdateCustomKeyStoreResult;
import com.amazonaws.services.kms.model.UpdateKeyDescriptionRequest;
import com.amazonaws.services.kms.model.UpdateKeyDescriptionResult;
import com.amazonaws.services.kms.model.UpdatePrimaryRegionRequest;
import com.amazonaws.services.kms.model.UpdatePrimaryRegionResult;
import com.amazonaws.services.kms.model.VerifyMacRequest;
import com.amazonaws.services.kms.model.VerifyMacResult;
import com.amazonaws.services.kms.model.VerifyRequest;
import com.amazonaws.services.kms.model.VerifyResult;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@ThreadSafe
public class AWSKMSAsyncClient
extends AWSKMSClient
implements AWSKMSAsync {
    private static final int DEFAULT_THREAD_POOL_SIZE = 50;
    private final ExecutorService executorService;

    @Deprecated
    public AWSKMSAsyncClient() {
        this(DefaultAWSCredentialsProviderChain.getInstance());
    }

    @Deprecated
    public AWSKMSAsyncClient(ClientConfiguration clientConfiguration) {
        this((AWSCredentialsProvider)DefaultAWSCredentialsProviderChain.getInstance(), clientConfiguration, Executors.newFixedThreadPool(clientConfiguration.getMaxConnections()));
    }

    @Deprecated
    public AWSKMSAsyncClient(AWSCredentials awsCredentials) {
        this(awsCredentials, Executors.newFixedThreadPool(50));
    }

    @Deprecated
    public AWSKMSAsyncClient(AWSCredentials awsCredentials, ExecutorService executorService) {
        this(awsCredentials, configFactory.getConfig(), executorService);
    }

    @Deprecated
    public AWSKMSAsyncClient(AWSCredentials awsCredentials, ClientConfiguration clientConfiguration, ExecutorService executorService) {
        super(awsCredentials, clientConfiguration);
        this.executorService = executorService;
    }

    @Deprecated
    public AWSKMSAsyncClient(AWSCredentialsProvider awsCredentialsProvider) {
        this(awsCredentialsProvider, Executors.newFixedThreadPool(50));
    }

    @Deprecated
    public AWSKMSAsyncClient(AWSCredentialsProvider awsCredentialsProvider, ClientConfiguration clientConfiguration) {
        this(awsCredentialsProvider, clientConfiguration, Executors.newFixedThreadPool(clientConfiguration.getMaxConnections()));
    }

    @Deprecated
    public AWSKMSAsyncClient(AWSCredentialsProvider awsCredentialsProvider, ExecutorService executorService) {
        this(awsCredentialsProvider, configFactory.getConfig(), executorService);
    }

    @Deprecated
    public AWSKMSAsyncClient(AWSCredentialsProvider awsCredentialsProvider, ClientConfiguration clientConfiguration, ExecutorService executorService) {
        super(awsCredentialsProvider, clientConfiguration);
        this.executorService = executorService;
    }

    public static AWSKMSAsyncClientBuilder asyncBuilder() {
        return AWSKMSAsyncClientBuilder.standard();
    }

    AWSKMSAsyncClient(AwsAsyncClientParams asyncClientParams) {
        this(asyncClientParams, false);
    }

    AWSKMSAsyncClient(AwsAsyncClientParams asyncClientParams, boolean endpointDiscoveryEnabled) {
        super(asyncClientParams, endpointDiscoveryEnabled);
        this.executorService = asyncClientParams.getExecutor();
    }

    public ExecutorService getExecutorService() {
        return this.executorService;
    }

    @Override
    public Future<CancelKeyDeletionResult> cancelKeyDeletionAsync(CancelKeyDeletionRequest request) {
        return this.cancelKeyDeletionAsync(request, null);
    }

    @Override
    public Future<CancelKeyDeletionResult> cancelKeyDeletionAsync(CancelKeyDeletionRequest request, final AsyncHandler<CancelKeyDeletionRequest, CancelKeyDeletionResult> asyncHandler) {
        final CancelKeyDeletionRequest finalRequest = this.beforeClientExecution(request);
        return this.executorService.submit(new Callable<CancelKeyDeletionResult>(){

            @Override
            public CancelKeyDeletionResult call() throws Exception {
                CancelKeyDeletionResult result = null;
                try {
                    result = AWSKMSAsyncClient.this.executeCancelKeyDeletion(finalRequest);
                }
                catch (Exception ex) {
                    if (asyncHandler != null) {
                        asyncHandler.onError(ex);
                    }
                    throw ex;
                }
                if (asyncHandler != null) {
                    asyncHandler.onSuccess(finalRequest, result);
                }
                return result;
            }
        });
    }

    @Override
    public Future<ConnectCustomKeyStoreResult> connectCustomKeyStoreAsync(ConnectCustomKeyStoreRequest request) {
        return this.connectCustomKeyStoreAsync(request, null);
    }

    @Override
    public Future<ConnectCustomKeyStoreResult> connectCustomKeyStoreAsync(ConnectCustomKeyStoreRequest request, final AsyncHandler<ConnectCustomKeyStoreRequest, ConnectCustomKeyStoreResult> asyncHandler) {
        final ConnectCustomKeyStoreRequest finalRequest = this.beforeClientExecution(request);
        return this.executorService.submit(new Callable<ConnectCustomKeyStoreResult>(){

            @Override
            public ConnectCustomKeyStoreResult call() throws Exception {
                ConnectCustomKeyStoreResult result = null;
                try {
                    result = AWSKMSAsyncClient.this.executeConnectCustomKeyStore(finalRequest);
                }
                catch (Exception ex) {
                    if (asyncHandler != null) {
                        asyncHandler.onError(ex);
                    }
                    throw ex;
                }
                if (asyncHandler != null) {
                    asyncHandler.onSuccess(finalRequest, result);
                }
                return result;
            }
        });
    }

    @Override
    public Future<CreateAliasResult> createAliasAsync(CreateAliasRequest request) {
        return this.createAliasAsync(request, null);
    }

    @Override
    public Future<CreateAliasResult> createAliasAsync(CreateAliasRequest request, final AsyncHandler<CreateAliasRequest, CreateAliasResult> asyncHandler) {
        final CreateAliasRequest finalRequest = this.beforeClientExecution(request);
        return this.executorService.submit(new Callable<CreateAliasResult>(){

            @Override
            public CreateAliasResult call() throws Exception {
                CreateAliasResult result = null;
                try {
                    result = AWSKMSAsyncClient.this.executeCreateAlias(finalRequest);
                }
                catch (Exception ex) {
                    if (asyncHandler != null) {
                        asyncHandler.onError(ex);
                    }
                    throw ex;
                }
                if (asyncHandler != null) {
                    asyncHandler.onSuccess(finalRequest, result);
                }
                return result;
            }
        });
    }

    @Override
    public Future<CreateCustomKeyStoreResult> createCustomKeyStoreAsync(CreateCustomKeyStoreRequest request) {
        return this.createCustomKeyStoreAsync(request, null);
    }

    @Override
    public Future<CreateCustomKeyStoreResult> createCustomKeyStoreAsync(CreateCustomKeyStoreRequest request, final AsyncHandler<CreateCustomKeyStoreRequest, CreateCustomKeyStoreResult> asyncHandler) {
        final CreateCustomKeyStoreRequest finalRequest = this.beforeClientExecution(request);
        return this.executorService.submit(new Callable<CreateCustomKeyStoreResult>(){

            @Override
            public CreateCustomKeyStoreResult call() throws Exception {
                CreateCustomKeyStoreResult result = null;
                try {
                    result = AWSKMSAsyncClient.this.executeCreateCustomKeyStore(finalRequest);
                }
                catch (Exception ex) {
                    if (asyncHandler != null) {
                        asyncHandler.onError(ex);
                    }
                    throw ex;
                }
                if (asyncHandler != null) {
                    asyncHandler.onSuccess(finalRequest, result);
                }
                return result;
            }
        });
    }

    @Override
    public Future<CreateGrantResult> createGrantAsync(CreateGrantRequest request) {
        return this.createGrantAsync(request, null);
    }

    @Override
    public Future<CreateGrantResult> createGrantAsync(CreateGrantRequest request, final AsyncHandler<CreateGrantRequest, CreateGrantResult> asyncHandler) {
        final CreateGrantRequest finalRequest = this.beforeClientExecution(request);
        return this.executorService.submit(new Callable<CreateGrantResult>(){

            @Override
            public CreateGrantResult call() throws Exception {
                CreateGrantResult result = null;
                try {
                    result = AWSKMSAsyncClient.this.executeCreateGrant(finalRequest);
                }
                catch (Exception ex) {
                    if (asyncHandler != null) {
                        asyncHandler.onError(ex);
                    }
                    throw ex;
                }
                if (asyncHandler != null) {
                    asyncHandler.onSuccess(finalRequest, result);
                }
                return result;
            }
        });
    }

    @Override
    public Future<CreateKeyResult> createKeyAsync(CreateKeyRequest request) {
        return this.createKeyAsync(request, null);
    }

    @Override
    public Future<CreateKeyResult> createKeyAsync(CreateKeyRequest request, final AsyncHandler<CreateKeyRequest, CreateKeyResult> asyncHandler) {
        final CreateKeyRequest finalRequest = this.beforeClientExecution(request);
        return this.executorService.submit(new Callable<CreateKeyResult>(){

            @Override
            public CreateKeyResult call() throws Exception {
                CreateKeyResult result = null;
                try {
                    result = AWSKMSAsyncClient.this.executeCreateKey(finalRequest);
                }
                catch (Exception ex) {
                    if (asyncHandler != null) {
                        asyncHandler.onError(ex);
                    }
                    throw ex;
                }
                if (asyncHandler != null) {
                    asyncHandler.onSuccess(finalRequest, result);
                }
                return result;
            }
        });
    }

    @Override
    public Future<CreateKeyResult> createKeyAsync() {
        return this.createKeyAsync(new CreateKeyRequest());
    }

    @Override
    public Future<CreateKeyResult> createKeyAsync(AsyncHandler<CreateKeyRequest, CreateKeyResult> asyncHandler) {
        return this.createKeyAsync(new CreateKeyRequest(), asyncHandler);
    }

    @Override
    public Future<DecryptResult> decryptAsync(DecryptRequest request) {
        return this.decryptAsync(request, null);
    }

    @Override
    public Future<DecryptResult> decryptAsync(DecryptRequest request, final AsyncHandler<DecryptRequest, DecryptResult> asyncHandler) {
        final DecryptRequest finalRequest = this.beforeClientExecution(request);
        return this.executorService.submit(new Callable<DecryptResult>(){

            @Override
            public DecryptResult call() throws Exception {
                DecryptResult result = null;
                try {
                    result = AWSKMSAsyncClient.this.executeDecrypt(finalRequest);
                }
                catch (Exception ex) {
                    if (asyncHandler != null) {
                        asyncHandler.onError(ex);
                    }
                    throw ex;
                }
                if (asyncHandler != null) {
                    asyncHandler.onSuccess(finalRequest, result);
                }
                return result;
            }
        });
    }

    @Override
    public Future<DeleteAliasResult> deleteAliasAsync(DeleteAliasRequest request) {
        return this.deleteAliasAsync(request, null);
    }

    @Override
    public Future<DeleteAliasResult> deleteAliasAsync(DeleteAliasRequest request, final AsyncHandler<DeleteAliasRequest, DeleteAliasResult> asyncHandler) {
        final DeleteAliasRequest finalRequest = this.beforeClientExecution(request);
        return this.executorService.submit(new Callable<DeleteAliasResult>(){

            @Override
            public DeleteAliasResult call() throws Exception {
                DeleteAliasResult result = null;
                try {
                    result = AWSKMSAsyncClient.this.executeDeleteAlias(finalRequest);
                }
                catch (Exception ex) {
                    if (asyncHandler != null) {
                        asyncHandler.onError(ex);
                    }
                    throw ex;
                }
                if (asyncHandler != null) {
                    asyncHandler.onSuccess(finalRequest, result);
                }
                return result;
            }
        });
    }

    @Override
    public Future<DeleteCustomKeyStoreResult> deleteCustomKeyStoreAsync(DeleteCustomKeyStoreRequest request) {
        return this.deleteCustomKeyStoreAsync(request, null);
    }

    @Override
    public Future<DeleteCustomKeyStoreResult> deleteCustomKeyStoreAsync(DeleteCustomKeyStoreRequest request, final AsyncHandler<DeleteCustomKeyStoreRequest, DeleteCustomKeyStoreResult> asyncHandler) {
        final DeleteCustomKeyStoreRequest finalRequest = this.beforeClientExecution(request);
        return this.executorService.submit(new Callable<DeleteCustomKeyStoreResult>(){

            @Override
            public DeleteCustomKeyStoreResult call() throws Exception {
                DeleteCustomKeyStoreResult result = null;
                try {
                    result = AWSKMSAsyncClient.this.executeDeleteCustomKeyStore(finalRequest);
                }
                catch (Exception ex) {
                    if (asyncHandler != null) {
                        asyncHandler.onError(ex);
                    }
                    throw ex;
                }
                if (asyncHandler != null) {
                    asyncHandler.onSuccess(finalRequest, result);
                }
                return result;
            }
        });
    }

    @Override
    public Future<DeleteImportedKeyMaterialResult> deleteImportedKeyMaterialAsync(DeleteImportedKeyMaterialRequest request) {
        return this.deleteImportedKeyMaterialAsync(request, null);
    }

    @Override
    public Future<DeleteImportedKeyMaterialResult> deleteImportedKeyMaterialAsync(DeleteImportedKeyMaterialRequest request, final AsyncHandler<DeleteImportedKeyMaterialRequest, DeleteImportedKeyMaterialResult> asyncHandler) {
        final DeleteImportedKeyMaterialRequest finalRequest = this.beforeClientExecution(request);
        return this.executorService.submit(new Callable<DeleteImportedKeyMaterialResult>(){

            @Override
            public DeleteImportedKeyMaterialResult call() throws Exception {
                DeleteImportedKeyMaterialResult result = null;
                try {
                    result = AWSKMSAsyncClient.this.executeDeleteImportedKeyMaterial(finalRequest);
                }
                catch (Exception ex) {
                    if (asyncHandler != null) {
                        asyncHandler.onError(ex);
                    }
                    throw ex;
                }
                if (asyncHandler != null) {
                    asyncHandler.onSuccess(finalRequest, result);
                }
                return result;
            }
        });
    }

    @Override
    public Future<DescribeCustomKeyStoresResult> describeCustomKeyStoresAsync(DescribeCustomKeyStoresRequest request) {
        return this.describeCustomKeyStoresAsync(request, null);
    }

    @Override
    public Future<DescribeCustomKeyStoresResult> describeCustomKeyStoresAsync(DescribeCustomKeyStoresRequest request, final AsyncHandler<DescribeCustomKeyStoresRequest, DescribeCustomKeyStoresResult> asyncHandler) {
        final DescribeCustomKeyStoresRequest finalRequest = this.beforeClientExecution(request);
        return this.executorService.submit(new Callable<DescribeCustomKeyStoresResult>(){

            @Override
            public DescribeCustomKeyStoresResult call() throws Exception {
                DescribeCustomKeyStoresResult result = null;
                try {
                    result = AWSKMSAsyncClient.this.executeDescribeCustomKeyStores(finalRequest);
                }
                catch (Exception ex) {
                    if (asyncHandler != null) {
                        asyncHandler.onError(ex);
                    }
                    throw ex;
                }
                if (asyncHandler != null) {
                    asyncHandler.onSuccess(finalRequest, result);
                }
                return result;
            }
        });
    }

    @Override
    public Future<DescribeKeyResult> describeKeyAsync(DescribeKeyRequest request) {
        return this.describeKeyAsync(request, null);
    }

    @Override
    public Future<DescribeKeyResult> describeKeyAsync(DescribeKeyRequest request, final AsyncHandler<DescribeKeyRequest, DescribeKeyResult> asyncHandler) {
        final DescribeKeyRequest finalRequest = this.beforeClientExecution(request);
        return this.executorService.submit(new Callable<DescribeKeyResult>(){

            @Override
            public DescribeKeyResult call() throws Exception {
                DescribeKeyResult result = null;
                try {
                    result = AWSKMSAsyncClient.this.executeDescribeKey(finalRequest);
                }
                catch (Exception ex) {
                    if (asyncHandler != null) {
                        asyncHandler.onError(ex);
                    }
                    throw ex;
                }
                if (asyncHandler != null) {
                    asyncHandler.onSuccess(finalRequest, result);
                }
                return result;
            }
        });
    }

    @Override
    public Future<DisableKeyResult> disableKeyAsync(DisableKeyRequest request) {
        return this.disableKeyAsync(request, null);
    }

    @Override
    public Future<DisableKeyResult> disableKeyAsync(DisableKeyRequest request, final AsyncHandler<DisableKeyRequest, DisableKeyResult> asyncHandler) {
        final DisableKeyRequest finalRequest = this.beforeClientExecution(request);
        return this.executorService.submit(new Callable<DisableKeyResult>(){

            @Override
            public DisableKeyResult call() throws Exception {
                DisableKeyResult result = null;
                try {
                    result = AWSKMSAsyncClient.this.executeDisableKey(finalRequest);
                }
                catch (Exception ex) {
                    if (asyncHandler != null) {
                        asyncHandler.onError(ex);
                    }
                    throw ex;
                }
                if (asyncHandler != null) {
                    asyncHandler.onSuccess(finalRequest, result);
                }
                return result;
            }
        });
    }

    @Override
    public Future<DisableKeyRotationResult> disableKeyRotationAsync(DisableKeyRotationRequest request) {
        return this.disableKeyRotationAsync(request, null);
    }

    @Override
    public Future<DisableKeyRotationResult> disableKeyRotationAsync(DisableKeyRotationRequest request, final AsyncHandler<DisableKeyRotationRequest, DisableKeyRotationResult> asyncHandler) {
        final DisableKeyRotationRequest finalRequest = this.beforeClientExecution(request);
        return this.executorService.submit(new Callable<DisableKeyRotationResult>(){

            @Override
            public DisableKeyRotationResult call() throws Exception {
                DisableKeyRotationResult result = null;
                try {
                    result = AWSKMSAsyncClient.this.executeDisableKeyRotation(finalRequest);
                }
                catch (Exception ex) {
                    if (asyncHandler != null) {
                        asyncHandler.onError(ex);
                    }
                    throw ex;
                }
                if (asyncHandler != null) {
                    asyncHandler.onSuccess(finalRequest, result);
                }
                return result;
            }
        });
    }

    @Override
    public Future<DisconnectCustomKeyStoreResult> disconnectCustomKeyStoreAsync(DisconnectCustomKeyStoreRequest request) {
        return this.disconnectCustomKeyStoreAsync(request, null);
    }

    @Override
    public Future<DisconnectCustomKeyStoreResult> disconnectCustomKeyStoreAsync(DisconnectCustomKeyStoreRequest request, final AsyncHandler<DisconnectCustomKeyStoreRequest, DisconnectCustomKeyStoreResult> asyncHandler) {
        final DisconnectCustomKeyStoreRequest finalRequest = this.beforeClientExecution(request);
        return this.executorService.submit(new Callable<DisconnectCustomKeyStoreResult>(){

            @Override
            public DisconnectCustomKeyStoreResult call() throws Exception {
                DisconnectCustomKeyStoreResult result = null;
                try {
                    result = AWSKMSAsyncClient.this.executeDisconnectCustomKeyStore(finalRequest);
                }
                catch (Exception ex) {
                    if (asyncHandler != null) {
                        asyncHandler.onError(ex);
                    }
                    throw ex;
                }
                if (asyncHandler != null) {
                    asyncHandler.onSuccess(finalRequest, result);
                }
                return result;
            }
        });
    }

    @Override
    public Future<EnableKeyResult> enableKeyAsync(EnableKeyRequest request) {
        return this.enableKeyAsync(request, null);
    }

    @Override
    public Future<EnableKeyResult> enableKeyAsync(EnableKeyRequest request, final AsyncHandler<EnableKeyRequest, EnableKeyResult> asyncHandler) {
        final EnableKeyRequest finalRequest = this.beforeClientExecution(request);
        return this.executorService.submit(new Callable<EnableKeyResult>(){

            @Override
            public EnableKeyResult call() throws Exception {
                EnableKeyResult result = null;
                try {
                    result = AWSKMSAsyncClient.this.executeEnableKey(finalRequest);
                }
                catch (Exception ex) {
                    if (asyncHandler != null) {
                        asyncHandler.onError(ex);
                    }
                    throw ex;
                }
                if (asyncHandler != null) {
                    asyncHandler.onSuccess(finalRequest, result);
                }
                return result;
            }
        });
    }

    @Override
    public Future<EnableKeyRotationResult> enableKeyRotationAsync(EnableKeyRotationRequest request) {
        return this.enableKeyRotationAsync(request, null);
    }

    @Override
    public Future<EnableKeyRotationResult> enableKeyRotationAsync(EnableKeyRotationRequest request, final AsyncHandler<EnableKeyRotationRequest, EnableKeyRotationResult> asyncHandler) {
        final EnableKeyRotationRequest finalRequest = this.beforeClientExecution(request);
        return this.executorService.submit(new Callable<EnableKeyRotationResult>(){

            @Override
            public EnableKeyRotationResult call() throws Exception {
                EnableKeyRotationResult result = null;
                try {
                    result = AWSKMSAsyncClient.this.executeEnableKeyRotation(finalRequest);
                }
                catch (Exception ex) {
                    if (asyncHandler != null) {
                        asyncHandler.onError(ex);
                    }
                    throw ex;
                }
                if (asyncHandler != null) {
                    asyncHandler.onSuccess(finalRequest, result);
                }
                return result;
            }
        });
    }

    @Override
    public Future<EncryptResult> encryptAsync(EncryptRequest request) {
        return this.encryptAsync(request, null);
    }

    @Override
    public Future<EncryptResult> encryptAsync(EncryptRequest request, final AsyncHandler<EncryptRequest, EncryptResult> asyncHandler) {
        final EncryptRequest finalRequest = this.beforeClientExecution(request);
        return this.executorService.submit(new Callable<EncryptResult>(){

            @Override
            public EncryptResult call() throws Exception {
                EncryptResult result = null;
                try {
                    result = AWSKMSAsyncClient.this.executeEncrypt(finalRequest);
                }
                catch (Exception ex) {
                    if (asyncHandler != null) {
                        asyncHandler.onError(ex);
                    }
                    throw ex;
                }
                if (asyncHandler != null) {
                    asyncHandler.onSuccess(finalRequest, result);
                }
                return result;
            }
        });
    }

    @Override
    public Future<GenerateDataKeyResult> generateDataKeyAsync(GenerateDataKeyRequest request) {
        return this.generateDataKeyAsync(request, null);
    }

    @Override
    public Future<GenerateDataKeyResult> generateDataKeyAsync(GenerateDataKeyRequest request, final AsyncHandler<GenerateDataKeyRequest, GenerateDataKeyResult> asyncHandler) {
        final GenerateDataKeyRequest finalRequest = this.beforeClientExecution(request);
        return this.executorService.submit(new Callable<GenerateDataKeyResult>(){

            @Override
            public GenerateDataKeyResult call() throws Exception {
                GenerateDataKeyResult result = null;
                try {
                    result = AWSKMSAsyncClient.this.executeGenerateDataKey(finalRequest);
                }
                catch (Exception ex) {
                    if (asyncHandler != null) {
                        asyncHandler.onError(ex);
                    }
                    throw ex;
                }
                if (asyncHandler != null) {
                    asyncHandler.onSuccess(finalRequest, result);
                }
                return result;
            }
        });
    }

    @Override
    public Future<GenerateDataKeyPairResult> generateDataKeyPairAsync(GenerateDataKeyPairRequest request) {
        return this.generateDataKeyPairAsync(request, null);
    }

    @Override
    public Future<GenerateDataKeyPairResult> generateDataKeyPairAsync(GenerateDataKeyPairRequest request, final AsyncHandler<GenerateDataKeyPairRequest, GenerateDataKeyPairResult> asyncHandler) {
        final GenerateDataKeyPairRequest finalRequest = this.beforeClientExecution(request);
        return this.executorService.submit(new Callable<GenerateDataKeyPairResult>(){

            @Override
            public GenerateDataKeyPairResult call() throws Exception {
                GenerateDataKeyPairResult result = null;
                try {
                    result = AWSKMSAsyncClient.this.executeGenerateDataKeyPair(finalRequest);
                }
                catch (Exception ex) {
                    if (asyncHandler != null) {
                        asyncHandler.onError(ex);
                    }
                    throw ex;
                }
                if (asyncHandler != null) {
                    asyncHandler.onSuccess(finalRequest, result);
                }
                return result;
            }
        });
    }

    @Override
    public Future<GenerateDataKeyPairWithoutPlaintextResult> generateDataKeyPairWithoutPlaintextAsync(GenerateDataKeyPairWithoutPlaintextRequest request) {
        return this.generateDataKeyPairWithoutPlaintextAsync(request, null);
    }

    @Override
    public Future<GenerateDataKeyPairWithoutPlaintextResult> generateDataKeyPairWithoutPlaintextAsync(GenerateDataKeyPairWithoutPlaintextRequest request, final AsyncHandler<GenerateDataKeyPairWithoutPlaintextRequest, GenerateDataKeyPairWithoutPlaintextResult> asyncHandler) {
        final GenerateDataKeyPairWithoutPlaintextRequest finalRequest = this.beforeClientExecution(request);
        return this.executorService.submit(new Callable<GenerateDataKeyPairWithoutPlaintextResult>(){

            @Override
            public GenerateDataKeyPairWithoutPlaintextResult call() throws Exception {
                GenerateDataKeyPairWithoutPlaintextResult result = null;
                try {
                    result = AWSKMSAsyncClient.this.executeGenerateDataKeyPairWithoutPlaintext(finalRequest);
                }
                catch (Exception ex) {
                    if (asyncHandler != null) {
                        asyncHandler.onError(ex);
                    }
                    throw ex;
                }
                if (asyncHandler != null) {
                    asyncHandler.onSuccess(finalRequest, result);
                }
                return result;
            }
        });
    }

    @Override
    public Future<GenerateDataKeyWithoutPlaintextResult> generateDataKeyWithoutPlaintextAsync(GenerateDataKeyWithoutPlaintextRequest request) {
        return this.generateDataKeyWithoutPlaintextAsync(request, null);
    }

    @Override
    public Future<GenerateDataKeyWithoutPlaintextResult> generateDataKeyWithoutPlaintextAsync(GenerateDataKeyWithoutPlaintextRequest request, final AsyncHandler<GenerateDataKeyWithoutPlaintextRequest, GenerateDataKeyWithoutPlaintextResult> asyncHandler) {
        final GenerateDataKeyWithoutPlaintextRequest finalRequest = this.beforeClientExecution(request);
        return this.executorService.submit(new Callable<GenerateDataKeyWithoutPlaintextResult>(){

            @Override
            public GenerateDataKeyWithoutPlaintextResult call() throws Exception {
                GenerateDataKeyWithoutPlaintextResult result = null;
                try {
                    result = AWSKMSAsyncClient.this.executeGenerateDataKeyWithoutPlaintext(finalRequest);
                }
                catch (Exception ex) {
                    if (asyncHandler != null) {
                        asyncHandler.onError(ex);
                    }
                    throw ex;
                }
                if (asyncHandler != null) {
                    asyncHandler.onSuccess(finalRequest, result);
                }
                return result;
            }
        });
    }

    @Override
    public Future<GenerateMacResult> generateMacAsync(GenerateMacRequest request) {
        return this.generateMacAsync(request, null);
    }

    @Override
    public Future<GenerateMacResult> generateMacAsync(GenerateMacRequest request, final AsyncHandler<GenerateMacRequest, GenerateMacResult> asyncHandler) {
        final GenerateMacRequest finalRequest = this.beforeClientExecution(request);
        return this.executorService.submit(new Callable<GenerateMacResult>(){

            @Override
            public GenerateMacResult call() throws Exception {
                GenerateMacResult result = null;
                try {
                    result = AWSKMSAsyncClient.this.executeGenerateMac(finalRequest);
                }
                catch (Exception ex) {
                    if (asyncHandler != null) {
                        asyncHandler.onError(ex);
                    }
                    throw ex;
                }
                if (asyncHandler != null) {
                    asyncHandler.onSuccess(finalRequest, result);
                }
                return result;
            }
        });
    }

    @Override
    public Future<GenerateRandomResult> generateRandomAsync(GenerateRandomRequest request) {
        return this.generateRandomAsync(request, null);
    }

    @Override
    public Future<GenerateRandomResult> generateRandomAsync(GenerateRandomRequest request, final AsyncHandler<GenerateRandomRequest, GenerateRandomResult> asyncHandler) {
        final GenerateRandomRequest finalRequest = this.beforeClientExecution(request);
        return this.executorService.submit(new Callable<GenerateRandomResult>(){

            @Override
            public GenerateRandomResult call() throws Exception {
                GenerateRandomResult result = null;
                try {
                    result = AWSKMSAsyncClient.this.executeGenerateRandom(finalRequest);
                }
                catch (Exception ex) {
                    if (asyncHandler != null) {
                        asyncHandler.onError(ex);
                    }
                    throw ex;
                }
                if (asyncHandler != null) {
                    asyncHandler.onSuccess(finalRequest, result);
                }
                return result;
            }
        });
    }

    @Override
    public Future<GenerateRandomResult> generateRandomAsync() {
        return this.generateRandomAsync(new GenerateRandomRequest());
    }

    @Override
    public Future<GenerateRandomResult> generateRandomAsync(AsyncHandler<GenerateRandomRequest, GenerateRandomResult> asyncHandler) {
        return this.generateRandomAsync(new GenerateRandomRequest(), asyncHandler);
    }

    @Override
    public Future<GetKeyPolicyResult> getKeyPolicyAsync(GetKeyPolicyRequest request) {
        return this.getKeyPolicyAsync(request, null);
    }

    @Override
    public Future<GetKeyPolicyResult> getKeyPolicyAsync(GetKeyPolicyRequest request, final AsyncHandler<GetKeyPolicyRequest, GetKeyPolicyResult> asyncHandler) {
        final GetKeyPolicyRequest finalRequest = this.beforeClientExecution(request);
        return this.executorService.submit(new Callable<GetKeyPolicyResult>(){

            @Override
            public GetKeyPolicyResult call() throws Exception {
                GetKeyPolicyResult result = null;
                try {
                    result = AWSKMSAsyncClient.this.executeGetKeyPolicy(finalRequest);
                }
                catch (Exception ex) {
                    if (asyncHandler != null) {
                        asyncHandler.onError(ex);
                    }
                    throw ex;
                }
                if (asyncHandler != null) {
                    asyncHandler.onSuccess(finalRequest, result);
                }
                return result;
            }
        });
    }

    @Override
    public Future<GetKeyRotationStatusResult> getKeyRotationStatusAsync(GetKeyRotationStatusRequest request) {
        return this.getKeyRotationStatusAsync(request, null);
    }

    @Override
    public Future<GetKeyRotationStatusResult> getKeyRotationStatusAsync(GetKeyRotationStatusRequest request, final AsyncHandler<GetKeyRotationStatusRequest, GetKeyRotationStatusResult> asyncHandler) {
        final GetKeyRotationStatusRequest finalRequest = this.beforeClientExecution(request);
        return this.executorService.submit(new Callable<GetKeyRotationStatusResult>(){

            @Override
            public GetKeyRotationStatusResult call() throws Exception {
                GetKeyRotationStatusResult result = null;
                try {
                    result = AWSKMSAsyncClient.this.executeGetKeyRotationStatus(finalRequest);
                }
                catch (Exception ex) {
                    if (asyncHandler != null) {
                        asyncHandler.onError(ex);
                    }
                    throw ex;
                }
                if (asyncHandler != null) {
                    asyncHandler.onSuccess(finalRequest, result);
                }
                return result;
            }
        });
    }

    @Override
    public Future<GetParametersForImportResult> getParametersForImportAsync(GetParametersForImportRequest request) {
        return this.getParametersForImportAsync(request, null);
    }

    @Override
    public Future<GetParametersForImportResult> getParametersForImportAsync(GetParametersForImportRequest request, final AsyncHandler<GetParametersForImportRequest, GetParametersForImportResult> asyncHandler) {
        final GetParametersForImportRequest finalRequest = this.beforeClientExecution(request);
        return this.executorService.submit(new Callable<GetParametersForImportResult>(){

            @Override
            public GetParametersForImportResult call() throws Exception {
                GetParametersForImportResult result = null;
                try {
                    result = AWSKMSAsyncClient.this.executeGetParametersForImport(finalRequest);
                }
                catch (Exception ex) {
                    if (asyncHandler != null) {
                        asyncHandler.onError(ex);
                    }
                    throw ex;
                }
                if (asyncHandler != null) {
                    asyncHandler.onSuccess(finalRequest, result);
                }
                return result;
            }
        });
    }

    @Override
    public Future<GetPublicKeyResult> getPublicKeyAsync(GetPublicKeyRequest request) {
        return this.getPublicKeyAsync(request, null);
    }

    @Override
    public Future<GetPublicKeyResult> getPublicKeyAsync(GetPublicKeyRequest request, final AsyncHandler<GetPublicKeyRequest, GetPublicKeyResult> asyncHandler) {
        final GetPublicKeyRequest finalRequest = this.beforeClientExecution(request);
        return this.executorService.submit(new Callable<GetPublicKeyResult>(){

            @Override
            public GetPublicKeyResult call() throws Exception {
                GetPublicKeyResult result = null;
                try {
                    result = AWSKMSAsyncClient.this.executeGetPublicKey(finalRequest);
                }
                catch (Exception ex) {
                    if (asyncHandler != null) {
                        asyncHandler.onError(ex);
                    }
                    throw ex;
                }
                if (asyncHandler != null) {
                    asyncHandler.onSuccess(finalRequest, result);
                }
                return result;
            }
        });
    }

    @Override
    public Future<ImportKeyMaterialResult> importKeyMaterialAsync(ImportKeyMaterialRequest request) {
        return this.importKeyMaterialAsync(request, null);
    }

    @Override
    public Future<ImportKeyMaterialResult> importKeyMaterialAsync(ImportKeyMaterialRequest request, final AsyncHandler<ImportKeyMaterialRequest, ImportKeyMaterialResult> asyncHandler) {
        final ImportKeyMaterialRequest finalRequest = this.beforeClientExecution(request);
        return this.executorService.submit(new Callable<ImportKeyMaterialResult>(){

            @Override
            public ImportKeyMaterialResult call() throws Exception {
                ImportKeyMaterialResult result = null;
                try {
                    result = AWSKMSAsyncClient.this.executeImportKeyMaterial(finalRequest);
                }
                catch (Exception ex) {
                    if (asyncHandler != null) {
                        asyncHandler.onError(ex);
                    }
                    throw ex;
                }
                if (asyncHandler != null) {
                    asyncHandler.onSuccess(finalRequest, result);
                }
                return result;
            }
        });
    }

    @Override
    public Future<ListAliasesResult> listAliasesAsync(ListAliasesRequest request) {
        return this.listAliasesAsync(request, null);
    }

    @Override
    public Future<ListAliasesResult> listAliasesAsync(ListAliasesRequest request, final AsyncHandler<ListAliasesRequest, ListAliasesResult> asyncHandler) {
        final ListAliasesRequest finalRequest = this.beforeClientExecution(request);
        return this.executorService.submit(new Callable<ListAliasesResult>(){

            @Override
            public ListAliasesResult call() throws Exception {
                ListAliasesResult result = null;
                try {
                    result = AWSKMSAsyncClient.this.executeListAliases(finalRequest);
                }
                catch (Exception ex) {
                    if (asyncHandler != null) {
                        asyncHandler.onError(ex);
                    }
                    throw ex;
                }
                if (asyncHandler != null) {
                    asyncHandler.onSuccess(finalRequest, result);
                }
                return result;
            }
        });
    }

    @Override
    public Future<ListAliasesResult> listAliasesAsync() {
        return this.listAliasesAsync(new ListAliasesRequest());
    }

    @Override
    public Future<ListAliasesResult> listAliasesAsync(AsyncHandler<ListAliasesRequest, ListAliasesResult> asyncHandler) {
        return this.listAliasesAsync(new ListAliasesRequest(), asyncHandler);
    }

    @Override
    public Future<ListGrantsResult> listGrantsAsync(ListGrantsRequest request) {
        return this.listGrantsAsync(request, null);
    }

    @Override
    public Future<ListGrantsResult> listGrantsAsync(ListGrantsRequest request, final AsyncHandler<ListGrantsRequest, ListGrantsResult> asyncHandler) {
        final ListGrantsRequest finalRequest = this.beforeClientExecution(request);
        return this.executorService.submit(new Callable<ListGrantsResult>(){

            @Override
            public ListGrantsResult call() throws Exception {
                ListGrantsResult result = null;
                try {
                    result = AWSKMSAsyncClient.this.executeListGrants(finalRequest);
                }
                catch (Exception ex) {
                    if (asyncHandler != null) {
                        asyncHandler.onError(ex);
                    }
                    throw ex;
                }
                if (asyncHandler != null) {
                    asyncHandler.onSuccess(finalRequest, result);
                }
                return result;
            }
        });
    }

    @Override
    public Future<ListKeyPoliciesResult> listKeyPoliciesAsync(ListKeyPoliciesRequest request) {
        return this.listKeyPoliciesAsync(request, null);
    }

    @Override
    public Future<ListKeyPoliciesResult> listKeyPoliciesAsync(ListKeyPoliciesRequest request, final AsyncHandler<ListKeyPoliciesRequest, ListKeyPoliciesResult> asyncHandler) {
        final ListKeyPoliciesRequest finalRequest = this.beforeClientExecution(request);
        return this.executorService.submit(new Callable<ListKeyPoliciesResult>(){

            @Override
            public ListKeyPoliciesResult call() throws Exception {
                ListKeyPoliciesResult result = null;
                try {
                    result = AWSKMSAsyncClient.this.executeListKeyPolicies(finalRequest);
                }
                catch (Exception ex) {
                    if (asyncHandler != null) {
                        asyncHandler.onError(ex);
                    }
                    throw ex;
                }
                if (asyncHandler != null) {
                    asyncHandler.onSuccess(finalRequest, result);
                }
                return result;
            }
        });
    }

    @Override
    public Future<ListKeysResult> listKeysAsync(ListKeysRequest request) {
        return this.listKeysAsync(request, null);
    }

    @Override
    public Future<ListKeysResult> listKeysAsync(ListKeysRequest request, final AsyncHandler<ListKeysRequest, ListKeysResult> asyncHandler) {
        final ListKeysRequest finalRequest = this.beforeClientExecution(request);
        return this.executorService.submit(new Callable<ListKeysResult>(){

            @Override
            public ListKeysResult call() throws Exception {
                ListKeysResult result = null;
                try {
                    result = AWSKMSAsyncClient.this.executeListKeys(finalRequest);
                }
                catch (Exception ex) {
                    if (asyncHandler != null) {
                        asyncHandler.onError(ex);
                    }
                    throw ex;
                }
                if (asyncHandler != null) {
                    asyncHandler.onSuccess(finalRequest, result);
                }
                return result;
            }
        });
    }

    @Override
    public Future<ListKeysResult> listKeysAsync() {
        return this.listKeysAsync(new ListKeysRequest());
    }

    @Override
    public Future<ListKeysResult> listKeysAsync(AsyncHandler<ListKeysRequest, ListKeysResult> asyncHandler) {
        return this.listKeysAsync(new ListKeysRequest(), asyncHandler);
    }

    @Override
    public Future<ListResourceTagsResult> listResourceTagsAsync(ListResourceTagsRequest request) {
        return this.listResourceTagsAsync(request, null);
    }

    @Override
    public Future<ListResourceTagsResult> listResourceTagsAsync(ListResourceTagsRequest request, final AsyncHandler<ListResourceTagsRequest, ListResourceTagsResult> asyncHandler) {
        final ListResourceTagsRequest finalRequest = this.beforeClientExecution(request);
        return this.executorService.submit(new Callable<ListResourceTagsResult>(){

            @Override
            public ListResourceTagsResult call() throws Exception {
                ListResourceTagsResult result = null;
                try {
                    result = AWSKMSAsyncClient.this.executeListResourceTags(finalRequest);
                }
                catch (Exception ex) {
                    if (asyncHandler != null) {
                        asyncHandler.onError(ex);
                    }
                    throw ex;
                }
                if (asyncHandler != null) {
                    asyncHandler.onSuccess(finalRequest, result);
                }
                return result;
            }
        });
    }

    @Override
    public Future<ListRetirableGrantsResult> listRetirableGrantsAsync(ListRetirableGrantsRequest request) {
        return this.listRetirableGrantsAsync(request, null);
    }

    @Override
    public Future<ListRetirableGrantsResult> listRetirableGrantsAsync(ListRetirableGrantsRequest request, final AsyncHandler<ListRetirableGrantsRequest, ListRetirableGrantsResult> asyncHandler) {
        final ListRetirableGrantsRequest finalRequest = this.beforeClientExecution(request);
        return this.executorService.submit(new Callable<ListRetirableGrantsResult>(){

            @Override
            public ListRetirableGrantsResult call() throws Exception {
                ListRetirableGrantsResult result = null;
                try {
                    result = AWSKMSAsyncClient.this.executeListRetirableGrants(finalRequest);
                }
                catch (Exception ex) {
                    if (asyncHandler != null) {
                        asyncHandler.onError(ex);
                    }
                    throw ex;
                }
                if (asyncHandler != null) {
                    asyncHandler.onSuccess(finalRequest, result);
                }
                return result;
            }
        });
    }

    @Override
    public Future<PutKeyPolicyResult> putKeyPolicyAsync(PutKeyPolicyRequest request) {
        return this.putKeyPolicyAsync(request, null);
    }

    @Override
    public Future<PutKeyPolicyResult> putKeyPolicyAsync(PutKeyPolicyRequest request, final AsyncHandler<PutKeyPolicyRequest, PutKeyPolicyResult> asyncHandler) {
        final PutKeyPolicyRequest finalRequest = this.beforeClientExecution(request);
        return this.executorService.submit(new Callable<PutKeyPolicyResult>(){

            @Override
            public PutKeyPolicyResult call() throws Exception {
                PutKeyPolicyResult result = null;
                try {
                    result = AWSKMSAsyncClient.this.executePutKeyPolicy(finalRequest);
                }
                catch (Exception ex) {
                    if (asyncHandler != null) {
                        asyncHandler.onError(ex);
                    }
                    throw ex;
                }
                if (asyncHandler != null) {
                    asyncHandler.onSuccess(finalRequest, result);
                }
                return result;
            }
        });
    }

    @Override
    public Future<ReEncryptResult> reEncryptAsync(ReEncryptRequest request) {
        return this.reEncryptAsync(request, null);
    }

    @Override
    public Future<ReEncryptResult> reEncryptAsync(ReEncryptRequest request, final AsyncHandler<ReEncryptRequest, ReEncryptResult> asyncHandler) {
        final ReEncryptRequest finalRequest = this.beforeClientExecution(request);
        return this.executorService.submit(new Callable<ReEncryptResult>(){

            @Override
            public ReEncryptResult call() throws Exception {
                ReEncryptResult result = null;
                try {
                    result = AWSKMSAsyncClient.this.executeReEncrypt(finalRequest);
                }
                catch (Exception ex) {
                    if (asyncHandler != null) {
                        asyncHandler.onError(ex);
                    }
                    throw ex;
                }
                if (asyncHandler != null) {
                    asyncHandler.onSuccess(finalRequest, result);
                }
                return result;
            }
        });
    }

    @Override
    public Future<ReplicateKeyResult> replicateKeyAsync(ReplicateKeyRequest request) {
        return this.replicateKeyAsync(request, null);
    }

    @Override
    public Future<ReplicateKeyResult> replicateKeyAsync(ReplicateKeyRequest request, final AsyncHandler<ReplicateKeyRequest, ReplicateKeyResult> asyncHandler) {
        final ReplicateKeyRequest finalRequest = this.beforeClientExecution(request);
        return this.executorService.submit(new Callable<ReplicateKeyResult>(){

            @Override
            public ReplicateKeyResult call() throws Exception {
                ReplicateKeyResult result = null;
                try {
                    result = AWSKMSAsyncClient.this.executeReplicateKey(finalRequest);
                }
                catch (Exception ex) {
                    if (asyncHandler != null) {
                        asyncHandler.onError(ex);
                    }
                    throw ex;
                }
                if (asyncHandler != null) {
                    asyncHandler.onSuccess(finalRequest, result);
                }
                return result;
            }
        });
    }

    @Override
    public Future<RetireGrantResult> retireGrantAsync(RetireGrantRequest request) {
        return this.retireGrantAsync(request, null);
    }

    @Override
    public Future<RetireGrantResult> retireGrantAsync(RetireGrantRequest request, final AsyncHandler<RetireGrantRequest, RetireGrantResult> asyncHandler) {
        final RetireGrantRequest finalRequest = this.beforeClientExecution(request);
        return this.executorService.submit(new Callable<RetireGrantResult>(){

            @Override
            public RetireGrantResult call() throws Exception {
                RetireGrantResult result = null;
                try {
                    result = AWSKMSAsyncClient.this.executeRetireGrant(finalRequest);
                }
                catch (Exception ex) {
                    if (asyncHandler != null) {
                        asyncHandler.onError(ex);
                    }
                    throw ex;
                }
                if (asyncHandler != null) {
                    asyncHandler.onSuccess(finalRequest, result);
                }
                return result;
            }
        });
    }

    @Override
    public Future<RetireGrantResult> retireGrantAsync() {
        return this.retireGrantAsync(new RetireGrantRequest());
    }

    @Override
    public Future<RetireGrantResult> retireGrantAsync(AsyncHandler<RetireGrantRequest, RetireGrantResult> asyncHandler) {
        return this.retireGrantAsync(new RetireGrantRequest(), asyncHandler);
    }

    @Override
    public Future<RevokeGrantResult> revokeGrantAsync(RevokeGrantRequest request) {
        return this.revokeGrantAsync(request, null);
    }

    @Override
    public Future<RevokeGrantResult> revokeGrantAsync(RevokeGrantRequest request, final AsyncHandler<RevokeGrantRequest, RevokeGrantResult> asyncHandler) {
        final RevokeGrantRequest finalRequest = this.beforeClientExecution(request);
        return this.executorService.submit(new Callable<RevokeGrantResult>(){

            @Override
            public RevokeGrantResult call() throws Exception {
                RevokeGrantResult result = null;
                try {
                    result = AWSKMSAsyncClient.this.executeRevokeGrant(finalRequest);
                }
                catch (Exception ex) {
                    if (asyncHandler != null) {
                        asyncHandler.onError(ex);
                    }
                    throw ex;
                }
                if (asyncHandler != null) {
                    asyncHandler.onSuccess(finalRequest, result);
                }
                return result;
            }
        });
    }

    @Override
    public Future<ScheduleKeyDeletionResult> scheduleKeyDeletionAsync(ScheduleKeyDeletionRequest request) {
        return this.scheduleKeyDeletionAsync(request, null);
    }

    @Override
    public Future<ScheduleKeyDeletionResult> scheduleKeyDeletionAsync(ScheduleKeyDeletionRequest request, final AsyncHandler<ScheduleKeyDeletionRequest, ScheduleKeyDeletionResult> asyncHandler) {
        final ScheduleKeyDeletionRequest finalRequest = this.beforeClientExecution(request);
        return this.executorService.submit(new Callable<ScheduleKeyDeletionResult>(){

            @Override
            public ScheduleKeyDeletionResult call() throws Exception {
                ScheduleKeyDeletionResult result = null;
                try {
                    result = AWSKMSAsyncClient.this.executeScheduleKeyDeletion(finalRequest);
                }
                catch (Exception ex) {
                    if (asyncHandler != null) {
                        asyncHandler.onError(ex);
                    }
                    throw ex;
                }
                if (asyncHandler != null) {
                    asyncHandler.onSuccess(finalRequest, result);
                }
                return result;
            }
        });
    }

    @Override
    public Future<SignResult> signAsync(SignRequest request) {
        return this.signAsync(request, null);
    }

    @Override
    public Future<SignResult> signAsync(SignRequest request, final AsyncHandler<SignRequest, SignResult> asyncHandler) {
        final SignRequest finalRequest = this.beforeClientExecution(request);
        return this.executorService.submit(new Callable<SignResult>(){

            @Override
            public SignResult call() throws Exception {
                SignResult result = null;
                try {
                    result = AWSKMSAsyncClient.this.executeSign(finalRequest);
                }
                catch (Exception ex) {
                    if (asyncHandler != null) {
                        asyncHandler.onError(ex);
                    }
                    throw ex;
                }
                if (asyncHandler != null) {
                    asyncHandler.onSuccess(finalRequest, result);
                }
                return result;
            }
        });
    }

    @Override
    public Future<TagResourceResult> tagResourceAsync(TagResourceRequest request) {
        return this.tagResourceAsync(request, null);
    }

    @Override
    public Future<TagResourceResult> tagResourceAsync(TagResourceRequest request, final AsyncHandler<TagResourceRequest, TagResourceResult> asyncHandler) {
        final TagResourceRequest finalRequest = this.beforeClientExecution(request);
        return this.executorService.submit(new Callable<TagResourceResult>(){

            @Override
            public TagResourceResult call() throws Exception {
                TagResourceResult result = null;
                try {
                    result = AWSKMSAsyncClient.this.executeTagResource(finalRequest);
                }
                catch (Exception ex) {
                    if (asyncHandler != null) {
                        asyncHandler.onError(ex);
                    }
                    throw ex;
                }
                if (asyncHandler != null) {
                    asyncHandler.onSuccess(finalRequest, result);
                }
                return result;
            }
        });
    }

    @Override
    public Future<UntagResourceResult> untagResourceAsync(UntagResourceRequest request) {
        return this.untagResourceAsync(request, null);
    }

    @Override
    public Future<UntagResourceResult> untagResourceAsync(UntagResourceRequest request, final AsyncHandler<UntagResourceRequest, UntagResourceResult> asyncHandler) {
        final UntagResourceRequest finalRequest = this.beforeClientExecution(request);
        return this.executorService.submit(new Callable<UntagResourceResult>(){

            @Override
            public UntagResourceResult call() throws Exception {
                UntagResourceResult result = null;
                try {
                    result = AWSKMSAsyncClient.this.executeUntagResource(finalRequest);
                }
                catch (Exception ex) {
                    if (asyncHandler != null) {
                        asyncHandler.onError(ex);
                    }
                    throw ex;
                }
                if (asyncHandler != null) {
                    asyncHandler.onSuccess(finalRequest, result);
                }
                return result;
            }
        });
    }

    @Override
    public Future<UpdateAliasResult> updateAliasAsync(UpdateAliasRequest request) {
        return this.updateAliasAsync(request, null);
    }

    @Override
    public Future<UpdateAliasResult> updateAliasAsync(UpdateAliasRequest request, final AsyncHandler<UpdateAliasRequest, UpdateAliasResult> asyncHandler) {
        final UpdateAliasRequest finalRequest = this.beforeClientExecution(request);
        return this.executorService.submit(new Callable<UpdateAliasResult>(){

            @Override
            public UpdateAliasResult call() throws Exception {
                UpdateAliasResult result = null;
                try {
                    result = AWSKMSAsyncClient.this.executeUpdateAlias(finalRequest);
                }
                catch (Exception ex) {
                    if (asyncHandler != null) {
                        asyncHandler.onError(ex);
                    }
                    throw ex;
                }
                if (asyncHandler != null) {
                    asyncHandler.onSuccess(finalRequest, result);
                }
                return result;
            }
        });
    }

    @Override
    public Future<UpdateCustomKeyStoreResult> updateCustomKeyStoreAsync(UpdateCustomKeyStoreRequest request) {
        return this.updateCustomKeyStoreAsync(request, null);
    }

    @Override
    public Future<UpdateCustomKeyStoreResult> updateCustomKeyStoreAsync(UpdateCustomKeyStoreRequest request, final AsyncHandler<UpdateCustomKeyStoreRequest, UpdateCustomKeyStoreResult> asyncHandler) {
        final UpdateCustomKeyStoreRequest finalRequest = this.beforeClientExecution(request);
        return this.executorService.submit(new Callable<UpdateCustomKeyStoreResult>(){

            @Override
            public UpdateCustomKeyStoreResult call() throws Exception {
                UpdateCustomKeyStoreResult result = null;
                try {
                    result = AWSKMSAsyncClient.this.executeUpdateCustomKeyStore(finalRequest);
                }
                catch (Exception ex) {
                    if (asyncHandler != null) {
                        asyncHandler.onError(ex);
                    }
                    throw ex;
                }
                if (asyncHandler != null) {
                    asyncHandler.onSuccess(finalRequest, result);
                }
                return result;
            }
        });
    }

    @Override
    public Future<UpdateKeyDescriptionResult> updateKeyDescriptionAsync(UpdateKeyDescriptionRequest request) {
        return this.updateKeyDescriptionAsync(request, null);
    }

    @Override
    public Future<UpdateKeyDescriptionResult> updateKeyDescriptionAsync(UpdateKeyDescriptionRequest request, final AsyncHandler<UpdateKeyDescriptionRequest, UpdateKeyDescriptionResult> asyncHandler) {
        final UpdateKeyDescriptionRequest finalRequest = this.beforeClientExecution(request);
        return this.executorService.submit(new Callable<UpdateKeyDescriptionResult>(){

            @Override
            public UpdateKeyDescriptionResult call() throws Exception {
                UpdateKeyDescriptionResult result = null;
                try {
                    result = AWSKMSAsyncClient.this.executeUpdateKeyDescription(finalRequest);
                }
                catch (Exception ex) {
                    if (asyncHandler != null) {
                        asyncHandler.onError(ex);
                    }
                    throw ex;
                }
                if (asyncHandler != null) {
                    asyncHandler.onSuccess(finalRequest, result);
                }
                return result;
            }
        });
    }

    @Override
    public Future<UpdatePrimaryRegionResult> updatePrimaryRegionAsync(UpdatePrimaryRegionRequest request) {
        return this.updatePrimaryRegionAsync(request, null);
    }

    @Override
    public Future<UpdatePrimaryRegionResult> updatePrimaryRegionAsync(UpdatePrimaryRegionRequest request, final AsyncHandler<UpdatePrimaryRegionRequest, UpdatePrimaryRegionResult> asyncHandler) {
        final UpdatePrimaryRegionRequest finalRequest = this.beforeClientExecution(request);
        return this.executorService.submit(new Callable<UpdatePrimaryRegionResult>(){

            @Override
            public UpdatePrimaryRegionResult call() throws Exception {
                UpdatePrimaryRegionResult result = null;
                try {
                    result = AWSKMSAsyncClient.this.executeUpdatePrimaryRegion(finalRequest);
                }
                catch (Exception ex) {
                    if (asyncHandler != null) {
                        asyncHandler.onError(ex);
                    }
                    throw ex;
                }
                if (asyncHandler != null) {
                    asyncHandler.onSuccess(finalRequest, result);
                }
                return result;
            }
        });
    }

    @Override
    public Future<VerifyResult> verifyAsync(VerifyRequest request) {
        return this.verifyAsync(request, null);
    }

    @Override
    public Future<VerifyResult> verifyAsync(VerifyRequest request, final AsyncHandler<VerifyRequest, VerifyResult> asyncHandler) {
        final VerifyRequest finalRequest = this.beforeClientExecution(request);
        return this.executorService.submit(new Callable<VerifyResult>(){

            @Override
            public VerifyResult call() throws Exception {
                VerifyResult result = null;
                try {
                    result = AWSKMSAsyncClient.this.executeVerify(finalRequest);
                }
                catch (Exception ex) {
                    if (asyncHandler != null) {
                        asyncHandler.onError(ex);
                    }
                    throw ex;
                }
                if (asyncHandler != null) {
                    asyncHandler.onSuccess(finalRequest, result);
                }
                return result;
            }
        });
    }

    @Override
    public Future<VerifyMacResult> verifyMacAsync(VerifyMacRequest request) {
        return this.verifyMacAsync(request, null);
    }

    @Override
    public Future<VerifyMacResult> verifyMacAsync(VerifyMacRequest request, final AsyncHandler<VerifyMacRequest, VerifyMacResult> asyncHandler) {
        final VerifyMacRequest finalRequest = this.beforeClientExecution(request);
        return this.executorService.submit(new Callable<VerifyMacResult>(){

            @Override
            public VerifyMacResult call() throws Exception {
                VerifyMacResult result = null;
                try {
                    result = AWSKMSAsyncClient.this.executeVerifyMac(finalRequest);
                }
                catch (Exception ex) {
                    if (asyncHandler != null) {
                        asyncHandler.onError(ex);
                    }
                    throw ex;
                }
                if (asyncHandler != null) {
                    asyncHandler.onSuccess(finalRequest, result);
                }
                return result;
            }
        });
    }

    @Override
    public void shutdown() {
        super.shutdown();
        this.executorService.shutdownNow();
    }
}

