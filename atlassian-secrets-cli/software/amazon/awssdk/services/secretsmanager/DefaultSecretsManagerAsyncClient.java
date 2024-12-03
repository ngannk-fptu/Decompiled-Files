/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.services.secretsmanager;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.awscore.client.handler.AwsAsyncClientHandler;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.awscore.internal.AwsProtocolMetadata;
import software.amazon.awssdk.awscore.internal.AwsServiceProtocol;
import software.amazon.awssdk.core.RequestOverrideConfiguration;
import software.amazon.awssdk.core.SdkPlugin;
import software.amazon.awssdk.core.SdkRequest;
import software.amazon.awssdk.core.client.config.SdkClientConfiguration;
import software.amazon.awssdk.core.client.config.SdkClientOption;
import software.amazon.awssdk.core.client.handler.AsyncClientHandler;
import software.amazon.awssdk.core.client.handler.ClientExecutionParams;
import software.amazon.awssdk.core.http.HttpResponseHandler;
import software.amazon.awssdk.core.metrics.CoreMetric;
import software.amazon.awssdk.metrics.MetricCollector;
import software.amazon.awssdk.metrics.MetricPublisher;
import software.amazon.awssdk.metrics.NoOpMetricCollector;
import software.amazon.awssdk.protocols.core.ExceptionMetadata;
import software.amazon.awssdk.protocols.json.AwsJsonProtocol;
import software.amazon.awssdk.protocols.json.AwsJsonProtocolFactory;
import software.amazon.awssdk.protocols.json.BaseAwsJsonProtocolFactory;
import software.amazon.awssdk.protocols.json.JsonOperationMetadata;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerAsyncClient;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerServiceClientConfiguration;
import software.amazon.awssdk.services.secretsmanager.internal.SecretsManagerServiceClientConfigurationBuilder;
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
import software.amazon.awssdk.services.secretsmanager.transform.CancelRotateSecretRequestMarshaller;
import software.amazon.awssdk.services.secretsmanager.transform.CreateSecretRequestMarshaller;
import software.amazon.awssdk.services.secretsmanager.transform.DeleteResourcePolicyRequestMarshaller;
import software.amazon.awssdk.services.secretsmanager.transform.DeleteSecretRequestMarshaller;
import software.amazon.awssdk.services.secretsmanager.transform.DescribeSecretRequestMarshaller;
import software.amazon.awssdk.services.secretsmanager.transform.GetRandomPasswordRequestMarshaller;
import software.amazon.awssdk.services.secretsmanager.transform.GetResourcePolicyRequestMarshaller;
import software.amazon.awssdk.services.secretsmanager.transform.GetSecretValueRequestMarshaller;
import software.amazon.awssdk.services.secretsmanager.transform.ListSecretVersionIdsRequestMarshaller;
import software.amazon.awssdk.services.secretsmanager.transform.ListSecretsRequestMarshaller;
import software.amazon.awssdk.services.secretsmanager.transform.PutResourcePolicyRequestMarshaller;
import software.amazon.awssdk.services.secretsmanager.transform.PutSecretValueRequestMarshaller;
import software.amazon.awssdk.services.secretsmanager.transform.RemoveRegionsFromReplicationRequestMarshaller;
import software.amazon.awssdk.services.secretsmanager.transform.ReplicateSecretToRegionsRequestMarshaller;
import software.amazon.awssdk.services.secretsmanager.transform.RestoreSecretRequestMarshaller;
import software.amazon.awssdk.services.secretsmanager.transform.RotateSecretRequestMarshaller;
import software.amazon.awssdk.services.secretsmanager.transform.StopReplicationToReplicaRequestMarshaller;
import software.amazon.awssdk.services.secretsmanager.transform.TagResourceRequestMarshaller;
import software.amazon.awssdk.services.secretsmanager.transform.UntagResourceRequestMarshaller;
import software.amazon.awssdk.services.secretsmanager.transform.UpdateSecretRequestMarshaller;
import software.amazon.awssdk.services.secretsmanager.transform.UpdateSecretVersionStageRequestMarshaller;
import software.amazon.awssdk.services.secretsmanager.transform.ValidateResourcePolicyRequestMarshaller;
import software.amazon.awssdk.utils.CompletableFutureUtils;

