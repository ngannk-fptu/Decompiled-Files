/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.http.SdkHttpClient
 *  software.amazon.awssdk.http.SdkHttpFullRequest$Builder
 *  software.amazon.awssdk.http.async.SdkAsyncHttpClient
 *  software.amazon.awssdk.utils.Logger
 *  software.amazon.awssdk.utils.StringUtils
 *  software.amazon.awssdk.utils.http.SdkHttpUtils
 */
package software.amazon.awssdk.core.internal.http.pipeline.stages;

import java.util.List;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.ApiName;
import software.amazon.awssdk.core.ClientType;
import software.amazon.awssdk.core.SdkSystemSetting;
import software.amazon.awssdk.core.client.config.SdkAdvancedClientOption;
import software.amazon.awssdk.core.client.config.SdkClientConfiguration;
import software.amazon.awssdk.core.client.config.SdkClientOption;
import software.amazon.awssdk.core.internal.http.HttpClientDependencies;
import software.amazon.awssdk.core.internal.http.RequestExecutionContext;
import software.amazon.awssdk.core.internal.http.pipeline.MutableRequestToRequestPipeline;
import software.amazon.awssdk.core.retry.RetryPolicy;
import software.amazon.awssdk.core.util.SdkUserAgent;
import software.amazon.awssdk.http.SdkHttpClient;
import software.amazon.awssdk.http.SdkHttpFullRequest;
import software.amazon.awssdk.http.async.SdkAsyncHttpClient;
import software.amazon.awssdk.utils.Logger;
import software.amazon.awssdk.utils.StringUtils;
import software.amazon.awssdk.utils.http.SdkHttpUtils;

@SdkInternalApi
public class ApplyUserAgentStage
implements MutableRequestToRequestPipeline {
    private static final Logger log = Logger.loggerFor(ApplyUserAgentStage.class);
    private static final String COMMA = ", ";
    private static final String SPACE = " ";
    private static final String IO = "io";
    private static final String HTTP = "http";
    private static final String CONFIG = "cfg";
    private static final String RETRY_MODE = "retry-mode";
    private static final String AWS_EXECUTION_ENV_PREFIX = "exec-env/";
    private static final String HEADER_USER_AGENT = "User-Agent";
    private final SdkClientConfiguration clientConfig;

    public ApplyUserAgentStage(HttpClientDependencies dependencies) {
        this.clientConfig = dependencies.clientConfiguration();
    }

    public static String resolveClientUserAgent(String userAgentPrefix, String internalUserAgent, ClientType clientType, SdkHttpClient syncHttpClient, SdkAsyncHttpClient asyncHttpClient, RetryPolicy retryPolicy) {
        String trimmedInternalUserAgent;
        String awsExecutionEnvironment = SdkSystemSetting.AWS_EXECUTION_ENV.getStringValue().orElse(null);
        StringBuilder userAgent = new StringBuilder(128);
        userAgent.append(StringUtils.trimToEmpty((String)userAgentPrefix));
        String systemUserAgent = SdkUserAgent.create().userAgent();
        if (!systemUserAgent.equals(userAgentPrefix)) {
            userAgent.append(COMMA).append(systemUserAgent);
        }
        if (!(trimmedInternalUserAgent = StringUtils.trimToEmpty((String)internalUserAgent)).isEmpty()) {
            userAgent.append(SPACE).append(trimmedInternalUserAgent);
        }
        if (!StringUtils.isEmpty((CharSequence)awsExecutionEnvironment)) {
            userAgent.append(SPACE).append(AWS_EXECUTION_ENV_PREFIX).append(awsExecutionEnvironment.trim());
        }
        if (clientType == null) {
            clientType = ClientType.UNKNOWN;
        }
        userAgent.append(SPACE).append(IO).append("/").append(StringUtils.lowerCase((String)clientType.name()));
        userAgent.append(SPACE).append(HTTP).append("/").append(SdkHttpUtils.urlEncode((String)ApplyUserAgentStage.clientName(clientType, syncHttpClient, asyncHttpClient)));
        String retryMode = retryPolicy.retryMode().toString();
        userAgent.append(SPACE).append(CONFIG).append("/").append(RETRY_MODE).append("/").append(StringUtils.lowerCase((String)retryMode));
        return userAgent.toString();
    }

    @Override
    public SdkHttpFullRequest.Builder execute(SdkHttpFullRequest.Builder request, RequestExecutionContext context) throws Exception {
        return request.putHeader(HEADER_USER_AGENT, this.getUserAgent(this.clientConfig, context.requestConfig().apiNames()));
    }

    private String getUserAgent(SdkClientConfiguration config, List<ApiName> requestApiNames) {
        String userDefinedSuffix;
        String clientUserAgent = this.clientConfig.option(SdkClientOption.CLIENT_USER_AGENT);
        if (clientUserAgent == null) {
            log.warn(() -> "Client user agent configuration is missing, so request user agent will be incomplete.");
            clientUserAgent = "";
        }
        StringBuilder userAgent = new StringBuilder(clientUserAgent);
        if (!requestApiNames.isEmpty()) {
            requestApiNames.forEach(apiName -> userAgent.append(SPACE).append(apiName.name()).append("/").append(apiName.version()));
        }
        if (!StringUtils.isEmpty((CharSequence)(userDefinedSuffix = config.option(SdkAdvancedClientOption.USER_AGENT_SUFFIX)))) {
            userAgent.append(COMMA).append(userDefinedSuffix.trim());
        }
        return userAgent.toString();
    }

    private static String clientName(ClientType clientType, SdkHttpClient syncHttpClient, SdkAsyncHttpClient asyncHttpClient) {
        if (clientType == ClientType.SYNC) {
            return syncHttpClient == null ? "null" : syncHttpClient.clientName();
        }
        if (clientType == ClientType.ASYNC) {
            return asyncHttpClient == null ? "null" : asyncHttpClient.clientName();
        }
        return ClientType.UNKNOWN.name();
    }
}

