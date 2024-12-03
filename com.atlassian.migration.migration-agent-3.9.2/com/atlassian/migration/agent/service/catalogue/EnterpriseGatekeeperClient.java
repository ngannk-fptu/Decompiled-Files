/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.collect.ImmutableSet
 *  com.google.gson.Gson
 *  com.google.gson.JsonElement
 *  javax.annotation.Nullable
 *  javax.annotation.ParametersAreNonnullByDefault
 *  lombok.Generated
 *  net.jodah.failsafe.Failsafe
 *  net.jodah.failsafe.FailsafeException
 *  net.jodah.failsafe.Policy
 *  net.jodah.failsafe.RetryPolicy
 *  net.jodah.failsafe.function.CheckedSupplier
 *  okhttp3.ConnectionPool
 *  okhttp3.Interceptor
 *  okhttp3.MediaType
 *  okhttp3.OkHttpClient
 *  okhttp3.Request
 *  okhttp3.Request$Builder
 *  okhttp3.RequestBody
 *  okhttp3.Response
 *  okio.BufferedSink
 *  okio.Okio
 *  org.apache.commons.codec.binary.Base64
 *  org.apache.commons.codec.digest.DigestUtils
 *  org.apache.commons.lang3.StringUtils
 *  org.codehaus.jackson.type.TypeReference
 *  org.jetbrains.annotations.NotNull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.http.HttpStatus
 *  org.springframework.web.util.UriComponentsBuilder
 */
package com.atlassian.migration.agent.service.catalogue;

import com.atlassian.migration.MigrationDarkFeaturesManager;
import com.atlassian.migration.agent.Tracker;
import com.atlassian.migration.agent.config.MigrationAgentConfiguration;
import com.atlassian.migration.agent.entity.CloudSite;
import com.atlassian.migration.agent.entity.MigrationStatus;
import com.atlassian.migration.agent.entity.TransferStatus;
import com.atlassian.migration.agent.json.Jsons;
import com.atlassian.migration.agent.mapi.entity.MapiStatusDto;
import com.atlassian.migration.agent.mapi.external.model.JobDefinitionResponse;
import com.atlassian.migration.agent.media.MediaConfigToken;
import com.atlassian.migration.agent.mma.model.ServerInstance;
import com.atlassian.migration.agent.mma.model.ServerInstanceCreateResponse;
import com.atlassian.migration.agent.mma.model.SpaceMetadataDTO;
import com.atlassian.migration.agent.mma.model.processor.MetadataBatch;
import com.atlassian.migration.agent.okhttp.HttpService;
import com.atlassian.migration.agent.okhttp.MediaTypes;
import com.atlassian.migration.agent.okhttp.OKHttpProxyBuilder;
import com.atlassian.migration.agent.okhttp.RetryPolicyBuilder;
import com.atlassian.migration.agent.okhttp.ServiceErrorCodeHandler;
import com.atlassian.migration.agent.service.ConfluenceImportExportTaskStatus;
import com.atlassian.migration.agent.service.GlobalEntitiesImportContextDto;
import com.atlassian.migration.agent.service.MCSUploadPath;
import com.atlassian.migration.agent.service.SpaceImportContextDto;
import com.atlassian.migration.agent.service.catalogue.ContainerCreateRequest;
import com.atlassian.migration.agent.service.catalogue.ContainersFetchResponse;
import com.atlassian.migration.agent.service.catalogue.ContainersUpdateRequest;
import com.atlassian.migration.agent.service.catalogue.MigrationCreateRequest;
import com.atlassian.migration.agent.service.catalogue.MigrationDetails;
import com.atlassian.migration.agent.service.catalogue.MigrationScopeCreateRequest;
import com.atlassian.migration.agent.service.catalogue.MigrationScopeDetails;
import com.atlassian.migration.agent.service.catalogue.Space;
import com.atlassian.migration.agent.service.catalogue.TransferProgressRequest;
import com.atlassian.migration.agent.service.catalogue.TransferStatusUpdateRequest;
import com.atlassian.migration.agent.service.catalogue.model.AbstractContainer;
import com.atlassian.migration.agent.service.catalogue.model.CloudPageTemplate;
import com.atlassian.migration.agent.service.catalogue.model.CompleteMultipartFileUploadRequest;
import com.atlassian.migration.agent.service.catalogue.model.CreateFileRequest;
import com.atlassian.migration.agent.service.catalogue.model.CreateSinglepartFileResponse;
import com.atlassian.migration.agent.service.catalogue.model.MigrationDomainsAllowlistResponse;
import com.atlassian.migration.agent.service.catalogue.model.MigrationMappingPage;
import com.atlassian.migration.agent.service.catalogue.model.PagedResponse;
import com.atlassian.migration.agent.service.catalogue.model.SpaceResponse;
import com.atlassian.migration.agent.service.catalogue.model.StorageFileDownloadResponse;
import com.atlassian.migration.agent.service.catalogue.model.StorageFileResponse;
import com.atlassian.migration.agent.service.catalogue.model.TransferResponseList;
import com.atlassian.migration.agent.service.catalogue.model.UploadFilePartMCSResponse;
import com.atlassian.migration.agent.service.catalogue.model.UploadFilePartS3Response;
import com.atlassian.migration.agent.service.check.email.InvalidEmailCheckRequest;
import com.atlassian.migration.agent.service.cloud.CloudSiteService;
import com.atlassian.migration.agent.service.confluence.request.BulkSpaceImportStatusRequest;
import com.atlassian.migration.agent.service.confluence.request.BulkSpaceImportStatusResponse;
import com.atlassian.migration.agent.service.confluence.request.GlobalEntitiesImportPayload;
import com.atlassian.migration.agent.service.confluence.request.SpaceImportV2Payload;
import com.atlassian.migration.agent.service.execution.UncheckedInterruptedException;
import com.atlassian.migration.agent.service.impl.StargateHelper;
import com.atlassian.migration.agent.service.impl.UserAgentInterceptor;
import com.atlassian.migration.agent.service.user.EmailCheckStatusResponse;
import com.atlassian.migration.agent.service.user.LicenceCheckRequest;
import com.atlassian.migration.agent.service.user.LicenceCheckStatusResponse;
import com.atlassian.migration.agent.service.user.MigrationResponse;
import com.atlassian.migration.agent.service.util.StopConditionCheckingUtil;
import com.atlassian.migration.app.ContainerType;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableSet;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.ClosedByInterruptException;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import lombok.Generated;
import net.jodah.failsafe.Failsafe;
import net.jodah.failsafe.FailsafeException;
import net.jodah.failsafe.Policy;
import net.jodah.failsafe.RetryPolicy;
import net.jodah.failsafe.function.CheckedSupplier;
import okhttp3.ConnectionPool;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.BufferedSink;
import okio.Okio;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.type.TypeReference;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.util.UriComponentsBuilder;

