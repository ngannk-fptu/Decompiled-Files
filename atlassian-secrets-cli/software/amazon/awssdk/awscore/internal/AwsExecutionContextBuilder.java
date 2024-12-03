/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.awscore.internal;

import java.util.Map;
import software.amazon.awssdk.annotations.SdkInternalApi;
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
import software.amazon.awssdk.core.client.config.SdkAdvancedClientOption;
import software.amazon.awssdk.core.client.config.SdkClientConfiguration;
import software.amazon.awssdk.core.client.config.SdkClientOption;
import software.amazon.awssdk.core.client.handler.ClientExecutionParams;
import software.amazon.awssdk.core.http.ExecutionContext;
import software.amazon.awssdk.core.interceptor.ExecutionAttributes;
import software.amazon.awssdk.core.interceptor.ExecutionInterceptorChain;
import software.amazon.awssdk.core.interceptor.InterceptorContext;
import software.amazon.awssdk.core.interceptor.SdkExecutionAttribute;
import software.amazon.awssdk.core.interceptor.SdkInternalExecutionAttribute;
import software.amazon.awssdk.core.internal.InternalCoreExecutionAttribute;
import software.amazon.awssdk.core.internal.util.HttpChecksumResolver;
import software.amazon.awssdk.core.signer.Signer;
import software.amazon.awssdk.endpoints.EndpointProvider;
import software.amazon.awssdk.http.auth.spi.scheme.AuthScheme;
import software.amazon.awssdk.http.auth.spi.scheme.AuthSchemeProvider;
import software.amazon.awssdk.identity.spi.IdentityProviders;
import software.amazon.awssdk.metrics.MetricCollector;

@SdkInternalApi
public final class AwsExecutionContextBuilder {
    private AwsExecutionContextBuilder() {
    }

