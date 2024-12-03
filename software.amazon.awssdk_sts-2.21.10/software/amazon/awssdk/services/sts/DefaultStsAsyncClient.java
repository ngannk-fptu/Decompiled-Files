/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.awscore.client.handler.AwsAsyncClientHandler
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
 *  software.amazon.awssdk.protocols.query.AwsQueryProtocolFactory
 *  software.amazon.awssdk.utils.CompletableFutureUtils
 */
package software.amazon.awssdk.services.sts;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.awscore.client.handler.AwsAsyncClientHandler;
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
import software.amazon.awssdk.protocols.query.AwsQueryProtocolFactory;
import software.amazon.awssdk.services.sts.StsAsyncClient;
import software.amazon.awssdk.services.sts.StsServiceClientConfiguration;
import software.amazon.awssdk.services.sts.internal.StsServiceClientConfigurationBuilder;
import software.amazon.awssdk.services.sts.model.AssumeRoleRequest;
import software.amazon.awssdk.services.sts.model.AssumeRoleResponse;
import software.amazon.awssdk.services.sts.model.AssumeRoleWithSamlRequest;
import software.amazon.awssdk.services.sts.model.AssumeRoleWithSamlResponse;
import software.amazon.awssdk.services.sts.model.AssumeRoleWithWebIdentityRequest;
import software.amazon.awssdk.services.sts.model.AssumeRoleWithWebIdentityResponse;
import software.amazon.awssdk.services.sts.model.DecodeAuthorizationMessageRequest;
import software.amazon.awssdk.services.sts.model.DecodeAuthorizationMessageResponse;
import software.amazon.awssdk.services.sts.model.ExpiredTokenException;
import software.amazon.awssdk.services.sts.model.GetAccessKeyInfoRequest;
import software.amazon.awssdk.services.sts.model.GetAccessKeyInfoResponse;
import software.amazon.awssdk.services.sts.model.GetCallerIdentityRequest;
import software.amazon.awssdk.services.sts.model.GetCallerIdentityResponse;
import software.amazon.awssdk.services.sts.model.GetFederationTokenRequest;
import software.amazon.awssdk.services.sts.model.GetFederationTokenResponse;
import software.amazon.awssdk.services.sts.model.GetSessionTokenRequest;
import software.amazon.awssdk.services.sts.model.GetSessionTokenResponse;
import software.amazon.awssdk.services.sts.model.IdpCommunicationErrorException;
import software.amazon.awssdk.services.sts.model.IdpRejectedClaimException;
import software.amazon.awssdk.services.sts.model.InvalidAuthorizationMessageException;
import software.amazon.awssdk.services.sts.model.InvalidIdentityTokenException;
import software.amazon.awssdk.services.sts.model.MalformedPolicyDocumentException;
import software.amazon.awssdk.services.sts.model.PackedPolicyTooLargeException;
import software.amazon.awssdk.services.sts.model.RegionDisabledException;
import software.amazon.awssdk.services.sts.model.StsException;
import software.amazon.awssdk.services.sts.transform.AssumeRoleRequestMarshaller;
import software.amazon.awssdk.services.sts.transform.AssumeRoleWithSamlRequestMarshaller;
import software.amazon.awssdk.services.sts.transform.AssumeRoleWithWebIdentityRequestMarshaller;
import software.amazon.awssdk.services.sts.transform.DecodeAuthorizationMessageRequestMarshaller;
import software.amazon.awssdk.services.sts.transform.GetAccessKeyInfoRequestMarshaller;
import software.amazon.awssdk.services.sts.transform.GetCallerIdentityRequestMarshaller;
import software.amazon.awssdk.services.sts.transform.GetFederationTokenRequestMarshaller;
import software.amazon.awssdk.services.sts.transform.GetSessionTokenRequestMarshaller;
import software.amazon.awssdk.utils.CompletableFutureUtils;

