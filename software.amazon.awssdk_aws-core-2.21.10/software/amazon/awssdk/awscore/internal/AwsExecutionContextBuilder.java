/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.auth.credentials.AwsCredentials
 *  software.amazon.awssdk.auth.signer.AwsSignerExecutionAttribute
 *  software.amazon.awssdk.auth.signer.internal.util.SignerMethodResolver
 *  software.amazon.awssdk.core.HttpChecksumConstant
 *  software.amazon.awssdk.core.RequestOverrideConfiguration
 *  software.amazon.awssdk.core.SdkRequest
 *  software.amazon.awssdk.core.SdkResponse
 *  software.amazon.awssdk.core.SelectedAuthScheme
 *  software.amazon.awssdk.core.client.config.ClientOption
 *  software.amazon.awssdk.core.client.config.SdkAdvancedClientOption
 *  software.amazon.awssdk.core.client.config.SdkClientConfiguration
 *  software.amazon.awssdk.core.client.config.SdkClientOption
 *  software.amazon.awssdk.core.client.handler.ClientExecutionParams
 *  software.amazon.awssdk.core.http.ExecutionContext
 *  software.amazon.awssdk.core.interceptor.Context$BeforeExecution
 *  software.amazon.awssdk.core.interceptor.ExecutionAttributes
 *  software.amazon.awssdk.core.interceptor.ExecutionInterceptorChain
 *  software.amazon.awssdk.core.interceptor.InterceptorContext
 *  software.amazon.awssdk.core.interceptor.SdkExecutionAttribute
 *  software.amazon.awssdk.core.interceptor.SdkInternalExecutionAttribute
 *  software.amazon.awssdk.core.internal.InternalCoreExecutionAttribute
 *  software.amazon.awssdk.core.internal.util.HttpChecksumResolver
 *  software.amazon.awssdk.core.signer.Signer
 *  software.amazon.awssdk.endpoints.EndpointProvider
 *  software.amazon.awssdk.http.auth.spi.scheme.AuthSchemeProvider
 *  software.amazon.awssdk.identity.spi.IdentityProviders
 *  software.amazon.awssdk.metrics.MetricCollector
 *  software.amazon.awssdk.profiles.ProfileFile
 */
package software.amazon.awssdk.awscore.internal;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.signer.AwsSignerExecutionAttribute;
import software.amazon.awssdk.auth.signer.internal.util.SignerMethodResolver;
import software.amazon.awssdk.awscore.AwsExecutionAttribute;
import software.amazon.awssdk.awscore.AwsRequestOverrideConfiguration;
import software.amazon.awssdk.awscore.client.config.AwsClientOption;
import software.amazon.awssdk.awscore.internal.authcontext.AuthorizationStrategy;
import software.amazon.awssdk.awscore.internal.authcontext.AuthorizationStrategyFactory;
import software.amazon.awssdk.awscore.util.SignerOverrideUtils;
import software.amazon.awssdk.core.HttpChecksumConstant;
import software.amazon.awssdk.core.RequestOverrideConfiguration;
import software.amazon.awssdk.core.SdkRequest;
import software.amazon.awssdk.core.SdkResponse;
import software.amazon.awssdk.core.SelectedAuthScheme;
import software.amazon.awssdk.core.client.config.ClientOption;
import software.amazon.awssdk.core.client.config.SdkAdvancedClientOption;
import software.amazon.awssdk.core.client.config.SdkClientConfiguration;
import software.amazon.awssdk.core.client.config.SdkClientOption;
import software.amazon.awssdk.core.client.handler.ClientExecutionParams;
import software.amazon.awssdk.core.http.ExecutionContext;
import software.amazon.awssdk.core.interceptor.Context;
import software.amazon.awssdk.core.interceptor.ExecutionAttributes;
import software.amazon.awssdk.core.interceptor.ExecutionInterceptorChain;
import software.amazon.awssdk.core.interceptor.InterceptorContext;
import software.amazon.awssdk.core.interceptor.SdkExecutionAttribute;
import software.amazon.awssdk.core.interceptor.SdkInternalExecutionAttribute;
import software.amazon.awssdk.core.internal.InternalCoreExecutionAttribute;
import software.amazon.awssdk.core.internal.util.HttpChecksumResolver;
import software.amazon.awssdk.core.signer.Signer;
import software.amazon.awssdk.endpoints.EndpointProvider;
import software.amazon.awssdk.http.auth.spi.scheme.AuthSchemeProvider;
import software.amazon.awssdk.identity.spi.IdentityProviders;
import software.amazon.awssdk.metrics.MetricCollector;
import software.amazon.awssdk.profiles.ProfileFile;

