/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.gadgets.GadgetSpecProvider
 *  com.atlassian.sal.api.ApplicationProperties
 *  javax.ws.rs.GET
 *  javax.ws.rs.Path
 *  javax.ws.rs.Produces
 *  javax.ws.rs.core.Response
 */
package com.atlassian.confluence.plugins.gadgets.rest;

import com.atlassian.confluence.plugins.gadgets.events.GadgetInfoRestFetchEvent;
import com.atlassian.confluence.plugins.gadgets.rest.GadgetSpecURIs;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.gadgets.GadgetSpecProvider;
import com.atlassian.sal.api.ApplicationProperties;
import java.lang.invoke.CallSite;
import java.net.URI;
import java.util.ArrayList;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

@Path(value="/gadgets")
@Produces(value={"application/json", "application/xml"})
public class GadgetInfo {
    private GadgetSpecProvider gadgetSpecProvider;
    private ApplicationProperties applicationProperties;
    private EventPublisher eventPublisher;

    public void setGadgetSpecProvider(GadgetSpecProvider gadgetSpecProvider) {
        this.gadgetSpecProvider = gadgetSpecProvider;
    }

    public void setApplicationProperties(ApplicationProperties applicationProperties) {
        this.applicationProperties = applicationProperties;
    }

    public void setEventPublisher(EventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @GET
    public Response getGadgetURIs() {
        ArrayList<CallSite> result = new ArrayList<CallSite>();
        for (URI uri : this.gadgetSpecProvider.entries()) {
            result.add((CallSite)((Object)(this.applicationProperties.getBaseUrl() + "/" + uri.toString())));
        }
        this.eventPublisher.publish((Object)new GadgetInfoRestFetchEvent(result.size()));
        GadgetSpecURIs gadgetSpecURIs = new GadgetSpecURIs();
        gadgetSpecURIs.setUris(result.toArray(new String[result.size()]));
        return Response.ok((Object)gadgetSpecURIs).build();
    }
}

