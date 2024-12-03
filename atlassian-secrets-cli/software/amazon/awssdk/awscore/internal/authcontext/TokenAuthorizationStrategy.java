/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.awscore.internal.authcontext;

import java.time.Duration;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.auth.token.credentials.SdkToken;
import software.amazon.awssdk.auth.token.credentials.internal.TokenUtils;
import software.amazon.awssdk.auth.token.signer.SdkTokenExecutionAttribute;
import software.amazon.awssdk.awscore.internal.authcontext.AuthorizationStrategy;
import software.amazon.awssdk.core.RequestOverrideConfiguration;
import software.amazon.awssdk.core.SdkRequest;
import software.amazon.awssdk.core.interceptor.ExecutionAttributes;
import software.amazon.awssdk.core.internal.util.MetricUtils;
import software.amazon.awssdk.core.metrics.CoreMetric;
import software.amazon.awssdk.core.signer.Signer;
import software.amazon.awssdk.identity.spi.IdentityProvider;
import software.amazon.awssdk.identity.spi.TokenIdentity;
import software.amazon.awssdk.metrics.MetricCollector;
import software.amazon.awssdk.utils.CompletableFutureUtils;
import software.amazon.awssdk.utils.Pair;
import software.amazon.awssdk.utils.Validate;

@Deprecated
@SdkInternalApi
public final class TokenAuthorizationStrategy
implements AuthorizationStrategy {
    private final SdkRequest request;
    private final Signer defaultSigner;
    private final IdentityProvider<? extends TokenIdentity> defaultTokenProvider;
    private final MetricCollector metricCollector;

    public TokenAuthorizationStrategy(Builder builder) {
        this.request = builder.request();
        this.defaultSigner = builder.defaultSigner();
        this.defaultTokenProvider = builder.defaultTokenProvider();
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
        SdkToken token = TokenUtils.toSdkToken(TokenAuthorizationStrategy.resolveToken(this.defaultTokenProvider, this.metricCollector));
        executionAttributes.putAttribute(SdkTokenExecutionAttribute.SDK_TOKEN, token);
    }

    private static TokenIdentity resolveToken(IdentityProvider<? extends TokenIdentity> tokenProvider, MetricCollector metricCollector) {
        Validate.notNull(tokenProvider, "No token provider exists to resolve a token from.", new Object[0]);
        Pair<TokenIdentity, Duration> measured = MetricUtils.measureDuration(() -> (TokenIdentity)CompletableFutureUtils.joinLikeSync(tokenProvider.resolveIdentity()));
        metricCollector.reportMetric(CoreMetric.TOKEN_FETCH_DURATION, measured.right());
        TokenIdentity token = measured.left();
        Validate.validState(token != null, "Token providers must never return null.", new Object[0]);
        return token;
    }

    public static final class Builder {
        private SdkRequest request;
        private Signer defaultSigner;
        private IdentityProvider<? extends TokenIdentity> defaultTokenProvider;
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

        public IdentityProvider<? extends TokenIdentity> defaultTokenProvider() {
            return this.defaultTokenProvider;
        }

        public Builder defaultTokenProvider(IdentityProvider<? extends TokenIdentity> defaultTokenProvider) {
            this.defaultTokenProvider = defaultTokenProvider;
            return this;
        }

        public MetricCollector metricCollector() {
            return this.metricCollector;
        }

        public Builder metricCollector(MetricCollector metricCollector) {
            this.metricCollector = metricCollector;
            return this;
        }

        public TokenAuthorizationStrategy build() {
            return new TokenAuthorizationStrategy(this);
        }
    }
}

