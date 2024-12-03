/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugins.rest.common.security.UnlicensedSiteAccess
 *  com.atlassian.whisper.plugin.api.CurrentUserPropertyService
 *  com.google.common.base.Strings
 *  javax.inject.Inject
 *  javax.ws.rs.DELETE
 *  javax.ws.rs.GET
 *  javax.ws.rs.PUT
 *  javax.ws.rs.Path
 *  javax.ws.rs.Produces
 *  javax.ws.rs.QueryParam
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 */
package com.atlassian.whisper.plugin.rest;

import com.atlassian.plugins.rest.common.security.UnlicensedSiteAccess;
import com.atlassian.whisper.plugin.api.CurrentUserPropertyService;
import com.atlassian.whisper.plugin.rest.PropertyRest;
import com.google.common.base.Strings;
import javax.inject.Inject;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

@Path(value="properties")
@Produces(value={"application/json"})
@UnlicensedSiteAccess
public class PropertiesResource {
    private final CurrentUserPropertyService currentUserPropertyService;

    @Inject
    public PropertiesResource(CurrentUserPropertyService currentUserPropertyService) {
        this.currentUserPropertyService = currentUserPropertyService;
    }

    @GET
    public Response getValue(@QueryParam(value="key") String key) {
        if (Strings.isNullOrEmpty((String)key)) {
            return this.badRequest();
        }
        String value = this.currentUserPropertyService.getValue(key);
        return this.okProperty(key, value);
    }

    @PUT
    public Response setValue(PropertyRest property) {
        if (property == null || Strings.isNullOrEmpty((String)property.key) || Strings.isNullOrEmpty((String)property.value)) {
            return this.badRequest();
        }
        this.currentUserPropertyService.setValue(property.key, property.value);
        return this.noContent();
    }

    @DELETE
    public Response deleteProperty(PropertyRest property) {
        if (property == null || property.key == null) {
            return this.badRequest();
        }
        this.currentUserPropertyService.deleteValue(property.key);
        return this.noContent();
    }

    private Response badRequest() {
        return Response.status((Response.Status)Response.Status.BAD_REQUEST).build();
    }

    private Response noContent() {
        return Response.noContent().build();
    }

    private Response okProperty(String key, String value) {
        return Response.ok((Object)new PropertyRest(key, value)).build();
    }
}