@SdkInternalApi
public final class AwsExecutionContextBuilder {
    private AwsExecutionContextBuilder() {
    }

    public static <InputT extends SdkRequest, OutputT extends SdkResponse> ExecutionContext invokeInterceptorsAndCreateExecutionContext(ClientExecutionParams<InputT, OutputT> executionParams, SdkClientConfiguration clientConfig) {
        SdkRequest originalRequest = executionParams.getInput();
        MetricCollector metricCollector = AwsExecutionContextBuilder.resolveMetricCollector(executionParams);
        ExecutionAttributes executionAttributes = AwsExecutionContextBuilder.mergeExecutionAttributeOverrides(executionParams.executionAttributes(), (ExecutionAttributes)clientConfig.option((ClientOption)SdkClientOption.EXECUTION_ATTRIBUTES), originalRequest.overrideConfiguration().map(c -> c.executionAttributes()).orElse(null));
        executionAttributes.putAttributeIfAbsent(SdkExecutionAttribute.API_CALL_METRIC_COLLECTOR, (Object)metricCollector);
        executionAttributes.putAttribute(InternalCoreExecutionAttribute.EXECUTION_ATTEMPT, (Object)1).putAttribute(AwsSignerExecutionAttribute.SERVICE_CONFIG, clientConfig.option((ClientOption)SdkClientOption.SERVICE_CONFIGURATION)).putAttribute(AwsSignerExecutionAttribute.SERVICE_SIGNING_NAME, clientConfig.option(AwsClientOption.SERVICE_SIGNING_NAME)).putAttribute(AwsExecutionAttribute.AWS_REGION, clientConfig.option(AwsClientOption.AWS_REGION)).putAttribute(AwsExecutionAttribute.ENDPOINT_PREFIX, clientConfig.option(AwsClientOption.ENDPOINT_PREFIX)).putAttribute(AwsSignerExecutionAttribute.SIGNING_REGION, clientConfig.option(AwsClientOption.SIGNING_REGION)).putAttribute(SdkInternalExecutionAttribute.IS_FULL_DUPLEX, (Object)executionParams.isFullDuplex()).putAttribute(SdkInternalExecutionAttribute.HAS_INITIAL_REQUEST_EVENT, (Object)executionParams.hasInitialRequestEvent()).putAttribute(SdkExecutionAttribute.CLIENT_TYPE, clientConfig.option((ClientOption)SdkClientOption.CLIENT_TYPE)).putAttribute(SdkExecutionAttribute.SERVICE_NAME, clientConfig.option((ClientOption)SdkClientOption.SERVICE_NAME)).putAttribute(SdkInternalExecutionAttribute.PROTOCOL_METADATA, (Object)executionParams.getProtocolMetadata()).putAttribute(SdkExecutionAttribute.PROFILE_FILE, clientConfig.option((ClientOption)SdkClientOption.PROFILE_FILE_SUPPLIER) != null ? (ProfileFile)((Supplier)clientConfig.option((ClientOption)SdkClientOption.PROFILE_FILE_SUPPLIER)).get() : null).putAttribute(SdkExecutionAttribute.PROFILE_FILE_SUPPLIER, clientConfig.option((ClientOption)SdkClientOption.PROFILE_FILE_SUPPLIER)).putAttribute(SdkExecutionAttribute.PROFILE_NAME, clientConfig.option((ClientOption)SdkClientOption.PROFILE_NAME)).putAttribute(AwsExecutionAttribute.DUALSTACK_ENDPOINT_ENABLED, clientConfig.option(AwsClientOption.DUALSTACK_ENDPOINT_ENABLED)).putAttribute(AwsExecutionAttribute.FIPS_ENDPOINT_ENABLED, clientConfig.option(AwsClientOption.FIPS_ENDPOINT_ENABLED)).putAttribute(SdkExecutionAttribute.OPERATION_NAME, (Object)executionParams.getOperationName()).putAttribute(SdkExecutionAttribute.CLIENT_ENDPOINT, clientConfig.option((ClientOption)SdkClientOption.ENDPOINT)).putAttribute(SdkExecutionAttribute.ENDPOINT_OVERRIDDEN, clientConfig.option((ClientOption)SdkClientOption.ENDPOINT_OVERRIDDEN)).putAttribute(SdkInternalExecutionAttribute.ENDPOINT_PROVIDER, (Object)AwsExecutionContextBuilder.resolveEndpointProvider(originalRequest, clientConfig)).putAttribute(SdkInternalExecutionAttribute.CLIENT_CONTEXT_PARAMS, clientConfig.option((ClientOption)SdkClientOption.CLIENT_CONTEXT_PARAMS)).putAttribute(SdkInternalExecutionAttribute.DISABLE_HOST_PREFIX_INJECTION, clientConfig.option((ClientOption)SdkAdvancedClientOption.DISABLE_HOST_PREFIX_INJECTION)).putAttribute(SdkExecutionAttribute.SIGNER_OVERRIDDEN, clientConfig.option((ClientOption)SdkClientOption.SIGNER_OVERRIDDEN)).putAttribute(AwsExecutionAttribute.USE_GLOBAL_ENDPOINT, clientConfig.option(AwsClientOption.USE_GLOBAL_ENDPOINT)).putAttribute(SdkExecutionAttribute.RESOLVED_CHECKSUM_SPECS, (Object)HttpChecksumResolver.resolveChecksumSpecs((ExecutionAttributes)executionAttributes));
        AwsExecutionContextBuilder.putAuthSchemeResolutionAttributes(executionAttributes, clientConfig, originalRequest);
        ExecutionInterceptorChain executionInterceptorChain = new ExecutionInterceptorChain((List)clientConfig.option((ClientOption)SdkClientOption.EXECUTION_INTERCEPTORS));
        InterceptorContext interceptorContext = InterceptorContext.builder().request(originalRequest).asyncRequestBody(executionParams.getAsyncRequestBody()).requestBody(executionParams.getRequestBody()).build();
        interceptorContext = AwsExecutionContextBuilder.runInitialInterceptors(interceptorContext, executionAttributes, executionInterceptorChain);
        Signer signer = null;
        if (AwsExecutionContextBuilder.loadOldSigner(executionAttributes, originalRequest)) {
            AuthorizationStrategyFactory authorizationStrategyFactory = new AuthorizationStrategyFactory(interceptorContext.request(), metricCollector, clientConfig);
            AuthorizationStrategy authorizationStrategy = authorizationStrategyFactory.strategyFor(executionParams.credentialType());
            authorizationStrategy.addCredentialsToExecutionAttributes(executionAttributes);
            signer = authorizationStrategy.resolveSigner();
        }
        executionAttributes.putAttribute(HttpChecksumConstant.SIGNING_METHOD, (Object)SignerMethodResolver.resolveSigningMethodUsed(signer, (ExecutionAttributes)executionAttributes, (AwsCredentials)executionAttributes.getOptionalAttribute(AwsSignerExecutionAttribute.AWS_CREDENTIALS).orElse(null)));
        return ExecutionContext.builder().interceptorChain(executionInterceptorChain).interceptorContext(interceptorContext).executionAttributes(executionAttributes).signer(signer).metricCollector(metricCollector).build();
    }

