/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugins.rest.common.security.AnonymousAllowed
 *  javax.ws.rs.GET
 *  javax.ws.rs.Path
 *  javax.ws.rs.Produces
 *  javax.ws.rs.core.Response
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.audit.database.usage.rest.v1;

import com.atlassian.audit.ao.dao.AuditEntityDao;
import com.atlassian.audit.rest.model.AuditDatabaseUsageJson;
import com.atlassian.audit.rest.model.ResponseErrorJson;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.math.BigDecimal;
import java.math.RoundingMode;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@OpenAPIDefinition(info=@Info(title="Audit Database Usage", version="1.0.0", description="This is a draft of the proposed cross-product APIs, which will be supported by Bitbucket, Confluence and Jira. The root path is /rest/auditing/1.0"))
@Path(value="/statistics/database")
@Produces(value={"application/json"})
@AnonymousAllowed
public class AuditDatabaseUsageRestResource {
    private static final Logger log = LoggerFactory.getLogger(AuditDatabaseUsageRestResource.class);
    private final AuditEntityDao auditEntityDao;

    public AuditDatabaseUsageRestResource(AuditEntityDao auditEntityDao) {
        this.auditEntityDao = auditEntityDao;
    }

    @GET
    @Path(value="usage")
    @Operation(summary="Check database storage usage", tags={"audit", "database"})
    @ApiResponses(value={@ApiResponse(responseCode="200", description="Successful operation", content={@Content(schema=@Schema(implementation=AuditDatabaseUsageJson.class))}), @ApiResponse(responseCode="400", description="Bad request", content={@Content(array=@ArraySchema(schema=@Schema(implementation=ResponseErrorJson.class)))})})
    public Response getUsage() {
        try {
            double allowedCount = Long.getLong("plugin.audit.db.limit.rows", 10000000L).longValue();
            double actualCount = this.auditEntityDao.fastCountEstimate();
            double percentage = actualCount / allowedCount;
            double roundedPercentage = BigDecimal.valueOf(percentage).setScale(2, RoundingMode.HALF_UP).doubleValue();
            return Response.ok((Object)new AuditDatabaseUsageJson(roundedPercentage)).build();
        }
        catch (RuntimeException e) {
            log.error("Unexpected exception :", (Throwable)e);
            return Response.serverError().build();
        }
    }
}