@ParametersAreNonnullByDefault
public class EnterpriseGatekeeperClient {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(EnterpriseGatekeeperClient.class);
    private static final String ENTITY_TYPE_KEY = "entityType";
    private static final String ID_PREFIX_FILTER_KEY = "idPrefixFilter";
    private static final String LAST_ENTITY_KEY = "lastEntity";
    private static final String PAGE_SIZE_KEY = "pageSize";
    private static final int PAGE_SIZE = 1000;
    private static final String MULTIPART = "MULTIPART";
    private static final String SINGLEPART = "SINGLEPART";
    public static final int LARGE_FILE_SIZE = 0x6400000;
    public static final int MAX_PART_SIZE = 0x1000000;
    private static final String ETAG_HEADER = "ETag";
    private static final String MIGRATION_PATH = "/migration";
    public static final String MIGRATION_ALLOWLIST_PATH = "/migration/allowlist/CONFLUENCE";
    private static final String STORAGE_FILE_PATH = "/storage/file/";
    private static final String CONTAINER_PATH = "/container";
    private static final String TRANSFER_PATH = "/transfer";
    private static final String PROGRESS_PATH = "/progress";
    private static final String SCOPE_PATH = "/scope";
    private static final String MAPI_PATH = "/cma/jobs";
    private static final String MIGRATION_METADATA_AGGREGATOR_SPACE_METADATA_UPSERT_PATH = "/spaces";
    private static final String MIGRATION_METADATA_AGGREGATOR_SERVER_CREATE_PATH = "/server";
    private static final String STATUS_PATH = "/status";
    public static final String CLOUD_ID_HEADER = "Cloud-Id";
    private static final String CLOUD_ID = "cloudid";
    private static final String IMPORT_PATH = "import";
    private static final String BULK_IMPORT_PATH = "/import/spaces";
    private static final String V1 = "v1";
    private static final String V2 = "v2";
    private static final int SPACES_BATCH_SIZE = 1000;
    private static final String SPACE_PATH = "space";
    private static final int PAGE_TEMPLATES_BATCH_SIZE = 200;
    private static final String PAGE_TEMPLATES_PATH = "template/page";
    private static final String CONFLUENCE_LIMIT_QUERY_PARAM = "limit";
    private static final String CONFLUENCE_START_QUERY_PARAM = "start";
    private static final String EMPTY_PREFIX = "";
    private static final String MD5_HEADER = "Content-MD5";
    private static final Integer CONNECTION_TIMEOUT_SECONDS = 5;
    private static final boolean SHOULD_FOLLOW_REDIRECTS = true;
    private static final boolean SHOULD_FOLLOW_SSL_REDIRECTS = true;
    private static final Integer READ_TIMEOUT_SECONDS = 60;
    private static final Integer DEFAULT_MAX_IDLE_CONNECTIONS = 5;
    private static final Long ATLASSIAN_KEEP_ALIVE_DURATION_SECONDS = 55L;
    public static final String BATCHING_QUERY_PARAM = "batch";
    private final HttpService httpService;
    private final MigrationAgentConfiguration configuration;
    private final CloudSiteService cloudSiteService;
    private final MigrationDarkFeaturesManager darkFeaturesManager;
    private final boolean bypassStargate;
    private final RetryPolicy<Response> responseRetryPolicy;
    private final Gson gson = new Gson();

    public EnterpriseGatekeeperClient(MigrationAgentConfiguration configuration, UserAgentInterceptor userAgentInterceptor, CloudSiteService cloudSiteService, OKHttpProxyBuilder okHttpProxyBuilder, MigrationDarkFeaturesManager darkFeaturesManager) {
        this(new HttpService(() -> EnterpriseGatekeeperClient.buildHttpClientWithCustomKeepAliveTimeout(userAgentInterceptor, okHttpProxyBuilder), new ServiceErrorCodeHandler()), configuration, cloudSiteService, RetryPolicyBuilder.enterpriseGatekeeperClientRetryPolicy().build(), darkFeaturesManager);
    }

    @VisibleForTesting
    EnterpriseGatekeeperClient(HttpService httpService, MigrationAgentConfiguration configuration, CloudSiteService cloudSiteService, RetryPolicy<Response> responseRetryPolicy, MigrationDarkFeaturesManager darkFeaturesManager) {
        this.bypassStargate = configuration.isBypassStargate();
        this.httpService = httpService;
        this.configuration = configuration;
        this.cloudSiteService = cloudSiteService;
        this.responseRetryPolicy = responseRetryPolicy;
        this.darkFeaturesManager = darkFeaturesManager;
    }

    @VisibleForTesting
    static OkHttpClient buildHttpClientWithCustomKeepAliveTimeout(UserAgentInterceptor userAgentInterceptor, OKHttpProxyBuilder okHttpProxyBuilder) {
        return okHttpProxyBuilder.getProxyBuilder().connectTimeout((long)CONNECTION_TIMEOUT_SECONDS.intValue(), TimeUnit.SECONDS).followRedirects(true).followSslRedirects(true).readTimeout((long)READ_TIMEOUT_SECONDS.intValue(), TimeUnit.SECONDS).addInterceptor((Interceptor)userAgentInterceptor).connectionPool(new ConnectionPool(DEFAULT_MAX_IDLE_CONNECTIONS.intValue(), ATLASSIAN_KEEP_ALIVE_DURATION_SECONDS.longValue(), TimeUnit.SECONDS)).build();
    }

    MigrationDetails createMigration(String cloudId, MigrationCreateRequest migrationCreateRequest) {
        Request request = this.getJsonBuilder(cloudId).url(this.getUriBuilder(V1).path(MIGRATION_PATH).toUriString()).post(RequestBody.create((MediaType)MediaTypes.APPLICATION_JSON_TYPE, (String)this.gson.toJson((Object)migrationCreateRequest))).build();
        return this.callGsonWithRetries(request, MigrationDetails.class);
    }

