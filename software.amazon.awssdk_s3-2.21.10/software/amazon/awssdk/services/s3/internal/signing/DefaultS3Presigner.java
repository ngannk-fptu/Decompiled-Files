/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.auth.signer.AwsS3V4Signer
 *  software.amazon.awssdk.auth.signer.AwsSignerExecutionAttribute
 *  software.amazon.awssdk.awscore.AwsExecutionAttribute
 *  software.amazon.awssdk.awscore.defaultsmode.DefaultsMode
 *  software.amazon.awssdk.awscore.endpoint.DefaultServiceEndpointBuilder
 *  software.amazon.awssdk.awscore.internal.AwsExecutionContextBuilder
 *  software.amazon.awssdk.awscore.internal.authcontext.AwsCredentialsAuthorizationStrategy
 *  software.amazon.awssdk.awscore.internal.defaultsmode.DefaultsModeConfiguration
 *  software.amazon.awssdk.awscore.presigner.PresignRequest
 *  software.amazon.awssdk.awscore.presigner.PresignedRequest$Builder
 *  software.amazon.awssdk.core.ClientType
 *  software.amazon.awssdk.core.SdkBytes
 *  software.amazon.awssdk.core.SdkRequest
 *  software.amazon.awssdk.core.client.config.ClientOption
 *  software.amazon.awssdk.core.client.config.SdkClientConfiguration
 *  software.amazon.awssdk.core.client.config.SdkClientOption
 *  software.amazon.awssdk.core.http.ExecutionContext
 *  software.amazon.awssdk.core.interceptor.ClasspathInterceptorChainFactory
 *  software.amazon.awssdk.core.interceptor.Context$AfterMarshalling
 *  software.amazon.awssdk.core.interceptor.Context$BeforeMarshalling
 *  software.amazon.awssdk.core.interceptor.ExecutionAttributes
 *  software.amazon.awssdk.core.interceptor.ExecutionInterceptor
 *  software.amazon.awssdk.core.interceptor.ExecutionInterceptorChain
 *  software.amazon.awssdk.core.interceptor.InterceptorContext
 *  software.amazon.awssdk.core.interceptor.SdkExecutionAttribute
 *  software.amazon.awssdk.core.interceptor.SdkInternalExecutionAttribute
 *  software.amazon.awssdk.core.signer.Presigner
 *  software.amazon.awssdk.core.signer.Signer
 *  software.amazon.awssdk.core.sync.RequestBody
 *  software.amazon.awssdk.http.ContentStreamProvider
 *  software.amazon.awssdk.http.SdkHttpFullRequest
 *  software.amazon.awssdk.http.SdkHttpMethod
 *  software.amazon.awssdk.http.SdkHttpRequest
 *  software.amazon.awssdk.http.SdkHttpRequest$Builder
 *  software.amazon.awssdk.http.auth.aws.scheme.AwsV4AuthScheme
 *  software.amazon.awssdk.http.auth.aws.scheme.AwsV4aAuthScheme
 *  software.amazon.awssdk.http.auth.spi.scheme.AuthScheme
 *  software.amazon.awssdk.identity.spi.IdentityProviders
 *  software.amazon.awssdk.metrics.MetricCollector
 *  software.amazon.awssdk.metrics.NoOpMetricCollector
 *  software.amazon.awssdk.protocols.xml.AwsS3ProtocolFactory
 *  software.amazon.awssdk.protocols.xml.AwsS3ProtocolFactory$Builder
 *  software.amazon.awssdk.protocols.xml.AwsXmlProtocolFactory
 *  software.amazon.awssdk.regions.ServiceMetadataAdvancedOption
 *  software.amazon.awssdk.utils.AttributeMap
 *  software.amazon.awssdk.utils.AttributeMap$Builder
 *  software.amazon.awssdk.utils.AttributeMap$Key
 *  software.amazon.awssdk.utils.CollectionUtils
 *  software.amazon.awssdk.utils.FunctionalUtils
 *  software.amazon.awssdk.utils.IoUtils
 *  software.amazon.awssdk.utils.Logger
 *  software.amazon.awssdk.utils.Validate
 */
