/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkPublicApi
 *  software.amazon.awssdk.annotations.ThreadSafe
 *  software.amazon.awssdk.awscore.AwsClient
 *  software.amazon.awssdk.awscore.exception.AwsServiceException
 *  software.amazon.awssdk.core.exception.SdkClientException
 *  software.amazon.awssdk.regions.ServiceMetadata
 */
package software.amazon.awssdk.services.secretsmanager;

import java.util.function.Consumer;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.annotations.ThreadSafe;
import software.amazon.awssdk.awscore.AwsClient;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.regions.ServiceMetadata;
import software.amazon.awssdk.services.secretsmanager.DefaultSecretsManagerClientBuilder;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClientBuilder;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerServiceClientConfiguration;
import software.amazon.awssdk.services.secretsmanager.model.CancelRotateSecretRequest;
import software.amazon.awssdk.services.secretsmanager.model.CancelRotateSecretResponse;
import software.amazon.awssdk.services.secretsmanager.model.CreateSecretRequest;
import software.amazon.awssdk.services.secretsmanager.model.CreateSecretResponse;
import software.amazon.awssdk.services.secretsmanager.model.DecryptionFailureException;
import software.amazon.awssdk.services.secretsmanager.model.DeleteResourcePolicyRequest;
import software.amazon.awssdk.services.secretsmanager.model.DeleteResourcePolicyResponse;
import software.amazon.awssdk.services.secretsmanager.model.DeleteSecretRequest;
import software.amazon.awssdk.services.secretsmanager.model.DeleteSecretResponse;
import software.amazon.awssdk.services.secretsmanager.model.DescribeSecretRequest;
import software.amazon.awssdk.services.secretsmanager.model.DescribeSecretResponse;
import software.amazon.awssdk.services.secretsmanager.model.EncryptionFailureException;
import software.amazon.awssdk.services.secretsmanager.model.GetRandomPasswordRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetRandomPasswordResponse;
import software.amazon.awssdk.services.secretsmanager.model.GetResourcePolicyRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetResourcePolicyResponse;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;
import software.amazon.awssdk.services.secretsmanager.model.InternalServiceErrorException;
import software.amazon.awssdk.services.secretsmanager.model.InvalidNextTokenException;
import software.amazon.awssdk.services.secretsmanager.model.InvalidParameterException;
import software.amazon.awssdk.services.secretsmanager.model.InvalidRequestException;
import software.amazon.awssdk.services.secretsmanager.model.LimitExceededException;
import software.amazon.awssdk.services.secretsmanager.model.ListSecretVersionIdsRequest;
import software.amazon.awssdk.services.secretsmanager.model.ListSecretVersionIdsResponse;
import software.amazon.awssdk.services.secretsmanager.model.ListSecretsRequest;
import software.amazon.awssdk.services.secretsmanager.model.ListSecretsResponse;
import software.amazon.awssdk.services.secretsmanager.model.MalformedPolicyDocumentException;
import software.amazon.awssdk.services.secretsmanager.model.PreconditionNotMetException;
import software.amazon.awssdk.services.secretsmanager.model.PublicPolicyException;
import software.amazon.awssdk.services.secretsmanager.model.PutResourcePolicyRequest;
import software.amazon.awssdk.services.secretsmanager.model.PutResourcePolicyResponse;
import software.amazon.awssdk.services.secretsmanager.model.PutSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.PutSecretValueResponse;
import software.amazon.awssdk.services.secretsmanager.model.RemoveRegionsFromReplicationRequest;
import software.amazon.awssdk.services.secretsmanager.model.RemoveRegionsFromReplicationResponse;
import software.amazon.awssdk.services.secretsmanager.model.ReplicateSecretToRegionsRequest;
import software.amazon.awssdk.services.secretsmanager.model.ReplicateSecretToRegionsResponse;
import software.amazon.awssdk.services.secretsmanager.model.ResourceExistsException;
import software.amazon.awssdk.services.secretsmanager.model.ResourceNotFoundException;
import software.amazon.awssdk.services.secretsmanager.model.RestoreSecretRequest;
import software.amazon.awssdk.services.secretsmanager.model.RestoreSecretResponse;
import software.amazon.awssdk.services.secretsmanager.model.RotateSecretRequest;
import software.amazon.awssdk.services.secretsmanager.model.RotateSecretResponse;
import software.amazon.awssdk.services.secretsmanager.model.SecretsManagerException;
import software.amazon.awssdk.services.secretsmanager.model.StopReplicationToReplicaRequest;
import software.amazon.awssdk.services.secretsmanager.model.StopReplicationToReplicaResponse;
import software.amazon.awssdk.services.secretsmanager.model.TagResourceRequest;
import software.amazon.awssdk.services.secretsmanager.model.TagResourceResponse;
import software.amazon.awssdk.services.secretsmanager.model.UntagResourceRequest;
import software.amazon.awssdk.services.secretsmanager.model.UntagResourceResponse;
import software.amazon.awssdk.services.secretsmanager.model.UpdateSecretRequest;
import software.amazon.awssdk.services.secretsmanager.model.UpdateSecretResponse;
import software.amazon.awssdk.services.secretsmanager.model.UpdateSecretVersionStageRequest;
import software.amazon.awssdk.services.secretsmanager.model.UpdateSecretVersionStageResponse;
import software.amazon.awssdk.services.secretsmanager.model.ValidateResourcePolicyRequest;
import software.amazon.awssdk.services.secretsmanager.model.ValidateResourcePolicyResponse;
import software.amazon.awssdk.services.secretsmanager.paginators.ListSecretVersionIdsIterable;
import software.amazon.awssdk.services.secretsmanager.paginators.ListSecretsIterable;