    void createContainers(String cloudId, String migrationId, ContainerCreateRequest containerCreateRequest) {
        Request request = this.getJsonBuilder(cloudId).url(this.getUriBuilder(V1).path(MIGRATION_PATH).pathSegment(new String[]{migrationId}).path(CONTAINER_PATH).toUriString()).post(RequestBody.create((MediaType)MediaTypes.APPLICATION_JSON_TYPE, (String)this.gson.toJson((Object)containerCreateRequest))).build();
        this.callWithRetries(request);
    }

    public ConfluenceImportExportTaskStatus initiateGlobalEntitiesImport(String cloudId, String containerToken, GlobalEntitiesImportContextDto importContext) {
        GlobalEntitiesImportPayload globalEntitiesImportPayload = new GlobalEntitiesImportPayload(importContext.getMigrationScopeId(), importContext.getMigrationId(), importContext.getFiles());
        UriComponentsBuilder importEndpointUriBuilder = this.getConfluenceUriBuilder(V2);
        Request request = this.getJsonBuilder(cloudId, containerToken).url(importEndpointUriBuilder.pathSegment(new String[]{CLOUD_ID, cloudId, IMPORT_PATH, "nonspace"}).toUriString()).post(RequestBody.create((String)Jsons.valueAsString(globalEntitiesImportPayload), (MediaType)MediaTypes.APPLICATION_JSON_TYPE)).build();
        return this.callJsonWithRetries(request, new TypeReference<ConfluenceImportExportTaskStatus>(){});
    }

    public ConfluenceImportExportTaskStatus getGlobalEntitiesImportProgress(String cloudId, String containerToken, String taskId) {
        Objects.requireNonNull(taskId);
        UriComponentsBuilder importEndpointUriBuilder = this.getConfluenceUriBuilder(V2);
        Request request = this.getJsonBuilder(cloudId, containerToken).url(importEndpointUriBuilder.pathSegment(new String[]{CLOUD_ID, cloudId, IMPORT_PATH, "nonspace", "taskid", taskId}).toUriString()).get().build();
        return this.callJsonWithRetries(request, new TypeReference<ConfluenceImportExportTaskStatus>(){});
    }

    public ConfluenceImportExportTaskStatus initiateConfluenceSpaceImport(String cloudId, String containerToken, SpaceImportContextDto spaceImportContext) {
        UriComponentsBuilder importEndpointUriBuilder = this.getConfluenceUriBuilder(V2);
        SpaceImportV2Payload spaceImportPayload = new SpaceImportV2Payload(spaceImportContext.getFileId(), spaceImportContext.getPlanId(), spaceImportContext.getMigrationScopeId(), spaceImportContext.getMigrationId(), spaceImportContext.getFiles());
        if (this.darkFeaturesManager.isUnlimitedSpaceImportConcurrencyEnabled()) {
            importEndpointUriBuilder.queryParam(BATCHING_QUERY_PARAM, new Object[]{true});
        }
        Request request = this.getJsonBuilder(cloudId, containerToken).url(importEndpointUriBuilder.pathSegment(new String[]{CLOUD_ID, cloudId, IMPORT_PATH}).toUriString()).post(RequestBody.create((String)Jsons.valueAsString(spaceImportPayload), (MediaType)MediaTypes.APPLICATION_JSON_TYPE)).build();
        return this.callJsonWithRetries(request, new TypeReference<ConfluenceImportExportTaskStatus>(){});
    }

    public ConfluenceImportExportTaskStatus getConfluenceSpaceImportProgress(String cloudId, String containerToken, String confTaskId) {
        Objects.requireNonNull(confTaskId);
        UriComponentsBuilder importEndpointUriBuilder = this.getConfluenceUriBuilder(V2);
        Request request = this.getJsonBuilder(cloudId, containerToken).url(importEndpointUriBuilder.pathSegment(new String[]{CLOUD_ID, cloudId, IMPORT_PATH, "taskid", confTaskId}).toUriString()).get().build();
        return this.callJsonWithRetries(request, new TypeReference<ConfluenceImportExportTaskStatus>(){});
    }

    public BulkSpaceImportStatusResponse getBulkConfluenceSpaceImportProgress(String cloudId, String containerToken, List<String> confTaskIds) {
        Objects.requireNonNull(confTaskIds);
        BulkSpaceImportStatusRequest requestPayload = new BulkSpaceImportStatusRequest(confTaskIds);
        UriComponentsBuilder importEndpointUriBuilder = this.getConfluenceUriBuilder(V2);
        Request request = this.getJsonBuilder(cloudId, containerToken).url(importEndpointUriBuilder.pathSegment(new String[]{CLOUD_ID, cloudId}).path(BULK_IMPORT_PATH).toUriString()).post(RequestBody.create((String)Jsons.valueAsString(requestPayload), (MediaType)MediaTypes.APPLICATION_JSON_TYPE)).build();
        return this.callJsonWithRetries(request, new TypeReference<BulkSpaceImportStatusResponse>(){});
    }

    public Map<String, String> getMappings(String cloudId, String migrationScopeId, String entityType, String idPrefixFilter) {
        MigrationMappingPage page;
        String lastEntity2 = null;
        HashMap<String, String> mappings = new HashMap<String, String>();
        do {
            page = this.getMappingsPage(cloudId, migrationScopeId, entityType, idPrefixFilter, lastEntity2);
            lastEntity2 = page.getMeta().getLastEntity();
            mappings.putAll(page.getItems());
        } while (lastEntity2 != null && !lastEntity2.isEmpty() && page.getMeta().isHasNext());
        return mappings;
    }

    public MigrationScopeDetails createMigrationScope(String cloudId, MigrationScopeCreateRequest migrationScopeCreateRequest) {
        Request request = this.getJsonBuilder(cloudId).url(this.getUriBuilder(V1).path(SCOPE_PATH).toUriString()).post(RequestBody.create((String)this.gson.toJson((Object)migrationScopeCreateRequest), (MediaType)MediaTypes.APPLICATION_JSON_TYPE)).build();
        return this.callGsonWithRetries(request, MigrationScopeDetails.class);
    }