    public static <InputT extends SdkRequest, OutputT extends SdkResponse> ExecutionContext invokeInterceptorsAndCreateExecutionContext(ClientExecutionParams<InputT, OutputT> executionParams, SdkClientConfiguration clientConfig) {
        InputT originalRequest = executionParams.getInput();
        MetricCollector metricCollector = AwsExecutionContextBuilder.resolveMetricCollector(executionParams);
        ExecutionAttributes executionAttributes = AwsExecutionContextBuilder.mergeExecutionAttributeOverrides(executionParams.executionAttributes(), clientConfig.option(SdkClientOption.EXECUTION_ATTRIBUTES), ((SdkRequest)originalRequest).overrideConfiguration().map(c -> c.executionAttributes()).orElse(null));
        executionAttributes.putAttributeIfAbsent(SdkExecutionAttribute.API_CALL_METRIC_COLLECTOR, metricCollector);
        executionAttributes.putAttribute(InternalCoreExecutionAttribute.EXECUTION_ATTEMPT, 1).putAttribute(AwsSignerExecutionAttribute.SERVICE_CONFIG, clientConfig.option(SdkClientOption.SERVICE_CONFIGURATION)).putAttribute(AwsSignerExecutionAttribute.SERVICE_SIGNING_NAME, clientConfig.option(AwsClientOption.SERVICE_SIGNING_NAME)).putAttribute(AwsExecutionAttribute.AWS_REGION, clientConfig.option(AwsClientOption.AWS_REGION)).putAttribute(AwsExecutionAttribute.ENDPOINT_PREFIX, clientConfig.option(AwsClientOption.ENDPOINT_PREFIX)).putAttribute(AwsSignerExecutionAttribute.SIGNING_REGION, clientConfig.option(AwsClientOption.SIGNING_REGION)).putAttribute(SdkInternalExecutionAttribute.IS_FULL_DUPLEX, executionParams.isFullDuplex()).putAttribute(SdkInternalExecutionAttribute.HAS_INITIAL_REQUEST_EVENT, executionParams.hasInitialRequestEvent()).putAttribute(SdkExecutionAttribute.CLIENT_TYPE, clientConfig.option(SdkClientOption.CLIENT_TYPE)).putAttribute(SdkExecutionAttribute.SERVICE_NAME, clientConfig.option(SdkClientOption.SERVICE_NAME)).putAttribute(SdkInternalExecutionAttribute.PROTOCOL_METADATA, executionParams.getProtocolMetadata()).putAttribute(SdkExecutionAttribute.PROFILE_FILE, clientConfig.option(SdkClientOption.PROFILE_FILE_SUPPLIER) != null ? clientConfig.option(SdkClientOption.PROFILE_FILE_SUPPLIER).get() : null).putAttribute(SdkExecutionAttribute.PROFILE_FILE_SUPPLIER, clientConfig.option(SdkClientOption.PROFILE_FILE_SUPPLIER)).putAttribute(SdkExecutionAttribute.PROFILE_NAME, clientConfig.option(SdkClientOption.PROFILE_NAME)).putAttribute(AwsExecutionAttribute.DUALSTACK_ENDPOINT_ENABLED, clientConfig.option(AwsClientOption.DUALSTACK_ENDPOINT_ENABLED)).putAttribute(AwsExecutionAttribute.FIPS_ENDPOINT_ENABLED, clientConfig.option(AwsClientOption.FIPS_ENDPOINT_ENABLED)).putAttribute(SdkExecutionAttribute.OPERATION_NAME, executionParams.getOperationName()).putAttribute(SdkExecutionAttribute.CLIENT_ENDPOINT, clientConfig.option(SdkClientOption.ENDPOINT)).putAttribute(SdkExecutionAttribute.ENDPOINT_OVERRIDDEN, clientConfig.option(SdkClientOption.ENDPOINT_OVERRIDDEN)).putAttribute(SdkInternalExecutionAttribute.ENDPOINT_PROVIDER, AwsExecutionContextBuilder.resolveEndpointProvider(originalRequest, clientConfig)).putAttribute(SdkInternalExecutionAttribute.CLIENT_CONTEXT_PARAMS, clientConfig.option(SdkClientOption.CLIENT_CONTEXT_PARAMS)).putAttribute(SdkInternalExecutionAttribute.DISABLE_HOST_PREFIX_INJECTION, clientConfig.option(SdkAdvancedClientOption.DISABLE_HOST_PREFIX_INJECTION)).putAttribute(SdkExecutionAttribute.SIGNER_OVERRIDDEN, clientConfig.option(SdkClientOption.SIGNER_OVERRIDDEN)).putAttribute(AwsExecutionAttribute.USE_GLOBAL_ENDPOINT, clientConfig.option(AwsClientOption.USE_GLOBAL_ENDPOINT)).putAttribute(SdkExecutionAttribute.RESOLVED_CHECKSUM_SPECS, HttpChecksumResolver.resolveChecksumSpecs(executionAttributes));
        AwsExecutionContextBuilder.putAuthSchemeResolutionAttributes(executionAttributes, clientConfig, originalRequest);
        ExecutionInterceptorChain executionInterceptorChain = new ExecutionInterceptorChain(clientConfig.option(SdkClientOption.EXECUTION_INTERCEPTORS));
        InterceptorContext interceptorContext = InterceptorContext.builder().request((SdkRequest)originalRequest).asyncRequestBody(executionParams.getAsyncRequestBody()).requestBody(executionParams.getRequestBody()).build();
        interceptorContext = AwsExecutionContextBuilder.runInitialInterceptors(interceptorContext, executionAttributes, executionInterceptorChain);
        Signer signer = null;
        if (AwsExecutionContextBuilder.loadOldSigner(executionAttributes, originalRequest)) {
            AuthorizationStrategyFactory authorizationStrategyFactory = new AuthorizationStrategyFactory(interceptorContext.request(), metricCollector, clientConfig);
            AuthorizationStrategy authorizationStrategy = authorizationStrategyFactory.strategyFor(executionParams.credentialType());
            authorizationStrategy.addCredentialsToExecutionAttributes(executionAttributes);
            signer = authorizationStrategy.resolveSigner();
        }
        executionAttributes.putAttribute(HttpChecksumConstant.SIGNING_METHOD, SignerMethodResolver.resolveSigningMethodUsed(signer, executionAttributes, executionAttributes.getOptionalAttribute(AwsSignerExecutionAttribute.AWS_CREDENTIALS).orElse(null)));
        return ExecutionContext.builder().interceptorChain(executionInterceptorChain).interceptorContext(interceptorContext).executionAttributes(executionAttributes).signer(signer).metricCollector(metricCollector).build();
    }

