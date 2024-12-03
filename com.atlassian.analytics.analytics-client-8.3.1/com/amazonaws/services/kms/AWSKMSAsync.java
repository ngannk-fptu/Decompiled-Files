/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.kms;

import com.amazonaws.handlers.AsyncHandler;
import com.amazonaws.services.kms.AWSKMS;
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

public interface AWSKMSAsync
extends AWSKMS {
    public Future<CancelKeyDeletionResult> cancelKeyDeletionAsync(CancelKeyDeletionRequest var1);

    public Future<CancelKeyDeletionResult> cancelKeyDeletionAsync(CancelKeyDeletionRequest var1, AsyncHandler<CancelKeyDeletionRequest, CancelKeyDeletionResult> var2);

    public Future<ConnectCustomKeyStoreResult> connectCustomKeyStoreAsync(ConnectCustomKeyStoreRequest var1);

    public Future<ConnectCustomKeyStoreResult> connectCustomKeyStoreAsync(ConnectCustomKeyStoreRequest var1, AsyncHandler<ConnectCustomKeyStoreRequest, ConnectCustomKeyStoreResult> var2);

    public Future<CreateAliasResult> createAliasAsync(CreateAliasRequest var1);

    public Future<CreateAliasResult> createAliasAsync(CreateAliasRequest var1, AsyncHandler<CreateAliasRequest, CreateAliasResult> var2);

    public Future<CreateCustomKeyStoreResult> createCustomKeyStoreAsync(CreateCustomKeyStoreRequest var1);

    public Future<CreateCustomKeyStoreResult> createCustomKeyStoreAsync(CreateCustomKeyStoreRequest var1, AsyncHandler<CreateCustomKeyStoreRequest, CreateCustomKeyStoreResult> var2);

    public Future<CreateGrantResult> createGrantAsync(CreateGrantRequest var1);

    public Future<CreateGrantResult> createGrantAsync(CreateGrantRequest var1, AsyncHandler<CreateGrantRequest, CreateGrantResult> var2);

    public Future<CreateKeyResult> createKeyAsync(CreateKeyRequest var1);

    public Future<CreateKeyResult> createKeyAsync(CreateKeyRequest var1, AsyncHandler<CreateKeyRequest, CreateKeyResult> var2);

    public Future<CreateKeyResult> createKeyAsync();

    public Future<CreateKeyResult> createKeyAsync(AsyncHandler<CreateKeyRequest, CreateKeyResult> var1);

    public Future<DecryptResult> decryptAsync(DecryptRequest var1);

    public Future<DecryptResult> decryptAsync(DecryptRequest var1, AsyncHandler<DecryptRequest, DecryptResult> var2);

    public Future<DeleteAliasResult> deleteAliasAsync(DeleteAliasRequest var1);

    public Future<DeleteAliasResult> deleteAliasAsync(DeleteAliasRequest var1, AsyncHandler<DeleteAliasRequest, DeleteAliasResult> var2);

    public Future<DeleteCustomKeyStoreResult> deleteCustomKeyStoreAsync(DeleteCustomKeyStoreRequest var1);

    public Future<DeleteCustomKeyStoreResult> deleteCustomKeyStoreAsync(DeleteCustomKeyStoreRequest var1, AsyncHandler<DeleteCustomKeyStoreRequest, DeleteCustomKeyStoreResult> var2);

    public Future<DeleteImportedKeyMaterialResult> deleteImportedKeyMaterialAsync(DeleteImportedKeyMaterialRequest var1);

    public Future<DeleteImportedKeyMaterialResult> deleteImportedKeyMaterialAsync(DeleteImportedKeyMaterialRequest var1, AsyncHandler<DeleteImportedKeyMaterialRequest, DeleteImportedKeyMaterialResult> var2);

    public Future<DescribeCustomKeyStoresResult> describeCustomKeyStoresAsync(DescribeCustomKeyStoresRequest var1);

    public Future<DescribeCustomKeyStoresResult> describeCustomKeyStoresAsync(DescribeCustomKeyStoresRequest var1, AsyncHandler<DescribeCustomKeyStoresRequest, DescribeCustomKeyStoresResult> var2);

    public Future<DescribeKeyResult> describeKeyAsync(DescribeKeyRequest var1);

    public Future<DescribeKeyResult> describeKeyAsync(DescribeKeyRequest var1, AsyncHandler<DescribeKeyRequest, DescribeKeyResult> var2);

    public Future<DisableKeyResult> disableKeyAsync(DisableKeyRequest var1);

    public Future<DisableKeyResult> disableKeyAsync(DisableKeyRequest var1, AsyncHandler<DisableKeyRequest, DisableKeyResult> var2);

    public Future<DisableKeyRotationResult> disableKeyRotationAsync(DisableKeyRotationRequest var1);

    public Future<DisableKeyRotationResult> disableKeyRotationAsync(DisableKeyRotationRequest var1, AsyncHandler<DisableKeyRotationRequest, DisableKeyRotationResult> var2);

    public Future<DisconnectCustomKeyStoreResult> disconnectCustomKeyStoreAsync(DisconnectCustomKeyStoreRequest var1);

    public Future<DisconnectCustomKeyStoreResult> disconnectCustomKeyStoreAsync(DisconnectCustomKeyStoreRequest var1, AsyncHandler<DisconnectCustomKeyStoreRequest, DisconnectCustomKeyStoreResult> var2);

    public Future<EnableKeyResult> enableKeyAsync(EnableKeyRequest var1);

    public Future<EnableKeyResult> enableKeyAsync(EnableKeyRequest var1, AsyncHandler<EnableKeyRequest, EnableKeyResult> var2);

    public Future<EnableKeyRotationResult> enableKeyRotationAsync(EnableKeyRotationRequest var1);

    public Future<EnableKeyRotationResult> enableKeyRotationAsync(EnableKeyRotationRequest var1, AsyncHandler<EnableKeyRotationRequest, EnableKeyRotationResult> var2);

    public Future<EncryptResult> encryptAsync(EncryptRequest var1);

    public Future<EncryptResult> encryptAsync(EncryptRequest var1, AsyncHandler<EncryptRequest, EncryptResult> var2);

    public Future<GenerateDataKeyResult> generateDataKeyAsync(GenerateDataKeyRequest var1);

    public Future<GenerateDataKeyResult> generateDataKeyAsync(GenerateDataKeyRequest var1, AsyncHandler<GenerateDataKeyRequest, GenerateDataKeyResult> var2);

    public Future<GenerateDataKeyPairResult> generateDataKeyPairAsync(GenerateDataKeyPairRequest var1);

    public Future<GenerateDataKeyPairResult> generateDataKeyPairAsync(GenerateDataKeyPairRequest var1, AsyncHandler<GenerateDataKeyPairRequest, GenerateDataKeyPairResult> var2);

    public Future<GenerateDataKeyPairWithoutPlaintextResult> generateDataKeyPairWithoutPlaintextAsync(GenerateDataKeyPairWithoutPlaintextRequest var1);

    public Future<GenerateDataKeyPairWithoutPlaintextResult> generateDataKeyPairWithoutPlaintextAsync(GenerateDataKeyPairWithoutPlaintextRequest var1, AsyncHandler<GenerateDataKeyPairWithoutPlaintextRequest, GenerateDataKeyPairWithoutPlaintextResult> var2);

    public Future<GenerateDataKeyWithoutPlaintextResult> generateDataKeyWithoutPlaintextAsync(GenerateDataKeyWithoutPlaintextRequest var1);

    public Future<GenerateDataKeyWithoutPlaintextResult> generateDataKeyWithoutPlaintextAsync(GenerateDataKeyWithoutPlaintextRequest var1, AsyncHandler<GenerateDataKeyWithoutPlaintextRequest, GenerateDataKeyWithoutPlaintextResult> var2);

    public Future<GenerateMacResult> generateMacAsync(GenerateMacRequest var1);

    public Future<GenerateMacResult> generateMacAsync(GenerateMacRequest var1, AsyncHandler<GenerateMacRequest, GenerateMacResult> var2);

    public Future<GenerateRandomResult> generateRandomAsync(GenerateRandomRequest var1);

    public Future<GenerateRandomResult> generateRandomAsync(GenerateRandomRequest var1, AsyncHandler<GenerateRandomRequest, GenerateRandomResult> var2);

    public Future<GenerateRandomResult> generateRandomAsync();

    public Future<GenerateRandomResult> generateRandomAsync(AsyncHandler<GenerateRandomRequest, GenerateRandomResult> var1);

    public Future<GetKeyPolicyResult> getKeyPolicyAsync(GetKeyPolicyRequest var1);

    public Future<GetKeyPolicyResult> getKeyPolicyAsync(GetKeyPolicyRequest var1, AsyncHandler<GetKeyPolicyRequest, GetKeyPolicyResult> var2);

    public Future<GetKeyRotationStatusResult> getKeyRotationStatusAsync(GetKeyRotationStatusRequest var1);

    public Future<GetKeyRotationStatusResult> getKeyRotationStatusAsync(GetKeyRotationStatusRequest var1, AsyncHandler<GetKeyRotationStatusRequest, GetKeyRotationStatusResult> var2);

    public Future<GetParametersForImportResult> getParametersForImportAsync(GetParametersForImportRequest var1);

    public Future<GetParametersForImportResult> getParametersForImportAsync(GetParametersForImportRequest var1, AsyncHandler<GetParametersForImportRequest, GetParametersForImportResult> var2);

    public Future<GetPublicKeyResult> getPublicKeyAsync(GetPublicKeyRequest var1);

    public Future<GetPublicKeyResult> getPublicKeyAsync(GetPublicKeyRequest var1, AsyncHandler<GetPublicKeyRequest, GetPublicKeyResult> var2);

    public Future<ImportKeyMaterialResult> importKeyMaterialAsync(ImportKeyMaterialRequest var1);

    public Future<ImportKeyMaterialResult> importKeyMaterialAsync(ImportKeyMaterialRequest var1, AsyncHandler<ImportKeyMaterialRequest, ImportKeyMaterialResult> var2);

    public Future<ListAliasesResult> listAliasesAsync(ListAliasesRequest var1);

    public Future<ListAliasesResult> listAliasesAsync(ListAliasesRequest var1, AsyncHandler<ListAliasesRequest, ListAliasesResult> var2);

    public Future<ListAliasesResult> listAliasesAsync();

    public Future<ListAliasesResult> listAliasesAsync(AsyncHandler<ListAliasesRequest, ListAliasesResult> var1);

    public Future<ListGrantsResult> listGrantsAsync(ListGrantsRequest var1);

    public Future<ListGrantsResult> listGrantsAsync(ListGrantsRequest var1, AsyncHandler<ListGrantsRequest, ListGrantsResult> var2);

    public Future<ListKeyPoliciesResult> listKeyPoliciesAsync(ListKeyPoliciesRequest var1);

    public Future<ListKeyPoliciesResult> listKeyPoliciesAsync(ListKeyPoliciesRequest var1, AsyncHandler<ListKeyPoliciesRequest, ListKeyPoliciesResult> var2);

    public Future<ListKeysResult> listKeysAsync(ListKeysRequest var1);

    public Future<ListKeysResult> listKeysAsync(ListKeysRequest var1, AsyncHandler<ListKeysRequest, ListKeysResult> var2);

    public Future<ListKeysResult> listKeysAsync();

    public Future<ListKeysResult> listKeysAsync(AsyncHandler<ListKeysRequest, ListKeysResult> var1);

    public Future<ListResourceTagsResult> listResourceTagsAsync(ListResourceTagsRequest var1);

    public Future<ListResourceTagsResult> listResourceTagsAsync(ListResourceTagsRequest var1, AsyncHandler<ListResourceTagsRequest, ListResourceTagsResult> var2);

    public Future<ListRetirableGrantsResult> listRetirableGrantsAsync(ListRetirableGrantsRequest var1);

    public Future<ListRetirableGrantsResult> listRetirableGrantsAsync(ListRetirableGrantsRequest var1, AsyncHandler<ListRetirableGrantsRequest, ListRetirableGrantsResult> var2);

    public Future<PutKeyPolicyResult> putKeyPolicyAsync(PutKeyPolicyRequest var1);

    public Future<PutKeyPolicyResult> putKeyPolicyAsync(PutKeyPolicyRequest var1, AsyncHandler<PutKeyPolicyRequest, PutKeyPolicyResult> var2);

    public Future<ReEncryptResult> reEncryptAsync(ReEncryptRequest var1);

    public Future<ReEncryptResult> reEncryptAsync(ReEncryptRequest var1, AsyncHandler<ReEncryptRequest, ReEncryptResult> var2);

    public Future<ReplicateKeyResult> replicateKeyAsync(ReplicateKeyRequest var1);

    public Future<ReplicateKeyResult> replicateKeyAsync(ReplicateKeyRequest var1, AsyncHandler<ReplicateKeyRequest, ReplicateKeyResult> var2);

    public Future<RetireGrantResult> retireGrantAsync(RetireGrantRequest var1);

    public Future<RetireGrantResult> retireGrantAsync(RetireGrantRequest var1, AsyncHandler<RetireGrantRequest, RetireGrantResult> var2);

    public Future<RetireGrantResult> retireGrantAsync();

    public Future<RetireGrantResult> retireGrantAsync(AsyncHandler<RetireGrantRequest, RetireGrantResult> var1);

    public Future<RevokeGrantResult> revokeGrantAsync(RevokeGrantRequest var1);

    public Future<RevokeGrantResult> revokeGrantAsync(RevokeGrantRequest var1, AsyncHandler<RevokeGrantRequest, RevokeGrantResult> var2);

    public Future<ScheduleKeyDeletionResult> scheduleKeyDeletionAsync(ScheduleKeyDeletionRequest var1);

    public Future<ScheduleKeyDeletionResult> scheduleKeyDeletionAsync(ScheduleKeyDeletionRequest var1, AsyncHandler<ScheduleKeyDeletionRequest, ScheduleKeyDeletionResult> var2);

    public Future<SignResult> signAsync(SignRequest var1);

    public Future<SignResult> signAsync(SignRequest var1, AsyncHandler<SignRequest, SignResult> var2);

    public Future<TagResourceResult> tagResourceAsync(TagResourceRequest var1);

    public Future<TagResourceResult> tagResourceAsync(TagResourceRequest var1, AsyncHandler<TagResourceRequest, TagResourceResult> var2);

    public Future<UntagResourceResult> untagResourceAsync(UntagResourceRequest var1);

    public Future<UntagResourceResult> untagResourceAsync(UntagResourceRequest var1, AsyncHandler<UntagResourceRequest, UntagResourceResult> var2);

    public Future<UpdateAliasResult> updateAliasAsync(UpdateAliasRequest var1);

    public Future<UpdateAliasResult> updateAliasAsync(UpdateAliasRequest var1, AsyncHandler<UpdateAliasRequest, UpdateAliasResult> var2);

    public Future<UpdateCustomKeyStoreResult> updateCustomKeyStoreAsync(UpdateCustomKeyStoreRequest var1);

    public Future<UpdateCustomKeyStoreResult> updateCustomKeyStoreAsync(UpdateCustomKeyStoreRequest var1, AsyncHandler<UpdateCustomKeyStoreRequest, UpdateCustomKeyStoreResult> var2);

    public Future<UpdateKeyDescriptionResult> updateKeyDescriptionAsync(UpdateKeyDescriptionRequest var1);

    public Future<UpdateKeyDescriptionResult> updateKeyDescriptionAsync(UpdateKeyDescriptionRequest var1, AsyncHandler<UpdateKeyDescriptionRequest, UpdateKeyDescriptionResult> var2);

    public Future<UpdatePrimaryRegionResult> updatePrimaryRegionAsync(UpdatePrimaryRegionRequest var1);

    public Future<UpdatePrimaryRegionResult> updatePrimaryRegionAsync(UpdatePrimaryRegionRequest var1, AsyncHandler<UpdatePrimaryRegionRequest, UpdatePrimaryRegionResult> var2);

    public Future<VerifyResult> verifyAsync(VerifyRequest var1);

    public Future<VerifyResult> verifyAsync(VerifyRequest var1, AsyncHandler<VerifyRequest, VerifyResult> var2);

    public Future<VerifyMacResult> verifyMacAsync(VerifyMacRequest var1);

    public Future<VerifyMacResult> verifyMacAsync(VerifyMacRequest var1, AsyncHandler<VerifyMacRequest, VerifyMacResult> var2);
}