    private static boolean loadOldSigner(ExecutionAttributes attributes, SdkRequest request) {
        Map authSchemes = (Map)attributes.getAttribute(SdkInternalExecutionAttribute.AUTH_SCHEMES);
        if (authSchemes == null) {
            return attributes.getOptionalAttribute(SdkInternalExecutionAttribute.IS_NONE_AUTH_TYPE_REQUEST).orElse(true);
        }
        SelectedAuthScheme selectedAuthScheme = (SelectedAuthScheme)attributes.getAttribute(SdkInternalExecutionAttribute.SELECTED_AUTH_SCHEME);
        return SignerOverrideUtils.isSignerOverridden(request, attributes) && selectedAuthScheme != null && !"smithy.api#noAuth".equals(selectedAuthScheme.authSchemeOption().schemeId());
    }

    private static void putAuthSchemeResolutionAttributes(ExecutionAttributes executionAttributes, SdkClientConfiguration clientConfig, SdkRequest originalRequest) {
        AuthSchemeProvider authSchemeProvider = (AuthSchemeProvider)clientConfig.option((ClientOption)SdkClientOption.AUTH_SCHEME_PROVIDER);
        Map authSchemes = (Map)clientConfig.option((ClientOption)SdkClientOption.AUTH_SCHEMES);
        IdentityProviders identityProviders = AwsExecutionContextBuilder.resolveIdentityProviders(originalRequest, clientConfig);
        executionAttributes.putAttribute(SdkInternalExecutionAttribute.AUTH_SCHEME_RESOLVER, (Object)authSchemeProvider).putAttribute(SdkInternalExecutionAttribute.AUTH_SCHEMES, (Object)authSchemes).putAttribute(SdkInternalExecutionAttribute.IDENTITY_PROVIDERS, (Object)identityProviders);
    }