    public JobDefinitionResponse getMigrationJobDefinition(String jobId, String containerToken) {
        Request request = StargateHelper.requestBuilder(containerToken, this.bypassStargate).url(this.getMapiUriBuilder(V1).path(MAPI_PATH).pathSegment(new String[]{jobId}).toUriString()).get().build();
        return this.callGsonWithRetries(request, JobDefinitionResponse.class, (Set<Integer>)ImmutableSet.of((Object)HttpStatus.METHOD_NOT_ALLOWED.value()));
    }

    private MigrationMappingPage getMappingsPage(String cloudId, String migrationScopeId, String entityType, String idPrefixFilter, @Nullable String lastEntity2) {
        UriComponentsBuilder builder = this.getMmsUriBuilder(V1).path(SCOPE_PATH).pathSegment(new String[]{migrationScopeId}).path("/mapping/page").queryParam(ENTITY_TYPE_KEY, new Object[]{entityType}).queryParam(ID_PREFIX_FILTER_KEY, new Object[]{idPrefixFilter}).queryParam(PAGE_SIZE_KEY, new Object[]{1000});
        if (lastEntity2 != null) {
            builder = builder.queryParam(LAST_ENTITY_KEY, new Object[]{lastEntity2});
        }
        Request request = this.getJsonBuilder(cloudId).url(builder.toUriString()).get().build();
        return this.callGsonWithRetries(request, MigrationMappingPage.class);
    }

    ContainersFetchResponse getContainersForMigration(String cloudId, String migrationId, AbstractContainer.Type type, int pageSize, @Nullable String nextId, boolean expand) {
        UriComponentsBuilder uri = this.getUriBuilder(V1).path(MIGRATION_PATH).pathSegment(new String[]{migrationId}).path(CONTAINER_PATH).queryParam("containerType", new Object[]{type}).queryParam(PAGE_SIZE_KEY, new Object[]{pageSize});
        if (expand) {
            uri.queryParam("expand", new Object[]{"TRANSFERS"});
        }
        if (StringUtils.isNotEmpty((CharSequence)nextId)) {
            uri.queryParam("nextId", new Object[]{nextId});
        }
        Request request = this.getJsonBuilder(cloudId).url(uri.toUriString()).get().build();
        return this.callGsonWithRetries(request, ContainersFetchResponse.class);
    }

    ContainersFetchResponse getContainersByStatusForMigration(String cloudId, String migrationId, ContainerType type, AbstractContainer.ContainerStatus status, int pageSize, @Nullable String nextId) {
        UriComponentsBuilder uri = this.getUriBuilder(V1).path(MIGRATION_PATH).pathSegment(new String[]{migrationId}).path(CONTAINER_PATH).queryParam("containerType", new Object[]{type}).queryParam("status", new Object[]{status}).queryParam(PAGE_SIZE_KEY, new Object[]{pageSize});
        if (StringUtils.isNotEmpty((CharSequence)nextId)) {
            uri.queryParam("nextId", new Object[]{nextId});
        }
        Request request = this.getJsonBuilder(cloudId).url(uri.toUriString()).get().build();
        return this.callGsonWithRetries(request, ContainersFetchResponse.class);
    }

    void updateMigrationStatusForContainers(String cloudId, String migrationId, String containerId, AbstractContainer.ContainerStatus status, @Nullable String statusMessage) {
        ContainersUpdateRequest containersUpdateRequest = new ContainersUpdateRequest(status, statusMessage);
        Request request = this.getJsonBuilder(cloudId).url(this.getUriBuilder(V1).path(MIGRATION_PATH).pathSegment(new String[]{migrationId}).path(CONTAINER_PATH).pathSegment(new String[]{containerId}).path(STATUS_PATH).toUriString()).put(RequestBody.create((String)this.gson.toJson((Object)containersUpdateRequest), (MediaType)MediaTypes.APPLICATION_JSON_TYPE)).build();
        this.callWithRetries(request);
    }

    public StorageFileResponse createStorageFileInMCS(String cloudId, String migrationId, String fileName, MCSUploadPath mcsUploadPath) {
        return this.createStorageFileInMCS(cloudId, migrationId, fileName, mcsUploadPath, EMPTY_PREFIX);
    }

    public StorageFileResponse createStorageFileInMCS(String cloudId, String migrationId, String fileName, MCSUploadPath mcsUploadPath, String prefix) {
        log.info("MULTIPART - Creating file in MCS cloudId: {} for {}: {} fileName: {}", new Object[]{cloudId, mcsUploadPath.getValue(), migrationId, fileName});
        Request request = this.buildCreateStorageFileRequest(cloudId, migrationId, fileName, MULTIPART, mcsUploadPath, prefix);
        return this.callGsonWithRetries(request, StorageFileResponse.class);
    }

    public CreateSinglepartFileResponse createStorageFileInMCSSinglePart(String cloudId, String migrationId, String fileName, MCSUploadPath mcsUploadPath) {
        return this.createStorageFileInMCSSinglePart(cloudId, migrationId, fileName, mcsUploadPath, EMPTY_PREFIX);
    }

    public CreateSinglepartFileResponse createStorageFileInMCSSinglePart(String cloudId, String migrationId, String fileName, MCSUploadPath mcsUploadPath, String prefix) {
        log.info("SINGLEPART - Creating file in MCS cloudId: {} for {} : {} fileName: {}", new Object[]{cloudId, mcsUploadPath.getValue(), migrationId, fileName});
        Request request = this.buildCreateStorageFileRequest(cloudId, migrationId, fileName, SINGLEPART, mcsUploadPath, prefix);
        return this.callGsonWithRetries(request, CreateSinglepartFileResponse.class);
    }

    @NotNull
    private Request buildCreateStorageFileRequest(String cloudId, String migrationId, String fileName, String uploadMethod, MCSUploadPath mcsUploadPath, String prefix) {
        return this.getJsonBuilder(cloudId).url(this.getUriBuilder(V1).path(this.getMigrationOrScopePathSegment(mcsUploadPath)).pathSegment(new String[]{migrationId}).path("/storage/file").queryParam("uploadMethod", new Object[]{uploadMethod}).toUriString()).post(RequestBody.create((String)this.gson.toJson((Object)new CreateFileRequest(fileName, prefix)), (MediaType)MediaTypes.APPLICATION_JSON_TYPE)).build();
    }

