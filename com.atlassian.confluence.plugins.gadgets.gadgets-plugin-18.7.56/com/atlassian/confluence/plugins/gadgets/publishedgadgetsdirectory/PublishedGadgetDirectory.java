/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugins.rest.common.security.AnonymousAllowed
 *  javax.ws.rs.GET
 *  javax.ws.rs.Path
 *  javax.ws.rs.Produces
 *  javax.ws.rs.core.Response
 */
package com.atlassian.confluence.plugins.gadgets.publishedgadgetsdirectory;

import com.atlassian.confluence.plugins.gadgets.publishedgadgetsdirectory.PublishedGadgetData;
import com.atlassian.confluence.plugins.gadgets.publishedgadgetsdirectory.PublishedGadgetDirectoryService;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

@Path(value="/published/gadgetsdirectory")
@Produces(value={"application/json", "application/xml"})
public class PublishedGadgetDirectory {
    private final PublishedGadgetDirectoryService publishedGadgetDirectoryService;

    public PublishedGadgetDirectory(PublishedGadgetDirectoryService publishedGadgetDirectoryService) {
        this.publishedGadgetDirectoryService = publishedGadgetDirectoryService;
    }

    @GET
    @AnonymousAllowed
    public Response getDirectory() {
        PublishedGadgetData list = new PublishedGadgetData(this.publishedGadgetDirectoryService.getGadgetData());
        return Response.ok((Object)list).build();
    }
}

