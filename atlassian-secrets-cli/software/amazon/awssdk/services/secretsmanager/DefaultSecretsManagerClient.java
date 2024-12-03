/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.services.secretsmanager;

import java.util.Collections;
import java.util.List;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.awscore.client.handler.AwsSyncClientHandler;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.awscore.internal.AwsProtocolMetadata;
import software.amazon.awssdk.awscore.internal.AwsServiceProtocol;
import software.amazon.awssdk.core.RequestOverrideConfiguration;
import software.amazon.awssdk.core.SdkPlugin;
import software.amazon.awssdk.core.SdkRequest;
import software.amazon.awssdk.core.client.config.SdkClientConfiguration;
import software.amazon.awssdk.core.client.config.SdkClientOption;
import software.amazon.awssdk.core.client.handler.ClientExecutionParams;
import software.amazon.awssdk.core.client.handler.SyncClientHandler;
import software.amazon.awssdk.core.exception.SdkClientException;
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
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
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
import software.amazon.awssdk.utils.Logger;

@SdkInternalApi
final class DefaultSecretsManagerClient
implements SecretsManagerClient {
    private static final Logger log = Logger.loggerFor(DefaultSecretsManagerClient.class);
    private static final AwsProtocolMetadata protocolMetadata = AwsProtocolMetadata.builder().serviceProtocol(AwsServiceProtocol.AWS_JSON).build();
    private final SyncClientHandler clientHandler;
    private final AwsJsonProtocolFactory protocolFactory;
    private final SdkClientConfiguration clientConfiguration;
    private final SecretsManagerServiceClientConfiguration serviceClientConfiguration;

    protected DefaultSecretsManagerClient(SecretsManagerServiceClientConfiguration serviceClientConfiguration, SdkClientConfiguration clientConfiguration) {
        this.clientHandler = new AwsSyncClientHandler(clientConfiguration);
        this.clientConfiguration = clientConfiguration;
        this.serviceClientConfiguration = serviceClientConfiguration;
        this.protocolFactory = this.init(AwsJsonProtocolFactory.builder()).build();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CancelRotateSecretResponse cancelRotateSecret(CancelRotateSecretRequest cancelRotateSecretRequest) throws ResourceNotFoundException, InvalidParameterException, InternalServiceErrorException, InvalidRequestException, AwsServiceException, SdkClientException, SecretsManagerException {
        JsonOperationMetadata operationMetadata = JsonOperationMetadata.builder().hasStreamingSuccessResponse(false).isPayloadJson(true).build();
        HttpResponseHandler responseHandler = this.protocolFactory.createResponseHandler(operationMetadata, CancelRotateSecretResponse::builder);
        HttpResponseHandler<AwsServiceException> errorResponseHandler = this.createErrorResponseHandler(this.protocolFactory, operationMetadata);
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration(cancelRotateSecretRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultSecretsManagerClient.resolveMetricPublishers(clientConfiguration, cancelRotateSecretRequest.overrideConfiguration().orElse(null));
        MetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create("ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, "Secrets Manager");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, "CancelRotateSecret");
            CancelRotateSecretResponse cancelRotateSecretResponse = (CancelRotateSecretResponse)this.clientHandler.execute(new ClientExecutionParams().withOperationName("CancelRotateSecret").withProtocolMetadata(protocolMetadata).withResponseHandler(responseHandler).withErrorResponseHandler(errorResponseHandler).withRequestConfiguration(clientConfiguration).withInput(cancelRotateSecretRequest).withMetricCollector(apiCallMetricCollector).withMarshaller(new CancelRotateSecretRequestMarshaller(this.protocolFactory)));
            return cancelRotateSecretResponse;
        }
        finally {
            metricPublishers.forEach(p -> p.publish(apiCallMetricCollector.collect()));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CreateSecretResponse createSecret(CreateSecretRequest createSecretRequest) throws InvalidParameterException, InvalidRequestException, LimitExceededException, EncryptionFailureException, ResourceExistsException, ResourceNotFoundException, MalformedPolicyDocumentException, InternalServiceErrorException, PreconditionNotMetException, DecryptionFailureException, AwsServiceException, SdkClientException, SecretsManagerException {
        JsonOperationMetadata operationMetadata = JsonOperationMetadata.builder().hasStreamingSuccessResponse(false).isPayloadJson(true).build();
        HttpResponseHandler responseHandler = this.protocolFactory.createResponseHandler(operationMetadata, CreateSecretResponse::builder);
        HttpResponseHandler<AwsServiceException> errorResponseHandler = this.createErrorResponseHandler(this.protocolFactory, operationMetadata);
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration(createSecretRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultSecretsManagerClient.resolveMetricPublishers(clientConfiguration, createSecretRequest.overrideConfiguration().orElse(null));
        MetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create("ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, "Secrets Manager");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, "CreateSecret");
            CreateSecretResponse createSecretResponse = (CreateSecretResponse)this.clientHandler.execute(new ClientExecutionParams().withOperationName("CreateSecret").withProtocolMetadata(protocolMetadata).withResponseHandler(responseHandler).withErrorResponseHandler(errorResponseHandler).withRequestConfiguration(clientConfiguration).withInput(createSecretRequest).withMetricCollector(apiCallMetricCollector).withMarshaller(new CreateSecretRequestMarshaller(this.protocolFactory)));
            return createSecretResponse;
        }
        finally {
            metricPublishers.forEach(p -> p.publish(apiCallMetricCollector.collect()));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public DeleteResourcePolicyResponse deleteResourcePolicy(DeleteResourcePolicyRequest deleteResourcePolicyRequest) throws ResourceNotFoundException, InternalServiceErrorException, InvalidRequestException, InvalidParameterException, AwsServiceException, SdkClientException, SecretsManagerException {
        JsonOperationMetadata operationMetadata = JsonOperationMetadata.builder().hasStreamingSuccessResponse(false).isPayloadJson(true).build();
        HttpResponseHandler responseHandler = this.protocolFactory.createResponseHandler(operationMetadata, DeleteResourcePolicyResponse::builder);
        HttpResponseHandler<AwsServiceException> errorResponseHandler = this.createErrorResponseHandler(this.protocolFactory, operationMetadata);
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration(deleteResourcePolicyRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultSecretsManagerClient.resolveMetricPublishers(clientConfiguration, deleteResourcePolicyRequest.overrideConfiguration().orElse(null));
        MetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create("ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, "Secrets Manager");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, "DeleteResourcePolicy");
            DeleteResourcePolicyResponse deleteResourcePolicyResponse = (DeleteResourcePolicyResponse)this.clientHandler.execute(new ClientExecutionParams().withOperationName("DeleteResourcePolicy").withProtocolMetadata(protocolMetadata).withResponseHandler(responseHandler).withErrorResponseHandler(errorResponseHandler).withRequestConfiguration(clientConfiguration).withInput(deleteResourcePolicyRequest).withMetricCollector(apiCallMetricCollector).withMarshaller(new DeleteResourcePolicyRequestMarshaller(this.protocolFactory)));
            return deleteResourcePolicyResponse;
        }
        finally {
            metricPublishers.forEach(p -> p.publish(apiCallMetricCollector.collect()));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public DeleteSecretResponse deleteSecret(DeleteSecretRequest deleteSecretRequest) throws ResourceNotFoundException, InvalidParameterException, InvalidRequestException, InternalServiceErrorException, AwsServiceException, SdkClientException, SecretsManagerException {
        JsonOperationMetadata operationMetadata = JsonOperationMetadata.builder().hasStreamingSuccessResponse(false).isPayloadJson(true).build();
        HttpResponseHandler responseHandler = this.protocolFactory.createResponseHandler(operationMetadata, DeleteSecretResponse::builder);
        HttpResponseHandler<AwsServiceException> errorResponseHandler = this.createErrorResponseHandler(this.protocolFactory, operationMetadata);
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration(deleteSecretRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultSecretsManagerClient.resolveMetricPublishers(clientConfiguration, deleteSecretRequest.overrideConfiguration().orElse(null));
        MetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create("ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, "Secrets Manager");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, "DeleteSecret");
            DeleteSecretResponse deleteSecretResponse = (DeleteSecretResponse)this.clientHandler.execute(new ClientExecutionParams().withOperationName("DeleteSecret").withProtocolMetadata(protocolMetadata).withResponseHandler(responseHandler).withErrorResponseHandler(errorResponseHandler).withRequestConfiguration(clientConfiguration).withInput(deleteSecretRequest).withMetricCollector(apiCallMetricCollector).withMarshaller(new DeleteSecretRequestMarshaller(this.protocolFactory)));
            return deleteSecretResponse;
        }
        finally {
            metricPublishers.forEach(p -> p.publish(apiCallMetricCollector.collect()));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public DescribeSecretResponse describeSecret(DescribeSecretRequest describeSecretRequest) throws ResourceNotFoundException, InternalServiceErrorException, InvalidParameterException, AwsServiceException, SdkClientException, SecretsManagerException {
        JsonOperationMetadata operationMetadata = JsonOperationMetadata.builder().hasStreamingSuccessResponse(false).isPayloadJson(true).build();
        HttpResponseHandler responseHandler = this.protocolFactory.createResponseHandler(operationMetadata, DescribeSecretResponse::builder);
        HttpResponseHandler<AwsServiceException> errorResponseHandler = this.createErrorResponseHandler(this.protocolFactory, operationMetadata);
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration(describeSecretRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultSecretsManagerClient.resolveMetricPublishers(clientConfiguration, describeSecretRequest.overrideConfiguration().orElse(null));
        MetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create("ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, "Secrets Manager");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, "DescribeSecret");
            DescribeSecretResponse describeSecretResponse = (DescribeSecretResponse)this.clientHandler.execute(new ClientExecutionParams().withOperationName("DescribeSecret").withProtocolMetadata(protocolMetadata).withResponseHandler(responseHandler).withErrorResponseHandler(errorResponseHandler).withRequestConfiguration(clientConfiguration).withInput(describeSecretRequest).withMetricCollector(apiCallMetricCollector).withMarshaller(new DescribeSecretRequestMarshaller(this.protocolFactory)));
            return describeSecretResponse;
        }
        finally {
            metricPublishers.forEach(p -> p.publish(apiCallMetricCollector.collect()));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public GetRandomPasswordResponse getRandomPassword(GetRandomPasswordRequest getRandomPasswordRequest) throws InvalidParameterException, InvalidRequestException, InternalServiceErrorException, AwsServiceException, SdkClientException, SecretsManagerException {
        JsonOperationMetadata operationMetadata = JsonOperationMetadata.builder().hasStreamingSuccessResponse(false).isPayloadJson(true).build();
        HttpResponseHandler responseHandler = this.protocolFactory.createResponseHandler(operationMetadata, GetRandomPasswordResponse::builder);
        HttpResponseHandler<AwsServiceException> errorResponseHandler = this.createErrorResponseHandler(this.protocolFactory, operationMetadata);
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration(getRandomPasswordRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultSecretsManagerClient.resolveMetricPublishers(clientConfiguration, getRandomPasswordRequest.overrideConfiguration().orElse(null));
        MetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create("ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, "Secrets Manager");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, "GetRandomPassword");
            GetRandomPasswordResponse getRandomPasswordResponse = (GetRandomPasswordResponse)this.clientHandler.execute(new ClientExecutionParams().withOperationName("GetRandomPassword").withProtocolMetadata(protocolMetadata).withResponseHandler(responseHandler).withErrorResponseHandler(errorResponseHandler).withRequestConfiguration(clientConfiguration).withInput(getRandomPasswordRequest).withMetricCollector(apiCallMetricCollector).withMarshaller(new GetRandomPasswordRequestMarshaller(this.protocolFactory)));
            return getRandomPasswordResponse;
        }
        finally {
            metricPublishers.forEach(p -> p.publish(apiCallMetricCollector.collect()));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public GetResourcePolicyResponse getResourcePolicy(GetResourcePolicyRequest getResourcePolicyRequest) throws ResourceNotFoundException, InternalServiceErrorException, InvalidRequestException, InvalidParameterException, AwsServiceException, SdkClientException, SecretsManagerException {
        JsonOperationMetadata operationMetadata = JsonOperationMetadata.builder().hasStreamingSuccessResponse(false).isPayloadJson(true).build();
        HttpResponseHandler responseHandler = this.protocolFactory.createResponseHandler(operationMetadata, GetResourcePolicyResponse::builder);
        HttpResponseHandler<AwsServiceException> errorResponseHandler = this.createErrorResponseHandler(this.protocolFactory, operationMetadata);
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration(getResourcePolicyRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultSecretsManagerClient.resolveMetricPublishers(clientConfiguration, getResourcePolicyRequest.overrideConfiguration().orElse(null));
        MetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create("ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, "Secrets Manager");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, "GetResourcePolicy");
            GetResourcePolicyResponse getResourcePolicyResponse = (GetResourcePolicyResponse)this.clientHandler.execute(new ClientExecutionParams().withOperationName("GetResourcePolicy").withProtocolMetadata(protocolMetadata).withResponseHandler(responseHandler).withErrorResponseHandler(errorResponseHandler).withRequestConfiguration(clientConfiguration).withInput(getResourcePolicyRequest).withMetricCollector(apiCallMetricCollector).withMarshaller(new GetResourcePolicyRequestMarshaller(this.protocolFactory)));
            return getResourcePolicyResponse;
        }
        finally {
            metricPublishers.forEach(p -> p.publish(apiCallMetricCollector.collect()));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public GetSecretValueResponse getSecretValue(GetSecretValueRequest getSecretValueRequest) throws ResourceNotFoundException, InvalidParameterException, InvalidRequestException, DecryptionFailureException, InternalServiceErrorException, AwsServiceException, SdkClientException, SecretsManagerException {
        JsonOperationMetadata operationMetadata = JsonOperationMetadata.builder().hasStreamingSuccessResponse(false).isPayloadJson(true).build();
        HttpResponseHandler responseHandler = this.protocolFactory.createResponseHandler(operationMetadata, GetSecretValueResponse::builder);
        HttpResponseHandler<AwsServiceException> errorResponseHandler = this.createErrorResponseHandler(this.protocolFactory, operationMetadata);
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration(getSecretValueRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultSecretsManagerClient.resolveMetricPublishers(clientConfiguration, getSecretValueRequest.overrideConfiguration().orElse(null));
        MetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create("ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, "Secrets Manager");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, "GetSecretValue");
            GetSecretValueResponse getSecretValueResponse = (GetSecretValueResponse)this.clientHandler.execute(new ClientExecutionParams().withOperationName("GetSecretValue").withProtocolMetadata(protocolMetadata).withResponseHandler(responseHandler).withErrorResponseHandler(errorResponseHandler).withRequestConfiguration(clientConfiguration).withInput(getSecretValueRequest).withMetricCollector(apiCallMetricCollector).withMarshaller(new GetSecretValueRequestMarshaller(this.protocolFactory)));
            return getSecretValueResponse;
        }
        finally {
            metricPublishers.forEach(p -> p.publish(apiCallMetricCollector.collect()));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public ListSecretVersionIdsResponse listSecretVersionIds(ListSecretVersionIdsRequest listSecretVersionIdsRequest) throws InvalidNextTokenException, ResourceNotFoundException, InternalServiceErrorException, InvalidParameterException, AwsServiceException, SdkClientException, SecretsManagerException {
        JsonOperationMetadata operationMetadata = JsonOperationMetadata.builder().hasStreamingSuccessResponse(false).isPayloadJson(true).build();
        HttpResponseHandler responseHandler = this.protocolFactory.createResponseHandler(operationMetadata, ListSecretVersionIdsResponse::builder);
        HttpResponseHandler<AwsServiceException> errorResponseHandler = this.createErrorResponseHandler(this.protocolFactory, operationMetadata);
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration(listSecretVersionIdsRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultSecretsManagerClient.resolveMetricPublishers(clientConfiguration, listSecretVersionIdsRequest.overrideConfiguration().orElse(null));
        MetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create("ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, "Secrets Manager");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, "ListSecretVersionIds");
            ListSecretVersionIdsResponse listSecretVersionIdsResponse = (ListSecretVersionIdsResponse)this.clientHandler.execute(new ClientExecutionParams().withOperationName("ListSecretVersionIds").withProtocolMetadata(protocolMetadata).withResponseHandler(responseHandler).withErrorResponseHandler(errorResponseHandler).withRequestConfiguration(clientConfiguration).withInput(listSecretVersionIdsRequest).withMetricCollector(apiCallMetricCollector).withMarshaller(new ListSecretVersionIdsRequestMarshaller(this.protocolFactory)));
            return listSecretVersionIdsResponse;
        }
        finally {
            metricPublishers.forEach(p -> p.publish(apiCallMetricCollector.collect()));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public ListSecretsResponse listSecrets(ListSecretsRequest listSecretsRequest) throws InvalidParameterException, InvalidRequestException, InvalidNextTokenException, InternalServiceErrorException, AwsServiceException, SdkClientException, SecretsManagerException {
        JsonOperationMetadata operationMetadata = JsonOperationMetadata.builder().hasStreamingSuccessResponse(false).isPayloadJson(true).build();
        HttpResponseHandler responseHandler = this.protocolFactory.createResponseHandler(operationMetadata, ListSecretsResponse::builder);
        HttpResponseHandler<AwsServiceException> errorResponseHandler = this.createErrorResponseHandler(this.protocolFactory, operationMetadata);
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration(listSecretsRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultSecretsManagerClient.resolveMetricPublishers(clientConfiguration, listSecretsRequest.overrideConfiguration().orElse(null));
        MetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create("ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, "Secrets Manager");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, "ListSecrets");
            ListSecretsResponse listSecretsResponse = (ListSecretsResponse)this.clientHandler.execute(new ClientExecutionParams().withOperationName("ListSecrets").withProtocolMetadata(protocolMetadata).withResponseHandler(responseHandler).withErrorResponseHandler(errorResponseHandler).withRequestConfiguration(clientConfiguration).withInput(listSecretsRequest).withMetricCollector(apiCallMetricCollector).withMarshaller(new ListSecretsRequestMarshaller(this.protocolFactory)));
            return listSecretsResponse;
        }
        finally {
            metricPublishers.forEach(p -> p.publish(apiCallMetricCollector.collect()));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public PutResourcePolicyResponse putResourcePolicy(PutResourcePolicyRequest putResourcePolicyRequest) throws MalformedPolicyDocumentException, ResourceNotFoundException, InvalidParameterException, InternalServiceErrorException, InvalidRequestException, PublicPolicyException, AwsServiceException, SdkClientException, SecretsManagerException {
        JsonOperationMetadata operationMetadata = JsonOperationMetadata.builder().hasStreamingSuccessResponse(false).isPayloadJson(true).build();
        HttpResponseHandler responseHandler = this.protocolFactory.createResponseHandler(operationMetadata, PutResourcePolicyResponse::builder);
        HttpResponseHandler<AwsServiceException> errorResponseHandler = this.createErrorResponseHandler(this.protocolFactory, operationMetadata);
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration(putResourcePolicyRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultSecretsManagerClient.resolveMetricPublishers(clientConfiguration, putResourcePolicyRequest.overrideConfiguration().orElse(null));
        MetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create("ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, "Secrets Manager");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, "PutResourcePolicy");
            PutResourcePolicyResponse putResourcePolicyResponse = (PutResourcePolicyResponse)this.clientHandler.execute(new ClientExecutionParams().withOperationName("PutResourcePolicy").withProtocolMetadata(protocolMetadata).withResponseHandler(responseHandler).withErrorResponseHandler(errorResponseHandler).withRequestConfiguration(clientConfiguration).withInput(putResourcePolicyRequest).withMetricCollector(apiCallMetricCollector).withMarshaller(new PutResourcePolicyRequestMarshaller(this.protocolFactory)));
            return putResourcePolicyResponse;
        }
        finally {
            metricPublishers.forEach(p -> p.publish(apiCallMetricCollector.collect()));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public PutSecretValueResponse putSecretValue(PutSecretValueRequest putSecretValueRequest) throws InvalidParameterException, InvalidRequestException, LimitExceededException, EncryptionFailureException, ResourceExistsException, ResourceNotFoundException, InternalServiceErrorException, DecryptionFailureException, AwsServiceException, SdkClientException, SecretsManagerException {
        JsonOperationMetadata operationMetadata = JsonOperationMetadata.builder().hasStreamingSuccessResponse(false).isPayloadJson(true).build();
        HttpResponseHandler responseHandler = this.protocolFactory.createResponseHandler(operationMetadata, PutSecretValueResponse::builder);
        HttpResponseHandler<AwsServiceException> errorResponseHandler = this.createErrorResponseHandler(this.protocolFactory, operationMetadata);
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration(putSecretValueRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultSecretsManagerClient.resolveMetricPublishers(clientConfiguration, putSecretValueRequest.overrideConfiguration().orElse(null));
        MetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create("ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, "Secrets Manager");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, "PutSecretValue");
            PutSecretValueResponse putSecretValueResponse = (PutSecretValueResponse)this.clientHandler.execute(new ClientExecutionParams().withOperationName("PutSecretValue").withProtocolMetadata(protocolMetadata).withResponseHandler(responseHandler).withErrorResponseHandler(errorResponseHandler).withRequestConfiguration(clientConfiguration).withInput(putSecretValueRequest).withMetricCollector(apiCallMetricCollector).withMarshaller(new PutSecretValueRequestMarshaller(this.protocolFactory)));
            return putSecretValueResponse;
        }
        finally {
            metricPublishers.forEach(p -> p.publish(apiCallMetricCollector.collect()));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public RemoveRegionsFromReplicationResponse removeRegionsFromReplication(RemoveRegionsFromReplicationRequest removeRegionsFromReplicationRequest) throws ResourceNotFoundException, InvalidRequestException, InvalidParameterException, InternalServiceErrorException, AwsServiceException, SdkClientException, SecretsManagerException {
        JsonOperationMetadata operationMetadata = JsonOperationMetadata.builder().hasStreamingSuccessResponse(false).isPayloadJson(true).build();
        HttpResponseHandler responseHandler = this.protocolFactory.createResponseHandler(operationMetadata, RemoveRegionsFromReplicationResponse::builder);
        HttpResponseHandler<AwsServiceException> errorResponseHandler = this.createErrorResponseHandler(this.protocolFactory, operationMetadata);
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration(removeRegionsFromReplicationRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultSecretsManagerClient.resolveMetricPublishers(clientConfiguration, removeRegionsFromReplicationRequest.overrideConfiguration().orElse(null));
        MetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create("ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, "Secrets Manager");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, "RemoveRegionsFromReplication");
            RemoveRegionsFromReplicationResponse removeRegionsFromReplicationResponse = (RemoveRegionsFromReplicationResponse)this.clientHandler.execute(new ClientExecutionParams().withOperationName("RemoveRegionsFromReplication").withProtocolMetadata(protocolMetadata).withResponseHandler(responseHandler).withErrorResponseHandler(errorResponseHandler).withRequestConfiguration(clientConfiguration).withInput(removeRegionsFromReplicationRequest).withMetricCollector(apiCallMetricCollector).withMarshaller(new RemoveRegionsFromReplicationRequestMarshaller(this.protocolFactory)));
            return removeRegionsFromReplicationResponse;
        }
        finally {
            metricPublishers.forEach(p -> p.publish(apiCallMetricCollector.collect()));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public ReplicateSecretToRegionsResponse replicateSecretToRegions(ReplicateSecretToRegionsRequest replicateSecretToRegionsRequest) throws ResourceNotFoundException, InvalidRequestException, InvalidParameterException, InternalServiceErrorException, AwsServiceException, SdkClientException, SecretsManagerException {
        JsonOperationMetadata operationMetadata = JsonOperationMetadata.builder().hasStreamingSuccessResponse(false).isPayloadJson(true).build();
        HttpResponseHandler responseHandler = this.protocolFactory.createResponseHandler(operationMetadata, ReplicateSecretToRegionsResponse::builder);
        HttpResponseHandler<AwsServiceException> errorResponseHandler = this.createErrorResponseHandler(this.protocolFactory, operationMetadata);
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration(replicateSecretToRegionsRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultSecretsManagerClient.resolveMetricPublishers(clientConfiguration, replicateSecretToRegionsRequest.overrideConfiguration().orElse(null));
        MetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create("ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, "Secrets Manager");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, "ReplicateSecretToRegions");
            ReplicateSecretToRegionsResponse replicateSecretToRegionsResponse = (ReplicateSecretToRegionsResponse)this.clientHandler.execute(new ClientExecutionParams().withOperationName("ReplicateSecretToRegions").withProtocolMetadata(protocolMetadata).withResponseHandler(responseHandler).withErrorResponseHandler(errorResponseHandler).withRequestConfiguration(clientConfiguration).withInput(replicateSecretToRegionsRequest).withMetricCollector(apiCallMetricCollector).withMarshaller(new ReplicateSecretToRegionsRequestMarshaller(this.protocolFactory)));
            return replicateSecretToRegionsResponse;
        }
        finally {
            metricPublishers.forEach(p -> p.publish(apiCallMetricCollector.collect()));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public RestoreSecretResponse restoreSecret(RestoreSecretRequest restoreSecretRequest) throws ResourceNotFoundException, InvalidParameterException, InvalidRequestException, InternalServiceErrorException, AwsServiceException, SdkClientException, SecretsManagerException {
        JsonOperationMetadata operationMetadata = JsonOperationMetadata.builder().hasStreamingSuccessResponse(false).isPayloadJson(true).build();
        HttpResponseHandler responseHandler = this.protocolFactory.createResponseHandler(operationMetadata, RestoreSecretResponse::builder);
        HttpResponseHandler<AwsServiceException> errorResponseHandler = this.createErrorResponseHandler(this.protocolFactory, operationMetadata);
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration(restoreSecretRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultSecretsManagerClient.resolveMetricPublishers(clientConfiguration, restoreSecretRequest.overrideConfiguration().orElse(null));
        MetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create("ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, "Secrets Manager");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, "RestoreSecret");
            RestoreSecretResponse restoreSecretResponse = (RestoreSecretResponse)this.clientHandler.execute(new ClientExecutionParams().withOperationName("RestoreSecret").withProtocolMetadata(protocolMetadata).withResponseHandler(responseHandler).withErrorResponseHandler(errorResponseHandler).withRequestConfiguration(clientConfiguration).withInput(restoreSecretRequest).withMetricCollector(apiCallMetricCollector).withMarshaller(new RestoreSecretRequestMarshaller(this.protocolFactory)));
            return restoreSecretResponse;
        }
        finally {
            metricPublishers.forEach(p -> p.publish(apiCallMetricCollector.collect()));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public RotateSecretResponse rotateSecret(RotateSecretRequest rotateSecretRequest) throws ResourceNotFoundException, InvalidParameterException, InternalServiceErrorException, InvalidRequestException, AwsServiceException, SdkClientException, SecretsManagerException {
        JsonOperationMetadata operationMetadata = JsonOperationMetadata.builder().hasStreamingSuccessResponse(false).isPayloadJson(true).build();
        HttpResponseHandler responseHandler = this.protocolFactory.createResponseHandler(operationMetadata, RotateSecretResponse::builder);
        HttpResponseHandler<AwsServiceException> errorResponseHandler = this.createErrorResponseHandler(this.protocolFactory, operationMetadata);
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration(rotateSecretRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultSecretsManagerClient.resolveMetricPublishers(clientConfiguration, rotateSecretRequest.overrideConfiguration().orElse(null));
        MetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create("ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, "Secrets Manager");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, "RotateSecret");
            RotateSecretResponse rotateSecretResponse = (RotateSecretResponse)this.clientHandler.execute(new ClientExecutionParams().withOperationName("RotateSecret").withProtocolMetadata(protocolMetadata).withResponseHandler(responseHandler).withErrorResponseHandler(errorResponseHandler).withRequestConfiguration(clientConfiguration).withInput(rotateSecretRequest).withMetricCollector(apiCallMetricCollector).withMarshaller(new RotateSecretRequestMarshaller(this.protocolFactory)));
            return rotateSecretResponse;
        }
        finally {
            metricPublishers.forEach(p -> p.publish(apiCallMetricCollector.collect()));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public StopReplicationToReplicaResponse stopReplicationToReplica(StopReplicationToReplicaRequest stopReplicationToReplicaRequest) throws ResourceNotFoundException, InvalidRequestException, InvalidParameterException, InternalServiceErrorException, AwsServiceException, SdkClientException, SecretsManagerException {
        JsonOperationMetadata operationMetadata = JsonOperationMetadata.builder().hasStreamingSuccessResponse(false).isPayloadJson(true).build();
        HttpResponseHandler responseHandler = this.protocolFactory.createResponseHandler(operationMetadata, StopReplicationToReplicaResponse::builder);
        HttpResponseHandler<AwsServiceException> errorResponseHandler = this.createErrorResponseHandler(this.protocolFactory, operationMetadata);
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration(stopReplicationToReplicaRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultSecretsManagerClient.resolveMetricPublishers(clientConfiguration, stopReplicationToReplicaRequest.overrideConfiguration().orElse(null));
        MetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create("ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, "Secrets Manager");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, "StopReplicationToReplica");
            StopReplicationToReplicaResponse stopReplicationToReplicaResponse = (StopReplicationToReplicaResponse)this.clientHandler.execute(new ClientExecutionParams().withOperationName("StopReplicationToReplica").withProtocolMetadata(protocolMetadata).withResponseHandler(responseHandler).withErrorResponseHandler(errorResponseHandler).withRequestConfiguration(clientConfiguration).withInput(stopReplicationToReplicaRequest).withMetricCollector(apiCallMetricCollector).withMarshaller(new StopReplicationToReplicaRequestMarshaller(this.protocolFactory)));
            return stopReplicationToReplicaResponse;
        }
        finally {
            metricPublishers.forEach(p -> p.publish(apiCallMetricCollector.collect()));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public TagResourceResponse tagResource(TagResourceRequest tagResourceRequest) throws ResourceNotFoundException, InvalidRequestException, InvalidParameterException, InternalServiceErrorException, AwsServiceException, SdkClientException, SecretsManagerException {
        JsonOperationMetadata operationMetadata = JsonOperationMetadata.builder().hasStreamingSuccessResponse(false).isPayloadJson(true).build();
        HttpResponseHandler responseHandler = this.protocolFactory.createResponseHandler(operationMetadata, TagResourceResponse::builder);
        HttpResponseHandler<AwsServiceException> errorResponseHandler = this.createErrorResponseHandler(this.protocolFactory, operationMetadata);
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration(tagResourceRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultSecretsManagerClient.resolveMetricPublishers(clientConfiguration, tagResourceRequest.overrideConfiguration().orElse(null));
        MetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create("ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, "Secrets Manager");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, "TagResource");
            TagResourceResponse tagResourceResponse = (TagResourceResponse)this.clientHandler.execute(new ClientExecutionParams().withOperationName("TagResource").withProtocolMetadata(protocolMetadata).withResponseHandler(responseHandler).withErrorResponseHandler(errorResponseHandler).withRequestConfiguration(clientConfiguration).withInput(tagResourceRequest).withMetricCollector(apiCallMetricCollector).withMarshaller(new TagResourceRequestMarshaller(this.protocolFactory)));
            return tagResourceResponse;
        }
        finally {
            metricPublishers.forEach(p -> p.publish(apiCallMetricCollector.collect()));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public UntagResourceResponse untagResource(UntagResourceRequest untagResourceRequest) throws ResourceNotFoundException, InvalidRequestException, InvalidParameterException, InternalServiceErrorException, AwsServiceException, SdkClientException, SecretsManagerException {
        JsonOperationMetadata operationMetadata = JsonOperationMetadata.builder().hasStreamingSuccessResponse(false).isPayloadJson(true).build();
        HttpResponseHandler responseHandler = this.protocolFactory.createResponseHandler(operationMetadata, UntagResourceResponse::builder);
        HttpResponseHandler<AwsServiceException> errorResponseHandler = this.createErrorResponseHandler(this.protocolFactory, operationMetadata);
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration(untagResourceRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultSecretsManagerClient.resolveMetricPublishers(clientConfiguration, untagResourceRequest.overrideConfiguration().orElse(null));
        MetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create("ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, "Secrets Manager");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, "UntagResource");
            UntagResourceResponse untagResourceResponse = (UntagResourceResponse)this.clientHandler.execute(new ClientExecutionParams().withOperationName("UntagResource").withProtocolMetadata(protocolMetadata).withResponseHandler(responseHandler).withErrorResponseHandler(errorResponseHandler).withRequestConfiguration(clientConfiguration).withInput(untagResourceRequest).withMetricCollector(apiCallMetricCollector).withMarshaller(new UntagResourceRequestMarshaller(this.protocolFactory)));
            return untagResourceResponse;
        }
        finally {
            metricPublishers.forEach(p -> p.publish(apiCallMetricCollector.collect()));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public UpdateSecretResponse updateSecret(UpdateSecretRequest updateSecretRequest) throws InvalidParameterException, InvalidRequestException, LimitExceededException, EncryptionFailureException, ResourceExistsException, ResourceNotFoundException, MalformedPolicyDocumentException, InternalServiceErrorException, PreconditionNotMetException, DecryptionFailureException, AwsServiceException, SdkClientException, SecretsManagerException {
        JsonOperationMetadata operationMetadata = JsonOperationMetadata.builder().hasStreamingSuccessResponse(false).isPayloadJson(true).build();
        HttpResponseHandler responseHandler = this.protocolFactory.createResponseHandler(operationMetadata, UpdateSecretResponse::builder);
        HttpResponseHandler<AwsServiceException> errorResponseHandler = this.createErrorResponseHandler(this.protocolFactory, operationMetadata);
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration(updateSecretRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultSecretsManagerClient.resolveMetricPublishers(clientConfiguration, updateSecretRequest.overrideConfiguration().orElse(null));
        MetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create("ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, "Secrets Manager");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, "UpdateSecret");
            UpdateSecretResponse updateSecretResponse = (UpdateSecretResponse)this.clientHandler.execute(new ClientExecutionParams().withOperationName("UpdateSecret").withProtocolMetadata(protocolMetadata).withResponseHandler(responseHandler).withErrorResponseHandler(errorResponseHandler).withRequestConfiguration(clientConfiguration).withInput(updateSecretRequest).withMetricCollector(apiCallMetricCollector).withMarshaller(new UpdateSecretRequestMarshaller(this.protocolFactory)));
            return updateSecretResponse;
        }
        finally {
            metricPublishers.forEach(p -> p.publish(apiCallMetricCollector.collect()));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public UpdateSecretVersionStageResponse updateSecretVersionStage(UpdateSecretVersionStageRequest updateSecretVersionStageRequest) throws ResourceNotFoundException, InvalidParameterException, InvalidRequestException, LimitExceededException, InternalServiceErrorException, AwsServiceException, SdkClientException, SecretsManagerException {
        JsonOperationMetadata operationMetadata = JsonOperationMetadata.builder().hasStreamingSuccessResponse(false).isPayloadJson(true).build();
        HttpResponseHandler responseHandler = this.protocolFactory.createResponseHandler(operationMetadata, UpdateSecretVersionStageResponse::builder);
        HttpResponseHandler<AwsServiceException> errorResponseHandler = this.createErrorResponseHandler(this.protocolFactory, operationMetadata);
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration(updateSecretVersionStageRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultSecretsManagerClient.resolveMetricPublishers(clientConfiguration, updateSecretVersionStageRequest.overrideConfiguration().orElse(null));
        MetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create("ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, "Secrets Manager");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, "UpdateSecretVersionStage");
            UpdateSecretVersionStageResponse updateSecretVersionStageResponse = (UpdateSecretVersionStageResponse)this.clientHandler.execute(new ClientExecutionParams().withOperationName("UpdateSecretVersionStage").withProtocolMetadata(protocolMetadata).withResponseHandler(responseHandler).withErrorResponseHandler(errorResponseHandler).withRequestConfiguration(clientConfiguration).withInput(updateSecretVersionStageRequest).withMetricCollector(apiCallMetricCollector).withMarshaller(new UpdateSecretVersionStageRequestMarshaller(this.protocolFactory)));
            return updateSecretVersionStageResponse;
        }
        finally {
            metricPublishers.forEach(p -> p.publish(apiCallMetricCollector.collect()));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public ValidateResourcePolicyResponse validateResourcePolicy(ValidateResourcePolicyRequest validateResourcePolicyRequest) throws MalformedPolicyDocumentException, ResourceNotFoundException, InvalidParameterException, InternalServiceErrorException, InvalidRequestException, AwsServiceException, SdkClientException, SecretsManagerException {
        JsonOperationMetadata operationMetadata = JsonOperationMetadata.builder().hasStreamingSuccessResponse(false).isPayloadJson(true).build();
        HttpResponseHandler responseHandler = this.protocolFactory.createResponseHandler(operationMetadata, ValidateResourcePolicyResponse::builder);
        HttpResponseHandler<AwsServiceException> errorResponseHandler = this.createErrorResponseHandler(this.protocolFactory, operationMetadata);
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration(validateResourcePolicyRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultSecretsManagerClient.resolveMetricPublishers(clientConfiguration, validateResourcePolicyRequest.overrideConfiguration().orElse(null));
        MetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create("ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, "Secrets Manager");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, "ValidateResourcePolicy");
            ValidateResourcePolicyResponse validateResourcePolicyResponse = (ValidateResourcePolicyResponse)this.clientHandler.execute(new ClientExecutionParams().withOperationName("ValidateResourcePolicy").withProtocolMetadata(protocolMetadata).withResponseHandler(responseHandler).withErrorResponseHandler(errorResponseHandler).withRequestConfiguration(clientConfiguration).withInput(validateResourcePolicyRequest).withMetricCollector(apiCallMetricCollector).withMarshaller(new ValidateResourcePolicyRequestMarshaller(this.protocolFactory)));
            return validateResourcePolicyResponse;
        }
        finally {
            metricPublishers.forEach(p -> p.publish(apiCallMetricCollector.collect()));
        }
    }

    @Override
    public final String serviceName() {
        return "secretsmanager";
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

    private HttpResponseHandler<AwsServiceException> createErrorResponseHandler(BaseAwsJsonProtocolFactory protocolFactory, JsonOperationMetadata operationMetadata) {
        return protocolFactory.createErrorResponseHandler(operationMetadata);
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

    private <T extends BaseAwsJsonProtocolFactory.Builder<T>> T init(T builder) {
        return ((BaseAwsJsonProtocolFactory.Builder)((BaseAwsJsonProtocolFactory.Builder)((BaseAwsJsonProtocolFactory.Builder)((BaseAwsJsonProtocolFactory.Builder)((BaseAwsJsonProtocolFactory.Builder)((BaseAwsJsonProtocolFactory.Builder)((BaseAwsJsonProtocolFactory.Builder)((BaseAwsJsonProtocolFactory.Builder)((BaseAwsJsonProtocolFactory.Builder)((BaseAwsJsonProtocolFactory.Builder)((BaseAwsJsonProtocolFactory.Builder)((BaseAwsJsonProtocolFactory.Builder)((BaseAwsJsonProtocolFactory.Builder)((BaseAwsJsonProtocolFactory.Builder)((BaseAwsJsonProtocolFactory.Builder)builder.clientConfiguration(this.clientConfiguration)).defaultServiceExceptionSupplier(SecretsManagerException::builder)).protocol(AwsJsonProtocol.AWS_JSON)).protocolVersion("1.1")).registerModeledException(ExceptionMetadata.builder().errorCode("EncryptionFailure").exceptionBuilderSupplier(EncryptionFailureException::builder).httpStatusCode(400).build())).registerModeledException(ExceptionMetadata.builder().errorCode("InvalidParameterException").exceptionBuilderSupplier(InvalidParameterException::builder).httpStatusCode(400).build())).registerModeledException(ExceptionMetadata.builder().errorCode("PublicPolicyException").exceptionBuilderSupplier(PublicPolicyException::builder).httpStatusCode(400).build())).registerModeledException(ExceptionMetadata.builder().errorCode("MalformedPolicyDocumentException").exceptionBuilderSupplier(MalformedPolicyDocumentException::builder).httpStatusCode(400).build())).registerModeledException(ExceptionMetadata.builder().errorCode("DecryptionFailure").exceptionBuilderSupplier(DecryptionFailureException::builder).httpStatusCode(400).build())).registerModeledException(ExceptionMetadata.builder().errorCode("InvalidRequestException").exceptionBuilderSupplier(InvalidRequestException::builder).httpStatusCode(400).build())).registerModeledException(ExceptionMetadata.builder().errorCode("ResourceNotFoundException").exceptionBuilderSupplier(ResourceNotFoundException::builder).httpStatusCode(400).build())).registerModeledException(ExceptionMetadata.builder().errorCode("InternalServiceError").exceptionBuilderSupplier(InternalServiceErrorException::builder).httpStatusCode(500).build())).registerModeledException(ExceptionMetadata.builder().errorCode("ResourceExistsException").exceptionBuilderSupplier(ResourceExistsException::builder).httpStatusCode(400).build())).registerModeledException(ExceptionMetadata.builder().errorCode("InvalidNextTokenException").exceptionBuilderSupplier(InvalidNextTokenException::builder).httpStatusCode(400).build())).registerModeledException(ExceptionMetadata.builder().errorCode("LimitExceededException").exceptionBuilderSupplier(LimitExceededException::builder).httpStatusCode(400).build())).registerModeledException(ExceptionMetadata.builder().errorCode("PreconditionNotMetException").exceptionBuilderSupplier(PreconditionNotMetException::builder).httpStatusCode(400).build());
    }

    @Override
    public final SecretsManagerServiceClientConfiguration serviceClientConfiguration() {
        return this.serviceClientConfiguration;
    }

    @Override
    public void close() {
        this.clientHandler.close();
    }
}