    public StorageFileDownloadResponse getFileDownloadUrlFromMCS(String cloudId, String migrationId, String fileId, MCSUploadPath mcsUploadPath) {
        log.info("Get file download url in MCS cloudId: {} for {} : {} fileId: {}", new Object[]{cloudId, mcsUploadPath.getValue(), migrationId, fileId});
        Request request = this.getJsonBuilder(cloudId).url(this.getUriBuilder(V1).path(this.getMigrationOrScopePathSegment(mcsUploadPath)).pathSegment(new String[]{migrationId}).path(STORAGE_FILE_PATH).pathSegment(new String[]{fileId}).toUriString()).get().build();
        return this.callGsonWithRetries(request, StorageFileDownloadResponse.class);
    }

    public MediaConfigToken getMediaConfigToken(String cloudId, String containerToken, Duration expiryDuration) {
        Request request = this.getJsonBuilder(cloudId, containerToken).url(this.getConfluenceUriBuilder(V1).pathSegment(new String[]{CLOUD_ID, cloudId}).path("/media/config/token").queryParam("expiryDuration", new Object[]{expiryDuration.toMinutes()}).toUriString()).get().build();
        return this.callGsonWithRetries(request, MediaConfigToken.class);
    }

    public MigrationResponse startEmailCheck(String cloudId, String migrationScopeId, InvalidEmailCheckRequest invalidEmailCheckRequest) {
        Request request = this.getJsonBuilder(cloudId).url(this.buildInvalidEmailsCheckUrl(migrationScopeId).toUriString()).post(RequestBody.create((String)this.gson.toJson((Object)invalidEmailCheckRequest), (MediaType)MediaTypes.APPLICATION_JSON_TYPE)).build();
        return this.callGsonWithRetries(request, MigrationResponse.class);
    }

    public EmailCheckStatusResponse getInvalidEmailsCheckStatus(String cloudId, String migrationScopeId, String taskId) {
        Request request = this.getJsonBuilder(cloudId).url(this.buildInvalidEmailsCheckUrl(migrationScopeId).pathSegment(new String[]{taskId}).toUriString()).get().build();
        return this.callGsonWithRetries(request, EmailCheckStatusResponse.class);
    }

    public LicenceCheckStatusResponse getLicenceCheckStatus(String cloudId, String migrationScopeId, String taskId) {
        Request request = this.getJsonBuilder(cloudId).url(this.buildLicenceCheckUrl(migrationScopeId).pathSegment(new String[]{taskId}).toUriString()).get().build();
        return this.callGsonWithRetries(request, LicenceCheckStatusResponse.class);
    }

    public MigrationResponse startLicenceCheck(String cloudId, String migrationScopeId, LicenceCheckRequest licenceCheckRequest) {
        Request request = this.getJsonBuilder(cloudId).url(this.buildLicenceCheckUrl(migrationScopeId).toUriString()).post(RequestBody.create((String)this.gson.toJson((Object)licenceCheckRequest), (MediaType)MediaTypes.APPLICATION_JSON_TYPE)).build();
        return this.callGsonWithRetries(request, MigrationResponse.class);
    }

    public MigrationDomainsAllowlistResponse getDomainAllowlist(String cloudId) {
        Request request = this.getJsonBuilder(cloudId).url(this.buildDomainAllowlistUrl().toUriString()).get().build();
        return this.callGsonWithRetries(request, MigrationDomainsAllowlistResponse.class);
    }

    private UriComponentsBuilder getUserMigrationUriBuilder(String version) {
        return UriComponentsBuilder.fromHttpUrl((String)this.configuration.getUserMigrationServiceViaEGBaseUrl(version));
    }

    private UriComponentsBuilder buildInvalidEmailsCheckUrl(String migrationScopeId) {
        return this.getUserMigrationUriBuilder(V1).path(SCOPE_PATH).pathSegment(new String[]{migrationScopeId}).path("/confluence/check/emails");
    }

    private UriComponentsBuilder buildLicenceCheckUrl(String migrationScopeId) {
        return this.getUserMigrationUriBuilder(V1).path(SCOPE_PATH).pathSegment(new String[]{migrationScopeId}).path("check/licence/");
    }

    public void uploadFileToMCS(String cloudId, String migrationId, String fileId, String uploadId, Path filePath, MCSUploadPath mcsUploadPath, Tracker ... trackers) {
        try {
            log.info("Uploading file cloudId: {} migrationId: {} fileId: {} uploadId: {} filename: {}", new Object[]{cloudId, migrationId, fileId, uploadId, filePath.toAbsolutePath()});
            try (FileInputStream file = new FileInputStream(filePath.toFile());){
                this.uploadMultipartFile(cloudId, migrationId, fileId, uploadId, file, mcsUploadPath, trackers);
            }
        }
        catch (UncheckedInterruptedException e) {
            throw new UncheckedInterruptedException(e);
        }
        catch (IOException e) {
            throw new RuntimeException("Could not read file: " + filePath.getFileName(), e);
        }
        catch (Exception e) {
            log.error("Error occurred when uploading file to MCS", (Throwable)e);
            throw new RuntimeException(e);
        }
    }

    protected void uploadMultipartFile(String cloudId, String migrationId, String fileId, String uploadId, FileInputStream file, MCSUploadPath mcsUploadPath, Tracker ... trackers) {
        try {
            long fileSize = file.getChannel().size();
            long partSize = 0x1000000L;
            long bytesRemaining = fileSize;
            ArrayList<UploadFilePartS3Response> fileParts = new ArrayList<UploadFilePartS3Response>();
            int index = 1;
            while (bytesRemaining > 0L) {
                long offSet = (long)(index - 1) * 0x1000000L;
                if (bytesRemaining < partSize) {
                    partSize = bytesRemaining;
                }
                StopConditionCheckingUtil.throwIfStopConditionWasReached();
                Response uploadResponse = this.uploadFilePartToPresignedURL(cloudId, migrationId, fileId, uploadId, offSet, index, partSize, file, mcsUploadPath);
                fileParts.add(this.processUploadResponse(index, uploadResponse));
                if (trackers.length > 0) {
                    trackers[0].track(partSize);
                }
                bytesRemaining -= partSize;
                ++index;
            }
            this.completeMultipartUpload(cloudId, migrationId, fileId, uploadId, fileParts, mcsUploadPath);
        }
        catch (UncheckedInterruptedException | ClosedByInterruptException e) {
            throw new UncheckedInterruptedException(e);
        }
        catch (Exception e) {
            log.error("Error uploading file, fileId: {}, migrationId: {}", new Object[]{fileId, migrationId, e});
            throw new RuntimeException(e);
        }
    }

