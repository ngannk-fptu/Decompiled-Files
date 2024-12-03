/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.http.ContentStreamProvider
 *  software.amazon.awssdk.http.SdkHttpFullRequest
 *  software.amazon.awssdk.http.SdkHttpRequest
 *  software.amazon.awssdk.http.SdkHttpRequest$Builder
 *  software.amazon.awssdk.http.auth.spi.scheme.AuthSchemeOption
 *  software.amazon.awssdk.http.auth.spi.signer.HttpSigner
 *  software.amazon.awssdk.http.auth.spi.signer.SignRequest
 *  software.amazon.awssdk.http.auth.spi.signer.SignRequest$Builder
 *  software.amazon.awssdk.http.auth.spi.signer.SignedRequest
 *  software.amazon.awssdk.identity.spi.Identity
 *  software.amazon.awssdk.metrics.MetricCollector
 *  software.amazon.awssdk.utils.CompletableFutureUtils
 *  software.amazon.awssdk.utils.Logger
 *  software.amazon.awssdk.utils.Pair
 */
package software.amazon.awssdk.core.internal.http.pipeline.stages;

import java.time.Clock;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.SelectedAuthScheme;
import software.amazon.awssdk.core.http.ExecutionContext;
import software.amazon.awssdk.core.interceptor.ExecutionAttributes;
import software.amazon.awssdk.core.interceptor.InterceptorContext;
import software.amazon.awssdk.core.interceptor.SdkExecutionAttribute;
import software.amazon.awssdk.core.interceptor.SdkInternalExecutionAttribute;
import software.amazon.awssdk.core.internal.http.HttpClientDependencies;
import software.amazon.awssdk.core.internal.http.InterruptMonitor;
import software.amazon.awssdk.core.internal.http.RequestExecutionContext;
import software.amazon.awssdk.core.internal.http.pipeline.RequestToRequestPipeline;
import software.amazon.awssdk.core.internal.util.MetricUtils;
import software.amazon.awssdk.core.metrics.CoreMetric;
import software.amazon.awssdk.core.signer.Signer;
import software.amazon.awssdk.http.ContentStreamProvider;
import software.amazon.awssdk.http.SdkHttpFullRequest;
import software.amazon.awssdk.http.SdkHttpRequest;
import software.amazon.awssdk.http.auth.spi.scheme.AuthSchemeOption;
import software.amazon.awssdk.http.auth.spi.signer.HttpSigner;
import software.amazon.awssdk.http.auth.spi.signer.SignRequest;
import software.amazon.awssdk.http.auth.spi.signer.SignedRequest;
import software.amazon.awssdk.identity.spi.Identity;
import software.amazon.awssdk.metrics.MetricCollector;
import software.amazon.awssdk.utils.CompletableFutureUtils;
import software.amazon.awssdk.utils.Logger;
import software.amazon.awssdk.utils.Pair;

