/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.services.secretsmanager.auth.scheme.internal;

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
import software.amazon.awssdk.services.secretsmanager.auth.scheme.SecretsManagerAuthSchemeParams;
import software.amazon.awssdk.services.secretsmanager.auth.scheme.SecretsManagerAuthSchemeProvider;
import software.amazon.awssdk.services.secretsmanager.endpoints.internal.AuthSchemeUtils;
import software.amazon.awssdk.utils.Logger;
import software.amazon.awssdk.utils.Validate;

@SdkInternalApi
public final class SecretsManagerAuthSchemeInterceptor
implements ExecutionInterceptor {
    private static Logger LOG = Logger.loggerFor(SecretsManagerAuthSchemeInterceptor.class);

    @Override
    public void beforeExecution(Context.BeforeExecution context, ExecutionAttributes executionAttributes) {
        List<AuthSchemeOption> authOptions = this.resolveAuthOptions(context, executionAttributes);
        SelectedAuthScheme<? extends Identity> selectedAuthScheme = this.selectAuthScheme(authOptions, executionAttributes);
        AuthSchemeUtils.putSelectedAuthScheme(executionAttributes, selectedAuthScheme);
    }

    private List<AuthSchemeOption> resolveAuthOptions(Context.BeforeExecution context, ExecutionAttributes executionAttributes) {
        SecretsManagerAuthSchemeProvider authSchemeProvider = Validate.isInstanceOf(SecretsManagerAuthSchemeProvider.class, executionAttributes.getAttribute(SdkInternalExecutionAttribute.AUTH_SCHEME_RESOLVER), "Expected an instance of SecretsManagerAuthSchemeProvider", new Object[0]);
        SecretsManagerAuthSchemeParams params = this.authSchemeParams(context.request(), executionAttributes);
        return authSchemeProvider.resolveAuthScheme(params);
    }

    private SelectedAuthScheme<? extends Identity> selectAuthScheme(List<AuthSchemeOption> authOptions, ExecutionAttributes executionAttributes) {
        MetricCollector metricCollector = executionAttributes.getAttribute(SdkExecutionAttribute.API_CALL_METRIC_COLLECTOR);
        Map<String, AuthScheme<?>> authSchemes = executionAttributes.getAttribute(SdkInternalExecutionAttribute.AUTH_SCHEMES);
        IdentityProviders identityProviders = executionAttributes.getAttribute(SdkInternalExecutionAttribute.IDENTITY_PROVIDERS);
        ArrayList<Supplier<String>> discardedReasons = new ArrayList<Supplier<String>>();
        for (AuthSchemeOption authOption : authOptions) {
            AuthScheme<?> authScheme;
            SelectedAuthScheme<?> selectedAuthScheme = this.trySelectAuthScheme(authOption, authScheme = authSchemes.get(authOption.schemeId()), identityProviders, discardedReasons, metricCollector);
            if (selectedAuthScheme == null) continue;
            if (!discardedReasons.isEmpty()) {
                LOG.debug(() -> String.format("%s auth will be used, discarded: '%s'", authOption.schemeId(), discardedReasons.stream().map(Supplier::get).collect(Collectors.joining(", "))));
            }
            return selectedAuthScheme;
        }
        throw SdkException.builder().message("Failed to determine how to authenticate the user: " + discardedReasons.stream().map(Supplier::get).collect(Collectors.joining(", "))).build();
    }

    private SecretsManagerAuthSchemeParams authSchemeParams(SdkRequest request, ExecutionAttributes executionAttributes) {
        String operation = executionAttributes.getAttribute(SdkExecutionAttribute.OPERATION_NAME);
        Region region = executionAttributes.getAttribute(AwsExecutionAttribute.AWS_REGION);
        return SecretsManagerAuthSchemeParams.builder().operation(operation).region(region).build();
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
        authOption.forEachIdentityProperty(identityRequestBuilder::putProperty);
        SdkMetric<Duration> metric = this.getIdentityMetric(identityProvider);
        CompletableFuture<T> identity = metric == null ? identityProvider.resolveIdentity((ResolveIdentityRequest)identityRequestBuilder.build()) : MetricUtils.reportDuration(() -> identityProvider.resolveIdentity((ResolveIdentityRequest)identityRequestBuilder.build()), metricCollector, metric);
        return new SelectedAuthScheme<T>(identity, authScheme.signer(), authOption);
    }

    private SdkMetric<Duration> getIdentityMetric(IdentityProvider<?> identityProvider) {
        Class<?> identityType = identityProvider.identityType();
        if (identityType == AwsCredentialsIdentity.class) {
            return CoreMetric.CREDENTIALS_FETCH_DURATION;
        }
        if (identityType == TokenIdentity.class) {
            return CoreMetric.TOKEN_FETCH_DURATION;
        }
        return null;
    }
}