@SdkInternalApi
final class DefaultSecretsManagerAsyncClient
implements SecretsManagerAsyncClient {
    private static final Logger log = LoggerFactory.getLogger(DefaultSecretsManagerAsyncClient.class);
    private static final AwsProtocolMetadata protocolMetadata = AwsProtocolMetadata.builder().serviceProtocol(AwsServiceProtocol.AWS_JSON).build();
    private final AsyncClientHandler clientHandler;
    private final AwsJsonProtocolFactory protocolFactory;
    private final SdkClientConfiguration clientConfiguration;
    private final SecretsManagerServiceClientConfiguration serviceClientConfiguration;

    protected DefaultSecretsManagerAsyncClient(SecretsManagerServiceClientConfiguration serviceClientConfiguration, SdkClientConfiguration clientConfiguration) {
        this.clientHandler = new AwsAsyncClientHandler(clientConfiguration);
        this.clientConfiguration = clientConfiguration;
        this.serviceClientConfiguration = serviceClientConfiguration;
        this.protocolFactory = this.init(AwsJsonProtocolFactory.builder()).build();
    }

    @Override
    public CompletableFuture<CancelRotateSecretResponse> cancelRotateSecret(CancelRotateSecretRequest cancelRotateSecretRequest) {
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration(cancelRotateSecretRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultSecretsManagerAsyncClient.resolveMetricPublishers(clientConfiguration, cancelRotateSecretRequest.overrideConfiguration().orElse(null));
        MetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create("ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, "Secrets Manager");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, "CancelRotateSecret");
            JsonOperationMetadata operationMetadata = JsonOperationMetadata.builder().hasStreamingSuccessResponse(false).isPayloadJson(true).build();
            HttpResponseHandler responseHandler = this.protocolFactory.createResponseHandler(operationMetadata, CancelRotateSecretResponse::builder);
            HttpResponseHandler<AwsServiceException> errorResponseHandler = this.createErrorResponseHandler(this.protocolFactory, operationMetadata);
            CompletableFuture<CancelRotateSecretResponse> executeFuture = this.clientHandler.execute(new ClientExecutionParams().withOperationName("CancelRotateSecret").withProtocolMetadata(protocolMetadata).withMarshaller(new CancelRotateSecretRequestMarshaller(this.protocolFactory)).withResponseHandler(responseHandler).withErrorResponseHandler(errorResponseHandler).withRequestConfiguration(clientConfiguration).withMetricCollector(apiCallMetricCollector).withInput(cancelRotateSecretRequest));
            CompletionStage whenCompleted = executeFuture.whenComplete((r, e) -> metricPublishers.forEach(p -> p.publish(apiCallMetricCollector.collect())));
            executeFuture = CompletableFutureUtils.forwardExceptionTo(whenCompleted, executeFuture);
            return executeFuture;
        }
        catch (Throwable t) {
            metricPublishers.forEach(p -> p.publish(apiCallMetricCollector.collect()));
            return CompletableFutureUtils.failedFuture(t);
        }
    }

    @Override
    public CompletableFuture<CreateSecretResponse> createSecret(CreateSecretRequest createSecretRequest) {
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration(createSecretRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultSecretsManagerAsyncClient.resolveMetricPublishers(clientConfiguration, createSecretRequest.overrideConfiguration().orElse(null));
        MetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create("ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, "Secrets Manager");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, "CreateSecret");
            JsonOperationMetadata operationMetadata = JsonOperationMetadata.builder().hasStreamingSuccessResponse(false).isPayloadJson(true).build();
            HttpResponseHandler responseHandler = this.protocolFactory.createResponseHandler(operationMetadata, CreateSecretResponse::builder);
            HttpResponseHandler<AwsServiceException> errorResponseHandler = this.createErrorResponseHandler(this.protocolFactory, operationMetadata);
            CompletableFuture<CreateSecretResponse> executeFuture = this.clientHandler.execute(new ClientExecutionParams().withOperationName("CreateSecret").withProtocolMetadata(protocolMetadata).withMarshaller(new CreateSecretRequestMarshaller(this.protocolFactory)).withResponseHandler(responseHandler).withErrorResponseHandler(errorResponseHandler).withRequestConfiguration(clientConfiguration).withMetricCollector(apiCallMetricCollector).withInput(createSecretRequest));
            CompletionStage whenCompleted = executeFuture.whenComplete((r, e) -> metricPublishers.forEach(p -> p.publish(apiCallMetricCollector.collect())));
            executeFuture = CompletableFutureUtils.forwardExceptionTo(whenCompleted, executeFuture);
            return executeFuture;
        }
        catch (Throwable t) {
            metricPublishers.forEach(p -> p.publish(apiCallMetricCollector.collect()));
            return CompletableFutureUtils.failedFuture(t);
        }
    }

    @Override
    public CompletableFuture<DeleteResourcePolicyResponse> deleteResourcePolicy(DeleteResourcePolicyRequest deleteResourcePolicyRequest) {
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration(deleteResourcePolicyRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultSecretsManagerAsyncClient.resolveMetricPublishers(clientConfiguration, deleteResourcePolicyRequest.overrideConfiguration().orElse(null));
        MetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create("ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, "Secrets Manager");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, "DeleteResourcePolicy");
            JsonOperationMetadata operationMetadata = JsonOperationMetadata.builder().hasStreamingSuccessResponse(false).isPayloadJson(true).build();
            HttpResponseHandler responseHandler = this.protocolFactory.createResponseHandler(operationMetadata, DeleteResourcePolicyResponse::builder);
            HttpResponseHandler<AwsServiceException> errorResponseHandler = this.createErrorResponseHandler(this.protocolFactory, operationMetadata);
            CompletableFuture<DeleteResourcePolicyResponse> executeFuture = this.clientHandler.execute(new ClientExecutionParams().withOperationName("DeleteResourcePolicy").withProtocolMetadata(protocolMetadata).withMarshaller(new DeleteResourcePolicyRequestMarshaller(this.protocolFactory)).withResponseHandler(responseHandler).withErrorResponseHandler(errorResponseHandler).withRequestConfiguration(clientConfiguration).withMetricCollector(apiCallMetricCollector).withInput(deleteResourcePolicyRequest));
            CompletionStage whenCompleted = executeFuture.whenComplete((r, e) -> metricPublishers.forEach(p -> p.publish(apiCallMetricCollector.collect())));
            executeFuture = CompletableFutureUtils.forwardExceptionTo(whenCompleted, executeFuture);
            return executeFuture;
        }
        catch (Throwable t) {
            metricPublishers.forEach(p -> p.publish(apiCallMetricCollector.collect()));
            return CompletableFutureUtils.failedFuture(t);
        }
    }

    @Override
    public CompletableFuture<DeleteSecretResponse> deleteSecret(DeleteSecretRequest deleteSecretRequest) {
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration(deleteSecretRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultSecretsManagerAsyncClient.resolveMetricPublishers(clientConfiguration, deleteSecretRequest.overrideConfiguration().orElse(null));
        MetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create("ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, "Secrets Manager");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, "DeleteSecret");
            JsonOperationMetadata operationMetadata = JsonOperationMetadata.builder().hasStreamingSuccessResponse(false).isPayloadJson(true).build();
            HttpResponseHandler responseHandler = this.protocolFactory.createResponseHandler(operationMetadata, DeleteSecretResponse::builder);
            HttpResponseHandler<AwsServiceException> errorResponseHandler = this.createErrorResponseHandler(this.protocolFactory, operationMetadata);
            CompletableFuture<DeleteSecretResponse> executeFuture = this.clientHandler.execute(new ClientExecutionParams().withOperationName("DeleteSecret").withProtocolMetadata(protocolMetadata).withMarshaller(new DeleteSecretRequestMarshaller(this.protocolFactory)).withResponseHandler(responseHandler).withErrorResponseHandler(errorResponseHandler).withRequestConfiguration(clientConfiguration).withMetricCollector(apiCallMetricCollector).withInput(deleteSecretRequest));
            CompletionStage whenCompleted = executeFuture.whenComplete((r, e) -> metricPublishers.forEach(p -> p.publish(apiCallMetricCollector.collect())));
            executeFuture = CompletableFutureUtils.forwardExceptionTo(whenCompleted, executeFuture);
            return executeFuture;
        }
        catch (Throwable t) {
            metricPublishers.forEach(p -> p.publish(apiCallMetricCollector.collect()));
            return CompletableFutureUtils.failedFuture(t);
        }
    }

    @Override
    public CompletableFuture<DescribeSecretResponse> describeSecret(DescribeSecretRequest describeSecretRequest) {
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration(describeSecretRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultSecretsManagerAsyncClient.resolveMetricPublishers(clientConfiguration, describeSecretRequest.overrideConfiguration().orElse(null));
        MetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create("ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, "Secrets Manager");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, "DescribeSecret");
            JsonOperationMetadata operationMetadata = JsonOperationMetadata.builder().hasStreamingSuccessResponse(false).isPayloadJson(true).build();
            HttpResponseHandler responseHandler = this.protocolFactory.createResponseHandler(operationMetadata, DescribeSecretResponse::builder);
            HttpResponseHandler<AwsServiceException> errorResponseHandler = this.createErrorResponseHandler(this.protocolFactory, operationMetadata);
            CompletableFuture<DescribeSecretResponse> executeFuture = this.clientHandler.execute(new ClientExecutionParams().withOperationName("DescribeSecret").withProtocolMetadata(protocolMetadata).withMarshaller(new DescribeSecretRequestMarshaller(this.protocolFactory)).withResponseHandler(responseHandler).withErrorResponseHandler(errorResponseHandler).withRequestConfiguration(clientConfiguration).withMetricCollector(apiCallMetricCollector).withInput(describeSecretRequest));
            CompletionStage whenCompleted = executeFuture.whenComplete((r, e) -> metricPublishers.forEach(p -> p.publish(apiCallMetricCollector.collect())));
            executeFuture = CompletableFutureUtils.forwardExceptionTo(whenCompleted, executeFuture);
            return executeFuture;
        }
        catch (Throwable t) {
            metricPublishers.forEach(p -> p.publish(apiCallMetricCollector.collect()));
            return CompletableFutureUtils.failedFuture(t);
        }
    }

    @Override
    public CompletableFuture<GetRandomPasswordResponse> getRandomPassword(GetRandomPasswordRequest getRandomPasswordRequest) {
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration(getRandomPasswordRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultSecretsManagerAsyncClient.resolveMetricPublishers(clientConfiguration, getRandomPasswordRequest.overrideConfiguration().orElse(null));
        MetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create("ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, "Secrets Manager");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, "GetRandomPassword");
            JsonOperationMetadata operationMetadata = JsonOperationMetadata.builder().hasStreamingSuccessResponse(false).isPayloadJson(true).build();
            HttpResponseHandler responseHandler = this.protocolFactory.createResponseHandler(operationMetadata, GetRandomPasswordResponse::builder);
            HttpResponseHandler<AwsServiceException> errorResponseHandler = this.createErrorResponseHandler(this.protocolFactory, operationMetadata);
            CompletableFuture<GetRandomPasswordResponse> executeFuture = this.clientHandler.execute(new ClientExecutionParams().withOperationName("GetRandomPassword").withProtocolMetadata(protocolMetadata).withMarshaller(new GetRandomPasswordRequestMarshaller(this.protocolFactory)).withResponseHandler(responseHandler).withErrorResponseHandler(errorResponseHandler).withRequestConfiguration(clientConfiguration).withMetricCollector(apiCallMetricCollector).withInput(getRandomPasswordRequest));
            CompletionStage whenCompleted = executeFuture.whenComplete((r, e) -> metricPublishers.forEach(p -> p.publish(apiCallMetricCollector.collect())));
            executeFuture = CompletableFutureUtils.forwardExceptionTo(whenCompleted, executeFuture);
            return executeFuture;
        }
        catch (Throwable t) {
            metricPublishers.forEach(p -> p.publish(apiCallMetricCollector.collect()));
            return CompletableFutureUtils.failedFuture(t);
        }
    }

    @Override
    public CompletableFuture<GetResourcePolicyResponse> getResourcePolicy(GetResourcePolicyRequest getResourcePolicyRequest) {
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration(getResourcePolicyRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultSecretsManagerAsyncClient.resolveMetricPublishers(clientConfiguration, getResourcePolicyRequest.overrideConfiguration().orElse(null));
        MetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create("ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, "Secrets Manager");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, "GetResourcePolicy");
            JsonOperationMetadata operationMetadata = JsonOperationMetadata.builder().hasStreamingSuccessResponse(false).isPayloadJson(true).build();
            HttpResponseHandler responseHandler = this.protocolFactory.createResponseHandler(operationMetadata, GetResourcePolicyResponse::builder);
            HttpResponseHandler<AwsServiceException> errorResponseHandler = this.createErrorResponseHandler(this.protocolFactory, operationMetadata);
            CompletableFuture<GetResourcePolicyResponse> executeFuture = this.clientHandler.execute(new ClientExecutionParams().withOperationName("GetResourcePolicy").withProtocolMetadata(protocolMetadata).withMarshaller(new GetResourcePolicyRequestMarshaller(this.protocolFactory)).withResponseHandler(responseHandler).withErrorResponseHandler(errorResponseHandler).withRequestConfiguration(clientConfiguration).withMetricCollector(apiCallMetricCollector).withInput(getResourcePolicyRequest));
            CompletionStage whenCompleted = executeFuture.whenComplete((r, e) -> metricPublishers.forEach(p -> p.publish(apiCallMetricCollector.collect())));
            executeFuture = CompletableFutureUtils.forwardExceptionTo(whenCompleted, executeFuture);
            return executeFuture;
        }
        catch (Throwable t) {
            metricPublishers.forEach(p -> p.publish(apiCallMetricCollector.collect()));
            return CompletableFutureUtils.failedFuture(t);
        }
    }

    @Override
    public CompletableFuture<GetSecretValueResponse> getSecretValue(GetSecretValueRequest getSecretValueRequest) {
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration(getSecretValueRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultSecretsManagerAsyncClient.resolveMetricPublishers(clientConfiguration, getSecretValueRequest.overrideConfiguration().orElse(null));
        MetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create("ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, "Secrets Manager");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, "GetSecretValue");
            JsonOperationMetadata operationMetadata = JsonOperationMetadata.builder().hasStreamingSuccessResponse(false).isPayloadJson(true).build();
            HttpResponseHandler responseHandler = this.protocolFactory.createResponseHandler(operationMetadata, GetSecretValueResponse::builder);
            HttpResponseHandler<AwsServiceException> errorResponseHandler = this.createErrorResponseHandler(this.protocolFactory, operationMetadata);
            CompletableFuture<GetSecretValueResponse> executeFuture = this.clientHandler.execute(new ClientExecutionParams().withOperationName("GetSecretValue").withProtocolMetadata(protocolMetadata).withMarshaller(new GetSecretValueRequestMarshaller(this.protocolFactory)).withResponseHandler(responseHandler).withErrorResponseHandler(errorResponseHandler).withRequestConfiguration(clientConfiguration).withMetricCollector(apiCallMetricCollector).withInput(getSecretValueRequest));
            CompletionStage whenCompleted = executeFuture.whenComplete((r, e) -> metricPublishers.forEach(p -> p.publish(apiCallMetricCollector.collect())));
            executeFuture = CompletableFutureUtils.forwardExceptionTo(whenCompleted, executeFuture);
            return executeFuture;
        }
        catch (Throwable t) {
            metricPublishers.forEach(p -> p.publish(apiCallMetricCollector.collect()));
            return CompletableFutureUtils.failedFuture(t);
        }
    }

    @Override
    public CompletableFuture<ListSecretVersionIdsResponse> listSecretVersionIds(ListSecretVersionIdsRequest listSecretVersionIdsRequest) {
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration(listSecretVersionIdsRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultSecretsManagerAsyncClient.resolveMetricPublishers(clientConfiguration, listSecretVersionIdsRequest.overrideConfiguration().orElse(null));
        MetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create("ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, "Secrets Manager");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, "ListSecretVersionIds");
            JsonOperationMetadata operationMetadata = JsonOperationMetadata.builder().hasStreamingSuccessResponse(false).isPayloadJson(true).build();
            HttpResponseHandler responseHandler = this.protocolFactory.createResponseHandler(operationMetadata, ListSecretVersionIdsResponse::builder);
            HttpResponseHandler<AwsServiceException> errorResponseHandler = this.createErrorResponseHandler(this.protocolFactory, operationMetadata);
            CompletableFuture<ListSecretVersionIdsResponse> executeFuture = this.clientHandler.execute(new ClientExecutionParams().withOperationName("ListSecretVersionIds").withProtocolMetadata(protocolMetadata).withMarshaller(new ListSecretVersionIdsRequestMarshaller(this.protocolFactory)).withResponseHandler(responseHandler).withErrorResponseHandler(errorResponseHandler).withRequestConfiguration(clientConfiguration).withMetricCollector(apiCallMetricCollector).withInput(listSecretVersionIdsRequest));
            CompletionStage whenCompleted = executeFuture.whenComplete((r, e) -> metricPublishers.forEach(p -> p.publish(apiCallMetricCollector.collect())));
            executeFuture = CompletableFutureUtils.forwardExceptionTo(whenCompleted, executeFuture);
            return executeFuture;
        }
        catch (Throwable t) {
            metricPublishers.forEach(p -> p.publish(apiCallMetricCollector.collect()));
            return CompletableFutureUtils.failedFuture(t);
        }
    }

    @Override
    public CompletableFuture<ListSecretsResponse> listSecrets(ListSecretsRequest listSecretsRequest) {
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration(listSecretsRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultSecretsManagerAsyncClient.resolveMetricPublishers(clientConfiguration, listSecretsRequest.overrideConfiguration().orElse(null));
        MetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create("ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, "Secrets Manager");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, "ListSecrets");
            JsonOperationMetadata operationMetadata = JsonOperationMetadata.builder().hasStreamingSuccessResponse(false).isPayloadJson(true).build();
            HttpResponseHandler responseHandler = this.protocolFactory.createResponseHandler(operationMetadata, ListSecretsResponse::builder);
            HttpResponseHandler<AwsServiceException> errorResponseHandler = this.createErrorResponseHandler(this.protocolFactory, operationMetadata);
            CompletableFuture<ListSecretsResponse> executeFuture = this.clientHandler.execute(new ClientExecutionParams().withOperationName("ListSecrets").withProtocolMetadata(protocolMetadata).withMarshaller(new ListSecretsRequestMarshaller(this.protocolFactory)).withResponseHandler(responseHandler).withErrorResponseHandler(errorResponseHandler).withRequestConfiguration(clientConfiguration).withMetricCollector(apiCallMetricCollector).withInput(listSecretsRequest));
            CompletionStage whenCompleted = executeFuture.whenComplete((r, e) -> metricPublishers.forEach(p -> p.publish(apiCallMetricCollector.collect())));
            executeFuture = CompletableFutureUtils.forwardExceptionTo(whenCompleted, executeFuture);
            return executeFuture;
        }
        catch (Throwable t) {
            metricPublishers.forEach(p -> p.publish(apiCallMetricCollector.collect()));
            return CompletableFutureUtils.failedFuture(t);
        }
    }

    @Override
    public CompletableFuture<PutResourcePolicyResponse> putResourcePolicy(PutResourcePolicyRequest putResourcePolicyRequest) {
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration(putResourcePolicyRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultSecretsManagerAsyncClient.resolveMetricPublishers(clientConfiguration, putResourcePolicyRequest.overrideConfiguration().orElse(null));
        MetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create("ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, "Secrets Manager");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, "PutResourcePolicy");
            JsonOperationMetadata operationMetadata = JsonOperationMetadata.builder().hasStreamingSuccessResponse(false).isPayloadJson(true).build();
            HttpResponseHandler responseHandler = this.protocolFactory.createResponseHandler(operationMetadata, PutResourcePolicyResponse::builder);
            HttpResponseHandler<AwsServiceException> errorResponseHandler = this.createErrorResponseHandler(this.protocolFactory, operationMetadata);
            CompletableFuture<PutResourcePolicyResponse> executeFuture = this.clientHandler.execute(new ClientExecutionParams().withOperationName("PutResourcePolicy").withProtocolMetadata(protocolMetadata).withMarshaller(new PutResourcePolicyRequestMarshaller(this.protocolFactory)).withResponseHandler(responseHandler).withErrorResponseHandler(errorResponseHandler).withRequestConfiguration(clientConfiguration).withMetricCollector(apiCallMetricCollector).withInput(putResourcePolicyRequest));
            CompletionStage whenCompleted = executeFuture.whenComplete((r, e) -> metricPublishers.forEach(p -> p.publish(apiCallMetricCollector.collect())));
            executeFuture = CompletableFutureUtils.forwardExceptionTo(whenCompleted, executeFuture);
            return executeFuture;
        }
        catch (Throwable t) {
            metricPublishers.forEach(p -> p.publish(apiCallMetricCollector.collect()));
            return CompletableFutureUtils.failedFuture(t);
        }
    }

    @Override
    public CompletableFuture<PutSecretValueResponse> putSecretValue(PutSecretValueRequest putSecretValueRequest) {
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration(putSecretValueRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultSecretsManagerAsyncClient.resolveMetricPublishers(clientConfiguration, putSecretValueRequest.overrideConfiguration().orElse(null));
        MetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create("ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, "Secrets Manager");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, "PutSecretValue");
            JsonOperationMetadata operationMetadata = JsonOperationMetadata.builder().hasStreamingSuccessResponse(false).isPayloadJson(true).build();
            HttpResponseHandler responseHandler = this.protocolFactory.createResponseHandler(operationMetadata, PutSecretValueResponse::builder);
            HttpResponseHandler<AwsServiceException> errorResponseHandler = this.createErrorResponseHandler(this.protocolFactory, operationMetadata);
            CompletableFuture<PutSecretValueResponse> executeFuture = this.clientHandler.execute(new ClientExecutionParams().withOperationName("PutSecretValue").withProtocolMetadata(protocolMetadata).withMarshaller(new PutSecretValueRequestMarshaller(this.protocolFactory)).withResponseHandler(responseHandler).withErrorResponseHandler(errorResponseHandler).withRequestConfiguration(clientConfiguration).withMetricCollector(apiCallMetricCollector).withInput(putSecretValueRequest));
            CompletionStage whenCompleted = executeFuture.whenComplete((r, e) -> metricPublishers.forEach(p -> p.publish(apiCallMetricCollector.collect())));
            executeFuture = CompletableFutureUtils.forwardExceptionTo(whenCompleted, executeFuture);
            return executeFuture;
        }
        catch (Throwable t) {
            metricPublishers.forEach(p -> p.publish(apiCallMetricCollector.collect()));
            return CompletableFutureUtils.failedFuture(t);
        }
    }

    @Override
    public CompletableFuture<RemoveRegionsFromReplicationResponse> removeRegionsFromReplication(RemoveRegionsFromReplicationRequest removeRegionsFromReplicationRequest) {
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration(removeRegionsFromReplicationRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultSecretsManagerAsyncClient.resolveMetricPublishers(clientConfiguration, removeRegionsFromReplicationRequest.overrideConfiguration().orElse(null));
        MetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create("ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, "Secrets Manager");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, "RemoveRegionsFromReplication");
            JsonOperationMetadata operationMetadata = JsonOperationMetadata.builder().hasStreamingSuccessResponse(false).isPayloadJson(true).build();
            HttpResponseHandler responseHandler = this.protocolFactory.createResponseHandler(operationMetadata, RemoveRegionsFromReplicationResponse::builder);
            HttpResponseHandler<AwsServiceException> errorResponseHandler = this.createErrorResponseHandler(this.protocolFactory, operationMetadata);
            CompletableFuture<RemoveRegionsFromReplicationResponse> executeFuture = this.clientHandler.execute(new ClientExecutionParams().withOperationName("RemoveRegionsFromReplication").withProtocolMetadata(protocolMetadata).withMarshaller(new RemoveRegionsFromReplicationRequestMarshaller(this.protocolFactory)).withResponseHandler(responseHandler).withErrorResponseHandler(errorResponseHandler).withRequestConfiguration(clientConfiguration).withMetricCollector(apiCallMetricCollector).withInput(removeRegionsFromReplicationRequest));
            CompletionStage whenCompleted = executeFuture.whenComplete((r, e) -> metricPublishers.forEach(p -> p.publish(apiCallMetricCollector.collect())));
            executeFuture = CompletableFutureUtils.forwardExceptionTo(whenCompleted, executeFuture);
            return executeFuture;
        }
        catch (Throwable t) {
            metricPublishers.forEach(p -> p.publish(apiCallMetricCollector.collect()));
            return CompletableFutureUtils.failedFuture(t);
        }
    }

    @Override
    public CompletableFuture<ReplicateSecretToRegionsResponse> replicateSecretToRegions(ReplicateSecretToRegionsRequest replicateSecretToRegionsRequest) {
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration(replicateSecretToRegionsRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultSecretsManagerAsyncClient.resolveMetricPublishers(clientConfiguration, replicateSecretToRegionsRequest.overrideConfiguration().orElse(null));
        MetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create("ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, "Secrets Manager");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, "ReplicateSecretToRegions");
            JsonOperationMetadata operationMetadata = JsonOperationMetadata.builder().hasStreamingSuccessResponse(false).isPayloadJson(true).build();
            HttpResponseHandler responseHandler = this.protocolFactory.createResponseHandler(operationMetadata, ReplicateSecretToRegionsResponse::builder);
            HttpResponseHandler<AwsServiceException> errorResponseHandler = this.createErrorResponseHandler(this.protocolFactory, operationMetadata);
            CompletableFuture<ReplicateSecretToRegionsResponse> executeFuture = this.clientHandler.execute(new ClientExecutionParams().withOperationName("ReplicateSecretToRegions").withProtocolMetadata(protocolMetadata).withMarshaller(new ReplicateSecretToRegionsRequestMarshaller(this.protocolFactory)).withResponseHandler(responseHandler).withErrorResponseHandler(errorResponseHandler).withRequestConfiguration(clientConfiguration).withMetricCollector(apiCallMetricCollector).withInput(replicateSecretToRegionsRequest));
            CompletionStage whenCompleted = executeFuture.whenComplete((r, e) -> metricPublishers.forEach(p -> p.publish(apiCallMetricCollector.collect())));
            executeFuture = CompletableFutureUtils.forwardExceptionTo(whenCompleted, executeFuture);
            return executeFuture;
        }
        catch (Throwable t) {
            metricPublishers.forEach(p -> p.publish(apiCallMetricCollector.collect()));
            return CompletableFutureUtils.failedFuture(t);
        }
    }

    @Override
    public CompletableFuture<RestoreSecretResponse> restoreSecret(RestoreSecretRequest restoreSecretRequest) {
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration(restoreSecretRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultSecretsManagerAsyncClient.resolveMetricPublishers(clientConfiguration, restoreSecretRequest.overrideConfiguration().orElse(null));
        MetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create("ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, "Secrets Manager");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, "RestoreSecret");
            JsonOperationMetadata operationMetadata = JsonOperationMetadata.builder().hasStreamingSuccessResponse(false).isPayloadJson(true).build();
            HttpResponseHandler responseHandler = this.protocolFactory.createResponseHandler(operationMetadata, RestoreSecretResponse::builder);
            HttpResponseHandler<AwsServiceException> errorResponseHandler = this.createErrorResponseHandler(this.protocolFactory, operationMetadata);
            CompletableFuture<RestoreSecretResponse> executeFuture = this.clientHandler.execute(new ClientExecutionParams().withOperationName("RestoreSecret").withProtocolMetadata(protocolMetadata).withMarshaller(new RestoreSecretRequestMarshaller(this.protocolFactory)).withResponseHandler(responseHandler).withErrorResponseHandler(errorResponseHandler).withRequestConfiguration(clientConfiguration).withMetricCollector(apiCallMetricCollector).withInput(restoreSecretRequest));
            CompletionStage whenCompleted = executeFuture.whenComplete((r, e) -> metricPublishers.forEach(p -> p.publish(apiCallMetricCollector.collect())));
            executeFuture = CompletableFutureUtils.forwardExceptionTo(whenCompleted, executeFuture);
            return executeFuture;
        }
        catch (Throwable t) {
            metricPublishers.forEach(p -> p.publish(apiCallMetricCollector.collect()));
            return CompletableFutureUtils.failedFuture(t);
        }
    }

    @Override
    public CompletableFuture<RotateSecretResponse> rotateSecret(RotateSecretRequest rotateSecretRequest) {
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration(rotateSecretRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultSecretsManagerAsyncClient.resolveMetricPublishers(clientConfiguration, rotateSecretRequest.overrideConfiguration().orElse(null));
        MetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create("ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, "Secrets Manager");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, "RotateSecret");
            JsonOperationMetadata operationMetadata = JsonOperationMetadata.builder().hasStreamingSuccessResponse(false).isPayloadJson(true).build();
            HttpResponseHandler responseHandler = this.protocolFactory.createResponseHandler(operationMetadata, RotateSecretResponse::builder);
            HttpResponseHandler<AwsServiceException> errorResponseHandler = this.createErrorResponseHandler(this.protocolFactory, operationMetadata);
            CompletableFuture<RotateSecretResponse> executeFuture = this.clientHandler.execute(new ClientExecutionParams().withOperationName("RotateSecret").withProtocolMetadata(protocolMetadata).withMarshaller(new RotateSecretRequestMarshaller(this.protocolFactory)).withResponseHandler(responseHandler).withErrorResponseHandler(errorResponseHandler).withRequestConfiguration(clientConfiguration).withMetricCollector(apiCallMetricCollector).withInput(rotateSecretRequest));
            CompletionStage whenCompleted = executeFuture.whenComplete((r, e) -> metricPublishers.forEach(p -> p.publish(apiCallMetricCollector.collect())));
            executeFuture = CompletableFutureUtils.forwardExceptionTo(whenCompleted, executeFuture);
            return executeFuture;
        }
        catch (Throwable t) {
            metricPublishers.forEach(p -> p.publish(apiCallMetricCollector.collect()));
            return CompletableFutureUtils.failedFuture(t);
        }
    }

    @Override
    public CompletableFuture<StopReplicationToReplicaResponse> stopReplicationToReplica(StopReplicationToReplicaRequest stopReplicationToReplicaRequest) {
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration(stopReplicationToReplicaRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultSecretsManagerAsyncClient.resolveMetricPublishers(clientConfiguration, stopReplicationToReplicaRequest.overrideConfiguration().orElse(null));
        MetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create("ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, "Secrets Manager");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, "StopReplicationToReplica");
            JsonOperationMetadata operationMetadata = JsonOperationMetadata.builder().hasStreamingSuccessResponse(false).isPayloadJson(true).build();
            HttpResponseHandler responseHandler = this.protocolFactory.createResponseHandler(operationMetadata, StopReplicationToReplicaResponse::builder);
            HttpResponseHandler<AwsServiceException> errorResponseHandler = this.createErrorResponseHandler(this.protocolFactory, operationMetadata);
            CompletableFuture<StopReplicationToReplicaResponse> executeFuture = this.clientHandler.execute(new ClientExecutionParams().withOperationName("StopReplicationToReplica").withProtocolMetadata(protocolMetadata).withMarshaller(new StopReplicationToReplicaRequestMarshaller(this.protocolFactory)).withResponseHandler(responseHandler).withErrorResponseHandler(errorResponseHandler).withRequestConfiguration(clientConfiguration).withMetricCollector(apiCallMetricCollector).withInput(stopReplicationToReplicaRequest));
            CompletionStage whenCompleted = executeFuture.whenComplete((r, e) -> metricPublishers.forEach(p -> p.publish(apiCallMetricCollector.collect())));
            executeFuture = CompletableFutureUtils.forwardExceptionTo(whenCompleted, executeFuture);
            return executeFuture;
        }
        catch (Throwable t) {
            metricPublishers.forEach(p -> p.publish(apiCallMetricCollector.collect()));
            return CompletableFutureUtils.failedFuture(t);
        }
    }

    @Override
    public CompletableFuture<TagResourceResponse> tagResource(TagResourceRequest tagResourceRequest) {
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration(tagResourceRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultSecretsManagerAsyncClient.resolveMetricPublishers(clientConfiguration, tagResourceRequest.overrideConfiguration().orElse(null));
        MetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create("ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, "Secrets Manager");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, "TagResource");
            JsonOperationMetadata operationMetadata = JsonOperationMetadata.builder().hasStreamingSuccessResponse(false).isPayloadJson(true).build();
            HttpResponseHandler responseHandler = this.protocolFactory.createResponseHandler(operationMetadata, TagResourceResponse::builder);
            HttpResponseHandler<AwsServiceException> errorResponseHandler = this.createErrorResponseHandler(this.protocolFactory, operationMetadata);
            CompletableFuture<TagResourceResponse> executeFuture = this.clientHandler.execute(new ClientExecutionParams().withOperationName("TagResource").withProtocolMetadata(protocolMetadata).withMarshaller(new TagResourceRequestMarshaller(this.protocolFactory)).withResponseHandler(responseHandler).withErrorResponseHandler(errorResponseHandler).withRequestConfiguration(clientConfiguration).withMetricCollector(apiCallMetricCollector).withInput(tagResourceRequest));
            CompletionStage whenCompleted = executeFuture.whenComplete((r, e) -> metricPublishers.forEach(p -> p.publish(apiCallMetricCollector.collect())));
            executeFuture = CompletableFutureUtils.forwardExceptionTo(whenCompleted, executeFuture);
            return executeFuture;
        }
        catch (Throwable t) {
            metricPublishers.forEach(p -> p.publish(apiCallMetricCollector.collect()));
            return CompletableFutureUtils.failedFuture(t);
        }
    }

    @Override
    public CompletableFuture<UntagResourceResponse> untagResource(UntagResourceRequest untagResourceRequest) {
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration(untagResourceRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultSecretsManagerAsyncClient.resolveMetricPublishers(clientConfiguration, untagResourceRequest.overrideConfiguration().orElse(null));
        MetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create("ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, "Secrets Manager");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, "UntagResource");
            JsonOperationMetadata operationMetadata = JsonOperationMetadata.builder().hasStreamingSuccessResponse(false).isPayloadJson(true).build();
            HttpResponseHandler responseHandler = this.protocolFactory.createResponseHandler(operationMetadata, UntagResourceResponse::builder);
            HttpResponseHandler<AwsServiceException> errorResponseHandler = this.createErrorResponseHandler(this.protocolFactory, operationMetadata);
            CompletableFuture<UntagResourceResponse> executeFuture = this.clientHandler.execute(new ClientExecutionParams().withOperationName("UntagResource").withProtocolMetadata(protocolMetadata).withMarshaller(new UntagResourceRequestMarshaller(this.protocolFactory)).withResponseHandler(responseHandler).withErrorResponseHandler(errorResponseHandler).withRequestConfiguration(clientConfiguration).withMetricCollector(apiCallMetricCollector).withInput(untagResourceRequest));
            CompletionStage whenCompleted = executeFuture.whenComplete((r, e) -> metricPublishers.forEach(p -> p.publish(apiCallMetricCollector.collect())));
            executeFuture = CompletableFutureUtils.forwardExceptionTo(whenCompleted, executeFuture);
            return executeFuture;
        }
        catch (Throwable t) {
            metricPublishers.forEach(p -> p.publish(apiCallMetricCollector.collect()));
            return CompletableFutureUtils.failedFuture(t);
        }
    }

    @Override
    public CompletableFuture<UpdateSecretResponse> updateSecret(UpdateSecretRequest updateSecretRequest) {
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration(updateSecretRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultSecretsManagerAsyncClient.resolveMetricPublishers(clientConfiguration, updateSecretRequest.overrideConfiguration().orElse(null));
        MetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create("ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, "Secrets Manager");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, "UpdateSecret");
            JsonOperationMetadata operationMetadata = JsonOperationMetadata.builder().hasStreamingSuccessResponse(false).isPayloadJson(true).build();
            HttpResponseHandler responseHandler = this.protocolFactory.createResponseHandler(operationMetadata, UpdateSecretResponse::builder);
            HttpResponseHandler<AwsServiceException> errorResponseHandler = this.createErrorResponseHandler(this.protocolFactory, operationMetadata);
            CompletableFuture<UpdateSecretResponse> executeFuture = this.clientHandler.execute(new ClientExecutionParams().withOperationName("UpdateSecret").withProtocolMetadata(protocolMetadata).withMarshaller(new UpdateSecretRequestMarshaller(this.protocolFactory)).withResponseHandler(responseHandler).withErrorResponseHandler(errorResponseHandler).withRequestConfiguration(clientConfiguration).withMetricCollector(apiCallMetricCollector).withInput(updateSecretRequest));
            CompletionStage whenCompleted = executeFuture.whenComplete((r, e) -> metricPublishers.forEach(p -> p.publish(apiCallMetricCollector.collect())));
            executeFuture = CompletableFutureUtils.forwardExceptionTo(whenCompleted, executeFuture);
            return executeFuture;
        }
        catch (Throwable t) {
            metricPublishers.forEach(p -> p.publish(apiCallMetricCollector.collect()));
            return CompletableFutureUtils.failedFuture(t);
        }
    }

    @Override
    public CompletableFuture<UpdateSecretVersionStageResponse> updateSecretVersionStage(UpdateSecretVersionStageRequest updateSecretVersionStageRequest) {
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration(updateSecretVersionStageRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultSecretsManagerAsyncClient.resolveMetricPublishers(clientConfiguration, updateSecretVersionStageRequest.overrideConfiguration().orElse(null));
        MetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create("ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, "Secrets Manager");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, "UpdateSecretVersionStage");
            JsonOperationMetadata operationMetadata = JsonOperationMetadata.builder().hasStreamingSuccessResponse(false).isPayloadJson(true).build();
            HttpResponseHandler responseHandler = this.protocolFactory.createResponseHandler(operationMetadata, UpdateSecretVersionStageResponse::builder);
            HttpResponseHandler<AwsServiceException> errorResponseHandler = this.createErrorResponseHandler(this.protocolFactory, operationMetadata);
            CompletableFuture<UpdateSecretVersionStageResponse> executeFuture = this.clientHandler.execute(new ClientExecutionParams().withOperationName("UpdateSecretVersionStage").withProtocolMetadata(protocolMetadata).withMarshaller(new UpdateSecretVersionStageRequestMarshaller(this.protocolFactory)).withResponseHandler(responseHandler).withErrorResponseHandler(errorResponseHandler).withRequestConfiguration(clientConfiguration).withMetricCollector(apiCallMetricCollector).withInput(updateSecretVersionStageRequest));
            CompletionStage whenCompleted = executeFuture.whenComplete((r, e) -> metricPublishers.forEach(p -> p.publish(apiCallMetricCollector.collect())));
            executeFuture = CompletableFutureUtils.forwardExceptionTo(whenCompleted, executeFuture);
            return executeFuture;
        }
        catch (Throwable t) {
            metricPublishers.forEach(p -> p.publish(apiCallMetricCollector.collect()));
            return CompletableFutureUtils.failedFuture(t);
        }
    }

    @Override
    public CompletableFuture<ValidateResourcePolicyResponse> validateResourcePolicy(ValidateResourcePolicyRequest validateResourcePolicyRequest) {
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration(validateResourcePolicyRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultSecretsManagerAsyncClient.resolveMetricPublishers(clientConfiguration, validateResourcePolicyRequest.overrideConfiguration().orElse(null));
        MetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create("ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, "Secrets Manager");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, "ValidateResourcePolicy");
            JsonOperationMetadata operationMetadata = JsonOperationMetadata.builder().hasStreamingSuccessResponse(false).isPayloadJson(true).build();
            HttpResponseHandler responseHandler = this.protocolFactory.createResponseHandler(operationMetadata, ValidateResourcePolicyResponse::builder);
            HttpResponseHandler<AwsServiceException> errorResponseHandler = this.createErrorResponseHandler(this.protocolFactory, operationMetadata);
            CompletableFuture<ValidateResourcePolicyResponse> executeFuture = this.clientHandler.execute(new ClientExecutionParams().withOperationName("ValidateResourcePolicy").withProtocolMetadata(protocolMetadata).withMarshaller(new ValidateResourcePolicyRequestMarshaller(this.protocolFactory)).withResponseHandler(responseHandler).withErrorResponseHandler(errorResponseHandler).withRequestConfiguration(clientConfiguration).withMetricCollector(apiCallMetricCollector).withInput(validateResourcePolicyRequest));
            CompletionStage whenCompleted = executeFuture.whenComplete((r, e) -> metricPublishers.forEach(p -> p.publish(apiCallMetricCollector.collect())));
            executeFuture = CompletableFutureUtils.forwardExceptionTo(whenCompleted, executeFuture);
            return executeFuture;
        }
        catch (Throwable t) {
            metricPublishers.forEach(p -> p.publish(apiCallMetricCollector.collect()));
            return CompletableFutureUtils.failedFuture(t);
        }
    }

    @Override
    public final SecretsManagerServiceClientConfiguration serviceClientConfiguration() {
        return this.serviceClientConfiguration;
    }

    @Override
    public final String serviceName() {
        return "secretsmanager";
    }

    private <T extends BaseAwsJsonProtocolFactory.Builder<T>> T init(T builder) {
        return ((BaseAwsJsonProtocolFactory.Builder)((BaseAwsJsonProtocolFactory.Builder)((BaseAwsJsonProtocolFactory.Builder)((BaseAwsJsonProtocolFactory.Builder)((BaseAwsJsonProtocolFactory.Builder)((BaseAwsJsonProtocolFactory.Builder)((BaseAwsJsonProtocolFactory.Builder)((BaseAwsJsonProtocolFactory.Builder)((BaseAwsJsonProtocolFactory.Builder)((BaseAwsJsonProtocolFactory.Builder)((BaseAwsJsonProtocolFactory.Builder)((BaseAwsJsonProtocolFactory.Builder)((BaseAwsJsonProtocolFactory.Builder)((BaseAwsJsonProtocolFactory.Builder)((BaseAwsJsonProtocolFactory.Builder)builder.clientConfiguration(this.clientConfiguration)).defaultServiceExceptionSupplier(SecretsManagerException::builder)).protocol(AwsJsonProtocol.AWS_JSON)).protocolVersion("1.1")).registerModeledException(ExceptionMetadata.builder().errorCode("EncryptionFailure").exceptionBuilderSupplier(EncryptionFailureException::builder).httpStatusCode(400).build())).registerModeledException(ExceptionMetadata.builder().errorCode("InvalidParameterException").exceptionBuilderSupplier(InvalidParameterException::builder).httpStatusCode(400).build())).registerModeledException(ExceptionMetadata.builder().errorCode("PublicPolicyException").exceptionBuilderSupplier(PublicPolicyException::builder).httpStatusCode(400).build())).registerModeledException(ExceptionMetadata.builder().errorCode("MalformedPolicyDocumentException").exceptionBuilderSupplier(MalformedPolicyDocumentException::builder).httpStatusCode(400).build())).registerModeledException(ExceptionMetadata.builder().errorCode("DecryptionFailure").exceptionBuilderSupplier(DecryptionFailureException::builder).httpStatusCode(400).build())).registerModeledException(ExceptionMetadata.builder().errorCode("InvalidRequestException").exceptionBuilderSupplier(InvalidRequestException::builder).httpStatusCode(400).build())).registerModeledException(ExceptionMetadata.builder().errorCode("ResourceNotFoundException").exceptionBuilderSupplier(ResourceNotFoundException::builder).httpStatusCode(400).build())).registerModeledException(ExceptionMetadata.builder().errorCode("InternalServiceError").exceptionBuilderSupplier(InternalServiceErrorException::builder).httpStatusCode(500).build())).registerModeledException(ExceptionMetadata.builder().errorCode("ResourceExistsException").exceptionBuilderSupplier(ResourceExistsException::builder).httpStatusCode(400).build())).registerModeledException(ExceptionMetadata.builder().errorCode("InvalidNextTokenException").exceptionBuilderSupplier(InvalidNextTokenException::builder).httpStatusCode(400).build())).registerModeledException(ExceptionMetadata.builder().errorCode("LimitExceededException").exceptionBuilderSupplier(LimitExceededException::builder).httpStatusCode(400).build())).registerModeledException(ExceptionMetadata.builder().errorCode("PreconditionNotMetException").exceptionBuilderSupplier(PreconditionNotMetException::builder).httpStatusCode(400).build());
    }

    private static List<MetricPublisher> resolveMetricPublishers(SdkClientConfiguration clientConfiguration, RequestOverrideConfiguration requestOverrideConfiguration) {
        List<MetricPublisher> publishers = null;
        if (requestOverrideConfiguration != null) {
            publishers = requestOverrideConfiguration.metricPublishers();
        }
        if (publishers == null || publishers.isEmpty()) {
            publishers = clientConfiguration.option(SdkClientOption.METRIC_PUBLISHERS);
        }
        if (publishers == null) {
            publishers = Collections.emptyList();
        }
        return publishers;
    }

    private SdkClientConfiguration updateSdkClientConfiguration(SdkRequest request, SdkClientConfiguration clientConfiguration) {
        List plugins = request.overrideConfiguration().map(c -> c.plugins()).orElse(Collections.emptyList());
        if (plugins.isEmpty()) {
            return clientConfiguration;
        }
        SecretsManagerServiceClientConfigurationBuilder.BuilderInternal serviceConfigBuilder = SecretsManagerServiceClientConfigurationBuilder.builder(clientConfiguration.toBuilder());
        serviceConfigBuilder.overrideConfiguration(this.serviceClientConfiguration.overrideConfiguration());
        for (SdkPlugin plugin : plugins) {
            plugin.configureClient(serviceConfigBuilder);
        }
        return serviceConfigBuilder.buildSdkClientConfiguration();
    }

    private HttpResponseHandler<AwsServiceException> createErrorResponseHandler(BaseAwsJsonProtocolFactory protocolFactory, JsonOperationMetadata operationMetadata) {
        return protocolFactory.createErrorResponseHandler(operationMetadata);
    }

    @Override
    public void close() {
        this.clientHandler.close();
    }
}

