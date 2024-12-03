/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.auth.signer.Aws4Signer
 *  software.amazon.awssdk.auth.signer.SignerLoader
 *  software.amazon.awssdk.awscore.AwsExecutionAttribute
 *  software.amazon.awssdk.awscore.endpoints.AwsEndpointAttribute
 *  software.amazon.awssdk.awscore.endpoints.authscheme.EndpointAuthScheme
 *  software.amazon.awssdk.awscore.endpoints.authscheme.SigV4AuthScheme
 *  software.amazon.awssdk.awscore.endpoints.authscheme.SigV4aAuthScheme
 *  software.amazon.awssdk.awscore.util.SignerOverrideUtils
 *  software.amazon.awssdk.core.SdkRequest
 *  software.amazon.awssdk.core.SelectedAuthScheme
 *  software.amazon.awssdk.core.exception.SdkClientException
 *  software.amazon.awssdk.core.interceptor.Context$ModifyHttpRequest
 *  software.amazon.awssdk.core.interceptor.Context$ModifyRequest
 *  software.amazon.awssdk.core.interceptor.ExecutionAttributes
 *  software.amazon.awssdk.core.interceptor.ExecutionInterceptor
 *  software.amazon.awssdk.core.interceptor.SdkExecutionAttribute
 *  software.amazon.awssdk.core.interceptor.SdkInternalExecutionAttribute
 *  software.amazon.awssdk.core.signer.Signer
 *  software.amazon.awssdk.endpoints.Endpoint
 *  software.amazon.awssdk.http.SdkHttpRequest
 *  software.amazon.awssdk.http.SdkHttpRequest$Builder
 *  software.amazon.awssdk.http.auth.aws.signer.AwsV4HttpSigner
 *  software.amazon.awssdk.http.auth.aws.signer.AwsV4aHttpSigner
 *  software.amazon.awssdk.http.auth.aws.signer.RegionSet
 *  software.amazon.awssdk.http.auth.spi.scheme.AuthSchemeOption
 *  software.amazon.awssdk.http.auth.spi.scheme.AuthSchemeOption$Builder
 *  software.amazon.awssdk.identity.spi.Identity
 */
package software.amazon.awssdk.services.secretsmanager.endpoints.internal;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionException;
import java.util.function.Supplier;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.auth.signer.Aws4Signer;
import software.amazon.awssdk.auth.signer.SignerLoader;
import software.amazon.awssdk.awscore.AwsExecutionAttribute;
import software.amazon.awssdk.awscore.endpoints.AwsEndpointAttribute;
import software.amazon.awssdk.awscore.endpoints.authscheme.EndpointAuthScheme;
import software.amazon.awssdk.awscore.endpoints.authscheme.SigV4AuthScheme;
import software.amazon.awssdk.awscore.endpoints.authscheme.SigV4aAuthScheme;
import software.amazon.awssdk.awscore.util.SignerOverrideUtils;
import software.amazon.awssdk.core.SdkRequest;
import software.amazon.awssdk.core.SelectedAuthScheme;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.interceptor.Context;
import software.amazon.awssdk.core.interceptor.ExecutionAttributes;
import software.amazon.awssdk.core.interceptor.ExecutionInterceptor;
import software.amazon.awssdk.core.interceptor.SdkExecutionAttribute;
import software.amazon.awssdk.core.interceptor.SdkInternalExecutionAttribute;
import software.amazon.awssdk.core.signer.Signer;
import software.amazon.awssdk.endpoints.Endpoint;
import software.amazon.awssdk.http.SdkHttpRequest;
import software.amazon.awssdk.http.auth.aws.signer.AwsV4HttpSigner;
import software.amazon.awssdk.http.auth.aws.signer.AwsV4aHttpSigner;
import software.amazon.awssdk.http.auth.aws.signer.RegionSet;
import software.amazon.awssdk.http.auth.spi.scheme.AuthSchemeOption;
import software.amazon.awssdk.identity.spi.Identity;
import software.amazon.awssdk.services.secretsmanager.endpoints.SecretsManagerEndpointParams;
import software.amazon.awssdk.services.secretsmanager.endpoints.SecretsManagerEndpointProvider;
import software.amazon.awssdk.services.secretsmanager.endpoints.internal.AuthSchemeUtils;
import software.amazon.awssdk.services.secretsmanager.endpoints.internal.AwsEndpointProviderUtils;

