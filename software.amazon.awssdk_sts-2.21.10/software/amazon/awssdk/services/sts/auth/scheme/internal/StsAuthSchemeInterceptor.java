/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.awscore.AwsExecutionAttribute
 *  software.amazon.awssdk.core.SdkRequest
 *  software.amazon.awssdk.core.SelectedAuthScheme
 *  software.amazon.awssdk.core.exception.SdkException
 *  software.amazon.awssdk.core.interceptor.Context$BeforeExecution
 *  software.amazon.awssdk.core.interceptor.ExecutionAttributes
 *  software.amazon.awssdk.core.interceptor.ExecutionInterceptor
 *  software.amazon.awssdk.core.interceptor.SdkExecutionAttribute
 *  software.amazon.awssdk.core.interceptor.SdkInternalExecutionAttribute
 *  software.amazon.awssdk.core.internal.util.MetricUtils
 *  software.amazon.awssdk.core.metrics.CoreMetric
 *  software.amazon.awssdk.http.auth.spi.scheme.AuthScheme
 *  software.amazon.awssdk.http.auth.spi.scheme.AuthSchemeOption
 *  software.amazon.awssdk.identity.spi.AwsCredentialsIdentity
 *  software.amazon.awssdk.identity.spi.Identity
 *  software.amazon.awssdk.identity.spi.IdentityProvider
 *  software.amazon.awssdk.identity.spi.IdentityProviders
 *  software.amazon.awssdk.identity.spi.ResolveIdentityRequest
 *  software.amazon.awssdk.identity.spi.ResolveIdentityRequest$Builder
 *  software.amazon.awssdk.identity.spi.TokenIdentity
 *  software.amazon.awssdk.metrics.MetricCollector
 *  software.amazon.awssdk.metrics.SdkMetric
 *  software.amazon.awssdk.regions.Region
 *  software.amazon.awssdk.utils.Logger
 *  software.amazon.awssdk.utils.Validate
 */
package software.amazon.awssdk.services.sts.auth.scheme.internal;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.awscore.AwsExecutionAttribute;
import software.amazon.awssdk.core.SdkRequest;
import software.amazon.awssdk.core.SelectedAuthScheme;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.core.interceptor.Context;
import software.amazon.awssdk.core.interceptor.ExecutionAttributes;
import software.amazon.awssdk.core.interceptor.ExecutionInterceptor;
import software.amazon.awssdk.core.interceptor.SdkExecutionAttribute;
import software.amazon.awssdk.core.interceptor.SdkInternalExecutionAttribute;
import software.amazon.awssdk.core.internal.util.MetricUtils;
import software.amazon.awssdk.core.metrics.CoreMetric;
import software.amazon.awssdk.http.auth.spi.scheme.AuthScheme;
import software.amazon.awssdk.http.auth.spi.scheme.AuthSchemeOption;
import software.amazon.awssdk.identity.spi.AwsCredentialsIdentity;
import software.amazon.awssdk.identity.spi.Identity;
import software.amazon.awssdk.identity.spi.IdentityProvider;
import software.amazon.awssdk.identity.spi.IdentityProviders;
import software.amazon.awssdk.identity.spi.ResolveIdentityRequest;
import software.amazon.awssdk.identity.spi.TokenIdentity;
import software.amazon.awssdk.metrics.MetricCollector;
import software.amazon.awssdk.metrics.SdkMetric;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sts.auth.scheme.StsAuthSchemeParams;
import software.amazon.awssdk.services.sts.auth.scheme.StsAuthSchemeProvider;
import software.amazon.awssdk.services.sts.endpoints.internal.AuthSchemeUtils;
import software.amazon.awssdk.utils.Logger;
import software.amazon.awssdk.utils.Validate;