@SdkInternalApi
public class SigningStage
implements RequestToRequestPipeline {
    private static final Logger log = Logger.loggerFor(SigningStage.class);
    private final HttpClientDependencies dependencies;

    public SigningStage(HttpClientDependencies dependencies) {
        this.dependencies = dependencies;
    }

    @Override
    public SdkHttpFullRequest execute(SdkHttpFullRequest request, RequestExecutionContext context) throws Exception {
        InterruptMonitor.checkInterrupted();
        this.updateHttpRequestInInterceptorContext(request, context.executionContext());
        if (context.signer() != null) {
            return this.signRequest(request, context);
        }
        if (context.executionAttributes().getAttribute(SdkInternalExecutionAttribute.AUTH_SCHEMES) != null) {
            SelectedAuthScheme<?> selectedAuthScheme = context.executionAttributes().getAttribute(SdkInternalExecutionAttribute.SELECTED_AUTH_SCHEME);
            log.debug(() -> String.format("Using SelectedAuthScheme: %s", selectedAuthScheme.authSchemeOption().schemeId()));
            return this.sraSignRequest(request, context, selectedAuthScheme);
        }
        return request;
    }

    private <T extends Identity> SdkHttpFullRequest sraSignRequest(SdkHttpFullRequest request, RequestExecutionContext context, SelectedAuthScheme<T> selectedAuthScheme) {
        this.adjustForClockSkew(context.executionAttributes());
        CompletableFuture<T> identityFuture = selectedAuthScheme.identity();
        Identity identity = (Identity)CompletableFutureUtils.joinLikeSync(identityFuture);
        Pair<SdkHttpFullRequest, Duration> measuredSign = MetricUtils.measureDuration(() -> this.doSraSign(request, selectedAuthScheme, identity));
        context.attemptMetricCollector().reportMetric(CoreMetric.SIGNING_DURATION, measuredSign.right());
        SdkHttpFullRequest signedRequest = (SdkHttpFullRequest)measuredSign.left();
        this.updateHttpRequestInInterceptorContext(signedRequest, context.executionContext());
        return signedRequest;
    }

    private <T extends Identity> SdkHttpFullRequest doSraSign(SdkHttpFullRequest request, SelectedAuthScheme<T> selectedAuthScheme, T identity) {
        SignRequest.Builder signRequestBuilder = (SignRequest.Builder)((SignRequest.Builder)((SignRequest.Builder)SignRequest.builder(identity).putProperty(HttpSigner.SIGNING_CLOCK, (Object)this.signingClock())).request((SdkHttpRequest)request)).payload(request.contentStreamProvider().orElse(null));
        AuthSchemeOption authSchemeOption = selectedAuthScheme.authSchemeOption();
        authSchemeOption.forEachSignerProperty((arg_0, arg_1) -> ((SignRequest.Builder)signRequestBuilder).putProperty(arg_0, arg_1));
        HttpSigner<T> signer = selectedAuthScheme.signer();
        SignedRequest signedRequest = signer.sign((SignRequest)signRequestBuilder.build());
        return this.toSdkHttpFullRequest(signedRequest);
    }

    private SdkHttpFullRequest toSdkHttpFullRequest(SignedRequest signedRequest) {
        SdkHttpRequest request = signedRequest.request();
        return SdkHttpFullRequest.builder().contentStreamProvider((ContentStreamProvider)signedRequest.payload().orElse(null)).protocol(request.protocol()).method(request.method()).host(request.host()).port(Integer.valueOf(request.port())).encodedPath(request.encodedPath()).applyMutation(r -> request.forEachHeader((arg_0, arg_1) -> ((SdkHttpRequest.Builder)r).putHeader(arg_0, arg_1))).applyMutation(r -> request.forEachRawQueryParameter((arg_0, arg_1) -> ((SdkHttpRequest.Builder)r).putRawQueryParameter(arg_0, arg_1))).build();
    }

    private SdkHttpFullRequest signRequest(SdkHttpFullRequest request, RequestExecutionContext context) {
        Signer signer = context.signer();
        MetricCollector metricCollector = context.attemptMetricCollector();
        this.adjustForClockSkew(context.executionAttributes());
        Pair<SdkHttpFullRequest, Duration> measuredSign = MetricUtils.measureDuration(() -> signer.sign(request, context.executionAttributes()));
        metricCollector.reportMetric(CoreMetric.SIGNING_DURATION, measuredSign.right());
        SdkHttpFullRequest signedRequest = (SdkHttpFullRequest)measuredSign.left();
        this.updateHttpRequestInInterceptorContext(signedRequest, context.executionContext());
        return signedRequest;
    }

    private void updateHttpRequestInInterceptorContext(SdkHttpFullRequest request, ExecutionContext executionContext) {
        executionContext.interceptorContext((InterceptorContext)executionContext.interceptorContext().copy(b -> b.httpRequest((SdkHttpRequest)request)));
    }

    private Clock signingClock() {
        int offsetInSeconds = this.dependencies.timeOffset();
        return Clock.offset(Clock.systemUTC(), Duration.ofSeconds(-offsetInSeconds));
    }

    private void adjustForClockSkew(ExecutionAttributes attributes) {
        attributes.putAttribute(SdkExecutionAttribute.TIME_OFFSET, this.dependencies.timeOffset());
    }
}