    private Response uploadFilePartToPresignedURL(String cloudId, String migrationId, String fileId, String uploadId, long offSet, int index, long partSize, FileInputStream file, MCSUploadPath mcsUploadPath) throws IOException {
        String checkSum = EnterpriseGatekeeperClient.getMD5CheckSum(partSize, file);
        UploadFilePartMCSResponse presignedURL = this.requestMultipartPresignedURL(cloudId, migrationId, fileId, uploadId, index, mcsUploadPath, checkSum);
        return this.uploadFilePartToPresignedURL(presignedURL, offSet, partSize, file, checkSum);
    }

    private static String getMD5CheckSum(long partSize, FileInputStream file) throws IOException {
        byte[] filePart = new byte[(int)partSize];
        file.read(filePart, 0, filePart.length);
        MessageDigest md5Digest = DigestUtils.getMd5Digest();
        md5Digest.update(filePart);
        return Base64.encodeBase64String((byte[])md5Digest.digest());
    }

    private Response uploadFilePartToPresignedURL(UploadFilePartMCSResponse result, final long offSet, final long partSize, final FileInputStream file, String checkSum) {
        try {
            Request uploadRequest = new Request.Builder().url(result.getUploadUrl()).addHeader(MD5_HEADER, checkSum).put(new RequestBody(){

                public long contentLength() {
                    return partSize;
                }

                public MediaType contentType() {
                    return MediaTypes.APPLICATION_STREAM_TYPE;
                }

                public void writeTo(BufferedSink sink) throws IOException {
                    if (partSize > 0L) {
                        file.getChannel().position(offSet);
                        sink.write(Okio.source((InputStream)file), partSize);
                    }
                }
            }).build();
            return this.callWithRetriesAndReturnResponse(uploadRequest);
        }
        catch (IllegalArgumentException exception) {
            log.error("Error while uploading the file part", (Throwable)exception);
            throw exception;
        }
    }

    private String getMigrationOrScopePathSegment(MCSUploadPath mcsUploadPath) {
        return mcsUploadPath == MCSUploadPath.MIGRATION_ID ? MIGRATION_PATH : SCOPE_PATH;
    }

    @NotNull
    private UploadFilePartS3Response processUploadResponse(int index, Response uploadResponse) {
        String etag = uploadResponse.header(ETAG_HEADER);
        if (etag == null) {
            throw new RuntimeException("No ETag found in header during upload file part in index: " + index);
        }
        return new UploadFilePartS3Response(index, etag);
    }

    private UploadFilePartMCSResponse requestMultipartPresignedURL(String cloudId, String migrationId, String fileId, String uploadId, int index, MCSUploadPath mcsUploadPath, String md5Checksum) {
        Request.Builder jsonBuilder = this.getJsonBuilder(cloudId);
        String buildUploadFilePart = this.buildUploadFilePartUrl(migrationId, fileId, uploadId, index, mcsUploadPath).toUriString();
        Request request = jsonBuilder.url(buildUploadFilePart).addHeader(MD5_HEADER, md5Checksum).post(RequestBody.create(null, (byte[])new byte[0])).build();
        return this.callGsonWithRetries(request, UploadFilePartMCSResponse.class);
    }

    private UriComponentsBuilder buildUploadFilePartUrl(String migrationId, String fileId, String uploadId, int index, MCSUploadPath mcsUploadPath) {
        return this.getUriBuilder(V1).path(this.getMigrationOrScopePathSegment(mcsUploadPath)).pathSegment(new String[]{migrationId}).path(STORAGE_FILE_PATH).pathSegment(new String[]{fileId}).path("/upload").pathSegment(new String[]{uploadId}).path("/part/").pathSegment(new String[]{Integer.toString(index)});
    }

    void completeMultipartUpload(String cloudId, String migrationId, String fileId, String uploadId, List<UploadFilePartS3Response> fileParts, MCSUploadPath mcsUploadPath) {
        log.info("Complete multipart upload for cloudId: {} for {} : {}, fileId: {}, # of file parts: {}", new Object[]{cloudId, mcsUploadPath.getValue(), migrationId, fileId, fileParts.size()});
        CompleteMultipartFileUploadRequest completeMultipartFileUploadRequest = new CompleteMultipartFileUploadRequest(fileParts);
        Request completeUploadRequest = this.getJsonBuilder(cloudId).url(this.buildCompleteUploadUrl(migrationId, fileId, uploadId, mcsUploadPath)).post(RequestBody.create((MediaType)MediaTypes.APPLICATION_JSON_TYPE, (String)this.gson.toJson((Object)completeMultipartFileUploadRequest))).build();
        Response completeMultipartUploadResponse = this.callWithRetriesAndReturnResponse(completeUploadRequest);
        if (completeMultipartUploadResponse.code() != 204) {
            throw new RuntimeException("Error while completing multipart file upload in MCS status code: " + completeMultipartUploadResponse.code());
        }
    }

    private String buildCompleteUploadUrl(String migrationId, String fileId, String uploadId, MCSUploadPath mcsUploadPath) {
        return this.getUriBuilder(V1).path(this.getMigrationOrScopePathSegment(mcsUploadPath)).pathSegment(new String[]{migrationId}).path(STORAGE_FILE_PATH).pathSegment(new String[]{fileId}).path("/upload").pathSegment(new String[]{uploadId}).path("/complete").toUriString();
    }

    private UriComponentsBuilder buildDomainAllowlistUrl() {
        return this.getUriBuilder(V1).path(MIGRATION_ALLOWLIST_PATH);
    }

    private UriComponentsBuilder getUriBuilder(String version) {
        return UriComponentsBuilder.fromHttpUrl((String)this.configuration.getMigrationCatalogueServiceUrl(version));
    }

    private UriComponentsBuilder getConfluenceUriBuilder(String version) {
        return UriComponentsBuilder.fromHttpUrl((String)this.configuration.getConfluenceCloudUrl(version));
    }

    private UriComponentsBuilder getMapiUriBuilder(String version) {
        return UriComponentsBuilder.fromHttpUrl((String)this.configuration.getMapiUrl(version));
    }

    private UriComponentsBuilder getMigrationMetadataAggregatorUriBuilder(String version) {
        return UriComponentsBuilder.fromHttpUrl((String)this.configuration.getMigrationMetadataAggregatorUrl(version));
    }

