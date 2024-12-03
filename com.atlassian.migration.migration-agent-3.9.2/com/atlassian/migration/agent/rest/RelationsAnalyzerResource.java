/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugins.rest.common.security.jersey.AdminOnlyResourceFilter
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.UrlMode
 *  com.sun.jersey.spi.container.ResourceFilters
 *  javax.annotation.ParametersAreNonnullByDefault
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.GET
 *  javax.ws.rs.PUT
 *  javax.ws.rs.Path
 *  javax.ws.rs.Produces
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 *  lombok.Generated
 *  org.apache.commons.lang3.StringUtils
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.migration.agent.rest;

import com.atlassian.migration.MigrationDarkFeaturesManager;
import com.atlassian.migration.agent.rest.Responses;
import com.atlassian.migration.agent.service.portfolioanalyzer.service.RelationsAnalyzerRunner;
import com.atlassian.plugins.rest.common.security.jersey.AdminOnlyResourceFilter;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.UrlMode;
import com.sun.jersey.spi.container.ResourceFilters;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import lombok.Generated;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.annotate.JsonProperty;

@ParametersAreNonnullByDefault
@Path(value="graph")
@ResourceFilters(value={AdminOnlyResourceFilter.class})
@Consumes(value={"application/json"})
@Produces(value={"application/json"})
public class RelationsAnalyzerResource {
    private final RelationsAnalyzerRunner runner;
    private final MigrationDarkFeaturesManager migrationDarkFeaturesManager;
    private final ApplicationProperties applicationProperties;

    @PUT
    @Path(value="/")
    public Response scheduleJob() {
        if (!this.migrationDarkFeaturesManager.isRelationsAnalysisEnabled()) {
            return Response.status((int)404).build();
        }
        return (Response)this.runner.scheduleJob().fold(it -> Response.status((Response.Status)Response.Status.CONFLICT).entity((Object)it.getMessage()).build(), it -> Response.status((Response.Status)Response.Status.ACCEPTED).build());
    }

    @GET
    @Path(value="/")
    public Response getJobStatus() {
        if (!this.migrationDarkFeaturesManager.isRelationsAnalysisEnabled()) {
            return Response.status((int)404).build();
        }
        switch (this.runner.getJobStatus()) {
            case COMPLETED: {
                return Response.ok((Object)new ConnectionGraphFileStatus(RelationsAnalyzerRunner.Status.COMPLETED, "Completed. Download relations assessment zip: " + this.getZipUrl())).build();
            }
            case IN_PROGRESS: {
                return Response.ok((Object)new ConnectionGraphFileStatus(RelationsAnalyzerRunner.Status.IN_PROGRESS, "Relations assessment is in progress.")).build();
            }
            case ERROR: {
                return Response.ok((Object)new ConnectionGraphFileStatus(RelationsAnalyzerRunner.Status.ERROR, "Error during relations assessment job. Download relations assessment zip which contains error log: " + this.getZipUrl())).build();
            }
            case UNKNOWN: {
                return Response.ok((Object)new ConnectionGraphFileStatus(RelationsAnalyzerRunner.Status.UNKNOWN, "Status unknown. Schedule new job or check logs.")).build();
            }
        }
        return Response.ok((Object)((Object)this.runner.getJobStatus())).build();
    }

    @GET
    @Path(value="/zip")
    public Response getZip() {
        if (!this.migrationDarkFeaturesManager.isRelationsAnalysisEnabled()) {
            return Response.status((int)404).build();
        }
        RelationsAnalyzerRunner.Status status = this.runner.getJobStatus();
        if (status == RelationsAnalyzerRunner.Status.COMPLETED || status == RelationsAnalyzerRunner.Status.ERROR) {
            return Responses.okStreamingFile(this.runner.getZipFilePath());
        }
        return Response.status((Response.Status)Response.Status.NOT_FOUND).entity((Object)"Relations assessment zip file is not available. Check assessment status.").build();
    }

    private String getZipUrl() {
        return StringUtils.removeEnd((String)this.applicationProperties.getBaseUrl(UrlMode.ABSOLUTE), (String)"/") + "/rest/migration/1.0/graph/zip";
    }

    @Generated
    public RelationsAnalyzerResource(RelationsAnalyzerRunner runner, MigrationDarkFeaturesManager migrationDarkFeaturesManager, ApplicationProperties applicationProperties) {
        this.runner = runner;
        this.migrationDarkFeaturesManager = migrationDarkFeaturesManager;
        this.applicationProperties = applicationProperties;
    }

    public static class ConnectionGraphFileStatus {
        @JsonProperty
        private final RelationsAnalyzerRunner.Status status;
        @JsonProperty
        private final String message;

        public ConnectionGraphFileStatus(@JsonProperty(value="status") RelationsAnalyzerRunner.Status status, @JsonProperty(value="message") String message) {
            this.status = status;
            this.message = message;
        }

        @Generated
        public RelationsAnalyzerRunner.Status getStatus() {
            return this.status;
        }

        @Generated
        public String getMessage() {
            return this.message;
        }

        @Generated
        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof ConnectionGraphFileStatus)) {
                return false;
            }
            ConnectionGraphFileStatus other = (ConnectionGraphFileStatus)o;
            if (!other.canEqual(this)) {
                return false;
            }
            RelationsAnalyzerRunner.Status this$status = this.getStatus();
            RelationsAnalyzerRunner.Status other$status = other.getStatus();
            if (this$status == null ? other$status != null : !((Object)((Object)this$status)).equals((Object)other$status)) {
                return false;
            }
            String this$message = this.getMessage();
            String other$message = other.getMessage();
            return !(this$message == null ? other$message != null : !this$message.equals(other$message));
        }

        @Generated
        protected boolean canEqual(Object other) {
            return other instanceof ConnectionGraphFileStatus;
        }

        @Generated
        public int hashCode() {
            int PRIME = 59;
            int result = 1;
            RelationsAnalyzerRunner.Status $status = this.getStatus();
            result = result * 59 + ($status == null ? 43 : ((Object)((Object)$status)).hashCode());
            String $message = this.getMessage();
            result = result * 59 + ($message == null ? 43 : $message.hashCode());
            return result;
        }

        @Generated
        public String toString() {
            return "RelationsAnalyzerResource.ConnectionGraphFileStatus(status=" + (Object)((Object)this.getStatus()) + ", message=" + this.getMessage() + ")";
        }
    }
}

