/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.reactivestreams.Publisher
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.http.ContentStreamProvider
 *  software.amazon.awssdk.http.SdkHttpFullRequest
 *  software.amazon.awssdk.http.SdkHttpFullRequest$Builder
 *  software.amazon.awssdk.http.SdkHttpRequest
 *  software.amazon.awssdk.http.SdkHttpRequest$Builder
 *  software.amazon.awssdk.http.auth.spi.scheme.AuthSchemeOption
 *  software.amazon.awssdk.http.auth.spi.signer.AsyncSignRequest
 *  software.amazon.awssdk.http.auth.spi.signer.AsyncSignRequest$Builder
 *  software.amazon.awssdk.http.auth.spi.signer.AsyncSignedRequest
 *  software.amazon.awssdk.http.auth.spi.signer.BaseSignedRequest
 *  software.amazon.awssdk.http.auth.spi.signer.HttpSigner
 *  software.amazon.awssdk.http.auth.spi.signer.SignRequest
 *  software.amazon.awssdk.http.auth.spi.signer.SignRequest$Builder
 *  software.amazon.awssdk.http.auth.spi.signer.SignedRequest
 *  software.amazon.awssdk.identity.spi.Identity
 *  software.amazon.awssdk.metrics.MetricCollector
 *  software.amazon.awssdk.utils.Logger
 */
package software.amazon.awssdk.core.internal.http.pipeline.stages;

import java.nio.ByteBuffer;
import java.time.Clock;
import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import org.reactivestreams.Publisher;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.SelectedAuthScheme;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.core.http.ExecutionContext;
import software.amazon.awssdk.core.interceptor.ExecutionAttributes;
import software.amazon.awssdk.core.interceptor.InterceptorContext;
import software.amazon.awssdk.core.interceptor.SdkExecutionAttribute;
import software.amazon.awssdk.core.interceptor.SdkInternalExecutionAttribute;
import software.amazon.awssdk.core.internal.http.HttpClientDependencies;
import software.amazon.awssdk.core.internal.http.RequestExecutionContext;
import software.amazon.awssdk.core.internal.http.pipeline.RequestPipeline;
import software.amazon.awssdk.core.internal.util.MetricUtils;
import software.amazon.awssdk.core.metrics.CoreMetric;
import software.amazon.awssdk.core.signer.AsyncRequestBodySigner;
import software.amazon.awssdk.core.signer.AsyncSigner;
import software.amazon.awssdk.core.signer.Signer;
import software.amazon.awssdk.http.ContentStreamProvider;
import software.amazon.awssdk.http.SdkHttpFullRequest;
import software.amazon.awssdk.http.SdkHttpRequest;
import software.amazon.awssdk.http.auth.spi.scheme.AuthSchemeOption;
import software.amazon.awssdk.http.auth.spi.signer.AsyncSignRequest;
import software.amazon.awssdk.http.auth.spi.signer.AsyncSignedRequest;
import software.amazon.awssdk.http.auth.spi.signer.BaseSignedRequest;
import software.amazon.awssdk.http.auth.spi.signer.HttpSigner;
import software.amazon.awssdk.http.auth.spi.signer.SignRequest;
import software.amazon.awssdk.http.auth.spi.signer.SignedRequest;
import software.amazon.awssdk.identity.spi.Identity;
import software.amazon.awssdk.metrics.MetricCollector;
import software.amazon.awssdk.utils.Logger;