    private static boolean loadOldSigner(ExecutionAttributes attributes, SdkRequest request) {
        Map<String, AuthScheme<?>> authSchemes = attributes.getAttribute(SdkInternalExecutionAttribute.AUTH_SCHEMES);
        if (authSchemes == null) {
            return attributes.getOptionalAttribute(SdkInternalExecutionAttribute.IS_NONE_AUTH_TYPE_REQUEST).orElse(true);
        }
        SelectedAuthScheme<?> selectedAuthScheme = attributes.getAttribute(SdkInternalExecutionAttribute.SELECTED_AUTH_SCHEME);
        return SignerOverrideUtils.isSignerOverridden(request, attributes) && selectedAuthScheme != null && !"smithy.api#noAuth".equals(selectedAuthScheme.authSchemeOption().schemeId());
    }

    private static void putAuthSchemeResolutionAttributes(ExecutionAttributes executionAttributes, SdkClientConfiguration clientConfig, SdkRequest originalRequest) {
        AuthSchemeProvider authSchemeProvider = clientConfig.option(SdkClientOption.AUTH_SCHEME_PROVIDER);
        Map<String, AuthScheme<?>> authSchemes = clientConfig.option(SdkClientOption.AUTH_SCHEMES);
        IdentityProviders identityProviders = AwsExecutionContextBuilder.resolveIdentityProviders(originalRequest, clientConfig);
        executionAttributes.putAttribute(SdkInternalExecutionAttribute.AUTH_SCHEME_RESOLVER, authSchemeProvider).putAttribute(SdkInternalExecutionAttribute.AUTH_SCHEMES, authSchemes).putAttribute(SdkInternalExecutionAttribute.IDENTITY_PROVIDERS, identityProviders);
    }

    private static IdentityProviders resolveIdentityProviders(SdkRequest originalRequest, SdkClientConfiguration clientConfig) {
        IdentityProviders identityProviders = clientConfig.option(SdkClientOption.IDENTITY_PROVIDERS);
        if (identityProviders == null) {
            return null;
        }
        return originalRequest.overrideConfiguration().filter(c -> c instanceof AwsRequestOverrideConfiguration).map(c -> (AwsRequestOverrideConfiguration)c).flatMap(AwsRequestOverrideConfiguration::credentialsIdentityProvider).map(identityProvider -> (IdentityProviders)identityProviders.copy(b -> b.putIdentityProvider(identityProvider))).orElse(identityProviders);
    }

    public static InterceptorContext runInitialInterceptors(InterceptorContext interceptorContext, ExecutionAttributes executionAttributes, ExecutionInterceptorChain executionInterceptorChain) {
        executionInterceptorChain.beforeExecution(interceptorContext, executionAttributes);
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
            metricCollector = MetricCollector.create("ApiCall");
        }
        return metricCollector;
    }

    private static EndpointProvider resolveEndpointProvider(SdkRequest request, SdkClientConfiguration clientConfig) {
        return request.overrideConfiguration().flatMap(RequestOverrideConfiguration::endpointProvider).orElse(clientConfig.option(SdkClientOption.ENDPOINT_PROVIDER));
    }
}

