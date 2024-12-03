/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.annotations.SdkTestInternalApi
 *  software.amazon.awssdk.core.async.listener.PublisherListener
 *  software.amazon.awssdk.core.interceptor.trait.HttpChecksum
 *  software.amazon.awssdk.crt.auth.credentials.CredentialsProvider
 *  software.amazon.awssdk.crt.auth.signing.AwsSigningConfig
 *  software.amazon.awssdk.crt.http.HttpHeader
 *  software.amazon.awssdk.crt.http.HttpProxyEnvironmentVariableSetting
 *  software.amazon.awssdk.crt.http.HttpProxyEnvironmentVariableSetting$HttpProxyEnvironmentVariableType
 *  software.amazon.awssdk.crt.http.HttpRequest
 *  software.amazon.awssdk.crt.http.HttpRequestBodyStream
 *  software.amazon.awssdk.crt.s3.ChecksumConfig
 *  software.amazon.awssdk.crt.s3.ResumeToken
 *  software.amazon.awssdk.crt.s3.S3Client
 *  software.amazon.awssdk.crt.s3.S3ClientOptions
 *  software.amazon.awssdk.crt.s3.S3MetaRequest
 *  software.amazon.awssdk.crt.s3.S3MetaRequestOptions
 *  software.amazon.awssdk.crt.s3.S3MetaRequestOptions$MetaRequestType
 *  software.amazon.awssdk.crt.s3.S3MetaRequestProgress
 *  software.amazon.awssdk.crt.s3.S3MetaRequestResponseHandler
 *  software.amazon.awssdk.http.SdkHttpRequest
 *  software.amazon.awssdk.http.async.AsyncExecuteRequest
 *  software.amazon.awssdk.http.async.SdkAsyncHttpClient
 *  software.amazon.awssdk.http.async.SdkAsyncHttpClient$Builder
 *  software.amazon.awssdk.regions.Region
 *  software.amazon.awssdk.utils.AttributeMap
 *  software.amazon.awssdk.utils.FunctionalUtils
 *  software.amazon.awssdk.utils.Logger
 *  software.amazon.awssdk.utils.NumericUtils
 *  software.amazon.awssdk.utils.http.SdkHttpUtils
 */
package software.amazon.awssdk.services.s3.internal.crt;

import java.net.URI;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.annotations.SdkTestInternalApi;
import software.amazon.awssdk.core.async.listener.PublisherListener;
import software.amazon.awssdk.core.interceptor.trait.HttpChecksum;
import software.amazon.awssdk.crt.auth.credentials.CredentialsProvider;
import software.amazon.awssdk.crt.auth.signing.AwsSigningConfig;
import software.amazon.awssdk.crt.http.HttpHeader;
import software.amazon.awssdk.crt.http.HttpProxyEnvironmentVariableSetting;
import software.amazon.awssdk.crt.http.HttpRequest;
import software.amazon.awssdk.crt.http.HttpRequestBodyStream;
import software.amazon.awssdk.crt.s3.ChecksumConfig;
import software.amazon.awssdk.crt.s3.ResumeToken;
import software.amazon.awssdk.crt.s3.S3Client;
import software.amazon.awssdk.crt.s3.S3ClientOptions;
import software.amazon.awssdk.crt.s3.S3MetaRequest;
import software.amazon.awssdk.crt.s3.S3MetaRequestOptions;
import software.amazon.awssdk.crt.s3.S3MetaRequestProgress;
import software.amazon.awssdk.crt.s3.S3MetaRequestResponseHandler;
import software.amazon.awssdk.http.SdkHttpRequest;
import software.amazon.awssdk.http.async.AsyncExecuteRequest;
import software.amazon.awssdk.http.async.SdkAsyncHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.crt.S3CrtSdkHttpExecutionAttribute;
import software.amazon.awssdk.services.s3.internal.crt.CrtChecksumUtils;
import software.amazon.awssdk.services.s3.internal.crt.S3CrtRequestBodyStreamAdapter;
import software.amazon.awssdk.services.s3.internal.crt.S3CrtResponseHandlerAdapter;
import software.amazon.awssdk.services.s3.internal.crt.S3InternalSdkHttpExecutionAttribute;
import software.amazon.awssdk.services.s3.internal.crt.S3MetaRequestPauseObservable;
import software.amazon.awssdk.services.s3.internal.crt.S3NativeClientConfiguration;
import software.amazon.awssdk.utils.AttributeMap;
import software.amazon.awssdk.utils.FunctionalUtils;
import software.amazon.awssdk.utils.Logger;
import software.amazon.awssdk.utils.NumericUtils;
import software.amazon.awssdk.utils.http.SdkHttpUtils;

