/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  javax.annotation.ParametersAreNonnullByDefault
 *  okhttp3.Interceptor
 *  okhttp3.MediaType
 *  okhttp3.OkHttpClient
 *  okhttp3.Request
 *  okhttp3.RequestBody
 *  org.codehaus.jackson.type.TypeReference
 *  org.springframework.web.util.UriComponentsBuilder
 */
package com.atlassian.migration.agent.service.user;

import com.atlassian.migration.agent.config.MigrationAgentConfiguration;
import com.atlassian.migration.agent.json.Jsons;
import com.atlassian.migration.agent.okhttp.HttpService;
import com.atlassian.migration.agent.okhttp.MediaTypes;
import com.atlassian.migration.agent.okhttp.OKHttpProxyBuilder;
import com.atlassian.migration.agent.okhttp.ServiceErrorCodeHandler;
import com.atlassian.migration.agent.service.impl.StargateHelper;
import com.atlassian.migration.agent.service.impl.UserAgentInterceptor;
import com.atlassian.migration.agent.service.stepexecutor.space.CreateTombstoneAccountRequest;
import com.atlassian.migration.agent.service.stepexecutor.space.PublishTombstoneMappingRequest;
import com.atlassian.migration.agent.service.stepexecutor.space.TombstoneAccountsResponse;
import com.atlassian.migration.agent.service.user.CloudEditionCheckResponse;
import com.atlassian.migration.agent.service.user.GroupConflictsCheckRequest;
import com.atlassian.migration.agent.service.user.GroupsConflictCheckResponse;
import com.atlassian.migration.agent.service.user.MigrationResponse;
import com.atlassian.migration.agent.service.user.UsersMigrationService;
import com.atlassian.migration.agent.service.user.UsersMigrationStatusResponse;
import com.atlassian.migration.agent.service.user.request.v2.UsersMigrationV2Request;
import com.google.common.annotations.VisibleForTesting;
import java.util.Collections;
import java.util.concurrent.TimeUnit;
import javax.annotation.ParametersAreNonnullByDefault;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.codehaus.jackson.type.TypeReference;
import org.springframework.web.util.UriComponentsBuilder;

