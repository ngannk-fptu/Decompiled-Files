/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.awscore.client.handler.AwsSyncClientHandler
 *  software.amazon.awssdk.awscore.exception.AwsServiceException
 *  software.amazon.awssdk.awscore.internal.AwsProtocolMetadata
 *  software.amazon.awssdk.awscore.internal.AwsServiceProtocol
 *  software.amazon.awssdk.core.RequestOverrideConfiguration
 *  software.amazon.awssdk.core.SdkPlugin
 *  software.amazon.awssdk.core.SdkProtocolMetadata
 *  software.amazon.awssdk.core.SdkRequest
 *  software.amazon.awssdk.core.SdkServiceClientConfiguration$Builder
 *  software.amazon.awssdk.core.client.config.ClientOption
 *  software.amazon.awssdk.core.client.config.SdkClientConfiguration
 *  software.amazon.awssdk.core.client.config.SdkClientOption
 *  software.amazon.awssdk.core.client.handler.ClientExecutionParams
 *  software.amazon.awssdk.core.client.handler.SyncClientHandler
 *  software.amazon.awssdk.core.exception.SdkClientException
 *  software.amazon.awssdk.core.http.HttpResponseHandler
 *  software.amazon.awssdk.core.metrics.CoreMetric
 *  software.amazon.awssdk.core.runtime.transform.Marshaller
 *  software.amazon.awssdk.metrics.MetricCollector
 *  software.amazon.awssdk.metrics.MetricPublisher
 *  software.amazon.awssdk.metrics.NoOpMetricCollector
 *  software.amazon.awssdk.protocols.core.ExceptionMetadata
 *  software.amazon.awssdk.protocols.json.AwsJsonProtocol
 *  software.amazon.awssdk.protocols.json.AwsJsonProtocolFactory
 *  software.amazon.awssdk.protocols.json.BaseAwsJsonProtocolFactory
 *  software.amazon.awssdk.protocols.json.BaseAwsJsonProtocolFactory$Builder
 *  software.amazon.awssdk.protocols.json.JsonOperationMetadata
 *  software.amazon.awssdk.utils.Logger
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
import software.amazon.awssdk.core.SdkProtocolMetadata;
import software.amazon.awssdk.core.SdkRequest;
import software.amazon.awssdk.core.SdkServiceClientConfiguration;
import software.amazon.awssdk.core.client.config.ClientOption;
import software.amazon.awssdk.core.client.config.SdkClientConfiguration;
import software.amazon.awssdk.core.client.config.SdkClientOption;
import software.amazon.awssdk.core.client.handler.ClientExecutionParams;
import software.amazon.awssdk.core.client.handler.SyncClientHandler;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.http.HttpResponseHandler;
import software.amazon.awssdk.core.metrics.CoreMetric;
import software.amazon.awssdk.core.runtime.transform.Marshaller;
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
        HttpResponseHandler<AwsServiceException> errorResponseHandler = this.createErrorResponseHandler((BaseAwsJsonProtocolFactory)this.protocolFactory, operationMetadata);
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)cancelRotateSecretRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultSecretsManagerClient.resolveMetricPublishers(clientConfiguration, cancelRotateSecretRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"Secrets Manager");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"CancelRotateSecret");
            CancelRotateSecretResponse cancelRotateSecretResponse = (CancelRotateSecretResponse)this.clientHandler.execute(new ClientExecutionParams().withOperationName("CancelRotateSecret").withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withResponseHandler(responseHandler).withErrorResponseHandler(errorResponseHandler).withRequestConfiguration(clientConfiguration).withInput((SdkRequest)cancelRotateSecretRequest).withMetricCollector((MetricCollector)apiCallMetricCollector).withMarshaller((Marshaller)new CancelRotateSecretRequestMarshaller((BaseAwsJsonProtocolFactory)this.protocolFactory)));
            return cancelRotateSecretResponse;
        }
        finally {
            metricPublishers.forEach(arg_0 -> DefaultSecretsManagerClient.lambda$cancelRotateSecret$0((MetricCollector)apiCallMetricCollector, arg_0));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CreateSecretResponse createSecret(CreateSecretRequest createSecretRequest) throws InvalidParameterException, InvalidRequestException, LimitExceededException, EncryptionFailureException, ResourceExistsException, ResourceNotFoundException, MalformedPolicyDocumentException, InternalServiceErrorException, PreconditionNotMetException, DecryptionFailureException, AwsServiceException, SdkClientException, SecretsManagerException {
        JsonOperationMetadata operationMetadata = JsonOperationMetadata.builder().hasStreamingSuccessResponse(false).isPayloadJson(true).build();
        HttpResponseHandler responseHandler = this.protocolFactory.createResponseHandler(operationMetadata, CreateSecretResponse::builder);
        HttpResponseHandler<AwsServiceException> errorResponseHandler = this.createErrorResponseHandler((BaseAwsJsonProtocolFactory)this.protocolFactory, operationMetadata);
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)createSecretRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultSecretsManagerClient.resolveMetricPublishers(clientConfiguration, createSecretRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"Secrets Manager");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"CreateSecret");
            CreateSecretResponse createSecretResponse = (CreateSecretResponse)this.clientHandler.execute(new ClientExecutionParams().withOperationName("CreateSecret").withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withResponseHandler(responseHandler).withErrorResponseHandler(errorResponseHandler).withRequestConfiguration(clientConfiguration).withInput((SdkRequest)createSecretRequest).withMetricCollector((MetricCollector)apiCallMetricCollector).withMarshaller((Marshaller)new CreateSecretRequestMarshaller((BaseAwsJsonProtocolFactory)this.protocolFactory)));
            return createSecretResponse;
        }
        finally {
            metricPublishers.forEach(arg_0 -> DefaultSecretsManagerClient.lambda$createSecret$1((MetricCollector)apiCallMetricCollector, arg_0));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public DeleteResourcePolicyResponse deleteResourcePolicy(DeleteResourcePolicyRequest deleteResourcePolicyRequest) throws ResourceNotFoundException, InternalServiceErrorException, InvalidRequestException, InvalidParameterException, AwsServiceException, SdkClientException, SecretsManagerException {
        JsonOperationMetadata operationMetadata = JsonOperationMetadata.builder().hasStreamingSuccessResponse(false).isPayloadJson(true).build();
        HttpResponseHandler responseHandler = this.protocolFactory.createResponseHandler(operationMetadata, DeleteResourcePolicyResponse::builder);
        HttpResponseHandler<AwsServiceException> errorResponseHandler = this.createErrorResponseHandler((BaseAwsJsonProtocolFactory)this.protocolFactory, operationMetadata);
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)deleteResourcePolicyRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultSecretsManagerClient.resolveMetricPublishers(clientConfiguration, deleteResourcePolicyRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"Secrets Manager");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"DeleteResourcePolicy");
            DeleteResourcePolicyResponse deleteResourcePolicyResponse = (DeleteResourcePolicyResponse)this.clientHandler.execute(new ClientExecutionParams().withOperationName("DeleteResourcePolicy").withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withResponseHandler(responseHandler).withErrorResponseHandler(errorResponseHandler).withRequestConfiguration(clientConfiguration).withInput((SdkRequest)deleteResourcePolicyRequest).withMetricCollector((MetricCollector)apiCallMetricCollector).withMarshaller((Marshaller)new DeleteResourcePolicyRequestMarshaller((BaseAwsJsonProtocolFactory)this.protocolFactory)));
            return deleteResourcePolicyResponse;
        }
        finally {
            metricPublishers.forEach(arg_0 -> DefaultSecretsManagerClient.lambda$deleteResourcePolicy$2((MetricCollector)apiCallMetricCollector, arg_0));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public DeleteSecretResponse deleteSecret(DeleteSecretRequest deleteSecretRequest) throws ResourceNotFoundException, InvalidParameterException, InvalidRequestException, InternalServiceErrorException, AwsServiceException, SdkClientException, SecretsManagerException {
        JsonOperationMetadata operationMetadata = JsonOperationMetadata.builder().hasStreamingSuccessResponse(false).isPayloadJson(true).build();
        HttpResponseHandler responseHandler = this.protocolFactory.createResponseHandler(operationMetadata, DeleteSecretResponse::builder);
        HttpResponseHandler<AwsServiceException> errorResponseHandler = this.createErrorResponseHandler((BaseAwsJsonProtocolFactory)this.protocolFactory, operationMetadata);
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)deleteSecretRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultSecretsManagerClient.resolveMetricPublishers(clientConfiguration, deleteSecretRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"Secrets Manager");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"DeleteSecret");
            DeleteSecretResponse deleteSecretResponse = (DeleteSecretResponse)this.clientHandler.execute(new ClientExecutionParams().withOperationName("DeleteSecret").withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withResponseHandler(responseHandler).withErrorResponseHandler(errorResponseHandler).withRequestConfiguration(clientConfiguration).withInput((SdkRequest)deleteSecretRequest).withMetricCollector((MetricCollector)apiCallMetricCollector).withMarshaller((Marshaller)new DeleteSecretRequestMarshaller((BaseAwsJsonProtocolFactory)this.protocolFactory)));
            return deleteSecretResponse;
        }
        finally {
            metricPublishers.forEach(arg_0 -> DefaultSecretsManagerClient.lambda$deleteSecret$3((MetricCollector)apiCallMetricCollector, arg_0));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public DescribeSecretResponse describeSecret(DescribeSecretRequest describeSecretRequest) throws ResourceNotFoundException, InternalServiceErrorException, InvalidParameterException, AwsServiceException, SdkClientException, SecretsManagerException {
        JsonOperationMetadata operationMetadata = JsonOperationMetadata.builder().hasStreamingSuccessResponse(false).isPayloadJson(true).build();
        HttpResponseHandler responseHandler = this.protocolFactory.createResponseHandler(operationMetadata, DescribeSecretResponse::builder);
        HttpResponseHandler<AwsServiceException> errorResponseHandler = this.createErrorResponseHandler((BaseAwsJsonProtocolFactory)this.protocolFactory, operationMetadata);
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)describeSecretRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultSecretsManagerClient.resolveMetricPublishers(clientConfiguration, describeSecretRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"Secrets Manager");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"DescribeSecret");
            DescribeSecretResponse describeSecretResponse = (DescribeSecretResponse)this.clientHandler.execute(new ClientExecutionParams().withOperationName("DescribeSecret").withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withResponseHandler(responseHandler).withErrorResponseHandler(errorResponseHandler).withRequestConfiguration(clientConfiguration).withInput((SdkRequest)describeSecretRequest).withMetricCollector((MetricCollector)apiCallMetricCollector).withMarshaller((Marshaller)new DescribeSecretRequestMarshaller((BaseAwsJsonProtocolFactory)this.protocolFactory)));
            return describeSecretResponse;
        }
        finally {
            metricPublishers.forEach(arg_0 -> DefaultSecretsManagerClient.lambda$describeSecret$4((MetricCollector)apiCallMetricCollector, arg_0));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public GetRandomPasswordResponse getRandomPassword(GetRandomPasswordRequest getRandomPasswordRequest) throws InvalidParameterException, InvalidRequestException, InternalServiceErrorException, AwsServiceException, SdkClientException, SecretsManagerException {
        JsonOperationMetadata operationMetadata = JsonOperationMetadata.builder().hasStreamingSuccessResponse(false).isPayloadJson(true).build();
        HttpResponseHandler responseHandler = this.protocolFactory.createResponseHandler(operationMetadata, GetRandomPasswordResponse::builder);
        HttpResponseHandler<AwsServiceException> errorResponseHandler = this.createErrorResponseHandler((BaseAwsJsonProtocolFactory)this.protocolFactory, operationMetadata);
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)getRandomPasswordRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultSecretsManagerClient.resolveMetricPublishers(clientConfiguration, getRandomPasswordRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"Secrets Manager");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"GetRandomPassword");
            GetRandomPasswordResponse getRandomPasswordResponse = (GetRandomPasswordResponse)this.clientHandler.execute(new ClientExecutionParams().withOperationName("GetRandomPassword").withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withResponseHandler(responseHandler).withErrorResponseHandler(errorResponseHandler).withRequestConfiguration(clientConfiguration).withInput((SdkRequest)getRandomPasswordRequest).withMetricCollector((MetricCollector)apiCallMetricCollector).withMarshaller((Marshaller)new GetRandomPasswordRequestMarshaller((BaseAwsJsonProtocolFactory)this.protocolFactory)));
            return getRandomPasswordResponse;
        }
        finally {
            metricPublishers.forEach(arg_0 -> DefaultSecretsManagerClient.lambda$getRandomPassword$5((MetricCollector)apiCallMetricCollector, arg_0));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public GetResourcePolicyResponse getResourcePolicy(GetResourcePolicyRequest getResourcePolicyRequest) throws ResourceNotFoundException, InternalServiceErrorException, InvalidRequestException, InvalidParameterException, AwsServiceException, SdkClientException, SecretsManagerException {
        JsonOperationMetadata operationMetadata = JsonOperationMetadata.builder().hasStreamingSuccessResponse(false).isPayloadJson(true).build();
        HttpResponseHandler responseHandler = this.protocolFactory.createResponseHandler(operationMetadata, GetResourcePolicyResponse::builder);
        HttpResponseHandler<AwsServiceException> errorResponseHandler = this.createErrorResponseHandler((BaseAwsJsonProtocolFactory)this.protocolFactory, operationMetadata);
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)getResourcePolicyRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultSecretsManagerClient.resolveMetricPublishers(clientConfiguration, getResourcePolicyRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"Secrets Manager");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"GetResourcePolicy");
            GetResourcePolicyResponse getResourcePolicyResponse = (GetResourcePolicyResponse)this.clientHandler.execute(new ClientExecutionParams().withOperationName("GetResourcePolicy").withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withResponseHandler(responseHandler).withErrorResponseHandler(errorResponseHandler).withRequestConfiguration(clientConfiguration).withInput((SdkRequest)getResourcePolicyRequest).withMetricCollector((MetricCollector)apiCallMetricCollector).withMarshaller((Marshaller)new GetResourcePolicyRequestMarshaller((BaseAwsJsonProtocolFactory)this.protocolFactory)));
            return getResourcePolicyResponse;
        }
        finally {
            metricPublishers.forEach(arg_0 -> DefaultSecretsManagerClient.lambda$getResourcePolicy$6((MetricCollector)apiCallMetricCollector, arg_0));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public GetSecretValueResponse getSecretValue(GetSecretValueRequest getSecretValueRequest) throws ResourceNotFoundException, InvalidParameterException, InvalidRequestException, DecryptionFailureException, InternalServiceErrorException, AwsServiceException, SdkClientException, SecretsManagerException {
        JsonOperationMetadata operationMetadata = JsonOperationMetadata.builder().hasStreamingSuccessResponse(false).isPayloadJson(true).build();
        HttpResponseHandler responseHandler = this.protocolFactory.createResponseHandler(operationMetadata, GetSecretValueResponse::builder);
        HttpResponseHandler<AwsServiceException> errorResponseHandler = this.createErrorResponseHandler((BaseAwsJsonProtocolFactory)this.protocolFactory, operationMetadata);
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)getSecretValueRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultSecretsManagerClient.resolveMetricPublishers(clientConfiguration, getSecretValueRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"Secrets Manager");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"GetSecretValue");
            GetSecretValueResponse getSecretValueResponse = (GetSecretValueResponse)this.clientHandler.execute(new ClientExecutionParams().withOperationName("GetSecretValue").withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withResponseHandler(responseHandler).withErrorResponseHandler(errorResponseHandler).withRequestConfiguration(clientConfiguration).withInput((SdkRequest)getSecretValueRequest).withMetricCollector((MetricCollector)apiCallMetricCollector).withMarshaller((Marshaller)new GetSecretValueRequestMarshaller((BaseAwsJsonProtocolFactory)this.protocolFactory)));
            return getSecretValueResponse;
        }
        finally {
            metricPublishers.forEach(arg_0 -> DefaultSecretsManagerClient.lambda$getSecretValue$7((MetricCollector)apiCallMetricCollector, arg_0));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public ListSecretVersionIdsResponse listSecretVersionIds(ListSecretVersionIdsRequest listSecretVersionIdsRequest) throws InvalidNextTokenException, ResourceNotFoundException, InternalServiceErrorException, InvalidParameterException, AwsServiceException, SdkClientException, SecretsManagerException {
        JsonOperationMetadata operationMetadata = JsonOperationMetadata.builder().hasStreamingSuccessResponse(false).isPayloadJson(true).build();
        HttpResponseHandler responseHandler = this.protocolFactory.createResponseHandler(operationMetadata, ListSecretVersionIdsResponse::builder);
        HttpResponseHandler<AwsServiceException> errorResponseHandler = this.createErrorResponseHandler((BaseAwsJsonProtocolFactory)this.protocolFactory, operationMetadata);
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)listSecretVersionIdsRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultSecretsManagerClient.resolveMetricPublishers(clientConfiguration, listSecretVersionIdsRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"Secrets Manager");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"ListSecretVersionIds");
            ListSecretVersionIdsResponse listSecretVersionIdsResponse = (ListSecretVersionIdsResponse)this.clientHandler.execute(new ClientExecutionParams().withOperationName("ListSecretVersionIds").withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withResponseHandler(responseHandler).withErrorResponseHandler(errorResponseHandler).withRequestConfiguration(clientConfiguration).withInput((SdkRequest)listSecretVersionIdsRequest).withMetricCollector((MetricCollector)apiCallMetricCollector).withMarshaller((Marshaller)new ListSecretVersionIdsRequestMarshaller((BaseAwsJsonProtocolFactory)this.protocolFactory)));
            return listSecretVersionIdsResponse;
        }
        finally {
            metricPublishers.forEach(arg_0 -> DefaultSecretsManagerClient.lambda$listSecretVersionIds$8((MetricCollector)apiCallMetricCollector, arg_0));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public ListSecretsResponse listSecrets(ListSecretsRequest listSecretsRequest) throws InvalidParameterException, InvalidRequestException, InvalidNextTokenException, InternalServiceErrorException, AwsServiceException, SdkClientException, SecretsManagerException {
        JsonOperationMetadata operationMetadata = JsonOperationMetadata.builder().hasStreamingSuccessResponse(false).isPayloadJson(true).build();
        HttpResponseHandler responseHandler = this.protocolFactory.createResponseHandler(operationMetadata, ListSecretsResponse::builder);
        HttpResponseHandler<AwsServiceException> errorResponseHandler = this.createErrorResponseHandler((BaseAwsJsonProtocolFactory)this.protocolFactory, operationMetadata);
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)listSecretsRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultSecretsManagerClient.resolveMetricPublishers(clientConfiguration, listSecretsRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"Secrets Manager");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"ListSecrets");
            ListSecretsResponse listSecretsResponse = (ListSecretsResponse)this.clientHandler.execute(new ClientExecutionParams().withOperationName("ListSecrets").withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withResponseHandler(responseHandler).withErrorResponseHandler(errorResponseHandler).withRequestConfiguration(clientConfiguration).withInput((SdkRequest)listSecretsRequest).withMetricCollector((MetricCollector)apiCallMetricCollector).withMarshaller((Marshaller)new ListSecretsRequestMarshaller((BaseAwsJsonProtocolFactory)this.protocolFactory)));
            return listSecretsResponse;
        }
        finally {
            metricPublishers.forEach(arg_0 -> DefaultSecretsManagerClient.lambda$listSecrets$9((MetricCollector)apiCallMetricCollector, arg_0));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public PutResourcePolicyResponse putResourcePolicy(PutResourcePolicyRequest putResourcePolicyRequest) throws MalformedPolicyDocumentException, ResourceNotFoundException, InvalidParameterException, InternalServiceErrorException, InvalidRequestException, PublicPolicyException, AwsServiceException, SdkClientException, SecretsManagerException {
        JsonOperationMetadata operationMetadata = JsonOperationMetadata.builder().hasStreamingSuccessResponse(false).isPayloadJson(true).build();
        HttpResponseHandler responseHandler = this.protocolFactory.createResponseHandler(operationMetadata, PutResourcePolicyResponse::builder);
        HttpResponseHandler<AwsServiceException> errorResponseHandler = this.createErrorResponseHandler((BaseAwsJsonProtocolFactory)this.protocolFactory, operationMetadata);
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)putResourcePolicyRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultSecretsManagerClient.resolveMetricPublishers(clientConfiguration, putResourcePolicyRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"Secrets Manager");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"PutResourcePolicy");
            PutResourcePolicyResponse putResourcePolicyResponse = (PutResourcePolicyResponse)this.clientHandler.execute(new ClientExecutionParams().withOperationName("PutResourcePolicy").withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withResponseHandler(responseHandler).withErrorResponseHandler(errorResponseHandler).withRequestConfiguration(clientConfiguration).withInput((SdkRequest)putResourcePolicyRequest).withMetricCollector((MetricCollector)apiCallMetricCollector).withMarshaller((Marshaller)new PutResourcePolicyRequestMarshaller((BaseAwsJsonProtocolFactory)this.protocolFactory)));
            return putResourcePolicyResponse;
        }
        finally {
            metricPublishers.forEach(arg_0 -> DefaultSecretsManagerClient.lambda$putResourcePolicy$10((MetricCollector)apiCallMetricCollector, arg_0));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public PutSecretValueResponse putSecretValue(PutSecretValueRequest putSecretValueRequest) throws InvalidParameterException, InvalidRequestException, LimitExceededException, EncryptionFailureException, ResourceExistsException, ResourceNotFoundException, InternalServiceErrorException, DecryptionFailureException, AwsServiceException, SdkClientException, SecretsManagerException {
        JsonOperationMetadata operationMetadata = JsonOperationMetadata.builder().hasStreamingSuccessResponse(false).isPayloadJson(true).build();
        HttpResponseHandler responseHandler = this.protocolFactory.createResponseHandler(operationMetadata, PutSecretValueResponse::builder);
        HttpResponseHandler<AwsServiceException> errorResponseHandler = this.createErrorResponseHandler((BaseAwsJsonProtocolFactory)this.protocolFactory, operationMetadata);
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)putSecretValueRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultSecretsManagerClient.resolveMetricPublishers(clientConfiguration, putSecretValueRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"Secrets Manager");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"PutSecretValue");
            PutSecretValueResponse putSecretValueResponse = (PutSecretValueResponse)this.clientHandler.execute(new ClientExecutionParams().withOperationName("PutSecretValue").withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withResponseHandler(responseHandler).withErrorResponseHandler(errorResponseHandler).withRequestConfiguration(clientConfiguration).withInput((SdkRequest)putSecretValueRequest).withMetricCollector((MetricCollector)apiCallMetricCollector).withMarshaller((Marshaller)new PutSecretValueRequestMarshaller((BaseAwsJsonProtocolFactory)this.protocolFactory)));
            return putSecretValueResponse;
        }
        finally {
            metricPublishers.forEach(arg_0 -> DefaultSecretsManagerClient.lambda$putSecretValue$11((MetricCollector)apiCallMetricCollector, arg_0));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public RemoveRegionsFromReplicationResponse removeRegionsFromReplication(RemoveRegionsFromReplicationRequest removeRegionsFromReplicationRequest) throws ResourceNotFoundException, InvalidRequestException, InvalidParameterException, InternalServiceErrorException, AwsServiceException, SdkClientException, SecretsManagerException {
        JsonOperationMetadata operationMetadata = JsonOperationMetadata.builder().hasStreamingSuccessResponse(false).isPayloadJson(true).build();
        HttpResponseHandler responseHandler = this.protocolFactory.createResponseHandler(operationMetadata, RemoveRegionsFromReplicationResponse::builder);
        HttpResponseHandler<AwsServiceException> errorResponseHandler = this.createErrorResponseHandler((BaseAwsJsonProtocolFactory)this.protocolFactory, operationMetadata);
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)removeRegionsFromReplicationRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultSecretsManagerClient.resolveMetricPublishers(clientConfiguration, removeRegionsFromReplicationRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"Secrets Manager");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"RemoveRegionsFromReplication");
            RemoveRegionsFromReplicationResponse removeRegionsFromReplicationResponse = (RemoveRegionsFromReplicationResponse)this.clientHandler.execute(new ClientExecutionParams().withOperationName("RemoveRegionsFromReplication").withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withResponseHandler(responseHandler).withErrorResponseHandler(errorResponseHandler).withRequestConfiguration(clientConfiguration).withInput((SdkRequest)removeRegionsFromReplicationRequest).withMetricCollector((MetricCollector)apiCallMetricCollector).withMarshaller((Marshaller)new RemoveRegionsFromReplicationRequestMarshaller((BaseAwsJsonProtocolFactory)this.protocolFactory)));
            return removeRegionsFromReplicationResponse;
        }
        finally {
            metricPublishers.forEach(arg_0 -> DefaultSecretsManagerClient.lambda$removeRegionsFromReplication$12((MetricCollector)apiCallMetricCollector, arg_0));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public ReplicateSecretToRegionsResponse replicateSecretToRegions(ReplicateSecretToRegionsRequest replicateSecretToRegionsRequest) throws ResourceNotFoundException, InvalidRequestException, InvalidParameterException, InternalServiceErrorException, AwsServiceException, SdkClientException, SecretsManagerException {
        JsonOperationMetadata operationMetadata = JsonOperationMetadata.builder().hasStreamingSuccessResponse(false).isPayloadJson(true).build();
        HttpResponseHandler responseHandler = this.protocolFactory.createResponseHandler(operationMetadata, ReplicateSecretToRegionsResponse::builder);
        HttpResponseHandler<AwsServiceException> errorResponseHandler = this.createErrorResponseHandler((BaseAwsJsonProtocolFactory)this.protocolFactory, operationMetadata);
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)replicateSecretToRegionsRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultSecretsManagerClient.resolveMetricPublishers(clientConfiguration, replicateSecretToRegionsRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"Secrets Manager");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"ReplicateSecretToRegions");
            ReplicateSecretToRegionsResponse replicateSecretToRegionsResponse = (ReplicateSecretToRegionsResponse)this.clientHandler.execute(new ClientExecutionParams().withOperationName("ReplicateSecretToRegions").withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withResponseHandler(responseHandler).withErrorResponseHandler(errorResponseHandler).withRequestConfiguration(clientConfiguration).withInput((SdkRequest)replicateSecretToRegionsRequest).withMetricCollector((MetricCollector)apiCallMetricCollector).withMarshaller((Marshaller)new ReplicateSecretToRegionsRequestMarshaller((BaseAwsJsonProtocolFactory)this.protocolFactory)));
            return replicateSecretToRegionsResponse;
        }
        finally {
            metricPublishers.forEach(arg_0 -> DefaultSecretsManagerClient.lambda$replicateSecretToRegions$13((MetricCollector)apiCallMetricCollector, arg_0));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public RestoreSecretResponse restoreSecret(RestoreSecretRequest restoreSecretRequest) throws ResourceNotFoundException, InvalidParameterException, InvalidRequestException, InternalServiceErrorException, AwsServiceException, SdkClientException, SecretsManagerException {
        JsonOperationMetadata operationMetadata = JsonOperationMetadata.builder().hasStreamingSuccessResponse(false).isPayloadJson(true).build();
        HttpResponseHandler responseHandler = this.protocolFactory.createResponseHandler(operationMetadata, RestoreSecretResponse::builder);
        HttpResponseHandler<AwsServiceException> errorResponseHandler = this.createErrorResponseHandler((BaseAwsJsonProtocolFactory)this.protocolFactory, operationMetadata);
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)restoreSecretRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultSecretsManagerClient.resolveMetricPublishers(clientConfiguration, restoreSecretRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"Secrets Manager");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"RestoreSecret");
            RestoreSecretResponse restoreSecretResponse = (RestoreSecretResponse)this.clientHandler.execute(new ClientExecutionParams().withOperationName("RestoreSecret").withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withResponseHandler(responseHandler).withErrorResponseHandler(errorResponseHandler).withRequestConfiguration(clientConfiguration).withInput((SdkRequest)restoreSecretRequest).withMetricCollector((MetricCollector)apiCallMetricCollector).withMarshaller((Marshaller)new RestoreSecretRequestMarshaller((BaseAwsJsonProtocolFactory)this.protocolFactory)));
            return restoreSecretResponse;
        }
        finally {
            metricPublishers.forEach(arg_0 -> DefaultSecretsManagerClient.lambda$restoreSecret$14((MetricCollector)apiCallMetricCollector, arg_0));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public RotateSecretResponse rotateSecret(RotateSecretRequest rotateSecretRequest) throws ResourceNotFoundException, InvalidParameterException, InternalServiceErrorException, InvalidRequestException, AwsServiceException, SdkClientException, SecretsManagerException {
        JsonOperationMetadata operationMetadata = JsonOperationMetadata.builder().hasStreamingSuccessResponse(false).isPayloadJson(true).build();
        HttpResponseHandler responseHandler = this.protocolFactory.createResponseHandler(operationMetadata, RotateSecretResponse::builder);
        HttpResponseHandler<AwsServiceException> errorResponseHandler = this.createErrorResponseHandler((BaseAwsJsonProtocolFactory)this.protocolFactory, operationMetadata);
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)rotateSecretRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultSecretsManagerClient.resolveMetricPublishers(clientConfiguration, rotateSecretRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"Secrets Manager");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"RotateSecret");
            RotateSecretResponse rotateSecretResponse = (RotateSecretResponse)this.clientHandler.execute(new ClientExecutionParams().withOperationName("RotateSecret").withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withResponseHandler(responseHandler).withErrorResponseHandler(errorResponseHandler).withRequestConfiguration(clientConfiguration).withInput((SdkRequest)rotateSecretRequest).withMetricCollector((MetricCollector)apiCallMetricCollector).withMarshaller((Marshaller)new RotateSecretRequestMarshaller((BaseAwsJsonProtocolFactory)this.protocolFactory)));
            return rotateSecretResponse;
        }
        finally {
            metricPublishers.forEach(arg_0 -> DefaultSecretsManagerClient.lambda$rotateSecret$15((MetricCollector)apiCallMetricCollector, arg_0));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public StopReplicationToReplicaResponse stopReplicationToReplica(StopReplicationToReplicaRequest stopReplicationToReplicaRequest) throws ResourceNotFoundException, InvalidRequestException, InvalidParameterException, InternalServiceErrorException, AwsServiceException, SdkClientException, SecretsManagerException {
        JsonOperationMetadata operationMetadata = JsonOperationMetadata.builder().hasStreamingSuccessResponse(false).isPayloadJson(true).build();
        HttpResponseHandler responseHandler = this.protocolFactory.createResponseHandler(operationMetadata, StopReplicationToReplicaResponse::builder);
        HttpResponseHandler<AwsServiceException> errorResponseHandler = this.createErrorResponseHandler((BaseAwsJsonProtocolFactory)this.protocolFactory, operationMetadata);
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)stopReplicationToReplicaRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultSecretsManagerClient.resolveMetricPublishers(clientConfiguration, stopReplicationToReplicaRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"Secrets Manager");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"StopReplicationToReplica");
            StopReplicationToReplicaResponse stopReplicationToReplicaResponse = (StopReplicationToReplicaResponse)this.clientHandler.execute(new ClientExecutionParams().withOperationName("StopReplicationToReplica").withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withResponseHandler(responseHandler).withErrorResponseHandler(errorResponseHandler).withRequestConfiguration(clientConfiguration).withInput((SdkRequest)stopReplicationToReplicaRequest).withMetricCollector((MetricCollector)apiCallMetricCollector).withMarshaller((Marshaller)new StopReplicationToReplicaRequestMarshaller((BaseAwsJsonProtocolFactory)this.protocolFactory)));
            return stopReplicationToReplicaResponse;
        }
        finally {
            metricPublishers.forEach(arg_0 -> DefaultSecretsManagerClient.lambda$stopReplicationToReplica$16((MetricCollector)apiCallMetricCollector, arg_0));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public TagResourceResponse tagResource(TagResourceRequest tagResourceRequest) throws ResourceNotFoundException, InvalidRequestException, InvalidParameterException, InternalServiceErrorException, AwsServiceException, SdkClientException, SecretsManagerException {
        JsonOperationMetadata operationMetadata = JsonOperationMetadata.builder().hasStreamingSuccessResponse(false).isPayloadJson(true).build();
        HttpResponseHandler responseHandler = this.protocolFactory.createResponseHandler(operationMetadata, TagResourceResponse::builder);
        HttpResponseHandler<AwsServiceException> errorResponseHandler = this.createErrorResponseHandler((BaseAwsJsonProtocolFactory)this.protocolFactory, operationMetadata);
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)tagResourceRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultSecretsManagerClient.resolveMetricPublishers(clientConfiguration, tagResourceRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"Secrets Manager");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"TagResource");
            TagResourceResponse tagResourceResponse = (TagResourceResponse)this.clientHandler.execute(new ClientExecutionParams().withOperationName("TagResource").withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withResponseHandler(responseHandler).withErrorResponseHandler(errorResponseHandler).withRequestConfiguration(clientConfiguration).withInput((SdkRequest)tagResourceRequest).withMetricCollector((MetricCollector)apiCallMetricCollector).withMarshaller((Marshaller)new TagResourceRequestMarshaller((BaseAwsJsonProtocolFactory)this.protocolFactory)));
            return tagResourceResponse;
        }
        finally {
            metricPublishers.forEach(arg_0 -> DefaultSecretsManagerClient.lambda$tagResource$17((MetricCollector)apiCallMetricCollector, arg_0));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public UntagResourceResponse untagResource(UntagResourceRequest untagResourceRequest) throws ResourceNotFoundException, InvalidRequestException, InvalidParameterException, InternalServiceErrorException, AwsServiceException, SdkClientException, SecretsManagerException {
        JsonOperationMetadata operationMetadata = JsonOperationMetadata.builder().hasStreamingSuccessResponse(false).isPayloadJson(true).build();
        HttpResponseHandler responseHandler = this.protocolFactory.createResponseHandler(operationMetadata, UntagResourceResponse::builder);
        HttpResponseHandler<AwsServiceException> errorResponseHandler = this.createErrorResponseHandler((BaseAwsJsonProtocolFactory)this.protocolFactory, operationMetadata);
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)untagResourceRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultSecretsManagerClient.resolveMetricPublishers(clientConfiguration, untagResourceRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"Secrets Manager");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"UntagResource");
            UntagResourceResponse untagResourceResponse = (UntagResourceResponse)this.clientHandler.execute(new ClientExecutionParams().withOperationName("UntagResource").withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withResponseHandler(responseHandler).withErrorResponseHandler(errorResponseHandler).withRequestConfiguration(clientConfiguration).withInput((SdkRequest)untagResourceRequest).withMetricCollector((MetricCollector)apiCallMetricCollector).withMarshaller((Marshaller)new UntagResourceRequestMarshaller((BaseAwsJsonProtocolFactory)this.protocolFactory)));
            return untagResourceResponse;
        }
        finally {
            metricPublishers.forEach(arg_0 -> DefaultSecretsManagerClient.lambda$untagResource$18((MetricCollector)apiCallMetricCollector, arg_0));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public UpdateSecretResponse updateSecret(UpdateSecretRequest updateSecretRequest) throws InvalidParameterException, InvalidRequestException, LimitExceededException, EncryptionFailureException, ResourceExistsException, ResourceNotFoundException, MalformedPolicyDocumentException, InternalServiceErrorException, PreconditionNotMetException, DecryptionFailureException, AwsServiceException, SdkClientException, SecretsManagerException {
        JsonOperationMetadata operationMetadata = JsonOperationMetadata.builder().hasStreamingSuccessResponse(false).isPayloadJson(true).build();
        HttpResponseHandler responseHandler = this.protocolFactory.createResponseHandler(operationMetadata, UpdateSecretResponse::builder);
        HttpResponseHandler<AwsServiceException> errorResponseHandler = this.createErrorResponseHandler((BaseAwsJsonProtocolFactory)this.protocolFactory, operationMetadata);
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)updateSecretRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultSecretsManagerClient.resolveMetricPublishers(clientConfiguration, updateSecretRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"Secrets Manager");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"UpdateSecret");
            UpdateSecretResponse updateSecretResponse = (UpdateSecretResponse)this.clientHandler.execute(new ClientExecutionParams().withOperationName("UpdateSecret").withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withResponseHandler(responseHandler).withErrorResponseHandler(errorResponseHandler).withRequestConfiguration(clientConfiguration).withInput((SdkRequest)updateSecretRequest).withMetricCollector((MetricCollector)apiCallMetricCollector).withMarshaller((Marshaller)new UpdateSecretRequestMarshaller((BaseAwsJsonProtocolFactory)this.protocolFactory)));
            return updateSecretResponse;
        }
        finally {
            metricPublishers.forEach(arg_0 -> DefaultSecretsManagerClient.lambda$updateSecret$19((MetricCollector)apiCallMetricCollector, arg_0));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public UpdateSecretVersionStageResponse updateSecretVersionStage(UpdateSecretVersionStageRequest updateSecretVersionStageRequest) throws ResourceNotFoundException, InvalidParameterException, InvalidRequestException, LimitExceededException, InternalServiceErrorException, AwsServiceException, SdkClientException, SecretsManagerException {
        JsonOperationMetadata operationMetadata = JsonOperationMetadata.builder().hasStreamingSuccessResponse(false).isPayloadJson(true).build();
        HttpResponseHandler responseHandler = this.protocolFactory.createResponseHandler(operationMetadata, UpdateSecretVersionStageResponse::builder);
        HttpResponseHandler<AwsServiceException> errorResponseHandler = this.createErrorResponseHandler((BaseAwsJsonProtocolFactory)this.protocolFactory, operationMetadata);
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)updateSecretVersionStageRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultSecretsManagerClient.resolveMetricPublishers(clientConfiguration, updateSecretVersionStageRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"Secrets Manager");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"UpdateSecretVersionStage");
            UpdateSecretVersionStageResponse updateSecretVersionStageResponse = (UpdateSecretVersionStageResponse)this.clientHandler.execute(new ClientExecutionParams().withOperationName("UpdateSecretVersionStage").withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withResponseHandler(responseHandler).withErrorResponseHandler(errorResponseHandler).withRequestConfiguration(clientConfiguration).withInput((SdkRequest)updateSecretVersionStageRequest).withMetricCollector((MetricCollector)apiCallMetricCollector).withMarshaller((Marshaller)new UpdateSecretVersionStageRequestMarshaller((BaseAwsJsonProtocolFactory)this.protocolFactory)));
            return updateSecretVersionStageResponse;
        }
        finally {
            metricPublishers.forEach(arg_0 -> DefaultSecretsManagerClient.lambda$updateSecretVersionStage$20((MetricCollector)apiCallMetricCollector, arg_0));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public ValidateResourcePolicyResponse validateResourcePolicy(ValidateResourcePolicyRequest validateResourcePolicyRequest) throws MalformedPolicyDocumentException, ResourceNotFoundException, InvalidParameterException, InternalServiceErrorException, InvalidRequestException, AwsServiceException, SdkClientException, SecretsManagerException {
        JsonOperationMetadata operationMetadata = JsonOperationMetadata.builder().hasStreamingSuccessResponse(false).isPayloadJson(true).build();
        HttpResponseHandler responseHandler = this.protocolFactory.createResponseHandler(operationMetadata, ValidateResourcePolicyResponse::builder);
        HttpResponseHandler<AwsServiceException> errorResponseHandler = this.createErrorResponseHandler((BaseAwsJsonProtocolFactory)this.protocolFactory, operationMetadata);
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)validateResourcePolicyRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultSecretsManagerClient.resolveMetricPublishers(clientConfiguration, validateResourcePolicyRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"Secrets Manager");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"ValidateResourcePolicy");
            ValidateResourcePolicyResponse validateResourcePolicyResponse = (ValidateResourcePolicyResponse)this.clientHandler.execute(new ClientExecutionParams().withOperationName("ValidateResourcePolicy").withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withResponseHandler(responseHandler).withErrorResponseHandler(errorResponseHandler).withRequestConfiguration(clientConfiguration).withInput((SdkRequest)validateResourcePolicyRequest).withMetricCollector((MetricCollector)apiCallMetricCollector).withMarshaller((Marshaller)new ValidateResourcePolicyRequestMarshaller((BaseAwsJsonProtocolFactory)this.protocolFactory)));
            return validateResourcePolicyResponse;
        }
        finally {
            metricPublishers.forEach(arg_0 -> DefaultSecretsManagerClient.lambda$validateResourcePolicy$21((MetricCollector)apiCallMetricCollector, arg_0));
        }
    }

    public final String serviceName() {
        return "secretsmanager";
    }

    private static List<MetricPublisher> resolveMetricPublishers(SdkClientConfiguration clientConfiguration, RequestOverrideConfiguration requestOverrideConfiguration) {
        List<MetricPublisher> publishers = null;
        if (requestOverrideConfiguration != null) {
            publishers = requestOverrideConfiguration.metricPublishers();
        }
        if (publishers == null || publishers.isEmpty()) {
            publishers = (List)clientConfiguration.option((ClientOption)SdkClientOption.METRIC_PUBLISHERS);
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
            plugin.configureClient((SdkServiceClientConfiguration.Builder)serviceConfigBuilder);
        }
        return serviceConfigBuilder.buildSdkClientConfiguration();
    }

    private <T extends BaseAwsJsonProtocolFactory.Builder<T>> T init(T builder) {
        return (T)builder.clientConfiguration(this.clientConfiguration).defaultServiceExceptionSupplier(SecretsManagerException::builder).protocol(AwsJsonProtocol.AWS_JSON).protocolVersion("1.1").registerModeledException(ExceptionMetadata.builder().errorCode("EncryptionFailure").exceptionBuilderSupplier(EncryptionFailureException::builder).httpStatusCode(Integer.valueOf(400)).build()).registerModeledException(ExceptionMetadata.builder().errorCode("InvalidParameterException").exceptionBuilderSupplier(InvalidParameterException::builder).httpStatusCode(Integer.valueOf(400)).build()).registerModeledException(ExceptionMetadata.builder().errorCode("PublicPolicyException").exceptionBuilderSupplier(PublicPolicyException::builder).httpStatusCode(Integer.valueOf(400)).build()).registerModeledException(ExceptionMetadata.builder().errorCode("MalformedPolicyDocumentException").exceptionBuilderSupplier(MalformedPolicyDocumentException::builder).httpStatusCode(Integer.valueOf(400)).build()).registerModeledException(ExceptionMetadata.builder().errorCode("DecryptionFailure").exceptionBuilderSupplier(DecryptionFailureException::builder).httpStatusCode(Integer.valueOf(400)).build()).registerModeledException(ExceptionMetadata.builder().errorCode("InvalidRequestException").exceptionBuilderSupplier(InvalidRequestException::builder).httpStatusCode(Integer.valueOf(400)).build()).registerModeledException(ExceptionMetadata.builder().errorCode("ResourceNotFoundException").exceptionBuilderSupplier(ResourceNotFoundException::builder).httpStatusCode(Integer.valueOf(400)).build()).registerModeledException(ExceptionMetadata.builder().errorCode("InternalServiceError").exceptionBuilderSupplier(InternalServiceErrorException::builder).httpStatusCode(Integer.valueOf(500)).build()).registerModeledException(ExceptionMetadata.builder().errorCode("ResourceExistsException").exceptionBuilderSupplier(ResourceExistsException::builder).httpStatusCode(Integer.valueOf(400)).build()).registerModeledException(ExceptionMetadata.builder().errorCode("InvalidNextTokenException").exceptionBuilderSupplier(InvalidNextTokenException::builder).httpStatusCode(Integer.valueOf(400)).build()).registerModeledException(ExceptionMetadata.builder().errorCode("LimitExceededException").exceptionBuilderSupplier(LimitExceededException::builder).httpStatusCode(Integer.valueOf(400)).build()).registerModeledException(ExceptionMetadata.builder().errorCode("PreconditionNotMetException").exceptionBuilderSupplier(PreconditionNotMetException::builder).httpStatusCode(Integer.valueOf(400)).build());
    }

    @Override
    public final SecretsManagerServiceClientConfiguration serviceClientConfiguration() {
        return this.serviceClientConfiguration;
    }

    public void close() {
        this.clientHandler.close();
    }

    private static /* synthetic */ void lambda$validateResourcePolicy$21(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$updateSecretVersionStage$20(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$updateSecret$19(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$untagResource$18(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$tagResource$17(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$stopReplicationToReplica$16(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$rotateSecret$15(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$restoreSecret$14(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$replicateSecretToRegions$13(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$removeRegionsFromReplication$12(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$putSecretValue$11(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$putResourcePolicy$10(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$listSecrets$9(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$listSecretVersionIds$8(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$getSecretValue$7(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$getResourcePolicy$6(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$getRandomPassword$5(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$describeSecret$4(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$deleteSecret$3(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$deleteResourcePolicy$2(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$createSecret$1(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$cancelRotateSecret$0(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }
}