@SdkPublicApi
@ThreadSafe
public interface SecretsManagerClient
extends AwsClient {
    public static final String SERVICE_NAME = "secretsmanager";
    public static final String SERVICE_METADATA_ID = "secretsmanager";

    default public CancelRotateSecretResponse cancelRotateSecret(CancelRotateSecretRequest cancelRotateSecretRequest) throws ResourceNotFoundException, InvalidParameterException, InternalServiceErrorException, InvalidRequestException, AwsServiceException, SdkClientException, SecretsManagerException {
        throw new UnsupportedOperationException();
    }

    default public CancelRotateSecretResponse cancelRotateSecret(Consumer<CancelRotateSecretRequest.Builder> cancelRotateSecretRequest) throws ResourceNotFoundException, InvalidParameterException, InternalServiceErrorException, InvalidRequestException, AwsServiceException, SdkClientException, SecretsManagerException {
        return this.cancelRotateSecret((CancelRotateSecretRequest)((Object)((CancelRotateSecretRequest.Builder)CancelRotateSecretRequest.builder().applyMutation(cancelRotateSecretRequest)).build()));
    }

    default public CreateSecretResponse createSecret(CreateSecretRequest createSecretRequest) throws InvalidParameterException, InvalidRequestException, LimitExceededException, EncryptionFailureException, ResourceExistsException, ResourceNotFoundException, MalformedPolicyDocumentException, InternalServiceErrorException, PreconditionNotMetException, DecryptionFailureException, AwsServiceException, SdkClientException, SecretsManagerException {
        throw new UnsupportedOperationException();
    }

    default public CreateSecretResponse createSecret(Consumer<CreateSecretRequest.Builder> createSecretRequest) throws InvalidParameterException, InvalidRequestException, LimitExceededException, EncryptionFailureException, ResourceExistsException, ResourceNotFoundException, MalformedPolicyDocumentException, InternalServiceErrorException, PreconditionNotMetException, DecryptionFailureException, AwsServiceException, SdkClientException, SecretsManagerException {
        return this.createSecret((CreateSecretRequest)((Object)((CreateSecretRequest.Builder)CreateSecretRequest.builder().applyMutation(createSecretRequest)).build()));
    }

    default public DeleteResourcePolicyResponse deleteResourcePolicy(DeleteResourcePolicyRequest deleteResourcePolicyRequest) throws ResourceNotFoundException, InternalServiceErrorException, InvalidRequestException, InvalidParameterException, AwsServiceException, SdkClientException, SecretsManagerException {
        throw new UnsupportedOperationException();
    }

    default public DeleteResourcePolicyResponse deleteResourcePolicy(Consumer<DeleteResourcePolicyRequest.Builder> deleteResourcePolicyRequest) throws ResourceNotFoundException, InternalServiceErrorException, InvalidRequestException, InvalidParameterException, AwsServiceException, SdkClientException, SecretsManagerException {
        return this.deleteResourcePolicy((DeleteResourcePolicyRequest)((Object)((DeleteResourcePolicyRequest.Builder)DeleteResourcePolicyRequest.builder().applyMutation(deleteResourcePolicyRequest)).build()));
    }

    default public DeleteSecretResponse deleteSecret(DeleteSecretRequest deleteSecretRequest) throws ResourceNotFoundException, InvalidParameterException, InvalidRequestException, InternalServiceErrorException, AwsServiceException, SdkClientException, SecretsManagerException {
        throw new UnsupportedOperationException();
    }

    default public DeleteSecretResponse deleteSecret(Consumer<DeleteSecretRequest.Builder> deleteSecretRequest) throws ResourceNotFoundException, InvalidParameterException, InvalidRequestException, InternalServiceErrorException, AwsServiceException, SdkClientException, SecretsManagerException {
        return this.deleteSecret((DeleteSecretRequest)((Object)((DeleteSecretRequest.Builder)DeleteSecretRequest.builder().applyMutation(deleteSecretRequest)).build()));
    }

    default public DescribeSecretResponse describeSecret(DescribeSecretRequest describeSecretRequest) throws ResourceNotFoundException, InternalServiceErrorException, InvalidParameterException, AwsServiceException, SdkClientException, SecretsManagerException {
        throw new UnsupportedOperationException();
    }

    default public DescribeSecretResponse describeSecret(Consumer<DescribeSecretRequest.Builder> describeSecretRequest) throws ResourceNotFoundException, InternalServiceErrorException, InvalidParameterException, AwsServiceException, SdkClientException, SecretsManagerException {
        return this.describeSecret((DescribeSecretRequest)((Object)((DescribeSecretRequest.Builder)DescribeSecretRequest.builder().applyMutation(describeSecretRequest)).build()));
    }

    default public GetRandomPasswordResponse getRandomPassword(GetRandomPasswordRequest getRandomPasswordRequest) throws InvalidParameterException, InvalidRequestException, InternalServiceErrorException, AwsServiceException, SdkClientException, SecretsManagerException {
        throw new UnsupportedOperationException();
    }

    default public GetRandomPasswordResponse getRandomPassword(Consumer<GetRandomPasswordRequest.Builder> getRandomPasswordRequest) throws InvalidParameterException, InvalidRequestException, InternalServiceErrorException, AwsServiceException, SdkClientException, SecretsManagerException {
        return this.getRandomPassword((GetRandomPasswordRequest)((Object)((GetRandomPasswordRequest.Builder)GetRandomPasswordRequest.builder().applyMutation(getRandomPasswordRequest)).build()));
    }

    default public GetRandomPasswordResponse getRandomPassword() throws InvalidParameterException, InvalidRequestException, InternalServiceErrorException, AwsServiceException, SdkClientException, SecretsManagerException {
        return this.getRandomPassword((GetRandomPasswordRequest)((Object)GetRandomPasswordRequest.builder().build()));
    }

    default public GetResourcePolicyResponse getResourcePolicy(GetResourcePolicyRequest getResourcePolicyRequest) throws ResourceNotFoundException, InternalServiceErrorException, InvalidRequestException, InvalidParameterException, AwsServiceException, SdkClientException, SecretsManagerException {
        throw new UnsupportedOperationException();
    }

    default public GetResourcePolicyResponse getResourcePolicy(Consumer<GetResourcePolicyRequest.Builder> getResourcePolicyRequest) throws ResourceNotFoundException, InternalServiceErrorException, InvalidRequestException, InvalidParameterException, AwsServiceException, SdkClientException, SecretsManagerException {
        return this.getResourcePolicy((GetResourcePolicyRequest)((Object)((GetResourcePolicyRequest.Builder)GetResourcePolicyRequest.builder().applyMutation(getResourcePolicyRequest)).build()));
    }

    default public GetSecretValueResponse getSecretValue(GetSecretValueRequest getSecretValueRequest) throws ResourceNotFoundException, InvalidParameterException, InvalidRequestException, DecryptionFailureException, InternalServiceErrorException, AwsServiceException, SdkClientException, SecretsManagerException {
        throw new UnsupportedOperationException();
    }

    default public GetSecretValueResponse getSecretValue(Consumer<GetSecretValueRequest.Builder> getSecretValueRequest) throws ResourceNotFoundException, InvalidParameterException, InvalidRequestException, DecryptionFailureException, InternalServiceErrorException, AwsServiceException, SdkClientException, SecretsManagerException {
        return this.getSecretValue((GetSecretValueRequest)((Object)((GetSecretValueRequest.Builder)GetSecretValueRequest.builder().applyMutation(getSecretValueRequest)).build()));
    }

    default public ListSecretVersionIdsResponse listSecretVersionIds(ListSecretVersionIdsRequest listSecretVersionIdsRequest) throws InvalidNextTokenException, ResourceNotFoundException, InternalServiceErrorException, InvalidParameterException, AwsServiceException, SdkClientException, SecretsManagerException {
        throw new UnsupportedOperationException();
    }

    default public ListSecretVersionIdsResponse listSecretVersionIds(Consumer<ListSecretVersionIdsRequest.Builder> listSecretVersionIdsRequest) throws InvalidNextTokenException, ResourceNotFoundException, InternalServiceErrorException, InvalidParameterException, AwsServiceException, SdkClientException, SecretsManagerException {
        return this.listSecretVersionIds((ListSecretVersionIdsRequest)((Object)((ListSecretVersionIdsRequest.Builder)ListSecretVersionIdsRequest.builder().applyMutation(listSecretVersionIdsRequest)).build()));
    }

    default public ListSecretVersionIdsIterable listSecretVersionIdsPaginator(ListSecretVersionIdsRequest listSecretVersionIdsRequest) throws InvalidNextTokenException, ResourceNotFoundException, InternalServiceErrorException, InvalidParameterException, AwsServiceException, SdkClientException, SecretsManagerException {
        return new ListSecretVersionIdsIterable(this, listSecretVersionIdsRequest);
    }

    default public ListSecretVersionIdsIterable listSecretVersionIdsPaginator(Consumer<ListSecretVersionIdsRequest.Builder> listSecretVersionIdsRequest) throws InvalidNextTokenException, ResourceNotFoundException, InternalServiceErrorException, InvalidParameterException, AwsServiceException, SdkClientException, SecretsManagerException {
        return this.listSecretVersionIdsPaginator((ListSecretVersionIdsRequest)((Object)((ListSecretVersionIdsRequest.Builder)ListSecretVersionIdsRequest.builder().applyMutation(listSecretVersionIdsRequest)).build()));
    }

    default public ListSecretsResponse listSecrets(ListSecretsRequest listSecretsRequest) throws InvalidParameterException, InvalidRequestException, InvalidNextTokenException, InternalServiceErrorException, AwsServiceException, SdkClientException, SecretsManagerException {
        throw new UnsupportedOperationException();
    }

    default public ListSecretsResponse listSecrets(Consumer<ListSecretsRequest.Builder> listSecretsRequest) throws InvalidParameterException, InvalidRequestException, InvalidNextTokenException, InternalServiceErrorException, AwsServiceException, SdkClientException, SecretsManagerException {
        return this.listSecrets((ListSecretsRequest)((Object)((ListSecretsRequest.Builder)ListSecretsRequest.builder().applyMutation(listSecretsRequest)).build()));
    }

    default public ListSecretsResponse listSecrets() throws InvalidParameterException, InvalidRequestException, InvalidNextTokenException, InternalServiceErrorException, AwsServiceException, SdkClientException, SecretsManagerException {
        return this.listSecrets((ListSecretsRequest)((Object)ListSecretsRequest.builder().build()));
    }

    default public ListSecretsIterable listSecretsPaginator() throws InvalidParameterException, InvalidRequestException, InvalidNextTokenException, InternalServiceErrorException, AwsServiceException, SdkClientException, SecretsManagerException {
        return this.listSecretsPaginator((ListSecretsRequest)((Object)ListSecretsRequest.builder().build()));
    }

    default public ListSecretsIterable listSecretsPaginator(ListSecretsRequest listSecretsRequest) throws InvalidParameterException, InvalidRequestException, InvalidNextTokenException, InternalServiceErrorException, AwsServiceException, SdkClientException, SecretsManagerException {
        return new ListSecretsIterable(this, listSecretsRequest);
    }

    default public ListSecretsIterable listSecretsPaginator(Consumer<ListSecretsRequest.Builder> listSecretsRequest) throws InvalidParameterException, InvalidRequestException, InvalidNextTokenException, InternalServiceErrorException, AwsServiceException, SdkClientException, SecretsManagerException {
        return this.listSecretsPaginator((ListSecretsRequest)((Object)((ListSecretsRequest.Builder)ListSecretsRequest.builder().applyMutation(listSecretsRequest)).build()));
    }

    default public PutResourcePolicyResponse putResourcePolicy(PutResourcePolicyRequest putResourcePolicyRequest) throws MalformedPolicyDocumentException, ResourceNotFoundException, InvalidParameterException, InternalServiceErrorException, InvalidRequestException, PublicPolicyException, AwsServiceException, SdkClientException, SecretsManagerException {
        throw new UnsupportedOperationException();
    }

    default public PutResourcePolicyResponse putResourcePolicy(Consumer<PutResourcePolicyRequest.Builder> putResourcePolicyRequest) throws MalformedPolicyDocumentException, ResourceNotFoundException, InvalidParameterException, InternalServiceErrorException, InvalidRequestException, PublicPolicyException, AwsServiceException, SdkClientException, SecretsManagerException {
        return this.putResourcePolicy((PutResourcePolicyRequest)((Object)((PutResourcePolicyRequest.Builder)PutResourcePolicyRequest.builder().applyMutation(putResourcePolicyRequest)).build()));
    }

    default public PutSecretValueResponse putSecretValue(PutSecretValueRequest putSecretValueRequest) throws InvalidParameterException, InvalidRequestException, LimitExceededException, EncryptionFailureException, ResourceExistsException, ResourceNotFoundException, InternalServiceErrorException, DecryptionFailureException, AwsServiceException, SdkClientException, SecretsManagerException {
        throw new UnsupportedOperationException();
    }

    default public PutSecretValueResponse putSecretValue(Consumer<PutSecretValueRequest.Builder> putSecretValueRequest) throws InvalidParameterException, InvalidRequestException, LimitExceededException, EncryptionFailureException, ResourceExistsException, ResourceNotFoundException, InternalServiceErrorException, DecryptionFailureException, AwsServiceException, SdkClientException, SecretsManagerException {
        return this.putSecretValue((PutSecretValueRequest)((Object)((PutSecretValueRequest.Builder)PutSecretValueRequest.builder().applyMutation(putSecretValueRequest)).build()));
    }

    default public RemoveRegionsFromReplicationResponse removeRegionsFromReplication(RemoveRegionsFromReplicationRequest removeRegionsFromReplicationRequest) throws ResourceNotFoundException, InvalidRequestException, InvalidParameterException, InternalServiceErrorException, AwsServiceException, SdkClientException, SecretsManagerException {
        throw new UnsupportedOperationException();
    }

    default public RemoveRegionsFromReplicationResponse removeRegionsFromReplication(Consumer<RemoveRegionsFromReplicationRequest.Builder> removeRegionsFromReplicationRequest) throws ResourceNotFoundException, InvalidRequestException, InvalidParameterException, InternalServiceErrorException, AwsServiceException, SdkClientException, SecretsManagerException {
        return this.removeRegionsFromReplication((RemoveRegionsFromReplicationRequest)((Object)((RemoveRegionsFromReplicationRequest.Builder)RemoveRegionsFromReplicationRequest.builder().applyMutation(removeRegionsFromReplicationRequest)).build()));
    }

    default public ReplicateSecretToRegionsResponse replicateSecretToRegions(ReplicateSecretToRegionsRequest replicateSecretToRegionsRequest) throws ResourceNotFoundException, InvalidRequestException, InvalidParameterException, InternalServiceErrorException, AwsServiceException, SdkClientException, SecretsManagerException {
        throw new UnsupportedOperationException();
    }

    default public ReplicateSecretToRegionsResponse replicateSecretToRegions(Consumer<ReplicateSecretToRegionsRequest.Builder> replicateSecretToRegionsRequest) throws ResourceNotFoundException, InvalidRequestException, InvalidParameterException, InternalServiceErrorException, AwsServiceException, SdkClientException, SecretsManagerException {
        return this.replicateSecretToRegions((ReplicateSecretToRegionsRequest)((Object)((ReplicateSecretToRegionsRequest.Builder)ReplicateSecretToRegionsRequest.builder().applyMutation(replicateSecretToRegionsRequest)).build()));
    }

    default public RestoreSecretResponse restoreSecret(RestoreSecretRequest restoreSecretRequest) throws ResourceNotFoundException, InvalidParameterException, InvalidRequestException, InternalServiceErrorException, AwsServiceException, SdkClientException, SecretsManagerException {
        throw new UnsupportedOperationException();
    }

    default public RestoreSecretResponse restoreSecret(Consumer<RestoreSecretRequest.Builder> restoreSecretRequest) throws ResourceNotFoundException, InvalidParameterException, InvalidRequestException, InternalServiceErrorException, AwsServiceException, SdkClientException, SecretsManagerException {
        return this.restoreSecret((RestoreSecretRequest)((Object)((RestoreSecretRequest.Builder)RestoreSecretRequest.builder().applyMutation(restoreSecretRequest)).build()));
    }

    default public RotateSecretResponse rotateSecret(RotateSecretRequest rotateSecretRequest) throws ResourceNotFoundException, InvalidParameterException, InternalServiceErrorException, InvalidRequestException, AwsServiceException, SdkClientException, SecretsManagerException {
        throw new UnsupportedOperationException();
    }

    default public RotateSecretResponse rotateSecret(Consumer<RotateSecretRequest.Builder> rotateSecretRequest) throws ResourceNotFoundException, InvalidParameterException, InternalServiceErrorException, InvalidRequestException, AwsServiceException, SdkClientException, SecretsManagerException {
        return this.rotateSecret((RotateSecretRequest)((Object)((RotateSecretRequest.Builder)RotateSecretRequest.builder().applyMutation(rotateSecretRequest)).build()));
    }

    default public StopReplicationToReplicaResponse stopReplicationToReplica(StopReplicationToReplicaRequest stopReplicationToReplicaRequest) throws ResourceNotFoundException, InvalidRequestException, InvalidParameterException, InternalServiceErrorException, AwsServiceException, SdkClientException, SecretsManagerException {
        throw new UnsupportedOperationException();
    }

    default public StopReplicationToReplicaResponse stopReplicationToReplica(Consumer<StopReplicationToReplicaRequest.Builder> stopReplicationToReplicaRequest) throws ResourceNotFoundException, InvalidRequestException, InvalidParameterException, InternalServiceErrorException, AwsServiceException, SdkClientException, SecretsManagerException {
        return this.stopReplicationToReplica((StopReplicationToReplicaRequest)((Object)((StopReplicationToReplicaRequest.Builder)StopReplicationToReplicaRequest.builder().applyMutation(stopReplicationToReplicaRequest)).build()));
    }

    default public TagResourceResponse tagResource(TagResourceRequest tagResourceRequest) throws ResourceNotFoundException, InvalidRequestException, InvalidParameterException, InternalServiceErrorException, AwsServiceException, SdkClientException, SecretsManagerException {
        throw new UnsupportedOperationException();
    }

    default public TagResourceResponse tagResource(Consumer<TagResourceRequest.Builder> tagResourceRequest) throws ResourceNotFoundException, InvalidRequestException, InvalidParameterException, InternalServiceErrorException, AwsServiceException, SdkClientException, SecretsManagerException {
        return this.tagResource((TagResourceRequest)((Object)((TagResourceRequest.Builder)TagResourceRequest.builder().applyMutation(tagResourceRequest)).build()));
    }

    default public UntagResourceResponse untagResource(UntagResourceRequest untagResourceRequest) throws ResourceNotFoundException, InvalidRequestException, InvalidParameterException, InternalServiceErrorException, AwsServiceException, SdkClientException, SecretsManagerException {
        throw new UnsupportedOperationException();
    }

    default public UntagResourceResponse untagResource(Consumer<UntagResourceRequest.Builder> untagResourceRequest) throws ResourceNotFoundException, InvalidRequestException, InvalidParameterException, InternalServiceErrorException, AwsServiceException, SdkClientException, SecretsManagerException {
        return this.untagResource((UntagResourceRequest)((Object)((UntagResourceRequest.Builder)UntagResourceRequest.builder().applyMutation(untagResourceRequest)).build()));
    }

    default public UpdateSecretResponse updateSecret(UpdateSecretRequest updateSecretRequest) throws InvalidParameterException, InvalidRequestException, LimitExceededException, EncryptionFailureException, ResourceExistsException, ResourceNotFoundException, MalformedPolicyDocumentException, InternalServiceErrorException, PreconditionNotMetException, DecryptionFailureException, AwsServiceException, SdkClientException, SecretsManagerException {
        throw new UnsupportedOperationException();
    }

    default public UpdateSecretResponse updateSecret(Consumer<UpdateSecretRequest.Builder> updateSecretRequest) throws InvalidParameterException, InvalidRequestException, LimitExceededException, EncryptionFailureException, ResourceExistsException, ResourceNotFoundException, MalformedPolicyDocumentException, InternalServiceErrorException, PreconditionNotMetException, DecryptionFailureException, AwsServiceException, SdkClientException, SecretsManagerException {
        return this.updateSecret((UpdateSecretRequest)((Object)((UpdateSecretRequest.Builder)UpdateSecretRequest.builder().applyMutation(updateSecretRequest)).build()));
    }

    default public UpdateSecretVersionStageResponse updateSecretVersionStage(UpdateSecretVersionStageRequest updateSecretVersionStageRequest) throws ResourceNotFoundException, InvalidParameterException, InvalidRequestException, LimitExceededException, InternalServiceErrorException, AwsServiceException, SdkClientException, SecretsManagerException {
        throw new UnsupportedOperationException();
    }

    default public UpdateSecretVersionStageResponse updateSecretVersionStage(Consumer<UpdateSecretVersionStageRequest.Builder> updateSecretVersionStageRequest) throws ResourceNotFoundException, InvalidParameterException, InvalidRequestException, LimitExceededException, InternalServiceErrorException, AwsServiceException, SdkClientException, SecretsManagerException {
        return this.updateSecretVersionStage((UpdateSecretVersionStageRequest)((Object)((UpdateSecretVersionStageRequest.Builder)UpdateSecretVersionStageRequest.builder().applyMutation(updateSecretVersionStageRequest)).build()));
    }

    default public ValidateResourcePolicyResponse validateResourcePolicy(ValidateResourcePolicyRequest validateResourcePolicyRequest) throws MalformedPolicyDocumentException, ResourceNotFoundException, InvalidParameterException, InternalServiceErrorException, InvalidRequestException, AwsServiceException, SdkClientException, SecretsManagerException {
        throw new UnsupportedOperationException();
    }

    default public ValidateResourcePolicyResponse validateResourcePolicy(Consumer<ValidateResourcePolicyRequest.Builder> validateResourcePolicyRequest) throws MalformedPolicyDocumentException, ResourceNotFoundException, InvalidParameterException, InternalServiceErrorException, InvalidRequestException, AwsServiceException, SdkClientException, SecretsManagerException {
        return this.validateResourcePolicy((ValidateResourcePolicyRequest)((Object)((ValidateResourcePolicyRequest.Builder)ValidateResourcePolicyRequest.builder().applyMutation(validateResourcePolicyRequest)).build()));
    }

    public static SecretsManagerClient create() {
        return (SecretsManagerClient)SecretsManagerClient.builder().build();
    }

    public static SecretsManagerClientBuilder builder() {
        return new DefaultSecretsManagerClientBuilder();
    }

    public static ServiceMetadata serviceMetadata() {
        return ServiceMetadata.of((String)"secretsmanager");
    }

    default public SecretsManagerServiceClientConfiguration serviceClientConfiguration() {
        throw new UnsupportedOperationException();
    }
}