@SdkInternalApi
public final class SecretsManagerResolveEndpointInterceptor
implements ExecutionInterceptor {
    public SdkRequest modifyRequest(Context.ModifyRequest context, ExecutionAttributes executionAttributes) {
        SdkRequest result = context.request();
        if (AwsEndpointProviderUtils.endpointIsDiscovered(executionAttributes)) {
            return result;
        }
        SecretsManagerEndpointProvider provider = (SecretsManagerEndpointProvider)executionAttributes.getAttribute(SdkInternalExecutionAttribute.ENDPOINT_PROVIDER);
        try {
            Optional<String> hostPrefix;
            Endpoint endpoint = provider.resolveEndpoint(SecretsManagerResolveEndpointInterceptor.ruleParams(result, executionAttributes)).join();
            if (!AwsEndpointProviderUtils.disableHostPrefixInjection(executionAttributes) && (hostPrefix = SecretsManagerResolveEndpointInterceptor.hostPrefix((String)executionAttributes.getAttribute(SdkExecutionAttribute.OPERATION_NAME), result)).isPresent()) {
                endpoint = AwsEndpointProviderUtils.addHostPrefix(endpoint, hostPrefix.get());
            }
            List endpointAuthSchemes = (List)endpoint.attribute(AwsEndpointAttribute.AUTH_SCHEMES);
            SelectedAuthScheme selectedAuthScheme = (SelectedAuthScheme)executionAttributes.getAttribute(SdkInternalExecutionAttribute.SELECTED_AUTH_SCHEME);
            if (endpointAuthSchemes != null && selectedAuthScheme != null) {
                selectedAuthScheme = this.authSchemeWithEndpointSignerProperties(endpointAuthSchemes, selectedAuthScheme);
                executionAttributes.putAttribute(SdkInternalExecutionAttribute.SELECTED_AUTH_SCHEME, selectedAuthScheme);
            }
            if (endpointAuthSchemes != null) {
                EndpointAuthScheme chosenAuthScheme = AuthSchemeUtils.chooseAuthScheme(endpointAuthSchemes);
                Supplier<Signer> signerProvider = this.signerProvider(chosenAuthScheme);
                result = SignerOverrideUtils.overrideSignerIfNotOverridden((SdkRequest)result, (ExecutionAttributes)executionAttributes, signerProvider);
            }
            executionAttributes.putAttribute(SdkInternalExecutionAttribute.RESOLVED_ENDPOINT, (Object)endpoint);
            return result;
        }
        catch (CompletionException e) {
            Throwable cause = e.getCause();
            if (cause instanceof SdkClientException) {
                throw (SdkClientException)cause;
            }
            throw SdkClientException.create((String)"Endpoint resolution failed", (Throwable)cause);
        }
    }

    public SdkHttpRequest modifyHttpRequest(Context.ModifyHttpRequest context, ExecutionAttributes executionAttributes) {
        Endpoint resolvedEndpoint = (Endpoint)executionAttributes.getAttribute(SdkInternalExecutionAttribute.RESOLVED_ENDPOINT);
        if (resolvedEndpoint.headers().isEmpty()) {
            return context.httpRequest();
        }
        SdkHttpRequest.Builder httpRequestBuilder = (SdkHttpRequest.Builder)context.httpRequest().toBuilder();
        resolvedEndpoint.headers().forEach((name, values) -> values.forEach(v -> httpRequestBuilder.appendHeader(name, v)));
        return (SdkHttpRequest)httpRequestBuilder.build();
    }

    public static SecretsManagerEndpointParams ruleParams(SdkRequest request, ExecutionAttributes executionAttributes) {
        SecretsManagerEndpointParams.Builder builder = SecretsManagerEndpointParams.builder();
        builder.region(AwsEndpointProviderUtils.regionBuiltIn(executionAttributes));
        builder.useDualStack(AwsEndpointProviderUtils.dualStackEnabledBuiltIn(executionAttributes));
        builder.useFips(AwsEndpointProviderUtils.fipsEnabledBuiltIn(executionAttributes));
        builder.endpoint(AwsEndpointProviderUtils.endpointBuiltIn(executionAttributes));
        SecretsManagerResolveEndpointInterceptor.setContextParams(builder, (String)executionAttributes.getAttribute(AwsExecutionAttribute.OPERATION_NAME), request);
        SecretsManagerResolveEndpointInterceptor.setStaticContextParams(builder, (String)executionAttributes.getAttribute(AwsExecutionAttribute.OPERATION_NAME));
        return builder.build();
    }

    private static void setContextParams(SecretsManagerEndpointParams.Builder params, String operationName, SdkRequest request) {
    }

    private static void setStaticContextParams(SecretsManagerEndpointParams.Builder params, String operationName) {
    }

    private <T extends Identity> SelectedAuthScheme<T> authSchemeWithEndpointSignerProperties(List<EndpointAuthScheme> endpointAuthSchemes, SelectedAuthScheme<T> selectedAuthScheme) {
        Iterator<EndpointAuthScheme> iterator = endpointAuthSchemes.iterator();
        if (iterator.hasNext()) {
            EndpointAuthScheme endpointAuthScheme = iterator.next();
            AuthSchemeOption.Builder option = (AuthSchemeOption.Builder)selectedAuthScheme.authSchemeOption().toBuilder();
            if (endpointAuthScheme instanceof SigV4AuthScheme) {
                SigV4AuthScheme v4AuthScheme = (SigV4AuthScheme)endpointAuthScheme;
                if (v4AuthScheme.isDisableDoubleEncodingSet()) {
                    option.putSignerProperty(AwsV4HttpSigner.DOUBLE_URL_ENCODE, (Object)(!v4AuthScheme.disableDoubleEncoding() ? 1 : 0));
                }
                if (v4AuthScheme.signingRegion() != null) {
                    option.putSignerProperty(AwsV4HttpSigner.REGION_NAME, (Object)v4AuthScheme.signingRegion());
                }
                if (v4AuthScheme.signingName() != null) {
                    option.putSignerProperty(AwsV4HttpSigner.SERVICE_SIGNING_NAME, (Object)v4AuthScheme.signingName());
                }
                return new SelectedAuthScheme(selectedAuthScheme.identity(), selectedAuthScheme.signer(), (AuthSchemeOption)option.build());
            }
            if (endpointAuthScheme instanceof SigV4aAuthScheme) {
                SigV4aAuthScheme v4aAuthScheme = (SigV4aAuthScheme)endpointAuthScheme;
                if (v4aAuthScheme.isDisableDoubleEncodingSet()) {
                    option.putSignerProperty(AwsV4aHttpSigner.DOUBLE_URL_ENCODE, (Object)(!v4aAuthScheme.disableDoubleEncoding() ? 1 : 0));
                }
                if (v4aAuthScheme.signingRegionSet() != null) {
                    RegionSet regionSet = RegionSet.create((Collection)v4aAuthScheme.signingRegionSet());
                    option.putSignerProperty(AwsV4aHttpSigner.REGION_SET, (Object)regionSet);
                }
                if (v4aAuthScheme.signingName() != null) {
                    option.putSignerProperty(AwsV4aHttpSigner.SERVICE_SIGNING_NAME, (Object)v4aAuthScheme.signingName());
                }
                return new SelectedAuthScheme(selectedAuthScheme.identity(), selectedAuthScheme.signer(), (AuthSchemeOption)option.build());
            }
            throw new IllegalArgumentException("Endpoint auth scheme '" + endpointAuthScheme.name() + "' cannot be mapped to the SDK auth scheme. Was it declared in the service's model?");
        }
        return selectedAuthScheme;
    }

    private static Optional<String> hostPrefix(String operationName, SdkRequest request) {
        return Optional.empty();
    }

    private Supplier<Signer> signerProvider(EndpointAuthScheme authScheme) {
        switch (authScheme.name()) {
            case "sigv4": {
                return Aws4Signer::create;
            }
            case "sigv4a": {
                return SignerLoader::getSigV4aSigner;
            }
        }
        throw SdkClientException.create((String)("Don't know how to create signer for auth scheme: " + authScheme.name()));
    }
}

