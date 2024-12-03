/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.DELETE
 *  javax.ws.rs.GET
 *  javax.ws.rs.POST
 *  javax.ws.rs.Path
 *  javax.ws.rs.Produces
 *  javax.ws.rs.core.CacheControl
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$ResponseBuilder
 *  javax.ws.rs.core.Response$Status
 *  org.codehaus.jackson.annotate.JsonAutoDetect
 *  org.codehaus.jackson.annotate.JsonProperty
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugins.helptips.rest;

import com.atlassian.plugins.helptips.HelpTipManager;
import com.atlassian.sal.usercompatibility.UserManager;
import java.util.Set;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Response;
import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path(value="/tips")
@Consumes(value={"application/json"})
@Produces(value={"application/json"})
public class HelpTipResource {
    private static final Logger log = LoggerFactory.getLogger(HelpTipResource.class);
    private final UserManager userManager;
    private final HelpTipManager helpTipManager;

    public HelpTipResource(UserManager userManager, HelpTipManager helpTipManager) {
        this.userManager = userManager;
        this.helpTipManager = helpTipManager;
    }

    @GET
    public Response index() {
        String userKey = this.userManager.getRemoteUserKey().getStringValue();
        Set<String> dismissedTips = this.helpTipManager.getDismissedTips(userKey);
        return HelpTipResource.withNoCache(Response.ok(dismissedTips));
    }

    @POST
    public Response dismiss(Tooltip tooltip) {
        if (tooltip == null || tooltip.id == null) {
            return HelpTipResource.withNoCache(Response.status((Response.Status)Response.Status.BAD_REQUEST));
        }
        try {
            String userKey = this.userManager.getRemoteUserKey().getStringValue();
            this.helpTipManager.dismissTip(userKey, tooltip.id);
            return HelpTipResource.withNoCache(Response.noContent());
        }
        catch (IllegalArgumentException e) {
            log.error("dismissal of help tip failed");
            log.warn("More details", (Throwable)e);
            return HelpTipResource.withNoCache(Response.status((Response.Status)Response.Status.BAD_REQUEST));
        }
        catch (Exception ex) {
            log.error("dismissal of help tip failed");
            log.warn("More details: ", (Throwable)ex);
            return HelpTipResource.withNoCache(Response.status((Response.Status)Response.Status.INTERNAL_SERVER_ERROR));
        }
    }

    @DELETE
    public Response undismiss(Tooltip tooltip) {
        if (tooltip == null || tooltip.id == null) {
            return HelpTipResource.withNoCache(Response.status((Response.Status)Response.Status.BAD_REQUEST));
        }
        try {
            String userKey = this.userManager.getRemoteUserKey().getStringValue();
            this.helpTipManager.undismissTip(userKey, tooltip.id);
            return HelpTipResource.withNoCache(Response.noContent());
        }
        catch (IllegalArgumentException e) {
            log.error("undismissal of help tip failed", (Throwable)e);
            return HelpTipResource.withNoCache(Response.status((Response.Status)Response.Status.BAD_REQUEST));
        }
        catch (Exception ex) {
            log.error("undismissal of help tip failed", (Throwable)ex);
            return HelpTipResource.withNoCache(Response.status((Response.Status)Response.Status.INTERNAL_SERVER_ERROR));
        }
    }

    private static Response withNoCache(Response.ResponseBuilder responseBuilder) {
        return responseBuilder.cacheControl(CacheControl.valueOf((String)"no-cache")).build();
    }

    @JsonAutoDetect
    public static class Tooltip {
        @JsonProperty
        public String id;
    }
}