@SdkInternalApi
final class DefaultStsAsyncClient
implements StsAsyncClient {
    private static final Logger log = LoggerFactory.getLogger(DefaultStsAsyncClient.class);
    private static final AwsProtocolMetadata protocolMetadata = AwsProtocolMetadata.builder().serviceProtocol(AwsServiceProtocol.QUERY).build();
    private final AsyncClientHandler clientHandler;
    private final AwsQueryProtocolFactory protocolFactory;
    private final SdkClientConfiguration clientConfiguration;
    private final StsServiceClientConfiguration serviceClientConfiguration;

    protected DefaultStsAsyncClient(StsServiceClientConfiguration serviceClientConfiguration, SdkClientConfiguration clientConfiguration) {
        this.clientHandler = new AwsAsyncClientHandler(clientConfiguration);
        this.clientConfiguration = clientConfiguration;
        this.serviceClientConfiguration = serviceClientConfiguration;
        this.protocolFactory = this.init();
    }

    @Override
    public CompletableFuture<AssumeRoleResponse> assumeRole(AssumeRoleRequest assumeRoleRequest) {
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)assumeRoleRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultStsAsyncClient.resolveMetricPublishers(clientConfiguration, assumeRoleRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"STS");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"AssumeRole");
            HttpResponseHandler responseHandler = this.protocolFactory.createResponseHandler(AssumeRoleResponse::builder);
            HttpResponseHandler errorResponseHandler = this.protocolFactory.createErrorResponseHandler();
            CompletableFuture executeFuture = this.clientHandler.execute(new ClientExecutionParams().withOperationName("AssumeRole").withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withMarshaller((Marshaller)new AssumeRoleRequestMarshaller(this.protocolFactory)).withResponseHandler(responseHandler).withErrorResponseHandler(errorResponseHandler).withRequestConfiguration(clientConfiguration).withMetricCollector((MetricCollector)apiCallMetricCollector).withInput((SdkRequest)assumeRoleRequest));
            CompletionStage whenCompleteFuture = null;
            whenCompleteFuture = executeFuture.whenComplete((arg_0, arg_1) -> DefaultStsAsyncClient.lambda$assumeRole$1(metricPublishers, (MetricCollector)apiCallMetricCollector, arg_0, arg_1));
            return CompletableFutureUtils.forwardExceptionTo((CompletableFuture)whenCompleteFuture, (CompletableFuture)executeFuture);
        }
        catch (Throwable t) {
            metricPublishers.forEach(arg_0 -> DefaultStsAsyncClient.lambda$assumeRole$2((MetricCollector)apiCallMetricCollector, arg_0));
            return CompletableFutureUtils.failedFuture((Throwable)t);
        }
    }

    @Override
    public CompletableFuture<AssumeRoleWithSamlResponse> assumeRoleWithSAML(AssumeRoleWithSamlRequest assumeRoleWithSamlRequest) {
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)assumeRoleWithSamlRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultStsAsyncClient.resolveMetricPublishers(clientConfiguration, assumeRoleWithSamlRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"STS");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"AssumeRoleWithSAML");
            HttpResponseHandler responseHandler = this.protocolFactory.createResponseHandler(AssumeRoleWithSamlResponse::builder);
            HttpResponseHandler errorResponseHandler = this.protocolFactory.createErrorResponseHandler();
            CompletableFuture executeFuture = this.clientHandler.execute(new ClientExecutionParams().withOperationName("AssumeRoleWithSAML").withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withMarshaller((Marshaller)new AssumeRoleWithSamlRequestMarshaller(this.protocolFactory)).withResponseHandler(responseHandler).withErrorResponseHandler(errorResponseHandler).withRequestConfiguration(clientConfiguration).withMetricCollector((MetricCollector)apiCallMetricCollector).withInput((SdkRequest)assumeRoleWithSamlRequest));
            CompletionStage whenCompleteFuture = null;
            whenCompleteFuture = executeFuture.whenComplete((arg_0, arg_1) -> DefaultStsAsyncClient.lambda$assumeRoleWithSAML$4(metricPublishers, (MetricCollector)apiCallMetricCollector, arg_0, arg_1));
            return CompletableFutureUtils.forwardExceptionTo((CompletableFuture)whenCompleteFuture, (CompletableFuture)executeFuture);
        }
        catch (Throwable t) {
            metricPublishers.forEach(arg_0 -> DefaultStsAsyncClient.lambda$assumeRoleWithSAML$5((MetricCollector)apiCallMetricCollector, arg_0));
            return CompletableFutureUtils.failedFuture((Throwable)t);
        }
    }

    @Override
    public CompletableFuture<AssumeRoleWithWebIdentityResponse> assumeRoleWithWebIdentity(AssumeRoleWithWebIdentityRequest assumeRoleWithWebIdentityRequest) {
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)assumeRoleWithWebIdentityRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultStsAsyncClient.resolveMetricPublishers(clientConfiguration, assumeRoleWithWebIdentityRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"STS");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"AssumeRoleWithWebIdentity");
            HttpResponseHandler responseHandler = this.protocolFactory.createResponseHandler(AssumeRoleWithWebIdentityResponse::builder);
            HttpResponseHandler errorResponseHandler = this.protocolFactory.createErrorResponseHandler();
            CompletableFuture executeFuture = this.clientHandler.execute(new ClientExecutionParams().withOperationName("AssumeRoleWithWebIdentity").withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withMarshaller((Marshaller)new AssumeRoleWithWebIdentityRequestMarshaller(this.protocolFactory)).withResponseHandler(responseHandler).withErrorResponseHandler(errorResponseHandler).withRequestConfiguration(clientConfiguration).withMetricCollector((MetricCollector)apiCallMetricCollector).withInput((SdkRequest)assumeRoleWithWebIdentityRequest));
            CompletionStage whenCompleteFuture = null;
            whenCompleteFuture = executeFuture.whenComplete((arg_0, arg_1) -> DefaultStsAsyncClient.lambda$assumeRoleWithWebIdentity$7(metricPublishers, (MetricCollector)apiCallMetricCollector, arg_0, arg_1));
            return CompletableFutureUtils.forwardExceptionTo((CompletableFuture)whenCompleteFuture, (CompletableFuture)executeFuture);
        }
        catch (Throwable t) {
            metricPublishers.forEach(arg_0 -> DefaultStsAsyncClient.lambda$assumeRoleWithWebIdentity$8((MetricCollector)apiCallMetricCollector, arg_0));
            return CompletableFutureUtils.failedFuture((Throwable)t);
        }
    }

    @Override
    public CompletableFuture<DecodeAuthorizationMessageResponse> decodeAuthorizationMessage(DecodeAuthorizationMessageRequest decodeAuthorizationMessageRequest) {
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)decodeAuthorizationMessageRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultStsAsyncClient.resolveMetricPublishers(clientConfiguration, decodeAuthorizationMessageRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"STS");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"DecodeAuthorizationMessage");
            HttpResponseHandler responseHandler = this.protocolFactory.createResponseHandler(DecodeAuthorizationMessageResponse::builder);
            HttpResponseHandler errorResponseHandler = this.protocolFactory.createErrorResponseHandler();
            CompletableFuture executeFuture = this.clientHandler.execute(new ClientExecutionParams().withOperationName("DecodeAuthorizationMessage").withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withMarshaller((Marshaller)new DecodeAuthorizationMessageRequestMarshaller(this.protocolFactory)).withResponseHandler(responseHandler).withErrorResponseHandler(errorResponseHandler).withRequestConfiguration(clientConfiguration).withMetricCollector((MetricCollector)apiCallMetricCollector).withInput((SdkRequest)decodeAuthorizationMessageRequest));
            CompletionStage whenCompleteFuture = null;
            whenCompleteFuture = executeFuture.whenComplete((arg_0, arg_1) -> DefaultStsAsyncClient.lambda$decodeAuthorizationMessage$10(metricPublishers, (MetricCollector)apiCallMetricCollector, arg_0, arg_1));
            return CompletableFutureUtils.forwardExceptionTo((CompletableFuture)whenCompleteFuture, (CompletableFuture)executeFuture);
        }
        catch (Throwable t) {
            metricPublishers.forEach(arg_0 -> DefaultStsAsyncClient.lambda$decodeAuthorizationMessage$11((MetricCollector)apiCallMetricCollector, arg_0));
            return CompletableFutureUtils.failedFuture((Throwable)t);
        }
    }

    @Override
    public CompletableFuture<GetAccessKeyInfoResponse> getAccessKeyInfo(GetAccessKeyInfoRequest getAccessKeyInfoRequest) {
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)getAccessKeyInfoRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultStsAsyncClient.resolveMetricPublishers(clientConfiguration, getAccessKeyInfoRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"STS");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"GetAccessKeyInfo");
            HttpResponseHandler responseHandler = this.protocolFactory.createResponseHandler(GetAccessKeyInfoResponse::builder);
            HttpResponseHandler errorResponseHandler = this.protocolFactory.createErrorResponseHandler();
            CompletableFuture executeFuture = this.clientHandler.execute(new ClientExecutionParams().withOperationName("GetAccessKeyInfo").withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withMarshaller((Marshaller)new GetAccessKeyInfoRequestMarshaller(this.protocolFactory)).withResponseHandler(responseHandler).withErrorResponseHandler(errorResponseHandler).withRequestConfiguration(clientConfiguration).withMetricCollector((MetricCollector)apiCallMetricCollector).withInput((SdkRequest)getAccessKeyInfoRequest));
            CompletionStage whenCompleteFuture = null;
            whenCompleteFuture = executeFuture.whenComplete((arg_0, arg_1) -> DefaultStsAsyncClient.lambda$getAccessKeyInfo$13(metricPublishers, (MetricCollector)apiCallMetricCollector, arg_0, arg_1));
            return CompletableFutureUtils.forwardExceptionTo((CompletableFuture)whenCompleteFuture, (CompletableFuture)executeFuture);
        }
        catch (Throwable t) {
            metricPublishers.forEach(arg_0 -> DefaultStsAsyncClient.lambda$getAccessKeyInfo$14((MetricCollector)apiCallMetricCollector, arg_0));
            return CompletableFutureUtils.failedFuture((Throwable)t);
        }
    }

    @Override
    public CompletableFuture<GetCallerIdentityResponse> getCallerIdentity(GetCallerIdentityRequest getCallerIdentityRequest) {
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)getCallerIdentityRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultStsAsyncClient.resolveMetricPublishers(clientConfiguration, getCallerIdentityRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"STS");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"GetCallerIdentity");
            HttpResponseHandler responseHandler = this.protocolFactory.createResponseHandler(GetCallerIdentityResponse::builder);
            HttpResponseHandler errorResponseHandler = this.protocolFactory.createErrorResponseHandler();
            CompletableFuture executeFuture = this.clientHandler.execute(new ClientExecutionParams().withOperationName("GetCallerIdentity").withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withMarshaller((Marshaller)new GetCallerIdentityRequestMarshaller(this.protocolFactory)).withResponseHandler(responseHandler).withErrorResponseHandler(errorResponseHandler).withRequestConfiguration(clientConfiguration).withMetricCollector((MetricCollector)apiCallMetricCollector).withInput((SdkRequest)getCallerIdentityRequest));
            CompletionStage whenCompleteFuture = null;
            whenCompleteFuture = executeFuture.whenComplete((arg_0, arg_1) -> DefaultStsAsyncClient.lambda$getCallerIdentity$16(metricPublishers, (MetricCollector)apiCallMetricCollector, arg_0, arg_1));
            return CompletableFutureUtils.forwardExceptionTo((CompletableFuture)whenCompleteFuture, (CompletableFuture)executeFuture);
        }
        catch (Throwable t) {
            metricPublishers.forEach(arg_0 -> DefaultStsAsyncClient.lambda$getCallerIdentity$17((MetricCollector)apiCallMetricCollector, arg_0));
            return CompletableFutureUtils.failedFuture((Throwable)t);
        }
    }

    @Override
    public CompletableFuture<GetFederationTokenResponse> getFederationToken(GetFederationTokenRequest getFederationTokenRequest) {
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)getFederationTokenRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultStsAsyncClient.resolveMetricPublishers(clientConfiguration, getFederationTokenRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"STS");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"GetFederationToken");
            HttpResponseHandler responseHandler = this.protocolFactory.createResponseHandler(GetFederationTokenResponse::builder);
            HttpResponseHandler errorResponseHandler = this.protocolFactory.createErrorResponseHandler();
            CompletableFuture executeFuture = this.clientHandler.execute(new ClientExecutionParams().withOperationName("GetFederationToken").withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withMarshaller((Marshaller)new GetFederationTokenRequestMarshaller(this.protocolFactory)).withResponseHandler(responseHandler).withErrorResponseHandler(errorResponseHandler).withRequestConfiguration(clientConfiguration).withMetricCollector((MetricCollector)apiCallMetricCollector).withInput((SdkRequest)getFederationTokenRequest));
            CompletionStage whenCompleteFuture = null;
            whenCompleteFuture = executeFuture.whenComplete((arg_0, arg_1) -> DefaultStsAsyncClient.lambda$getFederationToken$19(metricPublishers, (MetricCollector)apiCallMetricCollector, arg_0, arg_1));
            return CompletableFutureUtils.forwardExceptionTo((CompletableFuture)whenCompleteFuture, (CompletableFuture)executeFuture);
        }
        catch (Throwable t) {
            metricPublishers.forEach(arg_0 -> DefaultStsAsyncClient.lambda$getFederationToken$20((MetricCollector)apiCallMetricCollector, arg_0));
            return CompletableFutureUtils.failedFuture((Throwable)t);
        }
    }

    @Override
    public CompletableFuture<GetSessionTokenResponse> getSessionToken(GetSessionTokenRequest getSessionTokenRequest) {
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)getSessionTokenRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultStsAsyncClient.resolveMetricPublishers(clientConfiguration, getSessionTokenRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"STS");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"GetSessionToken");
            HttpResponseHandler responseHandler = this.protocolFactory.createResponseHandler(GetSessionTokenResponse::builder);
            HttpResponseHandler errorResponseHandler = this.protocolFactory.createErrorResponseHandler();
            CompletableFuture executeFuture = this.clientHandler.execute(new ClientExecutionParams().withOperationName("GetSessionToken").withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withMarshaller((Marshaller)new GetSessionTokenRequestMarshaller(this.protocolFactory)).withResponseHandler(responseHandler).withErrorResponseHandler(errorResponseHandler).withRequestConfiguration(clientConfiguration).withMetricCollector((MetricCollector)apiCallMetricCollector).withInput((SdkRequest)getSessionTokenRequest));
            CompletionStage whenCompleteFuture = null;
            whenCompleteFuture = executeFuture.whenComplete((arg_0, arg_1) -> DefaultStsAsyncClient.lambda$getSessionToken$22(metricPublishers, (MetricCollector)apiCallMetricCollector, arg_0, arg_1));
            return CompletableFutureUtils.forwardExceptionTo((CompletableFuture)whenCompleteFuture, (CompletableFuture)executeFuture);
        }
        catch (Throwable t) {
            metricPublishers.forEach(arg_0 -> DefaultStsAsyncClient.lambda$getSessionToken$23((MetricCollector)apiCallMetricCollector, arg_0));
            return CompletableFutureUtils.failedFuture((Throwable)t);
        }
    }

    @Override
    public final StsServiceClientConfiguration serviceClientConfiguration() {
        return this.serviceClientConfiguration;
    }

    public final String serviceName() {
        return "sts";
    }

    private AwsQueryProtocolFactory init() {
        return AwsQueryProtocolFactory.builder().registerModeledException(ExceptionMetadata.builder().errorCode("InvalidAuthorizationMessageException").exceptionBuilderSupplier(InvalidAuthorizationMessageException::builder).httpStatusCode(Integer.valueOf(400)).build()).registerModeledException(ExceptionMetadata.builder().errorCode("ExpiredTokenException").exceptionBuilderSupplier(ExpiredTokenException::builder).httpStatusCode(Integer.valueOf(400)).build()).registerModeledException(ExceptionMetadata.builder().errorCode("PackedPolicyTooLarge").exceptionBuilderSupplier(PackedPolicyTooLargeException::builder).httpStatusCode(Integer.valueOf(400)).build()).registerModeledException(ExceptionMetadata.builder().errorCode("RegionDisabledException").exceptionBuilderSupplier(RegionDisabledException::builder).httpStatusCode(Integer.valueOf(403)).build()).registerModeledException(ExceptionMetadata.builder().errorCode("MalformedPolicyDocument").exceptionBuilderSupplier(MalformedPolicyDocumentException::builder).httpStatusCode(Integer.valueOf(400)).build()).registerModeledException(ExceptionMetadata.builder().errorCode("IDPRejectedClaim").exceptionBuilderSupplier(IdpRejectedClaimException::builder).httpStatusCode(Integer.valueOf(403)).build()).registerModeledException(ExceptionMetadata.builder().errorCode("InvalidIdentityToken").exceptionBuilderSupplier(InvalidIdentityTokenException::builder).httpStatusCode(Integer.valueOf(400)).build()).registerModeledException(ExceptionMetadata.builder().errorCode("IDPCommunicationError").exceptionBuilderSupplier(IdpCommunicationErrorException::builder).httpStatusCode(Integer.valueOf(400)).build()).clientConfiguration(this.clientConfiguration).defaultServiceExceptionSupplier(StsException::builder).build();
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
        StsServiceClientConfigurationBuilder.BuilderInternal serviceConfigBuilder = StsServiceClientConfigurationBuilder.builder(clientConfiguration.toBuilder());
        serviceConfigBuilder.overrideConfiguration(this.serviceClientConfiguration.overrideConfiguration());
        for (SdkPlugin plugin : plugins) {
            plugin.configureClient((SdkServiceClientConfiguration.Builder)serviceConfigBuilder);
        }
        return serviceConfigBuilder.buildSdkClientConfiguration();
    }

    public void close() {
        this.clientHandler.close();
    }

    private static /* synthetic */ void lambda$getSessionToken$23(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$getSessionToken$22(List metricPublishers, MetricCollector apiCallMetricCollector, GetSessionTokenResponse r, Throwable e) {
        metricPublishers.forEach(p -> p.publish(apiCallMetricCollector.collect()));
    }

    private static /* synthetic */ void lambda$getFederationToken$20(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$getFederationToken$19(List metricPublishers, MetricCollector apiCallMetricCollector, GetFederationTokenResponse r, Throwable e) {
        metricPublishers.forEach(p -> p.publish(apiCallMetricCollector.collect()));
    }

    private static /* synthetic */ void lambda$getCallerIdentity$17(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$getCallerIdentity$16(List metricPublishers, MetricCollector apiCallMetricCollector, GetCallerIdentityResponse r, Throwable e) {
        metricPublishers.forEach(p -> p.publish(apiCallMetricCollector.collect()));
    }

    private static /* synthetic */ void lambda$getAccessKeyInfo$14(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$getAccessKeyInfo$13(List metricPublishers, MetricCollector apiCallMetricCollector, GetAccessKeyInfoResponse r, Throwable e) {
        metricPublishers.forEach(p -> p.publish(apiCallMetricCollector.collect()));
    }

    private static /* synthetic */ void lambda$decodeAuthorizationMessage$11(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$decodeAuthorizationMessage$10(List metricPublishers, MetricCollector apiCallMetricCollector, DecodeAuthorizationMessageResponse r, Throwable e) {
        metricPublishers.forEach(p -> p.publish(apiCallMetricCollector.collect()));
    }

    private static /* synthetic */ void lambda$assumeRoleWithWebIdentity$8(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$assumeRoleWithWebIdentity$7(List metricPublishers, MetricCollector apiCallMetricCollector, AssumeRoleWithWebIdentityResponse r, Throwable e) {
        metricPublishers.forEach(p -> p.publish(apiCallMetricCollector.collect()));
    }

    private static /* synthetic */ void lambda$assumeRoleWithSAML$5(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$assumeRoleWithSAML$4(List metricPublishers, MetricCollector apiCallMetricCollector, AssumeRoleWithSamlResponse r, Throwable e) {
        metricPublishers.forEach(p -> p.publish(apiCallMetricCollector.collect()));
    }

    private static /* synthetic */ void lambda$assumeRole$2(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$assumeRole$1(List metricPublishers, MetricCollector apiCallMetricCollector, AssumeRoleResponse r, Throwable e) {
        metricPublishers.forEach(p -> p.publish(apiCallMetricCollector.collect()));
    }
}