@SdkInternalApi
public final class StsAuthSchemeInterceptor
implements ExecutionInterceptor {
    private static Logger LOG = Logger.loggerFor(StsAuthSchemeInterceptor.class);

    public void beforeExecution(Context.BeforeExecution context, ExecutionAttributes executionAttributes) {
        List<AuthSchemeOption> authOptions = this.resolveAuthOptions(context, executionAttributes);
        SelectedAuthScheme<? extends Identity> selectedAuthScheme = this.selectAuthScheme(authOptions, executionAttributes);
        AuthSchemeUtils.putSelectedAuthScheme(executionAttributes, selectedAuthScheme);
    }

    private List<AuthSchemeOption> resolveAuthOptions(Context.BeforeExecution context, ExecutionAttributes executionAttributes) {
        StsAuthSchemeProvider authSchemeProvider = (StsAuthSchemeProvider)Validate.isInstanceOf(StsAuthSchemeProvider.class, (Object)executionAttributes.getAttribute(SdkInternalExecutionAttribute.AUTH_SCHEME_RESOLVER), (String)"Expected an instance of StsAuthSchemeProvider", (Object[])new Object[0]);
        StsAuthSchemeParams params = this.authSchemeParams(context.request(), executionAttributes);
        return authSchemeProvider.resolveAuthScheme(params);
    }

    private SelectedAuthScheme<? extends Identity> selectAuthScheme(List<AuthSchemeOption> authOptions, ExecutionAttributes executionAttributes) {
        MetricCollector metricCollector = (MetricCollector)executionAttributes.getAttribute(SdkExecutionAttribute.API_CALL_METRIC_COLLECTOR);
        Map authSchemes = (Map)executionAttributes.getAttribute(SdkInternalExecutionAttribute.AUTH_SCHEMES);
        IdentityProviders identityProviders = (IdentityProviders)executionAttributes.getAttribute(SdkInternalExecutionAttribute.IDENTITY_PROVIDERS);
        ArrayList<Supplier<String>> discardedReasons = new ArrayList<Supplier<String>>();
        for (AuthSchemeOption authOption : authOptions) {
            AuthScheme authScheme;
            SelectedAuthScheme selectedAuthScheme = this.trySelectAuthScheme(authOption, authScheme = (AuthScheme)authSchemes.get(authOption.schemeId()), identityProviders, discardedReasons, metricCollector);
            if (selectedAuthScheme == null) continue;
            if (!discardedReasons.isEmpty()) {
                LOG.debug(() -> String.format("%s auth will be used, discarded: '%s'", authOption.schemeId(), discardedReasons.stream().map(Supplier::get).collect(Collectors.joining(", "))));
            }
            return selectedAuthScheme;
        }
        throw SdkException.builder().message("Failed to determine how to authenticate the user: " + discardedReasons.stream().map(Supplier::get).collect(Collectors.joining(", "))).build();
    }

    private StsAuthSchemeParams authSchemeParams(SdkRequest request, ExecutionAttributes executionAttributes) {
        String operation = (String)executionAttributes.getAttribute(SdkExecutionAttribute.OPERATION_NAME);
        Region region = (Region)executionAttributes.getAttribute(AwsExecutionAttribute.AWS_REGION);
        return StsAuthSchemeParams.builder().operation(operation).region(region).build();
    }

    private <T extends Identity> SelectedAuthScheme<T> trySelectAuthScheme(AuthSchemeOption authOption, AuthScheme<T> authScheme, IdentityProviders identityProviders, List<Supplier<String>> discardedReasons, MetricCollector metricCollector) {
        if (authScheme == null) {
            discardedReasons.add(() -> String.format("'%s' is not enabled for this request.", authOption.schemeId()));
            return null;
        }
        IdentityProvider identityProvider = authScheme.identityProvider(identityProviders);
        if (identityProvider == null) {
            discardedReasons.add(() -> String.format("'%s' does not have an identity provider configured.", authOption.schemeId()));
            return null;
        }
        ResolveIdentityRequest.Builder identityRequestBuilder = ResolveIdentityRequest.builder();
        authOption.forEachIdentityProperty((arg_0, arg_1) -> ((ResolveIdentityRequest.Builder)identityRequestBuilder).putProperty(arg_0, arg_1));
        SdkMetric<Duration> metric = this.getIdentityMetric(identityProvider);
        CompletableFuture identity = metric == null ? identityProvider.resolveIdentity((ResolveIdentityRequest)identityRequestBuilder.build()) : MetricUtils.reportDuration(() -> identityProvider.resolveIdentity((ResolveIdentityRequest)identityRequestBuilder.build()), (MetricCollector)metricCollector, metric);
        return new SelectedAuthScheme(identity, authScheme.signer(), authOption);
    }

    private SdkMetric<Duration> getIdentityMetric(IdentityProvider<?> identityProvider) {
        Class identityType = identityProvider.identityType();
        if (identityType == AwsCredentialsIdentity.class) {
            return CoreMetric.CREDENTIALS_FETCH_DURATION;
        }
        if (identityType == TokenIdentity.class) {
            return CoreMetric.TOKEN_FETCH_DURATION;
        }
        return null;
    }
}