package software.amazon.awssdk.services.s3.internal.signing;

import java.io.InputStream;
import java.net.URI;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.auth.signer.AwsS3V4Signer;
import software.amazon.awssdk.auth.signer.AwsSignerExecutionAttribute;
import software.amazon.awssdk.awscore.AwsExecutionAttribute;
import software.amazon.awssdk.awscore.defaultsmode.DefaultsMode;
import software.amazon.awssdk.awscore.endpoint.DefaultServiceEndpointBuilder;
import software.amazon.awssdk.awscore.internal.AwsExecutionContextBuilder;
import software.amazon.awssdk.awscore.internal.authcontext.AwsCredentialsAuthorizationStrategy;
import software.amazon.awssdk.awscore.internal.defaultsmode.DefaultsModeConfiguration;
import software.amazon.awssdk.awscore.presigner.PresignRequest;
import software.amazon.awssdk.awscore.presigner.PresignedRequest;
import software.amazon.awssdk.core.ClientType;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.core.SdkRequest;
import software.amazon.awssdk.core.client.config.ClientOption;
import software.amazon.awssdk.core.client.config.SdkClientConfiguration;
import software.amazon.awssdk.core.client.config.SdkClientOption;
import software.amazon.awssdk.core.http.ExecutionContext;
import software.amazon.awssdk.core.interceptor.ClasspathInterceptorChainFactory;
import software.amazon.awssdk.core.interceptor.Context;
import software.amazon.awssdk.core.interceptor.ExecutionAttributes;
import software.amazon.awssdk.core.interceptor.ExecutionInterceptor;
import software.amazon.awssdk.core.interceptor.ExecutionInterceptorChain;
import software.amazon.awssdk.core.interceptor.InterceptorContext;
import software.amazon.awssdk.core.interceptor.SdkExecutionAttribute;
import software.amazon.awssdk.core.interceptor.SdkInternalExecutionAttribute;
import software.amazon.awssdk.core.signer.Presigner;
import software.amazon.awssdk.core.signer.Signer;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.http.ContentStreamProvider;
import software.amazon.awssdk.http.SdkHttpFullRequest;
import software.amazon.awssdk.http.SdkHttpMethod;
import software.amazon.awssdk.http.SdkHttpRequest;
import software.amazon.awssdk.http.auth.aws.scheme.AwsV4AuthScheme;
import software.amazon.awssdk.http.auth.aws.scheme.AwsV4aAuthScheme;
import software.amazon.awssdk.http.auth.spi.scheme.AuthScheme;
import software.amazon.awssdk.identity.spi.IdentityProviders;
import software.amazon.awssdk.metrics.MetricCollector;
import software.amazon.awssdk.metrics.NoOpMetricCollector;
import software.amazon.awssdk.protocols.xml.AwsS3ProtocolFactory;
import software.amazon.awssdk.protocols.xml.AwsXmlProtocolFactory;
import software.amazon.awssdk.regions.ServiceMetadataAdvancedOption;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.auth.scheme.S3AuthSchemeProvider;
import software.amazon.awssdk.services.s3.auth.scheme.internal.S3AuthSchemeInterceptor;
import software.amazon.awssdk.services.s3.endpoints.S3ClientContextParams;
import software.amazon.awssdk.services.s3.endpoints.S3EndpointProvider;
import software.amazon.awssdk.services.s3.endpoints.internal.S3RequestSetEndpointInterceptor;
import software.amazon.awssdk.services.s3.endpoints.internal.S3ResolveEndpointInterceptor;
import software.amazon.awssdk.services.s3.internal.endpoints.UseGlobalEndpointResolver;
import software.amazon.awssdk.services.s3.internal.signing.DefaultSdkPresigner;
import software.amazon.awssdk.services.s3.model.AbortMultipartUploadRequest;
import software.amazon.awssdk.services.s3.model.CompleteMultipartUploadRequest;
import software.amazon.awssdk.services.s3.model.CreateMultipartUploadRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.UploadPartRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.AbortMultipartUploadPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.CompleteMultipartUploadPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.CreateMultipartUploadPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.DeleteObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedAbortMultipartUploadRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedCompleteMultipartUploadRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedCreateMultipartUploadRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedDeleteObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedUploadPartRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.UploadPartPresignRequest;
import software.amazon.awssdk.services.s3.transform.AbortMultipartUploadRequestMarshaller;
import software.amazon.awssdk.services.s3.transform.CompleteMultipartUploadRequestMarshaller;
import software.amazon.awssdk.services.s3.transform.CreateMultipartUploadRequestMarshaller;
import software.amazon.awssdk.services.s3.transform.DeleteObjectRequestMarshaller;
import software.amazon.awssdk.services.s3.transform.GetObjectRequestMarshaller;
import software.amazon.awssdk.services.s3.transform.PutObjectRequestMarshaller;
import software.amazon.awssdk.services.s3.transform.UploadPartRequestMarshaller;
import software.amazon.awssdk.utils.AttributeMap;
import software.amazon.awssdk.utils.CollectionUtils;
import software.amazon.awssdk.utils.FunctionalUtils;
import software.amazon.awssdk.utils.IoUtils;
import software.amazon.awssdk.utils.Logger;
import software.amazon.awssdk.utils.Validate;

