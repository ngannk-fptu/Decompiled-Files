/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkPublicApi
 *  software.amazon.awssdk.annotations.ThreadSafe
 *  software.amazon.awssdk.awscore.AwsClient
 */
package software.amazon.awssdk.services.secretsmanager;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.annotations.ThreadSafe;
import software.amazon.awssdk.awscore.AwsClient;
import software.amazon.awssdk.services.secretsmanager.DefaultSecretsManagerAsyncClientBuilder;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerAsyncClientBuilder;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerServiceClientConfiguration;
import software.amazon.awssdk.services.secretsmanager.model.CancelRotateSecretRequest;
import software.amazon.awssdk.services.secretsmanager.model.CancelRotateSecretResponse;
import software.amazon.awssdk.services.secretsmanager.model.CreateSecretRequest;
import software.amazon.awssdk.services.secretsmanager.model.CreateSecretResponse;
import software.amazon.awssdk.services.secretsmanager.model.DeleteResourcePolicyRequest;
import software.amazon.awssdk.services.secretsmanager.model.DeleteResourcePolicyResponse;
import software.amazon.awssdk.services.secretsmanager.model.DeleteSecretRequest;
import software.amazon.awssdk.services.secretsmanager.model.DeleteSecretResponse;
import software.amazon.awssdk.services.secretsmanager.model.DescribeSecretRequest;
import software.amazon.awssdk.services.secretsmanager.model.DescribeSecretResponse;
import software.amazon.awssdk.services.secretsmanager.model.GetRandomPasswordRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetRandomPasswordResponse;
import software.amazon.awssdk.services.secretsmanager.model.GetResourcePolicyRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetResourcePolicyResponse;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;
import software.amazon.awssdk.services.secretsmanager.model.ListSecretVersionIdsRequest;
import software.amazon.awssdk.services.secretsmanager.model.ListSecretVersionIdsResponse;
import software.amazon.awssdk.services.secretsmanager.model.ListSecretsRequest;
import software.amazon.awssdk.services.secretsmanager.model.ListSecretsResponse;
import software.amazon.awssdk.services.secretsmanager.model.PutResourcePolicyRequest;
import software.amazon.awssdk.services.secretsmanager.model.PutResourcePolicyResponse;
import software.amazon.awssdk.services.secretsmanager.model.PutSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.PutSecretValueResponse;
import software.amazon.awssdk.services.secretsmanager.model.RemoveRegionsFromReplicationRequest;
import software.amazon.awssdk.services.secretsmanager.model.RemoveRegionsFromReplicationResponse;
import software.amazon.awssdk.services.secretsmanager.model.ReplicateSecretToRegionsRequest;
import software.amazon.awssdk.services.secretsmanager.model.ReplicateSecretToRegionsResponse;
import software.amazon.awssdk.services.secretsmanager.model.RestoreSecretRequest;
import software.amazon.awssdk.services.secretsmanager.model.RestoreSecretResponse;
import software.amazon.awssdk.services.secretsmanager.model.RotateSecretRequest;
import software.amazon.awssdk.services.secretsmanager.model.RotateSecretResponse;
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
import software.amazon.awssdk.services.secretsmanager.paginators.ListSecretVersionIdsPublisher;
import software.amazon.awssdk.services.secretsmanager.paginators.ListSecretsPublisher;