@SdkInternalApi
public final class S3CrtAsyncHttpClient
implements SdkAsyncHttpClient {
    private static final Logger log = Logger.loggerFor(S3CrtAsyncHttpClient.class);
    private final S3Client crtS3Client;
    private final S3NativeClientConfiguration s3NativeClientConfiguration;
    private final S3ClientOptions s3ClientOptions;

    private S3CrtAsyncHttpClient(Builder builder) {
        this.s3NativeClientConfiguration = builder.clientConfiguration;
        Long initialWindowSize = this.s3NativeClientConfiguration.readBufferSizeInBytes();
        this.s3ClientOptions = new S3ClientOptions().withRegion(this.s3NativeClientConfiguration.signingRegion()).withEndpoint(this.s3NativeClientConfiguration.endpointOverride() == null ? null : this.s3NativeClientConfiguration.endpointOverride().toString()).withCredentialsProvider(this.s3NativeClientConfiguration.credentialsProvider()).withClientBootstrap(this.s3NativeClientConfiguration.clientBootstrap()).withTlsContext(this.s3NativeClientConfiguration.tlsContext()).withPartSize(this.s3NativeClientConfiguration.partSizeBytes()).withMultipartUploadThreshold(this.s3NativeClientConfiguration.thresholdInBytes()).withComputeContentMd5(Boolean.valueOf(false)).withMaxConnections(this.s3NativeClientConfiguration.maxConcurrency()).withThroughputTargetGbps(this.s3NativeClientConfiguration.targetThroughputInGbps()).withInitialReadWindowSize(initialWindowSize.longValue()).withReadBackpressureEnabled(true);
        if (this.s3NativeClientConfiguration.standardRetryOptions() != null) {
            this.s3ClientOptions.withStandardRetryOptions(this.s3NativeClientConfiguration.standardRetryOptions());
        }
        if (Boolean.FALSE.equals(this.s3NativeClientConfiguration.isUseEnvironmentVariableValues())) {
            this.s3ClientOptions.withProxyEnvironmentVariableSetting(S3CrtAsyncHttpClient.disabledHttpProxyEnvironmentVariableSetting());
        }
        Optional.ofNullable(this.s3NativeClientConfiguration.proxyOptions()).ifPresent(arg_0 -> ((S3ClientOptions)this.s3ClientOptions).withProxyOptions(arg_0));
        Optional.ofNullable(this.s3NativeClientConfiguration.connectionTimeout()).map(Duration::toMillis).map(NumericUtils::saturatedCast).ifPresent(arg_0 -> ((S3ClientOptions)this.s3ClientOptions).withConnectTimeoutMs(arg_0));
        Optional.ofNullable(this.s3NativeClientConfiguration.httpMonitoringOptions()).ifPresent(arg_0 -> ((S3ClientOptions)this.s3ClientOptions).withHttpMonitoringOptions(arg_0));
        this.crtS3Client = new S3Client(this.s3ClientOptions);
    }

    @SdkTestInternalApi
    S3CrtAsyncHttpClient(S3Client crtS3Client, S3NativeClientConfiguration nativeClientConfiguration) {
        this.crtS3Client = crtS3Client;
        this.s3NativeClientConfiguration = nativeClientConfiguration;
        this.s3ClientOptions = null;
    }

    @SdkTestInternalApi
    public S3ClientOptions s3ClientOptions() {
        return this.s3ClientOptions;
    }

    public CompletableFuture<Void> execute(AsyncExecuteRequest asyncRequest) {
        CompletableFuture<Void> executeFuture = new CompletableFuture<Void>();
        URI uri = asyncRequest.request().getUri();
        HttpRequest httpRequest = S3CrtAsyncHttpClient.toCrtRequest(asyncRequest);
        S3CrtResponseHandlerAdapter responseHandler = new S3CrtResponseHandlerAdapter(executeFuture, asyncRequest.responseHandler(), (PublisherListener<S3MetaRequestProgress>)((PublisherListener)asyncRequest.httpExecutionAttributes().getAttribute(S3CrtSdkHttpExecutionAttribute.CRT_PROGRESS_LISTENER)));
        S3MetaRequestOptions.MetaRequestType requestType = S3CrtAsyncHttpClient.requestType(asyncRequest);
        HttpChecksum httpChecksum = (HttpChecksum)asyncRequest.httpExecutionAttributes().getAttribute(S3InternalSdkHttpExecutionAttribute.HTTP_CHECKSUM);
        ResumeToken resumeToken = (ResumeToken)asyncRequest.httpExecutionAttributes().getAttribute(S3InternalSdkHttpExecutionAttribute.CRT_PAUSE_RESUME_TOKEN);
        Region signingRegion = (Region)asyncRequest.httpExecutionAttributes().getAttribute(S3InternalSdkHttpExecutionAttribute.SIGNING_REGION);
        Path requestFilePath = (Path)asyncRequest.httpExecutionAttributes().getAttribute(S3InternalSdkHttpExecutionAttribute.OBJECT_FILE_PATH);
        ChecksumConfig checksumConfig = CrtChecksumUtils.checksumConfig(httpChecksum, requestType, this.s3NativeClientConfiguration.checksumValidationEnabled());
        URI endpoint = S3CrtAsyncHttpClient.getEndpoint(uri);
        S3MetaRequestOptions requestOptions = new S3MetaRequestOptions().withHttpRequest(httpRequest).withMetaRequestType(requestType).withChecksumConfig(checksumConfig).withEndpoint(endpoint).withResponseHandler((S3MetaRequestResponseHandler)responseHandler).withResumeToken(resumeToken).withRequestFilePath(requestFilePath);
        if (signingRegion != null && !this.s3ClientOptions.getRegion().equals(signingRegion.id())) {
            requestOptions.withSigningConfig(AwsSigningConfig.getDefaultS3SigningConfig((String)signingRegion.id(), (CredentialsProvider)this.s3ClientOptions.getCredentialsProvider()));
        }
        S3MetaRequest s3MetaRequest = this.crtS3Client.makeMetaRequest(requestOptions);
        S3MetaRequestPauseObservable observable = (S3MetaRequestPauseObservable)asyncRequest.httpExecutionAttributes().getAttribute(S3CrtSdkHttpExecutionAttribute.METAREQUEST_PAUSE_OBSERVABLE);
        responseHandler.metaRequest(s3MetaRequest);
        if (observable != null) {
            observable.subscribe(s3MetaRequest);
        }
        S3CrtAsyncHttpClient.addCancelCallback(executeFuture, s3MetaRequest, responseHandler);
        return executeFuture;
    }

    private static URI getEndpoint(URI uri) {
        return (URI)FunctionalUtils.invokeSafely(() -> new URI(uri.getScheme(), null, uri.getHost(), uri.getPort(), null, null, null));
    }

    public String clientName() {
        return "s3crt";
    }

    private static S3MetaRequestOptions.MetaRequestType requestType(AsyncExecuteRequest asyncRequest) {
        String operationName = (String)asyncRequest.httpExecutionAttributes().getAttribute(S3InternalSdkHttpExecutionAttribute.OPERATION_NAME);
        if (operationName != null) {
            switch (operationName) {
                case "GetObject": {
                    return S3MetaRequestOptions.MetaRequestType.GET_OBJECT;
                }
                case "PutObject": {
                    return S3MetaRequestOptions.MetaRequestType.PUT_OBJECT;
                }
            }
            return S3MetaRequestOptions.MetaRequestType.DEFAULT;
        }
        return S3MetaRequestOptions.MetaRequestType.DEFAULT;
    }

    private static void addCancelCallback(CompletableFuture<Void> executeFuture, S3MetaRequest s3MetaRequest, S3CrtResponseHandlerAdapter responseHandler) {
        executeFuture.whenComplete((r, t) -> {
            if (executeFuture.isCancelled()) {
                log.debug(() -> "The request is cancelled, cancelling meta request");
                responseHandler.cancelRequest();
                s3MetaRequest.cancel();
            }
        });
    }

    private static HttpRequest toCrtRequest(AsyncExecuteRequest asyncRequest) {
        SdkHttpRequest sdkRequest = asyncRequest.request();
        Path requestFilePath = (Path)asyncRequest.httpExecutionAttributes().getAttribute(S3InternalSdkHttpExecutionAttribute.OBJECT_FILE_PATH);
        String method = sdkRequest.method().name();
        String encodedPath = sdkRequest.encodedPath();
        if (encodedPath == null || encodedPath.isEmpty()) {
            encodedPath = "/";
        }
        String encodedQueryString = sdkRequest.encodedQueryParameters().map(value -> "?" + value).orElse("");
        HttpHeader[] crtHeaderArray = S3CrtAsyncHttpClient.createHttpHeaderList(asyncRequest).toArray(new HttpHeader[0]);
        S3CrtRequestBodyStreamAdapter sdkToCrtRequestPublisher = requestFilePath == null ? new S3CrtRequestBodyStreamAdapter(asyncRequest.requestContentPublisher()) : null;
        return new HttpRequest(method, encodedPath + encodedQueryString, crtHeaderArray, (HttpRequestBodyStream)sdkToCrtRequestPublisher);
    }

    public void close() {
        this.s3NativeClientConfiguration.close();
        this.crtS3Client.close();
    }

    public static Builder builder() {
        return new Builder();
    }

    private static List<HttpHeader> createHttpHeaderList(AsyncExecuteRequest asyncRequest) {
        SdkHttpRequest sdkRequest = asyncRequest.request();
        ArrayList<HttpHeader> crtHeaderList = new ArrayList<HttpHeader>();
        if (!sdkRequest.firstMatchingHeader("Host").isPresent()) {
            String hostHeader = S3CrtAsyncHttpClient.getHostHeaderValue(asyncRequest.request());
            crtHeaderList.add(new HttpHeader("Host", hostHeader));
        }
        Optional contentLength = asyncRequest.requestContentPublisher().contentLength();
        if (!sdkRequest.firstMatchingHeader("Content-Length").isPresent() && contentLength.isPresent()) {
            crtHeaderList.add(new HttpHeader("Content-Length", Long.toString((Long)contentLength.get())));
        }
        sdkRequest.forEachHeader((key, value) -> value.stream().map(val -> new HttpHeader(key, val)).forEach(crtHeaderList::add));
        return crtHeaderList;
    }

    private static String getHostHeaderValue(SdkHttpRequest request) {
        return SdkHttpUtils.isUsingStandardPort((String)request.protocol(), (Integer)request.port()) ? request.host() : request.host() + ":" + request.port();
    }

    private static HttpProxyEnvironmentVariableSetting disabledHttpProxyEnvironmentVariableSetting() {
        HttpProxyEnvironmentVariableSetting proxyEnvSetting = new HttpProxyEnvironmentVariableSetting();
        proxyEnvSetting.setEnvironmentVariableType(HttpProxyEnvironmentVariableSetting.HttpProxyEnvironmentVariableType.DISABLED);
        return proxyEnvSetting;
    }

    public static final class Builder
    implements SdkAsyncHttpClient.Builder<Builder> {
        private S3NativeClientConfiguration clientConfiguration;

        public Builder s3ClientConfiguration(S3NativeClientConfiguration clientConfiguration) {
            this.clientConfiguration = clientConfiguration;
            return this;
        }

        public SdkAsyncHttpClient build() {
            return new S3CrtAsyncHttpClient(this);
        }

        public SdkAsyncHttpClient buildWithDefaults(AttributeMap serviceDefaults) {
            return this.build();
        }
    }
}

