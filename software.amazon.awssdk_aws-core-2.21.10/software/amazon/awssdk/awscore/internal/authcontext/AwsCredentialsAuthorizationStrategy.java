/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.auth.credentials.AwsCredentials
 *  software.amazon.awssdk.auth.credentials.CredentialUtils
 *  software.amazon.awssdk.auth.signer.AwsSignerExecutionAttribute
 *  software.amazon.awssdk.core.RequestOverrideConfiguration
 *  software.amazon.awssdk.core.SdkRequest
 *  software.amazon.awssdk.core.interceptor.ExecutionAttributes
 *  software.amazon.awssdk.core.internal.util.MetricUtils
 *  software.amazon.awssdk.core.metrics.CoreMetric
 *  software.amazon.awssdk.core.signer.Signer
 *  software.amazon.awssdk.identity.spi.AwsCredentialsIdentity
 *  software.amazon.awssdk.identity.spi.IdentityProvider
 *  software.amazon.awssdk.metrics.MetricCollector
 *  software.amazon.awssdk.utils.CompletableFutureUtils
 *  software.amazon.awssdk.utils.Pair
 *  software.amazon.awssdk.utils.Validate
 */
package software.amazon.awssdk.awscore.internal.authcontext;

import java.util.concurrent.CompletableFuture;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.CredentialUtils;
import software.amazon.awssdk.auth.signer.AwsSignerExecutionAttribute;
import software.amazon.awssdk.awscore.AwsRequestOverrideConfiguration;
import software.amazon.awssdk.awscore.internal.authcontext.AuthorizationStrategy;
import software.amazon.awssdk.core.RequestOverrideConfiguration;
import software.amazon.awssdk.core.SdkRequest;
import software.amazon.awssdk.core.interceptor.ExecutionAttributes;
import software.amazon.awssdk.core.internal.util.MetricUtils;
import software.amazon.awssdk.core.metrics.CoreMetric;
import software.amazon.awssdk.core.signer.Signer;
import software.amazon.awssdk.identity.spi.AwsCredentialsIdentity;
import software.amazon.awssdk.identity.spi.IdentityProvider;
import software.amazon.awssdk.metrics.MetricCollector;
import software.amazon.awssdk.utils.CompletableFutureUtils;
import software.amazon.awssdk.utils.Pair;
import software.amazon.awssdk.utils.Validate;

@Deprecated
@SdkInternalApi
public final class AwsCredentialsAuthorizationStrategy
implements AuthorizationStrategy {
    private final SdkRequest request;
    private final Signer defaultSigner;
    private final IdentityProvider<? extends AwsCredentialsIdentity> defaultCredentialsProvider;
    private final MetricCollector metricCollector;

    public AwsCredentialsAuthorizationStrategy(Builder builder) {
        this.request = builder.request();
        this.defaultSigner = builder.defaultSigner();
        this.defaultCredentialsProvider = builder.defaultCredentialsProvider();
        this.metricCollector = builder.metricCollector();
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public Signer resolveSigner() {
        return this.request.overrideConfiguration().flatMap(RequestOverrideConfiguration::signer).orElse(this.defaultSigner);
    }

    @Override
    public void addCredentialsToExecutionAttributes(ExecutionAttributes executionAttributes) {
        IdentityProvider<? extends AwsCredentialsIdentity> credentialsProvider = AwsCredentialsAuthorizationStrategy.resolveCredentialsProvider(this.request, this.defaultCredentialsProvider);
        AwsCredentials credentials = CredentialUtils.toCredentials((AwsCredentialsIdentity)AwsCredentialsAuthorizationStrategy.resolveCredentials(credentialsProvider, this.metricCollector));
        executionAttributes.putAttribute(AwsSignerExecutionAttribute.AWS_CREDENTIALS, (Object)credentials);
    }

    private static IdentityProvider<? extends AwsCredentialsIdentity> resolveCredentialsProvider(SdkRequest originalRequest, IdentityProvider<? extends AwsCredentialsIdentity> defaultProvider) {
        return originalRequest.overrideConfiguration().filter(c -> c instanceof AwsRequestOverrideConfiguration).map(c -> (AwsRequestOverrideConfiguration)((Object)c)).flatMap(AwsRequestOverrideConfiguration::credentialsIdentityProvider).orElse(defaultProvider);
    }

    private static AwsCredentialsIdentity resolveCredentials(IdentityProvider<? extends AwsCredentialsIdentity> credentialsProvider, MetricCollector metricCollector) {
        Validate.notNull(credentialsProvider, (String)"No credentials provider exists to resolve credentials from.", (Object[])new Object[0]);
        Pair measured = MetricUtils.measureDuration(() -> (AwsCredentialsIdentity)CompletableFutureUtils.joinLikeSync((CompletableFuture)credentialsProvider.resolveIdentity()));
        metricCollector.reportMetric(CoreMetric.CREDENTIALS_FETCH_DURATION, measured.right());
        AwsCredentialsIdentity credentials = (AwsCredentialsIdentity)measured.left();
        Validate.validState((credentials != null ? 1 : 0) != 0, (String)"Credential providers must never return null.", (Object[])new Object[0]);
        return credentials;
    }

    public static final class Builder {
        private SdkRequest request;
        private Signer defaultSigner;
        private IdentityProvider<? extends AwsCredentialsIdentity> defaultCredentialsProvider;
        private MetricCollector metricCollector;

        private Builder() {
        }

        public SdkRequest request() {
            return this.request;
        }

        public Builder request(SdkRequest request) {
            this.request = request;
            return this;
        }

        public Signer defaultSigner() {
            return this.defaultSigner;
        }

        public Builder defaultSigner(Signer defaultSigner) {
            this.defaultSigner = defaultSigner;
            return this;
        }

        public IdentityProvider<? extends AwsCredentialsIdentity> defaultCredentialsProvider() {
            return this.defaultCredentialsProvider;
        }

        public Builder defaultCredentialsProvider(IdentityProvider<? extends AwsCredentialsIdentity> defaultCredentialsProvider) {
            this.defaultCredentialsProvider = defaultCredentialsProvider;
            return this;
        }

        public MetricCollector metricCollector() {
            return this.metricCollector;
        }

        public Builder metricCollector(MetricCollector metricCollector) {
            this.metricCollector = metricCollector;
            return this;
        }

        public AwsCredentialsAuthorizationStrategy build() {
            return new AwsCredentialsAuthorizationStrategy(this);
        }
    }
}