@SdkPublicApi
@ThreadSafe
public interface SecretsManagerAsyncClient
extends AwsClient {
    public static final String SERVICE_NAME = "secretsmanager";
    public static final String SERVICE_METADATA_ID = "secretsmanager";

    default public CompletableFuture<CancelRotateSecretResponse> cancelRotateSecret(CancelRotateSecretRequest cancelRotateSecretRequest) {
        throw new UnsupportedOperationException();
    }

    default public CompletableFuture<CancelRotateSecretResponse> cancelRotateSecret(Consumer<CancelRotateSecretRequest.Builder> cancelRotateSecretRequest) {
        return this.cancelRotateSecret((CancelRotateSecretRequest)((Object)((CancelRotateSecretRequest.Builder)CancelRotateSecretRequest.builder().applyMutation(cancelRotateSecretRequest)).build()));
    }

    default public CompletableFuture<CreateSecretResponse> createSecret(CreateSecretRequest createSecretRequest) {
        throw new UnsupportedOperationException();
    }

    default public CompletableFuture<CreateSecretResponse> createSecret(Consumer<CreateSecretRequest.Builder> createSecretRequest) {
        return this.createSecret((CreateSecretRequest)((Object)((CreateSecretRequest.Builder)CreateSecretRequest.builder().applyMutation(createSecretRequest)).build()));
    }

    default public CompletableFuture<DeleteResourcePolicyResponse> deleteResourcePolicy(DeleteResourcePolicyRequest deleteResourcePolicyRequest) {
        throw new UnsupportedOperationException();
    }

    default public CompletableFuture<DeleteResourcePolicyResponse> deleteResourcePolicy(Consumer<DeleteResourcePolicyRequest.Builder> deleteResourcePolicyRequest) {
        return this.deleteResourcePolicy((DeleteResourcePolicyRequest)((Object)((DeleteResourcePolicyRequest.Builder)DeleteResourcePolicyRequest.builder().applyMutation(deleteResourcePolicyRequest)).build()));
    }

    default public CompletableFuture<DeleteSecretResponse> deleteSecret(DeleteSecretRequest deleteSecretRequest) {
        throw new UnsupportedOperationException();
    }

    default public CompletableFuture<DeleteSecretResponse> deleteSecret(Consumer<DeleteSecretRequest.Builder> deleteSecretRequest) {
        return this.deleteSecret((DeleteSecretRequest)((Object)((DeleteSecretRequest.Builder)DeleteSecretRequest.builder().applyMutation(deleteSecretRequest)).build()));
    }

    default public CompletableFuture<DescribeSecretResponse> describeSecret(DescribeSecretRequest describeSecretRequest) {
        throw new UnsupportedOperationException();
    }

    default public CompletableFuture<DescribeSecretResponse> describeSecret(Consumer<DescribeSecretRequest.Builder> describeSecretRequest) {
        return this.describeSecret((DescribeSecretRequest)((Object)((DescribeSecretRequest.Builder)DescribeSecretRequest.builder().applyMutation(describeSecretRequest)).build()));
    }

    default public CompletableFuture<GetRandomPasswordResponse> getRandomPassword(GetRandomPasswordRequest getRandomPasswordRequest) {
        throw new UnsupportedOperationException();
    }

    default public CompletableFuture<GetRandomPasswordResponse> getRandomPassword(Consumer<GetRandomPasswordRequest.Builder> getRandomPasswordRequest) {
        return this.getRandomPassword((GetRandomPasswordRequest)((Object)((GetRandomPasswordRequest.Builder)GetRandomPasswordRequest.builder().applyMutation(getRandomPasswordRequest)).build()));
    }

    default public CompletableFuture<GetRandomPasswordResponse> getRandomPassword() {
        return this.getRandomPassword((GetRandomPasswordRequest)((Object)GetRandomPasswordRequest.builder().build()));
    }

    default public CompletableFuture<GetResourcePolicyResponse> getResourcePolicy(GetResourcePolicyRequest getResourcePolicyRequest) {
        throw new UnsupportedOperationException();
    }

    default public CompletableFuture<GetResourcePolicyResponse> getResourcePolicy(Consumer<GetResourcePolicyRequest.Builder> getResourcePolicyRequest) {
        return this.getResourcePolicy((GetResourcePolicyRequest)((Object)((GetResourcePolicyRequest.Builder)GetResourcePolicyRequest.builder().applyMutation(getResourcePolicyRequest)).build()));
    }

    default public CompletableFuture<GetSecretValueResponse> getSecretValue(GetSecretValueRequest getSecretValueRequest) {
        throw new UnsupportedOperationException();
    }

    default public CompletableFuture<GetSecretValueResponse> getSecretValue(Consumer<GetSecretValueRequest.Builder> getSecretValueRequest) {
        return this.getSecretValue((GetSecretValueRequest)((Object)((GetSecretValueRequest.Builder)GetSecretValueRequest.builder().applyMutation(getSecretValueRequest)).build()));
    }

    default public CompletableFuture<ListSecretVersionIdsResponse> listSecretVersionIds(ListSecretVersionIdsRequest listSecretVersionIdsRequest) {
        throw new UnsupportedOperationException();
    }

    default public CompletableFuture<ListSecretVersionIdsResponse> listSecretVersionIds(Consumer<ListSecretVersionIdsRequest.Builder> listSecretVersionIdsRequest) {
        return this.listSecretVersionIds((ListSecretVersionIdsRequest)((Object)((ListSecretVersionIdsRequest.Builder)ListSecretVersionIdsRequest.builder().applyMutation(listSecretVersionIdsRequest)).build()));
    }

    default public ListSecretVersionIdsPublisher listSecretVersionIdsPaginator(ListSecretVersionIdsRequest listSecretVersionIdsRequest) {
        return new ListSecretVersionIdsPublisher(this, listSecretVersionIdsRequest);
    }

    default public ListSecretVersionIdsPublisher listSecretVersionIdsPaginator(Consumer<ListSecretVersionIdsRequest.Builder> listSecretVersionIdsRequest) {
        return this.listSecretVersionIdsPaginator((ListSecretVersionIdsRequest)((Object)((ListSecretVersionIdsRequest.Builder)ListSecretVersionIdsRequest.builder().applyMutation(listSecretVersionIdsRequest)).build()));
    }

    default public CompletableFuture<ListSecretsResponse> listSecrets(ListSecretsRequest listSecretsRequest) {
        throw new UnsupportedOperationException();
    }

    default public CompletableFuture<ListSecretsResponse> listSecrets(Consumer<ListSecretsRequest.Builder> listSecretsRequest) {
        return this.listSecrets((ListSecretsRequest)((Object)((ListSecretsRequest.Builder)ListSecretsRequest.builder().applyMutation(listSecretsRequest)).build()));
    }

    default public CompletableFuture<ListSecretsResponse> listSecrets() {
        return this.listSecrets((ListSecretsRequest)((Object)ListSecretsRequest.builder().build()));
    }

    default public ListSecretsPublisher listSecretsPaginator() {
        return this.listSecretsPaginator((ListSecretsRequest)((Object)ListSecretsRequest.builder().build()));
    }

    default public ListSecretsPublisher listSecretsPaginator(ListSecretsRequest listSecretsRequest) {
        return new ListSecretsPublisher(this, listSecretsRequest);
    }

    default public ListSecretsPublisher listSecretsPaginator(Consumer<ListSecretsRequest.Builder> listSecretsRequest) {
        return this.listSecretsPaginator((ListSecretsRequest)((Object)((ListSecretsRequest.Builder)ListSecretsRequest.builder().applyMutation(listSecretsRequest)).build()));
    }

    default public CompletableFuture<PutResourcePolicyResponse> putResourcePolicy(PutResourcePolicyRequest putResourcePolicyRequest) {
        throw new UnsupportedOperationException();
    }

    default public CompletableFuture<PutResourcePolicyResponse> putResourcePolicy(Consumer<PutResourcePolicyRequest.Builder> putResourcePolicyRequest) {
        return this.putResourcePolicy((PutResourcePolicyRequest)((Object)((PutResourcePolicyRequest.Builder)PutResourcePolicyRequest.builder().applyMutation(putResourcePolicyRequest)).build()));
    }

    default public CompletableFuture<PutSecretValueResponse> putSecretValue(PutSecretValueRequest putSecretValueRequest) {
        throw new UnsupportedOperationException();
    }

    default public CompletableFuture<PutSecretValueResponse> putSecretValue(Consumer<PutSecretValueRequest.Builder> putSecretValueRequest) {
        return this.putSecretValue((PutSecretValueRequest)((Object)((PutSecretValueRequest.Builder)PutSecretValueRequest.builder().applyMutation(putSecretValueRequest)).build()));
    }

    default public CompletableFuture<RemoveRegionsFromReplicationResponse> removeRegionsFromReplication(RemoveRegionsFromReplicationRequest removeRegionsFromReplicationRequest) {
        throw new UnsupportedOperationException();
    }

    default public CompletableFuture<RemoveRegionsFromReplicationResponse> removeRegionsFromReplication(Consumer<RemoveRegionsFromReplicationRequest.Builder> removeRegionsFromReplicationRequest) {
        return this.removeRegionsFromReplication((RemoveRegionsFromReplicationRequest)((Object)((RemoveRegionsFromReplicationRequest.Builder)RemoveRegionsFromReplicationRequest.builder().applyMutation(removeRegionsFromReplicationRequest)).build()));
    }

    default public CompletableFuture<ReplicateSecretToRegionsResponse> replicateSecretToRegions(ReplicateSecretToRegionsRequest replicateSecretToRegionsRequest) {
        throw new UnsupportedOperationException();
    }

    default public CompletableFuture<ReplicateSecretToRegionsResponse> replicateSecretToRegions(Consumer<ReplicateSecretToRegionsRequest.Builder> replicateSecretToRegionsRequest) {
        return this.replicateSecretToRegions((ReplicateSecretToRegionsRequest)((Object)((ReplicateSecretToRegionsRequest.Builder)ReplicateSecretToRegionsRequest.builder().applyMutation(replicateSecretToRegionsRequest)).build()));
    }

    default public CompletableFuture<RestoreSecretResponse> restoreSecret(RestoreSecretRequest restoreSecretRequest) {
        throw new UnsupportedOperationException();
    }

    default public CompletableFuture<RestoreSecretResponse> restoreSecret(Consumer<RestoreSecretRequest.Builder> restoreSecretRequest) {
        return this.restoreSecret((RestoreSecretRequest)((Object)((RestoreSecretRequest.Builder)RestoreSecretRequest.builder().applyMutation(restoreSecretRequest)).build()));
    }

    default public CompletableFuture<RotateSecretResponse> rotateSecret(RotateSecretRequest rotateSecretRequest) {
        throw new UnsupportedOperationException();
    }

    default public CompletableFuture<RotateSecretResponse> rotateSecret(Consumer<RotateSecretRequest.Builder> rotateSecretRequest) {
        return this.rotateSecret((RotateSecretRequest)((Object)((RotateSecretRequest.Builder)RotateSecretRequest.builder().applyMutation(rotateSecretRequest)).build()));
    }

    default public CompletableFuture<StopReplicationToReplicaResponse> stopReplicationToReplica(StopReplicationToReplicaRequest stopReplicationToReplicaRequest) {
        throw new UnsupportedOperationException();
    }

    default public CompletableFuture<StopReplicationToReplicaResponse> stopReplicationToReplica(Consumer<StopReplicationToReplicaRequest.Builder> stopReplicationToReplicaRequest) {
        return this.stopReplicationToReplica((StopReplicationToReplicaRequest)((Object)((StopReplicationToReplicaRequest.Builder)StopReplicationToReplicaRequest.builder().applyMutation(stopReplicationToReplicaRequest)).build()));
    }

    default public CompletableFuture<TagResourceResponse> tagResource(TagResourceRequest tagResourceRequest) {
        throw new UnsupportedOperationException();
    }

    default public CompletableFuture<TagResourceResponse> tagResource(Consumer<TagResourceRequest.Builder> tagResourceRequest) {
        return this.tagResource((TagResourceRequest)((Object)((TagResourceRequest.Builder)TagResourceRequest.builder().applyMutation(tagResourceRequest)).build()));
    }

    default public CompletableFuture<UntagResourceResponse> untagResource(UntagResourceRequest untagResourceRequest) {
        throw new UnsupportedOperationException();
    }

    default public CompletableFuture<UntagResourceResponse> untagResource(Consumer<UntagResourceRequest.Builder> untagResourceRequest) {
        return this.untagResource((UntagResourceRequest)((Object)((UntagResourceRequest.Builder)UntagResourceRequest.builder().applyMutation(untagResourceRequest)).build()));
    }

    default public CompletableFuture<UpdateSecretResponse> updateSecret(UpdateSecretRequest updateSecretRequest) {
        throw new UnsupportedOperationException();
    }

    default public CompletableFuture<UpdateSecretResponse> updateSecret(Consumer<UpdateSecretRequest.Builder> updateSecretRequest) {
        return this.updateSecret((UpdateSecretRequest)((Object)((UpdateSecretRequest.Builder)UpdateSecretRequest.builder().applyMutation(updateSecretRequest)).build()));
    }

    default public CompletableFuture<UpdateSecretVersionStageResponse> updateSecretVersionStage(UpdateSecretVersionStageRequest updateSecretVersionStageRequest) {
        throw new UnsupportedOperationException();
    }

    default public CompletableFuture<UpdateSecretVersionStageResponse> updateSecretVersionStage(Consumer<UpdateSecretVersionStageRequest.Builder> updateSecretVersionStageRequest) {
        return this.updateSecretVersionStage((UpdateSecretVersionStageRequest)((Object)((UpdateSecretVersionStageRequest.Builder)UpdateSecretVersionStageRequest.builder().applyMutation(updateSecretVersionStageRequest)).build()));
    }

    default public CompletableFuture<ValidateResourcePolicyResponse> validateResourcePolicy(ValidateResourcePolicyRequest validateResourcePolicyRequest) {
        throw new UnsupportedOperationException();
    }

    default public CompletableFuture<ValidateResourcePolicyResponse> validateResourcePolicy(Consumer<ValidateResourcePolicyRequest.Builder> validateResourcePolicyRequest) {
        return this.validateResourcePolicy((ValidateResourcePolicyRequest)((Object)((ValidateResourcePolicyRequest.Builder)ValidateResourcePolicyRequest.builder().applyMutation(validateResourcePolicyRequest)).build()));
    }

    default public SecretsManagerServiceClientConfiguration serviceClientConfiguration() {
        throw new UnsupportedOperationException();
    }

    public static SecretsManagerAsyncClient create() {
        return (SecretsManagerAsyncClient)SecretsManagerAsyncClient.builder().build();
    }

    public static SecretsManagerAsyncClientBuilder builder() {
        return new DefaultSecretsManagerAsyncClientBuilder();
    }
}

