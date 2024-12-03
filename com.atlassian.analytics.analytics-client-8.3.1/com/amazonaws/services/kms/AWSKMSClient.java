/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package com.amazonaws.services.kms;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.AmazonWebServiceClient;
import com.amazonaws.AmazonWebServiceRequest;
import com.amazonaws.AmazonWebServiceResponse;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.ClientConfigurationFactory;
import com.amazonaws.Request;
import com.amazonaws.Response;
import com.amazonaws.ResponseMetadata;
import com.amazonaws.annotation.SdkInternalApi;
import com.amazonaws.annotation.ThreadSafe;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.client.AwsSyncClientParams;
import com.amazonaws.client.builder.AdvancedConfig;
import com.amazonaws.handlers.HandlerChainFactory;
import com.amazonaws.handlers.HandlerContextKey;
import com.amazonaws.http.ExecutionContext;
import com.amazonaws.http.HttpResponseHandler;
import com.amazonaws.internal.StaticCredentialsProvider;
import com.amazonaws.metrics.RequestMetricCollector;
import com.amazonaws.protocol.json.JsonClientMetadata;
import com.amazonaws.protocol.json.JsonErrorResponseMetadata;
import com.amazonaws.protocol.json.JsonErrorShapeMetadata;
import com.amazonaws.protocol.json.JsonOperationMetadata;
import com.amazonaws.protocol.json.SdkJsonProtocolFactory;
import com.amazonaws.services.kms.AWSKMS;
import com.amazonaws.services.kms.AWSKMSClientBuilder;
import com.amazonaws.services.kms.model.AWSKMSException;
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
import com.amazonaws.services.kms.model.transform.AlreadyExistsExceptionUnmarshaller;
import com.amazonaws.services.kms.model.transform.CancelKeyDeletionRequestProtocolMarshaller;
import com.amazonaws.services.kms.model.transform.CancelKeyDeletionResultJsonUnmarshaller;
import com.amazonaws.services.kms.model.transform.CloudHsmClusterInUseExceptionUnmarshaller;
import com.amazonaws.services.kms.model.transform.CloudHsmClusterInvalidConfigurationExceptionUnmarshaller;
import com.amazonaws.services.kms.model.transform.CloudHsmClusterNotActiveExceptionUnmarshaller;
import com.amazonaws.services.kms.model.transform.CloudHsmClusterNotFoundExceptionUnmarshaller;
import com.amazonaws.services.kms.model.transform.CloudHsmClusterNotRelatedExceptionUnmarshaller;
import com.amazonaws.services.kms.model.transform.ConnectCustomKeyStoreRequestProtocolMarshaller;
import com.amazonaws.services.kms.model.transform.ConnectCustomKeyStoreResultJsonUnmarshaller;
import com.amazonaws.services.kms.model.transform.CreateAliasRequestProtocolMarshaller;
import com.amazonaws.services.kms.model.transform.CreateAliasResultJsonUnmarshaller;
import com.amazonaws.services.kms.model.transform.CreateCustomKeyStoreRequestProtocolMarshaller;
import com.amazonaws.services.kms.model.transform.CreateCustomKeyStoreResultJsonUnmarshaller;
import com.amazonaws.services.kms.model.transform.CreateGrantRequestProtocolMarshaller;
import com.amazonaws.services.kms.model.transform.CreateGrantResultJsonUnmarshaller;
import com.amazonaws.services.kms.model.transform.CreateKeyRequestProtocolMarshaller;
import com.amazonaws.services.kms.model.transform.CreateKeyResultJsonUnmarshaller;
import com.amazonaws.services.kms.model.transform.CustomKeyStoreHasCMKsExceptionUnmarshaller;
import com.amazonaws.services.kms.model.transform.CustomKeyStoreInvalidStateExceptionUnmarshaller;
import com.amazonaws.services.kms.model.transform.CustomKeyStoreNameInUseExceptionUnmarshaller;
import com.amazonaws.services.kms.model.transform.CustomKeyStoreNotFoundExceptionUnmarshaller;
import com.amazonaws.services.kms.model.transform.DecryptRequestProtocolMarshaller;
import com.amazonaws.services.kms.model.transform.DecryptResultJsonUnmarshaller;
import com.amazonaws.services.kms.model.transform.DeleteAliasRequestProtocolMarshaller;
import com.amazonaws.services.kms.model.transform.DeleteAliasResultJsonUnmarshaller;
import com.amazonaws.services.kms.model.transform.DeleteCustomKeyStoreRequestProtocolMarshaller;
import com.amazonaws.services.kms.model.transform.DeleteCustomKeyStoreResultJsonUnmarshaller;
import com.amazonaws.services.kms.model.transform.DeleteImportedKeyMaterialRequestProtocolMarshaller;
import com.amazonaws.services.kms.model.transform.DeleteImportedKeyMaterialResultJsonUnmarshaller;
import com.amazonaws.services.kms.model.transform.DependencyTimeoutExceptionUnmarshaller;
import com.amazonaws.services.kms.model.transform.DescribeCustomKeyStoresRequestProtocolMarshaller;
import com.amazonaws.services.kms.model.transform.DescribeCustomKeyStoresResultJsonUnmarshaller;
import com.amazonaws.services.kms.model.transform.DescribeKeyRequestProtocolMarshaller;
import com.amazonaws.services.kms.model.transform.DescribeKeyResultJsonUnmarshaller;
import com.amazonaws.services.kms.model.transform.DisableKeyRequestProtocolMarshaller;
import com.amazonaws.services.kms.model.transform.DisableKeyResultJsonUnmarshaller;
import com.amazonaws.services.kms.model.transform.DisableKeyRotationRequestProtocolMarshaller;
import com.amazonaws.services.kms.model.transform.DisableKeyRotationResultJsonUnmarshaller;
import com.amazonaws.services.kms.model.transform.DisabledExceptionUnmarshaller;
import com.amazonaws.services.kms.model.transform.DisconnectCustomKeyStoreRequestProtocolMarshaller;
import com.amazonaws.services.kms.model.transform.DisconnectCustomKeyStoreResultJsonUnmarshaller;
import com.amazonaws.services.kms.model.transform.EnableKeyRequestProtocolMarshaller;
import com.amazonaws.services.kms.model.transform.EnableKeyResultJsonUnmarshaller;
import com.amazonaws.services.kms.model.transform.EnableKeyRotationRequestProtocolMarshaller;
import com.amazonaws.services.kms.model.transform.EnableKeyRotationResultJsonUnmarshaller;
import com.amazonaws.services.kms.model.transform.EncryptRequestProtocolMarshaller;
import com.amazonaws.services.kms.model.transform.EncryptResultJsonUnmarshaller;
import com.amazonaws.services.kms.model.transform.ExpiredImportTokenExceptionUnmarshaller;
import com.amazonaws.services.kms.model.transform.GenerateDataKeyPairRequestProtocolMarshaller;
import com.amazonaws.services.kms.model.transform.GenerateDataKeyPairResultJsonUnmarshaller;
import com.amazonaws.services.kms.model.transform.GenerateDataKeyPairWithoutPlaintextRequestProtocolMarshaller;
import com.amazonaws.services.kms.model.transform.GenerateDataKeyPairWithoutPlaintextResultJsonUnmarshaller;
import com.amazonaws.services.kms.model.transform.GenerateDataKeyRequestProtocolMarshaller;
import com.amazonaws.services.kms.model.transform.GenerateDataKeyResultJsonUnmarshaller;
import com.amazonaws.services.kms.model.transform.GenerateDataKeyWithoutPlaintextRequestProtocolMarshaller;
import com.amazonaws.services.kms.model.transform.GenerateDataKeyWithoutPlaintextResultJsonUnmarshaller;
import com.amazonaws.services.kms.model.transform.GenerateMacRequestProtocolMarshaller;
import com.amazonaws.services.kms.model.transform.GenerateMacResultJsonUnmarshaller;
import com.amazonaws.services.kms.model.transform.GenerateRandomRequestProtocolMarshaller;
import com.amazonaws.services.kms.model.transform.GenerateRandomResultJsonUnmarshaller;
import com.amazonaws.services.kms.model.transform.GetKeyPolicyRequestProtocolMarshaller;
import com.amazonaws.services.kms.model.transform.GetKeyPolicyResultJsonUnmarshaller;
import com.amazonaws.services.kms.model.transform.GetKeyRotationStatusRequestProtocolMarshaller;
import com.amazonaws.services.kms.model.transform.GetKeyRotationStatusResultJsonUnmarshaller;
import com.amazonaws.services.kms.model.transform.GetParametersForImportRequestProtocolMarshaller;
import com.amazonaws.services.kms.model.transform.GetParametersForImportResultJsonUnmarshaller;
import com.amazonaws.services.kms.model.transform.GetPublicKeyRequestProtocolMarshaller;
import com.amazonaws.services.kms.model.transform.GetPublicKeyResultJsonUnmarshaller;
import com.amazonaws.services.kms.model.transform.ImportKeyMaterialRequestProtocolMarshaller;
import com.amazonaws.services.kms.model.transform.ImportKeyMaterialResultJsonUnmarshaller;
import com.amazonaws.services.kms.model.transform.IncorrectKeyExceptionUnmarshaller;
import com.amazonaws.services.kms.model.transform.IncorrectKeyMaterialExceptionUnmarshaller;
import com.amazonaws.services.kms.model.transform.IncorrectTrustAnchorExceptionUnmarshaller;
import com.amazonaws.services.kms.model.transform.InvalidAliasNameExceptionUnmarshaller;
import com.amazonaws.services.kms.model.transform.InvalidArnExceptionUnmarshaller;
import com.amazonaws.services.kms.model.transform.InvalidCiphertextExceptionUnmarshaller;
import com.amazonaws.services.kms.model.transform.InvalidGrantIdExceptionUnmarshaller;
import com.amazonaws.services.kms.model.transform.InvalidGrantTokenExceptionUnmarshaller;
import com.amazonaws.services.kms.model.transform.InvalidImportTokenExceptionUnmarshaller;
import com.amazonaws.services.kms.model.transform.InvalidKeyUsageExceptionUnmarshaller;
import com.amazonaws.services.kms.model.transform.InvalidMarkerExceptionUnmarshaller;
import com.amazonaws.services.kms.model.transform.KMSInternalExceptionUnmarshaller;
import com.amazonaws.services.kms.model.transform.KMSInvalidMacExceptionUnmarshaller;
import com.amazonaws.services.kms.model.transform.KMSInvalidSignatureExceptionUnmarshaller;
import com.amazonaws.services.kms.model.transform.KMSInvalidStateExceptionUnmarshaller;
import com.amazonaws.services.kms.model.transform.KeyUnavailableExceptionUnmarshaller;
import com.amazonaws.services.kms.model.transform.LimitExceededExceptionUnmarshaller;
import com.amazonaws.services.kms.model.transform.ListAliasesRequestProtocolMarshaller;
import com.amazonaws.services.kms.model.transform.ListAliasesResultJsonUnmarshaller;
import com.amazonaws.services.kms.model.transform.ListGrantsRequestProtocolMarshaller;
import com.amazonaws.services.kms.model.transform.ListGrantsResultJsonUnmarshaller;
import com.amazonaws.services.kms.model.transform.ListKeyPoliciesRequestProtocolMarshaller;
import com.amazonaws.services.kms.model.transform.ListKeyPoliciesResultJsonUnmarshaller;
import com.amazonaws.services.kms.model.transform.ListKeysRequestProtocolMarshaller;
import com.amazonaws.services.kms.model.transform.ListKeysResultJsonUnmarshaller;
import com.amazonaws.services.kms.model.transform.ListResourceTagsRequestProtocolMarshaller;
import com.amazonaws.services.kms.model.transform.ListResourceTagsResultJsonUnmarshaller;
import com.amazonaws.services.kms.model.transform.ListRetirableGrantsRequestProtocolMarshaller;
import com.amazonaws.services.kms.model.transform.ListRetirableGrantsResultJsonUnmarshaller;
import com.amazonaws.services.kms.model.transform.MalformedPolicyDocumentExceptionUnmarshaller;
import com.amazonaws.services.kms.model.transform.NotFoundExceptionUnmarshaller;
import com.amazonaws.services.kms.model.transform.PutKeyPolicyRequestProtocolMarshaller;
import com.amazonaws.services.kms.model.transform.PutKeyPolicyResultJsonUnmarshaller;
import com.amazonaws.services.kms.model.transform.ReEncryptRequestProtocolMarshaller;
import com.amazonaws.services.kms.model.transform.ReEncryptResultJsonUnmarshaller;
import com.amazonaws.services.kms.model.transform.ReplicateKeyRequestProtocolMarshaller;
import com.amazonaws.services.kms.model.transform.ReplicateKeyResultJsonUnmarshaller;
import com.amazonaws.services.kms.model.transform.RetireGrantRequestProtocolMarshaller;
import com.amazonaws.services.kms.model.transform.RetireGrantResultJsonUnmarshaller;
import com.amazonaws.services.kms.model.transform.RevokeGrantRequestProtocolMarshaller;
import com.amazonaws.services.kms.model.transform.RevokeGrantResultJsonUnmarshaller;
import com.amazonaws.services.kms.model.transform.ScheduleKeyDeletionRequestProtocolMarshaller;
import com.amazonaws.services.kms.model.transform.ScheduleKeyDeletionResultJsonUnmarshaller;
import com.amazonaws.services.kms.model.transform.SignRequestProtocolMarshaller;
import com.amazonaws.services.kms.model.transform.SignResultJsonUnmarshaller;
import com.amazonaws.services.kms.model.transform.TagExceptionUnmarshaller;
import com.amazonaws.services.kms.model.transform.TagResourceRequestProtocolMarshaller;
import com.amazonaws.services.kms.model.transform.TagResourceResultJsonUnmarshaller;
import com.amazonaws.services.kms.model.transform.UnsupportedOperationExceptionUnmarshaller;
import com.amazonaws.services.kms.model.transform.UntagResourceRequestProtocolMarshaller;
import com.amazonaws.services.kms.model.transform.UntagResourceResultJsonUnmarshaller;
import com.amazonaws.services.kms.model.transform.UpdateAliasRequestProtocolMarshaller;
import com.amazonaws.services.kms.model.transform.UpdateAliasResultJsonUnmarshaller;
import com.amazonaws.services.kms.model.transform.UpdateCustomKeyStoreRequestProtocolMarshaller;
import com.amazonaws.services.kms.model.transform.UpdateCustomKeyStoreResultJsonUnmarshaller;
import com.amazonaws.services.kms.model.transform.UpdateKeyDescriptionRequestProtocolMarshaller;
import com.amazonaws.services.kms.model.transform.UpdateKeyDescriptionResultJsonUnmarshaller;
import com.amazonaws.services.kms.model.transform.UpdatePrimaryRegionRequestProtocolMarshaller;
import com.amazonaws.services.kms.model.transform.UpdatePrimaryRegionResultJsonUnmarshaller;
import com.amazonaws.services.kms.model.transform.VerifyMacRequestProtocolMarshaller;
import com.amazonaws.services.kms.model.transform.VerifyMacResultJsonUnmarshaller;
import com.amazonaws.services.kms.model.transform.VerifyRequestProtocolMarshaller;
import com.amazonaws.services.kms.model.transform.VerifyResultJsonUnmarshaller;
import com.amazonaws.util.AWSRequestMetrics;
import com.amazonaws.util.CredentialUtils;
import java.net.URI;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@ThreadSafe
public class AWSKMSClient
extends AmazonWebServiceClient
implements AWSKMS {
    private final AWSCredentialsProvider awsCredentialsProvider;
    private static final Log log = LogFactory.getLog(AWSKMS.class);
    private static final String DEFAULT_SIGNING_NAME = "kms";
    protected static final ClientConfigurationFactory configFactory = new ClientConfigurationFactory();
    private final AdvancedConfig advancedConfig;
    private static final SdkJsonProtocolFactory protocolFactory = new SdkJsonProtocolFactory(new JsonClientMetadata().withProtocolVersion("1.1").withSupportsCbor(false).withSupportsIon(false).addErrorMetadata(new JsonErrorShapeMetadata().withErrorCode("CloudHsmClusterNotFoundException").withExceptionUnmarshaller(CloudHsmClusterNotFoundExceptionUnmarshaller.getInstance())).addErrorMetadata(new JsonErrorShapeMetadata().withErrorCode("ExpiredImportTokenException").withExceptionUnmarshaller(ExpiredImportTokenExceptionUnmarshaller.getInstance())).addErrorMetadata(new JsonErrorShapeMetadata().withErrorCode("CustomKeyStoreNotFoundException").withExceptionUnmarshaller(CustomKeyStoreNotFoundExceptionUnmarshaller.getInstance())).addErrorMetadata(new JsonErrorShapeMetadata().withErrorCode("KMSInvalidMacException").withExceptionUnmarshaller(KMSInvalidMacExceptionUnmarshaller.getInstance())).addErrorMetadata(new JsonErrorShapeMetadata().withErrorCode("MalformedPolicyDocumentException").withExceptionUnmarshaller(MalformedPolicyDocumentExceptionUnmarshaller.getInstance())).addErrorMetadata(new JsonErrorShapeMetadata().withErrorCode("IncorrectKeyMaterialException").withExceptionUnmarshaller(IncorrectKeyMaterialExceptionUnmarshaller.getInstance())).addErrorMetadata(new JsonErrorShapeMetadata().withErrorCode("InvalidImportTokenException").withExceptionUnmarshaller(InvalidImportTokenExceptionUnmarshaller.getInstance())).addErrorMetadata(new JsonErrorShapeMetadata().withErrorCode("InvalidArnException").withExceptionUnmarshaller(InvalidArnExceptionUnmarshaller.getInstance())).addErrorMetadata(new JsonErrorShapeMetadata().withErrorCode("KMSInvalidSignatureException").withExceptionUnmarshaller(KMSInvalidSignatureExceptionUnmarshaller.getInstance())).addErrorMetadata(new JsonErrorShapeMetadata().withErrorCode("KMSInvalidStateException").withExceptionUnmarshaller(KMSInvalidStateExceptionUnmarshaller.getInstance())).addErrorMetadata(new JsonErrorShapeMetadata().withErrorCode("CloudHsmClusterNotRelatedException").withExceptionUnmarshaller(CloudHsmClusterNotRelatedExceptionUnmarshaller.getInstance())).addErrorMetadata(new JsonErrorShapeMetadata().withErrorCode("CustomKeyStoreInvalidStateException").withExceptionUnmarshaller(CustomKeyStoreInvalidStateExceptionUnmarshaller.getInstance())).addErrorMetadata(new JsonErrorShapeMetadata().withErrorCode("IncorrectTrustAnchorException").withExceptionUnmarshaller(IncorrectTrustAnchorExceptionUnmarshaller.getInstance())).addErrorMetadata(new JsonErrorShapeMetadata().withErrorCode("DisabledException").withExceptionUnmarshaller(DisabledExceptionUnmarshaller.getInstance())).addErrorMetadata(new JsonErrorShapeMetadata().withErrorCode("NotFoundException").withExceptionUnmarshaller(NotFoundExceptionUnmarshaller.getInstance())).addErrorMetadata(new JsonErrorShapeMetadata().withErrorCode("CustomKeyStoreHasCMKsException").withExceptionUnmarshaller(CustomKeyStoreHasCMKsExceptionUnmarshaller.getInstance())).addErrorMetadata(new JsonErrorShapeMetadata().withErrorCode("KeyUnavailableException").withExceptionUnmarshaller(KeyUnavailableExceptionUnmarshaller.getInstance())).addErrorMetadata(new JsonErrorShapeMetadata().withErrorCode("LimitExceededException").withExceptionUnmarshaller(LimitExceededExceptionUnmarshaller.getInstance())).addErrorMetadata(new JsonErrorShapeMetadata().withErrorCode("CloudHsmClusterInUseException").withExceptionUnmarshaller(CloudHsmClusterInUseExceptionUnmarshaller.getInstance())).addErrorMetadata(new JsonErrorShapeMetadata().withErrorCode("InvalidCiphertextException").withExceptionUnmarshaller(InvalidCiphertextExceptionUnmarshaller.getInstance())).addErrorMetadata(new JsonErrorShapeMetadata().withErrorCode("InvalidGrantIdException").withExceptionUnmarshaller(InvalidGrantIdExceptionUnmarshaller.getInstance())).addErrorMetadata(new JsonErrorShapeMetadata().withErrorCode("IncorrectKeyException").withExceptionUnmarshaller(IncorrectKeyExceptionUnmarshaller.getInstance())).addErrorMetadata(new JsonErrorShapeMetadata().withErrorCode("InvalidGrantTokenException").withExceptionUnmarshaller(InvalidGrantTokenExceptionUnmarshaller.getInstance())).addErrorMetadata(new JsonErrorShapeMetadata().withErrorCode("UnsupportedOperationException").withExceptionUnmarshaller(UnsupportedOperationExceptionUnmarshaller.getInstance())).addErrorMetadata(new JsonErrorShapeMetadata().withErrorCode("CustomKeyStoreNameInUseException").withExceptionUnmarshaller(CustomKeyStoreNameInUseExceptionUnmarshaller.getInstance())).addErrorMetadata(new JsonErrorShapeMetadata().withErrorCode("AlreadyExistsException").withExceptionUnmarshaller(AlreadyExistsExceptionUnmarshaller.getInstance())).addErrorMetadata(new JsonErrorShapeMetadata().withErrorCode("TagException").withExceptionUnmarshaller(TagExceptionUnmarshaller.getInstance())).addErrorMetadata(new JsonErrorShapeMetadata().withErrorCode("InvalidKeyUsageException").withExceptionUnmarshaller(InvalidKeyUsageExceptionUnmarshaller.getInstance())).addErrorMetadata(new JsonErrorShapeMetadata().withErrorCode("CloudHsmClusterInvalidConfigurationException").withExceptionUnmarshaller(CloudHsmClusterInvalidConfigurationExceptionUnmarshaller.getInstance())).addErrorMetadata(new JsonErrorShapeMetadata().withErrorCode("InvalidMarkerException").withExceptionUnmarshaller(InvalidMarkerExceptionUnmarshaller.getInstance())).addErrorMetadata(new JsonErrorShapeMetadata().withErrorCode("InvalidAliasNameException").withExceptionUnmarshaller(InvalidAliasNameExceptionUnmarshaller.getInstance())).addErrorMetadata(new JsonErrorShapeMetadata().withErrorCode("DependencyTimeoutException").withExceptionUnmarshaller(DependencyTimeoutExceptionUnmarshaller.getInstance())).addErrorMetadata(new JsonErrorShapeMetadata().withErrorCode("KMSInternalException").withExceptionUnmarshaller(KMSInternalExceptionUnmarshaller.getInstance())).addErrorMetadata(new JsonErrorShapeMetadata().withErrorCode("CloudHsmClusterNotActiveException").withExceptionUnmarshaller(CloudHsmClusterNotActiveExceptionUnmarshaller.getInstance())).withBaseServiceExceptionClass(AWSKMSException.class));

    @Deprecated
    public AWSKMSClient() {
        this(DefaultAWSCredentialsProviderChain.getInstance(), configFactory.getConfig());
    }

    @Deprecated
    public AWSKMSClient(ClientConfiguration clientConfiguration) {
        this(DefaultAWSCredentialsProviderChain.getInstance(), clientConfiguration);
    }

    @Deprecated
    public AWSKMSClient(AWSCredentials awsCredentials) {
        this(awsCredentials, configFactory.getConfig());
    }

    @Deprecated
    public AWSKMSClient(AWSCredentials awsCredentials, ClientConfiguration clientConfiguration) {
        super(clientConfiguration);
        this.awsCredentialsProvider = new StaticCredentialsProvider(awsCredentials);
        this.advancedConfig = AdvancedConfig.EMPTY;
        this.init();
    }

    @Deprecated
    public AWSKMSClient(AWSCredentialsProvider awsCredentialsProvider) {
        this(awsCredentialsProvider, configFactory.getConfig());
    }

    @Deprecated
    public AWSKMSClient(AWSCredentialsProvider awsCredentialsProvider, ClientConfiguration clientConfiguration) {
        this(awsCredentialsProvider, clientConfiguration, null);
    }

    @Deprecated
    public AWSKMSClient(AWSCredentialsProvider awsCredentialsProvider, ClientConfiguration clientConfiguration, RequestMetricCollector requestMetricCollector) {
        super(clientConfiguration, requestMetricCollector);
        this.awsCredentialsProvider = awsCredentialsProvider;
        this.advancedConfig = AdvancedConfig.EMPTY;
        this.init();
    }

    public static AWSKMSClientBuilder builder() {
        return AWSKMSClientBuilder.standard();
    }

    AWSKMSClient(AwsSyncClientParams clientParams) {
        this(clientParams, false);
    }

    AWSKMSClient(AwsSyncClientParams clientParams, boolean endpointDiscoveryEnabled) {
        super(clientParams);
        this.awsCredentialsProvider = clientParams.getCredentialsProvider();
        this.advancedConfig = clientParams.getAdvancedConfig();
        this.init();
    }

    private void init() {
        this.setServiceNameIntern(DEFAULT_SIGNING_NAME);
        this.setEndpointPrefix(DEFAULT_SIGNING_NAME);
        this.setEndpoint("https://kms.us-east-1.amazonaws.com/");
        HandlerChainFactory chainFactory = new HandlerChainFactory();
        this.requestHandler2s.addAll(chainFactory.newRequestHandlerChain("/com/amazonaws/services/kms/request.handlers"));
        this.requestHandler2s.addAll(chainFactory.newRequestHandler2Chain("/com/amazonaws/services/kms/request.handler2s"));
        this.requestHandler2s.addAll(chainFactory.getGlobalHandlers());
    }

    @Override
    public CancelKeyDeletionResult cancelKeyDeletion(CancelKeyDeletionRequest request) {
        request = this.beforeClientExecution(request);
        return this.executeCancelKeyDeletion(request);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @SdkInternalApi
    final CancelKeyDeletionResult executeCancelKeyDeletion(CancelKeyDeletionRequest cancelKeyDeletionRequest) {
        ExecutionContext executionContext = this.createExecutionContext(cancelKeyDeletionRequest);
        AWSRequestMetrics awsRequestMetrics = executionContext.getAwsRequestMetrics();
        awsRequestMetrics.startEvent(AWSRequestMetrics.Field.ClientExecuteTime);
        Request<CancelKeyDeletionRequest> request = null;
        Response response = null;
        try {
            awsRequestMetrics.startEvent(AWSRequestMetrics.Field.RequestMarshallTime);
            try {
                request = new CancelKeyDeletionRequestProtocolMarshaller(protocolFactory).marshall(super.beforeMarshalling(cancelKeyDeletionRequest));
                request.setAWSRequestMetrics(awsRequestMetrics);
                request.addHandlerContext(HandlerContextKey.CLIENT_ENDPOINT, this.endpoint);
                request.addHandlerContext(HandlerContextKey.ENDPOINT_OVERRIDDEN, this.isEndpointOverridden());
                request.addHandlerContext(HandlerContextKey.SIGNING_REGION, this.getSigningRegion());
                request.addHandlerContext(HandlerContextKey.SERVICE_ID, "KMS");
                request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "CancelKeyDeletion");
                request.addHandlerContext(HandlerContextKey.ADVANCED_CONFIG, this.advancedConfig);
            }
            finally {
                awsRequestMetrics.endEvent(AWSRequestMetrics.Field.RequestMarshallTime);
            }
            HttpResponseHandler responseHandler = protocolFactory.createResponseHandler(new JsonOperationMetadata().withPayloadJson(true).withHasStreamingSuccessResponse(false), new CancelKeyDeletionResultJsonUnmarshaller());
            response = this.invoke(request, responseHandler, executionContext);
            CancelKeyDeletionResult cancelKeyDeletionResult = (CancelKeyDeletionResult)response.getAwsResponse();
            return cancelKeyDeletionResult;
        }
        finally {
            this.endClientExecution(awsRequestMetrics, request, response);
        }
    }

    @Override
    public ConnectCustomKeyStoreResult connectCustomKeyStore(ConnectCustomKeyStoreRequest request) {
        request = this.beforeClientExecution(request);
        return this.executeConnectCustomKeyStore(request);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @SdkInternalApi
    final ConnectCustomKeyStoreResult executeConnectCustomKeyStore(ConnectCustomKeyStoreRequest connectCustomKeyStoreRequest) {
        ExecutionContext executionContext = this.createExecutionContext(connectCustomKeyStoreRequest);
        AWSRequestMetrics awsRequestMetrics = executionContext.getAwsRequestMetrics();
        awsRequestMetrics.startEvent(AWSRequestMetrics.Field.ClientExecuteTime);
        Request<ConnectCustomKeyStoreRequest> request = null;
        Response response = null;
        try {
            awsRequestMetrics.startEvent(AWSRequestMetrics.Field.RequestMarshallTime);
            try {
                request = new ConnectCustomKeyStoreRequestProtocolMarshaller(protocolFactory).marshall(super.beforeMarshalling(connectCustomKeyStoreRequest));
                request.setAWSRequestMetrics(awsRequestMetrics);
                request.addHandlerContext(HandlerContextKey.CLIENT_ENDPOINT, this.endpoint);
                request.addHandlerContext(HandlerContextKey.ENDPOINT_OVERRIDDEN, this.isEndpointOverridden());
                request.addHandlerContext(HandlerContextKey.SIGNING_REGION, this.getSigningRegion());
                request.addHandlerContext(HandlerContextKey.SERVICE_ID, "KMS");
                request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "ConnectCustomKeyStore");
                request.addHandlerContext(HandlerContextKey.ADVANCED_CONFIG, this.advancedConfig);
            }
            finally {
                awsRequestMetrics.endEvent(AWSRequestMetrics.Field.RequestMarshallTime);
            }
            HttpResponseHandler responseHandler = protocolFactory.createResponseHandler(new JsonOperationMetadata().withPayloadJson(true).withHasStreamingSuccessResponse(false), new ConnectCustomKeyStoreResultJsonUnmarshaller());
            response = this.invoke(request, responseHandler, executionContext);
            ConnectCustomKeyStoreResult connectCustomKeyStoreResult = (ConnectCustomKeyStoreResult)response.getAwsResponse();
            return connectCustomKeyStoreResult;
        }
        finally {
            this.endClientExecution(awsRequestMetrics, request, response);
        }
    }

    @Override
    public CreateAliasResult createAlias(CreateAliasRequest request) {
        request = this.beforeClientExecution(request);
        return this.executeCreateAlias(request);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @SdkInternalApi
    final CreateAliasResult executeCreateAlias(CreateAliasRequest createAliasRequest) {
        ExecutionContext executionContext = this.createExecutionContext(createAliasRequest);
        AWSRequestMetrics awsRequestMetrics = executionContext.getAwsRequestMetrics();
        awsRequestMetrics.startEvent(AWSRequestMetrics.Field.ClientExecuteTime);
        Request<CreateAliasRequest> request = null;
        Response response = null;
        try {
            awsRequestMetrics.startEvent(AWSRequestMetrics.Field.RequestMarshallTime);
            try {
                request = new CreateAliasRequestProtocolMarshaller(protocolFactory).marshall(super.beforeMarshalling(createAliasRequest));
                request.setAWSRequestMetrics(awsRequestMetrics);
                request.addHandlerContext(HandlerContextKey.CLIENT_ENDPOINT, this.endpoint);
                request.addHandlerContext(HandlerContextKey.ENDPOINT_OVERRIDDEN, this.isEndpointOverridden());
                request.addHandlerContext(HandlerContextKey.SIGNING_REGION, this.getSigningRegion());
                request.addHandlerContext(HandlerContextKey.SERVICE_ID, "KMS");
                request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "CreateAlias");
                request.addHandlerContext(HandlerContextKey.ADVANCED_CONFIG, this.advancedConfig);
            }
            finally {
                awsRequestMetrics.endEvent(AWSRequestMetrics.Field.RequestMarshallTime);
            }
            HttpResponseHandler responseHandler = protocolFactory.createResponseHandler(new JsonOperationMetadata().withPayloadJson(true).withHasStreamingSuccessResponse(false), new CreateAliasResultJsonUnmarshaller());
            response = this.invoke(request, responseHandler, executionContext);
            CreateAliasResult createAliasResult = (CreateAliasResult)response.getAwsResponse();
            return createAliasResult;
        }
        finally {
            this.endClientExecution(awsRequestMetrics, request, response);
        }
    }

    @Override
    public CreateCustomKeyStoreResult createCustomKeyStore(CreateCustomKeyStoreRequest request) {
        request = this.beforeClientExecution(request);
        return this.executeCreateCustomKeyStore(request);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @SdkInternalApi
    final CreateCustomKeyStoreResult executeCreateCustomKeyStore(CreateCustomKeyStoreRequest createCustomKeyStoreRequest) {
        ExecutionContext executionContext = this.createExecutionContext(createCustomKeyStoreRequest);
        AWSRequestMetrics awsRequestMetrics = executionContext.getAwsRequestMetrics();
        awsRequestMetrics.startEvent(AWSRequestMetrics.Field.ClientExecuteTime);
        Request<CreateCustomKeyStoreRequest> request = null;
        Response response = null;
        try {
            awsRequestMetrics.startEvent(AWSRequestMetrics.Field.RequestMarshallTime);
            try {
                request = new CreateCustomKeyStoreRequestProtocolMarshaller(protocolFactory).marshall(super.beforeMarshalling(createCustomKeyStoreRequest));
                request.setAWSRequestMetrics(awsRequestMetrics);
                request.addHandlerContext(HandlerContextKey.CLIENT_ENDPOINT, this.endpoint);
                request.addHandlerContext(HandlerContextKey.ENDPOINT_OVERRIDDEN, this.isEndpointOverridden());
                request.addHandlerContext(HandlerContextKey.SIGNING_REGION, this.getSigningRegion());
                request.addHandlerContext(HandlerContextKey.SERVICE_ID, "KMS");
                request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "CreateCustomKeyStore");
                request.addHandlerContext(HandlerContextKey.ADVANCED_CONFIG, this.advancedConfig);
            }
            finally {
                awsRequestMetrics.endEvent(AWSRequestMetrics.Field.RequestMarshallTime);
            }
            HttpResponseHandler responseHandler = protocolFactory.createResponseHandler(new JsonOperationMetadata().withPayloadJson(true).withHasStreamingSuccessResponse(false), new CreateCustomKeyStoreResultJsonUnmarshaller());
            response = this.invoke(request, responseHandler, executionContext);
            CreateCustomKeyStoreResult createCustomKeyStoreResult = (CreateCustomKeyStoreResult)response.getAwsResponse();
            return createCustomKeyStoreResult;
        }
        finally {
            this.endClientExecution(awsRequestMetrics, request, response);
        }
    }

    @Override
    public CreateGrantResult createGrant(CreateGrantRequest request) {
        request = this.beforeClientExecution(request);
        return this.executeCreateGrant(request);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @SdkInternalApi
    final CreateGrantResult executeCreateGrant(CreateGrantRequest createGrantRequest) {
        ExecutionContext executionContext = this.createExecutionContext(createGrantRequest);
        AWSRequestMetrics awsRequestMetrics = executionContext.getAwsRequestMetrics();
        awsRequestMetrics.startEvent(AWSRequestMetrics.Field.ClientExecuteTime);
        Request<CreateGrantRequest> request = null;
        Response response = null;
        try {
            awsRequestMetrics.startEvent(AWSRequestMetrics.Field.RequestMarshallTime);
            try {
                request = new CreateGrantRequestProtocolMarshaller(protocolFactory).marshall(super.beforeMarshalling(createGrantRequest));
                request.setAWSRequestMetrics(awsRequestMetrics);
                request.addHandlerContext(HandlerContextKey.CLIENT_ENDPOINT, this.endpoint);
                request.addHandlerContext(HandlerContextKey.ENDPOINT_OVERRIDDEN, this.isEndpointOverridden());
                request.addHandlerContext(HandlerContextKey.SIGNING_REGION, this.getSigningRegion());
                request.addHandlerContext(HandlerContextKey.SERVICE_ID, "KMS");
                request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "CreateGrant");
                request.addHandlerContext(HandlerContextKey.ADVANCED_CONFIG, this.advancedConfig);
            }
            finally {
                awsRequestMetrics.endEvent(AWSRequestMetrics.Field.RequestMarshallTime);
            }
            HttpResponseHandler responseHandler = protocolFactory.createResponseHandler(new JsonOperationMetadata().withPayloadJson(true).withHasStreamingSuccessResponse(false), new CreateGrantResultJsonUnmarshaller());
            response = this.invoke(request, responseHandler, executionContext);
            CreateGrantResult createGrantResult = (CreateGrantResult)response.getAwsResponse();
            return createGrantResult;
        }
        finally {
            this.endClientExecution(awsRequestMetrics, request, response);
        }
    }

    @Override
    public CreateKeyResult createKey(CreateKeyRequest request) {
        request = this.beforeClientExecution(request);
        return this.executeCreateKey(request);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @SdkInternalApi
    final CreateKeyResult executeCreateKey(CreateKeyRequest createKeyRequest) {
        ExecutionContext executionContext = this.createExecutionContext(createKeyRequest);
        AWSRequestMetrics awsRequestMetrics = executionContext.getAwsRequestMetrics();
        awsRequestMetrics.startEvent(AWSRequestMetrics.Field.ClientExecuteTime);
        Request<CreateKeyRequest> request = null;
        Response response = null;
        try {
            awsRequestMetrics.startEvent(AWSRequestMetrics.Field.RequestMarshallTime);
            try {
                request = new CreateKeyRequestProtocolMarshaller(protocolFactory).marshall(super.beforeMarshalling(createKeyRequest));
                request.setAWSRequestMetrics(awsRequestMetrics);
                request.addHandlerContext(HandlerContextKey.CLIENT_ENDPOINT, this.endpoint);
                request.addHandlerContext(HandlerContextKey.ENDPOINT_OVERRIDDEN, this.isEndpointOverridden());
                request.addHandlerContext(HandlerContextKey.SIGNING_REGION, this.getSigningRegion());
                request.addHandlerContext(HandlerContextKey.SERVICE_ID, "KMS");
                request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "CreateKey");
                request.addHandlerContext(HandlerContextKey.ADVANCED_CONFIG, this.advancedConfig);
            }
            finally {
                awsRequestMetrics.endEvent(AWSRequestMetrics.Field.RequestMarshallTime);
            }
            HttpResponseHandler responseHandler = protocolFactory.createResponseHandler(new JsonOperationMetadata().withPayloadJson(true).withHasStreamingSuccessResponse(false), new CreateKeyResultJsonUnmarshaller());
            response = this.invoke(request, responseHandler, executionContext);
            CreateKeyResult createKeyResult = (CreateKeyResult)response.getAwsResponse();
            return createKeyResult;
        }
        finally {
            this.endClientExecution(awsRequestMetrics, request, response);
        }
    }

    @Override
    public CreateKeyResult createKey() {
        return this.createKey(new CreateKeyRequest());
    }

    @Override
    public DecryptResult decrypt(DecryptRequest request) {
        request = this.beforeClientExecution(request);
        return this.executeDecrypt(request);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @SdkInternalApi
    final DecryptResult executeDecrypt(DecryptRequest decryptRequest) {
        ExecutionContext executionContext = this.createExecutionContext(decryptRequest);
        AWSRequestMetrics awsRequestMetrics = executionContext.getAwsRequestMetrics();
        awsRequestMetrics.startEvent(AWSRequestMetrics.Field.ClientExecuteTime);
        Request<DecryptRequest> request = null;
        Response response = null;
        try {
            awsRequestMetrics.startEvent(AWSRequestMetrics.Field.RequestMarshallTime);
            try {
                request = new DecryptRequestProtocolMarshaller(protocolFactory).marshall(super.beforeMarshalling(decryptRequest));
                request.setAWSRequestMetrics(awsRequestMetrics);
                request.addHandlerContext(HandlerContextKey.CLIENT_ENDPOINT, this.endpoint);
                request.addHandlerContext(HandlerContextKey.ENDPOINT_OVERRIDDEN, this.isEndpointOverridden());
                request.addHandlerContext(HandlerContextKey.SIGNING_REGION, this.getSigningRegion());
                request.addHandlerContext(HandlerContextKey.SERVICE_ID, "KMS");
                request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "Decrypt");
                request.addHandlerContext(HandlerContextKey.ADVANCED_CONFIG, this.advancedConfig);
            }
            finally {
                awsRequestMetrics.endEvent(AWSRequestMetrics.Field.RequestMarshallTime);
            }
            HttpResponseHandler responseHandler = protocolFactory.createResponseHandler(new JsonOperationMetadata().withPayloadJson(true).withHasStreamingSuccessResponse(false), new DecryptResultJsonUnmarshaller());
            response = this.invoke(request, responseHandler, executionContext);
            DecryptResult decryptResult = (DecryptResult)response.getAwsResponse();
            return decryptResult;
        }
        finally {
            this.endClientExecution(awsRequestMetrics, request, response);
        }
    }

    @Override
    public DeleteAliasResult deleteAlias(DeleteAliasRequest request) {
        request = this.beforeClientExecution(request);
        return this.executeDeleteAlias(request);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @SdkInternalApi
    final DeleteAliasResult executeDeleteAlias(DeleteAliasRequest deleteAliasRequest) {
        ExecutionContext executionContext = this.createExecutionContext(deleteAliasRequest);
        AWSRequestMetrics awsRequestMetrics = executionContext.getAwsRequestMetrics();
        awsRequestMetrics.startEvent(AWSRequestMetrics.Field.ClientExecuteTime);
        Request<DeleteAliasRequest> request = null;
        Response response = null;
        try {
            awsRequestMetrics.startEvent(AWSRequestMetrics.Field.RequestMarshallTime);
            try {
                request = new DeleteAliasRequestProtocolMarshaller(protocolFactory).marshall(super.beforeMarshalling(deleteAliasRequest));
                request.setAWSRequestMetrics(awsRequestMetrics);
                request.addHandlerContext(HandlerContextKey.CLIENT_ENDPOINT, this.endpoint);
                request.addHandlerContext(HandlerContextKey.ENDPOINT_OVERRIDDEN, this.isEndpointOverridden());
                request.addHandlerContext(HandlerContextKey.SIGNING_REGION, this.getSigningRegion());
                request.addHandlerContext(HandlerContextKey.SERVICE_ID, "KMS");
                request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "DeleteAlias");
                request.addHandlerContext(HandlerContextKey.ADVANCED_CONFIG, this.advancedConfig);
            }
            finally {
                awsRequestMetrics.endEvent(AWSRequestMetrics.Field.RequestMarshallTime);
            }
            HttpResponseHandler responseHandler = protocolFactory.createResponseHandler(new JsonOperationMetadata().withPayloadJson(true).withHasStreamingSuccessResponse(false), new DeleteAliasResultJsonUnmarshaller());
            response = this.invoke(request, responseHandler, executionContext);
            DeleteAliasResult deleteAliasResult = (DeleteAliasResult)response.getAwsResponse();
            return deleteAliasResult;
        }
        finally {
            this.endClientExecution(awsRequestMetrics, request, response);
        }
    }

    @Override
    public DeleteCustomKeyStoreResult deleteCustomKeyStore(DeleteCustomKeyStoreRequest request) {
        request = this.beforeClientExecution(request);
        return this.executeDeleteCustomKeyStore(request);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @SdkInternalApi
    final DeleteCustomKeyStoreResult executeDeleteCustomKeyStore(DeleteCustomKeyStoreRequest deleteCustomKeyStoreRequest) {
        ExecutionContext executionContext = this.createExecutionContext(deleteCustomKeyStoreRequest);
        AWSRequestMetrics awsRequestMetrics = executionContext.getAwsRequestMetrics();
        awsRequestMetrics.startEvent(AWSRequestMetrics.Field.ClientExecuteTime);
        Request<DeleteCustomKeyStoreRequest> request = null;
        Response response = null;
        try {
            awsRequestMetrics.startEvent(AWSRequestMetrics.Field.RequestMarshallTime);
            try {
                request = new DeleteCustomKeyStoreRequestProtocolMarshaller(protocolFactory).marshall(super.beforeMarshalling(deleteCustomKeyStoreRequest));
                request.setAWSRequestMetrics(awsRequestMetrics);
                request.addHandlerContext(HandlerContextKey.CLIENT_ENDPOINT, this.endpoint);
                request.addHandlerContext(HandlerContextKey.ENDPOINT_OVERRIDDEN, this.isEndpointOverridden());
                request.addHandlerContext(HandlerContextKey.SIGNING_REGION, this.getSigningRegion());
                request.addHandlerContext(HandlerContextKey.SERVICE_ID, "KMS");
                request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "DeleteCustomKeyStore");
                request.addHandlerContext(HandlerContextKey.ADVANCED_CONFIG, this.advancedConfig);
            }
            finally {
                awsRequestMetrics.endEvent(AWSRequestMetrics.Field.RequestMarshallTime);
            }
            HttpResponseHandler responseHandler = protocolFactory.createResponseHandler(new JsonOperationMetadata().withPayloadJson(true).withHasStreamingSuccessResponse(false), new DeleteCustomKeyStoreResultJsonUnmarshaller());
            response = this.invoke(request, responseHandler, executionContext);
            DeleteCustomKeyStoreResult deleteCustomKeyStoreResult = (DeleteCustomKeyStoreResult)response.getAwsResponse();
            return deleteCustomKeyStoreResult;
        }
        finally {
            this.endClientExecution(awsRequestMetrics, request, response);
        }
    }

    @Override
    public DeleteImportedKeyMaterialResult deleteImportedKeyMaterial(DeleteImportedKeyMaterialRequest request) {
        request = this.beforeClientExecution(request);
        return this.executeDeleteImportedKeyMaterial(request);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @SdkInternalApi
    final DeleteImportedKeyMaterialResult executeDeleteImportedKeyMaterial(DeleteImportedKeyMaterialRequest deleteImportedKeyMaterialRequest) {
        ExecutionContext executionContext = this.createExecutionContext(deleteImportedKeyMaterialRequest);
        AWSRequestMetrics awsRequestMetrics = executionContext.getAwsRequestMetrics();
        awsRequestMetrics.startEvent(AWSRequestMetrics.Field.ClientExecuteTime);
        Request<DeleteImportedKeyMaterialRequest> request = null;
        Response response = null;
        try {
            awsRequestMetrics.startEvent(AWSRequestMetrics.Field.RequestMarshallTime);
            try {
                request = new DeleteImportedKeyMaterialRequestProtocolMarshaller(protocolFactory).marshall(super.beforeMarshalling(deleteImportedKeyMaterialRequest));
                request.setAWSRequestMetrics(awsRequestMetrics);
                request.addHandlerContext(HandlerContextKey.CLIENT_ENDPOINT, this.endpoint);
                request.addHandlerContext(HandlerContextKey.ENDPOINT_OVERRIDDEN, this.isEndpointOverridden());
                request.addHandlerContext(HandlerContextKey.SIGNING_REGION, this.getSigningRegion());
                request.addHandlerContext(HandlerContextKey.SERVICE_ID, "KMS");
                request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "DeleteImportedKeyMaterial");
                request.addHandlerContext(HandlerContextKey.ADVANCED_CONFIG, this.advancedConfig);
            }
            finally {
                awsRequestMetrics.endEvent(AWSRequestMetrics.Field.RequestMarshallTime);
            }
            HttpResponseHandler responseHandler = protocolFactory.createResponseHandler(new JsonOperationMetadata().withPayloadJson(true).withHasStreamingSuccessResponse(false), new DeleteImportedKeyMaterialResultJsonUnmarshaller());
            response = this.invoke(request, responseHandler, executionContext);
            DeleteImportedKeyMaterialResult deleteImportedKeyMaterialResult = (DeleteImportedKeyMaterialResult)response.getAwsResponse();
            return deleteImportedKeyMaterialResult;
        }
        finally {
            this.endClientExecution(awsRequestMetrics, request, response);
        }
    }

    @Override
    public DescribeCustomKeyStoresResult describeCustomKeyStores(DescribeCustomKeyStoresRequest request) {
        request = this.beforeClientExecution(request);
        return this.executeDescribeCustomKeyStores(request);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @SdkInternalApi
    final DescribeCustomKeyStoresResult executeDescribeCustomKeyStores(DescribeCustomKeyStoresRequest describeCustomKeyStoresRequest) {
        ExecutionContext executionContext = this.createExecutionContext(describeCustomKeyStoresRequest);
        AWSRequestMetrics awsRequestMetrics = executionContext.getAwsRequestMetrics();
        awsRequestMetrics.startEvent(AWSRequestMetrics.Field.ClientExecuteTime);
        Request<DescribeCustomKeyStoresRequest> request = null;
        Response response = null;
        try {
            awsRequestMetrics.startEvent(AWSRequestMetrics.Field.RequestMarshallTime);
            try {
                request = new DescribeCustomKeyStoresRequestProtocolMarshaller(protocolFactory).marshall(super.beforeMarshalling(describeCustomKeyStoresRequest));
                request.setAWSRequestMetrics(awsRequestMetrics);
                request.addHandlerContext(HandlerContextKey.CLIENT_ENDPOINT, this.endpoint);
                request.addHandlerContext(HandlerContextKey.ENDPOINT_OVERRIDDEN, this.isEndpointOverridden());
                request.addHandlerContext(HandlerContextKey.SIGNING_REGION, this.getSigningRegion());
                request.addHandlerContext(HandlerContextKey.SERVICE_ID, "KMS");
                request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "DescribeCustomKeyStores");
                request.addHandlerContext(HandlerContextKey.ADVANCED_CONFIG, this.advancedConfig);
            }
            finally {
                awsRequestMetrics.endEvent(AWSRequestMetrics.Field.RequestMarshallTime);
            }
            HttpResponseHandler responseHandler = protocolFactory.createResponseHandler(new JsonOperationMetadata().withPayloadJson(true).withHasStreamingSuccessResponse(false), new DescribeCustomKeyStoresResultJsonUnmarshaller());
            response = this.invoke(request, responseHandler, executionContext);
            DescribeCustomKeyStoresResult describeCustomKeyStoresResult = (DescribeCustomKeyStoresResult)response.getAwsResponse();
            return describeCustomKeyStoresResult;
        }
        finally {
            this.endClientExecution(awsRequestMetrics, request, response);
        }
    }

    @Override
    public DescribeKeyResult describeKey(DescribeKeyRequest request) {
        request = this.beforeClientExecution(request);
        return this.executeDescribeKey(request);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @SdkInternalApi
    final DescribeKeyResult executeDescribeKey(DescribeKeyRequest describeKeyRequest) {
        ExecutionContext executionContext = this.createExecutionContext(describeKeyRequest);
        AWSRequestMetrics awsRequestMetrics = executionContext.getAwsRequestMetrics();
        awsRequestMetrics.startEvent(AWSRequestMetrics.Field.ClientExecuteTime);
        Request<DescribeKeyRequest> request = null;
        Response response = null;
        try {
            awsRequestMetrics.startEvent(AWSRequestMetrics.Field.RequestMarshallTime);
            try {
                request = new DescribeKeyRequestProtocolMarshaller(protocolFactory).marshall(super.beforeMarshalling(describeKeyRequest));
                request.setAWSRequestMetrics(awsRequestMetrics);
                request.addHandlerContext(HandlerContextKey.CLIENT_ENDPOINT, this.endpoint);
                request.addHandlerContext(HandlerContextKey.ENDPOINT_OVERRIDDEN, this.isEndpointOverridden());
                request.addHandlerContext(HandlerContextKey.SIGNING_REGION, this.getSigningRegion());
                request.addHandlerContext(HandlerContextKey.SERVICE_ID, "KMS");
                request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "DescribeKey");
                request.addHandlerContext(HandlerContextKey.ADVANCED_CONFIG, this.advancedConfig);
            }
            finally {
                awsRequestMetrics.endEvent(AWSRequestMetrics.Field.RequestMarshallTime);
            }
            HttpResponseHandler responseHandler = protocolFactory.createResponseHandler(new JsonOperationMetadata().withPayloadJson(true).withHasStreamingSuccessResponse(false), new DescribeKeyResultJsonUnmarshaller());
            response = this.invoke(request, responseHandler, executionContext);
            DescribeKeyResult describeKeyResult = (DescribeKeyResult)response.getAwsResponse();
            return describeKeyResult;
        }
        finally {
            this.endClientExecution(awsRequestMetrics, request, response);
        }
    }

    @Override
    public DisableKeyResult disableKey(DisableKeyRequest request) {
        request = this.beforeClientExecution(request);
        return this.executeDisableKey(request);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @SdkInternalApi
    final DisableKeyResult executeDisableKey(DisableKeyRequest disableKeyRequest) {
        ExecutionContext executionContext = this.createExecutionContext(disableKeyRequest);
        AWSRequestMetrics awsRequestMetrics = executionContext.getAwsRequestMetrics();
        awsRequestMetrics.startEvent(AWSRequestMetrics.Field.ClientExecuteTime);
        Request<DisableKeyRequest> request = null;
        Response response = null;
        try {
            awsRequestMetrics.startEvent(AWSRequestMetrics.Field.RequestMarshallTime);
            try {
                request = new DisableKeyRequestProtocolMarshaller(protocolFactory).marshall(super.beforeMarshalling(disableKeyRequest));
                request.setAWSRequestMetrics(awsRequestMetrics);
                request.addHandlerContext(HandlerContextKey.CLIENT_ENDPOINT, this.endpoint);
                request.addHandlerContext(HandlerContextKey.ENDPOINT_OVERRIDDEN, this.isEndpointOverridden());
                request.addHandlerContext(HandlerContextKey.SIGNING_REGION, this.getSigningRegion());
                request.addHandlerContext(HandlerContextKey.SERVICE_ID, "KMS");
                request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "DisableKey");
                request.addHandlerContext(HandlerContextKey.ADVANCED_CONFIG, this.advancedConfig);
            }
            finally {
                awsRequestMetrics.endEvent(AWSRequestMetrics.Field.RequestMarshallTime);
            }
            HttpResponseHandler responseHandler = protocolFactory.createResponseHandler(new JsonOperationMetadata().withPayloadJson(true).withHasStreamingSuccessResponse(false), new DisableKeyResultJsonUnmarshaller());
            response = this.invoke(request, responseHandler, executionContext);
            DisableKeyResult disableKeyResult = (DisableKeyResult)response.getAwsResponse();
            return disableKeyResult;
        }
        finally {
            this.endClientExecution(awsRequestMetrics, request, response);
        }
    }

    @Override
    public DisableKeyRotationResult disableKeyRotation(DisableKeyRotationRequest request) {
        request = this.beforeClientExecution(request);
        return this.executeDisableKeyRotation(request);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @SdkInternalApi
    final DisableKeyRotationResult executeDisableKeyRotation(DisableKeyRotationRequest disableKeyRotationRequest) {
        ExecutionContext executionContext = this.createExecutionContext(disableKeyRotationRequest);
        AWSRequestMetrics awsRequestMetrics = executionContext.getAwsRequestMetrics();
        awsRequestMetrics.startEvent(AWSRequestMetrics.Field.ClientExecuteTime);
        Request<DisableKeyRotationRequest> request = null;
        Response response = null;
        try {
            awsRequestMetrics.startEvent(AWSRequestMetrics.Field.RequestMarshallTime);
            try {
                request = new DisableKeyRotationRequestProtocolMarshaller(protocolFactory).marshall(super.beforeMarshalling(disableKeyRotationRequest));
                request.setAWSRequestMetrics(awsRequestMetrics);
                request.addHandlerContext(HandlerContextKey.CLIENT_ENDPOINT, this.endpoint);
                request.addHandlerContext(HandlerContextKey.ENDPOINT_OVERRIDDEN, this.isEndpointOverridden());
                request.addHandlerContext(HandlerContextKey.SIGNING_REGION, this.getSigningRegion());
                request.addHandlerContext(HandlerContextKey.SERVICE_ID, "KMS");
                request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "DisableKeyRotation");
                request.addHandlerContext(HandlerContextKey.ADVANCED_CONFIG, this.advancedConfig);
            }
            finally {
                awsRequestMetrics.endEvent(AWSRequestMetrics.Field.RequestMarshallTime);
            }
            HttpResponseHandler responseHandler = protocolFactory.createResponseHandler(new JsonOperationMetadata().withPayloadJson(true).withHasStreamingSuccessResponse(false), new DisableKeyRotationResultJsonUnmarshaller());
            response = this.invoke(request, responseHandler, executionContext);
            DisableKeyRotationResult disableKeyRotationResult = (DisableKeyRotationResult)response.getAwsResponse();
            return disableKeyRotationResult;
        }
        finally {
            this.endClientExecution(awsRequestMetrics, request, response);
        }
    }

    @Override
    public DisconnectCustomKeyStoreResult disconnectCustomKeyStore(DisconnectCustomKeyStoreRequest request) {
        request = this.beforeClientExecution(request);
        return this.executeDisconnectCustomKeyStore(request);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @SdkInternalApi
    final DisconnectCustomKeyStoreResult executeDisconnectCustomKeyStore(DisconnectCustomKeyStoreRequest disconnectCustomKeyStoreRequest) {
        ExecutionContext executionContext = this.createExecutionContext(disconnectCustomKeyStoreRequest);
        AWSRequestMetrics awsRequestMetrics = executionContext.getAwsRequestMetrics();
        awsRequestMetrics.startEvent(AWSRequestMetrics.Field.ClientExecuteTime);
        Request<DisconnectCustomKeyStoreRequest> request = null;
        Response response = null;
        try {
            awsRequestMetrics.startEvent(AWSRequestMetrics.Field.RequestMarshallTime);
            try {
                request = new DisconnectCustomKeyStoreRequestProtocolMarshaller(protocolFactory).marshall(super.beforeMarshalling(disconnectCustomKeyStoreRequest));
                request.setAWSRequestMetrics(awsRequestMetrics);
                request.addHandlerContext(HandlerContextKey.CLIENT_ENDPOINT, this.endpoint);
                request.addHandlerContext(HandlerContextKey.ENDPOINT_OVERRIDDEN, this.isEndpointOverridden());
                request.addHandlerContext(HandlerContextKey.SIGNING_REGION, this.getSigningRegion());
                request.addHandlerContext(HandlerContextKey.SERVICE_ID, "KMS");
                request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "DisconnectCustomKeyStore");
                request.addHandlerContext(HandlerContextKey.ADVANCED_CONFIG, this.advancedConfig);
            }
            finally {
                awsRequestMetrics.endEvent(AWSRequestMetrics.Field.RequestMarshallTime);
            }
            HttpResponseHandler responseHandler = protocolFactory.createResponseHandler(new JsonOperationMetadata().withPayloadJson(true).withHasStreamingSuccessResponse(false), new DisconnectCustomKeyStoreResultJsonUnmarshaller());
            response = this.invoke(request, responseHandler, executionContext);
            DisconnectCustomKeyStoreResult disconnectCustomKeyStoreResult = (DisconnectCustomKeyStoreResult)response.getAwsResponse();
            return disconnectCustomKeyStoreResult;
        }
        finally {
            this.endClientExecution(awsRequestMetrics, request, response);
        }
    }

    @Override
    public EnableKeyResult enableKey(EnableKeyRequest request) {
        request = this.beforeClientExecution(request);
        return this.executeEnableKey(request);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @SdkInternalApi
    final EnableKeyResult executeEnableKey(EnableKeyRequest enableKeyRequest) {
        ExecutionContext executionContext = this.createExecutionContext(enableKeyRequest);
        AWSRequestMetrics awsRequestMetrics = executionContext.getAwsRequestMetrics();
        awsRequestMetrics.startEvent(AWSRequestMetrics.Field.ClientExecuteTime);
        Request<EnableKeyRequest> request = null;
        Response response = null;
        try {
            awsRequestMetrics.startEvent(AWSRequestMetrics.Field.RequestMarshallTime);
            try {
                request = new EnableKeyRequestProtocolMarshaller(protocolFactory).marshall(super.beforeMarshalling(enableKeyRequest));
                request.setAWSRequestMetrics(awsRequestMetrics);
                request.addHandlerContext(HandlerContextKey.CLIENT_ENDPOINT, this.endpoint);
                request.addHandlerContext(HandlerContextKey.ENDPOINT_OVERRIDDEN, this.isEndpointOverridden());
                request.addHandlerContext(HandlerContextKey.SIGNING_REGION, this.getSigningRegion());
                request.addHandlerContext(HandlerContextKey.SERVICE_ID, "KMS");
                request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "EnableKey");
                request.addHandlerContext(HandlerContextKey.ADVANCED_CONFIG, this.advancedConfig);
            }
            finally {
                awsRequestMetrics.endEvent(AWSRequestMetrics.Field.RequestMarshallTime);
            }
            HttpResponseHandler responseHandler = protocolFactory.createResponseHandler(new JsonOperationMetadata().withPayloadJson(true).withHasStreamingSuccessResponse(false), new EnableKeyResultJsonUnmarshaller());
            response = this.invoke(request, responseHandler, executionContext);
            EnableKeyResult enableKeyResult = (EnableKeyResult)response.getAwsResponse();
            return enableKeyResult;
        }
        finally {
            this.endClientExecution(awsRequestMetrics, request, response);
        }
    }

    @Override
    public EnableKeyRotationResult enableKeyRotation(EnableKeyRotationRequest request) {
        request = this.beforeClientExecution(request);
        return this.executeEnableKeyRotation(request);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @SdkInternalApi
    final EnableKeyRotationResult executeEnableKeyRotation(EnableKeyRotationRequest enableKeyRotationRequest) {
        ExecutionContext executionContext = this.createExecutionContext(enableKeyRotationRequest);
        AWSRequestMetrics awsRequestMetrics = executionContext.getAwsRequestMetrics();
        awsRequestMetrics.startEvent(AWSRequestMetrics.Field.ClientExecuteTime);
        Request<EnableKeyRotationRequest> request = null;
        Response response = null;
        try {
            awsRequestMetrics.startEvent(AWSRequestMetrics.Field.RequestMarshallTime);
            try {
                request = new EnableKeyRotationRequestProtocolMarshaller(protocolFactory).marshall(super.beforeMarshalling(enableKeyRotationRequest));
                request.setAWSRequestMetrics(awsRequestMetrics);
                request.addHandlerContext(HandlerContextKey.CLIENT_ENDPOINT, this.endpoint);
                request.addHandlerContext(HandlerContextKey.ENDPOINT_OVERRIDDEN, this.isEndpointOverridden());
                request.addHandlerContext(HandlerContextKey.SIGNING_REGION, this.getSigningRegion());
                request.addHandlerContext(HandlerContextKey.SERVICE_ID, "KMS");
                request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "EnableKeyRotation");
                request.addHandlerContext(HandlerContextKey.ADVANCED_CONFIG, this.advancedConfig);
            }
            finally {
                awsRequestMetrics.endEvent(AWSRequestMetrics.Field.RequestMarshallTime);
            }
            HttpResponseHandler responseHandler = protocolFactory.createResponseHandler(new JsonOperationMetadata().withPayloadJson(true).withHasStreamingSuccessResponse(false), new EnableKeyRotationResultJsonUnmarshaller());
            response = this.invoke(request, responseHandler, executionContext);
            EnableKeyRotationResult enableKeyRotationResult = (EnableKeyRotationResult)response.getAwsResponse();
            return enableKeyRotationResult;
        }
        finally {
            this.endClientExecution(awsRequestMetrics, request, response);
        }
    }

    @Override
    public EncryptResult encrypt(EncryptRequest request) {
        request = this.beforeClientExecution(request);
        return this.executeEncrypt(request);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @SdkInternalApi
    final EncryptResult executeEncrypt(EncryptRequest encryptRequest) {
        ExecutionContext executionContext = this.createExecutionContext(encryptRequest);
        AWSRequestMetrics awsRequestMetrics = executionContext.getAwsRequestMetrics();
        awsRequestMetrics.startEvent(AWSRequestMetrics.Field.ClientExecuteTime);
        Request<EncryptRequest> request = null;
        Response response = null;
        try {
            awsRequestMetrics.startEvent(AWSRequestMetrics.Field.RequestMarshallTime);
            try {
                request = new EncryptRequestProtocolMarshaller(protocolFactory).marshall(super.beforeMarshalling(encryptRequest));
                request.setAWSRequestMetrics(awsRequestMetrics);
                request.addHandlerContext(HandlerContextKey.CLIENT_ENDPOINT, this.endpoint);
                request.addHandlerContext(HandlerContextKey.ENDPOINT_OVERRIDDEN, this.isEndpointOverridden());
                request.addHandlerContext(HandlerContextKey.SIGNING_REGION, this.getSigningRegion());
                request.addHandlerContext(HandlerContextKey.SERVICE_ID, "KMS");
                request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "Encrypt");
                request.addHandlerContext(HandlerContextKey.ADVANCED_CONFIG, this.advancedConfig);
            }
            finally {
                awsRequestMetrics.endEvent(AWSRequestMetrics.Field.RequestMarshallTime);
            }
            HttpResponseHandler responseHandler = protocolFactory.createResponseHandler(new JsonOperationMetadata().withPayloadJson(true).withHasStreamingSuccessResponse(false), new EncryptResultJsonUnmarshaller());
            response = this.invoke(request, responseHandler, executionContext);
            EncryptResult encryptResult = (EncryptResult)response.getAwsResponse();
            return encryptResult;
        }
        finally {
            this.endClientExecution(awsRequestMetrics, request, response);
        }
    }

    @Override
    public GenerateDataKeyResult generateDataKey(GenerateDataKeyRequest request) {
        request = this.beforeClientExecution(request);
        return this.executeGenerateDataKey(request);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @SdkInternalApi
    final GenerateDataKeyResult executeGenerateDataKey(GenerateDataKeyRequest generateDataKeyRequest) {
        ExecutionContext executionContext = this.createExecutionContext(generateDataKeyRequest);
        AWSRequestMetrics awsRequestMetrics = executionContext.getAwsRequestMetrics();
        awsRequestMetrics.startEvent(AWSRequestMetrics.Field.ClientExecuteTime);
        Request<GenerateDataKeyRequest> request = null;
        Response response = null;
        try {
            awsRequestMetrics.startEvent(AWSRequestMetrics.Field.RequestMarshallTime);
            try {
                request = new GenerateDataKeyRequestProtocolMarshaller(protocolFactory).marshall(super.beforeMarshalling(generateDataKeyRequest));
                request.setAWSRequestMetrics(awsRequestMetrics);
                request.addHandlerContext(HandlerContextKey.CLIENT_ENDPOINT, this.endpoint);
                request.addHandlerContext(HandlerContextKey.ENDPOINT_OVERRIDDEN, this.isEndpointOverridden());
                request.addHandlerContext(HandlerContextKey.SIGNING_REGION, this.getSigningRegion());
                request.addHandlerContext(HandlerContextKey.SERVICE_ID, "KMS");
                request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "GenerateDataKey");
                request.addHandlerContext(HandlerContextKey.ADVANCED_CONFIG, this.advancedConfig);
            }
            finally {
                awsRequestMetrics.endEvent(AWSRequestMetrics.Field.RequestMarshallTime);
            }
            HttpResponseHandler responseHandler = protocolFactory.createResponseHandler(new JsonOperationMetadata().withPayloadJson(true).withHasStreamingSuccessResponse(false), new GenerateDataKeyResultJsonUnmarshaller());
            response = this.invoke(request, responseHandler, executionContext);
            GenerateDataKeyResult generateDataKeyResult = (GenerateDataKeyResult)response.getAwsResponse();
            return generateDataKeyResult;
        }
        finally {
            this.endClientExecution(awsRequestMetrics, request, response);
        }
    }

    @Override
    public GenerateDataKeyPairResult generateDataKeyPair(GenerateDataKeyPairRequest request) {
        request = this.beforeClientExecution(request);
        return this.executeGenerateDataKeyPair(request);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @SdkInternalApi
    final GenerateDataKeyPairResult executeGenerateDataKeyPair(GenerateDataKeyPairRequest generateDataKeyPairRequest) {
        ExecutionContext executionContext = this.createExecutionContext(generateDataKeyPairRequest);
        AWSRequestMetrics awsRequestMetrics = executionContext.getAwsRequestMetrics();
        awsRequestMetrics.startEvent(AWSRequestMetrics.Field.ClientExecuteTime);
        Request<GenerateDataKeyPairRequest> request = null;
        Response response = null;
        try {
            awsRequestMetrics.startEvent(AWSRequestMetrics.Field.RequestMarshallTime);
            try {
                request = new GenerateDataKeyPairRequestProtocolMarshaller(protocolFactory).marshall(super.beforeMarshalling(generateDataKeyPairRequest));
                request.setAWSRequestMetrics(awsRequestMetrics);
                request.addHandlerContext(HandlerContextKey.CLIENT_ENDPOINT, this.endpoint);
                request.addHandlerContext(HandlerContextKey.ENDPOINT_OVERRIDDEN, this.isEndpointOverridden());
                request.addHandlerContext(HandlerContextKey.SIGNING_REGION, this.getSigningRegion());
                request.addHandlerContext(HandlerContextKey.SERVICE_ID, "KMS");
                request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "GenerateDataKeyPair");
                request.addHandlerContext(HandlerContextKey.ADVANCED_CONFIG, this.advancedConfig);
            }
            finally {
                awsRequestMetrics.endEvent(AWSRequestMetrics.Field.RequestMarshallTime);
            }
            HttpResponseHandler responseHandler = protocolFactory.createResponseHandler(new JsonOperationMetadata().withPayloadJson(true).withHasStreamingSuccessResponse(false), new GenerateDataKeyPairResultJsonUnmarshaller());
            response = this.invoke(request, responseHandler, executionContext);
            GenerateDataKeyPairResult generateDataKeyPairResult = (GenerateDataKeyPairResult)response.getAwsResponse();
            return generateDataKeyPairResult;
        }
        finally {
            this.endClientExecution(awsRequestMetrics, request, response);
        }
    }

    @Override
    public GenerateDataKeyPairWithoutPlaintextResult generateDataKeyPairWithoutPlaintext(GenerateDataKeyPairWithoutPlaintextRequest request) {
        request = this.beforeClientExecution(request);
        return this.executeGenerateDataKeyPairWithoutPlaintext(request);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @SdkInternalApi
    final GenerateDataKeyPairWithoutPlaintextResult executeGenerateDataKeyPairWithoutPlaintext(GenerateDataKeyPairWithoutPlaintextRequest generateDataKeyPairWithoutPlaintextRequest) {
        ExecutionContext executionContext = this.createExecutionContext(generateDataKeyPairWithoutPlaintextRequest);
        AWSRequestMetrics awsRequestMetrics = executionContext.getAwsRequestMetrics();
        awsRequestMetrics.startEvent(AWSRequestMetrics.Field.ClientExecuteTime);
        Request<GenerateDataKeyPairWithoutPlaintextRequest> request = null;
        Response response = null;
        try {
            awsRequestMetrics.startEvent(AWSRequestMetrics.Field.RequestMarshallTime);
            try {
                request = new GenerateDataKeyPairWithoutPlaintextRequestProtocolMarshaller(protocolFactory).marshall(super.beforeMarshalling(generateDataKeyPairWithoutPlaintextRequest));
                request.setAWSRequestMetrics(awsRequestMetrics);
                request.addHandlerContext(HandlerContextKey.CLIENT_ENDPOINT, this.endpoint);
                request.addHandlerContext(HandlerContextKey.ENDPOINT_OVERRIDDEN, this.isEndpointOverridden());
                request.addHandlerContext(HandlerContextKey.SIGNING_REGION, this.getSigningRegion());
                request.addHandlerContext(HandlerContextKey.SERVICE_ID, "KMS");
                request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "GenerateDataKeyPairWithoutPlaintext");
                request.addHandlerContext(HandlerContextKey.ADVANCED_CONFIG, this.advancedConfig);
            }
            finally {
                awsRequestMetrics.endEvent(AWSRequestMetrics.Field.RequestMarshallTime);
            }
            HttpResponseHandler responseHandler = protocolFactory.createResponseHandler(new JsonOperationMetadata().withPayloadJson(true).withHasStreamingSuccessResponse(false), new GenerateDataKeyPairWithoutPlaintextResultJsonUnmarshaller());
            response = this.invoke(request, responseHandler, executionContext);
            GenerateDataKeyPairWithoutPlaintextResult generateDataKeyPairWithoutPlaintextResult = (GenerateDataKeyPairWithoutPlaintextResult)response.getAwsResponse();
            return generateDataKeyPairWithoutPlaintextResult;
        }
        finally {
            this.endClientExecution(awsRequestMetrics, request, response);
        }
    }

    @Override
    public GenerateDataKeyWithoutPlaintextResult generateDataKeyWithoutPlaintext(GenerateDataKeyWithoutPlaintextRequest request) {
        request = this.beforeClientExecution(request);
        return this.executeGenerateDataKeyWithoutPlaintext(request);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @SdkInternalApi
    final GenerateDataKeyWithoutPlaintextResult executeGenerateDataKeyWithoutPlaintext(GenerateDataKeyWithoutPlaintextRequest generateDataKeyWithoutPlaintextRequest) {
        ExecutionContext executionContext = this.createExecutionContext(generateDataKeyWithoutPlaintextRequest);
        AWSRequestMetrics awsRequestMetrics = executionContext.getAwsRequestMetrics();
        awsRequestMetrics.startEvent(AWSRequestMetrics.Field.ClientExecuteTime);
        Request<GenerateDataKeyWithoutPlaintextRequest> request = null;
        Response response = null;
        try {
            awsRequestMetrics.startEvent(AWSRequestMetrics.Field.RequestMarshallTime);
            try {
                request = new GenerateDataKeyWithoutPlaintextRequestProtocolMarshaller(protocolFactory).marshall(super.beforeMarshalling(generateDataKeyWithoutPlaintextRequest));
                request.setAWSRequestMetrics(awsRequestMetrics);
                request.addHandlerContext(HandlerContextKey.CLIENT_ENDPOINT, this.endpoint);
                request.addHandlerContext(HandlerContextKey.ENDPOINT_OVERRIDDEN, this.isEndpointOverridden());
                request.addHandlerContext(HandlerContextKey.SIGNING_REGION, this.getSigningRegion());
                request.addHandlerContext(HandlerContextKey.SERVICE_ID, "KMS");
                request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "GenerateDataKeyWithoutPlaintext");
                request.addHandlerContext(HandlerContextKey.ADVANCED_CONFIG, this.advancedConfig);
            }
            finally {
                awsRequestMetrics.endEvent(AWSRequestMetrics.Field.RequestMarshallTime);
            }
            HttpResponseHandler responseHandler = protocolFactory.createResponseHandler(new JsonOperationMetadata().withPayloadJson(true).withHasStreamingSuccessResponse(false), new GenerateDataKeyWithoutPlaintextResultJsonUnmarshaller());
            response = this.invoke(request, responseHandler, executionContext);
            GenerateDataKeyWithoutPlaintextResult generateDataKeyWithoutPlaintextResult = (GenerateDataKeyWithoutPlaintextResult)response.getAwsResponse();
            return generateDataKeyWithoutPlaintextResult;
        }
        finally {
            this.endClientExecution(awsRequestMetrics, request, response);
        }
    }

    @Override
    public GenerateMacResult generateMac(GenerateMacRequest request) {
        request = this.beforeClientExecution(request);
        return this.executeGenerateMac(request);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @SdkInternalApi
    final GenerateMacResult executeGenerateMac(GenerateMacRequest generateMacRequest) {
        ExecutionContext executionContext = this.createExecutionContext(generateMacRequest);
        AWSRequestMetrics awsRequestMetrics = executionContext.getAwsRequestMetrics();
        awsRequestMetrics.startEvent(AWSRequestMetrics.Field.ClientExecuteTime);
        Request<GenerateMacRequest> request = null;
        Response response = null;
        try {
            awsRequestMetrics.startEvent(AWSRequestMetrics.Field.RequestMarshallTime);
            try {
                request = new GenerateMacRequestProtocolMarshaller(protocolFactory).marshall(super.beforeMarshalling(generateMacRequest));
                request.setAWSRequestMetrics(awsRequestMetrics);
                request.addHandlerContext(HandlerContextKey.CLIENT_ENDPOINT, this.endpoint);
                request.addHandlerContext(HandlerContextKey.ENDPOINT_OVERRIDDEN, this.isEndpointOverridden());
                request.addHandlerContext(HandlerContextKey.SIGNING_REGION, this.getSigningRegion());
                request.addHandlerContext(HandlerContextKey.SERVICE_ID, "KMS");
                request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "GenerateMac");
                request.addHandlerContext(HandlerContextKey.ADVANCED_CONFIG, this.advancedConfig);
            }
            finally {
                awsRequestMetrics.endEvent(AWSRequestMetrics.Field.RequestMarshallTime);
            }
            HttpResponseHandler responseHandler = protocolFactory.createResponseHandler(new JsonOperationMetadata().withPayloadJson(true).withHasStreamingSuccessResponse(false), new GenerateMacResultJsonUnmarshaller());
            response = this.invoke(request, responseHandler, executionContext);
            GenerateMacResult generateMacResult = (GenerateMacResult)response.getAwsResponse();
            return generateMacResult;
        }
        finally {
            this.endClientExecution(awsRequestMetrics, request, response);
        }
    }

    @Override
    public GenerateRandomResult generateRandom(GenerateRandomRequest request) {
        request = this.beforeClientExecution(request);
        return this.executeGenerateRandom(request);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @SdkInternalApi
    final GenerateRandomResult executeGenerateRandom(GenerateRandomRequest generateRandomRequest) {
        ExecutionContext executionContext = this.createExecutionContext(generateRandomRequest);
        AWSRequestMetrics awsRequestMetrics = executionContext.getAwsRequestMetrics();
        awsRequestMetrics.startEvent(AWSRequestMetrics.Field.ClientExecuteTime);
        Request<GenerateRandomRequest> request = null;
        Response response = null;
        try {
            awsRequestMetrics.startEvent(AWSRequestMetrics.Field.RequestMarshallTime);
            try {
                request = new GenerateRandomRequestProtocolMarshaller(protocolFactory).marshall(super.beforeMarshalling(generateRandomRequest));
                request.setAWSRequestMetrics(awsRequestMetrics);
                request.addHandlerContext(HandlerContextKey.CLIENT_ENDPOINT, this.endpoint);
                request.addHandlerContext(HandlerContextKey.ENDPOINT_OVERRIDDEN, this.isEndpointOverridden());
                request.addHandlerContext(HandlerContextKey.SIGNING_REGION, this.getSigningRegion());
                request.addHandlerContext(HandlerContextKey.SERVICE_ID, "KMS");
                request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "GenerateRandom");
                request.addHandlerContext(HandlerContextKey.ADVANCED_CONFIG, this.advancedConfig);
            }
            finally {
                awsRequestMetrics.endEvent(AWSRequestMetrics.Field.RequestMarshallTime);
            }
            HttpResponseHandler responseHandler = protocolFactory.createResponseHandler(new JsonOperationMetadata().withPayloadJson(true).withHasStreamingSuccessResponse(false), new GenerateRandomResultJsonUnmarshaller());
            response = this.invoke(request, responseHandler, executionContext);
            GenerateRandomResult generateRandomResult = (GenerateRandomResult)response.getAwsResponse();
            return generateRandomResult;
        }
        finally {
            this.endClientExecution(awsRequestMetrics, request, response);
        }
    }

    @Override
    public GenerateRandomResult generateRandom() {
        return this.generateRandom(new GenerateRandomRequest());
    }

    @Override
    public GetKeyPolicyResult getKeyPolicy(GetKeyPolicyRequest request) {
        request = this.beforeClientExecution(request);
        return this.executeGetKeyPolicy(request);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @SdkInternalApi
    final GetKeyPolicyResult executeGetKeyPolicy(GetKeyPolicyRequest getKeyPolicyRequest) {
        ExecutionContext executionContext = this.createExecutionContext(getKeyPolicyRequest);
        AWSRequestMetrics awsRequestMetrics = executionContext.getAwsRequestMetrics();
        awsRequestMetrics.startEvent(AWSRequestMetrics.Field.ClientExecuteTime);
        Request<GetKeyPolicyRequest> request = null;
        Response response = null;
        try {
            awsRequestMetrics.startEvent(AWSRequestMetrics.Field.RequestMarshallTime);
            try {
                request = new GetKeyPolicyRequestProtocolMarshaller(protocolFactory).marshall(super.beforeMarshalling(getKeyPolicyRequest));
                request.setAWSRequestMetrics(awsRequestMetrics);
                request.addHandlerContext(HandlerContextKey.CLIENT_ENDPOINT, this.endpoint);
                request.addHandlerContext(HandlerContextKey.ENDPOINT_OVERRIDDEN, this.isEndpointOverridden());
                request.addHandlerContext(HandlerContextKey.SIGNING_REGION, this.getSigningRegion());
                request.addHandlerContext(HandlerContextKey.SERVICE_ID, "KMS");
                request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "GetKeyPolicy");
                request.addHandlerContext(HandlerContextKey.ADVANCED_CONFIG, this.advancedConfig);
            }
            finally {
                awsRequestMetrics.endEvent(AWSRequestMetrics.Field.RequestMarshallTime);
            }
            HttpResponseHandler responseHandler = protocolFactory.createResponseHandler(new JsonOperationMetadata().withPayloadJson(true).withHasStreamingSuccessResponse(false), new GetKeyPolicyResultJsonUnmarshaller());
            response = this.invoke(request, responseHandler, executionContext);
            GetKeyPolicyResult getKeyPolicyResult = (GetKeyPolicyResult)response.getAwsResponse();
            return getKeyPolicyResult;
        }
        finally {
            this.endClientExecution(awsRequestMetrics, request, response);
        }
    }

    @Override
    public GetKeyRotationStatusResult getKeyRotationStatus(GetKeyRotationStatusRequest request) {
        request = this.beforeClientExecution(request);
        return this.executeGetKeyRotationStatus(request);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @SdkInternalApi
    final GetKeyRotationStatusResult executeGetKeyRotationStatus(GetKeyRotationStatusRequest getKeyRotationStatusRequest) {
        ExecutionContext executionContext = this.createExecutionContext(getKeyRotationStatusRequest);
        AWSRequestMetrics awsRequestMetrics = executionContext.getAwsRequestMetrics();
        awsRequestMetrics.startEvent(AWSRequestMetrics.Field.ClientExecuteTime);
        Request<GetKeyRotationStatusRequest> request = null;
        Response response = null;
        try {
            awsRequestMetrics.startEvent(AWSRequestMetrics.Field.RequestMarshallTime);
            try {
                request = new GetKeyRotationStatusRequestProtocolMarshaller(protocolFactory).marshall(super.beforeMarshalling(getKeyRotationStatusRequest));
                request.setAWSRequestMetrics(awsRequestMetrics);
                request.addHandlerContext(HandlerContextKey.CLIENT_ENDPOINT, this.endpoint);
                request.addHandlerContext(HandlerContextKey.ENDPOINT_OVERRIDDEN, this.isEndpointOverridden());
                request.addHandlerContext(HandlerContextKey.SIGNING_REGION, this.getSigningRegion());
                request.addHandlerContext(HandlerContextKey.SERVICE_ID, "KMS");
                request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "GetKeyRotationStatus");
                request.addHandlerContext(HandlerContextKey.ADVANCED_CONFIG, this.advancedConfig);
            }
            finally {
                awsRequestMetrics.endEvent(AWSRequestMetrics.Field.RequestMarshallTime);
            }
            HttpResponseHandler responseHandler = protocolFactory.createResponseHandler(new JsonOperationMetadata().withPayloadJson(true).withHasStreamingSuccessResponse(false), new GetKeyRotationStatusResultJsonUnmarshaller());
            response = this.invoke(request, responseHandler, executionContext);
            GetKeyRotationStatusResult getKeyRotationStatusResult = (GetKeyRotationStatusResult)response.getAwsResponse();
            return getKeyRotationStatusResult;
        }
        finally {
            this.endClientExecution(awsRequestMetrics, request, response);
        }
    }

    @Override
    public GetParametersForImportResult getParametersForImport(GetParametersForImportRequest request) {
        request = this.beforeClientExecution(request);
        return this.executeGetParametersForImport(request);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @SdkInternalApi
    final GetParametersForImportResult executeGetParametersForImport(GetParametersForImportRequest getParametersForImportRequest) {
        ExecutionContext executionContext = this.createExecutionContext(getParametersForImportRequest);
        AWSRequestMetrics awsRequestMetrics = executionContext.getAwsRequestMetrics();
        awsRequestMetrics.startEvent(AWSRequestMetrics.Field.ClientExecuteTime);
        Request<GetParametersForImportRequest> request = null;
        Response response = null;
        try {
            awsRequestMetrics.startEvent(AWSRequestMetrics.Field.RequestMarshallTime);
            try {
                request = new GetParametersForImportRequestProtocolMarshaller(protocolFactory).marshall(super.beforeMarshalling(getParametersForImportRequest));
                request.setAWSRequestMetrics(awsRequestMetrics);
                request.addHandlerContext(HandlerContextKey.CLIENT_ENDPOINT, this.endpoint);
                request.addHandlerContext(HandlerContextKey.ENDPOINT_OVERRIDDEN, this.isEndpointOverridden());
                request.addHandlerContext(HandlerContextKey.SIGNING_REGION, this.getSigningRegion());
                request.addHandlerContext(HandlerContextKey.SERVICE_ID, "KMS");
                request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "GetParametersForImport");
                request.addHandlerContext(HandlerContextKey.ADVANCED_CONFIG, this.advancedConfig);
            }
            finally {
                awsRequestMetrics.endEvent(AWSRequestMetrics.Field.RequestMarshallTime);
            }
            HttpResponseHandler responseHandler = protocolFactory.createResponseHandler(new JsonOperationMetadata().withPayloadJson(true).withHasStreamingSuccessResponse(false), new GetParametersForImportResultJsonUnmarshaller());
            response = this.invoke(request, responseHandler, executionContext);
            GetParametersForImportResult getParametersForImportResult = (GetParametersForImportResult)response.getAwsResponse();
            return getParametersForImportResult;
        }
        finally {
            this.endClientExecution(awsRequestMetrics, request, response);
        }
    }

    @Override
    public GetPublicKeyResult getPublicKey(GetPublicKeyRequest request) {
        request = this.beforeClientExecution(request);
        return this.executeGetPublicKey(request);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @SdkInternalApi
    final GetPublicKeyResult executeGetPublicKey(GetPublicKeyRequest getPublicKeyRequest) {
        ExecutionContext executionContext = this.createExecutionContext(getPublicKeyRequest);
        AWSRequestMetrics awsRequestMetrics = executionContext.getAwsRequestMetrics();
        awsRequestMetrics.startEvent(AWSRequestMetrics.Field.ClientExecuteTime);
        Request<GetPublicKeyRequest> request = null;
        Response response = null;
        try {
            awsRequestMetrics.startEvent(AWSRequestMetrics.Field.RequestMarshallTime);
            try {
                request = new GetPublicKeyRequestProtocolMarshaller(protocolFactory).marshall(super.beforeMarshalling(getPublicKeyRequest));
                request.setAWSRequestMetrics(awsRequestMetrics);
                request.addHandlerContext(HandlerContextKey.CLIENT_ENDPOINT, this.endpoint);
                request.addHandlerContext(HandlerContextKey.ENDPOINT_OVERRIDDEN, this.isEndpointOverridden());
                request.addHandlerContext(HandlerContextKey.SIGNING_REGION, this.getSigningRegion());
                request.addHandlerContext(HandlerContextKey.SERVICE_ID, "KMS");
                request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "GetPublicKey");
                request.addHandlerContext(HandlerContextKey.ADVANCED_CONFIG, this.advancedConfig);
            }
            finally {
                awsRequestMetrics.endEvent(AWSRequestMetrics.Field.RequestMarshallTime);
            }
            HttpResponseHandler responseHandler = protocolFactory.createResponseHandler(new JsonOperationMetadata().withPayloadJson(true).withHasStreamingSuccessResponse(false), new GetPublicKeyResultJsonUnmarshaller());
            response = this.invoke(request, responseHandler, executionContext);
            GetPublicKeyResult getPublicKeyResult = (GetPublicKeyResult)response.getAwsResponse();
            return getPublicKeyResult;
        }
        finally {
            this.endClientExecution(awsRequestMetrics, request, response);
        }
    }

    @Override
    public ImportKeyMaterialResult importKeyMaterial(ImportKeyMaterialRequest request) {
        request = this.beforeClientExecution(request);
        return this.executeImportKeyMaterial(request);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @SdkInternalApi
    final ImportKeyMaterialResult executeImportKeyMaterial(ImportKeyMaterialRequest importKeyMaterialRequest) {
        ExecutionContext executionContext = this.createExecutionContext(importKeyMaterialRequest);
        AWSRequestMetrics awsRequestMetrics = executionContext.getAwsRequestMetrics();
        awsRequestMetrics.startEvent(AWSRequestMetrics.Field.ClientExecuteTime);
        Request<ImportKeyMaterialRequest> request = null;
        Response response = null;
        try {
            awsRequestMetrics.startEvent(AWSRequestMetrics.Field.RequestMarshallTime);
            try {
                request = new ImportKeyMaterialRequestProtocolMarshaller(protocolFactory).marshall(super.beforeMarshalling(importKeyMaterialRequest));
                request.setAWSRequestMetrics(awsRequestMetrics);
                request.addHandlerContext(HandlerContextKey.CLIENT_ENDPOINT, this.endpoint);
                request.addHandlerContext(HandlerContextKey.ENDPOINT_OVERRIDDEN, this.isEndpointOverridden());
                request.addHandlerContext(HandlerContextKey.SIGNING_REGION, this.getSigningRegion());
                request.addHandlerContext(HandlerContextKey.SERVICE_ID, "KMS");
                request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "ImportKeyMaterial");
                request.addHandlerContext(HandlerContextKey.ADVANCED_CONFIG, this.advancedConfig);
            }
            finally {
                awsRequestMetrics.endEvent(AWSRequestMetrics.Field.RequestMarshallTime);
            }
            HttpResponseHandler responseHandler = protocolFactory.createResponseHandler(new JsonOperationMetadata().withPayloadJson(true).withHasStreamingSuccessResponse(false), new ImportKeyMaterialResultJsonUnmarshaller());
            response = this.invoke(request, responseHandler, executionContext);
            ImportKeyMaterialResult importKeyMaterialResult = (ImportKeyMaterialResult)response.getAwsResponse();
            return importKeyMaterialResult;
        }
        finally {
            this.endClientExecution(awsRequestMetrics, request, response);
        }
    }

    @Override
    public ListAliasesResult listAliases(ListAliasesRequest request) {
        request = this.beforeClientExecution(request);
        return this.executeListAliases(request);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @SdkInternalApi
    final ListAliasesResult executeListAliases(ListAliasesRequest listAliasesRequest) {
        ExecutionContext executionContext = this.createExecutionContext(listAliasesRequest);
        AWSRequestMetrics awsRequestMetrics = executionContext.getAwsRequestMetrics();
        awsRequestMetrics.startEvent(AWSRequestMetrics.Field.ClientExecuteTime);
        Request<ListAliasesRequest> request = null;
        Response response = null;
        try {
            awsRequestMetrics.startEvent(AWSRequestMetrics.Field.RequestMarshallTime);
            try {
                request = new ListAliasesRequestProtocolMarshaller(protocolFactory).marshall(super.beforeMarshalling(listAliasesRequest));
                request.setAWSRequestMetrics(awsRequestMetrics);
                request.addHandlerContext(HandlerContextKey.CLIENT_ENDPOINT, this.endpoint);
                request.addHandlerContext(HandlerContextKey.ENDPOINT_OVERRIDDEN, this.isEndpointOverridden());
                request.addHandlerContext(HandlerContextKey.SIGNING_REGION, this.getSigningRegion());
                request.addHandlerContext(HandlerContextKey.SERVICE_ID, "KMS");
                request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "ListAliases");
                request.addHandlerContext(HandlerContextKey.ADVANCED_CONFIG, this.advancedConfig);
            }
            finally {
                awsRequestMetrics.endEvent(AWSRequestMetrics.Field.RequestMarshallTime);
            }
            HttpResponseHandler responseHandler = protocolFactory.createResponseHandler(new JsonOperationMetadata().withPayloadJson(true).withHasStreamingSuccessResponse(false), new ListAliasesResultJsonUnmarshaller());
            response = this.invoke(request, responseHandler, executionContext);
            ListAliasesResult listAliasesResult = (ListAliasesResult)response.getAwsResponse();
            return listAliasesResult;
        }
        finally {
            this.endClientExecution(awsRequestMetrics, request, response);
        }
    }

    @Override
    public ListAliasesResult listAliases() {
        return this.listAliases(new ListAliasesRequest());
    }

    @Override
    public ListGrantsResult listGrants(ListGrantsRequest request) {
        request = this.beforeClientExecution(request);
        return this.executeListGrants(request);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @SdkInternalApi
    final ListGrantsResult executeListGrants(ListGrantsRequest listGrantsRequest) {
        ExecutionContext executionContext = this.createExecutionContext(listGrantsRequest);
        AWSRequestMetrics awsRequestMetrics = executionContext.getAwsRequestMetrics();
        awsRequestMetrics.startEvent(AWSRequestMetrics.Field.ClientExecuteTime);
        Request<ListGrantsRequest> request = null;
        Response response = null;
        try {
            awsRequestMetrics.startEvent(AWSRequestMetrics.Field.RequestMarshallTime);
            try {
                request = new ListGrantsRequestProtocolMarshaller(protocolFactory).marshall(super.beforeMarshalling(listGrantsRequest));
                request.setAWSRequestMetrics(awsRequestMetrics);
                request.addHandlerContext(HandlerContextKey.CLIENT_ENDPOINT, this.endpoint);
                request.addHandlerContext(HandlerContextKey.ENDPOINT_OVERRIDDEN, this.isEndpointOverridden());
                request.addHandlerContext(HandlerContextKey.SIGNING_REGION, this.getSigningRegion());
                request.addHandlerContext(HandlerContextKey.SERVICE_ID, "KMS");
                request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "ListGrants");
                request.addHandlerContext(HandlerContextKey.ADVANCED_CONFIG, this.advancedConfig);
            }
            finally {
                awsRequestMetrics.endEvent(AWSRequestMetrics.Field.RequestMarshallTime);
            }
            HttpResponseHandler responseHandler = protocolFactory.createResponseHandler(new JsonOperationMetadata().withPayloadJson(true).withHasStreamingSuccessResponse(false), new ListGrantsResultJsonUnmarshaller());
            response = this.invoke(request, responseHandler, executionContext);
            ListGrantsResult listGrantsResult = (ListGrantsResult)response.getAwsResponse();
            return listGrantsResult;
        }
        finally {
            this.endClientExecution(awsRequestMetrics, request, response);
        }
    }

    @Override
    public ListKeyPoliciesResult listKeyPolicies(ListKeyPoliciesRequest request) {
        request = this.beforeClientExecution(request);
        return this.executeListKeyPolicies(request);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @SdkInternalApi
    final ListKeyPoliciesResult executeListKeyPolicies(ListKeyPoliciesRequest listKeyPoliciesRequest) {
        ExecutionContext executionContext = this.createExecutionContext(listKeyPoliciesRequest);
        AWSRequestMetrics awsRequestMetrics = executionContext.getAwsRequestMetrics();
        awsRequestMetrics.startEvent(AWSRequestMetrics.Field.ClientExecuteTime);
        Request<ListKeyPoliciesRequest> request = null;
        Response response = null;
        try {
            awsRequestMetrics.startEvent(AWSRequestMetrics.Field.RequestMarshallTime);
            try {
                request = new ListKeyPoliciesRequestProtocolMarshaller(protocolFactory).marshall(super.beforeMarshalling(listKeyPoliciesRequest));
                request.setAWSRequestMetrics(awsRequestMetrics);
                request.addHandlerContext(HandlerContextKey.CLIENT_ENDPOINT, this.endpoint);
                request.addHandlerContext(HandlerContextKey.ENDPOINT_OVERRIDDEN, this.isEndpointOverridden());
                request.addHandlerContext(HandlerContextKey.SIGNING_REGION, this.getSigningRegion());
                request.addHandlerContext(HandlerContextKey.SERVICE_ID, "KMS");
                request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "ListKeyPolicies");
                request.addHandlerContext(HandlerContextKey.ADVANCED_CONFIG, this.advancedConfig);
            }
            finally {
                awsRequestMetrics.endEvent(AWSRequestMetrics.Field.RequestMarshallTime);
            }
            HttpResponseHandler responseHandler = protocolFactory.createResponseHandler(new JsonOperationMetadata().withPayloadJson(true).withHasStreamingSuccessResponse(false), new ListKeyPoliciesResultJsonUnmarshaller());
            response = this.invoke(request, responseHandler, executionContext);
            ListKeyPoliciesResult listKeyPoliciesResult = (ListKeyPoliciesResult)response.getAwsResponse();
            return listKeyPoliciesResult;
        }
        finally {
            this.endClientExecution(awsRequestMetrics, request, response);
        }
    }

    @Override
    public ListKeysResult listKeys(ListKeysRequest request) {
        request = this.beforeClientExecution(request);
        return this.executeListKeys(request);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @SdkInternalApi
    final ListKeysResult executeListKeys(ListKeysRequest listKeysRequest) {
        ExecutionContext executionContext = this.createExecutionContext(listKeysRequest);
        AWSRequestMetrics awsRequestMetrics = executionContext.getAwsRequestMetrics();
        awsRequestMetrics.startEvent(AWSRequestMetrics.Field.ClientExecuteTime);
        Request<ListKeysRequest> request = null;
        Response response = null;
        try {
            awsRequestMetrics.startEvent(AWSRequestMetrics.Field.RequestMarshallTime);
            try {
                request = new ListKeysRequestProtocolMarshaller(protocolFactory).marshall(super.beforeMarshalling(listKeysRequest));
                request.setAWSRequestMetrics(awsRequestMetrics);
                request.addHandlerContext(HandlerContextKey.CLIENT_ENDPOINT, this.endpoint);
                request.addHandlerContext(HandlerContextKey.ENDPOINT_OVERRIDDEN, this.isEndpointOverridden());
                request.addHandlerContext(HandlerContextKey.SIGNING_REGION, this.getSigningRegion());
                request.addHandlerContext(HandlerContextKey.SERVICE_ID, "KMS");
                request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "ListKeys");
                request.addHandlerContext(HandlerContextKey.ADVANCED_CONFIG, this.advancedConfig);
            }
            finally {
                awsRequestMetrics.endEvent(AWSRequestMetrics.Field.RequestMarshallTime);
            }
            HttpResponseHandler responseHandler = protocolFactory.createResponseHandler(new JsonOperationMetadata().withPayloadJson(true).withHasStreamingSuccessResponse(false), new ListKeysResultJsonUnmarshaller());
            response = this.invoke(request, responseHandler, executionContext);
            ListKeysResult listKeysResult = (ListKeysResult)response.getAwsResponse();
            return listKeysResult;
        }
        finally {
            this.endClientExecution(awsRequestMetrics, request, response);
        }
    }

    @Override
    public ListKeysResult listKeys() {
        return this.listKeys(new ListKeysRequest());
    }

    @Override
    public ListResourceTagsResult listResourceTags(ListResourceTagsRequest request) {
        request = this.beforeClientExecution(request);
        return this.executeListResourceTags(request);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @SdkInternalApi
    final ListResourceTagsResult executeListResourceTags(ListResourceTagsRequest listResourceTagsRequest) {
        ExecutionContext executionContext = this.createExecutionContext(listResourceTagsRequest);
        AWSRequestMetrics awsRequestMetrics = executionContext.getAwsRequestMetrics();
        awsRequestMetrics.startEvent(AWSRequestMetrics.Field.ClientExecuteTime);
        Request<ListResourceTagsRequest> request = null;
        Response response = null;
        try {
            awsRequestMetrics.startEvent(AWSRequestMetrics.Field.RequestMarshallTime);
            try {
                request = new ListResourceTagsRequestProtocolMarshaller(protocolFactory).marshall(super.beforeMarshalling(listResourceTagsRequest));
                request.setAWSRequestMetrics(awsRequestMetrics);
                request.addHandlerContext(HandlerContextKey.CLIENT_ENDPOINT, this.endpoint);
                request.addHandlerContext(HandlerContextKey.ENDPOINT_OVERRIDDEN, this.isEndpointOverridden());
                request.addHandlerContext(HandlerContextKey.SIGNING_REGION, this.getSigningRegion());
                request.addHandlerContext(HandlerContextKey.SERVICE_ID, "KMS");
                request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "ListResourceTags");
                request.addHandlerContext(HandlerContextKey.ADVANCED_CONFIG, this.advancedConfig);
            }
            finally {
                awsRequestMetrics.endEvent(AWSRequestMetrics.Field.RequestMarshallTime);
            }
            HttpResponseHandler responseHandler = protocolFactory.createResponseHandler(new JsonOperationMetadata().withPayloadJson(true).withHasStreamingSuccessResponse(false), new ListResourceTagsResultJsonUnmarshaller());
            response = this.invoke(request, responseHandler, executionContext);
            ListResourceTagsResult listResourceTagsResult = (ListResourceTagsResult)response.getAwsResponse();
            return listResourceTagsResult;
        }
        finally {
            this.endClientExecution(awsRequestMetrics, request, response);
        }
    }

    @Override
    public ListRetirableGrantsResult listRetirableGrants(ListRetirableGrantsRequest request) {
        request = this.beforeClientExecution(request);
        return this.executeListRetirableGrants(request);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @SdkInternalApi
    final ListRetirableGrantsResult executeListRetirableGrants(ListRetirableGrantsRequest listRetirableGrantsRequest) {
        ExecutionContext executionContext = this.createExecutionContext(listRetirableGrantsRequest);
        AWSRequestMetrics awsRequestMetrics = executionContext.getAwsRequestMetrics();
        awsRequestMetrics.startEvent(AWSRequestMetrics.Field.ClientExecuteTime);
        Request<ListRetirableGrantsRequest> request = null;
        Response response = null;
        try {
            awsRequestMetrics.startEvent(AWSRequestMetrics.Field.RequestMarshallTime);
            try {
                request = new ListRetirableGrantsRequestProtocolMarshaller(protocolFactory).marshall(super.beforeMarshalling(listRetirableGrantsRequest));
                request.setAWSRequestMetrics(awsRequestMetrics);
                request.addHandlerContext(HandlerContextKey.CLIENT_ENDPOINT, this.endpoint);
                request.addHandlerContext(HandlerContextKey.ENDPOINT_OVERRIDDEN, this.isEndpointOverridden());
                request.addHandlerContext(HandlerContextKey.SIGNING_REGION, this.getSigningRegion());
                request.addHandlerContext(HandlerContextKey.SERVICE_ID, "KMS");
                request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "ListRetirableGrants");
                request.addHandlerContext(HandlerContextKey.ADVANCED_CONFIG, this.advancedConfig);
            }
            finally {
                awsRequestMetrics.endEvent(AWSRequestMetrics.Field.RequestMarshallTime);
            }
            HttpResponseHandler responseHandler = protocolFactory.createResponseHandler(new JsonOperationMetadata().withPayloadJson(true).withHasStreamingSuccessResponse(false), new ListRetirableGrantsResultJsonUnmarshaller());
            response = this.invoke(request, responseHandler, executionContext);
            ListRetirableGrantsResult listRetirableGrantsResult = (ListRetirableGrantsResult)response.getAwsResponse();
            return listRetirableGrantsResult;
        }
        finally {
            this.endClientExecution(awsRequestMetrics, request, response);
        }
    }

    @Override
    public PutKeyPolicyResult putKeyPolicy(PutKeyPolicyRequest request) {
        request = this.beforeClientExecution(request);
        return this.executePutKeyPolicy(request);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @SdkInternalApi
    final PutKeyPolicyResult executePutKeyPolicy(PutKeyPolicyRequest putKeyPolicyRequest) {
        ExecutionContext executionContext = this.createExecutionContext(putKeyPolicyRequest);
        AWSRequestMetrics awsRequestMetrics = executionContext.getAwsRequestMetrics();
        awsRequestMetrics.startEvent(AWSRequestMetrics.Field.ClientExecuteTime);
        Request<PutKeyPolicyRequest> request = null;
        Response response = null;
        try {
            awsRequestMetrics.startEvent(AWSRequestMetrics.Field.RequestMarshallTime);
            try {
                request = new PutKeyPolicyRequestProtocolMarshaller(protocolFactory).marshall(super.beforeMarshalling(putKeyPolicyRequest));
                request.setAWSRequestMetrics(awsRequestMetrics);
                request.addHandlerContext(HandlerContextKey.CLIENT_ENDPOINT, this.endpoint);
                request.addHandlerContext(HandlerContextKey.ENDPOINT_OVERRIDDEN, this.isEndpointOverridden());
                request.addHandlerContext(HandlerContextKey.SIGNING_REGION, this.getSigningRegion());
                request.addHandlerContext(HandlerContextKey.SERVICE_ID, "KMS");
                request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "PutKeyPolicy");
                request.addHandlerContext(HandlerContextKey.ADVANCED_CONFIG, this.advancedConfig);
            }
            finally {
                awsRequestMetrics.endEvent(AWSRequestMetrics.Field.RequestMarshallTime);
            }
            HttpResponseHandler responseHandler = protocolFactory.createResponseHandler(new JsonOperationMetadata().withPayloadJson(true).withHasStreamingSuccessResponse(false), new PutKeyPolicyResultJsonUnmarshaller());
            response = this.invoke(request, responseHandler, executionContext);
            PutKeyPolicyResult putKeyPolicyResult = (PutKeyPolicyResult)response.getAwsResponse();
            return putKeyPolicyResult;
        }
        finally {
            this.endClientExecution(awsRequestMetrics, request, response);
        }
    }

    @Override
    public ReEncryptResult reEncrypt(ReEncryptRequest request) {
        request = this.beforeClientExecution(request);
        return this.executeReEncrypt(request);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @SdkInternalApi
    final ReEncryptResult executeReEncrypt(ReEncryptRequest reEncryptRequest) {
        ExecutionContext executionContext = this.createExecutionContext(reEncryptRequest);
        AWSRequestMetrics awsRequestMetrics = executionContext.getAwsRequestMetrics();
        awsRequestMetrics.startEvent(AWSRequestMetrics.Field.ClientExecuteTime);
        Request<ReEncryptRequest> request = null;
        Response response = null;
        try {
            awsRequestMetrics.startEvent(AWSRequestMetrics.Field.RequestMarshallTime);
            try {
                request = new ReEncryptRequestProtocolMarshaller(protocolFactory).marshall(super.beforeMarshalling(reEncryptRequest));
                request.setAWSRequestMetrics(awsRequestMetrics);
                request.addHandlerContext(HandlerContextKey.CLIENT_ENDPOINT, this.endpoint);
                request.addHandlerContext(HandlerContextKey.ENDPOINT_OVERRIDDEN, this.isEndpointOverridden());
                request.addHandlerContext(HandlerContextKey.SIGNING_REGION, this.getSigningRegion());
                request.addHandlerContext(HandlerContextKey.SERVICE_ID, "KMS");
                request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "ReEncrypt");
                request.addHandlerContext(HandlerContextKey.ADVANCED_CONFIG, this.advancedConfig);
            }
            finally {
                awsRequestMetrics.endEvent(AWSRequestMetrics.Field.RequestMarshallTime);
            }
            HttpResponseHandler responseHandler = protocolFactory.createResponseHandler(new JsonOperationMetadata().withPayloadJson(true).withHasStreamingSuccessResponse(false), new ReEncryptResultJsonUnmarshaller());
            response = this.invoke(request, responseHandler, executionContext);
            ReEncryptResult reEncryptResult = (ReEncryptResult)response.getAwsResponse();
            return reEncryptResult;
        }
        finally {
            this.endClientExecution(awsRequestMetrics, request, response);
        }
    }

    @Override
    public ReplicateKeyResult replicateKey(ReplicateKeyRequest request) {
        request = this.beforeClientExecution(request);
        return this.executeReplicateKey(request);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @SdkInternalApi
    final ReplicateKeyResult executeReplicateKey(ReplicateKeyRequest replicateKeyRequest) {
        ExecutionContext executionContext = this.createExecutionContext(replicateKeyRequest);
        AWSRequestMetrics awsRequestMetrics = executionContext.getAwsRequestMetrics();
        awsRequestMetrics.startEvent(AWSRequestMetrics.Field.ClientExecuteTime);
        Request<ReplicateKeyRequest> request = null;
        Response response = null;
        try {
            awsRequestMetrics.startEvent(AWSRequestMetrics.Field.RequestMarshallTime);
            try {
                request = new ReplicateKeyRequestProtocolMarshaller(protocolFactory).marshall(super.beforeMarshalling(replicateKeyRequest));
                request.setAWSRequestMetrics(awsRequestMetrics);
                request.addHandlerContext(HandlerContextKey.CLIENT_ENDPOINT, this.endpoint);
                request.addHandlerContext(HandlerContextKey.ENDPOINT_OVERRIDDEN, this.isEndpointOverridden());
                request.addHandlerContext(HandlerContextKey.SIGNING_REGION, this.getSigningRegion());
                request.addHandlerContext(HandlerContextKey.SERVICE_ID, "KMS");
                request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "ReplicateKey");
                request.addHandlerContext(HandlerContextKey.ADVANCED_CONFIG, this.advancedConfig);
            }
            finally {
                awsRequestMetrics.endEvent(AWSRequestMetrics.Field.RequestMarshallTime);
            }
            HttpResponseHandler responseHandler = protocolFactory.createResponseHandler(new JsonOperationMetadata().withPayloadJson(true).withHasStreamingSuccessResponse(false), new ReplicateKeyResultJsonUnmarshaller());
            response = this.invoke(request, responseHandler, executionContext);
            ReplicateKeyResult replicateKeyResult = (ReplicateKeyResult)response.getAwsResponse();
            return replicateKeyResult;
        }
        finally {
            this.endClientExecution(awsRequestMetrics, request, response);
        }
    }

    @Override
    public RetireGrantResult retireGrant(RetireGrantRequest request) {
        request = this.beforeClientExecution(request);
        return this.executeRetireGrant(request);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @SdkInternalApi
    final RetireGrantResult executeRetireGrant(RetireGrantRequest retireGrantRequest) {
        ExecutionContext executionContext = this.createExecutionContext(retireGrantRequest);
        AWSRequestMetrics awsRequestMetrics = executionContext.getAwsRequestMetrics();
        awsRequestMetrics.startEvent(AWSRequestMetrics.Field.ClientExecuteTime);
        Request<RetireGrantRequest> request = null;
        Response response = null;
        try {
            awsRequestMetrics.startEvent(AWSRequestMetrics.Field.RequestMarshallTime);
            try {
                request = new RetireGrantRequestProtocolMarshaller(protocolFactory).marshall(super.beforeMarshalling(retireGrantRequest));
                request.setAWSRequestMetrics(awsRequestMetrics);
                request.addHandlerContext(HandlerContextKey.CLIENT_ENDPOINT, this.endpoint);
                request.addHandlerContext(HandlerContextKey.ENDPOINT_OVERRIDDEN, this.isEndpointOverridden());
                request.addHandlerContext(HandlerContextKey.SIGNING_REGION, this.getSigningRegion());
                request.addHandlerContext(HandlerContextKey.SERVICE_ID, "KMS");
                request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "RetireGrant");
                request.addHandlerContext(HandlerContextKey.ADVANCED_CONFIG, this.advancedConfig);
            }
            finally {
                awsRequestMetrics.endEvent(AWSRequestMetrics.Field.RequestMarshallTime);
            }
            HttpResponseHandler responseHandler = protocolFactory.createResponseHandler(new JsonOperationMetadata().withPayloadJson(true).withHasStreamingSuccessResponse(false), new RetireGrantResultJsonUnmarshaller());
            response = this.invoke(request, responseHandler, executionContext);
            RetireGrantResult retireGrantResult = (RetireGrantResult)response.getAwsResponse();
            return retireGrantResult;
        }
        finally {
            this.endClientExecution(awsRequestMetrics, request, response);
        }
    }

    @Override
    public RetireGrantResult retireGrant() {
        return this.retireGrant(new RetireGrantRequest());
    }

    @Override
    public RevokeGrantResult revokeGrant(RevokeGrantRequest request) {
        request = this.beforeClientExecution(request);
        return this.executeRevokeGrant(request);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @SdkInternalApi
    final RevokeGrantResult executeRevokeGrant(RevokeGrantRequest revokeGrantRequest) {
        ExecutionContext executionContext = this.createExecutionContext(revokeGrantRequest);
        AWSRequestMetrics awsRequestMetrics = executionContext.getAwsRequestMetrics();
        awsRequestMetrics.startEvent(AWSRequestMetrics.Field.ClientExecuteTime);
        Request<RevokeGrantRequest> request = null;
        Response response = null;
        try {
            awsRequestMetrics.startEvent(AWSRequestMetrics.Field.RequestMarshallTime);
            try {
                request = new RevokeGrantRequestProtocolMarshaller(protocolFactory).marshall(super.beforeMarshalling(revokeGrantRequest));
                request.setAWSRequestMetrics(awsRequestMetrics);
                request.addHandlerContext(HandlerContextKey.CLIENT_ENDPOINT, this.endpoint);
                request.addHandlerContext(HandlerContextKey.ENDPOINT_OVERRIDDEN, this.isEndpointOverridden());
                request.addHandlerContext(HandlerContextKey.SIGNING_REGION, this.getSigningRegion());
                request.addHandlerContext(HandlerContextKey.SERVICE_ID, "KMS");
                request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "RevokeGrant");
                request.addHandlerContext(HandlerContextKey.ADVANCED_CONFIG, this.advancedConfig);
            }
            finally {
                awsRequestMetrics.endEvent(AWSRequestMetrics.Field.RequestMarshallTime);
            }
            HttpResponseHandler responseHandler = protocolFactory.createResponseHandler(new JsonOperationMetadata().withPayloadJson(true).withHasStreamingSuccessResponse(false), new RevokeGrantResultJsonUnmarshaller());
            response = this.invoke(request, responseHandler, executionContext);
            RevokeGrantResult revokeGrantResult = (RevokeGrantResult)response.getAwsResponse();
            return revokeGrantResult;
        }
        finally {
            this.endClientExecution(awsRequestMetrics, request, response);
        }
    }

    @Override
    public ScheduleKeyDeletionResult scheduleKeyDeletion(ScheduleKeyDeletionRequest request) {
        request = this.beforeClientExecution(request);
        return this.executeScheduleKeyDeletion(request);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @SdkInternalApi
    final ScheduleKeyDeletionResult executeScheduleKeyDeletion(ScheduleKeyDeletionRequest scheduleKeyDeletionRequest) {
        ExecutionContext executionContext = this.createExecutionContext(scheduleKeyDeletionRequest);
        AWSRequestMetrics awsRequestMetrics = executionContext.getAwsRequestMetrics();
        awsRequestMetrics.startEvent(AWSRequestMetrics.Field.ClientExecuteTime);
        Request<ScheduleKeyDeletionRequest> request = null;
        Response response = null;
        try {
            awsRequestMetrics.startEvent(AWSRequestMetrics.Field.RequestMarshallTime);
            try {
                request = new ScheduleKeyDeletionRequestProtocolMarshaller(protocolFactory).marshall(super.beforeMarshalling(scheduleKeyDeletionRequest));
                request.setAWSRequestMetrics(awsRequestMetrics);
                request.addHandlerContext(HandlerContextKey.CLIENT_ENDPOINT, this.endpoint);
                request.addHandlerContext(HandlerContextKey.ENDPOINT_OVERRIDDEN, this.isEndpointOverridden());
                request.addHandlerContext(HandlerContextKey.SIGNING_REGION, this.getSigningRegion());
                request.addHandlerContext(HandlerContextKey.SERVICE_ID, "KMS");
                request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "ScheduleKeyDeletion");
                request.addHandlerContext(HandlerContextKey.ADVANCED_CONFIG, this.advancedConfig);
            }
            finally {
                awsRequestMetrics.endEvent(AWSRequestMetrics.Field.RequestMarshallTime);
            }
            HttpResponseHandler responseHandler = protocolFactory.createResponseHandler(new JsonOperationMetadata().withPayloadJson(true).withHasStreamingSuccessResponse(false), new ScheduleKeyDeletionResultJsonUnmarshaller());
            response = this.invoke(request, responseHandler, executionContext);
            ScheduleKeyDeletionResult scheduleKeyDeletionResult = (ScheduleKeyDeletionResult)response.getAwsResponse();
            return scheduleKeyDeletionResult;
        }
        finally {
            this.endClientExecution(awsRequestMetrics, request, response);
        }
    }

    @Override
    public SignResult sign(SignRequest request) {
        request = this.beforeClientExecution(request);
        return this.executeSign(request);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @SdkInternalApi
    final SignResult executeSign(SignRequest signRequest) {
        ExecutionContext executionContext = this.createExecutionContext(signRequest);
        AWSRequestMetrics awsRequestMetrics = executionContext.getAwsRequestMetrics();
        awsRequestMetrics.startEvent(AWSRequestMetrics.Field.ClientExecuteTime);
        Request<SignRequest> request = null;
        Response response = null;
        try {
            awsRequestMetrics.startEvent(AWSRequestMetrics.Field.RequestMarshallTime);
            try {
                request = new SignRequestProtocolMarshaller(protocolFactory).marshall(super.beforeMarshalling(signRequest));
                request.setAWSRequestMetrics(awsRequestMetrics);
                request.addHandlerContext(HandlerContextKey.CLIENT_ENDPOINT, this.endpoint);
                request.addHandlerContext(HandlerContextKey.ENDPOINT_OVERRIDDEN, this.isEndpointOverridden());
                request.addHandlerContext(HandlerContextKey.SIGNING_REGION, this.getSigningRegion());
                request.addHandlerContext(HandlerContextKey.SERVICE_ID, "KMS");
                request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "Sign");
                request.addHandlerContext(HandlerContextKey.ADVANCED_CONFIG, this.advancedConfig);
            }
            finally {
                awsRequestMetrics.endEvent(AWSRequestMetrics.Field.RequestMarshallTime);
            }
            HttpResponseHandler responseHandler = protocolFactory.createResponseHandler(new JsonOperationMetadata().withPayloadJson(true).withHasStreamingSuccessResponse(false), new SignResultJsonUnmarshaller());
            response = this.invoke(request, responseHandler, executionContext);
            SignResult signResult = (SignResult)response.getAwsResponse();
            return signResult;
        }
        finally {
            this.endClientExecution(awsRequestMetrics, request, response);
        }
    }

    @Override
    public TagResourceResult tagResource(TagResourceRequest request) {
        request = this.beforeClientExecution(request);
        return this.executeTagResource(request);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @SdkInternalApi
    final TagResourceResult executeTagResource(TagResourceRequest tagResourceRequest) {
        ExecutionContext executionContext = this.createExecutionContext(tagResourceRequest);
        AWSRequestMetrics awsRequestMetrics = executionContext.getAwsRequestMetrics();
        awsRequestMetrics.startEvent(AWSRequestMetrics.Field.ClientExecuteTime);
        Request<TagResourceRequest> request = null;
        Response response = null;
        try {
            awsRequestMetrics.startEvent(AWSRequestMetrics.Field.RequestMarshallTime);
            try {
                request = new TagResourceRequestProtocolMarshaller(protocolFactory).marshall(super.beforeMarshalling(tagResourceRequest));
                request.setAWSRequestMetrics(awsRequestMetrics);
                request.addHandlerContext(HandlerContextKey.CLIENT_ENDPOINT, this.endpoint);
                request.addHandlerContext(HandlerContextKey.ENDPOINT_OVERRIDDEN, this.isEndpointOverridden());
                request.addHandlerContext(HandlerContextKey.SIGNING_REGION, this.getSigningRegion());
                request.addHandlerContext(HandlerContextKey.SERVICE_ID, "KMS");
                request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "TagResource");
                request.addHandlerContext(HandlerContextKey.ADVANCED_CONFIG, this.advancedConfig);
            }
            finally {
                awsRequestMetrics.endEvent(AWSRequestMetrics.Field.RequestMarshallTime);
            }
            HttpResponseHandler responseHandler = protocolFactory.createResponseHandler(new JsonOperationMetadata().withPayloadJson(true).withHasStreamingSuccessResponse(false), new TagResourceResultJsonUnmarshaller());
            response = this.invoke(request, responseHandler, executionContext);
            TagResourceResult tagResourceResult = (TagResourceResult)response.getAwsResponse();
            return tagResourceResult;
        }
        finally {
            this.endClientExecution(awsRequestMetrics, request, response);
        }
    }

    @Override
    public UntagResourceResult untagResource(UntagResourceRequest request) {
        request = this.beforeClientExecution(request);
        return this.executeUntagResource(request);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @SdkInternalApi
    final UntagResourceResult executeUntagResource(UntagResourceRequest untagResourceRequest) {
        ExecutionContext executionContext = this.createExecutionContext(untagResourceRequest);
        AWSRequestMetrics awsRequestMetrics = executionContext.getAwsRequestMetrics();
        awsRequestMetrics.startEvent(AWSRequestMetrics.Field.ClientExecuteTime);
        Request<UntagResourceRequest> request = null;
        Response response = null;
        try {
            awsRequestMetrics.startEvent(AWSRequestMetrics.Field.RequestMarshallTime);
            try {
                request = new UntagResourceRequestProtocolMarshaller(protocolFactory).marshall(super.beforeMarshalling(untagResourceRequest));
                request.setAWSRequestMetrics(awsRequestMetrics);
                request.addHandlerContext(HandlerContextKey.CLIENT_ENDPOINT, this.endpoint);
                request.addHandlerContext(HandlerContextKey.ENDPOINT_OVERRIDDEN, this.isEndpointOverridden());
                request.addHandlerContext(HandlerContextKey.SIGNING_REGION, this.getSigningRegion());
                request.addHandlerContext(HandlerContextKey.SERVICE_ID, "KMS");
                request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "UntagResource");
                request.addHandlerContext(HandlerContextKey.ADVANCED_CONFIG, this.advancedConfig);
            }
            finally {
                awsRequestMetrics.endEvent(AWSRequestMetrics.Field.RequestMarshallTime);
            }
            HttpResponseHandler responseHandler = protocolFactory.createResponseHandler(new JsonOperationMetadata().withPayloadJson(true).withHasStreamingSuccessResponse(false), new UntagResourceResultJsonUnmarshaller());
            response = this.invoke(request, responseHandler, executionContext);
            UntagResourceResult untagResourceResult = (UntagResourceResult)response.getAwsResponse();
            return untagResourceResult;
        }
        finally {
            this.endClientExecution(awsRequestMetrics, request, response);
        }
    }

    @Override
    public UpdateAliasResult updateAlias(UpdateAliasRequest request) {
        request = this.beforeClientExecution(request);
        return this.executeUpdateAlias(request);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @SdkInternalApi
    final UpdateAliasResult executeUpdateAlias(UpdateAliasRequest updateAliasRequest) {
        ExecutionContext executionContext = this.createExecutionContext(updateAliasRequest);
        AWSRequestMetrics awsRequestMetrics = executionContext.getAwsRequestMetrics();
        awsRequestMetrics.startEvent(AWSRequestMetrics.Field.ClientExecuteTime);
        Request<UpdateAliasRequest> request = null;
        Response response = null;
        try {
            awsRequestMetrics.startEvent(AWSRequestMetrics.Field.RequestMarshallTime);
            try {
                request = new UpdateAliasRequestProtocolMarshaller(protocolFactory).marshall(super.beforeMarshalling(updateAliasRequest));
                request.setAWSRequestMetrics(awsRequestMetrics);
                request.addHandlerContext(HandlerContextKey.CLIENT_ENDPOINT, this.endpoint);
                request.addHandlerContext(HandlerContextKey.ENDPOINT_OVERRIDDEN, this.isEndpointOverridden());
                request.addHandlerContext(HandlerContextKey.SIGNING_REGION, this.getSigningRegion());
                request.addHandlerContext(HandlerContextKey.SERVICE_ID, "KMS");
                request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "UpdateAlias");
                request.addHandlerContext(HandlerContextKey.ADVANCED_CONFIG, this.advancedConfig);
            }
            finally {
                awsRequestMetrics.endEvent(AWSRequestMetrics.Field.RequestMarshallTime);
            }
            HttpResponseHandler responseHandler = protocolFactory.createResponseHandler(new JsonOperationMetadata().withPayloadJson(true).withHasStreamingSuccessResponse(false), new UpdateAliasResultJsonUnmarshaller());
            response = this.invoke(request, responseHandler, executionContext);
            UpdateAliasResult updateAliasResult = (UpdateAliasResult)response.getAwsResponse();
            return updateAliasResult;
        }
        finally {
            this.endClientExecution(awsRequestMetrics, request, response);
        }
    }

    @Override
    public UpdateCustomKeyStoreResult updateCustomKeyStore(UpdateCustomKeyStoreRequest request) {
        request = this.beforeClientExecution(request);
        return this.executeUpdateCustomKeyStore(request);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @SdkInternalApi
    final UpdateCustomKeyStoreResult executeUpdateCustomKeyStore(UpdateCustomKeyStoreRequest updateCustomKeyStoreRequest) {
        ExecutionContext executionContext = this.createExecutionContext(updateCustomKeyStoreRequest);
        AWSRequestMetrics awsRequestMetrics = executionContext.getAwsRequestMetrics();
        awsRequestMetrics.startEvent(AWSRequestMetrics.Field.ClientExecuteTime);
        Request<UpdateCustomKeyStoreRequest> request = null;
        Response response = null;
        try {
            awsRequestMetrics.startEvent(AWSRequestMetrics.Field.RequestMarshallTime);
            try {
                request = new UpdateCustomKeyStoreRequestProtocolMarshaller(protocolFactory).marshall(super.beforeMarshalling(updateCustomKeyStoreRequest));
                request.setAWSRequestMetrics(awsRequestMetrics);
                request.addHandlerContext(HandlerContextKey.CLIENT_ENDPOINT, this.endpoint);
                request.addHandlerContext(HandlerContextKey.ENDPOINT_OVERRIDDEN, this.isEndpointOverridden());
                request.addHandlerContext(HandlerContextKey.SIGNING_REGION, this.getSigningRegion());
                request.addHandlerContext(HandlerContextKey.SERVICE_ID, "KMS");
                request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "UpdateCustomKeyStore");
                request.addHandlerContext(HandlerContextKey.ADVANCED_CONFIG, this.advancedConfig);
            }
            finally {
                awsRequestMetrics.endEvent(AWSRequestMetrics.Field.RequestMarshallTime);
            }
            HttpResponseHandler responseHandler = protocolFactory.createResponseHandler(new JsonOperationMetadata().withPayloadJson(true).withHasStreamingSuccessResponse(false), new UpdateCustomKeyStoreResultJsonUnmarshaller());
            response = this.invoke(request, responseHandler, executionContext);
            UpdateCustomKeyStoreResult updateCustomKeyStoreResult = (UpdateCustomKeyStoreResult)response.getAwsResponse();
            return updateCustomKeyStoreResult;
        }
        finally {
            this.endClientExecution(awsRequestMetrics, request, response);
        }
    }

    @Override
    public UpdateKeyDescriptionResult updateKeyDescription(UpdateKeyDescriptionRequest request) {
        request = this.beforeClientExecution(request);
        return this.executeUpdateKeyDescription(request);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @SdkInternalApi
    final UpdateKeyDescriptionResult executeUpdateKeyDescription(UpdateKeyDescriptionRequest updateKeyDescriptionRequest) {
        ExecutionContext executionContext = this.createExecutionContext(updateKeyDescriptionRequest);
        AWSRequestMetrics awsRequestMetrics = executionContext.getAwsRequestMetrics();
        awsRequestMetrics.startEvent(AWSRequestMetrics.Field.ClientExecuteTime);
        Request<UpdateKeyDescriptionRequest> request = null;
        Response response = null;
        try {
            awsRequestMetrics.startEvent(AWSRequestMetrics.Field.RequestMarshallTime);
            try {
                request = new UpdateKeyDescriptionRequestProtocolMarshaller(protocolFactory).marshall(super.beforeMarshalling(updateKeyDescriptionRequest));
                request.setAWSRequestMetrics(awsRequestMetrics);
                request.addHandlerContext(HandlerContextKey.CLIENT_ENDPOINT, this.endpoint);
                request.addHandlerContext(HandlerContextKey.ENDPOINT_OVERRIDDEN, this.isEndpointOverridden());
                request.addHandlerContext(HandlerContextKey.SIGNING_REGION, this.getSigningRegion());
                request.addHandlerContext(HandlerContextKey.SERVICE_ID, "KMS");
                request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "UpdateKeyDescription");
                request.addHandlerContext(HandlerContextKey.ADVANCED_CONFIG, this.advancedConfig);
            }
            finally {
                awsRequestMetrics.endEvent(AWSRequestMetrics.Field.RequestMarshallTime);
            }
            HttpResponseHandler responseHandler = protocolFactory.createResponseHandler(new JsonOperationMetadata().withPayloadJson(true).withHasStreamingSuccessResponse(false), new UpdateKeyDescriptionResultJsonUnmarshaller());
            response = this.invoke(request, responseHandler, executionContext);
            UpdateKeyDescriptionResult updateKeyDescriptionResult = (UpdateKeyDescriptionResult)response.getAwsResponse();
            return updateKeyDescriptionResult;
        }
        finally {
            this.endClientExecution(awsRequestMetrics, request, response);
        }
    }

    @Override
    public UpdatePrimaryRegionResult updatePrimaryRegion(UpdatePrimaryRegionRequest request) {
        request = this.beforeClientExecution(request);
        return this.executeUpdatePrimaryRegion(request);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @SdkInternalApi
    final UpdatePrimaryRegionResult executeUpdatePrimaryRegion(UpdatePrimaryRegionRequest updatePrimaryRegionRequest) {
        ExecutionContext executionContext = this.createExecutionContext(updatePrimaryRegionRequest);
        AWSRequestMetrics awsRequestMetrics = executionContext.getAwsRequestMetrics();
        awsRequestMetrics.startEvent(AWSRequestMetrics.Field.ClientExecuteTime);
        Request<UpdatePrimaryRegionRequest> request = null;
        Response response = null;
        try {
            awsRequestMetrics.startEvent(AWSRequestMetrics.Field.RequestMarshallTime);
            try {
                request = new UpdatePrimaryRegionRequestProtocolMarshaller(protocolFactory).marshall(super.beforeMarshalling(updatePrimaryRegionRequest));
                request.setAWSRequestMetrics(awsRequestMetrics);
                request.addHandlerContext(HandlerContextKey.CLIENT_ENDPOINT, this.endpoint);
                request.addHandlerContext(HandlerContextKey.ENDPOINT_OVERRIDDEN, this.isEndpointOverridden());
                request.addHandlerContext(HandlerContextKey.SIGNING_REGION, this.getSigningRegion());
                request.addHandlerContext(HandlerContextKey.SERVICE_ID, "KMS");
                request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "UpdatePrimaryRegion");
                request.addHandlerContext(HandlerContextKey.ADVANCED_CONFIG, this.advancedConfig);
            }
            finally {
                awsRequestMetrics.endEvent(AWSRequestMetrics.Field.RequestMarshallTime);
            }
            HttpResponseHandler responseHandler = protocolFactory.createResponseHandler(new JsonOperationMetadata().withPayloadJson(true).withHasStreamingSuccessResponse(false), new UpdatePrimaryRegionResultJsonUnmarshaller());
            response = this.invoke(request, responseHandler, executionContext);
            UpdatePrimaryRegionResult updatePrimaryRegionResult = (UpdatePrimaryRegionResult)response.getAwsResponse();
            return updatePrimaryRegionResult;
        }
        finally {
            this.endClientExecution(awsRequestMetrics, request, response);
        }
    }

    @Override
    public VerifyResult verify(VerifyRequest request) {
        request = this.beforeClientExecution(request);
        return this.executeVerify(request);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @SdkInternalApi
    final VerifyResult executeVerify(VerifyRequest verifyRequest) {
        ExecutionContext executionContext = this.createExecutionContext(verifyRequest);
        AWSRequestMetrics awsRequestMetrics = executionContext.getAwsRequestMetrics();
        awsRequestMetrics.startEvent(AWSRequestMetrics.Field.ClientExecuteTime);
        Request<VerifyRequest> request = null;
        Response response = null;
        try {
            awsRequestMetrics.startEvent(AWSRequestMetrics.Field.RequestMarshallTime);
            try {
                request = new VerifyRequestProtocolMarshaller(protocolFactory).marshall(super.beforeMarshalling(verifyRequest));
                request.setAWSRequestMetrics(awsRequestMetrics);
                request.addHandlerContext(HandlerContextKey.CLIENT_ENDPOINT, this.endpoint);
                request.addHandlerContext(HandlerContextKey.ENDPOINT_OVERRIDDEN, this.isEndpointOverridden());
                request.addHandlerContext(HandlerContextKey.SIGNING_REGION, this.getSigningRegion());
                request.addHandlerContext(HandlerContextKey.SERVICE_ID, "KMS");
                request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "Verify");
                request.addHandlerContext(HandlerContextKey.ADVANCED_CONFIG, this.advancedConfig);
            }
            finally {
                awsRequestMetrics.endEvent(AWSRequestMetrics.Field.RequestMarshallTime);
            }
            HttpResponseHandler responseHandler = protocolFactory.createResponseHandler(new JsonOperationMetadata().withPayloadJson(true).withHasStreamingSuccessResponse(false), new VerifyResultJsonUnmarshaller());
            response = this.invoke(request, responseHandler, executionContext);
            VerifyResult verifyResult = (VerifyResult)response.getAwsResponse();
            return verifyResult;
        }
        finally {
            this.endClientExecution(awsRequestMetrics, request, response);
        }
    }

    @Override
    public VerifyMacResult verifyMac(VerifyMacRequest request) {
        request = this.beforeClientExecution(request);
        return this.executeVerifyMac(request);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @SdkInternalApi
    final VerifyMacResult executeVerifyMac(VerifyMacRequest verifyMacRequest) {
        ExecutionContext executionContext = this.createExecutionContext(verifyMacRequest);
        AWSRequestMetrics awsRequestMetrics = executionContext.getAwsRequestMetrics();
        awsRequestMetrics.startEvent(AWSRequestMetrics.Field.ClientExecuteTime);
        Request<VerifyMacRequest> request = null;
        Response response = null;
        try {
            awsRequestMetrics.startEvent(AWSRequestMetrics.Field.RequestMarshallTime);
            try {
                request = new VerifyMacRequestProtocolMarshaller(protocolFactory).marshall(super.beforeMarshalling(verifyMacRequest));
                request.setAWSRequestMetrics(awsRequestMetrics);
                request.addHandlerContext(HandlerContextKey.CLIENT_ENDPOINT, this.endpoint);
                request.addHandlerContext(HandlerContextKey.ENDPOINT_OVERRIDDEN, this.isEndpointOverridden());
                request.addHandlerContext(HandlerContextKey.SIGNING_REGION, this.getSigningRegion());
                request.addHandlerContext(HandlerContextKey.SERVICE_ID, "KMS");
                request.addHandlerContext(HandlerContextKey.OPERATION_NAME, "VerifyMac");
                request.addHandlerContext(HandlerContextKey.ADVANCED_CONFIG, this.advancedConfig);
            }
            finally {
                awsRequestMetrics.endEvent(AWSRequestMetrics.Field.RequestMarshallTime);
            }
            HttpResponseHandler responseHandler = protocolFactory.createResponseHandler(new JsonOperationMetadata().withPayloadJson(true).withHasStreamingSuccessResponse(false), new VerifyMacResultJsonUnmarshaller());
            response = this.invoke(request, responseHandler, executionContext);
            VerifyMacResult verifyMacResult = (VerifyMacResult)response.getAwsResponse();
            return verifyMacResult;
        }
        finally {
            this.endClientExecution(awsRequestMetrics, request, response);
        }
    }

    @Override
    public ResponseMetadata getCachedResponseMetadata(AmazonWebServiceRequest request) {
        return this.client.getResponseMetadataForRequest(request);
    }

    private <X, Y extends AmazonWebServiceRequest> Response<X> invoke(Request<Y> request, HttpResponseHandler<AmazonWebServiceResponse<X>> responseHandler, ExecutionContext executionContext) {
        return this.invoke(request, responseHandler, executionContext, null, null);
    }

    private <X, Y extends AmazonWebServiceRequest> Response<X> invoke(Request<Y> request, HttpResponseHandler<AmazonWebServiceResponse<X>> responseHandler, ExecutionContext executionContext, URI cachedEndpoint, URI uriFromEndpointTrait) {
        executionContext.setCredentialsProvider(CredentialUtils.getCredentialsProvider(request.getOriginalRequest(), this.awsCredentialsProvider));
        return this.doInvoke(request, responseHandler, executionContext, cachedEndpoint, uriFromEndpointTrait);
    }

    private <X, Y extends AmazonWebServiceRequest> Response<X> anonymousInvoke(Request<Y> request, HttpResponseHandler<AmazonWebServiceResponse<X>> responseHandler, ExecutionContext executionContext) {
        return this.doInvoke(request, responseHandler, executionContext, null, null);
    }

    private <X, Y extends AmazonWebServiceRequest> Response<X> doInvoke(Request<Y> request, HttpResponseHandler<AmazonWebServiceResponse<X>> responseHandler, ExecutionContext executionContext, URI discoveredEndpoint, URI uriFromEndpointTrait) {
        if (discoveredEndpoint != null) {
            request.setEndpoint(discoveredEndpoint);
            request.getOriginalRequest().getRequestClientOptions().appendUserAgent("endpoint-discovery");
        } else if (uriFromEndpointTrait != null) {
            request.setEndpoint(uriFromEndpointTrait);
        } else {
            request.setEndpoint(this.endpoint);
        }
        request.setTimeOffset(this.timeOffset);
        HttpResponseHandler<AmazonServiceException> errorResponseHandler = protocolFactory.createErrorResponseHandler(new JsonErrorResponseMetadata());
        return this.client.execute(request, responseHandler, errorResponseHandler, executionContext);
    }

    @SdkInternalApi
    static SdkJsonProtocolFactory getProtocolFactory() {
        return protocolFactory;
    }

    @Override
    public void shutdown() {
        super.shutdown();
    }
}