@ParametersAreNonnullByDefault
public class DefaultUsersMigrationService
implements UsersMigrationService {
    private static final String USERS_TASK_PATH = "/{taskId}";
    private static final String GROUPS_PATH = "/groupsconflict";
    private static final String GROUPS_TASK_PATH = "/groupsconflict/{taskId}";
    private static final String CLOUD_EDITION_PATH = "/cloudedition";
    private static final String USER_MIGRATION_V2_PATH = "/migration/v2";
    private final HttpService httpService;
    private final MigrationAgentConfiguration configuration;

    public DefaultUsersMigrationService(MigrationAgentConfiguration configuration, UserAgentInterceptor userAgentInterceptor, OKHttpProxyBuilder okHttpProxyBuilder) {
        this(new HttpService(() -> DefaultUsersMigrationService.buildHttpClient(userAgentInterceptor, okHttpProxyBuilder), new ServiceErrorCodeHandler()), configuration);
    }

    @VisibleForTesting
    DefaultUsersMigrationService(HttpService httpService, MigrationAgentConfiguration configuration) {
        this.httpService = httpService;
        this.configuration = configuration;
    }

    private static OkHttpClient buildHttpClient(UserAgentInterceptor userAgentInterceptor, OKHttpProxyBuilder okHttpProxyBuilder) {
        return okHttpProxyBuilder.getProxyBuilder().connectTimeout(30L, TimeUnit.SECONDS).followRedirects(true).followSslRedirects(true).readTimeout(300L, TimeUnit.SECONDS).addInterceptor((Interceptor)userAgentInterceptor).build();
    }

    private UriComponentsBuilder getUriBuilder() {
        return UriComponentsBuilder.fromHttpUrl((String)this.configuration.getUserMigrationServiceBaseUrl());
    }

    @Override
    public String initiateUsersAndGroupsMigrationV2(String containerToken, UsersMigrationV2Request usersAndGroups) {
        Request request = StargateHelper.requestBuilder(containerToken, this.configuration.isBypassStargate()).addHeader("Accept", "application/json").url(this.getUriBuilder().path(USER_MIGRATION_V2_PATH).toUriString()).post(RequestBody.create((MediaType)MediaTypes.APPLICATION_JSON_TYPE, (String)Jsons.valueAsString(usersAndGroups))).build();
        return this.httpService.callJson((Request)request, new TypeReference<MigrationResponse>(){}).taskId;
    }

    @Override
    public UsersMigrationStatusResponse getUsersAndGroupsMigrationProgress(String containerToken, String taskId) {
        Request request = StargateHelper.requestBuilder(containerToken, this.configuration.isBypassStargate()).addHeader("Accept", "application/json").url(this.getUriBuilder().path("/migration").path(USERS_TASK_PATH).buildAndExpand(new Object[]{taskId}).toUriString()).get().build();
        return this.httpService.callJson(request, new TypeReference<UsersMigrationStatusResponse>(){});
    }

    @Override
    public String startGroupConflictsCheck(String containerToken, GroupConflictsCheckRequest groupNamesCheckRequest) {
        Request request = StargateHelper.requestBuilder(containerToken, this.configuration.isBypassStargate()).addHeader("Accept", "application/json").url(this.getUriBuilder().path(GROUPS_PATH).toUriString()).post(RequestBody.create((MediaType)MediaTypes.APPLICATION_JSON_TYPE, (String)Jsons.valueAsString(groupNamesCheckRequest))).build();
        return this.httpService.callJson((Request)request, new TypeReference<MigrationResponse>(){}).taskId;
    }

    @Override
    public GroupsConflictCheckResponse getGroupConflictsCheckStatus(String containerToken, String taskId) {
        Request request = StargateHelper.requestBuilder(containerToken, this.configuration.isBypassStargate()).addHeader("Accept", "application/json").url(this.getUriBuilder().path(GROUPS_TASK_PATH).buildAndExpand(new Object[]{taskId}).toUriString()).get().build();
        return this.httpService.callJson(request, new TypeReference<GroupsConflictCheckResponse>(){});
    }

    @Override
    public CloudEditionCheckResponse getCloudEditionCheck(String containerToken) {
        Request request = StargateHelper.requestBuilder(containerToken, this.configuration.isBypassStargate()).addHeader("Accept", "application/json").url(this.getUriBuilder().path(CLOUD_EDITION_PATH).toUriString()).post(RequestBody.create((byte[])new byte[0], null)).build();
        return this.httpService.callJson(request, new TypeReference<CloudEditionCheckResponse>(){});
    }

    @Override
    public UsersMigrationStatusResponse cancelUsersAndGroupsMigration(String containerToken, String taskId) {
        Request request = StargateHelper.requestBuilder(containerToken, this.configuration.isBypassStargate()).addHeader("Accept", "application/json").url(this.getUriBuilder().path(USERS_TASK_PATH).buildAndExpand(new Object[]{taskId}).toUriString()).delete().build();
        return this.httpService.callJson(request, new TypeReference<UsersMigrationStatusResponse>(){});
    }

    @Override
    public TombstoneAccountsResponse createTombstoneAccounts(String containerToken, int numOfUsers, CreateTombstoneAccountRequest createTombstoneAccountRequest) {
        Request request = StargateHelper.requestBuilder(containerToken, this.configuration.isBypassStargate()).addHeader("Accept", "application/json").url(this.getUriBuilder().path("/tombstones/confluence/").path(Integer.toString(numOfUsers)).toUriString()).post(RequestBody.create((String)Jsons.valueAsString(createTombstoneAccountRequest), (MediaType)MediaTypes.APPLICATION_JSON_TYPE)).build();
        return this.httpService.callJson(request, new TypeReference<TombstoneAccountsResponse>(){});
    }

    @Override
    public void publishTombstoneMappings(String containerToken, PublishTombstoneMappingRequest publishTombstoneMappingRequest) {
        Request request = StargateHelper.requestBuilder(containerToken, this.configuration.isBypassStargate()).addHeader("Accept", "application/json").url(this.getUriBuilder().path("/tombstones/publish/confluence").toUriString()).put(RequestBody.create((String)Jsons.valueAsString(publishTombstoneMappingRequest), (MediaType)MediaTypes.APPLICATION_JSON_TYPE)).build();
        this.httpService.call(request, Collections.emptySet());
    }
}