    private static IdentityProviders resolveIdentityProviders(SdkRequest originalRequest, SdkClientConfiguration clientConfig) {
        IdentityProviders identityProviders = (IdentityProviders)clientConfig.option((ClientOption)SdkClientOption.IDENTITY_PROVIDERS);
        if (identityProviders == null) {
            return null;
        }
        return originalRequest.overrideConfiguration().filter(c -> c instanceof AwsRequestOverrideConfiguration).map(c -> (AwsRequestOverrideConfiguration)((Object)c)).flatMap(AwsRequestOverrideConfiguration::credentialsIdentityProvider).map(identityProvider -> (IdentityProviders)identityProviders.copy(b -> b.putIdentityProvider(identityProvider))).orElse(identityProviders);
    }

    public static InterceptorContext runInitialInterceptors(InterceptorContext interceptorContext, ExecutionAttributes executionAttributes, ExecutionInterceptorChain executionInterceptorChain) {
        executionInterceptorChain.beforeExecution((Context.BeforeExecution)interceptorContext, executionAttributes);
        return executionInterceptorChain.modifyRequest(interceptorContext, executionAttributes);
    }

    private static <InputT extends SdkRequest, OutputT extends SdkResponse> ExecutionAttributes mergeExecutionAttributeOverrides(ExecutionAttributes executionAttributes, ExecutionAttributes clientOverrideExecutionAttributes, ExecutionAttributes requestOverrideExecutionAttributes) {
        executionAttributes.putAbsentAttributes(requestOverrideExecutionAttributes);
        executionAttributes.putAbsentAttributes(clientOverrideExecutionAttributes);
        return executionAttributes;
    }

    private static MetricCollector resolveMetricCollector(ClientExecutionParams<?, ?> params) {
        MetricCollector metricCollector = params.getMetricCollector();
        if (metricCollector == null) {
            metricCollector = MetricCollector.create((String)"ApiCall");
        }
        return metricCollector;
    }

    private static EndpointProvider resolveEndpointProvider(SdkRequest request, SdkClientConfiguration clientConfig) {
        return (EndpointProvider)request.overrideConfiguration().flatMap(RequestOverrideConfiguration::endpointProvider).orElse(clientConfig.option((ClientOption)SdkClientOption.ENDPOINT_PROVIDER));
    }
}

