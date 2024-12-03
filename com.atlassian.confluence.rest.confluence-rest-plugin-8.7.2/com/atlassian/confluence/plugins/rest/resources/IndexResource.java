/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.confluence.api.service.accessmode.ReadOnlyAccessAllowed
 *  com.atlassian.confluence.search.IndexManager
 *  com.atlassian.confluence.search.ReIndexOption
 *  com.atlassian.confluence.search.ReIndexTask
 *  com.atlassian.plugins.rest.common.security.jersey.AdminOnlyResourceFilter
 *  com.atlassian.plugins.rest.common.security.jersey.SysadminOnlyResourceFilter
 *  com.atlassian.spring.container.ContainerManager
 *  com.sun.jersey.spi.container.ResourceFilters
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.GET
 *  javax.ws.rs.POST
 *  javax.ws.rs.PUT
 *  javax.ws.rs.Path
 *  javax.ws.rs.Produces
 *  javax.ws.rs.QueryParam
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.rest.resources;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.confluence.api.service.accessmode.ReadOnlyAccessAllowed;
import com.atlassian.confluence.plugins.rest.resources.AbstractResource;
import com.atlassian.confluence.plugins.rest.service.ReIndexService;
import com.atlassian.confluence.search.IndexManager;
import com.atlassian.confluence.search.ReIndexOption;
import com.atlassian.confluence.search.ReIndexTask;
import com.atlassian.plugins.rest.common.security.jersey.AdminOnlyResourceFilter;
import com.atlassian.plugins.rest.common.security.jersey.SysadminOnlyResourceFilter;
import com.atlassian.spring.container.ContainerManager;
import com.sun.jersey.spi.container.ResourceFilters;
import java.util.EnumSet;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path(value="/index")
@Produces(value={"application/json;charset=UTF-8"})
@ResourceFilters(value={SysadminOnlyResourceFilter.class})
public class IndexResource
extends AbstractResource {
    private static final Logger log = LoggerFactory.getLogger(IndexResource.class);
    private final IndexManager indexManager;
    private final ReIndexService reIndexService;

    private IndexResource() {
        this.indexManager = (IndexManager)ContainerManager.getComponent((String)"indexManager", IndexManager.class);
        this.reIndexService = (ReIndexService)ContainerManager.getComponent((String)"indexService", ReIndexService.class);
    }

    @Deprecated(since="8.3.0", forRemoval=true)
    public IndexResource(IndexManager indexManager) {
        this.indexManager = indexManager;
        this.reIndexService = (ReIndexService)ContainerManager.getComponent((String)"indexService", ReIndexService.class);
    }

    public IndexResource(IndexManager indexManager, ReIndexService reIndexService) {
        this.indexManager = indexManager;
        this.reIndexService = reIndexService;
    }

    @Path(value="/reindex")
    @Consumes(value={"application/json"})
    @POST
    @ResourceFilters(value={AdminOnlyResourceFilter.class})
    @ReadOnlyAccessAllowed
    public Response reindex(@QueryParam(value="option") List<String> options, @QueryParam(value="spaceKey") List<String> spaceKeys) throws InterruptedException {
        if (this.reIndexService.isReIndexing()) {
            log.warn("Confluence is re-indexing");
            return Response.status((Response.Status)Response.Status.BAD_REQUEST).entity((Object)"Confluence is re-indexing").build();
        }
        EnumSet reIndexOptions = ReIndexOption.deserialise(options, (boolean)spaceKeys.isEmpty());
        if (this.reIndexService.reindex(spaceKeys, reIndexOptions)) {
            return Response.ok((Object)new ReIndexTaskEntity()).build();
        }
        return Response.status((Response.Status)Response.Status.BAD_REQUEST).build();
    }

    @Path(value="/resetjob")
    @Consumes(value={"application/json"})
    @PUT
    @ResourceFilters(value={AdminOnlyResourceFilter.class})
    @ReadOnlyAccessAllowed
    public Response resetJob() {
        this.reIndexService.resetJobStatus();
        return Response.ok().build();
    }

    @Path(value="/reindex")
    @GET
    public Response reIndexStatus() {
        ReIndexTask indexingTask = this.indexManager.getLastReindexingTask();
        if (indexingTask != null) {
            return Response.ok((Object)ReIndexTaskEntity.from(indexingTask)).build();
        }
        return Response.ok((Object)"Reindex status not available.").build();
    }

    @Path(value="/unindex")
    @Consumes(value={"application/json"})
    @POST
    @ResourceFilters(value={SysadminOnlyResourceFilter.class})
    public Response unIndexAll() {
        this.indexManager.unIndexAll();
        return Response.ok().build();
    }

    @XmlAccessorType(value=XmlAccessType.FIELD)
    static class ReIndexTaskEntity {
        private boolean finished = false;
        private int percentageComplete = 0;
        private String elapsedTime = "0";
        private String remainingTime = "0";
        private int jobID;

        ReIndexTaskEntity() {
        }

        static ReIndexTaskEntity from(ReIndexTask reIndexTask) {
            ReIndexTaskEntity entity = new ReIndexTaskEntity();
            entity.finished = reIndexTask.isFinishedReindexing();
            entity.percentageComplete = reIndexTask.getProgress().getPercentComplete();
            entity.elapsedTime = reIndexTask.getCompactElapsedTime();
            entity.jobID = reIndexTask.getJobID();
            return entity;
        }

        @VisibleForTesting
        public int getJobID() {
            return this.jobID;
        }
    }
}

