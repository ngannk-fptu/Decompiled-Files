/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.awscore.client.handler.AwsAsyncClientHandler
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
 *  software.amazon.awssdk.core.client.handler.AsyncClientHandler
 *  software.amazon.awssdk.core.client.handler.ClientExecutionParams
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
 *  software.amazon.awssdk.utils.CompletableFutureUtils
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
import software.amazon.awssdk.core.SdkProtocolMetadata;
import software.amazon.awssdk.core.SdkRequest;
import software.amazon.awssdk.core.SdkServiceClientConfiguration;
import software.amazon.awssdk.core.client.config.ClientOption;
import software.amazon.awssdk.core.client.config.SdkClientConfiguration;
import software.amazon.awssdk.core.client.config.SdkClientOption;
import software.amazon.awssdk.core.client.handler.AsyncClientHandler;
import software.amazon.awssdk.core.client.handler.ClientExecutionParams;
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
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)cancelRotateSecretRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultSecretsManagerAsyncClient.resolveMetricPublishers(clientConfiguration, cancelRotateSecretRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"Secrets Manager");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"CancelRotateSecret");
            JsonOperationMetadata operationMetadata = JsonOperationMetadata.builder().hasStreamingSuccessResponse(false).isPayloadJson(true).build();
            HttpResponseHandler responseHandler = this.protocolFactory.createResponseHandler(operationMetadata, CancelRotateSecretResponse::builder);
            HttpResponseHandler<AwsServiceException> errorResponseHandler = this.createErrorResponseHandler((BaseAwsJsonProtocolFactory)this.protocolFactory, operationMetadata);
            CompletableFuture executeFuture = this.clientHandler.execute(new ClientExecutionParams().withOperationName("CancelRotateSecret").withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withMarshaller((Marshaller)new CancelRotateSecretRequestMarshaller((BaseAwsJsonProtocolFactory)this.protocolFactory)).withResponseHandler(responseHandler).withErrorResponseHandler(errorResponseHandler).withRequestConfiguration(clientConfiguration).withMetricCollector((MetricCollector)apiCallMetricCollector).withInput((SdkRequest)cancelRotateSecretRequest));
            CompletionStage whenCompleted = executeFuture.whenComplete((arg_0, arg_1) -> DefaultSecretsManagerAsyncClient.lambda$cancelRotateSecret$1(metricPublishers, (MetricCollector)apiCallMetricCollector, arg_0, arg_1));
            executeFuture = CompletableFutureUtils.forwardExceptionTo((CompletableFuture)whenCompleted, (CompletableFuture)executeFuture);
            return executeFuture;
        }
        catch (Throwable t) {
            metricPublishers.forEach(arg_0 -> DefaultSecretsManagerAsyncClient.lambda$cancelRotateSecret$2((MetricCollector)apiCallMetricCollector, arg_0));
            return CompletableFutureUtils.failedFuture((Throwable)t);
        }
    }

    @Override
    public CompletableFuture<CreateSecretResponse> createSecret(CreateSecretRequest createSecretRequest) {
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)createSecretRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultSecretsManagerAsyncClient.resolveMetricPublishers(clientConfiguration, createSecretRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"Secrets Manager");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"CreateSecret");
            JsonOperationMetadata operationMetadata = JsonOperationMetadata.builder().hasStreamingSuccessResponse(false).isPayloadJson(true).build();
            HttpResponseHandler responseHandler = this.protocolFactory.createResponseHandler(operationMetadata, CreateSecretResponse::builder);
            HttpResponseHandler<AwsServiceException> errorResponseHandler = this.createErrorResponseHandler((BaseAwsJsonProtocolFactory)this.protocolFactory, operationMetadata);
            CompletableFuture executeFuture = this.clientHandler.execute(new ClientExecutionParams().withOperationName("CreateSecret").withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withMarshaller((Marshaller)new CreateSecretRequestMarshaller((BaseAwsJsonProtocolFactory)this.protocolFactory)).withResponseHandler(responseHandler).withErrorResponseHandler(errorResponseHandler).withRequestConfiguration(clientConfiguration).withMetricCollector((MetricCollector)apiCallMetricCollector).withInput((SdkRequest)createSecretRequest));
            CompletionStage whenCompleted = executeFuture.whenComplete((arg_0, arg_1) -> DefaultSecretsManagerAsyncClient.lambda$createSecret$4(metricPublishers, (MetricCollector)apiCallMetricCollector, arg_0, arg_1));
            executeFuture = CompletableFutureUtils.forwardExceptionTo((CompletableFuture)whenCompleted, (CompletableFuture)executeFuture);
            return executeFuture;
        }
        catch (Throwable t) {
            metricPublishers.forEach(arg_0 -> DefaultSecretsManagerAsyncClient.lambda$createSecret$5((MetricCollector)apiCallMetricCollector, arg_0));
            return CompletableFutureUtils.failedFuture((Throwable)t);
        }
    }

    @Override
    public CompletableFuture<DeleteResourcePolicyResponse> deleteResourcePolicy(DeleteResourcePolicyRequest deleteResourcePolicyRequest) {
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)deleteResourcePolicyRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultSecretsManagerAsyncClient.resolveMetricPublishers(clientConfiguration, deleteResourcePolicyRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"Secrets Manager");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"DeleteResourcePolicy");
            JsonOperationMetadata operationMetadata = JsonOperationMetadata.builder().hasStreamingSuccessResponse(false).isPayloadJson(true).build();
            HttpResponseHandler responseHandler = this.protocolFactory.createResponseHandler(operationMetadata, DeleteResourcePolicyResponse::builder);
            HttpResponseHandler<AwsServiceException> errorResponseHandler = this.createErrorResponseHandler((BaseAwsJsonProtocolFactory)this.protocolFactory, operationMetadata);
            CompletableFuture executeFuture = this.clientHandler.execute(new ClientExecutionParams().withOperationName("DeleteResourcePolicy").withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withMarshaller((Marshaller)new DeleteResourcePolicyRequestMarshaller((BaseAwsJsonProtocolFactory)this.protocolFactory)).withResponseHandler(responseHandler).withErrorResponseHandler(errorResponseHandler).withRequestConfiguration(clientConfiguration).withMetricCollector((MetricCollector)apiCallMetricCollector).withInput((SdkRequest)deleteResourcePolicyRequest));
            CompletionStage whenCompleted = executeFuture.whenComplete((arg_0, arg_1) -> DefaultSecretsManagerAsyncClient.lambda$deleteResourcePolicy$7(metricPublishers, (MetricCollector)apiCallMetricCollector, arg_0, arg_1));
            executeFuture = CompletableFutureUtils.forwardExceptionTo((CompletableFuture)whenCompleted, (CompletableFuture)executeFuture);
            return executeFuture;
        }
        catch (Throwable t) {
            metricPublishers.forEach(arg_0 -> DefaultSecretsManagerAsyncClient.lambda$deleteResourcePolicy$8((MetricCollector)apiCallMetricCollector, arg_0));
            return CompletableFutureUtils.failedFuture((Throwable)t);
        }
    }

    @Override
    public CompletableFuture<DeleteSecretResponse> deleteSecret(DeleteSecretRequest deleteSecretRequest) {
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)deleteSecretRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultSecretsManagerAsyncClient.resolveMetricPublishers(clientConfiguration, deleteSecretRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"Secrets Manager");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"DeleteSecret");
            JsonOperationMetadata operationMetadata = JsonOperationMetadata.builder().hasStreamingSuccessResponse(false).isPayloadJson(true).build();
            HttpResponseHandler responseHandler = this.protocolFactory.createResponseHandler(operationMetadata, DeleteSecretResponse::builder);
            HttpResponseHandler<AwsServiceException> errorResponseHandler = this.createErrorResponseHandler((BaseAwsJsonProtocolFactory)this.protocolFactory, operationMetadata);
            CompletableFuture executeFuture = this.clientHandler.execute(new ClientExecutionParams().withOperationName("DeleteSecret").withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withMarshaller((Marshaller)new DeleteSecretRequestMarshaller((BaseAwsJsonProtocolFactory)this.protocolFactory)).withResponseHandler(responseHandler).withErrorResponseHandler(errorResponseHandler).withRequestConfiguration(clientConfiguration).withMetricCollector((MetricCollector)apiCallMetricCollector).withInput((SdkRequest)deleteSecretRequest));
            CompletionStage whenCompleted = executeFuture.whenComplete((arg_0, arg_1) -> DefaultSecretsManagerAsyncClient.lambda$deleteSecret$10(metricPublishers, (MetricCollector)apiCallMetricCollector, arg_0, arg_1));
            executeFuture = CompletableFutureUtils.forwardExceptionTo((CompletableFuture)whenCompleted, (CompletableFuture)executeFuture);
            return executeFuture;
        }
        catch (Throwable t) {
            metricPublishers.forEach(arg_0 -> DefaultSecretsManagerAsyncClient.lambda$deleteSecret$11((MetricCollector)apiCallMetricCollector, arg_0));
            return CompletableFutureUtils.failedFuture((Throwable)t);
        }
    }

    @Override
    public CompletableFuture<DescribeSecretResponse> describeSecret(DescribeSecretRequest describeSecretRequest) {
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)describeSecretRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultSecretsManagerAsyncClient.resolveMetricPublishers(clientConfiguration, describeSecretRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"Secrets Manager");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"DescribeSecret");
            JsonOperationMetadata operationMetadata = JsonOperationMetadata.builder().hasStreamingSuccessResponse(false).isPayloadJson(true).build();
            HttpResponseHandler responseHandler = this.protocolFactory.createResponseHandler(operationMetadata, DescribeSecretResponse::builder);
            HttpResponseHandler<AwsServiceException> errorResponseHandler = this.createErrorResponseHandler((BaseAwsJsonProtocolFactory)this.protocolFactory, operationMetadata);
            CompletableFuture executeFuture = this.clientHandler.execute(new ClientExecutionParams().withOperationName("DescribeSecret").withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withMarshaller((Marshaller)new DescribeSecretRequestMarshaller((BaseAwsJsonProtocolFactory)this.protocolFactory)).withResponseHandler(responseHandler).withErrorResponseHandler(errorResponseHandler).withRequestConfiguration(clientConfiguration).withMetricCollector((MetricCollector)apiCallMetricCollector).withInput((SdkRequest)describeSecretRequest));
            CompletionStage whenCompleted = executeFuture.whenComplete((arg_0, arg_1) -> DefaultSecretsManagerAsyncClient.lambda$describeSecret$13(metricPublishers, (MetricCollector)apiCallMetricCollector, arg_0, arg_1));
            executeFuture = CompletableFutureUtils.forwardExceptionTo((CompletableFuture)whenCompleted, (CompletableFuture)executeFuture);
            return executeFuture;
        }
        catch (Throwable t) {
            metricPublishers.forEach(arg_0 -> DefaultSecretsManagerAsyncClient.lambda$describeSecret$14((MetricCollector)apiCallMetricCollector, arg_0));
            return CompletableFutureUtils.failedFuture((Throwable)t);
        }
    }

    @Override
    public CompletableFuture<GetRandomPasswordResponse> getRandomPassword(GetRandomPasswordRequest getRandomPasswordRequest) {
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)getRandomPasswordRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultSecretsManagerAsyncClient.resolveMetricPublishers(clientConfiguration, getRandomPasswordRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"Secrets Manager");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"GetRandomPassword");
            JsonOperationMetadata operationMetadata = JsonOperationMetadata.builder().hasStreamingSuccessResponse(false).isPayloadJson(true).build();
            HttpResponseHandler responseHandler = this.protocolFactory.createResponseHandler(operationMetadata, GetRandomPasswordResponse::builder);
            HttpResponseHandler<AwsServiceException> errorResponseHandler = this.createErrorResponseHandler((BaseAwsJsonProtocolFactory)this.protocolFactory, operationMetadata);
            CompletableFuture executeFuture = this.clientHandler.execute(new ClientExecutionParams().withOperationName("GetRandomPassword").withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withMarshaller((Marshaller)new GetRandomPasswordRequestMarshaller((BaseAwsJsonProtocolFactory)this.protocolFactory)).withResponseHandler(responseHandler).withErrorResponseHandler(errorResponseHandler).withRequestConfiguration(clientConfiguration).withMetricCollector((MetricCollector)apiCallMetricCollector).withInput((SdkRequest)getRandomPasswordRequest));
            CompletionStage whenCompleted = executeFuture.whenComplete((arg_0, arg_1) -> DefaultSecretsManagerAsyncClient.lambda$getRandomPassword$16(metricPublishers, (MetricCollector)apiCallMetricCollector, arg_0, arg_1));
            executeFuture = CompletableFutureUtils.forwardExceptionTo((CompletableFuture)whenCompleted, (CompletableFuture)executeFuture);
            return executeFuture;
        }
        catch (Throwable t) {
            metricPublishers.forEach(arg_0 -> DefaultSecretsManagerAsyncClient.lambda$getRandomPassword$17((MetricCollector)apiCallMetricCollector, arg_0));
            return CompletableFutureUtils.failedFuture((Throwable)t);
        }
    }

    @Override
    public CompletableFuture<GetResourcePolicyResponse> getResourcePolicy(GetResourcePolicyRequest getResourcePolicyRequest) {
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)getResourcePolicyRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultSecretsManagerAsyncClient.resolveMetricPublishers(clientConfiguration, getResourcePolicyRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"Secrets Manager");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"GetResourcePolicy");
            JsonOperationMetadata operationMetadata = JsonOperationMetadata.builder().hasStreamingSuccessResponse(false).isPayloadJson(true).build();
            HttpResponseHandler responseHandler = this.protocolFactory.createResponseHandler(operationMetadata, GetResourcePolicyResponse::builder);
            HttpResponseHandler<AwsServiceException> errorResponseHandler = this.createErrorResponseHandler((BaseAwsJsonProtocolFactory)this.protocolFactory, operationMetadata);
            CompletableFuture executeFuture = this.clientHandler.execute(new ClientExecutionParams().withOperationName("GetResourcePolicy").withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withMarshaller((Marshaller)new GetResourcePolicyRequestMarshaller((BaseAwsJsonProtocolFactory)this.protocolFactory)).withResponseHandler(responseHandler).withErrorResponseHandler(errorResponseHandler).withRequestConfiguration(clientConfiguration).withMetricCollector((MetricCollector)apiCallMetricCollector).withInput((SdkRequest)getResourcePolicyRequest));
            CompletionStage whenCompleted = executeFuture.whenComplete((arg_0, arg_1) -> DefaultSecretsManagerAsyncClient.lambda$getResourcePolicy$19(metricPublishers, (MetricCollector)apiCallMetricCollector, arg_0, arg_1));
            executeFuture = CompletableFutureUtils.forwardExceptionTo((CompletableFuture)whenCompleted, (CompletableFuture)executeFuture);
            return executeFuture;
        }
        catch (Throwable t) {
            metricPublishers.forEach(arg_0 -> DefaultSecretsManagerAsyncClient.lambda$getResourcePolicy$20((MetricCollector)apiCallMetricCollector, arg_0));
            return CompletableFutureUtils.failedFuture((Throwable)t);
        }
    }

    @Override
    public CompletableFuture<GetSecretValueResponse> getSecretValue(GetSecretValueRequest getSecretValueRequest) {
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)getSecretValueRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultSecretsManagerAsyncClient.resolveMetricPublishers(clientConfiguration, getSecretValueRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"Secrets Manager");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"GetSecretValue");
            JsonOperationMetadata operationMetadata = JsonOperationMetadata.builder().hasStreamingSuccessResponse(false).isPayloadJson(true).build();
            HttpResponseHandler responseHandler = this.protocolFactory.createResponseHandler(operationMetadata, GetSecretValueResponse::builder);
            HttpResponseHandler<AwsServiceException> errorResponseHandler = this.createErrorResponseHandler((BaseAwsJsonProtocolFactory)this.protocolFactory, operationMetadata);
            CompletableFuture executeFuture = this.clientHandler.execute(new ClientExecutionParams().withOperationName("GetSecretValue").withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withMarshaller((Marshaller)new GetSecretValueRequestMarshaller((BaseAwsJsonProtocolFactory)this.protocolFactory)).withResponseHandler(responseHandler).withErrorResponseHandler(errorResponseHandler).withRequestConfiguration(clientConfiguration).withMetricCollector((MetricCollector)apiCallMetricCollector).withInput((SdkRequest)getSecretValueRequest));
            CompletionStage whenCompleted = executeFuture.whenComplete((arg_0, arg_1) -> DefaultSecretsManagerAsyncClient.lambda$getSecretValue$22(metricPublishers, (MetricCollector)apiCallMetricCollector, arg_0, arg_1));
            executeFuture = CompletableFutureUtils.forwardExceptionTo((CompletableFuture)whenCompleted, (CompletableFuture)executeFuture);
            return executeFuture;
        }
        catch (Throwable t) {
            metricPublishers.forEach(arg_0 -> DefaultSecretsManagerAsyncClient.lambda$getSecretValue$23((MetricCollector)apiCallMetricCollector, arg_0));
            return CompletableFutureUtils.failedFuture((Throwable)t);
        }
    }

    @Override
    public CompletableFuture<ListSecretVersionIdsResponse> listSecretVersionIds(ListSecretVersionIdsRequest listSecretVersionIdsRequest) {
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)listSecretVersionIdsRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultSecretsManagerAsyncClient.resolveMetricPublishers(clientConfiguration, listSecretVersionIdsRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"Secrets Manager");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"ListSecretVersionIds");
            JsonOperationMetadata operationMetadata = JsonOperationMetadata.builder().hasStreamingSuccessResponse(false).isPayloadJson(true).build();
            HttpResponseHandler responseHandler = this.protocolFactory.createResponseHandler(operationMetadata, ListSecretVersionIdsResponse::builder);
            HttpResponseHandler<AwsServiceException> errorResponseHandler = this.createErrorResponseHandler((BaseAwsJsonProtocolFactory)this.protocolFactory, operationMetadata);
            CompletableFuture executeFuture = this.clientHandler.execute(new ClientExecutionParams().withOperationName("ListSecretVersionIds").withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withMarshaller((Marshaller)new ListSecretVersionIdsRequestMarshaller((BaseAwsJsonProtocolFactory)this.protocolFactory)).withResponseHandler(responseHandler).withErrorResponseHandler(errorResponseHandler).withRequestConfiguration(clientConfiguration).withMetricCollector((MetricCollector)apiCallMetricCollector).withInput((SdkRequest)listSecretVersionIdsRequest));
            CompletionStage whenCompleted = executeFuture.whenComplete((arg_0, arg_1) -> DefaultSecretsManagerAsyncClient.lambda$listSecretVersionIds$25(metricPublishers, (MetricCollector)apiCallMetricCollector, arg_0, arg_1));
            executeFuture = CompletableFutureUtils.forwardExceptionTo((CompletableFuture)whenCompleted, (CompletableFuture)executeFuture);
            return executeFuture;
        }
        catch (Throwable t) {
            metricPublishers.forEach(arg_0 -> DefaultSecretsManagerAsyncClient.lambda$listSecretVersionIds$26((MetricCollector)apiCallMetricCollector, arg_0));
            return CompletableFutureUtils.failedFuture((Throwable)t);
        }
    }

    @Override
    public CompletableFuture<ListSecretsResponse> listSecrets(ListSecretsRequest listSecretsRequest) {
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)listSecretsRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultSecretsManagerAsyncClient.resolveMetricPublishers(clientConfiguration, listSecretsRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"Secrets Manager");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"ListSecrets");
            JsonOperationMetadata operationMetadata = JsonOperationMetadata.builder().hasStreamingSuccessResponse(false).isPayloadJson(true).build();
            HttpResponseHandler responseHandler = this.protocolFactory.createResponseHandler(operationMetadata, ListSecretsResponse::builder);
            HttpResponseHandler<AwsServiceException> errorResponseHandler = this.createErrorResponseHandler((BaseAwsJsonProtocolFactory)this.protocolFactory, operationMetadata);
            CompletableFuture executeFuture = this.clientHandler.execute(new ClientExecutionParams().withOperationName("ListSecrets").withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withMarshaller((Marshaller)new ListSecretsRequestMarshaller((BaseAwsJsonProtocolFactory)this.protocolFactory)).withResponseHandler(responseHandler).withErrorResponseHandler(errorResponseHandler).withRequestConfiguration(clientConfiguration).withMetricCollector((MetricCollector)apiCallMetricCollector).withInput((SdkRequest)listSecretsRequest));
            CompletionStage whenCompleted = executeFuture.whenComplete((arg_0, arg_1) -> DefaultSecretsManagerAsyncClient.lambda$listSecrets$28(metricPublishers, (MetricCollector)apiCallMetricCollector, arg_0, arg_1));
            executeFuture = CompletableFutureUtils.forwardExceptionTo((CompletableFuture)whenCompleted, (CompletableFuture)executeFuture);
            return executeFuture;
        }
        catch (Throwable t) {
            metricPublishers.forEach(arg_0 -> DefaultSecretsManagerAsyncClient.lambda$listSecrets$29((MetricCollector)apiCallMetricCollector, arg_0));
            return CompletableFutureUtils.failedFuture((Throwable)t);
        }
    }

    @Override
    public CompletableFuture<PutResourcePolicyResponse> putResourcePolicy(PutResourcePolicyRequest putResourcePolicyRequest) {
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)putResourcePolicyRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultSecretsManagerAsyncClient.resolveMetricPublishers(clientConfiguration, putResourcePolicyRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"Secrets Manager");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"PutResourcePolicy");
            JsonOperationMetadata operationMetadata = JsonOperationMetadata.builder().hasStreamingSuccessResponse(false).isPayloadJson(true).build();
            HttpResponseHandler responseHandler = this.protocolFactory.createResponseHandler(operationMetadata, PutResourcePolicyResponse::builder);
            HttpResponseHandler<AwsServiceException> errorResponseHandler = this.createErrorResponseHandler((BaseAwsJsonProtocolFactory)this.protocolFactory, operationMetadata);
            CompletableFuture executeFuture = this.clientHandler.execute(new ClientExecutionParams().withOperationName("PutResourcePolicy").withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withMarshaller((Marshaller)new PutResourcePolicyRequestMarshaller((BaseAwsJsonProtocolFactory)this.protocolFactory)).withResponseHandler(responseHandler).withErrorResponseHandler(errorResponseHandler).withRequestConfiguration(clientConfiguration).withMetricCollector((MetricCollector)apiCallMetricCollector).withInput((SdkRequest)putResourcePolicyRequest));
            CompletionStage whenCompleted = executeFuture.whenComplete((arg_0, arg_1) -> DefaultSecretsManagerAsyncClient.lambda$putResourcePolicy$31(metricPublishers, (MetricCollector)apiCallMetricCollector, arg_0, arg_1));
            executeFuture = CompletableFutureUtils.forwardExceptionTo((CompletableFuture)whenCompleted, (CompletableFuture)executeFuture);
            return executeFuture;
        }
        catch (Throwable t) {
            metricPublishers.forEach(arg_0 -> DefaultSecretsManagerAsyncClient.lambda$putResourcePolicy$32((MetricCollector)apiCallMetricCollector, arg_0));
            return CompletableFutureUtils.failedFuture((Throwable)t);
        }
    }

    @Override
    public CompletableFuture<PutSecretValueResponse> putSecretValue(PutSecretValueRequest putSecretValueRequest) {
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)putSecretValueRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultSecretsManagerAsyncClient.resolveMetricPublishers(clientConfiguration, putSecretValueRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"Secrets Manager");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"PutSecretValue");
            JsonOperationMetadata operationMetadata = JsonOperationMetadata.builder().hasStreamingSuccessResponse(false).isPayloadJson(true).build();
            HttpResponseHandler responseHandler = this.protocolFactory.createResponseHandler(operationMetadata, PutSecretValueResponse::builder);
            HttpResponseHandler<AwsServiceException> errorResponseHandler = this.createErrorResponseHandler((BaseAwsJsonProtocolFactory)this.protocolFactory, operationMetadata);
            CompletableFuture executeFuture = this.clientHandler.execute(new ClientExecutionParams().withOperationName("PutSecretValue").withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withMarshaller((Marshaller)new PutSecretValueRequestMarshaller((BaseAwsJsonProtocolFactory)this.protocolFactory)).withResponseHandler(responseHandler).withErrorResponseHandler(errorResponseHandler).withRequestConfiguration(clientConfiguration).withMetricCollector((MetricCollector)apiCallMetricCollector).withInput((SdkRequest)putSecretValueRequest));
            CompletionStage whenCompleted = executeFuture.whenComplete((arg_0, arg_1) -> DefaultSecretsManagerAsyncClient.lambda$putSecretValue$34(metricPublishers, (MetricCollector)apiCallMetricCollector, arg_0, arg_1));
            executeFuture = CompletableFutureUtils.forwardExceptionTo((CompletableFuture)whenCompleted, (CompletableFuture)executeFuture);
            return executeFuture;
        }
        catch (Throwable t) {
            metricPublishers.forEach(arg_0 -> DefaultSecretsManagerAsyncClient.lambda$putSecretValue$35((MetricCollector)apiCallMetricCollector, arg_0));
            return CompletableFutureUtils.failedFuture((Throwable)t);
        }
    }

    @Override
    public CompletableFuture<RemoveRegionsFromReplicationResponse> removeRegionsFromReplication(RemoveRegionsFromReplicationRequest removeRegionsFromReplicationRequest) {
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)removeRegionsFromReplicationRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultSecretsManagerAsyncClient.resolveMetricPublishers(clientConfiguration, removeRegionsFromReplicationRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"Secrets Manager");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"RemoveRegionsFromReplication");
            JsonOperationMetadata operationMetadata = JsonOperationMetadata.builder().hasStreamingSuccessResponse(false).isPayloadJson(true).build();
            HttpResponseHandler responseHandler = this.protocolFactory.createResponseHandler(operationMetadata, RemoveRegionsFromReplicationResponse::builder);
            HttpResponseHandler<AwsServiceException> errorResponseHandler = this.createErrorResponseHandler((BaseAwsJsonProtocolFactory)this.protocolFactory, operationMetadata);
            CompletableFuture executeFuture = this.clientHandler.execute(new ClientExecutionParams().withOperationName("RemoveRegionsFromReplication").withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withMarshaller((Marshaller)new RemoveRegionsFromReplicationRequestMarshaller((BaseAwsJsonProtocolFactory)this.protocolFactory)).withResponseHandler(responseHandler).withErrorResponseHandler(errorResponseHandler).withRequestConfiguration(clientConfiguration).withMetricCollector((MetricCollector)apiCallMetricCollector).withInput((SdkRequest)removeRegionsFromReplicationRequest));
            CompletionStage whenCompleted = executeFuture.whenComplete((arg_0, arg_1) -> DefaultSecretsManagerAsyncClient.lambda$removeRegionsFromReplication$37(metricPublishers, (MetricCollector)apiCallMetricCollector, arg_0, arg_1));
            executeFuture = CompletableFutureUtils.forwardExceptionTo((CompletableFuture)whenCompleted, (CompletableFuture)executeFuture);
            return executeFuture;
        }
        catch (Throwable t) {
            metricPublishers.forEach(arg_0 -> DefaultSecretsManagerAsyncClient.lambda$removeRegionsFromReplication$38((MetricCollector)apiCallMetricCollector, arg_0));
            return CompletableFutureUtils.failedFuture((Throwable)t);
        }
    }

    @Override
    public CompletableFuture<ReplicateSecretToRegionsResponse> replicateSecretToRegions(ReplicateSecretToRegionsRequest replicateSecretToRegionsRequest) {
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)replicateSecretToRegionsRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultSecretsManagerAsyncClient.resolveMetricPublishers(clientConfiguration, replicateSecretToRegionsRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"Secrets Manager");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"ReplicateSecretToRegions");
            JsonOperationMetadata operationMetadata = JsonOperationMetadata.builder().hasStreamingSuccessResponse(false).isPayloadJson(true).build();
            HttpResponseHandler responseHandler = this.protocolFactory.createResponseHandler(operationMetadata, ReplicateSecretToRegionsResponse::builder);
            HttpResponseHandler<AwsServiceException> errorResponseHandler = this.createErrorResponseHandler((BaseAwsJsonProtocolFactory)this.protocolFactory, operationMetadata);
            CompletableFuture executeFuture = this.clientHandler.execute(new ClientExecutionParams().withOperationName("ReplicateSecretToRegions").withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withMarshaller((Marshaller)new ReplicateSecretToRegionsRequestMarshaller((BaseAwsJsonProtocolFactory)this.protocolFactory)).withResponseHandler(responseHandler).withErrorResponseHandler(errorResponseHandler).withRequestConfiguration(clientConfiguration).withMetricCollector((MetricCollector)apiCallMetricCollector).withInput((SdkRequest)replicateSecretToRegionsRequest));
            CompletionStage whenCompleted = executeFuture.whenComplete((arg_0, arg_1) -> DefaultSecretsManagerAsyncClient.lambda$replicateSecretToRegions$40(metricPublishers, (MetricCollector)apiCallMetricCollector, arg_0, arg_1));
            executeFuture = CompletableFutureUtils.forwardExceptionTo((CompletableFuture)whenCompleted, (CompletableFuture)executeFuture);
            return executeFuture;
        }
        catch (Throwable t) {
            metricPublishers.forEach(arg_0 -> DefaultSecretsManagerAsyncClient.lambda$replicateSecretToRegions$41((MetricCollector)apiCallMetricCollector, arg_0));
            return CompletableFutureUtils.failedFuture((Throwable)t);
        }
    }

    @Override
    public CompletableFuture<RestoreSecretResponse> restoreSecret(RestoreSecretRequest restoreSecretRequest) {
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)restoreSecretRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultSecretsManagerAsyncClient.resolveMetricPublishers(clientConfiguration, restoreSecretRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"Secrets Manager");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"RestoreSecret");
            JsonOperationMetadata operationMetadata = JsonOperationMetadata.builder().hasStreamingSuccessResponse(false).isPayloadJson(true).build();
            HttpResponseHandler responseHandler = this.protocolFactory.createResponseHandler(operationMetadata, RestoreSecretResponse::builder);
            HttpResponseHandler<AwsServiceException> errorResponseHandler = this.createErrorResponseHandler((BaseAwsJsonProtocolFactory)this.protocolFactory, operationMetadata);
            CompletableFuture executeFuture = this.clientHandler.execute(new ClientExecutionParams().withOperationName("RestoreSecret").withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withMarshaller((Marshaller)new RestoreSecretRequestMarshaller((BaseAwsJsonProtocolFactory)this.protocolFactory)).withResponseHandler(responseHandler).withErrorResponseHandler(errorResponseHandler).withRequestConfiguration(clientConfiguration).withMetricCollector((MetricCollector)apiCallMetricCollector).withInput((SdkRequest)restoreSecretRequest));
            CompletionStage whenCompleted = executeFuture.whenComplete((arg_0, arg_1) -> DefaultSecretsManagerAsyncClient.lambda$restoreSecret$43(metricPublishers, (MetricCollector)apiCallMetricCollector, arg_0, arg_1));
            executeFuture = CompletableFutureUtils.forwardExceptionTo((CompletableFuture)whenCompleted, (CompletableFuture)executeFuture);
            return executeFuture;
        }
        catch (Throwable t) {
            metricPublishers.forEach(arg_0 -> DefaultSecretsManagerAsyncClient.lambda$restoreSecret$44((MetricCollector)apiCallMetricCollector, arg_0));
            return CompletableFutureUtils.failedFuture((Throwable)t);
        }
    }

    @Override
    public CompletableFuture<RotateSecretResponse> rotateSecret(RotateSecretRequest rotateSecretRequest) {
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)rotateSecretRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultSecretsManagerAsyncClient.resolveMetricPublishers(clientConfiguration, rotateSecretRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"Secrets Manager");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"RotateSecret");
            JsonOperationMetadata operationMetadata = JsonOperationMetadata.builder().hasStreamingSuccessResponse(false).isPayloadJson(true).build();
            HttpResponseHandler responseHandler = this.protocolFactory.createResponseHandler(operationMetadata, RotateSecretResponse::builder);
            HttpResponseHandler<AwsServiceException> errorResponseHandler = this.createErrorResponseHandler((BaseAwsJsonProtocolFactory)this.protocolFactory, operationMetadata);
            CompletableFuture executeFuture = this.clientHandler.execute(new ClientExecutionParams().withOperationName("RotateSecret").withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withMarshaller((Marshaller)new RotateSecretRequestMarshaller((BaseAwsJsonProtocolFactory)this.protocolFactory)).withResponseHandler(responseHandler).withErrorResponseHandler(errorResponseHandler).withRequestConfiguration(clientConfiguration).withMetricCollector((MetricCollector)apiCallMetricCollector).withInput((SdkRequest)rotateSecretRequest));
            CompletionStage whenCompleted = executeFuture.whenComplete((arg_0, arg_1) -> DefaultSecretsManagerAsyncClient.lambda$rotateSecret$46(metricPublishers, (MetricCollector)apiCallMetricCollector, arg_0, arg_1));
            executeFuture = CompletableFutureUtils.forwardExceptionTo((CompletableFuture)whenCompleted, (CompletableFuture)executeFuture);
            return executeFuture;
        }
        catch (Throwable t) {
            metricPublishers.forEach(arg_0 -> DefaultSecretsManagerAsyncClient.lambda$rotateSecret$47((MetricCollector)apiCallMetricCollector, arg_0));
            return CompletableFutureUtils.failedFuture((Throwable)t);
        }
    }

    @Override
    public CompletableFuture<StopReplicationToReplicaResponse> stopReplicationToReplica(StopReplicationToReplicaRequest stopReplicationToReplicaRequest) {
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)stopReplicationToReplicaRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultSecretsManagerAsyncClient.resolveMetricPublishers(clientConfiguration, stopReplicationToReplicaRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"Secrets Manager");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"StopReplicationToReplica");
            JsonOperationMetadata operationMetadata = JsonOperationMetadata.builder().hasStreamingSuccessResponse(false).isPayloadJson(true).build();
            HttpResponseHandler responseHandler = this.protocolFactory.createResponseHandler(operationMetadata, StopReplicationToReplicaResponse::builder);
            HttpResponseHandler<AwsServiceException> errorResponseHandler = this.createErrorResponseHandler((BaseAwsJsonProtocolFactory)this.protocolFactory, operationMetadata);
            CompletableFuture executeFuture = this.clientHandler.execute(new ClientExecutionParams().withOperationName("StopReplicationToReplica").withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withMarshaller((Marshaller)new StopReplicationToReplicaRequestMarshaller((BaseAwsJsonProtocolFactory)this.protocolFactory)).withResponseHandler(responseHandler).withErrorResponseHandler(errorResponseHandler).withRequestConfiguration(clientConfiguration).withMetricCollector((MetricCollector)apiCallMetricCollector).withInput((SdkRequest)stopReplicationToReplicaRequest));
            CompletionStage whenCompleted = executeFuture.whenComplete((arg_0, arg_1) -> DefaultSecretsManagerAsyncClient.lambda$stopReplicationToReplica$49(metricPublishers, (MetricCollector)apiCallMetricCollector, arg_0, arg_1));
            executeFuture = CompletableFutureUtils.forwardExceptionTo((CompletableFuture)whenCompleted, (CompletableFuture)executeFuture);
            return executeFuture;
        }
        catch (Throwable t) {
            metricPublishers.forEach(arg_0 -> DefaultSecretsManagerAsyncClient.lambda$stopReplicationToReplica$50((MetricCollector)apiCallMetricCollector, arg_0));
            return CompletableFutureUtils.failedFuture((Throwable)t);
        }
    }

    @Override
    public CompletableFuture<TagResourceResponse> tagResource(TagResourceRequest tagResourceRequest) {
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)tagResourceRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultSecretsManagerAsyncClient.resolveMetricPublishers(clientConfiguration, tagResourceRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"Secrets Manager");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"TagResource");
            JsonOperationMetadata operationMetadata = JsonOperationMetadata.builder().hasStreamingSuccessResponse(false).isPayloadJson(true).build();
            HttpResponseHandler responseHandler = this.protocolFactory.createResponseHandler(operationMetadata, TagResourceResponse::builder);
            HttpResponseHandler<AwsServiceException> errorResponseHandler = this.createErrorResponseHandler((BaseAwsJsonProtocolFactory)this.protocolFactory, operationMetadata);
            CompletableFuture executeFuture = this.clientHandler.execute(new ClientExecutionParams().withOperationName("TagResource").withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withMarshaller((Marshaller)new TagResourceRequestMarshaller((BaseAwsJsonProtocolFactory)this.protocolFactory)).withResponseHandler(responseHandler).withErrorResponseHandler(errorResponseHandler).withRequestConfiguration(clientConfiguration).withMetricCollector((MetricCollector)apiCallMetricCollector).withInput((SdkRequest)tagResourceRequest));
            CompletionStage whenCompleted = executeFuture.whenComplete((arg_0, arg_1) -> DefaultSecretsManagerAsyncClient.lambda$tagResource$52(metricPublishers, (MetricCollector)apiCallMetricCollector, arg_0, arg_1));
            executeFuture = CompletableFutureUtils.forwardExceptionTo((CompletableFuture)whenCompleted, (CompletableFuture)executeFuture);
            return executeFuture;
        }
        catch (Throwable t) {
            metricPublishers.forEach(arg_0 -> DefaultSecretsManagerAsyncClient.lambda$tagResource$53((MetricCollector)apiCallMetricCollector, arg_0));
            return CompletableFutureUtils.failedFuture((Throwable)t);
        }
    }

    @Override
    public CompletableFuture<UntagResourceResponse> untagResource(UntagResourceRequest untagResourceRequest) {
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)untagResourceRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultSecretsManagerAsyncClient.resolveMetricPublishers(clientConfiguration, untagResourceRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"Secrets Manager");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"UntagResource");
            JsonOperationMetadata operationMetadata = JsonOperationMetadata.builder().hasStreamingSuccessResponse(false).isPayloadJson(true).build();
            HttpResponseHandler responseHandler = this.protocolFactory.createResponseHandler(operationMetadata, UntagResourceResponse::builder);
            HttpResponseHandler<AwsServiceException> errorResponseHandler = this.createErrorResponseHandler((BaseAwsJsonProtocolFactory)this.protocolFactory, operationMetadata);
            CompletableFuture executeFuture = this.clientHandler.execute(new ClientExecutionParams().withOperationName("UntagResource").withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withMarshaller((Marshaller)new UntagResourceRequestMarshaller((BaseAwsJsonProtocolFactory)this.protocolFactory)).withResponseHandler(responseHandler).withErrorResponseHandler(errorResponseHandler).withRequestConfiguration(clientConfiguration).withMetricCollector((MetricCollector)apiCallMetricCollector).withInput((SdkRequest)untagResourceRequest));
            CompletionStage whenCompleted = executeFuture.whenComplete((arg_0, arg_1) -> DefaultSecretsManagerAsyncClient.lambda$untagResource$55(metricPublishers, (MetricCollector)apiCallMetricCollector, arg_0, arg_1));
            executeFuture = CompletableFutureUtils.forwardExceptionTo((CompletableFuture)whenCompleted, (CompletableFuture)executeFuture);
            return executeFuture;
        }
        catch (Throwable t) {
            metricPublishers.forEach(arg_0 -> DefaultSecretsManagerAsyncClient.lambda$untagResource$56((MetricCollector)apiCallMetricCollector, arg_0));
            return CompletableFutureUtils.failedFuture((Throwable)t);
        }
    }

    @Override
    public CompletableFuture<UpdateSecretResponse> updateSecret(UpdateSecretRequest updateSecretRequest) {
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)updateSecretRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultSecretsManagerAsyncClient.resolveMetricPublishers(clientConfiguration, updateSecretRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"Secrets Manager");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"UpdateSecret");
            JsonOperationMetadata operationMetadata = JsonOperationMetadata.builder().hasStreamingSuccessResponse(false).isPayloadJson(true).build();
            HttpResponseHandler responseHandler = this.protocolFactory.createResponseHandler(operationMetadata, UpdateSecretResponse::builder);
            HttpResponseHandler<AwsServiceException> errorResponseHandler = this.createErrorResponseHandler((BaseAwsJsonProtocolFactory)this.protocolFactory, operationMetadata);
            CompletableFuture executeFuture = this.clientHandler.execute(new ClientExecutionParams().withOperationName("UpdateSecret").withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withMarshaller((Marshaller)new UpdateSecretRequestMarshaller((BaseAwsJsonProtocolFactory)this.protocolFactory)).withResponseHandler(responseHandler).withErrorResponseHandler(errorResponseHandler).withRequestConfiguration(clientConfiguration).withMetricCollector((MetricCollector)apiCallMetricCollector).withInput((SdkRequest)updateSecretRequest));
            CompletionStage whenCompleted = executeFuture.whenComplete((arg_0, arg_1) -> DefaultSecretsManagerAsyncClient.lambda$updateSecret$58(metricPublishers, (MetricCollector)apiCallMetricCollector, arg_0, arg_1));
            executeFuture = CompletableFutureUtils.forwardExceptionTo((CompletableFuture)whenCompleted, (CompletableFuture)executeFuture);
            return executeFuture;
        }
        catch (Throwable t) {
            metricPublishers.forEach(arg_0 -> DefaultSecretsManagerAsyncClient.lambda$updateSecret$59((MetricCollector)apiCallMetricCollector, arg_0));
            return CompletableFutureUtils.failedFuture((Throwable)t);
        }
    }

    @Override
    public CompletableFuture<UpdateSecretVersionStageResponse> updateSecretVersionStage(UpdateSecretVersionStageRequest updateSecretVersionStageRequest) {
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)updateSecretVersionStageRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultSecretsManagerAsyncClient.resolveMetricPublishers(clientConfiguration, updateSecretVersionStageRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"Secrets Manager");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"UpdateSecretVersionStage");
            JsonOperationMetadata operationMetadata = JsonOperationMetadata.builder().hasStreamingSuccessResponse(false).isPayloadJson(true).build();
            HttpResponseHandler responseHandler = this.protocolFactory.createResponseHandler(operationMetadata, UpdateSecretVersionStageResponse::builder);
            HttpResponseHandler<AwsServiceException> errorResponseHandler = this.createErrorResponseHandler((BaseAwsJsonProtocolFactory)this.protocolFactory, operationMetadata);
            CompletableFuture executeFuture = this.clientHandler.execute(new ClientExecutionParams().withOperationName("UpdateSecretVersionStage").withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withMarshaller((Marshaller)new UpdateSecretVersionStageRequestMarshaller((BaseAwsJsonProtocolFactory)this.protocolFactory)).withResponseHandler(responseHandler).withErrorResponseHandler(errorResponseHandler).withRequestConfiguration(clientConfiguration).withMetricCollector((MetricCollector)apiCallMetricCollector).withInput((SdkRequest)updateSecretVersionStageRequest));
            CompletionStage whenCompleted = executeFuture.whenComplete((arg_0, arg_1) -> DefaultSecretsManagerAsyncClient.lambda$updateSecretVersionStage$61(metricPublishers, (MetricCollector)apiCallMetricCollector, arg_0, arg_1));
            executeFuture = CompletableFutureUtils.forwardExceptionTo((CompletableFuture)whenCompleted, (CompletableFuture)executeFuture);
            return executeFuture;
        }
        catch (Throwable t) {
            metricPublishers.forEach(arg_0 -> DefaultSecretsManagerAsyncClient.lambda$updateSecretVersionStage$62((MetricCollector)apiCallMetricCollector, arg_0));
            return CompletableFutureUtils.failedFuture((Throwable)t);
        }
    }

    @Override
    public CompletableFuture<ValidateResourcePolicyResponse> validateResourcePolicy(ValidateResourcePolicyRequest validateResourcePolicyRequest) {
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)validateResourcePolicyRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultSecretsManagerAsyncClient.resolveMetricPublishers(clientConfiguration, validateResourcePolicyRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"Secrets Manager");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"ValidateResourcePolicy");
            JsonOperationMetadata operationMetadata = JsonOperationMetadata.builder().hasStreamingSuccessResponse(false).isPayloadJson(true).build();
            HttpResponseHandler responseHandler = this.protocolFactory.createResponseHandler(operationMetadata, ValidateResourcePolicyResponse::builder);
            HttpResponseHandler<AwsServiceException> errorResponseHandler = this.createErrorResponseHandler((BaseAwsJsonProtocolFactory)this.protocolFactory, operationMetadata);
            CompletableFuture executeFuture = this.clientHandler.execute(new ClientExecutionParams().withOperationName("ValidateResourcePolicy").withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withMarshaller((Marshaller)new ValidateResourcePolicyRequestMarshaller((BaseAwsJsonProtocolFactory)this.protocolFactory)).withResponseHandler(responseHandler).withErrorResponseHandler(errorResponseHandler).withRequestConfiguration(clientConfiguration).withMetricCollector((MetricCollector)apiCallMetricCollector).withInput((SdkRequest)validateResourcePolicyRequest));
            CompletionStage whenCompleted = executeFuture.whenComplete((arg_0, arg_1) -> DefaultSecretsManagerAsyncClient.lambda$validateResourcePolicy$64(metricPublishers, (MetricCollector)apiCallMetricCollector, arg_0, arg_1));
            executeFuture = CompletableFutureUtils.forwardExceptionTo((CompletableFuture)whenCompleted, (CompletableFuture)executeFuture);
            return executeFuture;
        }
        catch (Throwable t) {
            metricPublishers.forEach(arg_0 -> DefaultSecretsManagerAsyncClient.lambda$validateResourcePolicy$65((MetricCollector)apiCallMetricCollector, arg_0));
            return CompletableFutureUtils.failedFuture((Throwable)t);
        }
    }

    @Override
    public final SecretsManagerServiceClientConfiguration serviceClientConfiguration() {
        return this.serviceClientConfiguration;
    }

    public final String serviceName() {
        return "secretsmanager";
    }

    private <T extends BaseAwsJsonProtocolFactory.Builder<T>> T init(T builder) {
        return (T)builder.clientConfiguration(this.clientConfiguration).defaultServiceExceptionSupplier(SecretsManagerException::builder).protocol(AwsJsonProtocol.AWS_JSON).protocolVersion("1.1").registerModeledException(ExceptionMetadata.builder().errorCode("EncryptionFailure").exceptionBuilderSupplier(EncryptionFailureException::builder).httpStatusCode(Integer.valueOf(400)).build()).registerModeledException(ExceptionMetadata.builder().errorCode("InvalidParameterException").exceptionBuilderSupplier(InvalidParameterException::builder).httpStatusCode(Integer.valueOf(400)).build()).registerModeledException(ExceptionMetadata.builder().errorCode("PublicPolicyException").exceptionBuilderSupplier(PublicPolicyException::builder).httpStatusCode(Integer.valueOf(400)).build()).registerModeledException(ExceptionMetadata.builder().errorCode("MalformedPolicyDocumentException").exceptionBuilderSupplier(MalformedPolicyDocumentException::builder).httpStatusCode(Integer.valueOf(400)).build()).registerModeledException(ExceptionMetadata.builder().errorCode("DecryptionFailure").exceptionBuilderSupplier(DecryptionFailureException::builder).httpStatusCode(Integer.valueOf(400)).build()).registerModeledException(ExceptionMetadata.builder().errorCode("InvalidRequestException").exceptionBuilderSupplier(InvalidRequestException::builder).httpStatusCode(Integer.valueOf(400)).build()).registerModeledException(ExceptionMetadata.builder().errorCode("ResourceNotFoundException").exceptionBuilderSupplier(ResourceNotFoundException::builder).httpStatusCode(Integer.valueOf(400)).build()).registerModeledException(ExceptionMetadata.builder().errorCode("InternalServiceError").exceptionBuilderSupplier(InternalServiceErrorException::builder).httpStatusCode(Integer.valueOf(500)).build()).registerModeledException(ExceptionMetadata.builder().errorCode("ResourceExistsException").exceptionBuilderSupplier(ResourceExistsException::builder).httpStatusCode(Integer.valueOf(400)).build()).registerModeledException(ExceptionMetadata.builder().errorCode("InvalidNextTokenException").exceptionBuilderSupplier(InvalidNextTokenException::builder).httpStatusCode(Integer.valueOf(400)).build()).registerModeledException(ExceptionMetadata.builder().errorCode("LimitExceededException").exceptionBuilderSupplier(LimitExceededException::builder).httpStatusCode(Integer.valueOf(400)).build()).registerModeledException(ExceptionMetadata.builder().errorCode("PreconditionNotMetException").exceptionBuilderSupplier(PreconditionNotMetException::builder).httpStatusCode(Integer.valueOf(400)).build());
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

    private HttpResponseHandler<AwsServiceException> createErrorResponseHandler(BaseAwsJsonProtocolFactory protocolFactory, JsonOperationMetadata operationMetadata) {
        return protocolFactory.createErrorResponseHandler(operationMetadata);
    }

    public void close() {
        this.clientHandler.close();
    }

    private static /* synthetic */ void lambda$validateResourcePolicy$65(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$validateResourcePolicy$64(List metricPublishers, MetricCollector apiCallMetricCollector, ValidateResourcePolicyResponse r, Throwable e) {
        metricPublishers.forEach(p -> p.publish(apiCallMetricCollector.collect()));
    }

    private static /* synthetic */ void lambda$updateSecretVersionStage$62(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$updateSecretVersionStage$61(List metricPublishers, MetricCollector apiCallMetricCollector, UpdateSecretVersionStageResponse r, Throwable e) {
        metricPublishers.forEach(p -> p.publish(apiCallMetricCollector.collect()));
    }

    private static /* synthetic */ void lambda$updateSecret$59(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$updateSecret$58(List metricPublishers, MetricCollector apiCallMetricCollector, UpdateSecretResponse r, Throwable e) {
        metricPublishers.forEach(p -> p.publish(apiCallMetricCollector.collect()));
    }

    private static /* synthetic */ void lambda$untagResource$56(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$untagResource$55(List metricPublishers, MetricCollector apiCallMetricCollector, UntagResourceResponse r, Throwable e) {
        metricPublishers.forEach(p -> p.publish(apiCallMetricCollector.collect()));
    }

    private static /* synthetic */ void lambda$tagResource$53(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$tagResource$52(List metricPublishers, MetricCollector apiCallMetricCollector, TagResourceResponse r, Throwable e) {
        metricPublishers.forEach(p -> p.publish(apiCallMetricCollector.collect()));
    }

    private static /* synthetic */ void lambda$stopReplicationToReplica$50(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$stopReplicationToReplica$49(List metricPublishers, MetricCollector apiCallMetricCollector, StopReplicationToReplicaResponse r, Throwable e) {
        metricPublishers.forEach(p -> p.publish(apiCallMetricCollector.collect()));
    }

    private static /* synthetic */ void lambda$rotateSecret$47(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$rotateSecret$46(List metricPublishers, MetricCollector apiCallMetricCollector, RotateSecretResponse r, Throwable e) {
        metricPublishers.forEach(p -> p.publish(apiCallMetricCollector.collect()));
    }

    private static /* synthetic */ void lambda$restoreSecret$44(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$restoreSecret$43(List metricPublishers, MetricCollector apiCallMetricCollector, RestoreSecretResponse r, Throwable e) {
        metricPublishers.forEach(p -> p.publish(apiCallMetricCollector.collect()));
    }

    private static /* synthetic */ void lambda$replicateSecretToRegions$41(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$replicateSecretToRegions$40(List metricPublishers, MetricCollector apiCallMetricCollector, ReplicateSecretToRegionsResponse r, Throwable e) {
        metricPublishers.forEach(p -> p.publish(apiCallMetricCollector.collect()));
    }

    private static /* synthetic */ void lambda$removeRegionsFromReplication$38(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$removeRegionsFromReplication$37(List metricPublishers, MetricCollector apiCallMetricCollector, RemoveRegionsFromReplicationResponse r, Throwable e) {
        metricPublishers.forEach(p -> p.publish(apiCallMetricCollector.collect()));
    }

    private static /* synthetic */ void lambda$putSecretValue$35(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$putSecretValue$34(List metricPublishers, MetricCollector apiCallMetricCollector, PutSecretValueResponse r, Throwable e) {
        metricPublishers.forEach(p -> p.publish(apiCallMetricCollector.collect()));
    }

    private static /* synthetic */ void lambda$putResourcePolicy$32(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$putResourcePolicy$31(List metricPublishers, MetricCollector apiCallMetricCollector, PutResourcePolicyResponse r, Throwable e) {
        metricPublishers.forEach(p -> p.publish(apiCallMetricCollector.collect()));
    }

    private static /* synthetic */ void lambda$listSecrets$29(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$listSecrets$28(List metricPublishers, MetricCollector apiCallMetricCollector, ListSecretsResponse r, Throwable e) {
        metricPublishers.forEach(p -> p.publish(apiCallMetricCollector.collect()));
    }

    private static /* synthetic */ void lambda$listSecretVersionIds$26(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$listSecretVersionIds$25(List metricPublishers, MetricCollector apiCallMetricCollector, ListSecretVersionIdsResponse r, Throwable e) {
        metricPublishers.forEach(p -> p.publish(apiCallMetricCollector.collect()));
    }

    private static /* synthetic */ void lambda$getSecretValue$23(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$getSecretValue$22(List metricPublishers, MetricCollector apiCallMetricCollector, GetSecretValueResponse r, Throwable e) {
        metricPublishers.forEach(p -> p.publish(apiCallMetricCollector.collect()));
    }

    private static /* synthetic */ void lambda$getResourcePolicy$20(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$getResourcePolicy$19(List metricPublishers, MetricCollector apiCallMetricCollector, GetResourcePolicyResponse r, Throwable e) {
        metricPublishers.forEach(p -> p.publish(apiCallMetricCollector.collect()));
    }

    private static /* synthetic */ void lambda$getRandomPassword$17(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$getRandomPassword$16(List metricPublishers, MetricCollector apiCallMetricCollector, GetRandomPasswordResponse r, Throwable e) {
        metricPublishers.forEach(p -> p.publish(apiCallMetricCollector.collect()));
    }

    private static /* synthetic */ void lambda$describeSecret$14(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$describeSecret$13(List metricPublishers, MetricCollector apiCallMetricCollector, DescribeSecretResponse r, Throwable e) {
        metricPublishers.forEach(p -> p.publish(apiCallMetricCollector.collect()));
    }

    private static /* synthetic */ void lambda$deleteSecret$11(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$deleteSecret$10(List metricPublishers, MetricCollector apiCallMetricCollector, DeleteSecretResponse r, Throwable e) {
        metricPublishers.forEach(p -> p.publish(apiCallMetricCollector.collect()));
    }

    private static /* synthetic */ void lambda$deleteResourcePolicy$8(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$deleteResourcePolicy$7(List metricPublishers, MetricCollector apiCallMetricCollector, DeleteResourcePolicyResponse r, Throwable e) {
        metricPublishers.forEach(p -> p.publish(apiCallMetricCollector.collect()));
    }

    private static /* synthetic */ void lambda$createSecret$5(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$createSecret$4(List metricPublishers, MetricCollector apiCallMetricCollector, CreateSecretResponse r, Throwable e) {
        metricPublishers.forEach(p -> p.publish(apiCallMetricCollector.collect()));
    }

    private static /* synthetic */ void lambda$cancelRotateSecret$2(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$cancelRotateSecret$1(List metricPublishers, MetricCollector apiCallMetricCollector, CancelRotateSecretResponse r, Throwable e) {
        metricPublishers.forEach(p -> p.publish(apiCallMetricCollector.collect()));
    }
}

