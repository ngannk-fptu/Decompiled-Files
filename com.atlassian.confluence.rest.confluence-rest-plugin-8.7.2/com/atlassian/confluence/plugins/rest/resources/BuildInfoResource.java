/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.persistence.VersionHistoryDao
 *  com.atlassian.confluence.setup.BuildInformation
 *  com.atlassian.plugins.rest.common.security.AnonymousAllowed
 *  javax.ws.rs.GET
 *  javax.ws.rs.Path
 *  javax.ws.rs.Produces
 *  javax.ws.rs.QueryParam
 *  javax.ws.rs.core.Response
 *  org.apache.commons.lang3.math.NumberUtils
 */
package com.atlassian.confluence.plugins.rest.resources;

import com.atlassian.confluence.core.persistence.VersionHistoryDao;
import com.atlassian.confluence.plugins.rest.entities.BuildInfoEntity;
import com.atlassian.confluence.plugins.rest.entities.VersionHistoryEntity;
import com.atlassian.confluence.plugins.rest.entities.VersionHistoryEntityList;
import com.atlassian.confluence.setup.BuildInformation;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import java.util.stream.Collectors;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import org.apache.commons.lang3.math.NumberUtils;

@Path(value="/buildInfo")
@AnonymousAllowed
public class BuildInfoResource {
    private VersionHistoryDao versionHistoryDao;

    public BuildInfoResource(VersionHistoryDao versionHistoryDao) {
        this.versionHistoryDao = versionHistoryDao;
    }

    @GET
    @Produces(value={"application/json"})
    public Response getBuildId() {
        BuildInfoEntity entity = new BuildInfoEntity(BuildInformation.INSTANCE);
        return Response.ok((Object)entity).build();
    }

    @GET
    @Produces(value={"application/json"})
    @Path(value="/history")
    public Response getUpgradeHistory(@QueryParam(value="start-index") String startIndexString, @QueryParam(value="max-results") String maxResultsString) {
        int start = NumberUtils.toInt((String)startIndexString, (int)0);
        int maxResults = NumberUtils.toInt((String)maxResultsString, (int)90);
        VersionHistoryEntityList versionHistoryEntityList = new VersionHistoryEntityList(this.versionHistoryDao.getUpgradeHistory(start, maxResults).stream().map(VersionHistoryEntity::new).collect(Collectors.toList()));
        return Response.ok((Object)versionHistoryEntityList).build();
    }
}

