/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugins.rest.common.security.AnonymousAllowed
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.DefaultValue
 *  javax.ws.rs.GET
 *  javax.ws.rs.Path
 *  javax.ws.rs.Produces
 *  javax.ws.rs.QueryParam
 */
package com.atlassian.confluence.plugins.mobile.rest;

import com.atlassian.confluence.plugins.mobile.rest.RecentContentDto;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

public interface StreamResourceInterface {
    public static final String PARAM_NEXT_PAGE_OFFSET = "nextPageOffset";
    public static final String PARAM_URL_STRATEGY = "urlStrategy";

    @GET
    @Path(value="/recentblogs")
    @Consumes(value={"application/json", "application/x-www-form-urlencoded"})
    @Produces(value={"application/json"})
    @AnonymousAllowed
    public RecentContentDto getRecentlyAddedBlogs(@QueryParam(value="nextPageOffset") int var1, @QueryParam(value="urlStrategy") @DefaultValue(value="desktop") String var2, @QueryParam(value="token") @DefaultValue(value="0") long var3);

    @GET
    @Path(value="/network")
    @Consumes(value={"application/json", "application/x-www-form-urlencoded"})
    @Produces(value={"application/json"})
    @AnonymousAllowed
    public RecentContentDto getRecentlyAddedFromNetwork(@QueryParam(value="nextPageOffset") int var1, @QueryParam(value="urlStrategy") @DefaultValue(value="desktop") String var2, @QueryParam(value="token") @DefaultValue(value="0") long var3);
}

