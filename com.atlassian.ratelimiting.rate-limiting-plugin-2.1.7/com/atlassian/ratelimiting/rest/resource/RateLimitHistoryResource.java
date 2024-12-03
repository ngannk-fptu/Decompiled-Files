/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugins.rest.common.security.AnonymousAllowed
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.sal.api.permission.PermissionEnforcer
 *  com.sun.jersey.spi.resource.Singleton
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.DefaultValue
 *  javax.ws.rs.GET
 *  javax.ws.rs.Path
 *  javax.ws.rs.Produces
 *  javax.ws.rs.QueryParam
 *  javax.ws.rs.core.Response
 */
package com.atlassian.ratelimiting.rest.resource;

import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import com.atlassian.ratelimiting.history.RateLimitingReportOrder;
import com.atlassian.ratelimiting.history.RateLimitingReportSearchRequest;
import com.atlassian.ratelimiting.history.RateLimitingReportSearchResult;
import com.atlassian.ratelimiting.history.RateLimitingReportService;
import com.atlassian.ratelimiting.page.Page;
import com.atlassian.ratelimiting.page.PageRequest;
import com.atlassian.ratelimiting.rest.api.RestPage;
import com.atlassian.ratelimiting.rest.api.RestUserRateLimitingReport;
import com.atlassian.ratelimiting.rest.utils.RestUtils;
import com.atlassian.ratelimiting.user.UserService;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.permission.PermissionEnforcer;
import com.sun.jersey.spi.resource.Singleton;
import java.time.ZonedDateTime;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

@AnonymousAllowed
@Consumes(value={"application/json"})
@Path(value="admin/rate-limit/history")
@Produces(value={"application/json;charset=UTF-8"})
@Singleton
public class RateLimitHistoryResource {
    private final RateLimitingReportService rateLimitingReportService;
    private final I18nResolver i18nResolver;
    private final UserService userService;
    private final PermissionEnforcer permissionEnforcer;

    public RateLimitHistoryResource(I18nResolver i18nResolver, RateLimitingReportService rateLimitingReportService, UserService userService, PermissionEnforcer permissionEnforcer) {
        this.rateLimitingReportService = rateLimitingReportService;
        this.i18nResolver = i18nResolver;
        this.userService = userService;
        this.permissionEnforcer = permissionEnforcer;
    }

    @GET
    public Response getHistory(@QueryParam(value="filter") List<String> userFilter, @QueryParam(value="orderBy") String orderString, @QueryParam(value="startTime") String startTimeString, @QueryParam(value="finishTime") String finishTimeString, @DefaultValue(value="0") @QueryParam(value="page") int pageNumber, @DefaultValue(value="20") @QueryParam(value="size") int pageSize) {
        this.permissionEnforcer.enforceSystemAdmin();
        RateLimitingReportOrder order = RestUtils.validateHistoryRequest(orderString, this.i18nResolver);
        ZonedDateTime startTime = RestUtils.validateDateString("startTime", startTimeString, this.i18nResolver);
        ZonedDateTime finishTime = RestUtils.validateDateString("finishTime", finishTimeString, this.i18nResolver);
        List<String> userKeyList = RestUtils.lookupUserKeysForUsernames(userFilter, this.userService);
        RateLimitingReportSearchRequest searchRequest = RateLimitingReportSearchRequest.builder().userFilterList(userKeyList).sortOrder(order).startTime(startTime).finishTime(finishTime).pageRequest(new PageRequest(pageNumber, pageSize)).build();
        Page<RateLimitingReportSearchResult> page = this.rateLimitingReportService.getHistoryReport(searchRequest);
        return Response.ok(new RestPage<RestUserRateLimitingReport>(page.map(this::mapSearchResult))).build();
    }

    private RestUserRateLimitingReport mapSearchResult(RateLimitingReportSearchResult searchResult) {
        return new RestUserRateLimitingReport(searchResult.getUserRateLimitingReport(), searchResult.getUserProfile());
    }
}

