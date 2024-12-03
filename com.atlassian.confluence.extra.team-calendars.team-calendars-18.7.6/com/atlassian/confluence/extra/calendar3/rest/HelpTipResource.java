/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.user.User
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.DELETE
 *  javax.ws.rs.GET
 *  javax.ws.rs.POST
 *  javax.ws.rs.Path
 *  javax.ws.rs.Produces
 *  javax.ws.rs.core.CacheControl
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlRootElement
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.extra.calendar3.rest;

import com.atlassian.confluence.extra.calendar3.HelpTipManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.user.User;
import java.util.Collection;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Response;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path(value="tips")
@Consumes(value={"application/json"})
@Produces(value={"application/json"})
public class HelpTipResource {
    private static final Logger LOG = LoggerFactory.getLogger(HelpTipResource.class);
    private final HelpTipManager helpTipManager;

    public HelpTipResource(HelpTipManager helpTipManager) {
        this.helpTipManager = helpTipManager;
    }

    @GET
    public Response index() {
        ConfluenceUser user = AuthenticatedUserThreadLocal.get();
        Collection<String> dismissedTips = this.helpTipManager.getDismissedTips((User)user);
        return Response.ok(dismissedTips).cacheControl(this.cacheControlNoCache()).build();
    }

    @POST
    public Response dismiss(Tooltip tooltip) {
        if (tooltip == null || tooltip.id == null) {
            return Response.status((Response.Status)Response.Status.BAD_REQUEST).cacheControl(this.cacheControlNoCache()).build();
        }
        try {
            ConfluenceUser user = AuthenticatedUserThreadLocal.get();
            this.helpTipManager.dismissTip((User)user, tooltip.id);
            return Response.noContent().cacheControl(this.cacheControlNoCache()).build();
        }
        catch (IllegalArgumentException e) {
            LOG.debug("dismissal of help tip failed", (Throwable)e);
            return Response.status((Response.Status)Response.Status.BAD_REQUEST).cacheControl(this.cacheControlNoCache()).build();
        }
    }

    @DELETE
    public Response undismiss(Tooltip tooltip) {
        if (tooltip == null || tooltip.id == null) {
            return Response.status((Response.Status)Response.Status.BAD_REQUEST).cacheControl(this.cacheControlNoCache()).build();
        }
        try {
            ConfluenceUser user = AuthenticatedUserThreadLocal.get();
            this.helpTipManager.undismissTip((User)user, tooltip.id);
            return Response.noContent().cacheControl(this.cacheControlNoCache()).build();
        }
        catch (IllegalArgumentException e) {
            LOG.debug("undismissal of help tip failed", (Throwable)e);
            return Response.status((Response.Status)Response.Status.BAD_REQUEST).cacheControl(this.cacheControlNoCache()).build();
        }
    }

    private CacheControl cacheControlNoCache() {
        CacheControl cacheControl = new CacheControl();
        cacheControl.setNoCache(true);
        return cacheControl;
    }

    @XmlRootElement
    public static class Tooltip {
        @XmlElement
        public String id;
    }
}

