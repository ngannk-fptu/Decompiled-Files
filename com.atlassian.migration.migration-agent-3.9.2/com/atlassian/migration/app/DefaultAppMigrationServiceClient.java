/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.migration.app.dto.AppWebhookEndpointCheckRequest
 *  com.atlassian.migration.app.dto.AppWebhookEndpointCheckResponse
 *  com.atlassian.migration.app.dto.AppsCloudSiteResponse
 *  com.atlassian.migration.app.dto.AppsLicenseResponseDto
 *  com.atlassian.migration.app.dto.CloudFeedbackResponse
 *  com.atlassian.migration.app.dto.ConsentRequest
 *  com.atlassian.migration.app.dto.ConsentResponse
 *  com.atlassian.migration.app.dto.ContainersByPageResponse
 *  com.atlassian.migration.app.dto.FinalizeUploadRequest
 *  com.atlassian.migration.app.dto.GetUploadUrlRequest
 *  com.atlassian.migration.app.dto.GetUrlResponse
 *  com.atlassian.migration.app.dto.InitializeUploadResponse
 *  com.atlassian.migration.app.dto.MigrationMappingResponse
 *  com.atlassian.migration.app.dto.RegisterForgeTransferRequest
 *  com.atlassian.migration.app.dto.RegisterTransferRequest
 *  com.atlassian.migration.app.dto.RegisterTransferRerunRequest
 *  com.atlassian.migration.app.dto.RerunEnablementDto
 *  com.atlassian.migration.app.dto.RerunTransferResponse
 *  com.atlassian.migration.app.dto.TransferErrorRequest
 *  com.atlassian.migration.app.dto.TransferLogEnablement
 *  com.atlassian.migration.app.dto.TransferLogResponse
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.collect.ImmutableMap
 *  com.google.gson.Gson
 *  javax.annotation.ParametersAreNonnullByDefault
 *  net.jodah.failsafe.Failsafe
 *  net.jodah.failsafe.Policy
 *  net.jodah.failsafe.RetryPolicy
 *  okhttp3.Interceptor
 *  okhttp3.MediaType
 *  okhttp3.OkHttpClient$Builder
 *  okhttp3.Request
 *  okhttp3.Request$Builder
 *  okhttp3.RequestBody
 *  okhttp3.Response
 *  okio.ByteString
 *  org.apache.commons.io.IOUtils
 *  org.apache.commons.lang3.StringUtils
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 *  org.slf4j.Logger
 *  org.springframework.web.util.UriComponentsBuilder
 */
package com.atlassian.migration.app;

import com.atlassian.migration.agent.config.MigrationAgentConfiguration;
import com.atlassian.migration.agent.dto.AppMigrationProgressResponse;
import com.atlassian.migration.agent.dto.AppsProgressDto;
import com.atlassian.migration.agent.json.Jsons;
import com.atlassian.migration.agent.logging.ContextLoggerFactory;
import com.atlassian.migration.agent.okhttp.HttpException;
import com.atlassian.migration.agent.okhttp.HttpService;
import com.atlassian.migration.agent.okhttp.HttpServiceException;
import com.atlassian.migration.agent.okhttp.MediaTypes;
import com.atlassian.migration.agent.okhttp.OKHttpProxyBuilder;
import com.atlassian.migration.agent.okhttp.RetryPolicyBuilder;
import com.atlassian.migration.agent.okhttp.ServiceErrorCodeHandler;
import com.atlassian.migration.agent.service.check.app.webhook.AppWebhookEndpointCheckServiceClient;
import com.atlassian.migration.agent.service.cloud.CloudSiteService;
import com.atlassian.migration.agent.service.impl.StargateHelper;
import com.atlassian.migration.agent.service.impl.UserAgentInterceptor;
import com.atlassian.migration.app.AppAssessmentClient;
import com.atlassian.migration.app.AppMigrationServiceClient;
import com.atlassian.migration.app.AppMigrationServiceClientKt;
import com.atlassian.migration.app.ContainerType;
import com.atlassian.migration.app.MigrationDetailsV1;
import com.atlassian.migration.app.TransferLogException;
import com.atlassian.migration.app.dto.AppWebhookEndpointCheckRequest;
import com.atlassian.migration.app.dto.AppWebhookEndpointCheckResponse;
import com.atlassian.migration.app.dto.AppsCloudSiteResponse;
import com.atlassian.migration.app.dto.AppsLicenseResponseDto;
import com.atlassian.migration.app.dto.CloudFeedbackResponse;
import com.atlassian.migration.app.dto.ConsentRequest;
import com.atlassian.migration.app.dto.ConsentResponse;
import com.atlassian.migration.app.dto.ContainersByPageResponse;
import com.atlassian.migration.app.dto.FinalizeUploadRequest;
import com.atlassian.migration.app.dto.GetUploadUrlRequest;
import com.atlassian.migration.app.dto.GetUrlResponse;
import com.atlassian.migration.app.dto.InitializeUploadResponse;
import com.atlassian.migration.app.dto.MigrationMappingResponse;
import com.atlassian.migration.app.dto.RegisterForgeTransferRequest;
import com.atlassian.migration.app.dto.RegisterTransferRequest;
import com.atlassian.migration.app.dto.RegisterTransferRerunRequest;
import com.atlassian.migration.app.dto.RerunEnablementDto;
import com.atlassian.migration.app.dto.RerunTransferResponse;
import com.atlassian.migration.app.dto.TransferErrorRequest;
import com.atlassian.migration.app.dto.TransferLogEnablement;
import com.atlassian.migration.app.dto.TransferLogResponse;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import javax.annotation.ParametersAreNonnullByDefault;
import net.jodah.failsafe.Failsafe;
import net.jodah.failsafe.Policy;
import net.jodah.failsafe.RetryPolicy;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.ByteString;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.springframework.web.util.UriComponentsBuilder;

