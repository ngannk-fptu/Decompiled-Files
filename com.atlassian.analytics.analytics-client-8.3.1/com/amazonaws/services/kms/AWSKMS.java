/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.kms;

import com.amazonaws.AmazonWebServiceRequest;
import com.amazonaws.ResponseMetadata;
import com.amazonaws.regions.Region;
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

public interface AWSKMS {
    public static final String ENDPOINT_PREFIX = "kms";

    @Deprecated
    public void setEndpoint(String var1);

    @Deprecated
    public void setRegion(Region var1);

    public CancelKeyDeletionResult cancelKeyDeletion(CancelKeyDeletionRequest var1);

    public ConnectCustomKeyStoreResult connectCustomKeyStore(ConnectCustomKeyStoreRequest var1);

    public CreateAliasResult createAlias(CreateAliasRequest var1);

    public CreateCustomKeyStoreResult createCustomKeyStore(CreateCustomKeyStoreRequest var1);

    public CreateGrantResult createGrant(CreateGrantRequest var1);

    public CreateKeyResult createKey(CreateKeyRequest var1);

    public CreateKeyResult createKey();

    public DecryptResult decrypt(DecryptRequest var1);

    public DeleteAliasResult deleteAlias(DeleteAliasRequest var1);

    public DeleteCustomKeyStoreResult deleteCustomKeyStore(DeleteCustomKeyStoreRequest var1);

    public DeleteImportedKeyMaterialResult deleteImportedKeyMaterial(DeleteImportedKeyMaterialRequest var1);

    public DescribeCustomKeyStoresResult describeCustomKeyStores(DescribeCustomKeyStoresRequest var1);

    public DescribeKeyResult describeKey(DescribeKeyRequest var1);

    public DisableKeyResult disableKey(DisableKeyRequest var1);

    public DisableKeyRotationResult disableKeyRotation(DisableKeyRotationRequest var1);

    public DisconnectCustomKeyStoreResult disconnectCustomKeyStore(DisconnectCustomKeyStoreRequest var1);

    public EnableKeyResult enableKey(EnableKeyRequest var1);

    public EnableKeyRotationResult enableKeyRotation(EnableKeyRotationRequest var1);

    public EncryptResult encrypt(EncryptRequest var1);

    public GenerateDataKeyResult generateDataKey(GenerateDataKeyRequest var1);

    public GenerateDataKeyPairResult generateDataKeyPair(GenerateDataKeyPairRequest var1);

    public GenerateDataKeyPairWithoutPlaintextResult generateDataKeyPairWithoutPlaintext(GenerateDataKeyPairWithoutPlaintextRequest var1);

    public GenerateDataKeyWithoutPlaintextResult generateDataKeyWithoutPlaintext(GenerateDataKeyWithoutPlaintextRequest var1);

    public GenerateMacResult generateMac(GenerateMacRequest var1);

    public GenerateRandomResult generateRandom(GenerateRandomRequest var1);

    public GenerateRandomResult generateRandom();

    public GetKeyPolicyResult getKeyPolicy(GetKeyPolicyRequest var1);

    public GetKeyRotationStatusResult getKeyRotationStatus(GetKeyRotationStatusRequest var1);

    public GetParametersForImportResult getParametersForImport(GetParametersForImportRequest var1);

    public GetPublicKeyResult getPublicKey(GetPublicKeyRequest var1);

    public ImportKeyMaterialResult importKeyMaterial(ImportKeyMaterialRequest var1);

    public ListAliasesResult listAliases(ListAliasesRequest var1);

    public ListAliasesResult listAliases();

    public ListGrantsResult listGrants(ListGrantsRequest var1);

    public ListKeyPoliciesResult listKeyPolicies(ListKeyPoliciesRequest var1);

    public ListKeysResult listKeys(ListKeysRequest var1);

    public ListKeysResult listKeys();

    public ListResourceTagsResult listResourceTags(ListResourceTagsRequest var1);

    public ListRetirableGrantsResult listRetirableGrants(ListRetirableGrantsRequest var1);

    public PutKeyPolicyResult putKeyPolicy(PutKeyPolicyRequest var1);

    public ReEncryptResult reEncrypt(ReEncryptRequest var1);

    public ReplicateKeyResult replicateKey(ReplicateKeyRequest var1);

    public RetireGrantResult retireGrant(RetireGrantRequest var1);

    public RetireGrantResult retireGrant();

    public RevokeGrantResult revokeGrant(RevokeGrantRequest var1);

    public ScheduleKeyDeletionResult scheduleKeyDeletion(ScheduleKeyDeletionRequest var1);

    public SignResult sign(SignRequest var1);

    public TagResourceResult tagResource(TagResourceRequest var1);

    public UntagResourceResult untagResource(UntagResourceRequest var1);

    public UpdateAliasResult updateAlias(UpdateAliasRequest var1);

    public UpdateCustomKeyStoreResult updateCustomKeyStore(UpdateCustomKeyStoreRequest var1);

    public UpdateKeyDescriptionResult updateKeyDescription(UpdateKeyDescriptionRequest var1);

    public UpdatePrimaryRegionResult updatePrimaryRegion(UpdatePrimaryRegionRequest var1);

    public VerifyResult verify(VerifyRequest var1);

    public VerifyMacResult verifyMac(VerifyMacRequest var1);

    public void shutdown();

    public ResponseMetadata getCachedResponseMetadata(AmazonWebServiceRequest var1);
}

