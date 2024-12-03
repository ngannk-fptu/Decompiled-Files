/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.kms;

import com.amazonaws.AmazonWebServiceRequest;
import com.amazonaws.ResponseMetadata;
import com.amazonaws.regions.Region;
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

public class AbstractAWSKMS
implements AWSKMS {
    protected AbstractAWSKMS() {
    }

    @Override
    public void setEndpoint(String endpoint) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setRegion(Region region) {
        throw new UnsupportedOperationException();
    }

    @Override
    public CancelKeyDeletionResult cancelKeyDeletion(CancelKeyDeletionRequest request) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ConnectCustomKeyStoreResult connectCustomKeyStore(ConnectCustomKeyStoreRequest request) {
        throw new UnsupportedOperationException();
    }

    @Override
    public CreateAliasResult createAlias(CreateAliasRequest request) {
        throw new UnsupportedOperationException();
    }

    @Override
    public CreateCustomKeyStoreResult createCustomKeyStore(CreateCustomKeyStoreRequest request) {
        throw new UnsupportedOperationException();
    }

    @Override
    public CreateGrantResult createGrant(CreateGrantRequest request) {
        throw new UnsupportedOperationException();
    }

    @Override
    public CreateKeyResult createKey(CreateKeyRequest request) {
        throw new UnsupportedOperationException();
    }

    @Override
    public CreateKeyResult createKey() {
        return this.createKey(new CreateKeyRequest());
    }

    @Override
    public DecryptResult decrypt(DecryptRequest request) {
        throw new UnsupportedOperationException();
    }

    @Override
    public DeleteAliasResult deleteAlias(DeleteAliasRequest request) {
        throw new UnsupportedOperationException();
    }

    @Override
    public DeleteCustomKeyStoreResult deleteCustomKeyStore(DeleteCustomKeyStoreRequest request) {
        throw new UnsupportedOperationException();
    }

    @Override
    public DeleteImportedKeyMaterialResult deleteImportedKeyMaterial(DeleteImportedKeyMaterialRequest request) {
        throw new UnsupportedOperationException();
    }

    @Override
    public DescribeCustomKeyStoresResult describeCustomKeyStores(DescribeCustomKeyStoresRequest request) {
        throw new UnsupportedOperationException();
    }

    @Override
    public DescribeKeyResult describeKey(DescribeKeyRequest request) {
        throw new UnsupportedOperationException();
    }

    @Override
    public DisableKeyResult disableKey(DisableKeyRequest request) {
        throw new UnsupportedOperationException();
    }

    @Override
    public DisableKeyRotationResult disableKeyRotation(DisableKeyRotationRequest request) {
        throw new UnsupportedOperationException();
    }

    @Override
    public DisconnectCustomKeyStoreResult disconnectCustomKeyStore(DisconnectCustomKeyStoreRequest request) {
        throw new UnsupportedOperationException();
    }

    @Override
    public EnableKeyResult enableKey(EnableKeyRequest request) {
        throw new UnsupportedOperationException();
    }

    @Override
    public EnableKeyRotationResult enableKeyRotation(EnableKeyRotationRequest request) {
        throw new UnsupportedOperationException();
    }

    @Override
    public EncryptResult encrypt(EncryptRequest request) {
        throw new UnsupportedOperationException();
    }

    @Override
    public GenerateDataKeyResult generateDataKey(GenerateDataKeyRequest request) {
        throw new UnsupportedOperationException();
    }

    @Override
    public GenerateDataKeyPairResult generateDataKeyPair(GenerateDataKeyPairRequest request) {
        throw new UnsupportedOperationException();
    }

    @Override
    public GenerateDataKeyPairWithoutPlaintextResult generateDataKeyPairWithoutPlaintext(GenerateDataKeyPairWithoutPlaintextRequest request) {
        throw new UnsupportedOperationException();
    }

    @Override
    public GenerateDataKeyWithoutPlaintextResult generateDataKeyWithoutPlaintext(GenerateDataKeyWithoutPlaintextRequest request) {
        throw new UnsupportedOperationException();
    }

    @Override
    public GenerateMacResult generateMac(GenerateMacRequest request) {
        throw new UnsupportedOperationException();
    }

    @Override
    public GenerateRandomResult generateRandom(GenerateRandomRequest request) {
        throw new UnsupportedOperationException();
    }

    @Override
    public GenerateRandomResult generateRandom() {
        return this.generateRandom(new GenerateRandomRequest());
    }

    @Override
    public GetKeyPolicyResult getKeyPolicy(GetKeyPolicyRequest request) {
        throw new UnsupportedOperationException();
    }

    @Override
    public GetKeyRotationStatusResult getKeyRotationStatus(GetKeyRotationStatusRequest request) {
        throw new UnsupportedOperationException();
    }

    @Override
    public GetParametersForImportResult getParametersForImport(GetParametersForImportRequest request) {
        throw new UnsupportedOperationException();
    }

    @Override
    public GetPublicKeyResult getPublicKey(GetPublicKeyRequest request) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ImportKeyMaterialResult importKeyMaterial(ImportKeyMaterialRequest request) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ListAliasesResult listAliases(ListAliasesRequest request) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ListAliasesResult listAliases() {
        return this.listAliases(new ListAliasesRequest());
    }

    @Override
    public ListGrantsResult listGrants(ListGrantsRequest request) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ListKeyPoliciesResult listKeyPolicies(ListKeyPoliciesRequest request) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ListKeysResult listKeys(ListKeysRequest request) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ListKeysResult listKeys() {
        return this.listKeys(new ListKeysRequest());
    }

    @Override
    public ListResourceTagsResult listResourceTags(ListResourceTagsRequest request) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ListRetirableGrantsResult listRetirableGrants(ListRetirableGrantsRequest request) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PutKeyPolicyResult putKeyPolicy(PutKeyPolicyRequest request) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ReEncryptResult reEncrypt(ReEncryptRequest request) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ReplicateKeyResult replicateKey(ReplicateKeyRequest request) {
        throw new UnsupportedOperationException();
    }

    @Override
    public RetireGrantResult retireGrant(RetireGrantRequest request) {
        throw new UnsupportedOperationException();
    }

    @Override
    public RetireGrantResult retireGrant() {
        return this.retireGrant(new RetireGrantRequest());
    }

    @Override
    public RevokeGrantResult revokeGrant(RevokeGrantRequest request) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ScheduleKeyDeletionResult scheduleKeyDeletion(ScheduleKeyDeletionRequest request) {
        throw new UnsupportedOperationException();
    }

    @Override
    public SignResult sign(SignRequest request) {
        throw new UnsupportedOperationException();
    }

    @Override
    public TagResourceResult tagResource(TagResourceRequest request) {
        throw new UnsupportedOperationException();
    }

    @Override
    public UntagResourceResult untagResource(UntagResourceRequest request) {
        throw new UnsupportedOperationException();
    }

    @Override
    public UpdateAliasResult updateAlias(UpdateAliasRequest request) {
        throw new UnsupportedOperationException();
    }

    @Override
    public UpdateCustomKeyStoreResult updateCustomKeyStore(UpdateCustomKeyStoreRequest request) {
        throw new UnsupportedOperationException();
    }

    @Override
    public UpdateKeyDescriptionResult updateKeyDescription(UpdateKeyDescriptionRequest request) {
        throw new UnsupportedOperationException();
    }

    @Override
    public UpdatePrimaryRegionResult updatePrimaryRegion(UpdatePrimaryRegionRequest request) {
        throw new UnsupportedOperationException();
    }

    @Override
    public VerifyResult verify(VerifyRequest request) {
        throw new UnsupportedOperationException();
    }

    @Override
    public VerifyMacResult verifyMac(VerifyMacRequest request) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void shutdown() {
        throw new UnsupportedOperationException();
    }

    @Override
    public ResponseMetadata getCachedResponseMetadata(AmazonWebServiceRequest request) {
        throw new UnsupportedOperationException();
    }
}

