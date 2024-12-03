/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  com.atlassian.confluence.api.model.SynchronyRowsCount
 *  com.atlassian.confluence.api.model.eviction.SynchronyDatasetSize
 *  com.atlassian.confluence.api.model.eviction.SynchronyEvictionResult
 *  com.atlassian.confluence.api.service.eviction.SynchronyDataService
 *  com.atlassian.confluence.rest.api.model.HardEvictionParams
 *  com.atlassian.confluence.rest.api.model.SoftEvictionParams
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  javax.annotation.Nullable
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.GET
 *  javax.ws.rs.POST
 *  javax.ws.rs.Path
 *  javax.ws.rs.Produces
 *  javax.ws.rs.QueryParam
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.restapi.experimental.resources;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.confluence.api.model.SynchronyRowsCount;
import com.atlassian.confluence.api.model.eviction.SynchronyDatasetSize;
import com.atlassian.confluence.api.model.eviction.SynchronyEvictionResult;
import com.atlassian.confluence.api.service.eviction.SynchronyDataService;
import com.atlassian.confluence.rest.api.model.HardEvictionParams;
import com.atlassian.confluence.rest.api.model.SoftEvictionParams;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import javax.annotation.Nullable;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ExperimentalApi
@Consumes(value={"application/json"})
@Produces(value={"application/json"})
@Path(value="/collab/history")
public class SynchronyDataEvictionResource {
    private final SynchronyDataService synchronyDataService;
    private final Logger logger = LoggerFactory.getLogger(SynchronyDataEvictionResource.class);

    public SynchronyDataEvictionResource(@ComponentImport SynchronyDataService synchronyDataService) {
        this.synchronyDataService = synchronyDataService;
    }

    @POST
    @Path(value="evict/soft")
    public SynchronyEvictionResult performSoftDataEviction(SoftEvictionParams params) {
        try {
            this.synchronyDataService.softRemoveHistoryOlderThan(params.getThresholdHours(), params.getLimit());
            return SynchronyEvictionResult.ok();
        }
        catch (RuntimeException e) {
            this.logger.error("Soft eviction has been failed", (Throwable)e);
            return SynchronyEvictionResult.failed();
        }
    }

    @POST
    @Path(value="evict/hard")
    public SynchronyEvictionResult performHardDataEviction(HardEvictionParams params) {
        try {
            this.synchronyDataService.hardRemoveHistoryOlderThan(params.getThresholdHours());
            return SynchronyEvictionResult.ok();
        }
        catch (RuntimeException e) {
            this.logger.error("Hard eviction has been failed", (Throwable)e);
            return SynchronyEvictionResult.failed();
        }
    }

    @GET
    @Path(value="count")
    public SynchronyDatasetSize currentDataSize(@Nullable @QueryParam(value="contentId") Long contentId) {
        SynchronyRowsCount dataCount = this.synchronyDataService.currentSynchronyDatasetSize(contentId);
        return new SynchronyDatasetSize(dataCount.getNumberOfEvents(), dataCount.getNumberOfSnapshots());
    }
}

