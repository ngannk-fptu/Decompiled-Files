/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.compat.api.service.accessmode.ReadOnlyAccessAllowed
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.plugins.rest.common.security.jersey.AdminOnlyResourceFilter
 *  com.sun.jersey.spi.container.ResourceFilters
 *  javax.annotation.Nullable
 *  javax.annotation.ParametersAreNonnullByDefault
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.GET
 *  javax.ws.rs.POST
 *  javax.ws.rs.Path
 *  javax.ws.rs.Produces
 *  javax.ws.rs.QueryParam
 */
package com.atlassian.migration.agent.rest;

import com.atlassian.confluence.compat.api.service.accessmode.ReadOnlyAccessAllowed;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.migration.agent.dto.RequestValidationException;
import com.atlassian.migration.agent.dto.UsersGroupsStatsRequestDto;
import com.atlassian.migration.agent.dto.util.UserMigrationType;
import com.atlassian.migration.agent.model.stats.GlobalEntitiesStats;
import com.atlassian.migration.agent.model.stats.ServerStats;
import com.atlassian.migration.agent.model.stats.UsersGroupsStats;
import com.atlassian.migration.agent.service.StatisticsService;
import com.atlassian.migration.agent.service.analytics.AnalyticsEventBuilder;
import com.atlassian.migration.agent.service.analytics.AnalyticsEventService;
import com.atlassian.plugins.rest.common.security.jersey.AdminOnlyResourceFilter;
import com.sun.jersey.spi.container.ResourceFilters;
import java.util.Collections;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

@ParametersAreNonnullByDefault
@Path(value="stats")
@ReadOnlyAccessAllowed
@ResourceFilters(value={AdminOnlyResourceFilter.class})
@Consumes(value={"application/json"})
@Produces(value={"application/json"})
public class StatisticsResource {
    private final StatisticsService statisticsService;
    private final AnalyticsEventService analyticsEventService;
    private final AnalyticsEventBuilder analyticsEventBuilder;

    public StatisticsResource(StatisticsService statisticsService, AnalyticsEventService analyticsEventService, AnalyticsEventBuilder analyticsEventBuilder) {
        this.statisticsService = statisticsService;
        this.analyticsEventService = analyticsEventService;
        this.analyticsEventBuilder = analyticsEventBuilder;
    }

    @GET
    @Path(value="server")
    public ServerStats getServerStatistics() {
        ServerStats serverStats = this.statisticsService.loadServerStatistics();
        ConfluenceUser confluenceUser = AuthenticatedUserThreadLocal.get();
        this.analyticsEventService.saveAnalyticsEventAsync(() -> this.analyticsEventBuilder.buildCompletedInstanceAnalysisAnalyticsEvent(serverStats, confluenceUser));
        return serverStats;
    }

    @GET
    @Path(value="usersGroups")
    public UsersGroupsStats getUsersGroupsStatistics() {
        return this.statisticsService.getUsersGroupsStatistics(UserMigrationType.ALL, Collections.emptyList());
    }

    @POST
    @Path(value="usersGroups")
    public UsersGroupsStats getUsersGroupsStatisticsForSpace(UsersGroupsStatsRequestDto usersGroupsStatsRequestDto) {
        if (usersGroupsStatsRequestDto.getUserMigrationType().equals((Object)UserMigrationType.SCOPED) && usersGroupsStatsRequestDto.getSpaceKeys().isEmpty()) {
            throw new RequestValidationException("spaceKeys should not be empty for scoped migration");
        }
        return this.statisticsService.getUsersGroupsStatistics(usersGroupsStatsRequestDto.getUserMigrationType(), usersGroupsStatsRequestDto.getSpaceKeys());
    }

    @GET
    @Path(value="globalEntities")
    public GlobalEntitiesStats getGlobalEntitiesStatistics(@Nullable @QueryParam(value="planId") String planId) {
        return this.statisticsService.getGlobalEntitiesStatistics(planId);
    }
}