@ParametersAreNonnullByDefault
public class DefaultAppMigrationServiceClient
implements AppMigrationServiceClient,
AppAssessmentClient,
AppWebhookEndpointCheckServiceClient {
    private static final String TRANSFER_ID_PATH_SEGMENT = "{transferId}";
    private static final String TEXT_CSV = "text/csv";
    private final HttpService httpService;
    private final HttpService s3UploadHttpService;
    private final MigrationAgentConfiguration configuration;
    private final CloudSiteService cloudSiteService;
    private final boolean bypassStargate;
    private final RetryPolicy<Response> responseRetryPolicy;
    private final Gson gson = new Gson();
    private static final Logger log = ContextLoggerFactory.getLogger(DefaultAppMigrationServiceClient.class);

    public DefaultAppMigrationServiceClient(MigrationAgentConfiguration configuration, UserAgentInterceptor userAgentInterceptor, CloudSiteService cloudSiteService, OKHttpProxyBuilder okHttpProxyBuilder) {
        this(new HttpService(() -> DefaultAppMigrationServiceClient.buildHttpClient(userAgentInterceptor, okHttpProxyBuilder).build(), new ServiceErrorCodeHandler()), new HttpService(() -> DefaultAppMigrationServiceClient.buildHttpClient(userAgentInterceptor, okHttpProxyBuilder).connectTimeout(90L, TimeUnit.SECONDS).readTimeout(90L, TimeUnit.SECONDS).writeTimeout(90L, TimeUnit.SECONDS).build(), new ServiceErrorCodeHandler()), configuration, cloudSiteService, RetryPolicyBuilder.amsClientPolicy().build());
    }

    @VisibleForTesting
    DefaultAppMigrationServiceClient(HttpService httpService, HttpService s3UploadHttpService, MigrationAgentConfiguration configuration, CloudSiteService cloudSiteService, RetryPolicy<Response> responseRetryPolicy) {
        this.bypassStargate = configuration.isBypassStargate();
        this.httpService = httpService;
        this.s3UploadHttpService = s3UploadHttpService;
        this.configuration = configuration;
        this.cloudSiteService = cloudSiteService;
        this.responseRetryPolicy = responseRetryPolicy;
    }

    private static OkHttpClient.Builder buildHttpClient(UserAgentInterceptor userAgentInterceptor, OKHttpProxyBuilder okHttpProxyBuilder) {
        return okHttpProxyBuilder.getProxyBuilder().connectTimeout(5L, TimeUnit.SECONDS).readTimeout(20L, TimeUnit.SECONDS).followRedirects(true).followSslRedirects(true).addInterceptor((Interceptor)userAgentInterceptor);
    }

    @Override
    public UUID registerTransfer(String cloudId, String containerId, RegisterTransferRequest registerTransfer) {
        UriComponentsBuilder uri = this.getUriBuilder().pathSegment(new String[]{"{containerId}"}).path("/transfer");
        Request request = this.getJsonBuilder(cloudId).url(uri.buildAndExpand(new Object[]{containerId}).encode().toUriString()).post(RequestBody.create((String)this.gson.toJson((Object)registerTransfer), (MediaType)MediaTypes.APPLICATION_JSON_TYPE)).build();
        return this.callGsonWithRetries(request, UUID.class);
    }

    @Override
    @NotNull
    public UUID registerForgeTransfer(@NotNull String cloudId, @NotNull String containerId, @NotNull RegisterForgeTransferRequest registerTransfer) {
        UriComponentsBuilder uri = this.getUriBuilder().pathSegment(new String[]{"{containerId}"}).path("/forge/transfer");
        Request request = this.getJsonBuilder(cloudId).url(uri.buildAndExpand(new Object[]{containerId}).encode().toUriString()).post(RequestBody.create((MediaType)MediaTypes.APPLICATION_JSON_TYPE, (String)this.gson.toJson((Object)registerTransfer))).build();
        return this.callGsonWithRetries(request, UUID.class);
    }

    @Override
    public MigrationMappingResponse getMigrationMappingByPage(String cloudId, String transferId, String namespace, @Nullable String lastEntity2, int pageSize) {
        UriComponentsBuilder uri = this.getUriBuilder().path("/mapping").pathSegment(new String[]{TRANSFER_ID_PATH_SEGMENT}).path("page").queryParam("namespace", new Object[]{namespace}).queryParam("pageSize", new Object[]{pageSize});
        if (StringUtils.isNotEmpty((CharSequence)lastEntity2)) {
            uri.queryParam("lastEntity", new Object[]{lastEntity2});
        }
        Request request = this.getJsonBuilder(cloudId).url(uri.buildAndExpand(new Object[]{transferId, namespace}).encode().toUriString()).get().build();
        return this.callGsonWithRetries(request, MigrationMappingResponse.class);
    }

    @Override
    public Map<String, String> getMappingById(String cloudId, String transferId, String namespace, Set<String> ids) {
        Request request = this.getJsonBuilder(cloudId).url(this.getUriBuilder().path("/mapping").pathSegment(new String[]{TRANSFER_ID_PATH_SEGMENT}).path("find").queryParam("namespace", new Object[]{namespace}).buildAndExpand(new Object[]{transferId}).toUriString()).post(RequestBody.create((String)Jsons.valueAsString(ids), (MediaType)MediaTypes.APPLICATION_JSON_TYPE)).build();
        return this.callGsonWithRetries(request, Map.class);
    }

    @Override
    public ContainersByPageResponse getContainersByPage(String cloudId, String transferId, ContainerType containerType, @Nullable String lastEntity2, int pageSize) {
        UriComponentsBuilder uri = this.getUriBuilder().path("/container").pathSegment(new String[]{TRANSFER_ID_PATH_SEGMENT}).path("page").queryParam("containerType", new Object[]{containerType}).queryParam("pageSize", new Object[]{pageSize});
        if (StringUtils.isNotEmpty((CharSequence)lastEntity2)) {
            uri.queryParam("lastEntity", new Object[]{lastEntity2});
        }
        Request request = this.getJsonBuilder(cloudId).url(uri.buildAndExpand(new Object[]{transferId}).encode().toUriString()).get().build();
        return this.callGsonWithRetries(request, ContainersByPageResponse.class);
    }

    @Override
    public CloudFeedbackResponse getCloudFeedback(String cloudId, String transferId) {
        UriComponentsBuilder uri = this.getUriBuilder().path("/feedback").pathSegment(new String[]{TRANSFER_ID_PATH_SEGMENT});
        Request request = this.getJsonBuilder(cloudId).url(uri.buildAndExpand(new Object[]{transferId}).encode().toUriString()).get().build();
        return this.callGsonWithRetries(request, CloudFeedbackResponse.class);
    }

    @Override
    public AppsCloudSiteResponse getAppInfoForSite(String cloudId, List<String> appKeys) {
        Request request = this.getJsonBuilder(cloudId).url(this.getUriBuilder().path("/confluence/appsinfo").toUriString()).post(RequestBody.create((String)Jsons.valueAsString(ImmutableMap.of((Object)"appKeys", appKeys)), (MediaType)MediaTypes.APPLICATION_JSON_TYPE)).build();
        return this.callGsonWithRetries(request, AppsCloudSiteResponse.class);
    }

    @Override
    public AppsLicenseResponseDto getAppsLicense(String cloudId, List<String> appKeys) {
        Request request = this.getJsonBuilder(cloudId).url(this.getUriBuilder().path("/check/confluence/license").toUriString()).post(RequestBody.create((String)Jsons.valueAsString(ImmutableMap.of((Object)"appKeys", appKeys)), (MediaType)MediaTypes.APPLICATION_JSON_TYPE)).build();
        return this.callGsonWithRetries(request, AppsLicenseResponseDto.class);
    }

    @Override
    public InitializeUploadResponse initializeUpload(String cloudId, String transferId, Optional<String> label) {
        UriComponentsBuilder uri = this.getUriBuilder().path("/upload/initialize").pathSegment(new String[]{transferId});
        label.ifPresent(s -> uri.queryParam("label", new Object[]{s}));
        Request request = this.getJsonBuilder(cloudId).url(uri.toUriString()).post(RequestBody.create((String)"", (MediaType)MediaTypes.APPLICATION_JSON_TYPE)).build();
        return this.callGsonWithRetries(request, InitializeUploadResponse.class);
    }

    @Override
    public GetUrlResponse getMultipartUploadUrl(String cloudId, GetUploadUrlRequest getUploadUrl) {
        Request request = this.getJsonBuilder(cloudId).url(this.getUriBuilder().path("/upload/url").toUriString()).post(RequestBody.create((String)this.gson.toJson((Object)getUploadUrl), (MediaType)MediaTypes.APPLICATION_JSON_TYPE)).build();
        try (Response response = this.callWithAMSRetries(request);){
            String header = response.header("x-etag", null);
            if (header != null) {
                log.debug("Returning an etag for a cached file chunk");
                GetUrlResponse getUrlResponse = new GetUrlResponse(null, header);
                return getUrlResponse;
            }
            log.debug("Returning the uploadUrl");
            GetUrlResponse getUrlResponse = new GetUrlResponse((String)this.gson.fromJson(response.body().charStream(), String.class), null);
            return getUrlResponse;
        }
    }

    @Override
    public String uploadToS3(String s3SignedUrl, String contentMd5, byte[] bytes) {
        Request request = new Request.Builder().url(s3SignedUrl).put(RequestBody.create((byte[])bytes, (MediaType)MediaTypes.APPLICATION_STREAM_TYPE)).addHeader("Content-MD5", contentMd5).build();
        Response response = this.getResponseForS3Upload(request);
        return response.header("ETag");
    }

    @Override
    public void finalizeUpload(String cloudId, FinalizeUploadRequest finalizeUpload) {
        Request request = this.getRequestBuilder(cloudId).url(this.getUriBuilder().path("/upload/finalize").toUriString()).post(RequestBody.create((String)this.gson.toJson((Object)finalizeUpload), (MediaType)MediaTypes.APPLICATION_JSON_TYPE)).build();
        this.callWithRetries(request);
    }

    @Override
    public MigrationDetailsV1 getMigrationDetailsV1(String cloudId, String migrationId) {
        UriComponentsBuilder uri = this.getUriBuilder().path("/details").pathSegment(new String[]{"{migrationId}"});
        Request request = this.getJsonBuilder(cloudId).url(uri.buildAndExpand(new Object[]{migrationId}).encode().toUriString()).get().build();
        return this.callGsonWithRetries(request, MigrationDetailsV1.class);
    }

    @Override
    public void recordTransferError(@NotNull String cloudId, @NotNull String transferId, @NotNull TransferErrorRequest transferErrorRequest) {
        UriComponentsBuilder uri = this.getUriBuilder().path("/error").pathSegment(new String[]{TRANSFER_ID_PATH_SEGMENT});
        Request request = this.getJsonBuilder(cloudId).url(uri.buildAndExpand(new Object[]{transferId}).encode().toUriString()).post(RequestBody.create((String)this.gson.toJson((Object)transferErrorRequest), (MediaType)MediaTypes.APPLICATION_JSON_TYPE)).build();
        this.callGsonWithRetries(request, TransferErrorRequest.class);
    }

    @Override
    public void notifyListenerTriggered(@NotNull String cloudId, @NotNull String transferId) {
        UriComponentsBuilder uri = this.getUriBuilder().path("/transfer").pathSegment(new String[]{TRANSFER_ID_PATH_SEGMENT, "notify", "listener-triggered"});
        Request request = this.getJsonBuilder(cloudId).url(uri.buildAndExpand(new Object[]{transferId}).encode().toUriString()).post(RequestBody.create((ByteString)ByteString.EMPTY, (MediaType)MediaTypes.APPLICATION_JSON_TYPE)).build();
        this.callGsonWithRetries(request, Void.class);
    }

    @Override
    public AppWebhookEndpointCheckResponse retrieveRegisteredWebhooks(@NotNull String cloudId, @NotNull AppWebhookEndpointCheckRequest appWebhookEndpointCheckRequest) throws HttpException {
        Request request = this.getJsonBuilder(cloudId).url(this.getUriBuilder().path("/check/webhook/available").toUriString()).post(RequestBody.create((String)this.gson.toJson((Object)appWebhookEndpointCheckRequest), (MediaType)MediaTypes.APPLICATION_JSON_TYPE)).build();
        return this.callGsonWithRetries(request, AppWebhookEndpointCheckResponse.class);
    }

    @Override
    @NotNull
    public RerunTransferResponse registerRerunTransfer(@NotNull String cloudId, @NotNull String containerId, @NotNull RegisterTransferRerunRequest registerTransferRerunRequest) {
        UriComponentsBuilder uri = this.getUriBuilder().pathSegment(new String[]{"{containerId}"}).path("/transfer").path("/rerun");
        Request request = this.getJsonBuilder(cloudId).url(uri.buildAndExpand(new Object[]{containerId}).encode().toUriString()).post(RequestBody.create((String)this.gson.toJson((Object)registerTransferRerunRequest), (MediaType)MediaTypes.APPLICATION_JSON_TYPE)).build();
        return this.callGsonWithRetries(request, RerunTransferResponse.class);
    }

    public List<AppsProgressDto.App> getAppProgress(String cloudId, String migrationId) {
        UriComponentsBuilder uri = this.getUriBuilder().path("/progress").pathSegment(new String[]{"{migrationId}"});
        Request request = this.getJsonBuilder(cloudId).url(uri.buildAndExpand(new Object[]{migrationId}).encode().toUriString()).get().build();
        return this.callGsonWithRetries((Request)request, AppMigrationProgressResponse.class).progress;
    }

    @Override
    @NotNull
    public RerunEnablementDto isRerunEnabled(String cloudId, String containerId) {
        UriComponentsBuilder uri = this.getUriBuilder().path("/rerun").pathSegment(new String[]{"{containerId}"}).path("/enabled");
        Request request = this.getJsonBuilder(cloudId).url(uri.buildAndExpand(new Object[]{containerId}).encode().toUriString()).get().build();
        return this.callGsonWithRetries(request, RerunEnablementDto.class);
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @NotNull
    public TransferLogResponse getTransferLogResponse(String cloudId, String containerId) {
        UriComponentsBuilder uri = this.getUriBuilder().path("/transfer/log").queryParam("containerId", new Object[]{containerId});
        Request request = this.getRequestBuilder(cloudId).addHeader("Accept", TEXT_CSV).url(uri.buildAndExpand(new Object[0]).encode().toUriString()).get().build();
        try (Response response = this.httpService.callStream(request);){
            String csv = IOUtils.toString((InputStream)response.body().byteStream(), (Charset)StandardCharsets.UTF_8);
            TransferLogResponse transferLogResponse = new TransferLogResponse(csv, response.header("Content-Disposition"));
            return transferLogResponse;
        }
        catch (Exception e) {
            log.error("Failed to parse stream response for containerId {} and error {}", (Object)containerId, (Object)e);
            throw new TransferLogException("Failed to parse stream response", e);
        }
    }

    @NotNull
    public TransferLogEnablement isTransferLogsEnabled(String cloudId, String containerId) {
        UriComponentsBuilder uri = this.getUriBuilder().path("/transfer/log/enabled").queryParam("containerId", new Object[]{containerId});
        Request request = this.getJsonBuilder(cloudId).url(uri.buildAndExpand(new Object[0]).encode().toUriString()).get().build();
        try {
            return this.callGsonWithRetries(request, TransferLogEnablement.class);
        }
        catch (HttpServiceException httpServiceException) {
            log.warn("Failed to get transfer log enabled, error {}", (Object)httpServiceException.getMessage());
            return new TransferLogEnablement(false, Collections.singletonList("Migration does not exist or expired"));
        }
    }

    @Override
    @NotNull
    public ConsentResponse getConsent(@NotNull String cloudId, @NotNull String consentId, @NotNull String sen) {
        UriComponentsBuilder uri = this.getUriBuilder().path("/consent").pathSegment(new String[]{consentId}).queryParam("sen", new Object[]{sen});
        Request request = this.getJsonBuilder(cloudId).url(uri.toUriString()).get().build();
        return this.callGsonWithRetries(request, ConsentResponse.class);
    }

    @Override
    @NotNull
    public ConsentResponse saveConsent(@NotNull String cloudId, @NotNull String consentKey, @NotNull ConsentRequest consentRequest) {
        UriComponentsBuilder uri = this.getUriBuilder().path("/consent").pathSegment(new String[]{consentKey});
        Request request = this.getJsonBuilder(cloudId).url(uri.toUriString()).put(RequestBody.create((String)this.gson.toJson((Object)consentRequest), (MediaType)MediaTypes.APPLICATION_JSON_TYPE)).build();
        return this.callGsonWithRetries(request, ConsentResponse.class);
    }

    private Request.Builder getRequestBuilder(String cloudId) {
        return StargateHelper.requestBuilder(this.getContainerToken(cloudId), this.bypassStargate).addHeader("Cloud-Id", cloudId);
    }

    private Request.Builder getJsonBuilder(String cloudId) {
        return this.getRequestBuilder(cloudId).addHeader("Accept", "application/json");
    }

    private UriComponentsBuilder getUriBuilder() {
        return UriComponentsBuilder.fromHttpUrl((String)this.configuration.getAppMigrationServiceBaseUrl());
    }

    private String getContainerToken(String cloudId) {
        return this.cloudSiteService.getByCloudId(cloudId).get().getContainerToken();
    }

    private Response callWithRetries(Request request) {
        try {
            return (Response)Failsafe.with(this.responseRetryPolicy, (Policy[])new RetryPolicy[0]).get(() -> this.httpService.call(request));
        }
        catch (HttpServiceException httpServiceException) {
            throw AppMigrationServiceClientKt.handleHttpServiceException(httpServiceException.getStatusCode(), httpServiceException);
        }
    }

    private <T> T callGsonWithRetries(Request request, Class<T> bodyType) {
        try {
            RetryPolicy retryPolicy = RetryPolicyBuilder.amsClientPolicy().build();
            return (T)Failsafe.with(retryPolicy, (Policy[])new RetryPolicy[0]).get(() -> this.httpService.callGson(request, bodyType));
        }
        catch (HttpServiceException httpServiceException) {
            throw AppMigrationServiceClientKt.handleHttpServiceException(httpServiceException.getStatusCode(), httpServiceException);
        }
    }

    private Response callWithAMSRetries(Request request) {
        try {
            RetryPolicy retryPolicy = RetryPolicyBuilder.amsClientPolicy().build();
            return (Response)Failsafe.with(retryPolicy, (Policy[])new RetryPolicy[0]).get(() -> this.httpService.callStream(request));
        }
        catch (HttpServiceException httpServiceException) {
            throw AppMigrationServiceClientKt.handleHttpServiceException(httpServiceException.getStatusCode(), httpServiceException);
        }
    }

    private Response getResponseForS3Upload(Request request) {
        try {
            return (Response)Failsafe.with(this.responseRetryPolicy, (Policy[])new RetryPolicy[0]).get(() -> this.s3UploadHttpService.call(request));
        }
        catch (HttpServiceException httpServiceException) {
            throw AppMigrationServiceClientKt.handleHttpServiceException(httpServiceException.getStatusCode(), httpServiceException);
        }
    }
}