    private UriComponentsBuilder getMmsUriBuilder(String version) {
        return UriComponentsBuilder.fromHttpUrl((String)this.configuration.getMigrationMappingServiceBaseUrl(version));
    }

    private Request.Builder getJsonBuilder(String cloudId) {
        return StargateHelper.requestBuilder(this.getContainerToken(cloudId), this.bypassStargate).addHeader(CLOUD_ID_HEADER, cloudId).addHeader("Accept", "application/json");
    }

    private Request.Builder getJsonBuilder(String cloudId, String containerToken) {
        return StargateHelper.requestBuilder(containerToken, this.bypassStargate).addHeader(CLOUD_ID_HEADER, cloudId).addHeader("Accept", "application/json");
    }

    private String getContainerToken(String cloudId) {
        Optional<CloudSite> cloudSite = this.cloudSiteService.getByCloudId(cloudId);
        if (!cloudSite.isPresent()) {
            throw new IllegalStateException(String.format("Failed to find cloudSite entry for requested cloudId: %s", cloudId));
        }
        return cloudSite.get().getContainerToken();
    }

    private void callWithRetries(Request request) {
        this.withStopConditionExceptionHandling(() -> (Response)Failsafe.with(this.responseRetryPolicy, (Policy[])new RetryPolicy[0]).get(() -> this.httpService.call(request)));
    }

    private <T> T callGsonWithRetries(Request request, Class<T> bodyType) {
        RetryPolicy retryPolicy = RetryPolicyBuilder.enterpriseGatekeeperClientRetryPolicy().build();
        return this.withStopConditionExceptionHandling(() -> Failsafe.with((Policy)retryPolicy, (Policy[])new RetryPolicy[0]).get(() -> this.httpService.callGson(request, bodyType)));
    }

    private <T> T callGsonWithRetries(Request request, Class<T> bodyType, Set<Integer> expectedErrorCodes) {
        RetryPolicy retryPolicy = RetryPolicyBuilder.enterpriseGatekeeperClientRetryPolicy().build();
        return this.withStopConditionExceptionHandling(() -> Failsafe.with((Policy)retryPolicy, (Policy[])new RetryPolicy[0]).get(() -> this.httpService.callGsonWithExpectedErrorCodes(request, bodyType, expectedErrorCodes)));
    }

    public Response callWithRetriesAndReturnResponse(Request request) {
        return (Response)this.withStopConditionExceptionHandling(() -> (Response)Failsafe.with(this.responseRetryPolicy, (Policy[])new RetryPolicy[0]).get(() -> this.httpService.call(request)));
    }

    public Response callStreamWithRetriesAndReturnResponse(Request request, Set<Integer> expectedErrorCodes) {
        return (Response)this.withStopConditionExceptionHandling(() -> (Response)Failsafe.with(this.responseRetryPolicy, (Policy[])new RetryPolicy[0]).get(() -> this.httpService.callStream(request, expectedErrorCodes)));
    }

    private <T> T callJsonWithRetries(Request request, TypeReference<T> bodyType) {
        RetryPolicy retryPolicy = RetryPolicyBuilder.enterpriseGatekeeperClientRetryPolicy().build();
        return this.withStopConditionExceptionHandling(() -> Failsafe.with((Policy)retryPolicy, (Policy[])new RetryPolicy[0]).get(() -> this.httpService.callJson(request, bodyType)));
    }

    private <T> T withStopConditionExceptionHandling(CheckedSupplier<T> supplier) {
        try {
            StopConditionCheckingUtil.throwIfStopConditionWasReached();
            return (T)supplier.get();
        }
        catch (FailsafeException e) {
            if (StopConditionCheckingUtil.isStoppingExceptionInCausalChain((Exception)((Object)e))) {
                throw new UncheckedInterruptedException(e);
            }
            throw new FailsafeException((Throwable)e);
        }
        catch (UncheckedInterruptedException | InterruptedException e) {
            throw new UncheckedInterruptedException(e);
        }
        catch (Throwable e) {
            if (StopConditionCheckingUtil.isStoppingExceptionInCausalChain((Exception)e)) {
                throw new UncheckedInterruptedException(e);
            }
            throw new RuntimeException(e);
        }
    }

    public void sendMigrationStatusToMCS(String migrationId, String cloudId, MigrationStatus migrationStatus, String statusMessage) {
        HashMap<String, Object> body = new HashMap<String, Object>();
        body.put("status", (Object)migrationStatus);
        body.put("statusMessage", statusMessage);
        Request request = this.getJsonBuilder(cloudId).url(this.getUriBuilder(V1).path(MIGRATION_PATH).pathSegment(new String[]{migrationId}).path(STATUS_PATH).toUriString()).put(RequestBody.create((String)Jsons.valueAsString(body), (MediaType)MediaTypes.APPLICATION_JSON_TYPE)).build();
        this.callWithRetries(request);
    }

    public void uploadFileToMCSSinglePart(String uploadUrl, File file, Tracker ... trackers) {
        String contentDisposition = "attachment; filename*=UTF-8''" + file.getName();
        Request request = new Request.Builder().addHeader("Content-Disposition", contentDisposition).url(uploadUrl).put(RequestBody.create((File)file, (MediaType)MediaTypes.APPLICATION_STREAM_TYPE)).build();
        this.callWithRetries(request);
        if (trackers.length > 0) {
            trackers[0].track(file.length());
        }
    }

    public Set<Space> getAllSpaces(CloudSite cloudSite) {
        HashSet<Space> spaceResults = new HashSet<Space>();
        String cloudId = cloudSite.getCloudId();
        String containerToken = cloudSite.getContainerToken();
        int pageNum = 1;
        int start = 0;
        while (true) {
            UriComponentsBuilder uriBuilder = this.getConfluenceUriBuilder(V1).pathSegment(new String[]{CLOUD_ID, cloudId, SPACE_PATH}).queryParam(CONFLUENCE_LIMIT_QUERY_PARAM, new Object[]{1000}).queryParam(CONFLUENCE_START_QUERY_PARAM, new Object[]{start});
            Request request = this.getJsonBuilder(cloudId, containerToken).url(uriBuilder.toUriString()).get().build();
            SpaceResponse spaceResult = this.callJsonWithRetries(request, new TypeReference<SpaceResponse>(){});
            spaceResults.addAll(spaceResult.results);
            log.debug("FetchAllCloudSpaces: Fetched page [" + pageNum + " ], results = " + spaceResult.size);
            ++pageNum;
            if (spaceResult.size < 1000) break;
            start += 1000;
        }
        return spaceResults;
    }