@SdkInternalApi
public final class DefaultS3Presigner
extends DefaultSdkPresigner
implements S3Presigner {
    private static final Logger log = Logger.loggerFor(DefaultS3Presigner.class);
    private static final AwsS3V4Signer DEFAULT_SIGNER = AwsS3V4Signer.create();
    private static final String SERVICE_NAME = "s3";
    private static final String SIGNING_NAME = "s3";
    private final S3Configuration serviceConfiguration;
    private final List<ExecutionInterceptor> clientInterceptors;
    private final GetObjectRequestMarshaller getObjectRequestMarshaller;
    private final PutObjectRequestMarshaller putObjectRequestMarshaller;
    private final CreateMultipartUploadRequestMarshaller createMultipartUploadRequestMarshaller;
    private final UploadPartRequestMarshaller uploadPartRequestMarshaller;
    private final DeleteObjectRequestMarshaller deleteObjectRequestMarshaller;
    private final CompleteMultipartUploadRequestMarshaller completeMultipartUploadRequestMarshaller;
    private final AbortMultipartUploadRequestMarshaller abortMultipartUploadRequestMarshaller;
    private final SdkClientConfiguration clientConfiguration;
    private final AttributeMap clientContextParams;
    private final UseGlobalEndpointResolver useGlobalEndpointResolver;

    private DefaultS3Presigner(Builder b) {
        super(b);
        S3Configuration serviceConfiguration = b.serviceConfiguration != null ? b.serviceConfiguration : (S3Configuration)S3Configuration.builder().profileFile(this.profileFileSupplier()).profileName(this.profileName()).checksumValidationEnabled(false).build();
        S3Configuration.Builder serviceConfigBuilder = serviceConfiguration.toBuilder();
        if (serviceConfiguration.checksumValidationEnabled()) {
            log.debug(() -> "The provided S3Configuration has ChecksumValidationEnabled set to true. Please note that the pre-signed request can't be executed using a web browser if checksum validation is enabled.");
        }
        if (this.dualstackEnabled() != null && serviceConfigBuilder.dualstackEnabled() != null) {
            throw new IllegalStateException("Dualstack has been configured in both S3Configuration and at the presigner/global level. Please limit dualstack configuration to one location.");
        }
        if (this.dualstackEnabled() != null) {
            serviceConfigBuilder.dualstackEnabled(this.dualstackEnabled());
        }
        this.serviceConfiguration = (S3Configuration)serviceConfigBuilder.build();
        this.clientInterceptors = this.initializeInterceptors();
        this.clientConfiguration = this.createClientConfiguration();
        AwsS3ProtocolFactory protocolFactory = ((AwsS3ProtocolFactory.Builder)AwsS3ProtocolFactory.builder().clientConfiguration(this.clientConfiguration)).build();
        this.getObjectRequestMarshaller = new GetObjectRequestMarshaller((AwsXmlProtocolFactory)protocolFactory);
        this.putObjectRequestMarshaller = new PutObjectRequestMarshaller((AwsXmlProtocolFactory)protocolFactory);
        this.createMultipartUploadRequestMarshaller = new CreateMultipartUploadRequestMarshaller((AwsXmlProtocolFactory)protocolFactory);
        this.uploadPartRequestMarshaller = new UploadPartRequestMarshaller((AwsXmlProtocolFactory)protocolFactory);
        this.deleteObjectRequestMarshaller = new DeleteObjectRequestMarshaller((AwsXmlProtocolFactory)protocolFactory);
        this.completeMultipartUploadRequestMarshaller = new CompleteMultipartUploadRequestMarshaller((AwsXmlProtocolFactory)protocolFactory);
        this.abortMultipartUploadRequestMarshaller = new AbortMultipartUploadRequestMarshaller((AwsXmlProtocolFactory)protocolFactory);
        this.clientContextParams = this.createClientContextParams();
        this.useGlobalEndpointResolver = this.createUseGlobalEndpointResolver();
    }

    public static S3Presigner.Builder builder() {
        return new Builder();
    }

    private List<ExecutionInterceptor> initializeInterceptors() {
        ClasspathInterceptorChainFactory interceptorFactory = new ClasspathInterceptorChainFactory();
        List s3Interceptors = interceptorFactory.getInterceptors("software/amazon/awssdk/services/s3/execution.interceptors");
        ArrayList<Object> additionalInterceptors = new ArrayList<Object>();
        additionalInterceptors.add(new S3AuthSchemeInterceptor());
        additionalInterceptors.add(new S3ResolveEndpointInterceptor());
        additionalInterceptors.add(new S3RequestSetEndpointInterceptor());
        s3Interceptors = CollectionUtils.mergeLists((List)s3Interceptors, additionalInterceptors);
        return CollectionUtils.mergeLists((List)interceptorFactory.getGlobalInterceptors(), (List)s3Interceptors);
    }

    private SdkClientConfiguration createClientConfiguration() {
        if (this.endpointOverride() != null) {
            return SdkClientConfiguration.builder().option((ClientOption)SdkClientOption.ENDPOINT, (Object)this.endpointOverride()).option((ClientOption)SdkClientOption.ENDPOINT_OVERRIDDEN, (Object)true).build();
        }
        URI defaultEndpoint = new DefaultServiceEndpointBuilder("s3", "https").withRegion(this.region()).withProfileFile(this.profileFileSupplier()).withProfileName(this.profileName()).withDualstackEnabled(Boolean.valueOf(this.serviceConfiguration.dualstackEnabled())).withFipsEnabled(Boolean.valueOf(this.fipsEnabled())).getServiceEndpoint();
        return SdkClientConfiguration.builder().option((ClientOption)SdkClientOption.ENDPOINT, (Object)defaultEndpoint).build();
    }

    @Override
    public PresignedGetObjectRequest presignGetObject(GetObjectPresignRequest request) {
        return this.presign(PresignedGetObjectRequest.builder(), request, (SdkRequest)request.getObjectRequest(), GetObjectRequest.class, this.getObjectRequestMarshaller::marshall, "GetObject").build();
    }

    @Override
    public PresignedPutObjectRequest presignPutObject(PutObjectPresignRequest request) {
        return this.presign(PresignedPutObjectRequest.builder(), request, (SdkRequest)request.putObjectRequest(), PutObjectRequest.class, this.putObjectRequestMarshaller::marshall, "PutObject").build();
    }

    @Override
    public PresignedDeleteObjectRequest presignDeleteObject(DeleteObjectPresignRequest request) {
        return this.presign(PresignedDeleteObjectRequest.builder(), request, (SdkRequest)request.deleteObjectRequest(), DeleteObjectRequest.class, this.deleteObjectRequestMarshaller::marshall, "DeleteObject").build();
    }

    @Override
    public PresignedCreateMultipartUploadRequest presignCreateMultipartUpload(CreateMultipartUploadPresignRequest request) {
        return this.presign(PresignedCreateMultipartUploadRequest.builder(), request, (SdkRequest)request.createMultipartUploadRequest(), CreateMultipartUploadRequest.class, this.createMultipartUploadRequestMarshaller::marshall, "CreateMultipartUpload").build();
    }

    @Override
    public PresignedUploadPartRequest presignUploadPart(UploadPartPresignRequest request) {
        return this.presign(PresignedUploadPartRequest.builder(), request, (SdkRequest)request.uploadPartRequest(), UploadPartRequest.class, this.uploadPartRequestMarshaller::marshall, "UploadPart").build();
    }

    @Override
    public PresignedCompleteMultipartUploadRequest presignCompleteMultipartUpload(CompleteMultipartUploadPresignRequest request) {
        return this.presign(PresignedCompleteMultipartUploadRequest.builder(), request, (SdkRequest)request.completeMultipartUploadRequest(), CompleteMultipartUploadRequest.class, this.completeMultipartUploadRequestMarshaller::marshall, "CompleteMultipartUpload").build();
    }

    @Override
    public PresignedAbortMultipartUploadRequest presignAbortMultipartUpload(AbortMultipartUploadPresignRequest request) {
        return this.presign(PresignedAbortMultipartUploadRequest.builder(), request, (SdkRequest)request.abortMultipartUploadRequest(), AbortMultipartUploadRequest.class, this.abortMultipartUploadRequestMarshaller::marshall, "AbortMultipartUpload").build();
    }

    protected S3Configuration serviceConfiguration() {
        return this.serviceConfiguration;
    }

    private <T extends PresignedRequest.Builder, U> T presign(T presignedRequest, PresignRequest presignRequest, SdkRequest requestToPresign, Class<U> requestToPresignType, Function<U, SdkHttpFullRequest> requestMarshaller, String operationName) {
        ExecutionContext execCtx = this.invokeInterceptorsAndCreateExecutionContext(presignRequest, requestToPresign, operationName);
        this.callBeforeMarshallingHooks(execCtx);
        this.marshalRequestAndUpdateContext(execCtx, requestToPresignType, requestMarshaller);
        this.callAfterMarshallingHooks(execCtx);
        this.addRequestLevelHeadersAndQueryParameters(execCtx);
        this.callModifyHttpRequestHooksAndUpdateContext(execCtx);
        SdkHttpFullRequest httpRequest = this.getHttpFullRequest(execCtx);
        SdkHttpFullRequest signedHttpRequest = this.presignRequest(execCtx, httpRequest);
        this.initializePresignedRequest(presignedRequest, execCtx, signedHttpRequest);
        return presignedRequest;
    }

    private ExecutionContext invokeInterceptorsAndCreateExecutionContext(PresignRequest presignRequest, SdkRequest sdkRequest, String operationName) {
        ExecutionAttributes executionAttributes = new ExecutionAttributes().putAttribute(AwsSignerExecutionAttribute.SERVICE_SIGNING_NAME, (Object)"s3").putAttribute(AwsExecutionAttribute.AWS_REGION, (Object)this.region()).putAttribute(AwsSignerExecutionAttribute.SIGNING_REGION, (Object)this.region()).putAttribute(SdkInternalExecutionAttribute.IS_FULL_DUPLEX, (Object)false).putAttribute(SdkExecutionAttribute.CLIENT_TYPE, (Object)ClientType.SYNC).putAttribute(SdkExecutionAttribute.SERVICE_NAME, (Object)"s3").putAttribute(SdkExecutionAttribute.OPERATION_NAME, (Object)operationName).putAttribute(SdkExecutionAttribute.SERVICE_CONFIG, (Object)this.serviceConfiguration()).putAttribute(AwsSignerExecutionAttribute.PRESIGNER_EXPIRATION, (Object)Instant.now().plus(presignRequest.signatureDuration())).putAttribute(SdkExecutionAttribute.CLIENT_ENDPOINT, this.clientConfiguration.option((ClientOption)SdkClientOption.ENDPOINT)).putAttribute(SdkExecutionAttribute.ENDPOINT_OVERRIDDEN, this.clientConfiguration.option((ClientOption)SdkClientOption.ENDPOINT_OVERRIDDEN)).putAttribute(AwsExecutionAttribute.FIPS_ENDPOINT_ENABLED, (Object)this.fipsEnabled()).putAttribute(AwsExecutionAttribute.DUALSTACK_ENDPOINT_ENABLED, (Object)this.serviceConfiguration.dualstackEnabled()).putAttribute(SdkInternalExecutionAttribute.ENDPOINT_PROVIDER, (Object)S3EndpointProvider.defaultProvider()).putAttribute(AwsExecutionAttribute.USE_GLOBAL_ENDPOINT, (Object)this.useGlobalEndpointResolver.resolve(this.region())).putAttribute(SdkInternalExecutionAttribute.CLIENT_CONTEXT_PARAMS, (Object)this.clientContextParams).putAttribute(SdkInternalExecutionAttribute.AUTH_SCHEME_RESOLVER, (Object)S3AuthSchemeProvider.defaultProvider()).putAttribute(SdkInternalExecutionAttribute.AUTH_SCHEMES, this.authSchemes()).putAttribute(SdkInternalExecutionAttribute.IDENTITY_PROVIDERS, IdentityProviders.builder().putIdentityProvider(this.credentialsProvider()).build());
        ExecutionInterceptorChain executionInterceptorChain = new ExecutionInterceptorChain(this.clientInterceptors);
        InterceptorContext interceptorContext = InterceptorContext.builder().request(sdkRequest).build();
        interceptorContext = AwsExecutionContextBuilder.runInitialInterceptors((InterceptorContext)interceptorContext, (ExecutionAttributes)executionAttributes, (ExecutionInterceptorChain)executionInterceptorChain);
        AwsCredentialsAuthorizationStrategy authorizationContext = AwsCredentialsAuthorizationStrategy.builder().request(interceptorContext.request()).defaultSigner((Signer)DEFAULT_SIGNER).defaultCredentialsProvider(this.credentialsProvider()).metricCollector((MetricCollector)NoOpMetricCollector.create()).build();
        authorizationContext.addCredentialsToExecutionAttributes(executionAttributes);
        return ExecutionContext.builder().interceptorChain(executionInterceptorChain).interceptorContext(interceptorContext).executionAttributes(executionAttributes).signer(authorizationContext.resolveSigner()).build();
    }

    private Map<String, AuthScheme<?>> authSchemes() {
        HashMap<String, Object> schemes = new HashMap<String, Object>(2);
        AwsV4AuthScheme awsV4AuthScheme = AwsV4AuthScheme.create();
        schemes.put(awsV4AuthScheme.schemeId(), awsV4AuthScheme);
        AwsV4aAuthScheme awsV4aAuthScheme = AwsV4aAuthScheme.create();
        schemes.put(awsV4aAuthScheme.schemeId(), awsV4aAuthScheme);
        return Collections.unmodifiableMap(schemes);
    }

    private void callBeforeMarshallingHooks(ExecutionContext execCtx) {
        execCtx.interceptorChain().beforeMarshalling((Context.BeforeMarshalling)execCtx.interceptorContext(), execCtx.executionAttributes());
    }

    private <T> void marshalRequestAndUpdateContext(ExecutionContext execCtx, Class<T> requestType, Function<T, SdkHttpFullRequest> requestMarshaller) {
        Object sdkRequest = Validate.isInstanceOf(requestType, (Object)execCtx.interceptorContext().request(), (String)"Interceptor generated unsupported type (%s) when %s was expected.", (Object[])new Object[]{execCtx.interceptorContext().request().getClass(), requestType});
        SdkHttpFullRequest marshalledRequest = requestMarshaller.apply(sdkRequest);
        Optional<RequestBody> requestBody = marshalledRequest.contentStreamProvider().map(ContentStreamProvider::newStream).map(is -> (byte[])FunctionalUtils.invokeSafely(() -> IoUtils.toByteArray((InputStream)is))).map(RequestBody::fromBytes);
        execCtx.interceptorContext((InterceptorContext)execCtx.interceptorContext().copy(r -> r.httpRequest((SdkHttpRequest)marshalledRequest).requestBody((RequestBody)requestBody.orElse(null))));
    }

    private void callAfterMarshallingHooks(ExecutionContext execCtx) {
        execCtx.interceptorChain().afterMarshalling((Context.AfterMarshalling)execCtx.interceptorContext(), execCtx.executionAttributes());
    }

    private void addRequestLevelHeadersAndQueryParameters(ExecutionContext execCtx) {
        SdkHttpRequest httpRequest = execCtx.interceptorContext().httpRequest();
        SdkRequest sdkRequest = execCtx.interceptorContext().request();
        SdkHttpRequest updatedHttpRequest = (SdkHttpRequest)((SdkHttpRequest.Builder)((SdkHttpRequest.Builder)((SdkHttpRequest.Builder)httpRequest.toBuilder()).applyMutation(b -> this.addRequestLevelHeaders((SdkHttpRequest.Builder)b, sdkRequest))).applyMutation(b -> this.addRequestLeveQueryParameters((SdkHttpRequest.Builder)b, sdkRequest))).build();
        execCtx.interceptorContext((InterceptorContext)execCtx.interceptorContext().copy(c -> c.httpRequest(updatedHttpRequest)));
    }

    private void addRequestLevelHeaders(SdkHttpRequest.Builder builder, SdkRequest request) {
        request.overrideConfiguration().ifPresent(overrideConfig -> {
            if (!overrideConfig.headers().isEmpty()) {
                overrideConfig.headers().forEach((arg_0, arg_1) -> ((SdkHttpRequest.Builder)builder).putHeader(arg_0, arg_1));
            }
        });
    }

    private void addRequestLeveQueryParameters(SdkHttpRequest.Builder builder, SdkRequest request) {
        request.overrideConfiguration().ifPresent(overrideConfig -> {
            if (!overrideConfig.rawQueryParameters().isEmpty()) {
                overrideConfig.rawQueryParameters().forEach((arg_0, arg_1) -> ((SdkHttpRequest.Builder)builder).putRawQueryParameter(arg_0, arg_1));
            }
        });
    }

    private void callModifyHttpRequestHooksAndUpdateContext(ExecutionContext execCtx) {
        execCtx.interceptorContext(execCtx.interceptorChain().modifyHttpRequestAndHttpContent(execCtx.interceptorContext(), execCtx.executionAttributes()));
    }

    private SdkHttpFullRequest getHttpFullRequest(ExecutionContext execCtx) {
        SdkHttpRequest requestFromInterceptor = execCtx.interceptorContext().httpRequest();
        Optional bodyFromInterceptor = execCtx.interceptorContext().requestBody();
        return SdkHttpFullRequest.builder().method(requestFromInterceptor.method()).protocol(requestFromInterceptor.protocol()).host(requestFromInterceptor.host()).port(Integer.valueOf(requestFromInterceptor.port())).encodedPath(requestFromInterceptor.encodedPath()).applyMutation(r -> {
            requestFromInterceptor.forEachHeader((arg_0, arg_1) -> ((SdkHttpRequest.Builder)r).putHeader(arg_0, arg_1));
            requestFromInterceptor.forEachRawQueryParameter((arg_0, arg_1) -> ((SdkHttpRequest.Builder)r).putRawQueryParameter(arg_0, arg_1));
        }).contentStreamProvider((ContentStreamProvider)bodyFromInterceptor.map(RequestBody::contentStreamProvider).orElse(null)).build();
    }

    private SdkHttpFullRequest presignRequest(ExecutionContext execCtx, SdkHttpFullRequest request) {
        Presigner presigner = (Presigner)Validate.isInstanceOf(Presigner.class, (Object)execCtx.signer(), (String)"Configured signer (%s) does not support presigning (must implement %s).", (Object[])new Object[]{execCtx.signer().getClass(), Presigner.class});
        return presigner.presign(request, execCtx.executionAttributes());
    }

    private void initializePresignedRequest(PresignedRequest.Builder presignedRequest, ExecutionContext execCtx, SdkHttpFullRequest signedHttpRequest) {
        SdkBytes signedPayload = signedHttpRequest.contentStreamProvider().map(p -> SdkBytes.fromInputStream((InputStream)p.newStream())).orElse(null);
        List signedHeadersQueryParam = signedHttpRequest.firstMatchingRawQueryParameters("X-Amz-SignedHeaders");
        Validate.validState((!signedHeadersQueryParam.isEmpty() ? 1 : 0) != 0, (String)"Only SigV4 presigners are supported at this time, but the configured presigner (%s) did not seem to generate a SigV4 signature.", (Object[])new Object[]{execCtx.signer()});
        Map<String, List> signedHeaders = signedHeadersQueryParam.stream().flatMap(h -> Stream.of(h.split(";"))).collect(Collectors.toMap(h -> h, h -> signedHttpRequest.firstMatchingHeader(h).map(Collections::singletonList).orElseGet(ArrayList::new)));
        boolean isBrowserExecutable = signedHttpRequest.method() == SdkHttpMethod.GET && signedPayload == null && (signedHeaders.isEmpty() || signedHeaders.size() == 1 && signedHeaders.containsKey("host"));
        presignedRequest.expiration((Instant)execCtx.executionAttributes().getAttribute(AwsSignerExecutionAttribute.PRESIGNER_EXPIRATION)).isBrowserExecutable(Boolean.valueOf(isBrowserExecutable)).httpRequest((SdkHttpRequest)signedHttpRequest).signedHeaders(signedHeaders).signedPayload(signedPayload);
    }

    private AttributeMap createClientContextParams() {
        AttributeMap.Builder params = AttributeMap.builder();
        params.put(S3ClientContextParams.USE_ARN_REGION, (Object)this.serviceConfiguration.useArnRegionEnabled());
        params.put(S3ClientContextParams.DISABLE_MULTI_REGION_ACCESS_POINTS, (Object)(!this.serviceConfiguration.multiRegionEnabled() ? 1 : 0));
        params.put(S3ClientContextParams.FORCE_PATH_STYLE, (Object)this.serviceConfiguration.pathStyleAccessEnabled());
        params.put(S3ClientContextParams.ACCELERATE, (Object)this.serviceConfiguration.accelerateModeEnabled());
        return params.build();
    }

    private UseGlobalEndpointResolver createUseGlobalEndpointResolver() {
        String legacyOption = (String)DefaultsModeConfiguration.defaultConfig((DefaultsMode)DefaultsMode.LEGACY).get((AttributeMap.Key)ServiceMetadataAdvancedOption.DEFAULT_S3_US_EAST_1_REGIONAL_ENDPOINT);
        SdkClientConfiguration config = this.clientConfiguration.toBuilder().option((ClientOption)ServiceMetadataAdvancedOption.DEFAULT_S3_US_EAST_1_REGIONAL_ENDPOINT, (Object)legacyOption).option((ClientOption)SdkClientOption.PROFILE_FILE_SUPPLIER, this.profileFileSupplier()).option((ClientOption)SdkClientOption.PROFILE_NAME, (Object)this.profileName()).build();
        return new UseGlobalEndpointResolver(config);
    }

    @SdkInternalApi
    public static final class Builder
    extends DefaultSdkPresigner.Builder<Builder>
    implements S3Presigner.Builder {
        private S3Configuration serviceConfiguration;

        private Builder() {
        }

        @Override
        public Builder serviceConfiguration(S3Configuration serviceConfiguration) {
            this.serviceConfiguration = serviceConfiguration;
            return this;
        }

        @Override
        public S3Presigner build() {
            return new DefaultS3Presigner(this);
        }
    }
}

