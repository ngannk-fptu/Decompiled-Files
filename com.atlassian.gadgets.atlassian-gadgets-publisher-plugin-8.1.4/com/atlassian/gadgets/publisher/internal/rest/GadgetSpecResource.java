/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugins.rest.common.security.AnonymousAllowed
 *  javax.ws.rs.GET
 *  javax.ws.rs.Path
 *  javax.ws.rs.PathParam
 *  javax.ws.rs.Produces
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.gadgets.publisher.internal.rest;

import com.atlassian.gadgets.publisher.internal.PublishedGadgetSpecNotFoundException;
import com.atlassian.gadgets.publisher.internal.PublishedGadgetSpecWriter;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path(value="/g/{pluginKey}/{gadgetSpecName:.+}")
public final class GadgetSpecResource {
    private static final Logger log = LoggerFactory.getLogger(GadgetSpecResource.class);
    private final PublishedGadgetSpecWriter writer;

    public GadgetSpecResource(PublishedGadgetSpecWriter writer) {
        this.writer = writer;
    }

    @GET
    @AnonymousAllowed
    @Produces(value={"application/xml"})
    public Response getGadgetSpec(@PathParam(value="pluginKey") String pluginKey, @PathParam(value="gadgetSpecName") String gadgetSpecName) {
        if (pluginKey.contains(":")) {
            pluginKey = pluginKey.substring(0, pluginKey.indexOf(":"));
        }
        log.debug("GET received: allowing anonymous access to " + pluginKey + "/" + gadgetSpecName);
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            this.writer.writeGadgetSpecTo(pluginKey, gadgetSpecName, baos);
            log.debug("GET processed; request complete");
            return Response.ok((Object)baos.toByteArray()).type("application/xml").build();
        }
        catch (IOException ioe) {
            log.warn("Failed to write gadget spec to stream for " + pluginKey + "/" + gadgetSpecName + " due to " + ioe.getMessage());
            return Response.serverError().type("text/plain").entity((Object)ioe.getMessage()).build();
        }
        catch (PublishedGadgetSpecNotFoundException pgsnfe) {
            log.info("Gadget spec not found " + pluginKey + "/" + gadgetSpecName);
            return Response.status((Response.Status)Response.Status.NOT_FOUND).build();
        }
        catch (RuntimeException ex) {
            log.error("Exception writing gadget sopec to stream for " + pluginKey + "/" + gadgetSpecName, (Throwable)ex);
            throw ex;
        }
    }
}