    public List<CloudPageTemplate> getAllNonSpaceTemplates(String cloudId) {
        return this.getAllPagedResultsFromCloud(cloudId, 200, V1, PAGE_TEMPLATES_PATH, new TypeReference<PagedResponse<CloudPageTemplate>>(){});
    }

    private <R> List<R> getAllPagedResultsFromCloud(String cloudId, int batchSize, String version, String path, TypeReference<PagedResponse<R>> responseType) {
        ArrayList<R> results = new ArrayList<R>();
        int pageNum = 1;
        int start = 0;
        while (true) {
            List<R> currentPageResults = this.getPagedResultsFromCloud(cloudId, start, batchSize, version, path, responseType);
            results.addAll(currentPageResults);
            log.info("FetchType: {}, Fetched page [{}], resultSize = {}", new Object[]{responseType, pageNum, currentPageResults.size()});
            if (currentPageResults.size() < batchSize) break;
            start += batchSize;
            ++pageNum;
        }
        return results;
    }

    private <R> List<R> getPagedResultsFromCloud(String cloudId, int start, int limit, String version, String path, TypeReference<PagedResponse<R>> responseType) {
        UriComponentsBuilder uriBuilder = this.getConfluenceUriBuilder(version).pathSegment(new String[]{CLOUD_ID, cloudId}).path(path).queryParam(CONFLUENCE_START_QUERY_PARAM, new Object[]{start}).queryParam(CONFLUENCE_LIMIT_QUERY_PARAM, new Object[]{limit});
        Request request = this.getJsonBuilder(cloudId).url(uriBuilder.toUriString()).get().build();
        PagedResponse<R> response = this.callJsonWithRetries(request, responseType);
        return response.getResults();
    }

    public ServerInstanceCreateResponse sendServerInstanceInfoToMigrationMetadataAggregator(String containerToken, String cloudId, MetadataBatch<ServerInstance> serverInstanceDTO) {
        Request request = this.getJsonBuilder(cloudId, containerToken).url(this.getMigrationMetadataAggregatorUriBuilder(V1).path(MIGRATION_METADATA_AGGREGATOR_SERVER_CREATE_PATH).toUriString()).post(RequestBody.create((MediaType)MediaTypes.APPLICATION_JSON_TYPE, (String)this.gson.toJson(serverInstanceDTO))).build();
        return this.callGsonWithRetries(request, ServerInstanceCreateResponse.class);
    }

    void getContainerDetails(String cloudId, String migrationId, String containerId) {
        Request request = this.getJsonBuilder(cloudId).url(this.getUriBuilder(V1).path(MIGRATION_PATH).pathSegment(new String[]{migrationId}).path(CONTAINER_PATH).pathSegment(new String[]{containerId}).path(STATUS_PATH).toUriString()).get().build();
        this.callWithRetries(request);
    }

    TransferResponseList createTransfers(String cloudId, String migrationId, String containerId, List<String> operationKeys) {
        Request request = this.getJsonBuilder(cloudId).url(this.getUriBuilder(V1).path(MIGRATION_PATH).pathSegment(new String[]{migrationId}).path(CONTAINER_PATH).pathSegment(new String[]{containerId}).path(TRANSFER_PATH).toUriString()).post(RequestBody.create((String)Jsons.valueAsString(operationKeys), (MediaType)MediaTypes.APPLICATION_JSON_TYPE)).build();
        return this.callJsonWithRetries(request, new TypeReference<TransferResponseList>(){});
    }

    void updateTransferProgress(String cloudId, String migrationId, String transferId, TransferProgressRequest transferProgressRequest) {
        Request request = this.getJsonBuilder(cloudId).url(this.getUriBuilder(V1).path(MIGRATION_PATH).pathSegment(new String[]{migrationId}).path(TRANSFER_PATH).pathSegment(new String[]{transferId}).path(PROGRESS_PATH).toUriString()).put(RequestBody.create((String)Jsons.valueAsString(transferProgressRequest), (MediaType)MediaTypes.APPLICATION_JSON_TYPE)).build();
        this.callWithRetries(request);
    }

    void updateTransferStatus(String cloudId, String migrationId, String transferId, TransferStatus migrationStatus, @Nullable String statusMessage) {
        TransferStatusUpdateRequest transferStatusUpdateRequest = new TransferStatusUpdateRequest(migrationStatus, statusMessage);
        Request request = this.getJsonBuilder(cloudId).url(this.getUriBuilder(V1).path(MIGRATION_PATH).pathSegment(new String[]{migrationId}).path(TRANSFER_PATH).pathSegment(new String[]{transferId}).path(STATUS_PATH).toUriString()).put(RequestBody.create((String)this.gson.toJson((Object)transferStatusUpdateRequest), (MediaType)MediaTypes.APPLICATION_JSON_TYPE)).build();
        this.callWithRetries(request);
    }

    public void sendMapiTaskStatus(String jobId, String taskId, String cloudId, String containerToken, List<MapiStatusDto> mapiStatusDtoList) {
        Request request = this.getJsonBuilder(cloudId, containerToken).url(this.getMapiUriBuilder(V1).path(MAPI_PATH).pathSegment(new String[]{jobId}).pathSegment(new String[]{"tasks", taskId}).path("status").toUriString()).post(RequestBody.create((String)Jsons.valueAsString(mapiStatusDtoList), (MediaType)MediaTypes.APPLICATION_JSON_TYPE)).build();
        this.callWithRetries(request);
    }

    public JsonElement sendSpaceMetadataToMigrationMetadataAggregator(String containerToken, String cloudId, MetadataBatch<SpaceMetadataDTO> spaceMetaData) {
        Request request = this.getJsonBuilder(cloudId, containerToken).url(this.getMigrationMetadataAggregatorUriBuilder(V1).path(MIGRATION_METADATA_AGGREGATOR_SPACE_METADATA_UPSERT_PATH).toUriString()).post(RequestBody.create((MediaType)MediaTypes.APPLICATION_JSON_TYPE, (String)this.gson.toJson(spaceMetaData))).build();
        return this.callGsonWithRetries(request, JsonElement.class);
    }
}