@SdkInternalApi
public class AsyncSigningStage
implements RequestPipeline<SdkHttpFullRequest, CompletableFuture<SdkHttpFullRequest>> {
    private static final Logger log = Logger.loggerFor(AsyncSigningStage.class);
    private final HttpClientDependencies dependencies;

    public AsyncSigningStage(HttpClientDependencies dependencies) {
        this.dependencies = dependencies;
    }

    @Override
    public CompletableFuture<SdkHttpFullRequest> execute(SdkHttpFullRequest request, RequestExecutionContext context) throws Exception {
        this.updateHttpRequestInInterceptorContext(request, context.executionContext());
        if (context.signer() != null) {
            return this.signRequest(request, context);
        }
        if (context.executionAttributes().getAttribute(SdkInternalExecutionAttribute.AUTH_SCHEMES) != null) {
            SelectedAuthScheme<?> selectedAuthScheme = context.executionAttributes().getAttribute(SdkInternalExecutionAttribute.SELECTED_AUTH_SCHEME);
            log.debug(() -> String.format("Using SelectedAuthScheme: %s", selectedAuthScheme.authSchemeOption().schemeId()));
            return this.sraSignRequest(request, context, selectedAuthScheme);
        }
        return CompletableFuture.completedFuture(request);
    }

    private <T extends Identity> CompletableFuture<SdkHttpFullRequest> sraSignRequest(SdkHttpFullRequest request, RequestExecutionContext context, SelectedAuthScheme<T> selectedAuthScheme) {
        this.adjustForClockSkew(context.executionAttributes());
        CompletableFuture<T> identityFuture = selectedAuthScheme.identity();
        return identityFuture.thenCompose(identity -> {
            CompletableFuture signedRequestFuture = MetricUtils.reportDuration(() -> this.doSraSign(request, context, selectedAuthScheme, identity), context.attemptMetricCollector(), CoreMetric.SIGNING_DURATION);
            return signedRequestFuture.thenApply(r -> {
                this.updateHttpRequestInInterceptorContext((SdkHttpFullRequest)r, context.executionContext());
                return r;
            });
        });
    }

    private <T extends Identity> CompletableFuture<SdkHttpFullRequest> doSraSign(SdkHttpFullRequest request, RequestExecutionContext context, SelectedAuthScheme<T> selectedAuthScheme, T identity) {
        AuthSchemeOption authSchemeOption = selectedAuthScheme.authSchemeOption();
        HttpSigner<T> signer = selectedAuthScheme.signer();
        if (context.requestProvider() == null) {
            SignRequest.Builder signRequestBuilder = (SignRequest.Builder)((SignRequest.Builder)((SignRequest.Builder)SignRequest.builder(identity).putProperty(HttpSigner.SIGNING_CLOCK, (Object)this.signingClock())).request((SdkHttpRequest)request)).payload(request.contentStreamProvider().orElse(null));
            authSchemeOption.forEachSignerProperty((arg_0, arg_1) -> ((SignRequest.Builder)signRequestBuilder).putProperty(arg_0, arg_1));
            SignedRequest signedRequest2 = signer.sign((SignRequest)signRequestBuilder.build());
            return CompletableFuture.completedFuture(this.toSdkHttpFullRequest(signedRequest2));
        }
        AsyncSignRequest.Builder signRequestBuilder = (AsyncSignRequest.Builder)((AsyncSignRequest.Builder)((AsyncSignRequest.Builder)AsyncSignRequest.builder(identity).putProperty(HttpSigner.SIGNING_CLOCK, (Object)this.signingClock())).request((SdkHttpRequest)request)).payload((Object)context.requestProvider());
        authSchemeOption.forEachSignerProperty((arg_0, arg_1) -> ((AsyncSignRequest.Builder)signRequestBuilder).putProperty(arg_0, arg_1));
        CompletableFuture signedRequestFuture = signer.signAsync((AsyncSignRequest)signRequestBuilder.build());
        return signedRequestFuture.thenCompose(signedRequest -> {
            SdkHttpFullRequest result = this.toSdkHttpFullRequest((AsyncSignedRequest)signedRequest);
            AsyncSigningStage.updateAsyncRequestBodyInContexts(context, signedRequest);
            return CompletableFuture.completedFuture(result);
        });
    }

    private static void updateAsyncRequestBodyInContexts(RequestExecutionContext context, AsyncSignedRequest signedRequest) {
        Publisher signedPayload;
        Optional optionalPayload = signedRequest.payload();
        AsyncRequestBody newAsyncRequestBody = optionalPayload.isPresent() ? ((signedPayload = (Publisher)optionalPayload.get()) instanceof AsyncRequestBody ? (AsyncRequestBody)signedPayload : AsyncRequestBody.fromPublisher((Publisher<ByteBuffer>)signedPayload)) : null;
        context.requestProvider(newAsyncRequestBody);
        ExecutionContext executionContext = context.executionContext();
        executionContext.interceptorContext((InterceptorContext)executionContext.interceptorContext().copy(b -> b.asyncRequestBody(newAsyncRequestBody)));
    }

    private SdkHttpFullRequest toSdkHttpFullRequest(SignedRequest signedRequest) {
        return this.toSdkHttpFullRequestBuilder((BaseSignedRequest<?>)signedRequest).contentStreamProvider((ContentStreamProvider)signedRequest.payload().orElse(null)).build();
    }

    private SdkHttpFullRequest toSdkHttpFullRequest(AsyncSignedRequest signedRequest) {
        SdkHttpRequest request = signedRequest.request();
        if (request instanceof SdkHttpFullRequest) {
            return (SdkHttpFullRequest)request;
        }
        return this.toSdkHttpFullRequestBuilder((BaseSignedRequest<?>)signedRequest).build();
    }

    private SdkHttpFullRequest.Builder toSdkHttpFullRequestBuilder(BaseSignedRequest<?> baseSignedRequest) {
        SdkHttpRequest request = baseSignedRequest.request();
        return SdkHttpFullRequest.builder().protocol(request.protocol()).method(request.method()).host(request.host()).port(Integer.valueOf(request.port())).encodedPath(request.encodedPath()).applyMutation(r -> request.forEachHeader((arg_0, arg_1) -> ((SdkHttpRequest.Builder)r).putHeader(arg_0, arg_1))).applyMutation(r -> request.forEachRawQueryParameter((arg_0, arg_1) -> ((SdkHttpRequest.Builder)r).putRawQueryParameter(arg_0, arg_1)));
    }

    private CompletableFuture<SdkHttpFullRequest> signRequest(SdkHttpFullRequest request, RequestExecutionContext context) {
        Signer signer = context.signer();
        MetricCollector metricCollector = context.attemptMetricCollector();
        this.adjustForClockSkew(context.executionAttributes());
        AsyncSigner asyncSigner = this.asAsyncSigner(signer, context);
        long signingStart = System.nanoTime();
        CompletableFuture<SdkHttpFullRequest> signedRequestFuture = asyncSigner.sign(request, context.requestProvider(), context.executionAttributes());
        signedRequestFuture.whenComplete((r, t) -> metricCollector.reportMetric(CoreMetric.SIGNING_DURATION, (Object)Duration.ofNanos(System.nanoTime() - signingStart)));
        return signedRequestFuture.thenApply(r -> {
            this.updateHttpRequestInInterceptorContext((SdkHttpFullRequest)r, context.executionContext());
            return r;
        });
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

    private AsyncSigner asAsyncSigner(Signer signer, RequestExecutionContext context) {
        if (signer instanceof AsyncSigner) {
            return (AsyncSigner)((Object)signer);
        }
        return (request, requestBody, executionAttributes) -> {
            SdkHttpFullRequest signedRequest = signer.sign(request, executionAttributes);
            if (signer instanceof AsyncRequestBodySigner) {
                AsyncRequestBody transformedRequestProvider = ((AsyncRequestBodySigner)((Object)signer)).signAsyncRequestBody(signedRequest, context.requestProvider(), context.executionAttributes());
                context.requestProvider(transformedRequestProvider);
            }
            return CompletableFuture.completedFuture(signedRequest);
        };
    }
}

