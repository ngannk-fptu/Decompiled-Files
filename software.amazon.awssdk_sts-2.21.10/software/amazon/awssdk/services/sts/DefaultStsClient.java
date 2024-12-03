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
 *  software.amazon.awssdk.protocols.query.AwsQueryProtocolFactory
 *  software.amazon.awssdk.utils.Logger
 */
package software.amazon.awssdk.services.sts;

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
import software.amazon.awssdk.protocols.query.AwsQueryProtocolFactory;
import software.amazon.awssdk.services.sts.StsClient;
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
import software.amazon.awssdk.utils.Logger;

@SdkInternalApi
final class DefaultStsClient
implements StsClient {
    private static final Logger log = Logger.loggerFor(DefaultStsClient.class);
    private static final AwsProtocolMetadata protocolMetadata = AwsProtocolMetadata.builder().serviceProtocol(AwsServiceProtocol.QUERY).build();
    private final SyncClientHandler clientHandler;
    private final AwsQueryProtocolFactory protocolFactory;
    private final SdkClientConfiguration clientConfiguration;
    private final StsServiceClientConfiguration serviceClientConfiguration;

    protected DefaultStsClient(StsServiceClientConfiguration serviceClientConfiguration, SdkClientConfiguration clientConfiguration) {
        this.clientHandler = new AwsSyncClientHandler(clientConfiguration);
        this.clientConfiguration = clientConfiguration;
        this.serviceClientConfiguration = serviceClientConfiguration;
        this.protocolFactory = this.init();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public AssumeRoleResponse assumeRole(AssumeRoleRequest assumeRoleRequest) throws MalformedPolicyDocumentException, PackedPolicyTooLargeException, RegionDisabledException, ExpiredTokenException, AwsServiceException, SdkClientException, StsException {
        HttpResponseHandler responseHandler = this.protocolFactory.createResponseHandler(AssumeRoleResponse::builder);
        HttpResponseHandler errorResponseHandler = this.protocolFactory.createErrorResponseHandler();
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)assumeRoleRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultStsClient.resolveMetricPublishers(clientConfiguration, assumeRoleRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"STS");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"AssumeRole");
            AssumeRoleResponse assumeRoleResponse = (AssumeRoleResponse)this.clientHandler.execute(new ClientExecutionParams().withOperationName("AssumeRole").withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withResponseHandler(responseHandler).withErrorResponseHandler(errorResponseHandler).withRequestConfiguration(clientConfiguration).withInput((SdkRequest)assumeRoleRequest).withMetricCollector((MetricCollector)apiCallMetricCollector).withMarshaller((Marshaller)new AssumeRoleRequestMarshaller(this.protocolFactory)));
            return assumeRoleResponse;
        }
        finally {
            metricPublishers.forEach(arg_0 -> DefaultStsClient.lambda$assumeRole$0((MetricCollector)apiCallMetricCollector, arg_0));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public AssumeRoleWithSamlResponse assumeRoleWithSAML(AssumeRoleWithSamlRequest assumeRoleWithSamlRequest) throws MalformedPolicyDocumentException, PackedPolicyTooLargeException, IdpRejectedClaimException, InvalidIdentityTokenException, ExpiredTokenException, RegionDisabledException, AwsServiceException, SdkClientException, StsException {
        HttpResponseHandler responseHandler = this.protocolFactory.createResponseHandler(AssumeRoleWithSamlResponse::builder);
        HttpResponseHandler errorResponseHandler = this.protocolFactory.createErrorResponseHandler();
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)assumeRoleWithSamlRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultStsClient.resolveMetricPublishers(clientConfiguration, assumeRoleWithSamlRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"STS");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"AssumeRoleWithSAML");
            AssumeRoleWithSamlResponse assumeRoleWithSamlResponse = (AssumeRoleWithSamlResponse)this.clientHandler.execute(new ClientExecutionParams().withOperationName("AssumeRoleWithSAML").withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withResponseHandler(responseHandler).withErrorResponseHandler(errorResponseHandler).withRequestConfiguration(clientConfiguration).withInput((SdkRequest)assumeRoleWithSamlRequest).withMetricCollector((MetricCollector)apiCallMetricCollector).withMarshaller((Marshaller)new AssumeRoleWithSamlRequestMarshaller(this.protocolFactory)));
            return assumeRoleWithSamlResponse;
        }
        finally {
            metricPublishers.forEach(arg_0 -> DefaultStsClient.lambda$assumeRoleWithSAML$1((MetricCollector)apiCallMetricCollector, arg_0));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public AssumeRoleWithWebIdentityResponse assumeRoleWithWebIdentity(AssumeRoleWithWebIdentityRequest assumeRoleWithWebIdentityRequest) throws MalformedPolicyDocumentException, PackedPolicyTooLargeException, IdpRejectedClaimException, IdpCommunicationErrorException, InvalidIdentityTokenException, ExpiredTokenException, RegionDisabledException, AwsServiceException, SdkClientException, StsException {
        HttpResponseHandler responseHandler = this.protocolFactory.createResponseHandler(AssumeRoleWithWebIdentityResponse::builder);
        HttpResponseHandler errorResponseHandler = this.protocolFactory.createErrorResponseHandler();
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)assumeRoleWithWebIdentityRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultStsClient.resolveMetricPublishers(clientConfiguration, assumeRoleWithWebIdentityRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"STS");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"AssumeRoleWithWebIdentity");
            AssumeRoleWithWebIdentityResponse assumeRoleWithWebIdentityResponse = (AssumeRoleWithWebIdentityResponse)this.clientHandler.execute(new ClientExecutionParams().withOperationName("AssumeRoleWithWebIdentity").withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withResponseHandler(responseHandler).withErrorResponseHandler(errorResponseHandler).withRequestConfiguration(clientConfiguration).withInput((SdkRequest)assumeRoleWithWebIdentityRequest).withMetricCollector((MetricCollector)apiCallMetricCollector).withMarshaller((Marshaller)new AssumeRoleWithWebIdentityRequestMarshaller(this.protocolFactory)));
            return assumeRoleWithWebIdentityResponse;
        }
        finally {
            metricPublishers.forEach(arg_0 -> DefaultStsClient.lambda$assumeRoleWithWebIdentity$2((MetricCollector)apiCallMetricCollector, arg_0));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public DecodeAuthorizationMessageResponse decodeAuthorizationMessage(DecodeAuthorizationMessageRequest decodeAuthorizationMessageRequest) throws InvalidAuthorizationMessageException, AwsServiceException, SdkClientException, StsException {
        HttpResponseHandler responseHandler = this.protocolFactory.createResponseHandler(DecodeAuthorizationMessageResponse::builder);
        HttpResponseHandler errorResponseHandler = this.protocolFactory.createErrorResponseHandler();
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)decodeAuthorizationMessageRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultStsClient.resolveMetricPublishers(clientConfiguration, decodeAuthorizationMessageRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"STS");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"DecodeAuthorizationMessage");
            DecodeAuthorizationMessageResponse decodeAuthorizationMessageResponse = (DecodeAuthorizationMessageResponse)this.clientHandler.execute(new ClientExecutionParams().withOperationName("DecodeAuthorizationMessage").withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withResponseHandler(responseHandler).withErrorResponseHandler(errorResponseHandler).withRequestConfiguration(clientConfiguration).withInput((SdkRequest)decodeAuthorizationMessageRequest).withMetricCollector((MetricCollector)apiCallMetricCollector).withMarshaller((Marshaller)new DecodeAuthorizationMessageRequestMarshaller(this.protocolFactory)));
            return decodeAuthorizationMessageResponse;
        }
        finally {
            metricPublishers.forEach(arg_0 -> DefaultStsClient.lambda$decodeAuthorizationMessage$3((MetricCollector)apiCallMetricCollector, arg_0));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public GetAccessKeyInfoResponse getAccessKeyInfo(GetAccessKeyInfoRequest getAccessKeyInfoRequest) throws AwsServiceException, SdkClientException, StsException {
        HttpResponseHandler responseHandler = this.protocolFactory.createResponseHandler(GetAccessKeyInfoResponse::builder);
        HttpResponseHandler errorResponseHandler = this.protocolFactory.createErrorResponseHandler();
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)getAccessKeyInfoRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultStsClient.resolveMetricPublishers(clientConfiguration, getAccessKeyInfoRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"STS");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"GetAccessKeyInfo");
            GetAccessKeyInfoResponse getAccessKeyInfoResponse = (GetAccessKeyInfoResponse)this.clientHandler.execute(new ClientExecutionParams().withOperationName("GetAccessKeyInfo").withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withResponseHandler(responseHandler).withErrorResponseHandler(errorResponseHandler).withRequestConfiguration(clientConfiguration).withInput((SdkRequest)getAccessKeyInfoRequest).withMetricCollector((MetricCollector)apiCallMetricCollector).withMarshaller((Marshaller)new GetAccessKeyInfoRequestMarshaller(this.protocolFactory)));
            return getAccessKeyInfoResponse;
        }
        finally {
            metricPublishers.forEach(arg_0 -> DefaultStsClient.lambda$getAccessKeyInfo$4((MetricCollector)apiCallMetricCollector, arg_0));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public GetCallerIdentityResponse getCallerIdentity(GetCallerIdentityRequest getCallerIdentityRequest) throws AwsServiceException, SdkClientException, StsException {
        HttpResponseHandler responseHandler = this.protocolFactory.createResponseHandler(GetCallerIdentityResponse::builder);
        HttpResponseHandler errorResponseHandler = this.protocolFactory.createErrorResponseHandler();
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)getCallerIdentityRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultStsClient.resolveMetricPublishers(clientConfiguration, getCallerIdentityRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"STS");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"GetCallerIdentity");
            GetCallerIdentityResponse getCallerIdentityResponse = (GetCallerIdentityResponse)this.clientHandler.execute(new ClientExecutionParams().withOperationName("GetCallerIdentity").withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withResponseHandler(responseHandler).withErrorResponseHandler(errorResponseHandler).withRequestConfiguration(clientConfiguration).withInput((SdkRequest)getCallerIdentityRequest).withMetricCollector((MetricCollector)apiCallMetricCollector).withMarshaller((Marshaller)new GetCallerIdentityRequestMarshaller(this.protocolFactory)));
            return getCallerIdentityResponse;
        }
        finally {
            metricPublishers.forEach(arg_0 -> DefaultStsClient.lambda$getCallerIdentity$5((MetricCollector)apiCallMetricCollector, arg_0));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public GetFederationTokenResponse getFederationToken(GetFederationTokenRequest getFederationTokenRequest) throws MalformedPolicyDocumentException, PackedPolicyTooLargeException, RegionDisabledException, AwsServiceException, SdkClientException, StsException {
        HttpResponseHandler responseHandler = this.protocolFactory.createResponseHandler(GetFederationTokenResponse::builder);
        HttpResponseHandler errorResponseHandler = this.protocolFactory.createErrorResponseHandler();
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)getFederationTokenRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultStsClient.resolveMetricPublishers(clientConfiguration, getFederationTokenRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"STS");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"GetFederationToken");
            GetFederationTokenResponse getFederationTokenResponse = (GetFederationTokenResponse)this.clientHandler.execute(new ClientExecutionParams().withOperationName("GetFederationToken").withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withResponseHandler(responseHandler).withErrorResponseHandler(errorResponseHandler).withRequestConfiguration(clientConfiguration).withInput((SdkRequest)getFederationTokenRequest).withMetricCollector((MetricCollector)apiCallMetricCollector).withMarshaller((Marshaller)new GetFederationTokenRequestMarshaller(this.protocolFactory)));
            return getFederationTokenResponse;
        }
        finally {
            metricPublishers.forEach(arg_0 -> DefaultStsClient.lambda$getFederationToken$6((MetricCollector)apiCallMetricCollector, arg_0));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public GetSessionTokenResponse getSessionToken(GetSessionTokenRequest getSessionTokenRequest) throws RegionDisabledException, AwsServiceException, SdkClientException, StsException {
        HttpResponseHandler responseHandler = this.protocolFactory.createResponseHandler(GetSessionTokenResponse::builder);
        HttpResponseHandler errorResponseHandler = this.protocolFactory.createErrorResponseHandler();
        SdkClientConfiguration clientConfiguration = this.updateSdkClientConfiguration((SdkRequest)getSessionTokenRequest, this.clientConfiguration);
        List<MetricPublisher> metricPublishers = DefaultStsClient.resolveMetricPublishers(clientConfiguration, getSessionTokenRequest.overrideConfiguration().orElse(null));
        NoOpMetricCollector apiCallMetricCollector = metricPublishers.isEmpty() ? NoOpMetricCollector.create() : MetricCollector.create((String)"ApiCall");
        try {
            apiCallMetricCollector.reportMetric(CoreMetric.SERVICE_ID, (Object)"STS");
            apiCallMetricCollector.reportMetric(CoreMetric.OPERATION_NAME, (Object)"GetSessionToken");
            GetSessionTokenResponse getSessionTokenResponse = (GetSessionTokenResponse)this.clientHandler.execute(new ClientExecutionParams().withOperationName("GetSessionToken").withProtocolMetadata((SdkProtocolMetadata)protocolMetadata).withResponseHandler(responseHandler).withErrorResponseHandler(errorResponseHandler).withRequestConfiguration(clientConfiguration).withInput((SdkRequest)getSessionTokenRequest).withMetricCollector((MetricCollector)apiCallMetricCollector).withMarshaller((Marshaller)new GetSessionTokenRequestMarshaller(this.protocolFactory)));
            return getSessionTokenResponse;
        }
        finally {
            metricPublishers.forEach(arg_0 -> DefaultStsClient.lambda$getSessionToken$7((MetricCollector)apiCallMetricCollector, arg_0));
        }
    }

    public final String serviceName() {
        return "sts";
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

    private AwsQueryProtocolFactory init() {
        return AwsQueryProtocolFactory.builder().registerModeledException(ExceptionMetadata.builder().errorCode("InvalidAuthorizationMessageException").exceptionBuilderSupplier(InvalidAuthorizationMessageException::builder).httpStatusCode(Integer.valueOf(400)).build()).registerModeledException(ExceptionMetadata.builder().errorCode("ExpiredTokenException").exceptionBuilderSupplier(ExpiredTokenException::builder).httpStatusCode(Integer.valueOf(400)).build()).registerModeledException(ExceptionMetadata.builder().errorCode("PackedPolicyTooLarge").exceptionBuilderSupplier(PackedPolicyTooLargeException::builder).httpStatusCode(Integer.valueOf(400)).build()).registerModeledException(ExceptionMetadata.builder().errorCode("RegionDisabledException").exceptionBuilderSupplier(RegionDisabledException::builder).httpStatusCode(Integer.valueOf(403)).build()).registerModeledException(ExceptionMetadata.builder().errorCode("MalformedPolicyDocument").exceptionBuilderSupplier(MalformedPolicyDocumentException::builder).httpStatusCode(Integer.valueOf(400)).build()).registerModeledException(ExceptionMetadata.builder().errorCode("IDPRejectedClaim").exceptionBuilderSupplier(IdpRejectedClaimException::builder).httpStatusCode(Integer.valueOf(403)).build()).registerModeledException(ExceptionMetadata.builder().errorCode("InvalidIdentityToken").exceptionBuilderSupplier(InvalidIdentityTokenException::builder).httpStatusCode(Integer.valueOf(400)).build()).registerModeledException(ExceptionMetadata.builder().errorCode("IDPCommunicationError").exceptionBuilderSupplier(IdpCommunicationErrorException::builder).httpStatusCode(Integer.valueOf(400)).build()).clientConfiguration(this.clientConfiguration).defaultServiceExceptionSupplier(StsException::builder).build();
    }

    @Override
    public final StsServiceClientConfiguration serviceClientConfiguration() {
        return this.serviceClientConfiguration;
    }

    public void close() {
        this.clientHandler.close();
    }

    private static /* synthetic */ void lambda$getSessionToken$7(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$getFederationToken$6(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$getCallerIdentity$5(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$getAccessKeyInfo$4(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$decodeAuthorizationMessage$3(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$assumeRoleWithWebIdentity$2(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$assumeRoleWithSAML$1(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }

    private static /* synthetic */ void lambda$assumeRole$0(MetricCollector apiCallMetricCollector, MetricPublisher p) {
        p.publish(apiCallMetricCollector.collect());
    }
}

