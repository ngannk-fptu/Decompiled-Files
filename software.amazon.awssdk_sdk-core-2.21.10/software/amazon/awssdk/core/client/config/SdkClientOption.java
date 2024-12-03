/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkProtectedApi
 *  software.amazon.awssdk.endpoints.EndpointProvider
 *  software.amazon.awssdk.http.SdkHttpClient
 *  software.amazon.awssdk.http.async.SdkAsyncHttpClient
 *  software.amazon.awssdk.http.auth.spi.scheme.AuthScheme
 *  software.amazon.awssdk.http.auth.spi.scheme.AuthSchemeProvider
 *  software.amazon.awssdk.identity.spi.IdentityProviders
 *  software.amazon.awssdk.metrics.MetricPublisher
 *  software.amazon.awssdk.profiles.ProfileFile
 *  software.amazon.awssdk.utils.AttributeMap
 *  software.amazon.awssdk.utils.AttributeMap$Key$UnsafeValueType
 */
package software.amazon.awssdk.core.client.config;

import java.net.URI;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Supplier;
import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.core.ClientType;
import software.amazon.awssdk.core.CompressionConfiguration;
import software.amazon.awssdk.core.ServiceConfiguration;
import software.amazon.awssdk.core.client.config.ClientOption;
import software.amazon.awssdk.core.interceptor.ExecutionAttributes;
import software.amazon.awssdk.core.interceptor.ExecutionInterceptor;
import software.amazon.awssdk.core.retry.RetryMode;
import software.amazon.awssdk.core.retry.RetryPolicy;
import software.amazon.awssdk.endpoints.EndpointProvider;
import software.amazon.awssdk.http.SdkHttpClient;
import software.amazon.awssdk.http.async.SdkAsyncHttpClient;
import software.amazon.awssdk.http.auth.spi.scheme.AuthScheme;
import software.amazon.awssdk.http.auth.spi.scheme.AuthSchemeProvider;
import software.amazon.awssdk.identity.spi.IdentityProviders;
import software.amazon.awssdk.metrics.MetricPublisher;
import software.amazon.awssdk.profiles.ProfileFile;
import software.amazon.awssdk.utils.AttributeMap;

@SdkProtectedApi
public final class SdkClientOption<T>
extends ClientOption<T> {
    public static final SdkClientOption<Map<String, List<String>>> ADDITIONAL_HTTP_HEADERS = new SdkClientOption(new AttributeMap.Key.UnsafeValueType(Map.class));
    public static final SdkClientOption<RetryPolicy> RETRY_POLICY = new SdkClientOption<RetryPolicy>(RetryPolicy.class);
    public static final SdkClientOption<List<ExecutionInterceptor>> EXECUTION_INTERCEPTORS = new SdkClientOption(new AttributeMap.Key.UnsafeValueType(List.class));
    public static final SdkClientOption<URI> ENDPOINT = new SdkClientOption<URI>(URI.class);
    public static final SdkClientOption<Boolean> ENDPOINT_OVERRIDDEN = new SdkClientOption<Boolean>(Boolean.class);
    public static final SdkClientOption<ServiceConfiguration> SERVICE_CONFIGURATION = new SdkClientOption<ServiceConfiguration>(ServiceConfiguration.class);
    public static final SdkClientOption<Boolean> CRC32_FROM_COMPRESSED_DATA_ENABLED = new SdkClientOption<Boolean>(Boolean.class);
    public static final SdkClientOption<ScheduledExecutorService> SCHEDULED_EXECUTOR_SERVICE = new SdkClientOption<ScheduledExecutorService>(ScheduledExecutorService.class);
    public static final SdkClientOption<SdkAsyncHttpClient> ASYNC_HTTP_CLIENT = new SdkClientOption<SdkAsyncHttpClient>(SdkAsyncHttpClient.class);
    public static final SdkClientOption<SdkHttpClient> SYNC_HTTP_CLIENT = new SdkClientOption<SdkHttpClient>(SdkHttpClient.class);
    public static final SdkClientOption<ClientType> CLIENT_TYPE = new SdkClientOption<ClientType>(ClientType.class);
    public static final SdkClientOption<Duration> API_CALL_ATTEMPT_TIMEOUT = new SdkClientOption<Duration>(Duration.class);
    public static final SdkClientOption<Duration> API_CALL_TIMEOUT = new SdkClientOption<Duration>(Duration.class);
    public static final SdkClientOption<String> SERVICE_NAME = new SdkClientOption<String>(String.class);
    public static final SdkClientOption<Boolean> ENDPOINT_DISCOVERY_ENABLED = new SdkClientOption<Boolean>(Boolean.class);
    @Deprecated
    public static final SdkClientOption<ProfileFile> PROFILE_FILE = new SdkClientOption<ProfileFile>(ProfileFile.class);
    public static final SdkClientOption<Supplier<ProfileFile>> PROFILE_FILE_SUPPLIER = new SdkClientOption(new AttributeMap.Key.UnsafeValueType(Supplier.class));
    public static final SdkClientOption<String> PROFILE_NAME = new SdkClientOption<String>(String.class);
    public static final SdkClientOption<List<MetricPublisher>> METRIC_PUBLISHERS = new SdkClientOption(new AttributeMap.Key.UnsafeValueType(List.class));
    public static final SdkClientOption<Boolean> SIGNER_OVERRIDDEN = new SdkClientOption<Boolean>(Boolean.class);
    public static final SdkClientOption<ExecutionAttributes> EXECUTION_ATTRIBUTES = new SdkClientOption(new AttributeMap.Key.UnsafeValueType(ExecutionAttributes.class));
    public static final SdkClientOption<String> INTERNAL_USER_AGENT = new SdkClientOption<String>(String.class);
    public static final SdkClientOption<String> CLIENT_USER_AGENT = new SdkClientOption<String>(String.class);
    public static final SdkClientOption<RetryMode> DEFAULT_RETRY_MODE = new SdkClientOption<RetryMode>(RetryMode.class);
    public static final SdkClientOption<EndpointProvider> ENDPOINT_PROVIDER = new SdkClientOption<EndpointProvider>(EndpointProvider.class);
    public static final SdkClientOption<AuthSchemeProvider> AUTH_SCHEME_PROVIDER = new SdkClientOption<AuthSchemeProvider>(AuthSchemeProvider.class);
    public static final SdkClientOption<Map<String, AuthScheme<?>>> AUTH_SCHEMES = new SdkClientOption(new AttributeMap.Key.UnsafeValueType(Map.class));
    public static final SdkClientOption<IdentityProviders> IDENTITY_PROVIDERS = new SdkClientOption<IdentityProviders>(IdentityProviders.class);
    public static final SdkClientOption<AttributeMap> CLIENT_CONTEXT_PARAMS = new SdkClientOption<AttributeMap>(AttributeMap.class);
    public static final SdkClientOption<CompressionConfiguration> COMPRESSION_CONFIGURATION = new SdkClientOption<CompressionConfiguration>(CompressionConfiguration.class);

    private SdkClientOption(Class<T> valueClass) {
        super(valueClass);
    }

    private SdkClientOption(AttributeMap.Key.UnsafeValueType valueType) {
        super(valueType);
    }
}

