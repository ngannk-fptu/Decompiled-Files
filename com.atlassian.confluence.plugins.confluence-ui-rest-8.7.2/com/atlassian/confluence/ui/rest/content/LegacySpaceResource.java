/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.legacyapi.service.content.SpaceLabelService
 *  com.atlassian.plugins.rest.common.security.AnonymousAllowed
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.DELETE
 *  javax.ws.rs.Path
 *  javax.ws.rs.PathParam
 *  javax.ws.rs.Produces
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 */
package com.atlassian.confluence.ui.rest.content;

import com.atlassian.confluence.legacyapi.service.content.SpaceLabelService;
import com.atlassian.confluence.ui.rest.content.LegacyRestHelper;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import java.util.Map;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

@Deprecated
@Path(value="/space")
@AnonymousAllowed
@Consumes(value={"application/json"})
@Produces(value={"application/json"})
public class LegacySpaceResource {
    private final SpaceLabelService spaceLabelService;

    public LegacySpaceResource(SpaceLabelService spaceLabelService) {
        this.spaceLabelService = spaceLabelService;
    }

    @DELETE
    @Path(value="/{key}/label/{labelId}")
    public Response deleteLabel(@PathParam(value="key") String spaceKey, @PathParam(value="labelId") Long labelId) {
        try {
            this.spaceLabelService.removeLabel(spaceKey, labelId.longValue());
            return Response.status((Response.Status)Response.Status.NO_CONTENT).build();
        }
        catch (IllegalArgumentException e) {
            Map<String, Object> result = LegacyRestHelper.createFailureResultMap(e);
            return Response.ok(result).status(Response.Status.FORBIDDEN).build();
        }
    }
}

