/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.service.accessmode.ReadOnlyAccessAllowed
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.POST
 *  javax.ws.rs.Path
 *  javax.ws.rs.Produces
 *  javax.ws.rs.core.Response
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.sharepage.rest;

import com.atlassian.confluence.api.service.accessmode.ReadOnlyAccessAllowed;
import com.atlassian.confluence.plugins.sharepage.api.SharePageService;
import com.atlassian.confluence.plugins.sharepage.api.ShareRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Path(value="/")
@Component
public class ShareResource {
    private final SharePageService sharePageService;

    @Autowired
    public ShareResource(SharePageService sharePageService) {
        this.sharePageService = sharePageService;
    }

    @POST
    @Path(value="/share")
    @Consumes(value={"application/json"})
    @Produces(value={"text/plain"})
    @ReadOnlyAccessAllowed
    public Response share(ShareRequest shareRequest) {
        this.sharePageService.share(shareRequest);
        return Response.ok().build();
    }
}

