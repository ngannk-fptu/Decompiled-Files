/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.kms;

import com.amazonaws.handlers.AsyncHandler;
import com.amazonaws.services.kms.AWSKMSAsync;
import com.amazonaws.services.kms.AbstractAWSKMS;
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
import java.util.concurrent.Future;

public class AbstractAWSKMSAsync
extends AbstractAWSKMS
implements AWSKMSAsync {
    protected AbstractAWSKMSAsync() {
    }

    @Override
    public Future<CancelKeyDeletionResult> cancelKeyDeletionAsync(CancelKeyDeletionRequest request) {
        return this.cancelKeyDeletionAsync(request, null);
    }

    @Override
    public Future<CancelKeyDeletionResult> cancelKeyDeletionAsync(CancelKeyDeletionRequest request, AsyncHandler<CancelKeyDeletionRequest, CancelKeyDeletionResult> asyncHandler) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Future<ConnectCustomKeyStoreResult> connectCustomKeyStoreAsync(ConnectCustomKeyStoreRequest request) {
        return this.connectCustomKeyStoreAsync(request, null);
    }

    @Override
    public Future<ConnectCustomKeyStoreResult> connectCustomKeyStoreAsync(ConnectCustomKeyStoreRequest request, AsyncHandler<ConnectCustomKeyStoreRequest, ConnectCustomKeyStoreResult> asyncHandler) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Future<CreateAliasResult> createAliasAsync(CreateAliasRequest request) {
        return this.createAliasAsync(request, null);
    }

    @Override
    public Future<CreateAliasResult> createAliasAsync(CreateAliasRequest request, AsyncHandler<CreateAliasRequest, CreateAliasResult> asyncHandler) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Future<CreateCustomKeyStoreResult> createCustomKeyStoreAsync(CreateCustomKeyStoreRequest request) {
        return this.createCustomKeyStoreAsync(request, null);
    }

    @Override
    public Future<CreateCustomKeyStoreResult> createCustomKeyStoreAsync(CreateCustomKeyStoreRequest request, AsyncHandler<CreateCustomKeyStoreRequest, CreateCustomKeyStoreResult> asyncHandler) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Future<CreateGrantResult> createGrantAsync(CreateGrantRequest request) {
        return this.createGrantAsync(request, null);
    }

    @Override
    public Future<CreateGrantResult> createGrantAsync(CreateGrantRequest request, AsyncHandler<CreateGrantRequest, CreateGrantResult> asyncHandler) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Future<CreateKeyResult> createKeyAsync(CreateKeyRequest request) {
        return this.createKeyAsync(request, null);
    }

    @Override
    public Future<CreateKeyResult> createKeyAsync(CreateKeyRequest request, AsyncHandler<CreateKeyRequest, CreateKeyResult> asyncHandler) {
        throw new UnsupportedOperationException();
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
    public Future<DecryptResult> decryptAsync(DecryptRequest request, AsyncHandler<DecryptRequest, DecryptResult> asyncHandler) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Future<DeleteAliasResult> deleteAliasAsync(DeleteAliasRequest request) {
        return this.deleteAliasAsync(request, null);
    }

    @Override
    public Future<DeleteAliasResult> deleteAliasAsync(DeleteAliasRequest request, AsyncHandler<DeleteAliasRequest, DeleteAliasResult> asyncHandler) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Future<DeleteCustomKeyStoreResult> deleteCustomKeyStoreAsync(DeleteCustomKeyStoreRequest request) {
        return this.deleteCustomKeyStoreAsync(request, null);
    }

    @Override
    public Future<DeleteCustomKeyStoreResult> deleteCustomKeyStoreAsync(DeleteCustomKeyStoreRequest request, AsyncHandler<DeleteCustomKeyStoreRequest, DeleteCustomKeyStoreResult> asyncHandler) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Future<DeleteImportedKeyMaterialResult> deleteImportedKeyMaterialAsync(DeleteImportedKeyMaterialRequest request) {
        return this.deleteImportedKeyMaterialAsync(request, null);
    }

    @Override
    public Future<DeleteImportedKeyMaterialResult> deleteImportedKeyMaterialAsync(DeleteImportedKeyMaterialRequest request, AsyncHandler<DeleteImportedKeyMaterialRequest, DeleteImportedKeyMaterialResult> asyncHandler) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Future<DescribeCustomKeyStoresResult> describeCustomKeyStoresAsync(DescribeCustomKeyStoresRequest request) {
        return this.describeCustomKeyStoresAsync(request, null);
    }

    @Override
    public Future<DescribeCustomKeyStoresResult> describeCustomKeyStoresAsync(DescribeCustomKeyStoresRequest request, AsyncHandler<DescribeCustomKeyStoresRequest, DescribeCustomKeyStoresResult> asyncHandler) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Future<DescribeKeyResult> describeKeyAsync(DescribeKeyRequest request) {
        return this.describeKeyAsync(request, null);
    }

    @Override
    public Future<DescribeKeyResult> describeKeyAsync(DescribeKeyRequest request, AsyncHandler<DescribeKeyRequest, DescribeKeyResult> asyncHandler) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Future<DisableKeyResult> disableKeyAsync(DisableKeyRequest request) {
        return this.disableKeyAsync(request, null);
    }

    @Override
    public Future<DisableKeyResult> disableKeyAsync(DisableKeyRequest request, AsyncHandler<DisableKeyRequest, DisableKeyResult> asyncHandler) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Future<DisableKeyRotationResult> disableKeyRotationAsync(DisableKeyRotationRequest request) {
        return this.disableKeyRotationAsync(request, null);
    }

    @Override
    public Future<DisableKeyRotationResult> disableKeyRotationAsync(DisableKeyRotationRequest request, AsyncHandler<DisableKeyRotationRequest, DisableKeyRotationResult> asyncHandler) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Future<DisconnectCustomKeyStoreResult> disconnectCustomKeyStoreAsync(DisconnectCustomKeyStoreRequest request) {
        return this.disconnectCustomKeyStoreAsync(request, null);
    }

    @Override
    public Future<DisconnectCustomKeyStoreResult> disconnectCustomKeyStoreAsync(DisconnectCustomKeyStoreRequest request, AsyncHandler<DisconnectCustomKeyStoreRequest, DisconnectCustomKeyStoreResult> asyncHandler) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Future<EnableKeyResult> enableKeyAsync(EnableKeyRequest request) {
        return this.enableKeyAsync(request, null);
    }

    @Override
    public Future<EnableKeyResult> enableKeyAsync(EnableKeyRequest request, AsyncHandler<EnableKeyRequest, EnableKeyResult> asyncHandler) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Future<EnableKeyRotationResult> enableKeyRotationAsync(EnableKeyRotationRequest request) {
        return this.enableKeyRotationAsync(request, null);
    }

    @Override
    public Future<EnableKeyRotationResult> enableKeyRotationAsync(EnableKeyRotationRequest request, AsyncHandler<EnableKeyRotationRequest, EnableKeyRotationResult> asyncHandler) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Future<EncryptResult> encryptAsync(EncryptRequest request) {
        return this.encryptAsync(request, null);
    }

    @Override
    public Future<EncryptResult> encryptAsync(EncryptRequest request, AsyncHandler<EncryptRequest, EncryptResult> asyncHandler) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Future<GenerateDataKeyResult> generateDataKeyAsync(GenerateDataKeyRequest request) {
        return this.generateDataKeyAsync(request, null);
    }

    @Override
    public Future<GenerateDataKeyResult> generateDataKeyAsync(GenerateDataKeyRequest request, AsyncHandler<GenerateDataKeyRequest, GenerateDataKeyResult> asyncHandler) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Future<GenerateDataKeyPairResult> generateDataKeyPairAsync(GenerateDataKeyPairRequest request) {
        return this.generateDataKeyPairAsync(request, null);
    }

    @Override
    public Future<GenerateDataKeyPairResult> generateDataKeyPairAsync(GenerateDataKeyPairRequest request, AsyncHandler<GenerateDataKeyPairRequest, GenerateDataKeyPairResult> asyncHandler) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Future<GenerateDataKeyPairWithoutPlaintextResult> generateDataKeyPairWithoutPlaintextAsync(GenerateDataKeyPairWithoutPlaintextRequest request) {
        return this.generateDataKeyPairWithoutPlaintextAsync(request, null);
    }

    @Override
    public Future<GenerateDataKeyPairWithoutPlaintextResult> generateDataKeyPairWithoutPlaintextAsync(GenerateDataKeyPairWithoutPlaintextRequest request, AsyncHandler<GenerateDataKeyPairWithoutPlaintextRequest, GenerateDataKeyPairWithoutPlaintextResult> asyncHandler) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Future<GenerateDataKeyWithoutPlaintextResult> generateDataKeyWithoutPlaintextAsync(GenerateDataKeyWithoutPlaintextRequest request) {
        return this.generateDataKeyWithoutPlaintextAsync(request, null);
    }

    @Override
    public Future<GenerateDataKeyWithoutPlaintextResult> generateDataKeyWithoutPlaintextAsync(GenerateDataKeyWithoutPlaintextRequest request, AsyncHandler<GenerateDataKeyWithoutPlaintextRequest, GenerateDataKeyWithoutPlaintextResult> asyncHandler) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Future<GenerateMacResult> generateMacAsync(GenerateMacRequest request) {
        return this.generateMacAsync(request, null);
    }

    @Override
    public Future<GenerateMacResult> generateMacAsync(GenerateMacRequest request, AsyncHandler<GenerateMacRequest, GenerateMacResult> asyncHandler) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Future<GenerateRandomResult> generateRandomAsync(GenerateRandomRequest request) {
        return this.generateRandomAsync(request, null);
    }

    @Override
    public Future<GenerateRandomResult> generateRandomAsync(GenerateRandomRequest request, AsyncHandler<GenerateRandomRequest, GenerateRandomResult> asyncHandler) {
        throw new UnsupportedOperationException();
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
    public Future<GetKeyPolicyResult> getKeyPolicyAsync(GetKeyPolicyRequest request, AsyncHandler<GetKeyPolicyRequest, GetKeyPolicyResult> asyncHandler) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Future<GetKeyRotationStatusResult> getKeyRotationStatusAsync(GetKeyRotationStatusRequest request) {
        return this.getKeyRotationStatusAsync(request, null);
    }

    @Override
    public Future<GetKeyRotationStatusResult> getKeyRotationStatusAsync(GetKeyRotationStatusRequest request, AsyncHandler<GetKeyRotationStatusRequest, GetKeyRotationStatusResult> asyncHandler) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Future<GetParametersForImportResult> getParametersForImportAsync(GetParametersForImportRequest request) {
        return this.getParametersForImportAsync(request, null);
    }

    @Override
    public Future<GetParametersForImportResult> getParametersForImportAsync(GetParametersForImportRequest request, AsyncHandler<GetParametersForImportRequest, GetParametersForImportResult> asyncHandler) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Future<GetPublicKeyResult> getPublicKeyAsync(GetPublicKeyRequest request) {
        return this.getPublicKeyAsync(request, null);
    }

    @Override
    public Future<GetPublicKeyResult> getPublicKeyAsync(GetPublicKeyRequest request, AsyncHandler<GetPublicKeyRequest, GetPublicKeyResult> asyncHandler) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Future<ImportKeyMaterialResult> importKeyMaterialAsync(ImportKeyMaterialRequest request) {
        return this.importKeyMaterialAsync(request, null);
    }

    @Override
    public Future<ImportKeyMaterialResult> importKeyMaterialAsync(ImportKeyMaterialRequest request, AsyncHandler<ImportKeyMaterialRequest, ImportKeyMaterialResult> asyncHandler) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Future<ListAliasesResult> listAliasesAsync(ListAliasesRequest request) {
        return this.listAliasesAsync(request, null);
    }

    @Override
    public Future<ListAliasesResult> listAliasesAsync(ListAliasesRequest request, AsyncHandler<ListAliasesRequest, ListAliasesResult> asyncHandler) {
        throw new UnsupportedOperationException();
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
    public Future<ListGrantsResult> listGrantsAsync(ListGrantsRequest request, AsyncHandler<ListGrantsRequest, ListGrantsResult> asyncHandler) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Future<ListKeyPoliciesResult> listKeyPoliciesAsync(ListKeyPoliciesRequest request) {
        return this.listKeyPoliciesAsync(request, null);
    }

    @Override
    public Future<ListKeyPoliciesResult> listKeyPoliciesAsync(ListKeyPoliciesRequest request, AsyncHandler<ListKeyPoliciesRequest, ListKeyPoliciesResult> asyncHandler) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Future<ListKeysResult> listKeysAsync(ListKeysRequest request) {
        return this.listKeysAsync(request, null);
    }

    @Override
    public Future<ListKeysResult> listKeysAsync(ListKeysRequest request, AsyncHandler<ListKeysRequest, ListKeysResult> asyncHandler) {
        throw new UnsupportedOperationException();
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
    public Future<ListResourceTagsResult> listResourceTagsAsync(ListResourceTagsRequest request, AsyncHandler<ListResourceTagsRequest, ListResourceTagsResult> asyncHandler) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Future<ListRetirableGrantsResult> listRetirableGrantsAsync(ListRetirableGrantsRequest request) {
        return this.listRetirableGrantsAsync(request, null);
    }

    @Override
    public Future<ListRetirableGrantsResult> listRetirableGrantsAsync(ListRetirableGrantsRequest request, AsyncHandler<ListRetirableGrantsRequest, ListRetirableGrantsResult> asyncHandler) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Future<PutKeyPolicyResult> putKeyPolicyAsync(PutKeyPolicyRequest request) {
        return this.putKeyPolicyAsync(request, null);
    }

    @Override
    public Future<PutKeyPolicyResult> putKeyPolicyAsync(PutKeyPolicyRequest request, AsyncHandler<PutKeyPolicyRequest, PutKeyPolicyResult> asyncHandler) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Future<ReEncryptResult> reEncryptAsync(ReEncryptRequest request) {
        return this.reEncryptAsync(request, null);
    }

    @Override
    public Future<ReEncryptResult> reEncryptAsync(ReEncryptRequest request, AsyncHandler<ReEncryptRequest, ReEncryptResult> asyncHandler) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Future<ReplicateKeyResult> replicateKeyAsync(ReplicateKeyRequest request) {
        return this.replicateKeyAsync(request, null);
    }

    @Override
    public Future<ReplicateKeyResult> replicateKeyAsync(ReplicateKeyRequest request, AsyncHandler<ReplicateKeyRequest, ReplicateKeyResult> asyncHandler) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Future<RetireGrantResult> retireGrantAsync(RetireGrantRequest request) {
        return this.retireGrantAsync(request, null);
    }

    @Override
    public Future<RetireGrantResult> retireGrantAsync(RetireGrantRequest request, AsyncHandler<RetireGrantRequest, RetireGrantResult> asyncHandler) {
        throw new UnsupportedOperationException();
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
    public Future<RevokeGrantResult> revokeGrantAsync(RevokeGrantRequest request, AsyncHandler<RevokeGrantRequest, RevokeGrantResult> asyncHandler) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Future<ScheduleKeyDeletionResult> scheduleKeyDeletionAsync(ScheduleKeyDeletionRequest request) {
        return this.scheduleKeyDeletionAsync(request, null);
    }

    @Override
    public Future<ScheduleKeyDeletionResult> scheduleKeyDeletionAsync(ScheduleKeyDeletionRequest request, AsyncHandler<ScheduleKeyDeletionRequest, ScheduleKeyDeletionResult> asyncHandler) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Future<SignResult> signAsync(SignRequest request) {
        return this.signAsync(request, null);
    }

    @Override
    public Future<SignResult> signAsync(SignRequest request, AsyncHandler<SignRequest, SignResult> asyncHandler) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Future<TagResourceResult> tagResourceAsync(TagResourceRequest request) {
        return this.tagResourceAsync(request, null);
    }

    @Override
    public Future<TagResourceResult> tagResourceAsync(TagResourceRequest request, AsyncHandler<TagResourceRequest, TagResourceResult> asyncHandler) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Future<UntagResourceResult> untagResourceAsync(UntagResourceRequest request) {
        return this.untagResourceAsync(request, null);
    }

    @Override
    public Future<UntagResourceResult> untagResourceAsync(UntagResourceRequest request, AsyncHandler<UntagResourceRequest, UntagResourceResult> asyncHandler) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Future<UpdateAliasResult> updateAliasAsync(UpdateAliasRequest request) {
        return this.updateAliasAsync(request, null);
    }

    @Override
    public Future<UpdateAliasResult> updateAliasAsync(UpdateAliasRequest request, AsyncHandler<UpdateAliasRequest, UpdateAliasResult> asyncHandler) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Future<UpdateCustomKeyStoreResult> updateCustomKeyStoreAsync(UpdateCustomKeyStoreRequest request) {
        return this.updateCustomKeyStoreAsync(request, null);
    }

    @Override
    public Future<UpdateCustomKeyStoreResult> updateCustomKeyStoreAsync(UpdateCustomKeyStoreRequest request, AsyncHandler<UpdateCustomKeyStoreRequest, UpdateCustomKeyStoreResult> asyncHandler) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Future<UpdateKeyDescriptionResult> updateKeyDescriptionAsync(UpdateKeyDescriptionRequest request) {
        return this.updateKeyDescriptionAsync(request, null);
    }

    @Override
    public Future<UpdateKeyDescriptionResult> updateKeyDescriptionAsync(UpdateKeyDescriptionRequest request, AsyncHandler<UpdateKeyDescriptionRequest, UpdateKeyDescriptionResult> asyncHandler) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Future<UpdatePrimaryRegionResult> updatePrimaryRegionAsync(UpdatePrimaryRegionRequest request) {
        return this.updatePrimaryRegionAsync(request, null);
    }

    @Override
    public Future<UpdatePrimaryRegionResult> updatePrimaryRegionAsync(UpdatePrimaryRegionRequest request, AsyncHandler<UpdatePrimaryRegionRequest, UpdatePrimaryRegionResult> asyncHandler) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Future<VerifyResult> verifyAsync(VerifyRequest request) {
        return this.verifyAsync(request, null);
    }

    @Override
    public Future<VerifyResult> verifyAsync(VerifyRequest request, AsyncHandler<VerifyRequest, VerifyResult> asyncHandler) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Future<VerifyMacResult> verifyMacAsync(VerifyMacRequest request) {
        return this.verifyMacAsync(request, null);
    }

    @Override
    public Future<VerifyMacResult> verifyMacAsync(VerifyMacRequest request, AsyncHandler<VerifyMacRequest, VerifyMacResult> asyncHandler) {
        throw new UnsupportedOperationException();
    }
}

